# Mysql基础2

# 13.触发器

1. 创建触发器

   ```mysql
   create trigger trigger_name trigger_time trigger_event
   on tbl_name for each row trigger_stmt
   
   其中：
   trigger_time是触发器的触发时间，可以是 before或者 after， before的含义是指在检查约束前触发，而 after是在检查约束后触发。
   trigger_event是触发器触发的事件，可以是 insert、 update或者 delete
   
   触发器的执行顺序是： before insert、 before update、 after update
   ```

   **注：**触发器只能创建在永久表（Permanent Table）上，不能对临时表（Temporary Table）创建触发器。

   **示例：**当向user表插入数据时，也向user3表插入一条数据

   ```mysql
   create 
   trigger ins_user0
   after insert 
   on user for each row 
   begin
   	insert 
   	into user3
       (id, username) 
       values(new.id,new.username);
   end;
   $$
   ```

2. 删除触发器

   ```mysql
   drop trigger [schema_name.] trigger_name;
   ```

3. 查看触发器

   ```mysql
   show triggers \G
   ```

   ```mysql
   desc information_schema.triggers;
   ```

   查看触发器详细信息

   ```mysql
   select * from information_schema.triggers where trigger_name = 'ins_user0' \G
   ```

4. 触发器的限制

   - 触发器程序不能调用将数据返回客户端的存储过程，也不能适用采用call语句的动态sql语句，但是允许存储过程通过参数将数据返回触发程序。也就是存储过程或函数通过out或者inout类型的参数将数据返回触发器是可以的，但是不能调用直接返回数据的过程。
   - 不能再触发器中适用以显示或隐式方式开始或结束事务的语句，如start transaction、commit或rollback。

5. 触发器是按照before触发器、行操作、after触发器的顺序执行的，其中任何异步操作发生错误都不会继续执行剩下的操作。如果对事务表进行操作，那么会整个作为一个事件被回滚（Rollback），但是如果是对非事务表进行操作，那么已经更新的记录将无法回滚。

# 14.事务控制和锁定语句

1. lock table和 unlock table

   锁定和解锁一个表

   ```mysql
   lock tables
   tbl_name [as alias] { read [local]|[low_priority] write}
   [,tbl_name [as alias] { read [local]|[low_priority] write}]...
   unlock tables
   ```

   **示例：**锁定一个表，再查询，再解锁。这样查询的时候，别人就无法更新这个表了。

   ```mysql
   lock table user read;
   select * from user
   unlock tables;
   ```

2. 事务控制

   ```mysql
   //开始一项新的事务
   start transaction | begin [work]  
   //提交事务
   //chain表示在提交或回滚之后，立即启动一个新事物，并且和刚才的事务具有相同的隔离级别。
   //release则会断开和客户端的连接。
   commit [work] [and [no] chain] [[no] release]
   //回滚事务
   rollback [work][and [no] chain] [[no] release]
   //修改当前连接的提交方式，如果设置了0，则所有事务都需要通过明确的命令进行提交或回滚
   set autocommit={0|1}
   ```

   - 以start transaction开始的事务在提交之后自动回到自动提交方式。
   - 如果在提交的时候适用commit and chain，那么会在提交后立即开始一个新事务。（也就是是和commit; start transaction;效果一样）
   - start transaction会导致unlock tables执行

   **示例：**

   ```mysql
   start transaction;
   insert into user(id,username) values(23333,'xhsfxhsf');
   insert into dep (dep_name) values('财务部');
   commit;
   ```

   - savepoint：回滚到某个时间点

     **注：**相同名字的savepoint会覆盖之前的定义。

     可以通过release savepoint 删除不要的point。

   ```mysql
   savepoint point_name;
   release savepoint point_name;
   ```

3. 分布式事务

# 15.SQL安全

1. SQL注入
   - or 1 = 1
   - ;#

# 16.SQL Mode

1. SQL Mode作用

   - 通过设置SQL Mode，可以完成不同严格程度的数据校验，有效的保障数据准确性。
   - 设置SQL Mode为ANSI模式，来保证大多数SQL符合标注SQL语法，确保可迁移。
   - 在数据库之间进行数据迁移之前，通过设置SQL Mode可以使MySQL上的数据更方便地迁移到目标数据库中。

2. 查询默认SQL Mode

   ```mysql
   select @@sql_mode;
   ```

   ```mysql
   ONLY_FULL_GROUP_BY, 
   STRICT_TRANS_TABLES, 严格模式，实现了数据的严格校验，错误数据不能插入表中，不能插入非法日期，也不允许超过字段长度的值插入字段中
   NO_ZERO_IN_DATE,
   NO_ZERO_DATE,
   ERROR_FOR_DIVISION_BY_ZERO, 
   NO_AUTO_CREATE_USER, 
   NO_ENGINE_SUBSTITUTION,
   ANSI, 非法日期可以插入，但是插入值变成0，模0操作会返回null
   TRADITIONAL, 严格模式，非法日期不可以插入，模0操作会直接报错
   NO_BACKSLASH_ESCAPES,使反斜线成为普通字符。
   PIPES_AS_CONCAT,将||视为字符串连接操作符
   ```

3. 修改SQL Mode

   ```mysql
   set [session|global] sql_mode='STRICT_TRANS_TABLES';
   ```

   **注：**session只是在此次连接中生效，global则对新的连接生效，此次连接不生效。

   **示例：**

   ```mysql
   set session sql_mode='STRICT_TRANS_TABLES';
   ```

# 17.MySQL分区

1. 分区优势

   - 可以存储更多数据。
   - 优化查询。
     - 可以在while条件时包含分区条件。
     - 涉及sum()和count()等聚合函数时看并发处理。
   - 可以通过删除分区删除数据。
   - 跨多个磁盘来分散数据查询，获得更大的吞吐量。

2. 分区键：根据某个区间指（或范围值）、特定值列表或者Hash函数值执行数据聚集。

3. MyISAM、InnoDb、Memory支持分区表。

   - 同一个分区表的所有分区必须适用同一个存储引擎。

4. 创建分区表

   ```mysql
   create table emp (
   	empid int,
       salary decimal(7,2),
       birth_date date
   )
   engin=innodb 
   partition by hash(month(birth_date)) 
   partitions 6;
   ```

   ```mysql
   
   ```

   **注：**数据库和表是大小写敏感的,分区的名字是不区分大小写的。

   **如：**是会报错的，因为mypart和Mypart无法被区分。

   ```mysql
   create table t(var int)
   partition by list(var) (
   	partition mypart values in (1,3,5),
       partition Mypart values in (2,4,6)
   );
   ```

5. 删除分区表

   ```mysql
   alter table tbl_name drop partition p_name
   ```

6. 分区的注意点

   - 分区作用于整个表，也就是不能单独只对数据或者索引建立分区。
   - 分区表如果有主键，不能使用主键之外的其他字段分区。否则就必须不要主键。

7. range分区：基于一个给定的连续区间范围，把数据分配到不同的分区。

   - **例如：**根据store_id对range分区，也就是商店0-9的员工保存在p0中，10-19保存在p1中，但是如果store_id大于等于30会报错。

     ```mysql
     create table emp ( 
         id int not null, 
         store_id int not null 
     ) 
     partition by range (store_id) ( 
         partition p0 values less than(10), 
         partition p1 values less than (20) 
     );
     ```

   - **示例1：**可以通过设置partition p_name values less than maxvalue;表示对于大于最大值的都存储在这个分区表中。

     ```mysql
     alter table emp add partition (
     	partition p2 values less than maxvalue
     );
     ```

   - **示例2：**分区表中也可以使用表达式

     ```mysql
     create table emp_date ( 
         id int not null, 
         store_id int not null,
         separated date not null default '9999-12-31'
     ) 
     partition by range (year(separated)) ( 
         partition p0 values less than(1995), 
         partition p1 values less than (2000) 
     );
     ```

   - **示例3：**也可以通过字段来分区（5.5）

     ```mysql
     create table emp_date ( 
         id int not null, 
         store_id int not null,
         separated date not null default '9999-12-31'
     ) 
     partition by range columns (separated) ( 
         partition p0 values less than('1995-01-01'), 
         partition p1 values less than ('2000-01-01') 
     );
     ```

     

   - **注：**在range分区中，分区键如果是null值会被当作一个最小值来处理。

   - range分区适用场景：
     - 当需要删除过期数据时，可以通过删除分区表，来删除数据。这比delete有效很多。
     - 经常运行分区键的查询，比如查询商店id大于等于25的记录，只要查询p2就可以了。

8. list分区：基于枚举出的值列表分区。

   - 创建list分区

     ```mysql
     create table expenses (
     	expense_date date not null,
     	category int	
     ) 
     partition by list (category) (
     	partition p0 values in (3,5),
         partition p1 values in (1,10),
         partition p2 values in (4,9)
     );
     ```

   - **示例：**使用列作为分区键

     ```mysql
     create table expenses0 (
     	expense_date date not null,
     	category varchar(20)
     ) 
     partition by list columns (category) (
     	partition p0 values in ('lodging','food'),
         partition p1 values in ('flights','traval'),
         partition p2 values in ('game','book')
     );
     ```

9. columns分区

   - range columns和list columns分区都支持整数、日期和字符串三大数据类型。

   - 支持的数据类型有：

     | 类型 |                                           |
     | ---- | ----------------------------------------- |
     | 整数 | tinyint、smallint、mediumint、int、bigint |
     | 日期 | date、datetime                            |
     | 字符 | char、varchar、binary、varbinary          |

   - columns分区可以支持多列分区

     **例如：**

     ```mysql
     create table cp (
     	a int,
         b int
     )
     partition by range columns(a, b) (
     	partition p01 values less than (0,10),
         partition p02 values less than (10,10),
         partition p03 values less than (10,20),
         partition p04 values less than (10,maxvalue),
         partition p05 values less than (maxvalue,maxvalue)
     );
     ```

   - 比较是按照元组规则的：

     ```mysql
     ( ( a1 < b1 ) )
     or ( (a1 = n1) and (a2 < b2) ) 
     or ( (a1 = n1) and (a2 = n2) and (a3 < b3) ) 
     or ... 
     or ( (a1 = n1) and (a2 = n2) and ... and (an < bn) ) 
     ```

10. hash分区：基于给定分区个数，把数据分配到不同的分区。（分区键必须是int类型）

    

    



