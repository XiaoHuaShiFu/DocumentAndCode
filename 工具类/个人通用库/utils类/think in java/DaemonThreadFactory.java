package util;

import java.util.concurrent.ThreadFactory;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-18 16:32
 */
public class DaemonThreadFactory implements ThreadFactory{
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    }
}
