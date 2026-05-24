package com.example.util;

import com.example.model.Order;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderStore {
    private static final String FILE = "orders.txt";

    public static List<Order> findAll() throws IOException {
        List<Order> orders = new ArrayList<>();
        for (String line : FileHandler.readLines(FILE)) {
            Order order = Order.fromFileString(line);
            if (order != null) orders.add(order);
        }
        return orders;
    }

    public static List<Order> findVisibleTo(String username, boolean admin, boolean worker) throws IOException {
        List<Order> visible = new ArrayList<>();
        for (Order order : findAll()) {
            if (admin || worker || order.getUsername().equals(username)) {
                visible.add(order);
            }
        }
        return visible;
    }

    public static void save(Order order) throws IOException {
        FileHandler.appendLine(FILE, order.toFileString());
    }

    public static boolean updateStatus(String orderId, String status) throws IOException {
        List<String> updated = new ArrayList<>();
        boolean changed = false;
        for (Order order : findAll()) {
            if (order.getOrderId().equals(orderId)) {
                order.setStatus(status);
                changed = true;
            }
            updated.add(order.toFileString());
        }
        FileHandler.writeLines(FILE, updated);
        return changed;
    }

    public static boolean deleteOrder(String orderId, String username, boolean admin) throws IOException {
        List<String> updated = new ArrayList<>();
        boolean deleted = false;
        for (Order order : findAll()) {
            boolean owner = order.getUsername().equals(username);
            if (order.getOrderId().equals(orderId) && (admin || (owner && order.canCustomerCancel()))) {
                deleted = true;
                continue;
            }
            updated.add(order.toFileString());
        }
        FileHandler.writeLines(FILE, updated);
        return deleted;
    }
}
