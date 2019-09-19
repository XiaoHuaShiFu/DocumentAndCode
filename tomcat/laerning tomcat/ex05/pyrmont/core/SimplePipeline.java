package ex05.pyrmont.core;

import org.apache.catalina.*;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-12 10:29
 */
public class SimplePipeline implements Pipeline {

    /**
     * The basic Valve (if any) associated with this Pipeline.
     */
    protected Valve basic;

    /**
     * The Container with which this Pipeline is associated.
     */
    protected Container container;

    /**
     * the array of Valves
     */
    protected Valve[] valves = new Valve[0];

    public SimplePipeline(Container container) {
        setContainer(container);
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public Valve getBasic() {
        return basic;
    }

    public void setBasic(Valve valve) {
        this.basic = valve;
        ((Contained) valve).setContainer(container);
    }

    public void addValve(Valve valve) {
        if (valve instanceof Contained)
            ((Contained) valve).setContainer(this.container);

        synchronized (valves) {
            Valve results[] = new Valve[valves.length +1];
            System.arraycopy(valves, 0, results, 0, valves.length);
            results[valves.length] = valve;
            valves = results;
        }
    }

    public Valve[] getValves() {
        return valves;
    }

    public void invoke(Request request, Response response) throws IOException, ServletException {
        (new SimplePipelineValveContext()).invokeNext(request, response);
    }

    public void removeValve(Valve valve) {

    }

    /**
     * this class is copied from org.apache.catalina.core.StandardPipeline class's
     * StandardPipelineValveContext inner class.
     */
    protected class SimplePipelineValveContext implements ValveContext {
        int stage = 0;
        public String getInfo() {
            return null;
        }
        public void invokeNext(Request request, Response response) throws IOException, ServletException {
            int subscript = stage;
            stage = stage + 1;
            // Invoke the requested Valve for the current request thread
            if (subscript < valves.length) {
                valves[subscript].invoke(request, response, this);
            } else if ((subscript == valves.length) && (basic != null)) {
                basic.invoke(request, response, this);
            } else {
                throw new ServletException("No valve");
            }
        }
    }

}
