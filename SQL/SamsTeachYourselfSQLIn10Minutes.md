

# 2、检索数据

1. 

2.  

3.  

4.  

5.  检索不同的值

   - DISTINCT关键字：只返回不同的值。

     - 示例：

       ```mysql
       mysql> select distinct vend_id from products;
       +---------+
       | vend_id |
       +---------+
       | BRS01   |
       | DLL01   |
       | FNG01   |
       +---------+
       3 rows in set (0.03 sec)
       
       mysql> select vend_id from products;
       +---------+
       | vend_id |
       +---------+
       | BRS01   |
       | BRS01   |
       | BRS01   |
       | DLL01   |
       | DLL01   |
       | DLL01   |
       | DLL01   |
       | FNG01   |
       | FNG01   |
       +---------+
       9 rows in set (0.06 sec)
       ```

   - distinct关键字适用于distinct后面的所有列：
   
     - 示例：
   
       ```mysql
       mysql> select distinct vend_id, prod_price from products;
       +---------+------------+
       | vend_id | prod_price |
       +---------+------------+
       | DLL01   | 3.49       |
       | BRS01   | 5.99       |
       | BRS01   | 8.99       |
       | BRS01   | 11.99      |
       | DLL01   | 4.99       |
       | FNG01   | 9.49       |
       +---------+------------+
       6 rows in set (0.07 sec)
       
       mysql> select vend_id, prod_price from products;
       +---------+------------+
       | vend_id | prod_price |
       +---------+------------+
       | DLL01   | 3.49       |
       | DLL01   | 3.49       |
       | DLL01   | 3.49       |
       | BRS01   | 5.99       |
       | BRS01   | 8.99       |
       | BRS01   | 11.99      |
       | DLL01   | 4.99       |
       | FNG01   | 9.49       |
       | FNG01   | 9.49       |
       +---------+------------+
       9 rows in set (0.07 sec)
       ```
   
6. 限制结果

   - limit限制行数

   - limit n offset m：从第m行起拿n行（不包含第m行，比如offset 5，不包含第5行）；可以使用简化版的limit m, n

     - 示例：

       ```mysql
       mysql> select vend_id from products;
       +---------+
       | vend_id |
       +---------+
       | BRS01   |
       | BRS01   |
       | BRS01   |
       | DLL01   |
       | DLL01   |
       | DLL01   |
       | DLL01   |
       | FNG01   |
       | FNG01   |
       +---------+
       9 rows in set (0.07 sec)
       
       mysql> select vend_id from products limit 5;
       +---------+
       | vend_id |
       +---------+
       | BRS01   |
       | BRS01   |
       | BRS01   |
       | DLL01   |
       | DLL01   |
       +---------+
       5 rows in set (0.08 sec)
       
       mysql> select vend_id from products limit 5 offset 5;
       +---------+
       | vend_id |
       +---------+
       | DLL01   |
       | DLL01   |
       | FNG01   |
       | FNG01   |
       +---------+
       4 rows in set (0.11 sec)
       ```

# 3、排序检索数据

1.  排序检索数据

   - order by 列名1，列名2。。。：注意会按照列名1排序，再按照列名2排序。。。

     - 示例：

       ```mysql
       mysql> select prod_id, prod_price, prod_name from products order by prod_price, prod_name
           -> ;
       +---------+------------+---------------------+
       | prod_id | prod_price | prod_name           |
       +---------+------------+---------------------+
       | BNBG02  | 3.49       | Bird bean bag toy   |
       | BNBG01  | 3.49       | Fish bean bag toy   |
       | BNBG03  | 3.49       | Rabbit bean bag toy |
       | RGAN01  | 4.99       | Raggedy Ann         |
       | BR01    | 5.99       | 8 inch teddy bear   |
       | BR02    | 8.99       | 12 inch teddy bear  |
       | RYL01   | 9.49       | King doll           |
       | RYL02   | 9.49       | Queen doll          |
       | BR03    | 11.99      | 18 inch teddy bear  |
       +---------+------------+---------------------+
       9 rows in set (0.05 sec)
       ```

       

   - order by子句位于select语句的最后一条子句

   - order by子句使用的列不一定是要检索的列，可以使用非检索列

   - 使用order by  列名 desc进行降序排序，每个desc只对前面的一列有效，因此对多列进行desc时必须每一列都指定

     - 示例：

       ```mysql
       mysql> select prod_id, prod_price, prod_name from products order by prod_price desc, prod_name;
       +---------+------------+---------------------+
       | prod_id | prod_price | prod_name           |
       +---------+------------+---------------------+
       | BR03    | 11.99      | 18 inch teddy bear  |
       | RYL01   | 9.49       | King doll           |
       | RYL02   | 9.49       | Queen doll          |
       | BR02    | 8.99       | 12 inch teddy bear  |
       | BR01    | 5.99       | 8 inch teddy bear   |
       | RGAN01  | 4.99       | Raggedy Ann         |
       | BNBG02  | 3.49       | Bird bean bag toy   |
       | BNBG01  | 3.49       | Fish bean bag toy   |
       | BNBG03  | 3.49       | Rabbit bean bag toy |
       +---------+------------+---------------------+
       9 rows in set (0.07 sec)
       ```

   - 默认时A和a是相同的（按照字典顺序）

# 4、数据过滤

1. 

2.  where子句操作符

   - 操作符

     | 操作符          | 描述         |
     | --------------- | ------------ |
     | =               | 等于         |
     | <>  或者  !=    | 不等于       |
     | >               | 大于         |
     | <               | 小于         |
     | >=              | 大于等于     |
     | <=              | 小于等于     |
     | BETWEEN n AND m | 在n~m范围内  |
     | LIKE            | 搜索某种模式 |
     | IS NULL         | 为NULL值     |

# 5、高级数据过滤

1. 组合where子句

   1.  
   2.  
   3.  求值顺序
      - SQL中and的优先级高于or，因此有时候需要使用（条件1 or 条件2） and 条件3 来改变优先级

2. in操作符：where子句中用来指定要匹配值的清单的关键字，功能与or相当

   - in操作符一般比一组or操作符执行得更快

   - in最大的优点是包含其他select句子，能够更动态地建立where子句

   - 示例：

     ```mysql
     mysql> select prod_name, prod_price from products where vend_id in ('DLL01', 'BRS01') order by prod_name;
     +---------------------+------------+
     | prod_name           | prod_price |
     +---------------------+------------+
     | 12 inch teddy bear  | 8.99       |
     | 18 inch teddy bear  | 11.99      |
     | 8 inch teddy bear   | 5.99       |
     | Bird bean bag toy   | 3.49       |
     | Fish bean bag toy   | 3.49       |
     | Rabbit bean bag toy | 3.49       |
     | Raggedy Ann         | 4.99       |
     +---------------------+------------+
     7 rows in set (0.06 sec)
     ```

3. not操作符：否定where后面的某个条件

# 6、用通配符进行过滤

1. like操作符
   - %通配符：匹配任意字符（0，1，n个）
     - 匹配邮件：where email like '%@qq.com'
     - where prod_name like '%'不会匹配产品名为null的行
     
   - _通配符：匹配且只匹配一个字符
   
     - 示例：
   
       ```mysql
       mysql> select prod_id, prod_name from products where prod_name like '__ inch teddy bear';
       +---------+--------------------+
       | prod_id | prod_name          |
       +---------+--------------------+
       | BR02    | 12 inch teddy bear |
       | BR03    | 18 inch teddy bear |
       +---------+--------------------+
       2 rows in set (0.07 sec)
       ```
   
   - []通配符：匹配[]里的一个字符
   
     - 只有Access和SQL Server支持
   
     - 示例：
   
       ```sql
       select cust_contact from customers where cust_contact like '[JM]%' order by cust_contact;
       ```
   
2. 使用通配符的技巧

   - 通配符一般比较慢；
   - 如果能用其他操作符达到目的，不要使用通配符；
   - 尽量把通配符方搜索模式的后面；

# 7、创建计算字段

1. 拼接字段

   - 有操作符+、||

   - mysql中必须使用concat()函数

   - 示例：

     ```mysql
     mysql> select concat(vend_name , '(' , vend_country , ')') from vendors order by vend_name;
     +----------------------------------------------+
     | concat(vend_name , '(' , vend_country , ')') |
     +----------------------------------------------+
     | Bear Emporium(USA)                           |
     | Bears R Us(USA)                              |
     | Doll House Inc.(USA)                         |
     | Fun and Games(England)                       |
     | Furball Inc.(USA)                            |
     | Jouets et ours(France)                       |
     +----------------------------------------------+
     6 rows in set (0.05 sec)
     ```

   - 使用trim()、rtrim()、ltrim()去除字符串两边的空格

# 8、使用函数处理数据

1.  
2.  使用函数

# 9、汇总数据

1. 聚集函数

   ```
   count 行数计数
   sum 求和
   max 求最大值
   min 求最小值
   avg 求平均值
   ```

   1. avg

   2. count()函数：

      - count(*)不管列是不是空值（null）都会计入
      - count(column)会忽略null值

   3. max()函数：忽略null值的行。

   4. min()函数：忽略null值的行。

   5. sum()函数：忽略null值的行。

      - 示例：

        ```mysql
        select sum(item_price*quantity) as total_price from orderItems where order_num = 20005;
        ```

2. 聚集不同值

   - 默认使用all

   - 可以使用distinct

     - 示例：

       ```mysql
       mysql> select avg(distinct prod_price) as avg_price from products where vend_id = 'DLL01';
       +-----------+
       | avg_price |
       +-----------+
       | 4.240000  |
       +-----------+
       1 row in set (0.05 sec)
       
       mysql> select count(*) as num_items, min(prod_price) as price_min, max(prod_price) as price_max, avg(prod_price) as price_avg from products;
       +-----------+-----------+-----------+-----------+
       | num_items | price_min | price_max | price_avg |
       +-----------+-----------+-----------+-----------+
       |         9 | 3.49      | 11.99     | 6.823333  |
       +-----------+-----------+-----------+-----------+
       1 row in set (0.40 sec)
       ```

   - distinct不能用于count(*)，只能用于指定列名的count()

# 10、分组数据

1.  

2.  创建分组：使用group by

   - 示例：

     ```mysql
     mysql> select vend_id, count(*) as num_prods from products group by vend_id;
     +---------+-----------+
     | vend_id | num_prods |
     +---------+-----------+
     | BRS01   |         3 |
     | DLL01   |         4 |
     | FNG01   |         2 |
     +---------+-----------+
     3 rows in set (0.11 sec)
     
     ```

   - group by子句可以包含任意数目的列，因而可以对分组进行嵌套。

     - 示例：

       ```mysql
       mysql> select vend_id, prod_price, count(*) as num_prods from products group by vend_id, prod_price;
       +---------+------------+-----------+
       | vend_id | prod_price | num_prods |
       +---------+------------+-----------+
       | BRS01   | 5.99       |         1 |
       | BRS01   | 8.99       |         1 |
       | BRS01   | 11.99      |         1 |
       | DLL01   | 3.49       |         3 |
       | DLL01   | 4.99       |         1 |
       | FNG01   | 9.49       |         2 |
       +---------+------------+-----------+
       6 rows in set (0.09 sec)
       ```

   - group by 嵌套分组时，所以不能指定不在分组里的列的数据。

   - group by每一列都必须是检索列或有效的表达式（不能是聚合函数）。如果在select中使用表达式，必须在group by子句中指定相同的表达式，不能使用别名。

   - 大多数sql实现不允许group by列带有变长可变的数据类型。

   - null列的行将作为一个分组返回。

   - group by在where之后，order by之前。

3. 过滤分组

   - where过滤行。

   - having过滤分组。having可以代替where的所有功能。

   - 示例：

     ```mysql
     mysql> select vend_id, count(*) as num_prods from products group by vend_id having num_prods >= 4;
     +---------+-----------+
     | vend_id | num_prods |
     +---------+-----------+
     | DLL01   |         4 |
     +---------+-----------+
     1 row in set (0.10 sec)
     
     mysql> select vend_id, count(*) as num_prods from products where prod_price >= 4 group by vend_id having count(*) >= 2;
     +---------+-----------+
     | vend_id | num_prods |
     +---------+-----------+
     | BRS01   |         3 |
     | FNG01   |         2 |
     +---------+-----------+
     2 rows in set (0.12 sec)
     ```

4. 分组和排序

# 11、使用子查询

1.  

2.  利用子查询进行过滤

   - 示例：

     ```mysql
     select cust_name, cust_contact 
     from customers 
     where cust_id in ('1000000004', '1000000005');
     
     select cust_name, cust_contact 
     from customers 
     where cust_id in (select cust_id 
     					from orders 
     					where order_num in (20007, 20008));
     													
     select cust_name, cust_contact 
     from customers 
     where cust_id in (select cust_id 
     					from orders 
     					where order_num in (select order_num
     										from orderItems 
     										where prod_id = 'RGAN01'));
     ```

   - 子查询select只能返回单列。

3. 作为计算字段使用子查询

   - 示例：

     ```mysql
     select cust_name,cust_state, (select count(*)
     							from orders 
     							where orders.cust_id = customers.cust_id) as orders
     from customers
     order by cust_name;
     ```

# 12、联结表

1. 

2.  创建联结

   - 笛卡尔积

     - 示例：使用where语句联结，将vendors.vend_id 和 products.vend_id联结起来。

       ```mysql
       select vend_name, prod_name, prod_price 
       from vendors, products 
       where vendors.vend_id = products.vend_id;
       ```

     - 使用where子句可以使指定特定的第一个表的行和第二个表的行配对，而不是将第一个表的所有行和第二个表的所有行配对。

     - 没有联结条件的表关系返回的结果为笛卡尔积。检索处的行的数目将是第一个表中的行数乘以第二个表中的行数。

   - 内联结：也称等值联结（equijoin），基于两个表之间的相等测试。要明确指定联结类型。

     - 示例：两个SQL等价

       ```mysql
       select vend_name, prod_name, prod_price 
       from vendors, products 
       where vendors.vend_id = products.vend_id;
       
       select vend_name, prod_name, prod_price 
       from vendors inner join products 
       on vendors.vend_id = products.vend_id;
       
       Doll House Inc.	Fish bean bag toy	3.49
       Doll House Inc.	Bird bean bag toy	3.49
       Doll House Inc.	Rabbit bean bag toy	3.49
       Bears R Us	8 inch teddy bear	5.99
       Bears R Us	12 inch teddy bear	8.99
       Bears R Us	18 inch teddy bear	11.99
       Doll House Inc.	Raggedy Ann	4.99
       Fun and Games	King doll	9.49
       Fun and Games	Queen doll	9.49
       ```

   - 联结多个表

     - 联结多个表会导致性能下降，联结的越多下降得更加厉害。

# 13、创建高级联结

1.  

2.  使用不同类型的联结

   1. 自联结self-join

      - 示例：

        ```mysql
        select c1.cust_id, c1.cust_name, c1.cust_contact 
        from customers as c1, customers as c2 
        where c1.cust_name = c2.cust_name 
        and c2.cust_contact = 'Jim Jones';
        
        1000000003	Fun4All	Jim Jones
        1000000004	Fun4All	Denise L. Stephens
        ```

      - 用自联结而不用子查询：许多DBMS处理联结都比子查询快得多。

   2. 自然连接：排除列的多次重复出现，使每列只返回一次。

      - 自然联结要求你只能选择那些唯一的列，一般通过对一个表使用通配符（select *），而对其他表的列使用明确的子集来完成。

      - 示例：

        ```mysql
        select c.*, o.order_num, o.order_date, oi.prod_id, oi.quantity, oi.item_price 
        from customers as c, orders as o, orderItems as oi 
        where c.cust_id = o.cust_id 
        and oi.order_num = o.order_num 
        and prod_id = 'RGAN01';
        
        1000000004	Fun4All	829 Riverside Drive	Phoenix	AZ	88888	USA	Denise L. Stephens	dstephens@fun4all.com	20007	2012-01-30 00:00:00	RGAN01	50	4.49
        1000000005	The Toy Store	4545 53rd Street	Chicago	IL	54545	USA	Kim Howard		20008	2012-02-03 00:00:00	RGAN01	5	4.99
        ```

   3. 外联结：联结包含哪些在相关表中没有关联的行。如对每个顾客下的订单进行计数，包括那些至今尚未下订单的顾客。

      - 示例：使用内联结完成

        ```mysql
        select customers.cust_id, orders.order_num
        from customers inner join orders 
        on customers.cust_id = orders.cust_id;
        
        1000000001	20005
        1000000001	20009
        1000000003	20006
        1000000004	20007
        1000000005	20008
        ```

      - 示例：使用外联结完成

        ```mysql
        select customers.cust_id, orders.order_num
        from customers left join orders 
        on customers.cust_id = orders.cust_id;
        
        1000000001	20005
        1000000001	20009
        1000000002	
        1000000003	20006
        1000000004	20007
        1000000005	20008
        ```

      - 有左联结、右联结、全外联结

3. 使用带聚集函数的联结

   - 示例：

     ```mysql
     select customers.cust_id, count(orders.order_num) as num_ord 
     from customers inner join orders 
     on customers.cust_id = orders.cust_id
     group by customers.cust_id;
     
     1000000001	2
     1000000003	1
     1000000004	1
     1000000005	1
     
     select customers.cust_id, count(orders.order_num) as num_ord 
     from customers left join orders 
     on customers.cust_id = orders.cust_id
     group by customers.cust_id;
     
     1000000001	2
     1000000002	0
     1000000003	1
     1000000004	1
     1000000005	1
     ```

# 14、组合查询

1. 组合查询：将多条SQL并（union）或复合查询（compound query）

   - 用于
     - 在一个查询中从不同的表返回结构数据；
     - 对一个表执行多个查询，按一个查询返回数据。

2. 创建组合查询：利用union操作符和多条SQL查询

   - 示例：

     ```mysql
     select cust_name, cust_contact, cust_email 
     from customers
     where cust_state in ('IL', 'IN', 'MI');
     
     Village Toys	John Smith	sales@villagetoys.com
     Fun4All	Jim Jones	jjones@fun4all.com
     The Toy Store	Kim Howard	
     
     select cust_name, cust_contact, cust_email
     from customers 
     where cust_name = 'Fun4All';
     
     Fun4All	Jim Jones	jjones@fun4all.com
     Fun4All	Denise L. Stephens	dstephens@fun4all.com
     
     
     select cust_name, cust_contact, cust_email 
     from customers
     where cust_state in ('IL', 'IN', 'MI')
     union 
     select cust_name, cust_contact, cust_email
     from customers 
     where cust_name = 'Fun4All';
     
     Village Toys	John Smith	sales@villagetoys.com
     Fun4All	Jim Jones	jjones@fun4all.com
     The Toy Store	Kim Howard	
     Fun4All	Denise L. Stephens	dstephens@fun4all.com
     ```

   -  union规则

     - union中的每个查询必须包含相同的列、表达式或聚集函数（不过，各个列不需要以相同的次序列出）
     - 列数据类型必须兼容：可以隐含转换的类型也可以。

   - 包含或取消重复的行：默认去掉重复的行。可以使用union all返回所有的行。

     - union all的工作where做不到，但是union的工作where都可以做到。

   - 对组合查询结果排序：使用union时只能使用一条order by子句，且在最后一个select语句之后。

     - 虽然order by子句在最后一条select之后，但是DBMS将它用来排序所有select语句返回的结果。

     - 示例：

       ```mysql
       select cust_name, cust_contact, cust_email 
       from customers
       where cust_state in ('IL', 'IN', 'MI')
       union
       select cust_name, cust_contact, cust_email
       from customers 
       where cust_name = 'Fun4All'
       order by cust_name, cust_contact;
       
       Fun4All	Denise L. Stephens	dstephens@fun4all.com
       Fun4All	Jim Jones	jjones@fun4all.com
       The Toy Store	Kim Howard	
       Village Toys	John Smith	sales@villagetoys.com
       ```

   - 有的还支出except(或minus)用来检索值在第一个表中存在而在第二个表中不存在的行；intersect可用来检索两个表中都存在的行。

# 15、插入

1. 数据插入

   1.  

   2.  

   3.  插入检索处的数据（insert select）

      ```mysql
      INSERT INTO customers(cust_id, cust_contact, cust_email, cust_name, cust_address, cust_city, cust_state, cust_zip, cust_country) 
      select cust_id, cust_contact, cust_email, cust_name, cust_address, cust_city, cust_state, cust_zip, cust_country from cust_new;
      ```

2. 从一个表复制到另外一个表：表是运行中创建的

   - 示例：

     ```mysql
     SELECT * 
     into cust_copy 
     from customers;
     
     #mysql支持这个
     create table cust_copy 
     select * from customers;
     ```

   - 任何select的选项和子句都可以使用包括where和group by

   - 可以利用联结多个表插入数据

   - 不管从多少个表中检索数据，数据都只能插入一个表中。

   - 可以作为实验新SQL使用。

# 16、更新删除数据

# 17、创建和操纵表

1. 创建表
   1.  
   2.  
   3.  指定默认值：应该指定默认值而不是NULL
2. 更新表
   - 复杂的表结构修改过程：
     - 用新的列布局创建一个新表
     - 用insert select语句从旧表复制数据到新表
     - 检验包含所需数据的新表
     - 重命名旧表
     - 用旧表名重命名新表
     - 创建触发器、存储过程、索引和外键。
3. 删除表

# 18、使用视图

1. 使用视图

   1.  外什么使用视图
      - 重用SQL
      - 简化复杂的SQL操作，封装细节
      - 使用表的一部分而不是整个表
      - 保护数据。进行权限管理
      - 更改数据格式和表示。视图可返回与底层表的表示和格式不同的数据。
      - 性能问题：视图不包含数据，因此大量使用视图可能导致性能问题。
   2. 视图的规则和限制
      - 视图可以嵌套
      - 许多DBMS禁止在视图查询中使用order by子句
      - 视图不能索引，不能有关联的触发器或默认值

2. 创建视图

   - 示例：

     ```mysql
     create view product_customers as 
     select cust_name, cust_contact, prod_id 
     from customers, orders, order_items 
     where customers.cust_id = orders.cust_id 
     and order_items.order_num = orders.order_num;
     
     select cust_name, cust_contact
     from product_customers
     where prod_id = 'BR01';
     
     Village Toys	John Smith
     Fun4All	Jim Jones
     ```

   - 使用视图重新格式化检索处的数据：比如某个格式化操作经常用，可以创建视图进行格式化。

     - 示例：

       ```mysql
       create view vendor_locations as 
       select concat(rtrim(vend_name), ' (', rtrim(vend_country), ')')  as vend_title
       from vendors;
       
       Bear Emporium (USA)
       Bears R Us (USA)
       Doll House Inc. (USA)
       Fun and Games (England)
       Furball Inc. (USA)
       Jouets et ours (France)
       ```

   - 使用视图过滤数据：应用于where子句

   - 使用视图与计算字段

# 22、约束

1. 约束
   1. 主键
   2. 外键
   3.  唯一约束
      - 表可以包含多个唯一约束
      - 唯一约束列可包含NULL值
      - 唯一约束列可以修改或更新
      - 唯一约束列的值可重复使用
      - 唯一约束列不能定义外键
2. 索引
   - 使用用于数据过滤和数据排序。如果经常以某种特定的顺序排序数据，可能适用于索引。
3. 触发器