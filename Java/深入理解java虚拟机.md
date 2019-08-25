# 2、Java内存区域与内存溢出异常

1. 

2. 运行时数据区域

   1.  

   2.  

   3.  

   4.  Java堆

      - 

3. HotSpot虚拟机对象探秘

   1.  对象的创建
      - 虚拟机解决线程安全问题的方法：
        1. CAS
        2. TLAB（Thread Local Allocation Buffer）：哪个线程要分配内存，就在哪个线程的TLAB上分配，只有TLAB用完并分配新的TLAB时，才需要同步锁定。
           - 虚拟机是否使用TLAB，可以通过-XX:+/-UseTLAB参数来设定。
   
4. OutOfMemoryError异常

   1. Java堆溢出
   - -Xms 为jvm启动时分配的内存，比如-Xms200m，表示分配200M。
      - -Xmx 为jvm运行过程中分配的最大内存，比如-Xms500m，表示jvm进程最多只能够占用500M内存。
   -  -XX:+HeapDumpOnOutOfMemoryError: 在出现内存溢出异常时Dump出当前的内存堆转储快照。
   2. 虚拟机栈和本地方法栈溢出
      - -Xss: 线程栈容量大小。为jvm启动的每个线程分配的内存大小，默认JDK1.4中是256K，JDK1.5+中是1M。
   3. 方法区和运行时常量池溢出
      - -XX:PermSize: 限制方法区大小
      - -XX:MaxPermSize
   4. 本机直接内存溢出
      - -XX:MaxDirectMemorySize=10M: 指定DirectMemory大小。

# 3、GC与内存分配策略

1.  概述
   - -XX:+PrintGDetails: 查看GC回收细节。
2.  对象已死吗
   1. 引用计数算法
   2.  
   3.  
   4.  
   5.  回收方法区
      - -Xnoclassgc: 是否堆类进行回收
      - -verbose:class、-XX:+TraceClassLoading、-XX:+TraceClassUnLoading查看类加载和卸载信息，其中-verbose:class和-XX:+TraceClassLoading可以在Product版的虚拟机种使用，-XX:+TraceClassUnLoading参数需要FastDebug版的虚拟机支持。 
3.  
4.  
5.  垃圾收集器
   1. Serial：单线程，停止所有工作。
   2. ParNew：Serial的多线程版本。
      - 与Serial共用的参数：
        - -XX:SurvivorRatio
        - -XX:PretenureSizeThrehold
        - -XX:HandlePromotionFailure
      - -XX:+UseConcMarkSweepGC：使用此新生代收集器。
      - -XX:+UseParNewGC：强制指定它。
      - -XX:ParallelGCThreads：限制垃圾收集的线程数。
   3. Parallel Scavenge：尽量实现高的吞吐量。
      - -XX:MaxGCPauseMillis：最大垃圾收集停顿时间。
      - -XX:GCTimeRatio：直接设置吞吐量大小。
      - -XX:UseAdaptiveSizePolicy：自动调节-Xmn、Eden、Survivor区的比例、晋升老年代对象大小-XX:PretenureSizeThrehold等细节参数。
   4. Serial Old：单线程。
      - 作为CMS收集器的后备预案，在并发收集发生Concurrent Mode Failure时使用。
   5. Parallel Old：多线程。
   6. CMS：并发低停顿。
      - -XX:CMSInitiatingOccupancyFraction：CMS触发时内存占用率。如果CMS运行期间内存不够，会导致Concurrent Mode Failure。
      - -XX:+UseCMSCompactAtFullCollection：在CMS找不到足够大的连续空间时触发一次压缩的Full GC。（默认开启）
      - -XX:CMSFullGCsBeforeCompaction：多少次不压缩的Full GC后，来一次压缩的。（默认0）
   7. G1：

# 4、

# 5、

# 6、类文件结构