# jwt的组成部分

[![img](https://cdn2.jianshu.io/assets/default_avatar/12-aeeea4bedf10f2a12c0d50d626951489.jpg)](https://www.jianshu.com/u/840c2d8eff99)

[挡不住的柳Willow](https://www.jianshu.com/u/840c2d8eff99)关注

2017.11.06 15:39:08字数 235阅读 5,150

## 什么是JWT

jwt是信息加密的一种方式，一个JWT由三个部分组成：header，payload，signature。分别保存了不同的信息。三个部分在JWT中分别对应英文句号分割出来的三个串：

![img](https://upload-images.jianshu.io/upload_images/5000473-e9ad191a47b13e28.png?imageMogr2/auto-orient/strip|imageView2/2/w/503/format/webp)

image.png

### header

header部分由以下的json结构生成：

![img](https://upload-images.jianshu.io/upload_images/5000473-f892a100dec395f6.png?imageMogr2/auto-orient/strip|imageView2/2/w/118/format/webp)

image.png



typ用来标识整个token是一个jwt字符串，alg代表签名和摘要算法，一般签发JWT的时候，只要typ和alg就够了，生成方式是将header部分的json字符串经过Base64Url编码：

![img](https://upload-images.jianshu.io/upload_images/5000473-166f479fbdb63fb6.png?imageMogr2/auto-orient/strip|imageView2/2/w/401/format/webp)

image.png

### playload部分：

![img](https://upload-images.jianshu.io/upload_images/5000473-2edff0bebda9a7bb.png?imageMogr2/auto-orient/strip|imageView2/2/w/192/format/webp)

image.png



playload用来承载要传递的数据，它的一个属性对被称为claim，这样的标准成为claims标准，同样是将其用Base64Url编码

### signature

signature部分是将前两个部分的json拼接中间加一点，再将这个拼接后的字符串用alg中的算法处理



![img](https://upload-images.jianshu.io/upload_images/5000473-db2ecd1c448b8920.png?imageMogr2/auto-orient/strip|imageView2/2/w/278/format/webp)

image.png