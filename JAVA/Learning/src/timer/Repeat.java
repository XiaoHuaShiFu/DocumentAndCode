package timer;

import java.util.function.IntConsumer;

/**
 * 重复运行某个操作n次
 */

public class Repeat {
	
	//能接受方法
	public static void repeat(int n, Runnable action) {
		for(int i = 0; i < n; i++) action.run();
	}
	
	//可接受参数的方法
	public static void repeat(int n,IntConsumer action) {
		for(int i = 0; i < n; i++) action.accept(i);
	}
	
}

