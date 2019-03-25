package com.utils;

/**
 * 描述:非线程安全的坐标
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-03-23 21:05
 */
@ThreadSafe
public class Point {
    private final int x;
    private final int y;
    public Point() {
        x = 0;
        y = 0;
    }
    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
    }
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
}
