# MySQL数据库命名规范及约定

**一、【操作规范】**

1. 如无备注，则表中的第一个id字段一定是主键且为自动增长；
2. 如无备注，则数值类型的字段请使用UNSIGNED属性；
3. 如无备注，排序字段order_id在程序中默认使用降序排列；
4. 如无备注，所有字段都设置NOT NULL，并设置默认值；
5. 如无备注，所有的布尔值字段，如is_hot、is_deleted，都必须设置一个默认值，并设为0；
6. 所有的数字类型字段，都必须设置一个默认值，并设为0；
7. 针对varchar类型字段的程序处理，请验证用户输入，不要超出其预设的长度；
8. 建表时将数据字典中的字段中文名和属性备注写入数据表的备注中(“PK、自动增长”不用写)；
9. 如无说明，建表时一律采用innodb引擎；

**二、【常用表名约定】**

0. 说明：表前缀用项目名称首字母缩写；所以表名都小写，单词之间用下划线分开，单词都用单数形式
1. user – 用户
2. category – 分类
3. goods – 商品、产品等一切可交易网站的物品都用此命名
4. good_gallery – 物品的相册
5. good_cate – 物品的分类，除了单独作为表名，其他地方分类单词一律用缩写cate
4. attr – 属性
5. article – 文章、新闻、帮助中心等以文章形式出现的，一般都用此命名
6. cart – 购物车
7. feedback – 用户反馈
8. order – 订单
9. site_nav – 包括页头和页尾导航
10. site_config – 系统配置表
11. admin – 后台用户 【RBAC标准表】
12. role – 后台用户角色【RBAC标准表】
13. access – 后台操作权限，相当于action【RBAC标准表】
14. role_admin – 后台用户对应的角色【RBAC标准表】
15. access_role – 后台角色对应的权限【RBAC标准表】
16. 待续

**三、【常用列名约定】**

1. 表名_id – 通常用作外键命名
2. cid – 特殊的编号，带有元数据，方便关联查询，你可以把它理解成类别(层次)编号。举个例子，产品在分类时，往往需要将其归类到子分类下，相应的字段中也一般只记录子分类的id，这时若需要知道该产品属于哪个主分类，就需要通过子分类信息再查询到主分类信息，这是比较麻烦的，cid字段就是要解决这个问题。一般的站点几十个分类肯定是够用了，所以这里假设某一主分类的cid为11，则子分类的cid从1101开始编号，处理时只需截取前两位数值便可知道该产品属于哪一个主分类了。
3. add_time – 添加时间、上架时间等
4. last_time – 最后操作时间，如登录、修改记录
5. expire_time – 过期时间
6. name – 商品名称、商家名称等，不要跟title混用，title只用于文章标题、职称等
7. price – 价格
8. thumb – 只要是列表页面中的窗口图，一律用此命名
9. image_src – 相册中的图片地址一律用此命名，不要出现各种img,image,img_url,thumb_url等
10. head_thumb – 用户头像， 虽然有点长，一定要遵守。不要出现上述情况
11. image_alt – 相册中图片的alt属性
12. desc – 描述、简介，比如goods_desc，不要出现goods_txt这种
13. details – 详情、文章内容等
14. order_id – 排序
15. telephone – 座机号码
16. mobile – 手机号码
17. phone – 当不区分手机和座机时，请用phone命名
18. address – 地址，单独出现不要用addr缩写，组合出现时需用缩写，比如mac地址，mac_addr
19. zipcode – 邮编
20. region – 地区，大的区域，比如记录杭州市、温州市等
21. area – 区域，小的，比如上城区，江干区等
22. avg_cost – 人均消费
23. 待续

**四、【数据表字段设计范例】**

**分类表**

| **字段名** | **列名** | **类型**    | **属性备注** | **说明**                                                   |
| ---------- | -------- | ----------- | ------------ | ---------------------------------------------------------- |
| 流水号     | id       | int(10)     | PK、自动增长 |                                                            |
| 特殊编号   | cid      | varchar(4)  |              | 第一个主分类为11、第一个子分类为1101，类推，仅支持二级分类 |
| 名称       | name     | varchar(10) |              | 页面中需注明输入不超过10个字                               |
| 父分类     | pid      | int(10)     |              |                                                            |
| 统计量     | count    | int(10)     |              |                                                            |
| 是否热门   | is_hot   | tinyint(1)  |              |                                                            |
| 首页显示   | is_index | tinyint(1)  |              |                                                            |
| 排序       | order_id | int(10)     |              |                                                            |