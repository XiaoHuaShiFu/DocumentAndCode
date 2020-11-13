# Spring Security TokenStore实现3+1详解

![img](https://csdnimg.cn/release/blogv2/dist/pc/img/original.png)

[CatalpaFlat](https://me.csdn.net/DuShiWoDeCuo) 2017-12-29 11:03:55 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/articleReadEyes.png) 10323 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/tobarCollect.png) 收藏 2

分类专栏： [spring](https://blog.csdn.net/dushiwodecuo/category_7214637.html)

版权

> TokenStore：Persistence interface for OAuth2 tokens.(对于OAuth2令牌持久化接口)
> [官方文档](https://docs.spring.io/spring-security/oauth/apidocs/org/springframework/security/oauth2/provider/token/TokenStore.html)
> TokenStore 的默认实现有三种：
> \- InMemoryTokenStore
> \- JdbcTokenStore
> \- JwtTokenStore

![spring 关于tokenstore的文档](https://user-gold-cdn.xitu.io/2017/12/29/160a02ec23950c3d?w=551&h=143&f=png&s=9373)

**此外，将会根据TokenStor的特性多自定义一种实现——RedisTokenStore**

## 一、InMemoryTokenStore

### 1.1.概要

这个是OAuth2默认采用的实现方式。在单服务上可以体现出很好特效（即并发量不大，并且它在失败的时候不会进行备份），大多项目都可以采用此方法。根据名字就知道了，是存储在内存中，毕竟存在内存，而不是磁盘中，调试简易。

### 1.2.实现

既然InMemoryTokenStore是OAuth2默认实现，那么就不需要我们再去配置，直接调用即可。
![InMemoryTokenStore 实现](https://user-gold-cdn.xitu.io/2017/12/29/160a02ec2776c851?w=917&h=528&f=png&s=63903)

### 1.3.代码调用

```java
@Autowired(required = false)
private TokenStore inMemoryTokenStore;
/**
 * 端点（处理入口）
 */
@Override
public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
   endpoints.tokenStore(inMemoryTokenStore);
   ....
}12345678910
```

### 1.4.测试调用访问获取Token

**此处基于SpringBoot+Security的小demo，相关配置就不在本文太多出现，着重讲解TokenStore**
\- spring security 默认授权认证端点：oauth/token
\- 此处使用：grant_type—>password模式

![InMemoryTokenStore 测试](https://user-gold-cdn.xitu.io/2017/12/29/160a02ec294ac610?w=991&h=274&f=png&s=29272)

## 二、JdbcTokenStore

### 2.1.概要

这个是基于JDBC的实现，令牌（Access Token）会保存到数据库。这个方式，可以在多个服务之间实现令牌共享。

### 2.2.实现

1).既然是JDBC，那么肯定得需要一个数据源。此处使用的是SpringBoot，因此配置了一个数据源。所需jar依赖就不多说了。

```xml
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/security?useUnicode=yes&characterEncoding=UTF-8
    username: catalpaFlat
    password: catalpaFlat123456
```

2).除了数据源，那么jdbc肯定得有库表，因此OAuth2默认给出了表结构

```sql
Drop table  if exists oauth_access_token;
create table oauth_access_token (
  create_time timestamp default now(),
  token_id VARCHAR(255),
  token BLOB,
  authentication_id VARCHAR(255),
  user_name VARCHAR(255),
  client_id VARCHAR(255),
  authentication BLOB,
  refresh_token VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

Drop table  if exists oauth_refresh_token;
create table oauth_refresh_token (
  create_time timestamp default now(),
  token_id VARCHAR(255),
  token BLOB,
  authentication BLOB
) ENGINE=InnoDB DEFAULT CHARSET=utf8;12345678910111213141516171819
```

而且JdbcTokenStore源码中也有很多关于表的操作：
![JdbcTokenStore 源码](https://user-gold-cdn.xitu.io/2017/12/29/160a02ec2aa26c4f?w=1456&h=561&f=png&s=103313)

3).配置JdbcTokenStore

```java
@Autowired
private DataSource dataSource;
/**
 * jdbc token 配置
 */
@Bean
public TokenStore jdbcTokenStore() {
    Assert.state(dataSource != null, "DataSource must be provided");
    return new JdbcTokenStore(dataSource);
}12345678910
```

### 2.3.代码调用

```java
@Autowired(required = false)
private TokenStore jdbcTokenStore;
/**
 * 端点（处理入口）
 */
@Override
public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
   endpoints.tokenStore(jdbcTokenStore);
   ....
}12345678910
```

### 2.4.测试调用访问获取Token

![JdbcTokenStore 测试](https://user-gold-cdn.xitu.io/2017/12/29/160a02ec2dac5603?w=958&h=265&f=png&s=27907)
![JdbcTokenStore 测试](https://user-gold-cdn.xitu.io/2017/12/29/160a02ec259618d2?w=1231&h=55&f=png&s=8544)
![JdbcTokenStore 测试](https://user-gold-cdn.xitu.io/2017/12/29/160a02ec2a22bea5?w=658&h=55&f=png&s=4670)
![JdbcTokenStore 测试](https://user-gold-cdn.xitu.io/2017/12/29/160a02ec295da82d?w=1007&h=405&f=png&s=35669)

## 三、JwtTokenStore

### 3.1.概要

jwt全称 JSON Web Token。这个实现方式不用管如何进行存储（内存或磁盘），因为它可以把相关信息数据编码存放在令牌里。JwtTokenStore 不会保存任何数据，但是它在转换令牌值以及授权信息方面与 DefaultTokenServices 所扮演的角色是一样的。

### 3.2.实现

既然jwt是将信息存放在令牌中，那么就得考虑其安全性，因此，OAuth2提供了JwtAccessTokenConverter实现，添加jwtSigningKey，以此生成秘钥，以此进行签名，只有jwtSigningKey才能获取信息。

```java
/**
* jwt Token 配置, matchIfMissing = true
*
* @author ： CatalpaFlat
*/
@Configuration
public class JwtTokenConfig {

   private final Logger logger = LoggerFactory.getLogger(JwtTokenConfig.class);
   @Value("${default.jwt.signing.key}")
   private String defaultJwtSigningKey;
   @Autowired
   private CustomYmlConfig customYmlConfig;

   public JwtTokenConfig() {logger.info("Loading JwtTokenConfig ...");}

   @Bean
   public TokenStore jwtTokenStore() {
       return new JwtTokenStore(jwtAccessTokenConverter());
   }

   @Bean
   public JwtAccessTokenConverter jwtAccessTokenConverter() {
       JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
       String jwtSigningKey = customYmlConfig.getSecurity().getOauth2s().getOuter().getJwtSigningKey();
       Assert.state(StringUtils.isBlank(jwtSigningKey), "jwtSigningKey is not configured");
       //秘签
       jwtAccessTokenConverter.setSigningKey(StringUtils.isBlank(jwtSigningKey) ? defaultJwtSigningKey : jwtSigningKey);
       return jwtAccessTokenConverter;
   }
}12345678910111213141516171819202122232425262728293031
```

### 3.3.代码调用

```java
@Autowired(required = false)
private TokenStore jwtTokenStore;
@Autowired(required = false)
private JwtAccessTokenConverter jwtAccessTokenConverter;
/**
 * 端点（处理入口）
 */
@Override
public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
   endpoints.tokenStore(jwtTokenStore)
   .accessTokenConverter(jwtAccessTokenConverter);
   ....
}12345678910111213
```

### 3.4.测试调用访问获取Token

![JwtTokenStore 测试](https://user-gold-cdn.xitu.io/2017/12/29/160a02ec2ca5a13a?w=1076&h=442&f=png&s=68893)
![JwtTokenStore 测试](https://user-gold-cdn.xitu.io/2017/12/29/160a02ec2c9d92db?w=1010&h=402&f=png&s=54286)

## 四、RedisTokenStore

### 4.1.概要

由于TokenStore作用就是对于OAuth2令牌持久化接口，而我们在实际开发中，对于内存的使用是慎之又慎，而对于存储到数据库也是根据项目需求进行调配。因此就想，可不可以用redis来进行存储持久化我们的OAuth2令牌。偷偷瞄了一眼OAuth2还有那些实现了TokenStore的，找到了一个RedisTokenStore。

![RedisTokenStore 概述](https://user-gold-cdn.xitu.io/2017/12/29/160a02ec27e9010d?w=823&h=296&f=png&s=41099)

### 4.2.实现

**记得配置redis**

```java
@Autowired
private RedisConnectionFactory redisConnectionFactory;
/**
 * redis token 配置
 */
@Bean
public TokenStore redisTokenStore() {
    return new RedisTokenStore(redisConnectionFactory);
}123456789
```

### 4.3.代码调用

```java
@Autowired(required = false)
private TokenStore redisTokenStore;
/**
 * 端点（处理入口）
 */
@Override
public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
   endpoints.tokenStore(redisTokenStore);
   ....
}12345678910
```

### 4.4.测试调用访问获取Token

![RedisTokenStore 测试](https://user-gold-cdn.xitu.io/2017/12/29/160a02ec2cbdd5ca?w=996&h=264&f=png&s=28949)
![RedisTokenStore 测试](https://user-gold-cdn.xitu.io/2017/12/29/160a02ec292b9e6e?w=1020&h=378&f=png&s=35895)