package InOutStream;

import java.io.*;

public class FileUtils {
	
	/**
	 * 打印指定目录下的所有文件
	 * @param dir
	 */
	
	public static void listDirectory(File dir) {
		if (!dir.exists()) {
			throw new IllegalArgumentException("目录:" + dir + "不存在.");
		}
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException(dir + "不是目录.");
		}
		//获取目录的文件列表
		String[] fileNames = dir.list();
		for(String fileName : fileNames) {
			System.out.println(dir + "\\" + fileName);
		}
	}
	
	/**
	 * 打印指定目录下（包括子目录下）的所有文件
	 * @param dir
	 */
	public static void allListDirectory(File dir) {
		if (!dir.exists()) {
			throw new IllegalArgumentException("目录:" + dir + "不存在.");
		}
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException(dir + "不是目录.");
		}
		//获取目录的文件列表
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				allListDirectory(file);
			} else {
				System.out.println(file);
			}
		}
	}
	
}
