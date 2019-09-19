package ex03.pyrmont;
import ex03.pyrmont.connector.http.Constants;
import ex03.pyrmont.connector.http.HttpRequest;
import ex03.pyrmont.connector.http.HttpResponse;

import javax.servlet.Servlet;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-10 17:24
 */
public class ServletProcessor implements Processor {

    private static final String protocol = "file";

    public void process(HttpRequest request, HttpResponse response) throws Exception {
        String uri = request.getRequestURI();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        // the forming of repository is taken from the
        // createClassLoader method in
        // org.apache.catalina.startup.ClassLoaderFactory
        File classPath = new File(Constants.WEB_ROOT);
        String repository = new URL(protocol, null, classPath.getCanonicalPath() + File.separator).toString();

        // the code for forming the URL is taken from
        // the addRepository method in
        // org.apache.catalina.loader.StandardClassLoader.
        URLStreamHandler streamHandler = null;
        URL url = new URL(null, repository, streamHandler);
        URLClassLoader loader = new URLClassLoader(new URL[] {url});

        Class myClass = loader.loadClass(servletName);

        Servlet servlet = (Servlet) myClass.newInstance();
        servlet.service(request, response);
        response.finishResponse();
    }

}
