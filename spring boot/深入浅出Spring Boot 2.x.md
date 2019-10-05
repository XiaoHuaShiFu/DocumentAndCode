# 1、

# 2、

1. 
2.  
3.  使用自定义配置
   - 通过application.properties配置项目
     - 如更改端口，可以使用server.port=8090
   - Spring Boot的参数配置按照以下优先级顺序进行加载：
     - 命令行参数；
     - 来自java:comp/env的JNDI属性；
     - Java系统属性（System.getProperties）；
     - 操作系统环境变量；
     - RandomValuePropertySource配置的random.*属性值；
     - jar包外部的application-{profile}.properties或application.yml（带spring.profile）配置文件；
     - jar包内部的application-{profile}.properties或application.yml（带spring.profile）配置文件；
     - jar包外部的application.properties或application.yml（不带spring.profile）配置文件；
     - jar包内部的application.properties或application.yml（不带spring.profile）配置文件；
     - @Configuration注解类上的@PropertySource；
     - 通过SpringApplication.setDefaultProperties指定的默认属性；

# 3、全注解下的Spring IoC

1. IoC容器

   - BeanFactory源码：

     ```java
     public interface BeanFactory {
     
         //前缀
     	String FACTORY_BEAN_PREFIX = "&";
     
         //getBean方法
     	Object getBean(String name) throws BeansException;
     
     	<T> T getBean(String name, Class<T> requiredType) throws BeansException;
     
     	Object getBean(String name, Object... args) throws BeansException;
     
     	<T> T getBean(Class<T> requiredType) throws BeansException;
     
     	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;
     
     	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);
     
     	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);
     
         //是否包含Bean
     	boolean containsBean(String name);
     
         //是否单例
     	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;
     
         //是否原型
     	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;
     
         //是否类型匹配
     	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;
     	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;
     
         //获取Bean的类型
     	@Nullable
     	Class<?> getType(String name) throws NoSuchBeanDefinitionException;
     
         //获取Bean的别名
     	String[] getAliases(String name);
     
     }
     ```

   - Spring IoC容器的接口设计

     - 结构图：

       <img src="https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%20IoC%E5%AE%B9%E5%99%A8%E7%9A%84%E6%8E%A5%E5%8F%A3%E8%AE%BE%E8%AE%A1.jpg?raw=true" style="zoom: 33%; transform: rotate(-90deg);" />

     - ApplicationContext接口继承了BeanFactory接口，还扩展了消息国际化接口MessageSource、环境可配置接口EnvironmentCapable、应用事件发布接口ApplicationEventPublisher和资源模式解析接口ResourcePatternResolver接口。

     - 其中基于注解的IoC容器是AnnotationConfigApplicationContext

2. 装配Bean

   1. 通过扫描装配Bean

      - 注解@Value可以为Bean指定具体的值

        ```java
        @Componentpublic class User {    
        	@Value("1")    
            private Long id;
        }
        ```

      - ComponentScan源码

        ```java
        //
        // Source code recreated from a .class file by IntelliJ IDEA
        // (powered by Fernflower decompiler)
        //
        
        package org.springframework.context.annotation;
        
        import java.lang.annotation.Documented;
        import java.lang.annotation.ElementType;
        import java.lang.annotation.Repeatable;
        import java.lang.annotation.Retention;
        import java.lang.annotation.RetentionPolicy;
        import java.lang.annotation.Target;
        import org.springframework.beans.factory.support.BeanNameGenerator;
        import org.springframework.core.annotation.AliasFor;
        
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.TYPE})
        @Documented
        @Repeatable(ComponentScans.class)
        public @interface ComponentScan {
            
            @AliasFor("basePackages")
            String[] value() default {};
        
            @AliasFor("value")
            String[] basePackages() default {};
        
            Class<?>[] basePackageClasses() default {};
        
            // Bean name生成器
            Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;
        
            // 作用域解析器
            Class<? extends ScopeMetadataResolver> scopeResolver() default AnnotationScopeMetadataResolver.class;
        
            // 作用域代理模式
            ScopedProxyMode scopedProxy() default ScopedProxyMode.DEFAULT;
        
            // 资源匹配模式
            String resourcePattern() default "**/*.class";
        
            // 是否启用默认的过滤器
            boolean useDefaultFilters() default true;
        
            // 当满足过滤器的条件时扫描
            ComponentScan.Filter[] includeFilters() default {};
        
            // 当不满足过滤器的添加时扫描
            ComponentScan.Filter[] excludeFilters() default {};
        
            // 是否延迟初始化
            boolean lazyInit() default false;
        
            // 定义过滤器
            @Retention(RetentionPolicy.RUNTIME)
            @Target({})
            public @interface Filter {
                // 过滤器类型，可以按注解类型或者正则式等过滤
                FilterType type() default FilterType.ANNOTATION;
        
                // 定义过滤的类
                @AliasFor("classes")
                Class<?>[] value() default {};
        
                // 定义过滤的类
                @AliasFor("value")
                Class<?>[] classes() default {};
        
                // 匹配方式
                String[] pattern() default {};
            }
        }
        ```

      - SpringBootApplication源码：

        ```java
        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @Inherited
        @SpringBootConfiguration
        @EnableAutoConfiguration
        // 自定义派出的扫描类
        @ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
        public @interface SpringBootApplication {
        	// 通过类型派出自动配置类
        	@AliasFor(annotation = EnableAutoConfiguration.class)
        	Class<?>[] exclude() default {};
        
            // 通过名字排除自动配置类
        	@AliasFor(annotation = EnableAutoConfiguration.class)
        	String[] excludeName() default {};
        
        	// 定义扫描包
        	@AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
        	String[] scanBasePackages() default {};
        
            // 定义被扫描的类
        	@AliasFor(annotation = ComponentScan.class, attribute = "basePackageClasses")
        	Class<?>[] scanBasePackageClasses() default {};
        
        }
        ```

      - 为了排除其他类，可以这样配置文件：

        ```java
        @SpringBootApplication
        //排除service.class类
        @ComponentScan(basePackages = {"com.springboot.chhapter3"}, excludeFilters = {@Filter(classes = Service.class)})
        ```

   2. 自定义第三方Bean

3.  依赖注入

   1.  
   2.  消除歧义性——@Primary和@Quelifier
      - @Primary：当有多个同类型Bean时，优先选这个Bean。
      - @Qualifier：声明要注入的Bean的名字。
   3. 带有参数的构造方法类的装配
      - 可以使用@Autowired、@Qualifier注解指定要注入的bean。

4. 生命周期

   - Bean定义过程：

     - SPring通过我们的配置，如@ComponentScan定义的扫描路径去找到带有@Component的类，这个过程就是一个资源定位的过程。
     - 一旦找到了资源，那么就开始解析，并且将定义的信息保存起来。此时还没有实例化Bean，仅有Bean的定义。
     - 然后会把Bean定义发布到Spring IoC容器中。此时IoC容器也只有Bean的定义，还没有Bean的实例。

   - Spring Bean的初始化过程：

     - 图片：

       ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%20%E5%88%9D%E5%A7%8B%E5%8C%96Bean.jpg?raw=true)

     - @ComponentScan有一个配置项lazyInit，只可以配置Boolean值，默认为false。

   - Spring Bean的生命周期：

     - 图片：

       ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%20Bean%E7%9A%84%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F.jpg?raw=true)

     - 其中BeanPostProcessor是针对所有Bean而言的。

     - 即使定义了ApplicationContextAware接口，有时候还是不会被调用，这根据你的IoC容器来决定。因为使用的Spring IoC容器的实现可能是BeanFactory接口，而不是实现ApplicationContext接口。

   - 可以使用@Bean(initMethod="init", destroyMethod='"destroy')来指定初始化方法和销毁方法。

5. 使用属性文件

   - 属性文件依赖：

     ```xml
             <dependency>
                 <groupId>org.springframework.boot</groupId>
                 <artifactId>spring-boot-configuration-processor</artifactId>
             </dependency>
     ```

   - 配置属性

     ```properties
     database.driverName=com.mysql.jdbc.Driver
     database.url=jdbc:mysql://47.106.107.142:3306/sakila
     database.username=root
     database.password=Wujiaxian!1599
     ```

   - 使用属性配置

     ```java
     @Component
     public class DataBaseProperties {
     
         @Value("${database.driverName}")
         private String driverName = null;
     
         @Value("${database.url}")
         private String url = null;
     
         private String username = null;
     
         private String password = null;
     
         public String getDriverName() {
             return driverName;
         }
     
         public void setDriverName(String driverName) {
             this.driverName = driverName;
         }
     
         public String getUrl() {
             return url;
         }
     
         public void setUrl(String url) {
             this.url = url;
         }
     
         public String getUsername() {
             return username;
         }
     
         @Value("${database.username}")
         public void setUsername(String username) {
             this.username = username;
         }
     
         public String getPassword() {
             return password;
         }
     
         @Value("${database.password}")
         public void setPassword(String password) {
             this.password = password;
         }
     }
     ```

   - 可以通过@Value注解，以${....}这样的占位符读取配置在属性文件的内容。可以放在属性上，也可以在方法上。

   - 使用@ConfigurationProperties

     - 使用@ConfigurationProperties的字符串和属性的名字组成的全限定名去配置文件里查找。

     - 示例：

       ```java
       @Component
       @ConfigurationProperties("database")
       public class DataBaseProperties {
       
           private String driverName = null;
       
           private String url = null;
       
           private String username = null;
       
           private String password = null;
       
           public String getDriverName() {
               return driverName;
           }
       
           public void setDriverName(String driverName) {
               this.driverName = driverName;
           }
       
           public String getUrl() {
               return url;
           }
       
           public void setUrl(String url) {
               this.url = url;
           }
       
           public String getUsername() {
               return username;
           }
       
           public void setUsername(String username) {
               this.username = username;
           }
       
           public String getPassword() {
               return password;
           }
       
           public void setPassword(String password) {
               this.password = password;
           }
       }
       ```

   - 配置额外的配置文件

     - 新建一个配置文件xxx.properties

     - 用@PropertySource去定义对应的属性文件，把它加载到Spring的上下文中

     - 配置示例：

       ```java
       @SpringBootApplication
       // value指定多个配置文件，classpath前缀表示去类文件目录下找到属性文件
       // ignoreResourceNotFound表示忽略配置文件找不到的问题
       @PropertySource(value = {"classpath:jdbc.properties"}, ignoreResourceNotFound = true)
       public class JvmApplication {
       
           public static void main(String[] args) {
               SpringApplication.run(JvmApplication.class, args);
           }
       
       }
       ```

6. 添加装配Bean：@Conditional

7. Bean的作用域：singleton、prototype、session、application

8. 使用@Profile：在不同环境中切换Bean

   - 可以在启动Java项目中，配置一下参数就可以启动Profile机制：JAVA_OPTS="-Dspring.profiles.active=dev"
   - 配置文件也可以通过新增application-dev.properties文件，这个适合如果把选项-Dspring.profiles.active配置值记为{profile}，则它会用application-{profile}.properties文件去代替原理默认的application.properties文件，然后启动Spring Boot程序。

9. 引入XML配置Bean：使用@ImportResource(value={"classpath:spring-other.xml"})导入XML配置的Bean。

10. 使用Spring EL

    - 如获取当前时间：其中T(...)代表引入类

      ```java
          @Value("#{T(System).currentTimeMillis()}")
          private Long initTime = null;
      ```

    - 字符串：

      ```java
          @Value("#{'Hello'}")
          private String s = null;
      ```

    - 科学记数法赋值：

      ```java
          @Value("#{3E3}")
          private Long number = null;
      ```

    - 使用其他Spring Bean的属性来给当前Bean属性赋值：其中?表示如果前面不为空，则执行后面的操作

      ```java
         @Value("#{dataBaseProperties.username}")
          private String s = null;
      
      @Value("#{dataBaseProperties.username?.toUpperCase()}")
          private String s = null;
      ```

    - 运算：

      ```java
         @Value("#{1+2}")
         @Value("#{beanName.pi == 3.14f}")
         @Value("#{beanName.str eq 'Spring Boot'}")
         @Value("#{beanName.str + 'xxxx'}")
         @Value("#{beanName.d > 1000 ? '大于' : '小于'}")
      ```

# 4、Spring AOP

# 5、访问数据库

1. 配置数据源

   1. 启动默认数据源

      - 在pom文件中添加spring-boot-starter-data-jpa后，它会默认配置数据源。

        ```xml
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-data-jpa</artifactId>
                </dependency>
        ```

   2. 配置自定义数据源

      - 添加MySQL的依赖

        ```xml
                <dependency>
                    <groupId>mysql</groupId>
                    <artifactId>mysql-connector-java</artifactId>
                </dependency>
        
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-jdbc</artifactId>
                </dependency>
        ```

      - 配置数据库的相关信息，配置在application.properties配置文件里。其中spring.datasource.driver-class-name可以不配置，因为Spring Boot会尽可能地区判断数据源的类型，然后根据其默认的情况去匹配驱动类。在不能匹配的情况下，才需要明确的配置驱动类。

        ```properties
        mybatis.config-location=mybatis-config.xml
        
        spring.datasource.url=jdbc:mysql://47.106.107.142:3306/sakila
        spring.datasource.username=root
        spring.datasource.password=Wujiaxian!1599
        
        #MyBatis映射文件通配
        mybatis.mapper-locations=classpath:mapper/*Mapper.xml
        ##MyBatis扫描别名包，和注解@Alias联用
        mybatis.type-aliases-package=top.xiaohuashifu.learn.jvm.pojo.DO
        #配置typeHandler的扫描包
        mybatis.type-handlers-package=top.xiaohuashifu.learn.jvm.typehandler
        logging.level.root=DEBUG
        logging.level.org.springframework=DEBUG
        logging.level.org.mybatis=DEBUG
        ```

      - 配置DBCP数据源：只需要加入DBCP的数据源的Maven依赖

        ```properties
                <dependency>
                    <groupId>org.apache.commons</groupId>
                    <artifactId>commons-dbcp2</artifactId>
                </dependency>
        ```

      - 设置数据源的相关配置

        ```properties
        #指定数据库连接池类型
        spring.datasource.type=org.apache.commons.dbcp2.BasicDataSource
        #最大等待连接中的数量
        spring.datasource.dbcp2.max-idle=10
        #最大连接活动数
        spring.datasource.dbcp2.max-total=50
        #最大等待毫秒数，单位为ms，超过时间会发出错误信息
        spring.datasource.dbcp2.max-wait-millis=10000
        #数据库连接池初始化连接数
        spring.datasource.dbcp2.initial-size=5
        ```

2.  

3.  

4.  整合MyBatis框架

   1.  MyBatis简介

      - 引入MyBatis的starter

        ```xml
                <dependency>
                    <groupId>org.mybatis.spring.boot</groupId>
                    <artifactId>mybatis-spring-boot-starter</artifactId>
                    <version>1.3.1</version>
                </dependency>
        ```

   2. MyBatis的配置

      - MyBatis是一个基于SqlSessionFactory构建的框架。对于SqlSessionFactory而言，它的作用是生成SqlSession接口对象，这个接口对象是MyBatis操作的核心。

      - SqlSessionFactory对象往往是单例的。构建SqlSessionFactory是通过配置类来完成的，对于mybatis-spring-boot-starter它会给予哦我们在配置文件application.properties进行配置的相关内容。

      - MyBatis配置内容

        - 图：

          ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/MyBatis%E9%85%8D%E7%BD%AE%E5%86%85%E5%AE%B9%E7%BB%93%E6%9E%84%E5%9B%BE.jpg?raw=true)

        - 其中可以配置的内容为：

          - properties：属性文件在实际应用中一般采用Spring进行配置，而不是MyBatis。
          - settings：它的设置将改变MyBatis的底层行为，可以配置映射规则，如自动映射和驼峰映射、执行器（Executor）类型、缓存等内容。
          - typeAliases（类型别名）：可以通过typeAliases配置自定义的别名；
          - typeHandlers（类型处理器）：对于读写数据库过程中的不同类型的数据进行自定义转换。一般用于枚举类型上。
          - objectFactory（对象工厂）：MyBatis生成返回的POJO时会调用的工厂类。一般使用默认的对象工厂类DefaultObjectFactory。
          - plugins（插件）：也称拦截器，是MyBatis最强大也是最危险的组件，它是通过动态代理和责任链模式来完成，可以修改MyBatis底层的实现功能。
          - environments（数据库环境）：可以配置数据库连接内容和事务。
          - databaseIdProvider（数据库厂商标识）：允许MyBatis配置多类型数据库支持。
          - mapper（映射器）：提供SQL和POJO映射关系。

        - 使用别名：在DO上通过@Alias注解指定别名

          ```java
          @Alias(value = "user")
          public class UserDO {
          
              private Integer id;
          
              private String username;
          
              private Gender gender;
              //...
          }
          ```

        - typeHandler：需要实现TypeHandler\<T\>，可以通过抽象类BaseTypeHandler来实现。@MappedJdbcTypes声明JdbcType的类型，@MappedTypes声明JavaType类型。

          - 示例代码：

            ```java
            // 声明jdbcType为整型
            @MappedJdbcTypes(JdbcType.INTEGER)
            // 声明JavaType为Gender
            @MappedTypes(value = Gender.class)
            public class GenderTypeHandler extends BaseTypeHandler<Gender> {
                // 设置非空性别参数
                @Override
                public void setNonNullParameter(PreparedStatement ps, int i, Gender gender, JdbcType jdbcType) throws SQLException {
                    ps.setInt(i, gender.getId());
                }
            
                // 通过列名读取性别
                @Override
                public Gender getNullableResult(ResultSet rs, String columnName) throws SQLException {
                    int id = rs.getInt(columnName);
                    return Gender.getGenderById(id);
                }
            
                // 通过下标读取性别
                @Override
                public Gender getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
                    int id = rs.getInt(columnIndex);
                    return Gender.getGenderById(id);
                }
            
                // 通过存储过程读取性别
                @Override
                public Gender getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
                    int id = cs.getInt(columnIndex);
                    return Gender.getGenderById(id);
                }
            }
            ```

        - 映射文件

          ```xml
          <?xml version="1.0" encoding="UTF-8" ?>
          <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
                  "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
          <mapper namespace="top.xiaohuashifu.learn.jvm.dao.UserMapper" >
              <resultMap id="BaseResultMap" type="top.xiaohuashifu.learn.jvm.pojo.DO.UserDO" >
                  <constructor>
                      <idArg column="id" jdbcType="INTEGER" javaType="java.lang.Integer" />
                      <arg column="username" jdbcType="VARCHAR" javaType="java.lang.String" />
                      <arg column="gender" jdbcType="INTEGER" javaType="top.xiaohuashifu.learn.jvm.constant.Gender" />
                  </constructor>
              </resultMap>
          
              <select id="getUser" parameterType="integer" resultType="user" >
                  SELECT
                  id, username, gender
                  FROM t_user
                  WHERE id = #{id}
              </select>
          
          </mapper>
          ```

        - Dao层接口：需要标识@Mapper或@Repository注解

          - 示例代码：

            ```java
            @Mapper
            public interface UserMapper {
                UserDO getUser(Integer id);
            }
            ```

        - 配置映射文件

          ```properties
          #MyBatis映射文件通配
          mybatis.mapper-locations=classpath:mapper/*Mapper.xml
          ##MyBatis扫描别名包，和注解@Alias联用
          mybatis.type-aliases-package=top.xiaohuashifu.learn.jvm.pojo.DO
          #配置typeHandler的扫描包
          mybatis.type-handlers-package=top.xiaohuashifu.learn.jvm.typehandler
          
          #日志配置，方便debug
          logging.level.root=DEBUG
          logging.level.org.springframework=DEBUG
          logging.level.org.mybatis=DEBUG
          ```

   3. Spring Boot整合MyBatis

      - 可以通过@MapperScan将MyBatis所需的对应接口扫描装配到Spring IoC容器中。

      - 示例：注解配置：@MapperScan(value = "top.xiaohuashifu.learn.jvm.dao.*")

      - 实例：Java配置：

        - 代码：

          ```java
              @Bean
              public MapperScannerConfigurer mapperScannerConfigurer() {
                  // 定义扫描器实例
                  MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
                  // 加载SqlSessionFactory，Spring Boot会自动生成，SqlSessionFactory实例
                  mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
                  // 定义扫描的包
                  mapperScannerConfigurer.setBasePackage("top.xiaohuashifu.learn.jvm.dao.*");
                  // 限定被标注@Repository的接口才被扫描
                  mapperScannerConfigurer.setAnnotationClass(Mapper.class);
                  // 通过继承某个接口限制扫描
                  // mapperScannerConfigurer.setMarkerInterface(...);
                  return mapperScannerConfigurer;
              }
          ```

   4. MyBatis的其他配置

      - 常见的有如下：

        - mybatis.mapper-locations=定义Mapper的XML路径
        - mybatis.type-aliases-package=定义别名扫描的包，需要与@Alias联合使用
        - mybatis.config-location=MyBatis配置文件
        - mybatis.configuration.interceptors=配置MyBatis插件（拦截器）
        - mybatis.type-handlers-package=具体需要与MappedJdbcTypes联合使用
        - mybatis.configuration.aggressive-lazy-loading=级联延迟加载属性配置
        - mybatis.executor-type=执行器，可以配置SIMPLE、REUSE、BATCH，默认为SIMPLE

      - 定义MyBatis插件：要实现Interceptor接口

        - 示例代码：

          ```java
          @Intercepts({
                  @Signature(type = StatementHandler.class, method = "prepare",
                          args = {Connection.class, Integer.class})
          })
          public class MyPlugin implements Interceptor {
          
              private Properties properties;
          
              // 拦截方法逻辑
              @Override
              public Object intercept(Invocation invocation) throws Throwable {
                  System.out.println("插件拦截的方法");
                  return invocation.proceed();
              }
          
              // 生成MyBatis拦截器代理对象
              @Override
              public Object plugin(Object target) {
                  return Plugin.wrap(target, this);
              }
          
              // 设置插件属性
              @Override
              public void setProperties(Properties properties) {
                  this.properties = properties;
              }
          }
          ```

        - 配置mybatis-config.xml文件

          ```xml
              <plugins>
                  <plugin interceptor="top.xiaohuashifu.learn.jvm.plugin.MyPlugin">
                      <property name="key1" value="value1"/>
                      <property name="key2" value="value2"/>
                      <property name="key3" value="value3"/>
                  </plugin>
              </plugins>
          ```


# 6、JDBC的数据库事务

- 在Spring中，数据库事务是通过AOP技术来提供服务的。

1. JDBC的数据库事务

   - 会有很多重复代码。

   - 执行SQL事务流程

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/%E6%89%A7%E8%A1%8CSQL%E4%BA%8B%E5%8A%A1%E6%B5%81%E7%A8%8B.png?raw=true)

2. Spring声明式事务的使用

   1. Spring声明式数据库事务约定

      - 声明式事务通过使用@Transactional进行标注的。这个注解可以标注在类或方法上，当它标注在类上时，代表这个类所以公共public非静态的方法都将启用事务功能。

      - 可以在@Transactional中配置如隔离级别和传播行为等。

      - 也可以配置在发生什么异常下回滚，什么异常下不回滚等。

      - 这些配置内容，是在Spring IoC容器在加载时就将这些配置信息解析出来，然后把这些信息存到事务定义器（TransactionDefinition接口的实现类）里，并且记录哪些类或者方法需要启动事务功能，采取什么策略去执行事务。

      - Spring数据库事务约定：

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%E6%95%B0%E6%8D%AE%E5%BA%93%E4%BA%8B%E5%8A%A1%E7%BA%A6%E5%AE%9A.jpg?raw=true)

      - 当Spring的上下温开始调用被@Transactional标注的类或方法时，Spring就会产生AOP的功能。当它启动事务时，会根据事务定义器内的配置去设置事务，首先是根据传播行为去确定事务策略。然后是隔离级别、超时时间、只读等内容的设置。

      - 在Spring数据库事务的流程中，它会根据是否发生异常来采取不同的策略。

      - 如果不发生异常，那么将自动提交事务。如果发生异常，会判断一次事务定义器内的配置，如果事务定义器已经约定了该类型的异常不会滚事务就提交事务，如果没有任何配置或者不是配置不回滚事务的异常，则会回滚异常，并将异常抛出，这步也是由事务管理拦截器完成的。

      - 无论发生异常与否，Spring都会释放事务资源，这样就可以保证数据库连接池正常可用，这也是由Spring事务拦截器完成的。

   2. @Transactional的配置项

      - 源码：

        ```java
        @Target({ElementType.TYPE, ElementType.METHOD})
        @Retention(RetentionPolicy.RUNTIME)
        @Inherited
        @Documented
        public @interface Transactional {
        
            // 通过BeanName指定事务管理器
        	@AliasFor("transactionManager")
        	String value() default "";
        
            // 同value属性
        	@AliasFor("value")
        	String transactionManager() default "";
        
            // 指定传播行为
        	Propagation propagation() default Propagation.REQUIRED;
        
            // 指定隔离级别
        	Isolation isolation() default Isolation.DEFAULT;
        
            // 指定超时时间，事务允许存在的时间（单位秒）
        	int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;
        
            // 是否只读事务
        	boolean readOnly() default false;
        
            // 方法在发生指定异常时回滚，默认是所有异常都回滚
        	Class<? extends Throwable>[] rollbackFor() default {};
        
            // 方法在发生指定异常名称时回滚，默认是所有异常都回滚
        	String[] rollbackForClassName() default {};
        
            // 方法在发生指定异常时不回滚，默认是所有异常都回滚
        	Class<? extends Throwable>[] noRollbackFor() default {};
        
            // 方法在发生指定异常名称时不回滚，默认是所有异常都回滚
        	String[] noRollbackForClassName() default {};
        
        }
        ```

      - 事务可用放在接口或者类上，但推荐放在类上，因为基于接口上将使得你的类基于接口代理时才生效。

        - 如果使用接口，那么将不会切换为CGLIB动态代理，只能使用JDK动态代理，并且使用对应的接口去代理你的类，这样才能驱动注解，这将大大限制使用。因此推荐把@Transactional放在实现类上。

   3. Spring事务管理器

      - 事务管理器的顶层接口为PlatformTransactionManager

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%E4%BA%8B%E5%8A%A1%E7%AE%A1%E7%90%86%E5%99%A8.jpg?raw=true)

      - MyBatis的事务管理器是DataSourceTransactionManager，它实现了PlatformTransactionManager接口。

      - PlatformTransactionManager源码：

        ```java
        public interface PlatformTransactionManager {
        
            // 获取事务，回设置属性
        	TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException;
        
            // 提交事务
        	void commit(TransactionStatus status) throws TransactionException;
        
            // 回滚事务
        	void rollback(TransactionStatus status) throws TransactionException;
        
        }
        ```

        - getTransaction方法的参数是一个事务定义器（TransactionDefinition），它依赖于我们配置的@Transactional的配置生成的。通过它就可用设置事务的属性。

      - 在Spring Boot中，依赖于mybatis-spring-boot-starter之后，会自动创建一个DataSourceTransactionManager对象，作为事务管理器。

3. 隔离级别

   1. 数据库事务的知识

      - 数据库事务具有4哥基本特征，ACID
        - Atomic（原子性）：事务中包含的操作被看作一个整体的业务单元，这个业务单元中的操作要么全部成功，要么全部失败，不会出现部分失败、部门成功的场景。
        - Consistency（一致性）：事务在完成时，必须使所有的数据都保持一致性状态，在数据库中所有的修改都基于事务，保证了数据的完整性。
        - Isolation（隔离性）：在多个线程同时访问同一数据，可能会在不同的事务中被访问，会产生丢失更新。为压制丢失更新的产生，数据库定义了隔离级别，通过它的选择，可用在不同程度上压制丢失更新的发生。
        - Durability（持久性）：在硬盘中。
      - 第一类丢失更新：一个事务回滚另外一个事务提交而引发的数据不一致的情况，称为第一类丢失更新。此类丢失更新现在数据库基本不会出现。
      - 第二类丢失更新：多个事务都提交引发的丢失更新称为第二类丢失更新。

   2. 隔离级别

      1. 未提交读（read uncommitted）

         - 是最低的隔离级别，允许一个事务读取另外一个事务没提交的数据。优点是并发能力高，适合对数据一致性没要求而追求高并发的场景，它最大的坏处是出现脏读。

      2. 读写提交（read committed）

         - 指一个事务只能读取另外一个事务已经提交的数据，不能读取未提交的数据。
         - 会出现不可重复读场景，也就是刚开始读取的时候是1，进行操作的时候可能会发现已经变为0。

      3. 可重复读

         - 克服出现不可重复读的现像。会等待第一个事务提交之后再读取数据库。
         - 会出现幻读。幻读是针对多条记录而言的，例如统计数据。单条数据不会产生幻读。

      4. 串行化（Serializable）

         - 所有SQL按照顺序执行。
         - 能够完全保证数据一致性。

      5. 使用合理的隔离级别

         - 隔离级别以读写提交为主，能够防止脏读，不能避免可重复读和幻读。

         - 使用隔离级别示例：

           ```java
               @Override
               @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 1, propagation = Propagation.REQUIRES_NEW)
               public UserVO saveUser(UserVO userVO) {
                   UserDO userDO = new UserDO();
                   BeanUtils.copyProperties(userVO, userDO);
                   userMapper.saveUser(userDO);
                   return userVO;
               }
           ```

         - 可用再application.properties文件中配置默认隔离级别

           ```properties
           #-1 数据库默认隔离级别
           #1 未提交读
           #2 读写提交
           #4 可重复读
           #8 串行化
           
           #tomcat数据源默认隔离级别
           #spring.datasource.tomcat.default-transaction-isolation=2
           #dbcp2数据库连接池默认隔离级别
           spring.datasource.dbcp2.default-transaction-isolation=2
           ```

4. 传播行为

   - 如在一个批量任务中，如果某个子任务发生异常，只是回滚那些异常的任务，而不用回滚那些顺利完成的任务。
   - 在Spring中，当一个方法调用另外一个方法时，可用让事务采用不同的策略工作，如新建事务或者挂起当前事务等，这就是事务的传播行为。

   1. 传播行为的定义

      ```java
      public enum Propagation {
      	
          // 需要事务，默认传播行为，如果当前存在事务，则沿用当前事务
          // 否则创建一个事务运行子方法
      	REQUIRED(TransactionDefinition.PROPAGATION_REQUIRED),
      
          // 支持事务，如果当前存在事务，就沿用当前事务
          // 如果不存在，则继续采用无事务的方式运行子方法
      	SUPPORTS(TransactionDefinition.PROPAGATION_SUPPORTS),
      
          // 必须使用事务，如果当前没有事务，则抛出异常
          // 如果当前存在事务，就沿用当前事务
      	MANDATORY(TransactionDefinition.PROPAGATION_MANDATORY),
      
          // 无论当前事务是否存在，都会创建新事务运行方法
          // 这样新事务就可以用于新的锁和隔离级别等特性，与当前事务相互独立
      	REQUIRES_NEW(TransactionDefinition.PROPAGATION_REQUIRES_NEW),
      
          // 不支持事务，当前存在事务时，将挂起事务，运行方法
      	NOT_SUPPORTED(TransactionDefinition.PROPAGATION_NOT_SUPPORTED),
      
          // 不支持事务，如果当前方法存在事务，则抛出异常，否则继续使用无事务机制运行
      	NEVER(TransactionDefinition.PROPAGATION_NEVER),
      
          // 在当前方法调用子方法时，如果子方法发生异常
          // 只回滚子方法执行过的SQL，不回滚当前方法的事务
      	NESTED(TransactionDefinition.PROPAGATION_NESTED);
      
      	private final int value;
      
      	Propagation(int value) {
      		this.value = value;
      	}
      
      	public int value() {
      		return this.value;
      	}
      
      }
      ```

   2. 测试传播行为

      - REQUIRES_NEW传播行为：完全脱离了原有事务的管控，每个事务都可以拥有自己独立的隔离级别和锁。
      - REQUIRED：沿用当前事务。
      - NESTED：子方法回滚而当前事务不回滚的方法。

5. @Transacional自调用失效问题

   - 一个类自身方法之间的调用，称为自调用。

   - Spring数据库事务的约定，其实现原理是AOP，而AOP的原理是动态代理，在自调用的过程中，是类自身的调用，而不是代理对象的调用，那么就不会产生AOP，这样Spring据不能把代码植入到约定的流程中，于是就产生失败的场景。

   - 解决方法

     - 使用一个Service去调用另外一个Service。不会造成侵入，但比较麻烦。

     - 从Spring IoC容器中获取代理对象去启用AOP。要实现ApplicationContextAware接口，设置IoC容器到类中。不过这样会造成Spring API的侵入。

       - 示例代码：

         ```java
         @Service("userService")
         public class UserServiceImpl implements UserService, ApplicationContextAware {
         
             /**
              * UserMapper
              */
             @Autowired
             private UserMapper userMapper;
         
             private ApplicationContext applicationContext;
         
             @Override
             @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
             public int saveUsers(List<UserVO> userVOList) {
                 int count = 0;
                 for (UserVO userVO : userVOList) {
                     UserService userService = applicationContext.getBean(UserService.class);
                     userService.saveUser(userVO);
                     count ++;
                 }
                 return count;
             }
         
             @Override
             @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 1, propagation = Propagation.REQUIRES_NEW)
             public UserVO saveUser(UserVO userVO) {
                 UserDO userDO = new UserDO();
                 BeanUtils.copyProperties(userVO, userDO);
                 userMapper.saveUser(userDO);
                 return userVO;
             }
         
             @Override
             public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
                 this.applicationContext = applicationContext;
             }
         }
         
         ```


# 7、Redis

- 引入Redis依赖

  ```xml
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-data-redis</artifactId>
          </dependency>
          <dependency>
              <groupId>redis.clients</groupId>
              <artifactId>jedis</artifactId>
          </dependency>
  ```

1. Spring-data-redis简介

   1. spring-data-redis项目的设计

      - Spring提供了一个RedisConnectionFactory接口，通过它可以生成一个RedisConnection接口对象，而RedisConnection接口对象是对Redis底层接口的封装。如Jedis驱动，Spring就会提供RedisConnection接口的实现类JedisConnection去封装原有的Jedis。
      - ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%E5%AF%B9Redis%E7%9A%84%E7%B1%BB%E8%AE%BE%E8%AE%A1.jpg?raw=true)
      - 使用RedisConnectionFactory生成RedisConnection来进行缓存操作还需要手动关闭Redis连接，因此Spring提供了RedisTemplate。

   2. RedisTemplate

      - 它会从RedisConnectionFactory工厂中获取连接，然后执行Redis命令，最后关闭Redis连接。

      - 示例代码：

        ```java
            @GetMapping("/send/message")
            public String test(String message) {
                activeMqService.sendMessage(message);
                return "success";
            }
        
            @GetMapping("/send/user")
            public String sendUser(UserVO userVO) {
                activeMqService.sendUser(userVO);
                return "success";
            }
        ```

      - 类只要实现了java.io.Serializable接口，Redis就会序列化对象，以字符串进行存储。Spring提供了RedisSerializer接口，有serialize方法和deserialize方法进行序列化和反序列化。其中JdkSerializationRedisSerializer是RedisTemplate默认的序列化器。

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%E5%85%B3%E4%BA%8ERedis%E7%9A%84%E5%BA%8F%E5%88%97%E5%8C%96%E5%99%A8%E8%AE%BE%E7%BD%AE.jpg?raw=true)

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/spring-data-redis%E5%BA%8F%E5%88%97%E5%8C%96%E5%99%A8%E5%8E%9F%E7%90%86%E7%A4%BA%E6%84%8F%E5%9B%BE.jpg?raw=true)

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/RedisTemplate%E4%B8%AD%E7%9A%84%E5%BA%8F%E5%88%97%E5%8C%96%E5%99%A8%E5%B1%9E%E6%80%A7.jpg?raw=true)

        - 可以使用字符串序列化器

          ```java
              @Bean(name = "redisTemplate")
              public RedisTemplate<Object, Object> redisTemplate() {
                  RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
                  RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
                  // 设置字符串序列化器，这样Spring就会把Redis的key当成字符串处理
                  redisTemplate.setKeySerializer(stringSerializer);
                  redisTemplate.setHashKeySerializer(stringSerializer);
                  redisTemplate.setHashValueSerializer(stringSerializer);
                  redisTemplate.setConnectionFactory(redisConnectionFactory);
                  return redisTemplate;
              }
          ```

   3. Spring对Redis数据类型操作的封装

      - spring-data-redis数据类型封装操作接口

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/spring-data-redis%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E5%B0%81%E8%A3%85%E6%93%8D%E4%BD%9C%E6%8E%A5%E5%8F%A3.jpg?raw=true)

      - 获取Redis数据类型操作接口

        ```java
        //获取地理位置操作接口 
        redisTemplate.opsForGeo(); 
        //获取散列操作接口 
        redisTemplate.opsForHash(); 
        //获取基数操作接口 
        redisTemplate.opsForHyperLogLog(); 
        //获取列表操作接口 
        redisTemplate.opsForList(); 
        //获取集合操作接口 
        redisTemplate.opsForSet(); 
        //获取字符串操作接口
        redisTemplate.opsForValue();
        //获取有序集合操作接口 
        redisTemplate.opsForZSet();
        ```

      - 对一个键对值做连续操作，如对一个散列进行多次操作，Spring提供了BoundXXXXOperations接口

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/BoundXXXXOperations%E6%8E%A5%E5%8F%A3.jpg?raw=true)

        - 获取绑定键的操作类

          ```java
          // 获取地理位置绑定键操作接口 
          redisTemplate.boundGeoOps("geo"); 
          // 获取散列绑定键操作接口 
          redisTemplate.boundHashOps("hash"); 
          // 获取列表（链表）绑定键操作接口 
          redisTemplate.boundListops("list"); 
          // 获取集合绑定键操作接口 
          redisTemplate.boundSetOps("set"); 
          // 获取字符串绑定键操作接口 
          redisTemplate.boundValueOps("string");
          // 获取有序集合绑定键操作接口 
          redisTemplate.boundZSetOps("zset"); 
          ```

        - 获取其中的操作接口后，我们就可以对某个键的数据进行多次操作，这样我们就知道如何有效地通过 Spring 操作 Redis 的各种数据类型了 。

   4. SessionCallback和RedisCallback接口

      - 作用是让RedisTemplate进行回调，可以同一条连接下执行多个Redis命令，避免RedisTemplate多次获取不同连接。其中SessionCallback封装比较好。RedisCallback比较底层。

      - 示例：

        ```java
            @PostMapping("/session")
            public Object setSession() {
                redisTemplate.execute(new SessionCallback<>() {
                    @Override
                    public Object execute(RedisOperations redisOperations) throws DataAccessException {
                        redisOperations.opsForValue().set("hhh", "zzzzz");
                        redisOperations.opsForValue().set("zzz", "hhhh");
                        return null;
                    }
                });
                return "success";
            }
        
            @GetMapping("/session")
            public Object getSession() {
                return redisTemplate.execute((RedisConnection rc) -> {
                    HashMap<Object, Object> map = new HashMap<>();
                    map.put("hhh", Arrays.toString(rc.get("hhh".getBytes())));
                    map.put("zzz", String.valueOf(rc.get("zzz".getBytes())));
                    return map;
                });
            }
        ```

2. 在Spring Boot中配置和使用Redis

   1. Spring Boot中配置Redis

      - 配置文件

        ```properties
        spring.redis.jedis.pool.min-idle=5
        spring.redis.jedis.pool.max-active=10
        spring.redis.jedis.pool.max-idle=10
        spring.redis.jedis.pool.max-wait=2000
        spring.redis.port=6379
        spring.redis.host=47.106.107.142
        spring.redis.password=123456
        # Redis连接超时时间，单位毫秒
        spring.redis.timeout=1000
        ```

      - Spring boot会自动读取这些配置来生成有关Redis的操作对象，会生成RedisConnectionFactory、RedisTemplate、StringRedisTemplate常用的Redis对象。

   2. 操作Redis数据类型

      - Spring提供了TypedTuple接口，提供了value和score，以适应多了个score的变化。

3. Redis的一些特殊用法

   1. 使用Redis事务

      - Redis使用事务，通常用的命令组合是watch、multi、exec，也就是要多条命令，可以使用SessionCallback接口。

        - watch命令是监控Redis的一些键；

        - multi命令是开始事务，开始事务后，该客户端的命令不会马上被执行，而实存放在一个队列里，因此命令此时都返回null。

        - exec命令是执行事务，它判断被watch监控的Redis的键的数据是否发生过变化（即使赋予相同的值也会被认为发生变化），如果变化，则取消事务，否则执行事务，在执行事务时，要么全部执行，要么全部不执行，而且不会被其他客户端打断。

        - Redis事务执行过程

          ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Redis%E4%BA%8B%E5%8A%A1%E6%89%A7%E8%A1%8C%E8%BF%87%E7%A8%8B.jpg?raw=true)

        - Redis事务是先让命令进入队列，一开始并不检测命令，只有在exec命令执行的时候，才能发现错误，对于错误的命令Redis只是保持，而错误命令后面的命令依旧被执行。所以在执行Redis事务前，要严格的检查数据，避免这种情况发生。

      - 示例：

        ```java
        redisTemplate.execute(new SessionCallback<>() {
                    @Override
                    public  Object execute(RedisOperations redisOperations) throws DataAccessException {
                        redisOperations.watch("zzz");
                        redisOperations.multi();
                        redisOperations.opsForValue().get("hhh");
                        return redisOperations.exec();
                    }
                });
        ```

   2. 使用Redis流水线

      - 使用流水线可以在执行很多命令时提高性能，通过减少网络传输次数。

      - 使用流水线需要注意的地方：

        - 运行太多命令可能执行结果返回的list数组会过程，会导致内存消耗过大，导致JVM内存溢出。
        - 与事务一样，流水线的命令也是进入队列而没有执行，因此命令的返回值也为null。

      - 示例代码：

        ```java
        redisTemplate.executePipelined(new SessionCallback<>() {
                    @Override
                    public  Object execute(RedisOperations redisOperations) throws DataAccessException {
                        redisOperations.opsForValue().set(key, value);
                        redisOperations.opsForValue().set(key, value + 1);
                        return null;
                    }
                });
        ```

   3. 使用Redis发布订阅

      - 首先Redis提供一个渠道，让消息能够发送到这个渠道上，而多个系统可以监听这个渠道，当一条消息发送到渠道，渠道就会通知它的监听者。

      - 示例代码：

        ```java
            /**
             * Redis消息监听器
             * @return MessageListener
             */
            @Bean("redisMessageListener")
            public MessageListener redisMessageListener() {
                return ((message, pattern) -> {
                    // 消息体
                    String body = new String(message.getBody());
                    // 渠道名称
                    String topic = new String(pattern);
                    System.out.println(body);
                    System.out.println(topic);
                });
            }
        
            /**
             * 任务池
             *
             * @return
             */
            @Bean
            public ThreadPoolTaskScheduler taskScheduler() {
                ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
                taskScheduler.setPoolSize(20);
                return taskScheduler;
            }
        
            /**
             *  Redis监听容器
             * @return
             */
            @Bean
            public RedisMessageListenerContainer redisMessageListenerContainer() {
                RedisMessageListenerContainer container = new RedisMessageListenerContainer();
                // Redis连接工厂
                container.setConnectionFactory(redisConnectionFactory);
                // 运行任务池
                container.setTaskExecutor(taskScheduler());
                // 监听频道
                Topic topic = new ChannelTopic("xhsfhome");
                // 使用监听器监听Redis消息
                container.addMessageListener(redisMessageListener(), topic);
                return container;
            }
        ```

      - 发送消息

        - 可以使用redisTemplate.convertAndSend(channel, message)发送消息。

   4. 使用Lua脚本

      - Lua脚本在Redis具备原子性，在高并发环境中可以使用Lua来保证数据一致性。Lua脚本具备强大的运算功能。

      - Redis使用Lua有两种方法，一种是直接发送Lua到Redis服务器去执行，另外一种是先把Lua发送给Redis，Redis会对Lua脚本进行缓存，然后返回一个SHA1的32位编码回来，之后只需要发送SHA1和相关参数给Redis便可以执行了。

      - 为执行Redis的Lua脚本，Spring提供了RedisScript接口，同时提供了DefaultRedisScript默认实现类。

        - RedisScript接口

          ```java
          public interface RedisScript<T> {
              // 获取脚本的Sha1
              String getSha1();
          
              @Nullable
              // 获取脚本返回值
              Class<T> getResultType();
          
              //获取脚本的字符串
              String getScriptAsString();
          
              default boolean returnsRawValue() {
                  return this.getResultType() == null;
              }
          
              static <T> RedisScript<T> of(String script) {
                  return new DefaultRedisScript(script);
              }
          
              static <T> RedisScript of(String script, Class<T> resultType) {
                  Assert.notNull(script, "Script must not be null!");
                  Assert.notNull(resultType, "ResultType must not be null!");
                  return new DefaultRedisScript(script, resultType);
              }
          }
          ```

        - 示例代码1：

          ```java
                 DefaultRedisScript<String> rs = new DefaultRedisScript<>();
                  // 设置脚本
                  rs.setScriptText("return 'Hello Redis'");
                  // 设置返回类型。不设置则没有返回结果
                  rs.setResultType(String.class);
          
                  RedisSerializer<String> stringRedisSerializer = redisTemplate.getStringSerializer();
          
                  // 执行Lua脚本
                  String result = redisTemplate.execute(rs, stringRedisSerializer, stringRedisSerializer, null);
          
                  return result;
          ```

        - 实例代码2：

          ```java
              @GetMapping("/lua1")
              public Long lua1(String key1, String key2, String value1, String value2) {
                  String lua = "redis.call('set', KEYS[1], ARGV[1]) \n"
                          + "redis.call('set', KEYS[2], ARGV[2]) \n"
                          + "local str1 = redis.call('get', KEYS[1]) \n"
                          + "local str2 = redis.call('get', KEYS[2]) \n"
                          + "if str1 == str2 then \n"
                          + "return 1 \n"
                          + "end \n"
                          + "return 0 \n";
          
                  DefaultRedisScript<Long> rs = new DefaultRedisScript<Long>();
                  // 设置脚本
                  rs.setScriptText(lua);
                  // 设置返回类型。不设置则没有返回结果
                  rs.setResultType(Long.class);
          
                  RedisSerializer<String> stringRedisSerializer = redisTemplate.getStringSerializer();
          
                  List<String> keyList = new ArrayList<>();
                  keyList.add(key1);
                  keyList.add(key2);
          
                  // 执行Lua脚本
                  Long result = (Long) redisTemplate.execute(rs, stringRedisSerializer, stringRedisSerializer, keyList, value1, value2);
          
                  return result;
              }
          ```

4. 使用Spring缓存注解操作Redis

   1. 缓存管理器和缓存的启用

      - 需要配置缓存管理器，提供缓存类型，超时时间等。缓存管理器的接口是CacheManager，Spring提供了多种实现。

      - 缓存管理器配置

        ```properties
        # 缓存类型
        spring.cache.type=REDIS
        # 缓存名称
        spring.cache.cache-names=redisCache
        # 是否禁用缓存前缀
        spring.cache.redis.use-key-prefix=false
        # 超时时间
        spring.cache.redis.time-to-live=10000
        # 等等
        ```

      - 需要在配置文件中加入@EnableCaching注解启动缓存机制

   2. 开发缓存注解

      - 使用缓存注解

        - 示例:

          ```java
          // 将返回结果存到缓存中    
          @Override
              @CachePut(value = "redisCache", key = "'redis_user_'+#result.id")
              public UserVO saveUser(UserVO userVO) {
                  UserDO userDO = new UserDO();
                  BeanUtils.copyProperties(userVO, userDO);
                  userMapper.saveUser(userDO);
                  userVO.setId(userDO.getId());
                  return userVO;
              }
          
          // 通过定义的键溢出缓存,它有一个Booolean类型的配置项beforeInvocation,表示在方法之前或之后移除缓存,默认是false,也就是方法之后移除缓存。
              @Override
              @CacheEvict(value = "redisCache", key = "'redis_user_'+#id")
              public Map<String, Object> deleteUser(Integer id) {
                  int delete = userMapper.delete(id);
                  HashMap<String, Object> map = new HashMap<>();
                  map.put("msg", delete >= 1 ? "删除成功" : "删除失败");
                  return map;
              }
          
          // 从缓存中通过定义的键查询,如果查询到数据,则返回,否则执行方法,返回数据,并将返回结果存到缓存中
              @Override
              @Cacheable(value = "redisCache", key = "'redis_user_'+#id")
              public UserVO getUser(Integer id) {
                  UserDO userDO = userMapper.getUser(id);
                  UserVO userVO = new UserVO();
                  BeanUtils.copyProperties(userDO, userVO);
                  return userVO;
              }
          
              @Override
              @Cacheable(value = "redisCache", key = "'redis:userDO:'+#id")
              public UserDO getUserDO(String username) {
                  return userMapper.getUserByUsername(username);
              }
          ```

      - 其中value="redisCache"表示缓存的名字，键是一个Spring EL表达式，可以使用#id，#a[1]等形式引用。
      
      - 使用#result表示返回的对象，#result.id表示取出它的id。
      
      - @CachePut、@Cacheable、@CacheEvict的condition配置项也是一个Spring EL表达式，如果为true则进行缓存操作，否则不进行。
      
   3.  
   
   4.  缓存注解自调失效问题
   
      - 缓存机制是基于Spring AOP机制，Spring AOP是通过动态代理实现，因此自调调用的是类内部的调用，不是调用代理对象，便不会出现AOP。
      - 可以通过Spring IoC容器获取对象，或者使用两个服务类相互调用解决。
   
   5. 缓存脏数据
   
      - 如排行榜，可能缓存的数据不是真实的数据，可以通过设置过期时间来减少脏数据存在时间。
      - 对于数据库写操作，一般认为缓存不可信，所以会从数据库中先读取最新数据，然后更新数据，避免将缓存的脏数据写入数据库中。
   
   6. 自定义缓存管理器
   
      - 通过自定义RedisCacheManager Bean来实现。

# 8、MongoDB

# 9、Spring MVC

1. Spring MVC框架的设计

   ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%20MVC%E6%A1%86%E6%9E%B6%E8%AE%BE%E8%AE%A1%E5%9B%BE.png?raw=true)

   - 处理请求先到达控制器（Controller）、控制器进行分发，然后根据请求的内容去访问模型层（Model），模型层会被分为服务层（Service）和数据库访问层（DAO），模型层返回数据给控制层后，控制层九江数据渲染到视图中，展示给用户。

2. Spring MVC流程

   - 加入@ResponseBody时，是没有空格视图解析器和视图渲染的。

   - 在Spring Boot启动Spring MVC，它会初始化DispatcherServlet、HandlerAdapter的实现类RequestMappingHandlerAdapter等组件对象。配置文件在DispatcherServlet.properties。

   - Spring MVC全流程

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%20MVC%E5%85%A8%E6%B5%81%E7%A8%8B.jpg?raw=true)

   - DispatcherServlet.properties

     ```properties
     # Default implementation classes for DispatcherServlet ’ s strategy interfaces . # Used as fallback whe口 no match工ng beans are found 工n the DispatcherServlet context. # Not meant to be customized by application developers . 
     ＃国际化解析器 
     org.springframework.web. servlet.LocaleResolver=org. springframework. web . servlet .ilBn .A cceptHeaderLocaleResolver 
     ＃主题解析器
     org. springframework.web . servlet .ThemeResolver=org . springframework. web. servlet . theme.F 工xedThemeResolver
     # HandlerMapping 实例 org . springframework.web . servlet . HandlerMapping=org . spr工ngframework . web . servlet.handler . BeanNameUrlHandlerMapping,\ org . springframework.web.servlet.mvc .method. annotation.RequestMappingHandlerMapping 
     ＃ 处理器适配器
     org. springframework. web . servlet . HandlerAdapter=org. springframework.web. servlet . mvc. HttpRequestHandlerAdapter, \ org .springframework . web.servlet . mvc . S工mpleControllerHandlerAdapter , \ org . spri ngframework.web.servlet .mvc .method.annotation . RequestMappingHandlerAdapter 
     ＃处理器异常解析器 
     org.springframework.web.servlet.HandlerExceptionResolver=org. springframework . web . servlet .mvc.method. annotation.ExceptionHandlerExceptionResolver, \ 
     186 第 9 章初识 Spring MVC 
     org . springframework . web . servlet . mvc . annotat工on.ResponseStatusExceptionResolver , \ org.springframework. web.servlet .mvc . support.DefaultHandlerExceptionResolver 
     ＃策略视图名称转换器，当你没有返回视图逻辑名称的时候，通过它可以生成默认的视图名称 org . springframework.web. servlet.RequestToViewNameTranslator=org. springframework. web . servlet.view. DefaultRequestToViewNameTranslator 
     ＃视图解析器
     org.springframework.web.servlet.ViewResolver=org. springframework.web. servlet.view. Internal Resource飞liewResolver 
     # FlashMap 管理器。不常用，不再讨论 
     org.springframework. web . servlet . FlashMapManager=org. springframework web . servlet support.SessionFlashMapManager 
     ```

   - @RequestMapping代表请求路径和控制器的映射关系，它会在Web服务器启动Spring MVC时，就被扫描到HandlerMapping的机制中存储，之后用户发起的请求被DispatcherServlet拦截后，通过URI和其他条件，通过HandlerMapper机制就能找到对于的控制器或方法进行响应。只是通过HandlerMapping返回的是一个HandlerExecutionChain对象。

     - HandlerExecutionChain

       ```java
       public class HandlerExecutionChain {
           private static final Log logger = LogFactory.getLog(HandlerExecutionChain.class);
       	// 处理器
       	private final Object handler;
       	// 拦截器数组
       	@Nullable
       	private HandlerInterceptor[] interceptors;
       	// 拦截器列表
       	@Nullable
       	private List<HandlerInterceptor> interceptorList;
       	// 拦截器当前下标
       	private int interceptorIndex = -1;
       ```

     - handler是对控制器Controller的包装。控制器执行完成返回后，处理器可以通过配置信息对控制器的返回结果进行处理。处理器包含了控制器方法的逻辑和处理器的拦截器（interceptor）的逻辑，这样可以通过拦截处理器进一步增加处理器的功能。

     - 得到了处理器（handler），但是我们有HTTP请求，也有按BeanName的请求，也有WebSocket请求，所以需要一个适配器去运行HandlerExecutionChain对象包含的处理器，也就是HandlerAdapter接口的实现类。最常用的是HandlerRequestHandlerAdapter。

     - 处理器调用控制器时，回通过模型层得到数据，再放入数据模型中，最后将返回模型和视图（ModelAndView）对象。

   - JSON视图不需要视图解析器，因为它不是一个逻辑视图，只需要将数据模型转换成JSON而已。

3. 定制Spring MVC初始化

   - Spring MVC提供了WebMvcConfigurer接口进行配置。Spring Boot中通过配置类WebMvcAutoConfiguration定义，它有一个内部类WebMvcAutoConfigurationAdapter实现了WebMvcConfigurer，通过它Spring Boot就自动配置了Spring MVC的初始化。

   - WebMvcAutoConfigurationAdapter会读入Spring配置的Spring MVC的属性来初始化对于的组件。

   - 可配置项：

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%20MVC%E5%8F%AF%E9%85%8D%E7%BD%AE%E9%A1%B9.png?raw=true)

     - 这些配置会被Spring Boot的机制读入，然后使用WebMvcAutoConfigurationAdapter去初始化。

# 10、深入Spring MVC开发

1. 处理器映射

   - @RequestMapping源码

     ```java
     @Target({ElementType.TYPE, ElementType.METHOD})
     @Retention(RetentionPolicy.RUNTIME)
     @Documented
     @Mapping
     public @interface RequestMapping {
         // 配置请求映射名称
     	String name() default "";
         
     	// 通过路径映射
     	@AliasFor("path")
     	String[] value() default {};
         
     	// 通过路径映射回path配置项
     	@AliasFor("value")
     	String[] path() default {};
     
         // 请求类型 GET, POST, HEAD, OPTIONS, PUT, PATCH, DELETE, TRACE.
     	RequestMethod[] method() default {};
     
         // 当存在对应的HTTP参数时才响应请求
     	String[] params() default {};
     
         // 限定请求头存在对应的参数时才响应
     	String[] headers() default {};
     
     	// 限定HTTP请求体提交类型，如"application/json", "text/html"
     	String[] consumes() default {};
     
         // 限定返回的内容类型，仅当HTTP请求头中的Accept类型中包含该指定类型时才返回
     	String[] produces() default {};
     
     }
     ```

2. 获取控制器参数

   - 处理器是对控制器的包装，在处理器运行的过程中会调度控制器的方法，只是它在进入控制器方法之前会对HTTP的参数和上下文进行解析，将他们转换成为控制器所需的参数。

   1. 无注解：要求前后端参数名相同。

   2. @RequestParam：可以定义映射关系。

   3. 传递数组：Spring MVC支持以","分隔的数组参数。

   4. 传递JSON

      - 前端需要添加contentType: "application/json"，data: JSON.stringify(params)
      - 后端需要添加@RequestBody在参数上，意味着它将接收前端提交的JSON请求体，这样Spring MVC就会将JSON数组转换成Java对象。

   5. 通过URL传递参数：@PathVariable(paramName)指定路径参数的名称。

   6. 获取格式化参数

      - 对日期和数字类型的转换注解进行处理，分别是@DateTimeFormat和@NumberFormat。

      - 示例：

        ```java
            @GetMapping
            public Map<String, Object> format(
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)Date date, 
                    @NumberFormat(pattern = "#,###.##") Double number) {
                Map<String, Object> map = new HashMap<>();
                map.put("date", date);
                map.put("number", number);
                return map;
            }
        ```

      - 在Spring Boot中，日期参数的格式化也可以不使用@DateTimeFormat，只需要在配置中文件中配置spring.mvc.date-format=yyyy-MM-dd即可。

3. 自定义参数转换规则

   1. 处理器获取参数逻辑

      - 当一个请求来到时，处理器执行的过程中，会首先从HTTP请求和上下文环境来得到参数。如果是简单的参数它会以简单的转换器进行转换，这些转换器是Spring MVC自带的。如果是转换HTTP请求体（Body），它会调用HtppMessageConverter接口的方法对请求体的信息进行转换，首先会判断能否对请求体进行转换，如果可以就会将其转换为Java类型。

      - HttpMessageConverter接口

        ```java
        public interface HttpMessageConverter<T> {
        	// 是否可读，其中clazz为Java类型，MediaType为HTTP请求类型
        	boolean canRead(Class<?> clazz, @Nullable MediaType mediaType);
        
            // 判断clazz类型能否转换为mediaType媒体类型
        	boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType);
        
            // 可支持的媒体类型列表
        	List<MediaType> getSupportedMediaTypes();
        
            // 读入HTTP请求信息
        	T read(Class<? extends T> clazz, HttpInputMessage inputMessage)
        			throws IOException, HttpMessageNotReadableException;
        
            // 写入响应
        	void write(T t, @Nullable MediaType contentType, HttpOutputMessage outputMessage)
        			throws IOException, HttpMessageNotWritableException;
        
        }
        ```

      - 当控制器方法参数标注了@RequestBody，所以处理器会采用请求体（Body）的内容进行参数转换，而前端的请求体为JSON类型，所以它首先会调用canRead方法来确定请求体是否可读。判断可读后，接着调用read方法，将前端提交的用户JSON类型的请求体转换为控制器的Java类型参数。

      - Spring MVC是通过WebDataBinder机制来获取参数，它主要是通过解析HTTP请求上下文，然后在调用控制器之前进行参数转换和验证。处理器会从HTTP请求中读取数据，然后通过Converter、Formatter和GenericConverter进行转换。Spring MVC这三个接口都采用了注册机机制，默认Spring MVC已经注册了很多转换器，也就是可以自动转换Integer、Long、String类型的原因。当要自定义转换规则时，只需要在注册机上注册iji的转换器就可以了。

      - Spring MVC处理器HTTP请求体转换流程图

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%20MVC%E5%A4%84%E7%90%86%E5%99%A8HTTP%E8%AF%B7%E6%B1%82%E4%BD%93%E8%BD%AC%E6%8D%A2%E6%B5%81%E7%A8%8B.jpg?raw=true)

      - Spring MVC提供了一个服务机制去管理，就是ConversionService接口。默认情况下，会使用这个接口的子类DefaultFormattingConversionService对象来管理这些转换类。

      - 在Spring Boot红提供了特殊的机制来管理这些转换器。Spring Boot的自动配置类WebMvcAutoConfiguration还定义了一个内部类WebMvcAutoConfigurationAdapter，会自动把用户创建的Converter、Formatter和GenericConverter的Bean进行注册。

        ```java
        		@Override
        		public void addFormatters(FormatterRegistry registry) {
                    // 遍历IoC容器
        			for (Converter<?, ?> converter : getBeansOfType(Converter.class)) {
        				registry.addConverter(converter);
        			}
        			for (GenericConverter converter : getBeansOfType(GenericConverter.class)) {
        				registry.addConverter(converter);
        			}
        			for (Formatter<?> formatter : getBeansOfType(Formatter.class)) {
        				registry.addFormatter(formatter);
        			}
        		}
        ```

      - ConversionService转换机制设计

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/ConversionService%E8%BD%AC%E6%8D%A2%E6%9C%BA%E5%88%B6%E8%AE%BE%E8%AE%A1.png?raw=true)

   2. 一对一转换器（Converter）

      - 从一种类型转换称为另外一种类型。也就是1对1映射。

      - 示例：

        ```java
        @Component
        public class GenderConverter implements Converter<String, Gender> {
            @Override
            public Gender convert(String id) {
                return Gender.getGenderById(Integer.parseInt(id));
            }
        }
        ```

   3. GenericConverter集合和数组转换

      - 如List\<User\>，Spring MVC会先使用stringToUserConverter，然后再使用stringToCollectionConverter进行转换。

      - 示例：

        ```java
            @GetMapping("/list")
            public List<Gender> list(
                    List<Gender> genderList) {
                return genderList;
            }
        ```

4. 数据验证

   1. JSR-303验证
   2. Validator验证

5. 数据模型

   - Spring MVC数据模型设计图

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%20MVC%E6%95%B0%E6%8D%AE%E6%A8%A1%E5%9E%8B%E8%AE%BE%E8%AE%A1%E5%9B%BE.jpg?raw=true)

     - ModelMap继承自LinkedHashMap类，所以它具备Map接口的一切特性，除此之外它还可以添加数据属性。如果控制器方法使用ModelAndView、Model或者ModelMap作为参数，Spring MVC会自动创建数据模型对象。

6. 视图和视图解析器

   - 视图是渲染数据模型展示给用户的组件，在Spring MVC中又分为逻辑视图和非逻辑视图。逻辑视图是需要视图解析器（ViewResolver）进一步定位的。对于非逻辑视图，并不需要进一步地定位视图的位置，只需要直接将数据模型渲染出来即可。

   1. 视图设计

      - 视图都会实现Spring MVC定义的视图接口View。

      - View接口源码：

        ```java
        public interface View {
        	// 响应状态属性
        	String RESPONSE_STATUS_ATTRIBUTE = View.class.getName() + ".responseStatus";
        	
            // 路径变量
        	String PATH_VARIABLES = View.class.getName() + ".pathVariables";
        
            // 选择内容类型
        	String SELECTED_CONTENT_TYPE = View.class.getName() + ".selectedContentType";
        
            // 响应类型
        	@Nullable
        	default String getContentType() {
        		return null;
        	}
        
        
            // 渲染方法
            // 将数据模型渲染到视图，是视图的核心方法。
            // model是数据模，实际上是从控制器返回的数据模型，这样render方法就可以把它渲染出来。
        	void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
        			throws Exception;
        
        }
        ```

      - Spring MVC常用视图关系模型

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%20MVC%E5%B8%B8%E7%94%A8%E8%A7%86%E5%9B%BE%E5%85%B3%E7%B3%BB%E6%A8%A1%E5%9E%8B.jpg?raw=true)

   2. 导出PDF文件

7. 文件上传

   1. Spring MVC对文件上传的支持

      - 首先DispatcherServlet会使用适配器模式，将HttpServletRequest接口对象转换为MultipartHttpServletRequest对象。MultipartHttpServletRequest接口扩展了HttpServletRequest接口的所以方法，而且定义了一些操作文件的方法，这样就可以通过这些方法实现对上传文件的操作。

      - 文件请求转换类之间的关系

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/%E6%96%87%E4%BB%B6%E8%AF%B7%E6%B1%82%E8%BD%AC%E6%8D%A2%E7%B1%BB%E4%B9%8B%E9%97%B4%E7%9A%84%E5%85%B3%E7%B3%BB.jpg?raw=true)

      - 在默认情况下Spring推荐使用StandardServletMultipartResolver因为它只要依赖于Servlet API提供的包。Spring Boot会自动创建MultipartResolver对象，也就是StandardServletMultipartResolver对象。

      - 文件上传配置

        ```properties
        # 是否启用Spring MVC多分部上传功能
        spring.servlet.multipart.enabled=true
        # 将文件写入磁盘的阈值
        spring.servlet.multipart.file-size-threshold=5242880
        # 指定默认上传的文件夹
        spring.servlet.multipart.location=D:/buf_file/spring boot
        # 限制单个文件的最大大小
        spring.servlet.multipart.max-file-size=10MB
        # 限制所有文件的最大大小
        spring.servlet.multipart.max-request-size=10MB
        # 是否延迟多部分文件请求的参数和文件的解析
        spring.servlet.multipart.resolve-lazily=false
        ```

      - 文件的上传可以使用Servlet提供的Part接口或Spring MVC提供的MultipartFile接口作为参数。推荐使用Servlet的Part接口。

   2. 开发文件上传功能

      - 前端的\<form\>表单的enctype属性要声明为multipart/form-data。

8. 拦截器

   - 拦截器是对处理器进行拦截，以增强处理器的功能。

   1. 拦截器的设计

      - 所有的拦截器都要实现HandlerInterceptor接口

        ```java
        public interface HandlerInterceptor {
        	// 处理器执行前方法
        	default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        			throws Exception {
        
        		return true;
        	}
        	// 处理器处理后方法
        	default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        			@Nullable ModelAndView modelAndView) throws Exception {
        	}
        
            // 处理器完成后方法
        	default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
        			@Nullable Exception ex) throws Exception {
        	}
        
        }
        ```

      - 拦截器执行过程

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/%E6%8B%A6%E6%88%AA%E5%99%A8%E6%89%A7%E8%A1%8C%E8%BF%87%E7%A8%8B.jpg?raw=true)

   2. 开发拦截器

      - 定义拦截器

        ```java
        public class TestInterceptor implements HandlerInterceptor {
        
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                System.out.println("处理器前方法");
                return true;
            }
        
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                System.out.println("处理器后方法");
            }
        
            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                System.out.println("处理器完成方法");
            }
        }
        ```

      - 注册拦截器，拦截器不会自动被发现，所以需要配置

        ```java
        @Configuration
        public class WebConfig implements WebMvcConfigurer {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                InterceptorRegistration interceptorRegistration = registry.addInterceptor(new TestInterceptor());
                interceptorRegistration.addPathPatterns("/mvc");
            }
        }
        ```

   3. 多个拦截器顺序

      - 处理器前方法采用先注册先执行，处理器后方法和完成方法采用先注册后执行。
   
9. 国际化

   - 国际化消息源

     - Spring MVC提供了国际化消息源的机制，接口是MessageSource接口体系。它的作用是装载国际消息。

     - 大部分情况下是使用JDK的ResourceBundle处理国际化消息，主要使用ResourceBundleMessageSource这个国际化消息源。

     - 配置项：

       ```properties
       # 文件编码
       spring.messages.encoding=UTF-8
       # 国际化文件基础名称
       spring.messages.basename=international
       # 国际化消息缓存有效时间（单位秒），超时将重新载入
       spring.messages.cache-duration=3600
       ```

     - 国际化消息设计

       ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/%E5%9B%BD%E9%99%85%E5%8C%96%E6%B6%88%E6%81%AF%E8%AE%BE%E8%AE%A1.jpg?raw=true)

     - messages.properties是默认的国际化任务，没有它将不再启用国际化消息机制。文件名默认是messages开头，可以通过配置文件的spring.messages.basename进行识别。

   1. 国际化解析器

      - Spring MVC提供了LocaleResolver接口要确定用户的国际化区域。

   2. 国际化解析器

      - CookieLocaleResolver：将国际化信息设置在浏览器的Cookie中，有丢失数据的风险，因为浏览器有可能把Cookie禁用了。

      - SessionLocaleResolver：将国际化信息存储在Session中。

      - 国际化解析器设计

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/%E5%9B%BD%E9%99%85%E5%8C%96%E8%A7%A3%E6%9E%90%E5%99%A8%E8%AE%BE%E8%AE%A1.jpg?raw=true)

   3. SessionLocaleResolver

      - 信息文件都要放在一起。

      - 在Spring MVC中，它提供了一个拦截器LocaleChangeInterceptor可以在处理器前处理相关逻辑，也就是拦截器的preHandler方法的作用。这个拦截器可以拦截一个请求参数，通过这个参数确定国际化信息，并把国际化信息保存到Session。

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%20MVC%E5%9B%BD%E9%99%85%E5%8C%96%E6%B5%81%E7%A8%8B%E5%9B%BE.jpg?raw=true)

      - 添加国际化解析器和拦截器

        ```java
        @Configuration
        public class WebConfig implements WebMvcConfigurer {
        
            private LocaleChangeInterceptor lci = null;
        
            // 国际化解析器
            // 要包装beanName为localeResolver，这是Spring MVC中的约定。
            @Bean(name = "localeResolver")
            public LocaleResolver initLocaleResolver() {
                SessionLocaleResolver slr = new SessionLocaleResolver();
                // 默认国际化区域
                slr.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
                return slr;
            }
        
            // 国际化拦截器
            @Bean
            public LocaleChangeInterceptor localeChangeInterceptor() {
                if (lci != null) {
                    return lci;
                }
        
                lci = new LocaleChangeInterceptor();
                // 表示拦截器将读取HTTP请求为language的参数，用以设置国际化参数，这样可以通过这个参数来设置用户的国际化区域。
                lci.setParamName("language");
                return lci;
            }
        
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(localeChangeInterceptor());
            }
        }
        ```

      - 后台获取国际化区域

        ```java
            @GetMapping("/test")
            public String test(HttpServletRequest request) {
                Locale locale = LocaleContextHolder.getLocale();
                return messageSource.getMessage("msg",null, locale);
            }
        ```

10. Spring MVC

    1. @ResponseBody转换为JSON的秘密

       - 当方法标注@ResponseBody后，处理器就会记录这个方法的响应类型为JSON数据集。当控制器返回后，处理器会启用结果解析器（ResultResolver）去解析这个结果，它回去轮询注册给Spring MVC的HttpMessageConverter接口的实现类。因为MappingJackson2HttpMessageConverter这个实现类已经被Spring MVC注册，所以就通过它在处理器内部把结果转换为JSON。

       - 如果被MappingJackson2HttpMessageConverter进行了转换，那么后续的模型和视图（ModelAndView）就会返回null，这样视图解析器和视图渲染将不再被执行

       - @ResponseBody注解转换为JSON流程图

         ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/@Response%20Body%E6%B3%A8%E8%A7%A3%E8%BD%AC%E6%8D%A2%E4%B8%BAJSON%E6%B5%81%E7%A8%8B%E5%9B%BE.jpg?raw=true)

    2. 重定向

    3. 操作会话对象

       - @SessionAttribute用于参数，它是将HttpSession中的属性读出，赋予控制器的参数。
       - @SessionAttributes用于类注解，它会将相关数据模型的属性保存到Session中。

    4. 给控制器添加通知

       - @ControllerAdvice：定义一个控制器的通知类。
       - @InitBinder：定义控制器参数绑定规则，如转换规则、格式化等，它会在参数转换之前执行。
       - @ExceptionHandanler：定义控制器发生异常后的操作。
       - @ModelAttribute：可以在控制器方法执行之前，对数据模型进行操作。

    5. 获取请求头参数

       - 可以使用@RequestHeader(headerName)进行获取

# 11、REST风格网站

1.  
   1.  
   2.  
   3.  REST风格的一些误区
      - 不要加入版本号，如：/v1/user/1
        - 版本号可以通过HTTP头的Accept: version = 1.0进行区分
      - 尽量使用路径参数而不是body，除非参数过多。
2.  
   1.  
   2.  
      - @RequestBody的说明
        - 平时使用的x-www-form-urlencoded是k/v格式，不适合大批量传数据。
        - 微服务的接口参数也是JSON格式，需要使用@RequestBody注解。
3. 客户端请求RestTemplate
   1. 使用RestTemplate请求后端
      - RestTemplate底层是通过类HttpURLConnection实现的。
      - getForObject和getForEntity的区别是前一个返回的只是数据，而后一个返回的还带响应体、响应头、状态码等。
      - PATCH请求不能使用，因为HttpURLConnection中并不能支持PATCH请求。
      - RestTemplate还提供了exchange方法，可以作为资源交换使用，可以自定义更多的参数。

# 12、Spring Security

- 加入依赖就可以启动Spring Security

  ```xml
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-security</artifactId>
          </dependency>
  ```

1. 概述和简单安全认证

   - 在Java Web工程中，一般使用Servlet过滤器（Filter）对请求进行拦截，然后再Filter中通过自己的验证逻辑来决定是否放行请求。Spring Security也是基于这个原理，再进入到DispatcherServlet前就可以对Spring MVC的请求进行拦截，然后通过一定的验证，决定是否方向请求访问系统。

   - 对请求的拦截，Spring Security提供了过滤器DelegatingFilterProxy类基于开发者配置。

   - 一旦启用了Spring Security，Spring IoC容器会创建一个名为springSecurityFilterChain的Bean。它的类型为FilterChainProxy，事实上它实现了Filter接口，只是它是一个特殊的拦截器。在Spring Security操作的过程中它会提供Servlet过滤器DelegatingFilterProxy，这个过滤器会通过Spring Web IoC容器去获取Spring Security所自动创建的FilterChainProxy对象，这个对象上存在一个拦截器列表，列表上存在用户验证的拦截器、跨站点请求伪造等拦截器。

   - 通过FilterChainProxy对象，可以注册Filter，也就是允许自定义Filter来实现对应的拦截逻辑，以满足不同需要。

   - Spring Boot配置

     ```properties
     # Spring Security过滤器排序
     spring.security.filter.order=-100
     # 安全过滤器责任链拦截的分发类型
     spring.security.filter.dispatcher-types=async,error,request
     
     # OAuth提供者详细配置信息
     spring.security.oauth2.client.provider.*=#
     # OAuth 客户端登记信息
     spring.security.oauth2.client.registration.*=
     ```

2. 使用WebSecurityConfigurerAdapter自定义

   - 为了给FilterChainProxy对象加入自定义初始化，Spring Security提供了SecurityConfigurer接口，通过它就能够实现对Spring Security的配置。为了更方便，Spring还对Web工程专门提供了WebSecurityConfigurer并提供一个抽象类WebSecurityConfigurerAdapter。

   - 源码：

     ```java
     @EnableWebSecurity
     public class SecurityConfig extends WebSecurityConfigurerAdapter {
     
         @Value("${system.user.password.secret}")
         private String secret;
     
         @Autowired
         private UserDetailsService userDetailsService;
     
         // 指定用户和角色对应的URL访问权限
         @Override
         protected void configure(HttpSecurity http) throws Exception {
             http
                     .authorizeRequests()
                     .antMatchers("/users").hasAnyRole("USER")
                     .antMatchers("/users/export/**").hasRole("ADMIN")
                     .antMatchers("/redis").hasAnyRole("USER")
                     .anyRequest().permitAll()
                     .and()
                     .anonymous()
                     .and()
                     .formLogin()
                     .and()
                     .httpBasic()
                     .and()
                     .csrf()
                     .disable();
         }
     
         // 定义用户、密码和角色
         @Override
         protected void configure(AuthenticationManagerBuilder auth) throws Exception {
             PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
             auth.userDetailsService(userDetailsService)
                     .passwordEncoder(passwordEncoder);
         }
     
         // 对Filter链内容的配置，可以配置Filter链忽略哪些内容
         // WebSecurityConfigurerAdapter提供空实现，也就是没有忽略任何内容
         @Override
         public void configure(WebSecurity web) throws Exception {
             super.configure(web);
         }
     }
     ```

3.  自定义用户服务信息

   1.  使用内存签名服务

   2. 使用数据库认证服务

      - 源码：

        ```java
        @EnableWebSecurity
        public class SecurityConfig extends WebSecurityConfigurerAdapter {
        
            @Value("${system.user.password.secret}")
            private String secret;
        
            //注入自定义用户认证服务
            @Autowired
            private UserDetailsService userDetailsService;
        
            // 指定用户和角色对应的URL访问权限
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http
                        .authorizeRequests()
                        .antMatchers("/users").hasAnyRole("USER")
                        .antMatchers("/users/export/**").hasRole("ADMIN")
                        .antMatchers("/redis").hasAnyRole("USER")
                        .anyRequest().permitAll()
                        .and()
                        .anonymous()
                        .and()
                        .formLogin()
                        .and()
                        .httpBasic()
                        .and()
                        .csrf()
                        .disable();
            }
        
            @Override
            protected void configure(AuthenticationManagerBuilder auth) throws Exception 
                // 密码编码器
                // 可以使用Pbkdf2PasswordEncoder类更加安全
                // 可以在配置文件中加入system.user.password.secret=xxx加入密钥
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // 注入自定义用户认证服务
                auth.userDetailsService(userDetailsService)
                        .passwordEncoder(passwordEncoder);
            }
        
        
            @Override
            public void configure(WebSecurity web) throws Exception {
                super.configure(web);
            }
        }
        ```

   3. 自定义用户认证服务

      - Spring Security提供了一个UserDetailsService接口，通过它可以获取用户信息，这个街廓只需要实现loadUserByUsername方法返回一个UserDetails接口对象。

      - 示例代码：

        ```java
        // 实现UserDetailsService接口
        @Service("userDetailsService")
        public class UserDetailsServiceImpl implements UserDetailsService {
        
            @Autowired
            private RoleService roleService;
        
            @Autowired
            private UserService userService;
        
            // 获取UserDetails
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                UserDO userDO = userService.getUserDO(username);
                List<RoleDO> roleDOList = roleService.listRolesByUsername(username);
        
                List<GrantedAuthority> authorityList = new ArrayList<>();
                for (RoleDO roleDO : roleDOList) {
                    GrantedAuthority authority = new SimpleGrantedAuthority(roleDO.getRoleName());
                    authorityList.add(authority);
                }
                User user = new User(userDO.getUsername(), userDO.getPassword(), authorityList);
                System.out.println(user);
                return user;
            }
        }
        ```

      - 然后在SecurityConfig的configure(AuthenticationManagerBuilder auth) 方法中注入UserDetailsServiceImpl类。

4. 限制请求

   - 通过configure(HttpSecurity http)方法，可以使用Ant风格或者正则表达式风格来限制请求。

   - 源码：

     ```java
         @Override
         protected void configure(HttpSecurity http) throws Exception {
             http
                     .authorizeRequests()
                     .antMatchers("/users").hasAnyRole("USER")
                     .antMatchers("/users/export/**").hasRole("ADMIN")
                     .antMatchers("/redis").hasAnyRole("USER")
                     .anyRequest().permitAll()
                     .and()
                     .anonymous()
                     .and()
                     .formLogin()
                     .and()
                     .httpBasic()
                     .and()
                     .csrf()
                     .disable();
         }
     ```

   - 权限方法

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/%E6%9D%83%E9%99%90%E6%96%B9%E6%B3%95.jpg?raw=true)

   2. 使用Spring表达式配置访问权限

      - 需要用到access()方法，参数是一个表达式，如果这个表达式返回true，则可以访问，否则不可以访问。

      - Spring Security中的Spring表达式方法

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/Spring%20Security%E4%B8%AD%E7%9A%84Spring%E8%A1%A8%E8%BE%BE%E5%BC%8F%E6%96%B9%E6%B3%95.jpg?raw=true)

   3. 强制使用HTTPS

      - 示例代码：

        ```java
                http
                //使用安全渠道，限定HTTPS请求
                        .requiresChannel()
                        .antMatchers("/admin/**")
                        .requiresSecure()
                        .and()
                    //不使用HTTPS请求
                        .requiresChannel()
                        .antMatchers("/user/**")
                        .requiresInsecure();
        ```

   4. 防止跨站点请求伪造

      - 首先是浏览器请求安全网站，于是可以进行登录，在登录后，浏览器会记录一些信息，以Cookie的形式进行保存，然后在不关闭浏览器的情况下，用户可能访问一个危险网站，危险网站通过获取Cookie信息来仿造用户的请求，进而请求安全网站。

      - CSRF攻击场景

        ![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BASpring%20Boot2.x/CSRF%E6%94%BB%E5%87%BB%E5%9C%BA%E6%99%AF.jpg?raw=true)

      - Spring Security提供了CSRF过滤器，默认情况下，会启动这个过滤器来防止CSRF攻击。可以使用代码http.csrf().disable()来关闭。

5. 用户认证功能

   - 自定义登录页面

     ```java
     // 启动remember me 功能
     http.rememberMe().tokenValiditySeconds(86400).key("remember-me-key");
     ```

# 13、

