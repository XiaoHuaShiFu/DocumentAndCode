```
mvn clean package
依次执行clean、resources、compile、testResources、testCompile、test、jar(打包)等７个阶段。
```



```
mvn clean install
依次执行clean、resources、compile、testResources、testCompile、test、jar(打包)、install等8个阶段。
```



```
mvn clean deploy
依次执行clean、resources、compile、testResources、testCompile、test、jar(打包)、install、deploy等９个阶段。
```

***1\***|***2\*****区别**

package 命令完成了项目编译、单元测试、打包功能，但没有把打好的可执行jar包（war包或其它形式的包）布署到本地maven仓库和远程maven私服仓库.

install 命令完成了项目编译、单元测试、打包功能，同时把打好的可执行jar包（war包或其它形式的包）布署到本地maven仓库，但没有布署到远程maven私服仓库.

deploy 命令完成了项目编译、单元测试、打包功能，同时把打好的可执行jar包（war包或其它形式的包）布署到本地maven仓库和远程maven私服仓库.