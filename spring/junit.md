junit中的assert方法全部放在Assert类中，总结一下junit类中assert方法的分类。
1.assertTrue/False([String message,]boolean condition);
  判断一个条件是true还是false。感觉这个最好用了，不用记下来那么多的方法名。

2.fail([String message,]);
  失败，可以有消息，也可以没有消息。

3.assertEquals([String message,]Object expected,Object actual);
  判断是否相等，可以指定输出错误信息。
  第一个参数是期望值，第二个参数是实际的值。
  这个方法对各个变量有多种实现。在JDK1.5中基本一样。
  但是需要主意的是float和double最后面多一个delta的值。

4.assertNotEquals([String message,]Object expected,Object actual);
  判断是否不相等。
  第一个参数是期望值，第二个参数是实际的值。

5.assertArrayEquals([java.lang.String message,] java.lang.Object[] expecteds, java.lang.Object[] actuals) ;

6.assertNotNull/Null([String message,]Object obj);
  判读一个对象是否非空(非空)。

7.assertSame/NotSame([String message,]Object expected,Object actual);
  判断两个对象是否指向同一个对象。看内存地址。

8.failNotSame/failNotEquals(String message, Object expected, Object actual)
  当不指向同一个内存地址或者不相等的时候，输出错误信息。
  注意信息是必须的，而且这个输出是格式化过的。

9.assertThat(java.lang.String reason, java.lang.Object actual, org.hamcrest.Matcher matcher);

其中，reason为断言失败时的输出信息，actual为断言的值或对象，matcher为断言的匹配器，里面的逻辑决定了给定的actual对象满不满足断言。

（如果需要是用assertThat需要在项目中引入junit4的jar包,以及hamcrest-core.jar和hamcrest-library.jar）





1. 断言核心方法

assertArrayEquals(expecteds, actuals)	查看两个数组是否相等。
assertEquals(expected, actual)	查看两个对象是否相等。类似于字符串比较使用的equals()方法
assertNotEquals(first, second)	查看两个对象是否不相等。
assertNull(object)	查看对象是否为空。
assertNotNull(object)	查看对象是否不为空。
assertSame(expected, actual)	查看两个对象的引用是否相等。类似于使用“==”比较两个对象
assertNotSame(unexpected, actual)	查看两个对象的引用是否不相等。类似于使用“!=”比较两个对象
assertTrue(condition)	查看运行结果是否为true。
assertFalse(condition)	查看运行结果是否为false。
assertThat(actual, matcher)	查看实际值是否满足指定的条件
fail()	让测试失败
————————————————
版权声明：本文为CSDN博主「wangpeng047」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/wangpeng047/java/article/details/9628449

