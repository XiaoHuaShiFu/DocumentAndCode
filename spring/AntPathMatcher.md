# 前言

AntPathMatcher是什么？主要用来解决什么问题？

背景：在做uri匹配规则发现这个类，根据源码对该类进行分析，它主要用来做类URLs字符串匹配；

# 效果

可以做URLs匹配，规则如下

1. ？匹配一个字符
2. *匹配0个或多个字符
3. **匹配0个或多个目录

用例如下

- /trip/api/*x   匹配 /trip/api/x，/trip/api/ax，/trip/api/abx ；但不匹配 /trip/abc/x；
- /trip/a/a?x   匹配 /trip/a/abx；但不匹配 /trip/a/ax，/trip/a/abcx
- /**/api/alie   匹配 /trip/api/alie，/trip/dax/api/alie；但不匹配 /trip/a/api
- /**/*.htmlm  匹配所有以.htmlm结尾的路径





**Apache Ant样式的路径有三种通配符匹配方法。**

-   ‘?’ 匹配任何单字符
-   ‘*’ 匹配0或者任意数量的字符
-   ‘**’ 匹配0或者更多的目录
-   /app/*.x   匹配(Matches)所有在app路径下的.x文件
-   /app/p?ttern 匹配(Matches) /app/pattern 和 /app/pXttern,但是不包括/app/pttern
-   /**/example  匹配(Matches) /app/example, /app/foo/example, 和 /example
-   /app/**/dir/file. 匹配(Matches) /app/dir/file.jsp, /app/foo/dir/file.html, /app/foo/bar/dir/file.pdf
-   /**/*.jsp   匹配(Matches)任何的.jsp 文件