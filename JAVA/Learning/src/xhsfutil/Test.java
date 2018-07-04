package xhsfutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Test {
	
	public static void main(String[] args) {
		
		//建立LengthComparator()的实例
		Comparator<String> comp = new LengthComparator();
		String words = "wjxwdadasd";
		String word =  "wjxwd3123a";
		if(comp.compare(words, word) == 0) {
			System.out.println("test");
		}
		

		//把LengthComparable对象传递给sort方法
		String[] friends = {"peter","pau3131l","mary"};
//		Arrays.sort(friends,new LengthComparator());
		Arrays.sort(friends,(first, second) -> first.length() - second.length());
		System.out.println(friends[0] + " " + friends[1] + " "  + friends[2] + " ");
		
		//////////////////////////////////////////////////////////////
		String s = "Ok";
		StringBuilder sb = new StringBuilder(s);
		System.out.println(s.hashCode() + "  " + sb.hashCode());
		String t = "Ok";
		StringBuilder tb = new StringBuilder(t);
		System.out.println(t.hashCode() + "  " + tb.hashCode());
		
		/////////////////////////////////////////////////////////////
		ArrayList<Integer> list = new ArrayList<>();
		list.add(3312);
		list.add(6);

		int n = list.get(1);
		String S = list.get(0).toString();
		int X = Integer.parseInt(S);
//		System.out.println(true ?  list.get(0) : x.doubleValue());
		System.out.println(X);
		if(list.get(1) == list.get(0)) {
			System.out.println(n);
		}
		
		//////////////////////////////////////////////////////////////
		Size size = Enum.valueOf(Size.class, "SMALL");
		System.out.println(size.compareTo(Size.EXTRA_LARGE));
		
		//////////////////////////////////////////////////////////
		//数组自带public的clone方法
		int[] arr = {1,2,3,4,5,6,7};
		int[] cloned = arr.clone();
		cloned[5] = 100;
		System.out.println(arr[5] +" " + cloned[5]);
		
		///////////////////////////////////////////////////////
		ArrayList<Integer> arrList = new ArrayList<>();
		
		arrList.add(0,new Integer(1));
		arrList.add(1,new Integer(2));
		arrList.add(2,new Integer(0));
		arrList.add(3,new Integer(3));
		arrList.add(4,new Integer(-6));
		for(int element : arrList) {
			System.out.println(element);
		}
		
		arrList.sort((first, second) -> first - second);
		for(int element : arrList) {
			System.out.println(element);
		}
	}
	
}
	

	