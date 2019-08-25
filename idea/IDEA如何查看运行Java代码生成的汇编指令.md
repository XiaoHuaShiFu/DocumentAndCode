# IDEA如何查看运行Java代码生成的汇编指令

2019年04月20日 15:22:58

 

睶先生

 

阅读数 509

更多

所属专栏： [IntelliJ IDEA熟能生巧系列](https://blog.csdn.net/column/details/39322.html)





版权声明：本文为博主原创文章，遵循[ CC 4.0 by-sa ](http://creativecommons.org/licenses/by-sa/4.0/)版权协议，转载请附上原文出处链接和本声明。

本文链接：<https://blog.csdn.net/Butterfly_resting/article/details/89417899>

需要下载的工具：[hsdis-amd64.dll](https://pan.baidu.com/s/1pCiiGeGH-6ZNwM3qvc-aVg )    提取码：mdhj 

1）把 hsdis-amd64.dll放在 $JAVA_HOME/jre/bin/server 目录下

2）运行时可添加参数： -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly。

Run-->Edit Configurations

![img](https://img-blog.csdnimg.cn/20190420152110957.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0J1dHRlcmZseV9yZXN0aW5n,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20190420152028579.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0J1dHRlcmZseV9yZXN0aW5n,size_16,color_FFFFFF,t_70)

3)Run即可。