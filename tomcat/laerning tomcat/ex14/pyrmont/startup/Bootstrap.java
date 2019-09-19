package ex14.pyrmont.startup;

//explain Host

import ex14.pyrmont.core.SimpleContextConfig;
import org.apache.catalina.*;
import org.apache.catalina.connector.http.HttpConnector;
import org.apache.catalina.core.*;
import org.apache.catalina.loader.WebappLoader;


public final class Bootstrap {
    public static void main(String[] args) throws LifecycleException {
        //invoke: http://localhost:8080/app1/Primitive or http://localhost:8080/app1/Modern
        System.setProperty("catalina.base", System.getProperty("user.dir"));
        Connector connector = new HttpConnector();

        Wrapper wrapper1 = new StandardWrapper();
        wrapper1.setName("Primitive");
        wrapper1.setServletClass("PrimitiveServlet");
        Wrapper wrapper2 = new StandardWrapper();
        wrapper2.setName("Modern");
        wrapper2.setServletClass("ModernServlet");

        Context context = new StandardContext();
        // StandardContext's start method adds a default mapper
        context.setPath("/app1");
        context.setDocBase("app1");

        context.addChild(wrapper1);
        context.addChild(wrapper2);

        LifecycleListener listener = new SimpleContextConfig();
        ((Lifecycle) context).addLifecycleListener(listener);

        Host host = new StandardHost();
        host.addChild(context);
        host.setName("localhost");
        host.setAppBase("webapps");

        Loader loader = new WebappLoader();
        context.setLoader(loader);
        // context.addServletMapping(pattern, name);
        context.addServletMapping("/Primitive", "Primitive");
        context.addServletMapping("/Modern", "Modern");

        Engine engine =new StandardEngine();
        engine.addChild(host);
        engine.setDefaultHost("localhost");
        Service service = new StandardService();
        service.setName("Stand-alone Service");

        StandardServer server = new StandardServer();
        server.addService(service);
        service.addConnector(connector);
        service.setContainer(engine);

        server.initialize();
        server.start();
        server.await();

        server.stop();

    }
}