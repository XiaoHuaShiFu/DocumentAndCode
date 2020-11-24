# [dubbo超时时间的配置问题](https://www.cnblogs.com/yipihema/p/11556018.html)

## 问题

线上一个超时时间设置不合理引起的血案

## 解决方案

 

```
配置超时时间的时候，有两个地方，一个是provider提供的超时参数，一个是consumer提供的超时参数
provider的超时时间就是本系统向外提供的facade的请求超时时间，默认1000ms。consumer是指调用外部的系统接口的超时时间，默认1000ms。
provider的超时时间设置主要是为分析日志提供一些系统运行的情况，并不影响实际的调用过程，因为provider接受到一个请求时，会一直把整个处理逻辑走完，并不会管你是否设置了时间，dubbo只会在方法执行完，判断下
```

是否超时，如果超时，记一个warn日志。

```
dubbo的源码如下：
public class TimeoutFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TimeoutFilter.class);

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        long start = System.currentTimeMillis();

        Result result = invoker.invoke(invocation);//实际的dubbo接口业务处理

        long elapsed = System.currentTimeMillis() - start;
        if (invoker.getUrl() != null
 && elapsed > invoker.getUrl().getMethodParameter(invocation.getMethodName(),
                        "timeout", Integer.MAX_VALUE)) {
            if (logger.isWarnEnabled()) {
                logger.warn("invoke time out. method: " + invocation.getMethodName()
                        + "arguments: " + Arrays.toString(invocation.getArguments()) + " , url is "
 + invoker.getUrl() + ", invoke elapsed " + elapsed + " ms.");
            }
        }
        return result;
    }
    
}
```

 

 

```
consumer的超时时间就完全不一样了，当一个请求发出去，当前的请求线程处于锁等待的阻塞状态，而这个condition的唤醒条件1、超时，2、接收到provider的返回。
```

如果此时我们的provider在升级，又假使jenkins的升级脚本配置的是 kill -9（和运维确认过，是通过kill -9的方式关闭tomcat进程），那条件2是满足不了了，就只能寄希望于条件1即超时。如果此时我们的超时时间设置过大，那整个线程就处于假死状态，直至消耗完整个超时时间。

条件1、等待结果返回

private final Lock lock = new ReentrantLock();

```
private final Condition done = lock.newCondition();
done.await(timeout, TimeUnit.MILLISECONDS);
```

条件2、判断超时

```
private static class RemotingInvocationTimeoutScan implements Runnable {

    public void run() {
        while (true) {
            try {
                for (DefaultFuture future : FUTURES.values()) {
                    if (future == null || future.isDone()) {
                        continue;
                    }
                    if (System.currentTimeMillis() - future.getStartTimestamp() > future.getTimeout()) {
                        // create exception response.
  Response timeoutResponse = new Response(future.getId());
                        // set timeout status.
  timeoutResponse.setStatus(future.isSent() ? Response.SERVER_TIMEOUT : Response.CLIENT_TIMEOUT);
                        timeoutResponse.setErrorMessage(future.getTimeoutMessage(true));
                        // handle response.
  DefaultFuture.received(future.getChannel(), timeoutResponse);
                    }
                }
                Thread.sleep(30);
            } catch (Throwable e) {
                logger.error("Exception when scan the timeout invocation of remoting.", e);
            }
        }
    }
}
```

综上：

```
<dubbo:provider timeout="" >尽量设置下，log4j的日志级别需要warn级别，定期到线上去查看warn的日志（如果系统有监控最好了，比如cat，就有比较直观的统计信息），实时关注自己的系统调用情况
<dubbo:consumer timeout="" >如果设（尽量设置下，因为默认是1s，考虑的系统的调用链，1s并不是一个非常适合我们现在系统的设置），一定要设置一个合理的区间（1-5s），不要太大，不要太大，不要太大！！！
如果provider和consumer都设置了超时时间，dubbo会默认优先使用provider的配置。所以作为服务提供者，应该对自己的服务响应时间有所评
```