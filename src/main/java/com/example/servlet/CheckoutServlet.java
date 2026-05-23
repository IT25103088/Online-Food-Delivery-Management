package com.example.servlet;

import com.example.model.CartItem;
import com.example.model.Order;
import com.example.util.HtmlUtil;
import com.example.util.OrderStore;
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

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
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
        if (cart.isEmpty()) {
            res.sendRedirect("cart?msg=empty");
            return;
        }
        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        out.println("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>");
        out.println("<title>Checkout Page - Order Management</title><link rel='stylesheet' href='css/order-management.css'></head><body>");
        CartServlet.printNav(out, "checkout");
        out.println("<main class='page'><div class='page-header'><p class='eyebrow'>Component 04</p><h1>Checkout Page</h1><p>Confirm delivery details and place a new order.</p></div>");
        out.println("<section class='checkout-layout'><form class='form-card' method='post' action='checkout'>");
        out.println("<h2>Delivery Details</h2><label>Delivery address</label><textarea name='deliveryAddress' required placeholder='Enter your full delivery address'></textarea>");
        out.println("<label>Contact number</label><input name='phone' placeholder='Optional phone number'>");
        out.println("<label>Order note</label><textarea name='note' placeholder='Optional cooking or rider instructions'></textarea>");
        out.println("<button class='btn btn-primary full' type='submit'>Place Order</button></form>");
        out.println("<aside class='summary-card'><h2>Checkout Summary</h2>");
        for (CartItem item : cart) {
            out.println("<div class='summary-row'><span>" + HtmlUtil.esc(item.getItemName()) + " x " + item.getQuantity() + "</span><span>" + HtmlUtil.money(item.getSubtotal()) + "</span></div>");
        }
        out.println("<div class='summary-total'><span>Total</span><strong>" + HtmlUtil.money(total) + "</strong></div>");
        out.println("<p class='hint'>A separate order record is created for each restaurant/item line in orders.txt.</p>");
        out.println("</aside></section></main></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) {
            res.sendRedirect("component4.html");
            return;
        }
        List<CartItem> cart = getCart(req);
        if (cart.isEmpty()) {
            res.sendRedirect("cart?msg=empty");
            return;
        }
        String username = SessionUtil.getLoggedUser(req);
        String address = req.getParameter("deliveryAddress");
        for (CartItem item : cart) {
            Order order = Order.createNew(username, item.getRestaurantName(), item.getItemName(),
                    item.getQuantity(), item.getSubtotal(), address);
            OrderStore.save(order);
        }
        cart.clear();
        res.sendRedirect("orders?success=placed");
    }
}
