package JavaLearning;

/**
 * 
 * @author XHSF
 *
 */

public class Manager extends Employee{
	
	private double bonus;
	
	public Manager(String name, double salary, int year, int month, int day) {
		//super指调用父类拥有name, salary, year, month, day参数的构造器
		super(name, salary, year, month, day);
		this.bonus = 0;
	}
	
	public void setBonus(double bonus) {
		this.bonus = bonus;
	}
	
	public double getSalary() {
		//super告诉编译调用父类的方法
		double baseSalary = super.getSalary();
		return baseSalary + this.bonus;
	}
	
	/**
	 * 检测对象是否相等
	 */
	public boolean equals(Object otherObject) {
		//如果超类的equals都检测失败，那对象就不可能相等
		if(!super.equals(otherObject)) return false;
		
		Manager other = (Manager) otherObject;
		return this.bonus == other.bonus;
	}
	
	public int hashCode() {
		return super.hashCode() + 17 * Double.hashCode(this.bonus);
	}
	
	public String toString() {
		return super.toString()
			+ "[bouns=" + this.bonus
			+ "]";
	}
	
}
