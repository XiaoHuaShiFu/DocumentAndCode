package util;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-28 0:21
 */
public class StreamIteratorAdapter {

    /**
     * 将stream转换成iterable
     * @param stream
     * @param <E>
     * @return
     */
    public static <E> Iterable<E> iterableOf(Stream<E> stream) {
        return stream::iterator;
    }

    /**
     * 将iterable转换成stream
     * @param iterable
     * @param <E>
     * @return
     */
    public static <E> Stream<E> streamOf(Iterable<E> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

}
