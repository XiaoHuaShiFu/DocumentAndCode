package util;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-12 14:26
 */
public class Tuple {

    public static <A, B> TwoTuple<A, B> tuple(A a, B b) {
        return new TwoTuple<>(a, b);
    }

    public static <A, B, C> ThreeTuple<A, B, C> tuple(A a, B b, C c) {
        return new ThreeTuple<>(a, b, c);
    }

    public static <A, B, C, D> FourTuple<A, B, C, D> tuple(A a, B b, C c, D d) {
        return new FourTuple<>(a, b, c, d);
    }

    public static <A, B, C, D, E> FiveTuple<A, B, C, D, E> tuple(A a, B b, C c, D d, E e) {
        return new FiveTuple<>(a, b, c, d, e);
    }

}
