package cn.edu.scau.cmi.wujiaxian.comprehensive.db;

import java.util.ArrayList;

/**
 * Dao接口，有增删查改、获取全部元素、获取元素数的操作
 * @author xfa
 */
public interface Dao{
	
	/**
	 * 添加一个元素的数据到文件
	 * 需要这个Bean有setId方法
	 * @param B bean
	 */
	public <B> void add(B bean);
	
	/**
	 * 从文件删除一个元素
	 * parameter是所需匹配的参数的名字，value是参数的值
	 * @param String parameter
	 * @param V value
	 */
	public <V> void delete(String parameter, V value);
	
	/**
	 * 从文件通过对应的参数名获取元素
	 * parameter是所需匹配的参数的名字，value是参数的值
	 * @param String parameter
	 * @param V value
	 * @return 若找不到此元素则返回null
	 */
	public <B, V> ArrayList<B> select(String parameter, V value);
	
	/**
	 * 更新某个元素的数据
	 * parameter是所需匹配的参数的名字
	 * @param String parameter
	 * @param B bean
	 */
	public <B> void update(String parameter, B bean);
	
	/**
	 * 从文件中获取所有元素
	 */
	public ArrayList<?> selectAll();
	
	/**
	 * 获取该文件的元素数
	 */
	public int getSize();
	
}
