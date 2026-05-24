package com.example.servlet;

import com.example.model.CartItem;
import com.example.model.MenuItem;
import com.example.util.FileHandler;
import com.example.util.SessionUtil;
import com.example.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet("/menu")
public class MenuServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (!SessionUtil.isLoggedIn(req)) { res.sendRedirect("login.html"); return; }

        boolean isAdmin = SessionUtil.isAdmin(req);
        String action   = req.getParameter("action");

        // Edit form (admin only)
        if ("edit".equals(action) && isAdmin) {
            showEditForm(req, res); return;
        }

        // Get cart count for navbar badge
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) req.getSession().getAttribute("cart");
        int cartCount = cart != null ? cart.size() : 0;

        String restaurantFilter = req.getParameter("restaurant");
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        List<String> lines = FileHandler.readFromFile("menu.txt");

        // Collect unique restaurant names for filter dropdown
        Set<String> restaurants = new LinkedHashSet<>();
        for (String line : lines) {
            MenuItem m = MenuItem.fromFileString(line);
            if (m != null) restaurants.add(m.getRestaurantName());
        }

        out.println("<!DOCTYPE html><html><head><title>Menu – Zeatly</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("<style>");
        out.println(".filter-bar{background:#1f2937;border-radius:16px;padding:20px 24px;margin-bottom:24px;display:flex;gap:14px;align-items:center;flex-wrap:wrap;border:1px solid rgba(255,255,255,0.07)}");
        out.println(".filter-bar input,.filter-bar select{width:auto;flex:1;min-width:180px;padding:10px 14px}");
        out.println(".result-count{color:#9ca3af;font-size:0.85rem;margin-bottom:12px}");
        out.println(".price-range{display:flex;gap:8px;align-items:center;color:#9ca3af;font-size:0.85rem}");
        out.println(".price-range input{width:100px!important;flex:none!important}");
        out.println("</style></head><body>");

        out.println("<nav class='customer-nav'><a class='brand' href='" + (isAdmin ? "admin-dashboard.html" : "dashboard.html") + "'>🔥 Zeatly</a>");
        out.println("<div class='nav-links'>");
        out.println(isAdmin ? "<a href='admin-dashboard.html'>Dashboard</a>" : "<a href='dashboard.html'>Dashboard</a>");
        if (!isNotAdmin) {
            out.println("<a href='cart'>🛒 Cart <span class='nav-badge'>" + cartCount + "</span></a>");
            out.println("<a href='order'>My Orders</a>");
        }
        out.println("<a href='logout' class='nav-logout'>Logout</a></div></nav>");

        out.println("<div class='container'>");
        out.println("<div class='page-header'><h1>🍔 Menu</h1><p>Discover dishes from all our restaurants</p></div>");

        String success = req.getParameter("success");
        if (success != null) out.println("<div class='alert alert-success'>✓ Operation successful!</div>");
        if (isNotAdmin) out.println("<a href='add-menu.html' class='btn' style='margin-bottom:20px'>+ Add Item</a>");

        // --- Search & Filter Bar ---
        out.println("<div class='filter-bar'>");
        out.println("<input type='text' id='searchInput' placeholder='🔍 Search dishes...' oninput='filterMenu()'>");
        out.println("<select id='restaurantFilter' onchange='filterMenu()'>");
        out.println("<option value=''>All Restaurants</option>");
        for (String r : restaurants) {
            String selected = r.equals(restaurantFilter) ? " selected" : "";
            out.println("<option value='" + r + "'" + selected + ">" + r + "</option>");
        }
        out.println("</select>");
        out.println("<div class='price-range'>LKR <input type='number' id='minPrice' placeholder='Min' oninput='filterMenu()' min='0'>");
        out.println("– <input type='number' id='maxPrice' placeholder='Max' oninput='filterMenu()' min='0'></div>");
        out.println("</div>");
        out.println("<div class='result-count' id='resultCount'></div>");

        // --- Menu Grid ---
        out.println("<div class='menu-grid' id='menuGrid'>");

        for (String line : lines) {
            MenuItem item = MenuItem.fromFileString(line);
            if (item == null) continue;

            out.println("<div class='food-card' data-name='" + item.getItemName().toLowerCase() +
                "' data-restaurant='" + item.getRestaurantName() + "' data-price='" + item.getPrice() + "'>");

            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                out.println("<img src='" + item.getImageUrl() + "' alt='" + item.getItemName() + "' onerror=\"this.style.display='none'\">");
            } else {
                out.println("<div class='food-card-no-img'>🍽️</div>");
            }

            out.println("<div class='food-card-body'>");
            out.println("<div class='food-card-restaurant'>" + item.getRestaurantName() + "</div>");
            out.println("<div class='food-card-name'>" + item.getItemName() + "</div>");
            out.println("<div class='food-card-desc'>" + item.getDescription() + "</div>");
            out.println("<div class='food-card-footer'>");
            out.println("<span class='food-card-price'>LKR " + String.format("%.2f", item.getPrice()) + "</span>");

            if (!isAdmin) {
                // Add to Cart form
                out.println("<form method='post' action='cart?action=add' style='display:inline'>");
                out.println("<input type='hidden' name='restaurantName' value='" + item.getRestaurantName() + "'>");
                out.println("<input type='hidden' name='itemName' value='" + item.getItemName() + "'>");
                out.println("<input type='hidden' name='price' value='" + item.getPrice() + "'>");
                out.println("<input type='hidden' name='imageUrl' value='" + item.getImageUrl() + "'>");
                out.println("<button type='submit' class='btn btn-sm'>🛒 Add</button>");
                out.println("</form>");
            }
            out.println("</div>");

            if (isAdmin) {
                out.println("<div class='food-card-actions'>");
                out.println("<a href='menu?action=edit&item=" + java.net.URLEncoder.encode(item.getItemName(), "UTF-8") +
                    "&restaurant=" + java.net.URLEncoder.encode(item.getRestaurantName(), "UTF-8") +
                    "' class='btn btn-sm btn-blue'>✏️ Edit</a>");
                out.println("<form method='post' action='menu?action=delete' style='display:inline'>");
                out.println("<input type='hidden' name='itemName' value='" + item.getItemName() + "'>");
                out.println("<input type='hidden' name='restaurantName' value='" + item.getRestaurantName() + "'>");
                out.println("<button type='submit' class='btn btn-sm btn-danger' onclick=\"return confirm('Delete?')\">🗑️</button>");
                out.println("</form></div>");
            }

            out.println("</div></div>");
        }

        out.println("</div>"); // end menuGrid
        if (lines.isEmpty()) {
            out.println("<div class='empty-state'><div class='empty-icon'>🍽️</div><p>No menu items yet.</p></div>");
        }

        // --- JavaScript for search and filter ---
        out.println("<script>");
        out.println("function filterMenu() {");
        out.println("  const search = document.getElementById('searchInput').value.toLowerCase();");
        out.println("  const rest   = document.getElementById('restaurantFilter').value;");
        out.println("  const minP   = parseFloat(document.getElementById('minPrice').value) || 0;");
        out.println("  const maxP   = parseFloat(document.getElementById('maxPrice').value) || Infinity;");
        out.println("  const cards  = document.querySelectorAll('#menuGrid .food-card');");
        out.println("  let visible  = 0;");
        out.println("  cards.forEach(card => {");
        out.println("    const name  = card.dataset.name || '';");
        out.println("    const crest = card.dataset.restaurant || '';");
        out.println("    const price = parseFloat(card.dataset.price) || 0;");
        out.println("    const show  = name.includes(search) &&");
        out.println("                  (rest === '' || crest === rest) &&");
        out.println("                  price >= minP && price <= maxP;");
        out.println("    card.style.display = show ? '' : 'none';");
        out.println("    if (show) visible++;");
        out.println("  });");
        out.println("  document.getElementById('resultCount').textContent = visible + ' item' + (visible !== 1 ? 's' : '') + ' found';");
        out.println("}");
        out.println("filterMenu();"); // run on page load to show count
        // Pre-select restaurant from URL if passed
        out.println("const urlRest = new URLSearchParams(window.location.search).get('restaurant');");
        out.println("if (urlRest) { document.getElementById('restaurantFilter').value = urlRest; filterMenu(); }");
        out.println("</script>");

        out.println("</div></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (!SessionUtil.isLoggedIn(req)) { res.sendRedirect("login.html"); return; }

        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            if (!SessionUtil.isAdmin(req)) { res.sendRedirect("menu"); return; }
            String itemName = req.getParameter("itemName");
            String restName = req.getParameter("restaurantName");
            List<String> lines = FileHandler.readFromFile("menu.txt");
            List<String> updated = new ArrayList<>();
            for (String line : lines) {
                MenuItem m = MenuItem.fromFileString(line);
                if (m != null && m.getItemName().equals(itemName) && m.getRestaurantName().equals(restName)) continue;
                updated.add(line);
            }
            FileHandler.writeAllLines("menu.txt", updated);
            res.sendRedirect("menu?success=deleted");
            return;
        }

        if ("update".equals(action)) {
            if (!SessionUtil.isAdmin(req)) { res.sendRedirect("menu"); return; }
            String oldItem       = req.getParameter("oldItem");
            String oldRestaurant = req.getParameter("oldRestaurant");
            String restaurantName = ValidationUtil.sanitize(req.getParameter("restaurantName"));
            String itemName       = ValidationUtil.sanitize(req.getParameter("itemName"));
            String priceStr       = req.getParameter("price").trim();
            String description    = ValidationUtil.sanitize(req.getParameter("description"));
            String imageUrl       = req.getParameter("imageUrl").trim();

            if (!ValidationUtil.isValidPrice(priceStr)) { res.sendRedirect("menu?error=price"); return; }

            double price = Double.parseDouble(priceStr);
            MenuItem updated = new MenuItem(restaurantName, itemName, price, description, imageUrl);
            List<String> lines = FileHandler.readFromFile("menu.txt");
            List<String> newLines = new ArrayList<>();
            for (String line : lines) {
                MenuItem m = MenuItem.fromFileString(line);
                if (m != null && m.getItemName().equals(oldItem) && m.getRestaurantName().equals(oldRestaurant)) {
                    newLines.add(updated.toFileString());
                } else { newLines.add(line); }
            }
            FileHandler.writeAllLines("menu.txt", newLines);
            res.sendRedirect("menu?success=updated");
            return;
        }

        // Add new item (admin)
        if (!SessionUtil.isAdmin(req)) { res.sendRedirect("menu"); return; }
        String restaurantName = ValidationUtil.sanitize(req.getParameter("restaurantName"));
        String itemName       = ValidationUtil.sanitize(req.getParameter("itemName"));
        String priceStr       = req.getParameter("price").trim();
        String description    = ValidationUtil.sanitize(req.getParameter("description"));
        String imageUrl       = req.getParameter("imageUrl") != null ? req.getParameter("imageUrl").trim() : "";

        if (restaurantName.isEmpty() || itemName.isEmpty() || !ValidationUtil.isValidPrice(priceStr)) {
            res.sendRedirect("add-menu.html?error=invalid"); return;
        }
        double price = Double.parseDouble(priceStr);
        FileHandler.writeToFile("menu.txt", new MenuItem(restaurantName, itemName, price, description, imageUrl).toFileString());
        res.sendRedirect("menu?success=added");
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String itemName   = req.getParameter("item");
        String restaurant = req.getParameter("restaurant");
        List<String> lines = FileHandler.readFromFile("menu.txt");
        MenuItem found = null;
        for (String line : lines) {
            MenuItem m = MenuItem.fromFileString(line);
            if (m != null && m.getItemName().equals(itemName) && m.getRestaurantName().equals(restaurant)) {
                found = m; break;
            }
        }
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.println("<!DOCTYPE html><html><head><title>Edit Item</title>");
        out.println("<link rel='stylesheet' href='css/style.css'></head><body>");
        out.println("<nav class='customer-nav'><a class='brand' href='admin-dashboard.html'>🔥 Zeatly</a>");
        out.println("<div class='nav-links'><a href='menu'>Menu</a><a href='logout' class='nav-logout'>Logout</a></div></nav>");
        out.println("<div class='container'><div class='page-header'><h1>✏️ Edit Menu Item</h1></div>");
        if (found != null) {
            out.println("<div class='form-wrapper'><div class='form-card'>");
            out.println("<form action='menu?action=update' method='post'>");
            out.println("<input type='hidden' name='oldItem' value='" + found.getItemName() + "'>");
            out.println("<input type='hidden' name='oldRestaurant' value='" + found.getRestaurantName() + "'>");
            out.println("<div class='form-group'><label>Restaurant</label><input type='text' name='restaurantName' value='" + found.getRestaurantName() + "' required></div>");
            out.println("<div class='form-row'>");
            out.println("<div class='form-group'><label>Item Name</label><input type='text' name='itemName' value='" + found.getItemName() + "' required></div>");
            out.println("<div class='form-group'><label>Price (LKR)</label><input type='number' name='price' value='" + found.getPrice() + "' step='0.01' required></div>");
            out.println("</div>");
            out.println("<div class='form-group'><label>Description</label><textarea name='description'>" + found.getDescription() + "</textarea></div>");
            out.println("<div class='form-group'><label>Image URL</label><input type='text' name='imageUrl' value='" + found.getImageUrl() + "'></div>");
            out.println("<div style='display:flex;gap:12px'><button type='submit' class='btn'>Save Changes</button>");
            out.println("<a href='menu' class='btn btn-secondary'>Cancel</a></div>");
            out.println("</form></div></div>");
        }
        out.println("</div></body></html>");
    }
}
