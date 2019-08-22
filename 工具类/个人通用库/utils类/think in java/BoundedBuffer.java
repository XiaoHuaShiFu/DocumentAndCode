package util;

import com.xuexi.concurrency.ThreadSafe;

/**
 * 描述: 线程安全的阻塞缓冲队列
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-31 0:37
 */
@ThreadSafe
public class BoundedBuffer<V> extends BaseBoundedBuffer<V> {

    public BoundedBuffer(int size) {
        super(size);
    }

    public synchronized void put (V v) throws InterruptedException {
        while (isFull()) {
            wait();
        }
        boolean wasEmpty = isEmpty();
        doPut(v);
        if (wasEmpty) {
            notifyAll();
        }
    }

    public synchronized V take() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }
        boolean wasFull = isFull();
        V v = doTake();
        if (wasFull) {
            notifyAll();
        }
        return v;
    }

}
