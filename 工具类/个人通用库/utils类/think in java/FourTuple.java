package util;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-12 14:28
 */
public class FourTuple<A, B, C, D> extends ThreeTuple<A, B, C> {
    public final D fourth;

    public FourTuple(A first, B second, C third, D fourth) {
        super(first, second, third);
        this.fourth = fourth;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ", " + third + ", " + fourth + ")";
    }
}
