package cn.edu.scau.cmi.wujiaxian.comprehensive.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 字符串解析器
 * @author xfa
 * @param <B>
 */
/**
 * 支持的数据类型
 *
private static final Type[][] TYPES = {
		{Integer.class, int.class}, 
		{Long.class, long.class}, 
		{Float.class, float.class}, 
		{Double.class, double.class}, 
		{Boolean.class, boolean.class}, 
		{Character.class, char.class}, 
		{String.class},
		{ArrayList.class}
		};
*/
public class BeanParse<B> {
	
	private String[] parameterNames;
	private Class<?> clazz;
	private B bean;
	private Constructor<?> constructor;
	private Method[] methods;
	private Type[] propertyTypes;
	
	/**
	 * 需要要解析的参数名和bean的class对象
	 * @param parameterNames
	 * @param beanClazz
	 */
	public BeanParse(String[] parameterNames, Class<?> beanClazz) {
		this.parameterNames = parameterNames;
		this.clazz = beanClazz;
		initializeBean();
	}
	
	/**
	 * 解析字符串把字符串装配成一个bean对象返回
	 * @param <B>
	 * @param String[] parameters
	 * @return <B>
	 */
	@SuppressWarnings("unchecked")
	public B getBean(String[] parameters) {
		try {
			bean = (B) constructor.newInstance();
			for (int i = 0; i < parameterNames.length; i++) {
				if (propertyTypes[i] == String.class) {
					methods[i].invoke(bean, parameters[i]);
				} else if (propertyTypes[i] == Integer.class || propertyTypes[i] == int.class) {
					methods[i].invoke(bean, Integer.parseInt(parameters[i]));
				} else if (propertyTypes[i] == ArrayList.class) {
					Field field = clazz.getDeclaredField(parameterNames[i]);
					Type genericType = field.getGenericType();
					String typeName = genericType.getTypeName();
					if (typeName.equals("java.util.ArrayList<java.lang.Integer>") || typeName.equals("int")) {
						methods[i].invoke(bean, Utils.parseStringToIntegerList(parameters[i], ","));
					} else if (typeName.equals("java.util.ArrayList<java.lang.String>")){
						methods[i].invoke(bean, Utils.parseStringToStringList(parameters[i], ","));
					} else if (typeName.equals("java.util.ArrayList<java.lang.Double>") || typeName.equals("double")){
						methods[i].invoke(bean, Double.parseDouble(parameters[i]));
					} else if (typeName.equals("java.util.ArrayList<java.lang.Boolean>") || typeName.equals("boolean")){
						methods[i].invoke(bean, Boolean.parseBoolean(parameters[i]));
					} else if (typeName.equals("java.util.ArrayList<java.lang.Character>") || typeName.equals("char")){
						methods[i].invoke(bean, parameters[i].charAt(0));
					} else if (typeName.equals("java.util.ArrayList<java.lang.Float>") || typeName.equals("float")){
						methods[i].invoke(bean, Float.parseFloat(parameters[i]));
					} else if (typeName.equals("java.util.ArrayList<java.lang.Long>") || typeName.equals("long")){
						methods[i].invoke(bean, Long.parseLong(parameters[i]));
					}
				} else if (propertyTypes[i] == Double.class || propertyTypes[i] == double.class) {
					methods[i].invoke(bean, Double.parseDouble(parameters[i]));
				} else if (propertyTypes[i] == Boolean.class || propertyTypes[i] == boolean.class) {
					methods[i].invoke(bean, Boolean.parseBoolean(parameters[i]));
				} else if (propertyTypes[i] == Character.class || propertyTypes[i] == char.class) {
					methods[i].invoke(bean, parameters[i].charAt(0));
				} else if (propertyTypes[i] == Float.class || propertyTypes[i] == float.class) {
					methods[i].invoke(bean, Float.parseFloat(parameters[i]));
				} else if (propertyTypes[i] == Long.class || propertyTypes[i] == long.class) {
					methods[i].invoke(bean, Long.parseLong(parameters[i]));
				}
			}
		} catch (SecurityException | IllegalAccessException 
				| IllegalArgumentException | InvocationTargetException 
				| NoSuchFieldException | InstantiationException  e) {
			e.printStackTrace();
		}
		return bean;
	}
	
	/**
	 * 初始化bean的一些构造方法
	 */
	@SuppressWarnings("unchecked")
	private void initializeBean() {
		int pnLength = parameterNames.length;
		try {
			constructor = (Constructor<B>) clazz.getConstructor();
			PropertyDescriptor pd;
			methods = new Method[pnLength];
			propertyTypes = new Type[pnLength];
			for (int i = 0; i < pnLength; i++) {
				pd = new PropertyDescriptor(parameterNames[i], clazz);
				methods[i] = pd.getWriteMethod();
				propertyTypes[i] = pd.getPropertyType();
			}
		} catch (NoSuchMethodException | SecurityException 
				| IntrospectionException e) {
			e.printStackTrace();
		}
	}
	
}
