package backend;

import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class ProfileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.sendRedirect("login.html");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        try (Connection con = DBConnection.getConnection()) {

            // USER INFO
            PreparedStatement u = con.prepareStatement(
                "SELECT name, email FROM users WHERE id=?"
            );
            u.setInt(1, userId);
            ResultSet ur = u.executeQuery();
            if (ur.next()) {
                req.setAttribute("name", ur.getString("name"));
                req.setAttribute("email", ur.getString("email"));
            }

            // RATINGS
            PreparedStatement r = con.prepareStatement(
                "SELECT place_name, rating FROM ratings WHERE user_id=?"
            );
            r.setInt(1, userId);
            req.setAttribute("ratings", r.executeQuery());

            // VISITS
            PreparedStatement v = con.prepareStatement(
                "SELECT place_name FROM visit_history WHERE user_id=?"
            );
            v.setInt(1, userId);
            req.setAttribute("visits", v.executeQuery());

            RequestDispatcher rd = req.getRequestDispatcher("profile.html");
            rd.forward(req, res);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}