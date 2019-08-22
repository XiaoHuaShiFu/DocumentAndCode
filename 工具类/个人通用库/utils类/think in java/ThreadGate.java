package util;

import com.xuexi.concurrency.GuardedBy;
import com.xuexi.concurrency.ThreadSafe;

/**
 * 描述: 使用wait和notifyAll来实现可重新关闭的阀门
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-31 15:04
 */
@ThreadSafe
public class ThreadGate {
    @GuardedBy("this")
    private boolean isOpen;

    @GuardedBy("this")
    private int generation;

    public synchronized void close() {
        isOpen = false;
    }

    public synchronized void open() {
        ++generation;
        isOpen = true;
        notifyAll();
    }

    public synchronized void await() throws InterruptedException {
        int arrivalGeneration = generation;
        while (!isOpen && arrivalGeneration == generation) {
            wait();
        }
    }

}
