package InOutStream;

import java.io.*;

/**
 * 带缓冲的字符流
 * BufferedReader类
 * BufferedWriter类
 * PrintWriter类
 * @author lenovo
 *
 */

public class BWRUtils {
	
	/**
	 * 带缓冲的字符流，一次读取一行
	 * @param fileName
	 * @throws IOException
	 */
	public static void copyFile(File srcFile, File destFile) throws IOException{
		if (!srcFile.exists()) {
			throw new IllegalArgumentException("文件:" + srcFile + "不存在.");
		}
		if (!srcFile.isFile()) {
			throw new IllegalArgumentException(srcFile + "不是文件.");
		}
		FileInputStream in = new FileInputStream(srcFile);
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(isr);
		
		FileOutputStream out = new FileOutputStream(destFile);
		OutputStreamWriter osw = new OutputStreamWriter(out);
		BufferedWriter bw = new BufferedWriter(osw);
		
		PrintWriter pw = new PrintWriter(destFile);	
		
		String line;
		while((line = br.readLine()) != null) {
			/*
			bw.write(line);//不自带换行
			bw.newLine();//添加换行符
			bw.flush();
			*/
			pw.println(line);//自带换行
			pw.flush();
		}
		br.close();
		bw.close();
		pw.close();
	}
	
}
