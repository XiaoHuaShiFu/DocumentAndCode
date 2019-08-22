# 详解Spring Boot中PATCH上传文件的问题



这篇文章主要介绍了详解Spring Boot中PATCH上传文件的问题，小编觉得挺不错的，现在分享给大家，也给大家做个参考。一起跟随小编过来看看吧

Spring Boot中上传multipart/form-data文件只能是Post提交，而不针对PATCH，这个问题花了作者26个小时才解决这个问题，最后不得不调试Spring源代码来解决这个问题。

需求：在网页中构建一个表单，其中包含一个文本输入字段和一个用于文件上载的输入。很简单。这是表单：

[?](https://www.jb51.net/article/153345.htm#)

```java
`<``form` `id=”data” method=”PATCH” action=”/f” >``  ``<``input` `type``=``"text"` `required ``name``=``"company"` `>``  ``<``input` `type``=``"file"` `required ``name``=``"definition"` `/>``</``form``>`
```

RestController中的方法：

[?](https://www.jb51.net/article/153345.htm#)

```java
`@RequestMapping``(value = ``"/f"``,method = PATCH)``public` `void` `upload(``   ``@RequestPart``(``"definition"``) MultipartFile definition,``   ``@RequestPart``(``"company"``) String company``) {...}`
```

注意它是PATCH的方法（根据要求）而不是POST，部分要求是提交的ajax请求，并不是表单提交，代码如下：

[?](https://www.jb51.net/article/153345.htm#)

```java
`var fileInput = ...; ``//this is html element that holds the files``var textInput = ...; ``//thi is the string``var fd = ``new` `FormData();``fd.append(``'definition'``,fileInput.files[``0``]);``fd.append(``'name'``, textInput );``xhr = ``new` `XMLHttpRequest();``xhr.open( ``'PATCH'``, uploadForm.action, ``true` `);``xhr.send( fd );`
```

但无论怎么做，我都无法让它发挥作用。总是遇到以下异常：

> MissingServletRequestPartException: Required request part ‘definition' is not present

我做的第一件事就是将这个问题分解为最简单的问题。所以我将请求类型更改为POST，并删除了textInput。将MultiPart解析器的实现进行更改，从org.springframework.web.multipart.support.StandardServletMultipartResolver 改为org.springframework.web.multipart.commons.CommonsMultipartResolver

[?](https://www.jb51.net/article/153345.htm#)

```java
`@Configuration``public` `class` `MyConfig {` `  ``@Bean``  ``public` `MultipartResolver multipartResolver() {``   ``return` `new` `CommonsMultipartResolver();``  ``}``}`
```

这还需要将commons-fileupload库添加到类路径中。

但每当我添加一个字符串变量返回错误：the string field not the file field

这说明multi part request resolver 没有发现这部分字段。

这是由于Javascript的FormData问题，在FormData对象上调用的Append方法接受两个参数name和value（有第三个但不重要），该value字段可以是一个 [USVString](https://developer.mozilla.org/en-US/docs/Web/API/USVString)或[Blob](https://developer.mozilla.org/en-US/docs/Web/API/Blob)（包括子类等[File](https://developer.mozilla.org/en-US/docs/Web/API/File)）。更改代码为：

[?](https://www.jb51.net/article/153345.htm#)

```java
`var fileInput = ...; ``//this is html element that holds the files``var textInput = = ``new` `Blob([``'the info'``], {``  ``type: ``'text/plain'``});``; ``//thi is the string``var fd = ``new` `FormData();``fd.append(``'definition'``,fileInput.files[``0``]);``fd.append(``'name'``, textInput );``xhr = ``new` `XMLHttpRequest();``xhr.open( ``'PATCH'``, uploadForm.action, ``true` `);``xhr.send( fd );`
```

它突然开始工作:)。

看一下浏览器发送的内容：

> — — — WebKitFormBoundaryHGN3YjdgsELbgmZH
> Content-Disposition: form-data; name=”definition”; filename=”test.csv” Content-Type: text/csv
> this is the content of a file, browser hides it.
> — — — WebKitFormBoundaryHGN3YjdgsELbgmZH Content-Disposition: form-data; name=”name” 
> this is the string
> — — — WebKitFormBoundaryHGN3YjdgsELbgmZH —

你能注意到内容处置标题中缺少的内容吗？文件名和内容类型。在servlet处理期间，multi-part表单变成MultipartFile。在commons-fileupload中有一行：

[?](https://www.jb51.net/article/153345.htm#)

```java
`String subContentType = headers.getHeader(CONTENT_TYPE);``if` `(subContentType != ``null` `... ){}`
```

这是get的内容类型，如果它是null，则处理是通过不同的路由将我们的上传部分不是转为MultipartFile，而是转换为MultipartParameter（放在不同的Map中，而spring没有找到它），然后spring为每个参数创建单独的实例，形成在调用rest方法时实现绑定的表单。

RequestPartServletServerHttpRequest构造函数中可以找到抛出异常的位置：

[?](https://www.jb51.net/article/153345.htm#)

```java
`HttpHeaders headers = ``this``.multipartRequest.getMultipartHeaders(``this``.partName);``if` `(headers == ``null``) {``  ``throw` `new` `MissingServletRequestPartException(partName);``}`
```

重要的是getMultipartHeaders只查看multipart的文件files而不是参数parameters。

这就是为什么添加具有特定类型的blob解决了问题的原因：

[?](https://www.jb51.net/article/153345.htm#)

```javascript
`var textInput = = ``new` `Blob([``'the info'``], {``  ``type: ``'text/plain'``});`
```

现在回过来，前面我提到我必须切换到使用POST才正常，但当我改为PATCH时，问题又回来了。错误是一样的。

我很困惑。所以找到了源代码（毕竟这是最终的文档）。

请记住，在本文开头切换到了CommonsMultipartResolver。事实证明，在请求处理期间，调用此方法：

[?](https://www.jb51.net/article/153345.htm#)

```java
`public` `static` `final` `boolean` `isMultipartContent(``    ``HttpServletRequest request) {``  ``if` `(!POST_METHOD.equalsIgnoreCase(request.getMethod())) {``    ``return` `false``;``  ``}``  ``return` `FileUploadBase.isMultipartContent(``new` `ServletRequestContext(request));``}`
```

如果它不是POST请求，则立即确定该请求没有multipart内容。

那么久通过覆盖调用上面静态方法的方法解决了这个问题。

所以现在config bean看起来像这样：

[?](https://www.jb51.net/article/153345.htm#)

```java
`@Bean``public` `MultipartResolver multipartResolver() {``  ``return` `new` `CommonsMultipartResolverMine();``}`  `public` `static` `class` `CommonsMultipartResolverMine ``extends` `CommonsMultipartResolver {` `  ``@Override``  ``public` `boolean` `isMultipart(HttpServletRequest request) {``   ``final` `String header = request.getHeader(``"Content-Type"``);``   ``if``(header == ``null``){``     ``return` `false``;``   ``}``   ``return` `header.contains(``"multipart/form-data"``);``  ``}` `}`
```

以上就是本文的全部内容，希望对大家的学习有所帮助，也希望大家多多支持脚本之家。