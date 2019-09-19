/* explains Tomcat's default container */
package ex05.pyrmont.startup;

import ex05.pyrmont.core.SimpleContext;
import ex05.pyrmont.core.SimpleContextMapper;
import ex05.pyrmont.core.SimpleLoader;
import ex05.pyrmont.core.SimpleWrapper;
import ex05.pyrmont.valve.ClientIPLoggerValve;
import ex05.pyrmont.valve.HeaderLoggerValve;
import org.apache.catalina.*;
import org.apache.catalina.connector.http.HttpConnector;

public final class Bootstrap2 {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        Wrapper wrapper1 = new SimpleWrapper();
        wrapper1.setName("Primitive");
        wrapper1.setServletClass("PrimitiveServlet");
        Wrapper wrapper2 = new SimpleWrapper();
        wrapper2.setName("Modern");
        wrapper2.setServletClass("ModernServlet");

        Context context = new SimpleContext();
        context.addChild(wrapper1);
        context.addChild(wrapper2);

        HeaderLoggerValve headerLoggerValve = new HeaderLoggerValve();
        ((Pipeline) context).addValve(headerLoggerValve);
        ClientIPLoggerValve clientIPLoggerValve = new ClientIPLoggerValve();
        ((Pipeline) context).addValve(clientIPLoggerValve);

        Mapper mapper = new SimpleContextMapper();
        mapper.setProtocol("http");
        context.addMapper(mapper);
        Loader loader = new SimpleLoader();
        context.setLoader(loader);

        context.addServletMapping("/Primitive", "Primitive");
        context.addServletMapping("/Modern", "Modern");

        connector.setContainer(context);
        try {
            connector.initialize();
            connector.start();

            // make the application wait until we press any key.
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}