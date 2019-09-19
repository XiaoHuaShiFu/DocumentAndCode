/* explains Tomcat's default container */
package ex05.pyrmont.startup;

import ex05.pyrmont.core.SimpleLoader;
import ex05.pyrmont.core.SimpleWrapper;
import ex05.pyrmont.valve.ClientIPLoggerValve;
import ex05.pyrmont.valve.HeaderLoggerValve;
import org.apache.catalina.Loader;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;

public final class Bootstrap1 {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        Wrapper wrapper = new SimpleWrapper();
        wrapper.setServletClass("ModernServlet");
        Loader loader = new SimpleLoader();
        wrapper.setLoader(loader);
        HeaderLoggerValve headerLoggerValve = new HeaderLoggerValve();
        ((Pipeline) wrapper).addValve(headerLoggerValve);
        ClientIPLoggerValve clientIPLoggerValve = new ClientIPLoggerValve();
        ((Pipeline) wrapper).addValve(clientIPLoggerValve);

        connector.setContainer(wrapper);
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