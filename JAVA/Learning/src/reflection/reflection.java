package reflection;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.sun.org.apache.bcel.internal.generic.NEW;

import human.Employee;

public class reflection {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException,
	SecurityException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException {
		Employee employee = new Employee();
		@SuppressWarnings("unchecked")
		Class<Employee> employeeClass = (Class<Employee>) employee.getClass();
		String name1 = employeeClass.getName();
		@SuppressWarnings("unchecked")
		Class<Employee> employeeForClassName = (Class<Employee>) Class.forName(name1);
		System.out.println(employeeForClassName);
		Class<Integer> classInt = int.class;
		Class<Random> classRandom = Random.class;
		String classDoubleArray = Double[].class.getName();
		if (classRandom == Random.class) {
			System.out.println("equal");
		}
		@SuppressWarnings("deprecation")
		Employee employee2 = employee.getClass().newInstance();
		employee2.setName("wjx");
		System.out.println(employee2);
		System.out.println(classDoubleArray);
		String employeeClassName = "human.Employee";
		Employee employee3 = (Employee) Class.forName(employeeClassName).newInstance();
		employee3.setName("test");
		System.out.println(employee3);
		
		
		System.out.println("//////////////////////////////////////////////////");
		
		String name = "java.lang.Double";
		
		Class<?> cl = Class.forName(name);
		Class<?> supercl = cl.getSuperclass();
		String modifiers = Modifier.toString(cl.getModifiers());
		if (modifiers.length() > 0) {
			System.out.println(modifiers + "");
		}
		System.out.println(supercl.getName() + "");
		
		printConstructors(cl);
		printMethods(cl);
		printFields(cl);
		printFieldGet();
		System.out.println("//////////////////////////////////////////////////");
		
		Employee[] arr = new Employee[100];
		arr[0] = new Employee("test",4000,1998,1,6);
		arr[1] = new Employee("test",4000,1998,1,6);
		arr[2] = new Employee("test",4000,1998,1,6);
		System.out.println(arr.length + arr[0].toString());
		arr = Arrays.copyOf(arr, arr.length * 2);
		
		Employee[] badArr = new Employee[200];
		//System.arraycopy数组复制函数
		System.arraycopy(arr, 0, badArr, 0, 100);
		System.out.println(badArr[0].toString() + badArr[1].toString() + badArr[2].toString());
		
		System.out.println("//////////////////////////////////////////////////");
		arr = (Employee[]) GoodCopyOf.goodCopyOf(arr, 300);
		System.out.println(arr[0].toString() + arr[1] + arr[2]);
		System.out.println(Array.get(arr, 0));
		System.out.println("//////////////////////////////////////////////////");
		//getMethod()得到方法
		Method m1 = Employee.class.getMethod("getSalary");
		//method.invoke()使用方法
		double salary = (double) m1.invoke(badArr[0]);
		System.out.println(salary);
	}
	
	public static void printConstructors(Class<?> cl) {
		Constructor<?>[] constructors = cl.getDeclaredConstructors();
		
		for (Constructor<?> constructor : constructors) {
			String name = constructor.getName();
			String modifiers = Modifier.toString(constructor.getModifiers());
			System.out.print(modifiers);
			System.out.print("   " + name);
			
			Class<?>[] paramTypes = constructor.getParameterTypes();
			for (int j = 0; j < paramTypes.length; j++) {
				System.out.print("   " + paramTypes[j].getName());
			}
			System.out.println();
		}
	}
	
	public static void printMethods(Class<?> cl) {
		Method[] methods = cl.getDeclaredMethods();
		for (Method method : methods) {
			Class<?> retType = method.getReturnType();
			String name = method.getName();
			
			String modifiers = Modifier.toString(method.getModifiers());
			System.out.print(modifiers);
			System.out.print(" " + retType.getName() + " " + name);
			
			Class<?>[] paramTypes = method.getParameterTypes();
			for (int j = 0; j < paramTypes.length; j++) {
				System.out.print("  " + paramTypes[j].getName());
			}
			System.out.println();
		}
	}
	
	public static void printFields(Class<?> cl) {
		Field[] fields = cl.getDeclaredFields();
		
		
		for (Field field : fields) {
			Class<?> type = field.getType();
			String name = field.getName();
			String modifiers = Modifier.toString(field.getModifiers());
			System.out.print(modifiers + "  " + type.getName() + "  " + name );
			System.out.println();
		}
	}
	
	public static void printFieldGet() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Employee harry = new Employee("Harry Hacker",35000,1899,10,10);
		Class<?> cl = harry.getClass();
		Field field = cl.getDeclaredField("salary");
		field.setAccessible(true);
		field.set(harry, 30000);
		double har = field.getDouble(harry);
		System.out.print(harry);
 	}
	
	
	
	
	
}
