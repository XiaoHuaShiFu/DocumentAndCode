# spring security里的跨域设置

配置securityHttp

```
http.cors()
```



配置bean

```
/**
 * cors跨域配置，配置允许的跨域源
 *
 * @return CorsConfigurationSource
 */
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    // 配置允许的的源，就是允许哪个域的请求访问服务器
    corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
    // 允许的请求方法
    corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
    // 允许的自定义请求头
    corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
    corsConfiguration.addExposedHeader("Authorization");
    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
    return urlBasedCorsConfigurationSource;
}
```