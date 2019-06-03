# 7、AOP

1. Java平台上的AOP实现机制
   - 动态代理：Spring默认使用。
   - 动态字节码增强：在动态代理无法满足的情况下使用CGLIB库的动态字节码增强来实现AOP的功能扩展。
   - 自定义类加载器：所有Java程序的class都需要通过相应的类加载器（ClassLoader）加载到Java虚拟及后才能运行，默认的类加载器会读取class字节码文件，然后按照class字节码规范，解析并加载这些class文件到虚拟机运行。
     - 自定义类加载器在class文件加载到虚拟机运行 期间，将横切逻辑织入到class文件中。
   - AOL扩展：学习AOL语言。
   - Java代码生成：过时。
2. AOP国家的公民
   - Joinpoint：切入点
     - 方法调用（Method Call）：方法被调用时的程序执行点。（先于方法执行）
     - 方法调用执行（Method Call execution）：方法内部执行开始时间点。
     - 构造方法调用（Constructor Call）：对某个对象其构造方法进行初始化的时间点。
     - 构造方法执行（Constructor Call Execution）：构造方法内部执行的开始时间点。
     - 字段设置（Field Set）：通过setter或直接被设置的时间点。
     - 字段获取（Field Get）：通过getter或直接获取的时间点。
     - 异常处理执行（Exception Handler Execution）：异常抛出后，对异常进行处理逻辑执行的时间点。
     - 类初始化（Class initialization）：类中内静态类型或静态块的初始化时间点。
   - PointCut：表示具体的Joinpoint。
     - 直接指定Joinpoint所在方法名称：方法级别，且需要一个一个织入，只适合Joinpoint较少且较简单的情况。
     - 正则表达式：
     - 使用特定的Pointcut表述语言：Spring也支持，很强大，但难学。
     - Pointcut运算：可以进行逻辑运算（与或非）
   - Advice：单一横切关注点逻辑的载体，代表将会织入到Joinpoint的横切逻辑。
     - Before Advice：在Joinpoint指定位置之前的Advice逻辑。
       - 可以抛出异常来中断当前程序流程。
       - 可以做一些系统的初始化工作。
     - After Advice：在Joinpoint指定位置之后的Advice逻辑。
       - After returning Advice：在当前Joinpoint执行完正常流程后，After returning Advice才会执行。比如方法执行返回而没有抛出异常。
       - After throwing Advice：只有当前Joinpoint执行过程中抛出异常才会执行。
       - After Advice：不管怎么都会执行。类似于Java中的finally。
     - Around Advice：在Joinpoint的前后都执行的Advice逻辑。
     - Introduction：为原有对象添加新的特性或行为。
       - 在AspectJ上的性能很好，通过静态织入的形式。
       - JBoss AOP 或 Spring AOP采用动态织入的AOP实现，性能上要差一些。
   - Aspect：可以包含多个Pointcut及Advice定义。
   - 织入和织入器：weaving和waver。
   - 目标对象：要被织入横切逻辑的对象。

# 8、Spring AOP概述及其实现机制

1. Spring AOP的实现机制
   - 代理模式
     - 动态代理：Proxy动态生成的代理对象上相应的接口方法被调用时，对应的InvocationHandler就会拦截相应的方法调用，并进行相应处理。
       - InvocationHandler是实现横切逻辑的地方，是横切逻辑的载体，跟Advice是一样的。
       - 默认情况下，如果Spring发现目标对象实现了相应Interface，则采用动态代理机制为其生成代理对象实例。
   - 动态字节码生成
     - 对目标对象进行继承扩展，为其生成相应的子类，子类通过覆写来扩展父类的行为。
     - 通过将横切逻辑放到子类中，然后让系统使用扩展后的目标对象的子类。
     - 可以对实现了某种接口的类，或没有实现任何接口的类进行扩展。

# 9、Spring AOP一世

1. Spring AOP中的Joinpoint

   - 只提供方法拦截。
   - 属性代码规范。

2. Spring AOP中的Pointcut

   - 如果Pointcut类型为TruePointcut，默认会对系统中的所有对象，以及对象上所有被制裁的Joinpoint进行匹配。
   
     - Pointcut接口
   
       ```java
       public interface Pointcut {
           Pointcut TRUE;
           ClassFilter getClassFilter();
           MethodMatcher getMethodMatcher();
           static default {
               TRUE = TruePointcut.INSTANCE;
           }
       }
       ```
   
     - ClassFilter接口：对Joinpoint所处的对象进行Class级别的类型匹配。
   
       ```java
       public interface ClassFilter {
          boolean matches(Class<?> clazz);
          ClassFilter TRUE = TrueClassFilter.INSTANCE;
       }
       
       //例如想只匹配Foo类型的class，可以定义
       class FooClassFilter implements ClassFilter{
           @Override
           public boolean matches(Class<?> clazz) {
               return Foo.class.equals(clazz);
           }
       }
       ```
   
     - MethodMatcher接口：对方法进行匹配。
   
       ```java
       public interface MethodMatcher {
           //isRuntime返回false，不会具体考虑Joinpoint的方法参数，这种类型的MethodMatcher称为StaticMethodMatcher。只有此方法被执行。
           boolean matches(Method method, Class<?> targetClass);
           //如果两个参数的matches返回false，那么三个参数的matches则不会再执行。
           boolean isRuntime();
           //isRuntime返回true，标明MethodMatcher将每次都对方法调用的参数进行匹配检查，这种类型的MethodMatcher称之为DynamicMethodMatcher。因为每次都要检查方法参数类型，所有无法进行缓存。
           boolean matches(Method method, Class<?> targetClass, Object[] args);
          MethodMatcher TRUE = TrueMethodMatcher.INSTANCE;
       }
       ```
   
     - Pointcut局部“族谱”
   
       ![](https://github.com/XiaoHuaShiFu/img/blob/master/Pointcut%E5%B1%80%E9%83%A8%E2%80%9C%E6%97%8F%E8%B0%B1%E2%80%9D.jpg?raw=true)
   
   - 常见Pointcut
   
     - NameMatchMethodPointcut：根据方法名进行匹配，可以使用*作为通配符。
     
       ```java
       NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
       pointcut.setMappedName("*Resource");
       pointcut.setMappedName("setResource");
       pointcut.matches(UserVo.class.getMethod("getResource", null), UserVo.class);
       ```
     
     - JdkRegexpMethodPointcut和Perl5RegexpMethodPointcut：通过pattern指定正则表达式的匹配模式。
     
       - 使用正则表达式来匹配Joinpoint所处的方法时，正则表达式的匹配模式必须匹配整个方法签名（Method Signature），如com.springjiemi.dao.UserDao
     
       - 示例：
     
         ```java
         JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
         pointcut.setPattern(".*Resource.*");
         System.out.println(pointcut.matches(UserVo.class.getMethod("setResource", Resource.class), UserVo.class));
         ```
     
     - AnnotationMatchingPointcut：通过指定注解进行类级和方法级别的限定。
     
       ```java
       //只有同时标注了ClassLevelAnnotation和MethodLevelAnnotation的方法才会被匹配
               AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(ClassLevelAnnotation.class, MethodLevelAnnotation.class);
       
       AnnotationMatchingPointcut pointcut = AnnotationMatchingPointcut.forMethodAnnotation(MethodLevelAnnotation.class);
       MethodMatcher methodMatcher = pointcut.getMethodMatcher();
       methodMatcher.matches(UserVo.class.getMethod("setResource", Resource.class), UserVo.class);
       ```
     
     - CompossablePointcut：支持Point之间的并和交集运算。
     
       ```java
       AnnotationMatchingPointcut pointcut1 = AnnotationMatchingPointcut.forClassAnnotation(ClassLevelAnnotation.class);
       ClassFilter filter1 = pointcut1.getClassFilter();
       AnnotationMatchingPointcut pointcut2 = AnnotationMatchingPointcut.forMethodAnnotation(MethodLevelAnnotation.class);
       MethodMatcher filter2 = pointcut2.getMethodMatcher();
       
       //使用CompassablePointcut进行逻辑运算
       ComposablePointcut pointcut3 = new ComposablePointcut(pointcut1);
       ComposablePointcut pointcut4 = new ComposablePointcut(pointcut2);
       ComposablePointcut union = pointcut3.intersection(pointcut4);
       System.out.println(union.getMethodMatcher().matches(UserVo.class.getMethod("setResource", Resource.class), UserVo.class));
       
       //使用Pointcuts工具类进行逻辑运算
       Pointcut pointcut = Pointcuts.intersection(pointcut1, pointcut2);
       ```
     
     - ControlFloPointcut：匹配程序调用流程，如指定某个类调用某个方法时才对其方法进行拦截。需要在运行期间检查程序的调用栈，且每次方法调用都要检查，性能较差。
     
       ```java
       //只匹配EarthVo里的getUserName方法
       ControlFlowPointcut pointcut = new ControlFlowPointcut(EarthVo.class, "getUserName");
       //匹配EarthVo里的所有方法
       ControlFlowPointcut pointcut = new ControlFlowPointcut(EarthVo.class);
       
       Advice advice = ...;
       UserVo userVo = ...;
       UserVo userVoToUse = weaver.weave(advice).to(userVo).accordingto(pointcut);
       
       //不会触发advice
       userVoToUse.getName();
       
       //会触发advice
       EarthVo earthVo = ...;
       earthVo.setUserVo(userVoToUse);
       earthVo.getUserName();
       ```
     
     - 扩展Pointcut
     
       - 自定义StaticMethodMatcherPointcut：通过实现两个参数的matchers方法即可。
       - 自定义DynamicMethodMatcherPointcut：通过实现三个参数的matchers方法即可。
     
   - IoC容器中的Pointcut：通过Spring AOP的过程中，不会直接将某个Pointcut注册到容器，然后公开给容器中的对象使用。
   
3. Spring AOP中的Advice

   - Advice分为per-class类型（Advice自身实例可共享）和per-instance类型（Advice自身实例不可共享）。

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/Spring%E4%B8%AD%E7%9A%84Advice%E7%95%A5%E5%9B%BE.jpg?raw=true)

   - per-class类型的Advice：除了Introduction类型外都属于per-class类型。

     - Before Advice

       - 可以通过抛出异常终端程序流程。

       - 只需实现MethodBeforeAdvice接口。

       - 示例：

         ```java
         public class BeforeAdviceAspect implements MethodBeforeAdvice{
             @Override
             public void before(Method method, Object[] args, Object target) throws Throwable {
                 System.out.println("this is BeforeAdviceAspect");
             }
         }
         ```

     - ThrowsAdvice

       - 需要实现ThrowsAdvice接口：该接口没有任何方法，但实现时需要符合下述规则。

         ```java
         void afterThrowing([Method, args, tatget], ThrowableSubclass);
         ```

       - 示例：

         ```java
         public class ThrowsAdviceAspect implements ThrowsAdvice{
             public void afterThrowing(Throwable t) {
                 System.out.println(t.getMessage());
             }
         
             public void afterThrowing(Method m, Object[] args, Object target, Throwable t) {
                 System.out.println(t.getMessage());
             }
         }
         ```

       - 可以用于监控系统中运行时发生的异常，然后把捕获的异常通知营运人员。

     - AfterReturningAdvice

       - 实现AfterReturningAdvice接口。

       - 可以访问当前Joinpoint的方法返回值、方法、方法参数以及所在的目标对象。

       - 只有在方法正常返回时，AfterReturningAdvice才会执行，不适合用来资源清理等工作。

       - 不可以修改方法的返回值。

       - 示例：

         ```java
         public class AfterAdviceAspect implements AfterReturningAdvice{
             @Override
             public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
                 System.out.println("AfterAdviceAspect");
             }
         }
         ```

     - Around Advice

       - 实现MethodInterceptor接口。

       - 可以用于系统安全验证及检查、系统各处的性能检查、简单的日志记录以及系统附加行为的添加等。

       - 如果不调用invocation.proceed()那么程序将会被短路。

       - 可以修改返回值。

       - 示例：

         ```java
         public class AroundAdviceAspect implements MethodInterceptor{
             @Override
             public Object invoke(MethodInvocation invocation) throws Throwable {
                 System.out.println("start");
                 Object result = invocation.proceed();
                 System.out.println("end");
                 //修改结果
                 result = 1;
                 return result;
             }
         }
         ```

   - per-instance类型Advice

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/Introduction%E7%9B%B8%E5%85%B3%E7%B1%BB%E5%9B%BE.jpg?raw=true)

     - Introduction：在不改动目标类定义的情况下，为目标类添加新的属性以及行为。

     - 新的属性和行为必须声明相应的接口及实现。然后通过特定的拦截器将新的接口定义以及实现类中的逻辑附加到目标对象上。之后，目标对象就拥有了新的状态和行为。

     - 拦截器是IntroductionInterceptor：继承了MethodInterceptor以及DynamicIntroductionAdvice接口。通过DynamicIntroductionAdvice可以判断当前的IntroductionInterceptor为哪些接口提供相应的拦截功能。通过MethodInterceptor，IntroductionInterceptor可以处理新添加的接口上的方法调用了。

     - 可以把IntroductionInterceptor看出是为牛奶贴上标签的那个”人“。

     - DelegatingIntroductionInterceptor：将要添加到目标对象上的新的逻辑行为，委派给其他实现类。

       - 其delegate只有一个实例，不管有对少个目标对象都使用一个delegate实例。

       - 使用步骤：

         - 为新的状态和行为定义接口并提供实现类。

         - 通过DelegatingIntroductionInterceptor进行Introduction的拦截。

         - 示例：

           ```java
           ITester delegate = new Tester();
           DelegatingIntrodutionInterceptor interceptor = new DelegatingIntrodutionInterceptor(delegate);
           
           ITester tester = (Tester)weaver.weave(developer).with(interceptor).getProxy();
           ```

     - DelegatePerTargetObjectIntroductionInterceptor

       - 内部持有目标对象和相应的Introduction逻辑实现类之间的映射。当每个目标对象上的新定义的接口方法被调用的时候，DelegatePerTargetObjectIntroductionInterceptor会拦截这些调用，以目标对象为键，寻找Introduction实现类实例。如果没有找到对应的Introduction实现类实例，DelegatePerTargetObjectIntroductionInterceptor会为其创建一个新的，然后添加到映射关系中。

       - 不需要自己构造delegate实例，只需告知DelegatePerTargetObjectIntroductionInterceptor相应的delegate接口类型和对应实现类的类型。

         ```java
         DelegatePerTargetObjectIntroductionInterceptor interceptor =
                 new DelegatePerTargetObjectIntroductionInterceptor(EarthVo.class, IEarthVo.class);
         ```

     - Spring AOP采用动态代理将Introduction织入目标对象，性能上不太行。

4. Spring AOP中的Aspect

   -  Advisor：每个Advisor只有一个pointcut和一个advice

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/Advisor%E5%88%86%E6%94%AF.png?raw=true)

   - PointcutAdvisor：大部分Advisor实现都是PointcutAdvisor

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/PointcutAdvisor%E5%8F%8A%E7%9B%B8%E5%85%B3%E5%AD%90%E7%B1%BB.png?raw=true)

     - DefaultPointcutAdvisor：除了不能指定Introduction类型的Advice外，剩下的任何类型的Pointcut和Advice都可以通过DefaultPointcutAdvisor来使用。

       - 示例：

         ```java
         NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
                 pointcut.setMappedName("*Resource");
                 Advice advice = new BeforeAdviceAspect();
         
                 DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
         ```

     - NameMatchMethodPointcutAdvisor：只可使用Pointcut类型为NmaeMatchMethodPointcut，内部持有一个NameMatchMethodPointcut实例。

       - 示例：

         ```java
         Advice advice = new BeforeAdviceAspect();
         
         NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor(advice);
         advisor.setMappedName("getResource");
         ```

     - RegexpMethodPointcutAdvisor：只能通过正则表达式设置Pointcut。默认使用JdkRegexpMethodPointcut。

       - 示例：

         ```java
         Advice advice = new BeforeAdviceAspect();
         
         RegexpMethodPointcutAdvisor advisor = new RegexpMethodPointcutAdvisor(advice);
         advisor.setPattern(".*getResource。*");
         ```

     - DefaultBeanFactoryPointcutAdvisor：自身绑定到了BeanFactory。可以通过beanName来关联相应的Advice。

   - IntroductionAdvisor分支：只能用于类级别的拦截，只能使用Introduction型的Advice。

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/IntroductionAdvisor%E7%B1%BB%E7%BB%93%E6%9E%84%E5%9B%BE.png?raw=true)

     - 示例：

       ```java
       ITester delegate = new Tester();
       DelegatingIntrodutionInterceptor interceptor = new DelegatingIntrodutionInterceptor(delegate);
       
       DefaultIntroductionAdvisor advisor = new DefaultIntroductionAdvisor(interceptor, ITester.class);
       
       //or
       //前者作为Advice实例，后者作为IntroductionInfo实例，也就是可以提供接口类型
       ITester delegate = new Tester();
       DelegatingIntrodutionInterceptor interceptor = new DelegatingIntrodutionInterceptor(delegate);
       
       DefaultIntroductionAdvisor advisor = new DefaultIntroductionAdvisor(interceptor, interceptor);
       ```

   - Ordered的作用：Spring默认使用声明顺序执行Advisor。可以使用oder属性设置优先级，0表示最高优先级。

     - 示例：

       ```java
       advisor.setOrder(1);
       ```

5. Spring AOP织入

   - ProxyFactory织入

     - 示例：

       ```java
       //目标对象
       EarthVo earthVo = (EarthVo) ctx.getBean("earthVo");
       //代理工厂
       ProxyFactory weaver = new ProxyFactory(earthVo);
       //切点
       NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
       pointcut.setMappedName("getUserName");
       //织入逻辑
       Advice advice = new BeforeAdviceAspect();
       
       //包装成advisor
       DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
       //添加advisor给织入器
       weaver.addAdvisor(advisor);
       //获取织入成功的目标对象
       Object object = weaver.getProxy();
       //使用目标对象
       ((IEarthVo)object).getUserName();
       ```

     - 基于接口代理：可以通过seaver.setInterfaces(Interface.class)知名目标接口类型。但是默认也会检测到目标类实现的接口，也会对目标类进行基于接口的代理。

       - 示例：

         ```java
         ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
         EarthVo earthVo = (EarthVo) ctx.getBean("earthVo");
         ProxyFactory weaver = new ProxyFactory(earthVo);
         //指定目标接口
         weaver.setInterfaces(IEarthVo.class);
         NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
         pointcut.setMappedName("getUserName");
         Advice advice = new BeforeAdviceAspect();
         
         DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
         weaver.addAdvisor(advisor);
         Object object = weaver.getProxy();
         ((IEarthVo)object).getUserName();
         ```

     - 基于类的代理：如果目标类没有实现任何接口，那么ProxyFactory会对目标类进行基于类的代理，即使用CGLIB。

       - 可以使用setProxyTargetClass强制使用基于类代理。optimize属性也可以。

     - Introduction的织入

       - 只能对象级别拦截，所以不需要指定Pointcut，只需要指定目标接口类型。

       - 只能通过接口定义为当前对象添加新的行为，所以需要指定新织入的接口类型。

       - 示例：

         ```java
         ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
         EarthVo earthVo = (EarthVo) ctx.getBean("earthVo");
         ProxyFactory weaver = new ProxyFactory(earthVo);
         //指定目标对象实现的接口和新添加的接口类型
         weaver.setInterfaces(IEarthVo.class, INoJiekou.class);
         
         //拦截器
         DelegatingIntroductionInterceptor interceptor = new DelegatingIntroductionInterceptor(new NoJiekou());
         weaver.addAdvice(interceptor);
         
         Object object = weaver.getProxy();
         ((INoJiekou)object).message();
         ```

   - ProxyFactory本质

     - AopProxy相关结构图

       ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/AopProxy%E7%9B%B8%E5%85%B3%E7%BB%93%E6%9E%84%E5%9B%BE.jpg?raw=true)

       - AopProxy有Cglib2AopProxy和JdkDynamicAopProxy两种实现。因为动态代理需要通过InvocationHandler提供调用拦截，所以JdkDynamicAopProxy同时实现了InvocationHandler接口。AopProxy通过抽象工厂模式进行封装。

       - AopProxyFactory：根据传入的AdviseddSupport实例提供相关信息，来决定生成什么类型的AopProxy。默认是DefaultAopProxyFactory。

         ```java
         public interface AopProxyFactory {
         	AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException;
         }
         ```

         ```java
         public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
            if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
               //..
               return new ObjenesisCglibAopProxy(config);
            }
            else {
               return new JdkDynamicAopProxy(config);
            }
         }
         ```

       - AdvisedSupport是一个生成代理对象所需要的信息的载体：分为ProxyConfig记载生成代理对象的控制信息；和Advised生成代理对象所需的必要信息，如相关目标类、Advice、Advisor等。

         ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/AdvisedSupport%E7%B1%BB%E5%B1%82%E6%AC%A1%E5%9B%BE.jpg?raw=true)

         - ProxyConfig有5个信息：
           - proxyTargetClass：true会使用CGLIB对目标对象进行代理。
           - optimize：是否进一步优化。
           - opaque：默认fasle，即代理对象可以强制转型成Advised。
           - exposeProxy：将当前对象绑定到ThreadLocal，如果目标对象需要访问当前的代理对象，可以通过AopContexxt.currentProxy()获得。默认false。
           - frozen：一旦对代理对象各项信息配置完成，则不允许修改。默认为false。

     - ProxyFactory继承层次类图：集AopProxy和AdvisedSupport于一身，可以通过ProxyFactory设置生成代理对象所需要的相关信息（AdvisedSupport），也可以通过ProxyFactory取得最终生成的代理对象（AopProxy）。

     - ProxyFactory的兄弟：

       ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/ProxyFactory%E7%9A%84%E5%85%84%E5%BC%9F.jpg?raw=true)

   - 容器中的织入器——ProxyFactoryBean

     - PorxyFactoryBean的额外属性：
     
       - proxyInterfaces：代理的目标接口。ProxyFactoryBean有一个autodetectInterfaces属性，默认为true，即默认ProxyFactoryBean会自动检测目标对象所实现的接口类型并进行代理。
       - interceptorNames：可以指定多个要织入到目标对象的Adcice、拦截器以及Advisor。可以使用通配符*让ProxyFactoryBean在容器中搜索符合条件的所以Advisor并应用到目标对象。
       - singleton：指定getObject调用是否每次都返回同一个代理对象，还是返回一个新的。默认true。
     
     - 示例1：普通织入
     
       ```xml
       <bean id="proxy" class="org.springframework.aop.framework.ProxyFactoryBean">
           <property name="target" ref="earthVo"/>
           <property name="interceptorNames">
               <list>
                   <value>introduction*</value>
               </list>
           </property>
       </bean>
       
       <bean id="introductionAdviceAspect" class="com.springjiemi.aspect.IntroductionAdviceAspect"/>
       ```
     
     - 示例2：Introduction织入，目标bean、ProxyFactoryBean的bean、IntroductionInterceptor的bean都要声明为prototype。并且使用name来指定目标对象。
     
       ```xml
       <bean scope="prototype" id="interceptor" class="org.springframework.aop.support.DelegatingIntroductionInterceptor">
           <constructor-arg>
               <bean class="com.springjiemi.vo.Target"/>
           </constructor-arg>
       </bean>
       
       <bean id="proxy" class="org.springframework.aop.framework.ProxyFactoryBean" scope="prototype">
           <property name="target" name="earthVo"/>
           <property name="proxyInterfaces">
               <list>
                   <value>com.springjiemi.vo.IEarthVo</value>
                   <value>com.springjiemi.vo.ITarget</value>
               </list>
           </property>
           <property name="interceptorNames">
               <list>
                   <value>interceptor</value>
               </list>
           </property>
       </bean>
       ```
     
     - 示例3：使用DelegatePerTargetObjectIntroductionInterceptor就不用指定score为prototype了
     
       ```xml
       <bean id="interceptor" class="org.springframework.aop.support.DelegatePerTargetObjectIntroductionInterceptor">
           <constructor-arg index="0" value="com.springjiemi.vo.Target"/>
           <constructor-arg index="1" value="com.springjiemi.vo.ITarget"/>
       </bean>
       
       <bean id="proxy" class="org.springframework.aop.framework.ProxyFactoryBean" scope="prototype">
           <property name="targetName" value="earthVo"/>
           <property name="proxyInterfaces">
               <list>
                   <value>com.springjiemi.vo.IEarthVo</value>
                   <value>com.springjiemi.vo.ITarget</value>
               </list>
           </property>
           <property name="interceptorNames">
               <list>
                   <value>interceptor</value>
               </list>
           </property>
       </bean>
       ```
     
   - 自动代理
   
     - 通过BeanPostProcessor在遍历容器中所有bean的基础上，当符合拦截条件时，生成代理对象并返回。
   
     - BeanNameAutoProxyCreator：通过指定容器内的目标对象对应的beanName，将指定的一组拦截器应用到这些目标对象上。
   
       - 示例：通过beanNames指定目标对象，通过interceptorNames指定目标对象的拦截器、Advice和Advisor。beanNames可以使用*通配符。
   
         ```xml
         <bean class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator">
             <property name="beanNames" value="earthVo"/>
             <property name="interceptorNames" value="introductionAdviceAspect"/>
         </bean>
         
         <bean id="introductionAdviceAspect" class="com.springjiemi.aspect.IntroductionAdviceAspect"/>
         ```
   
     - DefaultAdvisorAutoProxyCreator：更加自动化，需要在容器中配置DefaultAdvisorAutoProxyCreator Bean。
   
       - 只需要指定Advisor即可自动装配（因为Advisor含有Pointcut信息和Advice）。
   
     - 扩展AutoProxyCreator：可以扩展AbstractAutoProxyCreator或者AbstractAdvisorAutoProxyCreator。之类只需提供规则匹配一类的逻辑。
   
       - Spring AOP的所有AutoProxyCreator都是InstantiationAwareBeanPostProcessor，当Spring IoC容器检测到有InstantiationAwareBeanPostProcessor类型的BeanPostProcessor时，会直接通过InstantiationAwareBeanPostProcessor中的逻辑构造对象实例并返回，也就是造成‘"短路"。
   
     - AutoProxyCreator实现结构类图
   
       ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/AutoProxyCreator%E5%AE%9E%E7%8E%B0%E7%BB%93%E6%9E%84%E7%B1%BB%E5%9B%BE.jpg?raw=true)
   
6. TragetSource：Spring AOP通过调用TargetSource来获取具体的目标对象，然后再调用从TargetSource获取的目标对象上面的方法。

   ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/TargetSource%E7%A4%BA%E6%84%8F%E5%9B%BE.jpg?raw=true)

   - 每次方法调用都会触发TargetSource的getTarget()方法，从而可以控制每次方法调用得到的具体目标对象实例。

     - 提供目标对象池，每次都从池中获取目标对象。
     - 按照某种规则返回目标对象实例。

   - TargetSource实现类

     - SingletonTargetSource：每次都会返回同一个目标对象实例的引用。

     - PrototypeTargetSource：每次都会返回新的目标对象实例。

       - 目标对象bean必须为prototype。
       - 通过targetBeanName属性指定目标对象的bean定义名称。

       - 示例：

         ```xml
         <bean id="earthVo" class="com.springjiemi.vo.EarthVo" scope="prototype"/>
         
         <bean id="earthVo0" class="org.springframework.aop.framework.ProxyFactoryBean">
             <property name="targetSource" ref="prototypeTargetSource"/>
             <property name="interceptorNames" value="beforeAdviceAspect"/>
         </bean>
         
         <bean id="prototypeTargetSource" class="org.springframework.aop.target.PrototypeTargetSource">
             <property name="targetBeanName" value="earthVo"/>
         </bean>
         ```

     - HotSwappableTargetSource：可以在程序运行时，动态地替换目标对象类。通过HotSwappableTargetSource的swap方法。

       - 示例：

         ```java
         IEarthVo earthVo = (IEarthVo) ctx.getBean("earthVo0");
         System.out.println(earthVo); //com.springjiemi.vo.EarthVo@2df4dede
         
         HotSwappableTargetSource hotSwappableTargetSource =
         (HotSwappableTargetSource) ctx.getBean("hotSwappableTargetSource");
         hotSwappableTargetSource.swap(new EarthVo());
         System.out.println(ctx.getBean("earthVo0")); //com.springjiemi.vo.EarthVo@2753d864
         ```

       - 可以使用ThrowsAdcice对数据库异常进行捕获，捕获到之后调用swap方法切换新的数据源。

     - CommonsPoolTargetSource：类似连接池，从池中选择一个目标对象并返回。可以设置对象池的大小、初始化对象等。

     - ThreadLocalTargetSource：为不同的线程调用提供不同的目标对象。内部是对ThreadLocal进行了封装。

   - 自定义TargetSource

     ```java
     public interface TargetSource extends TargetClassAware {
     	//返回目标对象类型
        Class<?> getTargetClass();
     	//是否返回同一个目标对象实例
        boolean isStatic();
     	//返回目标对象
        Object getTarget() throws Exception;
     	//如果isStatic返回fasle，会执行此方法释放目标对象
        void releaseTarget(Object target) throws Exception;
     }
     ```

# 10、Spring AOP二世

1. 使用形式

   - 示例：
   
     ```java
     @Aspect
     @Component
     public class TestAspect {
     
         @Pointcut(value = "execution(* com.xuexi.pojo.IUser.login(..)) && args(username, password)")
         public void pointcut(String username, String password) {}
     
         @Before(value = "pointcut(username, password))")
         public void beforeAspect(String username, String password) throws Throwable {
             System.out.println("before");
             System.out.println(username);
             System.out.println(password);
         }
     
     }
     ```
   
   - 配置：AnnotationAwareAspectJAutoProxyCreator会自动搜集IoC容器中注册的Aspect，并应用到Pointcut定义的各个目标对象上。
   
     ```xml
     <!-- 简化版 -->
     <!-- 和直接声明AnnotationAwareAspectJAutoProxyCreator一样 -->
     <aop:aspectj-autoproxy/>
     
     <!-- 直接声明AnnotationAwareAspectJAutoProxyCreator -->
     <bean class="org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator">
     </bean>
     ```
   
   - @AspectJ形式的Pointcut
   
     ```java
     //pointcut_expression
     @Pointcut(value = "execution(* com.xuexi.pojo.IUser.login(..)) && args(username, password)") 
     //pointcut_signature
     public void pointcut(String username, String password) {}
     ```
   
     - @AspectJ形式Pointcut表达式的标识符
   
       - execution：匹配拥有指定方法前面的Joinpoint
   
         - 格式：其中方法的返回类型、方法名及参数部分的匹配模式是必须的
   
           ```java
           execution(modifiiers-pattern? ret-type-pattern declaring-type-pattern? name-pattern(param-pattern) throws-pattern?)
           ```
   
         - 可以使用*匹配任何部分，匹配相邻的多个字符。
   
         - ..可以在declaring-type-pattern和方法参数位置使用。
   
           ```java
           //指定pojo包下的所有类型，参数部分指定由0-n个参数
           @Pointcut(value = "execution(* com.xuexi.pojo.*.login(..)) && args(username, password)")
           //指定pojo包下的所有类型，及下层包下的所有类型
           @Pointcut(value = "execution(* com.xuexi.pojo..*.login(..)) && args(username, password)")
           ```
   
       - within：只接收类型声明，会匹配指定类型下的所有Joinpoint。
   
         - 示例：
   
           ```java
           //匹配pojo包下的所有类型的方法基本Joinpoint
           @Pointcut("within(com.xuexi.pojo.*) && args(username, password)")
           public void pointcut0(String username, String password) {}
           ```
   
       - this和target：this指目标对象的代理对象，target指目标对象。
   
         - 示例：
   
           ```java
           //匹配代理对象为IUser的
           @Pointcut("this(com.xuexi.pojo.IUser) && args(username, password)")
           public void pointcut1(String username, String password) {}
           ```
   
         - 对于Introduction，代理对象所实现的接口数量通常比目标对象多，可以使用this和target进一步限定匹配规则。
   
           ```java
           this(IntroductionInterface) && target(TargetObjectType)
           ```
   
       - args：捕捉拥有指定参数类型、指定参数数量的方法级Joinpoint。
   
         - args标识符会在运行期间动态检查参数类型，也就是
   
           ```java
           //只要传入的是User类型的实例，那么也可以匹配到
           login(Object user);
           
           //而execution(* *(User))这样静态的Pointcut则无法匹配到
           ```
   
       - @within：如果@within指定了某种类型的注解，那么对象标注了该类型的注解，使用了@within标识符的Pointcut表达式将匹配该对象内的所有Joinpoint。
   
         - 示例：
   
           ```java
           @Pointcut("@within(com.xuexi.annotation.TestAnnotation) && args(username, password)")
           public void pointcut2(String username, String password) {}
           ```
   
           ```java
           @Retention(RetentionPolicy.RUNTIME)
           @Target({ElementType.METHOD, ElementType.TYPE})
           public @interface TestAnnotation {
           }
           ```
   
           ```java
           @Component
           @TestAnnotation
           public class Student {
               public String login(String username, String password) {
                   return "login success";
               }
           }
           ```
   
       - @target：如果目标对象拥有@target标识符指定的注解类型，那么目标对象内部所有Joinpoint将被匹配。@within属于
   
       - @args：使用@args标识符的Pointcut会尝试检查当前方法级的Joinpoint的方法参数类型，如果该次传入的参数类型拥有@args所指定的注解，当前Joinpoint将被匹配。
   
         - 示例：
   
           ```java
           @Component
           public class Student {
               public String login(User user) {
                   return "login success";
               }
           }
           ```
   
           ```java
           @Pointcut("@args(com.xuexi.annotation.TestAnnotation)")
           public void pointcut3() {}
           ```
   
         - @args会对每次方法执行动态检查，只要参数类型标注有@args指定的注解类型，就会匹配。
   
       - @annotation：检查系统中所有对象的所有方法级别的Joinpoint，如果由@annotation标志符所指定的类型，那么将被匹配。
   
         - 示例：
   
           ```java
           @Pointcut("@annotation(com.xuexi.annotation.TestAnnotation)")
           public void pointcut4() {}
           ```
   
           ```java
           @TestAnnotation
           public String login() {
               return "login success";
           }
           ```
   
         - 可以用于事务控制等。
   
     - @AspectJ形式的Pointcut在Spring AOP中的真实面目：@AspectJ形式声明的所有Pointcut表达式，在Spring AOP内部都会解析转换成具体的Pointcut对象。
   
       ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/AspectJ%20Pointcut%20%E6%89%A9%E5%B1%95%E7%9A%84%E7%B1%BB%E5%9B%BE.jpg?raw=true)
   
       - AspectJExpressionPointcut代表Spring AOP中面向AspectJ的Pointcut具体实现。
       - 通过AspectJProxyFactory或AnnotationAwareAspectJAutoProxyCreator通过反射获取一个Aspect中的@Pointcut定义的AspectJ形式的Pointcut定义之后，Spring AOP框架内会构造一个对于的AspectJExpressionPointcut对象实例。该实例持有Pointcut表达式。
       - AspectJExpressionPointcut的解析工资会委托AspectJ类库中的PointcutParser来解析Pointcut表达式（返回一个PointcutExpression对象），然后AspectJExpressionPointcut这个对象进行处理。
   
   - @AspectJ形式的Advice
   
     - 用于Advice定义方法的注解包括：
   
       - @Before
       - @AfterReturning
       - @AfterThrowing
       - @After：finally
       - @Around
       - @DeclareParents：用于标注Introduction类型的Advice，标注的对象是域。
   
     - 访问Joinpoint出的方法参数
   
       - 借助Joinpoint类型，将Advice方法中的第一个参数声明为Joinpoint类型。
   
         - 示例：
   
           ```java
           @Before(value = "pointcut4())" )
           public void beforeAspect1(JoinPoint joinPoint) throws Throwable {
               System.out.println(Arrays.toString(joinPoint.getArgs()));
               System.out.println(joinPoint.getThis());
               System.out.println(joinPoint.getStaticPart());
               System.out.println(joinPoint.getSignature());
               System.out.println(joinPoint.getClass());
               System.out.println(joinPoint.getKind());
               System.out.println(joinPoint.getSourceLocation());
               System.out.println(joinPoint.getTarget());
           }
           ```
   
       - 通过args标志符绑定：接受某个参数模名称，将这个参数名称对应的参数值绑定到Advice方法的调用。
   
         - 示例：
   
           ```java
           @Before(value = "pointcut(username, password))" )
           public void beforeAspect(String username, String password) throws Throwable {
               System.out.println("before");
               System.out.println(username);
               System.out.println(password);
           }
           ```
   
           ```java
           @Pointcut(value = "execution(* com.xuexi.pojo..*.login(..)) && args(username, password)")
           public void pointcut(String username, String password) {}
           ```
   
         - args指定的参数名称必须于Advice定义所在方法的参数名称相同。
   
         - 除了execution标识符不会直接指定对象类型外，其他的都会指定对象类型，如果它们指定的是参数名称，那么作用与args是一样的。
   
           ```java
           @Before(value = "execution(* com.xuexi.pojo..*.login(..)) && args(username, password) && this(user)")
           public void beforeAspect(String username, String password, IUser user) throws Throwable {
               System.out.println(user);
               System.out.println("before");
               System.out.println(username);
               System.out.println(password);
           }
           ```
   
       - After Throwing Advice
   
         - 示例：
   
           ```java
           //throwing参数指定异常类型
           @AfterThrowing(pointcut = "pointcut(username, password)", throwing = "e")
           public void afterThrowing(String username, String password, RuntimeException e) throws Throwable {
               System.out.println("afterThrowing");
               e.printStackTrace();
           }
           ```
   
       - AfterReturning
   
         - 示例：
   
           ```java
           //returning指定返回值
           @AfterReturning(pointcut = "pointcut(username, password)", returning = "ret")
           public void afterThrowing(String username, String password, String ret) throws Throwable {
               System.out.println(ret + "this is reuslt");
           }
           ```
   
       - After (Finally) Advice：不管如何，都会触发After Advice，适合用于资源释放。
   
       - Around Advice：第一个参数类型必须是ProceedingJoinPoint类型
   
         - 示例：
   
           ```java
           @Around(value = "pointcut(username, password)")
           public Object around(ProceedingJoinPoint joinPoint, String username, String password) throws Throwable {
               System.out.println("around" + "this is reuslt");
               //调用目标方法函数
               Object result = joinPoint.proceed(new Object[] {username, password});
               System.out.println(result + " this is result");
               //修改目标方法的返回值
               return "login false";
           }
           ```
   
       - Introduction：在Aspect中声明一个示例变量，它的类型是新增加的接口类型， 然后通过@DeclareParents对其标注，指定新接口的实现类和要加到的目标对象。
   
         - 示例：
   
           ```java
           @DeclareParents(
                   value = "com.xuexi.pojo.*",
                   defaultImpl = HomeWork.class
           )
           public IHomeWork homeWork;
           ```
   
           ```java
           IStudent student = (IStudent) ctx.getBean("student");
           ((IHomeWork)student).doHomeWork();
           ```
   
         - 可以使用*通配符。
   
   - @AspectJ的更多话题
   
     - Advice的执行顺序
   
       - 在同一个Aspect内默认按声明顺序。
   
       - 在不同Aspect内可以通过实现Ordered接口指定执行顺序，值越小优先级越高。
   
       - 示例：
   
         ```java
         @Aspect
         @Component
         public class TestAspect implements Ordered{
         	//。。。
             @Override
             public int getOrder() {
                 return 1;
             }
         }
         ```
   
     - Aspect的实例化模式：由三种模式singleton、perthis、pertarget
   
       - perthis：为每个代理对象实例化相应的Aspect实例。
   
       - pertarget：为每个目标对象实例化相应的Aspect实例。
   
       - 示例：其中User的@Scope要声明为prototype
   
         ```java
         @Aspect("perthis(execution(* com.xuexi.pojo.User.login(..)))")
         @Component
         @Scope(value = "prototype")
         public class UserAspect {
         
             @Pointcut(value = "execution(* com.xuexi.pojo.User.login(..))")
             public void pointcut() {}
         
             @Before("pointcut()")
             public void beforeAspect() throws Throwable {
                 System.out.println("this is user aspect");
             }
         
         }
         ```
   
2. 基于Schema的AOP

   - 略

# 11、AOP应用案例

1. 异常处理

   - Java异常处理

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/Java%E5%BC%82%E5%B8%B8%E5%B1%82%E6%AC%A1%E4%BD%93%E7%B3%BB%E5%9B%BE.jpg?raw=true)

     - unchecked exception称之为fault，checked exception称之为contingency。fault barrier处理的是fault的情况，也就是unchecked exception。
     - Fault Barrier
       - unchecked exception能做的事情很少，通常是记录日志、通知相应人员。
       - 可以分析unchecked exception 信息，通过邮件通过在相关人员。

2. 安全检查：可以通过添加拦截器（@Around）进行安全检查。

   - 有Spring Security框架。

3. 缓存：透明的添加缓存

   - 有EhCache、JBossCache等。
   - Spring Modules提供了现有Caching产品的集成，可以通过外部声明的方式为系统中的Joinpoint添加Caching。

# 12、Spring AOP扩展

1. 公开当前调用的代理对象

   - 一旦目标对象中的原始方法直接调用自身方法时，就会出现无法为调用的自身方法织入切面的情况。

     - 出现原因：Spring AOP的代理机制。

   - 解决方法：为目标对象注入依赖对象的代理对象，来解决问题。

     - Spring AOP提供了AopContext来公开当前目标对象的代理对象，只要在目标对象中使用AopContext.currentProxy()就可以获取到当前目标对象的代理对象。

     - 示例：

       ```java
       public String login(String username, String password) {
           IUser iUser = (IUser) AopContext.currentProxy();
           iUser.getUsername();
           return username + password;
       }
       ```

     - 注入的方式还有：

       - 在目标对象中声明一个实例变量，然后通过构造方法注入或setter方法将AopContext.currentProxy()注入。
       - 在目标对象中声明一个getThis()方法，然后return AopContext.currentProxy()。
       - 声明一个Wrapper类，在Wrapper类中声明getProxy()方法，然后return AopContext.currentProxy()。可以解耦目标对象和Spring API，也可以通过Wrapper的Utils类。
       - 为目标对象声明统一接口定义，然后通过BeanPostProcessor处理这些接口的实现类，将实现类的某个获取当前对象的代理对象的方法逻辑覆盖掉。

# 13、统一数据访问异常层次结构

1. 