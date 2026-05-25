package com.example.servlet;

import com.example.model.User;
import com.example.util.FileHandler;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.sendRedirect("login.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String username = req.getParameter("username").trim();
        String password = req.getParameter("password").trim();

        List<String> users = FileHandler.readFromFile("users.txt");
        boolean found = false;

        for (String line : users) {
            User user = User.fromFileString(line);
            if (user != null && user.getUsername().equals(username)) {
                // BCrypt.checkpw() hashes the typed password the same way
                // and compares — returns true if they match
                // Original password is NEVER stored or compared directly
                boolean passwordMatch = BCrypt.checkpw(password, user.getPassword());
                if (passwordMatch) {
                    found = true;
                    HttpSession session = req.getSession();
                    session.setAttribute("loggedUser", username);
                    session.setAttribute("userRole", user.getRole());
                    break;
                }
            }
        }

        if (found) {
            String role = (String) req.getSession().getAttribute("userRole");
            if ("admin".equalsIgnoreCase(role))       res.sendRedirect("admin-dashboard.html");
            else if ("worker".equalsIgnoreCase(role)) res.sendRedirect("worker-dashboard.html");
            else                                       res.sendRedirect("dashboard.html");
        } else {
            res.sendRedirect("login.html?error=invalid");
        }
    }
}
