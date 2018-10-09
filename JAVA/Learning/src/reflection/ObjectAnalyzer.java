package reflection;

import java.lang.reflect.*;
import java.util.ArrayList;

import human.Employee;


public class ObjectAnalyzer {
	
	public static void main(String[] args) {
		ArrayList<Integer> squares = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			squares.add(i * i);
		}
		
		System.out.println(new ObjectAnalyzer().toString(squares));
	}
	
	private ArrayList<Object> visited = new ArrayList<>();
	
	public String toString(Object obj) {
		if (obj == null) return "null";
		if (visited.contains(obj)) return "...";
		visited.add(obj);
		Class<? extends Object> cl = obj.getClass();
		if (cl == String.class) return (String) obj;
		if (cl.isArray()) {
			//getComponentType()获取数组类型
			String r = cl.getComponentType() + "[]{"; 
			for (int i = 0; i < Array.getLength(obj); i++) {
				if (i > 0) r += ",";
				//Array.get(obj, i);获取数组元素
				Object val = Array.get(obj, i);
				//isPrimitive()判断数组是否为原始数据类型，如int，double，boolean，char
				if (cl.getComponentType().isPrimitive()) r += val;
				else r += toString(val);
			}
			return r + "}";
		}
		
		String r = cl.getName();
		do {
			r += "[";
			//获取对应类的域
			Field[] fields = cl.getDeclaredFields();
			//使对应类的域可访问
			AccessibleObject.setAccessible(fields, true);
			for (Field field : fields) {
				//Modifier.isStatic(mod) 如果存在static返回true
				if (!Modifier.isStatic(field.getModifiers())) {
					if (!r.endsWith("[")) r += ",";
					r += field.getName() + "=";
					try {
						Class<? extends Object> t = field.getType();
						Object val = field.get(obj);
						if (t.isPrimitive()) r += val;
						else r += toString(val);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			r += "]";
			//getSuperclass()获取cl的超类
			cl = cl.getSuperclass();
		} while (cl != null);
		return r;
	}
	
}
