package generic;

public class PairAlg {
	
	/**
	 * 检测Pair是否包含一个null引用
	 * @param p
	 * @return
	 */
	public static boolean hasNulls(Pair<?> p) {
		return p.getFirst() == null || p.getSecond() == null;
	}
	/**
	 * 泛型版本
	 */
	/*public static <T> boolean hasNulls(Pair<T> p) {
		return p.getFirst() == null || p.getSecond() == null;
	}*/
	
	/**
	 * 通配符捕获
	 * @param p
	 */
	public static void swap(Pair<?> p) {
		swapHelper(p);
	}
	
	public static <T> void swapHelper(Pair<T> p) {
		T t = p.getFirst();
		p.setSecond(p.getSecond());
		p.setFirst(t);
	}
	
}
