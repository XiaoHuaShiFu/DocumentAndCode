/**
 * 这是一个抽奖小程序
 * 可以从n个数字中抽出m个随机的数字
 */
package com.xiaohua.mode;
import java.util.Arrays;

public class Lottery {

	static int n = 49;
	static final int m = 7;
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		
		//初始化数组numbers
		int[] numbers = new int[n];
		for(int i = 0; i < numbers.length; i++) {
			numbers[i] = i + 1;
		}
		
		//随机抽取并存储到数组results
		int[] results = new int[m];
		for(int i = 0; i < results.length; i++) {
			int r = (int)(Math.random() * n);
			results[i] = numbers[r];
			numbers[r] = numbers[n - 1];
			n--;
		}
		
		//排序数组results并打印
		Arrays.sort(results);
		for(int result : results) {
			System.out.print(result + " ");
		}
	}

}
