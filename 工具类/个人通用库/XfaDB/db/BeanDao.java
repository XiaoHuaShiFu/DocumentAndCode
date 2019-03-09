package cn.edu.scau.cmi.wujiaxian.comprehensive.db;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Scanner;


import cn.edu.scau.cmi.wujiaxian.comprehensive.util.BeanParse;
import cn.edu.scau.cmi.wujiaxian.comprehensive.util.IOFactory;
import cn.edu.scau.cmi.wujiaxian.comprehensive.util.Utils;

/**
 * 一个从文件中增删查改Bean类型数据的方法
 * 需要符合Bean类型的数据才能使用
 * 
 * Dao的默认实现接口，请使用Dao作为类型，这样可以保护其他公用方法不会被调用
 * 子类需要初始化path, names, beanClazz参数，即可使用此类进行增删查改的操作
 * 注：其中path为文件路径，names为所要增删的参数名，beanClazz为Bean的类型(Bean.class)
 * 
 * 注：此数据库依赖于bean的id属性进行增删查改,所以bean必须要有id属性（例如private int id;）
 * 
 * 注：此类依赖于Utils类
 * 
 * 此数据库支持的数据类型：
 * 		{Integer, int}, 
		{Long, long}, 
		{Float, float}, 
		{Double, double}, 
		{Boolean, boolean}, 
		{Character, char}, 
		{String},
		{ArrayList}
		其中数组必须用ArrayList代替，且不可以多重嵌套
 * @author xfa
 */
public class BeanDao implements Dao, DaoIO{
	
	private String encode = "UTF-8"; //编码格式，默认为utf-8
	private String separator = "|"; //分隔符，默认为'|'，如果需要使用到'|'，可覆盖为"$%"等
	private String regex = "\\|"; //正则表达式，默认为'\\|'，与separator同型
	
	protected String path; //文件存储路径
	protected String[] parameterNames; //参数名数组
	protected Class<?> beanClazz; //bean的class
	
	public static final String UTF_8 = "UTF-8"; //utf-8编码
	public static final String GBK = "GBK"; //gbk编码
	
	private Method[] getMethods; //get方法集
	private PrintWriter out; //输出流
	private Scanner in; //输入流
	
	/**
	 * 初始化存储地址，bean的类型，要增删的参数名，编码格式，分隔符
	 * 如果参数值为null则使用默认分隔符和编码
	 * @param path
	 * @param beanClazz
	 * @param names
	 */
	public BeanDao(String path, Class<?> beanClazz, String parameterNames, String encode, String separator) {
		this.path = path;
		this.beanClazz = beanClazz;
		this.parameterNames = parameterNames.split(",");
		if (encode != null) {
			this.encode = encode;
		}
		if (separator != null) {
			this.separator = separator;
			this.regex = Utils.separatorToRegex(separator);
		}
		initialize();
	}
	
	/**
	 * 从文件中获取所有元素
	 */
	public ArrayList<?> selectAll() {
		return readAll();
	}
	
	/**
	 * 添加一个元素的数据到文件
	 * 需要这个Bean有setId方法
	 * @param B bean
	 */
	public <B> void add(B bean) {
		ArrayList<B> beans = null;
		beans = readAll();
		int number = getNumber() + 1;
		Method method;
		try {
			method =  beanClazz.getMethod("setId", Integer.class);
			method.invoke(bean, number);
		} catch (NoSuchMethodException | SecurityException 
				| IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		beans.add(bean);
		writeAll(beans, number);
	}
	
	/**
	 * 从文件删除一个元素
	 * parameter是所需匹配的参数的名字，value是参数的值
	 * @param <B>
	 * @param String parameter
	 * @param V value
	 */
	public <V> void delete(String parameter, V value) {
		ArrayList<?> beans = readAll();
		ArrayList<Integer> indices = Utils.findIndicesOnArrayList(beans, parameter, value);
		if (indices == null) {
			return;
		}
		beans.remove(beans.get(indices.get(0)));
		writeAll(beans, getNumber());
	}
	
	/**
	 * 从文件通过对应的参数名获取元素
	 * parameter是所需匹配的参数的名字，value是参数的值
	 * @param String parameter
	 * @param V value
	 * @return 若找不到此元素则返回null
	 */
	public <B, V> ArrayList<B> select(String parameter, V value) {
		ArrayList<B> beans = readAll();
		ArrayList<Integer> indices = Utils.findIndicesOnArrayList(beans, parameter, value);
		if (indices == null) {
			return null;
		}
		ArrayList<B> arrayList = new ArrayList<>();
		for (Integer index : indices) {
			arrayList.add(beans.get(index));
		}
		return arrayList;
	}
	
	/**
	 * 更新某个元素的数据
	 * parameter是所需匹配的参数的名字
	 * @param String parameter
	 * @param B bean
	 */
	public <B> void update(String parameter, B bean) {
		ArrayList<B> beans = readAll();
		Method method = null;
		ArrayList<Integer> indices = null;
		try {
			method = beanClazz.getMethod(Utils.decorateGetName(parameter));
			indices = Utils.findIndicesOnArrayList(beans, parameter, method.invoke(bean));
		} catch (NoSuchMethodException | SecurityException 
				| IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		if (indices == null) {
			return;
		}
		for (Integer index : indices) {
			beans.set(index, bean);
		}
		writeAll(beans, getNumber());		
	}
	
	/**
	 *  获取当前数据的元素个数
	 * @return int
	 */
	public int getSize() {
		return getRows();
	}
	
	/**
	 *  通过输入流获取文件的行数
	 * @return int
	 */
	public int getRows() {
		in = IOFactory.getScanner(path, encode);
		int size = 0;
		if (in.hasNextInt()) {
			in.nextLine();
			if (in.hasNextInt()) {
				size = in.nextInt();
			}
		}
		in.close();
		return size;
	}
	
	/**
	 *  通过输入流获取当前编号
	 * @return int
	 */
	public int getNumber() {
		in = IOFactory.getScanner(path, encode);
		int number = 0;
		if (in.hasNextInt()) {    
			number = in.nextInt();
		}
		in.close();
		return number;
	}
	
	/**
	 * 从文件中获取所有元素
	 */
	public <B> ArrayList<B> readAll() {
		in = IOFactory.getScanner(path, encode);
		in.nextLine();//跳过第一行（当前编号）
		int n = in.nextInt(); //获取数组长度
		in.nextLine();
		
		ArrayList<B> beans = new ArrayList<>();
		BeanParse<B> parser = new BeanParse<>(parameterNames, beanClazz);
		String line;
		String[] parameters;
		for (int i = 0; i < n; i++) {
			line = in.nextLine();
			parameters = line.split(regex);
			beans.add(parser.getBean(parameters));
		}
		in.close();
		return beans;
	}
	
	/**
	 *  把所有bean写入文件
	 * @param ArrayList<B> beans
	 * @param int number 当前编号
	 */
	public <B> void writeAll(ArrayList<B> beans, int number) {
		out = IOFactory.getPrintWriter(path, encode);
		out.println(number);
		out.println(beans.size());
		
		for (B bean : beans) {
			writeOne(out, bean);
		}
		out.flush();
		out.close();
	}
	
	/**
	 * 添加一行数据到文件
	 * @param PrintWriter out
	 * @param B bean
	 */
	public <B> void writeOne(PrintWriter out, B bean) {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			for (int i = 0; i < parameterNames.length; i++) {
				stringBuilder.append(getMethods[i].invoke(bean) + separator);
			}
		} catch (SecurityException | IllegalAccessException 
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		String line = stringBuilder.substring(0, stringBuilder.length() - 1).toString(); //去掉最后一个 |
		out.println(line); //推进文件里
	}
	
	/**
	 * 初始化这个类
	 */
	private void initialize() {
		int pnLength = parameterNames.length;
		getMethods = new Method[pnLength];
		try {
			for (int i = 0; i < pnLength; i++) {
				getMethods[i] = beanClazz.getDeclaredMethod(Utils.decorateGetName(parameterNames[i]));
			}
		} catch (SecurityException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		if (getNumber() == 0) {
			out = IOFactory.getPrintWriter(path, encode);
			out.println("0");
			out.println("0");
			out.close();
		}
	}
	
}
