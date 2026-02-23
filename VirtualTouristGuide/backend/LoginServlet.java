package backend;

import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class LoginServlet extends HttpServlet {


    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT user_id FROM users WHERE username=? AND password=?"
            );
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int userId = rs.getInt("user_id");

                HttpSession session = req.getSession();
                session.setAttribute("userId", userId);
                session.setAttribute("username", username);

                res.sendRedirect("index.html");

            } else {
                res.sendRedirect("login.html?error=1");
            }

            con.close();

        } catch (Exception e) {
            res.getWriter().println("Error: " + e.getMessage());
        }
    }
}
