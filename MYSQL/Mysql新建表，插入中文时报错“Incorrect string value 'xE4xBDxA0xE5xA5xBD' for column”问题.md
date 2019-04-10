# Mysql新建表，插入中文时报错“Incorrect string value: '\xE4\xBD\xA0\xE5\xA5\xBD' for column”问题

解决的方法：

**1、在建立表时设置默认字符串编码方式为utf8**

```mysql
CREATE TABLE test2(
ID INT PRIMARY KEY AUTO_INCREMENT, 
test_name VARCHAR(20), 
test_num INT)default charset = utf8;
```

 建立一个表，加上“default charset = utf8”，设置默认字符串编码方式为utf8。

**2、已经添加的表，需要设置一下：ALTER TABLE tablename CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;**

```mysql
ALTER TABLE test CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;              
```


**3、直接修改数据库的字符串编码属性：ALTER DATABASE databasename CHARACTER SET utf8 COLLATE utf8_unicode_ci**

```mysql
ALTER DATABASE testdb CHARACTER SET utf8 COLLATE utf8_unicode_ci;      
```