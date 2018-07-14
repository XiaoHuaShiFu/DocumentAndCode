package human;
import java.time.*;
import java.util.*;

import generic.Pair;

/**
 * {@code}
 * Employee对象
 */

public class Employee extends Person implements Comparable<Employee>,Cloneable{
	 
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
	  * return -1是小于，0是等于，1是大于
	  * 实现Comparable接口
	  */
	 final public int compareTo(Employee other) {
		 if (this.getRank() != other.getRank()) {
			 return Integer.compare(this.getRank(), other.getRank());
		 } else {
			 return -Double.compare(this.getSalary(), other.getSalary());
		 }
	 }
	 
	 
	 /**
	  * 浅拷贝
	  * 实现Cloneable()接口
	  * 并把Object.clone()本来是protected类型的
	  * 改变为public类型的从而实现可以访问
	  */
	 public Employee clone() throws CloneNotSupportedException{
		 return (Employee) super.clone();
	 }
	 
	 /**
	  * 浅拷贝
	  * 实现Cloneable()接口
	  * 并把Object.clone()本来是protected类型的
	  * 改变为public类型的从而实现可以访问
	  * 用捕获异常的方式
	  */
	 /*public Employee clone() {
		 try {
			 return (Employee) super.clone();
		 } catch (CloneNotSupportedException e) {
			 return null;
		 }
	 }*/
	 
	 /**
	  * 深拷贝
	  * 实现Cloneable()接口
	  * 并把Object.clone()本来是protected类型的
	  * 改变为public类型的从而实现可以访问
	  * 并升级为深拷贝
	  */
	 /*Date HireDay = new Date();
	 public Employee clone() throws CloneNotSupportedException{
		 Employee cloned = (Employee) super.clone();
		 //因为Date类型是可改变的，所以需要深拷贝
		 cloned.HireDay = (Date) HireDay.clone();
		 return cloned;
	 }*/
	 
	 
	 
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
	 
	 public double getSalary() {
		 return this.salary;
	 }
	 
	 public LocalDate getHireDay() {
		 return this.hireDay;
	 }
	 
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
	 
	 /**
	  * 等级 越大等级越高
	  * @return 1
	  */
	 public int getRank() {
		 return 1;
	 }
	 
	 /**
	  * 打印雇员对的名字
	  * @param p
	  */
	 public static void printBuddies(Pair<? extends Employee> p) {
		 Employee first = p.getFirst();
		 Employee second = p.getSecond();
		 System.out.println(first.getName() + " and " + second.getName() + " are buddies.");
	 }
	 
	 /**
	  * 检测集合是否包含某个元素，库函数已经实现
	  * @param c
	  * @param obj
	  * @return
	  *//*
	 public static <E> boolean contains(Collection<E> c, Object obj) {
		 for (E element : c) {
			 if(element.equals(obj)) {
				 return true;
			 }
		 }
		 return false;
	 }*/
	 
	 /**
	  * 生成hash码
	  */
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
