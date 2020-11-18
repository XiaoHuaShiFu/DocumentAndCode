# [MySQL null与not null和null与空值''的区别](https://segmentfault.com/a/1190000009540449)

[空值](https://segmentfault.com/t/空值)[![img](https://avatar-static.segmentfault.com/949/844/9498447-54cb56e325a72_small) mysql](https://segmentfault.com/t/mysql)[null](https://segmentfault.com/t/null)

发布于 2017-05-24

![img](https://sponsor.segmentfault.com/lg.php?bannerid=0&campaignid=0&zoneid=25&loc=https%3A%2F%2Fsegmentfault.com%2Fa%2F1190000009540449&referer=https%3A%2F%2Fblog.csdn.net%2Fwangzaza%2Farticle%2Fdetails%2F79484069&cb=d907bb1e94)

相信很多用了`MySQL`很久的人，对这两个字段属性的概念还不是很清楚，一般会有以下疑问：

1. 我字段类型是`not null`，为什么我可以插入空值
2. 为毛`not null`的效率比`null`高
3. 判断字段不为空的时候，到底要 `select * from table where column <> ''` 还是要用 `select * from table wherecolumn is not null` 呢。

带着上面几个疑问，我们来深入研究一下`null 和 not null` 到底有什么不一样。
首先，我们要搞清楚“空值” 和 “NULL” 的概念：

- `空值是不占用空间的`
- mysql中的`NULL其实是占用空间的`，下面是来自于MYSQL官方的解释：

  “NULL columns require additional space in the row to record whether their values are NULL. For MyISAM tables, each NULL column takes one bit extra, rounded up to the nearest byte.”

**打个比方来说，你有一个杯子，空值代表杯子是真空的，NULL代表杯子中装满了空气，虽然杯子看起来都是空的，但是区别是很大的。**

搞清楚“空值”和“NULL”的概念之后，问题基本就明了了，我们搞个例子测试一下：

```
CREATE TABLE  `test` (  
 `col1` VARCHAR( 10 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,  
 `col2` VARCHAR( 10 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL  
) ENGINE = MYISAM ; 
```

插入数据：

```
INSERT INTO `test` VALUES (null,1);  
```

mysql发生错误：

```
#1048 - Column 'col1' cannot be null
```

再来一条

```
INSERT INTO `test` VALUES ('',1); 
```

成功插入。
可见，NOT NULL 的字段是不能插入“NULL”的，只能插入“空值”，上面的`问题1`也就有答案了。

对于`问题2`，上面我们已经说过了，`NULL 其实并不是空值，而是要占用空间`，所以mysql在进行比较的时候，NULL 会参与字段比较，所以对效率有一部分影响。
而且**`B树索引时不会存储NULL值的，所以如果索引的字段可以为NULL，索引的效率会下降很多`**。

我们再向test的表中插入几条数据：

```
INSERT INTO `test` VALUES ('', NULL);  
INSERT INTO `test` VALUES ('1', '2');  
```

现在表中数据：

![clipboard.png](https://segmentfault.com/img/bVOb3E?w=191&h=105)

现在根据需求，我要统计test表中col1不为空的所有数据，我是该用`“<> ''”` 还是 “IS NOT NULL” 呢，让我们来看一下结果的区别。

```
SELECT * FROM `test` WHERE col1 IS NOT NULL 
```

![clipboard.png](https://segmentfault.com/img/bVOb34?w=191&h=101)

```
SELECT * FROM `test` WHERE col1 <> ''
```

![clipboard.png](https://segmentfault.com/img/bVOb4b?w=183&h=55)

可以看到，结果迥然不同，**所以我们一定要根据业务需求，搞清楚到底是要用那种`搜索条件`**。

**MYSQL建议列属性尽量为NOT NULL**

长度验证：注意空值的''之间是没有空格的。

```
mysql> select length(''),length(null),length(' ');

+------------+--------------+--------------+

| length('') | length(null) | length(' ') |

+------------+--------------+--------------+

| 0 | NULL | 2 |

+------------+--------------+--------------+
```

**注意事项：**

1. 在进行`count()`统计某列的记录数的时候，如果采用的`NULL`值，**系统会自动忽略掉，但是空值是会进行统计到其中的。**
2. 判断`NULL` 用`IS NULL` 或者 `IS NOT NULL`, `SQL`语句函数中可以使用`ifnull()`函数来进行处理，判断空字符用`=''`或者 `<>''`来进行处理
3. 对于`MySQL`特殊的注意事项，对于`timestamp`数据类型，如果往这个数据类型插入的列插入`NULL`值，则出现的值是当前系统时间。插入空值，则会出现 `0000-00-00 00:00:00`
4. 对于空值的判断到底是使用`is null` 还是`=''` 要根据实际业务来进行区分。