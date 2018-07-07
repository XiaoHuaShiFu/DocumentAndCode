package xhsfutil;

/**
 * 可返回一个带有min和max的对象
 * 可调用getFirst和getSecond获取min和max的指
 * @author lenovo
 *
 */


public class ArrayAlg {
	
	//静态内部类，不需要使用外围类对象时使用
	public static class Pair{
		
		private double first;
		private double second;
		
		//静态内部类可实现构造器
		public Pair(double first, double second) {
			this.first = first;
			this.second = second;
		}
		
		public double getFirst() {
			return this.first;
		}
		
		public double getSecond() {
			return this.second;
		}
		
	}
	
	public static Pair minmax(double[] values) {
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for(double value : values) {
			if(min > value) min = value;
			if(max < value) max = value;
		}
		return new Pair(min,max);
	}
	
}
