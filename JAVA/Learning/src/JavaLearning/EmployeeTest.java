package JavaLearning;

import java.util.ArrayList;


public class EmployeeTest {
	
	public static void main(String[] args) {
		
		ArrayList<Employee> staff = new ArrayList<>();
		
		Manager boss = new Manager("wjx",50000,1997,5,9);
		boss.setBonus(50000);
		staff.add(boss);
		staff.add(new Employee("wjs",40000,1997,5,9));
		staff.add(new Employee("wjt",30000,1996,4,8));

		staff.set(1,new Employee("wwwwww",41221,2052,12,3));
		/*instanceof 运算符是用来在运行时指出对象是否是特定类的一个实例
		如果 object 是 class 的一个实例，则 instanceof 运算符返回 true。
		如果 object 不是指定类的一个实例，或者 object 是 null，则返回 false。 */
		if(staff.get(0) instanceof Manager) {
			Manager boss1 = (Manager) staff.get(0);
			boss1.setBonus(5000111);
		}
		
		staff.add(1,new Employee("test",30000,1996,4,8));
		staff.remove(1);
		
		
		staff.get(0).raiseSalary(100);
		staff.get(0).raiseSalary(0,1111);
		
		for(Employee e : staff) {
			System.out.println(e.getName() + ":" + e.getSalary() + 
					":" + e.getHireDay() + ":" + e.getId() + ":" + Employee.getNextId());
		}
		
		
		
		System.out.println( "  " + boss.hashCode());
		
		
	}
	
}
