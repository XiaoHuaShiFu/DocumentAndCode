package InOutStream;

import java.io.*;
import human.*;

/**
 * 对象序列化and反序列化流
 * ObjectInputStream类
 * ObjectOutputStream类
 * @author lenovo
 *
 */

public class ObjTest {

	public static void main(String[] args) throws IOException, ClassNotFoundException{
		String fileName = "file\\obj.dat";
		
		/**
		 * 序列化
		 */
//		FileOutputStream out = new FileOutputStream(fileName);
//		ObjectOutputStream oos = new ObjectOutputStream(out);
//		Student stu = new Student("wjx","cs",99);
//		oos.writeObject(stu);
//		oos.flush();
//		oos.close();
		
		
		/**
		 * 反序列化
		 * 无法解决构造器继承导致对象无法正常赋值的问题
		 */
//		FileInputStream in = new FileInputStream(fileName);
//		ObjectInputStream ois = new ObjectInputStream(in);
//		
//		Student stu = (Student)ois.readObject();
//		System.out.println(stu);
//		ois.close();
		
		
		//////////////////////////////////////////////////////
		Employee[] staff = new Employee[3];
		Employee harry = new Employee("harry", 50000, 1989, 10, 1);
		Manager Carl = new Manager("Carl", 80000, 1987, 12, 15);
		Manager tony = new Manager("Tony", 40000, 1990, 3, 15);
		Carl.setSecretary(harry);
		tony.setSecretary(harry);
		staff[0] = harry;
		staff[1] = Carl;
		staff[2] = tony;
		
//		ObjectOutputStream out = new ObjectOutputStream(
//				new FileOutputStream("file\\employee.dat"));
//		out.writeObject(staff);
		
		ObjectInputStream in = new ObjectInputStream(
				new FileInputStream("file\\employee.dat"));
		Employee[] newStaff = (Employee[])in.readObject();
		
		for(Employee e : newStaff) {
			System.out.println(e.toString());
		}
		
		////////////////////////////////////////////////////////
		/**
		 * 序列化机制接口Externalizable测试
		 */
//		Employee employee1 = new Employee("wjx", 60000, 1998, 6, 10);
//		ObjectOutputStream out1 = new ObjectOutputStream(
//				new FileOutputStream("file\\employee1.dat"));
//		out1.writeObject(employee1);
		
//		ObjectInputStream in1 = new ObjectInputStream(
//				new FileInputStream("file\\employee1.dat"));
//		Employee newEmployee1 = new Employee();
//		newEmployee1.readExternal(in1);
//		System.out.println(newEmployee1);
		
	}

}
