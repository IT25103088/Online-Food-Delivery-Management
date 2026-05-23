package com.example.servlet;

import com.example.model.Order;
import com.example.util.HtmlUtil;
import com.example.util.OrderStore;
import com.example.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) {
            res.sendRedirect("component4.html");
            return;
        }
        boolean admin = SessionUtil.isAdmin(req);
        boolean worker = SessionUtil.isWorker(req);
        String user = SessionUtil.getLoggedUser(req);
        boolean historyView = "history".equals(req.getParameter("view"));

        List<Order> visible = OrderStore.findVisibleTo(user, admin, worker);
        Collections.reverse(visible);
        List<Order> active = new ArrayList<>();
        List<Order> history = new ArrayList<>();
        for (Order order : visible) {
            if (order.isHistory()) history.add(order); else active.add(order);
        }
        List<Order> shown = historyView ? history : active;

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        out.println("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>");
        out.println("<title>" + (historyView ? "Order History" : "Order Tracking") + " - Order Management</title><link rel='stylesheet' href='css/order-management.css'></head><body>");
        CartServlet.printNav(out, historyView ? "history" : "orders");
        out.println("<main class='page'><div class='page-header'><p class='eyebrow'>Component 04</p><h1>" + (historyView ? "Order History Page" : "Order Tracking Page") + "</h1>");
        out.println("<p>Logged in as <strong>" + HtmlUtil.esc(user) + "</strong> (" + HtmlUtil.esc(SessionUtil.getRole(req)) + "). " + (historyView ? "View completed and cancelled orders." : "Track live order status and manage preparation.") + "</p></div>");

        String success = req.getParameter("success");
        String error = req.getParameter("error");
        if ("placed".equals(success)) out.println("<div class='alert success'>Order placed successfully and stored in orders.txt.</div>");
        if ("updated".equals(success)) out.println("<div class='alert success'>Order status updated.</div>");
        if ("deleted".equals(success)) out.println("<div class='alert success'>Order cancelled/deleted.</div>");
        if ("notallowed".equals(error)) out.println("<div class='alert error'>This action is not allowed for the selected order.</div>");

        out.println("<section class='stats-grid'><div class='stat-card'><span>Active Orders</span><strong>" + active.size() + "</strong></div><div class='stat-card'><span>History</span><strong>" + history.size() + "</strong></div><div class='stat-card'><span>Data File</span><strong>orders.txt</strong></div></section>");

        if (shown.isEmpty()) {
            out.println("<section class='empty-state'><h2>No orders found</h2><p>" + (historyView ? "Delivered and cancelled orders will appear here." : "New checkout orders will appear here for tracking.") + "</p></section>");
        } else {
            out.println("<section class='orders-grid'>");
            for (Order order : shown) printOrderCard(out, order, user, admin, worker);
            out.println("</section>");
        }
        out.println("</main></body></html>");
    }

    private void printOrderCard(PrintWriter out, Order order, String user, boolean admin, boolean worker) {
        out.println("<article class='order-card'>");
        out.println("<div class='order-card-header'><span class='order-id'>" + HtmlUtil.esc(order.getOrderId()) + "</span><span class='status-badge " + statusClass(order.getStatus()) + "'>" + HtmlUtil.esc(order.getStatus()) + "</span></div>");
        out.println("<div class='order-card-body'><p class='restaurant-pill'>" + HtmlUtil.esc(order.getRestaurantName()) + "</p><h3>" + HtmlUtil.esc(order.getItemName()) + "</h3>");
        out.println("<div class='meta-row'><span>Qty: " + order.getQuantity() + "</span><span>Customer: " + HtmlUtil.esc(order.getUsername()) + "</span><span>Created: " + HtmlUtil.esc(order.getCreatedAt()) + "</span></div>");
        out.println("<p class='address'>Delivery: " + HtmlUtil.esc(order.getDeliveryAddress()) + "</p>");
        out.println("<div class='progress'><span class='" + stepClass(order, Order.STATUS_PLACED) + "'>Placed</span><span class='" + stepClass(order, Order.STATUS_PREPARING) + "'>Preparing</span><span class='" + stepClass(order, Order.STATUS_OUT_FOR_DELIVERY) + "'>Out for Delivery</span><span class='" + stepClass(order, Order.STATUS_DELIVERED) + "'>Delivered</span></div>");
        out.println("<div class='total'>" + HtmlUtil.money(order.getTotalPrice()) + "</div></div>");
        out.println("<div class='order-card-footer'>");
        if (admin || worker) {
            out.println("<form class='inline-form' method='post' action='orders'><input type='hidden' name='action' value='updateStatus'><input type='hidden' name='orderId' value='" + HtmlUtil.esc(order.getOrderId()) + "'><select name='status'>");
            printOption(out, Order.STATUS_PLACED, order.getStatus());
            printOption(out, Order.STATUS_PREPARING, order.getStatus());
            printOption(out, Order.STATUS_OUT_FOR_DELIVERY, order.getStatus());
            printOption(out, Order.STATUS_DELIVERED, order.getStatus());
            out.println("</select><button class='btn btn-dark' type='submit'>Update</button></form>");
        }
        boolean owner = order.getUsername().equals(user);
        if (admin || (owner && order.canCustomerCancel())) {
            out.println("<form method='post' action='orders' onsubmit=\"return confirm('Cancel/delete this order?');\"><input type='hidden' name='action' value='deleteOrder'><input type='hidden' name='orderId' value='" + HtmlUtil.esc(order.getOrderId()) + "'><button class='btn btn-danger' type='submit'>" + (admin ? "Delete" : "Cancel Order") + "</button></form>");
        }
        out.println("</div></article>");
    }

    private void printOption(PrintWriter out, String value, String current) {
        out.println("<option value='" + HtmlUtil.esc(value) + "'" + (value.equals(current) ? " selected" : "") + ">" + HtmlUtil.esc(value) + "</option>");
    }

    private String statusClass(String status) {
        return "status-" + status.toLowerCase().replace(" ", "-");
    }

    private String stepClass(Order order, String step) {
        List<String> orderSteps = List.of(Order.STATUS_PLACED, Order.STATUS_PREPARING, Order.STATUS_OUT_FOR_DELIVERY, Order.STATUS_DELIVERED);
        int current = orderSteps.indexOf(order.getStatus());
        int target = orderSteps.indexOf(step);
        return current >= target && target >= 0 ? "done" : "";
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) {
            res.sendRedirect("component4.html");
            return;
        }
        String action = req.getParameter("action");
        String orderId = req.getParameter("orderId");

        boolean admin = SessionUtil.isAdmin(req);
        boolean worker = SessionUtil.isWorker(req);
        String user = SessionUtil.getLoggedUser(req);

        if ("updateStatus".equals(action) && (admin || worker)) {
            String status = req.getParameter("status");
            if (isValidStatus(status)) {
                OrderStore.updateStatus(orderId, status);
                res.sendRedirect("orders?success=updated");
            } else {
                res.sendRedirect("orders?error=notallowed");
            }
            return;
        }

        if ("deleteOrder".equals(action)) {
            boolean deleted = OrderStore.deleteOrder(orderId, user, admin);
            res.sendRedirect(deleted ? "orders?success=deleted" : "orders?error=notallowed");
            return;
        }
        res.sendRedirect("orders");
    }

    private boolean isValidStatus(String status) {
        return Order.STATUS_PLACED.equals(status) || Order.STATUS_PREPARING.equals(status)
                || Order.STATUS_OUT_FOR_DELIVERY.equals(status) || Order.STATUS_DELIVERED.equals(status);
    }
}
