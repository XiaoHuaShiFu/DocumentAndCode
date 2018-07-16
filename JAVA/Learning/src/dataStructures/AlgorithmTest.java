package dataStructures;

import java.util.*;

public class AlgorithmTest {

	public static void main(String[] args) {
		List<Integer> staff = new LinkedList<>();
		staff.add(3);
		staff.add(5);
		staff.add(1);
		staff.add(9);
		staff.add(-3);
		Collections.sort(staff);
		System.out.println(staff);
		//逆序排序
		staff.sort(Comparator.reverseOrder());
		System.out.println(staff);
		//打乱列表顺序
		Collections.shuffle(staff);
		System.out.println(staff);
		
		//二分查找
		Collections.sort(staff);
		int i = Collections.binarySearch(staff, 4);
		//把不存在的元素插到正确的位置上
		staff.add(-i - 1,4);
		System.out.println(staff);
		
		//批添加
		List<Integer> staff1 = new LinkedList<>();
		staff1.addAll(staff.subList(0, 4));
		System.out.println(staff1);
		
		//删除值大于三的所有数
		staff.removeIf(num -> num.intValue() >= 3);
		System.out.println(staff);
		
		//将输入转换成集合
		String[] arr = {"1","2","3","4","5","6","7","8","9"};
		List<String> staff3 = new LinkedList<>(Arrays.asList(arr));
		System.out.println(staff3);
		//放回一个Object类型的数组对象
		Object[] arr1 = staff3.toArray();
		//创建一个相同类型的数组
		String[] arr2 = staff3.toArray(new String[0]);
		//不会创建数组
		String[] arr3 = staff3.toArray(new String[staff3.size()]);
		System.out.println(arr3);
	}

}
