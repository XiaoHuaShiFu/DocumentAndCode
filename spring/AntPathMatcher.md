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