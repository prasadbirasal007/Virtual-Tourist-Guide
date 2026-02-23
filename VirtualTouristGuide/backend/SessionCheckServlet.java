package backend;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;


public class SessionCheckServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session != null && session.getAttribute("userId") != null) {
            res.setStatus(HttpServletResponse.SC_OK);  // 200
        } else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        }
    }
}
