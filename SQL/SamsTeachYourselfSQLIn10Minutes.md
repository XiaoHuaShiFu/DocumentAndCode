

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



