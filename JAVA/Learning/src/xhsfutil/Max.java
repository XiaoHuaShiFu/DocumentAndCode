package xhsfutil;

import java.util.*;

/**
 * 
 * @author XHSF
 *
 */

public class Max {
	
	public static double max(double... values) {
		double largest = Double.NEGATIVE_INFINITY;
		for (double v : values) {
			if (v > largest) {
				largest = v;
			}
		}
		return largest;
	}
	
	/**
	 * 使用泛型实现求最大值
	 * @param c
	 * @return
	 */
	public static <T extends Comparable<T>> T max(Collection<T> c) {
		if(c.isEmpty()) throw new NoSuchElementException();
		Iterator<T> iter = c.iterator();
		T largest = iter.next();
		while (iter.hasNext()) {
			T next = iter.next();
			if(largest.compareTo(next) < 0) {
				largest = next;
			} 
		}
		return largest;
	}
	 
}