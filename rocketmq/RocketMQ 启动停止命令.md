# [RocketMQ 启动停止命令](https://www.cnblogs.com/dand/p/10183674.html)

1、rocketmq的启动
 进入rocketMQ解压目录下的bin文件夹
启动namesrv服务：nohup sh bin/mqnamesrv & 
日志目录：{rocketMQ解压目录}/logs/rocketmqlogs/namesrv.log

启动broker服务：nohup sh bin/mqbroker &
日志目录：{rocketMQ解压目录}/logs/rocketmqlogs/broker.log
 
 以上的启动日志可以在启动目录下的nohub.out中看到
 

2、rocketmq服务关闭

关闭namesrv服务：sh bin/mqshutdown namesrv

关闭broker服务 ：sh bin/mqshutdown broker

 

sh bin/mqnamesrv &
sh bin/mqbroker -n 10.10.46.32:9876 autoCreateTopicEnable=true -c /opt/rocketmq-all-4.4.0-bin-release/conf/broker.conf &



# RocketMQ 单机部署（单master模式）

![img](https://csdnimg.cn/release/blogv2/dist/pc/img/original.png)

[lumengmeng_csdn](https://blog.csdn.net/lumengmeng_csdn) 2019-02-21 11:28:07 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/articleReadEyes.png) 5092 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/tobarCollect.png) 收藏 9

文章标签： [rocketmq单机部署](https://www.csdn.net/tags/MtTacg0sNDcxMTAtYmxvZwO0O0OO0O0O.html)

版权

## 一、为了快速了解rockmq，先搭建一个简单的单机版的rocketmq，前期准备：

1、CentOS 7.6 64位（阿里云）（4G内存）

2、jdk1.8

3、maven 3.5.4

4、直接从官网上面下载rockmq源码（下载地址：http://rocketmq.apache.org/release_notes/release-notes-4.4.0/）

  ![img](https://img-blog.csdnimg.cn/20190221100121559.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x1bWVuZ21lbmdfY3Nkbg==,size_16,color_FFFFFF,t_70)

 

## 二、部署

 

1、将rocketmq-all-4.4.0-bin-release.zip上传到linux服务器上面/usr/local/rocketmq（路径看自己习惯）

2、在centos上安装unzip工具包，运行命令yum install unzip

3、解压rocketmq-all-4.4.0-bin-release.zip，命令 unzip rocketmq-all-4.4.0-bin-release.zip

![img](https://blog.csdn.net/lumengmeng_csdn/article/details/500)![img](https://img-blog.csdnimg.cn/20190221102053649.png)

4、由于broker启动时，需要占用大量内存，测试时，可以修改配置文件，以减小内存的消耗

 修改bin目录下的runserver.sh文件：

```java
 JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn128m -XX:PermSize=128m -XX:MaxPermSize=320m"
```

同理修改runbroker.sh文件：

```java
JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn128m
```

同理修改tools.sh文件：

```
JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn128m -XX:PermSize=128m -XX:MaxPermSize=128m"
```

5、启动nameserver

```bash
nohup sh bin/mqnamesrv & // 启动
```

通过jps命令查看是否启动成功

![img](https://img-blog.csdnimg.cn/20190221110023835.png)

6、官网的这个启动broker命令特别坑，启动时broker会通过私有ip启动，会导致客户端无法远程连接，所以启动之前我们需要修改一下配置文件，修改如下：

```java
vi ./conf/broker.conf
```

![è¿éåå¾çæè¿°](https://img-blog.csdn.net/20180510102110211?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3pod3lqMTAxOQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

7、启动broker

```java
nohup sh bin/mqbroker -n xxxx:9876 autoCreateTopicEnable=true -c /usr/local/rocketmq/rocketmq-all-4.4.0-bin-release/conf/broker.conf & // 启动broker,xxxx为你的公有ip，或者是localhost也可以，路径根据自己的实际路径
```

通过jps命令查看是否启动成功

![img](https://img-blog.csdnimg.cn/20190221110743104.png)

8、以上rocketmq的服务已经全部起来

## 三、通过控制台连接rocketmq

1、下载rocketmq console，地址：https://github.com/apache/incubator-rocketmq-externals/tree/master/rocketmq-console

下载源码后，找到配置文件application.properties，并按照自己需求进行配置。

例如：rocketmq.config.namesrvAddr=namesrv服务地址（ip1：port;ip2:port）

2、在文件根目录执行命令，进行代码打包

在打包之前要修改pom文件，否则会报找不到对应版本的jar，不知道为啥没有官网提供的版本

![img](https://img-blog.csdnimg.cn/20190221112236925.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x1bWVuZ21lbmdfY3Nkbg==,size_16,color_FFFFFF,t_70)

mvn clean package -Dmaven.test.skip=true

然后在target目录下找到文件rocketmq-console-ng-1.0.0.jar，上传到centos上

3、启动rocketmq-console，执行命令：

java -jar rocketmq-console-ng-1.0.0.jar

通过jps命令查看是否成功

![img](https://img-blog.csdnimg.cn/20190221112431271.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x1bWVuZ21lbmdfY3Nkbg==,size_16,color_FFFFFF,t_70)

4.通过浏览器输入[http://192.168.162.xxx:8080/](http://192.168.162.235:8080/)回车显示监控界面如下：

![img](https://img-blog.csdnimg.cn/20190221112627398.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x1bWVuZ21lbmdfY3Nkbg==,size_16,color_FFFFFF,t_70)

说明rocketmq console控制台部署成功