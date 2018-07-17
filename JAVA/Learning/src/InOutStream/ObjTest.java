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
//		Student stu = new Student("wjx","cs");
//		oos.writeObject(stu);
//		oos.flush();
//		oos.close();
		
		/**
		 * 反序列化
		 * 无法解决构造器继承导致对象无法正常赋值的问题
		 */
		FileInputStream in = new FileInputStream(fileName);
		ObjectInputStream ois = new ObjectInputStream(in);
		
		Student stu = (Student)ois.readObject();
		System.out.println(stu);
		ois.close();
		
	}

}
