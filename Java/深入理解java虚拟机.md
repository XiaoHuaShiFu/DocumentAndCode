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
      - -Xnoclassgc: 是否堆类进行回收。
      - -verbose:gc：查看垃圾收集过程。
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

# 7、

# 8、虚拟机字节码执行引擎

1. 
2.  运行时栈帧结构
   1. 局部变量表
      - long和double数据类型的读写分割成两次的做法，在局部变量表中不会出现数据安全问题，因为局部变量表是线程私有的。
3. 方法调用
   1.  
   2.  分派
      - 虚拟机（准确来说是编译器）在重载时是通过参数的静态类型二不是实际类型作为判定依据。
      - 重载方法中参数的自动转型可以连续发生多次，按照char->int->long->float->double->....->Object->arg...的顺序进行匹配。

# 9、类加载及执行子系统的案例与实战

1.  
2.  案例分析
   1.  
   2.  
   3.  字节码生成技术与动态代理的实现
      - 可以使用System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");查看动态代理产生的代理类Class文件。

# 10、

# 11、晚期（运行期）优化

1.  
2.  HotSpot虚拟机内的即时编译器
   1. 解析器与编译器
      - 可以使用-client或-server参数去强制指定虚拟机运行再Client模式或Server模式。
      - 可以使用-Xint参数强制虚拟机运行于“解析模式”。完全使用解析器。
      - 可以使用-Xcomp参数强制虚拟机运行于“编译模式”。优先采用编译方式执行程序徐，但是解析器仍然要再编译无法进行的情况下介入执行过程。
   2. 编译对象与触发条件
      - 可以通过-XX:CompileThreshold来设定方法调用计数器触发JIT编译的阈值。
        - Client模式下阈值是1500次。
        - Server模式下是10000次。
      - 虚拟机方法调用计数器默认会带半衰周期，使计数值减半，是在虚拟机进行垃圾收集时顺便进行的。
        - 可以使用虚拟机参数-XX:-UseCounterDecay来关闭热度衰减，这样执行时间足够长，绝大部分方法都会被编译成本地代码。
        - 可以使用-XX:CounterHalfLifeTime参数设置半衰周期的时间，单位是秒。
      - 虚拟机的回边计数器是为了触发OSR编译。
        - 可以通过-XX:BackEdgeThreshold设置阈值。（当前未使用）
        - 可以通过-XX:OnStackReplacePercentage来简介调整回边计数器的阈值。
          - 虚拟机运行在Client模式下：
            - 回边计数器阈值=方法调用计数器阈值（CompileThreshold）*OSR比率（OnStackReplacePercentage）/100。OnStackReplacePercentage默认933，因此回边计数器在Client模式下阈值为13995。
          - 虚拟机运行在Server模式下：
            - 回边计数器阈值=方法调用计数器阈值（CompileThreshold）*（OSR比率（OnStackReplacePercentage）- 解析器监控比率（InterpreterProfilePercentage））/100。OnStackReplacePercentage默认140，InterpreterProfilePercentage默认33，因此回边计数器在Server模式下阈值为10700。
   3. 编译过程
      - 可以通过参数-XX:-BackGroundCompilation来禁止后台编译，这样一旦触发JIT编译，执行线程向虚拟机提交编译请求后将一直等待，直到编译过程完成后再执行编译器输出的本地代码。
3. 编译优化技术
   1.  
   2.  
   3.  
   4.  
   5.  逃逸分析
      - 使用-XX: +DoEscapeAnalysis来手动开启逃逸分析。
      - 使用-XX:+PrintEscapeAnalysis来查看分析结果。
      - 使用-XX:EliminateAllocations来开启标量替换。
      - 使用-XX:+EliminateLocks来开启同步消除。
      - 使用-XX:+PrintEliminateAllocations查看标量的替换情况。

# 12、Java内存模型与线程

1. 
2.  
3.  Java内存模型
   1. 主内存与工作内存
      - 局部变量与方法参数是线程私有的，不会被共享，不存在竞争问题。（reference类型虽然不会被共享，但是它引用的对象再Java堆中可以被各个线程共享）
   2.  
   3.  对于volatile型变量的特殊规则
      - 一个变量定义为volatile之后，它将具备两种特性：
        1. 保证此变量对所有线程的可见性，当一个线程修改这个变量的值，新值对其他线程来说是可以立即得知的。
      - volatile变量再各个线程的工作内存中不存在一致性问题，在各个线程的工作内存中，volatile变量也是可以存在不一致的情况，但是由于每次使用前都要先刷新，执行引擎看不到不一致的情况，因此可以认为不存在一致性问题。
      - Java的运算并非原子操作，导致volatile变量的运算在并发下不是安全的。在不符合以下两条规则的运算场景中，仍然要通过加锁保证原子性。
        1. 运算结果并不依赖变量的当前值，或者能够确保只有单一的线程修改变量的值。
        2. 变量不需要与其他的状态变量共同参与不变约束。
      - volatile可以禁止指令重排序优化。
        - 如在通过while(!initialized){}时，如果不用volatile关键字，另外一个改变initialized的地方，在没有完全初始化之前，就把initialized=true操作先进行了。
   4. 对于long和double型变量的特殊规则
      - 虽然会拆分成两次读写，但是大部分虚拟机都会把64位数据的读写操作作为原子操作来对待，因此一般不需要把long和double变量专门声明位volatile。
4. Java与线程
   1. 线程的实现
      1.  
      2.  
      3.  
      4.  Java线程的实现
         - 在Solaris可以使用-XX:+UseLWPSynchronization（默认值）和-XX:+UseBoundThreads来明确指定虚拟机使用哪种线程模型。

# 13、线程安全与锁优化

1. 
2.  
3.  锁优化
   1. 自旋锁与自适应自旋
      - 通过-XX: +UseSpinning来开启自旋锁，默认开启。
      - 通过-XX: PreBlockSpin来更改自旋次数，默认 10。
      - 自适应自旋锁：自动调节自旋次数。
   2.  
   3.  
   4.  
   5.  偏向锁
      - 使用-XX:+UseBiasedLocking打开偏向锁，默认开启。