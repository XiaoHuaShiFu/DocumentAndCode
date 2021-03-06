# 命令

| 命令                   | 作用                                  |
| ---------------------- | ------------------------------------- |
| mvn clean compile      | 编译项目，clean是清楚target目录的内容 |
| mvn clean test         | 测试项目                              |
| mvn clean package      | 打包项目                              |
| mvn clean install      | 安装项目                              |
| mvn clean deploy       | 部署项目                              |
| mvn archetype:generate | 生成项目骨架                          |
| mvn dependency:list    | 列出项目已解析依赖                    |
| mvn dependency:tree    | 列出项目的依赖树                      |
| mvn dependency:analyze | 检查项目依赖的问题                    |
|                        |                                       |

# 命令行参数

1. mvn clean install -U：-U强行让maven检查更新
2. mvn archetype:generate -DarchetypeCatalog=internal：让它不要从远程服务器上取catalog，不然会出现卡住的情况
3. -Dmaven.test.skip=true：跳过测试



# 2、Maven安装与配置

1. 设置MAVEN_OPTS环境变量：因为Java默认的最大可用内存往往不能满足Maven运行的需要，容易OOM

   - ```
     -Xms128m -Xmx512m 
     ```

2. 配置文件放在~/.m2/settings.xml：这个配置只作用在当前系统用户的范围内，且是推荐配置。

# 3、Maven入门

1. 要生成可执行的jar包，需要添加maven-shade-plugin
   - 汇总target下的artifactId-version.jar的META-INF/MANIFEST.MF文件下添加Main-Class:main-class-path

# 5、坐标与依赖

1. 依赖范围

   - | 依赖范围scope | 编译有效 | 测试有效 | 运行有效 | 例子                            |
     | ------------- | -------- | -------- | -------- | ------------------------------- |
     | compile       | Y        | Y        | Y        | spring-core                     |
     | test          |          | Y        |          | Junit                           |
     | provided      | Y        | Y        |          | servlet-api                     |
     | runtime       |          | Y        | Y        | JDBC驱动实现                    |
     | system        | Y        | Y        |          | 本地的，Maven仓库之外的类库文件 |
     | import        |          |          |          | 导入依赖范围                    |

# 8、聚合与继承

1. 需要用父类来管理的属性
   - groupId、version、artifactId前缀
   - 

# 9、Nexus搭建私服

1. https://www.cnblogs.com/shiyh/p/11956785.html
2. 

# 10、Maven测试

1. maven-surefire-plugin插件自动识别src/test/java目录下的所有：\*\*/Test\*.java，\*\*/\*Test.java，\*\*/\*TestCase.java类进行测试
2. 跳过测试：mvn package -DskipTests
3. 跳过测试和测试代码的编译：mvn package -Dmaven.test.skip=true
4. 动态指定运行的测试用例：mvn test -Dtest=RandomGeneratorTest, Account*Test
   - 即使没有任何测试也不报错：-DfailIfNoTests=false
5. 测试报告：
   - 测试完会在项目的target/surefire-reports目录下生成两个错误报告文件
   - 使用mvn cobertura:cobertura可以生成测试覆盖率报告：报告在项目target/site/cobertura下的index.html文件