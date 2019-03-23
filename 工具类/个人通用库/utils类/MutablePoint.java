package com.utils;

/**
 * 描述:非线程安全的坐标
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-03-23 21:05
 */
@NotThreadSafe
public class MutablePoint {
    public int x;
    public int y;
    public MutablePoint() {
        x = 0;
        y = 0;
    }
    public MutablePoint(MutablePoint p) {
        this.x = p.x;
        this.y = p.y;
    }
    public MutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
