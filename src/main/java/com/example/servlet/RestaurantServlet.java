package com.example.servlet;

import com.example.model.Restaurant;
import com.example.util.FileHandler;
import com.example.util.SessionUtil;
import com.example.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet("/restaurant")
public class RestaurantServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) { res.sendRedirect("login.html"); return; }

        boolean isAdmin = SessionUtil.isAdmin(req);
        String action   = req.getParameter("action");

        if ("edit".equals(action) && isAdmin) { showEditForm(req, res); return; }

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        List<String> lines = FileHandler.readFromFile("restaurants.txt");

        if (isAdmin) {
            AdminDashboardServlet.printPageStart(out, "Restaurants");
            AdminDashboardServlet.printSidebar(out, "restaurant");
            out.println("<div class='main-content'>");
            out.println("<div class='topbar'><div><h1 class='page-title'>Restaurants</h1><p class='page-sub'>Manage all restaurants on the platform</p></div></div>");
        } else {
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Restaurants</title>");
            out.println("<link rel='stylesheet' href='css/style.css'></head><body>");
            out.println("<nav class='customer-nav'><a href='dashboard.html' class='brand'>🔥 Zeatly</a>");
            out.println("<div class='nav-links'><a href='restaurant' class='active'>Restaurants</a><a href='menu'>Menu</a>");
            out.println("<a href='cart'>🛒 Cart</a><a href='order'>My Orders</a><a href='feedback'>Reviews</a>");
            out.println("<a href='logout' class='nav-logout'>Sign Out</a></div></nav>");
            out.println("<div class='customer-page'>");
        }

        String success = req.getParameter("success");
        String error   = req.getParameter("error");

        if (isAdmin) {
            out.println("<div class='actions-bar'>");
            out.println("<a href='add-restaurant.html' class='btn'>+ Add Restaurant</a>");
            if (success != null) out.println("<span class='alert alert-success' style='padding:8px 14px'>✓ Done!</span>");
            out.println("</div>");
        } else {
            out.println("<h1 style='font-size:1.4rem;font-weight:700;margin-bottom:6px'>🏪 Restaurants</h1>");
            out.println("<p style='color:#9ca3af;font-size:0.85rem;margin-bottom:20px'>Choose a restaurant to view their menu</p>");
        }

        if (lines.isEmpty()) {
            out.println("<div class='empty-state'><div class='empty-icon'>🏪</div><p>No restaurants added yet.</p></div>");
        } else if (isAdmin) {
            out.println("<div class='card'><table class='data-table'>");
            out.println("<tr><th>#</th><th>Name</th><th>Location</th><th>Cuisine</th><th>Actions</th></tr>");
            int i=1;
            for (String line : lines) {
                Restaurant r = Restaurant.fromFileString(line);
                if (r == null) continue;
                out.println("<tr><td>"+i+++"</td><td><strong>"+r.getName()+"</strong></td><td>"+r.getLocation()+"</td><td><span class='badge badge-placed'>"+r.getCuisine()+"</span></td>");
                out.println("<td><div style='display:flex;gap:6px'>");
                out.println("<a href='menu?restaurant="+java.net.URLEncoder.encode(r.getName(),"UTF-8")+"' class='btn btn-sm btn-success'>View Menu</a>");
                out.println("<a href='restaurant?action=edit&name="+java.net.URLEncoder.encode(r.getName(),"UTF-8")+"' class='btn btn-sm btn-warning'>✏️ Edit</a>");
                out.println("<form method='post' action='restaurant?action=delete' style='display:inline'><input type='hidden' name='name' value='"+r.getName()+"'>");
                out.println("<button class='btn btn-sm btn-danger' onclick=\"return confirm('Delete "+r.getName()+"?')\">🗑️ Delete</button></form>");
                out.println("</div></td></tr>");
            }
            out.println("</table></div>");
        } else {
            out.println("<div class='restaurant-grid'>");
            for (String line : lines) {
                Restaurant r = Restaurant.fromFileString(line);
                if (r == null) continue;
                out.println("<div class='rest-card'>");
                out.println("<div class='rest-card-header'>🏪</div>");
                out.println("<div class='rest-card-body'>");
                out.println("<div class='rest-card-name'>"+r.getName()+"</div>");
                out.println("<div class='rest-card-loc'>📍 "+r.getLocation()+"</div>");
                out.println("<div class='rest-card-cuisine'>"+r.getCuisine()+"</div>");
                out.println("<div class='rest-card-footer'>");
                out.println("<a href='menu?restaurant="+java.net.URLEncoder.encode(r.getName(),"UTF-8")+"' class='btn btn-sm' style='flex:1;justify-content:center'>View Menu</a>");
                out.println("</div></div></div>");
            }
            out.println("</div>");
        }

        out.println(isAdmin ? "</div></div>" : "</div>");
        AdminDashboardServlet.printPageEnd(out);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!SessionUtil.isAdmin(req)) { res.sendRedirect("restaurant"); return; }

        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            String name = req.getParameter("name");
            FileHandler.deleteByColumn("restaurants.txt", 0, name);
            FileHandler.deleteByColumn("menu.txt", 0, name);
            res.sendRedirect("restaurant?success=deleted"); return;
        }
        if ("update".equals(action)) {
            String oldName  = req.getParameter("oldName").trim();
            String name     = ValidationUtil.sanitize(req.getParameter("name"));
            String location = ValidationUtil.sanitize(req.getParameter("location"));
            String cuisine  = ValidationUtil.sanitize(req.getParameter("cuisine"));
            FileHandler.updateLine("restaurants.txt", 0, oldName, new Restaurant(name,location,cuisine).toFileString());
            res.sendRedirect("restaurant?success=updated"); return;
        }

        // Add
        String name     = ValidationUtil.sanitize(req.getParameter("name"));
        String location = ValidationUtil.sanitize(req.getParameter("location"));
        String cuisine  = ValidationUtil.sanitize(req.getParameter("cuisine"));
        if (name.isEmpty()||location.isEmpty()||cuisine.isEmpty()) { res.sendRedirect("add-restaurant.html?error=empty"); return; }
        FileHandler.writeToFile("restaurants.txt", new Restaurant(name,location,cuisine).toFileString());
        res.sendRedirect("restaurant?success=added");
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String name = req.getParameter("name");
        String line = FileHandler.findLine("restaurants.txt", 0, name);
        Restaurant r = line != null ? Restaurant.fromFileString(line) : null;
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        AdminDashboardServlet.printPageStart(out, "Edit Restaurant");
        AdminDashboardServlet.printSidebar(out, "restaurant");
        out.println("<div class='main-content'><div class='topbar'><div><h1 class='page-title'>Edit Restaurant</h1></div></div>");
        out.println("<div class='form-page'><div class='form-container'><div class='form-card'>");
        if (r != null) {
            out.println("<form action='restaurant?action=update' method='post'>");
            out.println("<input type='hidden' name='oldName' value='"+r.getName()+"'>");
            out.println("<div class='form-group'><label>Name</label><input type='text' name='name' value='"+r.getName()+"' required></div>");
            out.println("<div class='form-group'><label>Location</label><input type='text' name='location' value='"+r.getLocation()+"' required></div>");
            out.println("<div class='form-group'><label>Cuisine</label><input type='text' name='cuisine' value='"+r.getCuisine()+"' required></div>");
            out.println("<div style='display:flex;gap:10px'><button type='submit' class='btn'>Save</button><a href='restaurant' class='btn btn-secondary'>Cancel</a></div>");
            out.println("</form>");
        }
        out.println("</div></div></div></div></div>");
        AdminDashboardServlet.printPageEnd(out);
    }
}
