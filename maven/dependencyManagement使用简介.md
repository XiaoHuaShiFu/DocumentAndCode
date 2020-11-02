# dependencyManagement使用简介



## dependencyManagement使用简介

Maven中的dependencyManagement元素提供了一种管理依赖版本号的方式。在dependencyManagement元素中声明所依赖的jar包的版本号等信息，那么所有子项目再次引入此依赖jar包时则无需显式的列出版本号。Maven会沿着父子层级向上寻找拥有dependencyManagement 元素的项目，然后使用它指定的版本号。

## 举例

在父项目的POM.xml中配置：

```
<dependencyManagement>



        <dependencies>



            <dependency>



                <groupId>org.springframework.boot</groupId>



                <artifactId>spring-boot-starter-web</artifactId>



                <version>1.2.3.RELEASE</version>



            </dependency>



        </dependencies>



    </dependencyManagement>
```

此配置即生命了spring-boot的版本信息。

子项目则无需指定版本信息：

```
<dependency>



    <groupId>org.springframework.boot</groupId>



    <artifactId>spring-boot-starter-web</artifactId>



</dependency>
```

## 使用优点

如果有多个子项目都引用同一样依赖，则可以避免在每个使用的子项目里都声明一个版本号。当想升级或切换到另一个版本时，只需要在顶层父容器里更新，而不需要逐个修改子项目；另外如果某个子项目需要另外的一个版本，只需要声明version即可。

## 注意事项

dependencyManagement中定义的只是依赖的声明，并不实现引入，因此子项目需要显式的声明需要用的依赖。