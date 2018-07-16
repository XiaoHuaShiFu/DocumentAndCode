package InOutStream;

import java.io.*;

public class Test {

	public static void main(String[] args) throws IOException {
		/*File file = new File("C:\\Users\\lenovo\\Desktop\\Github\\"
				+ "DocumentAndCode\\JAVA\\Learning\\bin\\InOutStream\\test.txt");
		@SuppressWarnings("resource")
		FileInputStream in = new FileInputStream(file);
		
		int bytesAvailable = in.available();
		if(bytesAvailable > 0) {
			byte[] data = new byte[bytesAvailable];
			in.read(data);

			System.out.println(data[0]);
		}
		
		@SuppressWarnings("resource")
		FileOutputStream out = new FileOutputStream(file);
		byte[] data = new byte[3];
		data[0] = 'a';
		data[1] = 'b';
		data[2] = 'c';
		out.write(data);*/
		
		
		/////////////////////////////////////////////////////////////////////////////
		String str = "吴嘉贤abc";
		//gbk中文两个字节，英文1个字节
		byte[] bytes = str.getBytes("gbk");
		for(byte b : bytes) {
			System.out.print(Integer.toHexString(b & 0xff) + " ");
		} System.out.println();
		
		
		//utf-8中文三个字节，英文1个字节
		byte[] bytes1 = str.getBytes("utf-8");
		for(byte b : bytes1) {
			System.out.print(Integer.toHexString(b & 0xff) + " ");
		} System.out.println();
		
		//utf-16be中英文都是两个字节
		byte[] bytes2 = str.getBytes("utf-16be");
		for(byte b : bytes2) {
			System.out.print(Integer.toHexString(b & 0xff) + " ");
		} System.out.println();
		
		//什么样的编码转换成字符序列就要用什么样的编码格式,否则回出现乱码
		String str1 = new String(bytes2,"utf-16be");
		System.out.println(str1);
		
		////////////////////////////////////////////////////////////////////
		File file = new File("C:\\Users\\lenovo\\Desktop\\Github\\"
				+ "DocumentAndCode\\JAVA\\Learning\\bin\\InOutStream\\test");
		//判断文件/目录是否存在
		if (file.exists()) {
			//判断是否是目录or是否存在
			System.out.println(file.isDirectory());
			//判断是否是文件
			System.out.println(file.isFile());
		} else {
			//创建目录
			file.mkdirs();
			//删除文件/目录
			file.delete();
		}
		
		File file2 = new File("C:\\Users\\lenovo\\Desktop\\Github\\"
				+ "DocumentAndCode\\JAVA\\Learning\\bin\\InOutStream\\test\\test.txt");
		/*File file2 = new File("C:\\Users\\lenovo\\Desktop\\Github\\"
				+ "DocumentAndCode\\JAVA\\Learning\\bin\\InOutStream\\test","test.txt");*/
		if(!file2.exists()) {
			file2.createNewFile();
		} else {
			//获取文件路径
			System.out.println(file2.getAbsolutePath());
			//获取文件名
			System.out.println(file2.getName());
			//获取父目录
			System.out.println(file2.getParent());
		}
		
		////////////////////////////////////////////////////////////////////////////////////////
		File file3 = new File("C:\\Users\\lenovo\\Desktop\\Github\\"
				+ "DocumentAndCode\\JAVA");
		FileUtils.allListDirectory(file3);
		
	}

}
