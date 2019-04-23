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

3. 查找IP所属城市及国家

   - 表结构

     - zset实现ip查找对应城市id

       ```java
       public void importIpsToRedis(Jedis conn, File file) {
           FileReader reader = null;
           try{
               reader = new FileReader(file);
               CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
               Iterator<CSVRecord> integer = parser.iterator();
               int count = 0;
               CSVRecord line;
               while ((line = integer.next()) != null){
                   String startIp = line.size() > 1 ? line.get(0).substring(0, line.get(0).length() -3) : "";
                   if (startIp.toLowerCase().indexOf('i') != -1){
                       continue;
                   }
                   int score;
                   if (startIp.indexOf('.') != -1){
                       score = ipToScore(startIp);
                   }else{
                       try{
                           score = Integer.parseInt(startIp, 10);
                       }catch(NumberFormatException nfe){
                           continue;
                       }
                   }
       
                   String cityId = line.get(1) + '_' + count;
                   if (line.get(1).equals("")) {
                       break;
                   }
                   conn.zadd("ip2cityid:", score, cityId);
                   count++;
               }
           }catch(Exception e){
               throw new RuntimeException(e);
           }finally{
               try{
                   reader.close();
               }catch(Exception e){
                   // ignore
               }
           }
       }
       ```

     - hash实现城市id查找城市的实际信息

       ```java
       public void importCitiesToRedis(Jedis conn, File file) {
           Gson gson = new Gson();
           FileReader reader = null;
           try{
               reader = new FileReader(file);
               CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
               Iterator<CSVRecord> integer = parser.iterator();
               CSVRecord line;
               while ((line = integer.next()) != null){
                   if (line.size() < 4 || !Character.isDigit(line.get(0).charAt(0))){
                       continue;
                   }
                   String cityId = line.get(0);
                   String country = line.get(1);
                   String region = line.get(2);
                   String city = line.get(3);
                   String json = gson.toJson(new String[]{city, region, country});
                   conn.hset("cityid2city:", cityId, json);
               }
           }catch(Exception e){
               throw new RuntimeException(e);
           }finally{
               try{
                   reader.close();
               }catch(Exception e){
                   // ignore
               }
           }
       }
       ```

     - 查询

       ```java
       public String[] findCityByIp(Jedis conn, String ipAddress) {
           int score = ipToScore(ipAddress);
           Set<String> results = conn.zrevrangeByScore("ip2cityid:", score, 0, 0, 1);
           if (results.size() == 0) {
               return null;
           }
       
           String cityId = results.iterator().next();
           cityId = cityId.substring(0, cityId.indexOf('_'));
           return new Gson().fromJson(conn.hget("cityid2city:", cityId), String[].class);
       }
       ```

4. 服务的发现与配置

   - 使用redis存储配置信息

     - 使用isUnderMaintence()接口和查询isundermaintence键，判断服务器是否在进行维护。

     - isUnderMaintenance()接口最多1秒更新一次服务器信息，防止客户看到维护页面不断重试。

       ```java
       private long lastChecked;
       private boolean underMaintenance;
       public boolean isUnderMaintenance(Jedis conn) {
           if (lastChecked < System.currentTimeMillis() - 1000){
               lastChecked = System.currentTimeMillis();
               String flag = conn.get("is-under-maintenance");
               underMaintenance = "yes".equals(flag);
           }
       
           return underMaintenance;
       }
       ```

   - 把一个已知的redis服务器用作配置信息字典：通过这个字典存储的配置信息来连接为不同应用或服务组件提供数据的其他redis服务器。（如ip、端口号等）

     - 如获取存储统计信息的redis服务器的信息，就获取config:redis:statistics键的值

       - setConfig()

         ```java
         public void setConfig(Jedis conn, String type, String component, Map<String,Object> config) {
             Gson gson = new Gson();
             conn.set("config:" + type + ':' + component, gson.toJson(config));
         }
         ```

       - getConfig()

         ```java
         private static final Map<String,Map<String,Object>> CONFIGS =
                 new HashMap<>();
         private static final Map<String,Long> CHECKED = new HashMap<>();
         
         @SuppressWarnings("unchecked")
         public Map<String,Object> getConfig(Jedis conn, String type, String component) {
             int wait = 1000;
             String key = "config:" + type + ':' + component;
         
             Long lastChecked = CHECKED.get(key);
             //如果没有检查过，或者距离上一次检查的时间已经超过1s
             if (lastChecked == null || lastChecked < System.currentTimeMillis() - wait){
                 //把对应key的时间加到checked Map里
                 CHECKED.put(key, System.currentTimeMillis());
         
                 String value = conn.get(key);
                 Map<String,Object> config;
                 if (value != null){
                     Gson gson = new Gson();
                     config = gson.fromJson(
                             value, new TypeToken<Map<String,Object>>(){}.getType());
                 }else{
                     config = new HashMap<>();
                 }
         
                 CONFIGS.put(key, config);
             }
         
             return CONFIGS.get(key);
         }
         ```

       - 获取对应配置的连接

         ```java
         public static final Map<String,Jedis> REDIS_CONNECTIONS =
                 new HashMap<>();
         public Jedis redisConnection(String component){
             Jedis configConn = REDIS_CONNECTIONS.get("config:redis:" + component);
             if (configConn == null) {
                 configConn = jedisFactory.getJedis();
                 REDIS_CONNECTIONS.put("config:redis:" + component, configConn);
             }
         
             String key = "config:redis:" + component;
             //获取旧配置
             Map<String,Object> oldConfig = CONFIGS.get(key);
             //获取新配置
             Map<String,Object> config = getConfig(configConn, "redis", component);
         
             //新旧配置不同
             if (!config.equals(oldConfig)){
                 Jedis conn = new Jedis();
                 if (config.containsKey("db")){
                     conn.select(((Double)config.get("db")).intValue());
                 }
                 REDIS_CONNECTIONS.put(key, conn);
             }
         
             return REDIS_CONNECTIONS.get(key);
         }
         ```

# 6、使用redis构建应用程序组件

1. 自动补全（autocomplete）：在用户不进行搜索的情况下，快速找到所需东西的技术。

   - 自动补全最近联系人

     - 使用list，消耗内存较少且有序。key是以xx开头的用户，值是以xx开头的用户名全称。

     - 自动补全列表需要的操作：

       - 添加或更新一个联系人，让他称为最新的被联系用户（multi和exec保证事务安全）
         - 如果指定的联系人已经存在最近联系列表里，那么从列表里移除他。lrem
         - 将指定的联系人添加到最近联系人列表的最前面。lpush
         - 保留列表前面的100个联系人。ltrim

     - 示例：此做法适合列表小的操作。如果要大列表，可以使用带有时间戳的有序集合代替列表。

       ```java
       //添加或更新最近联系人
       public void addUpdateContact(Jedis conn, String user, String contact) {
           String acList = "recent:" + user;
           Transaction trans = conn.multi();
           trans.lrem(acList, 1, contact);
           trans.lpush(acList, contact);
           trans.ltrim(acList, 0, 99);
           trans.exec();
       }
       
       //删除某个不想看到的最近联系人
       public void removeContact(Jedis conn, String user, String contact) {
           conn.lrem("recent:" + user, 1, contact);
       }
       
       //获取自动补全列表
       public List<String> fetchAutocompleteList(Jedis conn, String user, String prefix) {
           List<String> candidates = conn.lrange("recent:" + user, 0, -1);
           List<String> matches = new ArrayList<>();
           for (String candidate : candidates) {
               if (candidate.toLowerCase().startsWith(prefix)){
                   matches.add(candidate);
               }
           }
           return matches;
       }
       ```

   - 通讯录自动补全

     - 英语字母：使用前驱后继解决查找区间问题

       - 如aba，前驱是ab`  后继是 aba{      如aba、abaa、abaz都在这个区间里面

       - 如abc，前驱是abb{   后继是 abc{     如abc 、 abcd都在这个区间里

       - 将前驱和后继插入到有序集合里，然后查看两个被插入元素的排名，从之间取出一些元素，再移除前驱和后继。

       - 为防止移除错其他用户的前驱和后继，把一个uuid加到前驱和后继元素后面。

       - 并再插入前驱和后继后，使用watch、multi和exec来确保有序集合不会再进行范围查找和范围取值之间发生变化。

         ```java
         //自动补全函数
         @SuppressWarnings("unchecked")
         public Set<String> autocompleteOnPrefix(Jedis conn, String guild, String prefix) {
             String[] range = findPrefixRange(prefix);
             String start = range[0];
             String end = range[1];
             String identifier = UUID.randomUUID().toString();
             start += identifier;
             end += identifier;
             String zsetName = "members:" + guild;
         
             //插入分隔符
             conn.zadd(zsetName, 0, start);
             conn.zadd(zsetName, 0, end);
         
             Set<String> items;
             while (true){
                 conn.watch(zsetName);
                 //获取位置
                 int sindex = conn.zrank(zsetName, start).intValue();
                 int eindex = conn.zrank(zsetName, end).intValue();
                 //获取范围
                 int erange = Math.min(sindex + 9, eindex - 2);
         
                 Transaction trans = conn.multi();
                 //移除前驱和后继
                 trans.zrem(zsetName, start);
                 trans.zrem(zsetName, end);
                 //获取元素列表
                 trans.zrange(zsetName, sindex, erange);
                 List<Object> results = trans.exec();
                 if (results != null){
                     items = (Set<String>) results.get(results.size() - 1);
                     break;
                 }
             }
         
             //过滤掉带“{”的元素
             for (Iterator<String> iterator = items.iterator(); iterator.hasNext(); ){
                 if (iterator.next().indexOf('{') != -1){
                     iterator.remove();
                 }
             }
             return items;
         }
         
         //加入公会
         public void joinGuild(Jedis conn, String guild, String user) {
             conn.zadd("members:" + guild, 0, user);
         }
         
         public void leaveGuild(Jedis conn, String guild, String user) {
             conn.zrem("members:" + guild, user);
         }
         ```

2. 分布式锁

   - watch、multi、exec组成的事务不具有扩展性，因为会随着数据量的增大，发生重试的次数急剧增加。

   - 这种锁是给不同机器上的不同redis客户端使用的。

     - 这种锁的范围很大。

   - 发生难以预料的锁意外：

     - 持有锁的进程因为操作时间过长而导致被自动释放，而进程本身并不知晓，甚至可能会错误地释放掉其他进程持有的锁。
     - 一个持有锁并打算执行长时间操作的进程已经崩溃，但其他想要锁的进程不知道哪个进程持有锁，也无法检测除持有锁的进程已经崩溃，只能白白浪费时间等待锁被释放。
     - 在一个进程持有的锁过期之后，其他多个进程同时尝试去获取锁，并且都得到了锁。
     - 第1和第3种情况同时出现。多个进程同时持有锁，但每个进程都以为自己是唯一一个获得锁的进程。

   - 使用redis构建锁

     - acquireLock()

       ```java
       public String acquireLock(Jedis conn, String lockName, long acquireTimeout){
           String identifier = UUID.randomUUID().toString();
       
           long end = System.currentTimeMillis() + acquireTimeout;
           while (System.currentTimeMillis() < end) {
               if (conn.setnx("lock:" + lockName, identifier) == 1){
                   return identifier;
               }
       
               try {
                   Thread.sleep(1);
               }catch(InterruptedException ie){
                   Thread.currentThread().interrupt();
               }
           }
       
           return null;
       }
       ```

3. 计数信号量

   - 如果获取失败不会等待，而是直接返回失败信息。

   - 使用zset构建，键为信号量名，域为一个为每个成员分配的唯一标识符，值为时间戳。

   - 进程将标识符添加到有序集合里，查看自己在有序集合里的排名，如果排名低于总数（从0开始），那么成功获取信号量。反之，移除自己的信号量。且在添加到有序集合前，会先清理所有时间戳大于超时数值（timeout number value）的标识符。

   - 简单信号量

     ```java
     //获取信号量
     public String acquireSemaphore(Jedis conn, String semmname, int limit, long timeout) {
         String identifier = UUID.randomUUID().toString();
         long now = System.currentTimeMillis();
     
         Transaction trans = conn.multi();
         //清除超时的所有元素
         trans.zremrangeByScore(semmname, "-inf", String.valueOf(now - timeout));
         //尝试获取信号量
         trans.zadd(semmname, now,  identifier);
         trans.zrank(semmname, identifier);
         List<Object> results = trans.exec();
         int count = ((Long)results.get(results.size() - 1)).intValue();
         if (count < limit) {
             return identifier;
         }
         conn.zrem(semmname, identifier);
         return null;
     }
     
     //释放信号量
     public long acquireSemaphore(Jedis conn, String semmname, String identifier) {
         return conn.zrem(semmname, identifier);
     }
     ```

   - 公平信号量：通过超时有序集合和计数集合，外加一个计数量string实现。

     - 通过增加一个计数量，来保证不会因为系统时间的原因，从而系统时间快的，抢占系统时间慢的信号量。

     - 其在zset里的位置是跟计数量有关的。
     - 其作用也只是使得插入信号量，不会插入到在此信号量之前插入的信号量之前。
     - 并不能避免信号量被无辜删除。
     - 要求系统的时间差较小。

     ```java
     //获取公平信号量
     public String acquireFairSemaphore(Jedis conn, String semmname, int limit, long timeout) {
         String identifier = UUID.randomUUID().toString();
         String czset = semmname + ":owner";
         String ctr = semmname + ":counter";
     
         long now = System.currentTimeMillis();
         Transaction trans = conn.multi();
         //移除超时信号量
         trans.zremrangeByScore(semmname, "-inf", String.valueOf(now - timeout));
         ZParams params = new ZParams();
         params.weights(1, 0);
         //移除超时信号量
         trans.zinterstore(czset, params, czset, semmname);
         trans.incr(ctr);
         List<Object> results = trans.exec();
         int counter = ((Long)results.get(results.size() - 1)).intValue();
     
         trans = conn.multi();
         trans.zadd(semmname, now, identifier);
         trans.zadd(czset, counter, identifier);
         trans.zrank(czset, identifier);
         results = trans.exec();
         int result = ((Long)results.get(results.size() - 1)).intValue();
         if (result < limit) {
             return identifier;
         }
     
         trans = conn.multi();
         trans.zrem(semmname, identifier);
         trans.zrem(czset, identifier);
         return null;
     }
     
     //释放信号量
     public boolean releaseFairSemaphore(Jedis conn, String semmname, String identifier) {
         Transaction trans = conn.multi();
         trans.zrem(semmname, identifier);
         trans.zrem(semmname + ":owner", identifier);
         List<Object> results = trans.exec();
         return ((Long)results.get(results.size() - 1)) == 1;
     }
     ```

   - 刷新信号量：通过对超时有序集合进行更新

     ```java
     //刷新信号量
     public boolean refreshFairSemaphore(Jedis conn, String semmname, String identifier) {
         //添加成功表明这个信号量已经不存在超时集合里了，所以这个信号量无效，删除信号量并返回false
         if (conn.zadd(semmname, System.currentTimeMillis(), identifier) > 0) {
             releaseFairSemaphore(conn, semmname, identifier);
             return false;
         }
         return true;
     }
     ```

   - 消除竞争条件：A、B同时尝试获取剩余的一个信号量，A对计数器执行自增操作，B将自己的标识符添加到有序集合里，获取到信号量。之后A将自己的标识符添加到有序集合里，这时候A将偷走B的信号量，而B只有在尝试释放信号量或尝试刷新信号量时才会察觉这一点。

     - 从而导致信号量多余预期数量。

     - 使用分布式锁：也就是保证同时只有一个进行在获取锁

       ```java
       //使用分布式锁解决竞争条件
       public String acquireSemaphoreWithLock(Jedis conn, String semmname, int limit, long timeout) {
           String identifier = null;
           try {
               identifier = acquireLock(conn, semmname, 10);
               if (identifier != null) {
                   return acquireFairSemaphore(conn, semmname, limit, timeout);
               }
           } finally {
               releaseLock(conn, semmname, identifier);
           }
           return null; 
       }
       ```

   - 各种实现的优缺点

     - 对系统时钟没用意见，也不需要刷新信号量，接受偶尔信号量超过限制，可以使用第一种。
     - 只接受偶尔信号量超过限制，可以使用第二种。
     - 必须正确，可以使用第三种。

   - 信号量还可以用于限制针对数据库的并发请求数量，限制对同一个服务器上的资源请求。

4. 任务队列（Task Queue）

   - 把某些长时间执行的任务相关信息放入队列里，之后对队列进行处理，使得可以推迟执行那些需要一段时间才能完成的操作。

   - FIFO队列

     - 使用FIFO队列来发送邮件，从右边（rpush）进入，从左边（blpop）取出。

     - 实现：

       ```java
       //往队列里添加邮件
       public void sendSoldEmailViaQueue(Jedis conn, String seller, String item, double pricce, String buyer) {
           Gson gson = new Gson();
           Map<String, Object> map = new HashMap<>();
           map.put("sellerId", seller);
           map.put("itemId", item);
           map.put("price", pricce);
           map.put("buyerId", buyer);
           map.put("time", System.currentTimeMillis());
           conn.rpush("queue:email", gson.toJson(map));
       }
       
       //从队列中持续取出元素
       public void  processSoldEmailQueue(Jedis conn) {
           new Thread(() -> {
               Gson gson = new Gson();
               while (true) {
                   List<String> packed = conn.blpop(30, "queue:email");
                   if (packed == null) {
                       continue;
                   }
                   Map<String, Object> toSend = gson.fromJson(packed.get(1), Map.class);
                   //下面执行送邮件操作
                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   System.out.println(toSend);
               }
           }).start();
       }
       ```

     - 可执行多种任务队列

       ```java
       //从多种任务的队列中持续取出元素
       public void  workerWatchQueue(Jedis conn) {
           new Thread(() -> {
               Gson gson = new Gson();
               while (true) {
                   List<String> packed = conn.blpop(30, "queue:email");
                   if (packed == null) {
                       continue;
                   }
                   Map<String, Object> toSend = gson.fromJson(packed.get(1), Map.class);
                   String methodName = (String) toSend.get("method");
                   //下面执行送邮件操作
                   try {
                       if (methodName.equals("sendEmail")) {
                           System.out.println("sendEmail");
                       } else if (methodName.equals("getEmail")) {
                           System.out.println("getEmail");
                       } else {
                           System.out.println("error");
                       }
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   System.out.println(toSend);
               }
           }).start();
       }
       ```

     - 优先级任务队列工作线程：每种优先级使用一个队列，使用blpop同时获取多个队列。

       ```java
       //从多种任务的优先级队列中持续取出元素
       public void  workerWatchQueuePriority(Jedis conn, String[] queues) {
           new Thread(() -> {
               Gson gson = new Gson();
               while (true) {
                   List<String> packed = conn.blpop(30, queues);
                   if (packed == null) {
                       continue;
                   }
                   Map<String, Object> toSend = gson.fromJson(packed.get(1), Map.class);
                   String methodName = (String) toSend.get("method");
                   //下面执行送邮件操作
                   try {
                       if (methodName.equals("sendEmail")) {
                           System.out.println("sendEmail");
                       } else if (methodName.equals("getEmail")) {
                           System.out.println("getEmail");
                       } else {
                           System.out.println("error");
                       }
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   System.out.println(toSend);
               }
           }).start();
       }
       ```

   - 延迟任务

     - 延迟的实现方案：
       - 在任务信息中加入执行时间。如果工作进程发现任务的执行时间尚未到，那么把任务重新推入队列。（浪费时间）
       - 工作进程使用一个本地的等待列表记录所有需要在未来执行的任务，并在每次进行while循环的时候，检查等待列表并执行那些已经到期的任务。（工作进程崩溃将导致数据丢失）
       - 把所有需要在未来执行的任务都添加到有序集合里面，并将任务的执行时间设置为分值，另外再使用一个进程来查看有序集合里是否存在可以立即被执行的任务，如果有的话，就从有序集合里移除那个任务，并将它添加到适当的任务队列里面。
       - 有序集合队列（zset queue）：每个任务都包含唯一标识符、处理任务的队列名、处理任务的方法、方法的参数。任务的分值为任务的执行时间。
         - 只要把任务的延迟时间设置为0就称为立即任务。
         
         - 实现：
         
           - 可以使用自适应方法，让函数如果发现没有可执行任务，就休眠更长时间。
         
           ```java
           //把任务添加进队列，如果有延迟参数，则添加进延迟队列
           public String executeLater(Jedis conn, String queue, String name, String args, long delay) {
               Gson gson = new Gson();
               String identifier = UUID.randomUUID().toString();
               String item = gson.toJson(new String[] {identifier, queue, name, args});
               //加入延迟队列里
               if (delay > 0) {
                   conn.zadd("delayed:", System.currentTimeMillis() + delay, item);
               }
               //否则，直接加入任务队列里
               else {
                   conn.rpush("queue:" + queue, item);
               }
               return identifier;
           }
           
           //从延迟队列弹出已经可以执行的任务到任务队列
           public void poollQueue(Jedis conn) {
               new Thread(() -> {
                   Gson gson = new Gson();
                   while (true) {
                       Set<Tuple> items = conn.zrangeWithScores("delayed:", 0, 0);
                       Tuple item = items.size() > 0 ? items.iterator().next() : null;
                       if (item == null || item.getScore() > System.currentTimeMillis()) {
                           try {
                               Thread.sleep(10);
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }
                           continue;
                       }
           
                       String json = item.getElement();
                       String[] values = gson.fromJson(json, String[].class);
                       String identifier = values[0];
                       String queue = values[1];
           
                       //使用细粒度锁，确保不会产生竞争条件
                       //使用其identifier加锁是因为每个任务都对应一个唯一的identifier
                       String locked = acquireLock(conn, identifier, 100);
                       if (locked == null) {
                           continue;
                       }
                       if (conn.zrem("delayed:", json) > 0) {
                           conn.rpush("queue:" + queue, json);
                       }
                       releaseLock(conn, identifier, locked);
                   }
               }).start();
           }
           ```
       
     - 优先级+延迟
     
       - 如实现先执行相同优先级的延迟任务，那么可以创建6个队列，分别为{被延迟的高优先级、高优先级、被延迟的中优先级、中优先级、被延迟的低优先级、低优先级}，然后传入workerWatchQueues()里，这样就会先执行相同优先级的延迟队列。
         - 这样可以实现“最先可执行的延迟任务总数最先被执行”。
   
5. 消息拉取

   - 两个客户端互相发送消息

     - 消息推送（push messaging）：由发送者来确保所有接收者已经成功收到了消息。
       - redis内置了进行消息推送的publish和subscribe命令。（必须客户端一直在线才能收到消息，断线可能会导致客户端丢失信息）
     - 消息拉取（pull messaging）：接收者自己去获取存储再某种邮箱（mailbox）里的消息。

   - 单接收者消息的发送与订阅

     - 可以使用publish和subscribe实现，但是不安全，可能导致消息丢失。

     - 为每个接收者客户端使用一个消息的列表。由发送者把消息推进列表里。

       ```
       list
       key: mailbox:username
       value1:{"sender":"xhsf","msg":"zzzz","time":"2019-04-22 01:08:13"}
       value2:msg2
       ...
       ```

       - 通过检查消息列表，可以直到接收者最近有没有上线，接收者是否已经收到了消息，接收者是否有太多未读消息。

   - 多接收者消息的发送与订阅

     - 此功能类似于群组聊天（group chat）。

     - 每个群组都有用户，可以加入或离开群组。

       ```
       zset
       chat:888
       field:username           score:receive:max:message:id
       field:xhsf           score:13
       ```

     - 用户有自己的有序集合记录自己参加的群组

       ```
       zset
       seen:xhsf
       field:groupId              socre:receive:max:message:id(已经收到的最大消息的id)
       field:888              socre:13
       field:329              socre:5
       ```

     - 创建群组聊天会话

       - 需要的数据结构

         - 群组id的string，控制群组id。
         - 群zset，维持成员和已查看消息条数对应关系的对应关系。
         - 用户已看消息zset，添加群id和已查看消息条数对应关系。

       - 实现：

         ```java
         //创建群组，包含初始化成员
         public String createChat(Jedis conn, String sender, Set<String> recipients, String message, String chatId) {
             if (chatId == null) {
                 chatId = String.valueOf(conn.incr("ids:chat:"));
             }
             recipients.add(sender);
         
             Transaction trans = conn.multi();
             for (String rec : recipients) {
                 trans.zadd("chat:" + chatId, 0, rec);
                 trans.zadd("seen:" + rec, 0, chatId);
             }
             trans.exec();
             return null;
         }
         ```

     - 发送消息

       - 需要的数据结构

         - 维持群id和消息id关系的string，获取消息id。

           ```
           string
           ids:888
           value:id
           39
           ```

         - 维持群id和消息的的对应关系

           ```
           zset
           msgs:chatId
           socre:mid
           value:msg
           ```

       - 实现：

         ```java
         public String sendMessage(Jedis conn, String chatId, String sender, String message) {
             String identifier = acquireLock(conn, "chat:" + chatId, 100);
             if (identifier == null) {
                 throw new RuntimeException("Couldn't get the lock");
             }
             try {
                 //消息的id
                 long mid = conn.incr("ids:" + chatId);
                 HashMap<String,Object> values = new HashMap<>();
                 values.put("id", mid);
                 values.put("ts", System.currentTimeMillis());
                 values.put("sender", sender);
                 values.put("message", message);
                 String packed = new Gson().toJson(values);
         		//添加消息到消息zset里
                 conn.zadd("msgs:" + chatId, mid, packed);
             }finally{
                 releaseLock(conn, "chat:" + chatId, identifier);
             }
             return chatId;
         }
         ```

     - 获取消息

       ```java
       //获取消息
       @SuppressWarnings("unchecked")
       public List<ChatMessages> fetchPendingMessages(Jedis conn, String recipient) {
           //获取此用户的所有群组
           Set<Tuple> seens = conn.zrangeWithScores("seen:" + recipient, 0 , -1);
           //转换成列表
           List<Tuple> seenList = new ArrayList<>(seens);
       
           Transaction trans = conn.multi();
           //获取此用户所有群组里的未读消息
           for (Tuple seen : seens) {
               String chatId = seen.getElement();
               int seenId = (int)seen.getScore();
               trans.zrangeByScore("msgs:" + chatId, String.valueOf(seenId), "inf");
           }
           //每个object都是该用户每个群组的未读消息列表
           List<Object> results = trans.exec();
       
           Gson gson = new Gson();
           Iterator<Tuple> seenIterator = seenList.iterator();
           Iterator<Object> resultsIterator = results.iterator();
       
           List<ChatMessages> chatMessages = new ArrayList<>();
           //此用户需要更新已查看数的群组列表
           List<Object[]> seenUpdates = new ArrayList<>();
           //需要删除的消息，也就是已经被所以用户查看过的消息
           List<Object[]> msgRemoves = new ArrayList<>();
           while (seenIterator.hasNext()) {
               //群组
               Tuple seen = seenIterator.next();
               //此群组里的未读消息set
               Set<String> messageStrings = (Set<String>)resultsIterator.next();
               //如果没有新消息，跳过这个群组
               if (messageStrings.size() == 0){
                   continue;
               }
       
               int seenId = 0;
               //获取群号
               String chatId = seen.getElement();
               //解析成Map对象的消息列表
               List<Map<String,Object>> messages = new ArrayList<>();
               //遍历未读消息列表
               for (String messageJson : messageStrings){
                   Map<String,Object> message = gson.fromJson(messageJson, new TypeToken<Map<String,Object>>(){}.getType());
                   System.out.println(message);
                   int messageId = ((Double)message.get("id")).intValue();
                   if (messageId > seenId) {
                       seenId = messageId;
                   }
                   message.put("id", messageId);
                   messages.add(message);
               }
       
               //更新群组里的用户的已查看消息数
               conn.zadd("chat:" + chatId, seenId, recipient);
               seenUpdates.add(new Object[]{"seen:" + recipient, seenId, chatId});
       
               //此score在zset里最小，所以代表此score之前的消息每个人都查看过了
               Set<Tuple> minIdSet = conn.zrangeWithScores("chat:" + chatId, 0, 0);
               if (minIdSet.size() > 0){
                   //把此score和对应的消息列表添加到需要移除的列表里
                   msgRemoves.add(new Object[] {"msgs:" + chatId, minIdSet.iterator().next().getScore()});
               }
               //把此用户在此群的未读消息列表和群id组成的消息对象添加到聊天消息列表里
               chatMessages.add(new ChatMessages(chatId, messages));
           }
       
           trans = conn.multi();
           //更新用户已查看数的群组列表
           for (Object[] seenUpdate : seenUpdates){
               trans.zadd(
                       (String)seenUpdate[0],
                       (Integer)seenUpdate[1],
                       (String)seenUpdate[2]);
           }
           //删除已经被所以用户看过的消息
           for (Object[] msgRemove : msgRemoves){
               trans.zremrangeByScore(
                       (String)msgRemove[0], 0, ((Double)msgRemove[1]).intValue());
           }
           trans.exec();
       
           return chatMessages;
       }
       
       public class ChatMessages
       {
           public String chatId;
           public List<Map<String,Object>> messages;
       
           public ChatMessages(String chatId, List<Map<String,Object>> messages){
               this.chatId = chatId;
               this.messages = messages;
           }
       
           public boolean equals(Object other){
               if (!(other instanceof ChatMessages)){
                   return false;
               }
               ChatMessages otherCm = (ChatMessages)other;
               return chatId.equals(otherCm.chatId) &&
                       messages.equals(otherCm.messages);
           }
       
       
           @Override
           public String toString() {
               return "ChatMessages{" +
                       "chatId='" + chatId + '\'' +
                       ", messages=" + messages +
                       '}';
           }
       }
       ```

     - 加入和退出群组

       ```java
       //加入群组
       public void joinChat(Jedis conn, String chatId, String user) {
           //获得当前消息的位置
           int messageId = Integer.parseInt(conn.get("ids:" + chatId));
       
           Transaction trans = conn.multi();
           //把用户添加到群组里
           trans.zadd("chat:" + chatId, messageId, user);
           //把群组添加到用户的群组已查看消息列表里
           trans.zadd("seen:" + user, messageId, chatId);
           trans.exec();
       }
       
       //退出群组
       public void leaveChat(Jedis conn, String chatId, String user) {
           Transaction trans = conn.multi();
           trans.zrem("chat:" + chatId, user);
           trans.zrem("seen:" + user, chatId);
           //查看集合元素个数
           trans.zcard("chat:" + chatId);
       
           List<Object> results = trans.exec();
       
           //如果群里没有一个成员，则删除此群的消息列表和消息id增长器
           if ((Long)results.get(results.size() - 1) < 1) {
               trans.del("msgs:" + chatId);
               trans.del("ids:" + chatId);
               trans.exec();
           } else {
               //查看那些已经被所有人查看过的消息
               Set<Tuple> oldest = conn.zrangeWithScores("chat:" + chatId, 0, 0);
               conn.zremrangeByScore("msgs:" + chatId, 0, oldest.iterator().next().getScore());
           }
       }
       ```

6. redis进行文件分发

   

