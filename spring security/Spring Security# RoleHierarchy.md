# Spring Security# RoleHierarchy

# 在哪配置

哪里需要认证，就在哪里配置。在使用Spring Security OAuth2的时候，分为Authorization Server、Resource Server，授权在Authorization Server，认证在Resource Server，那么这个配置就需要在Resource Server 配置。

# 怎么配置

```
/**
 * 角色继承配置
 * 
 * @return
 */
@Bean
public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_DBA ROLE_DBA > ROLE_USER");
    return roleHierarchy;
}1234567891011
```

只需要往Spring IOC容器中注入一个RoleHierarchy类型的Bean就可以了。
Spring Security会处理这个Bean，生成一个RoleHierarchyVoter，注入到AccessDecisionManager中，默认就是AffirmativeBased，这个了。

# 参考

RoleVoter
RoleHierarchyVoter
RoleHierarchyImpl
[spring-security-reference#Hierarchical Roles](https://docs.spring.io/spring-security/site/docs/4.2.3.RELEASE/reference/htmlsingle/#authz-hierarchical-roles)
[Spring Security Role Hierarchy not working using Java Config](https://stackoverflow.com/questions/29888458/spring-security-role-hierarchy-not-working-using-java-config)