package ex02.pyrmont;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-10 17:25
 */
public interface Processor {
    void process(Request request, Response response) throws Exception;
}
