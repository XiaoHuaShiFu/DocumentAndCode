# 2、Java内存区域与内存溢出异常

1. 

2. 运行时数据区域

   1.  

   2.  

   3.  

   4.  Java堆

      - -Xms 为jvm启动时分配的内存，比如-Xms200m，表示分配200M

        -Xmx 为jvm运行过程中分配的最大内存，比如-Xms500m，表示jvm进程最多只能够占用500M内存

        -Xss 为jvm启动的每个线程分配的内存大小，默认JDK1.4中是256K，JDK1.5+中是1M

3. HotSpot虚拟机对象探秘

   1.  对象的创建
      - 虚拟机解决线程安全问题的方法：
        1. CAS
        2. TLAB（Thread Local Allocation Buffer）：哪个线程要分配内存，就在哪个线程的TLAB上分配，只有TLAB用完并分配新的TLAB时，才需要同步锁定。
           - 虚拟机是否使用TLAB，可以通过-XX:+/-UseTLAB参数来设定。