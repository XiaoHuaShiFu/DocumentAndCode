package InOutStream;

import java.io.*;

public class WRUtils {
	
	/**
	 * 字符流，一次读取一个字符数组
	 * @param fileName
	 * @throws IOException
	 */
	public static void printFileByCharArray(String fileName) throws IOException{
		File file = new File(fileName);
		FileInputStream in = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(in);
		char[] buffer = new char[8 * 1024];
		int c;
		while((c = isr.read(buffer,0,buffer.length)) != -1) {
			String str = new String(buffer,0,c);
			System.out.print(str);
		}
		isr.close();
	}
	
	/**
	 * 字符流，一次读取一个字符
	 * @param fileName
	 * @throws IOException
	 */
	public static void printFileByChar(String fileName) throws IOException{
		File file = new File(fileName);
		FileInputStream in = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(in);
		int c;
		while((c = isr.read()) != -1) {
			//读取的是整形，要转换为字符型
			System.out.print((char)c);
		}
		isr.close();
	}
	
	/**
	 * 文件拷贝，一次一个字符数组
	 * @param fileName
	 * @throws IOException
	 */
	public static void copyFileByCharArray(File srcFile, File destFile) throws IOException{
		if (!srcFile.exists()) {
			throw new IllegalArgumentException("文件:" + srcFile + "不存在.");
		}
		if (!srcFile.isFile()) {
			throw new IllegalArgumentException(srcFile + "不是文件.");
		}
		FileInputStream in = new FileInputStream(srcFile);
		InputStreamReader isr = new InputStreamReader(in);
		FileOutputStream out = new FileOutputStream(destFile);
		OutputStreamWriter osw = new OutputStreamWriter(out);
		char[] buf = new char[8 * 1024];
		int c;
		while((c = isr.read(buf, 0, buf.length)) != -1) {
			osw.write(buf,0,c);
			osw.flush();
		}
		isr.close();
		osw.close();
	}
	
	/**
	 * 文件拷贝，一次一个字符
	 * @param fileName
	 * @throws IOException
	 */
	public static void copyFileByChar(File srcFile, File destFile) throws IOException{
		if (!srcFile.exists()) {
			throw new IllegalArgumentException("文件:" + srcFile + "不存在.");
		}
		if (!srcFile.isFile()) {
			throw new IllegalArgumentException(srcFile + "不是文件.");
		}
		FileInputStream in = new FileInputStream(srcFile);
		InputStreamReader isr = new InputStreamReader(in);
		FileOutputStream out = new FileOutputStream(destFile);
		OutputStreamWriter osw = new OutputStreamWriter(out);
		int c;
		while((c = isr.read()) != -1) {
			osw.write(c);
			osw.flush();
		}
		isr.close();
		osw.close();
	}
	
}
