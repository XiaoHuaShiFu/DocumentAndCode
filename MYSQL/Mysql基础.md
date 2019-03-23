# Mysql基础

# 1.DDL语句

1. create database db1; 创建数据库

2. use db1; 使用数据库

3. show datatables; 显示所有数据库

4. show tables; 显示该数据库的数据表

5. drop database db1; 删除数据库

6. create table tablename (

   column_name_1 column_type_1 constraints,

   column_name_2 column_type_2 constraints,

   column_name_3 column_type_3 constraints

   )

   创建数据表

7. desc tablename; 显示表结构

8. \G 使字段竖向排列

9. show create table tablename \G 或 show table status like tablename \G显示表的详细结构

10. drop table tablename; 删除一张数据表

11. alter table tablename modify [column] column_definition [first|after col_name] 修改表类型

    **示例:**

    ```    mysql
    alter table user modify username varchar(30);
    ```

12. alter table tablename add [column] column_definition [first|after col_name] 增加表字段

    **示例:**

    ```mysql
    alter table user add phone varchar(11);
    ```

13. alter table tablename drop [column] column_name 删除表字段

    **示例:**

    ```mysql
    alter table user drop age;
    ```

14. alter table tablename change [column] old_col_name column_definition first|after col_name] 修改字段名

    **示例:**

    ```mysql
    alter table user change password pass varchar(21);
    ```

15. alter table tablename rename newname; 修改表名

    **示例:**

    ```mysql
    alter table user rename user0;
    ```

16. 分析此次查询

    ```mysql
    explain select * from city where city = 'gd' \G
    ```

## 2.DML语句

1. 向数据表插入一条数据

   ```mysql
   insert into tablename 
   (field1, field2, ..., fieldn) 
   values
   (value1, value2,...,valuen); 
   ```

   

2. 向数据表插入n条数据

   ```mysql
   insert into tablename 
   (field1, field2, ..., fieldn) 
   values
   (value11, value12,...,value1n),
   (value21, value22,...,value2n),
   (valuen1, valuen2,...,valuenn);
   ```

3. 更新数据

   ```mysql
   update tablename 
   set 
   field1 = value1,...,fieldn = valuen 
   [where condition] 
   ```

4. 更新多个表的数据

   ```mysql
   update 
   t1, t2,...,tn 
   set 
   t1.field1 = expr1,tn.fieldn = exprn 
   [where condition]
   ```

   **示例:**

   ```mysql
   update 
   user0 a, user b 
   set 
   a.username = b.name
   where b.name = 'hjc';
   ```

5. 删除数据(一定要加where,不然凉凉)

   ```mysql
   delete from tablename 
   [where condition] 
   ```

6. 一次删除多个表的数据

   ```mysql
   delete
   t1, t2,..., tn
   from
   t1, t2,..., tn 
   [where condition]
   ```

   **示例:**

   ```mysql
   delete 
   a, b 
   from 
   user0 a, user b 
   where a.username = b.name 
   and a.pass = '123456';
   ```

7. 查询数据

   ```mysql
   select 
   field1,...,fieldn 
   from tablename 
   [where condition]
   ```

   **注:**可以使用> < >= <= !=等运算符,和or and等逻辑运算符

   **注:**排序和限制

   ```mysql
   select 
   field1,...,fieldn 
   from tablename 
   [where condition]
   [order by field1 [desc|asc],
   field2 [desc|asc],
   ...,
   fieldn [desc|asc]]
   [limit offset_start, row_count];
   ```

   **注:**聚合,会把通用的属性聚合在一起

   fun_name: 聚合函数,常用的又sum,count(*),max,min;

   group by: 要聚合的字段,行数要对那个字段进行统计,就要写哪个字段;

   with rollup: 表明释放对分类聚合后的结果在进行汇总,就是统计总数;

   having: 对分类后的结果再进行条件的过滤(也就是在where之后再过滤一次).

   ```mysql
   select 
   field1,...,fieldn
   fun_name
   from tablename 
   [where condition]
   [group by 
   field1,field2,...,fieldn
   [with rollup]]
   [having where_condition]
   ```

   **示例1:**

   ```mysql
   select 
   username, count(1) 
   from 
   user0 
   where username != 'hjc' 
   group by username 
   with rollup;
   ```

   **示例2:**

   ```mysql
   select 
   username, count(1) 
   from 
   user0 
   where username != 'hjc' 
   group by username 
   having count(1) > 10;
   ```

8. 表连接

   内链接仅选出两张表中相互匹配的记录,外连接会选出其他不匹配的记录.

   **示例:**内连接

   ```mysql
   select 
   a.username,a.password,b.dep_name 
   from 
   user a, dep b 
   where a.dep_id = b.id;
   ```

   **示例:**外连接

   ①左连接:包含左边的所有数据,即使右边表没有和它匹配的数据.

   ```mysql
   select 
   a.username,a.password,b.dep_name 
   from 
   user a 
   left join 
   dep b 
   on a.dep_id = b.id;
   ```
   ②右连接:包含右边的所有数据,即使左边表没有和它匹配的数据.

   ```mysql
   select 
   a.username,a.password,b.dep_name 
   from 
   user a 
   right join 
   dep b 
   on a.dep_id = b.id;
   ```

9. 子查询

   子查询关键字主要包括in, not in, =, !=, exists, not exists等

   **示例1:**

   ```mysql
   select 
   username,password,dep_id 
   from user 
   where dep_id in (
   	select 
   	id 
   	from dep);
   ```

   **示例2:**如果数据唯一,可以使用'='

   ```mysql
   select 
   username, password, dep_id 
   from user 
   where dep_id = (
       select 
       id 
       from dep 
       limit 1);
   ```

10. 记录联合,联合表的查询记录

    union和union all的主要区别是union all是把结果集直接合并在一起,而union是将union all后的结果进行一次distinct,去除重复记录后的结果.

    ```mysql
    select 
    field1,...,fieldn
    from t1 
    union|union all
    select 
    field1,...,fieldn
    ...
    union|union all
    select 
    field1,...,fieldn
    ```

    **示例:**

    ```mysql
    select 
    dep_id 
    from user 
    union 
    select 
    id 
    from dep;
    ```

# 3.DCL语句

1. 创建用户

   ```mysql
   grant all privileges on dbname.tablename to username@ip identified by password;
   ```

2. 帮助命令

   查看帮助分类

   ```mysql
   ? contents
   ```

   查看某分类详情

   ```mysql
   ? data types
   ```

   查看某项详情

   ```mysql
   ? int	
   ```

   查看某项语法

   ```mysql
   ? show
   ? create table
   ```

3. 查询元数据信息

   information_schema库, 用来存放MySQL中的元数据信息.

   **示例:** 删除xhsf库里以saf开头的数据表, 使用information_schema里面的元数据

   ```mysql
   select 
   concat('drop table xhsf.',table_name,';') 
   from tables 
   where table_schema='xhsf' 
   and table_name like 'saf%';
   ```

   | 视图名     | 描述                                                         |
   | ---------- | ------------------------------------------------------------ |
   | schemata   | 提供了当前mysql实例中所有数据库的信息,show databases的结果取之此表. |
   | tables     | 提供关于数据库中的表的信息(包括视图),详细的描述了某个表属于哪个schema, 表类型,表引擎,创建时间等信息. show tables schemaname的结果取之此表. |
   | columns    | 提供了表中的列信息,详细表述了某张表的所有列以及每个列的信息. show columns from schemaname.tablename的结果取之此表. |
   | statistics | 提供了关于索引的信息. show index from schemaname.tablename的结果取之此表. |

# 4.数据类型

1. 数值类型

   | 整数类型    | 字节 | 最小值                 | 最大值                                   |
   | ----------- | ---- | ---------------------- | ---------------------------------------- |
   | tinyint     | 1    | -128(有符号)/0(无符号) | +127/255                                 |
   | smallint    | 2    | -32768/0               | +32767/65535                             |
   | mediumint   | 3    | -8388608/0             | 8388607/1677215                          |
   | int/integer | 4    | -2147483648/0          | 2147483647/4294967295                    |
   | bigint      | 8    | -9223372036854775808/0 | 9223372036854775807/18446744073709551615 |

   | 浮点数类型 | 字节 | 最小值                   | 最大值                   |
   | ---------- | ---- | ------------------------ | ------------------------ |
   | float      | 4    | ±1.175494351E-38         | ±3.402823466E+38         |
   | double     | 8    | ±2.2250738585072014E-308 | ±1.7976931348623157E+308 |

   | 定点数类型   | 字节 | 描述                                                         |
   | ------------ | ---- | ------------------------------------------------------------ |
   | dec(m,d)     | m+2  | 最大取值范围与double相同，给定decimal的有效取值范围由m和d决定 |
   | decimal(m,d) |      |                                                              |

   | 位类型 | 字节 | 最小值 | 最大值  |
   | ------ | ---- | ------ | ------- |
   | bit(m) | 1~8  | bit(1) | bit(64) |

   **注意:**如果一个属性列位zerofill，则MySQL自动为其添加unsigned属性。

   **注意：**auto_increment，只能用于整数类型。一般配合not null primary key 或 unique键。

   **注意：**MySQL进行保存值的时候进行四舍五入。如float(7,4) 9.90009 -> 9.9001。

   **注意：** bit数值类型可以使用bin(fieldname)、hex(fieldname)进行查看，如果不用直接查询默认返回null。

2. 日期时间类型

   | 日期和时间类型 | 字节 | 最小值              | 最大值              |
   | -------------- | ---- | ------------------- | ------------------- |
   | date           | 4    | 1000-01-01          | 9999-12-31          |
   | datetime       | 8    | 1000-01-01 00:00:00 | 9999-12-31 23:59:59 |
   | timestamp      | 4    | 19700101080001      | 2038年的某个时刻    |
   | time           | 3    | -838:59:59          | 838:59:59           |
   | year           | 1    | 1901                | 2155                |

   **注意：**

   timestamp默认设置为当前系统时间(current_timestamp)，如果有第二个则默认设置为0值；timestamp只能由一列为current_timestamp类型，如果强制修改会报错；

   timestamp和时区相关，如用set time_zone = "+9.00", timestamp的列会快1小时。

   timestamp取值为1970010108001到2038年的某一天，因此它不适合存放比较久远的日期；

   timestamp即使插入null，系统也会自动设置为current_timestamp，如果超出范围则设置为0值。

   **注意：**日期都可以设置为now()

3. 字符串类型

   | 字符串类型   | 字节 | 描述及存储需求                                      |
   | ------------ | ---- | --------------------------------------------------- |
   | char(M)      | M    | M为0~255之间的整数                                  |
   | varchar(M)   |      | M为0~65535之间的整数，值的长度+1个字节              |
   | tinyblob     |      | 允许长度0~255字节，值的长度+1个字节                 |
   | blob         |      | 允许长度0~65535字节，值的长度+2个字节               |
   | mediumblob   |      | 允许长度0~167772150字节，值的长度+3个字节           |
   | longblob     |      | 允许长度0~4294967295字节，值的长度+4个字节          |
   | tinytext     |      | 允许长度0~255字节，值的长度+2个字节                 |
   | text         |      | 允许长度0~65535字节，值的长度+2个字节               |
   | mediumtext   |      | 允许长度0~167772150字节，值的长度+3个字节           |
   | longtext     |      | 允许长度0~4294967295字节，值的长度+4个字节          |
   | varbinary(M) |      | 允许长度0~M个字节的变长字节字符串，值的长度+1个字节 |
   | binary(M)    | M    | 允许长度0~M个字节的定长字节字符串                   |

   ①char会把字符串尾部的空格去掉，而varchar会保留。

   ②length(s) 可以查看字符串的长度

   **示例：**

   ```java
   select length(v),length(c) from vc;
   ```

   ③binary和varbinary类型会自动添加\0在不够长度的位置上。如binary(3), insert 'a',其存储为'a\0\0'

4. enum类型

   对1~255个 成员的枚举需要1字节存储，对255~65535个程序需要两个字节存储，最多由65535个成员。

   **示例：**

   ```mysql
   create table e(
       gender enum('M','F'), 
       name varchar(20)
   );
   insert 
   into e 
   (gender, name) 
   values
   (1,'wjx'),('2','wjc'),('M','zzz'),('f','ddd'),(null,'bbb');
   ```

5. set类型,类似于enum类型，只是一次可以选多个值，且不允许选重复的值。最多64个成员。

   1~8个成员的集合，占1个字节；9~16个占2个字节；17~24个占3个字节；25~32占4个字节；33~64占8个字节；

   ```mysql
   create table s (
       col set ('a','b','c','d') 
   );
   insert 
   into s 
   (col)
   values
   ('a,b'),('a,b,a'),('1'),('A');
   ```

6. 正确的选择数据类型

   ①char和varchar

   - char是固定长度的，varchar是变长的，但是varchar的实际长度比值的长度多1字节。

   - char会把字符串尾部的空格去掉，而varchar会保留。

   - char的处理速度比varchar快得多，因为它是固定长度的。

   - MyISAM存储引擎：建议使用固定长度的数据列代表可变长度的数据列。

     MEMORY存储引擎：不管是char还是varchar都作为char类型处理。

     InnoDB存储引擎：建议使用varchar类型。因为内部的行存储格式没有区分固定长度和可变长度列（所有数据行都使用指向数据列值的头指针），因此在本质上，使用固定长度的char列不一定比使用可变长度的varchar性能要号。因而，主要的性能因素是数据行使用的存储总量，由于char平均占用的空间多余varchar，因此使用varchar来最小化需要处理的数据行的存储总量和磁盘io是比较好的。

   ②text和blob

   - blob能用来保存二进制数据，比如照片；而text只能保存字符数据，比如一篇温总或日记。

   - text和blob会引起一些性能问题，特别在执行了大量删除操作时。

   - 删除操作会在数据表中留下很大的“空洞”，以后填入这些“空洞”的记录在插入的性能上会有影响。为了提高性能，建议定期使用optimize table或ALTER TABLE tablename ENGINE='InnoDB';功能对这些表进行碎片整理。

   - 使用合成的索引（Synthetic）来提高大文本字段的查询性能。就是通过文本内容建立一个散列值，并把这个值存到单独的数据列中。

     可以用md5()、sha1()、crc32()或自己的生成散列值。数值散列值可以很高效的存储。

     如果散列值尾部空格，就不要存储在char或varchar中，它们会收到尾部空格去除的影响。

     **示例：**

     ```mysql
     create table t(
         id varchar(100), 
         context blob, 
         hash_code varchar(40)
     );
     
     insert 
     into t2 
     values(1,'beijing',md5(context));
     
     select 
     * 
     from t2 
     where hash_code = md5('beijing');
     ```

   - 对blob或clob字段进行模糊查询，MySQL提供了前缀索引，也就是只为字段的前n列创建索引。

     **示例：**

     ```mysql
     create index idx_blob 
     on t2(context(100));
     
     select * from t2 where context like 'beijing %';
     ```

   ③定点数和浮点数

   - 定点数己实际上是以字符串形式存放的。如果实际插入的数值精度大于实际定义的精度，则MySQL会进行警告。超出部分按照四舍五入后插入。

   - 浮点数有float、double。

   - 定点数有decimal或numberic两种。

   - 浮点数存在误差,尽量避免做浮点数比较.


# 5.运算符

1. 算数运算符

   | 运算符 | 作用 |
   | ------ | ---- |
   | +      | 加   |
   | -      | 减   |
   | *      | 乘   |
   | /，DIV | 除法 |
   | %，MOD | 模   |

   **注意：**在模运算中，如果除数为0，结果返回null，还可以使用MOD(a,b)表示模运算

2. 比较运算符

   **注意：**数字作为浮点数比较，字符串不区分大小写进行比较。

   | 运算符        | 作用                                                         |
   | ------------- | ------------------------------------------------------------ |
   | =             | 等于。如果相等返回1，不等返回0                               |
   | <>或!=        | 不等于                                                       |
   | <=>           | null安全的等于。如select null<=> null; -> 1而如果不用<=>则只要和null比较都为null |
   | <             | 小于                                                         |
   | <=            | 小于等于                                                     |
   | >             | 大于                                                         |
   | >=            | 大于等于                                                     |
   | between       | 存在于指定范围。形式 a between min and max。如select * from user where dep_id between 1 and 2; |
   | in            | 存在于指定集合。形式 a in (v1,v2,...)。如select 2 in (select dep_id from user); |
   | is null       | 为null。如select * from user where dep_id is null;           |
   | is not null   | 不为null。如select * from user where dep_id is not null;     |
   | like          | 通配符匹配。形式a like '%123%'。如select * from user where username like '%x%'; |
   | regexp或rlike | 正则表达式匹配。形式str regexp str_pat。如select * from user where username regexp 'xh'; |

3. 逻辑运算符

   | 运算符   | 作用 |
   | -------- | ---- |
   | not或!   | 非   |
   | and或&&  | 与   |
   | or或\|\| | 或   |
   | xor      | 异或 |

4. 位运算符

   | 运算符 | 作用 |
   | ------ | ---- |
   | &      | 与   |
   | \|     | 或   |
   | ^      | 异或 |
   | ~      | 取反 |
   | >>     | 右移 |
   | <<     | 左移 |

5. 运算符优先级

   **注意：**可以使用()将要优先的括起来

   | 优先级 | 运算符                                             |
   | ------ | -------------------------------------------------- |
   | 1      | :=                                                 |
   | 2      | \|\|、or、xor                                      |
   | 3      | &&、and                                            |
   | 4      | not                                                |
   | 5      | between、case、when、then、else                    |
   | 6      | =、<=>、>=、>、<=、<、<>、!=、is、like、regexp、in |
   | 7      | \|                                                 |
   | 8      | &                                                  |
   | 9      | <<、>>                                             |
   | 10     | -、+                                               |
   | 11     | *、/、div、%、mod                                  |
   | 12     | ^                                                  |
   | 13     | -、~                                               |
   | 14     | !                                                  |

# 6.函数

1. 字符串函数

   | 函数                  | 功能                                                         |
   | --------------------- | ------------------------------------------------------------ |
   | concat(s1,s2,...,sn)  | 连接s1,s2,...,sn为一个字符串                                 |
   | insert(str,x,y,instr) | 将字符串str从第x位置开始，y个字符长的子串替换为字符串instr   |
   | lower(str)            | 转换成小写                                                   |
   | upper(str)            | 转换成大写                                                   |
   | left(str,x)           | 返回字符串str最左边的x个字符                                 |
   | right(str,x)          | 返回字符串str最右边的x个字符                                 |
   | lpad(str,n,pad)       | 用字符串pad对str最左边进行填充，直到长度为n个字符长度。比如lpad('d',3,'a'); -> 'aad'，即用a对左边进行填充，直到字符串长度为3 |
   | rpad(str,n,pad)       | 用字符串pad对str最右边进行填充，直到长度为n个字符长度        |
   | ltrim(str)            | 去掉字符串str左侧空格                                        |
   | rtrim(str)            | 去掉字符串str右侧空格                                        |
   | repeat(str,x)         | 返回str重复x次的结果                                         |
   | replace(str,a,b)      | 用字符串b替换字符串str中所有的字符串a                        |
   | strcmp(s1,s2)         | 比较字符串s1和s2                                             |
   | trim(str)             | 去掉字符串行尾和行头的空格                                   |
   | substring(str,x,y)    | 返回字符串str从x位置起y个字符长度的字符串                    |

2. 数值函数

   | 函数          | 功能                               |
   | ------------- | ---------------------------------- |
   | abs(x)        | 返回x的绝对值                      |
   | ceil(x)       | 返回大于x的最小整数值              |
   | floor(x)      | 返回小于x的最大整数值              |
   | mod(x,y)      | 返回x/y的膜                        |
   | rand()        | 返回0~1内的随机值                  |
   | round(x,y)    | 返回参数x的四舍五入的由y位小数的值 |
   | truncate(x,y) | 返回数字x截断位y位小数的结果       |

3. 日期和时间函数

   | 函数                              | 功能                                                         |
   | --------------------------------- | ------------------------------------------------------------ |
   | curdate()                         | 返回当前日期                                                 |
   | curtime()                         | 返回当前的时间                                               |
   | now()                             | 返回当前的日期和时间。如date_format(now(),'%Y-%m-%d %H:%i:%s'); |
   | unix_timestamp(date)              | 返回日期date的unix时间戳                                     |
   | from_unixtime                     | 返回unix时间戳的日期值                                       |
   | week(date)                        | 返回日期date为一年中的第几周                                 |
   | year(date)                        | 返回日期date的年份                                           |
   | hour(time)                        | 返回time的小时值                                             |
   | minute(time)                      | 返回time的分钟值                                             |
   | monthname(date)                   | 返回date的月份名                                             |
   | date_format(date,fmt)             | 返回按字符串fmt格式化日期date值                              |
   | date_add(date,interval expr type) | 返回一个日期或时间值加上一个时间间隔的时间值                 |
   | datediff(expr,expr2)              | 返回起始时间expr和结束时间expr2之间的天数                    |

   fmt的格式符：

   | 格式符 | 格式说明                                                   |
   | ------ | ---------------------------------------------------------- |
   | %S和%s | 两位数字形式的秒(00,01,...,59)                             |
   | %i     | 两位数字形式的分(00,01,...,59)                             |
   | %H     | 两位数字形式的小时,24小时(00,01,...,23)                    |
   | %h和%I | 两位数字形式的小时,12小时(01,02,...,12)                    |
   | %k     | 数字形式的小时，24小时(0,1,...,23)                         |
   | %l     | 数字形式的小时，12小时(1,2,...,12)                         |
   | %T     | 24小时的时间形式(hh:mm:ss)                                 |
   | %r     | 12小时的时间形式(hh:mm:ssAM或hh:mm:ssPM)                   |
   | %p     | AM或PM                                                     |
   | %W     | 一周每一天的名字(Sunday,Monday,...,Saturday)               |
   | %a     | 一周中每一天的名字缩写(Sun,Mon,...,Sat)                    |
   | %d     | 两位数字表示月中的天数(00,01,...,31)                       |
   | %e     | 数字表示月中的天数(1,2,...,31)                             |
   | %D     | 英文后缀表示月中的天数(1st,2nd,3rd,...)                    |
   | %w     | 以数字形式表示周中的天数(0=Sunday,1=Monday,...,6=Saturday) |
   | %J     | 以3位数字表示年中的天数(001,002,...,366)                   |
   | %U     | 周(0,1,...,52)，其中Sunday为周中的第一天                   |
   | %u     | 周(0,1,...,52)，其中Monday为周中的第一天                   |
   | %M     | 月名(January,February,...,December)                        |
   | %b     | 缩写的月名(January,February,...,December)                  |
   | %m     | 两位数字表示的月份(01,02,...,12)                           |
   | %c     | 数字表示的月份(1,2,...,12)                                 |
   | %Y     | 4位数字表示的年份                                          |
   | %y     | 两位数字表示的年份                                         |
   | %%     | 直接值%                                                    |

   时间间隔类型：

   | 表达式类型    | 描述     | 格式        |
   | ------------- | -------- | ----------- |
   | hour          | 小时     | hh          |
   | minute        | 分       | mm          |
   | second        | 秒       | ss          |
   | year          | 年       | YY          |
   | month         | 月       | MM          |
   | day           | 日       | DD          |
   | year_month    | 年和月   | YY-MM       |
   | day_hour      | 日和小时 | DD hh       |
   | day_minute    | 日和分钟 | DD hh:mm    |
   | day_second    | 日和秒   | DD hh:mm:ss |
   | hour_minute   | 小时和分 | hh:mm       |
   | hour_second   | 小时和秒 | hh:ss       |
   | minute_second | 分钟和秒 | mm:ss       |

4. 流程函数

   | 函数                                                   | 功能                                    |
   | ------------------------------------------------------ | --------------------------------------- |
   | if(value,t,f)                                          | 如果value为真，返回t，否则返回f         |
   | ifnull(v1,v2)                                          | 如果v1不为空，返回1，否则返回v2         |
   | case when [v1] then [r1] ... else [default] end        | 如果v1为真，返回r1，否则返回default     |
   | case [expr] when [v1] then [r1] ... else [default] end | 如果expr等于v1，返回r1，否则返回default |

   **示例：**

   ```mysql
   select 
   if(username='xhsf', 'my', 'other') 
   from user;
   
   select
   ifnull(dep_id, -1) 
   from user;
   
   //类似于if else
   select 
   case 
   when dep_id = 1 
   then 10 
   when dep_id = 2
   then 20 
   else -1 
   end 
   from user;
   
   //类似于switch
   select 
   case 
   dep_id 
   when 1 then 10 
   when 2 then 20 
   else -1 
   end 
   from user;
   ```

5. 其他常用函数

   | 函数           | 功能                    |
   | -------------- | ----------------------- |
   | database()     | 返回当前数据库名        |
   | version()      | 返回当前数据库版本      |
   | user()         | 返回当前登录用户名      |
   | inet_aton(ip)  | 返回ip地址的数字表示    |
   | inet_ntoa(num) | 返回数字代表的ip地址    |
   | password(str)  | 返回字符串str的加密版本 |
   | md5()          | 返回字符串str的md5值    |

# 7.存储引擎

1. 查看命令

   show engines \G 查看支持的存储引擎

   show variables like 'table_type' 查看默认存储引擎

2. 设置表的引擎

   ```mysql
   create table eg(
   	name varchar(20)
   ) engine=myisam default charset=utf-8;
   
   查看表详细结果
   show create table eg \G 
   
   修改表存储引擎
   alter table eg engine = innodb;
   ```

3. 存储引擎对别

   | 特点           | MyISAM | InnoDB | MEMORY | MERGE | NDB  |
   | -------------- | ------ | ------ | ------ | ----- | ---- |
   | 存储限制       | 有     | 64TB   | 有     | 没有  | 有   |
   | 事务安全       |        | 支持   |        |       |      |
   | 锁机制         | 表锁   | 行锁   | 表锁   | 表锁  | 行锁 |
   | B树索引        | 支持   | 支持   | 支持   | 支持  | 支持 |
   | 哈希索引       |        |        | 支持   |       | 支持 |
   | 全文索引       | 支持   |        |        |       |      |
   | 集群索引       |        | 支持   |        |       |      |
   | 数据缓存       |        | 支持   | 支持   |       | 支持 |
   | 索引缓存       | 支持   | 支持   | 支持   | 支持  | 支持 |
   | 数据可压缩     | 支持   |        |        |       |      |
   | 空间使用       | 低     | 高     | N/A    | 低    | 低   |
   | 内存使用       | 低     | 高     | 中等   | 低    | 高   |
   | 批量插入的速度 | 高     | 低     | 高     | 高    | 高   |
   | 支持外键       |        | 支持   |        |       |      |

4. MyISAM

   ①访问速度快，对事务完整性没有要求或者以select、insert为主的应用基本上都可以使用这个引擎来创建表。

   ②MyISAM在磁盘上存储成3个文件，其文件名和表名相同，但扩展名分别是：

   • .frm（存储表定义）；

   • .MYD（MYData，存储数据）；

   • .MYI（MYIdex，存储索引）。

   ③数据文件和索引文件可以放置在不同目录，平均分布IO，获得更快的速度。要指定索引文件和数据文件的路径，需要在创建表的时候通过data directory 和 index directory语句指定，文件路径需要是绝对路径，并且具有访问权限。

   ④MyISAM的表支持3种不同的存储格式：

   • 静态（固定长度）表；默认，表中的字段都是非变长字段，每个记录都是固定长度的，占用空间比动态表多。

   存储非常迅速，容易缓存。

   出现故障容易恢复；

   静态表会在存储时按照列的宽度定义不足空格，在返回给应用之前去掉空格。但是如果保存的内容后面本来就带有空格，那么在返回结果的时候也会被去掉。

   • 动态表；动态表包含变成字段，记录不是固定长度的，占用的空间比较少。

   但是频繁地更新和删除记录会产生碎片，需要定期执行optimize table 语句或 myisamchk-r命令来改善性能。

   出现故障比较难恢复。

   • 压缩表；压缩表由myisampack工具创建，占据非常小的磁盘空间。因为每个记录是被单独压缩的，所以只有非常小的访问开支。

   ⑤MyISAM的自动增长列可以是组合索引的其他列，插入数据后，根据组合索引的前几列的向后递增。也就是如过由两个字段的索引，那么就像一个二维自动增长索引一样。

   **示例：**其中index是把字段设置为索引

   ```mysql
   create table ma(
   d1 smallint not null auto_increment,
   d2 smallint not null,
   name varchar(10),
   index(d2,d1)
   ) engine=myisam;
   ```

   

5. InnoDB

   ①相比MyISAM，InnoDB写的处理效率差一些，并且会占用更多的磁盘空间以保留数据和索引。

   ②自动增长列，自动增长列可以手工插入，但是插入的值如果是空或者0，则实际插入的将是自动增长后的值。

   •自动增长列必须是索引，和必须是组合索引的第一列。

   •可以通过列的auto_increment 设置。

   •如果通过alter table user auto_increment = n;是存储在内存种的，重启后需要重新设置。

   •通过last_insert_id();可以查询最后一条自动增长插入的值。

   create table dep0( id int not null primary key auto_increment,city varchar(20),key idx_fk_country_id (country_id),onstraint 'fk_city_country' foreign key (country_id) references country (id) ;

   ③外键约束

   •在创建约束时，可以指定在删除、更新父表时，对字表进行相应操作。

   restrict和no action相同，限制在字表有关联记录的情况下，父表不能更新；

   cascade表锁父表在更新或者删除时，更新或删除字表对应的记录；

   set null 表锁父表在更新或者删除时，字表对应字段被设置为null。

   指定格式是：on delete restrict on update cascade...

   •在某个表被其他表创建了外键参照，那么该表对应索引或者主键禁止被删除。

   •在导入多个表的数据时，如果需要忽略表之前的导入顺序，可以暂时关闭外键检查；同理，在执行load data和alter table操作的时候，可以通过暂时关闭外键约束来加快处理速度，关闭的命令是set foreign_key_checks = 0; 通过set foreign_key_checks = 1;恢复外键检查。

   **示例：**

   ```mysql
   create table city(
       city_id smallint unsigned not null auto_increment primary key, 
       city varchar(50) not null, 
       country_id smallint unsigned not null, 
       key idx_fx_country_id(country_id), 
       
       constraint `fk_city_counttry` 
       foreign key (country_id) 
       references country (country_id) 
       on delete restrict 
       on update cascade
       
   ) engine=iinnodb default charset=utf8;
   ```

   ④

   •使用共享表空间存储，这种方式创建的表的表结构保存在.frm文件中，数据和索引保存在innodb_data_home_dir 和 innodb_data_file_path定义的表空间中，可以是多个文件。

   •使用多表空间存储，表结果仍然保存在.frm文件中，每个表的数据和索引都单独保存在.idb中。如果是个分区表，则每个分区对应单独的.idb文件，文件名是“表名+分区名”，可以在创建分区的时候指定每个分区的数据文件的位置，以此来将表的IO均匀分布在多个磁盘上。

   使用多表空间存储方式，需要设置参数innodb_file_per_table，并且重新启动服务后才生效。对于新建的表按照多表空间的方式创建，已有的表仍然使用共享表空间存储。

   如果将已有的多表空间方式修改回共享表空间的方式，则新建表会在共享表空间中创建，但已有的多表空间的表仍然保存原来的访问方式。所以多表空间的参数生效后，只对新建的表生效。

   多表空间的数据文件没有大小限制，不需要设置初始大小，也不需要设置文件的最大限制、扩展大小等参数。

   对于使用多表空间特性的表，可以方便的进行单表备份和恢复操作，但是直接复制.ibd文件是不行的，因为没有共享表空间的数据字典信息，直接复制.idb文件和.frm文件恢复时是不能被正确识别的，但是可以通过以下命令：

   alter table tbl_name discard tablespace;

   alter table tbl_name importtablespace;

   将备份数据恢复到数据库中，但是这样的单表备份，只能恢复到表原来所在的数据库中，而不能恢复到其他的数据库中。如果要将单表恢复到目标数据库，则需要通过mysqldump和mysqllimport来实现。

6. MEMORY

   ①memory存储引擎使用存在内存中的内容来创建表。每个memory表只实际对于一个磁盘文件，格式是.frm。memory类型的访问非常快，因为它的数据是放在内存中，并且默认使用hash所以，但不能持久化。

   **示例：**创建缓存表。

```mysql
create table cac0 
engine=memory  
select 
* 
from dep10;
```

​	**示例：**为memory表指定索引，可以使用hash索引或者是btree索引。

```mysql
create index id_hash 
using hash 
on cac0 (id);
//取消索引
drop index mem_hash 
on cac0;
```

​	②可以在启动mysql服务的时候使用--init-file选项，把insert into ... select 或load data infile这样的语句放入这个文件中，这样就可以在服务启动时用持久的数据源装配表。

​	③使用delete from tablename或 truncate table tablename释放memory使用的内存，或整个的删除表drop table tablename。

​	④memory表中放置数据量的大小，受到max_heap_table_size系统变量的约束，这个系统变量的初始值是16MB，可以根据需要加大。可以在定义memory表的时候，可以通过max_rows子句指定表的最大行数。

7. MERGE

   ①MERGE存储引擎是一组MyISAM表的组合，这些MyISAM表必须结构完全相同，MERGE表本身没有数据，对MERGE表的增删查改操作实际上是在MyISAM表进行的。

   ②对MERGE表类型的插入操作，是通过insert_method指定的表，可以有first和last表示插入到第一个或最后一个表，no表示不能进行插入操作。

   ③对MERGE表进行drop操作，实际上自身删除MERGE的定义，对数据没有影响。

   ④MERGE表在磁盘上保留两个文件，文件以表的名字开始，一个.frm文件存储表定义，另一个.MRG文件包含组合表信息，包含MERGE表由哪些表组成、插入新的数据时的依据。

   **示例：**

   ```mysql
   create table payment_2018( 
       amount decimal(15,2), 
       id int not null primary key auto_increment, 
       payment__date datetime
   ) 
   engine=myisam;
   create table payment_2019( 
       amount decimal(15,2), 
       id int not null primary key auto_increment, 
       payment__date datetime
   ) 
   engine=myisam;
   
   create table payment_all(
        amount decimal(15,2),
        id int not null primary key auto_increment,
        payment_date datetime
   ) 
   engine=merge 
   union=(payment_2018,payment_2019) 
   insert_method=last;
   ```

8. 引擎选择

   ①MyISAM：如果以读操作和插入操作为主，只有很少的更新和删除操作，并且对事务的完整性、并发性要求不是很高，那这个引擎非常适合。

   ②InnoDB：用于事务处理应用程序，支持外键。如果对事务的完整性要求比较高，在并发条件下要求数据的一致性，数据操作除了插入和查询外，还包括很多的更新、删除操作，那么InnoDB存储引擎比较适合。可以确保事务的完整提交(commit)和回滚(rollback)，对于类似计费系统或者财务系统等对数据准确性要求比较高的系统，InnoDB都是合适的选择。

   ③MEMORY：速度快。但是对表的大小由限制。

   ④MERGE：将一系列相同的MyISAM表以逻辑方式组合在一起。可以用于突破单个MyISAM表的大小限制，并且通过将不同表分布在多个磁盘上，可以有效的改善MERGE表的访问效率。


# 8.字符集

1. MySQL的字符串集支持到字段级别。

2. MySQL的字符集包括字符集（character）和校对规则（collation）两个概念。其中字符集用来定义MySQL的存储字符串方式，校对规则用来定义比较字符串的方式。

   可以使用show character set;查看字符集，用show collation like ‘’;查看校对规则。

   其中校对规则的命名约定：它们以其相关的字符集名开始，通常包括一个语言名并且以ci（大小写不敏感）、cs（大小写敏感）、_bin（二元，即比较是基于字符串编码的值而与language无关）。

3. 设置服务器字符集和校对规则

   ①可以在my.cnf中设置：character-set-server=utf8

   ②在启动选项中指定：mysqld --character-set-server=utf8

   ③在编译时指定： cmake . -DDEFAULT_CHARSET=utf8

   ④使用show variables like 'character_set_server';和show variables like 'collation_server';命令查询当前服务器的字符集和校对规则。

4. 数据库字符集和校对规则

   ①查看show variables like 'character_set_database';和show variables like 'collation_database';

   ②

5. 数据表字符集和校对规则

   ①查看show create table tablename \G;

6. 连接字符集和校对规则

   ①character_set_client、character_set_connection、character_set_result反别代表客户端、连接和返回结果的字符集，通常这3个应该是相同的。

   ②可以在my.cnf中设置 default-character-set=utf8

7. 导出表结构、数据等

   - 导出表结构

     **语法：**

     ```mysql
     mysqldump -uroot -p --default-character-set=gbk -d databasename> newdatabasename.sql
     ```

     **注：**--default-character-set标识设置以什么字符集连接，-d标识只导出表结构，不导出数据

     ```mysql
     mysqldump -uroot -p --default-character-set=gbk -d xhsf> xhsf.sql
     ```

   - 导出数据

     **语法：**

     ```mysql
     mysqldump -uroot -p --quick --no-create-info --extended-insert --default-character-set=utf8 xhsf> xhsf-data.sql
     ```

     **注：**

     - --quick用于一次一行地检索表中的行而不是检索所有行，并在输出前将它缓存到内存中。用于转储大的表。
     - --extended-insert：使用包括几个values列表的多行insert语法。这样转储的文件更新，重载文件时可以加速插入。
     - --no-create-info：不导出每个转储表的create table语句。
     - --default-character-set=utf8：按照原有的字符集导出所有数据，这样导出的文件中，所有中文都是可见的，不会保存成乱码。

   - 导入表结构

     **语法：**

     ```mysql
     mysql -uroot -p xhsf0 < xhsf.sql
     ```

   - 导入数据

     **语法：**

     ```mysql
     mysql -uroot -p xhsf0 < xhsf-data.sql
     ```

# 9.索引

1. MyISAM和InnoDB都是默认创建BTREE索引。MEMORY默认使用HASH索引，但也支持BTREE索引。

2. MyISAM的前缀索引可以1000字节长，InnoDB可以767字节长。（注是字节长，不是字符长）

3. MyISAM支持全文本索引（FULLTEXT）索引，只限于char、varchar和text列。

4. MyISAM支持空间类型索引，且索引的字段必须是非空的。

5. 创建新索引的语法为：

   ```mysql
   create [unique|fulltext|spatial] index index_name [using index_type] on table_name (index_col_name,...) 
   ```

   **其中：**index_col_name表示<!--col_name[(length)][asc|desc]-->

   **示例：**

   ```mysql
   create index cityname on city (city(10));
   ```

6. 删除索引的语法：

   ```mysql
   drop index index_name on table_name
   ```

   **示例：**

   ```mysql
   drop index cityname on city
   ```

7. 设计索引原则

   - 用于搜索的列，也就是出现在where或info中指定的列。

   - 使用唯一索引。考虑某列中值的分布。索引的列的基数越大，索引的效果越好。比如用生日作为索引比用性别作为索引好。

   - 使用短索引。如果对字符串列进行索引，应该指定一个前缀长度，只要有可能就应该这样做。

     - 如对一个char(200)的列
     - 对前10或20个字符进行索引能够节省大量索引空间
     - 较小的索引涉及的磁盘IO较少
     - 较短的值比较起来也更快
     - 在高速缓存中的块能容纳更多的键值
     - 因此，MySQL也可以在内存中容纳更多的值。加快速度。

   - 利用最左前缀。在创建一个n列索引时，实际是创建MySQL可利用的n个索引，多列索引可起几个索引的作用，因为可利用索引中最左边的列集来匹配行。这样的列集称为最左前缀。

   - 不要过度索引。

     - 过度索引不但增加额外磁盘空间，还降低写操作的性能。
     - 在修改表内容时，索引必须进行更新，有时可能需要重构，因此，索引越多，所花事件越长。
     - 如果有一个索引很少利用或从不利用，那么会不必要地减缓表的修改速度。
     - 此外，MySQL生成一个执行计划时，要考虑各个索引，也要花费时间。创建多余的索引给查询优化带来了更多的工作。索引太多，也可能会使MySQL选择不到所要使用的最好索引。只保持所需的索引有利于查询优化。

   - 对于InnoDB存储引擎的表，记录默认会按照一定的顺序保持

     - 如果有明确定义主键，则按照主键顺序保存。
     - 如果没有主键，但是又唯一索引，就按照唯一索引的顺序保存。
     - 如果没有主键又没有唯一索引，那么表中会自动生成一个内部列，按照这个列的顺序保存。
     - 按照主键或者内部列进行的访问是最快的，所以InnoDB表尽量自己指定主键，尽量选择最常用作访问条件的列作为主键。
     - InnoDB的普通索引都会保存主键的键值，所以主键要尽可能的选择较短的数据类型，减少索引的磁盘占用，提高索引缓存效果。

8. btree索引和hash索引

   - hash索引的重要特征：

     - 只用于=或<=>操作符的等式比较。
     - 优化器不能使用hash索引来加速order by操作。
     - MySQL不能确定在两个值之间大约又多少行。如果将一个MyISAM表改为hash索引的MEMORY表，会影响一些查询的执行效率。
     - 只能使用整个关键字来搜索一行。

   - btree：

     - 当使用>、<、>=、<=、between、!=或者<>，或者like 'pattern'（其中pattern不以通配符开始）的操作符时，都可以使用相关列上的索引。

# 10.视图









