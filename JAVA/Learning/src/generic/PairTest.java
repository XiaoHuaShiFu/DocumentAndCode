package generic;

import human.Employee;
import human.Manager;

public class PairTest {

	public static void main(String[] args) {
		String[] words = {"Mary","had","a","little","lamb"};
		Pair<String> maxmin = ArrayAlg.minmax(words);
		System.out.println(maxmin.getFirst());
		System.out.println(maxmin.getSecond());
		
		String[] arr = {"Mary","had","a","little","lamb"};
		System.out.println(ArrayAlg.min(arr));
		
		Pair<String> maxmin1 = ArrayAlg.<String>minmax(words);
		System.out.println(maxmin1.getFirst());
		System.out.println(maxmin1.getSecond());
		
		
		Employee[] staff = new Employee[3];
		Manager boss = new Manager("wjx11",50000,1997,5,9);
		boss.setBonus(50000);
		staff[0] = boss;
		staff[1] = new Employee("wjs",50000,1997,5,9);
		staff[2] = new Employee("wjt222",50000,1996,4,8);
		Pair<Employee> employee = ArrayAlg.<Employee>minmax(staff);
		
		/**
		 * 运用方法实例化泛型对象
		 */
		Pair<String> str = Pair.makePair(String::new);
		str = ArrayAlg.minmax(words);
		System.out.println(str.getFirst());
		System.out.println(str.getSecond());
		
		
		String[] strrr = ArrayAlg.minmax(String[]::new, "had1","Mary1","a1","little1","lamb1");
		System.out.println(strrr[0]);
		System.out.println(strrr[1]);
	}

}
