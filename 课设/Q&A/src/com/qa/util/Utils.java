package com.qa.util;

import com.qa.bean.User;

public class Utils {
	
	public static void main(String[] args) {
		User user1 = new User();
		User user2 = new User();
		user1.setUsername("aaaaaa");
		user2.setUsername("bbbbbb");
		user2 = swap(user1, user1 = user2);
		System.out.println(user1);
		System.out.println(user2);
	}
	
	/**
	 * 返回args第一个值,用于交换值.
	 * 用法 b = swap(a, a = b);
	 * @param args
	 * @return args[0]
	 */
	public static <T> T swap(T...args) {
		return args[0];
	}
	
	/**
	 * 返回n的阶乘
	 * @param n
	 * @return n*(n-1)*(n-2)*...*3*2*1
	 */
	public static int Factorial(int n) {
		if (n <= 1) return 1;
		else return Factorial(n - 1) * n; 
	}
	
	/** 
	 * 使用正则表达式去掉多余的.与0 
     * @param s 
     * @return  String
     */  
    public static String subZeroAndDot(String s){  
        if(s.indexOf(".") > 0){  
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }  
        return s;  
    }  
	
}
