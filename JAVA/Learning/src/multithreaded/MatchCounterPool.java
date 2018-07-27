package multithreaded;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * This task counts the files in a directory and its subdirectories that contain a given keyword;
 * @author lenovo
 *
 */

public class MatchCounterPool implements Callable<Integer>{
	
	private File directory;
	private String keyword;
	//新建一个线程吃
	private ExecutorService pool;
	private int count;
	
	public MatchCounterPool(File directory, String keyword, ExecutorService pool) {
		this.directory = directory;
		this.keyword = keyword;
		this.pool = pool;
	}
	
	public Integer call() {
		count = 0;
		try {
			File[] files = directory.listFiles();
			List<Future<Integer>> results = new ArrayList<>();
			
			for (File file : files) {
				if (file.isDirectory()) {
					MatchCounterPool counter = new MatchCounterPool(file, this.keyword, this.pool);
					//加入线程池，会尽快自动执行
					Future<Integer> result = pool.submit(counter);
					//把线程加入线程列表
					results.add(result);
				} else {
					if (search(file)) count++;
				}
			}
			
			for (Future<Integer> result : results) {
				try {
					count += result.get();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
		}
		return count;
	}
	
	public boolean search(File file) {
		try {
			try (Scanner in = new Scanner(file, "UTF-8")) {
				boolean found = false;
				while (!found && in.hasNextLine()) {
					String line = in.nextLine();
					if (line.contains(keyword)) found = true;
				}
				return found;
			}
		} catch (IOException e) {
			return false;
		}
	}
	
}

