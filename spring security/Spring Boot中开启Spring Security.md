## Spring Boot中开启Spring Security

 2018-04-30 |  Visit count 148037

Spring Security是一款基于Spring的安全框架，主要包含认证和授权两大安全模块，和另外一款流行的安全框架Apache Shiro相比，它拥有更为强大的功能。Spring Security也可以轻松的自定义扩展以满足各种需求，并且对常见的Web安全攻击提供了防护支持。如果你的Web框架选择的是Spring，那么在安全方面Spring Security会是一个不错的选择。

这里我们使用Spring Boot来集成Spring Security，Spring Boot版本为`1.5.14.RELEASE`，Spring Security版本为`4.2.7RELEASE`。

## 开启Spring Security

创建一个Spring Boot项目，然后引入spring-boot-starter-security：

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```



接下来我们创建一个TestController，对外提供一个`/hello`服务：

```
@RestController
public class TestController {
    @GetMapping("hello")
    public String hello() {
        return "hello spring security";
    }
}
```



这时候我们直接启动项目，访问http://localhost:8080/hello，可看到页面弹出了个HTTP Basic认证框：

![QQ截图20180707095933.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707095933.png)

当Spring项目中引入了Spring Security依赖的时候，项目会默认开启如下配置：

```
security:
  basic:
    enabled: true
```



这个配置开启了一个HTTP basic类型的认证，所有服务的访问都必须先过这个认证，默认的用户名为user，密码由Sping Security自动生成，回到IDE的控制台，可以找到密码信息：

```
Using default security password: e9ed391c-93de-4611-ac87-d871d9e749ac
```



输入用户名user，密码e9ed391c-93de-4611-ac87-d871d9e749ac后，我们便可以成功访问`/hello`接口。

## 基于表单认证

我们可以通过一些配置将HTTP Basic认证修改为基于表单的认证方式。

创建一个配置类`BrowserSecurityConfig`继承`org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter`这个抽象类并重写`configure(HttpSecurity http)`方法。`WebSecurityConfigurerAdapter`是由Spring Security提供的Web应用安全配置的适配器：

```
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin() // 表单方式
                .and()
                .authorizeRequests() // 授权配置
                .anyRequest()  // 所有请求
                .authenticated(); // 都需要认证
    }
}
```



Spring Security提供了这种链式的方法调用。上面配置指定了认证方式为表单登录，并且所有请求都需要进行认证。这时候我们重启项目，再次访问http://localhost:8080/hello，可以看到认证方式已经是form表单的方式了：

![QQ截图20180707102535.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707102535.png)

用户名依旧是user，密码由Spring Security自动生成。当输入凭证错误时，页面上将显示错误信息：

![QQ截图20180707103009.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707103009.png)

如果需要换回HTTP Basic的认证方式，我们只需要简单修改`configure`方法中的配置：

```
@Override
protected void configure(HttpSecurity http) throws Exception {
    // http.formLogin() // 表单方式
    http.httpBasic() // HTTP Basic方式
            .and()
            .authorizeRequests() // 授权配置
            .anyRequest()  // 所有请求
            .authenticated(); // 都需要认证
}
```



## 基本原理

上面我们开启了一个最简单的Spring Security安全配置，下面我们来了解下Spring Security的基本原理。通过上面的的配置，代码的执行过程可以简化为下图表示：

![QQ截图20180707111356.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707111356.png)

如上图所示，Spring Security包含了众多的过滤器，这些过滤器形成了一条链，所有请求都必须通过这些过滤器后才能成功访问到资源。其中`UsernamePasswordAuthenticationFilter`过滤器用于处理基于表单方式的登录认证，而`BasicAuthenticationFilter`用于处理基于HTTP Basic方式的登录验证，后面还可能包含一系列别的过滤器（可以通过相应配置开启）。在过滤器链的末尾是一个名为`FilterSecurityInterceptor`的拦截器，用于判断当前请求身份认证是否成功，是否有相应的权限，当身份认证失败或者权限不足的时候便会抛出相应的异常。`ExceptionTranslateFilter`捕获并处理，所以我们在`ExceptionTranslateFilter`过滤器用于处理了`FilterSecurityInterceptor`抛出的异常并进行处理，比如需要身份认证时将请求重定向到相应的认证页面，当认证失败或者权限不足时返回相应的提示信息。

下面我们通过debug来验证这个过程（登录方式改回表单的方式）。

我们在`/hello`服务上打个断点：

![QQ截图20180707132345.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707132345.png)

在`FilterSecurityInterceptor`的invoke方法的`super.beforeInvocation`上打个断点：

![QQ截图20180707132824.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707132824.png)

当这行代码执行通过后，便可以调用下一行的`doFilter`方法来真正调用`/hello`服务，否则将抛出相应的异常。

当`FilterSecurityInterceptor`抛出异常时，异常将由`ExceptionTranslateFilter`捕获并处理，所以我们在`ExceptionTranslateFilter`的`doFilter`方法`catch`代码块第一行打个断点：

![QQ截图20180707133347.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707133347.png)

我们待会模拟的是用户未登录直接访问`/hello`，所以应该是抛出用户未认证的异常，所以接下来应该跳转到`UsernamePasswordAuthenticationFilter`处理表单方式的用户认证。在`UsernamePasswordAuthenticationFilter`的`attemptAuthentication`方法上打个断点：

![QQ截图20180707134106.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707134106.png)

准备完毕后，我们启动项目，然后访问http://localhost:8080/hello，代码直接跳转到`FilterSecurityInteceptor`的断点上：

![QQ截图20180707134540.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707134540.png)

往下执行，因为当前请求没有经过身份认证，所以将抛出异常并被`ExceptionTranslateFilter`捕获：

![QQ截图20180707134540.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707134915.png)

捕获异常后重定向到登录表单登录页面，当我们在表单登录页面输入信息点login后，代码跳转到`UsernamePasswordAuthenticationFilter`过滤器的`attemptAuthentication`方法上：

![QQ截图20180707135743.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707135743.png)

判断用户名和密码是否正确之后，代码又跳回`FilterSecurityInterceptor`的`beforeInvocation`方法执行上：

![QQ截图20180707140158.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707140158.png)

当认证通过时，`FilterSecurityInterceptor`代码往下执行`doFilter`，然后代码最终跳转到`/hello`上：

![QQ截图20180707140532.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707140532.png)

浏览器页面将显示`hello spring security`信息。

源码链接：https://github.com/wuyouzhuguli/Spring-Boot-Demos/tree/master/34.Start-Spring-Security