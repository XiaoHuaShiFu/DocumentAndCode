# spring security控制session

![img](https://csdnimg.cn/release/blogv2/dist/pc/img/original.png)

[neweastsun](https://me.csdn.net/neweastsun) 2018-02-25 20:10:40 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/articleReadEyes.png) 15891 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/tobarCollect.png) 收藏 10

分类专栏： [spring](https://blog.csdn.net/neweastsun/category_7436609.html) [spring security 实战](https://blog.csdn.net/neweastsun/category_9275679.html) 文章标签： [spring security](https://so.csdn.net/so/search/s.do?q=spring security&t=blog&o=vip&s=&l=&f=&viparticle=) [session管理](https://www.csdn.net/gather_23/OtDaYg5sNzc2Mi1ibG9n.html) [session超时](https://www.csdn.net/gather_24/OtDacgwsMTU1OS1ibG9n.html) [session篡改](https://www.csdn.net/gather_22/MtTaggysMjE3MDgtYmxvZwO0O0OO0O0O.html) [session攻击](https://www.csdn.net/gather_23/OtTaYg2sMDU1Ni1ibG9n.html)

版权

# spring security控制session

本文给你描述在spring security中如何控制http session。包括session超时、启用并发session以及其他高级安全配置。

## 创建session时机

我们可以准确地控制什么时机创建session，有以下选项进行控制：

always – 如果session不存在总是需要创建；
ifRequired – 仅当需要时，创建session(默认配置)；
never – 框架从不创建session，但如果已经存在，会使用该session ；
stateless – Spring Security不会创建session，或使用session；

java config配置方式如下：

```
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
}12345
```

该配置仅控制spring security做什么，不是整个应用，根据配置spring security可能不创建session，但我们的应用可以创建session。理解这点很重要。

缺省值“ifRequired”，spring security根据需要创建session。

对于大多数无状态应用，“never”选项确保spring security自身不创建任何session；然而，如果应用自己创建session，那么spring security会使用它。

最后，最严格session创建选项是“stateless”，其确保应用也不会创建任何session。从spring3.1开始引入，将影响跳过部分spring security过滤器——主要与session相关的，如HttpSessionSecurityContextRepository, SessionManagementFilter, RequestCacheFilter。

这些大多数严格控制机制直接暗示cookies不被使用，每个请求需要被认证。无状态的体系结构与REST api及它们的无状态约束很好地发挥了作用。在Basic和Digest认证中也可以很好工作。

## 深入理解

在执行认证过程之前，spring security将运行SecurityContextPersistenceFilter过滤器负责存储安请求之间的全上下文，上下文根据策略进行存储，默认为HttpSessionSecurityContextRepository ，其使用http session作为存储器。

对于严格的创建session选项“stateless”，使用另一个存储策略—— NullSecurityContextRepository，没有session被创建或使用，用于保存上下文。

## 并发session控制

当已经认证过的用户尝试再次认证时，应用针对该事件有几种处理方式。可以注销当前用户session，使用新的session重新认证，或允许两个session并存。
启用并发session控制，首先需要在配置中增加下面监听器：

```
@Bean
public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
}1234
```

其本质是确保当session被销毁时，通知spring security session注册器。
在这种场景下，允许同一用户拥有多个并发session，配置如下：

```
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement().maximumSessions(2)
}1234
```

## session超时

session已经超时后，如果用户使用已经过期的sessionid发送请求，则会被重定向至指定url；同样如果使用未过期但完全无效的sessionid用户发送请求，也会被重定向至特定url，配置如下：

```
http.sessionManagement()
  .expiredUrl("/sessionExpired.html")
  .invalidSessionUrl("/invalidSession.html");123
```

## 防止使用url参数进行session跟踪

在url中暴露session信息会增加安全风险，自spring3.0开始，追加jsessionId至url的重新逻辑可以被禁用，通过设置disable-url-rewriting=”true” 。另外自servlet3.0开始，session跟踪机制也可以在web.xml中配置：

```
<session-config>
     <tracking-mode>COOKIE</tracking-mode>
</session-config>123
```

javaConfig方式配置如下：

```
servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));1
```

配置可以设置jessionId存储位置：cookie或url参数。

## spring security防止篡改session

框架提供了保护典型的session篡改攻击，当用户尝试再次认证时，已经存在的session可以配置实现以不同方式处理。配置方式如下：

```
http.sessionManagement()
  .sessionFixation().migrateSession()12
```

缺省情况，spring security启用migrateSession，即认证时，创建一个新http session，原session失效，属性从原session中拷贝过来。

如果不期望这种行为，还有其他两个选项：

- “none”，原session保持有效；
- “newSession”，新创建session，且不从原session中拷贝任何属性。

## 使用session

### session范围bean

bean可以简单地通过@Scope在web上下文中定义session范围：

```
@Component
@Scope("session")
public class Foo { .. }123
```

然后，可以再注入值另一个bean：

```
@Autowired
private Foo theFoo;12
```

这时，spring会在http session范围内创建一个新的bean。

### 在Controller中注入原生session

原生的session可以被直接注入至Controller方法：

```
@RequestMapping(..)
public void fooMethod(HttpSession session) {
    session.addAttribute(Constants.FOO, new Foo();
    //...
    Foo foo = (Foo) session.getAttribute(Constants.Foo);
}123456
```

### 获取原生session

也可以通过调用servlet api以编程方式获取当前http session：

```
ServletRequestAttributes attr = (ServletRequestAttributes) 
    RequestContextHolder.currentRequestAttributes();
HttpSession session= attr.getRequest().getSession(true); // true == allow create123
```

## 总结

本文讨论了spring security如何管理session，以及如何使用session。