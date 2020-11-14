# [Spring Security 与 OAuth2（资源服务器）](https://www.cnblogs.com/grimm/p/13518174.html)

### resource-server(资源服务器)

#### 资源服务器

- 要访问资源服务器受保护的资源需要携带令牌（从授权服务器获得）
- **客户端往往同时也是一个资源服务器，各个服务之间的通信（访问需要权限的资源）时需携带访问令牌**
- 资源服务器通过 @EnableResourceServer 注解来开启一个 **OAuth2AuthenticationProcessingFilter** 类型的过滤器
- 通过继承 ResourceServerConfigurerAdapter 类来配置资源服务器

#### ResourceServerProperties

- OAuth2 为资源服务器配置提供了 ResourceServerProperties 类，该类会读取配置文件中对资源服务器得配置信息（如授权服务器公钥访问地址）

#### ResourceServerSecurityConfigurer 可配置属性

- tokenServices：ResourceServerTokenServices 类的实例，用来实现令牌业务逻辑服务
- resourceId：这个资源服务的ID，这个属性是可选的，但是推荐设置并在授权服务中进行验证
- tokenExtractor 令牌提取器用来提取请求中的令牌
- 请求匹配器，用来设置需要进行保护的资源路径，默认的情况下是受保护资源服务的全部路径
- 受保护资源的访问规则，默认的规则是简单的身份验证（plain authenticated）
- 其他的自定义权限保护规则通过 HttpSecurity 来进行配置

#### 解析令牌方法：

- 使用 DefaultTokenServices 在资源服务器本地配置令牌存储、解码、解析方式
- 使用 RemoteTokenServices 资源服务器通过 HTTP 请求来解码令牌，每次都请求授权服务器端点 /oauth/check_token
- **若授权服务器是 JWT 非对称加密，则需要请求授权服务器的 /oauth/token_key 来获取公钥 key 进行解码**

### 代码案例

#### 令牌解析（JWT 对称加密）

> 资源服务器和授权服务器不在同一个应用，则需告诉资源服务器令牌如何存储与解析，并与授权服务器使用相同的密钥进行解密



```java
@Configuration
@EnableResourceServer
public class OAuth2ResourceConfig extends ResourceServerConfigurerAdapter{
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    //与授权服务器使用共同的密钥进行解析
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123");
        return converter;
    }
}
```

#### 令牌解析（JWT 非对称加密）

- 非对称加密需要公钥，可以从本地获取，也可以从授权服务器提供的公钥端点获取
- 若本地获取不到公钥资源文件 pubkey.txt 则从授权服务器端点获取



```java
@Configuration
@EnableResourceServer
public class OAuth2ResourceConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private ResourceServerProperties resourceServerProperties;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        //设置用于解码的非对称加密的公钥
        converter.setVerifierKey(getPubKey());
        return converter;
    }

    private String getPubKey() {
        Resource resource = new ClassPathResource("pubkey.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            System.out.println("本地公钥");
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            return getKeyFromAuthorizationServer();
        }
    }

    private String getKeyFromAuthorizationServer() {
        ObjectMapper objectMapper = new ObjectMapper();
        String pubKey = new RestTemplate().getForObject(resourceServerProperties.getJwt().getKeyUri(), String.class);
        try {
            Map map = objectMapper.readValue(pubKey, Map.class);
            System.out.println("联网公钥");
            return map.get("value").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
```

#### 令牌解析（通过访问授权服务器解析令牌-适用 JDBC、内存存储）

- 资源服务器通过访问授权服务器 /oauth/check_token 端点解析令牌需要使用 RemoteTokenServices
- 并且使用 DefaultAccessTokenConverter 来实现令牌数据的存储



```java
    @Autowired
    private OAuth2ClientProperties oAuth2ClientProperties;

    @Autowired
    private AuthorizationServerProperties authorizationServerProperties;

    @Bean
    public ResourceServerTokenServices tokenServices() {
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl(authorizationServerProperties.getCheckTokenAccess());
        remoteTokenServices.setClientId(oAuth2ClientProperties.getClientId());
        remoteTokenServices.setClientSecret(oAuth2ClientProperties.getClientSecret());
        remoteTokenServices.setAccessTokenConverter(accessTokenConverter());
        return remoteTokenServices;
    }

    @Bean
    public AccessTokenConverter accessTokenConverter() {
        return new DefaultAccessTokenConverter();
    }
```

- 修改配置文件



```go
security:
  oauth2:
    client:
      clientId: resource1
      clientSecret: secret
      userAuthorizationUri: http://localhost:9005/oauth/authorize
      grant-type: password
      scope: read
      access-token-uri: http://localhost:9005/oauth/token
    resource:
      userInfoUri: http://localhost:9005/user
    authorization:
      check-token-access: http://localhost:9005/oauth/check_token
  basic:
    enabled: false
```