package xhsfutil;

public interface Nameable {
	default String getName() {
		return getClass().getName() + "_" + hashCode();
	}
}
