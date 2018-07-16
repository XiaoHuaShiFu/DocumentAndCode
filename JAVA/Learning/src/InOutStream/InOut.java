package InOutStream;

import java.io.*;
import java.util.Arrays;

public class InOut {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		
		//输入流
		File file = new File("file\\raf.dat");
		FileInputStream in = new FileInputStream(file);
		
		//读取一个byte
		int b = in.read();
		System.out.println(b);
		//读取一个byte数组
		byte[] bytes = new byte[12];
		in.read(bytes);
		System.out.println(Arrays.toString(bytes));
		
		//从byte数组的某一位开始读取某一个数量的字节数
		byte[] bytes1 = new byte[12];
		in.read(bytes1,5,7);
		System.out.println(Arrays.toString(bytes1));
		//in.close();
		
		//////////////////////////////////////////////////////////////////////////////
		
		//输出流
		FileOutputStream out = new FileOutputStream(file);
		//在末尾追加的形式
//		FileOutputStream out = new FileOutputStream(file,true);
		//写出一个byte
		out.write(50);
		//写出一个byte数组
		String str = "123wjx.";
		byte[] bytes2 = str.getBytes();
		out.write(bytes2);
		//从byte数组的指定位置写入指定长度的byte
		out.write(bytes2,3,3);
		//out.close();
		
		////////////////////////////////////////////////////////////////////////////////
		
		//DataOutputStream可以一次写入int long string等数据类型
		File dosFile = new File("file\\dosFile.dat");
		FileOutputStream dosOut = new FileOutputStream(dosFile);
		DataOutputStream dos = new DataOutputStream(dosOut);
		dos.writeInt(10);
		dos.writeDouble(10.5);
		//采用utf-8
		dos.writeUTF("中国");
		//采用utf-16be
		dos.writeChars("中文");
		//dos.close();
		
		//DataInputStream可以一次读取int long string等数据类型
		FileInputStream disIn = new FileInputStream(dosFile);
		DataInputStream dis = new DataInputStream(disIn);
		int a = dis.readInt();
		Double c = dis.readDouble();
		System.out.println(a + c);
		//dis.close();
		
		///////////////////////////////////////////////////////////////////////
		IOUtils.printHexByByteArray("C:\\Users\\lenovo\\Desktop\\Github\\DocumentAndCode\\JAVA"
				+ "\\Learning\\src\\dataStructures\\Test.java");
		File destFile = new File("file\\destFile.dat");
		IOUtils.copyFile(file, destFile);
		
	}

}
