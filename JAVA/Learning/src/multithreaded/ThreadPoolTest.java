package multithreaded;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ThreadPoolTest {

	public static void main(String[] args) {
		
		try (Scanner in = new Scanner(System.in)) {
			System.out.print("Enter base directory: ");
			String directory = in.nextLine();
			System.out.printf("Enter keyword: ");
			String keyword = in.nextLine();
			
			//新建一个线程池
//			ExecutorService pool = Executors.newFixedThreadPool(10);
			ExecutorService pool = Executors.newCachedThreadPool();
			//新建一个对象
			MatchCounterPool counter = new MatchCounterPool(new File(directory), keyword, pool);
			//加入线程池，接受线程的放回结果
			Future<Integer> result = pool.submit(counter);
			
			
			try {
				System.out.println(result.get() + "matching files");
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				
			}
			
			//关闭线程池
			pool.shutdown();
			//线程池里最大同时打开的线程数
			int largestPoolSize = ((ThreadPoolExecutor) pool).getLargestPoolSize();
			System.out.println("largest pool size=" + largestPoolSize);
		}
		
	}

}
