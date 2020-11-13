## Spring Security添加图形验证码

 2018-05-05 |  Visit count 148034

添加验证码大致可以分为三个步骤：根据随机数生成验证码图片；将验证码图片显示到登录页面；认证流程中加入验证码校验。Spring Security的认证校验是由`UsernamePasswordAuthenticationFilter`过滤器完成的，所以我们的验证码校验逻辑应该在这个过滤器之前。下面一起学习下如何在上一节[Spring Security自定义用户认证](https://mrbird.cc/Spring-Security-Authentication.html)的基础上加入验证码校验功能。

## 生成图形验证码

验证码功能需要用到`spring-social-config`依赖：

```
 <dependency>
    <groupId>org.springframework.social</groupId>
    <artifactId>spring-social-config</artifactId>
</dependency>
```



首先定义一个验证码对象ImageCode：

```
public class ImageCode {

    private BufferedImage image;

    private String code;

    private LocalDateTime expireTime;

    public ImageCode(BufferedImage image, String code, int expireIn) {
        this.image = image;
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public ImageCode(BufferedImage image, String code, LocalDateTime expireTime) {
        this.image = image;
        this.code = code;
        this.expireTime = expireTime;
    }

    boolean isExpire() {
        return LocalDateTime.now().isAfter(expireTime);
    }
    // get,set 略
}
```



ImageCode对象包含了三个属性：`image`图片，`code`验证码和`expireTime`过期时间。`isExpire`方法用于判断验证码是否已过期。

接着定义一个ValidateCodeController，用于处理生成验证码请求：

```
@RestController
public class ValidateController {

    public final static String SESSION_KEY_IMAGE_CODE = "SESSION_KEY_IMAGE_CODE";

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @GetMapping("/code/image")
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageCode imageCode = createImageCode();
        sessionStrategy.setAttribute(new ServletWebRequest(request), SESSION_KEY, imageCode);
        ImageIO.write(imageCode.getImage(), "jpeg", response.getOutputStream());
    }
}
```



`createImageCode`方法用于生成验证码对象，`org.springframework.social.connect.web.HttpSessionSessionStrategy`对象封装了一些处理Session的方法，包含了`setAttribute`、`getAttribute`和`removeAttribute`方法，具体可以查看该类的源码。使用`sessionStrategy`将生成的验证码对象存储到Session中，并通过IO流将生成的图片输出到登录页面上。

其中`createImageCode`方法代码如下所示：

```
private ImageCode createImageCode() {

    int width = 100; // 验证码图片宽度
    int height = 36; // 验证码图片长度
    int length = 4; // 验证码位数
    int expireIn = 60; // 验证码有效时间 60s

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics g = image.getGraphics();

    Random random = new Random();

    g.setColor(getRandColor(200, 250));
    g.fillRect(0, 0, width, height);
    g.setFont(new Font("Times New Roman", Font.ITALIC, 20));
    g.setColor(getRandColor(160, 200));
    for (int i = 0; i < 155; i++) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(12);
        int yl = random.nextInt(12);
        g.drawLine(x, y, x + xl, y + yl);
    }

    StringBuilder sRand = new StringBuilder();
    for (int i = 0; i < length; i++) {
        String rand = String.valueOf(random.nextInt(10));
        sRand.append(rand);
        g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
        g.drawString(rand, 13 * i + 6, 16);
    }
    g.dispose();
    return new ImageCode(image, sRand.toString(), expireIn);
}

private Color getRandColor(int fc, int bc) {
    Random random = new Random();
    if (fc > 255) {
        fc = 255;
    }
    if (bc > 255) {
        bc = 255;
    }
    int r = fc + random.nextInt(bc - fc);
    int g = fc + random.nextInt(bc - fc);
    int b = fc + random.nextInt(bc - fc);
    return new Color(r, g, b);
}
```



生成验证码的方法写好后，接下来开始改造登录页面。

## 改造登录页

在登录页面加上如下代码：

```
<span style="display: inline">
    <input type="text" name="imageCode" placeholder="验证码" style="width: 50%;"/>
    <img src="/code/image"/>
</span>
```



<img>标签的src属性对应ValidateController的createImageCode方法。

要使生成验证码的请求不被拦截，需要在`BrowserSecurityConfig`的`configure`方法中配置免拦截：

```
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.formLogin() // 表单登录
            // http.httpBasic() // HTTP Basic
            .loginPage("/authentication/require") // 登录跳转 URL
            .loginProcessingUrl("/login") // 处理表单登录 URL
            .successHandler(authenticationSucessHandler) // 处理登录成功
            .failureHandler(authenticationFailureHandler) // 处理登录失败
            .and()
            .authorizeRequests() // 授权配置
            .antMatchers("/authentication/require",
                    "/login.html",
                    "/code/image").permitAll() // 无需认证的请求路径
            .anyRequest()  // 所有请求
            .authenticated() // 都需要认证
            .and().csrf().disable();
}
```



重启项目，访问http://localhost:8080/login.html，效果如下：

![QQ截图20180715105219.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180715105219.png)

## 认证流程添加验证码校验

在校验验证码的过程中，可能会抛出各种验证码类型的异常，比如“验证码错误”、“验证码已过期”等，所以我们定义一个验证码类型的异常类：

```
public class ValidateCodeException extends AuthenticationException {
    private static final long serialVersionUID = 5022575393500654458L;

    ValidateCodeException(String message) {
        super(message);
    }
}
```



注意，这里继承的是`AuthenticationException`而不是`Exception`。

我们都知道，Spring Security实际上是由许多过滤器组成的过滤器链，处理用户登录逻辑的过滤器为`UsernamePasswordAuthenticationFilter`，而验证码校验过程应该是在这个过滤器之前的，即只有验证码校验通过后采去校验用户名和密码。由于Spring Security并没有直接提供验证码校验相关的过滤器接口，所以我们需要自己定义一个验证码校验的过滤器`ValidateCodeFilter`：

```
@Component
public class ValidateCodeFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, 
    	FilterChain filterChain) throws ServletException, IOException {

        if (StringUtils.equalsIgnoreCase("/login", httpServletRequest.getRequestURI())
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
    private void validateCode(ServletWebRequest servletWebRequest) throws ServletRequestBindingException {
       ...
    }	

}
```

`ValidateCodeFilter`继承了`org.springframework.web.filter.OncePerRequestFilter`，该过滤器只会执行一次。

在`doFilterInternal`方法中我们判断了请求URL是否为`/login`，该路径对应登录`form`表单的`action`路径，请求的方法是否为**`POST`**，是的话进行验证码校验逻辑，否则直接执行`filterChain.doFilter`让代码往下走。当在验证码校验的过程中捕获到异常时，调用Spring Security的校验失败处理器`AuthenticationFailureHandler`进行处理。

`validateCode`的校验逻辑如下所示：

```
private void validateCode(ServletWebRequest servletWebRequest) throws ServletRequestBindingException {
    ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(servletWebRequest, ValidateController.SESSION_KEY);
    String codeInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "imageCode");

    if (StringUtils.isBlank(codeInRequest)) {
        throw new ValidateCodeException("验证码不能为空！");
    }
    if (codeInSession == null) {
        throw new ValidateCodeException("验证码不存在！");
    }
    if (codeInSession.isExpire()) {
        sessionStrategy.removeAttribute(servletWebRequest, ValidateController.SESSION_KEY);
        throw new ValidateCodeException("验证码已过期！");
    }
    if (!StringUtils.equalsIgnoreCase(codeInSession.getCode(), codeInRequest)) {
        throw new ValidateCodeException("验证码不正确！");
    }
    sessionStrategy.removeAttribute(servletWebRequest, ValidateController.SESSION_KEY);

}
```



我们分别从`Session`中获取了`ImageCode`对象和请求参数`imageCode`（对应登录页面的验证码`<input>`框`name`属性）,然后进行了各种判断并抛出相应的异常。当验证码过期或者验证码校验通过时，我们便可以删除`Session`中的`ImageCode`属性了。

验证码校验过滤器定义好了，怎么才能将其添加到`UsernamePasswordAuthenticationFilter`前面呢？很简单，只需要在`BrowserSecurityConfig`的`configure`方法中添加些许配置即可：

```
@Autowired
private ValidateCodeFilter validateCodeFilter;

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
                    "/login.html",
                    "/code/image").permitAll() // 无需认证的请求路径
            .anyRequest()  // 所有请求
            .authenticated() // 都需要认证
            .and().csrf().disable();
}
```



上面代码中，我们注入了`ValidateCodeFilter`，然后通过`addFilterBefore`方法将`ValidateCodeFilter`验证码校验过滤器添加到了`UsernamePasswordAuthenticationFilter`前面。

大功告成，重启项目，访问http://localhost:8080/login.html，当不输入验证码时点击登录，页面显示如下：

![QQ截图20180715114011.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180715114011.png)

当输入错误的验证码时点击登录，页面显示如下：

![QQ截图20180715114052.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180715114052.png)

当页面加载60秒后再输入验证码点击登录，页面显示如下：

![QQ截图20180715114539.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180715114539.png)

当验证码通过，并且用户名密码正确时，页面显示如下：

![QQ截图20180715114654.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180715114654.png)

> 源码链接 https://github.com/wuyouzhuguli/SpringAll/tree/master/36.Spring-Security-ValidateCode

请作者喝瓶肥宅水🥤

￥

- **本文作者：** MrBird
- **本文链接：** http://mrbird.cc/Spring-Security-ValidateCode.html
- **版权声明：** 本博客所有文章除特别声明外，均采用 [CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/) 许可协议。转载请注明出处！

[# Spring](https://mrbird.cc/tags/Spring/) [# Security](https://mrbird.cc/tags/Security/) [# Spring Security](https://mrbird.cc/tags/Spring-Security/)

[
  ](https://mrbird.cc/Spring-Security-Authentication.html)