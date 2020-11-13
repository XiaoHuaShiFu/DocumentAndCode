# Spring Security 中跨 request 请求保持 SecurityContext

![img](https://csdnimg.cn/release/blogv2/dist/pc/img/original.png)

[安迪源文](https://me.csdn.net/andy_zhang2007) 2018-08-10 12:17:37 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/articleReadEyes.png) 2406 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/tobarCollect.png) 收藏 6

分类专栏： [spring](https://blog.csdn.net/andy_zhang2007/category_7283829.html) [Java](https://blog.csdn.net/andy_zhang2007/category_2665929.html) [Spring Security 分析](https://blog.csdn.net/andy_zhang2007/category_9283760.html)

版权

在一个`ServletWeb`应用中，对于一个请求进行处理的全过程，服务端使用的是同一个线程，通过利用`SecurityContextHolder`的`MODE_THREADLOCAL`安全上下文`SecurityContext`保持机制,无论该处理过程中使用到了哪些对象的哪些方法，这些方法内都可以获得当前访问者的安全上下文`SecurityContext`。但是，如果跨多个请求需要保持同样的安全上下文`SecurityContext`,又该怎么办呢？

> 这种需求很常见，同一个用户登录之后会对服务器发起多次请求，每次请求用到的安全上下文`SecurityContext`都应该是一样的。

在`Spring Security`中，跨请求安全上下文`SecurityContext`的保持，是通过`SecurityContextPersistenceFilter`来实现的，缺省情况下，`SecurityContextPersistenceFilter`将安全上下文`SecurityContext`对象保存为当前用户`HttpSession`对象的一个属性，这样，当前用户在同一个会话的多个请求之间，就可以使用同一个安全上下文对象了。

具体来讲，可以理解为如下流程：

- 一个安全上下文在某个请求1处理过程中被创建并记录到`SecurityContextHolder`；
- 请求1的处理结束时，`SecurityContextPersistenceFilter`会将`SecurityContextHolder`中的安全上下文保存到`HttpSession`;
- 后续该用户会话中的另外一个请求2处理过程开始时，`SecurityContextPersistenceFilter`会将安全上下文从`HttpSession`恢复到`SecurityContextHolder`;
- 请求2处理过程结束时，`SecurityContextPersistenceFilter`会将`SecurityContextHolder`中的安全上下文保存到`HttpSession`;
- 后续其他请求的处理过程会重复和上面请求2处理过程中一样的使用`SecurityContextPersistenceFilter`重置/恢复`SecurityContext`的动作。

另外还有一些应用是不使用`Session`机制的，比如无状态的`Restful API`,这类接口需要每次认证。但是对于这种请求的处理过程，`SecurityContextPersistenceFilter`还是会被使用到，它被用来在每个请求处理结束时清除`SecurityContextHolder`中的安全上下文。