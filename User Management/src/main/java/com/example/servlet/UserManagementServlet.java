package com.example.servlet;

import com.example.model.User;
import com.example.util.FileHandler;
import com.example.util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.util.*;

@WebServlet("/users")
public class UserManagementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (!SessionUtil.isAdmin(req)) {
            res.sendRedirect("login.html");
            return;
        }

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        List<String> lines = FileHandler.readFromFile("users.txt");

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");

        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>User Management – Zeatly</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");

        out.println("<body>");

        out.println("<div class='app-layout'>");

        // SIDEBAR
        AdminDashboardServlet.printSidebar(out, "users");

        // MAIN CONTENT
        out.println("<div class='main-content'>");

        // TOPBAR
        out.println("<div class='topbar'>");

        out.println("<div>");
        out.println("<h1 class='page-title'>User Management</h1>");
        out.println("<p class='page-sub'>Manage platform users and permissions</p>");
        out.println("</div>");

        out.println("<div class='topbar-right'>");

        out.println("<div class='user-chip'>");

        out.println("<div class='user-avatar'>");
        out.println(SessionUtil.getLoggedUser(req).charAt(0));
        out.println("</div>");

        out.println("<div>");
        out.println("<div class='user-name'>");
        out.println(SessionUtil.getLoggedUser(req));
        out.println("</div>");
        out.println("<div class='user-role'>Administrator</div>");
        out.println("</div>");

        out.println("</div>");
        out.println("</div>");
        out.println("</div>");

        // CONTENT WRAPPER
        out.println("<div class='content-wrapper'>");

        String msg = req.getParameter("success");

        if (msg != null) {
            out.println("<div class='alert alert-success'>");
            out.println("✓ User updated successfully.");
            out.println("</div>");
        }

        // TABLE CARD
        out.println("<div class='table-wrapper'>");

        out.println("<table class='modern-table'>");

        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>#</th>");
        out.println("<th>Username</th>");
        out.println("<th>Email</th>");
        out.println("<th>Role</th>");
        out.println("<th>Actions</th>");
        out.println("</tr>");
        out.println("</thead>");

        out.println("<tbody>");

        int i = 1;

        for (String line : lines) {

            User u = User.fromFileString(line);

            if (u == null) continue;

            String roleBadge =
                    u.isAdmin() ? "badge-admin"
                            : u.isWorker() ? "badge-worker"
                            : "badge-user";

            out.println("<tr>");

            out.println("<td>" + i++ + "</td>");

            out.println("<td>");
            out.println("<div style='display:flex;align-items:center;gap:12px'>");

            out.println("<div class='user-avatar' style='width:40px;height:40px;font-size:14px'>");
            out.println(u.getUsername().substring(0,1).toUpperCase());
            out.println("</div>");

            out.println("<div>");
            out.println("<div style='font-weight:600'>" + u.getUsername() + "</div>");
            out.println("</div>");

            out.println("</div>");
            out.println("</td>");

            out.println("<td>" + u.getEmail() + "</td>");

            out.println("<td>");
            out.println("<span class='badge " + roleBadge + "'>");
            out.println(u.getRole().toUpperCase());
            out.println("</span>");
            out.println("</td>");

            out.println("<td>");

            out.println("<div class='td-actions'>");

            if (!u.isAdmin()) {

                // PROMOTE / DEMOTE

                out.println("<form method='post' action='users'>");

                out.println("<input type='hidden' name='username' value='" + u.getUsername() + "'>");

                if (u.isWorker()) {

                    out.println("<input type='hidden' name='action' value='demote'>");

                    out.println("<button class='btn btn-sm btn-secondary'>");
                    out.println("Make User");
                    out.println("</button>");

                } else {

                    out.println("<input type='hidden' name='action' value='promote'>");

                    out.println("<button class='btn btn-sm btn-blue'>");
                    out.println("Make Worker");
                    out.println("</button>");
                }

                out.println("</form>");

                // DELETE

                out.println("<form method='post' action='users'>");

                out.println("<input type='hidden' name='username' value='" + u.getUsername() + "'>");
                out.println("<input type='hidden' name='action' value='delete'>");

                out.println("<button class='btn btn-sm btn-danger' ");
                out.println("onclick=\"return confirm('Delete user?')\">");
                out.println("Delete");
                out.println("</button>");

                out.println("</form>");

            } else {

                out.println("<span style='color:#64748b;font-weight:600'>Protected</span>");
            }

            out.println("</div>");

            out.println("</td>");

            out.println("</tr>");
        }

        out.println("</tbody>");
        out.println("</table>");
        out.println("</div>");

        out.println("</div>");
        out.println("</div>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (!SessionUtil.isAdmin(req)) {
            res.sendRedirect("login.html");
            return;
        }

        String username = req.getParameter("username");
        String action = req.getParameter("action");

        if ("delete".equals(action)) {

            FileHandler.deleteByColumn("users.txt", 0, username);

        } else if ("promote".equals(action)) {

            String line = FileHandler.findLine("users.txt", 0, username);

            if (line != null) {

                User u = User.fromFileString(line);

                if (u != null) {

                    u = new User(
                            u.getUsername(),
                            u.getEmail(),
                            u.getPassword(),
                            "worker"
                    );

                    FileHandler.updateLine(
                            "users.txt",
                            0,
                            username,
                            u.toFileString()
                    );
                }
            }

        } else if ("demote".equals(action)) {

            String line = FileHandler.findLine("users.txt", 0, username);

            if (line != null) {

                User u = User.fromFileString(line);

                if (u != null) {

                    u = new User(
                            u.getUsername(),
                            u.getEmail(),
                            u.getPassword(),
                            "user"
                    );

                    FileHandler.updateLine(
                            "users.txt",
                            0,
                            username,
                            u.toFileString()
                    );
                }
            }
        }

        res.sendRedirect("users?success=updated");
    }
}