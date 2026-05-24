package com.example.servlet;

import com.example.model.CartItem;
import com.example.util.HtmlUtil;
import com.example.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        Object value = session.getAttribute("cart");
        if (value instanceof List<?>) return (List<CartItem>) value;
        List<CartItem> cart = new ArrayList<>();
        session.setAttribute("cart", cart);
        return cart;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) {
            res.sendRedirect("component4.html");
            return;
        }
        List<CartItem> cart = getCart(req);
        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        out.println("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>");
        out.println("<title>Cart Page - Order Management</title><link rel='stylesheet' href='css/order-management.css'></head><body>");
        printNav(out, "cart");
        out.println("<main class='page'><div class='page-header'><p class='eyebrow'>Component 04</p><h1>Cart Page</h1><p>Review selected food items before checkout.</p></div>");

        String msg = req.getParameter("msg");
        if ("updated".equals(msg)) out.println("<div class='alert success'>Cart updated.</div>");
        if ("removed".equals(msg)) out.println("<div class='alert success'>Item removed from cart.</div>");
        if ("empty".equals(msg)) out.println("<div class='alert error'>Your cart is empty.</div>");

        if (cart.isEmpty()) {
            out.println("<section class='empty-state'><h2>Your cart is empty</h2><p>Add menu items from component 3 before placing an order.</p></section>");
        } else {
            out.println("<section class='checkout-layout'><div class='stack'>");
            for (int i = 0; i < cart.size(); i++) {
                CartItem item = cart.get(i);
                out.println("<article class='cart-card'>");
                out.println("<div class='food-thumb'>" + (item.getImageUrl().isBlank() ? "Food" : "<img src='" + HtmlUtil.esc(item.getImageUrl()) + "' alt=''>") + "</div>");
                out.println("<div class='cart-info'><h3>" + HtmlUtil.esc(item.getItemName()) + "</h3><p>" + HtmlUtil.esc(item.getRestaurantName()) + "</p><strong>" + HtmlUtil.money(item.getPrice()) + " each</strong></div>");
                out.println("<div class='cart-actions'><form method='post' action='cart?action=update'><input type='hidden' name='index' value='" + i + "'><label>Qty</label><input type='number' name='quantity' min='1' max='20' value='" + item.getQuantity() + "'><button class='btn btn-dark' type='submit'>Update</button></form>");
                out.println("<form method='post' action='cart?action=remove'><input type='hidden' name='index' value='" + i + "'><button class='btn btn-danger' type='submit'>Remove</button></form></div>");
                out.println("</article>");
            }
            out.println("</div><aside class='summary-card'><h2>Order Summary</h2>");
            for (CartItem item : cart) {
                out.println("<div class='summary-row'><span>" + HtmlUtil.esc(item.getItemName()) + " x " + item.getQuantity() + "</span><span>" + HtmlUtil.money(item.getSubtotal()) + "</span></div>");
            }
            out.println("<div class='summary-total'><span>Total</span><strong>" + HtmlUtil.money(total) + "</strong></div>");
            out.println("<a class='btn btn-primary full' href='checkout'>Go to Checkout</a>");
            out.println("<form method='post' action='cart?action=clear'><button class='btn btn-outline full' type='submit'>Clear Cart</button></form>");
            out.println("</aside></section>");
        }
        out.println("</main></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) {
            res.sendRedirect("component4.html");
            return;
        }
        String action = req.getParameter("action");
        List<CartItem> cart = getCart(req);

        if ("add".equals(action)) {
            String restaurant = req.getParameter("restaurantName");
            String item = req.getParameter("itemName");
            double price = parseDouble(req.getParameter("price"), 0);
            int quantity = Math.max(1, parseInt(req.getParameter("quantity"), 1));
            String imageUrl = req.getParameter("imageUrl");
            cart.add(new CartItem(restaurant, item, price, quantity, imageUrl));
            res.sendRedirect("cart?msg=updated");
            return;
        }

        if ("update".equals(action)) {
            int index = parseInt(req.getParameter("index"), -1);
            int qty = Math.max(1, parseInt(req.getParameter("quantity"), 1));
            if (index >= 0 && index < cart.size()) cart.get(index).setQuantity(qty);
            res.sendRedirect("cart?msg=updated");
            return;
        }

        if ("remove".equals(action)) {
            int index = parseInt(req.getParameter("index"), -1);
            if (index >= 0 && index < cart.size()) cart.remove(index);
            res.sendRedirect("cart?msg=removed");
            return;
        }

        if ("clear".equals(action)) cart.clear();
        res.sendRedirect("cart?msg=updated");
    }

    static void printNav(PrintWriter out, String active) {
        out.println("<nav class='top-nav'><a class='brand' href='component4.html'>Zeatly</a><div class='nav-links'>");
        out.println("<a class='" + ("cart".equals(active) ? "active" : "") + "' href='cart'>Cart</a>");
        out.println("<a class='" + ("checkout".equals(active) ? "active" : "") + "' href='checkout'>Checkout</a>");
        out.println("<a class='" + ("orders".equals(active) ? "active" : "") + "' href='orders'>Order Tracking</a>");
        out.println("<a class='" + ("history".equals(active) ? "active" : "") + "' href='orders?view=history'>Order History</a>");
        out.println("<a href='logout'>Logout</a></div></nav>");
    }

    static int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value); } catch (Exception e) { return fallback; }
    }

    static double parseDouble(String value, double fallback) {
        try { return Double.parseDouble(value); } catch (Exception e) { return fallback; }
    }
}
