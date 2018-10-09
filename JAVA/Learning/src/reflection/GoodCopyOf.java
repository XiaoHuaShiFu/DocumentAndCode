package reflection;

import java.lang.reflect.Array;

public class GoodCopyOf {
	
	public static Object goodCopyOf(Object a, int newLength) {
		Class<? extends Object> cl = a.getClass();
		if (!cl.isArray()) return null;
		Class<?> componentType = cl.getComponentType();
		int length = Array.getLength(a);
		Object newArray = Array.newInstance(componentType, newLength);
		System.arraycopy(a, 0, newArray, 0, Math.min(length, newLength));
		return newArray;
	}
	
}
