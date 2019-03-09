package cn.edu.scau.cmi.wujiaxian.comprehensive.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

	/**
	 * 把字符串首字母大写
	 * 
	 * @param arg
	 * @return
	 */
	public static String toUpperCaseFirst(String arg) {
		char[] cs = arg.toCharArray();
		cs[0] -= 32;
		return String.valueOf(cs);
	}

	/**
	 * 把字符串里面的数字拆分成整型数组
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public static List<Integer> parseStringToIntegerList(String arg0, String arg1) {
		String[] strs = arg0.substring(1, arg0.length() - 1).split(arg1);
		ArrayList<Integer> list = new ArrayList<>();
		for (String s : strs) {
			list.add(Integer.parseInt(s.trim()));
		}
		return list;
	}
	
	/**
	 * 把字符串里面的数字拆分成字符串数组
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public static List<String> parseStringToStringList(String arg0, String arg1) {
		String[] strs = arg0.substring(1, arg0.length() - 1).split(arg1);
		ArrayList<String> list = new ArrayList<>(Arrays.asList(strs));
		return list;
	}
	
	/**
	 * 从arrayList里获取对应id的下标，如果不存在则返回-1
	 * 
	 * @param           <T>
	 * @param           <V>
	 * @param           <K>
	 * @param arrayList
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T, K> ArrayList<Integer> findIndicesOnArrayList(ArrayList<T> arrayList, String arg0, K arg1) {
		try {
			Class<T> clx = (Class<T>) arrayList.get(0).getClass();
			Method method = null;
			method = clx.getMethod("get" + toUpperCaseFirst(arg0));
			ArrayList<Integer> indices = new ArrayList<>();
			for (int i = 0; i < arrayList.size(); i++) {
				if (method.invoke(arrayList.get(i)).equals(arg1)) {
					indices.add(i);
				}
			}
			return indices;
		} catch (IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 把参数名转换成get方法的名字
	 * @param arg
	 * @return
	 */
	public static String decorateGetName(String arg) {
		return "get" + Utils.toUpperCaseFirst(arg);
	}
	
	/**
	 * 把参数名转换成set方法的名字
	 * @param arg
	 * @return
	 */
	public static String decorateSetName(String arg) {
		return "set" + Utils.toUpperCaseFirst(arg);
	}
	
	/**
	 * 检测分隔符是否为转义字符，如果是则转义
	 * @param arg
	 * @return
	 */
	public static String separatorToRegex(String arg) {
		char[] esc = {'*', '^', ':', '|', '.', '\\'};
		for (char c : esc) {
			if (String.valueOf(c).equals(arg)) {
				return "\\" + arg;
			}
		}
		return arg;
	}
	
}
