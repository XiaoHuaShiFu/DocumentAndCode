# DO、DTO、BO、AO、VO、POJO定义和转换的正确姿势

## 一、引言

DO、DTO、BO、AO、VO、POJO的概念看似简单，但是想区分好或者理解好也不容易，本文简单梳理一下。

通过各层POJO的使用，有助于提高代码的可读性和可维护性。

\------------------------------------------------------------------------------------------------------------------------------------------------------

## 二、区别

《阿里巴巴Java开发规范》关于领域模型的部分介绍如下：

> 分层领域模型规约:
>
> -  DO(Data Object):此对象与数据库表结构一一对应，通过 DAO 层向上传输数据源对象。
>
> -  DTO(Data Transfer Object):数据传输对象，Service 或 Manager 向外传输的对象。
>
> -  BO(Business Object):业务对象，由 Service 层输出的封装业务逻辑的对象。
>
> -   AO(ApplicationObject):应用对象，在Web层与Service层之间抽象的复用对象模型， 极为贴近展示层，复用度不高。
>
> -   VO(View Object):显示层对象，通常是 Web 向模板渲染引擎层传输的对象。
>
> -  Query:数据查询对象，各层接收上层的查询请求。注意超过 2 个参数的查询封装，禁止
>
>   使用 Map 类来传输。

\------------------------------------------------------------------------------------------------------------------------------------------------------

最难理解的是BO，大致这么理解：

BO这个对象可以包括一个或多个其它的对象。

比如一个简历，有教育经历、工作经历、社会关系等等。

我们可以把教育经历对应一个PO，工作经历对应一个PO，社会关系对应一个PO。

建立一个对应简历的BO对象处理简历，每个BO包含这些PO。这样处理业务逻辑时，我们就可以针对BO去处理。

\------------------------------------------------------------------------------------------------------------------------------------------------------

```java
大致的示例代码：

**Controller层**
public List<UserVO> getUsers(UserQuery userQuery);
此层常见的转换为：DTO转VO


**Service层、Manager层**
// 普通的service层接口
 List<UserDTO> getUsers(UserQuery userQuery);
然后在Service内部使用UserBO封装中间所需的逻辑对象

// 来自前端的请求
 List<UserDTO> getUsers(UserAO userAo);
此层常见的转换为：DO转BO、BO转DTO


**DAO层**
List<UserDO> getUsers(UserQuery userQuery);
```

\------------------------------------------------------------------------------------------------------------------------------------------------------

## 三、各种对象该怎么转换？

 

那么这里又涉及到另外一个问题，各种对象转换该怎么办呢？

常见的写一个转换方法，然后手动调用get/set方法，属性太多非常痛苦，容易遗漏或者重复，而且效率非常低下。

 

推荐两种方式：

（1）一种是通过IDEA插件实现快速自动生成转换代码.

如**Generate All setters插件，参见**<https://blog.csdn.net/w605283073/article/details/89163627>

定义好参数和返回值，使用快捷方式可以轻松生成转换的代码。

（2）一种是通过第三方转换库来转换。

- [Apache org.apache.commons.beanutils.PropertyUtils.copyProperties](https://link.jianshu.com/?t=http://commons.apache.org/proper/commons-beanutils/)
- [Apache org.apache.commons.beanutils.BeanUtils.copyProperties](https://link.jianshu.com/?t=http://commons.apache.org/proper/commons-beanutils/)
- [Spring org.springframework.beans.BeanUtils.copyProperties](https://link.jianshu.com/?t=http://spring.io/)
- [Cglib BeanCopier](https://link.jianshu.com/?t=https://github.com/cglib/cglib)
- [Dozer](https://link.jianshu.com/?t=http://dozer.sourceforge.net/)
- orika


对应的maven依赖（已经给出了maven地址，建议用最新版本）

```xml
<!-- https://mvnrepository.com/artifact/commons-beanutils/commons-beanutils -->
<dependency>
    <groupId>commons-beanutils</groupId>
    <artifactId>commons-beanutils</artifactId>
    <version>1.9.3</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.springframework/spring-beans -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-beans</artifactId>
    <version>5.1.6.RELEASE</version>
</dependency>

<!-- https://mvnrepository.com/artifact/cglib/cglib -->
<dependency>
    <groupId>cglib</groupId>
    <artifactId>cglib</artifactId>
    <version>3.2.11</version>
</dependency>


<!-- https://mvnrepository.com/artifact/net.sf.dozer/dozer -->
<dependency>
    <groupId>net.sf.dozer</groupId>
    <artifactId>dozer</artifactId>
    <version>5.5.1</version>
</dependency>

<!-- https://mvnrepository.com/artifact/ma.glasnost.orika/orika-core -->
<dependency>
    <groupId>ma.glasnost.orika</groupId>
    <artifactId>orika-core</artifactId>
    <version>1.5.4</version>
</dependency>
```

 

从效率来讲，读过其他文章，综合而言cglib应该最高，其次是orika。

因为

cglib用的是asm，直接操作内存对象的字节码增强技术。

orika用的是j*avassist*, 通过动态字节码生成实现对象转换。

 

具体用法在这里就不介绍了，需要的话去官网看看文档也可以去github看下demo。

**我的看法：**

第二种代码实现对象转换简洁并且功能强大，但是我个人非常推崇第一种写转换方法的方式，因为这种方式对象属性改变可以直观反映到代码上，也可以避免因为粗心和增删属性等出现的莫名其妙的错误。

 

> 如果觉得本文对你有帮助，欢迎点赞评论，欢迎关注我，我将努力创作更多更好的文章。

 

 

 

 