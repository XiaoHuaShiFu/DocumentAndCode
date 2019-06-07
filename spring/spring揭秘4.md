# 23、Spring MVC初体验

1. Spring MVC

   - DispatcherServlet作为Front Controller，接收并处理所有Web请求，然后委派给下一级控制器。
   - Controller作为Page Controller。

2. Spring MVC架构

   - Spring MVC配置结构

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/Spring%20MVC%E9%85%8D%E7%BD%AE%E7%BB%93%E6%9E%84%E5%9B%BE.jpg?raw=true)

     - ContextLoaderListener与/WEB-INF/applicationContext.xml

       - 示例：

         ```xml
         <listener>
             <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
         </listener>
         <context-param>
             <param-name>contextConfigLocation</param-name>
             <param-value>
                 classpath:applicationContext.xml,classpath:applicationContext-module1.xml
             </param-value>
         </context-param>
         ```

       - 用于加载应用程序顶层的WebApplicationContext（ROOT WebApplicationContext）：用于记载DataSource、DAO、Services等。

       - 可以再param-value里指定多个配置文件路径。

       - WebApplicationContext构造完成之后会 绑定到ServletContext。

     - DispatcherServlet与/WEB-INF/xxx-servlet.xml：用来加载Web请求中涉及的各个组件，包括HandlerMapping、Controller、ViewResolver等。

       - 示例：

         ```xml
         <!--顶级Controller  会通过HandlerMappings把请求转发给次级Controller -->
         <!-- 之后通过返回的ModelAndView找到对应的ViewResolver 解析ModelAndView 再把View返回给客户端 -->
         <servlet>
             <servlet-name>dispatcher</servlet-name>
             <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
             <init-param>
                 <param-name>contextConfigLocation</param-name>
                 <param-value>/WEB-INF/dispatcher-servlet.xml</param-value>
             </init-param>
             <!--如果大于0Servlet启动时会调用servlet-class的init方法-->
             <load-on-startup>1</load-on-startup>
         </servlet>
         
         
         <!--*.do的请求都会被SpringMVC拦截-->
         <servlet-mapping>
             <servlet-name>dispatcher</servlet-name>
             <url-pattern>*.do</url-pattern>
         </servlet-mapping>
         ```

       - DispatcherServlet会加载xxx-servlet.xml里的WebApplicationContext，并加载到ContextLoaderListener加载的顶层WebApplicationContext（作为父容器）里。

       - 可以在init-param里指定多个配置文件。

   - 顶层的WAC和DispatcherServlet的WAR之间的逻辑依赖关系

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/%E9%A1%B6%E5%B1%82%E7%9A%84WAC%E5%92%8CDispatcherServlet%E7%9A%84WAR%E4%B9%8B%E9%97%B4%E7%9A%84%E9%80%BB%E8%BE%91%E4%BE%9D%E8%B5%96%E5%85%B3%E7%B3%BB.jpg?raw=true)

# 24、Spring MVC各种组件

1. HandlerMapping：所有的次级控制器称为Handler，HandlerMapping就是处理请求到Handler之间的映射。

   - 可用的HandlerMapping

     - BeanNameUrlHandlerMapping：请求Url路径到Controller的beanName的一对一匹配。

     - SimpleUrlHandlerMaping：Url到Controller的映射，可用使用*通配符。

       - 示例：

         ```xml
         <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
             <property name="prefix" value="/WEB-INF/jsp/"/>
             <property name="suffix" value=".jsp"/>
         </bean>
         ```

   - HandlerMapping执行顺序：DispatcherServlet按优先级顺序遍历HandlerMapping，直到获取一个可用的Handler为止。Spring MVC里的HandlerMapping都实现了Ordered接口。默认为Integer.MAX_VALUE。

     - 示例：

       ```xml
       <bean id="handlerMapping" class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
           <property name="order" value="1"/>
           <property name="mappings">
               <props>
                   <prop key="/list.do">userController</prop>
               </props>
           </property>
       </bean>
       ```

2. Controller：请求参数提取、请求编码设定、国际化信息处理、Session数据的管理。

   ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/Controller%E5%B1%82%E6%AC%A1%E4%BD%93%E7%B3%BB.jpg?raw=true)

   - AbstractController：使用模板方法模式，只需在handleRequestInternal(request, response)模板方法实现具体Web请求的处理逻辑。

     - 管理当前Controller所支持的请求方法类型（GET/POST）；
     - 管理页面的缓存设置，即是否浏览器缓存当前页面；
     - 管理执行流程在会话上的同步

   - MultiActionController：对同一对象的CRUD（Create-Read-Update-Delete），对同一对象或多个对象的一组查询操作。

     - 请求参数到Command对象的绑定。

     - 通过Validator的数据验证。在Command对象绑定的过程中MultiActionController会调用注册的Validator进行数据验证。

     - 细化异常处理方法。可用定义一系列异常处理方法，用于Web请求过程中所抛出的特定类型的异常。

     - MultiActionController的方法签名的要求：

       - ```java
         public (ModelAndView | Map | String | void) actionName(HttpServletRequest request, HttpServletResponse response, [,HttpSession] [,AnyObject]);
         ```

       - 前两个参数是必须的，第三个参数是可选的。

     - 使用MethodNameResolver来进行方法到Web请求的映射

       - MultiActionController里的代码：

         ```java
         @Override
         protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
               throws Exception {
            try {
               String methodName = this.methodNameResolver.getHandlerMethodName(request);
               return invokeNamedMethod(methodName, request, response);
            }
            catch (NoSuchRequestHandlingMethodException ex) {
               return handleNoSuchRequestHandlingMethod(ex, request, response);
            }
         }
         ```

       - InternalPathMethodNameResolver：默认MethodNameResolver。提取URL的最后一个/之后的部分并取出扩展名，作为要返回的方法名字。

         - 可用在配置文件里设置前缀、后缀等。

         - 示例：

           ```xml
           <bean id="internalPathMethodNameResolver" class="org.springframework.web.servlet.mvc.multiaction.InternalPathMethodNameResolver">
               <property name="prefix" value="xhsf_"/>
           </bean>
           ```

       - PropertiesMethodNameResolver：URL与方法名之间的映射可以自己指定。可以使用*通配符。

         - 示例：

           ```xml
           <bean id="methodNameResolver" class="org.springframework.web.servlet.mvc.multiaction.PropertiesMethodNameResolver">
               <property name="mappings">
                   <value>
                       /xhsf_list.do = list
                   </value>
               </property>
           </bean>
           
           <bean name="userController" class="com.xuexi.controller.UserMultiActionController">
               <property name="methodNameResolver" ref="methodNameResolver"/>
           </bean>
           ```

       - ParameterMethodNameResolver：可以使用请求中某个参数的值作为映射的方法名，也可以使用请求中的一组参数来映射处理方法名称。

         - 使用某个参数：默认是action=xxxx，可以用paramName参数自定义参数名。

           ```xml
           <bean id="methodNameResolver" class="org.springframework.web.servlet.mvc.multiaction.ParameterMethodNameResolver">
                   <property name="paramName" value="methodName"/>
           </bean>
           ```

         - 根据一组参数作为映射后的方法名：

           ```xml
           <bean id="methodNameResolver" class="org.springframework.web.servlet.mvc.multiaction.ParameterMethodNameResolver">
               <property name="methodParamNames" value="list,delete,update"/>
               <property name="defaultMethodName" value="list"/>
           </bean>
           ```

   - SimpleFormController：有自动数据绑定、和Validator数据验证功能。

     - 数据绑定

       - 过程：Spring MVC获取请求中的所有参数名称，然后遍历它，获取对应的值，放入一个值对象中。然后根据Command对象中的各个域属性定义的类型进行数据转换，然后设置到Command对象上。

       - 使用BeanWrapperImpl所以来的一系列PropertyEditor进行数据转换。

       - list和map转换格式

         ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/list%E5%92%8Cmap%E5%8F%82%E6%95%B0%E8%BD%AC%E6%8D%A2%E6%A0%BC%E5%BC%8F.png?raw=true)

     - 数据验证：核心类为Validator（负责实现具体验证逻辑）和Errors（承载验证的错误信息）。

       - 示例：

         ```java
         public class UserValidator implements Validator {
             @Override
             //确认验证范围
             public boolean supports(Class<?> clazz) {
                 return ClassUtils.isAssignable(clazz, User.class);
             }
         
             @Override
             public void validate(Object target, Errors errors) {
                 User user = (User) target;
         		//被验证对象本身用reject
                 if (user == null) {
                     errors.reject("user not be null");
                 }
         		//至少判断是否为空用ValidationUtils.rejectIfEmpty
                 ValidationUtils.rejectIfEmpty(errors, "id", "id.empty");
                 ValidationUtils.rejectIfEmpty(errors, "username", "username.empty");
                 ValidationUtils.rejectIfEmpty(errors, "idUserNameMap", "idUserNameMap.empty");
         
                 if ((user != null ? user.getPassword() : null) == null) {
                     //属性域用rejectValue
                     errors.rejectValue("password", "password.is.null", "password not be null");
                 } else if (user.getPassword().length() < 6) {
                     errors.rejectValue("password", "password.length.must.be.great.6", "password length must be great 6");
                 }
         
                 List<String> itemList = user.getItemList();
                 if (CollectionUtils.isNotEmpty(itemList)) {
                     for (int i = 0; i < itemList.size(); i++) {
                         //列表嵌套要先调用errors.pushNestedPath()方法，否则Errors在记录错误信息时会以为是Command对象上的属性，会报错
                         errors.pushNestedPath("itemList[" + i + "]");
                         if ("".equals(itemList.get(0))) {
                             errors.rejectValue("", "item.not.be.null", "item can not be null");
                         }
                         errors.popNestedPath();
                     }
                 }
         
             }
         }
         ```

         ```java
         //Errors
         BindException errors = new BindException(user, "user");
         //Validator
         UserValidator userValidator = new UserValidator();
         //认证
         ValidationUtils.invokeValidator(userValidator, user, errors);
         ```

   - AbstractCommandController

   - ParameterizableViewController

   - UrlFilenameViewController

   - ServletForwardingController和ServletWrappingController

3. ModelAndView

4. ViewResolver（视图定位器）：可以通过继承AbstractCachingViewResolver实现ViewResolver，该Resolver默认启动View缓存功能，可以通过setCache(false)关闭。

   - 面向单一视图类型的ViewResolver：都继承自UrlBasedViewResolver。通过在指定位置寻找模板文件，构造对应的View实例并返回。ViewResolver和View之间的关系是1对1的。
     - InternalResourceViewResolver：jsp模板类型的视图映射。DispatcherServlet默认使用。
     - FreeMarkerViewResolver/VelocityViewResolver
     - JasperReportsViewResolver
     - XsltViewResolver
     - 只需要在xxx-servlet.xml配置文件中配置就可以使用。
   - 面向多视图的ViewResolver
     - ResourceBundleViewResolver
     - XmlViewResolver
     - BeanNameViewResolver
   - ViewResolver查找序列：通过Ordered接口。只要有一个ViewResolver返回非空View实例，就停止遍历。
   - View：通过render()方法去渲染视图，也就是将模板与数据合并在一起，然后返回给客户端。
     - View的属性
       - contenType：内容类型。
       - requestContextAttribute：是RequestContext对应的属性名。
       - staticAttributes：视图的静态属性，如页眉，页脚的固定信息等。
     - 使用JSP技术的View实现
     - 使用通用模板技术的View是实现
     - 面向二进制文档的View实现
     - 面向JsperReport的View实现
     - 面向XSLT技术的View实现
     - 自定义View实现：继承AbstractUrlBasedView

# 25、Spring MVC的其他成员

1. 文件上传与MultipartResolver
   - MultipartResolver流程
     - Web请求到达时DispatcherServlet将检查自己的WebApplicationContext中是否有名称为multipartResolver的MultipartResolver（由DispatcherServlet的MULTIPART_RESOLVER_BEAN_NAME决定）实例。如果有则调用MultipartResolver的isMultipart方法检查当前请求是否为multipart类型，如果是则DispatcherServlet调用MultipartResolver的resolveMultipart(request)方法返回一个MultipartHttpServletRequest供后续流程使用，否则然后HttpServletRequest。
     - MultipartHttpServletRequest继承了HttpServletRequest和MultipartRequest接口。
     - MultipartResolver实现类
       - CommonsMultipartResolver：使用Commons FIleUpload类库实现。
       - CosMultipartResolver：使用Oreilly Cos类库实现
     - 使用方法：在配置文件中配置MultipartResolver实现类bean。
     - DispatcherServlet将会调用MultipartHttpServletRequest的cleanupMultipart()方法，释放处理文件上传过程中所占用的系统资源。
     - Spring MVC提供了ByteArrayMultipartFileEditor负责将MultipartFile转换成byte[]数组，StringMultipartFileEditor，负责将MultipartFile转换成String。
   
2. Handler与HandlerAdaptor：HandlerAdaptor可以执行Handler的方法。
   - 流程：DIspatcherSerlvet通过HandlerMapping获得一个Handler之后，询问HandlerAdaptor的supports()方法，如果返回true，DispatcherServlet则调用HandlerAdaptor的handle()方法，将Handler作为参数，方法执行后返回ModelAndView。
   - 可用的Handler类型
     - Controller：其HandlerAdaptor为SimpleControllerHandlerAdapter。
     - 基于注解的Handler：其HandlerAdaptor为AnnotationMethodHandlerAdapter
   - HandlerAdaptor：只是简单的调用“认识”这个Handler的方法，然后将处理结果转换成ModelAndView。
     - supports()方法决定是否认识这个Handler。
     - handler()：执行Controller的方法
   - 告知Handler与HandlerAdpator的存在
     - Spring MVC将根据类型检测容器内可用的HandlerAdaptor。只要把HandlerAdaptor实现类添加到WebApplicationContext容器。
     - 默认使用以下的HandlerAdaptor：
       - HttpRequestHandlerAdapter。
       - SimpleControllerHandlerAdapter。
       - ThrowawayControllerhandlerAdapter。
       - AnnotationMethodHandlerAdapter。
   
3. 框架内的处理流程拦截与HandlerInterceptor：存在于HandlerExecutionChain里面（Handler和HandlerInterceptor），可以用于再Handler执行前后对处理流程进行拦截。

   ```java
   public interface HandlerInterceptor {
       //再HandlerAdaptor调用Handler处理Web请求之前执行。通过boolean返回值表示是否继续之后后续处理流程。
       //如果true，则继续处理下一个HandlerInterceptor的preHandle(..)方法或者执行Handler。
       //如果false，则不再执行后续流程，即认为preHandle(..)里已经处理完Web请求。也可以通过抛出相应异常的方式来达到这个效果。
      boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
          throws Exception;
   	//再HandlerAdaptor调用具体的Handler处理完Web请求后，且视图的解析和渲染之前。即对ModelAndView做进一步处理。不阻断后续流程。
      void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception;
   	//整个处理流程结束之后，不管如何，都会执行次方法。可以用于捕获异常，清理资源。不阻断后续流程。
      void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception;
   }
   ```

   1. 可用的HandlerInterceptor实现

      - UserRoleAuthorizationInterceptor：通过HttpServletRequest的isUserInRole()方法，使用一组指定的用户对当前请求进行验证。如果验证不通过会返回403状态码。可用通过覆写handleNotAuthrized(..)方法改变默认行为。

      - WebContentInterceptor

        - 检查请求方法类型是否在支持方法之列：可用通过设置supportMethods属性，如果超出范围则抛出HttpRequestMethodNotSupportedException阻断后续处理流程。
        - 检查必要的Session实例：如果requireSession属性为true，则当前请求的session需要已经存在。否则抛出HttpSessionRequiredException阻断后续处理流程。
        - 检查缓存时间并通过设置相应的HTTP头(Header)的方式控制缓存行为：cacheSeconds设置请求内容的缓存时间。
          - 可以进一步通过useCacheControlHader或者useExpiresHeader属性，进一步明确是使用HTTP1.1的CacheControl指令还是HTTP1.0的Expires指令。
          - 可以通过cacheMappings进一步指定不同请求与其缓存时间之间的细粒度的映射关系。

      - 自定义HandlerInterceptor：继承HandlerInterceptorAdapter。

        - 示例：配置Interceptor也可以通过HandlerMapping上的interceptors属性设置。

          ```java
          @Component
          public class TestInterceptor extends HandlerInterceptorAdapter {
              @Override
              public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                  System.out.println("preHandle is be call");
                  return super.preHandle(request, response, handler);
              }
          }
          ```

          ```xml
          <mvc:interceptors>
              <mvc:interceptor>
                  <mvc:mapping path="/user/**"/>
                  <ref bean="testInterceptor"/>
              </mvc:interceptor>
              <mvc:interceptor>
                  <mvc:mapping path="/user/**"/>
                  <ref bean="userRoleAuthorizationInterceptor"/>
              </mvc:interceptor>
          </mvc:interceptors>
          ```

   2. HandlerInterceptor原理：每个HandlerMapping都有一个interceptors属性用于设置interceptor，可以为每个特定的HandlerMapping指定interceptors从而细粒化的使用interceptors。

   3. HandlerInterceptor之外的选择：使用Servlet的标准组件Filter。

      ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/Servlet%20Filter%20%E5%92%8C%20HandlerInterceptor%E7%9A%84%E5%85%B3%E7%B3%BB%E7%A4%BA%E6%84%8F%E5%9B%BE.jpg?raw=true)

      - Filter在DispatcherServlet之前。

      - Filter通常被映射到Java Web程序中的某个servletr，或者一组符合某种URL匹配模式的访问资源上。

      - Filter的需要在web.xml中配置，生命周期由Web容器管理。

      - DelegatingFilterProxy承担web.xml中的FIlter的使命，可以通过它把具体工作委托给Filter对象，这些Filter存在WebApplicationContext中。

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/DelegatingFilterProxy%E4%B8%8E%E5%85%B6Filter%E5%A7%94%E6%B4%BE%E5%AF%B9%E8%B1%A1%E4%B9%8B%E9%97%B4%E7%9A%84%E5%85%B3%E7%B3%BB.jpg?raw=true)

        - 示例：

          ```java
          @Component
          public class TestFilter implements Filter {
              @Override
              public void init(FilterConfig filterConfig) throws ServletException {
          
              }
          
              @Override
              public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                  System.out.println("this is doFilter");
                  chain.doFilter(request, response);
              }
          
              @Override
              public void destroy() {
          
              }
          }
          ```

          ```xml
          <filter>
              <filter-name>testFilter</filter-name>
              <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
          </filter>
          <filter-mapping>
              <filter-name>testFilter</filter-name>
              <url-pattern>/*</url-pattern>
          </filter-mapping>
          ```

        - 配置：通过\<filter-name\>与bean-name进行匹配

        - Filter的生命周期：通过设置DelegatingFilterProxy的targetFilterLifecycle属性为true使Filter的生命周期由WebApplicationContext管理转向原始的Web容器管理。也就是DelegatingFilterProxy会调用Filter的初始化和摧毁方法。

          ```xml
          <filter>
              <filter-name>testFilter</filter-name>
              <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
              <init-param>
                  <param-name>targetFilterLifecycle</param-name>
                  <param-value>true</param-value>
              </init-param>
          </filter>
          ```

4. 异常处理与HandlerExceptionResolver：Handler执行过程中出现异常，那么HandlerExceptionResolver将接手处理并返回ModelAndView形式返回（异常信息）。

   ```java
   public interface HandlerExceptionResolver {
      ModelAndView resolveException(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex);
   }
   ```

   - SimpleMappingExceptionResolver：使用properties类型的exceptionMappings属性指定具体映射关系。会寻找与当前异常类型最接近的映射（使用indexOf()），所以推荐使用全限定类名。

     - 示例：

       ```xml
       <bean id="simpleMappingExceptionResolver" class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
           <property name="exceptionMappings">
               <props>
                   <!-- 在WEB-INF/jsp/**.jsp -->
                   <prop key="java.lang.Exception">/error</prop>
               </props>
           </property>
       </bean>
       ```

     - defaultErrorView：指定一个默认错误信息页面。

     - defaultStatusCode：异常情况下默认返回给客户端的HTTP状态码。

     - exceptionAttribute：可以设置异常实例并在页面中显示。如果不想公开可以设置为null。

     - mappedHandlers和mappedHandlerClasses：可以指定捕获哪些handler抛出的异常。

     - order：通过mappedHandlers和mappedHandlerClasses查看是否有处理的权力，如果没有转交给下一个，通过order属性设置谁先进行检查。

5. LocalResolver：略

# 26、基于注解的Controller

1. 实质上也是一个handler，通过反射获取注解来与相应的请求路径匹配。

2. Controller原型分析

   - 获取流程
     - 通过扫描Classpath，获取所有标注了@Controller的对象。扫描Classpath获取@Controller的对象的工作由\<context:component-scan/\>来完成。
   - controller与HandlerAdaptor：可以确定具体由哪个方法来处理Web请求。
     - HandlerAdaptor作用：将请求信息绑定到Controller实例，然后调用相应的处理方法，并将结果以ModelAndView的形式返回给DispatcherServlet使用。

3. @Controller详解

   - @Controller作用

     - 作为bean可以用于Ioc容器的注入和被注入。
     - 可以被HandleMapping发现并用于Web请求的处理。

   - @RequestMapping作用：映射请求路径和被HandlerAdaptor处理。

     - 属性

       - params：与请求参数匹配

         ```java
         @RequestMapping(value = "login.do",params = "locale=en")
         //则请求应该包含参数locale且等于en
         //如
         //http://localhost:8080/user/login.do?username=xhsf&locale=en
         ```

         ```java
         //通过是否由某个参数名来决定由哪个请求处理
         @RequestMapping(value = "login.do",params = "delete")
         public Map list(String username) {
             System.out.println(username);
             System.out.println("delete");
             return null;
         }
         
         @RequestMapping(value = "login.do",params = "update")
         public Map list1(String username) {
             System.out.println(username);
             System.out.println("update");
             return null;
         }
         ```

   - 请求方法签名

     - HttpServletRequest/response/session等。
     - WebRequest/response/session等。
     - Reader/Writer等。
     - ModelMap/Map等。
     - @RequestParam或者@ModelAttribute所标注的方法参数
       - @RequestParam可以指定required属性，表示是否一定要这个参数。
     - BindingResult/Errors：用于绑定错误信息，需要紧跟在command对象ReportSettings之后。
     - SessionStatus：用于管理请求之后的Session状态，比如清除Session中的数据。

   - 方法返回值

     - ModelAndView
     - String
     - ModelMap
     - void

   - @InitBinder标注的方法：在数据绑定之前先调用绑定了@InitBinder的方法。一定有WebDataBinder参数。返回值为void。也就是对每个请求参数调用这个init方法。
   
     - 示例：
   
       ```java
       @InitBinder
       public void init(WebDataBinder dataBinder) {
           System.out.println("init");
           System.out.println(dataBinder.getBindingResult().getModel());
       }
       ```
   
     - 自定义WebBindingInitializer：为HandlerAdapter指定一个WebBindingInitializer可以避免为每个Controller指定@InitBinder的方法。
   
       - 绑定在RequestMappingHandlerAdapter上。
       - 实现类要实现WebBindingInitializer接口，并配置到RequestMappingHandlerAdapter上。
   
   - @ModelAttribute
   
     - 用于方法上：返回的数据会被加到模型数据里。
     - 用于参数上：获取数据绑定的结果。
   
   - @SessionAttribute管理Session数据：标注什么数据需要通过session进行管理

# 27、Spring MVC扩展

1. 略