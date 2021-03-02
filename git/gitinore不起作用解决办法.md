# [gitignore 不起作用的解决办法](https://www.cnblogs.com/sumg/p/10251247.html)

 

**解决办法**

```
git rm -r --cached .
git add .
git commit -m 'update .gitignore'   //windows 使用的命令是  git commit -m "update .gitignore"  需要使用双引号
```

.gitignore文件,具体的规则一搜就有.我在使用GIT的过程中,明明写好了规则,但问题不起作用,每次还是重复提交,无法忍受.其实这个文件里的规则对已经追踪的文件是没有效果的.所以我们需要使用rm命令清除一下相关的缓存内容.这样文件将以未追踪的形式出现.然后再重新添加提交一下,.gitignore文件里的规则就可以起作用了.

 