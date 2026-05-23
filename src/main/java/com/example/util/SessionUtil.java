package com.example.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {
    public static boolean isLoggedIn(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null && s.getAttribute("loggedUser") != null;
    }
    public static boolean isAdmin(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null && "admin".equalsIgnoreCase((String)s.getAttribute("userRole"));
    }
    public static boolean isWorker(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null && "worker".equalsIgnoreCase((String)s.getAttribute("userRole"));
    }
    public static String getLoggedUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (String)s.getAttribute("loggedUser");
    }
    public static String getRole(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (String)s.getAttribute("userRole");
    }
}
