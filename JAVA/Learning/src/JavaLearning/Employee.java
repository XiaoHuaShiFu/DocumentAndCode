/*
 * Employee对象
 */
package JavaLearning;

import java.time.*;

public class Employee {
	
	 //实例域
	 //有final的实例除了构造时其余时候不可再被改变
	 private final String name;
	 private double salary;
	 private final LocalDate hireDay;
	 private int id;
	 //static表示整个类的所有对象只有一个nextId
	 private static int nextId = 1;
	 
	 
	 //构造器
	 public Employee(String name, double salary, int year, int month, int day) {
		 this.name = name;
		 this.salary = salary;
		 this.hireDay = LocalDate.of(year, month, day);
		 this.setId();
	 }
	 
	 //方法
	 public String getName() {
		 return this.name;
	 }
	 
	 public double getSalary() {
		 return this.salary;
	 }
	 
	 public LocalDate getHireDay() {
		 return this.hireDay;
	 }
	 
	 public void raiseSalary(double byPercent) {
		 double raise = this.salary * byPercent / 100;
		 this.salary += raise;
	 }
	 
	 //重载raiseSalary
	 public void raiseSalary(double byPercent,int money) {
		 double raise = this.salary * byPercent / 100;
		 this.salary += raise;
		 this.salary += money;
	 }
	 
	 public boolean equals(Employee other) {
		 //方法可以访问所属类的私有属性
		 //而不只是this（隐式参数）的私用属性
		 return this.name.equals(other.name);
	 }
	 
	 private void setId() {
		 this.id = nextId;
		 nextId++;
	 }
	 
	 public int getId() {
		 return this.id;
	 }
	 
	 //静态方法，可以直接通过这个类调用这个方法
	 //不可以使用this（隐式参数）
	 public static int getNextId() {
		 return nextId;
	 }
	 
}
