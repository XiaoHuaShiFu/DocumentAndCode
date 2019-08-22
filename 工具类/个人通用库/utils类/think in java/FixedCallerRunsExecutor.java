package util;

import java.util.concurrent.*;

/**
 * 描述:固定大小的线程池，并采用有界队列以及“调用者运行”饱和策略
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-28 15:20
 */
public class FixedCallerRunsExecutor {

    ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(10));
    public FixedCallerRunsExecutor() {
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
