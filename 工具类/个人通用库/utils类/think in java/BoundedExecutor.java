package util;

import java.util.concurrent.*;

/**
 * 描述: 用无界队列并使用信号量好控制正在执行的和等待执行的任务数量
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-28 15:20
 */
public class BoundedExecutor {

    private final Executor executor;
    private final Semaphore semaphore;

    public BoundedExecutor(Executor executor, int bound) {
        this.executor = executor;
        this.semaphore = new Semaphore(bound);
    }

    /**
     * 执行任务的方法
     * @param command
     * @throws InterruptedException
     */
    public void submitTask(final Runnable command) throws InterruptedException {
        semaphore.acquire();
        try {
            executor.execute(() -> {
                try {
                    command.run();
                } finally {
                    semaphore.release();
                }
            });
        } catch (RejectedExecutionException e) {
            semaphore.release();
        }
    }

}
