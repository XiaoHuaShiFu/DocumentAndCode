#### 11.3.1. Overview

Spring Security uses Spring EL for expression support and you should look at how that works if you are interested in understanding the topic in more depth. Expressions are evaluated with a "root object" as part of the evaluation context. Spring Security uses specific classes for web and method security as the root object, in order to provide built-in expressions and access to values such as the current principal.

##### Common Built-In Expressions

The base class for expression root objects is `SecurityExpressionRoot`. This provides some common expressions which are available in both web and method security.

| Expression                                                   | Description                                                  |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| `hasRole(String role)`                                       | Returns `true` if the current principal has the specified role.For example, `hasRole('admin')`By default if the supplied role does not start with 'ROLE_' it will be added. This can be customized by modifying the `defaultRolePrefix` on `DefaultWebSecurityExpressionHandler`. |
| `hasAnyRole(String… roles)`                                  | Returns `true` if the current principal has any of the supplied roles (given as a comma-separated list of strings).For example, `hasAnyRole('admin', 'user')`By default if the supplied role does not start with 'ROLE_' it will be added. This can be customized by modifying the `defaultRolePrefix` on `DefaultWebSecurityExpressionHandler`. |
| `hasAuthority(String authority)`                             | Returns `true` if the current principal has the specified authority.For example, `hasAuthority('read')` |
| `hasAnyAuthority(String… authorities)`                       | Returns `true` if the current principal has any of the supplied authorities (given as a comma-separated list of strings)For example, `hasAnyAuthority('read', 'write')` |
| `principal`                                                  | Allows direct access to the principal object representing the current user |
| `authentication`                                             | Allows direct access to the current `Authentication` object obtained from the `SecurityContext` |
| `permitAll`                                                  | Always evaluates to `true`                                   |
| `denyAll`                                                    | Always evaluates to `false`                                  |
| `isAnonymous()`                                              | Returns `true` if the current principal is an anonymous user |
| `isRememberMe()`                                             | Returns `true` if the current principal is a remember-me user |
| `isAuthenticated()`                                          | Returns `true` if the user is not anonymous                  |
| `isFullyAuthenticated()`                                     | Returns `true` if the user is not an anonymous or a remember-me user |
| `hasPermission(Object target, Object permission)`            | Returns `true` if the user has access to the provided target for the given permission. For example, `hasPermission(domainObject, 'read')` |
| `hasPermission(Object targetId, String targetType, Object permission)` | Returns `true` if the user has access to the provided target for the given permission. For example, `hasPermission(1, 'com.example.domain.Message', 'read')` |



# 作用：

当我们想要开启spring方法级安全时，只需要在任何 @Configuration实例上使用 @EnableGlobalMethodSecurity 注解就能达到此目的。同时这个注解为我们提供了prePostEnabled 、securedEnabled 和 jsr250Enabled 三种不同的机制来实现同一种功能：

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
}
12345
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200305182051572.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoaWhhaWhhaQ==,size_16,color_FFFFFF,t_70)

# prePostEnabled ：

prePostEnabled = true 会解锁 @PreAuthorize 和 @PostAuthorize 两个注解。从名字就可以看出@PreAuthorize 注解会在方法执行前进行验证，而 @PostAuthorize 注解会在方法执行后进行验证。

```java
public interface UserService {
    List<User> findAllUsers();

    @PostAuthorize ("returnObject.type == authentication.name")
    User findById(int id);
    
	//  @PreAuthorize("hasRole('ADMIN')") 必须拥有 ROLE_ADMIN 角色。
    @PreAuthorize("hasRole('ROLE_ADMIN ')")
    void updateUser(User user);
    
    @PreAuthorize("hasRole('ADMIN') AND hasRole('DBA')")
    void deleteUser(int id);
    
    // @PreAuthorize("principal.username.startsWith('Felordcn')") 用户名开头为 Felordcn 的用户才能访问。
    // @PreAuthorize("#id.equals(principal.username)") 入参 id 必须同当前的用户名相同。
    // @PreAuthorize("#id < 10") 限制只能查询 id 小于 10 的用户

}
123456789101112131415161718
```

[常见内置表达式](http://docs.spring.io/spring-security/site/docs/4.0.1.RELEASE/reference/htmlsingle/#el-common-built-in)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200305185937101.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoaWhhaWhhaQ==,size_16,color_FFFFFF,t_70)
**@PostAuthorize：** 该注解使用不多，在方法执行后再进行权限验证。 适合验证带有返回值的权限。Spring EL 提供 返回对象能够在表达式语言中获取返回的对象returnObject。区别在于先执行方法。而后进行表达式判断。如果方法没有返回值实际上等于开放权限控制；如果有返回值实际的结果是用户操作成功但是得不到响应。允许方法调用,但是如果表达式计算结果为false,将抛出一个安全性异常。

**@PreFilter：** 对集合类型的参数执行过滤，移除结果为false的元素。基于方法入参相关的表达式，对入参进行过滤。分页慎用！该过程发生在接口接收参数之前。 入参必须为 java.util.Collection 且支持 remove(Object) 的参数。如果有多个集合需要通过 filterTarget=<参数名> 来指定过滤的集合。内置保留名称 filterObject 作为集合元素的操作名来进行评估过滤。

```java
// 指定过滤的参数，过滤偶数
@PreFilter(filterTarget="ids", value="filterObject%2==0")
public void delete(List<Integer> ids, List<String> username)
123
```

**@PostFilter：** 和@PreFilter 不同的是， 基于返回值相关的表达式，对返回值进行过滤。分页慎用！该过程发生接口进行数据返回之前。

# Secured：

@Secured注解是用来定义业务方法的安全配置。在需要安全[角色/权限等]的方法上指定 @Secured，并且只有那些角色/权限的用户才可以调用该方法。
@Secured缺点（限制）就是不支持Spring EL表达式。不够灵活。并且指定的角色必须以ROLE_开头，不可省略。该注解功能要简单的多，默认情况下只能基于角色（默认需要带前缀 ROLE_）集合来进行访问控制决策。该注解的机制是只要其声明的角色集合（value）中包含当前用户持有的任一角色就可以访问。也就是 用户的角色集合和 @Secured 注解的角色集合要存在非空的交集。 不支持使用 SpEL 表达式进行决策。

```java
    @Secured({"ROLE_user"})
    void updateUser(User user);

    @Secured({"ROLE_admin", "ROLE_user1"})
    void updateUser();
123456
```

# jsr250E：

启用 JSR-250 安全控制注解，这属于 JavaEE 的安全规范（现为 jakarta 项目）。一共有五个安全注解。如果你在 @EnableGlobalMethodSecurity 设置 jsr250Enabled 为 true ，就开启了 JavaEE 安全注解中的以下三个：

1.@DenyAll： 拒绝所有访问

2.@RolesAllowed({“USER”, “ADMIN”})： 该方法只要具有"USER", "ADMIN"任意一种权限就可以访问。这里可以省略前缀ROLE_，实际的权限可能是ROLE_ADMIN

3.@PermitAll： 允许所有访问