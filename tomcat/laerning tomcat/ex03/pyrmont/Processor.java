package ex03.pyrmont;

import ex03.pyrmont.connector.http.HttpRequest;
import ex03.pyrmont.connector.http.HttpResponse;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-10 17:25
 */
public interface Processor {
    void process(HttpRequest request, HttpResponse response) throws Exception;
}
