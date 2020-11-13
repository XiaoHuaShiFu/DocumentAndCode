我有一个Spring Security配置，可以通过读取特定的请求头值来“认证”用户。 因此，使用RequestHeaderAuthenticationFilter（）：

```
@Bean
public RequestHeaderAuthenticationFilter requestHeaderFilter() throws Exception {
    RequestHeaderAuthenticationFilter authenticationFilter = new RequestHeaderAuthenticationFilter();
    authenticationFilter.setPrincipalRequestHeader("user");
    authenticationFilter.setAuthenticationManager(this.authenticationManager());
    return authenticationFilter;
}

private static final String HEALTH_PATH_NOAUTH = "/actuator/health";

@Override
public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(HEALTH_PATH_NOAUTH);
}

@Override
protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();
    http.cors();
    http.authorizeRequests().anyRequest().authenticated();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.addFilter(requestHeaderFilter());
}
```