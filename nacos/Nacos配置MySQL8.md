# Nacos配置MySQL8

![img](https://csdnimg.cn/release/blogv2/dist/pc/img/original.png)

[whisht](https://me.csdn.net/ljz9425) 2020-03-31 19:00:18 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/articleReadEyes.png) 2103 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/tobarCollect.png) 收藏 1

分类专栏： [Linux](https://blog.csdn.net/ljz9425/category_1136111.html) [JAVA](https://blog.csdn.net/ljz9425/category_194128.html)

版权

1、MySQL创建数据库nacos_config

2、选中数据库nacos_config，执行Nacos自带的创建表脚本https://github.com/alibaba/nacos/blob/master/distribution/conf/nacos-mysql.sql或nacos安装目录下conf/nacos-mysql.sql

3、下载源码 https://github.com/alibaba/nacos/tree/1.2.0

4、修改根pom.xml

![img](https://img-blog.csdnimg.cn/2020033118553587.png)

5、修改com.alibaba.nacos.naming.healthcheck.MysqlHealthCheckProcessor

![img](https://img-blog.csdnimg.cn/20200331185644754.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xqejk0MjU=,size_16,color_FFFFFF,t_70)

6、打包console工程

7、将console\target\nacos-server.jar替换部署nacos/target下的jar文件

8、修改nacos配置文件，db.url中添加serverTimezone=GMT%2B8

![img](https://img-blog.csdnimg.cn/20200331185917263.png)