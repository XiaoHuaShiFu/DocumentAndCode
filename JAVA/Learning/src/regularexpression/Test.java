package regularexpression;

import java.util.regex.*;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String patternString = "([1-9])*|([0-9]*[a-z]*)";
		Pattern pattern = Pattern.compile(patternString);
		String input = "10252,das,wjx";
		Matcher matcher = pattern.matcher(input);
		System.out.println(matcher.matches());
	
		
		/**
		 * 字符串的匹配
		 */
		System.out.println(input.matches(patternString));
		System.out.println(input.replaceFirst("[1-9]", "0"));
		System.out.println(input.replaceAll("[1-9]", "1"));
		//分割字符串
		String[] str = input.split(",");
		for(String element : str) {
			System.out.println(element);
		}
		
		/**
		 * 匹配示例
		 */
		//电话
		String telephoneNumber = "010-38389438";
		telephoneNumber.matches("\\d{3,4}-\\d{7,8}");
		//手机
		String phoneNumber = "13895271234";
		phoneNumber.matches("1[3-9]\\d{9}");
		//用户名(字母开头，下划线和数字字母组合)
		String username = "xhsf1234";
		username.matches("[a-zA-Z]+[\\w|_]*");
		//ip地址
		String ip = "172.16.14.60:8000/";
		ip.matches("\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}:\\d{1,4}/");
		//年龄
		String age = "18";
		age.matches("\\d{1,3}");
		//金额
		String price = "205.53";
		price.matches("\\d+.\\d+");
		
		
		
	}

}
