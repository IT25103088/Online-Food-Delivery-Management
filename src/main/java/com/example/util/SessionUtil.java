package com.example.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {
    public static boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("loggedUser") != null;
    }

    public static String getLoggedUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        Object value = session == null ? null : session.getAttribute("loggedUser");
        return value == null ? null : value.toString();
    }

    public static String getRole(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        Object value = session == null ? null : session.getAttribute("userRole");
        return value == null ? "customer" : value.toString();
    }

    public static boolean isAdmin(HttpServletRequest req) {
        return "admin".equalsIgnoreCase(getRole(req));
    }

    public static boolean isWorker(HttpServletRequest req) {
        return "worker".equalsIgnoreCase(getRole(req));
    }
}
