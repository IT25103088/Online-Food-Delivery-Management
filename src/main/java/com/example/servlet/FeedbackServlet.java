package com.example.servlet;

import com.example.model.Feedback;
import com.example.util.FileHandler;
import com.example.util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/feedback")
public class FeedbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect("login.html?error=notloggedin");
            return;
        }

        boolean isAdmin = SessionUtil.isAdmin(request);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        List<String> lines = FileHandler.readFromFile("feedback.txt");

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Reviews – Zeatly</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");

        out.println("<div class='app-layout'>");

        // ================= SIDEBAR =================
        out.println("<aside class='sidebar'>");
        out.println("<div class='sidebar-brand'>🔥 <span>Zeatly</span></div>");

        out.println("<div class='sidebar-section'>MAIN MODULES</div>");
        out.println("<a href='dashboard.html' class='sidebar-link'>🏠 Home</a>");
        out.println("<a href='restaurant' class='sidebar-link'>🏪 Restaurants</a>");
        out.println("<a href='menu' class='sidebar-link'>🍔 Menu Items</a>");
        out.println("<a href='cart' class='sidebar-link'>🛒 Cart</a>");
        out.println("<a href='order' class='sidebar-link'>📦 Orders</a>");
        out.println("<a href='feedback' class='sidebar-link active'>⭐ Reviews</a>");

        out.println("<div class='sidebar-section' style='margin-top:20px'>ACCOUNT</div>");
        out.println("<a href='logout' class='sidebar-link'>🚪 Sign Out</a>");

        out.println("</aside>");

        // ================= MAIN CONTENT =================
        out.println("<div class='main-content'>");

        // TOPBAR
        out.println("<div class='topbar'>");
        out.println("<div>");
        out.println("<h1 class='page-title'>⭐ Customer Reviews</h1>");
        out.println("<p class='page-sub'>Read feedback from customers</p>");
        out.println("</div>");

        if (!isAdmin) {
            out.println("<a href='add-feedback.html' class='btn'>+ Write Review</a>");
        }

        out.println("</div>");

        // ================= REVIEWS CARD =================
        out.println("<div class='card'>");

        if (lines.isEmpty()) {
            out.println("<p style='color:#9ca3af'>No reviews yet.</p>");
        } else {

            for (String line : lines) {

                Feedback fb = Feedback.fromFileString(line);
                if (fb == null) continue;

                out.println("<div style='padding:16px 0;border-bottom:1px solid #eee'>");

                out.println("<div style='display:flex;justify-content:space-between;align-items:flex-start'>");

                out.println("<div>");

                out.println("<div style='font-weight:600;font-size:0.95rem'>"
                        + fb.getUsername() + "</div>");

                out.println("<div style='font-size:0.8rem;color:#6b7280;margin-bottom:6px'>"
                        + fb.getRestaurantName() + "</div>");

                // STARS
                out.println("<div style='color:#fbbf24;font-size:1.1rem;margin-bottom:6px'>");
                for (int i = 1; i <= 5; i++) {
                    out.print(i <= fb.getRating() ? "★" : "☆");
                }
                out.println("</div>");

                out.println("<div style='color:#374151;font-size:0.9rem;line-height:1.4'>"
                        + fb.getReview() + "</div>");

                out.println("</div>");

                // ADMIN DELETE BUTTON
                if (isAdmin) {
                    out.println("<form method='post' action='feedback'>");
                    out.println("<input type='hidden' name='username' value='" + fb.getUsername() + "'>");
                    out.println("<input type='hidden' name='restaurant' value='" + fb.getRestaurantName() + "'>");
                    out.println("<button class='btn btn-sm btn-danger' "
                            + "onclick=\"return confirm('Delete this review?')\">🗑 Delete</button>");
                    out.println("</form>");
                }

                out.println("</div>");

                out.println("</div>");
            }
        }

        out.println("</div>"); // card
        out.println("</div>"); // main-content
        out.println("</div>"); // app-layout

        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect("login.html");
            return;
        }

        // DELETE (ADMIN ONLY)
        if (SessionUtil.isAdmin(request)) {

            String username = request.getParameter("username");
            String restaurant = request.getParameter("restaurant");

            List<String> lines = FileHandler.readFromFile("feedback.txt");
            List<String> updated = new java.util.ArrayList<>();

            for (String line : lines) {

                Feedback fb = Feedback.fromFileString(line);
                if (fb == null) continue;

                boolean match =
                        fb.getUsername().equalsIgnoreCase(username) &&
                                fb.getRestaurantName().equalsIgnoreCase(restaurant);

                if (!match) {
                    updated.add(line);
                }
            }

            FileHandler.writeAllLines("feedback.txt", updated);
            response.sendRedirect("feedback?success=deleted");
            return;
        }

        // ADD REVIEW (USER)
        String user = SessionUtil.getLoggedUser(request);
        String restaurant = request.getParameter("restaurantName");
        String review = request.getParameter("review");
        String ratingStr = request.getParameter("rating");

        if (restaurant == null || review == null || ratingStr == null ||
                restaurant.trim().isEmpty() ||
                review.trim().isEmpty() ||
                ratingStr.trim().isEmpty()) {

            response.sendRedirect("add-feedback.html?error=empty");
            return;
        }

        try {
            int rating = Integer.parseInt(ratingStr);

            Feedback fb = new Feedback(
                    user,
                    restaurant.trim(),
                    review.trim(),
                    rating
            );

            FileHandler.writeToFile("feedback.txt", fb.toFileString());

            response.sendRedirect("feedback?success=added");

        } catch (Exception e) {
            response.sendRedirect("add-feedback.html?error=invalid");
        }
    }
}