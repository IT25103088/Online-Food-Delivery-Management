package com.example.servlet;

import com.example.model.CartItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/demo-login")
public class DemoLoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String role = req.getParameter("role");
        if (role == null || role.isBlank()) role = "customer";
        role = role.toLowerCase();
        String username = switch (role) {
            case "admin" -> "admin";
            case "worker" -> "worker01";
            default -> "customer01";
        };

        HttpSession session = req.getSession(true);
        session.setAttribute("loggedUser", username);
        session.setAttribute("userRole", role);

        if ("customer".equals(role)) {
            List<CartItem> cart = new ArrayList<>();
            cart.add(new CartItem("Spice Garden", "Chicken Kottu", 1450.00, 2, ""));
            cart.add(new CartItem("Pizza House", "Cheese Pizza", 2200.00, 1, ""));
            session.setAttribute("cart", cart);
            res.sendRedirect("cart");
        } else {
            res.sendRedirect("orders");
        }
    }
}
