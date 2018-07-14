package generic;

import java.util.function.IntFunction;

public class ArrayAlg {
	
	/**
	 * 一个应用泛型类的方法
	 * @param arr
	 * @return
	 */
	public static Pair<String> minmax(String[] arr) {
		if(arr == null || arr.length == 0) {
			return null;
		}
		String min = arr[0];
		String max = arr[0];
		for(int i = 1; i < arr.length; i++) {
			if(min.compareTo(arr[i]) > 0) min = arr[i];
			if(max.compareTo(arr[i]) < 0) max = arr[i];
		}
		return new Pair<>(min,max);
	}
	
	
	/**
	 * 一个使用泛型类的方法
	 * @param arr
	 * @return
	 */
	public static <T extends Comparable<T>> Pair<T> minmax(T[] arr) {
		if(arr == null || arr.length == 0) {
			return null;
		}
		T min = arr[0];
		T max = arr[0];
		for(int i = 1; i < arr.length; i++) {
			if(min.compareTo(arr[i]) > 0) min = arr[i];
			if(max.compareTo(arr[i]) < 0) max = arr[i];
		}
		return new Pair<>(min,max);
	}
	
	
	/**
	 * 一个使用泛型类的方法
	 * 可以控制长度
	 * @param arr
	 * @return
	 */
	@SafeVarargs
	public static <T extends Comparable<T>> T[] minmax(IntFunction<T[]> constr, T... arr) {
		T[] maxmin = constr.apply(2);
		if(arr == null || arr.length == 0) {
			return null;
		}
		T min = arr[0];
		T max = arr[0];
		for(int i = 1; i < arr.length; i++) {
			if(min.compareTo(arr[i]) > 0) min = arr[i];
			if(max.compareTo(arr[i]) < 0) max = arr[i];
		}
		maxmin[0] = min;
		maxmin[1] = max;
		return maxmin;
	}
	
	
	
	/**
	 * 一个带泛型的方法
	 * @param arr
	 * @return
	 */
	@SafeVarargs//用于消除泛型数组的限制
	public static <T> T getMiddle(T... arr) {
		return arr[arr.length / 2];
	}
	
	
	/**
	 * 求数组里的最小值，只能给实现了Comparable的数组调用
	 * @param arr
	 * @return
	 */
	public static <T extends Comparable<T>> T min(T[] arr) {
		if(arr == null || arr.length == 0) {
			return null;
		}
		T smallest = arr[0];
		for(int i = 1; i < arr.length; i++) {
			if(smallest.compareTo(arr[i]) > 0) {
				smallest = arr[i];
			}
		}
		return smallest;
	}
	
	
}
