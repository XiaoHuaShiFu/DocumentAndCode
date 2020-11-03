# Spring异常：junit:no runnable methods 解决方法

![img](https://csdnimg.cn/release/blogv2/dist/pc/img/original.png)

[lansexiuzhifu](https://me.csdn.net/lansexiuzhifu) 2018-08-04 18:02:34 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/articleReadEyes.png) 24039 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/tobarCollect.png) 收藏 11

分类专栏： [bug消除](https://blog.csdn.net/lansexiuzhifu/category_7879807.html) 文章标签： [Spring异常](https://www.csdn.net/gather_22/MtzaAgxsNjU2OTUtYmxvZwO0O0OO0O0O.html) [Junit](https://www.csdn.net/gather_21/MtTaEg0sMjUyNjMtYmxvZwO0O0OO0O0O.html) [bug消除](https://so.csdn.net/so/search/s.do?q=bug消除&t=blog&o=vip&s=&l=&f=&viparticle=)

版权

# **BUG消除者(3):**

## Spring的Junit异常：

###     junit:no runnable methods

## 尝试解决方案:

***1.导错了包:***@Test时import的是@org.testng.annotations.Test   所以会报错

   ***解决方法:***改为import org.junit.Test;

​      **2.忘记在方法前面加入@Test注解**

  **解决方法:**使用自动提示引入import org.junit.Test;或将类设置成abstract

​      3.所测试的方法加上了输入参数,  系统信息会报:Method testAdd should have no parameters,IDEA说的很明确,测试的方法不能        有输入参数,这个虽然不算,解决  junit:no runnable methods异常,但可能是junit:no runnable methods的衍生错误

  解决方法:所测方法去掉输入参数,并且要求方法无返回值.

​      4.可能 json-lib-2.4.jar丢失，重新拷一个到目标路径下,笔者还没有遇到过这种情况.