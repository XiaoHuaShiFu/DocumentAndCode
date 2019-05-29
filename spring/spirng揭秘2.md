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

         ```
         
         ```

         