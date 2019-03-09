package com.utils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * 描述: 把对象的域转换成字符串的类
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-03-06 17:25
 */
public class ObjectAnalyzer {

    /**
     * 把对象的域转换成字符串
     * @param obj 要转换的对象
     * @return 对象的域转换成的字符串
     */
    public static String toString(Object obj) {
        if (obj == null) return null;
        Class cl = obj.getClass();
        if (cl == String.class) return (String) obj;
        //判断这个类对象是否描述一个数组
        if (cl.isArray()) {
            String r = "[";
            for (int i = 0; i < Array.getLength(obj); i++) {
                if (i > 0) r = r + ", ";
                Object val = Array.get(obj, i);
                //获取数组元素的类型
                if (cl.getComponentType().isPrimitive()) {
                    r = r + val;
                } else {
                    r = r + toString(val);
                }
            }
            r = r + "]";
            return r;
        }

        if (cl == ArrayList.class) {
            return obj.toString();
        }

        String r = "";
        do {
            if (!"".equals(r)) {
                r = r + "\n";
            }
            r = r + cl.getName();
            r = r + "{";
            Field[] fields = cl.getDeclaredFields();
            //设置所以field为可以访问私有变量
            AccessibleObject.setAccessible(fields, true);
            for (Field field : fields) {
                if (!Modifier.isStrict(field.getModifiers())) {
                    if (!r.endsWith("{")) {
                        r = r + ", ";
                    }
                    r = r + field.getName() + "=";
                    Class t = field.getType();
                    try {
                        Object val = field.get(obj);
                        //判断是否为原始类型（如int，long。。。）
                        if (t.isPrimitive()) {
                            r = r + val;
                        } else {
                            r = r + toString(val);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            r = r + "}";
            cl = cl.getSuperclass();
        } while (cl != null && cl != Object.class);
        return r;
    }

}
