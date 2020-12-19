| 状态码 | 状态码英文名称                  | 中文描述                                                     |
| :----- | :------------------------------ | :----------------------------------------------------------- |
| 100    | Continue                        | 继续。[客户端](http://www.dreamdu.com/webbuild/client_vs_server/)应继续其请求 |
| 101    | Switching Protocols             | 切换协议。服务器根据客户端的请求切换协议。只能切换到更高级的协议，例如，切换到HTTP的新版本协议 |
|        |                                 |                                                              |
| 200    | OK                              | 请求成功。一般用于GET与POST请求                              |
| 201    | Created                         | 已创建。成功请求并创建了新的资源                             |
| 202    | Accepted                        | 已接受。已经接受请求，但未处理完成                           |
| 203    | Non-Authoritative Information   | 非授权信息。请求成功。但返回的meta信息不在原始的服务器，而是一个副本 |
| 204    | No Content                      | 无内容。服务器成功处理，但未返回内容。在未更新网页的情况下，可确保浏览器继续显示当前文档 |
| 205    | Reset Content                   | 重置内容。服务器处理成功，用户终端（例如：浏览器）应重置文档视图。可通过此返回码清除浏览器的表单域 |
| 206    | Partial Content                 | 部分内容。服务器成功处理了部分GET请求                        |
|        |                                 |                                                              |
| 300    | Multiple Choices                | 多种选择。请求的资源可包括多个位置，相应可返回一个资源特征与地址的列表用于用户终端（例如：浏览器）选择 |
| 301    | Moved Permanently               | 永久移动。请求的资源已被永久的移动到新URI，返回信息会包括新的URI，浏览器会自动定向到新URI。今后任何新的请求都应使用新的URI代替 |
| 302    | Found                           | 临时移动。与301类似。但资源只是临时被移动。客户端应继续使用原有URI |
| 303    | See Other                       | 查看其它地址。与301类似。使用GET和POST请求查看               |
| 304    | Not Modified                    | 未修改。所请求的资源未修改，服务器返回此状态码时，不会返回任何资源。客户端通常会缓存访问过的资源，通过提供一个头信息指出客户端希望只返回在指定日期之后修改的资源 |
| 305    | Use Proxy                       | 使用代理。所请求的资源必须通过代理访问                       |
| 306    | Unused                          | 已经被废弃的HTTP状态码                                       |
| 307    | Temporary Redirect              | 临时重定向。与302类似。使用GET请求重定向                     |
| 308    | Permanent Redirect              | 重定向状态响应代码指示所请求的资源已明确移动到`Location`标题给定的 URL 。浏览器重定向到这个页面，搜索引擎更新它们到资源的链接（在 SEO 中，据说链接汁被发送到新的 URL）。<br />请求方法和主体不会被更改，`301`但有时可能会被错误地更改为[`GET`](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/GET)方法。 |
|        |                                 |                                                              |
| 400    | Bad Request                     | 客户端请求的语法错误，服务器无法理解                         |
| 401    | Unauthorized                    | 请求要求用户的身份认证                                       |
| 402    | Payment Required                | 保留，将来使用                                               |
| 403    | Forbidden                       | 服务器理解请求客户端的请求，但是拒绝执行此请求               |
| 404    | Not Found                       | 服务器无法根据客户端的请求找到资源（网页）。通过此代码，网站设计人员可设置"您所请求的资源无法找到"的个性页面 |
| 405    | Method Not Allowed              | 客户端请求中的方法被禁止                                     |
| 406    | Not Acceptable                  | 服务器无法根据客户端请求的内容特性完成请求                   |
| 407    | Proxy Authentication Required   | 请求要求代理的身份认证，与401类似，但请求者应当使用代理进行授权 |
| 408    | Request Time-out                | 服务器等待客户端发送的请求时间过长，超时                     |
| 409    | Conflict                        | 服务器完成客户端的 PUT 请求时可能返回此代码，服务器处理请求时发生了冲突 |
| 410    | Gone                            | 客户端请求的资源已经不存在。410不同于404，如果资源以前有现在被永久删除了可使用410代码，网站设计人员可通过301代码指定资源的新位置 |
| 411    | Length Required                 | 服务器无法处理客户端发送的不带Content-Length的请求信息       |
| 412    | Precondition Failed             | 客户端请求信息的先决条件错误                                 |
| 413    | Request Entity Too Large        | 由于请求的实体过大，服务器无法处理，因此拒绝请求。为防止客户端的连续请求，服务器可能会关闭连接。如果只是服务器暂时无法处理，则会包含一个Retry-After的响应信息 |
| 414    | Request-URI Too Large           | 请求的URI过长（URI通常为网址），服务器无法处理               |
| 415    | Unsupported Media Type          | 服务器无法处理请求附带的媒体格式                             |
| 416    | Requested range not satisfiable | 客户端请求的范围无效                                         |
| 417    | Expectation Failed              | 服务器无法满足Expect的请求头信息                             |
| 426    | Upgrade Required                | HTTP **`426 Upgrade Required`**客户端错误响应代码指示服务器拒绝使用当前协议执行请求，但可能在客户端升级到其他协议后愿意这样做。<br />服务器发送一个[`Upgrade`](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Upgrade)包含此响应的头部以指示所需的协议。 |
| 428    | Precondition Required           | HTTP **`428 Precondition Required`**响应状态码指示服务器要求有条件的请求。通常，这意味着所需的先决条件头，如`If-Match`，**缺少**。当前提条件头与服务器端状态**不匹配时**，响应应该是`412` `Precondition Failed`。 |
| 429    | Too Many Requests               | HTTP **`429 Too Many Requests`**响应状态码指示用户在给定时间内发送了太多请求（“限速”）。<br />一个`Retry-After`标题可能包括该回复表示多久作出新的请求前等待。 |
| 431    | Request Header Fields Too Large | HTTP **`431 Request Header Fields Too Large`**响应状态码指示服务器不愿意处理请求，因为它的头部字段太大。请求可以在减少请求头域的大小后重新提交。<br />它可以在请求头字段的总数太大或单个头字段太大时使用。<br />这个错误不应该发生在经过良好测试的生产系统上，但在测试新系统时可以更频繁地发现。 |
| 451    | Unavailable For Legal Reasons   | HTTP **`451 Unavailable For Legal Reasons`**客户端错误响应代码指示用户请求由于法律原因而不可用的资源，例如已发出法律行为的网页。 |
|        |                                 |                                                              |
| 500    | Internal Server Error           | 服务器内部错误，无法完成请求                                 |
| 501    | Not Implemented                 | 服务器不支持请求的功能，无法完成请求                         |
| 502    | Bad Gateway                     | 作为网关或者代理工作的服务器尝试执行请求时，从远程服务器接收到了一个无效的响应 |
| 503    | Service Unavailable             | 由于超载或系统维护，服务器暂时的无法处理客户端的请求。延时的长度可包含在服务器的Retry-After头信息中 |
| 504    | Gateway Time-out                | 充当网关或代理的服务器，未及时从远端服务器获取请求           |
| 505    | HTTP Version not supported      | 服务器不支持请求的HTTP协议的版本，无法完成处理               |
| 511    | Network Authentication Required | HTTP **`511 Network Authentication Required`**响应状态码指示客户端需要进行身份验证才能获得网络访问权限。<br />这种状态不是由原始服务器生成的，而是通过拦截代理来控制对网络的访问。<br />网络运营商在授予访问权限之前（例如在网吧或机场）有时需要一些认证，接受条款或其他用户互动。他们通常使用媒体访问控制（[MAC](https://developer.mozilla.org/en-US/docs/Glossary/MAC)）地址识别尚未这样做的客户。 |

```

```

