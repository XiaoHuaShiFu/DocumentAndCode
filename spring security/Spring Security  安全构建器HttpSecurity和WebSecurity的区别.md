# 一、三句话解释框架原理

1. 整个框架的核心是一个过滤器，这个过滤器名字叫`springSecurityFilterChain`类型是`FilterChainProxy`
2. 核心过滤器里面是`过滤器链`（列表），`过滤器链`的每个元素都是`一组URL对应一组过滤器`
3. `WebSecurity`用来创建`FilterChainProxy`过滤器，
   `HttpSecurity`用来创建过滤器链的每个元素。

> 关注两个东西：`建造者`和`配置器`

> 框架的用法就是通过`配置器`对`建造者`进行配置

> 框架用法是写一个自定义配置类，继承`WebSecurityConfigurerAdapter`，重写几个`configure()`方法
>
> `WebSecurityConfigurerAdapter`就是`Web安全配置器`的适配器对象



```java
// 安全建造者
// 顾名思义是一个builder构造器，创建并返回一个类型为O的对象
public interface SecurityBuilder<O> {
    O build() throws Exception;
}

// 抽象安全建造者
public abstract class AbstractSecurityBuilder<O> implements SecurityBuilder<O> {
    private AtomicBoolean building = new AtomicBoolean();
    private O object;

    public final O build() throws Exception {
        // 限定build()只会进行一次！
        if (this.building.compareAndSet(false, true)) {
            this.object = doBuild();
            return this.object;
        }
        throw new AlreadyBuiltException("This object has already been built");
    }

    // 子类需要重写doBuild()方法
    protected abstract O doBuild() throws Exception;
}

// 配置后的抽象安全建造者
public abstract class AbstractConfiguredSecurityBuilder<O, B extends SecurityBuilder<O>>
        extends AbstractSecurityBuilder<O> {

    // 实现了doBuild()方法，遍历configurers进行init()和configure()。
    protected final O doBuild() throws Exception {
        synchronized (configurers) {
            buildState = BuildState.INITIALIZING;

            beforeInit();
            init();

            buildState = BuildState.CONFIGURING;

            beforeConfigure();
            configure();

            buildState = BuildState.BUILDING;

            O result = performBuild();

            buildState = BuildState.BUILT;

            return result;
        }
    }
    // 它的子类HttpSecurity和WebSecurity都实现了它的performBuild()方法！！！
    protected abstract O performBuild() throws Exception;

    // 主要作用是将安全配置器SecurityConfigurer注入到属性configurers中，
    private void configure() throws Exception {
        Collection<SecurityConfigurer<O, B>> configurers = getConfigurers();

        for (SecurityConfigurer<O, B> configurer : configurers) {
            configurer.configure((B) this);
        }
    }
}


// 安全配置器，配置建造者B，B可以建造O
// 初始化(init)SecurityBuilder，且配置(configure)SecurityBuilder
public interface SecurityConfigurer<O, B extends SecurityBuilder<O>> {
    void init(B builder) throws Exception;
    void configure(B builder) throws Exception;
}


// Web安全配置器，配置建造者T，T可以建造web过滤器
public interface WebSecurityConfigurer<T extends SecurityBuilder<Filter>> 
        extends SecurityConfigurer<Filter, T> {
}

// Web安全配置器的适配器
// 配置建造者WebSecurity，WebSecurity可以建造核心过滤器
public abstract class WebSecurityConfigurerAdapter 
        implements WebSecurityConfigurer<WebSecurity> {
}


// 用于构建FilterChainProxy的建造者
public final class WebSecurity 
    extends AbstractConfiguredSecurityBuilder<Filter, WebSecurity>
    implements
        SecurityBuilder<Filter>, ApplicationContextAware {
}

// 用于构建SecurityFilterChain的建造者
public final class HttpSecurity 
    extends AbstractConfiguredSecurityBuilder<DefaultSecurityFilterChain, HttpSecurity>
    implements 
        SecurityBuilder<DefaultSecurityFilterChain>,
        HttpSecurityBuilder<HttpSecurity> {

}
```

**总结**：

1. 看到`建造者`去看他的方法，`build(); doBuild(); init(); configure(); performBuild();`
2. 看到`配置器`去看他的方法，`init(); config();`

## 从写`MySecurityConfig`时使用的`@EnableWebSecurity`注解开始看源码：

> ```
> @EnableWebSecurity`注解导入了三个类，重点关注`WebSecurityConfiguration
> ```

> 我们后面依次分析:

![@EnableWebSecurity注解](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS85LzI3LzE2ZDcwODk1OWZhNWJlMmE?x-oss-process=image/format,png)

## `WebSecurityConfiguration`中需要关注两个方法

1. `setFilterChainProxySecurityConfigurer()`方法

   > 创建了`WebSecurity`建造者对象，用于后面建造`FilterChainProxy`过滤器

2. `springSecurityFilterChain()`方法

   > 调用`WebSecurity.build()`，建造出`FilterChainProxy`过滤器对象





# 二、FilterChainProxy的创建过程

- 框架的核心是一个过滤器，这个过滤器`名字`叫`springSecurityFilterChain`，`类型`是`FilterChainProxy`
- `WebSecurity`和`HttpSecurity`都是`建造者`
- `WebSecurity`构建目标是`FilterChainProxy`对象
- `HttpSecurity`的构建目标仅仅是`FilterChainProxy`中的一个`SecurityFilterChain`。
- `@EnableWebSecurity`注解，导入了`WebSecurityConfiguration`类
- `WebSecurityConfiguration`中创建了建造者对象`WebSecurity`，和核心过滤器`FilterChainProxy`

![image-20201113212240900](C:\Users\82703\AppData\Roaming\Typora\typora-user-images\image-20201113212240900.png)







## 从`WebSecurityConfiguration`开始

`WebSecurityConfiguration`中需要关注两个方法：

1. ```
   setFilterChainProxySecurityConfigurer()
   ```

   方法

   > 创建了`WebSecurity`建造者对象，用于后面建造`FilterChainProxy`过滤器

2. ```
   springSecurityFilterChain()
   ```

   方法

   > 调用`WebSecurity.build()`，建造出`FilterChainProxy`过滤器对象

## `WebSecurity`的创建过程：`setFilterChainProxySecurityConfigurer()`方法

该方法负责收集`配置类对象`列表`webSecurityConfigurers`，并创建`WebSecurity`：

> 1. @Value("#{}") 是SpEl表达式通常用来获取bean的属性或者调用bean的某个方法。
>
> 2. 方法执行时,会先得到`webSecurityConfigurers`并排序（所有实现了`WebSecurityConfigurerAdapter`的配置类实例）
>
> 3. `new`出`websecurity`对象,并使用Spring的容器工具初始化
>
> 4. 判断`webSecurityConfigurers`内元素的`@Order`是否有相同，相同的`order`会抛异常。
>
>    默认`order`等于`LOWEST_PRECEDENCE = 2147483647`（参考`Integer order = AnnotationAwareOrderComparator.lookupOrder(config)`）
>
> 5. 将`WebSecurityConfigurerAdapter`的子类`apply()`放入`websecurity`的`List<SecurityConfigurer<O, B>> configurersAddedInInitializing`中。

![setFilterChainProxySecurityConfigurer()](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS85LzI3LzE2ZDcxYmQwM2E0YTc3N2I?x-oss-process=image/format,png)

> 下图是通过`AutowiredWebSecurityConfigurersIgnoreParents`的`getWebSecurityConfigurers()`方法，获取所有实现`WebSecurityConfigurer`的配置类

![getWebSecurityConfigurers](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS85LzI3LzE2ZDcyMDcwNzEzNDkyNjQ?x-oss-process=image/format,png)

## `FilterChainProxy`的创建过程：`springSecurityFilterChain()`方法

> 在`springSecurityFilterChain()`方法中调用`webSecurity.build()`创建了`FilterChainProxy`。

> PS：根据下面代码，我们可以知道如果创建的`MySecurityConfig`类没有被sping扫描到，
>
> 框架会新new 出一个`WebSecurityConfigureAdapter`对象，这会导致我们配置的用户名和密码失效。

![springSecurityFilterChain](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS85LzI3LzE2ZDcxYzM0MTlmM2ExNDI?x-oss-process=image/format,png)
![springSecurityFilterChain()](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS85LzI3LzE2ZDcwOGMzNTUwYmUyOTU?x-oss-process=image/format,png)

> 我们继续看`FilterChainProxy`的创建过程：
>
> ```
> WebSecurity`是一个建造者，所以我们去看这些方法`build(); doBuild(); init(); configure(); performBuild();
> ```

> `build()`方法定义在`WebSecurity`对象的父类`AbstractSecurityBuilder`中：

![AbstractSecurityBuilder#build()](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS85LzI3LzE2ZDcwNzM1ZGU5ZjE2YWI?x-oss-process=image/format,png)

> `build()`方法会调用`WebSecurity`对象的父类`AbstractConfiguredSecurityBuilder#doBuild()`：

![AbstractConfiguredSecurityBuilder#doBuild()](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS85LzI3LzE2ZDcwNzdhMzZkNmQ5OGE?x-oss-process=image/format,png)

> `doBuild()`先调用`init();configure();`等方法
>
> 我们上面已经得知了`configurersAddedInInitializing`里是所有的配置类对象
>
> 如下图，这里会依次执行配置类的`configure();init()`方法

![img](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS85LzMwLzE2ZDgwMzBlOTA4YjRmMDk?x-oss-process=image/format,png)

> `doBuild()`最后调用了`WebSecurity`对象的`perfomBuild()`，来创建了`FilterChainProxy`对象
>
> `performBuild()`里遍历`securityFilterChainBuilders`建造者列表
>
> 把每个`SecurityBuilder`建造者对象构建成`SecurityFilterChain`实例
>
> 最后创建并返回`FilterChainProxy`

![performBuild()](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS85LzI3LzE2ZDcwN2NmMjVjNTk3OTM?x-oss-process=image/format,png)

## `securityFilterChainBuilders`建造者列表是什么时候初始化的呢

> 这时候要注意到`WebSecurityConfigurerAdapter`，这个类的创建了`HttpSecurity`并放入了`securityFilterChainBuilders`

![securityFilterChainBuilders](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC84LzE2ZGFhOGEyY2Q3YTBhMDc?x-oss-process=image/format,png)

> `WebSecurityConfigurerAdapter`是一个安全配置器，我们知道建造者在`performBuild()`之前都会把循环调用`安全配置器`的`init();configure();`方法，然后创建`HttpSecurity`并放入自己的`securityFilterChainBuilders`里。

> PS: 前面已经提到了，在`WebSecurity`初始化时，会依次将`WebSecurityConfigurerAdapter`的子类放入`WebSecurity`。

```java
public abstract class WebSecurityConfigurerAdapter implements
		WebSecurityConfigurer<WebSecurity> {
}
public interface WebSecurityConfigurer<T extends SecurityBuilder<Filter>> extends
		SecurityConfigurer<Filter, T> {
}
```



# 3、FilterChainProxy的运行过程



## 上篇回顾

我们已经知道了`Spring Security`的核心过滤器的创建和原理，本文主要介绍核心过滤器`FilterChainProxy`是如何在tomcat的`ServletContext`中生效的。

## `ServletContext`如何拿到`FilterChainProxy`的过滤器对象

> 我们都知道，Bean都是存在Spring的Bean工厂里的，
> 而且在Web项目中`Servlet`、`Filter`、`Listener`都要放入`ServletContext`中。

> 看下面这张图，`ServletContainerInitializer`接口提供了一个`onStartup()`方法，用于在Servlet容器启动时动态注册一些对象到`ServletContext`中。

![ServletContainerInitializer](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS85LzI3LzE2ZDcwYmVhNDI5ZWI5YjA?x-oss-process=image/format,png)

> 官方的解释是：为了支持可以不使用`web.xml`。提供了`ServletContainerInitializer`，它可以通过`SPI`机制，当启动web容器的时候，会自动到添加的相应jar包下找到`META-INF/services`下以`ServletContainerInitializer`的全路径名称命名的文件，它的内容为`ServletContainerInitializer`实现类的全路径，将它们实例化。

> 通过下图可知，Spring框架通过META-INF配置了`SpringServletContainerInitializer`类

![META-INF配置](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS85LzI3LzE2ZDcwYzEwYzVmMjNmYzU?x-oss-process=image/format,png)

> `SpringServletContainerInitializer`实现了`ServletContainerInitializer`接口。
>
> 请注意该类上的`@HandlesTypes(WebApplicationInitializer.class)`注解.

> 根据Sevlet3.0规范，Servlet容器在调用`onStartup()`方法时，会以Set集合的方式注入`WebApplicationInitializer`的子类（包括接口，抽象类）。然后会依次调用`WebApplicationInitializer`的实现类的onStartup方法，从而起到启动web.xml相同的作用（添加`servlet`，`listener`实例到`ServletContext`中）。

![SpringServletContainerInitializer](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS85LzI3LzE2ZDcwY2EyMjUzZWFkY2Y?x-oss-process=image/format,png)

> `Spring Security`中的`AbstractSecurityWebApplicationInitializer`就是`WebApplicationInitializer`的抽象子类.
>
> 当执行到下面的`onStartup()`方法时，会调用`insertSpringSecurityFilterChain()`
>
> 将类型为`FilterChainProxy`名称为`springSecurityFilterChain`的过滤器对象用`DelegatingFilterProxy`包装，然后注入`ServletContext`

![AbstractSecurityWebApplicationInitializer](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS85LzI3LzE2ZDcxNTA4OTQ2NzRmZWI?x-oss-process=image/format,png)

## 运行过程

请求到达的时候，FilterChainProxy的dofilter()方法内部，会遍历所有的SecurityFilterChain，对匹配到的url，则一一调用SecurityFilterChain中的filter做认证或授权。

```java
public class FilterChainProxy extends GenericFilterBean {

    private final static String FILTER_APPLIED = FilterChainProxy.class.getName().concat(".APPLIED");
    private List<SecurityFilterChain> filterChains;
    private FilterChainValidator filterChainValidator = new NullFilterChainValidator();
    private HttpFirewall firewall = new StrictHttpFirewall();

    public FilterChainProxy() {
    }

    public FilterChainProxy(SecurityFilterChain chain) {
        this(Arrays.asList(chain));
    }

    public FilterChainProxy(List<SecurityFilterChain> filterChains) {
        this.filterChains = filterChains;
    }

    @Override
    public void afterPropertiesSet() {
        filterChainValidator.validate(this);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        boolean clearContext = request.getAttribute(FILTER_APPLIED) == null;
        if (clearContext) {
            try {
                request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
                doFilterInternal(request, response, chain);
            }
            finally {
                SecurityContextHolder.clearContext();
                request.removeAttribute(FILTER_APPLIED);
            }
        }
        else {
            doFilterInternal(request, response, chain);
        }
    }

    private void doFilterInternal(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        FirewalledRequest fwRequest = firewall.getFirewalledRequest((HttpServletRequest) request);
        HttpServletResponse fwResponse = firewall.getFirewalledResponse((HttpServletResponse) response);

        // 根据当前请求，获得一组过滤器链
        List<Filter> filters = getFilters(fwRequest);

        if (filters == null || filters.size() == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug(UrlUtils.buildRequestUrl(fwRequest)
                        + (filters == null ? " has no matching filters": " has an empty filter list"));
            }
            fwRequest.reset();
            chain.doFilter(fwRequest, fwResponse);
            return;
        }

        VirtualFilterChain vfc = new VirtualFilterChain(fwRequest, chain, filters);
        // 请求依次经过这组滤器链
        vfc.doFilter(fwRequest, fwResponse);
    }

    /**
     * 根据Request请求获得一个过滤器链
     */
    private List<Filter> getFilters(HttpServletRequest request) {
        for (SecurityFilterChain chain : filterChains) {
            if (chain.matches(request)) {
                return chain.getFilters();
            }
        }
        return null;
    }

    /**
     * 根据URL获得一个过滤器链
     */
    public List<Filter> getFilters(String url) {
        return getFilters(firewall.getFirewalledRequest((new FilterInvocation(url, null)
                .getRequest())));
    }

    /**
     * 返回一个过滤器链
     */
    public List<SecurityFilterChain> getFilterChains() {
        return Collections.unmodifiableList(filterChains);
    }

    // 过滤器链内部类
    private static class VirtualFilterChain implements FilterChain {
        private final FilterChain originalChain;
        private final List<Filter> additionalFilters;
        private final FirewalledRequest firewalledRequest;
        private final int size;
        private int currentPosition = 0;

        private VirtualFilterChain(FirewalledRequest firewalledRequest,
                FilterChain chain, List<Filter> additionalFilters) {
            this.originalChain = chain;
            this.additionalFilters = additionalFilters;
            this.size = additionalFilters.size();
            this.firewalledRequest = firewalledRequest;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response)
                throws IOException, ServletException {
            if (currentPosition == size) {
                if (logger.isDebugEnabled()) {
                    logger.debug(UrlUtils.buildRequestUrl(firewalledRequest)
                            + " reached end of additional filter chain; proceeding with original chain");
                }

                // Deactivate path stripping as we exit the security filter chain
                this.firewalledRequest.reset();

                originalChain.doFilter(request, response);
            }
            else {
                currentPosition++;

                Filter nextFilter = additionalFilters.get(currentPosition - 1);

                if (logger.isDebugEnabled()) {
                    logger.debug(UrlUtils.buildRequestUrl(firewalledRequest)
                            + " at position " + currentPosition + " of " + size
                            + " in additional filter chain; firing Filter: '"
                            + nextFilter.getClass().getSimpleName() + "'");
                }

                nextFilter.doFilter(request, response, this);
            }
        }
    }

    public interface FilterChainValidator {
        void validate(FilterChainProxy filterChainProxy);
    }

    private static class NullFilterChainValidator implements FilterChainValidator {
        @Override
        public void validate(FilterChainProxy filterChainProxy) {
        }
    }

}
```

































# Spring Security : 安全构建器HttpSecurity和WebSecurity的区别



`Spring Security`中，安全构建器`HttpSecurity`和`WebSecurity`的区别是 :

1. `WebSecurity`不仅通过`HttpSecurity`定义某些请求的安全控制，也通过其他方式定义其他某些请求可以忽略安全控制;
2. `HttpSecurity`仅用于定义需要安全控制的请求(当然`HttpSecurity`也可以指定某些请求不需要安全控制);
3. 可以认为`HttpSecurity`是`WebSecurity`的一部分，`WebSecurity`是包含`HttpSecurity`的更大的一个概念;
4. 构建目标不同
   1. `WebSecurity`构建目标是整个`Spring Security`安全过滤器`FilterChainProxy`,
   2. 而`HttpSecurity`的构建目标仅仅是`FilterChainProxy`中的一个`SecurityFilterChain`。

# 4、WebSecurity与HttpSecurity

## 上篇回顾

前面我们已经分析了`Spring Security`的核心过滤器`FilterChainProxy`的创建和运行过程，认识了`建造者`和`配置器`的作用。

现在我们知道`WebSecurity`作为一个`建造者`就是用来创建核心过滤器`FilterChainProxy`实例的。

`WebSecurity`在初始化的时候会扫描`WebSecurityConfigurerAdapter`配置器适配器的子类（即生成`HttpSecurity配置器`）。

所有的`配置器`会被调用`init();configure();`初始化配置，其中生成的每个`HttpSecurity配置器`都代表了一个过滤器链。

本篇要说的就是`HttpSecurity`作为一个`建造者`，是如何去建造出`SecurityFilterChain`过滤器链实例的！

> PS:如果有多个`WebSecurityConfigurerAdapter`配置器适配器的子类，会产生多个`SecurityFilterChain`过滤器链实例。`Spring Security Oauth2`的拓展就是这么做的，有机会再介绍

## spring security 怎么创建的过滤器

> 我们已经知道了`springSecurityFilterChain`（类型为`FilterChainProxy`）是实际起作用的过滤器链，`DelegatingFilterProxy`起到代理作用。

> 我们创建的`MySecurityConfig`继承了`WebSecurityConfigurerAdapter`。`WebSecurityConfigurerAdapter`就是用来创建过滤器链，重写的`configure(HttpSecurity http)`的方法就是用来配置`HttpSecurity`的。

```java
protected void configure(HttpSecurity http) throws Exception {
        http
            .requestMatchers() // 指定当前`SecurityFilterChain`实例匹配哪些请求
                .anyRequest().and()
            .authorizeRequests() // 拦截请求，创建FilterSecurityInterceptor
                .anyRequest().authenticated() // 在创建过滤器的基础上的一些自定义配置
                .and() // 用and来表示配置过滤器结束，以便进行下一个过滤器的创建和配置
            .formLogin().and() // 设置表单登录，创建UsernamePasswordAuthenticationFilter
            .httpBasic(); // basic验证，创建BasicAuthenticationFilter
}
12345678910
```

> 上面的`configure(HttpSecurity http)`方法内的配置最终内容主要是Filter的创建。

> `http.authorizeRequests()`、`http.formLogin()`、`http.httpBasic()`分别创建了`ExpressionUrlAuthorizationConfigurer`，`FormLoginConfigurer`，`HttpBasicConfigurer`。在三个类从父级一直往上找，会发现它们都是`SecurityConfigurer建造器`的子类。`SecurityConfigurer`中又有`configure()`方法。该方法被子类实现就用于创建各个过滤器，并将过滤器添加进`HttpSecurity`中维护的装有Filter的List中，比如HttpBasicConfigurer中的configure方法，源码如下：

> `HttpSecurity`作为`建造者`会把根据api把这些`配置器`添加到实例中

![HttpBasicConfigurer](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC85LzE2ZGFlODQ5M2NhYzU1Mjg?x-oss-process=image/format,png)

![getOrApply](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC85LzE2ZGFlODRmOTQ4NmM0ODk?x-oss-process=image/format,png)

![apply](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC85LzE2ZGFlODVkNDFhYmQzNWY?x-oss-process=image/format,png)

> 这些`配置器`中大都是创建了相应的过滤器，并进行配置，最终在`HttpSecurity`建造`SecurityFilterChain`实例时放入过滤器链

![configure()](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC85LzE2ZGFlODc2ZmY3MGU3OTg?x-oss-process=image/format,png)



# 5、认证授权过程

## 上篇回顾

上篇介绍了`HttpSecurity`如何建造过滤器链，本文主要介绍几个主要的过滤器。

## 认证过滤器 `UsernamePasswordAuthenticationFilter`

参数有`username`,`password`的，走`UsernamePasswordAuthenticationFilter`，提取参数构造`UsernamePasswordAuthenticationToken`进行认证，成功则填充`SecurityContextHolder的Authentication`

`UsernamePasswordAuthenticationFilter`实现了其父类`AbstractAuthenticationProcessingFilter`中的`attemptAuthentication`方法。这个方法会调用认证管理器`AuthenticationManager`去认证。

![attemptAuthentication](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOTY4ZDc2NmQzY2Ey?x-oss-process=image/format,png)

> `AbstractAuthenticationProcessingFilter`中的`doFilter()`方法，会判断每个请求是否需要认证。
>
> 不需要认证的请求直接放行，需要的认证的会被拦下。

![doFilter](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOTZhOTlkM2ZlNjQy?x-oss-process=image/format,png)
![requiresAuthentication](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOTZkOTYwNzE0M2M5?x-oss-process=image/format,png)

> 判断是否需要认证是怎么做的呢？
>
> 其实是我们在调用`httpSecurity.formLogin().permitAll()`时设置的。

![formLogin](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOTczNjk4NzNiODE1?x-oss-process=image/format,png)

![FormLoginConfigurer](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOTczZmFiYWVmZTEx?x-oss-process=image/format,png)

![UsernamePasswordAuthenticationFilter](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOTc0NDMyMWY1YmMx?x-oss-process=image/format,png)

![AbstractAuthenticationProcessingFilter](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOTc1MGNjYjA5ZDFk?x-oss-process=image/format,png)

> `ProviderManager`是认证管理器`AuthenticationManager`的默认实现
>
> 通过提供不同的`AuthenticationProvider`实现类，可以通过多种方式进行认证
>
> 其内部会调用`authenticate(Authentication authentication)`遍历providers，调用`provider.authenticate()`来尝试认证
>
> 我们可以实现`AuthenticationProvider`接口，重写`authenticate()`方法，来查询数据库对用户名密码做认证

![ProviderManager](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOTdkOGE0MTcxOWEy?x-oss-process=image/format,png)

> PS: 上面的`parent`其实就是存放的我们自定义编写的provider的认证管理器。这里就不贴出来了

## 认证过滤器 `BasicAuthenticationFilter`

header里头有`Authorization`，而且value是以`Basic`开头的，则走`BasicAuthenticationFilter`，提取参数构造`UsernamePasswordAuthenticationToken`进行认证，成功则填充`SecurityContextHolder的Authentication`

![BasicAuthenticationFilter](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMi8xNmRiZDk1MjUzYWMwZDRm?x-oss-process=image/format,png)

## 认证过滤器 `AnonymousAuthenticationFilter`

给没有登陆的用户，填充`AnonymousAuthenticationToken`到`SecurityContextHolder`的`Authentication`

![AnonymousAuthenticationFilter](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMi8xNmRiZDk2NDZiNzQyYjNj?x-oss-process=image/format,png)

## 授权过滤器 `AbstractSecurityInterceptor`

> 默认的过滤器是`FilterSecurityInterceptor`,继承了`AbstractSecurityInterceptor`实现了`Filter`接口
>
> 我们一般直接继承这个过滤器或者继承他的父类，自定义一个`AuthorizeSecurityInterceptor`。
>
> 目的是为了注入自定义的授权管理器`AccessDecisionManager`、和权限元数据`FilterInvocationSecurityMetadataSource`

> `FilterSecurityInterceptor`是在`WebSecurityConfigurerAdapter`的`init()`里配置的

![img](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOWM0ZjcwZTA4YjMx?x-oss-process=image/format,png)

> `FilterSecurityInterceptor`中的`doFilter()`会调用`super.beforeInvocation(fi)`方法，内部调用授权管理器做授权

![AuthorizeSecurityInterceptor](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOWJkZjQ2NzFlNDBk?x-oss-process=image/format,png)

![doFilter](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOWMwM2QyNTA2MThi?x-oss-process=image/format,png)

![invoke](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOWJmOTYwN2I5Y2Vm?x-oss-process=image/format,png)

![beforeInvocation](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOWI2MTBmMDA3NWNh?x-oss-process=image/format,png)

> 自定义的`AuthorizeSecurityMetadataSource`实现了`FilterInvocationSecurityMetadataSource`的`getAttributes()`方法，可以根据url获取对应的角色列表

![AuthorizeSecurityMetadataSource](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOWI3NjcwMzk2Mzc3?x-oss-process=image/format,png)

> 自定义的`AuthorizeAccessDecisionManager`实现了`AccessDecisionManager`，实现了`decide()`方法来判断当前用户是否有此url的权限

![AuthorizeAccessDecisionManager](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMS8xNmRiOWI4MWU0NmVjMWU0?x-oss-process=image/format,png)

> 框架默认的`AccessDecisionManager`通过投票决策的方式来授权

- `AffirmativeBased`（spring security默认使用）

  只要有投通过（`ACCESS_GRANTED=1`）票，则直接判为通过。如果没有投通过票且反对（`ACCESS_DENIED=-1`）票在1个及其以上的，则直接判为不通过。

  ![AffirmativeBased](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMi8xNmRiZGNmNjJmMGE0NTFl?x-oss-process=image/format,png)
  ![vote](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAxOS8xMC8xMi8xNmRiZGQwMjJkNGFjMzQ3?x-oss-process=image/format,png)

- `ConsensusBased`（少数服从多数）

  通过的票数大于反对的票数则判为通过;通过的票数小于反对的票数则判为不通过;通过的票数和反对的票数相等，则可根据配置allowIfEqualGrantedDeniedDecisions（默认为true）进行判断是否通过。

- `UnanimousBased`（反对票优先）

  无论多少投票者投了多少通过（ACCESS_GRANTED）票，只要有反对票（ACCESS_DENIED），那都判为不通过;如果没有反对票且有投票者投了通过票，那么就判为通过.

## 其他过滤器

`ExceptionTranslationFilter`：

```
该过滤器主要用来捕获处理spring security抛出的异常，异常主要来源于FilterSecurityInterceptor
```