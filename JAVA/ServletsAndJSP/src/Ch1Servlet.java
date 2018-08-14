
import javax.servlet.http.*;
import java.io.*;

public class Ch1Servlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest reque, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		java.util.Date today = new java.util.Date();
		out.println("<html> " + 
				"<body>" + 
				"<h1 align=center>HF\'s Chapter1 Servlet</h1>" + 
				"<br>" + today + "</body>" + "</html>");
	}
	
}
