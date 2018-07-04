package human;

import java.util.Arrays;

public class EmployeeTest {
	
	public static void main(String[] args) {
		
		//用list的操作(变长数组）
/*		ArrayList<Employee> staff = new ArrayList<>();
		staff.add(1,new Employee("test",30000,1996,4,8));
		staff.set(1,new Employee("wwwwww",41221,2052,12,3));
		staff.get(0);
		staff.remove(1);*/
		
		Employee[] staff = new Employee[3];
		
		
		
		Manager boss = new Manager("wjx",50000,1997,5,9);
		boss.setBonus(50000);
		staff[0] = boss;
		staff[1] = new Employee("wjs",40000,1997,5,9);
		staff[2] = new Employee("wjt",300000,1996,4,8);

		/*instanceof 运算符是用来在运行时指出对象是否是特定类的一个实例
		如果 object 是 class 的一个实例，则 instanceof 运算符返回 true。
		如果 object 不是指定类的一个实例，或者 object 是 null，则返回 false。 */
		if(staff[0] instanceof Manager) {
			Manager boss1 = (Manager) staff[0];
			boss1.setBonus(50001);
		}
		
//		staff[0].raiseSalary(100);
//		staff[0].raiseSalary(0,1111);
		
		Arrays.sort(staff);
		
		for(Employee e : staff) {
			System.out.println(e.getName() + ":" + e.getSalary() + 
					":" + e.getHireDay() + ":" + e.getId() + ":" + Employee.getNextId());
		}
		
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
		
		
	}
	
}
