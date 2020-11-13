# 2、简单HTTP协议

1. 使用URI定位资源

   - 可以写完整URI，如：GET http://xiaohuashifu.top/index.html HTTP/1.1

   - 也可以在首部的Host字段中写IP地址，如：

     - ```
       GET /index.html HTTP/1.1
       Host: xiaohuashifu.top
       ```

   - 可以用*代替URI，OPTIONS * HTTP/1.1

     - 这个请求是用来查询服务器端支持的HTTP方法种类

2. HTTP方法

   - HEAD用于确认URI的有效性及资源更新的日期时间等

     - ```
       请求：
       HEAD /index.html HTTP/1.1
       Host: xiaohuashifu.top
       
       响应：
       返回index.html有关的响应首部
       ```

   - OPTIONS用于查询服务器端支持的HTTP方法种类

     - ```
       请求：
       OPTIONS * HTTP/1.1
       Host: xiaohuashifu.top
       
       响应：
       HTTP/1.1 200 OK
       Allow: GET,POST,HEAD,OPTIONS
       ```

   - CONNECT要求用隧道协议连接代理

     - ```
       请求：
       CONNECT proxy.xiaohuashifu.top:8080 HTTP/1.1
       Host: proxy.xiaohuashifu.top
       
       响应：
       HTTP/1.1 200 OK (之后进入网络隧道)
       ```

# 3、HTTP报文的HTTP信息

1. HTTP采用多部分对象集合，发送的一份报文主体内可含多类型实体。通常是在图片或文本文件等上传时使用。
   - multipart/form-data：在Web表单文件上传时使用
   - multipart/byteranges：状态码206（Partial Content，部分内容）响应报文包含了多个范围的内容时使用。
   - 使用boundary作为各类实体的分隔符，在各个实体的起始行加上--标记，如--AaB03x，在多部份对象集合对应的字符串的最后插入--标记，如--AaB03x--作为结束。多部分对象集合的而每个部分类型中，都可以含有首部字段。可以在某个部分中嵌套使用多部份对象集合。
2. 获取部分内容的范围请求
   - 请求5001-10000字节：Range: bytes=5001-10000
   - 请求5001字节之后的全部：Range: bytes=5001-
   - 请求5001-10000，20001-25000字节：Range: bytes=5001-10000, 20001-25000
   - 范围请求响应会返回状态码206 Partial Content
   - 范围请求响应会添加Content-Range指定范围的实体内容
   - 多重范围请求响应会在首部字段Content-Type标明multipart/byteranges
   - 如果服务器无法响应范围请求，则返回状态码200 Ok和完整实体内容
3. 内容协商返回最合适的内容
   - 使用请求报文的首部字段：
     - Accept
     - Accept-Charset
     - Accept-Encoding
     - Accept-Language
     - Content-Language

# 4、状态码

1. 301 Moved Permanently：永久重定向，表示请求的资源已被分配了新的URI，以后应使用资源现在所指的URI。
2. 302 Found：临时性重定向，表示请求的资源已被分配了新的URI，本次请求应使用资源新的URI。
3. 303 See Other：表示资源存在着另一个URI，应使用**GET方法**定向获取请求的资源。
4. 304 Not Modified：指GET方法请求包含IF-Match、If-Modified-Since，If-None-Match，If-Range，If-Unmodified-Since中任何一首部，且未符合条件的请求，直接返回304 Not Modified。响应不包含主题部分，可直接使用客户端未过期的缓存。
5. 307 Temporary Redirect：临时重定向，同302。但不会把POST改成GET（302会）。
6. 401 Unauthorized：表示请求需要有通过HTTP认证的认证信息。若未带信息失败则响应认证界面，带认证信息失败则响应认证失败。
7. 503 Service Unavailable：服务器处于超负载或正在进行停机维护，无法处理请求。若得到接触需要的时候，最好写入RetryAfter首部字段后再响应。

# 5、与HTTP协作的Web服务器

1. 代理
   - 请求每通过一个代理服务器在请求首部Via加一个代理服务器主机信息，响应也是。

# 6、HTTP首部

1. 通用首部字段

   1. Cache-Control：可以通过指令，操作缓存的工作机制。
   2. Connection：1控制代理不再转发的首部字段。2管理持久连接。
   3. Date：表面创建HTTP报文的日期时间。
   4. Trailer：事先说明在报文主题后记录了哪些首部字段。
   5. Transfer-Encoding：规定了传输报文主题时采用的编码方式。
   6. Upgrade：用于检测HTTP协议及其他协议是否可使用更高的版本进行通信。
   7. Via：追踪客户端与服务器之间请求和响应报文的传输路径。
   8. Warning：告知一些与缓存相关的警告。

2. 请求首部字段

   1. Accept：告知服务器，用户能够处理的媒体类型及媒体类型的相对优先级。使用type/subtype，可一次指定多种媒体类型。

      - 如文本文件text/html，text/plain，text/css，application/xhtml+xml，application/xml
      - 图片文件image/jpeg，image/gif，image/png
      - 视频文件video/mpeg，video/quicktime
      - 应用程序使用的二进制文件application/octet-stream，application/zip
      - 使用q=表示权重值，用；分隔，权重值范围0~1，默认q=1

   2. Accept-Charset：字符集的相对优先级。可用q指定权重。

   3. Accept-Encoding：告知内容编码及编码的优先顺序。可用使用q指定权重值。

   4. Accept-Language：告知能够处理的自然语言集和优先级。可用q指定权重值。

   5. Authorization：告知服务器用户的认证信息。通常在收到401状态码响应后，会把首部字段Authorization加入请求中。

      - ```
        第一次请求响应：
        401 Unauthorized
        WWW-Authenticate: Basic
        
        第二次请求：
        Get /index.htm
        Authorization: Basic xdasd1123fdfaasdasd=
        ```

   6. Except：告诉服务器，期望出现的某种特定行为。

   7. From：告知服务器用户的电子邮件地址。

   8. Host：告知服务器请求的资源所处的互联网主机名和端口号。（必须添加在请求首部）

   9. If-Match，If-xxx：条件请求，只有真才会执行请求。

   10. Max-Forwards：最大转发次数。

   11. Proxy-Authorization：认证发生在客户端与代理之间。

   12. Range：只请求资源的部分。

   13. Referer：告知服务器请求的原始资源URI。

   14. TE：告知服务器客户端能够处理响应的传输编码方式级相对优先级。

   15. User-Agent：将请求的浏览器和用户代理名称等信息传达给服务器。

3. 响应首部字段：

   1. Accept-Ranges：是否可用接受范围请求。可以时为bytes，不能时为none。

   2. Age：告知客户端，源服务器在多久前创建了响应。

   3. Etag：告知实体的标识。

   4. Location：使用Location可以将响应接收方引导至某个与请求URI位置不同的资源。会配合3xx状态码的响应，提供重定向。

      - ```
        第一次请求响应：
        302 Found
        Location: http://www.usagidesign.jp/sample.html
        
        第二次请求：
        GET /sample.html
        ```

   5. Proxy-Authenticate：把代理服务器所要求的认证信息发送给客户端。

   6. Retry-After：多久后再尝试。配合状态码503 Service Unavailable，或者3xx重定向一起使用。

   7. Server：返回服务器信息。

   8. Vary：对缓存进行控制。

   9. WWW-Authenticate：用户HTTP访问认证，告知客户端用于请求URI所指定资源的认证方案。在状态码码401中肯定带有WWW-Authenticate首部。

4. 实体首部字段

   1. Allow：允许的HTTP方法
   2. Content-Encoding：告知客户端服务器对实体主题的编码方式。
   3. Content-Language：实体主题使用的自热语言。
   4. Content-Length：实体主题的大小。
   5. Content-Location：表示报文主题返回资源对应的URI。
   6. Content-MD5：主体的MD5编码
   7. Content-Range：针对范围请求。
   8. Content-Type：主体对象的媒体类型。
   9. Expires：资源失效时间。
   10. Last-Modified：资源最后修改的时间。

5. Cookie首部字段

   1. Set-Cookie：服务器告知客户端的信息。如Set-Cookie: status=enable; expires=Tue, 05 Jul 2011 07:26:31 GMT; Path=/; domain=.hackr.jp;
      - NAME=VALUE：赋予Cookie的名称和值。必须
      - expires=DATE：Cookie有效期
        - 若省略则有效期是会话。
      - path=Path： 将服务器上的文件目录作为Cookie的适用对象
      - domain=域名：作为Cookie适用对象的域名
      - Secure：仅在HTTPS安全通信时才会发送Cookie
      - HttpOnly：加以限制，使Cookie不能被JavaScript脚本访问
   2. Cookie：包含从服务器接收的Cookie。

6. 其他首部字段

   1. X-Frame-Options：防止点击劫持攻击。
   2. X-XSS-Protection：针对跨站脚本攻击（XSS）的一种对策。
   3. DNT：Do Not Track，拒绝个人信息被收集。0表示同意，1表示拒绝。
   4. P3P：隐私保护。

# 7、HTTPS

# 8、认证

1. Basic认证：不常用

2. Digest认证：

3. SSL认证：使用HTTPS完成认证。

   - 使用双因素认证：SSL通信+用户名密码

4. 表单认证：

   - ```
     客户端登陆：
     发送账号密码
     
     服务器响应：
     cookie
     
     客户端请求：
     带cookie的请求
     ```

# 9、基于HTTP的协议

1. WebSocket

   - ```
     握手请求：
     使用Upgrade: websocket表示通信协议发生变化
     Sec-WebSocket-Key记录握手过程必不可少的键值
     Sec-WebSocket-Protocol记录使用的子协议
     
     握手响应：
     然后状态码101 Swiching Protocols响应
     HTTP/1.1 101 Switching Protocols
     Upgrade: websocket
     Connection: Upgrade
     Sec-WebSocket-Accept:
     Sec-WebSocket-Protocol: chat
     
     全双工通信：
     WebSocket API
     var socket = new WebSocket("ws://game.example.com:12010/updates")
     socket.send("xxxx")
     ```

2. 