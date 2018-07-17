package InOutStream;

import java.io.*;
/**
 * 文件流
 * FileReader类
 * FileWriter类
 * @author lenovo
 *
 */

public class FileWRUtils {
	
	/**
	 * 文件流copy文件，一次一个字符数组
	 * @param srcFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void copyFile(File srcFile, File destFile) throws IOException{
		FileReader fr = new FileReader(srcFile);
		FileWriter fw = new FileWriter(destFile);
		char[] buf = new char[8192];
		int c;
		while ((c = fr.read(buf, 0, buf.length)) != -1) {
			fw.write(buf, 0, c);
			fw.flush();
		}
		fr.close();
		fw.close();
	}
	
	/**
	 * 打印文件内容，通过字符数组
	 * @param fileName
	 * @throws IOException
	 */
	public static void printFile(String fileName) throws IOException{
		FileReader fr = new FileReader(fileName);
		int c;
		char[] buffer = new char[8192];
		while((c = fr.read(buffer, 0, buffer.length)) != -1) {
			String str = new String(buffer, 0, c);
			System.out.print(str);
		}
		fr.close();
	}
	
}
