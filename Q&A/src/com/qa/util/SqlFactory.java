package com.qa.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SqlFactory {
	
	/**
	 * 通过过滤器返回所拼接的sql语句
	 * 过滤器可以类型为标准的bean
	 * 如需进行子查询参数需要用","隔开
	 * @param (Object)filter
	 * @return String
	 */
	public String get(Object filter) {
		StringBuilder stringBuilder = new StringBuilder();
		//1.获取到相关的类class
		Class<?> cl = filter.getClass();
		//2.获取到table的名字
		if (!cl.isAnnotationPresent(Table.class)) {
			return null;
		} 
		Table table = cl.getAnnotation(Table.class);
		String tableName = table.value();
		stringBuilder.append("SELECT * FROM " + tableName + " WHERE 1 = 1 ");
		//3.遍历所有字段
		Field[] fields = cl.getDeclaredFields();
		for (Field field : fields) {
			//4.处理每个字段对应的sql
			//4.1拿到字段名
			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}
			Column column = field.getAnnotation(Column.class);
			String columnName = column.value();
			//4.2拿到字段值
			String fieldName = field.getName();
			//拼接get方法名
			String getMothodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			Object fieldValue = null;
			try {
				//通过反射得到函数
				Method getMethod = cl.getMethod(getMothodName);
				try {
					//通过回调函数获取域的值
					fieldValue = getMethod.invoke(filter);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			//4.3拼接sql
			//过滤无效字段
			if (fieldValue == null || 
					(fieldValue instanceof Integer && (Integer) fieldValue == 0)) {
				continue;
			}
			if (fieldValue instanceof String) {
				//如果有","分隔说明需要进行子查询，否则进行模糊查询
				if (((String) fieldValue).contains(",")) {
					String[] values = ((String) fieldValue).split(",");
					stringBuilder.append(" IN(");
					for (String value : values) {
						stringBuilder.append("'" + value + "'" + ",");
					}
					stringBuilder.deleteCharAt(stringBuilder.length() - 1);
					stringBuilder.append(")");
				} else {
					stringBuilder.append(" AND " + columnName + " = " + "'%" + fieldValue + "%'");
				}
			} else {
				stringBuilder.append(" AND " + columnName + " = " + fieldValue);
			}
		}
		return stringBuilder.toString();
	}
	
}
