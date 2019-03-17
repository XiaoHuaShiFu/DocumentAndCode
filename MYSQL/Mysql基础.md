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

9. show create table tablename \G 显示表的详细结构

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

   

3. 

