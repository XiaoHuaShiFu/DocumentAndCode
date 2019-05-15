# Spring揭秘1

# 1、

1. IoC的理念：让别人为你服务！
   - 也就是IoC容器为你提供所依赖的对象。

# 3、IoC Service Provider

1. IoC Service Provider的职责：

   - 业务对象的构建管理。
   - 业务对象间的依赖绑定。

2. 管理对象间的依赖关系

   - 文本文件

   - XML

     - XML配置：然后再用Java代码从容器中获取newsProvider并使用。

       ```xml
       <bean id="newsProvider" class="...FXNewsProvider">
       	<property name="newsListener">
               <ref bean="djNewsListener" />
           </property>
       </bean>
       ```

   - Java Config

     - 直接编码

       ```java
       IoContainer container = ...;
       container.register(FXNewsProvider.class, new FXNewsProvider());
       container.register(IFXNewsListener.class, new DowJonesNewsListener());
       //...
       FXNewsProvider newsProvider = (FXNewsProvider) container.get(FXNewsProvider.class);
       newProvider.getAndPersistNew();
       ```

     - 接口注入

       ```java
       IoContainer container = ...;
       container.register(FXNewsProvider.class, new FXNewsProvider());
       container.register(IFXNewsListener.class, new DowJonesNewsListener());
       //...
       //将“被注入对象”的由IFXNewsListenerCallable所标识的“所依赖对象”绑定位容器中注册过的IFXNewsListener类型实例对象
       container.bind(IFXNewsListenerCallable.class, container.get(IFXNewsListener.class));
       //...
       FXNewsProvider newsProvider = (FXNewsProvider) container.get(FXNewsProvider.class);
       newProvider.getAndPersistNew();
       ```

   - 注解

# 4、Spring的IoC容器之BeanFactory

1. Ioc Service Provider是Spring的IoC容器的一个子集（元素），Spring的IoC容器的元素还由AOP、线程管理、对象生命周期管理、查找服务、企业服务集成等。

2. Spring提供了两种容器类型：

   - BeanFactory：基础IoC容器，提供完整的IoC服务支持。

     - 默认采用延迟初始化策略（lazy-load）。
     - 

   - ApplicationContext：ApplicationContext在BeanFactory的基础上构建。

     - 支持高级特性。如事件发布、国际化信息等。
     - 默认容器启动之后，全部初始化并绑定完成。

   - BeanFactory和ApplicationContext继承关系。

     <div align="center">
     <img src="https://github.com/XiaoHuaShiFu/img/blob/master/BeanFactory%E5%92%8CApplicationContext%E7%BB%A7%E6%89%BF%E5%85%B3%E7%B3%BB.jpg?raw=true" width="75%" height="75%" style="transform: rotate(0deg);">
      </div>

   - BeanFactory：公开获取一个组装完成的对象的方法接口，也包含查询方法。

     - 如获取某个对象（getBean）、查询某个对象是否存在容器中（containsBean）、获取某个bean的状态或者类型的方法等。

3. BeanFactory的对象注册与绑定方式

   - 直接编码方式

     - 示例：

       ```java
       public static void  testBindViaCode() {
           //DefaultListableBeanFactory是BeanFactory的一个间接实现
           //也实现了BeanDefinitionRegistry接口，此接口充当Bean注册管理的角色
           DefaultListableBeanFactory beanRegistry = new DefaultListableBeanFactory();
           //BeanFactory是用于对实际Bean进行访问管理的接口
           BeanFactory container = bindViaCode(beanRegistry);
           UserVo userVo = (UserVo) container.getBean("userVo");
           System.out.println(userVo);
       }
       
       public static BeanFactory bindViaCode(BeanDefinitionRegistry registry) {
           //部门bean
           //通过setter方法注入
           AbstractBeanDefinition dep = new RootBeanDefinition(Dep.class);
           registry.registerBeanDefinition("dep", dep);
           MutablePropertyValues depArgValues = new MutablePropertyValues();
           depArgValues.addPropertyValue(new PropertyValue("depid", "332"));
           depArgValues.addPropertyValue(new PropertyValue("name", "自科部"));
           dep.setPropertyValues(depArgValues);
       
           //用户Vo Bean
           //通过构造器方法注入
           AbstractBeanDefinition userVo = new RootBeanDefinition(UserVo.class);
           registry.registerBeanDefinition("userVo", userVo);
           ConstructorArgumentValues userArgValues = new ConstructorArgumentValues();
           userArgValues.addIndexedArgumentValue(0, "23214");
           userArgValues.addIndexedArgumentValue(1, "吴嘉贤");
           userArgValues.addIndexedArgumentValue(2, "332");
           userArgValues.addIndexedArgumentValue(3, "男");
           userArgValues.addIndexedArgumentValue(4, "123456");
           userArgValues.addIndexedArgumentValue(5, "VIP");
           userArgValues.addIndexedArgumentValue(6, dep);
           userVo.setConstructorArgumentValues(userArgValues);
       
           //测试BeanDefinitionRegistry
           //此接口负责注册管理Bean
           System.out.println(registry.getBeanDefinitionCount());
           System.out.println(Arrays.toString(registry.getBeanDefinitionNames()));
           //获取Bean定义
           BeanDefinition beanDefinition = registry.getBeanDefinition("dep");
           //获取Bean的各个参数
           MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();
           //获取Bean参数的值
           System.out.println(mutablePropertyValues.get("depid"));
       
       
           //返回registry
           return (BeanFactory) registry;
       }
       ```
   
     - BeanFactory、BeanDefinitionRegistry以及DefaultListableBeanFactory的关系
   
       <div align="center">
       <img src="https://github.com/XiaoHuaShiFu/img/blob/master/BeanFactory%E3%80%81BeanDefinitionRegistry%E4%BB%A5%E5%8F%8ADefaultListableBeanFactory%E7%9A%84%E5%85%B3%E7%B3%BB.jpg?raw=true" width="50%" height="10%" style="transform: rotate(0deg);">
        </div>
   
   - 外部配置文件方式
   
     - properties方式
   
       - 使用PropertiesBeanDefinitionReader读取配置文件
     
       - 示例：
     
         ```java
         public static void testProperties() {
             //bean的注册和管理
             DefaultListableBeanFactory beanRegistry = new DefaultListableBeanFactory();
             //读取properties的信息的类
             PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(beanRegistry);
             //加载properties信息
             //其中classpath指代classes文件夹
             reader.loadBeanDefinitions("classpath:beans.properties");
             //转换成BeanFactory
             BeanFactory container = beanRegistry;
         
             System.out.println(container.getBean("userVo"));
         }
         ```
     
         ```properties
         userVo.(class)=com.springjiemi.vo.UserVo
         String userid, String name, String depid, String sex, String password, String roleUser, Dep dep
         #通过constructor方法注入
         userVo.$0=332
         userVo.$1=吴嘉贤
         userVo.$2=322
         userVo.$3=男
         userVo.$4=123456
         userVo.$5=VIP
         userVo.$6(ref)=dep
         
         #通过setter方法注入
         dep.(class)=com.springjiemi.pojo.Dep
         dep.depid=322
         dep.name=自科部
         ```
     
     - XML方式
     
       - 使用XmlBeanDefinitionReader读取配置文件，也可以用BeanFactory简化注册过程。
     
       - 示例：
     
         ```java
         //bean的注册和管理
         DefaultListableBeanFactory beanRegistry = new DefaultListableBeanFactory();
         //读取XML的信息的类
         XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanRegistry);
         //加载XML信息
         //其中classpath指代classes文件夹
         reader.loadBeanDefinitions("classpath:benas.xml");
         //转换成BeanFactory
         BeanFactory container = beanRegistry;
         
         //另外的方法，其内部也和上面做法类似
         //BeanFactory container = new XmlBeanFactory(new ClassPathResource("benas.xml"));
         
         System.out.println(container.getBean("userVo"));
         ```
     
         ```xml
         <bean id="userVo" class="com.springjiemi.vo.UserVo">
             <constructor-arg value="3333"/>
             <constructor-arg value="wjx"/>
             <constructor-arg value="322"/>
             <constructor-arg value="男"/>
             <constructor-arg value="123456"/>
             <constructor-arg value="VIP"/>
             <constructor-arg ref="dep"/>
         </bean>
         
         <bean id="dep" class="com.springjiemi.pojo.Dep">
             <property name="depid" value="322"/>
             <property name="name" value="自科部"/>
         </bean>
         ```
     
     - 注解方式
     
       - 在beans里配置
     
         ```xml
         <context:component-scan base-package="com.springjiemi.pojo"/>
         ```
     
       - 示例：
     
         ```java
         public static void testAnnotation() {
             //bean的注册和管理
             ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
         
             AnnotationPojo annotationPojo = (AnnotationPojo) ctx.getBean("annotationPojo");
             annotationPojo.print();
         }
         ```
     
   - XML方式
   
     - beans
   
       - 它拥有的属性：
   
         | 属性                     | 描述                                                         |
         | ------------------------ | ------------------------------------------------------------ |
         | default-lazy-init        | 默认false。标志是否对所有的<bean>进行延迟初始化              |
         | default-autowire         | 默认no，可取byName、byType、constructor以及autodetect。自动绑定用那种默认绑定方式。 |
         | default-init-method      | 如果所管辖的<bean>按照某种规则，都有同样名称的初始化方法的话，可以在这里同一指定这个初始化方法名，而不用在每个<bean>上都重复单独指定 |
         | default-dependency-check | 默认none，可取objects、simple、all。是否和什么程度的依赖检查。 |
         | default-destroy-method   | 与default-init-method相类似，这个指代销毁方法。              |
   
       - 它拥有的元素：
   
         - description：描述信息。
   
         - import：导入其他配置文件
   
         - alias：为<bean>起别名。
   
         - bean：
   
           - id属性：唯一标识符。
           - name属性：起别名。
           - class属性：bean的类型。
   
           - 注入方式：
   
             - constructor方式：
   
               - type属性：标明此参数的类型，适用于多个构造器无法区分的情况下。
   
               - index属性：标明此参数所在构造器的参数列表的第几个。从0开始。
   
               - 示例：
   
                 ```xml
                 <bean id="userVo" class="com.springjiemi.vo.UserVo">
                     <constructor-arg value="3333"/>
                     <constructor-arg value="wjx"/>
                     <constructor-arg value="322"/>
                     <constructor-arg value="男"/>
                     <constructor-arg value="123456"/>
                     <constructor-arg value="VIP"/>
                     <constructor-arg ref="dep"/>
                 </bean>
                 ```
   
             - setter方式：
   
               ```xml
               <bean id="dep" class="com.springjiemi.pojo.Dep">
                   <property name="depid" value="322"/>
                   <property name="name" value="自科部"/>
               </bean>
               ```
   
             - <property>和<constructor-arg/>中的可选用项：
   
               - <value>：类似于value属性，只能注入String类型和原始类型及它们的包装器类型。
   
               - <ref>：类似于ref属性，引用容器中其他的对象实例。
   
                 - local属性：同一个配置文件的对象。
   
                 - parent属性：父容器中定义的对象引用。
   
                   - BeanFactory可以嵌套加载：
   
                     ```java
                     BeanFactory p = new XmlBeanFactory(new ClassPathResource("p.xml"));
                     BeanFactory c = new XmlBeanFactory(new ClassPathResource("c.xml"),p);
                     ```
   
                 - bean属性：所有实例对象。
   
               - <idref>：此会在解析配置的时候检查所依赖对象的beanName是否存在，而不用等到运行时才发现beanName所对应的对象实例不存在。
   
               - 内部<bean>：类似内部类，外部无法直接访问。
   
               - <list>：里面的元素可以用<ref>、<value>、<bean>
   
               - <set>：里面的元素可以用<ref>、<value>、<bean>
   
               - \<map\>：里面元素是\<entry\>
   
                 - \<entry\>里面的元素是\<key\>或\<key-ref\>+\<ref\>、\<value\>、\<value-ref\>、\<list\>等。
   
               - \<props\>：\<prop key="keyValue"\>StringValue \<\\prop\>
   
               - \<null\>：指定为null。如果用\<value\>\<\\value\>指示的是""。
   
           - depends-on属性：在实例化某个对象之前先实例化另外一个实例对象。适用于非显示依赖关系。
   
           - autowire属性：自动注入。
   
             - autodetect：如果对象拥有默认无参数的构造方法，容器会考虑byType的自动绑定模式，否则，会使用constructor模式。如果用constructor后还有未绑定属性，也会用byType对剩余属性进行自动绑定。
             - 手工绑定会覆盖自动绑定。
             - 自动绑定不适用于”String、原生类型、Classes类型及这些类型的数组“。
   
           - dependency-check属性：确保自动绑定后，最终确认每个对象所依赖的对象是否按照所预期的那样被注入。
   
           - lazy-init属性：延迟加载。
   
           - parent属性：继承父bean的一些参数。类似Java的extends。
   
           - abstact属性：作为一个模板，里面的参数可以被子bean继承，自身在容器初始化时不会实例化。类似Java的抽象类。
   
             - 在ApplicationContext容器下，初始化时会默认实例化所有bean，可以用此属性来避免容器将其实例化。
   
           - scope属性：bean的生命周期。
   
             - singleton：一个容器里的单例。
             - prototype：每次收到要此对象的请求都会生产一新的实例。
             - request：每个HTTP请求创建一个全新的实例对象，请求结束后，此对象生命周期结束。
             - session：每个session创建一个实例对象。
             - globalSession：只有应用在基于portlet的Web应用程序中才有一样，它映射到portlet的global范围的session。
             - 自定义scopo类型：
               - 实现scope接口。
               - 用ConfigurableBeanFactory的registerScope方法取注册此scope。
               - 如果使用ApplicationContext容器，它可以自动识别并加载BeanFactoryPostProcessor，所以可以在**bean**配置文件中，通过CustomScopeConfigureer来注册自定义Scope。
   
     - 工厂方法和FactoryBean
   
       - 静态工厂方法注入
   
         ```xml
         <!--通过工厂类和工厂方法名注入对象-->
         <bean id="bar" class="com.springjiemi.pojo.StaticBarFactory" factory-method="getIstance"/>
         
         <!--方法带参数-->
         <bean id="bar" class="com.springjiemi.pojo.StaticBarFactory" factory-method="getIstance">
             <constructor-arg value="arg"/>
         </bean>
         ```
   
       - 非静态工厂方法
   
         ```xml
         <!--工厂bean-->
         <bean id="barFactory" class="com.springjiemi.pojo.BarFactory"/>
         
         <bean id="bar" factory-bean="barFactory" factory-method="getIstance">
             <constructor-arg value="arg"/>
         </bean>
         ```
   
       - FactoryBean
   
         - 示例：
   
           ```java
           public class NextDayDateFactoryBean implements FactoryBean<DateTime>{
           
               @Override
               public DateTime getObject() throws Exception {
                   return new DateTime().plusDays(1);
               }
           
               @Override
               public Class<?> getObjectType() {
                   return DateTime.class;
               }
           
               @Override
               public boolean isSingleton() {
                   return false;
               }
           }
           ```
   
           ```xml
           <bean id="nextDayDate" class="com.springjiemi.pojo.NextDayDateFactoryBean"/>
           ```
   
           ```java
           FactoryBean factoryBean = (FactoryBean)container.getBean("&nextDayDate");
           ```
   
         - 常见的FactoryBean实现：
   
           - JndiObjectFactoryBean
           - LocalSessionFactoryBean
           - SqlMapClientFactoryBean
           - ProxyFactoryBean
           - TransactionProxyFactoryBean
       
     - 方法注入（Method Injection）与方法替换（Method Replacement）
     
       - 方法注入：每次调用都注入一个新的实例。
     
         - 示例：
     
           ```xml
           <bean id="userVo" class="com.springjiemi.vo.UserVo">
               <property name="userid" value="3333"/>
               <property name="name" value="wjx"/>
               <property name="depid" value="322"/>
               <property name="sex" value="男"/>
               <property name="password" value="123456"/>
               <property name="roleUser" value="VIP"/>
               <property name="dep" ref="dep"/>
               <!-- 每次调用getDep都返回一个新的dep实例 -->
               <!-- name指示要调用的方法 -->
               <!-- bean指示方法返回的实例 -->
               <!-- 实质是通过Cglib动态生成一个子类实现 -->
               <lookup-method name="getDep" bean="dep"/>
           </bean>
           
           <bean id="dep" class="com.springjiemi.pojo.Dep" scope="prototype">
               <property name="depid" value="322"/>
               <property name="name" value="自科部"/>
           </bean>
           ```
     
         - 使用BeanFactoryAware接口：容器对实现了此接口的对象，会把容器自身注入到此bean，这样此bean就拥有BeanFactory的引用。
     
           - 实现此接口：
     
             ```java
             public interface BeanFactoryAware extends Aware {
                 void setBeanFactory(BeanFactory var1) throws BeansException;
             }
             ```
     
             ```java
             private BeanFactory beanFactory;
             
             public Dep getDep() {
                 return (Dep) beanFactory.getBean("dep");
             }
             
             @Override
             public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
                 this.beanFactory = beanFactory;
             }
             ```
     
         - 使用ObjectFactoryCreatingFactoryBean：此类是FactoryBean的一个实现，它返回一个ObjectFactory实例。此实例可以返回容器管理的对象，隔离了客户端对象对BeanFactory的直接引用。
     
           - 实现：
     
             ```xml
             <bean id="userVo" class="com.springjiemi.vo.UserVo">
                 <property name="userid" value="3333"/>
                 <property name="name" value="wjx"/>
                 <property name="depid" value="322"/>
                 <property name="sex" value="男"/>
                 <property name="password" value="123456"/>
                 <property name="roleUser" value="VIP"/>
                 <property name="dep" ref="dep"/>
                 
                 <!-- 注入工厂 -->
                 <property name="depBeanFactory" ref="depBeanFactory"/>
             </bean>
             
             <bean id="dep" class="com.springjiemi.pojo.Dep" scope="prototype">
                 <property name="depid" value="322"/>
                 <property name="name" value="自科部"/>
             </bean>
             
             <!-- 此工厂只是管理特定的bean -->
             <bean id="depBeanFactory" class="org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean">
                 <property name="targetBeanName">
                     <idref bean="dep"/>
                 </property>
             </bean>
             ```
     
             ```java
             //管理特定bean的工厂
             private ObjectFactory<Dep> depBeanFactory;
             public Dep getDep() {
                 //使用此工厂返回所需要的实例对象
                 return depBeanFactory.getObject();
             }
             //设置此工厂
             public void setDepBeanFactory(ObjectFactory<Dep> depBeanFactory) {
                 this.depBeanFactory = depBeanFactory;
             }
             ```
     
         - 可以使用ServiceLocatorFactoryBean来代替ObjectFactoryCreatingFactoryBean，该FactoryBean可以让我们自定义工厂接口，而不用使用ObjectFactory。
     
           - 示例：
     
             ```xml
             <bean id="userVo" class="com.springjiemi.vo.UserVo">
                 <property name="userid" value="3333"/>
                 <property name="name" value="wjx"/>
                 <property name="depid" value="322"/>
                 <property name="sex" value="男"/>
                 <property name="password" value="123456"/>
                 <property name="roleUser" value="VIP"/>
                 <property name="dep" ref="dep"/>
             
                 <property name="depFactory" ref="depFactory"/>
             </bean>
             
             <bean id="dep" class="com.springjiemi.pojo.Dep" scope="prototype">
                 <property name="depid" value="322"/>
                 <property name="name" value="自科部"/>
             </bean>
             
             <!-- 设置工厂对象的接口 -->
             <bean id="depFactory" class="org.springframework.beans.factory.config.ServiceLocatorFactoryBean">
                 <property name="serviceLocatorInterface" value="com.springjiemi.pojo.DepFactory"/>
             </bean>
             ```
     
             ```java
             private DepFactory depFactory;
             public Dep getDep() {
                 return depFactory.getDep();
             }
             public void setDepFactory(DepFactory depFactory) {
                 this.depFactory = depFactory;
             }
             ```
     
             ```java
             public interface DepFactory {
                 Dep getDep();
             }
             ```
     
     - 方法替换：实现方法拦截功能。
     
       - 实现MethodReplacer接口作为替换类：
     
         ```java
         public class UserVoReplacer implements MethodReplacer {
             private static final transient Logger logger = LoggerFactory.getLogger(UserVoReplacer.class);
             @Override
             public Object reimplement(Object o, Method method, Object[] objects) throws Throwable {
                 logger.info("method" + method.getName());
                 return "hahhahah test";
             }
         }
         ```
     
       - 注入替换类并指明要替换的方法：
     
         ```xml
         <bean id="userVo" class="com.springjiemi.vo.UserVo">
             <property name="userid" value="3333"/>
             <property name="name" value="wjx"/>
             <property name="depid" value="322"/>
             <property name="sex" value="男"/>
             <property name="password" value="123456"/>
             <property name="roleUser" value="VIP"/>
             <property name="dep" ref="dep"/>
         
             <!-- 替换某个方法 -->
             <replaced-method name="toString" replacer="userVoReplacer" />
         </bean>
         
         <!-- 替换方法的替换类 -->
         <bean id="userVoReplacer" class="com.springjiemi.pojo.UserVoReplacer"/>
         ```
     
         - 可以用\<arg-type\>指明要替换方法的参数类型，如果有重载方法的话
     
           ```xml
           <arg-type match="String"/>
           ```
   
4. 容器

   - Spring的IoC容器可以分为两个阶段：容器启动阶段和Bean实例化阶段。

   - 容器启动阶段：

     - 加载配置：加载Configuration MetaData。
     - 分析配置信息。
     - 装备到BeanDefinition。生成Bean定义。
     - 方式
       - 代码方式。
       - 依赖工具类（BeanDefinitionReader）进行解析和分析，并将分析后的信息编组成为相应的BeanDefinition，然后把这些BeanDefinition注册到相应的BeanDefinitionRegistry。

   - Bean实例化阶段

     - 实例化对象。
     - 装配依赖。
     - 生命周期回调。
     - 对象其他处理。
     - 注册回调接口。

     - 当请求通过容器的getBean方法请求某个对象时，就会根据BeanDefinition所提供的信息实例化被请求的对象，为其注入依赖。如果该对象实现了某些回调接口，也会根据回调接口的要求来装配它。当对象装配完毕后，容器会立即将对象返回给请求方法使用。

   - 插手容器的启动

     - Spring提供BeanFactoryPostProcessor的容器扩展机制。允许我们在容器实例化对象前，对注册到容器的BeanDefinition所保存的信息做相应的修改。相当于在容器实现的第一阶段最后加入一道工序，让我们对最终的BeanDefinition做一些额外的操作，比如修改bean定义的某些属性，为bean定义增加其他信息等等。

     - 如果一个容器有用多个BeanFactoryPostProcessor，需要同时实现Spring的Ordered接口，以保证按预先顺序执行。

     - Spring已经实现的BeanFactoryPostProcessor：

       - PropertyPlaceholderConfigurer
         - 实现用占位符（PlaceHolder）${jdbc.url}来指定properties里面的属性。
         - 在BeanFactory第一阶段加载完成所有配置信息是，BeanFactory中保存的对象的属性信息还是以占位符的形式存在。当PropertyPlaceholderConfigurer作为BeanFactoryPostProcessor被应用时，它会使用properties配置文件中的配置信息来替换相应BeanDefinition中占位符所表示的属性值。这样当进入容器实现第二阶段实例化bean时，bean定义中的属性值就是最终替换完成的了。
         - PropertyPlaceholderConfigurer不单会从配置的properties文件中加载配置项，还会检查Java的System类中的Properties。
           - 可以通过setSystemPropertiesMode()或setSystemPropertiesModeName()来控制是否加载或覆盖System相应的Properties。
           - PropertyPlaceholderConfigurer提供了SYSTEM_PROPERTIES_MODE_NEVER、SYSTEM_PROPERTIES_MODE_FALLBACK、SYSTEM_PROPERTIES_MODE_OVERRIDE三种模式。默认采用SYSTEM_PROPERTIES_MODE_FALLBACK（备选模式）。
         
       - PropertyOverrideConfigurer
         
       - 用来覆盖bean定义中的property信息。
     
       - PropertyOverrideConfigurer的properties文件结构：
     
           ```properties
        #将beanName的bean的propertyName属性的值替换成value
           beanName.propertyName=value
           userVo.userid=233
           ```
           
         - ```xml
           <bean class="org.springframework.beans.factory.config.PropertyOverrideConfigurer">
               <property name="location" value="beans-override.properties"/>
           </bean>
           ```
      ```
         
       - PropertyOverrideConfigurer的父类PropertyResourceConfigurer提供一个protected类型的方法convertPropertyValue，允许子类覆盖这个方法对相应的配置项进行转换，如对加密后的字符串解密之后再覆盖到相应的bean定义中。当然，PropertyPlaceholderConfigurer也同样继承了PropertyResourceConfigurer，也有同样的功能。
         
     - CustomEditorConfigurer：注册自定义的PropertyEditor。进行配置文件中的数据类型与真正的业务对象所定义的数据类型转换。
     
         - 它可以将会用到的信息注册到容器，不会对BeanDefinition做任何改变。它帮助传达转换规则相关的信息。
     
         - Spring内部通过PropertyEditor来帮助String类型到其他类型的转换。可以对原始类型，String，Color，Font等类型进行转换。
     
         - Spring自身实现了一些PropertyEditor，大部分位于propertyeditors包下。
     
           - StringArrayPropertyEditor。将符合CSV格式的字符串转换成String[]数组形式，默认（,）分隔的字符串，可以指定自定义的字符串分隔符。
           - ClassEditor。根据String类型的class名称，直接将其转换成相应的Class对象，相当于通过Class.forName(String)完成功能。还有ClassArrayEditor（接受String[]）。
           - FileEditor。对应File类型的PropertyEditor。还有InputStreamEditor、URLEditor。
           - LocaleEditor。针对Locale类型。
           - PatternEditor。针对Pattern。
           - 以上的PropertyEditor，容器会默认加载使用。
     
         - 自定义PropertyEditor：实现PropertyEditor接口，也可以继承PropertyEditorSupport类，然后实现setAsText(String)方法。
     
           - 如果只是支持从String到对应对象的转换，只需覆盖setAsText(String)方法，如果需从对象到String，需覆盖getAsText()方法。
     
           - **示例：**使用propertyEditorRegistrars属性来指定自定义的PropertyEditor，这样我们需要多给出一个PropertyEditorRegistrar的实现。
     
             ```java
             public class DatePropertyEditor extends PropertyEditorSupport {
                 private String datePattern;
             
                 @Override
                 public void setAsText(String text) throws IllegalArgumentException {
                     DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(getDatePattern());
                     Date dateValue = dateTimeFormatter.parseDateTime(text).toDate();
                     setValue(dateValue);
                 }
             
                 public String getDatePattern() {
                     return datePattern;
                 }
             
                 public void setDatePattern(String datePattern) {
                     this.datePattern = datePattern;
                 }
             }
      ```
     
             ```java
             public class DatePropertyEditorRegistrar implements PropertyEditorRegistrar {
             
                 private PropertyEditor propertyEditor;
             
                 @Override
                 public void registerCustomEditors(PropertyEditorRegistry propertyEditorRegistry) {
                     propertyEditorRegistry.registerCustomEditor(Date.class, getPropertyEditor());
                 }
             
                 public PropertyEditor getPropertyEditor() {
                     return propertyEditor;
                 }
             
                 public void setPropertyEditor(PropertyEditor propertyEditor) {
                     this.propertyEditor = propertyEditor;
                 }
             }
             ```
         
             ```xml
             <!-- 注册Configurer -->
             <bean class="org.springframework.beans.factory.config.CustomEditorConfigurer">
                 <property name="propertyEditorRegistrars">
                     <list>
                         <ref bean="datePropertyEditorRegistrar"/>
                     </list>
                 </property>
             </bean>
             
             <!-- 注册Registrar -->
             <bean id="datePropertyEditorRegistrar" class="com.springjiemi.pojo.DatePropertyEditorRegistrar">
                 <property name="propertyEditor" ref="datePropertyEditor"/>
             </bean>
             
             <!-- 注册Editor -->
             <bean id="datePropertyEditor" class="com.springjiemi.pojo.DatePropertyEditor">
                 <property name="datePattern" value="yyyy-MM-dd"/>
             </bean>
             ```
     
     - BeanFactory应用BeanFactoryPostProcessor
     
       - **示例：**在Java里注册
     
         ```java
      //BeanFactory
         ConfigurableListableBeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("benas.xml"));
      //声明BeanFactoryPostProcessor
         PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
         propertyPlaceholderConfigurer.setLocation(new ClassPathResource("datasource.properties"));
         //注册到beanFactory
         propertyPlaceholderConfigurer.postProcessBeanFactory(beanFactory);
         ```
     
     - ApplicationContext应用BeanFactoryPostProcessor
     
       - **示例：**XML注册
       
         ```xml
         <bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
               <property name="locations">
                   <list>
                       <value>datasource.properties</value>
                       <value>spring.properties</value>
                   </list>
               </property>
           </bean>
         ```
     
   - 了解bean的一生
   
     - Bean的实例化过程：
   
       ![](https://github.com/XiaoHuaShiFu/img/blob/master/Bean%E5%AE%9E%E4%BE%8B%E5%8C%96%E8%BF%87%E7%A8%8B.png?raw=true)
   
     - 当getBean方法发现该bean定义没有被实例化之后，会通过createBean()方法来进行具体的对象实例化。
   
       - AbstractBeanFactory类有getBean()方法的实现逻辑。
       - AbstractAutowireCapableBeanFactory有createBean()方法的全貌。
   
     - BeanWrapper：容器内部采用“策略模式”来决定采用那种方式初始化Bean实例。
   
       - 通常通过反射或CGLIB动态字节码生成来初始化相应的bean实例或者动态生成其子类。
   
         - InstantiationStrategy定义实例化策略的抽象接口，其直接子类SimpleInstantiationStrategy实现了简单的对象实例化功能，通过反射。
         - CglibSubclassingInstantiationStrategy继承了SimpleInstantiationStrategy的反射实例化对象功能，并可以通过CGLIB动态字节码，实现方法注入的对象实例化功能。容器默认使用此类。
   
       - 容器构造完成的对象实例，还会用BeanWrapper对bean实例进行包裹，然后获取或设置bean的相应属性值。
   
         - BeanWrapper继承了PropertyAccessor接口，可以统一的对对象属性进行访问。也继承了PropertyEditorRegistry和TypeConverter接口。
   
         - 构造完成对象之后，spring会根据对象实例构造一个BeanWrapperImpl实例，然后将在CustomEditorConfigurer注册的PropertyEditor复制一份给BeanWrapperImpl实例。这样BeanWrapper就可以进行类型转换、设置对象属性值。
   
           - 示例：使用BeanWrapper对bean属性进行设置非常方便
   
             ```java
             BeanWrapper beanWrapper = new BeanWrapperImpl(ctx.getBean("userVo"));
             //使用BeanWrapper对bean的属性进行设置
             beanWrapper.setPropertyValue("name", "xxxxxxxxxxxxxxxxxx");
             ```
   
     - Aware接口：当对象实例化完成，并设置完相关属性及依赖后，spring容器会检查当前对象实例是否实现了一系列的以Aware命名结尾的接口定义。如果是，则将这些Aware接口定义中规定的依赖注入给当前对象实例。
   
       - Aware接口有：
         - BeanNameAware：把beanName设置到当前实例。
         - BeanClassLoaderAware：把当前bean的ClassLoader注入当前对象实例。默认使用ClassUtils类的Classloader。
         - BeanFactoryAware：BeanFactory容器会将自身设置到当前对象实例。
       - 对于ApplicationContext容器还有（使用BeanPostProcessor方式）：
         - ResourceLoaderAware：会将ApplicationContext自身设置到对象实例。ApplicationContext实现了ResourceLoader接口。
         - ApplicationEventPublisherAware：会将ApplicationContext自身设置到对象实例。ApplicationContext实现了ApplicationEventPublisher接口。
         - MessageSourceAware：会将ApplicationContext自身设置到对象实例。ApplicationContext通过MessageSource接口提供国际化支持。
         - ApplicationContextAware：会将ApplicationContext自身设置到对象实例。
   
     - BeanPostProcessor（bean的后置处理器）：处理容器内所有符合条件的实例化后的对象实例。
   
       - postProcessBeforeInitialization(bean, beanName)方法是BeanPostProcessor前置处理。
   
       - postProcessAfterInitialization(bean, beanName)方法是BeanPostProcessor后置处理。
   
       - 可以用于处理标记接口实现类，或者为当前对象提供代理实现。Application的Aware接口实际上就是通过BeanPostProcessor的方式进行处理的。
   
         - ApplicationContext容器会检测到之前注册到容器的ApplicationContextAwareProcessor（BeanPostprocessor）的实现类，然后会调用postProcessBeforeInitialization()方法，检查并设置Aware相关依赖。
   
       - 还可以用于对对象实例或字节码增强当前对象实例；Spring的AOP也使用BeanPostProcessor来为对象生成代理对象，如BeanNameAutoProxyCreator。
   
       - 自定义BeanPostProcessor
   
         - 标注需要进行解密的实现类：
   
           ```java
           public interface PasswordDecodable {
               String getEncodedPassword();
               void setDecodedPassword(String password);
           }
           
           public class UserVo implements BeanNameAware, PasswordDecodable {
           }
           ```
   
         - 实现相应的BeanPostProcessor对符合条件的Bean实例进行处理。
   
           ```java
           public class PasswordDecodePostProcessor implements BeanPostProcessor {
               @Override
               public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                   if (bean instanceof  PasswordDecodable) {
                       String encoded = ((PasswordDecodable) bean).getEncodedPassword();
                       String decoded = encoded + "decoded success";
                       ((PasswordDecodable) bean).setDecodedPassword(decoded);
                   }
                   return bean;
               }
               @Override
               public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                   return bean;
               }
           }
           ```
   
         - 将自定义的BeanPostProcessor注册到容器
   
           - 可以通过ConfigurableBeanFactory的addBeanPostProcessor()方法注册到容器。
   
           - 对于ApplicationContext容器，会自动识别并注册XML配置里的BeanPostProcessor。
   
             ```xml
             <bean id="passwordDecodePostProcessor" class="com.springjiemi.pojo.PasswordDecodePostProcessor"/>
             ```
   
         - InstantiationAwareBeanPostProcessor接口可以在对象实例化过程中导致短路的效果。也就是在实例化对象步骤之前，容器会检查是否有注册InstantiationAwareBeanPostProcessor接口，如果有会首先使用相应的InstantiationAwareBeanPostProcessor来构造对象实例。构造完成之后直接返回对象实例。
   
     - InitializingBean：对象生命周期标识接口。
     
       - 在进行前置处理之后，会检查是否实现了InitializingBean接口，如果实现了会第哦啊用其afterPropertiesSet()方法进一步处理实例。
     
       - 可以使用\<bean\>的init-method属性对系统中业务对象的自定义初始化操作可以以任何方式命名。
     
       - 可以用\<beans\>的default-init-method指定全部bean的统一初始化方法名。
     
       - 一般只有在集成第三方库，或者其他特殊情况下，才会需要使用此特性。
     
       - 示例：
     
         ```java
         private void init() {
             System.out.println("this is init method!!!");
         }
         ```
     
         ```xml
         <bean id="userVo" class="com.springjiemi.vo.UserVo" init-method="init">
         	<property name="userid" value="${ftp.server.http.prefix}"/>
             <property name="name" value="${db.driverClassName}"/>
             <property name="depid" value="322"/>
             <property name="sex" value="男"/>
             <property name="password" value="123456"/>
             <property name="roleUser" value="VIP"/>
             <property name="dep" ref="dep"/>
             <property name="date" value="2019-05-11"/>
         </bean>
         ```
     
       - 可以同时使用InitializingBean和init-method实现两个初始化方法。
     
     - DisposableBean与destroy-method：对象销毁方法。
     
       - 示例：
     
         ```java
         private void destroy() {
             System.out.println("this is destroy method!!!");
         }
         ```
     
         ```xml
         <bean id="userVo" class="com.springjiemi.vo.UserVo" init-method="init" destroy-method="destroy">
         ```
     
       - 销毁方法不会自动执行，需要手动设置。
     
         - BeanFactory容器需要调用：
     
           ```java
           ((ConfigurableListableBeanFactory)container).destroySingletons();
           ```
     
         - ApplicationContext容器需调用：
     
           ```java
           ((AbstractApplicationContext) ctx).registerShutdownHook();
           ```

# 5、ApplicationContext容器

1. 统一资源加载策略

   - 使用Resource接口作为所有资源的抽象和访问接口。

     - ByteArrayResource。将字节（byte）数组提供的数据作为一种资源进行封装。
     - ClassPathResource。对ClassPath中加载具体资源进行封装，可以使用指定的类加载器（ClassLoader）或给定的类进行资源加载。
     - FileSystemResource。对File类型进行封装。
     - UrlResource。对URL的具体资源查找定位的实现类，内部委派URL进行具体的资源操作。
     - InputStreamResource。将InputStream作为一种资源。
     - 如果需自定义此接口，可以继承AbstractResoource抽象类。

   - ResourceLoader：查找和定位资源的接口。

     - 通过getResource(location)方法可以指定资源位置，定位到具体的资源实例。

     - DefaultResourceLoader

       - 先检查以classpath:前缀打头，如果是，尝试构造ClassPathResource类型资源并返回。

       - 否则，尝试通过URL，如果没有抛出MalformedURLException则会构造UrlResource类型资源并返回。

       - 否则，委派getResourceByPath(string)来定位，该方法默认构造ClassPathResource类型资源并返回。

       - 该方法如果最终没有找到符合条件的资源，getResourceByPath()方法会构造一个实际上并不存在的资源并返回。

       - 示例：

         ```java
         ResourceLoader resourceLoader = new DefaultResourceLoader();
         Resource resource = resourceLoader.getResource("classpath:benas.xml");
         File file = resource.getFile();
         ```

     - FileSystemResourceLoader：

       - 继承自DefaultResourceLoader，并覆盖getResourceByPath(string)方法，使之以FileSystemResource类型返回。
       - FileSystemXmlApplicationContext也覆盖了getResourceByPath(string)方法，使之以FileSystemResource类型返回。
       
     - ResourcePatternResolver：根据路径的匹配模式批量查找的ResourceLoader。扩展自ResourceLoader。
     
       - 引入了classpath*:前缀。
       - PathMatchingResourcePatternResolver，支持单个查找资源，支持Ant风格的路径匹配模式（类似于**/*.suffix），支持classpath*:。
         - 可以指定一个ResourceLoader，默认会构造一个DefaultResourceLoader。
         - PathMatchingResourcePatternResolver内部会将匹配确定的资源路径委派给ResourceLoader来查找和定位资源。
     
   - Resource和ResourceLoader类层次图
   
     ![](https://github.com/XiaoHuaShiFu/img/blob/master/Resource%E5%92%8CResourceLoader%E7%B1%BB%E5%B1%82%E6%AC%A1%E5%9B%BE.jpg?raw=true)
   
   - ApplicationContext与ResourceLoader
   
     - ApplicationContext继承了ResourcePatternResolver，也间接实现了ResourceLoader接口。可以把ApplicationContext看作一个ResourceLoader甚至ResourcePatternResolver。
   
     - AbstractApplicationContext继承了DefaultResourceLoader，它的getResource(String)方法直接调用DefaultResourceLoader的。还有ResourcePatternResolver的getResources(String)也调用此方法。AbstractApplicationContext内部的resourcePatternResolver就是PathMatchingResourcePatternResolver，它的ResourceLoader就是AbstractApplicationContext对象本身。
   
     - AbstractApplicationContext作为ResourceLoader和ResourcePatternResolver图
   
       ![](https://github.com/XiaoHuaShiFu/img/blob/master/AbstractApplicationContext%E4%BD%9C%E4%B8%BAResourceLoader%E5%92%8CResourcePatternResolver.jpg?raw=true)
   
     - 扮演ResourceLoader角色
   
       - ResourceLoader类型的注入
   
         - 使用构造方法注入或setter方法注入。（需要自己构造ResourceLoader实例）
   
         - 使用ResourceLoaderAware和ApplicationContextAware接口。（需要实现Aware接口）
   
         - 示例：
   
           ```java
           private ResourceLoader resourceLoader;
           @Override
           public void setResourceLoader(ResourceLoader resourceLoader) {
               this.resourceLoader = resourceLoader;	 	       System.out.println(this.resourceLoader.getResource("classpath:benas.xml"));
           }
           ```
   
     - Resource类型注入
   
       - 示例：
   
         ```java
         private Resource resource;
         public void setResource(Resource resource) {
             this.resource = resource;
         }
         ```
   
         ```xml
         <property name="resource" value="applicationContext.xml"/>
         ```
   
       - ApplicationContext启动时，会通过ResourceEditorRegistrar来注册针对Resorce类型的PropertyEditor实现到容器中，这个PropertyEditor叫ResourceEditor。这样ApplicationContext就可以正确地识别Resource类型的依赖。
   
       - ResourceEditor是让ApplicationContext作为ResourceLoader去定位资源。
   
       - 如果对象依赖一组Resource，Spring提供了ResourceArrayPropertyEditor实现，只需要通过CustomEditorConfigurar告知容器即可。
   
2. 国际化信息支持（I18n MessageSource）

   - Java SE的国际化支持

     - Locate

     - ResourceBundle：用来保存特定的某个Locale信息，管理一组信息序列，所有的信息序列有一个统一的一个basename，然后特定的Locale的信息，可以根据basename后追加的语言或者地区代码来区分。

       - 示例：妻子红messages部分作ResourceBundle将加载资源的basename，其他语言或地区的资源在basename上追加Locale特定代码。

         ```properties
         messages.properties
         messages_zh.properties
         messages_zh_CN.properties
         messages_en.properties
         messages_en_US.properties
         ```

       - 每个资源文件中都有相同的键来标识具体资源条目，但每个资源相同键的内容根据Locale不同而不同。

         ```properties
         #messages_zh_CN.properties
         menu.file=文件({0})
         menu.edit=编辑
         
         #messages_en_US.properties
         menu.file=File({0})
         menu.edit=Edit
         ```

       - 可以通过ResourceBundle的getBundle(baseName, locale)方法获取不同Locale对象的ResourceBundle，然后根据资源的键获取相应的Locale的资源条目内容。

         - 示例：可以指定多个同基名，不同Locate的properties文件，通过统一的方式访问。

           ```java
           ResourceBundle bundle = ResourceBundle.getBundle("spring", Locale.CHINA);
           System.out.println(bundle.getString("ftp.user"));
           ```

   - MessageSource与ApplicationContext

     - MessageSource：进一步抽象的国际化信息访问接口，传入Locate、资源的键和相应的参数就可以获取相应的信息。

       - MessageSource接口：

         ```java
         public interface MessageSource {
            //如果没有找到返回默认的defaultMessage
            String getMessage(String code, Object[] args, String defaultMessage, Locale locale);
         	//没有默认信息，没找到会抛出异常
            String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException;
         	//使用MessageSourceResolvable对象对资源条目的键、信息参数等进行封装。
            String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException;
         }
         ```

     - ApplicationContext实现了MessageSource接口，所以也是一个MessageSource。

       - ApplicationContext默认委派容器里的名为messageSource的MessageSource接口实现来完成操作。如果找不到一个名字为MessageSource的实现，ApplicationContext内部会默认实例化一个不含任何内容的StaticMessageSource实例。

       - 示例：

         ```xml
         <bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
             <property name="basenames">
                 <list>
                     <value>spring</value>
                     <value>beans</value>
                 </list>
             </property>
         </bean>
         ```

         ```java
         MessageSource messageSource = (MessageSource) ctx.getBean("messageSource");
         System.out.println(messageSource.getMessage("dep.name", new Object[] {"牛逼"}, null));
         ```

     - 可用的MessageSource实现

       - StaticMessageSource：不用于生产。

       - ResourceBundleMessageSource：基于标准的ResourceBundle实现，对AbstractMessageSource进行扩展，提供了对多个ResourceBundle的缓存以提高查询速度。对参数化和非参数化的信息处理进行了优化，对参数化的信息格式化的MessageFormat实例也进行了缓存。

       - ReloadableResourceBundleMessageSource：基于标准的ResourceBundle实现。可通过cacheSeconds属性指定时间段，以定期刷新并检查底层properties资源文件是否有更变。可通过ResourceLoader来加载信息资源文件。使用ReloadableResourceBundleMessageSource要避免将信息资源文件放到classpath中，因为这样它就无法定期加载文件更变。

       - 示例：

         ```java
         ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
         messageSource.setBasename("beans");
         System.out.println(messageSource.getMessage("dep.name", new Object[] {"牛逼"}, null));
         ```

       - 可以基础AbstractMessageSource自定义MessageSource。

       - MessageSource类层次结构

         ![](https://github.com/XiaoHuaShiFu/img/blob/master/MessageSource%E5%B1%82%E6%AC%A1%E7%BB%93%E6%9E%84.jpg?raw=true)

     - MessageSourceAware和MessageSource注入

       - setter或构造器。
       - 实现MessageSourceAware，将ApplicationContext自身传递给此bean。

3. 容器内部事件发布

