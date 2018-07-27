package human;

import generic.Pair;
import generic.PairAlg;

/**
 * 
 * @author XHSF
 *
 */

public class Manager extends Employee{
	
	/**
	 * 序列化id
	 */
	private static final long serialVersionUID = -4400282544385571100L;
	private double bonus;
	private Employee secretary;
	
	public Manager(String name, double salary, int year, int month, int day) {
		//super指调用父类拥有name, salary, year, month, day参数的构造器
		super(name, salary, year, month, day);
		this.bonus = 0;
		
	}
	
	public void setBonus(double bonus) {
		this.bonus = bonus;
	}
	
	public void setSecretary(Employee secretary) {
		this.secretary = secretary;
	}
	
	public double getBonus() {
		return this.bonus;
	}
	
	public double getSalary() {
		//super告诉编译调用父类的方法
		double baseSalary = super.getSalary();
		return baseSalary + this.bonus;
	}
	
	public Employee getSecretary() {
		return this.secretary;
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
	
	/**
	 * 返回奖金最多的两个经理
	 * @param arr
	 * @param result
	 */
	public static void minmaxBonus(Manager[] arr, Pair<? super Manager> result) {
		if(arr.length == 0) return;
		Manager min = arr[0];
		Manager max = arr[0];
		for(int i = 1; i < arr.length; i++) {
			if(min.getBonus() > arr[i].getBonus()) {
				min = arr[i];
			}
			if(min.getBonus() < arr[i].getBonus()) {
				max = arr[i];
			}
		}
		result.setFirst(min);
		result.setSecond(max);
	}
	
	public static void maxminBonus(Manager[] staff, Pair<? super Manager> result) {
		minmaxBonus(staff, result);
		PairAlg.swap(result);
	}
	
	public int hashCode() {
		return super.hashCode() + 17 * Double.hashCode(this.bonus);
	}
	
	public String toString() {
		return super.toString()
			+ "[bouns=" + this.bonus
			+ ",secretary=" + this.secretary
			+ "]";
	}
	
	
	/**
	  * 等级 越大等级越高
	  * @return 2
	  */
	public int getRank() {
		 return 2;
	 }

	
	
}
