### 基于 JWT 的实践

既然 JWT 依然存在诸多问题，甚至无法满足一些业务上的需求，但是我们依然可以基于 JWT 在实践中进行一些改进，来形成一个折中的方案，毕竟，在用户会话管理场景下，没有银弹。

前面讲的 Token，都是 Access Token，也就是访问资源接口时所需要的 Token，还有另外一种 Token，Refresh Token，通常情况下，Refresh Token 的有效期会比较长，而 Access Token 的有效期比较短，当 Access Token 由于过期而失效时，使用 Refresh Token 就可以获取到新的 Access Token，如果 Refresh Token 也失效了，用户就只能重新登录了。

在 JWT 的实践中，引入 Refresh Token，将会话管理流程改进如下。

- 客户端使用用户名密码进行认证
- 服务端生成有效时间较短的 Access Token（例如 10 分钟），和有效时间较长的 Refresh Token（例如 7 天）
- 客户端访问需要认证的接口时，携带 Access Token
- 如果 Access Token 没有过期，服务端鉴权后返回给客户端需要的数据
- 如果携带 Access Token 访问需要认证的接口时鉴权失败（例如返回 401 错误），则客户端使用 Refresh Token 向刷新接口申请新的 Access Token
- 如果 Refresh Token 没有过期，服务端向客户端下发新的 Access Token
- 客户端使用新的 Access Token 访问需要认证的接口



![img](https://user-gold-cdn.xitu.io/2018/12/13/167a65394a170c1c?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



将生成的 Refresh Token 以及过期时间存储在服务端的数据库中，由于 Refresh Token 不会在客户端请求业务接口时验证，只有在申请新的 Access Token 时才会验证，所以将 Refresh Token 存储在数据库中，不会对业务接口的响应时间造成影响，也不需要像 Session 一样一直保持在内存中以应对大量的请求。

上述的架构，提供了服务端禁用用户 Token 的方式，当用户需要登出或禁用用户时，只需要将服务端的 Refresh Token 禁用或删除，用户就会在 Access Token 过期后，由于无法获取到新的 Access Token 而再也无法访问需要认证的接口。这样的方式虽然会有一定的窗口期（取决于 Access Token 的失效时间），但是结合用户登出时客户端删除 Access Token 的操作，基本上可以适应常规情况下对用户认证鉴权的精度要求。


作者：simpleapples
链接：https://juejin.cn/post/6844903736712626184
来源：掘金
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。