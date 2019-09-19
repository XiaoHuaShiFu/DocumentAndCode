import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModernServlet extends HttpServlet {
    public ModernServlet() {
    }

    public void init(ServletConfig var1) {
        System.out.println("ModernServlet -- init");
    }

    public void doGet(HttpServletRequest var1, HttpServletResponse var2) throws ServletException, IOException {
        var2.setContentType("text/html");
        PrintWriter var3 = var2.getWriter();
        var3.println("HTTP/1.0 200 OK");
        var3.println();
        var3.println("<html>");
        var3.println("<head>");
        var3.println("<title>Modern Servlet</title>");
        var3.println("</head>");
        var3.println("<body>");
        var3.println("<h2>Headers</h2");
        Enumeration var4 = var1.getHeaderNames();

        while(var4.hasMoreElements()) {
            String var5 = (String)var4.nextElement();
            var3.println("<br>" + var5 + " : " + var1.getHeader(var5));
        }

        var3.println("<br><h2>Method</h2");
        var3.println("<br>" + var1.getMethod());
        var3.println("<br><h2>Parameters</h2");
        Enumeration var7 = var1.getParameterNames();

        while(var7.hasMoreElements()) {
            String var6 = (String)var7.nextElement();
            var3.println("<br>" + var6 + " : " + var1.getParameter(var6));
        }

        var3.println("<br><h2>Query String</h2");
        var3.println("<br>" + var1.getQueryString());
        var3.println("<br><h2>Request URI</h2");
        var3.println("<br>" + var1.getRequestURI());
        var3.println("</body>");
        var3.println("</html>");
    }
}