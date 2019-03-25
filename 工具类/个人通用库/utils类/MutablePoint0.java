package com.utils;

/**
 * 描述:线程安全的可变坐标
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-03-23 21:05
 */
@ThreadSafe
public class MutablePoint0 {
    @GuardedBy("this") private int x;
    @GuardedBy("this") private int y;
    private MutablePoint0(int[] a) {
        this(a[0], a[1]);
    }
    public MutablePoint0(MutablePoint0 p) {
        this(p.get());
    }
    public MutablePoint0(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public synchronized int[] get() {
        return new int[] {x, y};
    }
    public synchronized void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
