package ex03.pyrmont;


import ex03.pyrmont.connector.http.HttpRequest;
import ex03.pyrmont.connector.http.HttpResponse;

import java.io.IOException;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-10 17:23
 */
public class StaticResourceProcessor implements Processor {

    public void process(HttpRequest request, HttpResponse response) {
        try {
            response.sendStaticResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
