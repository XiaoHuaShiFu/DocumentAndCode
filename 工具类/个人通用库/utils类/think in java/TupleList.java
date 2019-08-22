package util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-12 14:55
 */
public class TupleList<A, B, C, D> extends ArrayList<FourTuple<A, B, C, D>> {
    public static void main(String[] args) {
        TupleList<String, String, String, String> tupleList = new TupleList<>();
        tupleList.add(new FourTuple<>("3", "wjx", "dd", "dd"));
        System.out.println(tupleList);
        System.out.println(tupleList.getClass().getName());
    }
}
