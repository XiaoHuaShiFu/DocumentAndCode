package cn.edu.scau.cmi.wujiaxian.comprehensive.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/**
 * 静态流工厂
 * @author lenovo
 *
 */
public class IOFactory {
	
	/**
	 * 获取Scanner流
	 * @param path
	 * @param encode
	 * @return
	 */
	public static Scanner getScanner(String path, String encode) {
		try {
			return new Scanner(
					new FileInputStream(path), encode);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 获取PrintWriter流
	 * @param path
	 * @param encode
	 * @return
	 */
	public static PrintWriter getPrintWriter(String path, String encode) {
		try {
			return new PrintWriter(path, encode);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
