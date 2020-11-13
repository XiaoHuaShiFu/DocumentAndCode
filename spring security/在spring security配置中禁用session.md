在spring security配置中禁用session

```java
httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
```