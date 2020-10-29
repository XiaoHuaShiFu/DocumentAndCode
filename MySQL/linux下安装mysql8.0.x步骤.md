# [linux下安装mysql8.0.x步骤](https://www.cnblogs.com/cxyyh/p/12431424.html)

https://www.cnblogs.com/cxyyh/p/12431424.html

**1.下载mysql**

mysql官网：https://dev.mysql.com/downloads/mysql/

![img](https://img2020.cnblogs.com/blog/1114349/202003/1114349-20200305223341309-1217424852.png)

![img](https://img2020.cnblogs.com/blog/1114349/202003/1114349-20200305223412180-1927028201.png)

将下载的mysql上传打linux

**2.解压并重命名**

```
[root@rsyncClient local]# tar -zxvf mysql-8.0.18-el7-x86_64.tar.gz -C /usr/local/ 

[root@rsyncClient local]# mv mysql-8.0.18-el7-x86_64/ mysql
```

**3.在mysql根目录下创建data目录，存放数据**

```
[root@rsyncClientopt]# cd /usr/local/mysql/

[root@rsyncClient mysql]# mkdir data
```

**4.创建mysql用户组和mysql用户**

```
[root@rsyncClient local]# groupadd mysql

[root@rsyncClient local]# useradd -g mysql mysql
```

**5.改变mysql目录权限**

```
[root@rsyncClient local]# chown -R mysql.mysql /usr/local/mysql/
```

**6.初始化数据库**

```
[root@rsyncClient mysql]# bin/mysqld --initialize --user=mysql --basedir=/usr/local/mysql 
--datadir=/usr/local/mysql/data
```

![img](https://img2020.cnblogs.com/blog/1114349/202003/1114349-20200305225512503-812827912.png)

**7.配置mysql**

在mysql/support-files创建文件my-default.cnf

```
[root@rsyncClient support-files]# cd /usr/local/mysql/support-files/
[root@rsyncClient support-files]# touch my-default.cnf
```

复制配置文件到/etc/my.cnf

```
[root@rsyncClient support-files]# cp -a ./my-default.cnf /etc/my.cnf 
cp: overwrite ‘/etc/my.cnf’? y
```

编辑my.cnf

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
[client]
port=3306
socket=/tmp/mysql.sock

[mysqld]
port=3306
user=mysql
socket=/tmp/mysql.sock
basedir=/usr/local/mysql
datadir=/usr/local/mysql/data
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

**8.配置环境变量**

编辑 / etc/profile 文件

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
[root@rsyncClient mysql]# vim /etc/profile
#配置mysql环境变量
PATH=/data/mysql/bin:/data/mysql/lib:$PATH
export PATH#让其生效
[root@rsyncClient mysql]# source /etc/profile#看环境变量是否生效
[root@rsyncClient mysql]# echo $PATH
/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/root/bin
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

**9.启动mysql**

```
[root@rsyncClient mysql]# systemctl start mysqld
```

**启动失败报错1:**

Job for mysql.service failed because the control process exited with error code. See "systemctl status mysql.service" and "journalctl -xe" for details.

解决方案:

```
[root@rsyncClient ~]# chown mysql:mysql -R /usr/local/mysql/
```

**启动失败报错2:**

```
[root@rsyncClient mysql]# service mysql start
/etc/init.d/mysql: ./bin/my_print_defaults: /lib/ld-linux.so.2: bad ELF interpreter: No such file or directory  Starting MySQL. ERROR! The server quit without updating PID file (/var/lib/mysql/rsyncClient.pid).去这个目录下面查看 cat/usr/local/mysql/data/rsyncClient.err错误，对应的的解决，这里错误是因为my.conf配置错误
```

 **启动失败报错3:**

```
mysql: error while loading shared libraries: libncurses.so.5: cannot open shared object file: No such file or directory
[root@rsyncClient init.d]# yum install libncurses.so.5以这个为例，如果缺少这样依赖，直接用yum安装
```

**启动失败报错4:**

```
[root@rsyncClient data]# mysql -uroot -p
Enter password: 
ERROR 2059 (HY000): Authentication plugin 'caching_sha2_password' cannot be loaded: /usr/lib/mysql/plugin/caching_sha2_password.so: cannot open shared object file: No such file or directory身份验证插件不能加载
```

解决办法:

```
[root@rsyncClient lib]# vim /etc/my.cnf
在这个[mysqld]下添加一行：
default_authentication_plugin=mysql_native_password
如果忘记了密码在加上:
skip-grant-tables(跳过密码验证)等设置了密码就去掉
```

**10 使用systemctl命令启动关闭mysql服务**

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
启动mysql服务：
 
#systemctl start mysqld.service
 
停止mysql服务
 
#systemctl stop mysqld.service
 
重启mysql服务
 
#systemctl restart mysqld.service
 
查看mysql服务当前状态
 
#systemctl status mysqld.service
 
设置mysql服务开机自启动
 
#systemctl enable mysqld.service
 
停止mysql服务开机自启动
 
#systemctl disable mysqld.service
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

**11.mysql的基本操作
**

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
# 使用mysql客户端连接mysql
```

\```>/usr/local/mysql/bin/mysql` `-u root -p password`

```
修改mysql的默认初始化密码
> alter user ``'root'``@``'localhost'` `identified by ``'root'``;
```

\# 创建用户 CREATE USER '用户名称'@'主机名称' INDENTIFIED BY '用户密码'

\> create user 'yehui'@'localhost' identified by 'yehui';

\#给所有远程登录的进行授权，此方式已经报错了

```
> grant all privileges ``on` `*.* to ``'root'``@``'%'` `identified ``by` `'root'` `with grant option;
ERROR 1064 (42000): You have an error ``in` `your SQL syntax; check the manual that corresponds to your MySQL server version ``for` `the right syntax to use near ``'identified by '``root``' with grant option'` `at line 1
# 修改root用户可以远程连接
> update mysql.user ``set` `host=``'%'` `where user=``'root'``;
```

 

```
# 授予权限 grant 权限 on 数据库.表 to '用户名'@'登录主机' [INDENTIFIED BY '用户密码'];
> grant replication slave on *.* to ``'yehui'``@``'localhost'``;
#刷新
>flush privileges;
```

\#权限问题可以参考https://www.cnblogs.com/php-linux/p/11561300.html

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

 **12.防火墙问题**

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
[root@rsyncClient data]# firewall-cmd --permanent --zone=public --add-port=3306/tcp  #允许访问
success
[root@rsyncClient data]# firewall-cmd --reload  #重新加载
success
```

[root@rsyncClient data]# firewall-cmd --permanent --zone=public --query-port=3306/tcp #查看是否开通访问权限
yes  

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

 

 

 

 

 