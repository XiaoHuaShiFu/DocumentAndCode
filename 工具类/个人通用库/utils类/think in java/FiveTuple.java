package util;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-12 14:28
 */
public class FiveTuple<A, B, C, D, E> extends FourTuple<A, B, C, D> {
    public final E fifth;

    public FiveTuple(A first, B second, C third, D fourth, E fifth) {
        super(first, second, third, fourth);
        this.fifth = fifth;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ", " + third
                + ", " + fourth  + ", " + fourth + ", " + fifth + ")";
    }
}
