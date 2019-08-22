package util;

/**
 * 描述: 生成range范围的整数数组
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-06-09 13:42
 */
public final class Range {

    public static int[] range(int max) {
        if (max <= 0) {
            throw new IllegalArgumentException("max must be great 0");
        }

        int[] range = new int[max];
        for (int i = 0; i < max; i++) {
            range[i] = i;
        }
        return range;
    }


    public static int[] range(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be great min");
        }

        int length = max - min;
        int[] range = new int[length];
        for (int i = 0; i < length; i++) {
            range[i] = i + min;
        }
        return range;
    }


    public static int[] range(int min, int max, int step) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be great min");
        }

        if (step <= 0) {
            throw new IllegalArgumentException("step must be great 0");
        }

        int length = (int) Math.ceil((double)(max - min) / step);
        int[] range = new int[length];
        for (int i = 0; i < length; i++) {
            range[i] = min + i * step;
        }
        return range;
    }

    public static void main(String[] args) {
        for (int i : range(5, 33)) {
            System.out.println(i);
        }
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
