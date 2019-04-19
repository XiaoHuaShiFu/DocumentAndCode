# 4、数据安全与性能保障

1. 持久化
   - 有快照（snapshotting），将某一时刻的所有数据都写道磁盘里；只追加文件（append-only file，AOF），将被执行的写命令复制到磁盘里。

   - 持久化配置选项
     - 快照
       - save 60 1000 多久执行一次自动快照操作
       - stop-writes-on-bgsave-error no 创建快照失败后是否仍然继续执行写命令
       - rdbcompression yes 是否对快照文件进行压缩
       - dbfilename dump.rdb 如何命名硬盘上的快照文件
     - AOF
       - appendonly no 是否使用aof持久化

       - appendfsync everysec 多久才将写入内容同步到磁盘

         | 选项     | 同步频率                         |
         | -------- | -------------------------------- |
         | always   | 每个redis写命令都要同步写入硬盘  |
         | everysec | 每秒执行一次同步                 |
         | no       | 让操作系统来决定应该何时进行同步 |

         - redis每秒同步一次AOF文件时的性能和不适用任何持久化时的性能相差无几，且可以保证出现崩溃时，只会丢一秒内产生的数据。
         - 如果no，可以回导致缓冲区被等待写入硬盘的数据填满，导致redis写入操作被阻塞。

       - no-appendfsync-on-rewrite no 对AOF进行压缩时是否执行同步操作

       - auto-aof-rewrite-percentage 100 多久执行一次AOF压缩

         - 当auto-aof-rewrite-percentage 100时，且AOF文件体积大于64Mb时，自动执行bgrewrtieaof命令。

       - auto-aof-rewrite-min-size 64mb

         - 当AOF文件体积大于64Mb时，且满足auto-aof-rewrite-percentage 100时，自动执行bgrewrtieaof命令。
     - dir ./ 决定快照文件和AOF文件的保存位置

   - 快照持久化
     - 创建快照的方法
       - 客户端通过向redis发送bgsave命令来创建一个快照。redis会调用fork来创建一个子进程，然后紫禁城负责将快照写入硬盘，父进程继续处理命令请求。
       - 客户端发送save命令创建快照。redis在处理完创建快照完毕之前将不再响应任何其他命令。
       - 如果用户设置了save配置选项，比如save 60 10000，那么从redis最近一次创建快照之后算起，当60秒内有10000次写入这个条件满足时，redis就会自动触发bgsave命令。如果用户设置了多个save选项，那么任意一个save配置选项所设置的条件被满足时，redis就会触发一次bgsave命令。
       - redis通过shutdown命令接收到关闭服务器的请求时，或者接收到标准term信号时，会执行一个save命令，阻塞所有客户端，不再执行客户端发送的任何命令，并在save命令执行完毕后关闭服务器。
       - 当一个redis服务器连接另外一个redis服务器，并且对方发送sync命令来开始一次复制操作时，如果主服务器目前没有执行bgsave操作，或者主服务器并非刚刚执行完bgsave操作，那么主服务器就会执行bgsave命令。

   - AOF持久化

     - AOF持久化会将被执行的写命令写到AOF文件的末尾，以此来记录数据发生的变化。因此，Redis只要从头到尾重新执行一次AOF文件包含的所有命令，就可以恢复AOF文件所记录的数据集。

     - 当调用file.wrtie()方法进行写入时，写入的内容首先会被存储到缓冲区，然后操作系统会在未来的某个时间将缓冲区存储的内容写入硬盘。

       - 可以调用file.flush()方法来尽快的将缓冲区里的数据写入硬盘。

       - 还可以命令操作系统将文件同步（sync）到硬盘，同步操作会一只阻塞直到指定的文件被写入硬盘为止。

   - 重写/压缩AOF文件

     - 因为AOF文件会持续加大，重启之后执行AOF文件记录的所有写命令来还原数据集也可能非常长。
     - 用户可以发生bgrewrtieaof命令，来移除AOF文件中的冗余命令来重写AOF文件，使AOF文件体积变小。

2. 复制

3. 处理系统故障

4. redis事务

   - 延迟执行事务有助于提升性能（multi、exec）。

     **示例：**减少客户端往返连接的次数

     ```java
     public void updateTokenPipeline(Jedis conn, String token, String user, String item) {
         long timestamp = System.currentTimeMillis() / 1000;
         Pipeline pipe = conn.pipelined();
         pipe.hset("login:", token, user);
         pipe.zadd("recent:", timestamp, token);
         if (item != null){
             pipe.zadd("viewed:" + token, timestamp, item);
             pipe.zremrangeByRank("viewed:" + token, 0, -26);
             pipe.zincrby("viewed:", -1, item);
         }
         pipe.exec();
     }
     ```

   - 为什么redis没有实现典型的加锁功能？redis为了减少客户端的等待时间，使用乐观锁，也就是只有在数据被修改的情况下，通知客户端。

# 5、使用Redis构建支持程序

1. redis记录日志

   - 替换syslog，最好把系统的syslog换成syslog-ng，在管理和组织日志消息的配置语言更简单。

   - 对于那些需要处理请求时立即执行的操作，和那些可以在请求处理完成后再执行的操作（记录日志和更新计数器），这种服务很适合用作两种操作之间的中间层。

   - 最新日志：记录最新出现的日志

     **示例：**

     ```java
     public void logRecent(Jedis conn, String name, String message, String severity) {
         String destination = "recent:" + name + ':' + severity;
         //使用流来加快速度
         Pipeline pipe = conn.pipelined();
         //插入日志
         pipe.lpush(destination, new Date().toString() + ' ' + message);
         //只保留最近的100条
         pipe.ltrim(destination, 0, 99);
         pipe.sync();
     }
     ```

   - 常见日志：

     **示例：**

     ```java
     public void logCommon(Jedis conn, String name, String message, String severity, int timeout) {
         String commonDest = "common:" + name + ':' + severity;
         String startKey = commonDest + ":start";
         long end = System.currentTimeMillis() + timeout;
         while (System.currentTimeMillis() < end){
             conn.watch(startKey);
             String hourStart = ISO_FORMAT.format(new Date());
             String existing = conn.get(startKey);
     
             Transaction trans = conn.multi();
             //将一个小时前的事件添加到过去的事件去
             if (existing != null && COLLATOR.compare(existing, hourStart) < 0){
                 trans.rename(commonDest, commonDest + ":last");
                 trans.rename(startKey, commonDest + ":pstart");
                 trans.set(startKey, hourStart);
             }
             //如果事件不存在，将事件设置到redis里
             else if (existing == null) {
                 trans.set(startKey, hourStart);
             }
     
             trans.zincrby(commonDest, 1, message);
     
             String recentDest = "recent:" + name + ':' + severity;
             trans.lpush(recentDest, TIMESTAMP.format(new Date()) + ' ' + message);
             trans.ltrim(recentDest, 0, 99);
             List<Object> results = trans.exec();
             // null response indicates that the transaction was aborted due to
             // the watched key changing.
             if (results == null){
                 continue;
             }
             return;
         }
     }
     ```

2. 计数器和统计数据

   - 时间片点击量计数器

     - 用hash，每个键存时间片，值存点击量

       ```java
       public static final int[] PRECISION = new int[]{1, 5, 60, 300, 3600, 18000, 86400};
       public void updateCounter(Jedis conn, String name, int count, long now){
           Transaction trans = conn.multi();
           for (int prec : PRECISION) {
               long pnow = (now / prec) * prec;
               String hash = String.valueOf(prec) + ':' + name;
               trans.zadd("known:", 0, hash);
               trans.hincrBy("count:" + hash, String.valueOf(pnow), count);
           }
           trans.exec();
       }
       ```

     - 清除计数器线程：隔一段时间清除一定数量的计数器

       ```java
       public class CleanCountersThread extends Thread {
           private Jedis conn;
           private int sampleCount = 100;
           private boolean quit;
           private long timeOffset; // used to mimic a time in the future.
       
           public CleanCountersThread(Jedis conn, int sampleCount, long timeOffset){
               this.conn = conn;
               this.sampleCount = sampleCount;
               this.timeOffset = timeOffset;
           }
       
           public void quit(){
               quit = true;
           }
       
           public void run(){
               int passes = 0;
               while (!quit){
                   long start = System.currentTimeMillis() + timeOffset;
                   int index = 0;
                   while (index < conn.zcard("known:")){
                       Set<String> hashSet = conn.zrange("known:", index, index);
                       index++;
                       if (hashSet.size() == 0) {
                           break;
                       }
                       String hash = hashSet.iterator().next();
                       int prec = Integer.parseInt(hash.substring(0, hash.indexOf(':')));
                       int bprec = (int)Math.floor(prec / 60);
                       if (bprec == 0){
                           bprec = 1;
                       }
                       if ((passes % bprec) != 0){
                           continue;
                       }
       
                       String hkey = "count:" + hash;
                       String cutoff = String.valueOf(
                               ((System.currentTimeMillis() + timeOffset) / 1000) - sampleCount * prec);
                       ArrayList<String> samples = new ArrayList<>(conn.hkeys(hkey));
                       Collections.sort(samples);
                       int remove = bisectRight(samples, cutoff);
                       if (remove != 0){
                           conn.hdel(hkey, samples.subList(0, remove).toArray(new String[0]));
                           if (remove == samples.size()){
                               conn.watch(hkey);
                               if (conn.hlen(hkey) == 0) {
                                   Transaction trans = conn.multi();
                                   trans.zrem("known:", hash);
                                   trans.exec();
                                   index--;
                               }else{
                                   conn.unwatch();
                               }
                           }
                       }
                   }
                   
                   passes++;
                   long duration = Math.min(
                           (System.currentTimeMillis() + timeOffset) - start + 1000, 6000);
                   try {
                       sleep(Math.max(6000 - duration, 1000));
                   }catch(InterruptedException ie){
                       Thread.currentThread().interrupt();
                   }
               }
           }
           // mimic python's bisect.bisect_right
           public int bisectRight(List<String> values, String key) {
               int index = Collections.binarySearch(values, key);
               return index < 0 ? Math.abs(index) - 1 : index + 1;
           }
       }
       ```

   - 存储统计数据

     - 使用有序集合存粗最小值、最大值、样本数量、值的和、值的平方和（sumsq）等信息。

     - 使用zset的好处是可以进行并集运算。

     - 可以使用graphite进行计数器和统计数据的功能。

     - **统计数据：**

       ```java
       public List<Object> updateStats(Jedis conn, String context, String type, double value){
           int timeout = 5000;
           String destination = "stats:" + context + ":" + type;
           String startKey = destination + ":start";
           long end = System.currentTimeMillis() + timeout;
           while (System.currentTimeMillis() < end) {
               conn.watch(startKey);
               String hourStart = ISO_FORMAT.format(new Date());
       
               String existing = conn.get(startKey);
               Transaction tran = conn.multi();
               if (existing != null && COLLATOR.compare(existing, hourStart) < 0) {
                   tran.rename(destination, destination + ":last");
                   tran.rename(startKey, destination + ":pstart");
                   tran.set(startKey, hourStart);
               } else if (existing == null) {
                   tran.set(startKey, hourStart);
               }
               String tkey1 = UUID.randomUUID().toString();
               String tkey2 = UUID.randomUUID().toString();
               tran.zadd(tkey1, value, "min");
               tran.zadd(tkey2, value, "max");
       
               tran.zunionstore(destination, new ZParams().aggregate(ZParams.Aggregate.MIN), destination, tkey1);
               tran.zunionstore(destination, new ZParams().aggregate(ZParams.Aggregate.MAX), destination, tkey2);
       
               tran.del(tkey1, tkey2);
               tran.zincrby(destination, 1, "count");
               tran.zincrby(destination, value, "sum");
               tran.zincrby(destination, value * value, "sumsq");
       
               List<Object> results = tran.exec();
               if (results == null) {
                   continue;
               }
               return results.subList(results.size() - 3, results.size());
           }
           return null;
       }
       ```

     - 获取统计值

       ```java
       public Map<String,Double> getStats(Jedis conn, String context, String type){
           String key = "stats:" + context + ":" + type;
           Map<String,Double> stats = new HashMap<>();
           Set<Tuple> data = conn.zrangeWithScores(key, 0, -1);
           for (Tuple tuple : data){
               stats.put(tuple.getElement(), tuple.getScore());
           }
           //平均值
           stats.put("average", stats.get("sum") / stats.get("count"));
           //标准差
           double numerator = stats.get("sumsq") - Math.pow(stats.get("sum"), 2) / stats.get("count");
           double count = stats.get("count");
           stats.put("stddev", Math.pow(numerator / (count > 1 ? count - 1 : 1), .5));
           return stats;
       }
       ```

     - 统计某个功能的时间，并添加到zset里，保持时间最长的100个

       ```java
       public class AccessTimer {
           private Jedis conn;
           private long start;
       
           public AccessTimer(Jedis conn){
               this.conn = conn;
           }
       
           public void start(){
               start = System.currentTimeMillis();
           }
       
           public void stop(String context){
               long delta = System.currentTimeMillis() - start;
               List<Object> stats = updateStats(conn, context, "AccessTime", delta / 1000.0);
               double average = (Double)stats.get(1) / (Double)stats.get(0);
       
               Transaction trans = conn.multi();
               trans.zadd("slowest:AccessTime", average, context);
               trans.zremrangeByRank("slowest:AccessTime", 0, -101);
               trans.exec();
           }
       }
       ```

     - 增加加入到log的功能

       ```java
       。。。
       public void stop(String context){
           long delta = System.currentTimeMillis() - start;
           List<Object> stats = updateStats(conn, context, "AccessTime", delta / 1000.0);
           double average = (Double)stats.get(1) / (Double)stats.get(0);
       
           //把运行时长操作平均值的加入log里
           List<Object> status = updateStats(conn, "ProfilePage", "AccessTime", delta / 1000.0);
           double totalAverage = (Double)status.get(1) / (Double)status.get(0);
           if (average > totalAverage) {
               logCommon(conn, "slowestRequest", "This web request timeout: " + context , INFO, 1000);
           }
       
           Transaction trans = conn.multi();
           trans.zadd("slowest:AccessTime", average, context);
           trans.zremrangeByRank("slowest:AccessTime", 0, -101);
           trans.exec();
       }
       。。。
       ```

