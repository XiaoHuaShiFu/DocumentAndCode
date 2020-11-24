# vue2.0项目中使用sass(含sass基础用法示例)

![img](https://csdnimg.cn/release/blogv2/dist/pc/img/original.png)

[前端李大人](https://me.csdn.net/u014789022) 2019-02-21 11:33:32 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/articleReadEyes.png) 15522 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/tobarCollect.png) 收藏 19

文章标签： [vue sass](https://so.csdn.net/so/search/s.do?q=vue sass&t=blog&o=vip&s=&l=&f=&viparticle=) [sass的使用](https://www.csdn.net/gather_29/MtjaUg0sMTY0OTEtYmxvZwO0O0OO0O0O.html) [vue使用sass](https://www.csdn.net/gather_29/MtjaggxsNzcyNy1ibG9n.html) [sass示例](https://so.csdn.net/so/search/s.do?q=sass示例&t=blog&o=vip&s=&l=&f=&viparticle=) [sass](https://www.csdn.net/gather_25/MtTaEg0sMTAyNjQtYmxvZwO0O0OO0O0O.html)

版权

Sass是一种CSS扩展语言，目前广泛地应用到web项目开发中，使用sass可以用更少的代码去实现CSS同样的样式效果，在书写上也更为简洁，其原理是通过一定的规则转化为对应的CSS样式，可以说是样式中的“高级语言”。Sass在日常的开发中，是需要通过转换工具转成CSS再引入到HTML文件中，然而，在vue的webpack项目模板中，引入解释sass文件的loader，即可使用sass。本文将展示**vue项目使用sass的关键步骤**和**sass的常用语法使用**。

一、**vue项目使用sass**

1、安装依赖包

vue的webpack项目中需要安装上node-sass、sass-loader和style-loader，所以，在项目中，运行一遍：

> npm i node-sass sass-loader style-loader -D

运行以上的命令之后，将在package.json文件中的“devDependencies”属性中看到对应的版本号。

2、配置loader

在项目中的**build目录**找到**webpack.base.conf.js**文件，在该文件module.export中的module.rules加入解释scss文件的loader，具体做法是，在数组中加入一个对象，对象的内容如下：

> {
>
>   test: /\.scss$/,
>
>   loader: 'sass-loader!style-loader!css-loader',
>
> }

3、指定语言为scss

在.vue文件的style标签中加 lang="scss"，写法如下：

![img](https://img-blog.csdnimg.cn/20190221101959366.png)

通过以上的步骤就可以在项目中使用sass。

**二、sass的基本使用**

1、变量的使用

sass中的一个核心就是使用变量，变量的使用总结有以下几点：

（1）以$开头声明变量

​     例：声明一个错误信息的颜色值

![img](https://img-blog.csdnimg.cn/20190221103702239.png)

（2）变量具有作用域的。

（3）变量中连接符中划线和下划线是互通的。

​    例：

![img](https://img-blog.csdnimg.cn/20190221103938373.png)

上面最终color值为red。

2、样式嵌套

sass中的嵌套规则可以减少很多代码的书写，形式是在外层选择器中，嵌入其后代的选择器，例如：在CSS中的div p {}，在sass的形式是div{ p {} }，最终sass编译出的css是div p {}，在复杂的多层样式下，显然用sass会方便很多，举例：

用sass实现：

![img](https://img-blog.csdnimg.cn/20190221105002891.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTQ3ODkwMjI=,size_16,color_FFFFFF,t_70)

用css实现：

![img](https://img-blog.csdnimg.cn/20190221105031425.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTQ3ODkwMjI=,size_16,color_FFFFFF,t_70)

由此可见，用sass会方便很多。

这种嵌套的规则，也可以应用于伪类，在sass用“&”指代了外层的选择器，例如在a标签中，添加hover的效果：

![img](https://img-blog.csdnimg.cn/2019022110535027.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTQ3ODkwMjI=,size_16,color_FFFFFF,t_70)

3、混入样式

混入样式的关键字是mixin和include，这是一个强大的功能，可以类似于函数一般复用代码并通过参数个性化，例如，有一个通用的边框样式：

![img](https://img-blog.csdnimg.cn/20190221110228139.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTQ3ODkwMjI=,size_16,color_FFFFFF,t_70)

以上在类名为test的元素中加入了定义好的边框样式。

使用参数则可以是：

![img](https://img-blog.csdnimg.cn/20190221110507440.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTQ3ODkwMjI=,size_16,color_FFFFFF,t_70)

以上是默认边框颜色是#ccc，在使用box-border时不传入颜色将默认为#ccc，上面传入了一个值为red的颜色值，则最终的颜色值为red。

4、样式继承

样式继承的关键字是extend，使用继承可以把已写好的样式，给其它选择器复用，用继承实现上面的例子：

![img](https://img-blog.csdnimg.cn/20190221111105108.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTQ3ODkwMjI=,size_16,color_FFFFFF,t_70)

上面是一个简单例子，只为说明继承的作用，在.test中继承了.test-border，最终会拷贝.test-border的样式到.test中。

5、导入文件

导入文件的关键字是import，在sass中可以导入sass文件,也可以导入css文件，省略文件名时，默认导入sass文件。导入时语法如下：

> @import "xxx.sass"

**三、总结**

sass可以使书写样式代码更方便快捷，所以建议在项目中使用，本文列举了在vue项目中使用的关键步骤，并用简单的示例演示了sass的基础使用，要了解sass更详尽的用法和特性，建议到官网上查看文档。