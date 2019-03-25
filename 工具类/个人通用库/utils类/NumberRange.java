package com.utils;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-03-24 16:04
 */
@ThreadSafe
public class NumberRange {
    private long lower = 0;
    private long upper = 0;
    public synchronized void setLower(int l) throws IllegalArgumentException{
        if (l > upper) {
            throw new IllegalArgumentException("can't set lower to " + l + " > upper");
        }
        lower = l;
    }
    public synchronized void setUpper(int u) throws IllegalArgumentException {
        if (u < lower) {
            throw new IllegalArgumentException("can't set upper to " + u + " > lower");
        }
        upper = u;
    }
    public synchronized boolean isInRange(int i) {
        return i >= lower && i <= upper;
    }
}
