# 13、统一数据访问异常层次结构

1. DAO模式的背景

   - DAO（Data Access Object，数据访问对象）
   - RDBMS（关系数据库）
   - LDAP（Lightweight Directory Access Protocol，轻量级目录访问协议）

2. 略

3. 略

4. Spring异常体系

   - 以DataAccessException为顶层接口，再根据职能划分不同的异常子类型。

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/Spring%E5%BC%82%E5%B8%B8%E5%B1%82%E6%AC%A1%E4%BD%93%E7%B3%BB%E7%AE%80%E5%9B%BE.jpg?raw=true)

   - CleanupFailureDataAccessException：当完成数据访问操作，对资源的清除却失败时，抛出该异常。如关闭数据库连接时出现SQLException。

   - DataAccessResourceFailureException：在无法访问相应的数据资源时抛出。如数据库服务器挂了。

   - DataSourceLookupFailureException：尝试对JNDI（Java Naming and Directory Interface，Java命名和目录接口）服务或其他位置上的DataSource进行查找，却查找失败时抛出。

   - ConcurrencyFailureException：进行并发数据访问操作失败时抛出。如无法获得相应的数据库锁，或乐观锁更新冲突时。

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/ConcurrencyFailureException%E7%9B%B8%E5%85%B3%E5%B1%82%E6%AC%A1%E5%85%B3%E7%B3%BB%E5%9B%BE.jpg?raw=true)

     - OptimisticLockingFailureException乐观锁冲突。
     - PesimisticLockingFailureException悲观锁冲突。
     - 等。。。

   - InvalidDataAccessApiUsageException：以错误的方式，使用了特定的数据访问API。如应该返回单行结果，却返回多行结果。

   - InvalidDataAccessResourceUsageException：以错误的方式访问数据资源。如错误的SQL。

     - 基于JDBC的数据访问会抛出其子类BadSqlGrammarException。
     - 基于Hibernate的数据访问会抛出HibernateQueryException。

   - DataRetrievalFailureException：在要获取预期数据却失败时。

   - PermissionDeniedDataAccessException：访问某些数据，却没有权限时。

   - DataIntegrityViolationException：数据一致性冲突。

     - 可以catch后再尝试新的更新操作。

   - UncategorizedDataAccessException：其他类型的异常。可以扩展。

# 14、JDBC API

1. 基于Template的JDBC使用方式

   - 以JdbcTemplate为顶层抽象。

     ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/JdbcTemplate%E7%BB%A7%E6%89%BF%E5%B1%82%E6%AC%A1.jpg?raw=true)

     - 以统一格式和规范使用JDBC API。

     - 对SQLException的异常进行统一转移，纳入Spring自身的异常体系。

     - Callback与模板方法的关系可以看作是服务与被服务的关系，Callback做事，模板方法提供资源。

     - JdbcTemplate继承层次

       - JdbcOperations接口定义了JdbcTemplate可以使用的JDBC操作集合。

       - JdbcTemplate的直接父类是JdbcAccessor，为JdbcTemplate提供一些公用的属性。

         - DataSource
         - SQLExceptionTranslator：JdbcTemplate委托其对SQLException进行转译。

       - JdbcTemplate的各种模板方法的API根据自由度大小，可以分为：

         - 面向Connection的模板方法：通过ConnectionCallback回调接口所公开的Connection进行数据访问。
         - 面向Statement的模板方法：通过StatementCallback回调接口所公开的Statement进行数据访问。
         - 面向PreparedStatement的模板方法：可以避免SQL注入攻击。使用PreparedStatement之前，需要根据传入的包含参数的SQL对其进行创建。通过PreparedStatementCreator回调接口公开Connection以运行PreparedStatment的创建。PreparedStatement创建之后，会公开给PreparedStatementCallback回调接口，以支持其PreparedStatement进行数据访问。
         - 面向CallableStatement的模板方法：进行数据库存储过程的访问。通过CallableStatementCreator公开相应的Connection以便创建用于调用存储过程的CallableStatement，再通过CallableStatementCallback公开创建一个CallableStatment操作句柄，是西安基于存储过程的数据访问。

       - 使用DataSourceUtils进行Connection管理：DataSourceUtils会将取得的Connection绑定到当前线程，以便再使用Spring提供的统一事务抽象层进行事务管理的时候使用。

       - 使用NativeJdbcExtractor来获取数据库驱动所提供的原始API。

         - 可以通过JdbcTemplate的setNativeJdbcExtractor来设置相应的NativeJdbcExtractor实现类。
         - Spring默认提供Commons DBCP、C3P0、Weblogic、WebSphere等数据源的NativeJdbcExtractor实现类。

       - 控制JdbcTemplate的行为：使用applyStatementSettings(stmt|ps|cs)

         - 可以控制每次获得的最大结果集，超时时间等。

         - 示例：

           ```java
           JdbcTemplate jdbcTemplate = new JdbcTemplate();
           jdbcTemplate.setFetchSize(100);
           ```

       - SQLException到DataAccessException的转译

         ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/SQLExceptionTranslator%E4%BD%93%E7%B3%BB.png?raw=true)

         - SQLErrorCodeSQLExceptionTranslator会基于SQLException所返回的ErrorCode进行异常转译。JdbcTemplate默认情况下采用这方法。

         - SQLStateSQLExceptionTranslator根据SQlException.getSQLState()所返回的信息进行异常转译。但个数据库厂商在执行上存在差异。

         - SQLErrorCodeSQLExceptionTranslator的异常转译流程：

           - 先检查customTranslate(task,sql,sqlEx)能否进行转译，如果返回null则进行下一步。

           - 尝试让SQLExceptionSubclassTranslator对异常进行转译。

           - SQLErrorCodeFactory所加载的SQLErrorCodes进行转译：

             - 加载位于Spring发布jar包中org/springframework/jdbc/support/sql-error-codes.xml记载的各个数据库提供商ErrorCode的配置文件，提取相应的SQLErrorCodes。
             - 加载Classpath根路径下的名称为sql-error-codes.xml配置文件，则加载该文件内容，并覆盖默认的ErrorCode定义。

           - 求助于SQLStateSQLExceptionTranslator。

           - 我们可以继承SQLErrorCodeSQLExceptionTranslator覆盖它的customTranslate方法，然后用这个子类代替SQLErrorCodeSQLExceptionTranslator。

             ```java
             DataSource dataSource = new DriverManagerDataSource();
             JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
             
             SQLErrorCodeSQLExceptionTranslator exTranslator = new ToySqlExceptionTranslator();
             exTranslator.setDataSource(dataSource);
             jdbcTemplate.setExceptionTranslator(exTranslator);
             ```

           - 我们也可以提供一个sql-error-codes.xml配置文件，格式与org/springframework/jdbc/support/sql-error-codes.xml相同。

   - JdbcTemplate与它的兄弟们

     - 使用JdbcTempate进行数据访问

       - 初始化JdbcTemplate：只需传入DataSource，可以在配置文件中配置。

         ```xml
         <!-- 配置destroy-method使在应用退出后数据库连接可以关闭 -->
         <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
             <property name="url" value="123.231.33.33"/>
             <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
             <property name="username" value="username"/>
             <property name="password" value="password"/>
         </bean>
         
         <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
             <property name="dataSource" ref="dataSource"/>
         </bean>
         ```

       - 基于JdbcTemplate的数据访问

         - 使用JdbcTemplate查询数据
         
           - 自带方法
         
             ```java
             JdbcTemplate jdbcTemplate = (JdbcTemplate) ctx.getBean("jdbcTemplate");
             Integer rowCount = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM user");
             Map result = jdbcTemplate.queryForMap("SELECT * FROM user WHERE id = 1");
             ```
         
           - 使用回调接口
         
             - ResultSetExtracor
         
               ```java
               List<User> userList = jdbcTemplate.query("SELECT * FROM user", (rs) -> {
                       List<User> list = new ArrayList<>();
                       while (rs.next()) {
                           User user1 = new User();
                           user1.setId(rs.getInt(1));
                           user1.setUsername(rs.getString(2));
                           user1.setPassword("12344");
                           list.add(user1);
                       }
                       return list;
               });
               ```
         
             - RowCallBackHandler：同RowMapper，内部使用RowCallbackHandlerResultSetExtractor来实现循环。
         
               ```java
               List<User> userList = new ArrayList<>();
               jdbcTemplate.query("SELECT * FROM user", (rs) -> {
                   User user1 = new User();
                   user1.setId(rs.getInt(1));
                   user1.setUsername(rs.getString(2));
                   user1.setPassword("12344");
                   userList.add(user1);
               });
               ```
         
             - RowMapper：JdbcTemplate内部会使用一个ResultSetExtractor实现类来做其余工作。使用的ResultSetExtractor实现类为RowMapperResultSetExtractor，单行数据处理会委托给传入的RowMapper实例。
         
               ```java
               List<User> userList = jdbcTemplate.query("SELECT * FROM user", (rs, rowNumber) -> {
                   User user1 = new User();
                   user1.setId(rs.getInt(1));
                   user1.setUsername(rs.getString(2));
                   user1.setPassword("12344");
                   return user1;
               });
               ```
         
         - 基于JdbcTemplate的数据更新
         
           - 自带方法
         
             ```java
             jdbcTemplate.update("insert into user(nick_name, openid, avatar_url) values ('xhsf', '22222', '333333333')");
             ```
         
             ```java
             jdbcTemplate.update("insert into user(nick_name, openid, avatar_url) values (?,?,?)", new Object[] {"xhsf", "22222", "333333333"});
             ```
         
           - 使用PreoaredStatement相关的Callback接口
         
             - 使用PreparedStatementSetter
         
               ```java
               jdbcTemplate.update("insert into user(nick_name, openid, avatar_url) values (?,?,?)", (ps) -> {
                   ps.setString(1, "dddd");
                   ps.setString(2, "cccc");
                   ps.setString(3, "zzz");
               });
               ```
         
             - 使用PreparedStatementCreator
         
               ```java
               jdbcTemplate.update((con) -> {
                   PreparedStatement ps = con.prepareStatement("insert into user(nick_name, openid, avatar_url) values (?,?,?)");
                   ps.setString(1, "dddd");
                   ps.setString(2, "cccc");
                   ps.setString(3, "zzz");
                   return ps;
               });
               ```
         
             - 如果不需要返回值可以使用jdbcTemplate的execute方法，使用方式同上。
         
         - 批量更新：执行之前会先检查JDBC驱动是否支持批量更新操作，如果不支持则单独执行每一笔更新。
         
           - 使用BatchPreparedStatementSetter
         
             ```java
             List<User> updateList = userList;
             jdbcTemplate.batchUpdate("UPDATE user SET password = ?", new BatchPreparedStatementSetter() {
                 @Override
                 public void setValues(PreparedStatement ps, int i) throws SQLException {
                     ps.setString(1, "123457");
                 }
                 //进行批量更新的行数
                 @Override
                 public int getBatchSize() {
                     return updateList.size();
                 }
             });
             ```
         
           - 相对于直接使用jdbc会有微笑的性能损失，但是在极端情况下（每个事务100万更新），Spring的批量更新可以取得很好的性能。
         
         - 调用存储过程
         
           - 略
         
       - 递增主键生成策略的抽象
       
         - 使用数据库的自增主键策略：可以充分利用数据库的特征及优化措施，但可移植性比较差，数据库过多负担。
       
         - 使用客户端主键生成策略：灵活性好。
       
         - DataFieldMaxValueIncrementer是顶层接口
       
           ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/DataFieldMaxValueIncrementer%E7%BB%A7%E6%89%BF%E5%B1%82%E6%AC%A1%E5%9B%BE.jpg?raw=true)
       
           - 独立于主键表：为每一个数据表单独定义主键表，可以CacheSize减少对数据库的访问。
       
             - 示例：
       
               ```sql
               //使用MyISAM减少事务开销
               DROP TABLE IF EXISTS `user_key`;
               CREATE TABLE `user_key`  (
                 `value` bigint(20) NOT NULL,
                 PRIMARY KEY (`value`) USING BTREE
               ) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;
               ```
       
               ```java
               DataFieldMaxValueIncrementer incrementer =
                       new MySQLMaxValueIncrementer(jdbcTemplate.getDataSource(), "user_key", "value");
               //设置缓存
               ((MySQLMaxValueIncrementer)incrementer).setCacheSize(5);
               //使用
               jdbcTemplate.update(
                       "insert into user(id, nick_name, openid, avatar_url) values (?, ?, ?, ?)",
                       new Object[] {incrementer.nextIntValue(), "xhsf", "22222", "333333333"});
               ```
       
             - 示例：配置到IoC容器
       
               ```xml
               <bean id="incrementer" class="org.springframework.jdbc.support.incrementer.MySQLMaxValueIncrementer">
                   <property name="dataSource" ref="dataSource"/>
                   <property name="incrementerName" value="user_key"/>
                   <property name="columnName" value="value"/>
                   <property name="cacheSize" value="5"/>
               </bean>
               ```
       
             - 还可以作为一个bean注入到需要使用的对象里
       
           - 基于Sequence：如DB2或Oracle。
       
             - 略
       
       - Spring中的LOB类型：通常分为BLOB（二进制）和CLOB（文本）两种。
       
         ![](https://github.com/XiaoHuaShiFu/img/blob/master/spring%E6%8F%AD%E7%A7%98/LobHandler%E7%BB%A7%E6%89%BF%E5%85%B3%E7%B3%BB%E5%9B%BE.jpg?raw=true)
       
         - 以LobHandler接口作为顶层抽象：带有getLobCreator()方法，也就是一个LobCreator的工厂，LobCreator用于LOB数据的创建。
       
           - DefaultLobHandler使用JDBC API来创建和访问LOB数据
       
             - 示例：使用AbstractLobCreatingPreparedStatementCallback作为回调接口
       
               ```java
               File imageFile =
                       new File("C:\\Users\\lenovo\\Desktop\\Github\\img\\spring揭秘\\AdvisedSupport类层次图.jpg");
               InputStream ins = new FileInputStream(imageFile);
               LobHandler lobHandler = new DefaultLobHandler();
               jdbcTemplate.execute("INSERT INTO images(filename,entity) VALUES(?,?)", new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
                   @Override
                   protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException, DataAccessException {
                       ps.setString(1, imageFile.getName());
                       lobCreator.setBlobAsBinaryStream(ps, 2, ins, (int)imageFile.length());
                   }
               });
               ins.close();
               ```
       
             - 示例：AbstractLobStreamingResultSetExtractor回调接口处理结果
       
               ```java
               LobHandler lobHandler = new DefaultLobHandler();
               File imageFile = new File("C:\\Users\\lenovo\\Desktop\\AdvisedSupport类层次图.jpg");
               OutputStream ous = new FileOutputStream(imageFile);
               jdbcTemplate.query("select * from images", new AbstractLobStreamingResultSetExtractor() {
                   @Override
                   protected void streamData(ResultSet rs) throws SQLException, IOException, DataAccessException {
                       InputStream ins = lobHandler.getBlobAsBinaryStream(rs, 3);
                       IOUtils.write(IOUtils.toByteArray(ins), ous);
                       IOUtils.closeQuietly(ins);
                   }
               });
               IOUtils.closeQuietly(ous);
               ```
       
             - 可以将LobHandler配置到配置文件中
       
     - NamedParameterJdbcTemplate：可以使用有语义的占位符
     
     - SimpleJdbcTemplate：引入动态参数、自动拆箱解箱、泛型
     
   - Spring中的DataSource
   
     - DriverManagerDataSource：每次都会返回一个新的数据库连接。
     - SingleConnectionDataSource：每次都返回同一个数据库连接。
     - 拥有缓冲池的DataSource：如C3P0。
     - 支出分布式事务的DataSource：是XADataSource的实现类，获取到的数据库连接类型是XAConnection。XAconnection扩展自PooledConnection，所以也支持缓冲。
     - DataSource访问方式
       - 本地：只需要将DataSource实现类的jar包加入classpath即可。
       - 远程：通过JNDI方式。
         - 可以通过JndiObjectFactoryBean对DataSource进行访问，这是一个FactoryBean的实现，getObject()返回的是DataSource。
         - 可以通过配置文件配置。
         - 可以使用<jee:jndi-lookup id="dataSource1" jndi-name="java:env/myyDataSource"/>简化配置。
     - 自定义DataSource：扩展AbstractDataSource
       - AbstractRoutingDataSource：可以持有一组DataSource，所以每次getConnection()都会从中选一个进行getConnection()。适合多个数据库的情况。
         - 只需扩展determineCurrentLookupKey()方法，此方法决定getConnection()使用哪一个DataSource。
     - 使用DelegatingDataSource为DataSource添加新行为：它的getConnection()方法等方法委派给内部持有的dataSource实例，可以覆盖其方法添加自定义逻辑。
       - Spring中的DelegatingDataSource实现类
         - UserCredentialsDataSourceAdapter：加入验证信息。
         - TransactionAwareDataSourceProxy：为connection加入Spring的事务管理。
         - LazyConnectionDataSourceProxy：当connection被使用时才从此类持有的DataSource目标对象上获取。
   
   - JdbcDaoSupport：作为所以基于JDBC进行数据访问的DAO实现类的超类，直接继承JdbcDaoSupport就可以。
   
     - 示例：这样就不用为每个类都写上获取JdbcTemplate的代码了
   
       ```java
       public class UserDao extends JdbcDaoSupport implements IUserDao {
       
           public List<User> selectUserList() {
               List<User> userList = new ArrayList<>();
               getJdbcTemplate().query("SELECT * FROM user", (rs) -> {
                   User user1 = new User();
                   user1.setId(rs.getInt(1));
                   user1.setUsername(rs.getString(2));
                   user1.setPassword("12344");
                   userList.add(user1);
               });
               return userList;
           }
       }
       ```