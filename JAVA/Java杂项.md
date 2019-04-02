# 1.ArrayList基本操作

```java
ArrayList<String> arrayList = new ArrayList<>(100);
 
 //使arrayList分配包含100个对象的内部数组
 //如果知道已经要添加多少元素的话，可以节省重新分配空间带来的开销
 arrayList.ensureCapacity(100);
 
 //将数组的存储容量削减到当前尺寸
 arrayList.trimToSize();
 
 arrayList.add("element");
 //更新某个下表的元素
 arrayList.set(0, "element");
 //获取元素
 arrayList.get(59);
 //移除一个下标的元素
 arrayList.remove(0);
 //移除某个和一个对象相等的元素，使用equals方法
 //如果为null,会移除数组里的为null的元素
 arrayList.remove("element");
 
 //把arrayList转换成数组
 String[] strings = new String[arrayList.size()];
 arrayList.toArray(strings);
```

# 2.深克隆实现

实现Clone步骤 

①实现Cloneable接口, 这是一个标记接口，指示类设计者了解克隆过程，它使类在类型查询中可以使用instanceof语法

②重新定义clone方法，并指定punlic访问修饰符

```java
//深克隆实现
@Override
public User clone() throws CloneNotSupportedException {
    User user = (User) super.clone();
      //非不可变类型的域也要clone
    if (ar != null) {
        user.setAr((ArrayList) ar.clone());
    }
    if (product != null) {
        user.setProduct(product.clone());
    }
    return user;
}
```

# 3.3hashCode和equals方法的重写方式

```java
public class EqualsDemo{
    private String name;
    private String password;
    private int id;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EqualsDemo that =(EqualsDemo) o;
        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return password != null ? password.equals(that.password) : that.password == null;
    }
    
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + id;
        return result;
    }
}

class EqualsChild extends EqualsDemo{
    private String password;
    @Override
    public boolean equals(Object o) {
        //判断引用是否相等
        if (this == o) return true;
        //所有子类都有不同
        //判断类是否为null和类是否一样
        if (o == null || getClass() != o.getClass()) return false;
        //如果所有子类都有同一语义时使用
        //if (!(o instanceof EqualsDemo)) return false;
        //引用超类的equals方法
        if (!super.equals(o)) return false;
        //设置为本类对象
        EqualsChild that = (EqualsChild) o;
        //逐一校验此类每个参数
        return password != null ? password.equals(that.password) : that.password == null;
    }
    @Override
    public int hashCode() {
        return password != null ? password.hashCode() : 0;
    }
}
```

# 4.IntHolder BooleanHolder等类型，可以修改值又不改变对象

```java
IntHolder x  = new IntHolder(3);
x.value = x.value * 3;
System.out.println(x.value);
```

# 5.Math.ceil() Math.round Math.floor()

```java
Math.ceil(); 向上取整
Math.floor(); 向下取整
Math.round();四舍五入
```

# 6.System类

```java
//复制一个新的数组
 System.arraycopy(strings, 0, newArray, 0, len);
//返回Object.hashCode计算出来的相同散列码（根据对象的内存地址产生），即使obj所属的类已经重新定义了hashCode方法也是如此
System.identityHashCode(person);
```

# 7.transient

**transient**

java语言的关键字，[变量](https://baike.baidu.com/item/%E5%8F%98%E9%87%8F/3956968)[修饰符](https://baike.baidu.com/item/%E4%BF%AE%E9%A5%B0%E7%AC%A6/4088564)，如果用transient声明一个[实例变量](https://baike.baidu.com/item/%E5%AE%9E%E4%BE%8B%E5%8F%98%E9%87%8F/3386159)，当对象存储时，它的值不需要维持。换句话来说就是，用transient关键字标记的成员变量不参与序列化过程。

**持久化对象**

可能有一个特殊的对象数据成员 

**变量的值**

不包括在序列化的表示中 

**序列化**

transient型变量的值 

作用

Java的serialization提供了一种持久化对象实例的机制。当持久化对象时，可能有一个特殊的对象数据成员，我们不想用serialization机制来保存它。为了在一个特定对象的一个域上关闭serialization，可以在这个域前加上关键字transient。当一个对象被序列化的时候，transient型变量的值不包括在序列化的表示中，然而非transient型的变量是被包括进去的。

# 8.UUID.randomUUID().toString() 生成随机字符串

# 9.valueOf,parseInt,getInteger的区别

```java
parseInt //解析成一个整型
valueOf //解析成一个Integer
getInteger //方法假设String参数是一个系统属性数值的名称，会读取该系统属性，然后把系统属性的值转换成一个数字
```

# 10.变量的作用域分为四个级别：类级、对象实例级、方法级、块级

- 在Java中，变量的作用域分为四个级别：类级、对象实例级、方法级、块级。

  - 类级变量又称全局级变量或静态变量，需要使用static关键字修饰，你可以与 C/C++ 中的static 变量对比学习。类级变量在类定义后就已经存在，占用内存空间，可以通过类名来访问，不需要实例化。

  - 对象实例级变量就是成员变量，实例化后才会分配内存空间，才能访问。

  - 方法级变量就是在方法内部定义的变量，就是局部变量。

  - 块级变量就是定义在一个块内部的变量，变量的生存周期就是这个块，出了这个块就消失了，比如 if、for语句的块。块是指由大括号包围的代码

# 11.接收器参数

- 接收器参数，这个参数会引用当前对象，可以用非静态方法

```java
@Override
public boolean equals(AnnotionTest this, Object o)
```

- 内部类也可以引用外围类的对象

```java
public class AnnotionTest {
     class Iterator implements java.util.Iterator<Integer> {
         public Iterator(AnnotionTest AnnotionTest.this) {
         }
     }
}
```

