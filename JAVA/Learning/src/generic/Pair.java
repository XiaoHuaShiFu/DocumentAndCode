package generic;

import java.util.function.Supplier;

/**
 * 一对相同变量的泛型定义方法
 * @author lenovo
 *
 * @param <T>
 */

public class Pair<T>{
	private T first;
	private T second;
	
	public Pair() {
		this.first = null;
		this.second = null;
	}
	
	
	public Pair(T first, T second) {
		this.first = first;
		this.second = second;
	}
	
	
	/**
	 * 实现实例化的方法
	 * @param constr
	 * @return
	 */
	public static <T> Pair<T> makePair(Supplier<T> constr){
		return new Pair<>(constr.get(),constr.get());
	}
	
	
	
	
	
	public T getFirst() {
		return this.first;
	}
	
	public T getSecond() {
		return this.second;
	}
	
	public void setFirst(T newValue) {
		this.first = newValue;
	}
	
	public void setSecond(T newValue) {
		this.second = newValue;
	}
	
	
	
}
