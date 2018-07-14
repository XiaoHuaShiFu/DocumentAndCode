package human;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Comparator.*;

import generic.Pair;

public class EmployeeTest {
	
	public static void main(String[] args) {
		
		//用list的操作(变长数组）
/*		ArrayList<Employee> staff = new ArrayList<>();
		staff.add(1,new Employee("test",30000,1996,4,8));
		staff.set(1,new Employee("wwwwww",41221,2052,12,3));
		staff.get(0);
		staff.remove(1);*/
		
		Employee[] staff = new Employee[3];
		
		
		
		
		Manager boss = new Manager("wjx11",50000,1997,5,9);
		boss.setBonus(50000);
		staff[0] = boss;
		staff[1] = new Employee("wjs",50000,1997,5,9);
		staff[2] = new Employee("wjt222",50000,1996,4,8);

//-----------------------------------------------------------------------------------------------------//
		/*instanceof 运算符是用来在运行时指出对象是否是特定类的一个实例
		如果 object 是 class 的一个实例，则 instanceof 运算符返回 true。
		如果 object 不是指定类的一个实例，或者 object 是 null，则返回 false。 */
		if(staff[0] instanceof Manager) {
			Manager boss1 = (Manager) staff[0];
			boss1.setBonus(50001);
		}
		
		
//-----------------------------------------------------------------------------------------------------//
		
		//用自己在Employee里面实现的方法compareTo去排序
		Arrays.sort(staff);
		for(Employee e : staff) {
			System.out.println(e.getName() + ":" + e.getSalary() + 
					":" + e.getHireDay() + ":" + e.getId() + ":" + Employee.getNextId());
		}
		System.out.println("-----------------------");//分割线
		
		
		//利用Comparator的comparing方法进行排序
		Arrays.sort(staff, Comparator.comparing(Employee::getSalary));
		for(Employee e : staff) {
			System.out.println(e.getName() + ":" + e.getSalary() + 
					":" + e.getHireDay() + ":" + e.getId() + ":" + Employee.getNextId());
		}
		System.out.println("-----------------------");//分割线
		
		
		//利用Comparator的comparing和thenComparing方法进行排序
		//如果comparing比较结果相等，则调用thenComparing方法
		Arrays.sort(staff, Comparator.comparing(Employee::getSalary).thenComparing(Employee::getName));
		for(Employee e : staff) {
			System.out.println(e.getName() + ":" + e.getSalary() + 
					":" + e.getHireDay() + ":" + e.getId() + ":" + Employee.getNextId());
		}
		System.out.println("-----------------------");//分割线
		
		
		//利用Comparator的comparing方法附加lambda表达式进行排序
		Arrays.sort(staff, Comparator.comparing(Person::getName, (first,second)
				-> Integer.compare(first.length(), second.length())));
		for(Employee e : staff) {
			System.out.println(e.getName() + ":" + e.getSalary() + 
					":" + e.getHireDay() + ":" + e.getId() + ":" + Employee.getNextId());
		}
		System.out.println("-----------------------");//分割线
		
		
		//利用Comparator的comparingDouble方法附加lambda表达式进行排序（有int，long，double）
		Arrays.sort(staff, Comparator.comparingDouble(p -> p.getSalary()));
		for(Employee e : staff) {
			System.out.println(e.getName() + ":" + e.getSalary() + 
					":" + e.getHireDay() + ":" + e.getId() + ":" + Employee.getNextId());
		}
		System.out.println("-----------------------");//分割线
		
		
		//利用Comparator的comparing方法附加nullsFirst适配器进行排序（有nullsFirst，nullsLast）
		Arrays.sort(staff, Comparator.comparing(Employee::getName, Comparator.nullsFirst(
				(first,second)-> Integer.compare(second.length(), first.length()))));
		for(Employee e : staff) {
			System.out.println(e.getName() + ":" + e.getSalary() + 
							":" + e.getHireDay() + ":" + e.getId() + ":" + Employee.getNextId());
		}
		
		System.out.println("-----------------------");//分割线
	
		
//-----------------------------------------------------------------------------------------------------//
		
		Employee Staff;
		try {
			Staff = staff[1].clone();
			System.out.println(Staff.getSalary() + " ");
			Staff.raiseSalary(100);
			System.out.println(Staff.getSalary() + " ");
			System.out.println(staff[1].getSalary() + " ");
		} catch (CloneNotSupportedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//-----------------------------------------------------------------------------------------------------//
		
		Manager[] manager = new Manager[3];
		manager[0] = new Manager("wjx11",50000,1997,5,9);
		manager[1] = new Manager("wjs",50000,1997,5,9);
		
		manager[2] = new Manager("wjt222",50000,1996,4,8);
		Pair<Manager> result1 = new Pair<>();
		Manager.maxminBonus(manager, result1);
		
//-----------------------------------------------------------------------------------------------------//
		Collection<Employee> staff1 = new ArrayList<Employee>();
		Manager boss1 = new Manager("wjx11",50000,1997,5,9);
		boss.setBonus(50000);
		staff1.add(boss1);
		staff1.add(new Employee("wjs",50000,1997,5,9));
		staff1.add(new Employee("wjt222",50000,1996,4,8));
		/**
		 * 看集合是否包含元素obj
		 */
		System.out.println(staff1.contains(boss1));
		
	}
	
}
