import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-10 13:00
 */
public class PrimitiveServlet implements Servlet {
    public void init(ServletConfig config) throws ServletException {
        System.out.println("init");
    }

    public ServletConfig getServletConfig() {
        return null;
    }

    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.println("HTTP/1.0 200 OK");
        out.println();
        out.println("Hello. Roses are red.");
        out.print("Violets are blue.");
    }

    public String getServletInfo() {
        return null;
    }

    public void destroy() {
        System.out.println("destroy");
    }
}
