# Dozer

时间:2019-02-10

本文章向大家介绍Dozer，主要包括Dozer使用实例、应用技巧、基本知识点总结和需要注意事项，具有一定的参考价值，需要的朋友可以参考一下。



# Dozer简介

Dozer 是 Java Bean 到 Java Bean 的映射器，他以递归的方式将数据从一个对象复制到另一个对象。Dozer 支持简单属性映射、双向映射、隐式映射以及递归映射。使用该映射器可以很方便的在项目中进行 pojo、do、vo 之间的转换。

# 快速入门

在 Maven 的配置文件中导入 Dozer 的依赖。

```
<dependency>
    <groupId>com.github.dozermapper</groupId>
    <artifactId>dozer-core</artifactId>
    <version>6.4.0</version>
</dependency>
```

创建两个 Java Bean 类：

```
public class User {
    private String name;
    private Integer age;
    private Date birthday;
    // 省略 setter 和 getter 方法
}
public class UserApiDestinationObject {
    private String name;
    private String age;
    public String birthday;

    // 重写 toString 方法，方便测试
    @Override
    public String toString() {
        return "UserApiDestinationObject{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }

    // 省略 getter、setter 方法
}
```

创建 Junit 的测试类，测试 Dozer 的使用，DozerTest 的代码如下：

```
public class DozerTest {

    @Test
    public void apiTest() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        User user = new User();
        user.setName("hxy");
        user.setAge(123);
        user.setBirthday(new Date());

        UserApiDestinationObject destinationObject = mapper.map(user, UserApiDestinationObject.class);
        // 或者
        // UserApiDestinationObject destinationObject = new UserApiDestinationObject();
        // mapper.map(user, destinationObject);

        System.out.println(destinationObject);
    }

}
```

执行 apiTest 方法，控制台输出：

```
UserApiDestinationObject{name='hxy', age='123', birthday='Sun Feb 10 14:28:41 GMT+08:00 2019'}
```

此时，Dozer 自动完成 User 类到 ApiUserDestination 类的映射。这是 Dozer 的默认映射方式——隐式映射，Dozer 自动的将两个实体类的相同属性名的属性进行映射。如果两个属性的属性名相同，但是类型不同，Dozer 会按照默认的转换规则进行类型的转换，而且不同修饰符的属性也能正常进行映射。

# 通过xml进行映射

有时候两个 bean 的属性名并不完全相同，这时候通过 Dozer 的隐式映射并不能满足我们的实际需求，这时候就可以通过 Dozer 的另一种映射方式——显示映射 进行映射。通过显示映射的方法需要我们自己创建一个 xml 的映射文件来指定两个类的映射关系。这些 xml 配置文件将在运行时由 Dozer 引擎使用。

下面演示 Dozer 的显示映射方式：

新建 UserXmlDestinationObject 类：

```
public class UserXmlDestinationObject {
    private String username;
    private String age;
    public String dateOfBirth;

    // 省略 setter 和 getter 方法
    // 省略 toString 方法
}
```

在 resource 目录下新建 userMapping.xml 文件，xml 中的配置信息如下：

```
<?xml version="1.0" encoding="UTF-8"?>
<mappings xmlns="http://dozermapper.github.io/schema/bean-mapping"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://dozermapper.github.io/schema/bean-mapping http://dozermapper.github.io/schema/bean-mapping.xsd">

    <!--全局配置，配置日期的映射格式-->
    <configuration>
        <date-format>yyyy-MM-dd HH:mm:ss</date-format>
    </configuration>

    <mapping wildcard="true">
        <class-a>com.huang.dozer.bean.User</class-a>
        <class-b>com.huang.dozer.bean.UserXmlDestinationObject</class-b>
        <field>
            <a>name</a>
            <b>username</b>
        </field>
        <field>
            <a>birthday</a>
            <b>dateOfBirth</b>
        </field>
    </mapping>

</mappings>
```

一个 mappings 元素包含多个 mapping 元素，每个 mapping 元素都有类映射声明和字段的映射关系。wildcard 属性默认值为true，这意味着 Dozer 将会尝试映射两个类的每个字段，当该属性设置为 false 时，Dozer 将仅映射显示定义的字段。也可以在filed 下的 <a> 或者 <b> 节点下添加配置信息，如：<a date-fomat="MM/dd/yyyy HH:mm">，此时字段的配置信息优先级别高于全局配置。

在 DozerTest 中新增 xmlTest 方法：

```
    @Test
    public void xmlTest(){
        // withMappingFiles 方法加载 xml 配置文件，多个配置文件可以用 "," 隔开，如 withMappingFiles("userMapping.xml" , "anotherMapping.xml")
        Mapper mapper = DozerBeanMapperBuilder.create().withMappingFiles("mapping/userMapping.xml").build();
        User user = new User();
        user.setName("hxy");
        user.setAge(123);
        user.setBirthday(new Date());
        UserApiDestinationObject destinationObject = mapper.map(user, UserApiDestinationObject.class);
        System.out.println(destinationObject);
    }
```

执行该方法，控制台输出：

```
UserXmlDestinationObject{username='hxy', age='123', dateOfBirth='2019-02-10 15:14:16'}
```

# 通过注解进行映射

从版本 5.3.2 开始，Dozer 也开始提供注解支持，使用注释的明显原因是避免在映射代码中复制字段和方法名称，注释可以放在映射类的属性上，从而减少代码量。但是有些情况应该减少使用注解，甚至无法使用注解，如：

- 你正在映射类时，这些类不在你的控制下，但在库中提供
- 映射类非常复杂，而且需要许多配置

用法：

新建 UserAnnotationsObject 类：

```
public class UserAnnotationsObject {
    @Mapping("name")
    public String username;
    private String age;
    @Mapping("birthday")
    private String dateOfBirth;

    // 省略 getter、setter 方法
    // 省略 toString 方法
}
```

注：dozer 是双向映射的，无论使用 xml 或者 注解的方式进行映射，都只需要配置一个类的映射关系就行。

在 DozerTest 中新增 annotationsTest 方法：

```
    @Test
    public void annotationsTest(){
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        User user = new User();
        user.setName("hxy");
        user.setAge(123);
        user.setBirthday(new Date());
        UserAnnotationsObject destinationObject = mapper.map(user, UserAnnotationsObject.class);
        System.out.println(destinationObject);
    }
```

通过注解的方式，User 类中的 "name" 属性将映射到 UserAnnotationsObject 类中的 username 属性，不用担心 private 修饰符，Dozer 将会自动处理。目前 Dozer 只提供 @Mapping 这一个注解，后续版本可能会添加新的注解，至于现在，你可以混合api方式、xml方式、注解方式进行类的映射。

> 注意：对于实际应用程序，建议不要在每次映射对象时创建一个新的 Mapper 实例，而是重新使用上次创建的 Mapper 实例，可以把 Mapper 封装成单例模式使用。

#  Spring Boot 集成 

自从 6.2.0 版本之后，Dozer 提供了 dozer-spring-boot-starter 用于 Spring Boot 的集成，如果使用 Maven 构建的项目，只需要在 pom.xml 文件中引入如下依赖：

```
<dependency>
    <groupId>com.github.dozermapper</groupId>
    <artifactId>dozer-spring-boot-starter</artifactId>
    <version>{dozer-version}</version>
</dependency>
```

新建 DozerMapperConfig 配置类：

```
@Configuration
public class DozerMapperConfig {

    @Bean
    public DozerBeanMapperFactoryBean dozerMapper(@Value("classpath:mapping/*.xml") Resource[] resources) throws IOException {
        DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean = new DozerBeanMapperFactoryBean();
        dozerBeanMapperFactoryBean.setMappingFiles(resources);
        return dozerBeanMapperFactoryBean;
    }

}
```

把 DozerBeanMapperFactoryBean 注入到 IOC 容器中，就能优雅的使用 Dozer 啦，如：

```
@SpringBootTest
@RunWith(SpringRunner.class)
public class DozerTest {

    @Autowired
    private Mapper mapper;
    @Test
    public void dozerForSbTest(){
        User user = new User();
        user.setName("hxy");
        user.setAge(123);
        user.setBirthday(new Date());
        UserXmlDestinationObject destinationObject = mapper.map(user, UserXmlDestinationObject.class);
        System.out.println(destinationObject);
    }
}
```

 