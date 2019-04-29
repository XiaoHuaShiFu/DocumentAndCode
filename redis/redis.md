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

   - 把文件分发到redis 服务器
   
     ```java
     public class CopyLogsThread
             extends Thread {
         private Jedis conn;
         private File path;
         private String channel;
         private int count;
         private long limit;
     
         public CopyLogsThread(Jedis conn, File path, String channel, int count, long limit) {
             this.conn = conn;
             this.path = path;
             this.channel = channel;
             this.count = count;
             this.limit = limit;
         }
     
         public void run() {
             Deque<File> waiting = new ArrayDeque<>();
             long bytesInRedis = 0;
     
             Set<String> recipients = new HashSet<>();
             for (int i = 0; i < count; i++) {
                 recipients.add(String.valueOf(i));
             }
             //创造一个群组
             createChat(conn, "source", recipients, "", channel);
             //获取该路径下的文件列表
             File[] logFiles = path.listFiles((dir, name) -> name.startsWith("redis.md"));
             System.out.println(logFiles);
             //对文件进行排序
             Arrays.sort(logFiles);
             //遍历所有日志文件
             for (File logFile : logFiles) {
                 long fsize = logFile.length();
                 //如果程序需要更多的空间，那么清除已经处理完毕的文件
                 while ((bytesInRedis + fsize) > limit) {
                     long cleaned = clean(waiting, count);
                     if (cleaned != 0) {
                         bytesInRedis -= cleaned;
                     } else {
                         try {
                             sleep(250);
                         } catch (InterruptedException ie) {
                             Thread.interrupted();
                         }
                     }
                 }
     
                 BufferedInputStream in = null;
                 try {
                     in = new BufferedInputStream(new FileInputStream(logFile));
                     int read;
                     byte[] buffer = new byte[8192];
                     //读取文件
                     while ((read = in.read(buffer, 0, buffer.length)) != -1) {
                         //如果读出的长度不等于数组长度，那么也就是buffer里的内容，并不是所有都是这次读出来的
                         //从buffer里复制出此次读出的长度，添加到新的bytes数组里
                         if (buffer.length != read) {
                             byte[] bytes = new byte[read];
                             System.arraycopy(buffer, 0, bytes, 0, read);
                             System.out.println(String.valueOf(bytes));
                             //把日志一直追加到之前日志的后面
                             conn.append((channel + logFile).getBytes(), bytes);
                         } else {
                             conn.append((channel + logFile).getBytes(), buffer);
                         }
                     }
                 } catch (IOException ioe) {
                     ioe.printStackTrace();
                     throw new RuntimeException(ioe);
                 } finally {
                     try {
                         in.close();
                     } catch (Exception ignore) {
                     }
                 }
     
                 sendMessage(conn, channel, "source", logFile.toString());
     
                 bytesInRedis += fsize;
                 //把刚查询过的日志文件加到队列末端
                 waiting.addLast(logFile);
             }
     
             sendMessage(conn, channel, "source", ":done");
     
             while (waiting.size() > 0) {
                 long cleaned = clean(waiting, count);
                 if (cleaned != 0) {
                     bytesInRedis -= cleaned;
                 } else {
                     try {
                         sleep(250);
                     } catch (InterruptedException ie) {
                         Thread.interrupted();
                     }
                 }
             }
         }
     
         //清除处理完毕的文件
         private long clean(Deque<File> waiting, int count) {
             if (waiting.size() == 0) {
                 return 0;
             }
             File w0 = waiting.getFirst();
             //如果此群组的此文件已经被标识为done，则删除该文件
             if (String.valueOf(count).equals(conn.get(channel + w0 + ":done"))) {
                 conn.del(channel + w0, channel + w0 + ":done");
                 //返回被删除文件的长度
                 return waiting.removeFirst().length();
             }
             return 0;
         }
     }
     ```
   
   - 接收并处理日志文件
   
     ```java
     public interface Callback {
         void callback(String line);
     }
     
     //接收日志文件
     public void processLogsFromRedis(Jedis conn, String id, Callback callback)
             throws InterruptedException, IOException
     {
         while (true){
             List<ChatMessages> fdata = fetchPendingMessages(conn, id);
     
             for (ChatMessages messages : fdata){
                 for (Map<String,Object> message : messages.messages){
                     System.out.println(message);
                     String logFile = (String)message.get("message");
     
                     if (":done".equals(logFile)){
                         return;
                     }
                     if (logFile == null || logFile.length() == 0){
                         continue;
                     }
     
                     //调用回调函数处理日志
                     InputStream in = new RedisInputStream(
                             conn, messages.chatId + logFile);
                     if (logFile.endsWith(".gz")){
                         in = new GZIPInputStream(in);
                     }
     
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                     try{
                         String line;
                         while ((line = reader.readLine()) != null){
                             callback.callback(line);
                         }
                         callback.callback(null);
                     }finally{
                         reader.close();
                     }
     
                     conn.incr(messages.chatId + logFile + ":done");
                 }
             }
     
             if (fdata.size() == 0){
                 Thread.sleep(100);
             }
         }
     }
     
     //redis流处理函数
     public class RedisInputStream
             extends InputStream
     {
         private Jedis conn;
         private String key;
         private int pos;
     
         public RedisInputStream(Jedis conn, String key){
             this.conn = conn;
             this.key = key;
         }
     
         @Override
         public int available()
                 throws IOException
         {
             long len = conn.strlen(key);
             return (int)(len - pos);
         }
     
         @Override
         public int read()
                 throws IOException
         {
             byte[] block = conn.substr(key.getBytes(), pos, pos);
             if (block == null || block.length == 0){
                 return -1;
             }
             pos++;
             return (block[0] & 0xff);
         }
     
         @Override
         public int read(byte[] buf, int off, int len)
                 throws IOException
         {
             byte[] block = conn.substr(key.getBytes(), pos, pos + (len - off - 1));
             if (block == null || block.length == 0){
                 return -1;
             }
             System.arraycopy(block, 0, buf, off, block.length);
             pos += block.length;
             return block.length;
         }
     
         @Override
         public void close() {
             // no-op
         }
     }
     ```

# 7、基于搜索的应用程序

1. 使用redis进行搜索

   - 对文档进行预处理：通常称为建索引（indexing）

     - 反向索引（inverted indexes）：

       - redis的set和zset都很适合处理反向索引。

       - 反向索引会从每个被索引的文档里提取出一些单词，并创建表格来记录每篇文章都包含哪些单词。

       - 如对只含标题《lord of the rings》的文档docA以及只含标题《lord of the dance》的文档docB，程序将在redis里面为lord这个单词创建一个集合，并在集合里包含docA和docB着两个文档的名字。

         ```
         ind:lord 					set
         docA
         docB
         ```

         ```
         ind:of 					set
         docA
         docB
         ```

         ```
         ind:the 					set
         docA
         docB
         ```

         ```
         ind:rings 					set
         docA
         ```

         ```
         ind:dance 					set
         docB
         ```

     - 从文档提取单词：

       - 语法分析（parsing）

       - 标记化（tokenization）：生成一系列用于标识文档的标记（token），有时候称为单词（word）

         - 移除内容中的非用词（stop word）：在文档中频繁出现但是没用提供相应信息量的单词，减少索引体积，加快搜索速度。

       - 实现：

         ```java
         private static final Pattern QUERY_RE = Pattern.compile("[+-]?[a-z']{2,}");
         private static final Pattern WORDS_RE = Pattern.compile("[a-z']{2,}");
         private static final Set<String> STOP_WORDS = new HashSet<>();
         
         static {
             for (String word :
                     ("able about across after all almost also am among " +
                             "an and any are as at be because been but by can " +
                             "cannot could dear did do does either else ever " +
                             "every for from get got had has have he her hers " +
                             "him his how however if in into is it its just " +
                             "least let like likely may me might most must my " +
                             "neither no nor not of off often on only or other " +
                             "our own rather said say says she should since so " +
                             "some than that the their them then there these " +
                             "they this tis to too twas us wants was we were " +
                             "what when where which while who whom why will " +
                             "with would yet you your").split(" ")) {
                 STOP_WORDS.add(word);
             }
         }
         
         //获取被保留的，不是非用词的单词
         public Set<String> tokenize(String content) {
             Set<String> words = new HashSet<>();
             Matcher matcher = WORDS_RE.matcher(content);
             while (matcher.find()) {
                 String word = matcher.group().trim();
                 if (word.length() > 2 && !STOP_WORDS.contains(word)) {
                     words.add(word);
                 }
             }
             return words;
         }
         ```

   - 基本搜索操作

     - 建立反向索引库

       ```java
       /**
        * 把文档添加到反向索引集合里
        *
        * @param conn
        * @param docid   文档名称
        * @param content 内容
        * @return 成功建立索引的个数
        */
       public int indexDocument(Jedis conn, String docid, String content) {
           Set<String> words = tokenize(content);
           Gson gson = new Gson();
       
           //删除失效单词
           Set<String> oldWords = gson.fromJson(conn.get(docid), new TypeToken<Set<String>>() {
           }.getType());
           if (oldWords != null) {
               Transaction trans = conn.multi();
               for (String oldWord : oldWords) {
                   if (!words.contains(oldWord)) {
                       trans.srem("idx:" + oldWord, docid);
                   }
               }
               trans.exec();
           }
       
       
           //添加文档对应单词的索引
           Transaction trans = conn.multi();
           for (String word : words) {
               trans.sadd("idx:" + word, docid);
           }
           //把当前文档的键值添加到一个键里
           trans.set(docid, gson.toJson(words));
       
           return trans.exec().size() - 1;
       }
       ```

     - 使用sinter和sinterstore命令来找出那些同时存在于所有给定集合的元素。

     - 使用交集操作处理反向索引可以彻底的忽略无关信息。

     - 包含某个单词或句子，但是不包含另外的单词或句子，可以使用sdiff和sdiffstore。

     - 实现：

       ```java
       //事务流水线
       //执行所调用的聚合函数
       private String setCommon(Transaction trans, String method, int ttl, String... items) {
           String[] keys = new String[items.length];
           //给每个单词加上idx:前缀
           for (int i = 0; i < items.length; i++) {
               keys[i] = "idx:" + items[i];
           }
       
           String id = UUID.randomUUID().toString();
           try {
               //获取对应方法并执行
               trans.getClass()
                       .getDeclaredMethod(method, String.class, String[].class)
                       .invoke(trans, "idx:" + id, keys);
           } catch (Exception e) {
               throw new RuntimeException(e);
           }
           //设置生成的集合的过时时间
           trans.expire("idx:" + id, ttl);
           return id;
       }
       
       //交集操作
       public String intersect(Transaction trans, int ttl, String... items) {
           return setCommon(trans, "sinterstore", ttl, items);
       }
       
       //并集操作
       public String union(Transaction trans, int ttl, String... items) {
           return setCommon(trans, "sunionstore", ttl, items);
       }
       
       //差集操作
       public String difference(Transaction trans, int ttl, String... items) {
           return setCommon(trans, "sdiffstore", ttl, items);
       }
       ```

   - 分析并执行搜索

     - 如果只是包含某些单词，那么只需执行sinterstore。

     - 如果使用了“-”号表示不希望包含这个单词，则使用sdiffstore。

     - 如果使用了“+”号表示这个单词是前一个单词的同义词，搜索程序会首先收集各个同义词组并对他们执行union操作，然后再执行sintersotre。（如果带“+”号的单词前面有带”-“号的单词，那么程序会掠过那些带“-”号的单词，并把最先遇到的不带“-”号的单词看作是同义词）。

     - 什么都不加类似于且，+号类似于或，-号类似于非

     - 排序：关联度计算，判断一个文档具有更高关联度的方法就是看哪个文档的更新时间最接近当前时间。

     - 实现：解析查询语句

       ```java
       /**
        * 搜索查询语句的语法分析器
        *
        * @param queryString
        * @return
        */
       public Query parse(String queryString) {
           Query query = new Query();
           //存储同义词
           Set<String> current = new HashSet<>();
           //获取匹配的单词
           Matcher matcher = QUERY_RE.matcher(queryString.toLowerCase());
           while (matcher.find()){
               //获取匹配单词中的一个
               String word = matcher.group().trim();
               //获取单词的前缀
               char prefix = word.charAt(0);
               //如果前缀是+或-，则去掉前缀
               if (prefix == '+' || prefix == '-') {
                   word = word.substring(1);
               }
       
               //如果单词的长度小于2，或者是非用词，则过滤掉
               if (word.length() < 2 || STOP_WORDS.contains(word)) {
                   continue;
               }
       
               //如果前缀是-
               if (prefix == '-') {
                   //把单词添加到不需要的set里
                   query.unwanted.add(word);
                   continue;
               }
       
               //如果有同义词集合非空，且单词的前缀不等于+，创建一个新的同义词集合
               //反过来也就是说，第一个单词是一个普通的单词，只会加到同义词set里
               //下次如果遇到一个带+号的单词，那么就会跳过这个语句，直接执行下条语句
               //那么等到执行这条语句的时候，那么current里就会有多个单词，也就是多个同义词
               if (!current.isEmpty() && prefix != '+') {
                   query.all.add(new ArrayList<>(current));
                   current.clear();
               }
               current.add(word);
           }
       
           //如果有最后一个同义词
           //把它加到交集运算的集合里
           if (!current.isEmpty()){
               query.all.add(new ArrayList<>(current));
           }
           return query;
       }
       
       public class Query {
           //需要执行交集的单词
           public final List<List<String>> all = new ArrayList<>();
           //存储不需要的单词
           public final Set<String> unwanted = new HashSet<>();
       }
       ```

     - 实现：搜索结果

       ```java
       //解析和查询
       public String parseAndSearch(Jedis conn, String queryString, int ttl) {
           //解析查询语句
           Query query = parse(queryString);
           if (query.all.isEmpty()) {
               return null;
           }
       
           //要进行交集的列表
           List<String> toIntersect = new ArrayList<>();
           //遍历各个同义词列表
           for (List<String> syn : query.all) {
               //如果同义词列表布置一个，那么执行并集计算
               if (syn.size() > 1) {
                   Transaction trans = conn.multi();
                   toIntersect.add(union(trans, ttl, syn.toArray(new String[syn.size()])));
                   trans.exec();
               }
               //否则，直接把这个单词加入交集列表
               else {
                   toIntersect.add(syn.get(0));
               }
           }
       
           //交集结果
           String intersectResult;
           //对交集列表的集合进行交集操作
           if (toIntersect.size() > 1) {
               Transaction trans = conn.multi();
               intersectResult = intersect(
                       trans, ttl, toIntersect.toArray(new String[toIntersect.size()]));
               trans.exec();
           } else {
               intersectResult = toIntersect.get(0);
           }
       
           //对不需要列表里的集合进行差集运算
           if (!query.unwanted.isEmpty()) {
               String[] keys = query.unwanted
                       .toArray(new String[query.unwanted.size() + 1]);
               keys[keys.length - 1] = intersectResult;
               ArrayUtils.swap(keys, 0, keys.length - 1);
               Transaction trans = conn.multi();
               intersectResult = difference(trans, ttl, keys);
               trans.exec();
           }
           return intersectResult;
       }
       ```

     - 搜索并对结果进行排序

       ```java
       //搜索并对结果进行排序
       @SuppressWarnings("unchecked")
       public SearchResult searchAndSort(Jedis conn, String queryString, String sort, int ttl, int pageNum, int pageSize) {
           //决定文档是更具哪个属性进行排序的，以及是升序还是降序
           boolean desc = sort.startsWith("-");
           //获取排序的属性
           if (desc){
               sort = sort.substring(1);
           }
           //判断是以数值的方式进行排序还是以字母的方式
           //如果是updated或id字段进行排序，则使用数值
           //否则使用字母
           boolean alpha = !"updated".equals(sort) && !"id".equals(sort);
           //按照kb:doc:{sort}属性对idx:{id}进行排序
           String by = "kb:doc:*->" + sort;
       
           //查找查询结果集合的id
           String id = parseAndSearch(conn, queryString, ttl);
       
           Transaction trans = conn.multi();
           //获取结果集合的元素数量
           trans.scard("idx:" + id);
           SortingParams params = new SortingParams();
           if (desc) {
               params.desc();
           }
           if (alpha){
               params.alpha();
           }
           params.by(by);
           //分页
           params.limit((pageNum - 1) * pageSize, pageSize);
           trans.sort("idx:" + id, params);
           List<Object> results = trans.exec();
           return new SearchResult(id,
                   (Long) results.get(0),
                   (List<String>)results.get(1));
       }
       
       public class SearchResult {
           public final String id;
           public final long total;
           public final List<String> results;
       
           public SearchResult(String id, long total, List<String> results) {
               this.id = id;
               this.total = total;
               this.results = results;
           }
       }
       
       //添加文档的相关信息
       public void addDoc(Jedis conn, String updated, String id, String docid) {
           HashMap<String,String> values = new HashMap<>();
           values.put("updated", updated);
           values.put("id", id);
           conn.hmset("kb:doc:" + docid, values);
       }
       ```

2. 有序索引

   - 使用有序集合对搜索结果进行排序

     - 可以把存储“文档搜索结果的有序集合”和“文档更新时间的有序集合”，通过zinterstore命令，通过设置更新时间的分值为1，进行排序。

     - 问题：通过文章的更新时间和投票数进行排序

     - 使用两个有序集合分别记录文章的更新时间以及文章获得投票的数量，成员都是文章的ID。

     - 实现：搜索与排序，同时基于更新时间和投票数进行排序。

       ```java
       /**
        * 通过zinterstore按照更新时间和投票数的一定比例进行交集运算，
        * 生成有序集合
        * 再进行输出
        *
        * @param conn
        * @param queryString 查询字符串
        * @param desc 是否降序
        * @param weights 关键字的权重
        * @param ttl 超时时间
        * @param pageNum 页码
        * @param pageSize 每页条数
        * @return
        */
       @SuppressWarnings("unchecked")
       public SearchResult searchAndZsort(
               Jedis conn, String queryString, boolean desc, Map<String, Integer> weights, int ttl, int pageNum, int pageSize) {
           //获取搜索结果集合
           String id = parseAndSearch(conn, queryString, ttl);
       
           //获取排序的权重
           int updateWeight = weights.containsKey("update") ? weights.get("update") : 1;
           int voteWeight = weights.containsKey("vote") ? weights.get("vote") : 0;
       
       
           String[] keys = new String[]{id, "sort:update", "sort:votes"};
           Transaction trans = conn.multi();
           //对搜索结果集合，更新时间集合，和投票集合，按照给定的权重，进行球交集
           id = zintersect(
                   trans, ttl, new ZParams().weights(0, updateWeight, voteWeight), keys);
       
           //获取交集后，交集元素的个数
           trans.zcard("idx:" + id);
           //降序排列
           if (desc) {
               trans.zrevrange("idx:" + id, (pageNum - 1) * pageSize, pageNum * pageSize);
           }
           //升序排列
           else {
               trans.zrange("idx:" + id, (pageNum - 1) * pageSize, pageNum * pageSize);
           }
           List<Object> results = trans.exec();
       
           return new SearchResult(
                   id,
                   (Long) results.get(results.size() - 2),
                   // Note: it's a LinkedHashSet, so it's ordered
                   new ArrayList<>((Set<String>) results.get(results.size() - 1)));
       }
       
       private String zsetCommon(Transaction trans, String method, int ttl, ZParams params, String... sets) {
           String[] keys = new String[sets.length];
           for (int i = 0; i < sets.length; i++) {
               keys[i] = "idx:" + sets[i];
           }
       
           String id = UUID.randomUUID().toString();
           try {
               trans.getClass().getSuperclass()
                       .getDeclaredMethod(method, String.class, ZParams.class, String[].class)
                       .invoke(trans, "idx:" + id, params, keys);
           } catch (Exception e) {
               throw new RuntimeException(e);
           }
           trans.expire("idx:" + id, ttl);
           return id;
       }
       
       public String zintersect(Transaction trans, int ttl, ZParams params, String... sets) {
           return zsetCommon(trans, "zinterstore", ttl, params, sets);
       }
       
       public String zunion(
               Transaction trans, int ttl, ZParams params, String... sets) {
           return zsetCommon(trans, "zunionstore", ttl, params, sets);
       }
       
       //保存一个文档的更新时间和投票数到集合里
       public void saveSortVoteAndUpdate(Jedis conn, int update, int vote, String id) {
           conn.zadd("idx:sort:update", update, id);
           conn.zadd("idx:sort:votes", vote, id);
       }
       ```

     - 使用有序集合实现非数值排序

       - 有序集合的分值是以IEEE754双精度浮点数格式存储的，所以转换操作最大只能使用64个二进制，且不能全部使用64个二进制位，使用63个是可行的。

       - 对每个字符进行排序需要8位。

       - 转换操作：把字符串截取6位（不足6位扩展到6位），接着把字符串的每个字符都转换成ASCII值，最后将这些ASCII值合并为一个整数。也就是score = score * 257 + piece + 1;之前的分值*257并添加当前的分值。

       - 实现：把字符串转换成分值

         ```java
         //把字符串转换成分值
         public long stringToScore(String string, boolean ignoreCase) {
             //忽略大小写
             if (ignoreCase) {
                 string = string.toLowerCase();
             }
         
             //将字符转换成ASCII码值
             List<Integer> pieces = new ArrayList<>();
             for (int i = 0; i < Math.min(string.length(), 6); i++) {
                 pieces.add((int) string.charAt(i));
             }
         
             //补充到6位
             while (pieces.size() < 6) {
                 pieces.add(-1);
             }
         
             int score = 0;
             for (Integer piece : pieces) {
                 score = score * 257 + piece + 1;
             }
         
             //大于6位+1使得可以区分robber和robbers这样的单词
             return score * 2 + (string.length() > 6 ? 1 : 0);
         }
         ```

       - 实现：搜索并排序

         ```java
         //保存一个文档用于排序的分值到有序集合里
         public void saveSortScore(Jedis conn, long score, String id) {
             conn.zadd("idx:sort:score", score, id);
         }
         
         //搜索并通过字符前缀进行排序
         @SuppressWarnings("unchecked")
         public SearchResult searchAndZsortWithScore(
                 Jedis conn, String queryString, boolean desc, int ttl, int pageNum, int pageSize) {
             //获取搜索结果集合
             String id = parseAndSearch(conn, queryString, ttl);
         
             String[] keys = new String[]{id, "sort:score"};
             Transaction trans = conn.multi();
             //对搜索结果集合，和文档的分值集合，进行交集操作
             id = zintersect(trans, ttl, new ZParams().weights(0, 1), keys);
         
             //获取交集后，交集元素的个数
             trans.zcard("idx:" + id);
             //降序排列
             if (desc) {
                 trans.zrevrange("idx:" + id, (pageNum - 1) * pageSize, pageNum * pageSize);
             }
             //升序排列
             else {
                 trans.zrange("idx:" + id, (pageNum - 1) * pageSize, pageNum * pageSize);
             }
             List<Object> results = trans.exec();
         
             return new SearchResult(
                     id,
                     (Long) results.get(results.size() - 2),
                     new ArrayList<>((Set<String>) results.get(results.size() - 1)));
         }
         ```

       - 实现：使用字符串的分值进行自动补全，只支持前6位

         ```java
         //找出自动补全的分值范围，最大6位
         //如a，匹配的最小分值为a本身，最大为azzzzz
         //如abc，匹配的最小值为abc本身，最大为abczzz
         //如Abc，匹配的最小值为Abc本身，最大为abczzz
         //如AbCdEf，匹配的最小值和最大值都为如AbCdEf
         public long[] findScorePrefixRange(String prefix) {
             long start = Charter7.stringToScore(prefix, false);
             int addLenth = 6 - prefix.length();
             for (int i = 0; i < addLenth; i++) {
                 prefix = prefix + "z";
             }
             long end = Charter7.stringToScore(prefix, false);
             return new long[]{start, end};
         }
         
         //自动补全函数，通过把字符串转换为分值来实现
         @SuppressWarnings("unchecked")
         public Set<String> autocompleteOnPrefixWithScore(Jedis conn, String guild, String prefix) {
             long[] range = findScorePrefixRange(prefix.length() > 6 ? prefix.substring(0, 6) : prefix);
             long start = range[0];
             long end = range[1];
             String zsetName = "members:" + guild;
         
             //获取元素列表
             return conn.zrangeByScore(zsetName, start, end);
         }
         
         //加入公会，并附带名字的分值
         public void joinGuildWithScore(Jedis conn, String guild, String user) {
             conn.zadd("members:" + guild, Charter7.striScore(user, false), user);
         }
         ```

3. 广告定向

   - 广告服务器

     - 需要接收一系列定向参数以便挑选出具体的广告，包含浏览者的基本位置信息（IP，GPS）、浏览者的操作系统、Web浏览器、正在浏览的页面的内容，甚至最近浏览过的一些页面。
     - 广告预算：每个广告都会有一个随着时间减少的预算。一般，广告预算应该被分配到不同的时间上。可以基于小时数对广告的总预算进行划分，并在同一小时的不同时间段把预算分配给不同广告。

   - 对广告进行索引

     - 广告索引是一对一。被索引的广告通常都拥有像位置、年龄或性别这类必须的定向参数。

     - 计算广告的价格：

       - 按展示次数计费（cost per view）：又称CMP或按千次计费（cost per mille）
       - 按点击次数计费（cost per click）：CPC
       - 按执行动作次数计费（cost per action）：按购买次数收费（cost per acquisition）

     - 让广告的价格保持一致：估算CPM（estimated CPM），简称eCPM。对于CPC和CPA广告，可以按照相应规制计算出eCPM。

     - 计算CPC广告的eCPM：

       ```
       eCPM=每次点击的价格*广告的点击通过率(click-through rate,CTR)*1000
       广告的点击通过率=广告被点击的次数/广告展示的次数
       如eCPM=0.25*0.002*1000=0.5
       
       eCOM=广告被点击的次数/广告展示的次数*每次点击的价格*1000
       ```

       - 实现：

         ```java
         public enum Ecpm {CPC, CPA, CPM}
         public double toEcpm(Ecpm type, double views, double avg, double value) {
             switch(type){
                 case CPC:
                 case CPA:
                     return 1000. * value * avg / views;
                 case CPM:
                     return value;
             }
             return value;
         }
         ```

     - 计算CPA广告的eCPM

       ```
       eCPM=广告的点击通过率*目标网页上执行动作的概率*被执行动作的价格*1000
       如 eCPM=0.002*0.1*3*1000=0.6
       
       eCOM=目标网页上执行动作的次数/广告展示的次数*被执行动作的价格*1000
       ```

     - 将广告插入索引

       - 这个定向广告的实现接受两个定向选项：位置和内容。其中位置选项（包括城市、州和国家）是必须的，而广告与页面的内容直接的任何匹配单词是可选的，并且只作为广告的附加值存在。

       - 使用集合和有序集合构成的反向索引来存储广告ID。必须的位置定向参数会被存储到集合里面。

       - 索引做的事情：

         - 将广告与任意个位置关联起来
         - 将广告的平均点击次数和平均执行动作次数的相关新信息存储起来，使得可以对e'CPM
           做出合理估算。
         - 把能够对广告进行定向的单词都存储到集合里面。

       - 实现：

         ```java
         //对广告进行索引
         private Map<Ecpm,Double> AVERAGE_PER_1K = new HashMap<>();
         public void indexAd(Jedis conn, String id, String[] locations, String content, Ecpm type, double value) {
             Transaction trans = conn.multi();
         
             //把广告ID添加到索引相关的位置集合里
             for (String location : locations) {
                 trans.sadd("idx:req:" + location, id);
             }
         
             //对广告包含的单词进行索引
             Set<String> words = tokenize(content);
             for (String word : tokenize(content)) {
                 trans.zadd("idx:" + word, 0, id);
             }
         
             //每1000次操作的数量，如点击，购买等
             double avg = AVERAGE_PER_1K.containsKey(type) ? AVERAGE_PER_1K.get(type) : 1;
             //计算出eCPM
             double rvalue = toEcpm(type, 1000, avg, value);
         
             //把广告的id和对应的类型加到hash里
             trans.hset("type:", id, type.name().toLowerCase());
             //把广告的eCPM和对应的id加到zset里
             trans.zadd("idx:ad:value:", rvalue, id);
             //把广告的每次操作的价钱和队友的id加到zset里
             trans.zadd("ad:base_value:", value, id);
             //把能够对广告进行定向的单词记录起来
             for (String word : words){
                 trans.sadd("terms:" + id, word);
             }
             trans.exec();
         }
         ```

   - 执行广告定向操作

     - 在匹配用户所在位置的一系列广告里面，找出eCPM最高的那个广告。

     - 与Web页面相匹配的内容会作为附加值被计入由CPC和CPA计算出的eCPM里面，使得那些包含了匹配内容的广告能够更多地被展示出来。

     - 系统会记录下广告中包含的哪个单词改善或损害了广告的预期效果，并据此修改各个可选的定向单词的相对价格。

     - 对所有相关位置集合执行并集操作，产生出最初的一组广告，向浏览者进行展示；再分析广告所在页面，添加相关的附加值，并最终为每个广告计算出一个总计eCPM值；然后获取eCPM最高的哪个广告ID，记录一些关于本次定向操作的统计数据；最后返回那个广告。

     - 实现：

       - 寻找匹配位置的所有广告：

         ```java
         //寻找匹配位置的所有广告
         public String matchLocation(Transaction trans, String[] locations) {
             String[] required = new String[locations.length];
             for (int i = 0; i < locations.length; i++) {
                 required[i] = "req:" + locations[i];
             }
             return union(trans, 300, required);
         }
         ```

       - 计算定向附加值：

         - 基于页面内容和广告内容两者之间相匹配的单词，计算出应该给广告的eCPM价格加上多少增量。

         - 预先为每个广告中的每个单词都计算好了附加值，并将这些附加值存储到每个单词的有序集合里，其中有序集合的成员为广告的ID，而成员的分值则是给eCPM增加的分值。

         - 估算广告真正eCPM的最佳方法，就是找出最大附加值和最小附加值，然后计算它们的平均值，并将这个平均值用作多个匹配单词的附加值。（由更好的计算方法，但是次方法简单）

         - 通过zunionstore和max、min聚合函数，可以计算出广告的最大附加值和最小附加值。在执行zunionstore操作并使用sum作为聚合函数的时候，只要将最大附加值和最小附加值用作命令输入，并将最大附加值和最小附加值的权重设置为0.5就可以计算出平均附加值。

         - 实现：

           ```java
           //计算附加值
           public Pair<Set<String>, String> finishScoring(Transaction trans, String matched, String base, String content) {
               //寻找由和传进来的匹配zset里相匹配的单词的集合的Map
               Map<String, Integer> bonusEcpm = new HashMap<>();
               //获取页面内容的单词
               Set<String> words = tokenize(content);
               //找出那些即位于定向位置之内，又拥有页面内容其中一个单词的广告
               for (String word : words) {
                   //新生成的zset的score为单词zset对应的score
                   String wordBonus = zintersect(trans, 30, new ZParams().weights(0, 1), matched, word);
                   bonusEcpm.put(wordBonus, 1);
               }
           
               if (bonusEcpm.size() > 0) {
           
                   //新生成集合键值数组
                   String[] keys = new String[bonusEcpm.size()];
                   //权重数组
                   double[] weights = new double[bonusEcpm.size()];
                   int index = 0;
                   //把Map转换成为两个数组
                   for (Map.Entry<String, Integer> bonus : bonusEcpm.entrySet()) {
                       keys[index] = bonus.getKey();
                       weights[index] = bonus.getValue();
                       index++;
                   }
           
                   //计算最小附加值
                   ZParams minParams = new ZParams().aggregate(ZParams.Aggregate.MIN).weights(weights);
                   String minimum = zunion(trans, 30, minParams, keys);
           
                   //计算最大附加值
                   ZParams maxParams = new ZParams().aggregate(ZParams.Aggregate.MAX).weights(weights);
                   String maximum = zunion(trans, 30, maxParams, keys);
           
                   //计算来个附加值和基础eCPM的平均值，来个附加值各取0.25权重，基础eCPM取0.5
                   //获取计算后的集合
                   String result = zunion(trans, 30, new ZParams().weights(2, 1, 1), base, minimum, maximum);
                   return new Pair<>(words, result);
               }
           
               //如果次广告没有匹配的单词，则返回基础eCPM值
               return new Pair<>(words, base);
           }
           ```
     
   - 从用户行为中学习
   
     - 记录的信息包括：
   
       - 被定向至给定广告的单词
       - 给定广告被定向的总次数
       - 广告中的某个单词被用于计算附加值的总次数
   
     - 实现：使用集合存储被定向的单词，为每个广告创建一个有序集合，用于存储广告的展示次数以及广告包含的各个单词的展示次数。
   
       ```java
       //在广告定向操作完毕之后记录执行结果
       public void recordTargetingResult(Jedis conn, long targetId, String adId, Set<String> words) {
           //获得与广告匹配的那些单词
           Set<String> terms = conn.smembers("terms:" + adId);
           //获取广告的类型，如CPC, CPA, CPM
           String type = conn.hget("type:", adId);
       
           Transaction trans = conn.multi();
           terms.addAll(words);
           if (terms.size() > 0) {
               String matchedKey = "terms:matched:" + targetId;
               //将当前广告的数组，添加到次广告定义的单词匹配集合中
               for (String term : terms) {
                   trans.sadd(matchedKey, term);
               }
               trans.expire(matchedKey, 900);
           }
       
           //次类型的广告观看数+1
           trans.incr("type:" + type + ":views:");
           //此广告的观看数+1
           for (String term : terms) {
               trans.zincrby("views:" + adId, 1, term);
           }
           //广告的观看数+1
           trans.zincrby("views:" + adId, 1, "");
       
           List<Object> response = trans.exec();
       
           //获取广告观看数
           double views = (Double) response.get(response.size() - 1);
           //每100观看数更新一次eCPM
           if ((views % 100) == 0) {
               updateCpms(conn, adId);
           }
       }
       ```
   
     - 记录点击和动作
   
       ```java
       //记录点击和动作
       public void recordClick(Jedis conn, long targetId, String adId, boolean action) {
           //获取广告的类型
           String type = conn.hget("type:", adId);
           Ecpm ecpm = Enum.valueOf(Ecpm.class, type.toUpperCase());
       
           //广告的点击数key
           String clickKey = "clicks:" + adId;
           //广告的匹配单词集合key
           String matchKey = "terms:matched:" + targetId;
           //获取匹配单词的集合
           Set<String> matched = conn.smembers(matchKey);
           matched.add("");
       
           Transaction trans = conn.multi();
           //如果广告是一个按动作计费的广告
           if (Ecpm.CPA.equals(ecpm)) {
               //刷新被匹配单词的集合
               trans.expire(matchKey, 900);
               //如果是动作信息，则把clickKey替换成动作信息
               if (action) {
                   clickKey = "actions:" + adId;
               }
           }
       
           //根据广告的类型，维持一个全局的点击/动作计数器
           if (action && Ecpm.CPA.equals(ecpm)) {
               trans.incr("type:" + type + ":actions:");
           }else{
               trans.incr("type:" + type + ":clicks:");
           }
       
           //为本次广告以及被定向至该广告的单词记录本次点击
           for (String word : matched) {
               trans.zincrby(clickKey, 1, word);
           }
           trans.exec();
       
           //更新eCPM（因为每次点击都大约展示了100次左右）
           updateCpms(conn, adId);
       }
       ```
   
     - 更新eCPM
   
       - 计算出广告的点击通过率。
   
       - 计算出动作执行率。
   
       - 更新广告的eCPM。
   
       - 更新广告包含的每个单词的eCPM附加值。
   
       - ```java
         //更新eCPM
         @SuppressWarnings("unchecked")
         public void updateCpms(Jedis conn, String adId) {
             Transaction trans = conn.multi();
             //获取广告类型
             trans.hget("type:", adId);
             //获取广告的基本价格
             trans.zscore("ad:base_value:", adId);
             //获取与广告匹配的那些单词
             trans.smembers("terms:" + adId);
             List<Object> response = trans.exec();
             String type = (String) response.get(0);
             Double baseValue = (Double) response.get(1);
             Set<String> words = (Set<String>) response.get(2);
         
             //查看广告的eCPM是基于动作执行次数还是点击次数进行计算
             String which = "clicks";
             Ecpm ecpm = Enum.valueOf(Ecpm.class, type.toUpperCase());
             if (Ecpm.CPA.equals(ecpm)) {
                 which = "actions";
             }
         
             trans = conn.multi();
             //获取此类型广告的点技术
             trans.get("type:" + type + ":views:");
             //获取此类型广告的点击数或者操作数
             trans.get("type:" + type + ':' + which);
             response = trans.exec();
             String typeViews = (String) response.get(0);
             String typeClicks = (String) response.get(1);
         
             //将广告的点击率或动作执行率重新写入全局字典里
             AVERAGE_PER_1K.put(ecpm, 1000. *
                             Integer.valueOf(typeClicks != null ? typeClicks : "1") /
                             Integer.valueOf(typeViews != null ? typeViews : "1"));
         
             //如果处理的是一个CPM广告，那么它的eCPM已经计算完毕，无需再做其他处理
             if (Ecpm.CPM.equals(ecpm)) {
                 return;
             }
         
         
             String viewKey = "views:" + adId;
             String clickKey = which + ':' + adId;
         
             trans = conn.multi();
             //获取广告的展示数
             trans.zscore(viewKey, "");
             //获取广告的执行数
             trans.zscore(clickKey, "");
             response = trans.exec();
             Double adViews = (Double) response.get(0);
             Double adClicks = (Double) response.get(1);
         
             double adEcpm;
             //如果广告还没有被点击过
             if (adClicks == null || adClicks < 1) {
                 //使用已有的eCPM
                 Double score = conn.zscore("idx:ad:value:", adId);
                 adEcpm = score != null ? score : 0;
             }
             //计算广告的eCPM并更新它
             else {
                 adEcpm = toEcpm(
                         ecpm,
                         adViews != null ? adViews : 1,
                         adClicks,
                         baseValue);
                 conn.zadd("idx:ad:value:", adEcpm, adId);
             }
             //获取单词的展示次数和点击次数（或动作执行次数）
             for (String word : words) {
                 trans = conn.multi();
                 //获取单词的展示次数
                 trans.zscore(viewKey, word);
                 //获取单词的点击次数或动作的执行次数
                 trans.zscore(clickKey, word);
                 response = trans.exec();
                 Double views = (Double) response.get(0);
                 Double clicks = (Double) response.get(1);
         
                 //如果广告还没有被点击过，那么不对eCPM进行更新
                 if (clicks == null || clicks < 1) {
                     continue;
                 }
         
                 //计算单词eCPM
                 double wordEcpm = toEcpm(
                         ecpm,
                         views != null ? views : 1,
                         clicks,
                         baseValue);
                 //计算单词的附加值
                 double bonus = wordEcpm - adEcpm;
                 //将单词的附加值重新写入为广告包含的每个单词分别记录附加值的有序集合里
                 conn.zadd("idx:" + word, bonus, adId);
             }
         }
         ```
   
   - 改进方案
   
     - 随着时间流逝，每个广告的总点击次数和总展示次数都会稳定在一个特定的比率附近，而之后发生的点击和展示都没办法显著地改变这个比率，但真正的广告点击通过率却总是每时每刻都处于变化当中。定期降低广告的展示次数和点击次数（或动作执行次数），并将这一概念应用到不同类型的广告而设置的全局预期点击通过率（global expected CTR）上面。
     - 为了扩展系统的学习能力，让它可以对不止一个计数值进行学习，请考虑对前一天、前一个星期或者其他时间段发生的点击和动作进行技术，并基于时间段的寿命长度，为它们设置不同的权重。
     - 所有大型的广告网络都使用第二价格拍卖（second-price auction）的方式来决定广告位的费用，也就是说，系统不是按照固定的价格对每次点击、每前次展示或者每次动作进行收费，而是按照被定向广告中，价格排名第二的广告的价格进行收费。
     - 大多数广告网络都会为给定的一系列关键字设置多个广告，这些广告会在价格最高的位置上面交替出现，直到每个关键字的预算都被耗尽为止。这些交替出现的广告通常都有很高的价格和点击通过率，但这也意味着价格不够高的新广告将不会被展示。为此，系统可以在10~50%的时间里面，获取收益排名前100的广告。
     - 一个广告刚开始被添加到系统里面的时候，可以用于计算它的eCPM的信息是非常少的。前面展示的程序通过使用同类型广告的平均点击通过率暂时解决这个问题。另外一种解决方法，就是在给定广告类型的平均点击通过率以及基于广告目前已展示次数计算出的已展示点击通过率之间，构建一种简单的反线性关系或者反S形关系，直到广告有足够的展示次数为止。（2000~5000个左右才能确定一个可靠的点击通过率）。
     - 在广告展示次数达到2000~5000次之前，系统也可以通过人为地提高广告的点击通过率或eCPm来确保系统由足够多的流量来学习广告真正eCPM。
     - 本节的单词附加值学习方法与贝叶斯统计有相似的地方，可以通过真正的贝叶斯统计、神经网络、关联规制学习、聚类计算或其他技术来计算附加值。
     - 记录广告展示信息、点击信息以及动作信息的操作。这些操作会耗费一定时间，可以以外部任务的方式执行这些操作。
   
   - 处理无匹配内容
   
     - 处理targetAds函数。
     - finishScoring函数。
   
   - 对点击或者动作进行计数可以使用匹配的单词数量进行计数。
   
   - 优化eCPM计算：减少其传输次数。
   
4. 职位搜索

   - 逐个查找适合职位

     - 通过把求职者拥有技能集合和职位所需技能集合求差集，可以得出求职者是否满足职位要求。

     - 实现：

       ```java
       //添加一份工作
       public void addJob(Jedis conn, String jobId, String[] requiredSkills) {
           conn.sadd("job:" + jobId, requiredSkills);
       }
       
       //判断求职者是否具备职位所需的全部技能
       public  boolean isQualified(Jedis conn, String jobId, String[] candidateSkills) {
           String temp = UUID.randomUUID().toString();
           Transaction trans = conn.multi();
           trans.sadd(temp, candidateSkills);
           trans.expire(temp, 5);
           trans.sdiff("job:" + jobId, temp);
           List<Object> result = trans.exec();
           return ((Set<String>)result.get(2)).size() <= 0;
       }
       ```

   - 以搜索方式查找合适的职位

     - ```java
       //对工作进行索引
       public void indexJob(Jedis conn, String jobId, String... skills) {
           Transaction trans = conn.multi();
           //将职位ID添加到相应的技能集合里面
           for (String skill : skills) {
               trans.sadd("idx:skill:" + skill, jobId);
           }
           //将职位所需技能数量添加到所需技能数量zset里
           trans.zadd("idx:jobs:req", skills.length, jobId);
           trans.exec();
       }
       
       //查找适合的工作
       public Set<String> findJobs(Jedis conn, String... candidateSkills) {
           String[] keys = new String[candidateSkills.length];
           double[] weights = new double[candidateSkills.length];
           for (int i = 0; i < candidateSkills.length; i++) {
               keys[i] = "skill:" + candidateSkills[i];
               weights[i] = 1;
           }
       
           Transaction trans = conn.multi();
           String jobScores = zunion(trans, 30, new ZParams().weights(weights), keys);
           String finalResult = zintersect(trans, 30, new ZParams().weights(-1, 1), jobScores, "jobs:req");
           trans.exec();
           return conn.zrangeByScore(finalResult, 0, 0);
       }
       ```

   - 带熟练度的搜索

     ```java
     //对工作进行索引，带熟练度
     public void indexJob(Jedis conn, String jobId, Set<Skill> skills) {
         Transaction trans = conn.multi();
         //将职位ID添加到相应的技能集合里面
         int req = 0;
         for (Skill skill : skills) {
             trans.zadd("idx:skill:" + skill.getSkill(), skill.getProficiency(), jobId);
             //工作所需的技能和熟练度列表
             trans.zadd("idx:skills:" + jobId, skill.getProficiency(), skill.getSkill());
             req = req + skill.getProficiency();
         }
         //将职位所需技能熟练度总和添加到所需技能熟练度总和的zset里
         trans.zadd("idx:jobs:req", req, jobId);
         trans.exec();
     }
     public class Skill {
         private final String skill;
         private final int proficiency;
         public Skill(String skill, int proficiency) {
             this.skill = skill;
             this.proficiency = proficiency;
         }
         public String getSkill() {
             return skill;
         }
         public int getProficiency() {
             return proficiency;
         }
         @Override
         public boolean equals(Object o) {
             if (this == o) return true;
             if (o == null || getClass() != o.getClass()) return false;
     
             Skill skill1 = (Skill) o;
     
             if (proficiency != skill1.proficiency) return false;
             return skill != null ? skill.equals(skill1.skill) : skill1.skill == null;
         }
         @Override
         public int hashCode() {
             int result = skill != null ? skill.hashCode() : 0;
             result = 31 * result + proficiency;
             return result;
         }
     }
     
     //查找适合的工作，带熟练度
     public Set<String> findJobs(Jedis conn, Set<Skill> candidateSkills) {
         String[] keys = new String[candidateSkills.size()];
         double[] weights = new double[candidateSkills.size()];
         Iterator<Skill> skillIterator = candidateSkills.iterator();
         String temp = UUID.randomUUID().toString();
         Transaction trans = conn.multi();
         for (int i = 0; skillIterator.hasNext(); i++) {
             Skill skill  = skillIterator.next();
             keys[i] = "skill:" + skill.getSkill();
             weights[i] = 1;
             //把此求职者的技能和熟练度添加到一个zset里
             trans.zadd("idx:" + temp, skill.getProficiency(), skill.getSkill());
         }
     
         //获得此用户所满足全部技能的工作
         String jobScores = zunion(trans, 30, new ZParams().weights(weights), keys);
         String finalResult = zintersect(trans, 30, new ZParams().weights(-1, 1), jobScores, "jobs:req");
         trans.exec();
         Set<String> jobs = conn.zrangeByScore("idx:" + finalResult, 0, 0);
     
         //获得用户满足熟练度的工作
         Set<String> finishJobs = new HashSet<>();
         for (String job : jobs) {
             trans = conn.multi();
             String result = zintersect(trans, 60, new ZParams().weights(1, -1), temp, "skills:" + job);
             trans.zrangeWithScores("idx:" + result, 0, 0);
             Set<Tuple> tuples = (Set<Tuple>) trans.exec().get(2);
             Tuple tuple = tuples.iterator().next();
             if (tuple.getScore() >= 0) {
                 finishJobs.add(job);
             }
         }
         return finishJobs;
     }
     ```

   - 技能经验

# 8、构建简单的社交网站

1. 用户和状态

   - 用户信息

     ```java
     /**
      *
      * @param conn
      * @param login 用户名
      * @param name 姓名
      * @return
      */
     public Long createUser(Jedis conn, String login, String name) {
         String llogin = login.toLowerCase();
         //防止多个用户同时使用相同用户名来注册
         String lock = Charter6.acquireLock(conn, "user:" + llogin, 1);
         if (lock == null) {
             return null;
         }
     
         //users:存储用户名和用户ID的映射
         if (conn.hget("users:", llogin) != null) {
             Charter6.releaseLock(conn, "user:" + llogin, lock);
             return null;
         }
     
         Long id = conn.incr("user:id");
         Transaction trans = conn.multi();
         trans.hset("users:", llogin, id.toString());
         Map<String, String> map = new HashMap<>();
         map.put("login", login);
         map.put("id", id.toString());
         map.put("name", name);
         map.put("followers", "0");
         map.put("following", "0");
         map.put("posts", "0");
         map.put("signup", String.valueOf(System.currentTimeMillis() / 1000));
         trans.hmset("user:" + id, map);
         trans.exec();
         Charter6.releaseLock(conn, "user:" + llogin, lock);
         return id;
     }
     ```

   - 状态消息

     ```java
     //发布状态消息
     public Long createStatus(Jedis conn, Long uid, String message, Map<String, String> data) {
         Transaction trans = conn.multi();
         trans.hget("user:" + uid, "login");
         trans.incr("status:id");
         List<Object> results = trans.exec();
         String login = (String) results.get(0);
         Long id = (Long) results.get(1);
     
         //如果用户名不存在
         if (login == null) {
             return null;
         }
     
         data.put("message", message);
         data.put("posted", String.valueOf(System.currentTimeMillis() / 1000));
         data.put("id", id.toString());
         data.put("uid", uid.toString());
         data.put("login", login);
     
         trans = conn.multi();
         trans.hmset("status:" + id, data);
         trans.hincrBy("user:" + uid, "posts", 1);
         trans.exec();
         return id;
     }
     ```

2. 主页时间线

   - 在用户登录情况下访问Twitter时，首先看到的是他们的主页时间线，时间线是一个列表，它由用户以及用户正在关注的忍所发布的状态消息组成。

   - 可以用zset实现时间线，因为可以附带时间戳。

   - 实现：timeline可以控制是获取主页时间线，还是用户个人时间线

     ```java
     //获取状态消息列表
     public List<Map<String,String>> getStatusMessages(Jedis conn, Long uid, String timeline, int page, int count) {
         //反向，也就是从最新的获取
         Set<String> statusIds = conn.zrevrange(timeline + ":" + uid, (page - 1) * count, page * count - 1);
         Transaction trans = conn.multi();
         for (String id : statusIds) {
             trans.hgetAll("status:" + id);
         }
     
         //过滤掉不存在的状态消息
         List<Map<String,String>> statuses = new ArrayList<>();
         for (Object result : trans.exec()) {
             Map<String,String> status = (Map<String,String>)result;
             if (status != null && status.size() > 0){
                 statuses.add(status);
             }
         }
         return statuses;
     }
     ```

3. 关注者列表和正在关注列表

   - 存储结构

     ```
     粉丝
     followers:uid zset
     被关注者用户id 关注时间
     uid	time
     
     关注的人
     following:uid zset
     关注者用户id 关注时间
     uid	time
     ```

   - 关注用户：

     ```java
     //关注用户
     private static int HOME_TIMELINE_SIZE = 1000;
     public boolean followUser(Jedis conn, Long uid, Long otherUid) {
         String fkey1 = "following:" + uid;
         String fkey2 = "followers:" + otherUid;
     
         //如果用户已经关注过此otherUid的用户
         if (conn.zscore(fkey1, otherUid.toString()) != null) {
             return false;
         }
     
         Long now = System.currentTimeMillis() / 1000;
     
         Transaction trans = conn.multi();
         //把被关注者添加到用户的关注列表
         trans.zadd(fkey1, now, otherUid.toString());
         //把被关注者的粉丝列表添加用户的ID
         trans.zadd(fkey2, now, uid.toString());
         trans.zcard(fkey1);
         trans.zcard(fkey2);
         trans.zrevrangeWithScores("profile:" + otherUid, 0, HOME_TIMELINE_SIZE - 1);
         List<Object> results = trans.exec();
     
         Long following = (Long) results.get(results.size() - 3);
         Long followers = (Long) results.get(results.size() - 2);
         Set<Tuple> statusAndScore = (Set<Tuple>) results.get(results.size() -1);
     
         trans = conn.multi();
         trans.hset("user:" + uid, "following", following.toString());
         trans.hset("user:" + otherUid, "followers", followers.toString());
     
         //对用户主页时间线进行更新，保留最新的1000条信息
         if (statusAndScore.size() > 0) {
             for (Tuple status : statusAndScore){
                 trans.zadd("home:" + uid, status.getScore(), status.getElement());
             }
         }
     
         //用户主页时间线只保留最近的1000条信息
         trans.zremrangeByRank("home:" + uid, 0, -HOME_TIMELINE_SIZE - 1);
         trans.exec();
         return true;
     }
     ```

   - 取消关注

     ```java
     //取消关注
     @SuppressWarnings("unchecked")
     public boolean unfollowUser(Jedis conn, Long uid, Long otherUid) {
         String fkey1 = "following:" + uid;
         String fkey2 = "followers:" + otherUid;
     
         //如果用户未关注过此otherUid的用户
         if (conn.zscore(fkey1, otherUid.toString()) == null) {
             return false;
         }
     
         Transaction trans = conn.multi();
         trans.zrem(fkey1, otherUid.toString());
         trans.zrem(fkey2, uid.toString());
         //获取被关注者最近发布的状态消息
         trans.zrevrange("profile:" + otherUid, 0, HOME_TIMELINE_SIZE - 1);
         List<Object> results = trans.exec();
     
         Long following = (Long) results.get(results.size() - 3);
         Long followers = (Long) results.get(results.size() - 2);
         Set<String> statuses = (Set<String>) results.get(results.size() -1);
     
         trans = conn.multi();
         trans.hset("user:" + uid, "following", String.valueOf(following));
         trans.hset("user:" + otherUid, "followers", String.valueOf(followers));
         //删除用户主页的被取消关注者的状态消息
         if (statuses.size() > 0){
             for (String status : statuses) {
                 trans.zrem("home:" + uid, status);
             }
         }
         trans.exec();
         return true;
     }
     ```

   - 重新填充时间线：在取消关注后，时间线的状态消息数会减少，这时候可以用正在关注的其他用户的状态消息来填充。

     - 未想到如何获取这些数据。

   - 用户列表：用户列表里包含指定的多个用户发布的状态消息，也就是类似于分组。

     - 可以通过对followUser()和unfollowUser()函数更新，让它们能够通过可选的列表ID参数来支持用户列表特性，并添加相应的用户列表创建函数以及用户列表获取函数。
     - 重新填充时间线也要填充此列表。

4. 状态消息的发布与删除

   - 把新状态消息的ID添加到每个关注者的主页时间线里。

     - 如果用户的关注者数量较少，那么立即更新每个关注者的主页时间线。
     - 如果用户的关注者数量较多，把添加到主页时间线的操作转移到任务队列里执行。

   - 实现：对用户的个人时间线进行更新

     ```java
     //发布状态消息
     public Long postStatus(Jedis conn, Long uid, String message, Map<String,String> data) {
         Long id = createStatus(conn, uid, message, data);
         if (id == null){
             return null;
         }
     
         //获取消息发布的时间
         String postedString = conn.hget("status:" + id, "posted");
         if (postedString == null) {
             return null;
         }
     
         //将消息添加到个人时间线里
         long posted = Long.parseLong(postedString);
         conn.zadd("profile:" + uid, posted, id.toString());
     
         //将消息添加到关注者的主页时间线里
         syndicateStatus(conn, uid, id, posted, 0);
         return id;
     }
     ```

   - 实现：对关注者主页时间线进行更新

     ```java
     //对关注者的主页时间线进行更新
     private static int POSTS_PER_PASS = 1000;
     public void syndicateStatus(Jedis conn, long uid, long postId, long postTime, double start) {
         //获取接下来的1000个关注者
         Set<Tuple> followers = conn.zrangeByScoreWithScores("followers:" + uid, String.valueOf(start), "inf", 0, POSTS_PER_PASS);
     
         Transaction trans = conn.multi();
         for (Tuple tuple : followers) {
             String follower = tuple.getElement();
             start = tuple.getScore();
             trans.zadd("home:" + follower, postTime, String.valueOf(postId));
             //将集合进行排序
             trans.zrange("home:" + follower, 0, -1);
             trans.zremrangeByRank("home:" + follower, 0, - HOME_TIMELINE_SIZE - 1);
         }
         trans.exec();
     
         //把剩下任务加入延迟队列中执行
         if (followers.size() >= POSTS_PER_PASS) {
             Gson gson = new Gson();
             Map<String, String> map = new HashMap<>();
             map.put("uid", String.valueOf(uid));
             map.put("postId", String.valueOf(postId));
             map.put("postTime", String.valueOf(postTime));
             map.put("start", String.valueOf(start));
             executeLater(conn, "default", "syndicateStatus", gson.toJson(map), 0);
         }
     }
     ```

   - 删除状态消息

     ```java
     //删除状态消息
     public boolean deleteStatus(Jedis conn, Long uid, Long statusId) {
         String key = "status:" + statusId;
         //对指定的状态消息进行加锁，防止两个程序同时删除同一条状态消息
         String lock = Charter6.acquireLock(conn, key, 1);
         if (lock == null) {
             return false;
         }
     
         try {
             if (!uid.toString().equals(conn.hget(key, "uid"))) {
                 return false;
             }
     
             Transaction trans = conn.multi();
             trans.del(key);
             trans.zrem("profile:" + uid, statusId.toString());
             trans.zrem("home:" + uid, statusId.toString());
             trans.hincrBy("user:" + uid, "posts", -1);
             trans.exec();
             //删除关注者主页时间线的这条状态消息
             syndicateDeleteStatus(conn, uid, statusId, .0);
             return true;
         } finally {
             Charter6.releaseLock(conn, key, lock);
         }
     }
     ```

     ```java
     //删除关注者主页时间线的这条状态消息
     public void syndicateDeleteStatus(Jedis conn, Long uid, Long statusId, Double start) {
         //获取接下来的1000个关注者
         Set<Tuple> followers = conn.zrangeByScoreWithScores("followers:" + uid, start.toString(), "inf", 0, POSTS_PER_PASS);
     
         Transaction trans = conn.multi();
         for (Tuple tuple : followers) {
             String follower = tuple.getElement();
             start = tuple.getScore();
             trans.zrem("home:" + follower, statusId.toString());
         }
         trans.exec();
     
         //把剩下任务加入延迟队列中执行
         if (followers.size() >= POSTS_PER_PASS) {
             Gson gson = new Gson();
             Map<String, String> map = new HashMap<>();
             map.put("uid", uid.toString());
             map.put("statusId", statusId.toString());
             map.put("start", start.toString());
             executeLater(conn, "default", "syndicateDeleteStatus", gson.toJson(map), 0);
         }
     }
     ```

   - 还可以实现的特性

     - 私人用户，关注这些用户需要经过主人批准。
     - 收藏（注意状态消息的私密性）。
     - 用户之间可以进行私聊。
     - 对消息进行回复将产生一个会话流（conversation flow）。
     - 转发消息。
     - 使用@指明一个用户，或者使用#标记一个话题。
     - 记录用户使用@指名了谁。
     - 针对广告行为和滥用行为的投诉与管理机制。
     - 对状态消息进行“赞”。
     - 根据重要性对状态消息进行排序。
     - 在预先设置的一群用户之间进行私聊。
     - 对用户进行分组，只有组员能够关注组时间线（group timeline）并在里面发布状态消息。小组可以是公开的、私密的甚至是公告形式的。

5. 流API

   未看

# 9、降低内存占用

1. 短结构

   - 压缩列表：每个结点由两个长度值和一个字符串组成，第一个长度值是前一个结点的长度，第二个长度值是当前结点的长度，第三个值是被存储的字符串值。

     - 压缩列表配置

       ```
       list-max-ziplist-entries 512  //在被编码为压缩列表的情况下，允许包含的最大元素数量
       list-max-ziplist-value 64  //压缩列表每个结点的最大体积是多少字节
       
       hash-max-ziplist-entries 512 
       hash-max-ziplist-value 64
       
       zset-max-ziplist-entries 512
       zset-max-ziplist-value 64
       ```

   - 快速列表：quickList 是 zipList 和 linkedList 的混合体，它将 linkedList 按段切分，每一段使用 zipList 来紧凑存储，多个 zipList 之间使用双向指针串接起来。（默认使用）

     ![快速列表示意图](<https://images2018.cnblogs.com/blog/1294391/201808/1294391-20180827151851500-1561398239.png>)

   - 整数集合（intset）：以有序整数数组的方式存储集合，可以降低内存消耗，提升所有标准集合操作的执行速度。

     - 如果整数包含的所有成员都可以被解释为10进制整数，且这些整数春雨平台的有符号整数范围内，就使用整数集合。（也就是所有成员只能是整数）

     ```
     set-max-intset-entries 512
     ```

   - hashtable：当set的元素数量超过512后使用hashtable代替intset。

   - 长压缩列表和大整数集合带来的性能问题

2. 分片结构

   - 不再将值X存储到键Y里，而是将值X存储到键Y:<shardid>里

   - 对有序集合进行分片：因为zrange、zrangebyscore、zrank、zcount、zremrange、zremrangebyscore等命令的分片版本需要对有序集合的所有分片进行操作才能计算出命令的最终结果，所以速度比较慢。

     - 如果需要将信息存储到一个体积较大的有序集合里面，但是只会对分值排名前N位和后N位的元素进行操作，那么可以使用最高分值有序集合和最低分值有序集合，然后zadd为这两个有序集合添加新元素，并通过zremrangebyrank命令确保元素数量不会超过限制。
     - 搜索索引体积较大的情况下，使用分片是有序集合可以减少执行单个命令的延迟时间。但是查找分值最大和最小的元素需要调用很多次zunionstore和zremrangbyrank。

   - 分布式散列

     - 可以使用键计算出一个数字散列值，然后程序根据要存储键的总数量以及每个分片需要存储的键数，计算出所需的分片数量，用分片数量和键的散列值来决定应该把键存储到哪个分片里。

     - 可以减低内存消耗，因为可以压缩数据，如把hash-max-ziplist-entires设置为1024等。（短结构）

     - 实现：

       ```java
       /**
        * 根据基础键以及散列包含的键计算出分片键
        * @param base 基础键
        * @param key 键（field）
        * @param totalElements 预计元素总数量
        * @param shardSize 每个分片的大小
        * @return
        */
       public String shardKey(String base, String key, long totalElements, int shardSize) {
           long shardId;
           if (isDigit(key)) {
               //如果是数字键值，用很简单的除法求出分区ID
               shardId = Integer.parseInt(key, 10) / shardSize;
           } else {
               CRC32 crc = new CRC32();
               crc.update(key.getBytes());
               //分区数
               long shards = 2 * totalElements / shardSize;
               //用模求出分区ID
               shardId = Math.abs(((int) crc.getValue()) % shards);
           }
           //基础键+分区ID
           return base + ':' + shardId;
       }
       
       //判断一个字符串是否是数字组成的
       private boolean isDigit(String string) {
           for (char c : string.toCharArray()) {
               if (!Character.isDigit(c)) {
                   return false;
               }
           }
           return true;
       }
       ```

     - 分布式hset和hget等操作：

       ```java
       //分区的hset操作
       public Long shardHset(Jedis conn, String base, String key, String value, long totalElements, int shardSize) {
           //获取分区键
           String shard = shardKey(base, key, totalElements, shardSize);
           return conn.hset(shard, key, value);
       }
       
       //分区的hget操作
       public String shardHget(Jedis conn, String base, String key, long totalElements, int shardSize) {
           //获取分区键
           String shard = shardKey(base, key, totalElements, shardSize);
           return conn.hget(shard, key);
       }
       
       //分区的hdel操作
       public Long shardHdel(Jedis conn, String base, String key, long totalElements, int shardSize) {
           //获取分区键
           String shard = shardKey(base, key, totalElements, shardSize);
           return conn.hdel(shard, key);
       }
       
       //分区的hincrBy操作
       public Long shardHincrBy(Jedis conn, String base, String key, long totalElements, int shardSize, Long value) {
           //获取分区键
           String shard = shardKey(base, key, totalElements, shardSize);
           return conn.hincrBy(shard, key, value);
       }
       
       //分区的hincrByFloat操作
       public Double shardHincrByFloat(Jedis conn, String base, String key, long totalElements, int shardSize, double value) {
           //获取分区键
           String shard = shardKey(base, key, totalElements, shardSize);
           return conn.hincrByFloat(shard, key, value);
       }
       ```

   - 分片集合

     - 使用UUID的前15个16进制数字作为被分片的键。（注，如果用16位会导致花费额外时间将64位无符号整数转换成有符号整数）

     