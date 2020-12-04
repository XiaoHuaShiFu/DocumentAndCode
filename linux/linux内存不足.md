# [linux内存不足](https://www.cnblogs.com/zgyw/p/12322077.html)

Linux内存不足优化

执行free -m 命令查看可用内存，发现cached占比过高，free可用内存过少。

![img](https://img2018.cnblogs.com/i-beta/1943163/202002/1943163-20200217154021443-1892069619.png)

 

 

Linux手动释放缓存的方法
Linux释放内存的命令：
sync
echo 1 > /proc/sys/vm/drop_caches

drop_caches的值可以是0-3之间的数字，代表不同的含义：
0：不释放（系统默认值）
1：释放页缓存
2：释放dentries和inodes
3：释放所有缓存

释放完内存后改回去让系统重新自动分配内存。
echo 0 >/proc/sys/vm/drop_caches

free -m #看内存是否已经释放掉了。

如果我们需要释放所有缓存，就输入下面的命令：
echo 3 > /proc/sys/vm/drop_caches

继续继续跟踪：监听JVM使用情况，找出原因，进行优化

# CentOS/Linux服务器的内存不够解决方法

## 使用虚拟内存扩展内存

1.打开终端，切换到root用户，输入：free -m查看内存状态
[maker@LLM ~]$ free -m
       total    used    free   shared buff/cache available
Mem:      992    189     79     13    722    614
Swap:      0     0     0
Swap也就是虚拟内存为0

2.选择一个较大的分区，建立分区文件
[root@LLM ~]# dd if=/dev/zero of=/opt/swap bs=1024 count=1024000
1024000+0 records in
1024000+0 records out
1048576000 bytes (1.0 GB) copied, 16.6877 s, 62.8 MB/s
[root@LLM ~]#

该命令表示在opt分区建立名为swap，大小为1G的虚拟内存文件

3.将swap文件设置为swap分区文件
chmod 600 /opt/swap  //注意更改swap文件的权限
[root@LLM ~]# mkswap /opt/swap
Setting up swapspace version 1, size = 1023996 KiB
no label, UUID=fc47f29e-31af-401e-856d-0fec5262179e

4.激活swap,启用分区交换文件
swapon /opt/swap

5.现在看下结果
[root@LLM ~]# free -m
       total    used    free   shared buff/cache available
Mem:      992    191     63     13    737    625
Swap:     999     0    999

**虚拟内存的设置部分就完成了，接下来讲一下卸载虚拟内存，这个需求也是存在的，比如你走上人生巅峰了，不屑于使用虚拟内存，就需要卸载掉了，就是任性！**

1.首先停止swap分区
[root@LLM ~]# swapoff /opt/swap
[root@LLM ~]# free -m
       total    used    free   shared buff/cache available
Mem:      992    191     63     13    738    626
Swap:      0     0     0

2.其次删除掉swap文件即可
首先看一下磁盘大小
[root@LLM ~]# df -h
Filesystem   Size Used Avail Use% Mounted on
/dev/vda1    40G 3.9G 34G 11% /
devtmpfs    487M  0 487M 0% /dev
tmpfs     497M 4.0K 497M 1% /dev/shm
tmpfs     497M 420K 496M 1% /run
tmpfs     497M  0 497M 0% /sys/fs/cgroup
tmpfs     100M  0 100M 0% /run/user/0
tmpfs     100M  0 100M 0% /run/user/1001
[root@LLM ~]# rm -rf /opt/swap
[root@LLM ~]# df -h
Filesystem   Size Used Avail Use% Mounted on
/dev/vda1    40G 3.0G 35G 8% /
devtmpfs    487M  0 487M 0% /dev
tmpfs     497M 4.0K 497M 1% /dev/shm
tmpfs     497M 420K 496M 1% /run
tmpfs     497M  0 497M 0% /sys/fs/cgroup
tmpfs     100M  0 100M 0% /run/user/0
tmpfs     100M  0 100M 0% /run/user/1001

可以看出删除后多了1G的空间。

# Linux 内存使用过高排查

 

```js
<article class="baidu_pl" style="box-sizing: inherit; outline: 0px; margin: 0px; padding: 16px 0px 0px; display: block; position: relative; color: rgb(51, 51, 51); font-family: "Microsoft YaHei", "SF Pro Display", Roboto, Noto, Arial, "PingFang SC", sans-serif; font-size: 14px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;">
```

Linux释放内存的命令：
sync
echo 1 > /proc/sys/vm/drop_caches

drop_caches的值可以是0-3之间的数字，代表不同的含义：
0：不释放（系统默认值）
1：释放页缓存
2：释放dentries和inodes
3：释放所有缓存

释放完内存后改回去让系统重新自动分配内存。
echo 0 >/proc/sys/vm/drop_caches

free -m #看内存是否已经释放掉了。

如果我们需要释放所有缓存，就输入下面的命令：
echo 3 > /proc/sys/vm/drop_caches

\######### Linux释放内存的相关知识 ###############

在Linux系统下，我们一般不需要去释放内存，因为系统已经将内存管理的很好。但是凡事也有例外，有的时候内存会被缓存占用掉，导致系统使用SWAP空 间影响性能，例如当你在linux下频繁存取文件后,物理内存会很快被用光,当程序结束后,内存不会被正常释放,而是一直作为caching。，此时就需 要执行释放内存（清理缓存）的操作了。

Linux系统的缓存机制是相当先进的，他会针对dentry（用于VFS，加速文件路径名到inode的转换）、Buffer Cache（针对磁盘块的读写）和Page Cache（针对文件inode的读写）进行缓存操作。但是在进行了大量文件操作之后，缓存会把内存资源基本用光。但实际上我们文件操作已经完成，这部分 缓存已经用不到了。这个时候，我们难道只能眼睁睁的看着缓存把内存空间占据掉吗？所以，我们还是有必要来手动进行Linux下释放内存的操作，其实也就是 释放缓存的操作了。/proc是一个虚拟文件系统,我们可以通过对它的读写操作做为与kernel实体间进行通信的一种手段.也就是说可以通过修改 /proc中的文件,来对当前kernel的行为做出调整.那么我们可以通过调整/proc/sys/vm/drop_caches来释放内存。要达到释 放缓存的目的，我们首先需要了解下关键的配置文件/proc/sys/vm/drop_caches。这个文件中记录了缓存释放的参数，默认值为0，也就 是不释放缓存。

一般复制了文件后,可用内存会变少，都被cached占用了，这是linux为了提高文件读取效率的做法：为了提高磁盘存取效率, Linux做了一些精心的设计, 除了对dentry进行缓存(用于VFS,加速文件路径名到inode的转换), 还采取了两种主要Cache方式：Buffer Cache和Page Cache。前者针对磁盘块的读写，后者针对文件inode的读写。这些Cache有效缩短了 I/O系统调用(比如read,write,getdents)的时间。"

释放内存前先使用sync命令做同步，以确保文件系统的完整性，将所有未写的系统缓冲区写到磁盘中，包含已修改的 i-node、已延迟的块 I/O 和读写映射文件。否则在释放缓存的过程中，可能会丢失未保存的文件。

[[root@centos](https://links.jianshu.com/go?to=mailto%3Aroot%40fcbu.com) ~]# free -m
total    used    free   shared  buffers   cached
Mem:     7979    7897     82     0     30    3918
-/ buffers/cache:    3948    4031
Swap:     4996    438    4558

第一行用全局角度描述系统使用的内存状况：
total 内存总数
used 已经使用的内存数，一般情况这个值会比较大，因为这个值包括了cache 应用程序使用的内存
free 空闲的内存数
shared 多个进程共享的内存总额
buffers 缓存，主要用于目录方面,inode值等（ls大目录可看到这个值增加）
cached 缓存，用于已打开的文件

第二行描述应用程序的内存使用：
-buffers/cache 的内存数:used - buffers - cached
buffers/cache 的内存数:free buffers cached
前个值表示-buffers/cache 应用程序使用的内存大小，used减去缓存值
后个值表示 buffers/cache 所有可供应用程序使用的内存大小，free加上缓存值

第三行表示swap的使用：
used 已使用
free 未使用

可用的内存=free memory buffers cached。

为什么free这么小，是否关闭应用后内存没有释放？
但实际上，我们都知道这是因为Linux对内存的管理与Windows不同，free小并不是说内存不够用了，应该看的是free的第二行最后一个值：-/ buffers/cache:    3948    4031 ，这才是系统可用的内存大小。

实际项目中的经验告诉我们，如果因为是应用有像内存泄露、溢出的问题，从swap的使用情况是可以比较快速可以判断的，但free上面反而比较难查看。我觉得既然核心是可以快速清空buffer或cache，但核心并没有这样做（默认值是0），我们不应该随便去改变它。

一般情况下，应用在系统上稳定运行了，free值也会保持在一个稳定值的，虽然看上去可能比较小。当发生内存不足、应用获取不到可用内存、OOM错 误等问题时，还是更应该去分析应用方面的原因，如用户量太大导致内存不足、发生应用内存溢出等情况，否则，清空buffer，强制腾出free的大小，可 能只是把问题给暂时屏蔽了，所以说一般情况下linux都不用经常手动释放内存。

1、cached主要负责缓存文件使用, 日志文件过大造成cached区内存增大把内存占用完 .

Free中的buffer和cache：（它们都是占用内存）：
buffer : 作为buffer cache的内存，是块设备的读写缓冲区
cache: 作为page cache的内存, 文件系统的cache
如果 cache 的值很大，说明cache住的文件数很多。

Linux 内存管理做了很多精心的设计，除了对dentry进行缓存（用于VFS，加速文件路径名到inode的转换），还采取了两种主要Cache方式：Buffer Cache和Page Cache，目的就是为了提升磁盘IO的性能。从低速的块设备上读取数据会暂时保存在内存中，即使数据在当时已经不再需要了，但在应用程序下一次访问该数据时，它可以从内存中直接读取，从而绕开低速的块设备，从而提高系统的整体性能。而Linux会充分利用这些空闲的内存，设计思想是内存空闲还不如拿来多缓存一些数据，等下次程序再次访问这些数据速度就快了，而如果程序要使用内存而系统中内存又不足时，这时不是使用交换分区，而是快速回收部分缓存，将它们留给用户程序使用。

因此，可以看出，buffers/cached真是百益而无一害，真正的坏处可能让用户产生一种错觉——Linux耗内存！其实不然，Linux并没有吃掉你的内存，只要还未使用到交换分区，你的内存所剩无几时，你应该感到庆幸，因为Linux 缓存了大量的数据，也许下一次你就从中受益！

2、手动释放cached

To free pagecache: echo 1 > /proc/sys/vm/drop_caches

To free dentries and inodes: echo 2 > /proc/sys/vm/drop_caches

To free pagecache, dentries and inodes: echo 3 > /proc/sys/vm/drop_caches

当你在Linux下频繁存取文件后,物理内存会很快被用光,当程序结束后,内存不会被正常释放,而是一直作为caching.这个问题,貌似有不少人在问,不过都没有看到有什么很好解决的办法.那么我来谈谈这个问题.

先来说说free命令

[root@server ~]# free -m
total used free shared buffers cached
Mem: 249 163 86 0 10 94
-/+ buffers/cache: 58 191
Swap: 511 0 511

其中:

total 内存总数

used 已经使用的内存数

free 空闲的内存数

shared 多个进程共享的内存总额

buffers Buffer Cache和cached Page Cache 磁盘缓存的大小

-buffers/cache 的内存数:used - buffers - cached

+buffers/cache 的内存数:free + buffers + cached

可用的memory=free memory+buffers+cached

有了这个基础后,可以得知,我现在used为163MB,free为86,buffer和cached分别为10,94

那么我们来看看,如果我执行复制文件,内存会发生什么变化.

[root@server ~]# cp -r /etc ~/test/
[root@server ~]# free -m
total used free shared buffers cached
Mem: 249 244 4 0 8 174
-/+ buffers/cache: 62 187
Swap: 511 0 511

在命令执行结束后,used为244MB,free为4MB,buffers为8MB,cached为174MB,天呐都被cached吃掉了.别紧张,这是为了提高文件读取效率的做法.

引用[http://www.wujianrong.com/archives/2007/09/linux_free.html](https://links.jianshu.com/go?to=http%3A%2F%2Fwww.wujianrong.com%2Farchives%2F2007%2F09%2Flinux_free.html)“为了提高磁盘存取效率, Linux做了一些精心的设计, 除了对dentry进行缓存(用于VFS,加速文件路径名到inode的转换), 还采取了两种主要Cache方式：Buffer Cache和Page Cache。前者针对磁盘块的读写，后者针对文件inode的读写。这些Cache有效缩短了 I/O系统调用(比如read,write,getdents)的时间。”

那么有人说过段时间,linux会自动释放掉所用的内存,我们使用free再来试试,看看是否有释放>?

[root@server test]# free -m
total used free shared buffers cached
Mem: 249 244 5 0 8 174
-/+ buffers/cache: 61 188
Swap: 511 0 511

MS没有任何变化,那么我能否手动释放掉这些内存呢?回答是可以的!

/proc是一个虚拟文件系统,我们可以通过对它的读写操作做为与kernel实体间进行通信的一种手段.也就是说可以通过修改/proc中的文件,来对当前kernel的行为做出调整.那么我们可以通过调整/proc/sys/vm/drop_caches来释放内存.操作如下:

[root@server test]# cat /proc/sys/vm/drop_caches
0
首先,/proc/sys/vm/drop_caches的值,默认为0

[root@server test]# sync

手动执行sync命令(描述:sync 命令运行 sync 子例程。如果必须停止系统，则运行 sync 命令以确保文件系统的完整性。sync 命令将所有未写的系统缓冲区写到磁盘中，包含已修改的 i-node、已延迟的块 I/O 和读写映射文件)

```javascript
[root@server test]# echo 3 > /proc/sys/vm/drop_caches [root@server test]# cat /proc/sys/vm/drop_caches 3 
```

 

将/proc/sys/vm/drop_caches值设为3

```javascript
[root@server test]# free -m total used free shared buffers cached Mem: 249 66 182 0 0 11 -/+ buffers/cache: 55 194 Swap: 511 0 511 
```

 

再来运行free命令,发现现在的used为66MB,free为182MB,buffers为0MB,cached为11MB.那么有效的释放了buffer和cache.

有关/proc/sys/vm/drop_caches的用法在下面进行了说明

```js
/proc/sys/vm/drop_caches (since Linux 2.6.16) Writing to this file causes the kernel to drop clean caches, dentries and inodes from memory, causing that memory to become free.  To free pagecache, use echo 1 > /proc/sys/vm/drop_caches; to free dentries and inodes, use echo 2 > /proc/sys/vm/drop_caches; to free pagecache, dentries and inodes, use echo 3 > /proc/sys/vm/drop_caches.  Because this is a non-destructive operation and dirty objects are not freeable, the user should run sync(8) first
</article>
```

 

标签: [linux](https://www.cnblogs.com/zgyw/tag/linux/)