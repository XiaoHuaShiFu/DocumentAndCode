package xhsfutil;

public class Pair<T> {
	
	private T first;
	private T second;
	
	public Pair(T first, T second) {
		this.first = first;
		this.second = second;
	}

	public T getFirst() {
		return first;
	}

	public void setFirst(T i) {
		this.first = i;
	}

	public T getSecond() {
		return second;
	}

	public void setSecond(T second) {
		this.second = second;
	}
	
	
}
