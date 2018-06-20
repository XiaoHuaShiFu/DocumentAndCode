package JavaLearning;

import java.util.*;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class Test {

	
	public static void main(String[] args) {
		
		String s = "Ok";
		StringBuilder sb = new StringBuilder(s);
		System.out.println(s.hashCode() + "  " + sb.hashCode());
		String t = "Ok";
		StringBuilder tb = new StringBuilder(t);
		System.out.println(t.hashCode() + "  " + tb.hashCode());
		ArrayList<Integer> list = new ArrayList<>();
		list.add(3312);
		list.add(6);
		Double x = 2.0;
		Integer n = list.get(1);
		String S = list.get(0).toString();
		int X = Integer.parseInt(S);
		System.out.println(true ?  list.get(0) : x.doubleValue());
		System.out.println(X);
		if(list.get(1) == list.get(0)) {
			System.out.println(n);
		}
		
		Size size = Enum.valueOf(Size.class, "SMALL");
		System.out.println(size.compareTo(Size.EXTRA_LARGE));
		
	}
	
	

}
