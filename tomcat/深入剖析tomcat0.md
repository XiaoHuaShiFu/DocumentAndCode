# 前言

- Servlet容器如何工作的：对每个请求，servlet容器会为其完成以下三个操作：
  - 创建一个request对象，填充参数、头、cookie、查询字符串、URI等。request是javax.servlet.ServletRequest接口或javax.servlet.http.ServletRequest接口的一个实例；
  - 创建一个response对象，用来向Web客户端发送响应。response是javax.servlet.ServletResponse接口或javax.servlet.http.ServletResponse接口的一个实例；
  - 调用Servlet的service()方法，将request和response作为参数传入。Servlet从request对象中读取信息，并通过response对象发送响应信息。
- Catalina框图
  - Catalina将servlet容器的任务划分为两个模块：连接器（connector）和容器（container）。![](https://github.com/XiaoHuaShiFu/img/blob/master/%E6%B7%B1%E5%85%A5%E5%89%96%E6%9E%90Tomcat/Catalina%E7%9A%84%E4%B8%BB%E8%A6%81%E6%A8%A1%E5%9D%97.jpg?raw=true)
  - 连接器负责将请求与容器相关联。它负责为收到的每个HTTP请求创建一个request对象和一个response对象。然后将处理过程交给容器。容器从连接器中接收到request和response对象，并负责调用相应的Servlet的service()方法。
  - 容器调用相应Servlet的service()方法之前，还要先载入该Servlet类，对用户进行身份验证，为用户更新会话信息等。
  - 容器使用不同模块来处理这些事情，如用管理器模块来处理用户会话信息，用载入器模块来载入所需的Servlet类等。

# 1、一个简单的Web服务器

1. HTTP

   - HTTP使用可靠的TCP连接，TCP默认端口80。
   - 在HTTP中，总是由客户端通过建立连接并发送HTTP请求来初始化一个事务的。Web服务器端并不负责联系客户端或建立一个到客户端的回调连接。客户端或服务器端可提前关闭连接。

   1. HTTP请求
      - 一个HTTP请求包含以下三部分：
        - 请求方法--统一资源标识符（Uniform Resource Identifier, URI）--协议/版本
        - 请求头
        - 实体
      - URI指定Internet资源的完整路径。URI通常会被解释为相对于服务器根目录的相对路径。因此总是以/开头的。
      - URL（统一资源定位符，Uniform Resource Locator）实际上是URI的一种类型。
      - 请求头包含客户端环境和请求实体正文的相关信息。
        - 如浏览器使用的语言，请求实体正文的长度等。
        - 各个请求头之间用回车/换行符间隔开。
      - 请求头与请求实体正文之间由一个空行，该空行只有CRLF符。CRLF符告诉HTTP服务器请求实体正文从哪里开始。
   2. HTTP响应
      - 一个HTTP响应包含以下三部分：
        - 协议/版本--状态码--描述
        - 响应头
        - 响应实体段
      - 响应头与响应体正文之间由一个空行，该空行只有CRLF符。

2. Socket类

   - java.net.Socket是Java客户端的套接字。

     - 可以使用Socket类的实例来发送或接收字节流。

       - 发送字节流，使用socket类的getOutputStream()将获取一个java.io.OutputStream对象。
       - 发送字符流，使用OutputStream对象创建一个java.io.PrintWriter对象。
       - 接收字节流，使用Socket类的getInputStream()方法，获取一个java.io.InputStream对象。

     - Socket类示例：

       ```java
           public void test() throws IOException, InterruptedException {
               Socket socket = new Socket("127.0.0.1", 8080);
               OutputStream os = socket.getOutputStream();
               PrintStream out = new PrintStream(os, true);
               BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
       
               //send an HTTP request to the web server
               out.println("GET /users HTTP/1.1");
               out.println("Host: localhost:8080");
               out.println("Connection: Close");
               out.println();
               out.println("name=hhx&age=11");
       
               //read the response
               StringBuilder sb = new StringBuilder(8096);
               boolean loop = true;
               while (loop) {
                   if (in.ready()) {
                       int i = 0;
                       while (i != -1) {
                           i = in.read();
                           sb.append((char) i);
                       }
                       loop = false;
                   }
                   Thread.sleep(50);
               }
       
               System.out.println(sb.toString());
               socket.close();
           }
       ```

   - java.net.ServerSocket是Java服务端的套接字。

     - 服务器套接字会等待客户端的连接请求，当服务器套接字收到了连接请求之后，它会创建一个Socket实例来处理与客户端的通信。
     - 服务器套接字的重要属性backlog，表示服务器拒绝接收传入请求之前，传入的连接请求的最大队列长度。
     - 参数绑定地址必须是java.net.InetAddress类的实例。可以通过InetAddress.getByName()获取。

# 2、一个简单的servlet容器

1. javax.servlet.Servlet接口
2. 应用程序1
   1.  
   2.  
   3.  
   4.  
   5.  
      - 使用URLClassLoader类来加载Servlet类。

# 3、连接器

1.  

2.  应用程序

   - 启动模块：Bootstrap类，负责启动程序。
   - 连接器模块：
     - 连接器及其支持类（HttpConnector和HttpProcessor）；
     - 表示HTTP请求的类（HttpRequest）及其支持类；
     - 表示HTTP响应的类（HttpResponse）及其支持类；
     - 外观类（HttpRequestFacade和HttpResponseFacade）；
     - 常量类。

   1. 启动应用程序
   2. HttpConnector类
      - 等待HTTP请求；
      - 为每个请求创建一个HttpProcessor实例；
      - 调用HttpProcessor对象的process()方法；
      - HttpProcessor类接收来自传入的HTTP请求的套接字。
        - 创建一个HttpRequest对象；
        - 创建一个HttpResponse对象；
        - 解析HTTP请求的第一行内容和请求头信息，填充HttpRequest对象；
        - 将HttpRequest对象和HttpResponse对象传递给ServletProcessor或StaticResourceProcessor的process()方法。
   3. HttpRequest对象
      - 请求头、Cookie和请求参数存储在HttpRequest的成员变量中；
      - 解析Http请求参数会在使用时才进行解析；
      - HTTP请求解析过程：
        - 读取套接字的输入流
        - 解析请求行
        - 解析请求头
        - 解析Cookie
        - 获取参数

# 4、Tomcat的默认连接器

- Tomcat使用的连接器必须满足的要求：
  - 实现org.apache.catalina.Connector接口；
  - 负责创建实现了org.apache.catalina.Request接口的request对象；
  - 负责创建实现了org.apache.catalina.Response接口的response对象。
- Tomcat连接器工作原理：
  - 等待HTTP请求，创建request和response对象，然后调用org.apache.catalina.Container接口的invoke()方法，将request对象和response对象传给servlet容器。
  - invoke()方法内，servlet容器会载入相应的servlet类，然后调用其service()方法，管理session对象，记录错误消息等。

1. HTTP 1.1的新特性

   1. 持久连接

      - 使用持久连接后，当下载了页面后，服务器并不会立即关闭连接。它会等待Web客户端请求被该页面引用的所有资源。这样，页面和被页面引用的资源都会使用同一个连接来下载。
      - HTTP1.1中是默认开启的，可以使用connection: keep-alive显式开启。

   2. 块编码

      - 接收方在不确定发送内容的长度的情况下，解析已接收到的内容。

      - HTTP1.1使用transfer-encoding请求头，来指明字节流会分块发送，对每一个块，块的长度（16进制表示）后面会有一个CRLF，然后是具体的数据。一个事务以一个长度为0的块标记。

        - 例如：

          ```
          1D\r\n
          I'm as helpless as a kitten u
          9\r\n
          p a tree.
          0\r\n
          ```

   3. 状态码100的使用

      - HTTP1.1的客户端可以在向服务器发送请求体之前发送如下请求头，并等待服务器的确认：
        - Expect: 100-continue
      - 当客户端准备发送一个较长的请求体，而不确定服务端是否会接收时，就可能会发送上面的头信息。若是客户端发送了较长的请求体，却发送服务器拒绝接收时，会是较大的浪费。
      - 接收到Expect: 100-continue请求头后，若服务器可以接收并处理该请求时，可以发送以下响应头：
        - HTTP/1.1 100 Continue
        - 加上CRLF字符
        - 然后服务器继续读取输入流内容

2. Connector接口

   - 有getContainer()、setContainer()、createRequest()、createResponse()方法。

3. HttpConnector类

   - 实现了Connector接口（成为Catalina中的连接器）、Runnable接口（实例可以在自己的线程中运行）和Lifecycle接口（该接口维护每个实现了该接口的每个Catalina组件的生命周期）。
   - 实现Lifecycle接口的类在创建实例后，一个调用其initialize()方法和start()方法。这两个方法在整个生命周期内，只应该被调用一次。

   1. 创建服务器套接字：创建ServerSocket实例，通过ServerSocketFactory。

   2. 维护HttpProcessor实例：维护一个HttpProcessor对象池。

      - HttpProcessor实例负责解析HTTP请求行和请求头，填充request对象。因此，每个HttpProcessor对象都关联一个request对象和一个response对象。

   3. 提供HTTP请求服务：HttpConnector类的主要业务逻辑在其run()方法中。获取HttpProcessor调用其assign方法。

      ```java
      HttpProcessor processor = createProcessor();
      //。。。
      processor.assign(socket);
      ```

4. HttpProcessor类

   - process()方法负责解析HTTP请求，并调用相应servlet容器的invoke方法。
   - 每个HttpProcessor类都运行在自己的线程内，称为“处理器线程”。
   - assgin()方法是HttpConnector对象的run()方法中调用的。await()方法是HttpProcessor的run()方法调用的。也就是，平时HttpProcessor()是等待状态，在收到assgin()方法之后，变成能够执行任务的状态，然后继续await()。

5. Request对象

6. Response对象

7. 处理请求：HttpProcessor类的run()方法会调用process()方法执行：解析连接、解析请求、解析请求头，调用对应容器的invoke()方法。

   - 解析连接：parseConnection()方法从套接字中获取Internet地址，将其赋值给HttpRequestImpl对象，还会检查是否使用了代理，将Socket对象赋值给request对象。
   - 解析请求：
   - 解析请求头：使用HttpHeader类和DefaultHeader类。HttpHeader类表示一个HTTP请求头。parseHeaders()方法先从allocateHeader()方法获取一个空的HttpHeader实例，然后将HttpHeader实例传入SocketInputStream实例的readHeader()方法中填充，直到readHeader()方法不再给HttpHeader实例设置name属性。

# 5、servlet容器

- servlet容器是用来处理请求servlet资源，并为Web客户端填充response对象的模块。servlet容器是org.apache.catalina.Container接口的实例。

1. Container接口：通过连接器的setContainer()方法设置到连接器，连接器调用其invoke()方法。

   - 有4种类型的servlet容器，分别对应不同的概念层次：
     - Engine：表示整个Catalina servlet引擎；
     - Host：表示包含一个或多个Context容器的虚拟主机；
     - Context：表示一个Web应用程序。一个Context可以有多个Wrapper；
     - Wrapper：表示一个独立的servlet。
   - 上述分别对应org.apache.catalina包的Engine、Host、Context、Wrapper，都继承自Container接口。标准实现分别是StandardEngine、StandardHost、StandardContext、StandardWrapper类，所有实现类都继承自抽象类ContainerBase。
   - 一个容器可以有0个或多个低级子容器。通过Container.addChild()方法添加，removeChild()移除，findChild(String name)寻找，findChildren()列出。
   - 容器可以包含一些支持的组件，如载入器、记录器、管理器、领域、资源等，Container接口提供了对应的getter和setter方法。
   - Container接口可以在部署应用时，通过配置文件server.xml来决定使用哪种容器。是通过在容器种引入管道pipeline和阀valve的集合实现的。

2.  管道任务

   - 相关接口Pipeline、Valve、ValveContext和Contained。

   - 管道包含该Servlet容器将要调用的任务。一个阀表示一个具体的执行任务。在servlet容器的管道中，有一个基础阀。可以通过编辑tomcat的server.xml来动态添加阀。

   - 管道像过滤器链，阀像过滤器。阀接收request和response对象，会链式调用，基础阀是最后一个执行的。

   - 一个servlet容器可以有一条管道。当调用容器的invoke()方法后，容器会将处理工作交由管道完成，而管道会调用其中的第一个阀开始处理，然后会继续调用下一个阀，直到管道中的所有阀都处理完成。

   - 当连接器调用容器的invoke()方法后，容器会调用管道的invoke()方法。

   - 管道会创建一个ValveContext接口的实例，实现所有阀包括基础阀都被调用一次。

     -  ValveContext接口

     - ```java
       public interface ValveContext {
           public String getInfo();
           public void invokeNext(Request request, Response response)
               throws IOException, ServletException;
       }
       ```

     - StandardPipelineValveContext类：通过subscript和stage标明当前正在调用的阀。

       ```java
           protected class StandardPipelineValveContext
               implements ValveContext {
               protected int stage = 0;
               
               public void invokeNext(Request request, Response response)
                   throws IOException, ServletException {
       
                   int subscript = stage;
                   stage = stage + 1;
       
                   // Invoke the requested Valve for the current request thread
                   if (subscript < valves.length) {
                       valves[subscript].invoke(request, response, this);
                   } else if ((subscript == valves.length) && (basic != null)) {
                       basic.invoke(request, response, this);
                   } else {
                       throw new ServletException
                           (sm.getString("standardPipeline.noValve"));
                   }
               }
           }
       ```

       

     - Valve接口：通过context.invokeNext(request, response)调用下一个阀。

       ```java
       public interface Valve {
           public String getInfo();
       
           public void invoke(Request request, Response response,
                              ValveContext context)
               throws IOException, ServletException;
       }
       ```

   1.  Pipeline接口：通过Pipeline接口的addValve()方法可以向管道中添加新的阀，调用setBasic()方法设置基础阀。
   2.  Valve接口：用来处理收到的请求。
   3.  ValveContext接口：主要是invokeNext()方法。
   4.  Contained接口：阀可以实现此接口，该接口的实现类可以通过接口中的方法与一个servlet容器相关联。

3. Wrapper接口：表示一个独立的servlet定义，主要负责基础servlet类的servlet生命周期，如调用servlet的init()、service()、destroy()等方法。无法添加子容器。

   - Wrapper接口的allocate()方法会分配一个已经初始化的servlet实例，且会考虑servlet类是否实现了SingleThreadModel接口。load()方法载入并初始化servlet类。

4. Context接口：表示一个Web引用程序。

5.  Wrapper应用程序

   1. SimpleLoader类
      - servlet类的载入工作由实现Loader接口的实例完成，它直到servlet类的位置，通过调用getClassLoader()方法可以返回一个java.lang.ClassLoader实例，可以用来搜索servlet类的位置。
   2. SimplePipeline类：包含一个内部类SimplePipelineValveContext
   3.  SimpleWrapper类：包含loader变量指明载入servlet类要使用的载入器，parent变量指明该Wrapper类的父容器。
      - 其中getLoader()方法先查看自身有没有loader，如果为null，则调用父类parent的getLoader()方法。
      - Wrapper实例有一个pipeline成员变量，并为该Pipeline实例设置基础阀。都是在SimpleWrapper类的构造函数中完成的。
   4. SimpleWrapperValve类：是一个基础阀，实现了Valve和Contained接口，专门处理SimpleWrapper类的请求。
      - 会先调用allocate()方法获取wrapper实例表示的servlet实例，然后调用servlet实例的service()方法。
   5. ClientPLoggerValve类：将客户端的IP输出到控制台上。
   6. HeaderLoggerValve类：将请求头输出到控制台上。
   7. Bootstrap1类：用于启动应用程序。
   
6. Context应用程序

   1. SImpleContextValve类：SimpleContext的基础阀。其invoke()方法会从request对象中解析除请求的上下文路径，并找到对应的wrapper实例，调用其invoke()方法。
   2. SimpleContextMapper类：map()方法解析request对象并返回Wrapper实例。
   3. SimpleContext类：每个独立servlet的请求是由Wrapper实例完成。需要context通过映射方法来选择对应的实例。
      - addServletMapping()，添加一个URL模式/Wrapper实例的名称对；
      - findServletMapping()，通过URL寻找对应的Wrapper实例名字；
      - addMapper()，在Context容器中添加一个映射器。
      - findMapper()，找到正确的映射器。
      - map()，返回负责处理当前请求的Wrapper实例。

# 6、生命周期

- 相关的类：Lifecycle、LifecycleEvent、LifecycleListener和LifecycleSupport。

1. Lifecycle接口

   - 一个组件可以包含其他组件。父组件负责启动/关闭它的子组件。这样所有的组件都置于其父组件的监护之下，这样Catalina的启动类只需要启动一个组件就可以将全部应用的组件都启动起来。这种单一启动/关闭机制是通过Lifecycle接口实现的。
   - Lifecycle有START_EVENTSTART_EVENT、BEFORE_START_EVENT、AFTER_START_EVENT、STOP_EVENT、BEFORE_STOP_EVENT、AFTER_STOP_EVENT事件。
   - Lifecycle接口的实现类必须实现start()和stop()方法，以供父组件调用。其他3个方法addLifecycleListener()、findLifecycleListeners()、removeLifecycleListener()都是与事件监听器相关的。组件可以注册多个事件监听器来对发生在该组件上的某些事件进行监听。当某个事件发生时，相应的事件监听器会收到通知。

2. LifecyleEvent类：生命周期事件

3. LifecycleListener接口：生命周期监听器。当监听到事件时，会调用lifecycleEvent()方法。

4. LifecycleSupport类：为Lifecycle接口中的3个与监听器相关的方法addLifecycleListener()、findLifecycleListeners()、removeLifecycleListener()提供了实现。可以降低Lifecycle的实现难度。

5. 应用程序

   1. SimpleContext类：start()和stop()方法的实现。

      ```java
          public synchronized void start() throws LifecycleException {
              if (started)
                  throw new LifecycleException("SimpleContext has already started");
      
              // Notify our interested LifecycleListeners
              lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
              started = true;
              try {
                  // Start our subordinate components, if any
                  if ((loader != null) && (loader instanceof Lifecycle))
                      ((Lifecycle) loader).start();
      
                  // Start our child containers, if any
                  Container children[] = findChildren();
                  for (int i = 0; i < children.length; i++) {
                      if (children[i] instanceof Lifecycle)
                          ((Lifecycle) children[i]).start();
                  }
      
                  // Start the Valves in our pipeline (including the basic),
                  // if any
                  if (pipeline instanceof Lifecycle)
                      ((Lifecycle) pipeline).start();
                  // Notify our interested LifecycleListeners
                  lifecycle.fireLifecycleEvent(START_EVENT, null);
              }
              catch (Exception e) {
                  e.printStackTrace();
              }
      
              // Notify our interested LifecycleListeners
              lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);
          }
      
          public void stop() throws LifecycleException {
              if (!started)
                  throw new LifecycleException("SimpleContext has not been started");
              // Notify our interested LifecycleListeners
              lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);
              lifecycle.fireLifecycleEvent(STOP_EVENT, null);
              started = false;
              try {
                  // Stop the Valves in our pipeline (including the basic), if any
                  if (pipeline instanceof Lifecycle) {
                      ((Lifecycle) pipeline).stop();
                  }
      
                  // Stop our child containers, if any
                  Container children[] = findChildren();
                  for (int i = 0; i < children.length; i++) {
                      if (children[i] instanceof Lifecycle)
                          ((Lifecycle) children[i]).stop();
                  }
                  if ((loader != null) && (loader instanceof Lifecycle)) {
                      ((Lifecycle) loader).stop();
                  }
              }
              catch (Exception e) {
                  e.printStackTrace();
              }
              // Notify our interested LifecycleListeners
              lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
          }
      ```

# 7、日志记录器

1. Logger接口：Tomcat中的日志记录器都必须实现Logger接口。
   - log()方法可以接收日志级别参数，只有日志级别比日志记录器中设定的等级低，才会记录该条信息；有FATAL、ERROR、WARNING、INFORMATION、DEBUG五种等级。可以通过serVerbosity()方法来设置。可以通过setContainer()设置相关联的servlet容器。addPropertyChangeListener()设置PropertyChangeListener实例。
2. Tomcat的日志记录器
   1. LoggerBase类：是一个抽象类，实现了Logger接口中除log(String msg)外的所有方法。所有的其他重载的log()方法都会调用此方法。
      - 此类默认verbosity为ERROR；
   2. SystemOutLogger类：通过System.out.println()输出日志信息。
   3. SystemErrLogger类：通过System.err.println()输出日志信息。
   4. FileLogger类：将日志信息写道一个文件中，可以指定是否为每条信息添加时间戳。该类首次被实例化时会创建一个日志文件，文件名包含当前的日期。若日期发生了编号，则会创建一个新的文件。可以指定文件的前缀和后缀。
      - 该类实现了Lifecycle接口，所以由servlet容器负责启动/关闭。stop()方法会调用close()方法。
      - 使用new Timestamp(System.currentTimeMillis())可以获取yyyy-mm-dd hh:mm:ss.fff格式的时间字符串。
3. 应用程序
   - 只要在container里设置一个Logger成员变量就可以直接使用了。

# 8、载入器 

- servlet应该只允许载入WEB-INF/classes目录下及其子目录下的类，和从部署的库到WEB-INF/lib目录载入类。所以servlet容器要实现一个自定义的载入器。载入器必须实现Loader接口。
- 当WEB-INF/lib目录下的类发生变化的时候，Web应用程序会重新载入这些类。也就是启动一个线程来不断检查servlet类和其他类的文件的时间戳。实现自动重载功能的载入器必须实现Reloader接口。
- 仓库repository和资源resource。仓库表示要在哪搜索载入的类，资源是指载入器中的DirContext对象，它的文件根路径指的就是上下文的文件根路径。

1. Java的类载入器

   - JVM使用3种类加载器来载入所需要的类，分别是引导类加载器（Bootstrap）、扩展类加载器（extension）、系统类加载器（System）。引导类加载器位于最上层，系统类加载器位于最下层。
   - ①引导类载入器用于启动Java虚拟机，当调用javax.exe时，就会启动引导类载入器。它会载入运行JVM所需要的类，以及所有的Java核心类，如java.lang包和java.io包下的类。②启动类载入器会在rt.jar和i18n.jar等java包中搜索所需要载入的类。③扩展类载入器负责载入标准扩展目录中的类。如/jdk/jre/lib/ext/。系统类载入器是默认的类载入器，它会搜索在环境变量中CLASSPATH中指明的路径和JVR文件。
   - Tomat使用自定义载入器有以下三条原因：
     - 为了在载入类中指定某些规则；
     - 为了缓存以及载入的类；
     - 为了实现类的预载入。

2.   Loader接口：载入器必须实现org.apache.catalina.Loader接口。默认类载入器的实现是WebappClassLoader。可以通过Loader.getClassLoader()来获取Web载入器中的ClassLoader类的实例。

    - Web应用程序的WEB-INF/classes目录和WEB-INF/lib目录是作为仓库添加到载入器中的。使用Loader接口的addRepository()方法可以添加一个新的仓库。
    - Tomcat载入器通常会与一个Context级别的Servlet容器相关联。可以通过Loader接口的setContainer与servlet容器关联。
    - 载入器可以对被修改的类进行自动重载，不需要重新启动Tomcat。通过Loader接口的modified()方法来支持类的自动重载。需要modified()返回true才能重载。载入器类本身并不能自动重载，它会调用Context接口的reload()方法来实现。

3.  Reloader接口：为了重载需要实现此接口。其中modified()返回会返回true，如果仓库中某个servlet或相关的类被修改了。addRepository()方法用来添加一个仓库，findRepositories()返回一个字符串数组，包含了实现reloader接口的类载入器的所有仓库。

4.   WebappLoader类：实现了Loader接口、Lifecycle接口、Runnable接口（可以指定一个线程不断的调用类加载器的modified()方法）。

    - WebappLoader的start()方法会完成：
      - 创建一个类载入器；
      - 设置仓库；
      - 设置类路径；
      - 设置访问权限；
      - 启动一个新线程来支持自动重载。

    1.  创建类载入器：自定义类载入器必须继承自WebappClassLoader类。
    2. 设置仓库：通过setRepositories()方法将仓库添加到其类载入器中。Web-INF/classes目录被传入到类载入器的addRepository()方法中，而WEB-INF/lib目录被传入到载入器的setJarPath()方法中。
    3. 设置类路径：通过setClassPath()，为JSP编译器设置一个字符串形式的属性来指明类路径信息。
    4. 设置访问权限：如果运行Tomcat时，使用了安全管理器，则setPermissions()方法会为类载入器设置访问相关目录的权限。
    5. 开启新线程执行类的重新载入
       - 间隔时间默认15秒，可以通过setCheckInterval()方法来设置间隔时间。
       - 通过Runnable接口来实现重载，重载是通过通知与WebAppLoader实例相关联的Context容器重新载入相关类。
         - WebappLoader的run()方法执行步骤：
           - 使线程休眠一段时间；
           - 检查modified()方法的返回值；
           - 调用notifyContext()，通知webappLoader实例关联的Context容器重新载入相关类。
         - WebappContextNotifier实现Runnable接口，是WebappLoader的一个内部类。此类启动一个线程来调用相关列容器的reload()方法进行重新载入类。

5.   WebappClassLoader类：继承自java.net.URLClassLoader类。

    - 它可以指定不允许载入某些类或者某些包下的类。

    1.  类缓存：把以及载入的类缓存起来，增加性能。
    2.  载入类：
       - 先检查以及载入的类的缓存；
       - 若缓存没有，则检查上一级缓存，即调用java.lang.ClassLoader类的findLoadedClass()方法；
       - 若都没，则使用系统的类载入器进行加载，防止Web应用程序中的类覆盖J2EE的类；
       - 若启用了SecurityManager，则检查是否允许载入该类；
       - 如打开标志位delegate，或者载入的类是属于包触发器中的包名，则调用父载入器来载入相关类。若父载入器为null，则使用系统的类载入器；
       - 从当前仓库中载入相关类；
       - 若当前仓库没有需要的类，且标志位delegate关闭，则使用父类载入器。若父载入器为null，则使用系统的类载入器；
       - 未找到需要的类，抛出ClassNofFoundException异常。

# 9、Session管理

- 通过org.apache.catalina.Manager接口的实例来管理Session对象。Session管理器必须与一个Context容器相关联。Session管理器负责创建、更新、销毁Session对象，当有请求到来时，会返回一个有效的Session对象。
- Servlet实例可以通过javax.servlet.http.HttpServletRequest接口的getSession()方法来获取一个Session对象。在Catalina的默认连接器中，HttpRequestBase类实现了HttpServletRequest接口，可以用来获取Session对象。
- Session管理器默认会把Session对象放在内存中。但Sessiob管理器也可以将Session对象进行持久化，存储到文件存储器或通过JDBC写入到数据库中。

1.  Session对象

   - 在Servlet编程中，Session对象由javax.servlet.http.HttpSession接口表示。在Catalina中Session的标准实现是org.apache.catalina.session.StandardSession类，此类实现了javax.servlet.http.HttpSession和org.apache.catalina.Session接口。此类只会暴露除其外观类org.apache.catalina.session.StandardSessionFacade（实现了javax.servlet.http.HttpSession接口）。

   1.  Session接口：作为Catalina内部的外观类使用的。
      - Session对象总是存在于Session管理器中，可以通过setManager()将Session实例和某个Session管理器相关联。
      - 每个Session在与它相关联的Context实例内，由一个唯一标识符，可以通过setId()方法来设置标识符，getId()方法访问标识符。
      - Session管理器会调用getLastAccessedTime()方法，根据返回值来判断一个Session对象的有效性。
      - Session管理器会调用setValid()方法来设置或重置Session对象的有效性。
      - 当访问一个Session实例时，会调用其access()方法来修改Session对象的最后访问时间。
      - Session管理器会调用Session对象的expire()方法将其设置未过期。
      - 通过getSession()方法获取一个经过StandardSessionFacade外观类包装过的HttpSession对象。
   2.  StandardSession类：实现了javax.servlet.http.HttpSession、javax.servlet.http.HttpSession、java.lang.Serializable接口。
      - 构造函数带有一个Manager接口的参数，强制Session对象一定要拥有一个Session管理器实例。
   3.  StandardSessionFacade类：StandardSession的外观类。

2.  Manager：管理器负责创建、更新、销毁Session对象，当有请求到来时，会返回一个有效的Session对象。

   - 由一个ManagerBase的工具类，实现了常见的功能。它由两个直接子类StandardManager类和PersistentManagerBase类。
   - Catalina运行时，StandardManager实例会将Session对象存储在内存中。Catalina关闭时，它会将当前内存中的所有Session对象存储到一个文件中，再次启动Catalina时，又会将这些Session对象重新载入到内存中。
   - PersistentManagerBase类由两个子类：PersistentManager类和DistributedManager类。

   1.  Manager接口
      - 通过setContainer()方法与一个Context容器相关联；
      - 通过createSession()方法创建一个Session实例；
      - 通过add()方法将一个Session实例添加到Session池中，remove()方法将一个Session实例从Session池中移除。
      - setMaxInactiveInterval()方法来设置一个Session对象的最长存活时间。
      - load()和unload()将对象持久化到辅助存储器中。load()会载入到内存，unload()会持久化。
   2.  ManagerBase类：是一个抽象类，所有的Session管理器组件都会继承此类。
      - generateSessionId()可以返回一个Session的唯一标识符。
      - 活动的Session对象都存储在一个名为sessions的HashMap中。
   3.  StandardManager类：Manager接口的标准实现。实现了Lifecycle接口。
      - 其中stop()方法会调用unload()方法，以将Session对象序列化到一个名未SESSION.ser的文件中，每个Context容器都会产生一个这样的文件。SESSION.ser文件位于环境变量CATALINA_HOME指定的目录下的work目录中。
      - 通过一个专门的线程来摧毁已经失效的Session对象。
   4.  PersistentManagerBase类：通过一个Store对象来备份Session。
      - 通过一个专门的线程来执行摧毁已经失效、备份和换出活动的Session对象的任务。
      - 其中会做的事情：
        - 摧毁已经失效
        - 检查：
          - 太长的休眠时间的
          - 太多活动的session
          - 备份session
      - findSession()方法会先从内存中寻找session，找不到再从辅助存储器中找。
   5.  PersistentManager类：
   6.  DistributedManager类： 用于集群环境。一个节点表示部署的一台Tomcat服务器。每个节点使用DistributedManager实例作为其Session管理器，从而支持复制Session对象。
      - 通过ClusterSender类向集群中的节点发送信息，ClusterReceiver类接收集群中其他节点发送的消息。
      - DistributedManger实例的createSession()方法创建一个Session对象，存储在当前的DistributedManager实例中，并使用ClusterSender实例向其他节点发送消息。
      - 启动一个专门的线程检查Session是否过期，并从集群中的其他节点接收信息。

3.  存器器：org.apache.catalina.Store接口的实例，用于Session管理器持久化Session对象的组件。

   1. StoreBase类：实现了除save()方法和load()方法外的其他方法。
      - 启动一个专门的线程从活动的Session集合中移除过期的Session对象。在Tomcat5中，不在使用专门的线程调用processExpires()方法，其相关联的PersistentManagerBase实例的backgroundProcess()方法会周期性地调用processExpires()方法。
   2.  FileStore类：将Session对象存储到文件中。文件名会使用Session对象的标识符再加上一个.session构成。文件位于临时的工作目录下，可以调用FileStore类的setDirectory()方法修改临时目录的位置。
      - save()和load()方法分别使用java.io.ObjectOutputStream和java.io.ObjectInputStream类来实现序列化和反序列化。
   3.  JDBCStore类：将Session对象通过JDBC存入数据库。需要调用setDriverName()方法和setConnectionURL()方法来设置驱动程序名称和连接URL。

4.  应用程序

   1.  Bootstrap类
   2.  SimpleWrapperValve类
      - 在invoke()方法中为request设置wrapper类相关联的Context容器，这样在HttpRequestBase类的doGetSession()方法中就可以调用context.getManger()来获取manager，通过manager（Session管理器）就可以获取到Session或创建一个新的Session对象。

# 10、安全性

- Servlet支持通过配置部署器（web.xml文件）来进行访问控制。
- servlet容器是通过一个名为验证器的阀来支持安全限制的。当servlet容器启动时，验证器阀会被添加到Context容器的管道中。
- 在调用Wrapper阀之前，会先调用验证器阀，对当前用户进行身份验证。如果用户输入了正确的账号密码，则验证器阀会调用后续的阀，继续显示请求的servlet。否则不会调用后面的阀，也就看不到servlet资源了。
- 验证器阀会调用Context容器的领域对象的authenticate()方法，转入用户输入的用户名和密码，来对用户进行身份验证。领域对象可以访问有效用户的用户名和密码的集合。

1.  领域
   - 领域对象是用来对用户进行身份验证的组件。它会调用用户输入的用户名和密码对进行有效性判断。
   - 领域对象通常会和一个Context容器相关联。可以通过Context容器的setRealm()设置领域对象。
   - 领域对象保存了有效用户的用户名和密码对，或者它会访问存储这些数据的存储器。如Tomcat中的tomcat-users.xm文件，或者数据库。
   - 领域对象是org.apache.catalina.Realm接口的实例，有：
     - authenticate()方法负责验证身份；
     - hasRole()方法检查有没有此权限；
     - setContainer()方法与一个Context实例相关联；
   - 基本实现是RealmBase类，是一个抽象类。其实现子类有JDBCRealm、JNDIRealm、MemoryRealm、UserDatabaseRealm。
     - MemoryRealm第一次调用时，会读取tomcat-users.xml的内容。
2. GenericPrincipal类：是java.security.Principal接口的实例。GenericPrincipal实例必须始终与一个领域对象相关联。Principal实例必须有一个用户名和密码对，角色列表是可选的。可以通过其hasRole()方法检查该主体对象是否拥有该指定角色。
3.  LoginConfig类：是org.apache.catalina.deploy.LoginConfig类的实例，包含一个领域对象的名字。
   - LoginConfig实例封装了领域对象名和所要使用的身份验证方方法。
   - getRealmName()可以获取领域对象的名字；
   - getAuthName()可以获取使用的身份验证方法的名字，可以是：BASIC、DIGEST、FORM或CLIENT-CERT。如果是FORM身份认证，LoginConfig实例还需要在loginPage属性和errorPage属性中分别存储字符串形式的登录页面和错误页面的URL。
   - Tomcat在启动时会读取web.xml文件的内容。如果web.xml文件包含一个login-config元素的配置，则Tomcat会创建一个LoginConfig对象，并设置相应属性。验证器阀会调用LoginConfig实例的getRealmName()方法获取领域对象名，并将该领域对象名发送到浏览器，显示在登录对话框中。
4.  Authenticator接口：验证器的接口。该接口没有方法，只是一个标识作用。基本实现类是AuthenticatorBase类，此类还扩展了ValveBase类。也就是说，AuthenticatorBase类也是一个阀。其实现子类有BasicAuthenticator、FormAuthenticator、DigestAuthentication、SSLAuthenticator。
   - 当Tomcat用户没有指定验证方法时，NonLoginAuthenticator类用于对来访者的身份进行验证，NonLoginAuthenticator类只会检查安全限制，不会设计用户身份的验证。
5.  安装验证器阀
   - 在部署描述器中，login-config元素只能出现一次。login-config元素包含一个auth-method元素来指定身份验证方法。也就是说，一个Context实例只能由一个LoginConfig实例和利用一个验证类的实现。
   - StandardContext类使用org.apache.catalina.startup.ContextConfig类来对StandardContext实例的属性进行设置。这些设置就包括实例化一个验证器类，并将其与Context实例相关联。

# 11、StandardWrapper

1. 方法调用序列：对每个引入的HTTP请求，连接器都会调用于其关联的servlet容器的invoke()方法。然后，servlet容器会调用所有子容器的invoke()方法。

   - 具体过程如下：

     1. 连接器创建request和reponse对象；
     2. 连接器调用standardContext实例的invoke()方法；
     3. 接着，StandardContext实例的invoke()方法调用其管道对象的invoke()方法。StandardContext中管道对象的基础阀是StandardContextValve类的实例，因此，StandardContext的管道对象会调用StandardContextValve实例的invoke()方法；
     4. StandardContextValve实例的invoke()方法获取相应的Wrapper实例处理HTTP请求，调用Wrapper实例的invoke()方法；
     5. StandardWrapper实例的invoke()方法会调用其管道对象的invoke()方法；
     6. StandardWrapper的管道对象中的基础阀是StandardWrapperValve类的实例，因此，会调用StandardWrapperValve的invoke()方法，StandardWrapperValve的invoke()方法调用Wrapper实例的allocate()方法获取servlet实例；
     7. allocate()方法调用load()方法载入相应的servlet类，若已经载入，则无需重复载入；
     8. load()方法调用servlet实例的init()方法；
     9. StandardWrapperValve调用servlet实例的service()方法。

     - 其中StandardContext类的构造函数会设置一个StandardContextValve类作为实例的一个基础阀；
     - 其中StandardWrapper类的构造函数会设置一个StandardWrapperValve类作为实例的一个基础阀；

2. SingleThreadModel：servlet类可以实现javax.servlet.SingleThreadModel接口，这样的servlet类也称为STMservlet类。实现此接口的目的是保证servlet实例一次只处理一个请求。

3. StandardWrapper：主要任务是载入它所代表的servlet类，并进行实例化。但StandardWrapper类并不调用servlet的service方法。该任务由StandarWrapperValve对象完成。StandardWrapperValve对象通过调用allocate()方法从StandardWrapper实例中获取servlet实例。在获取servlet实例后，StandardWrapperValve实例就会调用servlet实例的service()方法。

   - 第一次请求某个servlet的时候，StandardWrapper载入servlet类。可以通过调用StandardWrapper的setServletClass()方法指定servlet类的完全限定名，调用setName()方法为该servlet类指定一个名字。
   - StandardWrapper会考虑到servlet是否实现了SingleThreadModel接口。
   - 对于没有实现SingleThreadModel接口的servlet，每次返回的实例都是同一个，因为它假设servlet类的service()方法是线程安全的。
   - StandardWrapper实例会维护一个STM servlet实例池。
   - Wrapper实例负责准备一个javax.servlet.ServletConfig实例。

   1. 分配servlet实例：allocate()方法
      - STM servlet实例池存储在一个java.util.Stack栈里。
   2. 载入servlet类：load()方法会调用loadServlet()方法载入某个servlet类，并调用其init()方法，传入一个javax.servlet.ServletConfig实例作为参数。
      - 它会检查servlet类是否是一个ContainerServlet类型的servlet，即实现了org.apache.catalina.ContainerServlet接口的servlet可以访问Catalina的内部功能。若是，则会调用ContainerServlet接口的serWrapper()方法，传入这个StandardWrapper实例。
      - 调用init()方法传入javax.servlet.ServletConfig对象的一个外观变量。
   3. ServletConfig对象：其StandardWrapper类不仅实现了Wrapper接口，还实现了ServletConfig接口。
      - getServletContext()方法，返回其父容器的容器，一般来说就是context实例。
      - getServletName()返回servlet的名字；
      - getInitParameter()方法，返回指定初始参数的值；存储在StandardWrapper类的parameters成员变量中，可以通过StandardWrapper的addInitParameter()方法来添加参数，findInitParameter()来获取参数值。
      - getInitParameterNames()获取初始化参数的名字集合，此集合以Enumerator（实现了java.util.Enumeration接口）的形式返回。
   4. servlet容器的父子关系

4. StandardWrapperFacade类：把StandardWrapper实例的方法隐藏起来，都实现了ServletConfig接口。把StandardWrapper实例作为它自身的一个成员变量，然后调用它的方法。

   - 其getServletContext()方法返回的是ServletContext类的外观类实例，而不是它自身。

5. StandardWrapperValve类：是StandardWrapper的基础阀。

   - 完成以下两个操作：
     1. 执行与该servlet实例相关联的全部过滤器；
     2. 调用servlet实例的service()方法。
   - 在invoke()方法中会做：
     1. 调用StandardWrapper实例的allocate()方法获取该StandardWrapper实例所表示的servlet实例；
     2. 调用私有方法createFilterChain()创建过滤器链；创建一个ApplicationFilterChain实例，并将所有需要用到该Wrapper实例代表的servlet实例的过滤器添加到其中。
     3. 调用过滤器链的doFilter()方法，其中包括调用servlet实例的service()方法；
     4. 释放过滤器链；
     5. 调用Wrapper实例的deallocate()方法；
     6. 若该servlet类再也不会被使用到，则调用Wrapper实例的unload()方法；

6. FilterDef类：表示一个过滤器的定义，如在部署描述器中定义的filter元素那样。它的每个属性表示定义filter元素时声明的子元素。map类型的变量parameters存储了初始化过滤器所需要的所有参数。addInitParameter()方法用于向parameters中添加新的k/v对。

7. ApplicationFilterConfig类：实现了javax.servlet.FilterConfig接口。用于在Web应用程序第一次启动时创建所有的过滤器实例。

   - 构造器需要org.apache.catalina.Context对象（代表一个Web应用程序）和一个FilterDef对象（代表一个过滤器）。

8. ApplicationFilterChain类：实现了javax.servlet.FilterChain接口。StandardWrapperValve类的invoke()方法会创建ApplicationFilterChain类的一个实例，并调用其doFilter()方法。ApplicationFilterChain类的doFilter()方法会调用过滤器链中第一个过滤器的doFilter()方法。doFilter()方法会将ApplicationFilterChain类自身作为第三个参数传递给过滤器的doFilter()方法。

   - 可以显式的调用FilterChain对象的doFilter()方法来调用另外一个Filter。直到最后一个过滤器，不再调用filterChain.doFilter()方法。

# 12、StandardContext类

1. StandardContext的配置

   - 创建StandardContext实例后，调用start()启动来为每个HTTP请求提供服务。如果StandardContext对象启动失败。这时候会设置available属性为false，available属性表明StandardContext对象是否可用。
   - Tomcat的实际部署中，配置StandardContext对象需要一系列操作。正确设置后，StandardContext对象才能读取并解析默认的web.xml文件。
   - StandardContext类的configured属性是一个布尔变量，表明StandardContext实例是否被正确设置。StandardContext类使用一个事件监听器作为配置器，当调用StandardContext实例的start()方法时，其中一件要做的事情就是触发生命周期事件。该事件对StandardContext容器进行配置。若配置成功，会把configured设置为true。否则StandardContext实例将拒绝启动，无法提供HTTP请求的服务。

   1. StandardContext类的构造函数

      ```java
          public StandardContext() {
              super();
              //为对象设置基础阀，此基础阀会处理从连接器中接收到对的每个HTTP请求。
              pipeline.setBasic(new StandardContextValve());
              namingResources.setContainer(this);
          }
      ```

   2. 启动StandardContext实例

      - start()如果生命监听器成功执行了其配置StandardContext实例的任务，它就会把StandardContext的configured属性设置为true。在start()方法的结尾，会检查configured的值，如果为false，则会调用stop()方法关闭在start()方法已经启动的所有组件。
      - start()方法需要完成的工作：
        1. 触发BEFORE_START事件；
        2. 将availability属性设置为false；
        3. 将configured属性设置为false；
        4. 配置资源；
        5. 设置载入器；
        6. 设置Session管理器；
        7. 初始化字符集映射器；
        8. 启动与该Context容器相关联的组件；
        9. 启动子容器；
        10. 启动管道对象；
        11. 启动Session管理器；
        12. 触发START事件，在这里监听器ContextConfig实例会执行一些配置操作；并设置configured的值。
        13. 检查configured的值，若为true，则调用postWelcomePages()方法，载入那些需要在启动时就载入的子容器，即Wrapper实例，将availability属性设置为true。若configured变量为false，则调用stop()方法；
        14. 触发AFTER_START事件。

   3. invoke()方法：在Tomcat4中，由相关联的连接器调用，或者由相关联的Host容器调用，invoke()方法会检查应用程序是否正在重载过程中，若是，将等待应用程序重载完成。然后调用其父类ComntainerBase的invoke()方法。

      - 在Tomcat5中，会直接调用ContainerBase的invoke()方法。检查应用程序是否正在重载的工作移动到了StandardContextValve类的invoke()方法中。

2. StandardContextMapper类：在Tomcat4中，StandardContextValve实例在它包含的StandardContext中查找Wrapper实例。StandardContextValve会在StandardContext实例的映射器找到一个Wrapper实例。获得Wrapper实例后，会调用Wrapper实例的invoke()方法。

   - ContainerBase类通过addDefaultMapper()方法来添加一个默认的映射器。

   - StandardContextMapper类必须调用setContainer()方法与一个Context级的容器相关联。

   - map()方法会返回用来处理HTTP请求的子容器，需要一个Request和一个Boolean update作为参数。

   - 对于每个HTTP请求，StandardContextValve实例会调用Context容器的map()方法，传入一个Request对象。map()方法会根据特定的协议调用findMapper()方法返回一个映射器对象，然后调用映射器对象的map()方法获取Wrapper实例；

     ```java
         public Container map(Request request, boolean update) {
             // Select the Mapper we will use
             Mapper mapper = findMapper(request.getRequest().getProtocol());
             if (mapper == null)
                 return (null);
             // Use this Mapper to perform this mapping
             return (mapper.map(request, update));
         }
     ```

   - 其容器通过配置信息去映射请求和servlet实例。

   - 在Tomcat5中，Mapper接口以及不存在了。StandardContextValve类的invoke()方法会从request对象中获取适合的Wrapper实例；

3. 对重载的支持

   - 通过StandardContext类的reloadable属性来指明应用程序是否启动了重载功能。当重载功能启动之后，当web.xml文件发生变化或者WEB-INF/classes目录下的其中一个文件被重新编译后，应用程序会重载。
   - StandardContext类是通过其载入器实现应用程序的重载的。在Tomcat4中，StandardContext对象中的WebappLoader类实现了Loader接口，并使用另外一个线程检查WEB-INF目录中的所有类和JAR文件的时间戳。只需要调用其setContainer()方法将WebappLoader对象与StandardContext对象相关联就可以启动该检查线程。
   - 在Tomcat5中，支持重载功能的工作改为由backgroundProcess()方法执行。

4. backgroundProcess()方法

   - 在Tomcat5中，所有的后台处理共享一个线程。若某个组件或servlet容器需要周期性地执行一个操作，只需要将代码写道其backgroundProcess()方法中即可。
   - 这个共享线程在ContainerBase对象中创建。ContainerBase类在其start()方法中调用其threadStart()方法启动该后台线程。
   - threadStart()方法通过传入一个实现了java.lang.Runnable接口的ContainerBackgroundProcessor类的实例构造一个新线程。
   - ContainerBackgroundProcessor类实际上是ContainerBase类的内部类。在其run()方法中周期性的调用其proccessChildren()方法。而processChildren()方法会调用自身对象的backgroundProcess()方法和其每个子容器的processChildren()方法。通过实现backgroundProcess()方法，ContainerBase类的子类可以使用一个专用线程来执行周期性任务，例如检查类的时间戳或检查session对象的超时时间。

# 13、Host和Engine

- 如果想在同一个Tomcat上部署多个Context容器的化，就需要Host容器。实际部署中，总会使用一个Host容器，并且使用Context作为子容器。
- Engine容器表示Catalina的整个servlet引擎。默认情况下，Tomcat会使用Engine容器，并且会使用Host作为子容器。

1. Host接口：继承自Container接口。

   - 主要方法是map()方法，返回一用来处理引入的HTTP请求的Context容器的实例。

2. StandardHost类：Host接口的标准实现。该类继承自org.apache.catalina.core.ContainerBase类，实现了Host和Deployer接口。

   - 其构造器会将一个基础阀添加到其管道对象中。

     ```java
         public StandardHost() {
             super();
             pipeline.setBasic(new StandardHostValve());
         }
     ```

   - start()方法会添加两个阀，分别是ErrorReportValve类和ErrorDispatcherValve类的实例。

     ```java
         public synchronized void start() throws LifecycleException {
             // Set error report valve
             if ((errorReportValveClass != null)
                 && (!errorReportValveClass.equals(""))) {
                 try {
                     Valve valve = (Valve) Class.forName(errorReportValveClass)
                         .newInstance();
                     addValve(valve);
                 } catch (Throwable t) {
                     log(sm.getString
                         ("standardHost.invalidErrorReportValveClass",
                          errorReportValveClass));
                 }
             }
     
             // Set dispatcher valve
             addValve(new ErrorDispatcherValve());
     
             super.start();
     
         }
     ```

   - 对于每个HTTP请求，都会调用Host实例的invoke方法。StandardHost类没有invoke()方法的实现，因此会调用ContainerBase类的invoke()方法。而ContainerBase的invoke()方法调用StandardHost实例的基础阀StandardHostValve的invoke()方法。StandardHostValve类的invoke()方法调用StandardHost类的map()方法来获取相应的Context实例来处理HTTP请求。
   
   - 在Tomcat4中，会调用ContainerBase的map()方法，而它又调用StandardHost的map()方法。在Tomcat5中，映射器组件已经移除，Context实例是通过request对象获取的。
   
3. StandardHostMapper类：ContainerBase类会调用其addDefaultMapper方法创建一个默认映射器。默认映射器的类型由mapperClass属性的值决定。

   - StandardHost类的start()方法会在方法末尾调用父类的start()方法，确保默认映射器的创建完成。
   - StandardHostMapper类的map()方法只是简单的调用Host实例的map()方法。

4. StandardHostValve类：StandardHost的基础阀。当有HTTP请求时，会调用invoke()方法进行处理。

   - invoke()方法会调用StandardHost实例的map()方法来获取一个相应的Context实例。

   - invoke()方法会获取与该request对象相关联的session对象，并调用其access()方法。access()方法会修改session对象的最后访问时间。

     ```java
         public void invoke(Request request, Response response,
                            ValveContext valveContext)
             throws IOException, ServletException {
     
             // Validate the request and response object types
             if (!(request.getRequest() instanceof HttpServletRequest) ||
                 !(response.getResponse() instanceof HttpServletResponse)) {
                 return;     // NOTE - Not much else we can do generically
             }
     
             // Select the Context to be used for this Request
             StandardHost host = (StandardHost) getContainer();
             Context context = (Context) host.map(request, true);
             if (context == null) {
                 ((HttpServletResponse) response.getResponse()).sendError
                     (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                      sm.getString("standardHost.noContext"));
                 return;
             }
     
             // Bind the context CL to the current thread
             Thread.currentThread().setContextClassLoader
                 (context.getLoader().getClassLoader());
     
             // Update the session last access time for our session (if any)
             HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
             String sessionId = hreq.getRequestedSessionId();
             if (sessionId != null) {
                 Manager manager = context.getManager();
                 if (manager != null) {
                     Session session = manager.findSession(sessionId);
                     if ((session != null) && session.isValid())
                         session.access();
                 }
             }
     
             // Ask this Context to process this request
             context.invoke(request, response);
     
         }
     ```

     

   - 然后调用context实例的invoke()方法来处理请求。

5. 为什么必须有一个Host容器

   - 若一个Context实例使用ContextConfig对象进行设置，就必须使用一个Host对象。原因如下：

   - ContextConfig对象需要知道应用程序的web.xml文件的位置。在其applicationConfig()方法中它试图打开web.xml文件。

     - applicationConfig()方法：

       ```java
               synchronized (webDigester) {
                   try {
                       URL url = servletContext.getResource(Constants.ApplicationWebXml);
       
                       InputSource is = new InputSource(url.toExternalForm());
                       is.setByteStream(stream);
                       webDigester.setDebug(getDebug());
                       if (context instanceof StandardContext) {
                           ((StandardContext) context).setReplaceWelcomeFiles(true);
                       }
                       webDigester.clear();
                       webDigester.push(context);
                       webDigester.parse(is);
       ```

   - ApplicationContext类的getResource()方法的部分实现代码：

     ```java
         public URL getResource(String path)
             throws MalformedURLException {
     
             DirContext resources = context.getResources();
             if (resources != null) {
                 String fullPath = context.getName() + path;
                 // this is the problem. Host must not be null
                 //如果要实现ContextConfig实例进行配置的化，Context实例必须有一个Host实例作为其父容器。
                 String hostName = context.getParent().getName();
     ```

6.  

7.  Engine接口：Engine容器也就是Tomcat的servlet引擎。当部署Tomcat时需要支持多个虚拟机的话，就需要使用Engine容器。一般部署Tomcat都会使用一个Engine容器。

   - Engine容器可以与Host容器或Context容器相关联，也可以与一个服务实例相关联。

8. StandardEngine类：Engine接口的标准实现。

   - 构造时会添加一个StandardEngineValve基础阀。
   - 不能有父容器，只能有Host类型的子容器。

9. StandardEngineValve类：是StandardEngine的基础阀。

   - invoke()方法会从StandardEngine的map()方法获取Host实例，然后调用Host实例的invoke()方法。

# 14、服务器组件和服务组件

1. 服务器组件

   - org.apache.catalina.Server接口的实例表示Catalina的整个servlet引擎，囊括了所有的组件。服务器组件使用一种优雅的方法来启动/关闭整个系统，不需要再对连接器和容器分别启动/关闭。
   - 启动服务器组件时，它会启动其中所有的组件，然后它就无限期地等待关闭命令。如果要关闭系统，可以向指定端口发送一条关闭命令。然后服务器组件就会关闭所有组件。
   - 服务器组件使用另外一个组件（服务组件）来包含其他组件，如一个容器组件和一个/多个连接器组件。
   - shutdown属性保存了必须发送给Server实例用来关闭整个系统的关闭命令。port属性表明监听关闭命令的端口。addService()方法可以添加服务组件。initialize()方法进行系统启动前的初始化工作。

2. StandardServer类：Server接口的标准实现。此类许多方法都与server.xml文件中的服务器配置的存储相关。

   - 一个服务器组件可以有0个或多个服务组件。可以通过addService()方法添加。
   - StandardServer有4个与生命周期相关的方法，分别是initialize()方法、start()方法、stop()方法和await()方法。可以初始化并启动服务器组件（initialize()方法和start()方法）。调用await()方法会一直阻塞，知道从8085端口上接收到关闭命令。await()方法返回时，会运行stop()方法来关闭其下的所有子组件。

   1. initialize()方法：用于初始化添加到其中的服务组件。
      - 其中使用一个initialized的布尔变量来防止服务器组件初始化两次。stop()方法并不会重置initialized的值，因此服务器组件关闭重启，是不会再次进行初始化的。
   2. start()方法：启动服务器组件。会启动所有的服务组件，如连接器组件和servlet容器。
      - 使用started布尔变量来防止服务器组件重复启动。stop()方法会重置这个值。
   3. stop()方法：关闭服务器组件。会关闭所有服务器组件，并重置布尔变量started。
   4. await()方法：负责等待关闭整个Tomcat部署的命令。它会创建一个ServerSocket对象，监听8085端口，并在while循环中调用它的accept()方法。当在指定端口上接收到信息时，才会从accept()方法中返回。然后与关闭命令的字符串相比较，如果相同则跳出while循环，关闭SocketServer。

3. Service接口：服务组件的接口。一个服务组件可以有一个servlet容器和多个连接器实例。可以自由的把连接器添加到服务组件中，这样所有连接器都会与这个servlet容器相关联。

4. StandardService类：是Service接口的标准实现。initialize()方法用于初始化添加到其中的所有连接器。还实现了Lifecyle接口，因此它的start()方法可以启动连接器和所有servlet()容器。

   1. connector和container
      - StandardService实例的两种组件，连接器和servlet容器。servlet容器只有一个，连接器可以有多个。多个连接器使Tomcat可以为多种不同的请求协议提供服务。例如一个处理HTTP请求和另一个处理HTTPS请求。
      - 通过成员变量container和connectors来保存容器和连接器的引用。setContainer()方法将servlet容器与服务组件相关联。然后会将每个连接器与servlet容器相关联。
      - addConnector()方法将连接器添加到服务组件中，会初始化并启动添加到其中的连接器。
   2. 与生命周期相关的方法：initialize()方法、start()方法、stop()方法和await()方法。
      - initialize()方法：初始化连接器；
      - start()方法：启动servlet容器和连接器；
      - stop()方法：关闭servlet容器和连接器。

5. 应用程序

   1.  

   2.  Stopper类：提供更优雅的方式来关闭Catalina服务器，也保证了所有的生命周期组件的stop()方法都能够调用。

      - Stopper类的main()方法会创建一个Socket对象，然后将正确的关闭命令”SHUTDOWN“发送给端口8005。Catalina服务器收到关闭命令后，就会执行相应的关闭操作。

      - 源码：直接发送关闭命令

        ```java
        public class Stopper {
            public static void main(String[] args) throws IOException {
                int port = 8005;
                Socket socket = new Socket("127.0.0.1", port);
                OutputStream stream = socket.getOutputStream();
                String shutdown = "SHUTDOWN";
                for (int i = 0; i < shutdown.length(); i++) {
                    stream.write(shutdown.charAt(i));
                }
                stream.flush();
                stream.close();
                socket.close();
            }
        }
        ```

# 15、Digester库

- Bootstrap类用来实例化连接器、servlet容器、Wrapper实例和其他组件，然后调用各个对象的set方法将它们关联起来。
- Tomcat使用一个名为server.xml的XML文档来进行应用程序的配置。server.xml的每个元素都会转换为一个Java对象，元素的属性会用于设置Java对象的属性。这样，就可以通过简单地编辑servlet.xml文件来修改Tomcat的配置。
- Tomcat使用开源库Digester来将XML文档中的元素转换成Java对象。
- 配置Web应用程序是通过对已经实例化的Context实例进行配置完成的。用来配置Web应用程序的XML文件的名称是web.xml，该文件位于Web应用程序的WEB-INF目录下。

1. Digester库

   1. Digester类：是Digester库的主类，用于解析XML文档。对于每个XML文档中的元素，Digester对象都会检查它是否要做事先预定义的事件。在调用DIgester对象的parse()方法前，程序员要先定义好Digester对象执行哪些动作。

      - 要先定义好模式，然后将每个模式与一条或多条规则相关联。XML文档中根元素的模式与元素的名字相同。

      - 子元素的模式是由该元素的父元素的模式再加上“/”符号，以及该元素名字拼接而成的。

      - 规则是org.apache.commons.digester.Rule类的实例。Digester类可以包含0个或多个Rule对象。再Digester实例中，这些规则和其相关联的模式都存储再由org.apache.commons.digester.Rules接口表示的一类存储器中。每当把一条规则添加到Digester实例中时，Rule对象都会被添加到Rules对象中。

      - Rule类由begin()和end()方法。在解析XML文档时，当Digester实例遇到匹配某个模式的元素的开始标签时，它会调用相应的Rule对象的begin()方法。当Digester实例遇到相应元素的结束标签时，它会调用Rule对象的end()方法。

      - 一些预定于的规则：

        1. 创建对象：可以调用Digester对象的addObjectCreate()方法

           - 相关方法1：

             ```java
             //需要指定一个模式和一个Class对象或类名来调用方法
             public void addObjectCreate(String pattern, String className) {
                     this.addRule(pattern, new ObjectCreateRule(className));
                 }
                 public void addObjectCreate(String pattern, Class clazz) {
                     this.addRule(pattern, new ObjectCreateRule(clazz));
                 }
             ```

           - 相关方法2：

             ```java
             //可以在XML文档中指定类的名字，无需将其作为参数传入。这使得类名可以在运行时决定。
             //其中attributeName参数指明了XML元素的属性的名字，该属性的值是将要实例化的类的名字。
             //className可以为null，也可以设置默认值。如果attributeName属性对应的值为空，则使用默认值。
             public void addObjectCreate(String pattern, String className, String attributeName) {
                     this.addRule(pattern, new ObjectCreateRule(className, attributeName));
                 }
             
                 public void addObjectCreate(String pattern, String attributeName, Class clazz) {
                     this.addRule(pattern, new ObjectCreateRule(attributeName, clazz));
                 }
             ```

           - addObjectCreate()方法创建的对象会被压入到一个内部栈中。Digester提供了一些其他的方法来对新创建的对象执行查看、入栈、出栈等操作。

        2. 设置属性：addSetProperties()方法，为Digester对象设置属性。

           - 相关方法：

             ```java
             //需要指定一个模式，会把该模式相应的属性通过setter方法设置。
             public void addSetProperties(String pattern) {
                     this.addRule(pattern, new SetPropertiesRule());
                 }
             ```

        3. 方法调用：可以遇到某个规则的时候，调用内部栈最顶端对象的某个方法。通过addCallMethod()方法。

           - 相关方法：

             ```java
             //指定模式和方法名
             public void addCallMethod(String pattern, String methodName) {
                     this.addRule(pattern, new CallMethodRule(methodName));
                 }
             ```

        4. 创建对象之间的关系：通过addSetNext()方法会把栈顶的倒数第一个元素作为参数设置到倒数第二个元素里。

           - 相关方法：

             ```java
             //触发的模式（栈顶倒数第一个元素），调用的方法（栈顶倒数第二个元素的方法）   
             public void addSetNext(String pattern, String methodName) {
                     this.addRule(pattern, new SetNextRule(methodName));
                 }
             ```

        5. 验证XML文档：可以通过某个模式进行XML文档有效性验证。Digester类的validating属性指明了是否要对XML文档进行有效性验证。默认情况下为false。可以通过setValidating()方法来设置。

   2.  

   3.  

   4.  Rule类

      - Digester对象的addXXX()方法都会简介的调用Digester类的addRule()方法。该方法会将一个Rule对象和它所匹配的模式添加到Digester对象的Rules集合中。

      - addRule()方法：

        ```java
        public void addRule(String pattern, Rule rule) {
                rule.setDigester(this);
                this.getRules().add(pattern, rule);
            }
        ```

      - ObjectCreateRule类的begin()和end()方法：

        ```java
        public void begin(Attributes attributes) throws Exception {
            // 关键    
            String realClassName = this.className;
                if (this.attributeName != null) {
                    // 关键  
                    String value = attributes.getValue(this.attributeName);
                    if (value != null) {
                        realClassName = value;
                    }
                }
        
                if (this.digester.log.isDebugEnabled()) {
                    this.digester.log.debug("[ObjectCreateRule]{" + this.digester.match + "}New " + realClassName);
                }
        
            // 关键  
                Class clazz = this.digester.getClassLoader().loadClass(realClassName);
            // 关键  
                Object instance = clazz.newInstance();
            // 关键  
                this.digester.push(instance);
            }
        
            public void end() throws Exception {
                // 关键  
                Object top = this.digester.pop();
                if (this.digester.log.isDebugEnabled()) {
                    this.digester.log.debug("[ObjectCreateRule]{" + this.digester.match + "} Pop " + top.getClass().getName());
                }
        
            }
        ```

   5. RuleSet：可以通过Digester实例中的addRuleSet()方法给Digester实例添加Rule对象。

      - RuleSet对象集合是org.apache.commons.digester.RuleSet接口的实例，此接口比较重要的方法是addRuleInstances(Digester)。由基础实现类RuleSetBase，此类是一个抽象类，只需要实现它的addRuleInstances()方法即可。
      - 其getNamespaceURI()方法返回将要应用在RuleSet中所有Rule对象的命名空间的URI。

2. ContextConfig类：是StandarContext实例必须要要的监听器实例，这个监听器负责配置StandardContext实例，设置成功后会将StandarContext实例的变量configured设置为true。

   - ContextConfig类会安装一个验证器阀到StandardContext实例的管道对象中。还会添加一个许可阀（org.apache.catalina.valves.CertificateValve类的实例）到管道对象中。
   - ContextConfig类还会读取和解析默认的web.xml文件和应用程序自定义的web.xml文件，将XML元素转换为Java对象。默认的web.xml文件位于CATALINA_HOME目录下的conf目录中。其中定义并映射了很多默认的servlet，设置了很多MIME类型文件的映射，定义了默认Session超时事件，以及定义了欢迎文件的列表。
   - 应用程序的web.xml文件是应用程序自定义的配置文件，位于应用程序目录下的WEB-INF目录中。这两个文件都不是必须的，即使两个文件都没有找到，ContextConfig实例仍然会继续执行。
   - ContextConfig实例会为每个servlet元素创建一个StandardWrapper类。因此，不再需要自己实例化Wrapper实例了。
   - StandardContext实例启动和停止时，分别会触发START_EVENTSTART_EVENT、BEFORE_START_EVENT、AFTER_START_EVENT、STOP_EVENT、BEFORE_STOP_EVENT、AFTER_STOP_EVENT事件。
     - ContextConfig实例会对这两种事件做出响应，分别是START_EVENT和STOP_EVENT，每次触发时，都会调用ContextConfig的lifecycleEvent()方法：
       - 其中START_EVENT会调用ContextConfig的start()方法：
         - 该方法会为StandardContext实例的父容器（Host或Engine）设置默认Context实例。
         - 调用defaultConfig()和applicationConfig()方法。

   1. defaultConfig()方法：读取位于%CATALINA_HOME%/conf目录下的默认web.xml文件
      - 读取此File文件
      - 然后用webDigester处理此web.xml文件。webDigester包含处理此web.xml所需要的规则。
   2. applicationConfig()方法：读取应用程序目录下的WEB-INF下的web.xml文件，解析过程同defaultConfig()方法。
   3. 创建WebDigester：在ContextConfig类中，使用成员变量webDigester来应用Digester类型的对象，此对象通过createWebDigester()方法创建。
      - createWebDigester()方法：会为webDigester变量设置一个WebRuleSet实例，WebRuleSet包含了解析web.xml文件所需的规则。

# 16、关闭钩子

- 在Tomcat的部署应用中，通过实例化一个Server对象来启动servlet容器，调用其start()方法，然后逐个调用组件的start()方法。
- 在Java中，虚拟机会对两类事件进行响应，并执行关闭操作：
  - 当调用System.exit()方法或程序的最后一个非守护线程退出时，应用程序正常退出；
  - 用户突然强制虚拟机中断运行，如用户按下CTRL+C快捷键或在未关闭Java程序的情况下下，从系统中退出。
- 虚拟机在执行关闭操作时，会经过以下两个阶段：
  1. 虚拟机启动所有已经注册的关闭钩子，如果有的化。关闭钩子是先前已经通过Runtime类注册的线程，所有的关闭够子会并发执行，直到完成任务；
  2. 虚拟机根据情况调用所有没有被调用过的终结器（finalizer）。
- 创建关闭钩子，关闭钩子是java.lang.Thread类的一个子类实例：
  1. 创建Thread类的一个子类；
  2. 实现run()方法，当应用程序关闭时，会调用此方法；
  3. 在应用程序中，实例化关闭钩子类；
  4. 使用当前的Runtime类的addShutdownHook()方法注册关闭钩子；

1.  

2.  Tomcat中的关闭钩子：在Catalina类有一个CatalinaShutdownHook的内部类继承自Thread类，提供了run()方法的实现，它会调用Server对象的stop()方法，执行关闭操作。Catalina实例启动时，会实例化关闭钩子，并在一个阶段将其添加到Runtime类中。

   - 实现源码：

     ```java
         protected class CatalinaShutdownHook extends Thread {
     
             public void run() {
     
                 if (server != null) {
                     try {
                         ((Lifecycle) server).stop();
                     } catch (LifecycleException e) {
                         System.out.println("Catalina.stop: " + e);
                         e.printStackTrace(System.out);
                         if (e.getThrowable() != null) {
                             System.out.println("----- Root Cause -----");
                             e.getThrowable().printStackTrace(System.out);
                         }
                     }
                 }
     
             }
         }
     ```

# 17、启动Tomcat

- Catalina类用于启动或关闭Server对象，并负责解析Tomcat配置文件：server.xml文件。Bootstrao类是一个入口点，负责创建Catalina实例，并调用其process()方法。两个类分开是为了支持Tomcat的多种运行模式，如BootstrapService可以使Tomcat作为一个Windows NT服务来运行。
- 也可以使用批处理文件和Shell脚本来启动或关闭servlet容器。

1. Catalina类：启动类。它包含一个Digester镀锡，用于解析位于%CATALINA_HOME%/conf目录下的server.xml文件。

   - Catalina类封装了一个Server对象，该对象有一个Service对象。Service对象包含一个servlet容器和一个或多个连接器。可以使用Catalina类来启动/关闭Server对象。

   - 可以通过实例化Catalina类，并调用其process()方法来运行Tomcat，第一个参数是start或stop。还有其他可选的参数，包括-help、-config、-debug和-nonaming。

   - Catalina类提供main()方法作为程序的入口点。当一般使用Bootstrap的main作为入口。

   - process()方法会设置两个系统属性，分别是catalina.home和catalina.base.catalina.home，默认值均为user.dir属性的值。user.dir属性值指明了用户的工作目录，即会从哪个目录下调用Java命令。

     - process()方法：会调用arguments()方法处理命令行参数，如果Catalina对象能够继续处理的话，arguments()方法返回true。

       ```java
           public void process(String args[]) {
       
               setCatalinaHome();
               setCatalinaBase();
               try {
                   if (arguments(args))
                       execute();
               } catch (Exception e) {
                   e.printStackTrace(System.out);
               }
           }
       ```

     - arguments()方法：

       ```java
           protected boolean arguments(String args[]) {
       
               boolean isConfig = false;
       
               if (args.length < 1) {
                   usage();
                   return (false);
               }
       
               for (int i = 0; i < args.length; i++) {
                   if (isConfig) {
                       configFile = args[i];
                       isConfig = false;
                   } else if (args[i].equals("-config")) {
                       isConfig = true;
                   } else if (args[i].equals("-debug")) {
                       debug = true;
                   } else if (args[i].equals("-nonaming")) {
                       useNaming = false;
                   } else if (args[i].equals("-help")) {
                       usage();
                       return (false);
                   } else if (args[i].equals("start")) {
                       starting = true;
                   } else if (args[i].equals("stop")) {
                       stopping = true;
                   } else {
                       usage();
                       return (false);
                   }
               }
       
               return (true);
       
           }
       ```

     - execute()方法会调用start()或stop()方法来启动或关闭Tomcat

       ```java
           protected void execute() throws Exception {
               if (starting)
                   start();
               else if (stopping)
                   stop();
           }
       ```

   1. start()方法：会创建一个Digester实例来解析server.xml文件。在解析文件之前，会先调用Digester对象的push()方法传入当前的Catalina对象作为参数。这样Catalina对象就成为了Digester对象的内部栈中的第一个对象。解析server.xml文件之后，会使成员变量server引用一个Server对象，默认是StandardServer类型的对象。然后，start()方法调用Server对象的initialize()和start()方法。接着，Catalina的start()方法会调用Server对象的await()方法，Server对象会使用一个专门的线程来等待关闭命令。await()方法会循环等待，知道接收到正确的关闭命令。当await()方法返回时，Catalina对象的start()方法会调用Server对象的stop()方法，从而关闭Server对象和其他的组件。此外，start()方法还会利用关闭钩子，确保用户突然退出应用程序时会执行Server对象的stop()方法。
   2. stop()方法：用来关闭Catalina和Server对象。
   3. 启动Digester对象：Catalina类的createStartDigester方法用于解析server.xml文件。server.xml用来配置Tomcat，位于%CATALINA_HOME%/conf目录下。
      - 会添加Server、GlobalNamingResources、Listener、Service、Connector、DefaultServerSocketFactory、NamingRuleSet、EngineRuleSet、HostRuleSet、ContextRuleSet、SetParentClassLoaderRule。
   4. 关闭Digester对象：只对XML文档的根元素感兴趣。也就是Server元素。

2. Bootstrap类：启动Tomcat的入口点。当运行startup.bat文件或startup.sh文件时，实际上是调用了该类的main()方法。main()方法会创建3个类载入器，实例化Catalina类并调用其process()方法。

   - 使用多个类载入器的目的是为了防止应用程序中的类使用WEB-INF/classes目录和WEB-INF/lib目录之外的类。部署到%CATALINA_HOME%/common/lib目录下的JAR文件的类文件是可以使用的。
   - 三个类载入器：
     - commonLoader可以载入%CATALINA_HOME%/common/classes目录、%CATALINA_HOME%/common/endorsed目录、%CATALINA_HOME%/common/lib目录下的Java类。
     - catalinaLoader负责载入运行Catalina servlet容器所需要的类。可以载入%CATALINA_HOME%/server/classes目录、%CATALINA_HOME%/server/lib目录以及commonLoader类载入器可以访问的所有目录中的Java类。
     - sharedLoader类可以载入%CATALINA_HOME%/shared/classes目录和%CATALINA_HOME%/shared/lib目录以及commonLoader类载入器可以访问的所有目录中的Java类。在Tomcat中，每个Web应用程序中与Context容器相关联的每个类载入器的父类载入器都是sharedLoader类载入器。
       - sharedLoader类载入器并不能访问Catalina的内部类，或CLASSPATH环境变量指定的类路径中的类。
   - main()方法会载入Catalina类并创建它的一个实例，然后它调用Catalina实例的setParentClassLoader()方法，将sharedLoader类载入器作为参数传入；
   - 最后main()方法调用Catalina实例的process()方法。

# 18、部署器

1. 