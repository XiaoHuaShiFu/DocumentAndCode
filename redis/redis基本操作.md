# 1、key pattern 查询相应的key

1. redis允许模糊查询key　　有3个通配符  *、?、[]
2. randomkey：返回随机key　　
3. type key：返回key存储的类型
4. exists key：判断某个key是否存在
5. del key：删除key
6. rename key newkey：改名
7. renamenx key newkey：如果newkey不存在则修改成功
8. move key 1：将key移动到1数据库
9. ttl key：查询key的生命周期（秒）
10. expire key 整数值：设置key的生命周期以秒为单位
11. pexpire key 整数值：设置key的生命周期以毫秒为单位
12. pttl key：查询key 的生命周期（毫秒）
13. perisist key：把指定key设置为永久有效

# 2、字符串类型的操作

1. set key value [ex 秒数] [px 毫秒数] [nx/xx]
   - 如果ex和px同时写，则以后面的有效期为准
   - nx：如果key不存在则建立
   - xx：如果key存在则修改其值
2. get key：取值
3. mset key1 value1 key2 value2 一次设置多个值
4. mget key1 key2 ：一次获取多个值
5. setrange key offset value：把字符串的offset偏移字节改成value
   - 如果偏移量 > 字符串长度，该字符自动补0x00
6. append key value ：把value追加到key 的原值上
7. getrange key start stop：获取字符串中[start, stop]范围的值
   - 对于字符串的下标，左数从0开始，右数从-1开始
   - 注意：当start>length，则返回空字符串
   - 当stop>=length，则截取至字符串尾
   - 如果start所处位置在stop右边，则返回空字符串
8. getset key nrevalue：获取并返回旧值，在设置新值
9. incr key：自增，返回新值，如果incr一个不是int的value则返回错误，incr一个不存在的key，则设置key为1
10. incrby key 2：跳2自增
11. incrbyfloat by 0.7： 自增浮点数　
12. setbit key offset value：设置offset对应二进制上的值，返回该位上的旧值
    - 注意：如果offset过大，则会在中间填充0
    - offset最大到 2^32-1，即可推出最大的字符串为512M
13. bitop operation destkey key1 [key2..]    对key1 key2做opecation并将结果保存在destkey上
    - opecation可以是AND OR NOT XOR
14. strlen key：取指定key的value值的长度
15. setex key time value：设置key对应的值value，并设置有效期为time秒 
16. setnx key value 
    - 在键不存在的情况下为键设置值

# 3、链表操作

　　Redis的list类型其实就是一个每个子元素都是string类型的双向链表，链表的最大长度是2^32。list既可以用做栈，也可以用做队列。

　　list的pop操作还有阻塞版本，主要是为了避免轮询

- 有序，且占用的内存是最少的。

1. lpush key value：把值插入到链表头部

2. rpush key value：把值插入到链表尾部

3. lpop key ：返回并删除链表头部元素

4. rpop key： 返回并删除链表尾部元素

5. lrange key start stop：返回链表中[start, stop]中的元素  lrange sort-input 0 -1

6. lrem key count value：从链表中删除value值，删除count的绝对值个value后结束

   - count > 0 从表头删除　　count < 0 从表尾删除　　count=0 全部删除

7. ltrim key start stop：剪切key对应的链接，切[start, stop]一段并把改制重新赋给key

8. lindex key index：返回index索引上的值

9. llen key：计算链表的元素个数

10. linsert key after|before search value：在key 链表中寻找search，并在search值之前|之后插入value

11. rpoplpush source dest：把source 的末尾拿出，放到dest头部，并返回单元值
    - 应用场景： task + bak 双链表完成安全队列　![https://images0.cnblogs.com/blog/649054/201502/211807189718832.png](file:///C:/Users/lenovo/AppData/Local/Temp/msohtmlclip1/01/clip_image001.png)
    - 业务逻辑： rpoplpush task bak：接收返回值并做业务处理，如果成功则rpop bak清除任务，如果不成功，下次从bak表取任务。

12. brpoplpush source dest timeout：把source 的末尾拿出，放到dest头部，并返回单元值,如果source为空，那么在timeout秒之内阻塞并等待可弹出的元素出现。

13. brpop，blpop key [key1...]timeout：等待弹出key的尾/头元素

    - 可接受多个列表作为参数

    - timeout为等待超时时间，如果timeout为0则一直等待下去
    - 应用场景：长轮询ajax，在线聊天时能用到

# 4、hash

　　Redis hash 是一个string类型的field和value的映射表，它的添加、删除操作都是O(1)（平均）。hash特别适用于存储对象，将一个对象存储在hash类型中会占用更少的内存，并且可以方便的存取整个对象。

　　配置： hash_max_zipmap_entries 64 #配置字段最多64个

　　　　　 hash_max_zipmap_value 512 #配置value最大为512字节

1. hset myhash field value：设置myhash的field为value
2. hsetnx myhash field value：不存在的情况下设置myhash的field为value
3. hmset myhash field1 value1 field2 value2：同时设置多个field
4. hget myhash field：获取指定的hash field
5. hmget myhash field1 field2：一次获取多个field
6. hincrby myhash field 5：指定的hash field加上给定的值
7. hexists myhash field：测试指定的field是否存在
8. hlen myhash：返回hash的field数量
9. hdel myhash field：删除指定的field
10. hkeys myhash：返回hash所有的field
11. hvals myhash：返回hash所有的value
12. hgetall myhash：获取某个hash中全部的field及value　
13. hincrbyfloat key-name key increment：将key存储的值加上浮点数increment

# 5、set

　　特点：无序性、确定性、唯一性

1. sadd key value1 value2：往集合里面添加元素
2. smembers key：获取集合所有的元素
3. srem key value：删除集合某个元素
4. spop key：返回并删除集合中1个随机元素（可以坐抽奖，不会重复抽到某人）
5. srandmember key：随机取一个元素
6. sismember key value：判断集合是否有某个值
7. scard key：返回集合元素的个数
8. smove source dest value：把source的value移动到dest集合中
9. sinter key1 key2 key3：求key1 key2 key3的交集
10. sunion key1 key2：求key1 key2 的并集
11. sdiff key1 key2：求key1 key2的差集
12. sinterstore des key1 key2：求key1 key2的交集并存在res里
13. sdiffstore dest key-name1 key-name2：将那些存在第一个集合但不存在于其他集合中的元素（数学上的差集运算）存储到dest里面。
14. sunionstore dest key-name1 key-name2 将那些至少存在于一个集合中的元素（数学上的并集运算）存储到dest里面。

# 6、zset

　　概念：它是在set的基础上增加了一个顺序属性，这一属性在添加修改元素的时候可以指定，每次指定后，zset会自动按新的值调整顺序。可以理解为有两列的mysql表，一列存储value，一列存储顺序，操作中key理解为zset的名字。

　　和set一样sorted，sets也是string类型元素的集合，不同的是每个元素都会关联一个double型的score。sorted set的实现是skip list和hash table的混合体。

　　当元素被添加到集合中时，一个元素到score的映射被添加到hash table中，所以给定一个元素获取score的开销是O(1)。另一个score到元素的映射被添加的skip list，并按照score排序，所以就可以有序地获取集合中的元素。添加、删除操作开销都是O(logN)和skip list的开销一致，redis的skip list 实现是双向链表，这样就可以逆序从尾部去元素。sorted set最经常使用方式应该就是作为索引来使用，我们可以把要排序的字段作为score存储，对象的ID当元素存储。

- 当redis尝试按分值对有序集合进行排序的时候，如果成员分值都为0，那么会按照成员名进行排序。

1. zadd key score1 value1：添加元素

2. zrange key start stop [withscore]：把集合排序后,返回名次[start,stop]的元素  默认是升序排列  withscores 是把score也打印出来

3. zrank key member：查询member的排名（升序0名开始）

4. zrangebyscore key min max [withscores] limit offset N：集合（升序）排序后取score在[min, max]内的元素，并跳过offset个，取出N个 

   - 指令规则

     ```
     指令	是否必须	说明
     ZREVRANGEBYSCORE	是	指令
     key	是	有序集合键名称
     max	是	最大分数值,可使用”+inf”代替
     min	是	最小分数值,可使用”-inf”代替
     WITHSCORES	否	将成员分数一并返回
     LIMIT	否	返回结果是否分页,指令中包含LIMIT后offset、count必须输入
     offset	否	返回结果起始位置
     count	否	返回结果数量
     ```

5. zrevrank key member：查询member排名（降序 0名开始）

6. zremrangebyscore key min max：按照score来删除元素，删除score在[min, max]之间

7. zrem key value1 value2：删除集合中的元素

8. zremrangebyrank key start end：按排名删除元素，删除名次在[start, end]之间的

9. zcard key：返回集合元素的个数

10. zcount key min max：返回[min, max]区间内元素数量

11. zinterstore dest numkeys key1[key2..] [WEIGHTS weight1 [weight2...]] [AGGREGATE SUM|MIN|MAX]
    - 求key1，key2的交集，key1，key2的权值分别是weight1，weight2
    - 聚合方法用 sum|min|max
    - 聚合结果 保存子dest集合内
    - 注意：weights,aggregate：如果有交集，交集元素又有score，score怎么处理？aggregate sum->score相加，min最小score，max最大score，另外可以通过weights设置不同的key的权重，交集时  score*weight

12. zscore key member 获取对应key的对应member的分数值

13. zincrby key increment member 对应key的对应member的分数值自增

14. ZREVRANGE key start stop [WITHSCORES] 返回有序集中，指定区间内的成员。

15. ZREVRANGEbyscore key-name min max 返回有序集中，指定区间内的成员，按照分支从大到小的顺序来返回。

16. zunionstore dest-key key-count key1 key2 key3 [weights weight1 weight2] [aggregate sum|min|max] :对给定的有序集合执行类似于集合的并集运算。默认为sum

# 7、发布与订阅

1. 订阅者（listener）负责订阅频道（channel），发布者（publisher）负责向频道发送二进制字符串信息。当有消息被发送到给定频道时，频道的所有订阅者都会收到消息。

2. 发布与订阅的命令

   | 命令         | 描述                                                         |
   | ------------ | ------------------------------------------------------------ |
   | subscribe    | subscribe channel [channel ...] 订阅给定的一个或多个频道     |
   | unsubscribe  | unsubscribe channel [channel ...] 退订给定的一个或多个频道，如果没有给定任何频道，那么将退订所有频道 |
   | publish      | publish channel message 向给定频道发送消息                   |
   | psubscribe   | psubscribe pattern [pattern ...] 订阅与给定模式相匹配的所有频道 |
   | punsubscribe | punsubscribe [pattern [pattern ...]] 退订给定的模式，如果没有任何给定模式，将退订所有模式 |

# 8、其他命令

1. 排序

   ```
   sort source-key [by pattern] [limit offset count] [get pattern [get pattern ...]] [asc|desc] [alpha] [store dest-key] 
   
   BY 参数的语法为“BY参考键”。其中参考键可以是字符串类型键或者是散列类型键的某个字段（表示为键名—>字段名）。如果提供了BY参数，SORT命令将不再依据元素自身的值进行排序，而是对每个元素使用元素的值替换参考键中的第一个“*”并获取其值，然后依据该值对元素排序。
   
   GET参数不影响排序，它的作用是使SORT命令的返回结果不再是元素自身的值，而是GET参数中指定的键
   值。GET参数的规则和BY参数一样，GET参数也支持字符串类型和散列类型的键，并使用“*”作为占位符。
   
   SORT命令的时间复杂度是O(n+mlogm )，其中n 表示要排序的列表（集合或有序集合）中的元素个数，m表示要返回的元素个数。当n 较大的时候SORT命令的性能相对较低，并且Redis在排序前会建立一个长度为n（有一个例外是当键类型为有序集合且参考键为常量键名时容器大小为m 而不是n）的容器来存储待排序的元素，虽然是一个临时的过程，但如果同时进行较多的大数据量排序操作则会严重影响性能。
   ```

   **示例：**根据字母表顺序

   ```
   sort sort-input alpha
   ```

   **示例：**将散列的域作用权重，进行排序

   ```
   sort sort-input by d-*->field
   ```

   **示例：**将排序结果加到目标键里

   ```
   sort sort-input store sort0
   ```
   
   **示例：**让 `uid` 键按照 `user_level_{uid}` 的大小来排序：
   
   ```
   SORT uid BY user_level_*
   ```
   
   **示例：**让 `idx:xxxx` 键按照 `kb:doc{idx:xxxx}`里的`updated`属性 的大小来排序：
   
   ```
   SORT idx:xxxx BY kb:doc:*-> updated
   ```

# 9、事务

1. 有5个命令可以让用户在不被打断的情况下对多个键执行操作，分别是watch、multi、exec、unwatch、discard

   | 命令    | 描述                                                         |
   | ------- | ------------------------------------------------------------ |
   | watch   | 在用户使用watch命令对键进行监视之后，直到用户执行exec命令的这段时间里，如果又其他客户端抢先对任何被监视的键进行了替换、更新或删除等操作，那么当用户尝试执行exec命令的时候，事务将失败并返回一个错误。 |
   | discard | 在multi之后、exec之前对连接进行重置。也就是用户在使用watch监视一个或多个键，接着使用multi开始一个心的事务，并将多个命令入队到事务队列后，仍然可以发送discard来取消watch命令并清空所有已入队的命令。 |
   | unwitch | 在watch之后，multi之前对连接进行重置（reset）                |

   

2. redis基本事务：可以让客户端在不被其他客户端打断的情况下执行多个命令。在redis里，被multi和exec命令包围的所有命令会一个接一个地执行，直到所有命令都执行完毕为止。

   - **使用方法：**先执行multi命令，相关操作，exec命令。为减少redis与客户端之间的通信次数，客户端会一次把事务的所有命令发送给redis。

   - **示例：**简单的自增自减操作，通过pipelined保障安全

     ```java
     for (int i = 0; i < 3; i++) {
                 new Thread(() -> {
                     Jedis jedis = jedisFactory.getJedis();
                     Pipeline pipeline = jedis.pipelined();
                     pipeline.multi();
                     pipeline.incr("trans:");
                     pipeline.decr("trans:");
                     pipeline.exec();
                 }).start();
             }
     ```

   - **示例：**通过pipelined进行批量操作

     ```java
     //获取文章id列表
     Set<String> ids = conn.zrevrange(order, start, end);
     //获得流
     Pipeline pipeline = conn.pipelined();
     //开启事务
     pipeline.multi();
     for (String id : ids) {
         pipeline.hgetAll(id);
     }
     //执行并获得结果集
     Response<List<Object>> result = pipeline.exec();
     //关闭流
     pipeline.close();
     //获得结果列表
     List<Object> resultList = result.get();
     ```

# 10、设置键的过期时间

1. 处理过期时间的命令

   | 命令      | 描述                                                         |
   | --------- | ------------------------------------------------------------ |
   | persist   | persist key-name 移除键的过期时间                            |
   | ttl       | ttl key-name 查看给定键距离过期还有多少秒                    |
   | expire    | expire key-name seconds 让给定键再指定的秒数之后过期         |
   | expireat  | expireat key-name timestamp 将给定键的过期时间设置为给定的unix时间戳 |
   | pttl      | pttl key-name 查看给定键距离过期时间还有多少毫秒             |
   | pexpire   | pexpire key-name milliseconds 让给定键在指定的毫秒之后过期   |
   | pexpireat | pexpireat key-name timestamp-milliseconds 将一个毫秒级精度的unix时间戳设置为给定的过期时间 |

   

# 8、服务器相关命令

1. ping：测定连接是否存活
2. echo：在命令行打印一些内容
3. select：选择数据库
4. quit：退出连接
5. dbsize：返回当前数据库中key的数目
6. info：获取服务器的信息和统计
7. monitor：实时转储收到的请求
8. config get 配置项：获取服务器配置的信息
9. config set 配置项  值：设置配置项信息
10. flushdb：删除当前选择数据库中所有的key
11. flushall：删除所有数据库中的所有的key
12. time：显示服务器时间，时间戳（秒），微秒数
13. bgrewriteaof：后台保存rdb快照
14. bgsave：后台保存rdb快照
15. save：保存rdb快照
16. lastsave：上次保存时间
17. shutdown [save/nosave]
    - 注意：如果不小心运行了flushall，立即shutdown nosave，关闭服务器，然后手工编辑aof文件，去掉文件中的flushall相关行，然后开启服务器，就可以倒回原来是数据。如果flushall之后，系统恰好bgwriteaof了，那么aof就清空了，数据丢失。
18. showlog：显示慢查询
    - 超时参数由slowlog-log-slower-than 10000 来指定（单位为微秒）
    - 服务器存储慢查询记录数由 slowlog-max-len 128 来决定　　　