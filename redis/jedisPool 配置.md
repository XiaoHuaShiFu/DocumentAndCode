先配置redis.properties在配置redis.xml

redis.properties

```properties
redis.url=120.78.182.93
redis.port=6379
redis.password=scauaieredis2019!
redis.timeout=100
redis.maxTotal=30
redis.maxIdle=10
```

redis.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--把这个文件的参量分离到classpath:redis.properties-->
    <bean id="jedisPropertyConfigurer" class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
        <property name="order" value="2"/>
        <property name="ignoreUnresolvablePlaceholders" value="true"/>
        <property name="locations">
            <list>
                <value>classpath:redis.properties</value>
            </list>
        </property>
        <property name="fileEncoding" value="utf-8"/> <!--指定编码-->
    </bean>

    <!-- jedisPool 配置文件 -->
    <bean id="jedisPoolConfig" class="redis.clients.jedis.JedisPoolConfig">
        <property name="maxTotal" value="${redis.maxTotal}" />
        <property name="maxIdle" value="${redis.maxIdle}" />
    </bean>

    <!-- jedisPool bean -->
    <bean id="jedisPool" class="redis.clients.jedis.JedisPool">
        <constructor-arg ref="jedisPoolConfig" />
        <constructor-arg value="${redis.url}" />
        <constructor-arg value="${redis.port}" />
        <constructor-arg value="${redis.timeout}" />
        <constructor-arg value="${redis.password}" />
    </bean>
</beans>
```