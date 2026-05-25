package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Destroys the current session (logs the user out).
 * URL: /logout
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get current session and invalidate it
        HttpSession session = request.getSession(false); // false = don't create a new one
        if (session != null) {
            session.invalidate(); // destroys all session data
        }

        response.sendRedirect("login.html?msg=loggedout");
    }
}
