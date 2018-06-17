
package JavaLearning;

public class EmployeeTest {
	
	public static void main(String[] args) {
		
		Employee[] staff = new Employee[3];
		
		staff[0] = new Employee("wjx",50000,1998,6,10);
		staff[1] = new Employee("wjs",40000,1997,5,9);
		staff[2] = new Employee("wjt",30000,1996,4,8);
		
		
		
		staff[0].raiseSalary(100);
		staff[0].raiseSalary(0,1111);
		
		System.out.println(staff[0].getName() + ":" + staff[0].getSalary() + 
				":" + staff[0].getHireDay() + ":" + staff[0].getId() + ":" + Employee.getNextId());
		
	}
	
}
