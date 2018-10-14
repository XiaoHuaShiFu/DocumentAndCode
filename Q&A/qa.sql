/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50544
 Source Host           : localhost:3306
 Source Schema         : qa

 Target Server Type    : MySQL
 Target Server Version : 50544
 File Encoding         : 65001

 Date: 14/10/2018 20:18:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `author_id` int(11) UNSIGNED NOT NULL,
  `content` varchar(10000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `collection` int(11) UNSIGNED NULL DEFAULT NULL,
  `date` datetime NOT NULL,
  `click` int(11) UNSIGNED NULL DEFAULT NULL,
  `likes` int(11) UNSIGNED NULL DEFAULT NULL,
  `comment` int(11) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of article
-- ----------------------------
INSERT INTO `article` VALUES (1, '从程序员的角度深入理解MySQL', 1, '前言 作为一名工作了4年的程序猿，今天我将站在程序员的角度以MySQL为例探索数据库的奥秘！ 数据库基本原理 我对DB的理解 第一，数据库的组成：存储 + 实例 不必多说，数据当然需要存储；存储了还不够，显然需要提供程序对存储的操作进行封装，对外提供增删改查的API，即实例。 一个存储，可以对应多个实例，这将提高这个存储的负载能力以及高可用；多个存储可以分布在不同的机房、地域，将实现容灾。 第二，按Block or Page读取数据 用大腿想也知道，数据库不可能按行读取数据（Why? ^_^）。实质上，数据库，如Oracle/MySQL，都是基于固定大小（比如16K）的物理块（Block or Page，我这里就不区分统一称为Block）来实现调度和管理的。要知道Block是数据库的概念，如何对应到文件系统呢？显然需要指出“这个Block的地址在哪里”，当查找到地址后，读取固定大小的数据就相当于完成了Block的读取了。 数据库很聪明的，它不会仅仅只读取需要读取的Block，它还会替我们把附近的Block块都读取加载至内存。实际上，这是为了减少IO次数，提高命中率。事实上，一个Block块的附近Block也是热点数据，这种处理方式很有必要！ 第三，磁盘IO是数据库的性能瓶颈 毫无疑问，数据在磁盘上，少不了磁盘IO。什么磁头旋转，定位磁道，寻址的过程，就不说了，我们是程序员，也管不了这些。但是这个过程确实是非常耗时的，和内存读取不是一个数量级，所以后来出现了很多方式来减少IO，提升数据库性能。 比如，增加内存，让数据库把数据更多的加载至内存。内存虽好，但也不能滥用，为什么这么说呢？假设数据库中有100G数据，如果都加载至内存，也就说数据库要管理100G磁盘数据+100G内存数据，你说累不累？（数据库要处理磁盘和内存的映射关系，数据的同步，还要对内存数据进行清理，如果涉及数据库事务，又是一系列复杂操作......）不过这里需要指出的是，为了加快内存查找速度，数据库一般对内存进行HASH存放。 比如，利用索引，索引相比内存，是一个性价比非常高的东西，后文详细介绍MySQL的索引原理。 比如，利用性能更好的磁盘...（和咱们就没关系呢） 第四，提出一些问题思考下： 为什么我们说利用delete删除一个表的数据较trancate一个表要慢？ 【一个按行查找删除，多费劲；一个基于Block的体系结构删除】 为什么我们说要小表驱动大表？ 【小表驱动大表会快？什么鬼？M*N和N*M不是一样的么？有鬼的地方，就有索引！】 探索MySQL索引背后的原理 对于绝大数的应用系统，读写比例在10:1，甚至100:1，而且insert/update很难出现性能问题，遇到最多的，最棘手的就是select了，select优化是重中之重，显然少不了索引！ 说起MySQL的索引，我们会冒出很多这些东西：BTree索引/B+Tree索引/Hash索引/聚集索引/非聚集索引...这么多，晕头！ 索引到底是什么，想解决什么问题？ 老生常谈了，官网说MySQL索引是一种数据结构，索引的目的就是为了提高查询效率。 说白了，不使用索引的话，磁盘IO次数比较多！要想减少磁盘IO次数，怎么办？ 我们想通过不断缩小想要获取的数据的范围来筛选出最终想要的结果，把每次查找数据的磁盘IO次数控制在一个很小的数量级，最好是常数数量级。 为了应对上述问题，B+Tree索引出来了！ Hello，B+Tree 在MySQL中，不同存储引擎对索引的实现方式是不同的，这里将重点分析MyISAM和Innodb。 ', 1, '2018-09-20 16:49:34', 100, 4523, 1);
INSERT INTO `article` VALUES (3, '浅谈XSS攻击的那些事（附常用绕过姿势）', 1, '前言\r\n随着互联网的不断发展，web应用的互动性也越来越强。但正如一个硬币会有两面一样，在用户体验提升的同时安全风险也会跟着有所增加。今天，我们就来讲一讲web渗透中常见的一种攻击方式：XSS攻击。\r\n什么是XSS攻击\r\n先上一段标准解释（摘自百度百科）。\r\n“XSS是跨站脚本攻击(Cross Site Scripting)，为不和层叠样式表(Cascading Style Sheets, CSS)的缩写混淆，故将跨站脚本攻击缩写为XSS。恶意攻击者往Web页面里插入恶意Script代码，当用户浏览该页之时，嵌入其中Web里面的Script代码会被执行，从而达到恶意攻击用户的目的。”\r\n相信以上的解释也不难理解，但为了再具体些，这里举一个简单的例子，就是留言板。我们知道留言板通常的任务就是把用户留言的内容展示出来。正常情况下，用户的留言都是正常的语言文字，留言板显示的内容也就没毛病。然而这个时候如果有人不按套路出牌，在留言内容中丢进去一行 XSS的危害\r\n其实归根结底，XSS的攻击方式就是想办法“教唆”用户的浏览器去执行一些这个网页中原本不存在的前端代码。\r\n可问题在于尽管一个信息框突然弹出来并不怎么友好，但也不至于会造成什么真实伤害啊。的确如此，但要说明的是，这里拿信息框说事仅仅是为了举个栗子，真正的黑客攻击在XSS中除非恶作剧，不然是不会在恶意植入代码中写上alert（“say something”）的。\r\n在真正的应用中，XSS攻击可以干的事情还有很多，这里举两个例子。', 0, '2018-09-20 20:12:05', 32, 312, 6);

-- ----------------------------
-- Table structure for article_collect
-- ----------------------------
DROP TABLE IF EXISTS `article_collect`;
CREATE TABLE `article_collect`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `collector_id` int(11) UNSIGNED NOT NULL,
  `article_id` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `collector_id`(`collector_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of article_collect
-- ----------------------------
INSERT INTO `article_collect` VALUES (1, 1, 1);
INSERT INTO `article_collect` VALUES (4, 8, 3);
INSERT INTO `article_collect` VALUES (6, 1, 3);

-- ----------------------------
-- Table structure for article_comment
-- ----------------------------
DROP TABLE IF EXISTS `article_comment`;
CREATE TABLE `article_comment`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `article_id` int(11) UNSIGNED NOT NULL,
  `respondent_id` int(11) UNSIGNED NOT NULL,
  `content` varchar(10000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `likes` int(11) UNSIGNED NULL DEFAULT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of article_comment
-- ----------------------------
INSERT INTO `article_comment` VALUES (1, 1, 8, 'with as语句好像不支持', 313, '2018-10-08 21:02:27');
INSERT INTO `article_comment` VALUES (2, 3, 10, '在3、配合csrf攻击完成恶意请求中，“具体插入代码如下”后面没给代码(*꒦ິ⌓꒦ີ)', 532, '2018-10-08 21:02:48');
INSERT INTO `article_comment` VALUES (3, 3, 11, '很不错', 1351, '2018-10-08 21:03:11');
INSERT INTO `article_comment` VALUES (4, 3, 9, '谢谢', 41, '2018-10-08 21:14:42');
INSERT INTO `article_comment` VALUES (5, 3, 12, '刚学前端的一点知识，但是也看懂了一点，学习了，谢谢！', 3, '2018-10-09 00:09:27');
INSERT INTO `article_comment` VALUES (6, 3, 8, '有不能理解的欢迎提问，才疏学浅也只能尽量解答。', 0, '2018-10-14 19:42:06');
INSERT INTO `article_comment` VALUES (7, 3, 8, 'xss姿势不用那么多，一个htmlspecialchars函数就GG了，最主要还是编码和结合js', 0, '2018-10-14 19:43:31');
INSERT INTO `article_comment` VALUES (8, 1, 1, '最后一个Demo，如果我就是想在一个age的区间中查找，应该怎样优化呢？', 1, '2018-10-14 19:57:29');

-- ----------------------------
-- Table structure for article_comment_like
-- ----------------------------
DROP TABLE IF EXISTS `article_comment_like`;
CREATE TABLE `article_comment_like`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `comment_id` int(11) UNSIGNED NOT NULL,
  `date` datetime NOT NULL,
  `liker_id` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 54 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of article_comment_like
-- ----------------------------
INSERT INTO `article_comment_like` VALUES (45, 5, '2018-10-14 18:24:26', 9);
INSERT INTO `article_comment_like` VALUES (46, 3, '2018-10-20 18:24:41', 9);
INSERT INTO `article_comment_like` VALUES (47, 2, '2018-10-14 18:24:54', 10);
INSERT INTO `article_comment_like` VALUES (48, 3, '2018-10-14 18:25:10', 11);
INSERT INTO `article_comment_like` VALUES (49, 4, '2018-10-14 18:54:07', 8);
INSERT INTO `article_comment_like` VALUES (51, 1, '2018-10-14 19:50:06', 1);
INSERT INTO `article_comment_like` VALUES (52, 8, '2018-10-14 19:57:33', 1);
INSERT INTO `article_comment_like` VALUES (53, 5, '2018-10-14 20:10:17', 1);

-- ----------------------------
-- Table structure for article_like
-- ----------------------------
DROP TABLE IF EXISTS `article_like`;
CREATE TABLE `article_like`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `article_id` int(11) UNSIGNED NOT NULL,
  `liker_id` int(11) UNSIGNED NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of article_like
-- ----------------------------
INSERT INTO `article_like` VALUES (2, 3, 9, '2018-10-14 02:18:02');
INSERT INTO `article_like` VALUES (3, 3, 10, '2018-10-14 02:18:09');
INSERT INTO `article_like` VALUES (4, 1, 16, '2018-10-14 02:18:15');
INSERT INTO `article_like` VALUES (6, 3, 8, '2018-10-14 02:21:34');

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `content` varchar(10000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `author_id` int(11) UNSIGNED NOT NULL,
  `follow` int(11) UNSIGNED NULL DEFAULT NULL,
  `date` datetime NOT NULL,
  `click` int(11) UNSIGNED NULL DEFAULT NULL,
  `comment` int(11) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `author_id`(`author_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES (1, 'java web现在流行用什么框架？', '有可能转到web端，先了解下。\r\n如果是提供接口和简单后台，用什么框架好？\r\n如果是中小型项目，又用什么框架？\r\n大型项目呢？', 8, 312, '2018-09-23 00:51:09', 321, 135);
INSERT INTO `question` VALUES (3, '多线程有什么用？', '最近看了多线程讲解很模糊', 8, 314, '2018-09-23 00:59:26', 764, 51);

-- ----------------------------
-- Table structure for question_comment
-- ----------------------------
DROP TABLE IF EXISTS `question_comment`;
CREATE TABLE `question_comment`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `question_id` int(11) UNSIGNED NOT NULL,
  `respondent_id` int(11) UNSIGNED NOT NULL,
  `content` varchar(10000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `likes` int(11) UNSIGNED NULL DEFAULT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of question_comment
-- ----------------------------
INSERT INTO `question_comment` VALUES (1, 3, 1, '使用多线程一般有两个不同的目的：\r\n一是把程序细分成几个功能相对独立的模块，防止其中一个功能模块阻塞导致整个程序假死（GUI程序是典型）\r\n另一个就是提高运行效率，比如多个核同时跑，或者单核里面，某个线程进行IO操作时，另一个线程可以同时执行。', 321, '2018-10-09 13:03:38');
INSERT INTO `question_comment` VALUES (2, 3, 10, '这么解释问题吧：\r\n1。单进程单线程：一个人在一个桌子上吃菜。\r\n2。单进程多线程：多个人在同一个桌子上一起吃菜。\r\n3。多进程单线程：多个人每个人在自己的桌子上吃菜。', 23, '2018-10-09 13:04:03');
INSERT INTO `question_comment` VALUES (3, 3, 12, '因为你有多个CPU啊，一个线程只能用一个CPU，其它的放在那儿又不会生利息。\r\n你说你只有一个CPU？那么多线程可以让它假装同时在干很多件事，也就是假装多个CPU，当然动作会慢一点，但很多时候你更慢对不。\r\n哦，你说你就两个CPU为什么要创建500个线程？谁让你用Tomcat！\r\n\r\n\r\n作者：徐辰\r\n链接：https://www.zhihu.com/question/19901763/answer/92822658\r\n来源：知乎\r\n', 311, '2018-10-09 13:04:31');
INSERT INTO `question_comment` VALUES (4, 3, 11, '我本来有一个问题\r\n用多程线了后，现在两个题问了有我', 75, '2018-10-09 13:04:46');
INSERT INTO `question_comment` VALUES (5, 1, 9, '大部分Java Web项目，通常会使用SSM(Spring+SpringMVC+Mybatis)来搭建项目的主体框架。如果想学习这些框架，下方将给亲推荐下免费学习课程：', 880, '2018-10-09 13:05:01');
INSERT INTO `question_comment` VALUES (6, 3, 8, '打个比方：我们寝室洗手台有2个水龙头，就是多线程。可以满足2个人刷牙。不然排队蛋疼', 0, '2018-10-09 13:32:19');
INSERT INTO `question_comment` VALUES (7, 1, 8, '大部分Java Web项目，通常会使用SSM(Spring+SpringMVC+Mybatis)来搭建项目的主体框架。如果想学习这些框架，下方将给亲推荐下免费学习课程：', 1, '2018-10-13 13:34:38');
INSERT INTO `question_comment` VALUES (8, 1, 1, '要转就直奔Spring Boot吧，包你贴心。', 1, '2018-10-13 13:55:16');
INSERT INTO `question_comment` VALUES (9, 1, 8, '基础框架Spring Boot，Netty\r\n微服务Spring Cloud\r\n数据访问层Hibernate，MyBatis\r\n楼上有提到Struts2的，不推荐，漏洞，陈旧。', 1, '2018-10-13 14:03:34');

-- ----------------------------
-- Table structure for question_comment_like
-- ----------------------------
DROP TABLE IF EXISTS `question_comment_like`;
CREATE TABLE `question_comment_like`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `comment_id` int(11) UNSIGNED NOT NULL,
  `date` datetime NOT NULL,
  `liker_id` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 44 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of question_comment_like
-- ----------------------------
INSERT INTO `question_comment_like` VALUES (3, 3, '2018-10-10 22:07:30', 11);
INSERT INTO `question_comment_like` VALUES (31, 5, '2018-10-12 21:08:49', 17);
INSERT INTO `question_comment_like` VALUES (33, 5, '2018-10-12 21:23:33', 18);
INSERT INTO `question_comment_like` VALUES (35, 5, '2018-10-13 00:25:52', 19);
INSERT INTO `question_comment_like` VALUES (41, 7, '2018-10-14 01:48:56', 1);
INSERT INTO `question_comment_like` VALUES (42, 8, '2018-10-14 01:48:59', 1);
INSERT INTO `question_comment_like` VALUES (43, 9, '2018-10-14 01:49:00', 1);

-- ----------------------------
-- Table structure for question_follow
-- ----------------------------
DROP TABLE IF EXISTS `question_follow`;
CREATE TABLE `question_follow`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `follower_id` int(11) UNSIGNED NOT NULL,
  `question_id` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `follower_id`(`follower_id`) USING BTREE,
  INDEX `question_id`(`question_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of question_follow
-- ----------------------------
INSERT INTO `question_follow` VALUES (1, 1, 3);
INSERT INTO `question_follow` VALUES (2, 8, 1);
INSERT INTO `question_follow` VALUES (4, 9, 1);
INSERT INTO `question_follow` VALUES (6, 8, 3);

-- ----------------------------
-- Table structure for search
-- ----------------------------
DROP TABLE IF EXISTS `search`;
CREATE TABLE `search`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `key_word` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `number_search` int(11) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of search
-- ----------------------------
INSERT INTO `search` VALUES (1, '滴滴打车', 132);
INSERT INTO `search` VALUES (2, '红海行动', 31);
INSERT INTO `search` VALUES (3, '沙漠', 566);
INSERT INTO `search` VALUES (4, '腾讯', 312);
INSERT INTO `search` VALUES (5, '阿里巴巴', 1214);
INSERT INTO `search` VALUES (6, '天猫', 312);
INSERT INTO `search` VALUES (7, '蚂蚁', 312);
INSERT INTO `search` VALUES (8, '小米', 76);
INSERT INTO `search` VALUES (9, '苹果', 3125);
INSERT INTO `search` VALUES (10, '百度', 756);
INSERT INTO `search` VALUES (11, '华为', 878);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `email` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `sex` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `introduction` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `follower` int(11) UNSIGNED NULL DEFAULT NULL,
  `follow` int(11) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '吴嘉贤', '15992321303', '123456', '827032783@qq.com', NULL, '我是一个很强的人', 2, 321);
INSERT INTO `user` VALUES (8, 'xhsf', '15992321101', '1234567', '827032786@qq.com', NULL, '啦啦啦', 654, 412);
INSERT INTO `user` VALUES (9, 'xhsf', '15992321301', '123456', '827032785@qq.com', NULL, '六六大顺', 857, 534);
INSERT INTO `user` VALUES (10, 'wujunhao', '13222323363', '135790', 'wujunhao@qq.com', NULL, NULL, 65, 312);
INSERT INTO `user` VALUES (11, '沙和', '12345678911', '123456', '123@123', NULL, NULL, 312, 74);
INSERT INTO `user` VALUES (12, '黄俊豪', '13112543213', '125321', '312312@qq.com', NULL, '这是一个晴朗的早晨', 643, 86);
INSERT INTO `user` VALUES (13, '吴家玄', '13632272563', '123456', '7723712371237@qq.com', NULL, NULL, 6735, 977);
INSERT INTO `user` VALUES (14, '桃子', '17520353254', '201119991.4xx', 'silu_635921728@qq,com', NULL, NULL, 321, 635);
INSERT INTO `user` VALUES (15, '王碧云', '15767691995', 'Wangbiyunskxj', '642708339@qq.com', NULL, NULL, 53, 65);
INSERT INTO `user` VALUES (16, '桃纸', '13415374833', '123456789', 'silu_635921728@qq.com', NULL, NULL, 6, 423);
INSERT INTO `user` VALUES (17, '陈雅静一号', '11111111111', '123456', '123@163.com', NULL, NULL, 76, 756);
INSERT INTO `user` VALUES (18, '陈雅静2号', '88888888888', 'chenyajing', 'chenyajing@qq.com', NULL, NULL, 765, 365);
INSERT INTO `user` VALUES (19, '吴泽林', '15521399288', '15521399288.', '171755422@qq.com', NULL, NULL, 987, 967);

-- ----------------------------
-- Table structure for user_follow
-- ----------------------------
DROP TABLE IF EXISTS `user_follow`;
CREATE TABLE `user_follow`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `follower_id` int(11) UNSIGNED NOT NULL,
  `user_id` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `follower_id`(`follower_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user_follow
-- ----------------------------
INSERT INTO `user_follow` VALUES (1, 1, 8);
INSERT INTO `user_follow` VALUES (2, 1, 9);
INSERT INTO `user_follow` VALUES (3, 1, 10);
INSERT INTO `user_follow` VALUES (5, 9, 8);
INSERT INTO `user_follow` VALUES (16, 1, 1);

SET FOREIGN_KEY_CHECKS = 1;
