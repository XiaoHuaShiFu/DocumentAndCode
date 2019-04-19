centos 下yum安装redis以及使用

1、yum install redis      --查看是否有redis   yum源

```shell
[root@localhost ~]# yum install redis 
```

2、yum install epel-release    --下载fedora的epe仓库

```shell
[root@localhost ~]# yum install epel-release
```

3、yum install redis    -- 安装redis数据库

```shell
[root@localhost ~]# yum install redis 
```

4、service redis start

　  systemctl start redis.service   --开启redis服务

　  redis-server /etc/redis.conf   --开启方式二

```shell
[root@localhost ~]# service redis start
Redirecting to /bin/systemctl start redis.service
```

5、ps -ef | grep redis   -- 查看redis是否开启

```shell
[root@localhost ~]# systemctl start redis.service
[root@localhost ~]# ps -ef|grep redis
```

6、redis-cli       -- 进入redis服务

```
[root@localhost ~]# redis-cli 
127.0.0.1:6379> ?
redis-cli 3.2.10
To get help about Redis commands type:
​      "help @<group>" to get a list of commands in <group>
​      "help <command>" for help on <command>
​      "help <tab>" to get a list of possible help topics
​      "quit" to exit
To set redis-cli perferences:
​      ":set hints" enable online hints
​      ":set nohints" disable online hints
Set your preferences in ~/.redisclirc

127.0.0.1:6379> get 
(error) ERR wrong number of arguments for 'get' command
127.0.0.1:6379> get key
(nil)
127.0.0.1:6379> get key abc
(error) ERR wrong number of arguments for 'get' command
127.0.0.1:6379> set key abc 
OK
127.0.0.1:6379> get key abc
(error) ERR wrong number of arguments for 'get' command
127.0.0.1:6379> get key 
"abc"
127.0.0.1:6379> 
```

7、redis-cli  shutdown      --关闭服务

```shell
[root@localhost ~]# redis-cli
127.0.0.1:6379> shutdown
not connected> 
not connected> 
[root@localhost ~]# ps -ef |grep redis
root      5127  4497  0 08:34 pts/0    00:00:00 grep --color=auto redis
[root@localhost ~]# systemctl start redis.service
[root@localhost ~]# ps -ef |grep redis
redis     5134     1  0 08:34 ?        00:00:00 /usr/bin/redis-server 127.0.0.1:6379
root      5138  4497  0 08:35 pts/0    00:00:00 grep --color=auto redis
[root@localhost ~]# 
```

8、开放端口6379、6380的防火墙

/sbin/iptables -I INPUT -p tcp --dport 6379  -j ACCEPT   开启6379

/sbin/iptables -I INPUT -p tcp --dport 6380 -j ACCEPT  开启6380

```shell
[root@localhost ~]# /sbin/iptables -I INPUT -p tcp --dport 6379 -j ACCEPT
[root@localhost ~]# /sbin/iptables -I INPUT -p tcp --dport 6380 -j ACCEPT
```

9、使用redis  desktop manager连接redis