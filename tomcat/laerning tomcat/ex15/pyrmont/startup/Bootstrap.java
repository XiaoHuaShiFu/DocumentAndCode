package ex15.pyrmont.startup;

import org.apache.catalina.*;
import org.apache.catalina.connector.http.HttpConnector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.ContextConfig;

import java.io.IOException;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-16 19:58
 */
public class Bootstrap {

    public static void main(String[] args) throws LifecycleException, IOException {
        System.setProperty("catalina.base", System.getProperty("user.dir"));
        HttpConnector connector = new HttpConnector();
        StandardContext context = new StandardContext();
        context.setPath("/app1");
        context.setDocBase("app1");
        LifecycleListener listener = new ContextConfig();
        context.addLifecycleListener(listener);
        StandardHost host = new StandardHost();
        host.addChild(context);
        host.setName("localhost");
        host.setAppBase("webapps");
        Loader loader = new WebappLoader();
        context.setLoader(loader);
        connector.setContainer(host);

        connector.initialize();
        connector.start();
        host.start();
        Container[] c = context.findChildren();
        for (Container container : c) {
            System.out.println(container.getName());
        }
        System.in.read();
        host.stop();

    }

}
