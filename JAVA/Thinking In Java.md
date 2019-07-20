[TOC]



# 2、一切都是对象  

1. 常量池：存放字符串常量和基本类型常量（public static final）

   常量池：JVM为每个已加载的类型维护一个常量池，常量池就是这个类型用到的常量的一个有序集合。包括直接常量(基本类型，String)和对其他类型、方法、字段的符号引用。池中的数据和数组一样通过索引访问。由于常量池包含了一个类型所有的对其他类型、方法、字段的符号引用，所以常量池在Java的动态链接中起了核心作用。常量池存在于堆中。

   ![](https://images2015.cnblogs.com/blog/1087853/201612/1087853-20161228171311414-415257293.png)

   - final修饰的局部变量是存放在栈中还是在常量池中 ？
     存放在常量池中。
     - 首先final关键字对于变量的存储区域是没有任何影响的。
     - jvm规范中，类的静态变量存储在方法区，实例变量存储在堆区。也就是说static关键字才对变量的存储区域造成影响。final关键字来修饰变量表明了该变量一旦赋值就无法更改。
     - 在Java中你可以这样理解：所有的变量，包括基本类型和引用类型，它们的变量都是存放在栈中，栈中的每个变量都包含类型、名称、值这些内容，只不过基本类型变量的值为一个具体的值，而引用类型的变量的值为对象在堆中的地址。

   - java中的常量池技术，是为了方便快捷地创建某些对象而出现的，当需要一个对象时，就可以从池中取一个出来（如果池中没有则创建一个），则在需要重复创建相等变量时节省了很多时间。常量池其实也就是一个内存空间，不同于使用new关键字创建的对象所在的堆空间。

2. 基本类型：在栈中直接存储值而非引用。

   - 所有数值类型都有正负号。
   - boolean类型村粗空间的大小没有明确指定，只能取字面值true或false。
   - 包装器类型是在堆中创建一个对象。

3. 数组：

4. 类：

   - 成员变量如果没进行初始化，Java也会保证有一个默认值。
   - 局部变量如果没进行初始化，会提示错误。

5. 略

6. 略

7. 略

8. 注释

   - @see：引用其他类
   - {@link package.class#member label}：用label作为超链接文本
   - {@docRoot}：到文档根目录的相对路径
   - {@inheritDoc}：这个类的最直接的基类中继承相关文档到当前的文档注释中
   - @version version-information：版本
   - @author author-information：作者
   - @since：最早使用版本
   - @param parameter-name description
   - @return：返回值
   - @throws fully-qualified-class-name description：异常
   - @deprecated：过时
   - @Deprecated：过时

# 3、操作符

1. 基本类型的赋值是直接修改值。
2. ==和!=比较的是对象的引用。
3. 除了boolean外，其他类型都可以转换位其他基本类型。
4. 对float或double转换成整形，都进行了截尾操作。

# 4、执行流程

# 5、初始化与清理

1. this：当前对象的引用，或调用一个构造器。
   - 调用类的方法时，会隐式的把所操作对象的引用传递给类的方法。
2. static方法：没有this的方法。
3. 构造器初始化
   - 无法阻止自动初始化的进行，它在构造器被调用之前发生。
4. 数组初始化：数组是一个引用，所以不可以指定大小。用{}声明的数组的存储空间由编译器负责。

# 6、访问权限控制

1. 略
2. Java访问权限修饰词
3. 接口与实现
4. 类的访问权限

# 7、复用类

1. 组合语法

   1. 初始化引用的位置
      - 定义对象的地方。总能在构造器调用之前被初始化。
      - 在类的构造器中。
      - 在要用这些对象前，惰性初始化。在生成对象不值得及不必每次都生成对象的情况下，这种方式可以减少额外的负担。
      - 使用实例初始化。也就是把一个引用赋值给这个引用。
2. 继承语法

   1. 初始化基类：基类的子对象被包装在导出类对象内部。构造过程是从基类向外扩散的，所以基类在导出类构造器可以访问它之前，就以及完成了初始化。
   2. 基类构造器总会被调用，在导出类构造器之前被调用。
   3. 如果没有默认基类构造器，或者想调用一个带参数的基类构造器，就必须使用super显式的调用基类构造器，并且配以适当的参数列表。调用基类构造器是在导出类构造器中要做的第一件事。
3. 代理：使用代理类可以只使用成员对象的方法的某个子集。而不像组合和代理一样会使用所有方法。
4. 结合使用组合和继承

   1. 确保正确清理

      - 垃圾回收的顺序和生成顺序相反，也就是从派生类到基类。
      - 如果需要进行清理，最好是编写子集的清理方法，但不要使用finalize()。
5. 组合与继承之间的选择
   1. is-a是一个的关系是用继承来表达的。
   2. has-a有一个的关系是用组合来表达的。
6. 向上转型：将派生类向基类转型称为向上转型。也就是UML图的箭头是向上指的。
   - 使用继承的情况：新类需要向上转型。
7. final关键字
   1. final数据
      - 一个永不改变的编译时常量。
      - 一个在运行时被初始化的值，而你不希望它被改变。
      - final的基本类型是常量。
      - final的引用类型是引用恒定不变。final的值还是可能改变的。
      - final的数据在编译时有可能不知道它的值。如用一个随机数去填充final数据。
   2. final参数
      - 无法修改参数的引用。
      - 主要用来向匿名内部类传递数据。
   3. final方法
      - 继承类无法修改它的含义。
      - 类中所有的private方法隐式地指定为final的。虽然没什么意义。
   4. final类
      - 无法被继承。
      - 所有方法隐式的指定为final。
8. 初始化及类的加载
   - Java中的类文件在初次使用时才会加载。一般发生于创建类的第一个对象时，或者访问static域或static方法时。
   - 加载全过程
     1. 启动加载器找出Beetle类的.class代码，并加载它的基类代码。
     2. 根据基类中的static初始化，然后导出类的static初始化。
     3. 把对象中所有的基本类型设置位默认注，对象引用设置为null。
     4. 调用基类的构造器，实例变量初始化。
     5. 执行构造器其余部分。

# 8、多态

1. 多态的概念

   - 多态可以消除类型之间的耦合关系。
   - 也就是所有导出类可以通过一个基类来调用。
   - 多态也称为后期绑定和动态绑定。
   - 除了static方法和final和private方法外，其他方法都属于后期绑定。
   - 可扩展性。操作基类接口的方法不需要任何改动就可以应用于新类。
   - 可以将改变的事物与未变的事物分离开来。
   - 基类中方法1调用方法2，而派生类覆盖方法2，再次调用基类中的方法1时，会调用到派生类的方法2。
     - 因为是动态绑定的，调用基类的方法1时，回去动态检查要调用方法的实现，这时会检查到派生类的方法2。也可以理解为，虽然从基类中继承了一些东西，但是自己也改变了一些东西。
   - 导出类中，对于基类中的private方法，最好采用不同名字。
     - 因为private方法是final，不会被覆盖，如果同名可能会产生误解。
   - 任何域访问操作都将由编译器解析，因此域不是多态的。
     - 也就是如果派生类和基类都有相同的域，那么派生类会同时拥有自身和基类的两个域。想要调用基类的域必须super.field，默认是调用this.field。
     - 此情况在正常开发中不会发生。因为正常都将域设置为private。
     - 最好不要对基类和导出类中的域赋予相同的名字，会造成混淆。
   - 静态方法是与类，而不是单个对象关联的。

2. 构造器的多态。

   - 构造器实际上是static方法，隐式的。

   - 构造器调用顺序

     1. 调用基类构造器。不断递归下去。先调用跟类，一直到最低层的导出类。
     2. 按声明顺序调用成员的初始化方法。
     3. 调用导出类构造器的主体。

   - 摧毁顺序与初始化顺序相反，也就是先对导出类进行清理，然后才是基类。

   - 可以通过在被分享的类中设置引用计数器，这样可以确保在引用数为0时才摧毁对象。

     - 示例：

       ```java
       public class Human {
           
           private static long counter = 0;
           private final long id = counter++;
           private long refcount = 0;
           
           public Human() {
               addRef();
           }
           
           public void addRef() {
               this.refcount++;
           }
           
           protected void dispose() {
               if (--refcount == 0) {
                   System.out.println("Disposing " + this);
               }
           }
       }
       ```

   - 如果在基类中调用导出类的方法，可能导致导出类实际上的成员变量未初始化。

     - 初始化的过程：
       1. 在其他任何事物发生之前，将分配给对象的存储空间初始化成二进制的0。
       2. 调用基类构造器。在基类构造器内会调用导出类的覆盖后的方法。
       3. 按照声明的顺序调用成员的初始化方法。
       4. 调用导出类的构造器主体。

3. 协变返回类型：在导出类中覆盖的方法可以返回基类方法的返回类型的某种导出类型。

   - 示例：

     ```java
     public class Home {
         public Human print() {
             System.out.println("I am Home");
             return new Human();
         }
     }
     
     public class Family extends Home {
         @Override
         public Student print() {
             System.out.println("I am student");
             return new Student();
         }
     }
     ```

4. 向下转型

   - 一定要类型匹配才可以向下转型。
   - Java在运行期间采用“运行时类型识别（RTTI）”来确保类型的正确。

# 9、接口

1. 接口中声明的域默认是final和static的。
2. 使用接口+设配器模式实现无法修改类的解耦。
3. Java中的多重继承
   - 接口的方法可由实现类继承来的方法来实现。
   - 接口的核心原因：为了能够向上转型为多个基类型。
   - 次原因：防止客户端程序员创建该类的对象，并确保这仅仅是建立一个接口。
4. 组合接口时尽量不要使用相同的方法名，会造成混乱。也无法通过返回值来区分。
5. 接口中的域不是接口的一部分，它们的值被存储在该接口的静态存储区域内。
6. 接口嵌套
   - 用私有接口实现的共有类只能被自身类所使用，也就是有权使用私有接口的类，也就是包含此私有接口的类。
   - 嵌套在另一个接口中的接口自动就是public的。
   - private接口不能再定义它的类之外被实现。
7. 接口与工厂：可以使工厂返回的实现类通过接口进行解耦。

# 10、内部类

1. 内部类可以访问外围对象的所有成员，而不需要任何特殊条件。

   - 当外围类的对象创建了一个内部类对象时，此内部类对象必定会秘密地捕获一个指向那个外围类对象的引用。
   - 然后，在你访问此外围类的成员时，就是用那个引用来选择外围类的成员。
   - 构建内部类对象时，需要一个指向其外围类对象的引用，如果编译器访问不到这个引用就会报错。

2. .this与.new

   - 其他类构造某类的内部类必须为其提供外围类的引用（.new）。

   - 拥有外围类之前是不可能创建内部类对象的，因为内部类对象会隐式地连接到创建它的外围类对象上。除非内部类是static的。

   - 通过外部类名.this获取外部类对象的引用。

   - 示例：

     ```java
     public interface FarInte {
         interface SubInte {
             void f();
         }
         void g();
     }
     
     /**
      * 实现了TestInte.SubInte接口
      */
     class OtherClass implements TestInte.SubInte {
         @Override
         public void f() {
             /**
              * 构造内部类必须为其提供外围类的引用（.new）
              */
             TestInte testInte =  new TestInte();
             TestInte.Inner inner = testInte.new Inner();
         }
     }
     
     /**
      * TestInte从FarInte继承了它的内部的SubInte接口
      */
     class TestInte implements FarInte, TestInte.SubInte{
         public class Inner {
             /**
              * 内部类对象引用外围类对象（.this）
              */
             public TestInte outer() {
                 return TestInte.this;
             }
         }
     
         @Override
         public void f() {
     
         }
     
         @Override
         public void g() {
             Inner inner = new Inner();
         }
     
     }
     ```

3. 内部类与向上转型

   - private内部类可以完全阻止任何依赖于类型的编码，并且完全隐藏的实现的细节。因为得到的只是指向基类或接口的引用。
   - protected内部类只能由外围类、子类及同包下的类访问。
   - 无法向下转型成priavate或protected的内部类（除非是继承自它的子类或是包内的类，因为可以访问类的名字），因为private内部类的名字不可以访问。
   - 从客户端程序员的角度，由于不能访问任何新增加的、原本不属于公共接口的方法，所有扩展接口是没有价值的。

4. 在方法和作用域的内部类

   - 方法内的内部类：只有定义此类的方法内才能使用。

     - 示例：

       ```java
       class OtherClass implements TestInte.SubInte {
           @Override
           public void f() {
               class Tes implements interface{
                   
               }
           }
       }
       ```

5. 匿名内部类

   - 表达式里的匿名类对象被自动向上转型为基类。
   - 对于有参数的基类构造器只需要传递参数给基类构造器即可。
   - 通过实例初始化，就能够达到为匿名内部类创建一个构造器的效果。

6. 嵌套类：被声明为static的内部类。

   - 嵌套类与外围类没有关系。

   - 创建嵌套类对象不需要外围类的对象。

   - 不能从嵌套类的对象中访问非静态的外围类对象。

   - 普通内部类的字段与方法，只能放在类的外部层次上，所以普通内部类不能由static数据和static字段，也不能包含嵌套类。而嵌套类都可以有。

   - 接口内部的类

     - 嵌套类可以作为接口的一部分。自动是public和static的。

     - 示例：

       ```java
       public interface FarInte {
           void g(Date a);
       
           //嵌套类
           class InnerClass3 {
               //嵌套类里的内部类
               public class Test {
                   public void out3() {
                       System.out.println("dddddddddddddddddd");
                   }
               }
           }
       }
       ```

   - 内部类能够透明的访问所以它所嵌入的外围类的所有成员。

7. 内部类的作用

   - 每个内部类都能独立地继承自一个（接口的）实现，所以无论外围类是否已经继承了某个（接口的）实现，对于内部类都没有影响。
   - 内部类可以有多个实例，每个实例都有自己的状态信息，并且与外围类对象的信息相互独立。
   - 在单个外围类中，可以让多个内部类以不同的方式实现同一个接口，或继承同一个类。
   - 创建内部类对象的时刻并不依赖于外围类对象的创建。
   - 内部类并没有is-a关系，它只是一个独立的实体。
   
8. 闭包：一个可调用的对象，记录了来自于创建它的作用域。

   - 内部类是面向对象的闭包，包含外围类对象的信息，拥有指向外围类的引用。内部类有权按操作所有的成员，包括private成员。

   - 通过公有接口+私人内部类+外围类公有实例化对象方法，把私人内部类传递给外部，这样私人内部类既可以使用外围基类的所有方法和属性，还可以在运行时候动态决定需要调用什么方法。

   - 示例：

     ```java
     public class Family extends Home {
         public static void main(String[] args) {
             Family family = new Family();
             Human human = family.getCallbackReference();
             human.eat("apple");
         }
         
         private String eatAnything(String anything) {
             return "I have been eat " + anything;
         }
         private class HaiXin implements Human {
             @Override
             public void eat(String food) {
                 String result = eatAnything(food);
                 System.out.println(result);
             }
         }
         public Human getCallbackReference() {
             return new HaiXin();
         }
     }
     
     public interface Human {
         void eat(String food);
     }
     ```

9. 内部类与控制框架

   - 控制框架：解决响应式事件的需求。通过Event抽象接口，ready，action方法来实现。

   - 要根据不同的Event有不同的行为。

     - 内部类可以用来表示解决问题所需的各种不同action()。
     - 内部类可以很容易地访问外围类的任意成员。从而使实现变得优雅。

   - 可以通过实例化方法或obj.new innerClass()。

   - 示例：

     ```java
     public class Family extends Home {
         
         public static void main(String[] args) {
             Family family = new Family();
             family.addEvent(family.new XiaoHuaShiFu(30));
             List<Event> events = new ArrayList<>();
             events.add(family.new Paidaxing(30));
             events.add(family.new XiaoHuaShiFu(30));
             family.addEvent(family.new Restart(30, events));
             family.run();
         }
     
         /************ 控制框架 **********/
         private List<Event> eventList = new ArrayList<>();
     
         public void addEvent(Event e) {
             eventList.add(e);
         }
     
         public void run() {
             while (eventList.size() > 0) {
                 new ArrayList<>(eventList).stream().filter(Event::ready).forEach(e -> {
                     System.out.println(e);
                     e.action();
                     eventList.remove(e);
                 });
             }
         }
     
         /**
          * 可以轻松的调用外围类-控制框架类的方法
          */
         public class XiaoHuaShiFu extends Event {
     
             public XiaoHuaShiFu(long delayTime) {
                 super(delayTime);
             }
     
             @Override
             public void action() {
                 System.out.println(eatAnything("haixing"));
             }
         }
     
         public class Paidaxing extends Event {
     
             public Paidaxing(long delayTime) {
                 super(delayTime);
             }
     
             @Override
             public void action() {
                 System.out.println(eatAnything("xiaohua"));
             }
         }
     
         /**
          * 不断的使事件循环
          */
         public class Restart extends Event {
     
             private List<Event> eventList;
     
             public Restart(long delayTime, List<Event> eventList) {
                 super(delayTime);
                 this.eventList = eventList;
                 eventList.stream().forEach(Family.this::addEvent);
             }
     
             @Override
             public void action() {
                 eventList.stream().forEach(e -> {
                     e.start();
                     addEvent(e);
                 });
                 start();
                 addEvent(this);
             }
     
             @Override
             public String toString() {
                 return "this is restart";
             }
         }
     
     }
     ```

10. 内部类继承：指向外围类对象的引用必须被初始化，必须使用enclosingClassReference.super(); 来解决。

    ```java
    public class Family {
        
        public class XiaoHuaShiFu extends Event {
            public XiaoHuaShiFu(long delayTime) {
                super(delayTime);
            }
            @Override
            public void action() {
                System.out.println(eatAnything("haixing"));
            }
        }
    
    }
    
    public class Wu extends Family.XiaoHuaShiFu {
        public Wu(long delayTime, Family family) {
            family.super(delayTime);
        }
    }
    ```

11. 内部类不会被覆盖：父子两个类生成的对象里的同名内部类是两个独立的实体。要覆外围类里的内部类只能通过在内部类设置外围类类型的成员变量a，然后在外围类的子类里覆盖a为b时，设置外围类的成员变量为b。

12. 局部内部类：在方法体里面声明一个内部类。

    - 此内部类不能有访问说明符，因为它不是外围类的一部分。

    - 但是它可以范围当前代码块内的常量，及此外围类的所有成员。

    - 使用场景：需要不止一个该内部类的对象。

    - 示例：

      ```java
      public class Family extends Home {
          public Event getXiaohua() {
              String food = "li";
              class XiaoHua extends Event {
                  public XiaoHua(long delayTime) {
                      super(delayTime);
                  }
                  @Override
                  public void action() {
                      System.out.println(eatAnything(food));
                  }
              }
              return new XiaoHua(30);
          }
      }
      ```

13. 内部类标识符

    - 匿名内部类：外围类名+$+数字+.class
    - 局部内部类：外围类名+$+数字+局部内部类名+.class

# 11、持有对象

1. 添加一组元素

   - 使用Connections.addAll(userList, users)，此方式会逐一添加数组里元素的引用。

     ```java
     List<Integer> idList = new ArrayList<>();
     Collections.addAll(idList, ids);
     ```

   - 使用Arrays.asList(users)，此方式会添加数组的引用。

     ```java
     List<User> userList = Arrays.asList(users);
     ```

2. 截取list中的一个片段

   - ```java
     userList.subList(0, 3)
     ```

3. 排序

   - Collections.sort()

     ```java
     Collections.sort(userList, (o1, o2) -> {
         if (o1.getId() > o2.getId()) {
             return 1;
         } else if (o1.getId() < o2.getId()) {
             return -1;
         }
         return 0;
     });
     ```

   - list.sort()

     ```java
     userList.sort((o1, o2) -> {
         if (o1.getId() > o2.getId()) {
             return 1;
         } else if (o1.getId() < o2.getId()) {
             return -1;
         }
         return 0;
     });
     ```

4. 乱序

   - Collections.shuffle()：把集合中的元素打乱

     ```java
     Collections.shuffle(userList);
     ```

5. 迭代器

   - ListIterator：双向迭代器，可以获取前后元素索引，set()方法替换访问的最后一个元素，通过listIterator(n)指定迭代器的起始位置。

     ```java
     ListIterator<User> iterator = userList.listIterator();
     while (iterator.hasNext()) {
         System.out.println(iterator.nextIndex());
         System.out.println(iterator.next());
     }
     
     
     while (iterator.hasPrevious()) {
         System.out.println(iterator.previousIndex());
         System.out.println(iterator.previous());
     }
     ```

6. map

   - HashMap：查找速度最快，但元素无序。
   - TreeMap：按照比较结果升序保存键。
   - LinkedHashMap：按照插入顺序保存键，保留了HashMap的查询速度。

7. list

   - ArrayList：随机访问元素快，但在list中间插入和移除元素时慢。
   - LinkedList：在list中间插入和移除元素快，随机访问较慢。可以用作栈、队列和双端队列。
   - PriorityQueue：实际上是维护一个堆。最小值拥有最高优先级。用Comparator比较。

8. set

   - HashSet：使用散列函数。快速查找。
   - TreeSet：将元素存储在红黑树中。
   - LinkedHashSet：使用散列函数。

# 12、通过异常处理错误

1. 记录异常信息

   - 捕获异常信息写入流里

     ```java
     StringWriter trace = new StringWriter();
     e.printStackTrace(new PrintWriter(trace));
     trace.toString();
     ```

   - 通常是写入日志里

2. finally语句

   - 当要把除内存之外的资源回复到它们的初始状态时，就要用到finally子句。如打开的文件、网络连接。
   - 与return的位置无关，也就是不管怎样都会被执行。

3. 异常的限制

   - 当覆盖方法时，只能抛出在基类方法的异常说明里列出的那些异常。接口实现也是一样。这是为了基类代码应用到其派生类对象的时候，一样能够工作。
   - 异常限制堆构造器不起作用。但是派生类构造器异常说明必须包含基类构造器的异常说明。
   - 派生类构造器不能捕获基类构造器抛出的异常。
   - 基类的方法带有异常说明，派生类的方法也可以不带异常说明。因为这样不会破坏已有程序。向上转型的时候只会使异常变多，不会变少。
   - 派生类的异常可以是基类异常的派生类。

4. 构造器

   - 对于构造阶段可能会抛出的异常，并且要求清理的类，最安全的使用方式是使用嵌套的try子句。这样在打开失败时，不会关闭资源。但如果打开成功，一定会关闭资源。

     - 这种用法在构造器不抛出任何异常时也应该运用：在创建需要清理的对象之后，立即进入一个try-finally语句块。

     - 示例：

       ```java
       try {
           //①打开资源
           in = new BufferedReader(new FileReader("filename"));
           //②打开资源后马上一个try-finally语句块
           try {
       		
           } finally {
               try {
                   //③关闭资源
                   in.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }
       ```

5. 把被检查异常转换成不检查异常

   - 把被检查的异常包装进RuntimeException里。

     ```java
     try {
         throw new FileNotFoundException();
     } catch (Exception e) {
         throw new RuntimeException(e);
     }
     ```

     ```java
     try {
         throw new FileNotFoundException();
     } catch (FileNotFoundException e) {
         RuntimeException re = new RuntimeException(e);
         try {
             throw re.getCause();
         } catch (FileNotFoundException e1) {
             System.out.println("dddddddddddddd");
         } catch (Throwable throwable) {
             throwable.printStackTrace();
         }
     }
     ```

# 13、字符串

1. Formatter：将格式化字符串与数据翻译成需要的结果，并输出到目的地。

   - 示例：

     ```java
     Formatter formatter = new Formatter(System.out);
     formatter.format("I am %s ", "wjx");
     ```

   - String.format()也可以格式化输出一个String，不过这是一次性的。内部其实也是用Formatter。

2. 格式化说明符

   - %-15s：15表示最短长度，-表示向左对齐，默认向右。
   - %.3：表示最大长度3。

3. 正则

# 14、类型信息

1. super和extends：此类是某个类的超类或子类

   - 示例：

     ```java
     //Child继承自Father
     Class<? extends Father> c = Child.class;
     //Father的超类是Child
     Class<? super Child> f = Father.class;
     ```

2. 类型转换

   - cast()

     ```java
     Class<? super Child> f = Father.class;
     Class<? extends Father> c = Child.class;
     Object child = new Child();
     
     //转换成基类
     f.cast(child);
     //转换为自身类
     c.cast(child);
     ```

3. 动态instanceof

   - c.isInstance(o)方法：检查o是不是c类型。

     - 示例：

       ```java
       f.isInstance(child); //child是f类型
       ```

   - bc.isAssignableFrom(c)方法：c是不是可以转换成bc，也就是bc是基类。

     - 示例：

       ```java
       f.isAssignableFrom(c) //c可以转换成f
       ```

4. 注册工厂：在基类保存一个工厂列表，动态的管理这个列表，然后通过这个列表生成对象。

   - 列表里的每个元素都是一个工厂。

   - 通过add方法添加新的工厂。

   - 工厂存在于要创建类里。也是就要创建类的一个子类。

   - 示例：

     ```java
     //工厂接口
     interface Factory<T> {
         T create();
     }
     
     //基类
     public class Home1 {
         //子类的工厂列表
         static List<Factory<? extends Home1>> home1Factories = new ArrayList<>();
     
         //初始化工厂列表
         static {
             home1Factories.add(new Family1.Factory());
             home1Factories.add(new House.Factory());
         }
     
         //获取工厂的方法
         public static Home1 create() {
             return home1Factories.get((int) (Math.random() * 2)).create();
         }
         
         //增加工厂的方法
         public static void add(Factory<? extends Home1> factory) {
             home1Factories.add(factory);
         }
     }
     
     //子类1
     class Family1 extends Home1{
         public static class Factory implements com.xuexi.thinginginjava.Factory<Family1> {
             @Override
             public Family1 create() {
                 return new Family1();
             }
         }
     }
     //子类2
     class House extends Home1{
         public static class Factory implements com.xuexi.thinginginjava.Factory<House> {
             @Override
             public House create() {
                 return new House();
             }
         }
     }
     ```

5. final域再遭遇修改时是安全的，运行时系统会在不抛出异常的情况下接收任何修改的尝试，但是实际上不会发生任何修改。

# 15、泛型

1.   各种容器帮助类
     - Sets：交集、并集、差集、补给。
     - EnumSet：enum的集合。EnumSet也包含各种对Enum的各种操作。
     
2. Generator使用

   - 示例：

     ```java
     public interface Generator<T> {
         T next();
     }
     
     //每次都新的一个Generator
     class Customer {
         private Customer() {}
         public static Generator<Customer> generator() {
             return Customer::new;
         }
     }
     
     //使用静态单例Generator
     class Teller {
         private Teller() {}
         public static Generator<Teller> generator = Teller::new;
     }
     ```
   
3.   使用泛型参数T创建对象的方法

     - 通过显式工厂对象

       ```java
       //工厂接口
       public interface FactoryI<T> {
           T create();
       }
       //需要使用泛型参数T来创建对象的类
       class Foo2<T> {
           private T x;
           public <F extends FactoryI<T>> Foo2(F factory) {
               x = factory.create();
           }
       }
       
       //类Integer的工厂
       class IntegerFactory implements FactoryI<Integer> {
           public Integer create() {
               return 0;
           }
       }
       
       class Widget {
           //类Widget的工厂
           public static class Factor implements FactoryI<Widget> {
               public Widget create() {
                   return new Widget();
               }
           }
       }
       
       class Test {
           public static void main(String[] args) {
               Foo2<Integer> foo2 = new Foo2<>(new IntegerFactory());
               Foo2<Widget> foo21 = new Foo2<>(new Widget.Factor());
           }
       }
       ```

     - 使用模板方法

       ```java
       abstract class GenericWithCreate<T> {
           //泛型参数T
           final T element;
           GenericWithCreate() {
               element = create();
           }
       	//模板方法
           abstract T create();
       }
       
       //类ABC
       class ABC {}
       
       //实现模板方法的类
       class Creator extends GenericWithCreate<ABC> {
           @Override
           ABC create() {
               return new ABC();
           }
       }
       ```

4.   通配符

     - ? super class：?可以是任意T的子类型或自身类型。

       ```java
       public <T> void test(List<? super T> list, T item) {
           list.add(item);
       }
       ```

     - ? extends class：基本难以确定类型。

5.   自动包装机制不能应用于数组。

     - 示例：

       ```java
       Integer[] arr = new Integer[10];
       arr[0] = 10;
       arr[1] = 12;
       //报错
       //int[] arr1 = arr;
       ```

6.   可以使用List.class.cast()进行强制转型，但此方法无法保证类型正确。

     - 示例：

       ```java
       List<Apple> list1 = List.class.cast(getObjects());
       ```

7.   由于擦除的原因，重载方法将产生相同的类型签名。

     - 示例：将无法通过编译，因为两个方法效果是一样的。

       ```java
       class UserList<T, W> {
           void f(T v) {}
           void f(W v) {}
       }
       ```

8.   自限定：强制有要求将正在定义的类当作参数传递给基类。

     - 示例：

       ```java
       class Self<T extends Self<T>> {
           T element;
           Self<T> set(T arg) {
               element = arg;
               return this;
           }
           T get() {
               return element;
           }
           public static void main(String[] args) {
               A a = new A();
               a.set(new A());
               a.f(new B());
               //报错，C不是自限定参数
               //a.f(new C());
               //报错，B不是A的正在定义的类。
               //a.set(new B());
           }
       }
       
       class A extends Self<A> {
       }
       
       class B extends Self<B> {
       }
       
       class C {}
       ```

     - 如果不适用自限定，将重载参数类型。如果使用自限定，只能获得某个方法的有一个版本，它将接收确切的参数类型。

9.   动态类型安全集合

     - Collections.checkedCollection()，checkedSet，checkedSortedSet，checkedNavigableSet，checkedList，checkedMap，checkedSortedMap。checkedNavigableMap，checkedQueue，checkedCollection将生成能够受检查的列表。

     - 如果不使用这种列表，如下情况将不会报错。当然可以通过\<Dog\>限定类型，会在编译时报错。

       ```java
       //在狗列表里添加猫
       public static void main(String[] args) {
       	List<Dog> dogs = new ArrayList<>();
           method(dogs);
       }
       static void method(List l) {
           l.add(new Cat());
       }
       ```

10.   动态异常：编写随检查型异常的类型而发生变化的泛型代码。

      - 示例：

        ```java
        interface Processor<T, E extends Exception> {
            void process(List<T> list) throws E;
        }
        
        class Processor1 implements Processor<String, NullPointerException> {
            @Override
            public void process(List<String> list) throws NullPointerException {
            }
        }
        
        class Processor2 implements Processor<Integer, IllegalAccessException> {
            @Override
            public void process(List<Integer> list) throws IllegalAccessException {
            }
        }
        ```

# 16、数组

1. Arrays方法

   - deepToString()：把多维数组转换成字符串。示例：

     ```java
     System.out.println(Arrays.deepToString(a));
     ```

   - equals()和deepEquals()比较两个数组是否相等。

   - fill()对数组进行填充。

   - sort()排序。

     - 比较类实现Comparable。
     - 构建一个Comparator。可以用函数式。
     - String数组可用String.CASE_INSENSITIVE_ORDER忽略大小写。默认是大写字母排在前面。

   - binarySearch()在已经排序的数组中查找元素。二分查找。

     - 若找不到则会返回   ( -插入点 - 1 )

   - toString()

   - hashCode()产生数组的散列码。

   - asList()将数组转换成列表。

2. 复制数组的方法

   - System.arraycopy()：复制数组，速度比for循环快很多。这是进行引用的复制，也就是浅复制。不会进行自动包装和自动拆包。

# 17、容器

1. 填充容器

   - Collections.nCopies()可以填充容器。

     ```java
     List<Integer> list = new ArrayList<>(Collections.nCopies(10, 3));
     ```

   - Collections.fill()可以替换list中的元素。

     ```java
     Collections.fill(list, 6);
     ```

2. Map

   - TreeMap

     - 唯一带有subMap()方法的Map，返回一个子树。
     - headMap()和tailMap()返回一个小于toKey的子树或大于等于fromKey的子树。
     - firstKey()和lastKey()方法返回第一个和最后一个键。
     - 次序由Comparable或Comparator决定。

   - LinkedHashMap

     - 维持插入顺序。

     - 也可以构造时设定LRU排序。最近最少使用的会被排到最前面。

       ```java
       Map<String, Integer> lruMap = new LinkedHashMap<>(16, 0.75f, true);
       ```

     - 访问速度只比HashMap慢一点。

     - 迭代访问时更快。

   - WeakHashMap：如果键没有指向某个引用，将会被CG回收。

     - 适用场景：如果在系统中需要一张很大的Map表，Map中的表项作为缓存只用，这也意味着即使没能从该Map中取得相应的数据，系统也可以通过候选方案获取这些数据。虽然这样会消耗更多的时间，但是不影响系统的正常运行。

   - values()方法：返回一个只包含Map的值的Collection。

3. 散列和散列码

   - HashMap使用equals()判断当前的键是否与表中存在的键相同。
   - equals()必须满足的5个条件：
     1. 自反性。x.equals(x) -> true
     2. 对称性。(x.equals(y) -> true) -> (y.equals(x) -> true)
     3. 传递性。
     4. 一致性。
     5. 对任何不是null的x, x.equals(null) -> false。
   - 默认的Object.equals()只是比较对象的地址。

# 18、IO

1. 列表查看器

   - File.list()列出此路径下的所有文件名

     - 示例：列出并使用过滤器过滤出需要的文件名。

       ```java
       path.list((dir, name) -> name.endsWith(".jpg"))
       ```

2. 字节流

   - DataInputStream和DataOutputStream：基本数据类型

     ```java
     DataInputStream dataInputStream = new DataInputStream(
             new FileInputStream(new File("D:\\pictures\\data.txt")));
     System.out.println(dataInputStream.readInt());
     
     DataOutputStream dataOutputStream = new DataOutputStream(
             new FileOutputStream(new File("D:\\pictures\\data.txt")));
     dataOutputStream.writeInt(1);
     ```

   - PrintStream：格式化输出

     ```java
     PrintStream printStream = new PrintStream(
             new FileOutputStream
                     (new File("D:\\pictures\\data.txt")));
     printStream.println("dddddddddddddddddsssssssss");
     ```

3. 字符流

   - 适配器InputStreamReader和OutputStreamWriter可以把字节流转换成字符流。

   - PrintWriter

     ```java
     BufferedReader reader = new BufferedReader(
             new FileReader(new File("D:\\pictures\\data.txt")));
     PrintWriter printWriter = new PrintWriter(
             new FileOutputStream(new File("D:\\pictures\\data.txt")), true);
     printWriter.println("hhx shi yi zhi da z1111111111111111111hu");
     ```

4. 缓冲输入文件：用被BufferedReader包装的FileReader

   - 示例：

     ```java
     public static void bufferedIn(String fileName) throws IOException {
         BufferedReader reader = new BufferedReader(new FileReader(fileName));
         System.out.println(reader.readLine());
         reader.close();
     }
     ```

5. 从内存输入：用StringReader包装字符串。

   - 示例：

     ```java
     public static void bufferedInMem(String s) throws IOException {
         StringReader in = new StringReader(s);
         int c;
         while ((c = in.read()) != -1) {
             System.out.print((char) c);
         }
         in.close();
     }
     ```

6. 格式化的内存输入：

   - 示例：用BufferedInputStream包装FileInputStream输入字节流。

     ```java
     public static void bufferedInForm(String fileName) throws IOException {
         DataInputStream in = new DataInputStream(
                 new BufferedInputStream(new FileInputStream(fileName)));
         //因为是字节流，所以无法检测是否输入结果，得通过available()方法检查
         //也就是在没有阻塞的情况下所能读取的字节数。对于文件，意味着整个文件。
         while (in.available() != 0) {
             System.out.print((char)in.readByte());
         }
         in.close();
     }
     ```

   - 示例：用ByteArrayInputStream包装字节数组。

     ```java
     public static void bufferedInForm1(String s) throws IOException {
         DataInputStream in = new DataInputStream(
                 new ByteArrayInputStream(s.getBytes()));
         //因为是字节流，所以无法检测是否输入结果，得通过available()方法检查
         while (in.available() != 0) {
             System.out.print((char)in.readByte());
         }
         in.close();
     }
     ```

7. 基本文件输出

   - 示例：用BufferedWriter包装的PrintWriter

     ```java
     public static void bufferedOut(String fileName) throws IOException {
         PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
         out.println("this is a output");
         out.close();
     }
     ```

   - 示例：带文件名的PrintWriter构造器

     ```java
     PrintWriter out = new PrintWriter(fileName);
     
     //内部实现
     this(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName))),
                  false);
     ```

8. 存储和恢复数据

   - 示例：DataOutputStream和DataInputStream用Buffered和File流包装。可以读中文，因为是用的UTF-8。

     ```java
     public static void saveAndReset(String fileName) throws IOException {
         DataOutputStream out = new DataOutputStream(
                 new BufferedOutputStream(new FileOutputStream(fileName)));
         out.writeUTF("this is dos dis ");
         out.flush();
     
         DataInputStream in = new DataInputStream(
                 new BufferedInputStream(new FileInputStream(fileName)));
         System.out.println(in.readUTF());
         out.close();
         in.close();
     }
     ```

9. 读写随机访问文件

   - 示例：

     ```java
     public static void rom(String fileName) throws IOException {
         RandomAccessFile rf = new RandomAccessFile(fileName, "rw");
         System.out.println(rf.readUTF());
         rf.writeUTF("hhx xhsf ");
         rf.seek(20);
         rf.writeUTF("xhsf love hhx ");
         rf.close();
     }
     ```

10. 读取二进制文件

    - 示例：

      ```java
      public static void bytRead(String fileName) throws IOException {
          BufferedInputStream bf = new BufferedInputStream(new FileInputStream(fileName));
          //bf.available()获取可以读取长度
          byte[] data = new byte[bf.available()];
          int a = bf.read(data);
          System.out.println(Arrays.toString(data));
      }
      ```

11. 标准IO

    - 从标准输入中读取：用InputStreamReader把System.in转换成Reader再用BufferedReader包装。

      ```java
      public static void sysin() throws IOException {
          BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
          String s = stdin.readLine();
          System.out.println(s);
      }
      ```

    - 示例：将System.out是OutputStream包装成PrintWriter。第二个参数设置为true进行自动flush。

      ```java
      public static void sysout() throws IOException {
          PrintWriter stdout = new PrintWriter(System.out, true);
          stdout.println("hhhhhhhhh");
      }
      ```

    - 示例：将输入/输出流重定向到一个文件

      ```java
      public static void cdx(String fileName) throws IOException {
          BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));
          System.setIn(in);
      
          BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
          System.out.println(br.readLine());
      }
      
      public static void cdx(String fileName) throws IOException {
              PrintStream out = new PrintStream(
                      new BufferedOutputStream(new FileOutputStream(fileName)));
              System.setOut(out);
      
              PrintWriter pw = new PrintWriter(System.out);
              pw.println("ddddddddddddddddddddd");
              pw.close();
      }
      ```

12. 新IO

    - 旧的IO已经使用nio重新实现过，因此即使不显示的使用nio编写代码，也可以从nio中受益。

    - 唯一直接与通道交互的缓冲器是ByteBuffer，它是存储未加工字节的缓冲器。

      - 通过告知需要多少存储空间来创建一个ByteBuffer对象，并且可以以原始的字节形式或基本类型输出和读取数据。一般大小在1K-8K之间。但实际上得通过测试来得到最佳尺寸。
      - allocate()方法分配ByteBuffer。可以使用allocateDirect()产生一个与操作系统更高耦合性的直接缓冲器，但是这种分配的开支会更大，得看实际情况决定是否使用。
      - 用put()方法是复制数据，用warp()方法是将已存在数组作为。
        - 使用read()之后要使用flip()告知别人可以读取数据，使用write()之后要使用clear()来为read()做好准备。
      - rewind()回到ByteBuffer的起点。
      
    - 示例：
    
      ```java
      //写入文件
      public static void writeFile() throws IOException {
          FileChannel fc =  new FileOutputStream("D:\\pictures\\data.txt").getChannel();
          fc.write(ByteBuffer.wrap("I am xhsf".getBytes()));
          fc.close();
      }
      
      //写入文件末尾
      public static void writeFileEnd() throws IOException {
          FileChannel fc =  new RandomAccessFile("D:\\pictures\\data.txt", "rw").getChannel();
          fc.position(fc.size());
          fc.write(ByteBuffer.wrap("I am xhsf".getBytes()));
          fc.close();
      }
      
      //读文件
      public static void readFile() throws IOException {
          FileChannel fc =  new FileInputStream("D:\\pictures\\data.txt").getChannel();
          ByteBuffer buff = ByteBuffer.allocate(1024);
          fc.read(buff);
          buff.flip();
      
          while (buff.hasRemaining()) {
              System.out.print((char)buff.get());
          }
      
      fc.close();
      }
      ```
    
    - 示例：把一个文件里的数据通过管道写到另外一个文件里
    
      ```java
      public static void readAndWriteFile() throws IOException {
          FileChannel in =  new FileInputStream("D:\\pictures\\data.txt").getChannel();
          FileChannel out =  new 4567FileOutputStream("D:\\pictures\\datacp.txt").getChannel();
          ByteBuffer buff = ByteBuffer.allocate(1024);
          // -1表示到文件末尾
          while (in.read(buff) != -1) {
              buff.flip();
              out.write(buff);
              buff.clear();
          }
          in.close();
          out.close();
      }
      ```
    
    - 示例：通过transferTo()和transferFrom()直接输入输出。
    
      ```java
          public static void connectChannel() throws IOException {
              FileChannel in =  new FileInputStream("D:\\pictures\\data.txt").getChannel();
              FileChannel out =  new FileOutputStream("D:\\pictures\\datacp.txt").getChannel();
              //从输入流到输出流，输入流输出到哪里
      //        in.transferTo(0, in.size(), out);
              //从输入流到输出流，只是写法不一样，这个是表示输出流从什么输入
              out.transferFrom(in, 0, in.size());
              in.close();
              out.close();
          }
      ```
    
    - 示例：编码转换，通过asCharBuffer()可以转换成字符Buffer，但是编码可能不对。可以通过Charset工具进行字节流的编码转换。
    
      ```java
      public static void asChar() throws IOException {
          //FileChannel fc =  new FileOutputStream("D:\\pictures\\data.txt").getChannel();
          //在输入时需要进行编码，不然编码格式会错误
          //String encoding = System.getProperty("file.encoding");
          //fc.write(ByteBuffer.wrap("dddddddddddd11dd无须进".getBytes(encoding)));
          //fc.close();
      
          FileChannel fc =  new FileInputStream("D:\\pictures\\data.txt").getChannel();
          ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
          fc.read(byteBuffer);
          byteBuffer.flip();
          //将字节Buffer转换成字符Buffer
          System.out.println(byteBuffer.asCharBuffer());
          String encoding = System.getProperty("file.encoding");
          //通过Charset的方法进行解码
          System.out.println(Charset.forName(encoding).decode(byteBuffer));
          //rewind()重回开头
          byteBuffer.rewind();
          System.out.println(Charset.forName(encoding).decode(byteBuffer));
          fc.close();
      }
      ```
    
    - 示例：获取和添加基本类型。使用buff.asIntBuffer().put()添加，buff.getInt()等基本类型方法获取。例外：使用ShortBuffer的put()方法时需要进行类型转换put((short) 1111222)。
    
      ```java
      public static void baseType() throws IOException {
          ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
          byteBuffer.asIntBuffer().put(333);
          System.out.println(byteBuffer.getInt());
      }
      ```
    
    - 视图缓冲器：IntBufffer等视图缓冲器实际上数据存储的地方还是ByteBuffer，所以对IntBuffer等的修改最终还是会修改ByteBuffer。
    
      - 视图缓冲器运行成批的读取和设置基本类型值。
    
      - 更改字节存放顺序：就是高位在前还是高位在后的问题。默认高位在前。
    
        ```java
        public static void orderType() throws IOException {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.asShortBuffer().put((short) 97);
            //通过ByteBuffer.order()设置
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            System.out.println(byteBuffer.getShort());
        }
        ```
    
    - 使用Charset进行编码和解码
    
      ```java
      Charset charset = Charset.forName("UTF-8");
      CharsetEncoder encoder = charset.newEncoder();
      CharsetDecoder decoder = charset.newDecoder();
      System.out.println(decoder.decode(encoder.encode(CharBuffer.wrap("abc"))));
      ```
    
    - 使用内存映射文件：允许我们创建和修改那些因为太大而不能放入内存的文件。可以当作普通的ByteBuffer来操作。
    
      ```java
      public static void memMap() throws IOException {
          //
          MappedByteBuffer out =
                  new RandomAccessFile("D:\\pictures\\data.txt", "rw")
                          .getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 4096);
          out.put((byte) 'x');
          System.out.println(Charset.forName("UTF-8").decode(out));
      }
      ```
    
    - 内存映射文件的存取速度比旧IO流快很多。但是创建时花费时间会多一点。
    
      ```java
      public static void memMapWrite() throws IOException {
          FileChannel fc = new RandomAccessFile("D:\\pictures\\data.txt", "rw").getChannel();
          IntBuffer ib = fc.map(FileChannel.MapMode.READ_WRITE, 0, 111).asIntBuffer();
          System.out.println(ib.get());
          System.out.println(ib.get());
          fc.close();
      }
      ```
    
13. 文件加锁

    - 文件加锁：可以部分加锁，也可以整个文件加锁。

      ```
       public static void lock1() throws IOException {
           FileOutputStream fos = new FileOutputStream("D:\\pictures\\data.txt");
           FileLock fl = fos.getChannel().tryLock();
           if (fl != null) {
               fos.write("wwww".getBytes());
               fl.release();
           }
           fos.close();
       }
      ```

    - 对映射文件部分加锁：

      ```
       public static void lockMap() throws IOException {
           FileChannel fc = new RandomAccessFile("D:\\pictures\\data.txt", "rw").getChannel();
           MappedByteBuffer out = fc.map(FileChannel.MapMode.READ_WRITE, 0, 1024);
           //只能在通道上加锁，不能再ByteBuffer上加锁。
           FileLock fl = fc.lock(0, 5, false);
           out.put("aaaaa".getBytes());
           fl.release();
           fc.close();
       }
      ```

14. 压缩

    - gzip压缩解压缩

      ```java
      public static void nogzipTest() throws IOException {
          BufferedOutputStream out = new BufferedOutputStream(
                  new FileOutputStream("D:\\pictures\\data.txt"));
          out.write("av cd ss dsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss".getBytes());
          out.close();
      }
      
      public static void gzipTest() throws IOException {
          BufferedOutputStream out = new BufferedOutputStream(
                  new GZIPOutputStream(new FileOutputStream("D:\\pictures\\data.txt")));
          out.write("av cd ss dsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss".getBytes());
          out.close();
      }
      
      public static void gzipTest11() throws IOException {
          BufferedReader in = new BufferedReader(
                  new InputStreamReader(new GZIPInputStream(new FileInputStream("D:\\pictures\\data.txt"))));
          System.out.println(in.readLine());
          in.close();
      }
      ```

15. 对象序列化：将实现了Serializable接口的对象转换成一个字节序列，并能够在以后将这个字节序列完全恢复为原理的对象。

    - 序列化操作：

      ```java
      //序列化，通过ObjectOutputStream的writeObject()
      public static void storageObj() throws IOException {
          Student student = new Student();
          student.setAge(6);
          student.setName("xhsf");
          ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("D:\\pictures\\student.txt"));
          out.writeObject("Student storage");
          out.writeObject(student);
          out.close();
      }
      
      //反序列化，通过ObjectInputStream的readObject()
      public static void decode() throws IOException, ClassNotFoundException {
          ObjectInputStream in = new ObjectInputStream(new FileInputStream("D:\\pictures\\student.txt"));
          String s = (String) in.readObject();
          Student student = (Student) in.readObject();
          System.out.println(s);
          System.out.println(student);
          in.close();
      }
      ```

    - 带控制的序列化：实现Externalizable接口

      ```java
      public static void controlSerialize() throws IOException {
          Student student = new Student();
          student.setAge(6);
          student.setName("xhsf");
          ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("D:\\pictures\\student.txt"));
          out.writeObject(student);
          out.close();
      }
      
      public static void controlDeSerialize() throws IOException, ClassNotFoundException {
          ObjectInputStream in = new ObjectInputStream(new FileInputStream("D:\\pictures\\student.txt"));
          Student student = (Student) in.readObject();
          System.out.println(student);
          in.close();
      }
      
      //此类必须有默认构造器
      public class Student implements Externalizable{
          private String name;
          private int age;
      
          @Override
          public void writeExternal(ObjectOutput out) throws IOException {
              out.writeObject(name);
          }
      
          @Override
          public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
              name = (String) in.readObject();
              age = (int) (Math.random() * 10);
          }
      }
      ```

    - transient：此字段不会被序列化。

    - 可以在实现Serializable接口后，添加准确方法签名的writeObject和readObject方法，这样序列化和反序列化的时候，就会自动分别调用这两个方法，而不是默认的序列化机制。

    - 如果从同一个对象流中序列化同一个对象两次，那么从同一个流中反序列化出来的也是同一个对象的引用。如果不同流的话就不是同一个对象引用。

      ```java
      public static void storageObj() throws IOException {
          Student student = new Student();
          student.setAge(6);
          ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("D:\\pictures\\student.txt"));
          out.writeObject(student);
          out.writeObject(student);
          out.close();
      }
      
      public static void decode() throws IOException, ClassNotFoundException {
          ObjectInputStream in = new ObjectInputStream(new FileInputStream("D:\\pictures\\student.txt"));
          Student student1 = (Student) in.readObject();
          Student student2 = (Student) in.readObject();
          System.out.println(student1);
          System.out.println(student2);
          in.close();
      }
      ```

    - 静态成员变量处理：通过静态serializeStaticState和deserializeStaticState方法显式调用来序列化和反序列化。

      ```java
      public static void storageObj() throws IOException {
          Student student = new Student();
          student.setAge(6);
          Student.setId(10);
          ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("D:\\pictures\\student.txt"));
          out.writeObject(student);
          Student.serializeStaticState(out);
          out.close();
      }
      
      public static void decode() throws IOException, ClassNotFoundException {
          ObjectInputStream in = new ObjectInputStream(new FileInputStream("D:\\pictures\\student.txt"));
          Student student1 = (Student) in.readObject();
          Student.deserializeStaticState(in);
          System.out.println(student1);
          in.close();
      }
      
      //序列化方法
      public static void serializeStaticState(ObjectOutputStream os) throws IOException {
          os.writeInt(id);
      }
      
      //反序列化方法
      public static void deserializeStaticState(ObjectInputStream os) throws IOException {
          setId(os.readInt());
      }
      ```

16. XML序列化

17. Preferences：是要用键对值来存储数据。它会找合适的系统资源来完成这个任务，如windows的注册表。

    - 示例：

      ```java
      public static void preferencesTest() throws IOException {
          Preferences prefs = Preferences.userNodeForPackage(SerializeTest.class);
          prefs.put("xhsf", "chaoshuai");
          prefs.put("hhx", "chaomei");
      }
      
      public static void preferencesTest1() throws IOException, BackingStoreException {
          Preferences prefs = Preferences.userNodeForPackage(SerializeTest.class);
          for (String k : prefs.keys()) {
              System.out.println(prefs.get(k, null));
          }
      }
      ```

# 19、枚举类型

1. 基本操作

   ```java
   System.out.println(Arrays.toString(Num.values()));
   System.out.println(Num.valueOf("ONE"));
   System.out.println(Num.valueOf(Num.class, "ONE"));
   //返回enum实例数组
   for (Num n : Num.values()) {
       //返回声明次序
       System.out.println(n.ordinal());
       System.out.println(n.name());
       //可用==比较
       System.out.println(n == Num.ONE);
       System.out.println(n.equals(Num.ONE));
       System.out.println(n.hashCode());
       //返回按声明次序的间隔
       System.out.println(n.compareTo(Num.ONE));
       System.out.println(n.compareTo(Num.TWO));
       //知道所属Enum类
       System.out.println(n.getDeclaringClass());
       System.out.println(n.toString());
   }
   ```

2. 可用通过Class的getEnumConstants()方法获得所有enum实例

   - 示例：即使是Num的父类Enum，也可以获得所有实例。

     ```java
     Enum<Num> one = Num.ONE;
     for (Enum numEnum : one.getClass().getEnumConstants()) {
         System.out.println(numEnum);
     }
     ```

3. 所有enum都继承自Enum类。

4. 枚举的组织方式

   - 枚举
   - 接口里的枚举
   - 枚举里的(接口里的枚举)：这里的接口可用放在枚举类里，也可以单独作为一个类。

5. EnumSet代替标志：可用灵活的操作Enum，性能也很快。

   - 示例：

     ```java
     EnumSet<Num> nums = EnumSet.noneOf(Num.class);
     nums.add(Num.FOUR);
     System.out.println(nums);
     //添加of()参数里的元素
     nums.addAll(EnumSet.of(Num.ONE, Num.TWO));
     System.out.println(nums);
     //用enum类的所有元素组装一个EnumSet
     System.out.println(EnumSet.allOf(Num.class));
     //获得从from到to的元素
     System.out.println(EnumSet.range(Num.ONE, Num.THREE));
     //获得补集
     System.out.println(EnumSet.complementOf(nums));
     //移除元素
     System.out.println(nums.remove(Num.ONE));
     System.out.println(nums);
     ```

   - EnumSet基础是long，一个enum实例需要一位bit表示是否存在，也就是一个long最多表达64个enum。如果超过64个会再增加一个long。

6. EnumMap：键为enum的Map。内部由数组实现，所以EnumMap的速度很快。

   - 示例：

     ```java
     EnumMap<Num, String> enumMap = new EnumMap<>(Num.class);
     enumMap.put(Num.ONE, "hhx");
     enumMap.put(Num.THREE, "wjx");
     System.out.println(enumMap);
     System.out.println(enumMap.get(Num.ONE));
     //返回null，因为num.TWO没赋值
     System.out.println(enumMap.get(Num.TWO));
     ```

7. 常量相关的方法：为每个enum实例编写方法。

   - 示例：

     ```java
     enum Num {
         ONE(1) {
             @Override
             String getInfo() {
                 return "one";
             }
         },
         TWO(4) {
             @Override
             String getInfo() {
                 return "two";
             }
             //覆盖普通方法
             @Override
             public int getId() {
                 return 222;
             }
         },
         THREE(9) {
             @Override
             String getInfo() {
                 return "three";
             }
         },
         FOUR(16) {
             @Override
             String getInfo() {
                 return "four";
             }
         };
     
         private int id;
     
         Num(int id) {
             this.id = id;
         }
     
         abstract String getInfo();
     
         public int getId() {
             return id;
         }
     }
     ```

   - 甚至可用覆盖除abstract之外的方法。见上例。

   - 可用使用enum的常量相关的方法实现职责链。
   
   - enum实现状态机。
   
   - 多路分发
   
     - 使用interface+实现类实现。每个实现类是一个通路。Java自动匹配类型。
   
     - 使用enum分发：第一次分发是方法的调用，第二次分发是switch。
   
       - 示例：通过对每个enum用构造器并且以一组结果作为参数。形成类似查询表的结构。
   
         ```java
         jenum Outcome {
             WIN, LOSE, DRAW
         }
         
         interface Competitor<T extends Competitor<T>> {
             Outcome compete(T competitor);
         }
         
         
         public enum RoShamBo implements Competitor<RoShamBo>{
             PAPER(Outcome.DRAW, Outcome.LOSE, Outcome.WIN),
             SCISSORS(Outcome.WIN, Outcome.DRAW, Outcome.LOSE),
             ROCK(Outcome.LOSE, Outcome.WIN, Outcome.DRAW);
         
             private Outcome vPAPER;
             private Outcome vSCISSORS;
             private Outcome vROCK;
         
             RoShamBo(Outcome vPAPER, Outcome vSCISSORS, Outcome vROCK) {
                 this.vPAPER = vPAPER;
                 this.vSCISSORS = vSCISSORS;
                 this.vROCK = vROCK;
             }
         
             @Override
             public Outcome compete(RoShamBo it) {
                 switch (it) {
                     default:
                     case PAPER: return vPAPER;
                     case SCISSORS: return vSCISSORS;
                     case ROCK: return vROCK;
                 }
             }
         
             public static void main(String[] args) {
                 System.out.println(RoShamBo.PAPER.compete(RoShamBo.SCISSORS));
             }
         }
         ```
   
     - 使用常量相关方法分发：很清晰。
   
       - 示例：
   
         ```java
         enum Outcome {
             WIN, LOSE, DRAW
         }
         interface Competitor<T extends Competitor<T>> {
             Outcome compete(T competitor);
         }
         public enum RoShamBo1 implements Competitor<RoShamBo1>{
             PAPER {
                 @Override
                 public Outcome compete(RoShamBo1 it) {
                     switch (it) {
                         default:
                         case PAPER: return Outcome.DRAW;
                         case SCISSORS: return Outcome.LOSE;
                         case ROCK: return Outcome.WIN;
                     }
                 }
             },
             SCISSORS {
                 @Override
                 public Outcome compete(RoShamBo1 it) {
                     switch (it) {
                         default:
                         case PAPER: return Outcome.WIN;
                         case SCISSORS: return Outcome.DRAW;
                         case ROCK: return Outcome.LOSE;
                     }
                 }
             },
             ROCK {
                 @Override
                 public Outcome compete(RoShamBo1 it) {
                     switch (it) {
                         default:
                         case PAPER: return Outcome.LOSE;
                         case SCISSORS: return Outcome.WIN;
                         case ROCK: return Outcome.DRAW;
                     }
                 }
             };
         
             public abstract Outcome compete(RoShamBo1 it);
         
             public static void main(String[] args) {
                 System.out.println(RoShamBo1.PAPER.compete(RoShamBo1.ROCK));
             }
         }
         ```
   
     - 使用EnumMap分发：也很清晰，且性能很好。
   
       - 示例：
   
         ```java
         enum Outcome {
             WIN, LOSE, DRAW
         }
         interface Competitor<T extends Competitor<T>> {
             Outcome compete(T competitor);
         }
         
         public enum RoShamBo2 implements Competitor<RoShamBo2>{
             PAPER,
             SCISSORS,
             ROCK;
         
             static EnumMap<RoShamBo2, EnumMap<RoShamBo2, Outcome>> table = new EnumMap<>(RoShamBo2.class);
         
             static {
                 for (RoShamBo2 it : RoShamBo2.values()) {
                     table.put(it, new EnumMap<>(RoShamBo2.class));
                 }
                 initRow(PAPER, Outcome.DRAW, Outcome.LOSE, Outcome.WIN);
                 initRow(SCISSORS, Outcome.WIN, Outcome.DRAW, Outcome.LOSE);
                 initRow(ROCK, Outcome.LOSE, Outcome.WIN, Outcome.DRAW);
             }
         
             static void initRow(RoShamBo2 it, Outcome vPAPER, Outcome vSCISSORS, Outcome vROCK) {
                 EnumMap<RoShamBo2, Outcome> row = RoShamBo2.table.get(it);
                 row.put(PAPER, vPAPER);
                 row.put(SCISSORS, vSCISSORS);
                 row.put(ROCK, vROCK);
             }
         
             public Outcome compete(RoShamBo2 it) {
                 return table.get(this).get(it);
             }
         
             public static void main(String[] args) {
                 System.out.println(RoShamBo2.PAPER.compete(RoShamBo2.PAPER));
             }
         }
         ```
   
     - 使用二维数组进行分发：最快，最清晰，最直接。
   
       - 示例：
   
         ```java
         enum Outcome {
             WIN, LOSE, DRAW
         }
         interface Competitor<T extends Competitor<T>> {
             Outcome compete(T competitor);
         }
         
         public enum RoShamBo3 implements Competitor<RoShamBo3>{
             PAPER,
             SCISSORS,
             ROCK;
         
             private static Outcome[][] table = {
                     {Outcome.DRAW, Outcome.LOSE, Outcome.WIN},
                     {Outcome.WIN, Outcome.DRAW, Outcome.LOSE},
                     {Outcome.LOSE, Outcome.WIN, Outcome.DRAW}
             };
         
         
             public Outcome compete(RoShamBo3 it) {
                 return table[this.ordinal()][it.ordinal()];
             }
         
             public static void main(String[] args) {
                 System.out.println(RoShamBo3.PAPER.compete(RoShamBo3.SCISSORS));
             }
         }
         ```

# 20、注解

# 21、并发

1. 基本操作

   - Thread.yield()把此线程占的CPU转移给另外一个线程。

   - 每个Thread都注册了自己，在start()调用完成后，它继续存在，直到退出run()并死亡，垃圾回收器才可用清除它。

   - sleep()会使线程休眠一定时间。

   - Thread.setDaemon()方法，将线程设置为后台线程。

     - 示例：非后台线程不会等待daemon线程执行结束。

       ```java
       Thread thread = new Thread(() -> {
           try {
               TimeUnit.MILLISECONDS.sleep(100);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           System.out.println("dddd");
       });
       thread.setDaemon(true);
       thread.start();
       System.out.println("ddddddddddddddd");
       TimeUnit.MILLISECONDS.sleep(101);
       ```

   - isDaemon()检查线程是否是后台线程。

     - 后台线程里创建的任何线程都会成为后台线程。

       ```java
       Thread thread = new DaemonThreadFactory().newThread(() -> {
           System.out.println("ddddddddd");
           Thread thread1 = new Thread(() -> {
               System.out.println("zzzzzzzzzzzzzz");
           });
           //return true
           System.out.println("neibu" + thread1.isDaemon());
       });
       ```

   - join()方法：等待一个线程结束。可以指定超时参数。

     - 此方法可以被中段。调用interrupt()方法，join()将抛出InterruptedException。在另外一个线程调用interrupt()时，将给此线程设置一个标志，表示这个线程已经被中断。然后异常被捕获时将清除这个标志，所以在catc子句中，isInterrupted()总是false。

   - Thread.interrupted()静态方法：查看当前线程是否被中断。

2. Executor

   - 它是用来管理Thread对象的，简化并发编程。它在客户端和任务执行之间提供了一个简介层。Executor还允许管理异步任务的执行，而无需显式地管理线程的生命周期。

   - ExecutorService（具有服务生命周期的Executor）知道如何构建恰当的上下文来执行Runnable对象。

     - 示例：shutdown()方法将阻止新任务被提交给这个Executor，当前线程将继续运行在shutdown()前提交的任务。这个程序将在Executor中所有任务完成后尽快退出。

       ```java
       ExecutorService exec = Executors.newCachedThreadPool();
       //可以很容易的替换成其他Executor
       //ExecutorService exec = Executors.newFixedThreadPool(3);
       for (int i = 0; i < 5; i++) {
           exec.execute(() -> {
               for (int j = 0; j < 10; j++) {
                   System.out.println(j);
               }
           });
       }
       exec.shutdown();
       ```

     - FixedThreadPool：可以限制线程数量。可以减少为每个任务都固定地付出创建线程的开销。

     - CachedThreadPool：创建与所需数量相同的线程，然后在它回收旧线程时停止创建新线程，它是合理的Executor首选。

     - SingleThreadExecutor：只有一个线程的FixedThreadPool。多个任务会排序按顺序进行。

     - 示例：使用Callable产生有返回值的任务。isDone()查询Future是否完成。get()获取结果。

       ```java
       public static void callableTest() throws ExecutionException, InterruptedException {
           ExecutorService exec = Executors.newSingleThreadExecutor();
           ArrayList<Future<Integer>> arrayList = new ArrayList<>();
           for (int i = 0; i < 5; i++) {
               //用submit()方法提交任务，产生Future对象
               arrayList.add(exec.submit(() -> {
                   int a = 0;
                   for (int j = 0; j < 10; j++) {
                       a += j;
                   }
                   return a;
               }));
           }
           for (Future<Integer> f : arrayList) {
               System.out.println(f.get());
           }
           exec.shutdown();
       }
       ```

   - 在调用shutdown()方法后，可以调用awaitTermination()方法，等待任务完成，如果任务在超时时间前完成，则返回true，否则返回false。

   - shutdownNow()则会发送interrupt()给所有它启动的线程。

   - 使用Future可以灵活的中断某个线程。

     - 示例：

       ```java
       public void test4() throws InterruptedException {
           ExecutorService executorService = Executors.newCachedThreadPool();
           ArrayList<Future<?>> futures = new ArrayList<>(8);
           for (int i = 0; i < 5; i++) {
               int k = i;
               //保存任务
               futures.add(executorService.submit(() -> {
                   try {
                       TimeUnit.SECONDS.sleep(2);
                       System.out.println(k);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }));
           }
           //关闭任务所在的线程
           futures.get(2).cancel(true);
           executorService.shutdown();
       }
       ```

   - 被阻塞的nio通道会自动地响应中断。

3. ThreadFactory：用来产生Thread，可以把它给ExecutorService

   - 示例：

     ```java
     //工厂类
     public class DaemonThreadFactory implements ThreadFactory{
         @Override
         public Thread newThread(Runnable r) {
             Thread t = new Thread(r);
             t.setDaemon(true);
             return t;
         }
     }
     
     //使用
     public static void threadFactory() throws InterruptedException {
         ExecutorService exec = Executors.newCachedThreadPool(new DaemonThreadFactory());
         for (int i = 0; i < 10; i++) {
             exec.execute(() -> {
                 System.out.println("this is a thread");
             });
         }
         TimeUnit.MILLISECONDS.sleep(1);
     }
     ```

4. 异常处理：通过Thread.UncaughtExceptionHadler接口，新建一个异常处理器。在线程因未捕获异常而临近死亡时，Thread.UncaughtExceptionHadler.uncaughtException()将会被调用。

   - 一般用于Executor里的线程，因为Executor无法自己捕获和处理线程，所以需要异常处理器。

   - 示例：使用ThreadFactory创建实现了Thread.UncaughtExceptionHadler的线程。

     ```java
     //把会产生带异常处理器的线程的工厂给Executor
     ExecutorService executorService = Executors.newCachedThreadPool(new HandlerThreadFactory());
     //把任务给Executor
     executorService.execute(() -> {
         System.out.println("this is a thread");
         throw new NullPointerException("lallala");
     });
     ```

   - 也可以为所有线程设置默认的异常处理器。当然优先级低于线程专有的异常处理器。

     ```java
     Thread.setDefaultUncaughtExceptionHandler((t1, e) -> System.out.println("catch " + e));
     ExecutorService executorService = Executors.newCachedThreadPool();
     executorService.execute(() -> {
         System.out.println("this is a thread");
         throw new NullPointerException("lallala");
     });
     executorService.shutdown();
     ```

5. 共享受限资源

   - 除long和double外，基本类型的赋值和返回值这样的操作是原子的。long和double添加volatile关键字后也是原子的。

   - ThreadLocal：使每个不同线程都创建不同存储。如有5个线程都要用x变量，那线程本地存储旧会生成5个用于x的不同存储快。可以使得将状态与线程关联起来。

     - 示例：

       ```java
       class ThreadLocalVariableHolder {
           private static ThreadLocal<Integer> value = new ThreadLocal<Integer>() {
               @Override
               protected synchronized Integer initialValue() {
                   return (int)(Math.random() * 10000);
               }
           };
           public static void increment() {
               value.set(value.get() + 1);
           }
           public static int get() {
               return value.get();
           }
       }
       ```

     - 创建ThreadLocal时，只能通过get和set方法来访问该对象的内容。都只是获取和删除当前线程的内容。

     - ThreadLocal确保不会出现竞争条件，所以不需要同步。

   - 在其他对象上同步。可以使用private Object syncObject = new Object(); 如果锁不够的话。这样可以保证不同的方法都有独立的锁。

   - synchronized关键字不属于方法特征签名的组成部分，可以在覆盖方法的时候加上去。

   - RentratLock上阻塞的任务具备可以被中断的能力。通过调用ReentrantLock.lockInterruptibly()方法。

     - 示例：本来此类的f()方法无法获取锁，所以f()方法无法退出和被中断。但是通过lockInterruptibly()使该线程可以被interrupt()中断。

       ```java
       public class BlockedMutex {
           private Lock lock = new ReentrantLock();
           public BlockedMutex() {
               lock.lock();
           }
       
           public void f() throws InterruptedException {
               lock.lockInterruptibly();
               System.out.println("ddddddddddddzzzzzzzzzzz");
           }
       
       }
       
       public void test6() throws InterruptedException, FileNotFoundException {
           BlockedMutex blockedMutex = new BlockedMutex();
           Thread t = new Thread(() -> {
               try {
                   blockedMutex.f();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           });
           t.start();
           t.interrupt();
       }
       ```

6. 协作

   - 示例：涂蜡和打磨，交替进行。通过车进行同步。

     ```java
     public class Car {
     
         public static void main(String[] args) throws InterruptedException {
             Car car = new Car();
             ExecutorService executorService = Executors.newCachedThreadPool();
             executorService.execute(new WaxOn(car));
             executorService.execute(new WaxOff(car));
             TimeUnit.SECONDS.sleep(5);
             executorService.shutdownNow();
         }
     
         private boolean waxOn = false;
         public synchronized void waxed() {
             waxOn = true;
             notifyAll();
         }
     
         public synchronized void buffed() {
             waxOn = false;
             notifyAll();
         }
     
         public synchronized void waitForWaxing() throws InterruptedException {
             while (!waxOn) {
                 wait();
             }
         }
     
         public synchronized void waitForBuffing() throws InterruptedException {
             while (waxOn) {
                 wait();
             }
         }
     
     }
     
     class WaxOn implements Runnable {
     
         private Car car;
         public WaxOn(Car car) {
             this.car = car;
         }
     
         @Override
         public void run() {
             try {
                 while (!Thread.interrupted()) {
                     System.out.println("Wax On!");
                     TimeUnit.MILLISECONDS.sleep(200);
                     car.waxed();
                     car.waitForBuffing();
                 }
             } catch (InterruptedException e) {
                 System.out.println("Wax On interrupt");
             }
             System.out.println("Ending Wax On task");
         }
     }
     
     class WaxOff implements Runnable {
     
         private Car car;
         public WaxOff(Car car) {
             this.car = car;
         }
     
         @Override
         public void run() {
             try {
                 while (!Thread.interrupted()) {
                     car.waitForWaxing();
                     System.out.println("Wax Off!");
                     TimeUnit.MILLISECONDS.sleep(200);
                     car.buffed();
                 }
             } catch (InterruptedException e) {
                 System.out.println("Wax Off interrupt");
             }
             System.out.println("Ending Wax Off task");
         }
     }
     ```

   - 使用Lock和Condition进行协作：在复杂的多线程问题中才是必须的。

     - 示例：代码比不使用Lock和Condition更加复杂。

       ```java
       public class Car {
       
           public static void main(String[] args) throws InterruptedException {
               Car car = new Car();
               ExecutorService executorService = Executors.newCachedThreadPool();
               executorService.execute(new WaxOn(car));
               executorService.execute(new WaxOff(car));
               TimeUnit.SECONDS.sleep(5);
               executorService.shutdownNow();
           }
       
           private Lock lock = new ReentrantLock();
           private Condition condition = lock.newCondition();
           private boolean waxOn = false;
       
           public void waxed() {
               lock.lock();
               try {
                   waxOn = true;
                   condition.signalAll();
               } finally {
                   lock.unlock();
               }
           }
       
           public void buffed() {
               lock.lock();
               try {
                   waxOn = false;
                   condition.signalAll();
               } finally {
                   lock.unlock();
               }
           }
       
           public synchronized void waitForWaxing() throws InterruptedException {
               lock.lock();
               try {
                   while (!waxOn) {
                       condition.await();
                   }
               } finally {
                   lock.unlock();
               }
           }
       
           public synchronized void waitForBuffing() throws InterruptedException {
               lock.lock();
               try {
                   while (waxOn) {
                       condition.await();
                   }
               } finally {
                   lock.unlock();
               }
           }
       
       }
       
       class WaxOn implements Runnable {
       
           private Car car;
           public WaxOn(Car car) {
               this.car = car;
           }
       
           @Override
           public void run() {
               try {
                   while (!Thread.interrupted()) {
                       System.out.println("Wax On!");
                       TimeUnit.MILLISECONDS.sleep(200);
                       car.waxed();
                       car.waitForBuffing();
                   }
               } catch (InterruptedException e) {
                   System.out.println("Wax On interrupt");
               }
               System.out.println("Ending Wax On task");
           }
       }
       
       class WaxOff implements Runnable {
       
           private Car car;
           public WaxOff(Car car) {
               this.car = car;
           }
       
           @Override
           public void run() {
               try {
                   while (!Thread.interrupted()) {
                       car.waitForWaxing();
                       System.out.println("Wax Off!");
                       TimeUnit.MILLISECONDS.sleep(200);
                       car.buffed();
                   }
               } catch (InterruptedException e) {
                   System.out.println("Wax Off interrupt");
               }
               System.out.println("Ending Wax Off task");
           }
       }
       ```

   - 使用生产者消费者队列。

   - 任务间使用管道进行输入/输出。基本上就是一个阻塞队列。

7. CountDownLatch：它被用来同步一个或多个任务，强制它们等待由其他任务执行的一组操作完成。

   - CountDownLatch可以设置一个初始计数值，其他任务在这个对象上调用wait()方法都阻塞，直到这个计数值为0。其他任务在结束工作时，可以在该对象上调用countDown()来减小这个计数值。

   - 调用countDown()并不会产生阻塞，只有调用await()才会阻塞，直到计数值为0。

   - CountDownLatch只能触发一次，计数值不能重置。如需重置，可用CyclicBarrier。

   - 示例：

     ```java
     //任务
     class TaskPortion implements Runnable {
     
         private static int counter = 0;
         private final int id = counter++;
         private static Random random = new Random(47);
         private final CountDownLatch latch;
     
         TaskPortion(CountDownLatch latch) {
             this.latch = latch;
         }
     
         @Override
         public void run() {
          try {
              TimeUnit.MILLISECONDS.sleep(random.nextInt(200));
              System.out.println(this + "completed");
              latch.countDown();
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
         }
     
         @Override
         public String toString() {
             return "TaskPortion{" +
                     "id=" + id +
                     ", latch=" + latch +
                     '}';
         }
     }
     
     //需要等待第一个任务满足此任务的条件
     class WaitingTask implements Runnable {
     
         private static int counter = 0;
         private final int id = counter++;
         private final CountDownLatch latch;
     
         WaitingTask(CountDownLatch latch) {
             this.latch = latch;
         }
     
         @Override
         public void run() {
             try {
                 latch.await();
                 System.out.println("Latch barrier passed for " + this);
             } catch (InterruptedException e) {
                 System.out.println(this + " interrupted");
             }
         }
     
         @Override
         public String toString() {
             return "TaskPortion{" +
                     "id=" + id +
                     ", latch=" + latch +
                     '}';
         }
     }
     
     public class CountDowLatchTest {
         public static void main(String[] args) {
             ExecutorService executorService = Executors.newCachedThreadPool();
             CountDownLatch latch = new CountDownLatch(100);
             for (int i = 0; i < 10; i++) {
                 executorService.execute(new WaitingTask(latch));
             }
             for (int i = 0; i < 100; i++) {
                 executorService.execute(new TaskPortion(latch));
             }
             System.out.println("all tasks");
             executorService.shutdown();
         }
     }
     ```

8. CyclicBarrier：适用于希望创建一组任务，它们并行地执行，然后进行下一个步骤之前等待，直到所有任务完成。也就是使得所有并行任务都在栅栏处列队。

   - 可以为CyclicBarrier设置一个栅栏动作，会在计数值为0时自动执行。

   - 示例：赛马

     ```java
     class Horse implements Runnable {
     
         private static int counter = 0;
         private final int id = counter++;
         private int strides = 0;
         private static Random random = new Random(47);
         private static CyclicBarrier barrier;
     
         public Horse(CyclicBarrier barrier) {
             Horse.barrier = barrier;
         }
     
         @Override
         public void run() {
             try {
                 while (!Thread.interrupted()) {
                     synchronized (this) {
                         strides += random.nextInt(3);
                     }
                     barrier.await();
                 }
             } catch (InterruptedException e) {
                 e.printStackTrace();
             } catch (BrokenBarrierException e) {
                 e.printStackTrace();
                 throw new RuntimeException(e);
             }
         }
     
         public synchronized int getStrides() {
             return this.strides;
         }
     
         @Override
         public String toString() {
             return "Horse " + id + " ";
         }
     
         public String tracks() {
             StringBuilder s = new StringBuilder();
             for (int i = 0; i < getStrides(); i++) {
                 s.append("*");
             }
             s.append(id);
             return s.toString();
         }
     
     }
     
     public class HorseRace {
     
         static final int FINISH_LINE = 75;
         private List<Horse> horses = new ArrayList<>();
         private ExecutorService exec = Executors.newCachedThreadPool();
         private CyclicBarrier barrier;
     
         public HorseRace(int nHorses, final int pause) {
             barrier = new CyclicBarrier(nHorses, () -> {
                 StringBuilder s = new StringBuilder();
                 for (int i = 0; i < FINISH_LINE; i++) {
                     s.append("=");
                 }
                 System.out.println(s);
                 for (Horse horse : horses) {
                     System.out.println(horse.tracks());
                 }
                 for (Horse horse : horses) {
                     if (horse.getStrides() >= FINISH_LINE) {
                         System.out.println(horse + "won!");
                         exec.shutdownNow();
                         return;
                     }
                 }
                 try {
                     TimeUnit.MILLISECONDS.sleep(pause);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             });
             for (int i = 0; i < nHorses; i++) {
                 Horse horse = new Horse(barrier);
                 horses.add(horse);
                 exec.execute(horse);
             }
         }
     
         public static void main(String[] args) {
             int nHorses = 7;
             int pause = 200;
             new HorseRace(nHorses, pause);
         }
     
     }
     ```

9. DelayQueue：

   - 无界的BlockingQueue，用于放置实现了Delayed接口的对象，对象只有在到期时才能被取走。
   - 对头对象的延迟到期的时间最长。如果没有任何延迟到期，就不会由任何头元素，poll()将返回null。
   - Delayed对象自身就是任务，所以一般会实现Runnable。
     - getDelay()返回延迟到期还有多少时间，或者延迟在多长时间前已经到期。这个方法需要用到TimeUnit类。
     - 此接口还继承了Comparable接口，因此必须实现compareTo()，使其产生合理的比较。

10. PriorityBlockingQueue：基础的优先级队列，具有可阻塞的读取操作。

    - 任务需要实现Runnable和Comparable接口。

11. ScheduledThreadPoolExecutor：定时或者是延迟一段时间后执行任务。

    - 示例：

      ```java
      public static void main(String[] args) {
          ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10);
          scheduledThreadPoolExecutor.schedule(
                  () -> System.out.println(Math.random()), 1000, TimeUnit.MILLISECONDS);
          scheduledThreadPoolExecutor.scheduleAtFixedRate(
                  () -> System.out.println(Math.random()), 1000, 1000, TimeUnit.MILLISECONDS);
      }
      ```

12. Semaphore：信号量，同时允许n个任务访问这个资源。可以看作是向外分发使用资源的许可证。

    - 示例：可以用于对象池，发放许可证，拿到对象，再收回许可证，交还对象。

13. Exchanger：当两个任务同时调用Exhanger.exchange()的时候，交换两个任务当前所持有的对象。

    - 可以用于生产者，消费者模式。也就是生产者生产的队列，给消费者消费，消费者消费完的队列，交给生产者填充。和正常的消费者和生产者模式只有一个队列有点区别。

14. SynchronousQueue：一种没有内部容量的阻塞队列，因此每个put()都必须等待一个take()，反之亦然。好像把一个对象交给某人，但是没有桌子可以放置这个对象。

15. 使用Lock和Atomic类正常来说比synchronized关键字性能好。

16. 免锁容器：性能一般比加锁容器好。

    - CopyOnWriteArrayList/Set/Map等。

17. 乐观加锁：某些Atomic类允许使用compareAndSet()方法避免加锁。但是可能会导致某次操作直接失败而被抛弃。

18. ReadWriteLock：对向数据结构相对不频繁地写入，但是有多个任务要经常读取这个数据结构的情况进行了优化。

    - 也就是读不加锁，写要加锁。锁比较复杂，因此短操作不能带来好处。
    - 写入者应该远少于读取者。
    - ReentraReadWriteLock：还有大量的其他方法，如设计公平性和政策性决策等问题。只有在需要高性能时才应该想到它。

