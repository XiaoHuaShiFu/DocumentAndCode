# myMySQL基础3

# 18.SQL优化

1. 使用show [session|global] status like 'xxxx%';查询服务器状态。

   ```mysql
   show status like 'Com_%';
   ```

   - Com_xxx表示每个xxx语句的执行次数
     - 如Com_select、Com_insert、Com_update、Com_delete等，表示所有存储引擎的表操作次数。
   - 也可以查询Innodb表的操作次数，如：show status like 'Innodb_%';
   - Com_commit和Com_rollback可以了解事务提交和回滚的情况。
   - Connections：试图连接MySQL服务器的次数。
   - Uptime：服务器工作时间。
   - Slow_queries：慢查询次数。

2. 使用explain分析sql执行计划

   - 例如：

     ```mysql
     explain select sum(amount) from customer a, payment b where 1 = 1 and a.customer_id = b.customer_id and email = 'JANE.BENNETT@sakilacustomer.org' \G
     
     *************************** 1. row ***************************
                id: 1
       select_type: SIMPLE
             table: a
        partitions: NULL
              type: ALL
     possible_keys: PRIMARY
               key: NULL
           key_len: NULL
               ref: NULL
              rows: 599
          filtered: 10.00
             Extra: Using where
     *************************** 2. row ***************************
                id: 1
       select_type: SIMPLE
             table: b
        partitions: NULL
              type: ref
     possible_keys: idx_fk_customer_id
               key: idx_fk_customer_id
           key_len: 2
               ref: sakila.a.customer_id
              rows: 26
          filtered: 100.00
             Extra: NULL
     ```

     - select_type：表示select的类型，有simple（简单表，即不适用表连接或者子查询）、primary（主查询，即外层的查询）、union（union中的第二个或者后面的查询语句）、subquery（子查询中的第一个select）

     - table：输出结果集的表

     - type：表示MySQL在表中找到所需行的方式，也叫访问方式。

       下表从上到下性能从最差到最好：

       | 类型            | 描述                                                         |
       | --------------- | ------------------------------------------------------------ |
       | all             | 全表扫描。例如：select * from film where rating > 9 ;        |
       | index           | 全索引扫描，遍历整个索引来查询匹配的行。例如：select title from film; |
       | range           | 索引范围扫描，常见于<、<=、>、>=、between等操作符。例如：select * from payment where customer_id >= 300 and customer_id <= 350; |
       | ref             | 使用非唯一索引扫描或唯一索引的前缀扫描，返回某个单独值的记录行。例如：select b.*,a.* from payment a, customer b where a.customer_id = b.customer_id; |
       | eq_ref          | 类似ref，只是使用的索引是唯一索引，对于每个索引键值，表中只有一条记录匹配；简单来说，就是多表连接中使用primary key或者unique index作为关联条件。例如：select * from film a, film_text b where a.film_id = b.film_id; |
       | const/system    | 单表中最多右一个匹配行，所以这个匹配行中的其他列的值可以被优化器在当前查询中当作常量来处理。例如主键primary key 或 唯一索引unique index进行查询。例如：select * from (select * from customer where email = 'AARON.SELBY@sakilacustomer.org') b; |
       | null            | 不用访问表或者索引，直接就能得到结果。例如：select 1 from dual; |
       | ref_or_null     | 与ref区别在于条件中包含对null的查询                          |
       | index_merge     | 索引合并优化                                                 |
       | unique_subquery | in的后面是一个查询主键字段的子查询                           |
       | index_subquery  | in的后面是一个查询非唯一索引字段的子查询                     |

     - possible_key：表示查询时可能使用的索引。

     - key：表示实际使用的索引。

     - key_len：使用到索引字段的长度。

     - rows：扫描行的数量。

     - extra：执行情况的说明和描述。

   - 使用show profile分析SQL

     - 开启profiling：set profiling=1;

     - show profiles：查看查询信息

       ```mysql
       mysql> show profiles;
       +----------+------------+------------------------------+
       | Query_ID | Duration   | Query                        |
       +----------+------------+------------------------------+
       |        1 | 0.00286725 | select count(*) from payment |
       +----------+------------+------------------------------+
       ```

     - show profile for query query_id：查看执行过程中线程的每个状态和消耗的时间。

       ```mysql
       mysql> show profile for query 1;
       +----------------------+----------+
       | Status               | Duration |
       +----------------------+----------+
       | starting             | 0.000088 |
       | checking permissions | 0.000008 |
       | Opening tables       | 0.000019 |
       | init                 | 0.000016 |
       | System lock          | 0.000009 |
       | optimizing           | 0.000005 |
       | statistics           | 0.000014 |
       | preparing            | 0.000018 |
       | executing            | 0.000003 |
       | Sending data         | 0.002627 |
       | end                  | 0.000010 |
       | query end            | 0.000010 |
       | closing tables       | 0.000009 |
       | freeing items        | 0.000015 |
       | cleaning up          | 0.000017 |
       +----------------------+----------+
       ```

       - **注：**其中Sending data表示MySQL线程开始访问数据行并把结果返回给客户端，因此要左大量磁盘读取操作。

       - 可以进一步选择all、cpu、block io、context switch、page faults等明细类型来查看MySQL在使用什么资源上耗费了过高的时间。

         **例如：**

         ```mysql
         show profile cpu for query query_id;
         ```

       - 可以使用source查看SQL解析执行过程中每个步骤对于的源码的文件、函数名以及具体的源文件函数

         **例如：**

         ```mysql
         show profile source for query query_id;
         ```

   - 使用trace分析优化器如何选择执行计划

     - 使用方式：首先打开trace，设置格式位json，设置trace最大能够使用的内存大小，避免解析通过因默认内存过小而不能够完整显示。

     - ```mysql
       set optimizer_trace='enabled=on',end_markers_in_json=on;
       set optimizer_trace_max_mem_size=1000000;
       //执行想做trace的SQL语句
       select ....
       //检查information_schema.optimizer_trace
       select * from information_schema.optimizer_trace \G
       ```

3. 索引问题

   - 索引类型

     | 索引类型  | 描述                            | MyISAM | InnoDB   | Memory |
     | --------- | ------------------------------- | ------ | -------- | ------ |
     | B-Tree    | 适用于KV查询                    | 支持   | 支持     | 支持   |
     | HASH      | b不适合范围查询（<、>、<=、>=） |        |          | 支持   |
     | R-Tree    | 主要用于地理空间数据类型        | 支持   |          |        |
     | Full-text |                                 | 支持   | 暂不支持 |        |

   - 前缀索引可以大大缩小索引文件的大小，但是在排序OrderBy和 Group By操作时无法使用。

   - Memory/Heap引擎只有在“=”的条件下才会适用索引。

   - extra类型

     | 类型        | 描述                                         |
     | ----------- | -------------------------------------------- |
     | Using where | 通过索引回表查询数据                         |
     | Using index | 直接访问索引就可以获取到所需数据，不需要回表 |
     |             |                                              |
     |             |                                              |

     

   - 索引使用场景

     - 匹配全值（Match the full value）：对索引中所有列都指定具体指，即是对索引中的所有列都有等值匹配的条件。

     - 匹配值的范围查询（Match a range of values）：对索引的值能够进行范围查找。

     - 匹配最左前缀（Match a leftmost prefix）：仅使用索引的最左边列进行查找。

       **例如：**

       ```mysql
       //创建这个索引
       alter table payment add index idx_payment_date (
           payment_date,amount,last_update);
       //只用索引的第一行payment_date去匹配
       select * from payment where payment_date = '2006-02-14 15:16:03'  and last_update='2006-02-15 5 22';
       
                  id: 1
         select_type: SIMPLE
               table: payment
          partitions: NULL
                type: ref
       possible_keys: idx_payment_date
                 key: idx_payment_date
             key_len: 5
                 ref: const
                rows: 182
            filtered: 10.00
               Extra: Using index condition
       
       select * from payment where amount = 3.98 and last_update='2006-02-15 22:12:32';
       
       		   id: 1
         select_type: SIMPLE
               table: payment
          partitions: NULL
                type: ALL
       possible_keys: NULL
                 key: NULL
             key_len: NULL
                 ref: NULL
                rows: 16086
            filtered: 1.00
               Extra: Using where
       ```

     - 仅仅对索引进行查询（Index only query）:当查询列都在索引的字段中时，查询的效率更高；

       **示例：**其中last_update在索引中

       ```mysql
       explain select last_update from payment where payment_date = '2006-02-14 15:16:03' and amount = 3.98 \G
       
                  id: 1
         select_type: SIMPLE
               table: payment
          partitions: NULL
                type: ref
       possible_keys: idx_payment_date
                 key: idx_payment_date
             key_len: 8
                 ref: const,const
                rows: 8
            filtered: 100.00
               Extra: Using index
       ```

     - 匹配列前缀（Match a column prefix）：仅仅使用索引中的第一列，并且只包含索引第一列开头的一部分进行查询。

       **示例：**查询以AFRICAN开头的电影

       ```mysql
       create index idx_title_desc_part on film_text (title(10), description(20));
       
       explain select title from film_text where title like 'AFRICAN%' \G
       
                  id: 1
         select_type: SIMPLE
               table: film_text
          partitions: NULL
                type: range
       possible_keys: idx_title_desc_part,idx_title_description
                 key: idx_title_desc_part
             key_len: 32
                 ref: NULL
                rows: 1
            filtered: 100.00
               Extra: Using where
       ```

     - 索引匹配部分精确而其他部分进行范围匹配（Match one part exactly and match a range on another part）

       **例如：**ref表示精确查找，选择idx_rental_date加速查询，Using index表示inventory_id使用覆盖索引扫描。

       ```mysql
       explain select inventory_id from rental where rental_date='2006-02-14 15:16:03' and customer_id >= 300 and customer_id <= 400 \G
       
                  id: 1
         select_type: SIMPLE
               table: rental
          partitions: NULL
                type: ref
       possible_keys: rental_date,idx_fk_customer_id
                 key: rental_date
             key_len: 5
                 ref: const
                rows: 182
            filtered: 16.85
               Extra: Using where; Using index
       ```

     - 如果是列名是索引，那么使用column_name is null 就会使用索引。

       **例如：**查询支付表payment的租赁编号rental_id字段为空记录就用到了索引；

       ```mysql
       explain select * from payment where rental_id is null \G
       
                  id: 1
         select_type: SIMPLE
               table: payment
          partitions: NULL
                type: ref
       possible_keys: fk_payment_rental
                 key: fk_payment_rental
             key_len: 5
                 ref: const
                rows: 5
            filtered: 100.00
               Extra: Using index condition
       ```

     - Index Condition Pushdown（ICP）特性，进一步优化了查询。Pushdown表示下放，某些情况下的条件过滤操作下放到存储引擎。

       **示例：**直接在检索的时候，把不符合条件的customer_id过滤掉。

       ```mysql
       explain select * from rental where rental_date = '2006-02-14 15:16:03' and customer_id >= 300 and customer_id <= 400 \G
       
                  id: 1
         select_type: SIMPLE
               table: rental
          partitions: NULL
                type: ref
       possible_keys: rental_date,idx_fk_customer_id
                 key: rental_date
             key_len: 5
                 ref: const
                rows: 182
            filtered: 16.85
               Extra: Using index condition
       ```

   - 存在索引但不能使用索引的场景

     - 以%开头的like查询无法利用B-Tree索引

       - 解决方法：一般情况下，索引都会比表小，扫描索引比扫描表更快，而InnoDB表上二级索引idx_last_name实际上存储字段last_name还有主键actor_id，那么可以先扫描二级索引idx_last_name获得满足条件的last_name like '%NI%'的主键actor_id列表，之后根据主键徽标去检索记录，避开全表扫描actor。（理论上比全表扫描快一点）

         ```mysql
         explain select * from (select actor_id from actor where last_name like '%NI%') a, actor b where a.actor_id = b.actor_id \G
         *************************** 1. row ***************************
                    id: 1
           select_type: SIMPLE
                 table: actor
            partitions: NULL
                  type: index
         possible_keys: PRIMARY
                   key: idx_actor_last_name
               key_len: 137
                   ref: NULL
                  rows: 200
              filtered: 11.11
                 Extra: Using where; Using index
         *************************** 2. row ***************************
                    id: 1
           select_type: SIMPLE
                 table: b
            partitions: NULL
                  type: eq_ref
         possible_keys: PRIMARY
                   key: PRIMARY
               key_len: 2
                   ref: sakila.actor.actor_id
                  rows: 1
              filtered: 100.00
                 Extra: NULL
         ```

     - 数据类型出现隐式转换时也不会使用索引，特别当列类型是字符串，那么一定记得再where条件中把字符串常量值用引号括起来，否则索引将用不到，因为MySQL默认把输入的常量值进行转换以后才进行检索。

       **示例：**

       ```mysql
       explain select * from actor where last_name = 1 \G
       *************************** 1. row ***************************
                  id: 1
         select_type: SIMPLE
               table: actor
          partitions: NULL
                type: ALL
       possible_keys: idx_actor_last_name
                 key: NULL
             key_len: NULL
                 ref: NULL
                rows: 200
            filtered: 10.00
               Extra: Using where
       ```

     - 符合索引的情况下，如果查询条件不包含索引列最左边部分，则不会使用复合索引。

       **示例：**

       ```mysql
       explain select * from payment where amount = 3.98 and last_update='2006-02-15 22:12:32' \G
       *************************** 1. row ***************************
                  id: 1
         select_type: SIMPLE
               table: payment
          partitions: NULL
                type: ALL
       possible_keys: NULL
                 key: NULL
             key_len: NULL
                 ref: NULL
                rows: 16086
            filtered: 1.00
               Extra: Using where
       ```

     - 如果全表扫描比索引快，则不使用索引。在查询时，筛选性越高越容易使用到索引，筛选性越低越不容易使用索引。

       **例如：**

       ```mysql
       update film_text set title = concat('s',title);
       
       explain select * from film_text where title like 'S%' \G
       ```

     - 用or分割开的条件，如果or前的条件中的列有索引，而后面的列中没有索引，那么涉及的索引都不会被用到。因为or后面的条件中没有索引，那么后面的查询肯定要全表扫描，就没必要再扫描一次索引增加io访问。

       **例如：**

       ```mysql
       explain select * from payment where customer_id = 203 or amount = 3.96 \G
       *************************** 1. row ***************************
                  id: 1
         select_type: SIMPLE
               table: payment
          partitions: NULL
                type: ALL
       possible_keys: idx_fk_customer_id
                 key: NULL
             key_len: NULL
                 ref: NULL
                rows: 16086
            filtered: 10.15
               Extra: Using where
       ```

   - 查看索引使用情况

     Handler_read_rnd_next的值越高意味着查询运行低效，并且应该建立索引补救。

     ```mysql
     show status like 'handler_read%';
     ```

4. 优化方法

   - analyze、check、optimize、alter table执行期间将对表进行锁定，因此一定不要在数据库繁忙时执行。

   - 定期分析表和检查表

     分析和存储表的关键字分布。对MyISAM、BDB和InnoDB有用

     ```mysql
     analyze [local|no_write_to_binlog] table tbl_name [,tbl_name]...
     ```

     检查一个或多个表（也可以是视图）是否有错误。对MyISAM和InnoDB有用

     ```mysql
     check table tbl_name [,tbl_name]...[option]...option = {quick|fast|medium|extended|change}
     ```

   - 定期优化表：如果已经删除了表的一大部分，或者如果已经对含有可变长度的行的表（varchar、blob或text列的表）进行了很多更改，则是哦那个optimize table来优化。这个命令可以将表中的空间碎片进行合并，并且可以消除由于删除或者更新造成的空间浪费，但optimize table命令只对MyISAM、BDB和InnoDB表起作用。

     ```mysql
     optimize [local|no_write_to_binlog] table tbl_name [,tbl_name]...
     ```

     - 对于InnoDB引擎来说，通过设置innodb_file_per_table参数，设置innoDB为独立表空间模式，这样每个数据库的每个表都会生成一个独立的ibd文件，用于存储表的数据和索引，这样可以一定程度上减轻InnoDB表的空间回收问题。

     - 另外，在删除大量数据后，InnoDB表可以通过alter table但是不修改引擎的方式来回收不用的空间。

       ```mysql
       alter table payment engine=innodb;
       ```

5. sql优化

   - 大批量插入：对于MyISAM存储引擎的表，可以通过下面方式快速地导入大量数据。

     ```mysql
     //使用 disable keys和 enable keys来打开或关闭MyISAM表非唯一索引的更新。
     alter table tbl_name disable keys; 
     load data infile '/home/mysql/xxxx.txt' into table film_test2;
     alter table tbl_name enable keys;
     ```

     - 对于InnoDB类型的表是按照主键的顺序保存的，所以导入的数据按照主键的顺序排序，可以有效的提高导入数据的效率。
     - 在导入数据前执行set unique_checks=0，关闭唯一性校验，导入结束后执行set unique_checks=1可以提高导入效率。
     - 如果应用使用自动提交方式，建议在导入前执行set autocommit=0，关闭自动提交，结束后再执行set autocommit=1。

   - 优化insert语句

     - 一次批量插入。可以减少客户端与数据库之间的连接、关闭等消耗。
     - 如果从不同用户插入很多行，可以通过使用insert delayed语句加快速度。delayed是让insert被存放到内存的队列中，并没有写入磁盘；low_priority则是在所有用户对表的读写完成后才进行插入。
     - 将索引文件和数据文件分在不同的磁盘上存放，利用建表中的选项。
     - 批量插入，可以增加bulk_insert_buffer_size变量值的方法来提高速度，但是只能对MyISAM表使用。
     - 从一个文本文件装载一个表时，使用load data infile比insert快20倍。
