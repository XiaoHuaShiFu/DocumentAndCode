package util;

import com.xuexi.concurrency.GuardedBy;
import com.xuexi.concurrency.ThreadSafe;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 描述: 线程安全的阻塞缓冲队列
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-31 0:37
 */
@ThreadSafe
public class ConditionBoundedBuffer<T> {

    protected final Lock lock = new ReentrantLock();

    private final Condition notFull = lock.newCondition();

    private final Condition notEmpty = lock.newCondition();

    @GuardedBy("this")
    private final T[] items;

    @GuardedBy("this")
    private int tail;

    @GuardedBy("this")
    private int head;

    @GuardedBy("this")
    private int count;

    public ConditionBoundedBuffer(int size) {
        this.items = (T[]) new Object[size];
    }

    public void put (T x) throws InterruptedException {
        lock.lock();
        try {
            while (count ==  items.length) {
                notFull.await();
            }
            items[tail] = x;
            if (++tail == items.length) {
                tail = 0;
            }
            ++count;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            T x = items[head];
            items[head] = null;
            if (++head == items.length) {
                head = 0;
            }
            --count;
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }

}
