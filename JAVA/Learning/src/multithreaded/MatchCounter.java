package multithreaded;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * This task counts the files in a directory and its subdirectories that contain a given keyword;
 * @author lenovo
 *
 */

public class MatchCounter implements Callable<Integer>{
	
	private File directory;
	private String keyword;
	
	public MatchCounter(File directory, String keyword) {
		this.directory = directory;
		this.keyword = keyword;
	}
	
	public Integer call() {
		int count = 0;
		try {
			File[] files = directory.listFiles();
			List<Future<Integer>> results = new ArrayList<>();
			
			for (File file : files) {
				if (file.isDirectory()) {
					MatchCounter counter = new MatchCounter(file, this.keyword);
					//新建一个FutureTask线程
					FutureTask<Integer> task = new FutureTask<>(counter);
					//把线程加入线程列表
					results.add(task);
					//启动线程
					Thread t = new Thread(task);
					t.start();
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
