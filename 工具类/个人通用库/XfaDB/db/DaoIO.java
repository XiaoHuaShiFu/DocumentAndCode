package cn.edu.scau.cmi.wujiaxian.comprehensive.db;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 进行文件的增删操作获取数据等操作
 * @author xfa
 */
public interface DaoIO {
	
	/**
	 * 把所有bean从文件取出
	 * @return ArrayList<B>
	 */
	<B> List<B> readAll();
	
	/**
	 *  把所有bean写入文件
	 * @param ArrayList<B> beans
	 * @param int number 当前编号
	 */
	<B> void writeAll(ArrayList<B> beans, int number);
	
	/**
	 * 添加一行数据到文件
	 * @param PrintWriter out
	 * @param B bean
	 */
	<B> void writeOne(PrintWriter out, B bean);
	
	/**
	 *  通过输入流获取当前编号
	 * @return int
	 */
	int getNumber();
	
	/**
	 * 获取该文件的数据行数
	 */
	public int getRows();
	
}
