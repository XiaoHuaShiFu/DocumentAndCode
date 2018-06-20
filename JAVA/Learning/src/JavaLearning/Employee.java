package JavaLearning;
import java.time.*;
import java.util.*;

/**
 * {@code}
 * Employee对象
 */

public class Employee extends Person{
	 
	 //实例域
	 //有final的实例除了构造时其余时候不可再被改变
	 private double salary;
	 private LocalDate hireDay;
	 //定义类方法assignId给id域在构造之前就赋予初始值（当然id限制不被修改）
	 private final int id;
	 //static表示整个类的所有对象只有一个nextId
	 /**
	  * nextId 是这个类的公有变量
	  */
	 private static int nextId;
	 
	 
	 //初始化块
	 //id的起始值赋予一个随机数
	 static {
		 Random generator = new Random();
		 nextId = generator.nextInt(10000);
	 }
	 
	 //在代码块中初始化初始值
	 {
		 this.id = nextId;
		 nextId++;
	 }
	 
	 /**
	  * 
	  * @param name 名字
	  * @param salary 工资
	  * @param year 入职年份
	  * @param month 入职月份
	  * @param day 入职日
	  */
	 public Employee(String name, double salary, int year, int month, int day) {
		 super(name);
		 this.salary = salary;
		 this.hireDay = LocalDate.of(year, month, day);
/*		 this.setId();*/
	 }
	 
	 //无参构造器（全为默认值）
//	 public Employee() {
//	 }
	 
	 
	 /**
	  * 
	  * @return Employee's name
	  */
//	 public String getName() {
//		 return this.name;
//	 }
	 
	 public double getSalary() {
		 return this.salary;
	 }
	 
	 public LocalDate getHireDay() {
		 return this.hireDay;
	 }
	 
	 /**
	  * 
	  * @param byPercent 工资提升的百分百，浮点型
	  */
	 public void raiseSalary(double byPercent) {
		 double raise = this.salary * byPercent / 100;
		 this.salary += raise;
	 }
	 
	 //重载raiseSalary
	 /**
	  * 
	  * @param byPercent 工资提升的百分百，浮点型
	  * @param money 工资提升的数额，浮点型
	  */
	 public void raiseSalary(double byPercent,double money) {
		 double raise = this.salary * byPercent / 100;
		 this.salary += raise;
		 this.salary += money;
	 }
	 
	 /**
	  * 判断两个对象是否相等
	  * @param other 另外一个对象
	  * @return true or false 
	  */
	 //@Override 该方法必须覆盖其超类Object的方法，否则将报错
	 @Override public boolean equals(Object otherObject) {
		 //方法可以访问所属类的私有属性
		 //而不只是this（隐式参数）的私用属性
/*		 String ownName = this.getName();
		 String otherName = other.getName();
		 return ownName.equals(otherName);*/
		 
		 //先直接看两个对象是不是具有相同引用
		 if(this == otherObject) {
			 return true;
		 }
		 
		 //判断另外是否为null
		 if(otherObject == null) {
			 return false;
		 }
		 
		 //判断两个对象是否是属于同一个类
		 if(getClass() != otherObject.getClass()) {
			 return false;
		 }
		 
		 //判断值是否全部相等
		 //强制转换为相同的类（因为由上面知道两个对象所属的类相同）
		 Employee other = (Employee) otherObject;
		 String thisName = this.getName();
		 String otherName = other.getName();
		 /*return thisName.equals(otherName)
				 && this.salary == other.salary
				 && this.hireDay.equals(other.hireDay);*/
		 //Objects.equals(,)考虑到两个参数可能为null的情况
		 return Objects.equals(thisName, otherName)
				 && this.salary == other.salary
				 && this.hireDay.equals(other.hireDay);
		 
	 } 
	 
	 
/*	 private void setId() {
		 this.id = nextId;
		 nextId++;
	 }*/
	 
/*	 private static int assignId(){
		 return nextId++;
	 }*/
	 
	 public int getId() {
		 return this.id;
	 }
	 
	 /**
	  * 静态方法，可以直接通过这个类调用这个方法
	  * 不可以使用this（隐式参数）
	  * @return nextId
	  */
	 public static int getNextId() {
		 return nextId;
	 }
	 
	 public String getDescription() {
			return "a employee id is " + this.id;
	 }
	 
	 public int hashCode() {
		/* return 7 * Objects.hashCode(this.getName())
			+ 11 * Double.hashCode(this.salary)
			+ 13 * Objects.hashCode(this.hireDay);*/
		 return Objects.hash(this.getName(),this.salary,this.hireDay);
	 }
	 
	 public String toString() {
		 return getClass().getName()
			+ "[name=" + this.getName()
		 	+ ",salary=" + this.salary
		 	+ ",hireDay=" + this.hireDay
		 	+ "]";
	 }
	 
}
