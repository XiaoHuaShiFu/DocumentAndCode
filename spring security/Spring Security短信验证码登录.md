## Spring Security短信验证码登录

 2018-05-11 |  Visit count 148032

在[Spring Security添加图形验证码](https://mrbird.cc/Spring-Security-ValidateCode.html)一节中，我们已经实现了基于Spring Boot + Spring Security的账号密码登录，并集成了图形验证码功能。时下另一种非常常见的网站登录方式为手机短信验证码登录，但Spring Security默认只提供了账号密码的登录认证逻辑，所以要实现手机短信验证码登录认证功能，我们需要模仿Spring Security账号密码登录逻辑代码来实现一套自己的认证逻辑。

## 短信验证码生成

我们在上一节[Spring Security添加图形验证码](https://mrbird.cc/Spring-Security-ValidateCode.html)的基础上来集成短信验证码登录的功能。

和图形验证码类似，我们先定义一个短信验证码对象SmsCode：

```
public class SmsCode {
    private String code;
    private LocalDateTime expireTime;

    public SmsCode(String code, int expireIn) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public SmsCode(String code, LocalDateTime expireTime) {
        this.code = code;
        this.expireTime = expireTime;
    }

    boolean isExpire() {
        return LocalDateTime.now().isAfter(expireTime);
    }
    // get,set略
}
```



SmsCode对象包含了两个属性：code验证码和expireTime过期时间。isExpire方法用于判断短信验证码是否已过期。

接着在ValidateCodeController中加入生成短信验证码相关请求对应的方法：

```
@RestController
public class ValidateController {

    public final static String SESSION_KEY_SMS_CODE = "SESSION_KEY_SMS_CODE";

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @GetMapping("/code/sms")
    public void createSmsCode(HttpServletRequest request, HttpServletResponse response, String mobile) throws IOException {
        SmsCode smsCode = createSMSCode();
        sessionStrategy.setAttribute(new ServletWebRequest(request), SESSION_KEY_SMS_CODE + mobile, smsCode);
        // 输出验证码到控制台代替短信发送服务
        System.out.println("您的登录验证码为：" + smsCode.getCode() + "，有效时间为60秒");
    }

    private SmsCode createSMSCode() {
        String code = RandomStringUtils.randomNumeric(6);
        return new SmsCode(code, 60);
    }
}
```



这里我们使用`createSMSCode`方法生成了一个6位的纯数字随机数，有效时间为60秒。然后通过`SessionStrategy`对象的`setAttribute`方法将短信验证码保存到了Session中，对应的key为`SESSION_KEY_SMS_CODE`。

至此，短信验证码生成模块编写完毕，下面开始改造登录页面。

## 改造登录页

我们在登录页面中加入一个与手机短信验证码认证相关的Form表单：

```
<form class="login-page" action="/login/mobile" method="post">
    <div class="form">
        <h3>短信验证码登录</h3>
        <input type="text" placeholder="手机号" name="mobile" value="17777777777" required="required"/>
        <span style="display: inline">
            <input type="text" name="smsCode" placeholder="短信验证码" style="width: 50%;"/>
            <a href="/code/sms?mobile=17777777777">发送验证码</a>
        </span>
        <button type="submit">登录</button>
    </div>
</form>
```



其中a标签的`href`属性值对应我们的短信验证码生成方法的请求URL。Form的action对应处理短信验证码登录方法的请求URL，这个方法下面在进行具体实现。同时，我们需要在Spring Security中配置`/code/sms`路径免验证：

```
@Override
protected void configure(HttpSecurity http) throws Exception {

    http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class) // 添加验证码校验过滤器
            .formLogin() // 表单登录
                // http.httpBasic() // HTTP Basic
                .loginPage("/authentication/require") // 登录跳转 URL
                .loginProcessingUrl("/login") // 处理表单登录 URL
                .successHandler(authenticationSucessHandler) // 处理登录成功
                .failureHandler(authenticationFailureHandler) // 处理登录失败
            .and()
                .authorizeRequests() // 授权配置
                .antMatchers("/authentication/require",
                        "/login.html", "/code/image","/code/sms").permitAll() // 无需认证的请求路径
                .anyRequest()  // 所有请求
                .authenticated() // 都需要认证
            .and()
                .csrf().disable();
}
```



重启项目，访问http://localhost:8080/login.html：

![QQ截图20180729204953.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180729204953.png)

点击发送验证码，控制台输出如下：

```
您的登录验证码为：693583，有效时间为60秒
```



接下来开始实现使用短信验证码登录认证逻辑。

## 添加短信验证码认证

在Spring Security中，使用用户名密码认证的过程大致如下图所示：

![QQ截图20180730220603.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180730220603.png)

Spring Security使用`UsernamePasswordAuthenticationFilter`过滤器来拦截用户名密码认证请求，将用户名和密码封装成一个`UsernamePasswordToken`对象交给`AuthenticationManager`处理。`AuthenticationManager`将挑出一个支持处理该类型Token的`AuthenticationProvider`（这里为`DaoAuthenticationProvider`，`AuthenticationProvider`的其中一个实现类）来进行认证，认证过程中`DaoAuthenticationProvider`将调用`UserDetailService`的`loadUserByUsername`方法来处理认证，如果认证通过（即`UsernamePasswordToken`中的用户名和密码相符）则返回一个`UserDetails`类型对象，并将认证信息保存到Session中，认证后我们便可以通过`Authentication`对象获取到认证的信息了。

由于Spring Security并没用提供短信验证码认证的流程，所以我们需要仿照上面这个流程来实现：

![QQ截图20180730224103.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180730224103.png)

在这个流程中，我们自定义了一个名为`SmsAuthenticationFitler`的过滤器来拦截短信验证码登录请求，并将手机号码封装到一个叫`SmsAuthenticationToken`的对象中。在Spring Security中，认证处理都需要通过`AuthenticationManager`来代理，所以这里我们依旧将`SmsAuthenticationToken`交由`AuthenticationManager`处理。接着我们需要定义一个支持处理`SmsAuthenticationToken`对象的`SmsAuthenticationProvider`，`SmsAuthenticationProvider`调用`UserDetailService`的`loadUserByUsername`方法来处理认证。与用户名密码认证不一样的是，这里是通过`SmsAuthenticationToken`中的手机号去数据库中查询是否有与之对应的用户，如果有，则将该用户信息封装到`UserDetails`对象中返回并将认证后的信息保存到`Authentication`对象中。

为了实现这个流程，我们需要定义`SmsAuthenticationFitler`、`SmsAuthenticationToken`和`SmsAuthenticationProvider`，并将这些组建组合起来添加到Spring Security中。下面我们来逐步实现这个过程。

### 定义SmsAuthenticationToken

查看`UsernamePasswordAuthenticationToken`的源码，将其复制出来重命名为`SmsAuthenticationToken`，并稍作修改，修改后的代码如下所示：

```
public class SmsAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final Object principal;

    public SmsAuthenticationToken(String mobile) {
        super(null);
        this.principal = mobile;
        setAuthenticated(false);
    }

    public SmsAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true); // must use super, as we override
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
```



`SmsAuthenticationToken`包含一个`principal`属性，从它的两个构造函数可以看出，在认证之前`principal`存的是手机号，认证之后存的是用户信息。`UsernamePasswordAuthenticationToken`原来还包含一个`credentials`属性用于存放密码，这里不需要就去掉了。

### 定义SmsAuthenticationFilter

定义完`SmsAuthenticationToken`后，我们接着定义用于处理短信验证码登录请求的过滤器`SmsAuthenticationFilter`，同样的复制`UsernamePasswordAuthenticationFilter`源码并稍作修改：

```
public class SmsAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String MOBILE_KEY = "mobile";

    private String mobileParameter = MOBILE_KEY;
    private boolean postOnly = true;


    public SmsAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login/mobile", "POST"));
    }


    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        String mobile = obtainMobile(request);

        if (mobile == null) {
            mobile = "";
        }

        mobile = mobile.trim();

        SmsAuthenticationToken authRequest = new SmsAuthenticationToken(mobile);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected String obtainMobile(HttpServletRequest request) {
        return request.getParameter(mobileParameter);
    }

    protected void setDetails(HttpServletRequest request,
                              SmsAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    public void setMobileParameter(String mobileParameter) {
        Assert.hasText(mobileParameter, "mobile parameter must not be empty or null");
        this.mobileParameter = mobileParameter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getMobileParameter() {
        return mobileParameter;
    }
}
```



构造函数中指定了当请求为`/login/mobile`，请求方法为**POST**的时候该过滤器生效。`mobileParameter`属性值为mobile，对应登录页面手机号输入框的name属性。`attemptAuthentication`方法从请求中获取到mobile参数值，并调用`SmsAuthenticationToken`的`SmsAuthenticationToken(String mobile)`构造方法创建了一个`SmsAuthenticationToken`。下一步就如流程图中所示的那样，`SmsAuthenticationFilter`将`SmsAuthenticationToken`交给`AuthenticationManager`处理。

### 定义SmsAuthenticationProvider

在创建完`SmsAuthenticationFilter`后，我们需要创建一个支持处理该类型Token的类，即`SmsAuthenticationProvider`，该类需要实现`AuthenticationProvider`的两个抽象方法：

```
public class SmsAuthenticationProvider implements AuthenticationProvider {

    private UserDetailService userDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsAuthenticationToken authenticationToken = (SmsAuthenticationToken) authentication;
        UserDetails userDetails = userDetailService.loadUserByUsername((String) authenticationToken.getPrincipal());

        if (userDetails == null)
            throw new InternalAuthenticationServiceException("未找到与该手机号对应的用户");

        SmsAuthenticationToken authenticationResult = new SmsAuthenticationToken(userDetails, userDetails.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return SmsAuthenticationToken.class.isAssignableFrom(aClass);
    }

    public UserDetailService getUserDetailService() {
        return userDetailService;
    }

    public void setUserDetailService(UserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }
}
```



其中`supports`方法指定了支持处理的Token类型为`SmsAuthenticationToken`，`authenticate`方法用于编写具体的身份认证逻辑。在`authenticate`方法中，我们从`SmsAuthenticationToken`中取出了手机号信息，并调用了`UserDetailService`的`loadUserByUsername`方法。该方法在用户名密码类型的认证中，主要逻辑是通过用户名查询用户信息，如果存在该用户并且密码一致则认证成功；而在短信验证码认证的过程中，该方法需要通过手机号去查询用户，如果存在该用户则认证通过。认证通过后接着调用`SmsAuthenticationToken`的`SmsAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities)`构造函数构造一个认证通过的Token，包含了用户信息和用户权限。

你可能会问，为什么这一步没有进行短信验证码的校验呢？实际上短信验证码的校验是在`SmsAuthenticationFilter`之前完成的，即只有当短信验证码正确以后才开始走认证的流程。所以接下来我们需要定一个过滤器来校验短信验证码的正确性。

### 定义SmsCodeFilter

短信验证码的校验逻辑其实和图形验证码的校验逻辑基本一致，所以我们在图形验证码过滤器的基础上稍作修改，代码如下所示：

```
@Component
public class SmsCodeFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, 
    	FilterChain filterChain) throws ServletException, IOException {
        if (StringUtils.equalsIgnoreCase("/login/mobile", httpServletRequest.getRequestURI())
                && StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), "post")) {
            try {
                validateCode(new ServletWebRequest(httpServletRequest));
            } catch (ValidateCodeException e) {
                authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void validateSmsCode(ServletWebRequest servletWebRequest) throws ServletRequestBindingException {
        String smsCodeInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "smsCode");
        String mobile = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "mobile");
        ValidateCode codeInSession = (ValidateCode) sessionStrategy.getAttribute(servletWebRequest, FebsConstant.SESSION_KEY_SMS_CODE + mobile);

        if (StringUtils.isBlank(smsCodeInRequest)) {
            throw new ValidateCodeException("验证码不能为空！");
        }
        if (codeInSession == null) {
            throw new ValidateCodeException("验证码不存在，请重新发送！");
        }
        if (codeInSession.isExpire()) {
            sessionStrategy.removeAttribute(servletWebRequest, FebsConstant.SESSION_KEY_SMS_CODE + mobile);
            throw new ValidateCodeException("验证码已过期，请重新发送！");
        }
        if (!StringUtils.equalsIgnoreCase(codeInSession.getCode(), smsCodeInRequest)) {
            throw new ValidateCodeException("验证码不正确！");
        }
        sessionStrategy.removeAttribute(servletWebRequest, FebsConstant.SESSION_KEY_SMS_CODE + mobile);

    }
}
```



方法的基本逻辑和之前定义的`ValidateCodeFilter`一致，这里不再赘述。

### 配置生效

在定义完所需的组件后，我们需要进行一些配置，将这些组件组合起来形成一个和上面流程图对应的流程。创建一个配置类`SmsAuthenticationConfig`：

```
@Component
public class SmsAuthenticationConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private UserDetailService userDetailService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        SmsAuthenticationFilter smsAuthenticationFilter = new SmsAuthenticationFilter();
        smsAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        smsAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        smsAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        
        SmsAuthenticationProvider smsAuthenticationProvider = new SmsAuthenticationProvider();
        smsAuthenticationProvider.setUserDetailService(userDetailService);

        http.authenticationProvider(smsAuthenticationProvider)
                .addFilterAfter(smsAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }
}
```



在流程中第一步需要配置`SmsAuthenticationFilter`，分别设置了`AuthenticationManager`、`AuthenticationSuccessHandler`和`AuthenticationFailureHandler`属性。这些属性都是来自`SmsAuthenticationFilter`继承的`AbstractAuthenticationProcessingFilter`类中。

第二步配置`SmsAuthenticationProvider`，这一步只需要将我们自个的`UserDetailService`注入进来即可。

最后调用`HttpSecurity`的`authenticationProvider`方法指定了`AuthenticationProvider`为`SmsAuthenticationProvider`，并将`SmsAuthenticationFilter`过滤器添加到了`UsernamePasswordAuthenticationFilter`后面。

到这里我们已经将短信验证码认证的各个组件组合起来了，最后一步需要做的是配置短信验证码校验过滤器，并且将短信验证码认证流程加入到Spring Security中。在`BrowserSecurityConfig`的`configure`方法中添加如下配置：

```
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyAuthenticationSucessHandler authenticationSucessHandler;

    @Autowired
    private MyAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private ValidateCodeFilter validateCodeFilter;

    @Autowired
    private SmsCodeFilter smsCodeFilter;

    @Autowired
    private SmsAuthenticationConfig smsAuthenticationConfig;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class) // 添加验证码校验过滤器
            .addFilterBefore(smsCodeFilter, UsernamePasswordAuthenticationFilter.class) // 添加短信验证码校验过滤器
                .formLogin() // 表单登录
                    // http.httpBasic() // HTTP Basic
                    .loginPage("/authentication/require") // 登录跳转 URL
                    .loginProcessingUrl("/login") // 处理表单登录 URL
                    .successHandler(authenticationSucessHandler) // 处理登录成功
                    .failureHandler(authenticationFailureHandler) // 处理登录失败
                .and()
                    .authorizeRequests() // 授权配置
                    .antMatchers("/authentication/require",
                            "/login.html",
                            "/code/image","/code/sms").permitAll() // 无需认证的请求路径
                    .anyRequest()  // 所有请求
                    .authenticated() // 都需要认证
                .and()
                    .csrf().disable()
                .apply(smsAuthenticationConfig); // 将短信验证码认证配置加到 Spring Security 中
    }
}
```



具体含义见注释，这里不再赘述。

## 测试

重启项目，访问http://localhost:8080/login.html，点击发送验证码，控制台输出如下：

```
您的登录验证码为：169638，有效时间为60秒
```



输入该验证码，点击登录后页面如下所示：

![QQ截图20180731230532.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180731230532.png)

认证成功。

> 源码链接 https://github.com/wuyouzhuguli/SpringAll/tree/master/38.Spring-Security-SmsCode

请作者喝瓶肥宅水🥤

￥

- **本文作者：** MrBird
- **本文链接：** http://mrbird.cc/Spring-Security-SmsCode.html
- **版权声明：** 本博客所有文章除特别声明外，均采用 [CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/) 许可协议。转载请注明出处！

[# Spring](https://mrbird.cc/tags/Spring/) [# Security](https://mrbird.cc/tags/Security/) [# Spring Security](https://mrbird.cc/tags/Spring-Security/)

[
  ](https://mrbird.cc/Spring-Security-RememberMe.html)