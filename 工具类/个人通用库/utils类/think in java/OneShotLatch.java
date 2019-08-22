package util;

import com.xuexi.concurrency.ThreadSafe;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-31 15:59
 */
@ThreadSafe
public class OneShotLatch {

    private final Sync sync = new Sync();

    public void signal() {
        sync.releaseShared(0);
    }

    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(0);
    }


    private class Sync extends AbstractQueuedSynchronizer {
        @Override
        protected boolean tryReleaseShared(int arg) {
            setState(1);
            return true;
        }

        @Override
        protected int tryAcquireShared(int arg) {
            return (getState() == 1) ? 1 : -1;
        }
    }

}
