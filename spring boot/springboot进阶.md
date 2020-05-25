# [SpringMVC单元测试-MockMvc](https://www.cnblogs.com/ken-jl/p/9724814.html)

## **一 简介**

 MockMvc实现对Http请求的模拟，可以方便对Controller进行测试，使得测试速度快、不依赖网络环境，而且提供验证的工具，使得请求的验证统一而且很方便。

 

**二 常见使用方式**

1 MockMvcBuilder构造MockMvc的构造器

2 MockMvcRequestBuilders创建请求request

3 mockMvc调用perform，执行一个request请求，调用controller的业务处理逻辑，返回ResultActions

4 可以通过ResultActions, MockMvcResultMatchers对结果进行验证

　　

**1 MockMvcBuilder**

MockMvcBuilder是MockMvc的构造器，主要有两个实现：StandaloneMockMvcBuilder和DefaultMockMvcBuilder。

① MockMvcBuilders.webAppContextSetup(WebApplicationContext context)：集成Web环境方式，指定WebApplicationContext，将会从该上下文获取相应的控制器并得到相应的MockMvc；

② MockMvcBuilders.standaloneSetup(Object... controllers)：独立测试方式，通过参数指定一组控制器，这样就不需要从上下文获取了，比如this.mockMvc =MockMvcBuilders.standaloneSetup(new AccountController()).build();

 

**2 MockMvcRequestBuilders**

MockMvcRequestBuilders用于构建请求，返回MockHttpServletRequestBuilder / MockMultipartHttpServletRequestBuilder ，

![img](http://pms.ipo.com/download/attachments/99205836/image2018-9-29%2016%3A34%3A49.png?version=1&modificationDate=1538210083733&api=v2)

**MockMvcRequestBuilders常用API：**

MockHttpServletRequestBuilder get(String urlTemplate, Object... urlVariables)：根据uri模板和uri变量值得到一个GET请求方式的MockHttpServletRequestBuilder；如get(/user/{id}, 1L)；

MockHttpServletRequestBuilder post(String urlTemplate, Object... urlVariables)：同get类似，但是是POST方法；

MockHttpServletRequestBuilder put(String urlTemplate, Object... urlVariables)：同get类似，但是是PUT方法；

MockHttpServletRequestBuilder delete(String urlTemplate, Object... urlVariables) ：同get类似，但是是DELETE方法；

MockHttpServletRequestBuilder options(String urlTemplate, Object... urlVariables)：同get类似，但是是OPTIONS方法；

MockHttpServletRequestBuilder request(HttpMethod httpMethod, String urlTemplate, Object... urlVariables)： 提供自己的Http请求方法及uri模板和uri变量，如上API都是委托给这个API；

MockMultipartHttpServletRequestBuilder fileUpload(String urlTemplate, Object... urlVariables)：提供文件上传方式的请求，得到MockMultipartHttpServletRequestBuilder；

RequestBuilder asyncDispatch(final MvcResult mvcResult)：创建一个从启动异步处理的请求的MvcResult进行异步分派的RequestBuilder；

**MockHttpServletRequestBuilder常用API：**

MockHttpServletRequestBuilder header(String name, Object... values)/MockHttpServletRequestBuilder headers(HttpHeaders httpHeaders)：添加头信息；

MockHttpServletRequestBuilder contentType(MediaType mediaType)：指定请求的contentType头信息；

MockHttpServletRequestBuilder accept(MediaType... mediaTypes)/MockHttpServletRequestBuilder accept(String... mediaTypes)：指定请求的Accept头信息；

MockHttpServletRequestBuilder content(byte[] content)/MockHttpServletRequestBuilder content(String content)：指定请求Body体内容；

MockHttpServletRequestBuilder cookie(Cookie... cookies)：指定请求的Cookie；

MockHttpServletRequestBuilder locale(Locale locale)：指定请求的Locale；

MockHttpServletRequestBuilder characterEncoding(String encoding)：指定请求字符编码；

MockHttpServletRequestBuilder requestAttr(String name, Object value) ：设置请求属性数据；

MockHttpServletRequestBuilder sessionAttr(String name, Object value)/MockHttpServletRequestBuilder sessionAttrs(Map<string, object=""> sessionAttributes)：设置请求session属性数据；

MockHttpServletRequestBuilder flashAttr(String name, Object value)/MockHttpServletRequestBuilder flashAttrs(Map<string, object=""> flashAttributes)：指定请求的flash信息，比如重定向后的属性信息；

MockHttpServletRequestBuilder session(MockHttpSession session) ：指定请求的Session；

MockHttpServletRequestBuilder principal(Principal principal) ：指定请求的Principal；

MockHttpServletRequestBuilder contextPath(String contextPath) ：指定请求的上下文路径，必须以“/”开头，且不能以“/”结尾；

MockHttpServletRequestBuilder pathInfo(String pathInfo) ：请求的路径信息，必须以“/”开头；

MockHttpServletRequestBuilder secure(boolean secure)：请求是否使用安全通道；

MockHttpServletRequestBuilder with(RequestPostProcessor postProcessor)：请求的后处理器，用于自定义一些请求处理的扩展点；

**MockMultipartHttpServletRequestBuilder继承自MockHttpServletRequestBuilder，又提供了如下API：**

MockMultipartHttpServletRequestBuilder file(String name, byte[] content)/MockMultipartHttpServletRequestBuilder file(MockMultipartFile file)：指定要上传的文件；

**示例：**

MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/account/info")
.contentType("text/html")
.accept(MediaType.APPLICATION_JSON);

 

**3 ResultActions**

MockMvc.perform(RequestBuilder requestBuilder)调用后返回ResultActions：

ResultActions.andExpect：添加执行完成后的断言。添加ResultMatcher验证规则，验证控制器执行完成后结果是否正确；

ResultActions.andDo：添加一个结果处理器，比如此处使用.andDo(MockMvcResultHandlers.print())输出整个响应结果信息，可以在调试的时候使用；

ResultActions.andReturn：表示执行完成后返回相应的结果，用于自定义验证/下一步的异步处理；

 

**4 ResultMatchers**

ResultMatcher验证匹配执行完请求后的结果，只有一个match(MvcResult result)断言方法，如果匹配失败将抛出相应的异常。

![img](http://pms.ipo.com/download/attachments/99205836/image2018-9-29%2016%3A47%3A7.png?version=1&modificationDate=1538210822252&api=v2)

spring mvc测试框架提供了很多***ResultMatchers来满足测试需求，可以通过MockMvcResultMatchers查看内置ResultMatcher。（注意：***ResultMatchers不是ResultMatcher的实现子类）

HandlerResultMatchers handler()：请求的Handler验证器，比如验证处理器类型/方法名；此处的Handler其实就是处理请求的控制器；

RequestResultMatchers request()：得到RequestResultMatchers验证器；

ModelResultMatchers model()：得到模型验证器；

ViewResultMatchers view()：得到视图验证器；

FlashAttributeResultMatchers flash()：得到Flash属性验证；

StatusResultMatchers status()：得到响应状态验证器；

HeaderResultMatchers header()：得到响应Header验证器；

CookieResultMatchers cookie()：得到响应Cookie验证器；

ContentResultMatchers content()：得到响应内容验证器；

JsonPathResultMatchers jsonPath(String expression, Object ... args)/ResultMatcher jsonPath(String expression, Matcher matcher)：得到Json表达式验证器；

XpathResultMatchers xpath(String expression, Object... args)/XpathResultMatchers xpath(String expression, Map<string, string=""> namespaces, Object... args)：得到Xpath表达式验证器；

ResultMatcher forwardedUrl(final String expectedUrl)：验证处理完请求后转发的url（绝对匹配）；

ResultMatcher forwardedUrlPattern(final String urlPattern)：验证处理完请求后转发的url（Ant风格模式匹配，@since spring4）；

ResultMatcher redirectedUrl(final String expectedUrl)：验证处理完请求后重定向的url（绝对匹配）；

ResultMatcher redirectedUrlPattern(final String expectedUrl)：验证处理完请求后重定向的url（Ant风格模式匹配，@since spring4）；

 

**5 MvcResult**

MvcResult为执行完控制器后得到的整个结果，并不仅仅是返回值，其包含了测试时需要的所有信息。

MockHttpServletRequest getRequest()：得到执行的请求；

MockHttpServletResponse getResponse()：得到执行后的响应；

Object getHandler()：得到执行的处理器，一般就是控制器；

HandlerInterceptor[] getInterceptors()：得到对处理器进行拦截的拦截器；

ModelAndView getModelAndView()：得到执行后的ModelAndView；

Exception getResolvedException()：得到HandlerExceptionResolver解析后的异常；

FlashMap getFlashMap()：得到FlashMap；

Object getAsyncResult()/Object getAsyncResult(long timeout)：得到异步执行的结果；



# 2、

四、一般匹配方法
要求所有的条件都要通过测试才算成功
assertThat( testedNumber, allOf( greaterThan(8), lessThan(16) ) );
配符表明如果接下来的所有条件必须都成立测试才通过，相当于“与”（&&）
接下来的所有条件只要有一个成立则测试通过
assertThat( testedNumber, anyOf( greaterThan(16), lessThan(8) ) );
注释：anyOf匹配符表明如果接下来的所有条件只要有一个成立则测试通过，相当于“或”（||）
无论什么条件，永远为true
assertThat( testedNumber, anything() );
注释：anything匹配符表明无论什么条件，永远为true
等于判断
assertThat( testedString, is( “developerWorks” ) );
注释： is匹配符表明如果前面待测的object等于后面给出的object，则测试通过
取反判断
assertThat( testedString, not( “developerWorks” ) );
注释：not匹配符和is匹配符正好相反，表明如果前面待测的object不等于后面给出的object，则测试通过
四、字符串相关匹配符
包含字符串
assertThat( testedString, containsString( “developerWorks” ) );
注释：containsString匹配符表明如果测试的字符串testedString包含子字符串”developerWorks”则测试通过
以指定字符串结尾
assertThat( testedString, endsWith( “developerWorks” ) );
注释：endsWith匹配符表明如果测试的字符串testedString以子字符串”developerWorks”结尾则测试通过
以指定字符串开始
assertThat( testedString, startsWith( “developerWorks” ) );
注释：startsWith匹配符表明如果测试的字符串testedString以子字符串”developerWorks”开始则测试通过
字符串相等测试
assertThat( testedValue, equalTo( expectedValue ) );
注释： equalTo匹配符表明如果测试的testedValue等于expectedValue则测试通过，equalTo可以测试数值之间，字符串之间和对象之间是否相等，相当于Object的equals方法
忽略大小写判断是否相等
assertThat( testedString, equalToIgnoringCase( “developerWorks” ) );
注释：equalToIgnoringCase匹配符表明如果测试的字符串testedString在忽略大小写的情况下等于”developerWorks”则测试通过
忽略头尾的任意个空格的情况下等于待测字符串
assertThat( testedString, equalToIgnoringWhiteSpace( “developerWorks” ) );
注释：equalToIgnoringWhiteSpace匹配符表明如果测试的字符串testedString在忽略头尾的任意个空格的情况下等于”developerWorks”则测试通过，注意：字符串中的空格不能被忽略
五、数值相关匹配符
范围测试
assertThat( testedDouble, closeTo( 20.0, 0.5 ) );
注释：closeTo匹配符表明如果所测试的浮点型数testedDouble在20.0±0.5范围之内则测试通过
大于判断
assertThat( testedNumber, greaterThan(16.0) );
注释：greaterThan匹配符表明如果所测试的数值testedNumber大于16.0则测试通过
小于判断
assertThat( testedNumber, lessThan (16.0) );
注释：lessThan匹配符表明如果所测试的数值testedNumber小于16.0则测试通过
大于等于
assertThat( testedNumber, greaterThanOrEqualTo (16.0) );
注释： greaterThanOrEqualTo匹配符表明如果所测试的数值testedNumber大于等于16.0则测试通过
小于等于
assertThat( testedNumber, lessThanOrEqualTo (16.0) );
注释：lessThanOrEqualTo匹配符表明如果所测试的数值testedNumber小于等于16.0则测试通过
六、collection相关匹配符
map包含测试
assertThat( mapObject, hasEntry( “key”, “value” ) );
注释：hasEntry匹配符表明如果测试的Map对象mapObject含有一个键值为”key”对应元素值为”value”的Entry项则测试通过
迭代对象包含测试
assertThat( iterableObject, hasItem ( “element” ) );
注释：hasItem匹配符表明如果测试的迭代对象iterableObject含有元素“element”项则测试通过
map包含key测试
assertThat( mapObject, hasKey ( “key” ) );
注释： hasKey匹配符表明如果测试的Map对象mapObject含有键值“key”则测试通过
map包含value测试
assertThat( mapObject, hasValue ( “key” ) );
注释：hasValue匹配符表明如果测试的Map对象mapObject含有元素值“value”则测试通过
————————————————
版权声明：本文为CSDN博主「Edocevol」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/u010570551/article/details/51492649