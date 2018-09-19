package mypackage;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.*;

public class Ping extends HttpServlet {
  private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

  public void init(ServletConfig config) throws ServletException {
    super.init(config);
  }

  public void doGet(HttpServletRequest request,
                    HttpServletResponse response) throws ServletException,
                                                                                     IOException {
    response.setContentType(CONTENT_TYPE);
    PrintWriter out = response.getWriter();
    out.println("<html>");
    out.println("<head><title>Ping</title></head>");
    out.println("<body>");
    out.println(request.getParameter("foo"));
    out.println("</body></html>");
    out.close();
  }
}
