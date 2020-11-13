Spring Security OAuth2的认证流程：

![clipboard.png](https://segmentfault.com/img/bV8sGr?w=1201&h=417)

这个流程当中，切入点不多，集成登录的思路如下：

1. 在进入流程之前先进行拦截，设置集成认证的类型，例如：短信验证码、图片验证码等信息。
2. 在拦截的通知进行预处理，预处理的场景有很多，比如验证短信验证码是否匹配、图片验证码是否匹配、是否是登录IP白名单等处理
3. 在UserDetailService.loadUserByUsername方法中，根据之前设置的集成认证类型去获取用户信息，例如：通过手机号码获取用户、通过微信小程序OPENID获取用户等等

接入这个流程之后，基本上就可以优雅集成第三方登录。