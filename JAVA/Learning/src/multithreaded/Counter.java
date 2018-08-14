package multithreaded;

import java.util.concurrent.*;
import java.util.function.*;

public class Counter extends RecursiveTask<Integer> {
	
	/**
	 * serialId
	 */
	private static final long serialVersionUID = 1721864248726688276L;
	public static final int THRESHOLD = 1000;
	private double[] values;
	private int from;
	private int to;
	private DoublePredicate filter;
	
	public Counter(double[] values, int from, int to, DoublePredicate filter) {
		this.values = values;
		this.from = from;
		this.to = to;
		this.filter = filter;
	}
	
	protected Integer compute() {
		if (to - from < THRESHOLD) {
			int count = 0;
			for (int i = from; i < to; i++) {
				if (filter.test(values[i])) count++;
			}
			return count;
		} else {
			int mid = (from + to) / 2;
			Counter first = new Counter(values, from, mid , filter);
			Counter second = new Counter(values, mid, to, filter);
			invokeAll(first, second);
			return first.join() + second.join();
		}
	}
	
}
