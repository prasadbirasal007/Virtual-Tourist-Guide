package backend;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class PlaceServlet extends HttpServlet {
    //          GET
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setCharacterEncoding("UTF-8");

        String type = req.getParameter("type");
        String avg = req.getParameter("avg");
        String name = req.getParameter("name");
        String search = req.getParameter("search");

        // PROFILE DATA API
        if ("profile".equals(type)) {

            res.setContentType("application/json");
            PrintWriter out = res.getWriter();

            HttpSession session = req.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                out.print("{\"error\":\"not_logged_in\"}");
                return;
            }

            int userId = (int) session.getAttribute("userId");

            try (Connection con = DBConnection.getConnection()) {

                // ALL RATINGS (NO LIMIT)

                PreparedStatement ps1 = con.prepareStatement(
                        "SELECT place_name, rating FROM ratings WHERE user_id=? ORDER BY created_at DESC"
                );
                ps1.setInt(1, userId);
                ResultSet rs1 = ps1.executeQuery();

                StringBuilder ratings = new StringBuilder("[");
                while (rs1.next()) {
                    ratings.append("{")
                            .append("\"place\":\"").append(rs1.getString("place_name")).append("\",")
                            .append("\"rating\":").append(rs1.getInt("rating"))
                            .append("},");
                }
                if (ratings.length() > 1 && ratings.charAt(ratings.length() - 1) == ',') {
                    ratings.deleteCharAt(ratings.length() - 1);
                }
                ratings.append("]");

                // ALL VISITS (NO DISTINCT)

                PreparedStatement ps2 = con.prepareStatement(
                        "SELECT place_name FROM visit_history WHERE user_id=? ORDER BY visited_at DESC"
                );
                ps2.setInt(1, userId);
                ResultSet rs2 = ps2.executeQuery();

                StringBuilder visits = new StringBuilder("[");
                while (rs2.next()) {
                    visits.append("\"")
                            .append(rs2.getString("place_name"))
                            .append("\",");
                }
                if (visits.length() > 1 && visits.charAt(visits.length() - 1) == ',') {
                    visits.deleteCharAt(visits.length() - 1);
                }
                visits.append("]");

                out.print("{\"ratings\":" + ratings + ",\"visits\":" + visits + "}");

            } catch (Exception e) {
                out.print("{\"error\":\"server_error\"}");
            }

            return;
        }

        // AVG RATING API

        if ("true".equals(avg) && name != null) {
            try (Connection con = DBConnection.getConnection()) {

                PreparedStatement ps = con.prepareStatement(
                        "SELECT ROUND(AVG(rating),1) FROM ratings WHERE place_name=?"
                );
                ps.setString(1, name);

                ResultSet rs = ps.executeQuery();
                PrintWriter out = res.getWriter();

                if (rs.next()) {
                    double r = rs.getDouble(1);
                    out.print(r == 0 ? "No ratings" : r);
                }
                return;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // PLACE VISIT TRACKING

        if (name != null && avg == null) {

            HttpSession session = req.getSession(false);

            if (session != null && session.getAttribute("userId") != null) {
                int userId = (int) session.getAttribute("userId");

                try (Connection con = DBConnection.getConnection()) {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO visit_history(user_id, place_name) VALUES (?, ?)"
                    );
                    ps.setInt(1, userId);
                    ps.setString(2, name);
                    ps.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            res.sendRedirect("place.html?name=" + name);
            return;
        }
        // SEARCH
        
        if (search != null) {
            try (Connection con = DBConnection.getConnection()) {

                PreparedStatement ps = con.prepareStatement(
                        "SELECT name FROM places WHERE name LIKE ? LIMIT 1"
                );
                ps.setString(1, "%" + search + "%");

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    res.sendRedirect("place.html?name=" + rs.getString("name"));
                } else {
                    res.sendRedirect("index.html?error=notfound");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    //          POST (SAVE RATING)
    
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String place = req.getParameter("place");
        int rating = Integer.parseInt(req.getParameter("rating"));

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            res.sendRedirect("login.html");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO ratings(place_name, rating, user_id) VALUES (?, ?, ?)"
            );
            ps.setString(1, place);
            ps.setInt(2, rating);
            ps.setInt(3, userId);

            ps.executeUpdate();

            res.sendRedirect("place.html?name=" + place);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
