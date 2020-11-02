# [Maven [ERROR\] 不再支持源选项 5。请使用 6 或更高版本](https://www.cnblogs.com/williamjie/p/11306768.html)

## 报错信息如下

![img](https://img2018.cnblogs.com/blog/1334967/201903/1334967-20190323235257533-1559215029.png)

## 解决办法一 在settings.xml文件中指定jdk版本

既可以修改全局的settings.xml文件（C:\Program Files\apache-maven-3.6.0\conf\settings.xml）

也可以修改用户的settings.xml文件（~\.m2\settings.xml）

在settings.xml文件中找到<profiles>标签，在里面新建一个字标签<profile> 在里面指定jdk版本

我的jdk版本是10.0.2 所以写的是10 根据你自己的jdk版本写 1.7/1.8~~~~ 

```
<profile> ``   ``<id>jdk-10</id> ``   ``<activation> ``     ``<activeByDefault>``true``</activeByDefault> ``     ``<jdk>10</jdk> ``   ``</activation>``   ``<properties>``     ``<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>``     ``<maven.compiler.source>10</maven.compiler.source> ``     ``<maven.compiler.target>10</maven.compiler.target> ``   ``</properties> ``</profile>
```

 

## 解决办法二 在项目的pom.xml文件中指定jdk版本

我的jdk版本是10.0.2 所以写的是10 根据你自己的jdk版本写 1.7/1.8~~~~ 

<properties>元素时根元素<project>的子元素

```
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>10</maven.compiler.source>
    <maven.compiler.target>10</maven.compiler.target>
</properties>
```

测试成功

![img](https://img2018.cnblogs.com/blog/1334967/201903/1334967-20190323235610567-941005743.png)

 