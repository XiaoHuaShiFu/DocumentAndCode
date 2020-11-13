# [【详解】Spring Security 之 SecurityContext](https://www.cnblogs.com/longfurcat/p/10293819.html)

## 前言

　　本文主要整理一下SecurityContext的存储方式。

## SecurityContext接口

顾名思义，安全上下文。即存储认证授权的相关信息，实际上就是存储"**当前用户**"账号信息和相关权限。这个接口只有两个方法，Authentication对象的getter、setter。

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
package org.springframework.security.core.context;

import java.io.Serializable;
import org.springframework.security.core.Authentication;

public interface SecurityContext extends Serializable {
    Authentication getAuthentication();

    void setAuthentication(Authentication var1);
}
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

## Authentication接口又是干嘛的？

注意：SecurityContext存储的Authentication对象是经过认证的，所以它会带有权限，它的getAuthorities()方法会返回相关权限。

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
package org.springframework.security.core;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;

public interface Authentication extends Principal, Serializable {
    Collection<? extends GrantedAuthority> getAuthorities();

    Object getCredentials();

    Object getDetails();

    Object getPrincipal();

    boolean isAuthenticated();

    void setAuthenticated(boolean var1) throws IllegalArgumentException;
}
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

## SecurityContextHolder工具类

前面说的"**当前用户**"实际上指的是当前这个请求所对应的用户，那么怎么知道当前用户是谁呢？由于一个请求从开始到结束都由一个线程处理，这个线程中途也不会去处理其他的请求。所以在这段时间内，相当于这个线程跟当前用户是一一对应的。SecurityContextHolder工具类就是把SecurityContext存储在当前线程中。

SecurityContextHolder可以用来设置和获取SecurityContext。它主要是给框架内部使用的，可以利用它获取当前用户的SecurityContext进行请求检查，和访问控制等。

在Web环境下，SecurityContextHolder是利用ThreadLocal来存储SecurityContext的。

## **请求结束，SecurityContext存储在哪里？**

我们知道Sevlet中线程是被池化复用的，一旦处理完当前的请求，它可能马上就会被分配去处理其他的请求。而且也不能保证用户下次的请求会被分配到同一个线程。所以存在线程里面，请求一旦结束，就没了。如果没有保存，不是每次请求都要重新认证登录？想想看，如果没有权限框架我们是怎么处理的？

想到了吧，如果不用权限框架，我们一般是把认证结果存在Session中的。同理，它也把认证结果存储到Session了。

对应的Key是："**SPRING_SECURITY_CONTEXT**"

## 流程视图

![img](https://img2018.cnblogs.com/blog/1313132/201901/1313132-20190120155653707-1681055739.png)

## SecurityContextPersistenceFilter拦截器

 SecurityContextPersistenceFilter是Security的拦截器，而且是拦截链中的第一个拦截器，请求来临时它会从HttpSession中把SecurityContext取出来，然后放入SecurityContextHolder。在所有拦截器都处理完成后，再把SecurityContext存入HttpSession，并清除SecurityContextHolder内的引用。

注：其中repo对象是HttpSessionSecurityContextRepository

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (request.getAttribute(FILTER_APPLIED) != null) {
            // ensure that filter is only applied once per request
            chain.doFilter(request, response);
            return;
        }

        final boolean debug = logger.isDebugEnabled();

        request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

        if (forceEagerSessionCreation) {
            HttpSession session = request.getSession();

            if (debug && session.isNew()) {
                logger.debug("Eagerly created session: " + session.getId());
            }
        }

        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request,
                response);
        //利用HttpSecurityContextRepository从HttpSesion中获取SecurityContext对象
        //如果没有HttpSession，即浏览器第一次访问服务器，还没有产生会话。
        //它会创建一个空的SecurityContext对象
        SecurityContext contextBeforeChainExecution = repo.loadContext(holder);

        try {
            //把SecurityContext放入到SecurityContextHolder中
            SecurityContextHolder.setContext(contextBeforeChainExecution);
            //执行拦截链，这个链会逐层向下执行
            chain.doFilter(holder.getRequest(), holder.getResponse());

        }
        finally { 
            //当拦截器都执行完的时候把当前线程对应的SecurityContext从SecurityContextHolder中取出来
            SecurityContext contextAfterChainExecution = SecurityContextHolder
                    .getContext();
            // Crucial removal of SecurityContextHolder contents - do this before anything
            // else.
            SecurityContextHolder.clearContext();
            //利用HttpSecurityContextRepository把SecurityContext写入HttpSession
            repo.saveContext(contextAfterChainExecution, holder.getRequest(),
                    holder.getResponse());
            request.removeAttribute(FILTER_APPLIED);

            if (debug) {
                logger.debug("SecurityContextHolder now cleared, as request processing completed");
            }
        }
    }
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

### Tomcat建立会话的流程

　　有人可能对Tomcat建立会话的流程还不熟悉，这里稍微整理一下。是这样的，当客户浏览器打开后第一次访问Tomcat服务器，Tomcat会创建一个HttpSesion对象，存入一个ConcurrentHashMap，Key是SessionId，Value就是HttpSession。然后请求完成后，在返回的报文中添加**Set-Cookie：JSESSIONID=xxx**，然后客户端浏览器会保存这个Cookie。当浏览器再次访问这个服务器的时候，都会带上这个Cookie。Tomcat接收到这个请求后，根据JSESSIONID把对应的HttpSession对象取出来，放入HttpSerlvetRequest对象里面。

**重点：**

1.HttpSession会一直存在服务端，实际上是存在运行内存中。除非Session过期 OR Tomcat奔溃 OR 服务器奔溃，否则会话信息不会消失。

2.如无特殊处理，Cookie JSESSIONID会在浏览器关闭的时候清除。

3.Tomcat中HttpSesion的默认过期时间为30分钟。

4.这些处理都在Security的拦截链之前完成。

——————————————————2019年1月21日更正——————————————

浏览器访问Web项目并不会创建Session，只有在编程调用request.getSession()方法后Session才会建立，浏览器才会有JSESSIONID的Cookie。

一般是认证成功后，调用getSession()（其实在这之前Session并不存在），获得Session对象，然后把信息存里面。

所以，一般的，在登录之前，你不管怎么访问网站，都不会建立会话。