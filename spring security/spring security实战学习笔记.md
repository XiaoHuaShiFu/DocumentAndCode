# 2、表单认证

1. 默认访问的页面资源是在resources/static/目录下

2. # [springboot配置spring security 静态资源不能访问](https://www.cnblogs.com/zouhong/p/11964232.html)

   在springboot整合spring security 过程中曾遇到下面问题：（spring boot 2.0以上版本  spring security 5.x   (spring security 4.x不知道是否会存在以下问题) ）

   ![img](https://img2018.cnblogs.com/i-beta/1436863/201911/1436863-20191130215432897-2110954096.png)

    

    

    springsecurity会自动屏蔽我们引用的css,js等静态资源，导致页面不能加载出该有的样式。
   应在继承了WebSecurityConfigurerAdapter类中的configure方法中添加允许加载的配置

   ![img](https://img2018.cnblogs.com/i-beta/1436863/201911/1436863-20191130215635071-1757022627.png)或

    

    

    

    ![img](https://img2018.cnblogs.com/i-beta/1436863/201911/1436863-20191130215917511-1632712692.png)

    

    

    

3. 