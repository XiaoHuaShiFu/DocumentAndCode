# Spring揭秘1

# 1、

1. IoC的理念：让别人为你服务！
   - 也就是IoC容器为你提供所依赖的对象。

# 3、IoC Service Provider

1. IoC Service Provider的职责：

   - 业务对象的构建管理。
   - 业务对象间的依赖绑定。

2. 管理对象间的依赖关系

   - 文本文件

   - XML

     - XML配置：然后再用Java代码从容器中获取newsProvider并使用。

       ```xml
       <bean id="newsProvider" class="...FXNewsProvider">
       	<property name="newsListener">
               <ref bean="djNewsListener" />
           </property>
       </bean>
       ```

   - Java Config

     - 直接编码

       ```java
       IoContainer container = ...;
       container.register(FXNewsProvider.class, new FXNewsProvider());
       container.register(IFXNewsListener.class, new DowJonesNewsListener());
       //...
       FXNewsProvider newsProvider = (FXNewsProvider) container.get(FXNewsProvider.class);
       newProvider.getAndPersistNew();
       ```

     - 接口注入

       ```java
       IoContainer container = ...;
       container.register(FXNewsProvider.class, new FXNewsProvider());
       container.register(IFXNewsListener.class, new DowJonesNewsListener());
       //...
       //将“被注入对象”的由IFXNewsListenerCallable所标识的“所依赖对象”绑定位容器中注册过的IFXNewsListener类型实例对象
       container.bind(IFXNewsListenerCallable.class, container.get(IFXNewsListener.class));
       //...
       FXNewsProvider newsProvider = (FXNewsProvider) container.get(FXNewsProvider.class);
       newProvider.getAndPersistNew();
       ```

   - 注解

# 4、Spring的IoC容器之BeanFactory

1. Ioc Service Provider是Spring的IoC容器的一个子集（元素），Spring的IoC容器的元素还由AOP、线程管理、对象生命周期管理、查找服务、企业服务集成等。

2. Spring提供了两种容器类型：

   - BeanFactory：基础IoC容器，提供完整的IoC服务支持。

     - 默认采用延迟初始化策略（lazy-load）。
     - 

   - ApplicationContext：ApplicationContext在BeanFactory的基础上构建。

     - 支持高级特性。如事件发布、国际化信息等。
     - 默认容器启动之后，全部初始化并绑定完成。

   - BeanFactory和ApplicationContext继承关系。

     <div align="center">
     <img src="https://github.com/XiaoHuaShiFu/img/blob/master/BeanFactory%E5%92%8CApplicationContext%E7%BB%A7%E6%89%BF%E5%85%B3%E7%B3%BB.jpg?raw=true" width="75%" height="75%" style="transform: rotate(180deg);">
      </div>

   - BeanFactory：公开获取一个组装完成的对象的方法接口，也包含查询方法。

     - 如获取某个对象（getBean）、查询某个对象是否存在容器中（containsBean）、获取某个bean的状态或者类型的方法等。

3. 