package com.example.servlet;

import com.example.model.User;
import com.example.util.FileHandler;
import com.example.util.ValidationUtil;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.sendRedirect("register.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String username = req.getParameter("username").trim();
        String email    = req.getParameter("email").trim();
        String password = req.getParameter("password").trim();

        // --- Server-side validation ---
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            res.sendRedirect("register.html?error=empty"); return;
        }
        if (username.length() > 20) {
            res.sendRedirect("register.html?error=usernamelong"); return;
        }
        if (ValidationUtil.containsPipe(username) || ValidationUtil.containsPipe(password)) {
            res.sendRedirect("register.html?error=invalidchars"); return;
        }
        if (!ValidationUtil.isValidEmail(email)) {
            res.sendRedirect("register.html?error=invalidemail"); return;
        }
        if (password.length() < 6) {
            res.sendRedirect("register.html?error=shortpassword"); return;
        }
        if (FileHandler.valueExistsInColumn("users.txt", 0, username)) {
            res.sendRedirect("register.html?error=exists"); return;
        }

        // --- Hash the password with BCrypt ---
        // BCrypt.gensalt() generates a random salt (makes every hash unique)
        // BCrypt.hashpw() runs the hashing algorithm
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        List<String> existing = FileHandler.readFromFile("users.txt");
        String role = existing.isEmpty() ? "admin" : "user";

        // Save hashed password — original is never stored
        User user = new User(username, email, hashedPassword, role);
        FileHandler.writeToFile("users.txt", user.toFileString());

        res.sendRedirect("login.html?success=registered");
    }
}
