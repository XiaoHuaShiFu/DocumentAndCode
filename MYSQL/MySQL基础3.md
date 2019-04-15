# MySQL基础3

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

   - 优化order by语句

     - 有序索引顺序扫描直接返回有序数据

       ```mysql
       explain select customer_id from customer order by store_id \G
       *************************** 1. row ***************************
                  id: 1
         select_type: SIMPLE
               table: customer
          partitions: NULL
                type: index
       possible_keys: NULL
                 key: idx_fk_store_id
             key_len: 1
                 ref: NULL
                rows: 599
            filtered: 100.00
               Extra: Using index
       ```

     - 返回数据进行排序，Filesort排序，即说明了进行了一个排序操作

       ```mysql
       explain select * from customer order by store_id \G
       *************************** 1. row ***************************
                  id: 1
         select_type: SIMPLE
               table: customer
          partitions: NULL
                type: ALL
       possible_keys: NULL
                 key: NULL
             key_len: NULL
                 ref: NULL
                rows: 599
            filtered: 100.00
               Extra: Using filesort
       ```

       - 虽然只是对索引进行扫描，但是在索引上进行了一次排序操作，索引仍然有Filesort

         ```mysql
         explain select store_id,email,customer_id from customer order by email \G
         *************************** 1. row ***************************
                    id: 1
           select_type: SIMPLE
                 table: customer
            partitions: NULL
                  type: index
         possible_keys: NULL
                   key: idx_storeid_email
               key_len: 154
                   ref: NULL
                  rows: 599
              filtered: 100.00
                 Extra: Using index; Using filesort
         ```

       - Filesort是将取得的数据在sort_buffer_size系统变量设置的内存排序区中进行排序，如果内存装载不下，它就回将磁盘上的数据进行分块排序。sort_buffer_size设置的排序区是每个线程独占的。

     - 优化目标：减少额外的排序，通过索引直接返回有序数据。where和order by使用相同的索引，并且order by的顺序和索引顺序相同，并且order by的字段都是升序或都是降序。

       - **例如：**优化器直接扫描idx_storeid_email索引返回排序完毕的记录。

         ```mysql
         explain select store_id , email , customer_id from customer where store_id = 1 order by email desc \G
         *************************** 1. row ***************************
                    id: 1
           select_type: SIMPLE
                 table: customer
            partitions: NULL
                  type: ref
         possible_keys: idx_fk_store_id,idx_storeid_email
                   key: idx_storeid_email
               key_len: 1
                   ref: const
                  rows: 326
              filtered: 100.00
                 Extra: Using where; Using index
         ```

       - **例如：**按照索引扫描的结果，对email进行逆序排序

         ```mysql
         explain select store_id , email , customer_id from customer where store_id >= 1 and store_id <= 3 orr der by email desc \G
         *************************** 1. row ***************************
                    id: 1
           select_type: SIMPLE
                 table: customer
            partitions: NULL
                  type: index
         possible_keys: idx_fk_store_id,idx_storeid_email
                   key: idx_storeid_email
               key_len: 154
                   ref: NULL
                  rows: 599
              filtered: 100.00
                 Extra: Using where; Using index; Using filesort
         ```

     - 索引使用

       - 下列SQL可以使用索引

         ```mysql
         select * from tbl_name order by key_part1,key_part2...;
         select * from tbl_name where key_part1 = 1 order by key_part1 desc, key_part2 desc;
         select * from tbl_name order by key_part1 desc, key_part2 desc;
         ```

       - 不使用索引

         ```mysql
         // asc和 desc混合
         select * from tbl_name order by key_part1 desc, key_part2 asc;
         //查询行关键字与 order by中所用的不相同
         select * from tbl_name where key2 = constant order by key1;
         //对不同的关键字使用 order by
         select * from tbl_name order by key1, key2;
         ```

     - Filesort优化

       - 两次扫描算法：先取出排序字段和行指针信息，在排序区sort buffer中排序，如果排序去sort buffer不够，则在临时表Temporary Table中存储排序结果。完成排序后根据行指针回表读取记录。
         - 优点：内存开销少。
         - 缺点：第二次读取导致大量随机I/O操作。
       - 一次扫描算法：一次读取满足条件的行的所有字段，然后在排序区sort buffer中排序后直接输出结果集。
         - 优点：快。
         - 缺点：内存开销大。
       - MySQL通过比较系统变量max_length_for_sort_data的大小和Query语句取出的字段总大小来判断使用哪种排序算法。适当的加大系统变量max_length_for_sort_data值可以让MySQL更多的选择一次扫描算法。
       - 尽量只使用必要的字段，select具体字段名字，减少排序去内存的使用。

   - group by优化

     - 默认情况下，group by col1,col2,...的字段都回进行排序。因此在group by 语句中显式的包含order by对性能没什么影响。如果想避免排序的消耗，可以指定order by null禁止排序。

   - 优化嵌套查询

     - 使用join而不是子查询：因为不需要再内存中创建临时表

       ```mysql
       explain select * from customer a left join payment b on a.customer_id = b.customer_id where b.customer_id is null \G
       *************************** 1. row ***************************
                  id: 1
         select_type: SIMPLE
               table: a
          partitions: NULL
                type: ALL
       possible_keys: NULL
                 key: NULL
             key_len: NULL
                 ref: NULL
                rows: 599
            filtered: 100.00
               Extra: NULL
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
               Extra: Using where; Not exists
       ```

   - or优化

     - or如果要利用索引，则or之间的每个条件都必须用到索引。

     - MySQL在处理独立字段含有OR字句的查询时，实际是对OR的各个字段分别查询后的结果进行了union操作。

       ```mysql
       explain select * from payment where payment_date='2006-02-15 22:15:23' or customer_id = 10 \G
       *************************** 1. row ***************************
                  id: 1
         select_type: SIMPLE
               table: payment
          partitions: NULL
                type: index_merge
       possible_keys: idx_fk_customer_id,idx_payment_date
                 key: idx_payment_date,idx_fk_customer_id
             key_len: 5,2
                 ref: NULL
                rows: 26
            filtered: 100.00
               Extra: Using sort_union(idx_payment_date,idx_fk_customer_id); Using where
       ```

     - 在处理复合索引列的时候，用不到索引

       ```mysql
       explain select * from payment where payment_date = '2005-05-25 11:30:37' or amount = 2.99 or last_update='2006-02-15 22:12:30'  \G
       *************************** 1. row ***************************
                  id: 1
         select_type: SIMPLE
               table: payment
          partitions: NULL
                type: ALL
       possible_keys: idx_payment_date
                 key: NULL
             key_len: NULL
                 ref: NULL
                rows: 16125
            filtered: 19.01
               Extra: Using where
       ```

   - 优化分页查询

     - 按照索引分页回表：避免直接对全表扫描

       **示例：**

       ```mysql
        explain select a.film_id, a.description from film a inner join (select film_id from film order by title limit 50, 5) b on a.film_id = b.film_id \G
       *************************** 1. row ***************************
                  id: 1
         select_type: PRIMARY
               table: <derived2>
          partitions: NULL
                type: ALL
       possible_keys: NULL
                 key: NULL
             key_len: NULL
                 ref: NULL
                rows: 55
            filtered: 100.00
               Extra: NULL
       *************************** 2. row ***************************
                  id: 1
         select_type: PRIMARY
               table: a
          partitions: NULL
                type: eq_ref
       possible_keys: PRIMARY
                 key: PRIMARY
             key_len: 2
                 ref: b.film_id
                rows: 1
            filtered: 100.00
               Extra: NULL
       *************************** 3. row ***************************
                  id: 2
         select_type: DERIVED
               table: film
          partitions: NULL
                type: index
       possible_keys: NULL
                 key: idx_title
             key_len: 767
                 ref: NULL
                rows: 55
            filtered: 100.00
               Extra: Using index
       
       ```

     - 把limit查询转换成某个位置的查询。

       ```mysql
       //如果使用limit 400,10这样的写法会全盘扫描
       select * from payment order by rental_id desc limit 400, 10
       
       //修改成先查找rental_id < 15640的再进行排序，可以减少查询的行数
       explain select * from payment where rental_id < 15640 order by rental_id desc limit 10 \G
       *************************** 1. row ***************************
                  id: 1
         select_type: SIMPLE
               table: payment
          partitions: NULL
                type: range
       possible_keys: fk_payment_rental
                 key: fk_payment_rental
             key_len: 5
                 ref: NULL
                rows: 8062
            filtered: 100.00
               Extra: Using index condition
       ```

   - 使用sql提示

     - sql_buffer_results：生成一个临时结果集，只要临时结果集生成后，表上的锁定均被释放。这能再遇到表锁问题时或要花很长时间将结果传给客户端时有所帮助，因为可以尽快释放锁资源。

       ```mysql
       select sql_buffer_results * from ...
       ```

     - use index：提供希望MySQLq去参考的索引列表

       ```mysql
       explain select count(*) from rental use index (rental_date) \G
       *************************** 1. row ***************************
                  id: 1
         select_type: SIMPLE
               table: rental
          partitions: NULL
                type: index
       possible_keys: NULL
                 key: rental_date
             key_len: 10
                 ref: NULL
                rows: 16008
            filtered: 100.00
               Extra: Using index
       ```

     - ignore index：让MySQL忽略某个索引

       ```mysql
        explain select count(*) from rental ignore index (idx_fk_staff_id) \G
       *************************** 1. row ***************************
                  id: 1
         select_type: SIMPLE
               table: rental
          partitions: NULL
                type: index
       possible_keys: NULL
                 key: idx_fk_customer_id
             key_len: 2
                 ref: NULL
                rows: 16008
            filtered: 100.00
               Extra: Using index
       ```

     - force index：强制MySQL使用一个特定索引。

       ```mysql
       explain select * from rental force index (idx_fk_inventory_id) where inventory_id > 1\G
       *************************** 1. row ***************************
                  id: 1
         select_type: SIMPLE
               table: rental
          partitions: NULL
                type: range
       possible_keys: idx_fk_inventory_id
                 key: idx_fk_inventory_id
             key_len: 3
                 ref: NULL
                rows: 8004
            filtered: 100.00
               Extra: Using index condition
       ```

6. 常用sql技巧

   - 正则表达式

   - 使用rand()提取随机行

     - 按照随机顺序检索数据行：

       ```mysql
       select * from category order by rand();
       ```

   - 利用group by和with rollup子句：能够检索处本组类的整体聚合信息。

     - **注：**在使用rollup时，不能同时使用order by子句进行结果排序。也就是rollup和order by是互斥的。
     - **注：**limit用在rollup后面。

     - **示例：**在支付表payment中，按照支付时间payment_date的年月、经手员工编号staff_id列分组对支付金额amount列进行聚合计算：

       ```mysql
       select date_format(payment_date, '%Y-%m'),staff_id,sum(amount) from payment group by date_format(payment_date, '%Y-%m'),staff_id;
       +------------------------------------+----------+-------------+
       | date_format(payment_date, '%Y-%m') | staff_id | sum(amount) |
       +------------------------------------+----------+-------------+
       | 2005-05                            |        1 |     2621.83 |
       | 2005-05                            |        2 |     2202.60 |
       | 2005-06                            |        1 |     4776.36 |
       | 2005-06                            |        2 |     4855.52 |
       | 2005-07                            |        1 |    14003.54 |
       | 2005-07                            |        2 |    14370.35 |
       | 2005-08                            |        1 |    11853.65 |
       | 2005-08                            |        2 |    12218.48 |
       | 2006-02                            |        1 |      234.09 |
       | 2006-02                            |        2 |      280.09 |
       +------------------------------------+----------+-------------+
       
       //比第一个多了更加多的聚合信息
       select date_format(payment_date, '%Y-%m'),ifnull(staff_id,''),sum(amount) from payment group by date_format(_format(payment_date, '%Y-%m'),staff_id with rollup;
       +------------------------------------+---------------------+-------------+
       | date_format(payment_date, '%Y-%m') | ifnull(staff_id,'') | sum(amount) |
       +------------------------------------+---------------------+-------------+
       | 2005-05                            | 1                   |     2621.83 |
       | 2005-05                            | 2                   |     2202.60 |
       | 2005-05                            |                     |     4824.43 |
       | 2005-06                            | 1                   |     4776.36 |
       | 2005-06                            | 2                   |     4855.52 |
       | 2005-06                            |                     |     9631.88 |
       | 2005-07                            | 1                   |    14003.54 |
       | 2005-07                            | 2                   |    14370.35 |
       | 2005-07                            |                     |    28373.89 |
       | 2005-08                            | 1                   |    11853.65 |
       | 2005-08                            | 2                   |    12218.48 |
       | 2005-08                            |                     |    24072.13 |
       | 2006-02                            | 1                   |      234.09 |
       | 2006-02                            | 2                   |      280.09 |
       | 2006-02                            |                     |      514.18 |
       | NULL                               |                     |    67416.51 |
       +------------------------------------+---------------------+-------------+
       ```

   - 用bit group functions做统计

     - 就是通过bit_or运算，把种类进行聚合or运算，如果有任何记录里有1，聚合后就会有一。如3（0011）和4（0100）bit_or后是7（0111）就是有这3个种类的商品。

       ```mysql
       //建表
       create table order_rab(id int ,customer_id int , kind int);
       //插入数据
       insert into order_rab values(1,1,5),(2,1,4);
       insert into order_rab values(3,2,3),(4,2,4);
       //统计所购买的商品种类
       select customer_id,bit_or(kind) from order_rab group by customer_id;
       
       //统计每次都会购买的商品
       select customer_id,bit_and(kind) from order_rab group by customer_id;
       ```

   - 数据库名、表名大小写问题

   - 使用外键需要注意的问题

# 19.优化数据库对象

   1. 优化表的数据类型

      - procedure analyse()：输出每一列信息会对数据表中的列的数据类型提出优化建议。

        ```mysql
        select * from tbl_name procedure analyse();
        //不要包含值多于16或者256个字节的enum类型提出建议
        select * from tbl_name procedure analyse(16,255);
        ```

   2. 通过拆分提高表的访问效率

      - 垂直拆分：把主码和一些列放到一个表里，然后把主码和另外的列放到另外一个表中。
        - 优点：数据行变小，数据页能存更多数据，减少查询时的io次数。
        - 缺点：需要管理冗余列，查询所有数据需要联合（join）操作。
      - 水平拆分：即通过一列或多列数据的值把数据行放到两个独立的表中。
        - 适用场景：
          - 表很大，降低查询时需要读的数据和索引的页数，同时页降低了索引的层数，提高查询速度。
          - 表中的数据本来就有独立性，例如，表中分别记录各个地区的数据或不同时期的数据，特别是有些数据常用，有一些不常用。
          - 需要把数据存放到多个介质上。
        - 缺点：需要查询多个表名，查询所有数据需要union操作。
        - 优点：当表中增加2~3被数据量，查询时索引层的读磁盘次数就增加一层，所以水平拆分要考虑数据的增长速度，根据实际情况决定是否需要对表进行水平拆分。

   3. 逆规范化

      - 增加冗余列：值在多个表中具有相同的列，避免查询时连接操作。
      - 增加派生列：值增加的列来自其他表中的数据，由其他表的数据经过计算生成。增加派生列作用是查询时减少连接操作，避免适用集函数。
      - 重新组表：把两个表重新组成一个表来减少连接而提高性能。
      - 分割表：

   4. 适用中间表提高统计查询速度

      - 操作步骤：
        - 创建中间表，与源表完全一致。
        - 把要统计的数据转移到中间表。
        - 在中间表上进行统计。
      - 优点：
        - 数据与源表隔离，不会对线上应用产生负面影响。
        - 中间表上可以灵活的添加索引或增加临时用的新字段，从而提高统计查询效率和辅助统计查询作用。

# 20.锁问题

1. 各存储引擎支持的锁类型

   | 引擎           | 锁类型 | 描述                                                         |
   | -------------- | ------ | ------------------------------------------------------------ |
   | MyISAM、MEMORY | 表级锁 | 开销小，加锁快；不会出现死锁；锁定粒度大，发生锁冲突的概率最高，并发度最低。 |
   | InnoDB         | 行级锁 | 开销大，加锁慢；会出现死锁；锁定粒度最小，发生锁冲突的概率最低，并发度最高。 |
   | BDB            | 页面锁 | 开销和加锁时间界于表锁和行锁之间；会出现死锁；锁定粒度界于表锁和行锁之间，并发度一般。 |

   - 表锁适合以查询为主，只有少量按索引条件更新数据的应用，如Web应用；
   - 行锁适合于由大量按索引条件并发更新少量不同数据，同时又有并发查询的应用，如一些在线事务处理（OLTP）系统。

2. MyISAM表锁

   - 查询表级锁争用情况：如果Table_locks_waited较高说明存在着较严重的表锁争用情况。

     ```mysql
     show status like 'table%';
     +----------------------------+-------+
     | Variable_name              | Value |
     +----------------------------+-------+
     | Table_locks_immediate      | 96726 |
     | Table_locks_waited         | 0     |
     | Table_open_cache_hits      | 190   |
     | Table_open_cache_misses    | 10    |
     | Table_open_cache_overflows | 0     |
     +----------------------------+-------+
     ```

   - MySQL表级锁模式

     - 表锁兼容性：

       |      | 读锁 | 写锁 |
       | ---- | ---- | ---- |
       | None | 是   | 是   |
       | 读锁 | 是   | 否   |
       | 写锁 | 否   | 否   |

     - **示例：**和Java的锁差不多

       ```mysql
       lock table film_text write;
       insert into film_text (film_id,title) values(1003,'test');
       update film_text set title ='text111'  where film_id = 1003;
       unlock tables;
       ```

     - MyISAM在执行查询语句（select）前，会自动给涉及的所有表加读锁，在执行更新（update、delete、insert）前，会自动给涉及的表加写锁，并不需要用户显式添加。

       - 给MyISAM表显式加锁，一般是为了在一定程度模拟事务操作，实现对某一时间多个表的一致性读取。

         **例如：**加锁，防止在查询出第一句的结果后，第二句执行时表已经变了。

         ```mysql
         lock table orders read local, order_detail read local;
         select sum(total) from orders;
         select sum(subtotal) from order_detail;
         unlock tables;
         ```

         - local选项是MyISAM表并发插入条件的情况下，运行其他用户在表尾并发插入数据。

         - lock tables 给表显式加锁时，必须同时取得所有涉及表的锁（不会出现死锁的原因）。且不支持表升级，也就是执行lock tables后，只能访问显式加锁的这些表，不能访问为加锁的表。

         - 如果加的是读锁，只能执行查询操作。

         - 同一各表在SQL语句中出现多少次，就要通过与SQL语句中相同的别名锁定多少次。

           - **错误示例：**

             ```mysql
             lock tables payment read local;
             select a.amount from payment a where a.customer_id = 10000;
             ERROR 1100 (HY000): Table 'a' was not locked with LOCK TABLES
             ```

           - **正确示例：**

             ```mysql
             lock tables payment as a read local;
             select a.amount from payment a where a.customer_id = 10000;
             ```

   - 并发插入（Concurrent inserts）

     - MyISAM存储引擎系统变量concurrent_insert，专门控制并发插入的行为：
       - 当concurrent_insert为0时，不允许并发插入。
       - 当concurrent_insert为1时，如果MyISAM表中没有空洞（即表中没有被删除的行），MyISAM允许在一个进程读表的同时，另外一个进程从表尾插入记录。默认设置。
       - 当concurrent_insert为2时，无论MyISAM表中有没有空洞，都允许在表尾并发插入记录。

   - MyISAM的锁调度

     - 如果一个进程请求MyISAM的读锁，同时另外一个请求写锁，那么写进程先获得锁。
     - 而且即使读请求先到锁等待队列，写请求后到，写锁也会查到读锁请求之前。因为MySQL认为写请求一般比读请求重要。这也是MyISAM不适合有大量更新操作和查询操作应用的原因。
     - 可以设置来调节MyISAM的调度行为：
       - 指定启动参数low-priority-updates，使MyISAM引擎默认给予读请求优先权力。
       - 执行命令set low-priority-updates=1，使该连接发出的更新请求优先级降低。
       - 通过指定insert、update、delete语句的low_priority属性，降低该语句的优先级。
     - 也可以指定系统参数max_write_lock_count设置一个合适的值，当一个表的读锁达到这个值后，MySQL就暂时将写请求的优先级降低，给读进程一定获得锁的机会。

3. InnoDB锁问题

   - 事务（Transaction）及其ACID属性：事务是由一组SQL组成的逻辑处理单元，事务具有以下4各属性：

     - 原子性（Atomicity）：事务是一个原子操作单元，其对数据的修改，要么全部执行，要么全部都不执行。
     - 一致性（Consistent）：在事务开始和完成时，数据都必须保持一致状态。这意味着所有相关的数据规则都必须应用于事务的修改，以保证数据的完整性；事务结束时，所有的内部数据结构页必须是正确的。
     - 隔离性（Isolation）：事务处理过程中的中间状态对外部是不可见的。
     - 持久性（Durable）：事务完成后，它对数据的修改是永久性的，即使出现系统故障也能够保持。

   - 并发事务处理带来的问题

     - 更新丢失（Lost Update）：当两个或多个事务选择同一行，然后基于最初选的的值更新该行时，每个事务都不知道其他事务的存在，最后的更新覆盖了由其他事务所做的更新。
     - 脏读（Dirty Reads）：一个事务正在对一条记录做修改，这个事务完成并提交前，这条记录的数据就处于不一致状态；这时，另外一个事务也来读取同一条记录，如果不加控制，第二个事务读取了这些”脏“数据，并据此做进一步处理，就会产生未提交的数据依赖关系。
     - 不可重复读（Non-Repeatable Reads）：一个事务在读取某些数据后的某个时间，再次读取以前读过的数据，却发现其读出的数据已经发生了改变或某些记录已经被删除了。
     - 幻读（Phantom Reads）：一个事务按相同的查询条件重新读取以前检索过的数据，却发现其他事务插入了满足查询条件的新数据。

     
