

# 数组

- MEMBER OF()：是数组中其中一个元素即可

```sql
SELECT * 
FROM customers  
WHERE 94507 MEMBER OF(custinfo->'$.zipcode');

-- 判断该组织是否有该标签
select count(*) from organization
where id = 1
and '竞赛' MEMBER OF(labels->'$')
```

- JSON_CONTAINS()：必须包含全部

```sql
SELECT * 
FROM customers
WHERE JSON_CONTAINS(custinfo->'$.zipcode', CAST('[94507,94582]' AS JSON));
```

- JSON_OVERLAPS()：包含其中一个即可

```sql
SELECT * 
FROM customers
WHERE JSON_OVERLAPS(custinfo->'$.zipcode', CAST('[94507,94582]' AS JSON));
```

# 多值索引（数组）

- 建表时创建

  ```sql
  CREATE TABLE customers (
      id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
      modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      custinfo JSON,
      INDEX zips( (CAST(custinfo->'$.zip' AS UNSIGNED ARRAY)) )
      );
  ```

- alter table创建

  ```sql
  ALTER TABLE customers ADD INDEX zips( (CAST(custinfo->'$.zip' AS UNSIGNED ARRAY)) );
  ```

- create index创建

  ```sql
  REATE INDEX zips ON customers ( (CAST(custinfo->'$.zip' AS UNSIGNED ARRAY)) );
  ```

- 把多值索引作为组合索引的一部分

  ```sql
  ALTER TABLE customers ADD INDEX comp(id, modified,
      (CAST(custinfo->'$.zipcode' AS UNSIGNED ARRAY)) );
  ```

  - 注：组合索引里只能有一个多值索引

# 函数索引

```sql
CREATE TABLE t1 (col1 INT, col2 INT, INDEX func_index ((ABS(col1))));
CREATE INDEX idx1 ON t1 ((col1 + col2));
CREATE INDEX idx2 ON t1 ((col1 + col2), (col1 - col2), col1);
ALTER TABLE t1 ADD INDEX ((col1 * 40) DESC);
```

- 表达式应该被包围

  ```sql
  -- 错
  CREATE INDEX idx2 ON t1 (col1 + col2, col1 - col2, col1);
  -- 正确
  CREATE INDEX idx2 ON t1 ((col1 + col2), (col1 - col2), col1);
  ```

- 使用函数索引构造前缀索引

  ```sql
  CREATE TABLE tbl (
    col1 LONGTEXT,
    INDEX idx1 ((SUBSTRING(col1, 1, 10)))
  );
  ```

- 使用函数索引可用构造JSON数据的索引

  ```sql
  CREATE TABLE employees (
    data JSON,
    INDEX ((CAST(data->>'$.name' AS CHAR(30))))
  );
  ```

# CAST函数

把一个数据转换成确定类型。

语法CAST(expr AS type)

```sql
-- 转换成JSON类型
CAST('[94507,94582]' AS JSON)
```

支持的数据类型

| 类型                             | 说明                                                         |
| -------------------------------- | ------------------------------------------------------------ |
| BINARY[(*`N`*)]                  |                                                              |
| CHAR[(*`N`*)] [*`charset_info`*] |                                                              |
| DATE                             |                                                              |
| DATETIME                         |                                                              |
| DECIMAL[(*`M`*[,*`D`*])]         |                                                              |
| DOUBLE                           |                                                              |
| FLOAT[(*`p`*)]                   |                                                              |
| JSON                             |                                                              |
| NCHAR[(*`N`*)]                   | 会产生本地字符集                                             |
| REAL                             |                                                              |
| SIGNED [INTEGER]                 |                                                              |
| TIME                             |                                                              |
| UNSIGNED [INTEGER]               |                                                              |
| YEAR                             | 产生一个年份，详情请看https://dev.mysql.com/doc/refman/8.0/en/cast-functions.html#function_cast |

# JSON 工具函数

- JSON_PRETTY(json_val)：产生一个好看格式的JSON数据

  ```sql
  mysql> SELECT JSON_PRETTY('123'); # scalar
  +--------------------+
  | JSON_PRETTY('123') |
  +--------------------+
  | 123                |
  +--------------------+
  
  mysql> SELECT JSON_PRETTY("[1,3,5]"); # array
  +------------------------+
  | JSON_PRETTY("[1,3,5]") |
  +------------------------+
  | [
    1,
    3,
    5
  ]      |
  +------------------------+
  
  mysql> SELECT JSON_PRETTY('{"a":"10","b":"15","x":"25"}'); # object
  +---------------------------------------------+
  | JSON_PRETTY('{"a":"10","b":"15","x":"25"}') |
  +---------------------------------------------+
  | {
    "a": "10",
    "b": "15",
    "x": "25"
  }   |
  +---------------------------------------------+
  
  mysql> SELECT JSON_PRETTY('["a",1,{"key1":
      '>    "value1"},"5",     "77" ,
      '>       {"key2":["value3","valueX",
      '> "valueY"]},"j", "2"   ]')\G  # nested arrays and objects
  *************************** 1. row ***************************
  JSON_PRETTY('["a",1,{"key1":
               "value1"},"5",     "77" ,
                  {"key2":["value3","valuex",
            "valuey"]},"j", "2"   ]'): [
    "a",
    1,
    {
      "key1": "value1"
    },
    "5",
    "77",
    {
      "key2": [
        "value3",
        "valuex",
        "valuey"
      ]
    },
    "j",
    "2"
  ]
  ```

-  JSON_STORAGE_FREE(json_val)：在使用JSON_SET(), JSON_REPLACE(), or JSON_REMOVE()更新后释放的空间。

  ```sql
  mysql> SELECT * FROM jtable;
  +--------------------------------+
  | jcol                           |
  +--------------------------------+
  | {"a": 10, "b": "wxyz", "c": 1} |
  +--------------------------------+
  1 row in set (0.00 sec)
  
  mysql> SELECT JSON_STORAGE_FREE(jcol) FROM jtable;
  +-------------------------+
  | JSON_STORAGE_FREE(jcol) |
  +-------------------------+
  |                      14 |
  +-------------------------+
  1 row in set (0.00 sec)
  ```

- JSON_STORAGE_SIZE(json_val)：JSON数据所占空间

  ```sql
  mysql> CREATE TABLE jtable (jcol JSON);
  Query OK, 0 rows affected (0.42 sec)
  
  mysql> INSERT INTO jtable VALUES
      ->     ('{"a": 1000, "b": "wxyz", "c": "[1, 3, 5, 7]"}');
  Query OK, 1 row affected (0.04 sec)
  
  mysql> SELECT
      ->     jcol,
      ->     JSON_STORAGE_SIZE(jcol) AS Size,
      ->     JSON_STORAGE_FREE(jcol) AS Free
      -> FROM jtable;
  +-----------------------------------------------+------+------+
  | jcol                                          | Size | Free |
  +-----------------------------------------------+------+------+
  | {"a": 1000, "b": "wxyz", "c": "[1, 3, 5, 7]"} |   47 |    0 |
  +-----------------------------------------------+------+------+
  1 row in set (0.00 sec)
  ```

# JSON值属性函数

- JSON_DEPTH(json_doc)：JSON数据深度

  空数组、空对象、值深度1；非空数组包含深度为1的元素，非空对象包含深度1的元素，深度2；否则深度都大于2。

  ```sql
  mysql> SELECT JSON_DEPTH('{}'), JSON_DEPTH('[]'), JSON_DEPTH('true');
  +------------------+------------------+--------------------+
  | JSON_DEPTH('{}') | JSON_DEPTH('[]') | JSON_DEPTH('true') |
  +------------------+------------------+--------------------+
  |                1 |                1 |                  1 |
  +------------------+------------------+--------------------+
  mysql> SELECT JSON_DEPTH('[10, 20]'), JSON_DEPTH('[[], {}]');
  +------------------------+------------------------+
  | JSON_DEPTH('[10, 20]') | JSON_DEPTH('[[], {}]') |
  +------------------------+------------------------+
  |                      2 |                      2 |
  +------------------------+------------------------+
  mysql> SELECT JSON_DEPTH('[10, {"a": 20}]');
  +-------------------------------+
  | JSON_DEPTH('[10, {"a": 20}]') |
  +-------------------------------+
  |                             3 |
  +-------------------------------+
  ```

- JSON_LENGTH(json_doc[, path])：JSON数据长度

  - 值长度1

  - 数组长度是元素个数

  - 对象长度是元素个数

  - 不嵌套

    ```sql
    mysql> SELECT JSON_LENGTH('[1, 2, {"a": 3}]');
    +---------------------------------+
    | JSON_LENGTH('[1, 2, {"a": 3}]') |
    +---------------------------------+
    |                               3 |
    +---------------------------------+
    mysql> SELECT JSON_LENGTH('{"a": 1, "b": {"c": 30}}');
    +-----------------------------------------+
    | JSON_LENGTH('{"a": 1, "b": {"c": 30}}') |
    +-----------------------------------------+
    |                                       2 |
    +-----------------------------------------+
    mysql> SELECT JSON_LENGTH('{"a": 1, "b": {"c": 30}}', '$.b');
    +------------------------------------------------+
    | JSON_LENGTH('{"a": 1, "b": {"c": 30}}', '$.b') |
    +------------------------------------------------+
    |                                              1 |
    +------------------------------------------------+
    ```

- JSON_TYPE(json_val)：JSON值的类型

  ```sql
  mysql> SET @j = '{"a": [10, true]}';
  mysql> SELECT JSON_TYPE(@j);
  +---------------+
  | JSON_TYPE(@j) |
  +---------------+
  | OBJECT        |
  +---------------+
  mysql> SELECT JSON_TYPE(JSON_EXTRACT(@j, '$.a'));
  +------------------------------------+
  | JSON_TYPE(JSON_EXTRACT(@j, '$.a')) |
  +------------------------------------+
  | ARRAY                              |
  +------------------------------------+
  mysql> SELECT JSON_TYPE(JSON_EXTRACT(@j, '$.a[0]'));
  +---------------------------------------+
  | JSON_TYPE(JSON_EXTRACT(@j, '$.a[0]')) |
  +---------------------------------------+
  | INTEGER                               |
  +---------------------------------------+
  mysql> SELECT JSON_TYPE(JSON_EXTRACT(@j, '$.a[1]'));
  +---------------------------------------+
  | JSON_TYPE(JSON_EXTRACT(@j, '$.a[1]')) |
  +---------------------------------------+
  | BOOLEAN                               |
  +---------------------------------------+
  ```

  - 参数为null时返回null

    ```sql
    mysql> SELECT JSON_TYPE(NULL);
    +-----------------+
    | JSON_TYPE(NULL) |
    +-----------------+
    | NULL            |
    +-----------------+
    ```

  - 可能返回的类型

    - Purely JSON types:
      - `OBJECT`: JSON objects
      - `ARRAY`: JSON arrays
      - `BOOLEAN`: The JSON true and false literals
      - `NULL`: The JSON null literal
    - Numeric types:
      - `INTEGER`: MySQL [`TINYINT`](https://dev.mysql.com/doc/refman/8.0/en/integer-types.html), [`SMALLINT`](https://dev.mysql.com/doc/refman/8.0/en/integer-types.html), [`MEDIUMINT`](https://dev.mysql.com/doc/refman/8.0/en/integer-types.html) and [`INT`](https://dev.mysql.com/doc/refman/8.0/en/integer-types.html) and [`BIGINT`](https://dev.mysql.com/doc/refman/8.0/en/integer-types.html) scalars
      - `DOUBLE`: MySQL [`DOUBLE`](https://dev.mysql.com/doc/refman/8.0/en/floating-point-types.html) [`FLOAT`](https://dev.mysql.com/doc/refman/8.0/en/floating-point-types.html) scalars
      - `DECIMAL`: MySQL [`DECIMAL`](https://dev.mysql.com/doc/refman/8.0/en/fixed-point-types.html) and [`NUMERIC`](https://dev.mysql.com/doc/refman/8.0/en/fixed-point-types.html) scalars
    - Temporal types:
      - `DATETIME`: MySQL [`DATETIME`](https://dev.mysql.com/doc/refman/8.0/en/datetime.html) and [`TIMESTAMP`](https://dev.mysql.com/doc/refman/8.0/en/datetime.html) scalars
      - `DATE`: MySQL [`DATE`](https://dev.mysql.com/doc/refman/8.0/en/datetime.html) scalars
      - `TIME`: MySQL [`TIME`](https://dev.mysql.com/doc/refman/8.0/en/time.html) scalars
    - String types:
      - `STRING`: MySQL `utf8` character type scalars: [`CHAR`](https://dev.mysql.com/doc/refman/8.0/en/char.html), [`VARCHAR`](https://dev.mysql.com/doc/refman/8.0/en/char.html), [`TEXT`](https://dev.mysql.com/doc/refman/8.0/en/blob.html), [`ENUM`](https://dev.mysql.com/doc/refman/8.0/en/enum.html), and [`SET`](https://dev.mysql.com/doc/refman/8.0/en/set.html)
    - Binary types:
      - `BLOB`: MySQL binary type scalars including [`BINARY`](https://dev.mysql.com/doc/refman/8.0/en/binary-varbinary.html), [`VARBINARY`](https://dev.mysql.com/doc/refman/8.0/en/binary-varbinary.html), [`BLOB`](https://dev.mysql.com/doc/refman/8.0/en/blob.html), and [`BIT`](https://dev.mysql.com/doc/refman/8.0/en/bit-type.html)
    - All other types:
      - `OPAQUE` (raw bits)

- JSON_VALID(val)：返回0或1表示值是不是一个JSON类型，val=null时返回null

  ```sql
  mysql> SELECT JSON_VALID('{"a": 1}');
  +------------------------+
  | JSON_VALID('{"a": 1}') |
  +------------------------+
  |                      1 |
  +------------------------+
  mysql> SELECT JSON_VALID('hello'), JSON_VALID('"hello"');
  +---------------------+-----------------------+
  | JSON_VALID('hello') | JSON_VALID('"hello"') |
  +---------------------+-----------------------+
  |                   0 |                     1 |
  +---------------------+-----------------------+
  ```

# JSON值修改函数

- JSON_ARRAY_APPEND(json_doc, path, val[, path, val] ...)：在数组里面追加值。

  ```sql
  mysql> SET @j = '["a", ["b", "c"], "d"]';
  mysql> SELECT JSON_ARRAY_APPEND(@j, '$[1]', 1);
  +----------------------------------+
  | JSON_ARRAY_APPEND(@j, '$[1]', 1) |
  +----------------------------------+
  | ["a", ["b", "c", 1], "d"]        |
  +----------------------------------+
  mysql> SELECT JSON_ARRAY_APPEND(@j, '$[0]', 2);
  +----------------------------------+
  | JSON_ARRAY_APPEND(@j, '$[0]', 2) |
  +----------------------------------+
  | [["a", 2], ["b", "c"], "d"]      |
  +----------------------------------+
  mysql> SELECT JSON_ARRAY_APPEND(@j, '$[1][0]', 3);
  +-------------------------------------+
  | JSON_ARRAY_APPEND(@j, '$[1][0]', 3) |
  +-------------------------------------+
  | ["a", [["b", 3], "c"], "d"]         |
  +-------------------------------------+
  
  mysql> SET @j = '{"a": 1, "b": [2, 3], "c": 4}';
  mysql> SELECT JSON_ARRAY_APPEND(@j, '$.b', 'x');
  +------------------------------------+
  | JSON_ARRAY_APPEND(@j, '$.b', 'x')  |
  +------------------------------------+
  | {"a": 1, "b": [2, 3, "x"], "c": 4} |
  +------------------------------------+
  mysql> SELECT JSON_ARRAY_APPEND(@j, '$.c', 'y');
  +--------------------------------------+
  | JSON_ARRAY_APPEND(@j, '$.c', 'y')    |
  +--------------------------------------+
  | {"a": 1, "b": [2, 3], "c": [4, "y"]} |
  +--------------------------------------+
  
  mysql> SET @j = '{"a": 1}';
  mysql> SELECT JSON_ARRAY_APPEND(@j, '$', 'z');
  +---------------------------------+
  | JSON_ARRAY_APPEND(@j, '$', 'z') |
  +---------------------------------+
  | [{"a": 1}, "z"]                 |
  +---------------------------------+
  ```

- JSON_ARRAY_INSERT(json_doc, path, val[, path, val] ...)：在数组中插入值

  ```sql
  mysql> SET @j = '["a", {"b": [1, 2]}, [3, 4]]';
  mysql> SELECT JSON_ARRAY_INSERT(@j, '$[1]', 'x');
  +------------------------------------+
  | JSON_ARRAY_INSERT(@j, '$[1]', 'x') |
  +------------------------------------+
  | ["a", "x", {"b": [1, 2]}, [3, 4]]  |
  +------------------------------------+
  mysql> SELECT JSON_ARRAY_INSERT(@j, '$[100]', 'x');
  +--------------------------------------+
  | JSON_ARRAY_INSERT(@j, '$[100]', 'x') |
  +--------------------------------------+
  | ["a", {"b": [1, 2]}, [3, 4], "x"]    |
  +--------------------------------------+
  mysql> SELECT JSON_ARRAY_INSERT(@j, '$[1].b[0]', 'x');
  +-----------------------------------------+
  | JSON_ARRAY_INSERT(@j, '$[1].b[0]', 'x') |
  +-----------------------------------------+
  | ["a", {"b": ["x", 1, 2]}, [3, 4]]       |
  +-----------------------------------------+
  mysql> SELECT JSON_ARRAY_INSERT(@j, '$[2][1]', 'y');
  +---------------------------------------+
  | JSON_ARRAY_INSERT(@j, '$[2][1]', 'y') |
  +---------------------------------------+
  | ["a", {"b": [1, 2]}, [3, "y", 4]]     |
  +---------------------------------------+
  mysql> SELECT JSON_ARRAY_INSERT(@j, '$[0]', 'x', '$[2][1]', 'y');
  +----------------------------------------------------+
  | JSON_ARRAY_INSERT(@j, '$[0]', 'x', '$[2][1]', 'y') |
  +----------------------------------------------------+
  | ["x", "a", {"b": [1, 2]}, [3, 4]]                  |
  +----------------------------------------------------+
  ```

- JSON_INSERT(json_doc, path, val[, path, val] ...)：给JSON数据插入值

  - 存在的值不会被覆盖，但是可以发现'[true, false]'没有被转换成JSON格式

    ```sql
    mysql> SET @j = '{ "a": 1, "b": [2, 3]}';
    mysql> SELECT JSON_INSERT(@j, '$.a', 10, '$.c', '[true, false]');
    +----------------------------------------------------+
    | JSON_INSERT(@j, '$.a', 10, '$.c', '[true, false]') |
    +----------------------------------------------------+
    | {"a": 1, "b": [2, 3], "c": "[true, false]"}        |
    +----------------------------------------------------+
    ```

    - 正确做法

      ```sql
      mysql> SELECT JSON_INSERT(@j, '$.a', 10, '$.c', CAST('[true, false]' AS JSON));
      +------------------------------------------------------------------+
      | JSON_INSERT(@j, '$.a', 10, '$.c', CAST('[true, false]' AS JSON)) |
      +------------------------------------------------------------------+
      | {"a": 1, "b": [2, 3], "c": [true, false]}                        |
      +------------------------------------------------------------------+
      1 row in set (0.00 sec)
      ```

- JSON_MERGE_PATCH(json_doc, json_doc[, json_doc] ...)：

  - 如果第一个参数不是一个对象，那么结果将与一个空对象和第二个参数合并结果相同。

  - 如果第二个参数不是一个对象，合并结果是第二个对象。

  - 如果两个参数都是对象，合并结果是下面所示对象：

    - 第一个对象里面没有与第二个对象相同key的成员；
    - 第二个对象里面没有与第一个对象相同key的成员；
    - 所有第一第二个对象都有的且第二个对象里的值不是null的成员。这个过程会递归进行。

  - ```sql
    mysql> SELECT JSON_MERGE_PATCH('[1, 2]', '[true, false]');
    +---------------------------------------------+
    | JSON_MERGE_PATCH('[1, 2]', '[true, false]') |
    +---------------------------------------------+
    | [true, false]                               |
    +---------------------------------------------+
    
    mysql> SELECT JSON_MERGE_PATCH('{"name": "x"}', '{"id": 47}');
    +-------------------------------------------------+
    | JSON_MERGE_PATCH('{"name": "x"}', '{"id": 47}') |
    +-------------------------------------------------+
    | {"id": 47, "name": "x"}                         |
    +-------------------------------------------------+
    
    mysql> SELECT JSON_MERGE_PATCH('1', 'true');
    +-------------------------------+
    | JSON_MERGE_PATCH('1', 'true') |
    +-------------------------------+
    | true                          |
    +-------------------------------+
    
    mysql> SELECT JSON_MERGE_PATCH('[1, 2]', '{"id": 47}');
    +------------------------------------------+
    | JSON_MERGE_PATCH('[1, 2]', '{"id": 47}') |
    +------------------------------------------+
    | {"id": 47}                               |
    +------------------------------------------+
    
    mysql> SELECT JSON_MERGE_PATCH('{ "a": 1, "b":2 }',
         >     '{ "a": 3, "c":4 }');
    +-----------------------------------------------------------+
    | JSON_MERGE_PATCH('{ "a": 1, "b":2 }','{ "a": 3, "c":4 }') |
    +-----------------------------------------------------------+
    | {"a": 3, "b": 2, "c": 4}                                  |
    +-----------------------------------------------------------+
    
    mysql> SELECT JSON_MERGE_PATCH('{ "a": 1, "b":2 }','{ "a": 3, "c":4 }',
         >     '{ "a": 5, "d":6 }');
    +-------------------------------------------------------------------------------+
    | JSON_MERGE_PATCH('{ "a": 1, "b":2 }','{ "a": 3, "c":4 }','{ "a": 5, "d":6 }') |
    +-------------------------------------------------------------------------------+
    | {"a": 5, "b": 2, "c": 4, "d": 6}                                              |
    +-------------------------------------------------------------------------------+
    ```

  - 可以通过指定第二对象的成员为null去移除第一个对象里相同的成员。

    ```sql
    mysql> SELECT JSON_MERGE_PATCH('{"a":1, "b":2}', '{"b":null}');
    +--------------------------------------------------+
    | JSON_MERGE_PATCH('{"a":1, "b":2}', '{"b":null}') |
    +--------------------------------------------------+
    | {"a": 1}                                         |
    +--------------------------------------------------+
    ```

  - 这是一个递归进行的例子。

    ```sql
    mysql> SELECT JSON_MERGE_PATCH('{"a":{"x":1}}', '{"a":{"y":2}}');
    +----------------------------------------------------+
    | JSON_MERGE_PATCH('{"a":{"x":1}}', '{"a":{"y":2}}') |
    +----------------------------------------------------+
    | {"a": {"x": 1, "y": 2}}                            |
    +----------------------------------------------------+
    ```

- JSON_MERGE_PRESERVE(json_doc, json_doc[, json_doc] ...)：合并两个JSON。如果参数为null返回null。

  - 合并规则

    - 合并两个数组为一个。

    - 合并两个对象为一个。

    - 一个纯值（scalar value）被自动装配成一个数组。

    - 一个数组与一个对象合并时，对象自动装配成一个数组。

      ```sql
      mysql> SELECT JSON_MERGE_PRESERVE('[1, 2]', '[true, false]');
      +------------------------------------------------+
      | JSON_MERGE_PRESERVE('[1, 2]', '[true, false]') |
      +------------------------------------------------+
      | [1, 2, true, false]                            |
      +------------------------------------------------+
      
      mysql> SELECT JSON_MERGE_PRESERVE('{"name": "x"}', '{"id": 47}');
      +----------------------------------------------------+
      | JSON_MERGE_PRESERVE('{"name": "x"}', '{"id": 47}') |
      +----------------------------------------------------+
      | {"id": 47, "name": "x"}                            |
      +----------------------------------------------------+
      
      mysql> SELECT JSON_MERGE_PRESERVE('1', 'true');
      +----------------------------------+
      | JSON_MERGE_PRESERVE('1', 'true') |
      +----------------------------------+
      | [1, true]                        |
      +----------------------------------+
      
      mysql> SELECT JSON_MERGE_PRESERVE('[1, 2]', '{"id": 47}');
      +---------------------------------------------+
      | JSON_MERGE_PRESERVE('[1, 2]', '{"id": 47}') |
      +---------------------------------------------+
      | [1, 2, {"id": 47}]                          |
      +---------------------------------------------+
      
      mysql> SELECT JSON_MERGE_PRESERVE('{ "a": 1, "b": 2 }',
           >    '{ "a": 3, "c": 4 }');
      +--------------------------------------------------------------+
      | JSON_MERGE_PRESERVE('{ "a": 1, "b": 2 }','{ "a": 3, "c":4 }') |
      +--------------------------------------------------------------+
      | {"a": [1, 3], "b": 2, "c": 4}                                |
      +--------------------------------------------------------------+
      
      mysql> SELECT JSON_MERGE_PRESERVE('{ "a": 1, "b": 2 }','{ "a": 3, "c": 4 }',
           >    '{ "a": 5, "d": 6 }');
      +----------------------------------------------------------------------------------+
      | JSON_MERGE_PRESERVE('{ "a": 1, "b": 2 }','{ "a": 3, "c": 4 }','{ "a": 5, "d": 6 }') |
      +----------------------------------------------------------------------------------+
      | {"a": [1, 3, 5], "b": 2, "c": 4, "d": 6}                                         |
      +----------------------------------------------------------------------------------+
      ```

  - 与JSON_MERGE_PATCH()的区别

    - JSON_MERGE_PATCH()移除任何在第一个对象里出现在第二个对象里的key，提供值如果第二个对象的值非null。

    - JSON_MERGE_PRESERVE()则是把第二个对象里的值 APPENDS 进 第一个对象的值里。

      ```sql
      mysql> SET @x = '{ "a": 1, "b": 2 }',
           >     @y = '{ "a": 3, "c": 4 }',
           >     @z = '{ "a": 5, "d": 6 }';
      
      mysql> SELECT  JSON_MERGE_PATCH(@x, @y, @z)    AS Patch,
          ->         JSON_MERGE_PRESERVE(@x, @y, @z) AS Preserve\G
      *************************** 1. row ***************************
         Patch: {"a": 5, "b": 2, "c": 4, "d": 6}
      Preserve: {"a": [1, 3, 5], "b": 2, "c": 4, "d": 6}
      ```

- JSON_REMOVE(json_doc, path[, path] ...)：移除JSON里一个值。移除不存在的值不会报错。

  ```sql
  mysql> SET @j = '["a", ["b", "c"], "d"]';
  mysql> SELECT JSON_REMOVE(@j, '$[1]');
  +-------------------------+
  | JSON_REMOVE(@j, '$[1]') |
  +-------------------------+
  | ["a", "d"]              |
  +-------------------------+
  ```

- JSON_REPLACE(json_doc, path, val[, path, val] ...)：代替存在的值。

  ```sql
  mysql> SET @j = '{ "a": 1, "b": [2, 3]}';
  mysql> SELECT JSON_REPLACE(@j, '$.a', 10, '$.c', '[true, false]');
  +-----------------------------------------------------+
  | JSON_REPLACE(@j, '$.a', 10, '$.c', '[true, false]') |
  +-----------------------------------------------------+
  | {"a": 10, "b": [2, 3]}                              |
  +-----------------------------------------------------+
  ```

- JSON_SET(json_doc, path, val[, path, val] ...)：代替存在的值，增加不存在的值。参数为null返回null。

  - JSON_INSERT()：插入值如果不存在。
  - JSON_REPLACE()：代替值如果存在。

  ```sql
  mysql> SET @j = '{ "a": 1, "b": [2, 3]}';
  mysql> SELECT JSON_SET(@j, '$.a', 10, '$.c', '[true, false]');
  +-------------------------------------------------+
  | JSON_SET(@j, '$.a', 10, '$.c', '[true, false]') |
  +-------------------------------------------------+
  | {"a": 10, "b": [2, 3], "c": "[true, false]"}    |
  +-------------------------------------------------+
  mysql> SELECT JSON_INSERT(@j, '$.a', 10, '$.c', '[true, false]');
  +----------------------------------------------------+
  | JSON_INSERT(@j, '$.a', 10, '$.c', '[true, false]') |
  +----------------------------------------------------+
  | {"a": 1, "b": [2, 3], "c": "[true, false]"}        |
  +----------------------------------------------------+
  mysql> SELECT JSON_REPLACE(@j, '$.a', 10, '$.c', '[true, false]');
  +-----------------------------------------------------+
  | JSON_REPLACE(@j, '$.a', 10, '$.c', '[true, false]') |
  +-----------------------------------------------------+
  | {"a": 10, "b": [2, 3]}                              |
  +-----------------------------------------------------+
  ```

- JSON_UNQUOTE(json_val)：取消JSON值引号并返回一个utf8mb4的字符串。参数为null返回null。

  - 转义的特殊字符

    | 转义字符     | 代替的字符                                                   |
    | :----------- | :----------------------------------------------------------- |
    | `\"`         | A double quote (`"`) character，双引号"                      |
    | `\b`         | A backspace character，退格符                                |
    | `\f`         | A formfeed character，换页符                                 |
    | `\n`         | A newline (linefeed) character，换行                         |
    | `\r`         | A carriage return character，回车                            |
    | `\t`         | A tab character，制表符                                      |
    | `\\`         | A backslash (`\`) character，斜杆\                           |
    | `\u*`XXXX`*` | UTF-8 bytes for Unicode value *`XXXX`*。Unicode值的UTF-8字符 |

  - ```sql
    mysql> SET @j = '"abc"';
    mysql> SELECT @j, JSON_UNQUOTE(@j);
    +-------+------------------+
    | @j    | JSON_UNQUOTE(@j) |
    +-------+------------------+
    | "abc" | abc              |
    +-------+------------------+
    mysql> SET @j = '[1, 2, 3]';
    mysql> SELECT @j, JSON_UNQUOTE(@j);
    +-----------+------------------+
    | @j        | JSON_UNQUOTE(@j) |
    +-----------+------------------+
    | [1, 2, 3] | [1, 2, 3]        |
    +-----------+------------------+
    ```

  - 使用 SET @@sql_mode = 'NO_BACKSLASH_ESCAPES' 设置没有斜杆转义

    ```sql
    mysql> SELECT @@sql_mode;
    +------------+
    | @@sql_mode |
    +------------+
    |            |
    +------------+
    
    mysql> SELECT JSON_UNQUOTE('"\\t\\u0032"');
    +------------------------------+
    | JSON_UNQUOTE('"\\t\\u0032"') |
    +------------------------------+
    |       2                           |
    +------------------------------+
    
    mysql> SET @@sql_mode = 'NO_BACKSLASH_ESCAPES';
    mysql> SELECT JSON_UNQUOTE('"\\t\\u0032"');
    +------------------------------+
    | JSON_UNQUOTE('"\\t\\u0032"') |
    +------------------------------+
    | \t\u0032                     |
    +------------------------------+
    
    mysql> SELECT JSON_UNQUOTE('"\t\u0032"');
    +----------------------------+
    | JSON_UNQUOTE('"\t\u0032"') |
    +----------------------------+
    |       2                         |
    +----------------------------+
    ```

# JSON值创建函数

1. JSON_ARRAY([val[, val] ...])：创建一个JSON数组包含参数的值。

   ```sql
   mysql> SELECT JSON_ARRAY(1, "abc", NULL, TRUE, CURTIME());
   +---------------------------------------------+
   | JSON_ARRAY(1, "abc", NULL, TRUE, CURTIME()) |
   +---------------------------------------------+
   | [1, "abc", null, true, "11:30:24.000000"]   |
   +---------------------------------------------+
   ```

2. JSON_OBJECT([key, val[, key, val] ...])：创建一个JSON对象包含参数的值。当一个key是null时报错。

   ```sql
   mysql> SELECT JSON_OBJECT('id', 87, 'name', 'carrot');
   +-----------------------------------------+
   | JSON_OBJECT('id', 87, 'name', 'carrot') |
   +-----------------------------------------+
   | {"id": 87, "name": "carrot"}            |
   +-----------------------------------------+
   ```

3. JSON_QUOTE(string)：为一个字符串添加双引号并转义内部引号和其他字符，然后返回一个utf8mb4的字符串。如果参数为null返回null。

   - 一般用于产生一个合法的JSON 字符串用于JSON Document里。

     ```sql
     mysql> SELECT JSON_QUOTE('null'), JSON_QUOTE('"null"');
     +--------------------+----------------------+
     | JSON_QUOTE('null') | JSON_QUOTE('"null"') |
     +--------------------+----------------------+
     | "null"             | "\"null\""           |
     +--------------------+----------------------+
     mysql> SELECT JSON_QUOTE('[1, 2, 3]');
     +-------------------------+
     | JSON_QUOTE('[1, 2, 3]') |
     +-------------------------+
     | "[1, 2, 3]"             |
     +-------------------------+
     ```

   - 也可以通过CAST()函数。

# JSON值搜索函数

1. JSON_CONTAINS(target, candidate[, path])：返回1或0表示是否有 candidate JSON 包含在 target JSON 里，如果包含 path 参数，则需要是 target 是在指定的 path。返回null如果参数是null，或者 path 不能标识 target的一部分。若target或candidate不是JSON，或path不是一个合法的路径表达式则报错。

   - 以下规则被定义为包含：

     -  一个candidate纯值包含在一个target纯值如果它们能够比较相等。前提是它们类型相同（JSON_TYPE() ），但是INTEGER and DECIMAL也可以进行比较。
     - 一个candidate数组的所有元素都在target数组里。
     - 一个candidate非数组包含在target数组里。
     - 一个candidate对象的所有key和value都在target对象里，且得是一一对应的。

   - ```sql
     mysql> SET @j = '{"a": 1, "b": 2, "c": {"d": 4}}';
     mysql> SET @j2 = '1';
     mysql> SELECT JSON_CONTAINS(@j, @j2, '$.a');
     +-------------------------------+
     | JSON_CONTAINS(@j, @j2, '$.a') |
     +-------------------------------+
     |                             1 |
     +-------------------------------+
     mysql> SELECT JSON_CONTAINS(@j, @j2, '$.b');
     +-------------------------------+
     | JSON_CONTAINS(@j, @j2, '$.b') |
     +-------------------------------+
     |                             0 |
     +-------------------------------+
     
     mysql> SET @j2 = '{"d": 4}';
     mysql> SELECT JSON_CONTAINS(@j, @j2, '$.a');
     +-------------------------------+
     | JSON_CONTAINS(@j, @j2, '$.a') |
     +-------------------------------+
     |                             0 |
     +-------------------------------+
     mysql> SELECT JSON_CONTAINS(@j, @j2, '$.c');
     +-------------------------------+
     | JSON_CONTAINS(@j, @j2, '$.c') |
     +-------------------------------+
     |                             1 |
     +-------------------------------+
     ```

   - 可以被用于多值索引。

2. JSON_CONTAINS_PATH(json_doc, one_or_all, path[, path] ...)：返回0或1标识一个JSON文档包含给定路径。返回null如果参数是null。

   - one表示至少一个路径存在文档；all表示所有路径存在文档。

   - ```sql
     mysql> SET @j = '{"a": 1, "b": 2, "c": {"d": 4}}';
     mysql> SELECT JSON_CONTAINS_PATH(@j, 'one', '$.a', '$.e');
     +---------------------------------------------+
     | JSON_CONTAINS_PATH(@j, 'one', '$.a', '$.e') |
     +---------------------------------------------+
     |                                           1 |
     +---------------------------------------------+
     mysql> SELECT JSON_CONTAINS_PATH(@j, 'all', '$.a', '$.e');
     +---------------------------------------------+
     | JSON_CONTAINS_PATH(@j, 'all', '$.a', '$.e') |
     +---------------------------------------------+
     |                                           0 |
     +---------------------------------------------+
     mysql> SELECT JSON_CONTAINS_PATH(@j, 'one', '$.c.d');
     +----------------------------------------+
     | JSON_CONTAINS_PATH(@j, 'one', '$.c.d') |
     +----------------------------------------+
     |                                      1 |
     +----------------------------------------+
     mysql> SELECT JSON_CONTAINS_PATH(@j, 'one', '$.a.d');
     +----------------------------------------+
     | JSON_CONTAINS_PATH(@j, 'one', '$.a.d') |
     +----------------------------------------+
     |                                      0 |
     +----------------------------------------+
     ```

3. JSON_EXTRACT(json_doc, path[, path] ...)：提取相应路径的值。

   - 多个值自动装配成数组。

     ```sql
     mysql> SELECT JSON_EXTRACT('[10, 20, [30, 40]]', '$[1]');
     +--------------------------------------------+
     | JSON_EXTRACT('[10, 20, [30, 40]]', '$[1]') |
     +--------------------------------------------+
     | 20                                         |
     +--------------------------------------------+
     mysql> SELECT JSON_EXTRACT('[10, 20, [30, 40]]', '$[1]', '$[0]');
     +----------------------------------------------------+
     | JSON_EXTRACT('[10, 20, [30, 40]]', '$[1]', '$[0]') |
     +----------------------------------------------------+
     | [20, 10]                                           |
     +----------------------------------------------------+
     mysql> SELECT JSON_EXTRACT('[10, 20, [30, 40]]', '$[2][*]');
     +-----------------------------------------------+
     | JSON_EXTRACT('[10, 20, [30, 40]]', '$[2][*]') |
     +-----------------------------------------------+
     | [30, 40]                                      |
     +-----------------------------------------------+
     ```

   - 也可以使用->操作符。

   - 可以[N]操作符：数组的第几个。

     ```sql
     mysql> SELECT JSON_SET('"x"', '$[0]', 'a');
     +------------------------------+
     | JSON_SET('"x"', '$[0]', 'a') |
     +------------------------------+
     | "a"                          |
     +------------------------------+
     1 row in set (0.00 sec)
     ```

   - [M to N]操作符：从第M到第N个。

   - \* or \*\*：

     - .\[\*\]：JSON 对象的所有成员。
     - \[\*\]：JSON 数组的所有成员。
     - prefix \*\* suffix：所有以prefix为前缀以suffix为后缀的成员。

   - ```sql
     mysql> SELECT JSON_EXTRACT('{"a": 1, "b": 2, "c": [3, 4, 5]}', '$.*');
     +---------------------------------------------------------+
     | JSON_EXTRACT('{"a": 1, "b": 2, "c": [3, 4, 5]}', '$.*') |
     +---------------------------------------------------------+
     | [1, 2, [3, 4, 5]]                                       |
     +---------------------------------------------------------+
     mysql> SELECT JSON_EXTRACT('{"a": 1, "b": 2, "c": [3, 4, 5]}', '$.c[*]');
     +------------------------------------------------------------+
     | JSON_EXTRACT('{"a": 1, "b": 2, "c": [3, 4, 5]}', '$.c[*]') |
     +------------------------------------------------------------+
     | [3, 4, 5]                                                  |
     +------------------------------------------------------------+
     ```

   - ```sql
     mysql> SELECT JSON_EXTRACT('{"a": {"b": 1}, "c": {"b": 2}}', '$**.b');
     +---------------------------------------------------------+
     | JSON_EXTRACT('{"a": {"b": 1}, "c": {"b": 2}}', '$**.b') |
     +---------------------------------------------------------+
     | [1, 2]                                                  |
     +---------------------------------------------------------+
     ```

   - ```sql
     mysql> SELECT JSON_EXTRACT('[1, 2, 3, 4, 5]', '$[1 to 3]');
     +----------------------------------------------+
     | JSON_EXTRACT('[1, 2, 3, 4, 5]', '$[1 to 3]') |
     +----------------------------------------------+
     | [2, 3, 4]                                    |
     +----------------------------------------------+
     1 row in set (0.00 sec)
     ```

   - 提取数组右边的元素：last语法

     ```sql
     mysql> SELECT JSON_EXTRACT('[1, 2, 3, 4, 5]', '$[last-3 to last-1]');
     +--------------------------------------------------------+
     | JSON_EXTRACT('[1, 2, 3, 4, 5]', '$[last-3 to last-1]') |
     +--------------------------------------------------------+
     | [2, 3, 4]                                              |
     +--------------------------------------------------------+
     1 row in set (0.01 sec)
     ```

   - 

4. column->path：JSON_EXTRACT()的另外一种形式，支持两个参数。

   ```sql
   mysql> SELECT c, JSON_EXTRACT(c, "$.id"), g
        > FROM jemp
        > WHERE JSON_EXTRACT(c, "$.id") > 1
        > ORDER BY JSON_EXTRACT(c, "$.name");
   +-------------------------------+-----------+------+
   | c                             | c->"$.id" | g    |
   +-------------------------------+-----------+------+
   | {"id": "3", "name": "Barney"} | "3"       |    3 |
   | {"id": "4", "name": "Betty"}  | "4"       |    4 |
   | {"id": "2", "name": "Wilma"}  | "2"       |    2 |
   +-------------------------------+-----------+------+
   3 rows in set (0.00 sec)
   
   mysql> SELECT c, c->"$.id", g
        > FROM jemp
        > WHERE c->"$.id" > 1
        > ORDER BY c->"$.name";
   +-------------------------------+-----------+------+
   | c                             | c->"$.id" | g    |
   +-------------------------------+-----------+------+
   | {"id": "3", "name": "Barney"} | "3"       |    3 |
   | {"id": "4", "name": "Betty"}  | "4"       |    4 |
   | {"id": "2", "name": "Wilma"}  | "2"       |    2 |
   +-------------------------------+-----------+------+
   3 rows in set (0.00 sec)
   ```

   - 还可以使用在其他地方：

     ```sql
     mysql> ALTER TABLE jemp ADD COLUMN n INT;
     Query OK, 0 rows affected (0.68 sec)
     Records: 0  Duplicates: 0  Warnings: 0
     
     mysql> UPDATE jemp SET n=1 WHERE c->"$.id" = "4";
     Query OK, 1 row affected (0.04 sec)
     Rows matched: 1  Changed: 1  Warnings: 0
     
     mysql> SELECT c, c->"$.id", g, n
          > FROM jemp
          > WHERE JSON_EXTRACT(c, "$.id") > 1
          > ORDER BY c->"$.name";
     +-------------------------------+-----------+------+------+
     | c                             | c->"$.id" | g    | n    |
     +-------------------------------+-----------+------+------+
     | {"id": "3", "name": "Barney"} | "3"       |    3 | NULL |
     | {"id": "4", "name": "Betty"}  | "4"       |    4 |    1 |
     | {"id": "2", "name": "Wilma"}  | "2"       |    2 | NULL |
     +-------------------------------+-----------+------+------+
     3 rows in set (0.00 sec)
     
     mysql> DELETE FROM jemp WHERE c->"$.id" = "4";
     Query OK, 1 row affected (0.04 sec)
     
     mysql> SELECT c, c->"$.id", g, n
          > FROM jemp
          > WHERE JSON_EXTRACT(c, "$.id") > 1
          > ORDER BY c->"$.name";
     +-------------------------------+-----------+------+------+
     | c                             | c->"$.id" | g    | n    |
     +-------------------------------+-----------+------+------+
     | {"id": "3", "name": "Barney"} | "3"       |    3 | NULL |
     | {"id": "2", "name": "Wilma"}  | "2"       |    2 | NULL |
     +-------------------------------+-----------+------+------+
     2 rows in set (0.00 sec)
     ```

   - 也可以使用在JSON数组：

     ```sql
     mysql> CREATE TABLE tj10 (a JSON, b INT);
     Query OK, 0 rows affected (0.26 sec)
     
     mysql> INSERT INTO tj10
          > VALUES ("[3,10,5,17,44]", 33), ("[3,10,5,17,[22,44,66]]", 0);
     Query OK, 1 row affected (0.04 sec)
     
     mysql> SELECT a->"$[4]" FROM tj10;
     +--------------+
     | a->"$[4]"    |
     +--------------+
     | 44           |
     | [22, 44, 66] |
     +--------------+
     2 rows in set (0.00 sec)
     
     mysql> SELECT * FROM tj10 WHERE a->"$[0]" = 3;
     +------------------------------+------+
     | a                            | b    |
     +------------------------------+------+
     | [3, 10, 5, 17, 44]           |   33 |
     | [3, 10, 5, 17, [22, 44, 66]] |    0 |
     +------------------------------+------+
     2 rows in set (0.00 sec)
     ```

   - 嵌套数组也支持：

     ```sql
     mysql> SELECT * FROM tj10 WHERE a->"$[4][1]" IS NOT NULL;
     +------------------------------+------+
     | a                            | b    |
     +------------------------------+------+
     | [3, 10, 5, 17, [22, 44, 66]] |    0 |
     +------------------------------+------+
     
     mysql> SELECT a->"$[4][1]" FROM tj10;
     +--------------+
     | a->"$[4][1]" |
     +--------------+
     | NULL         |
     | 44           |
     +--------------+
     2 rows in set (0.00 sec)
     ```

5. column->>path：比column->path多嵌套了JSON_UNQUOTE( )函数。

   - 下面3个等价：

     - JSON_UNQUOTE( JSON_EXTRACT(column, path) )
     - JSON_UNQUOTE(column -> path)
     - column->>path

   - ```sql
     mysql> SELECT * FROM jemp WHERE g > 2;
     +-------------------------------+------+
     | c                             | g    |
     +-------------------------------+------+
     | {"id": "3", "name": "Barney"} |    3 |
     | {"id": "4", "name": "Betty"}  |    4 |
     +-------------------------------+------+
     2 rows in set (0.01 sec)
     
     mysql> SELECT c->'$.name' AS name
         ->     FROM jemp WHERE g > 2;
     +----------+
     | name     |
     +----------+
     | "Barney" |
     | "Betty"  |
     +----------+
     2 rows in set (0.00 sec)
     
     mysql> SELECT JSON_UNQUOTE(c->'$.name') AS name
         ->     FROM jemp WHERE g > 2;
     +--------+
     | name   |
     +--------+
     | Barney |
     | Betty  |
     +--------+
     2 rows in set (0.00 sec)
     
     mysql> SELECT c->>'$.name' AS name
         ->     FROM jemp WHERE g > 2;
     +--------+
     | name   |
     +--------+
     | Barney |
     | Betty  |
     +--------+
     2 rows in set (0.00 sec)
     ```

   - 可以在数组使用：

     ```sql
     mysql> CREATE TABLE tj10 (a JSON, b INT);
     Query OK, 0 rows affected (0.26 sec)
     
     mysql> INSERT INTO tj10 VALUES
         ->     ('[3,10,5,"x",44]', 33),
         ->     ('[3,10,5,17,[22,"y",66]]', 0);
     Query OK, 2 rows affected (0.04 sec)
     Records: 2  Duplicates: 0  Warnings: 0
     
     mysql> SELECT a->"$[3]", a->"$[4][1]" FROM tj10;
     +-----------+--------------+
     | a->"$[3]" | a->"$[4][1]" |
     +-----------+--------------+
     | "x"       | NULL         |
     | 17        | "y"          |
     +-----------+--------------+
     2 rows in set (0.00 sec)
     
     mysql> SELECT a->>"$[3]", a->>"$[4][1]" FROM tj10;
     +------------+---------------+
     | a->>"$[3]" | a->>"$[4][1]" |
     +------------+---------------+
     | x          | NULL          |
     | 17         | y             |
     +------------+---------------+
     2 rows in set (0.00 sec)
     ```

6. JSON_KEYS(json_doc[, path])：返回顶层的JSON对象keys，以数组返回。可以指定path。若参数为null，或者不是对象，或者给定路径不能标识一个对象则返回null。

   - ```sql
     mysql> SELECT JSON_KEYS('{"a": 1, "b": {"c": 30}}');
     +---------------------------------------+
     | JSON_KEYS('{"a": 1, "b": {"c": 30}}') |
     +---------------------------------------+
     | ["a", "b"]                            |
     +---------------------------------------+
     mysql> SELECT JSON_KEYS('{"a": 1, "b": {"c": 30}}', '$.b');
     +----------------------------------------------+
     | JSON_KEYS('{"a": 1, "b": {"c": 30}}', '$.b') |
     +----------------------------------------------+
     | ["c"]                                        |
     +----------------------------------------------+
     ```

7. JSON_OVERLAPS(json_doc1, json_doc2)：如果两个JSON文档有相同的key-value对或者数组元素。如果是两个纯值则直接比较。

   - 可以被用在多值索引。

   - ```sql
     mysql> SELECT JSON_OVERLAPS("[1,3,5,7]", "[2,5,7]");
     +---------------------------------------+
     | JSON_OVERLAPS("[1,3,5,7]", "[2,5,7]") |
     +---------------------------------------+
     |                                     1 |
     +---------------------------------------+
     1 row in set (0.00 sec)
     
     mysql> SELECT JSON_OVERLAPS("[1,3,5,7]", "[2,6,7]");
     +---------------------------------------+
     | JSON_OVERLAPS("[1,3,5,7]", "[2,6,7]") |
     +---------------------------------------+
     |                                     1 |
     +---------------------------------------+
     1 row in set (0.00 sec)
     
     mysql> SELECT JSON_OVERLAPS("[1,3,5,7]", "[2,6,8]");
     +---------------------------------------+
     | JSON_OVERLAPS("[1,3,5,7]", "[2,6,8]") |
     +---------------------------------------+
     |                                     0 |
     +---------------------------------------+
     1 row in set (0.00 sec)
     ```

   - ```sql
     mysql> SELECT JSON_OVERLAPS('[[1,2],[3,4],5]', '[1,[2,3],[4,5]]');
     +-----------------------------------------------------+
     | JSON_OVERLAPS('[[1,2],[3,4],5]', '[1,[2,3],[4,5]]') |
     +-----------------------------------------------------+
     |                                                   0 |
     +-----------------------------------------------------+
     1 row in set (0.00 sec)
     ```

   - ```sql
     mysql> SELECT JSON_OVERLAPS('{"a":1,"b":10,"d":10}', '{"c":1,"e":10,"f":1,"d":10}');
     +-----------------------------------------------------------------------+
     | JSON_OVERLAPS('{"a":1,"b":10,"d":10}', '{"c":1,"e":10,"f":1,"d":10}') |
     +-----------------------------------------------------------------------+
     |                                                                     1 |
     +-----------------------------------------------------------------------+
     1 row in set (0.00 sec)
     
     mysql> SELECT JSON_OVERLAPS('{"a":1,"b":10,"d":10}', '{"a":5,"e":10,"f":1,"d":20}');
     +-----------------------------------------------------------------------+
     | JSON_OVERLAPS('{"a":1,"b":10,"d":10}', '{"a":5,"e":10,"f":1,"d":20}') |
     +-----------------------------------------------------------------------+
     |                                                                     0 |
     +-----------------------------------------------------------------------+
     1 row in set (0.00 sec)
     ```

   - ```sql
     mysql> SELECT JSON_OVERLAPS('5', '5');
     +-------------------------+
     | JSON_OVERLAPS('5', '5') |
     +-------------------------+
     |                       1 |
     +-------------------------+
     1 row in set (0.00 sec)
     
     mysql> SELECT JSON_OVERLAPS('5', '6');
     +-------------------------+
     | JSON_OVERLAPS('5', '6') |
     +-------------------------+
     |                       0 |
     +-------------------------+
     1 row in set (0.00 sec)
     ```

   - 当比较一个纯值与一个数组，会尝试用纯值去匹配数组里的元素。

     ```sql
     mysql> SELECT JSON_OVERLAPS('[4,5,6,7]', '6');
     +---------------------------------+
     | JSON_OVERLAPS('[4,5,6,7]', '6') |
     +---------------------------------+
     |                               1 |
     +---------------------------------+
     1 row in set (0.00 sec)
     ```

   - 不自动进行类型转换

     ```sql
     mysql> SELECT JSON_OVERLAPS('[4,5,"6",7]', '6');
     +-----------------------------------+
     | JSON_OVERLAPS('[4,5,"6",7]', '6') |
     +-----------------------------------+
     |                                 0 |
     +-----------------------------------+
     1 row in set (0.00 sec)
     
     mysql> SELECT JSON_OVERLAPS('[4,5,6,7]', '"6"');
     +-----------------------------------+
     | JSON_OVERLAPS('[4,5,6,7]', '"6"') |
     +-----------------------------------+
     |                                 0 |
     +-----------------------------------+
     1 row in set (0.00 sec)
     ```

8. JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])：返回给定字符串在JSON文档里的路径。返回null，如果一个参数为null，path不存在，search_str不存在。

   - one：匹配第一个然后直接返回。all：搜索全部匹配的，多个值会自动装配成数组。

   - search_str：可以使用%匹配任何数量字符，_匹配一个字符。

   - escape_char：对于%和_的转义字符。默认是\，如果该值缺失或是null。必须是一个常量的空或1个字符。

   - ```sql
     mysql> SET @j = '["abc", [{"k": "10"}, "def"], {"x":"abc"}, {"y":"bcd"}]';
     
     mysql> SELECT JSON_SEARCH(@j, 'one', 'abc');
     +-------------------------------+
     | JSON_SEARCH(@j, 'one', 'abc') |
     +-------------------------------+
     | "$[0]"                        |
     +-------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', 'abc');
     +-------------------------------+
     | JSON_SEARCH(@j, 'all', 'abc') |
     +-------------------------------+
     | ["$[0]", "$[2].x"]            |
     +-------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', 'ghi');
     +-------------------------------+
     | JSON_SEARCH(@j, 'all', 'ghi') |
     +-------------------------------+
     | NULL                          |
     +-------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', '10');
     +------------------------------+
     | JSON_SEARCH(@j, 'all', '10') |
     +------------------------------+
     | "$[1][0].k"                  |
     +------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', '10', NULL, '$');
     +-----------------------------------------+
     | JSON_SEARCH(@j, 'all', '10', NULL, '$') |
     +-----------------------------------------+
     | "$[1][0].k"                             |
     +-----------------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', '10', NULL, '$[*]');
     +--------------------------------------------+
     | JSON_SEARCH(@j, 'all', '10', NULL, '$[*]') |
     +--------------------------------------------+
     | "$[1][0].k"                                |
     +--------------------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', '10', NULL, '$**.k');
     +---------------------------------------------+
     | JSON_SEARCH(@j, 'all', '10', NULL, '$**.k') |
     +---------------------------------------------+
     | "$[1][0].k"                                 |
     +---------------------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', '10', NULL, '$[*][0].k');
     +-------------------------------------------------+
     | JSON_SEARCH(@j, 'all', '10', NULL, '$[*][0].k') |
     +-------------------------------------------------+
     | "$[1][0].k"                                     |
     +-------------------------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', '10', NULL, '$[1]');
     +--------------------------------------------+
     | JSON_SEARCH(@j, 'all', '10', NULL, '$[1]') |
     +--------------------------------------------+
     | "$[1][0].k"                                |
     +--------------------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', '10', NULL, '$[1][0]');
     +-----------------------------------------------+
     | JSON_SEARCH(@j, 'all', '10', NULL, '$[1][0]') |
     +-----------------------------------------------+
     | "$[1][0].k"                                   |
     +-----------------------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', 'abc', NULL, '$[2]');
     +---------------------------------------------+
     | JSON_SEARCH(@j, 'all', 'abc', NULL, '$[2]') |
     +---------------------------------------------+
     | "$[2].x"                                    |
     +---------------------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', '%a%');
     +-------------------------------+
     | JSON_SEARCH(@j, 'all', '%a%') |
     +-------------------------------+
     | ["$[0]", "$[2].x"]            |
     +-------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', '%b%');
     +-------------------------------+
     | JSON_SEARCH(@j, 'all', '%b%') |
     +-------------------------------+
     | ["$[0]", "$[2].x", "$[3].y"]  |
     +-------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', '%b%', NULL, '$[0]');
     +---------------------------------------------+
     | JSON_SEARCH(@j, 'all', '%b%', NULL, '$[0]') |
     +---------------------------------------------+
     | "$[0]"                                      |
     +---------------------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', '%b%', NULL, '$[2]');
     +---------------------------------------------+
     | JSON_SEARCH(@j, 'all', '%b%', NULL, '$[2]') |
     +---------------------------------------------+
     | "$[2].x"                                    |
     +---------------------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', '%b%', NULL, '$[1]');
     +---------------------------------------------+
     | JSON_SEARCH(@j, 'all', '%b%', NULL, '$[1]') |
     +---------------------------------------------+
     | NULL                                        |
     +---------------------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', '%b%', '', '$[1]');
     +-------------------------------------------+
     | JSON_SEARCH(@j, 'all', '%b%', '', '$[1]') |
     +-------------------------------------------+
     | NULL                                      |
     +-------------------------------------------+
     
     mysql> SELECT JSON_SEARCH(@j, 'all', '%b%', '', '$[3]');
     +-------------------------------------------+
     | JSON_SEARCH(@j, 'all', '%b%', '', '$[3]') |
     +-------------------------------------------+
     | "$[3].y"                                  |
     +-------------------------------------------+
     ```

9. JSON_VALUE(json_doc, path)：提取指定路径的文档。

   - 完整语法：

     ```sql
     JSON_VALUE(json_doc, path [RETURNING type] [on_empty] [on_error])
     
     on_empty:
         {NULL | ERROR | DEFAULT value} ON EMPTY
     
     on_error:
         {NULL | ERROR | DEFAULT value} ON ERROR
     ```

   - 类型支持：

     ```sql
     FLOAT
     
     DOUBLE
     
     DECIMAL
     
     SIGNED
     
     UNSIGNED
     
     DATE
     
     TIME
     
     DATETIME
     
     YEAR (MySQL 8.0.22 and later)
     
     YEAR values of one or two digits are not supported.
     
     CHAR
     
     JSON
     ```

   - 默认返回类型是 VARCHAR(512)，默认字符集是 utf8mb4，是类型敏感的。

   - ```sql
     mysql> SELECT JSON_VALUE('{"fname": "Joe", "lname": "Palmer"}', '$.fname');
     +--------------------------------------------------------------+
     | JSON_VALUE('{"fname": "Joe", "lname": "Palmer"}', '$.fname') |
     +--------------------------------------------------------------+
     | Joe                                                          |
     +--------------------------------------------------------------+
     
     mysql> SELECT JSON_VALUE('{"item": "shoes", "price": "49.95"}', '$.price'
         -> RETURNING DECIMAL(4,2)) AS price;
     +-------+
     | price |
     +-------+
     | 49.95 |
     +-------+
     ```

   - 下面两个语句等价：

     ```sql
     SELECT JSON_VALUE(json_doc, path RETURNING type)
     
     SELECT CAST(
         JSON_UNQUOTE( JSON_EXTRACT(json_doc, path) )
         AS type
     );
     ```

   - 可以用来简化创建索引：

     ```sql
     CREATE TABLE t1(
         j JSON,
         INDEX i1 ( (JSON_VALUE(j, '$.id' RETURNING UNSIGNED)) )
     );
     ```

     ```sql
     -- 等价操作，看起来不太清晰
     -- 产生一个generated列，然后再对j索引
     CREATE TABLE t2 (
         j JSON,
         g INT GENERATED ALWAYS AS (j->"$.id"),
         INDEX i1 (j)
     );
     ```

10. value MEMBER OF(json_array)：返回1如果value存在 json_array里，否则0。value必须是一个纯或JSON文档。

    - 可以被多值索引支持。

    - ```sql
      mysql> SELECT 17 MEMBER OF('[23, "abc", 17, "ab", 10]');
      +-------------------------------------------+
      | 17 MEMBER OF('[23, "abc", 17, "ab", 10]') |
      +-------------------------------------------+
      |                                         1 |
      +-------------------------------------------+
      1 row in set (0.00 sec)
      
      mysql> SELECT 'ab' MEMBER OF('[23, "abc", 17, "ab", 10]');
      +---------------------------------------------+
      | 'ab' MEMBER OF('[23, "abc", 17, "ab", 10]') |
      +---------------------------------------------+
      |                                           1 |
      +---------------------------------------------+
      1 row in set (0.00 sec)
      ```

    - ```sql
      mysql> SELECT 7 MEMBER OF('[23, "abc", 17, "ab", 10]');
      +------------------------------------------+
      | 7 MEMBER OF('[23, "abc", 17, "ab", 10]') |
      +------------------------------------------+
      |                                        0 |
      +------------------------------------------+
      1 row in set (0.00 sec)
      
      mysql> SELECT 'a' MEMBER OF('[23, "abc", 17, "ab", 10]');
      +--------------------------------------------+
      | 'a' MEMBER OF('[23, "abc", 17, "ab", 10]') |
      +--------------------------------------------+
      |                                          0 |
      +--------------------------------------------+
      1 row in set (0.00 sec)
      ```

    - ```sql
      mysql> SELECT
          -> 17 MEMBER OF('[23, "abc", "17", "ab", 10]'),
          -> "17" MEMBER OF('[23, "abc", 17, "ab", 10]')\G
      *************************** 1. row ***************************
      17 MEMBER OF('[23, "abc", "17", "ab", 10]'): 0
      "17" MEMBER OF('[23, "abc", 17, "ab", 10]'): 0
      1 row in set (0.00 sec)
      ```

    - 当搜索一个数组时，必须使用CAST(... AS JSON)实其转换成JSON数组。

      ```sql
      mysql> SELECT CAST('[4,5]' AS JSON) MEMBER OF('[[3,4],[4,5]]');
      +--------------------------------------------------+
      | CAST('[4,5]' AS JSON) MEMBER OF('[[3,4],[4,5]]') |
      +--------------------------------------------------+
      |                                                1 |
      +--------------------------------------------------+
      1 row in set (0.00 sec)
      ```

    - 也可以使用JSON_ARRAY()

      ```sql
      mysql> SELECT JSON_ARRAY(4,5) MEMBER OF('[[3,4],[4,5]]');
      +--------------------------------------------+
      | JSON_ARRAY(4,5) MEMBER OF('[[3,4],[4,5]]') |
      +--------------------------------------------+
      |                                          1 |
      +--------------------------------------------+
      1 row in set (0.00 sec)
      ```

    - ```sql
      mysql> SET @a = CAST('{"a":1}' AS JSON);
      Query OK, 0 rows affected (0.00 sec)
      
      mysql> SET @b = JSON_OBJECT("b", 2);
      Query OK, 0 rows affected (0.00 sec)
      
      mysql> SET @c = JSON_ARRAY(17, @b, "abc", @a, 23);
      Query OK, 0 rows affected (0.00 sec)
      
      mysql> SELECT @a MEMBER OF(@c), @b MEMBER OF(@c);
      +------------------+------------------+
      | @a MEMBER OF(@c) | @b MEMBER OF(@c) |
      +------------------+------------------+
      |                1 |                1 |
      +------------------+------------------+
      1 row in set (0.00 sec)
      ```

# 生成列索引

- 用于给JSON数据建索引。

  ```sql
  mysql> CREATE TABLE jemp (
      ->     c JSON,
      ->     g INT GENERATED ALWAYS AS (c->"$.id"),
      ->     INDEX i (g)
      -> );
  Query OK, 0 rows affected (0.28 sec)
  
  mysql> INSERT INTO jemp (c) VALUES
       >   ('{"id": "1", "name": "Fred"}'), ('{"id": "2", "name": "Wilma"}'),
       >   ('{"id": "3", "name": "Barney"}'), ('{"id": "4", "name": "Betty"}');
  Query OK, 4 rows affected (0.04 sec)
  Records: 4  Duplicates: 0  Warnings: 0
  
  mysql> SELECT c->>"$.name" AS name
       >     FROM jemp WHERE g > 2;
  +--------+
  | name   |
  +--------+
  | Barney |
  | Betty  |
  +--------+
  2 rows in set (0.00 sec)
  
  mysql> EXPLAIN SELECT c->>"$.name" AS name
       >    FROM jemp WHERE g > 2\G
  *************************** 1. row ***************************
             id: 1
    select_type: SIMPLE
          table: jemp
     partitions: NULL
           type: range
  possible_keys: i
            key: i
        key_len: 5
            ref: NULL
           rows: 2
       filtered: 100.00
          Extra: Using where
  1 row in set, 1 warning (0.00 sec)
  ```

- 也可以使用 JSON_VALUE()去建。

# JSON与非JSON转换

| other type                                       | CAST(other type AS JSON)                                     | CAST(JSON AS other type)                                     |
| :----------------------------------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| JSON                                             | No change                                                    | No change                                                    |
| utf8 character type (`utf8mb4`, `utf8`, `ascii`) | The string is parsed into a JSON value.                      | The JSON value is serialized into a `utf8mb4` string.        |
| Other character types                            | Other character encodings are implicitly converted to `utf8mb4` and treated as described for utf8 character type. | The JSON value is serialized into a `utf8mb4` string, then cast to the other character encoding. The result may not be meaningful. |
| `NULL`                                           | Results in a `NULL` value of type JSON.                      | Not applicable.                                              |
| Geometry types                                   | The geometry value is converted into a JSON document by calling [`ST_AsGeoJSON()`](https://dev.mysql.com/doc/refman/8.0/en/spatial-geojson-functions.html#function_st-asgeojson). | Illegal operation. Workaround: Pass the result of [`CAST(*`json_val`* AS CHAR)`](https://dev.mysql.com/doc/refman/8.0/en/cast-functions.html#function_cast) to [`ST_GeomFromGeoJSON()`](https://dev.mysql.com/doc/refman/8.0/en/spatial-geojson-functions.html#function_st-geomfromgeojson). |
| All other types                                  | Results in a JSON document consisting of a single scalar value. | Succeeds if the JSON document consists of a single scalar value of the target type and that scalar value can be cast to the target type. Otherwise, returns `NULL` and produces a warning. |

# JSON聚合函数

1. JSON_ARRAYAGG(col_or_expr) [over_clause]：对JSON数组进行聚合。如果有over_clause 将执行一个窗口函数。

   ```sql
   mysql> SELECT o_id, attribute, value FROM t3;
   +------+-----------+-------+
   | o_id | attribute | value |
   +------+-----------+-------+
   |    2 | color     | red   |
   |    2 | fabric    | silk  |
   |    3 | color     | green |
   |    3 | shape     | square|
   +------+-----------+-------+
   4 rows in set (0.00 sec)
   
   mysql> SELECT o_id, JSON_ARRAYAGG(attribute) AS attributes
        > FROM t3 GROUP BY o_id;
   +------+---------------------+
   | o_id | attributes          |
   +------+---------------------+
   |    2 | ["color", "fabric"] |
   |    3 | ["color", "shape"]  |
   +------+---------------------+
   2 rows in set (0.00 sec)
   ```

2. JSON_OBJECTAGG(key, value) [over_clause]：返回一个key value对。如果有over_clause 将执行一个窗口函数。

   ```sql
   mysql> SELECT o_id, attribute, value FROM t3;
   +------+-----------+-------+
   | o_id | attribute | value |
   +------+-----------+-------+
   |    2 | color     | red   |
   |    2 | fabric    | silk  |
   |    3 | color     | green |
   |    3 | shape     | square|
   +------+-----------+-------+
   4 rows in set (0.00 sec)
   
   mysql> SELECT o_id, JSON_OBJECTAGG(attribute, value)
        > FROM t3 GROUP BY o_id;
   +------+---------------------------------------+
   | o_id | JSON_OBJECTAGG(attribute, value)      |
   +------+---------------------------------------+
   |    2 | {"color": "red", "fabric": "silk"}    |
   |    3 | {"color": "green", "shape": "square"} |
   +------+---------------------------------------+
   2 rows in set (0.00 sec)
   ```

   - 重复key只保留最后一个。

     ```sql
     mysql> CREATE TABLE t(c VARCHAR(10), i INT);
     Query OK, 0 rows affected (0.33 sec)
     
     mysql> INSERT INTO t VALUES ('key', 3), ('key', 4), ('key', 5);
     Query OK, 3 rows affected (0.10 sec)
     Records: 3  Duplicates: 0  Warnings: 0
     
     mysql> SELECT c, i FROM t;
     +------+------+
     | c    | i    |
     +------+------+
     | key  |    3 |
     | key  |    4 |
     | key  |    5 |
     +------+------+
     3 rows in set (0.00 sec)
     
     mysql> SELECT JSON_OBJECTAGG(c, i) FROM t;
     +----------------------+
     | JSON_OBJECTAGG(c, i) |
     +----------------------+
     | {"key": 5}           |
     +----------------------+
     1 row in set (0.00 sec)
     
     mysql> DELETE FROM t;
     Query OK, 3 rows affected (0.08 sec)
     
     mysql> INSERT INTO t VALUES ('key', 3), ('key', 5), ('key', 4);
     Query OK, 3 rows affected (0.06 sec)
     Records: 3  Duplicates: 0  Warnings: 0
     
     mysql> SELECT c, i FROM t;
     +------+------+
     | c    | i    |
     +------+------+
     | key  |    3 |
     | key  |    5 |
     | key  |    4 |
     +------+------+
     3 rows in set (0.00 sec)
     
     mysql> SELECT JSON_OBJECTAGG(c, i) FROM t;
     +----------------------+
     | JSON_OBJECTAGG(c, i) |
     +----------------------+
     | {"key": 4}           |
     +----------------------+
     1 row in set (0.00 sec)
     ```

   - 使用over()

     ```sql
     mysql> SELECT JSON_OBJECTAGG(c, i)
            OVER () AS json_object FROM t;
     +-------------+
     | json_object |
     +-------------+
     | {"key": 4}  |
     | {"key": 4}  |
     | {"key": 4}  |
     +-------------+
     ```

   - 使用over(order by i)

     ```sql
     mysql> SELECT JSON_OBJECTAGG(c, i)
            OVER (ORDER BY i) AS json_object FROM t;
     +-------------+
     | json_object |
     +-------------+
     | {"key": 3}  |
     | {"key": 4}  |
     | {"key": 5}  |
     +-------------+
     mysql> SELECT JSON_OBJECTAGG(c, i)
            OVER (ORDER BY i DESC) AS json_object FROM t;
     +-------------+
     | json_object |
     +-------------+
     | {"key": 5}  |
     | {"key": 4}  |
     | {"key": 3}  |
     +-------------+
     ```

   - 返回一个最大和最小值：

     ```sql
     mysql> SELECT JSON_OBJECTAGG(c, i)
            OVER (ORDER BY i) AS json_object FROM t LIMIT 1;
     +-------------+
     | json_object |
     +-------------+
     | {"key": 3}  |
     +-------------+
     mysql> SELECT JSON_OBJECTAGG(c, i)
            OVER (ORDER BY i DESC) AS json_object FROM t LIMIT 1;
     +-------------+
     | json_object |
     +-------------+
     | {"key": 5}  |
     +-------------+
     ```

   -  With ORDER BY and an explicit frame of the entire partition:

     ```sql
     mysql> SELECT JSON_OBJECTAGG(c, i)
            OVER (ORDER BY i
                 ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING)
             AS json_object
            FROM t;
     +-------------+
     | json_object |
     +-------------+
     | {"key": 5}  |
     | {"key": 5}  |
     | {"key": 5}  |
     +-------------+
     ```

# 窗口函数

1. 很简单，每个记录都有聚合值

   ```sql
   mysql> SELECT
            year, country, product, profit,
            SUM(profit) OVER() AS total_profit,
            SUM(profit) OVER(PARTITION BY country) AS country_profit
          FROM sales
          ORDER BY country, year, product, profit;
   +------+---------+------------+--------+--------------+----------------+
   | year | country | product    | profit | total_profit | country_profit |
   +------+---------+------------+--------+--------------+----------------+
   | 2000 | Finland | Computer   |   1500 |         7535 |           1610 |
   | 2000 | Finland | Phone      |    100 |         7535 |           1610 |
   | 2001 | Finland | Phone      |     10 |         7535 |           1610 |
   | 2000 | India   | Calculator |     75 |         7535 |           1350 |
   | 2000 | India   | Calculator |     75 |         7535 |           1350 |
   | 2000 | India   | Computer   |   1200 |         7535 |           1350 |
   | 2000 | USA     | Calculator |     75 |         7535 |           4575 |
   | 2000 | USA     | Computer   |   1500 |         7535 |           4575 |
   | 2001 | USA     | Calculator |     50 |         7535 |           4575 |
   | 2001 | USA     | Computer   |   1200 |         7535 |           4575 |
   | 2001 | USA     | Computer   |   1500 |         7535 |           4575 |
   | 2001 | USA     | TV         |    100 |         7535 |           4575 |
   | 2001 | USA     | TV         |    150 |         7535 |           4575 |
   +------+---------+------------+--------+--------------+----------------+
   ```

2. 允许在over列的函数：

   ```sql
   AVG()
   BIT_AND()
   BIT_OR()
   BIT_XOR()
   COUNT()
   JSON_ARRAYAGG()
   JSON_OBJECTAGG()
   MAX()
   MIN()
   STDDEV_POP(), STDDEV(), STD()
   STDDEV_SAMP()
   SUM()
   VAR_POP(), VARIANCE()
   VAR_SAMP()
   ```

3. 下面的也支持：

   ```sql
   CUME_DIST()
   DENSE_RANK()
   FIRST_VALUE()
   LAG()
   LAST_VALUE()
   LEAD()
   NTH_VALUE()
   NTILE()
   PERCENT_RANK()
   RANK()
   ROW_NUMBER()
   ```

4. ```sql
   mysql> SELECT
            year, country, product, profit,
            ROW_NUMBER() OVER(PARTITION BY country) AS row_num1,
            ROW_NUMBER() OVER(PARTITION BY country ORDER BY year, product) AS row_num2
          FROM sales;
   +------+---------+------------+--------+----------+----------+
   | year | country | product    | profit | row_num1 | row_num2 |
   +------+---------+------------+--------+----------+----------+
   | 2000 | Finland | Computer   |   1500 |        2 |        1 |
   | 2000 | Finland | Phone      |    100 |        1 |        2 |
   | 2001 | Finland | Phone      |     10 |        3 |        3 |
   | 2000 | India   | Calculator |     75 |        2 |        1 |
   | 2000 | India   | Calculator |     75 |        3 |        2 |
   | 2000 | India   | Computer   |   1200 |        1 |        3 |
   | 2000 | USA     | Calculator |     75 |        5 |        1 |
   | 2000 | USA     | Computer   |   1500 |        4 |        2 |
   | 2001 | USA     | Calculator |     50 |        2 |        3 |
   | 2001 | USA     | Computer   |   1500 |        3 |        4 |
   | 2001 | USA     | Computer   |   1200 |        7 |        5 |
   | 2001 | USA     | TV         |    150 |        1 |        6 |
   | 2001 | USA     | TV         |    100 |        6 |        7 |
   +------+---------+------------+--------+----------+----------+
   ```

5. over的语法：

   - ```sql
     over_clause:
         {OVER (window_spec) | OVER window_name}
     ```

   - 第一种直接指定，第二种通过命名窗口。

   - ```sql
     window_spec:
         [window_name] [partition_clause] [order_clause] [frame_clause]
     ```

   - 如果over()是空，则返回所有行，计算所有行。否则将根据括号里的规则进行计算，划分，排序。

     - window_name：给定一个命名窗口。

     - partition_clause：指定如何去划分数据行。

       - ```sql
         partition_clause:
             PARTITION BY expr [, expr] ..
         ```

     - order_clause：如何去排序partition。可以指定ASC` or `DESC，默认ASC。当ASC时null排第一，DESC时排最后。

       - ```sql
         order_clause:
             ORDER BY expr [ASC|DESC] [, expr [ASC|DESC]] ...
         ```

     - frame_clause：一个partition的子集。

6. 命名窗口

   - 给一个窗口一个名字。

   - ```sql
     -- 两个查询等价
     SELECT
       val,
       ROW_NUMBER() OVER (ORDER BY val) AS 'row_number',
       RANK()       OVER (ORDER BY val) AS 'rank',
       DENSE_RANK() OVER (ORDER BY val) AS 'dense_rank'
     FROM numbers;
     
     SELECT
       val,
       ROW_NUMBER() OVER w AS 'row_number',
       RANK()       OVER w AS 'rank',
       DENSE_RANK() OVER w AS 'dense_rank'
     FROM numbers
     WINDOW w AS (ORDER BY val);
     ```

   - ```sql
     SELECT
       DISTINCT year, country,
       FIRST_VALUE(year) OVER (w ORDER BY year ASC) AS first,
       FIRST_VALUE(year) OVER (w ORDER BY year DESC) AS last
     FROM sales
     WINDOW w AS (PARTITION BY country);
     ```

7. Frame：指定子集（https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html)

   - ```sql
     mysql> SELECT
              time, subject, val,
              SUM(val) OVER (PARTITION BY subject ORDER BY time
                             ROWS UNBOUNDED PRECEDING)
                AS running_total,
              AVG(val) OVER (PARTITION BY subject ORDER BY time
                             ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING)
                AS running_average
            FROM observations;
     +----------+---------+------+---------------+-----------------+
     | time     | subject | val  | running_total | running_average |
     +----------+---------+------+---------------+-----------------+
     | 07:00:00 | st113   |   10 |            10 |          9.5000 |
     | 07:15:00 | st113   |    9 |            19 |         14.6667 |
     | 07:30:00 | st113   |   25 |            44 |         18.0000 |
     | 07:45:00 | st113   |   20 |            64 |         22.5000 |
     | 07:00:00 | xh458   |    0 |             0 |          5.0000 |
     | 07:15:00 | xh458   |   10 |            10 |          5.0000 |
     | 07:30:00 | xh458   |    5 |            15 |         15.0000 |
     | 07:45:00 | xh458   |   30 |            45 |         20.0000 |
     | 08:00:00 | xh458   |   25 |            70 |         27.5000 |
     +----------+---------+------+---------------+-----------------+
     ```

   - 可以使用下面函数操作行:

     ```sql
     FIRST_VALUE()
     LAST_VALUE()
     NTH_VALUE()
     ```

   - 下面函数会作用整个分区：

     ```sql
     CUME_DIST()
     DENSE_RANK()
     LAG()
     LEAD()
     NTILE()
     PERCENT_RANK()
     RANK()
     ROW_NUMBER()
     ```

   - ```sql
     frame_clause:
         frame_units frame_extent
     
     frame_units:
         {ROWS | RANGE}
     ```

     - rows：开始位置和结束位置，Offset是当前行开始。
     - range：The frame is defined by rows within a value range. Offsets are differences in row values from the current row value.

   - ```sql
     frame_extent:
         {frame_start | frame_between}
     
     frame_between:
         BETWEEN frame_start AND frame_end
     
     frame_start, frame_end: {
         CURRENT ROW
       | UNBOUNDED PRECEDING
       | UNBOUNDED FOLLOWING
       | expr PRECEDING
       | expr FOLLOWING
     }
     ```

     - frame_start and frame_end 含义：
       - CURRENT ROW：对于ROWS，当前行；对于RANGE，当前行的对等点。
       - UNBOUNDED PRECEDING：界限是partition的第一行；
       - UNBOUNDED FOLLOWING：界限是partition的最后一行；
       - expr PRECEDING：对于ROWS，界限是expr相对于当前行；对于RANGE，界限是当前行的值减去expr的行。

