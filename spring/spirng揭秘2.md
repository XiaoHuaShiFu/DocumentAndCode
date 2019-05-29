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

     

