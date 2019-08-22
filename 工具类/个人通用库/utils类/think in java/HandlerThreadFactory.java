package util;

import java.util.concurrent.ThreadFactory;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-19 11:36
 */
public class HandlerThreadFactory implements ThreadFactory{
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setUncaughtExceptionHandler((t1, e) -> {
            System.out.println("catch " + e);
        });
        return t;
    }
}
