package com.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 描述:表明类是不可变的
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-03-19 16:54
 */
@Target({ElementType.TYPE})
public @interface Immutable {
}
