package com.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 描述:表明域或方法是不可变的
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-03-19 16:54
 */
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface GuardedBy {

    /**
     * 表明只有在持有了某个特定的锁时才能访问这个域或方法。
     * 参数lock表示在访问被标注的域或方法时需要持有的锁。
     * 可以取值有：
     * "this" 表示在包含对象上的内置锁（被标注的方法或域是该对象的成员）
     * "fieldName" 表示于fieldName引用对象相关联的锁，
     *    可以是一个隐式锁（对于不引用一个Lock的域），
     *    也可以是一个显示锁（对于引用了一个Lock的域）
     * "Class Name.fieldName" 类似于"fieldName"，但指向在另一个类的静态域中持有的锁对象。
     * "methodName()" 是指通过调用命名方法返回的所对象。
     * "ClassName.class" 是指命名类的类字面量对象。
     *
     *
     * 通过@GuardedBy来标识每个需要加所的状态变量以及保护该变量的锁。
     */
    String value();

}
