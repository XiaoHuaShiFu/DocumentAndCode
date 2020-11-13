## Spring Security OAuth2自定义Token获取方式

 2018-05-26 |  Visit count 146984

在上一节[Spring Security OAuth2入门](https://mrbird.cc/Spring-Security-OAuth2-Guide.html)中，我们使用了Spring Security OAuth2封装的授权码和密码模式成功获取了令牌，这节记录下如何通过自定义的用户名密码和手机短信验证码的方式来获取令牌。

## 自定义用户名密码方式获取令牌

在上一节的基础上，我们先在资源服务器上加入一些基本的Spring Security配置:

```
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Autowired
    private MyAuthenticationSucessHandler authenticationSucessHandler;
    @Autowired
    private MyAuthenticationFailureHandler authenticationFailureHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.formLogin() // 表单登录
                .loginProcessingUrl("/login") // 处理表单登录 URL
                .successHandler(authenticationSucessHandler) // 处理登录成功
                .failureHandler(authenticationFailureHandler) // 处理登录失败
            .and()
                .authorizeRequests() // 授权配置
                .anyRequest()  // 所有请求
                .authenticated() // 都需要认证
            .and()
                .csrf().disable();
    }
}
```



`MyAuthenticationFailureHandler`失败处理器的逻辑很简单，就是认证失败放回相应提示：

```
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Autowired
    private ObjectMapper mapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(mapper.writeValueAsString(exception.getMessage()));
    }
}
```



问题的关键是，如何在登录成功处理器里返回令牌。在研究Spring Security OAuth2自带的令牌获取方式后，会发现令牌的产生可以归纳为以下几个步骤：

![QQ截图20190624223930.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20190624223930.png)

我们可以参考这个流程，来实现在登录成功处理器`MyAuthenticationSucessHandler`里生成令牌并返回：

```
@Component
public class MyAuthenticationSucessHandler implements AuthenticationSuccessHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 1. 从请求头中获取 ClientId
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            throw new UnapprovedClientAuthenticationException("请求头中无client信息");
        }

        String[] tokens = this.extractAndDecodeHeader(header, request);
        String clientId = tokens[0];
        String clientSecret = tokens[1];

        TokenRequest tokenRequest = null;

        // 2. 通过 ClientDetailsService 获取 ClientDetails
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

        // 3. 校验 ClientId和 ClientSecret的正确性
        if (clientDetails == null) {
            throw new UnapprovedClientAuthenticationException("clientId:" + clientId + "对应的信息不存在");
        } else if (!StringUtils.equals(clientDetails.getClientSecret(), clientSecret)) {
            throw new UnapprovedClientAuthenticationException("clientSecret不正确");
        } else {
            // 4. 通过 TokenRequest构造器生成 TokenRequest
            tokenRequest = new TokenRequest(new HashMap<>(), clientId, clientDetails.getScope(), "custom");
        }

        // 5. 通过 TokenRequest的 createOAuth2Request方法获取 OAuth2Request
        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
        // 6. 通过 Authentication和 OAuth2Request构造出 OAuth2Authentication
        OAuth2Authentication auth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

        // 7. 通过 AuthorizationServerTokenServices 生成 OAuth2AccessToken
        OAuth2AccessToken token = authorizationServerTokenServices.createAccessToken(auth2Authentication);

        // 8. 返回 Token
        log.info("登录成功");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(token));
    }

    private String[] extractAndDecodeHeader(String header, HttpServletRequest request) {
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException var7) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);
        int delim = token.indexOf(":");
        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        } else {
            return new String[]{token.substring(0, delim), token.substring(delim + 1)};
        }
    }
}
```



启动项目，使用postman发送登录请求[localhost:8080/login](localhost:8080/login)：

![QQ截图20190625162337.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20190625162337.png)

![QQ截图20190625162422.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20190625162422.png)

点击发送后便可以成功获取到令牌：

```
{
    "access_token": "88a3dd6c-ab27-41af-95ee-5cd406fe5ab1",
    "token_type": "bearer",
    "refresh_token": "b316177d-68e9-4fc9-9f4a-804a7367ebc9",
    "expires_in": 43199
}
```



使用这个令牌便可以成功访问`/index`接口，这里就不演示了。

![QQ截图20190625162634.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20190625162634.png)

## 短信验证码获取令牌

在[Spring Security短信验证码登录](https://mrbird.cc/Spring-Security-SmsCode.html)一节中，我们实现了通过短信验证码登录系统的功能，通过短信验证码获取令牌和它唯一的区别就是验证码的存储策略。之前的例子验证码存储在Session中，现在使用令牌的方式和系统交互后Session已经不适用了，我们可以使用第三方存储来保存我们的验证码（无论是短信验证码还是图形验证码都是一个道理），比如Redis等。

引入Redis依赖：

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```



定义一个`RedisCodeService`，用于验证码的增删改：

```
/**
 * Redis操作验证码服务
 */
@Service
public class RedisCodeService {

    private final static String SMS_CODE_PREFIX = "SMS_CODE:";
    private final static Integer TIME_OUT = 300;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 保存验证码到 redis
     *
     * @param smsCode 短信验证码
     * @param request ServletWebRequest
     */
    public void save(SmsCode smsCode, ServletWebRequest request, String mobile) throws Exception {
        redisTemplate.opsForValue().set(key(request, mobile), smsCode.getCode(), TIME_OUT, TimeUnit.SECONDS);
    }

    /**
     * 获取验证码
     *
     * @param request ServletWebRequest
     * @return 验证码
     */
    public String get(ServletWebRequest request, String mobile) throws Exception {
        return redisTemplate.opsForValue().get(key(request, mobile));
    }

    /**
     * 移除验证码
     *
     * @param request ServletWebRequest
     */
    public void remove(ServletWebRequest request, String mobile) throws Exception {
        redisTemplate.delete(key(request, mobile));
    }

    private String key(ServletWebRequest request, String mobile) throws Exception {
        String deviceId = request.getHeader("deviceId");
        if (StringUtils.isBlank(deviceId)) {
            throw new Exception("请在请求头中设置deviceId");
        }
        return SMS_CODE_PREFIX + deviceId + ":" + mobile;
    }
}
```



然后将[Spring Security短信验证码登录](https://mrbird.cc/Spring-Security-SmsCode.html)一节中的实现都挪到现在的Demo里，修改相应的地方（涉及到验证码的增删改的地方，具体可以参考下面的源码，这里就不赘述了）。

启动系统，使用postman发送验证码：

![QQ截图20190626093154.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20190626093154.png)

请求头中带上deviceId（这里为随便填写的模拟值）：

![QQ截图20190626093234.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20190626093234.png)

点击发送后，控制台输出：

```
手机号17720202020的登录验证码为：619963，有效时间为120秒
```



接着用这个验证码去换取令牌，使用postman发送如下请求：

![QQ截图20190626093418.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20190626093418.png)

同样请求头中要带上deviceId和经过base64加密的`client_id:client_secret`：

![QQ截图20190626093515.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20190626093515.png)

点击发送后，便可以成功获取到令牌：

```
{
    "access_token": "7fe22e67-1a11-4708-8707-0100555a9d1a",
    "token_type": "bearer",
    "refresh_token": "7c7a814f-2ace-4171-9748-56cb1994b04b",
    "expires_in": 41982
}
```



源码链接：https://github.com/wuyouzhuguli/SpringAll/tree/master/64.Spring-Security-OAuth2-Customize