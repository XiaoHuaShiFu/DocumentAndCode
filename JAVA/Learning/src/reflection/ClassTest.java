package reflection;

import xhsfutil.Pair;

public class ClassTest {

	@SuppressWarnings("unchecked")
	public static <T> void main(String[] args) throws InstantiationException, IllegalAccessException {
//		Pair<T> stringPair = (Pair<T>) makePair(String.class);
//		stringPair.setFirst((T)"wjx");
//		stringPair.setSecond((T)"wjsx");
//		System.out.println("" + stringPair.getFirst() + stringPair.getSecond());
		
		
		
		
	}
	
	public static <T> void test() throws InstantiationException, IllegalAccessException {
		Class<Integer> class1 = Integer.class;
		System.out.println(class1);
	}
	
	
	@SuppressWarnings({ "deprecation" })
	public static <T> Pair<T> makePair(Class<T> c) throws InstantiationException, IllegalAccessException {
		return new Pair<T>(c.newInstance(), c.newInstance());
	}
	
}
