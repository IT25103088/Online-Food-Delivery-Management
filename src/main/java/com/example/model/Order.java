package com.example.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Order {
    public static final String STATUS_PLACED = "Placed";
    public static final String STATUS_PREPARING = "Preparing";
    public static final String STATUS_OUT_FOR_DELIVERY = "Out for Delivery";
    public static final String STATUS_DELIVERED = "Delivered";
    public static final String STATUS_CANCELLED = "Cancelled";

    private String orderId;
    private String username;
    private String restaurantName;
    private String itemName;
    private int quantity;
    private double totalPrice;
    private String deliveryAddress;
    private String status;
    private String createdAt;

    public Order(String orderId, String username, String restaurantName, String itemName,
                 int quantity, double totalPrice, String deliveryAddress, String status,
                 String createdAt) {
        this.orderId = orderId;
        this.username = username;
        this.restaurantName = restaurantName;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Order createNew(String username, String restaurantName, String itemName,
                                  int quantity, double totalPrice, String deliveryAddress) {
        String id = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return new Order(id, username, restaurantName, itemName, quantity, totalPrice,
                deliveryAddress, STATUS_PLACED, now);
    }

    public String getOrderId() { return orderId; }
    public String getUsername() { return username; }
    public String getRestaurantName() { return restaurantName; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return totalPrice; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }

    public void setStatus(String status) { this.status = status; }

    public boolean canCustomerCancel() {
        return STATUS_PLACED.equals(status);
    }

    public boolean isHistory() {
        return STATUS_DELIVERED.equals(status) || STATUS_CANCELLED.equals(status);
    }

    public String toFileString() {
        return safe(orderId) + "|" + safe(username) + "|" + safe(restaurantName) + "|" +
                safe(itemName) + "|" + quantity + "|" + totalPrice + "|" +
                safe(deliveryAddress) + "|" + safe(status) + "|" + safe(createdAt);
    }

    public static Order fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] p = line.split("\\|", -1);
        try {
            if (p.length == 9) {
                return new Order(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]),
                        Double.parseDouble(p[5]), p[6], p[7], p[8]);
            }
            if (p.length == 8) {
                return new Order(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]),
                        Double.parseDouble(p[5]), "", p[6], "");
            }
        } catch (NumberFormatException ignored) {
            return null;
        }
        return null;
    }

    private static String safe(String value) {
        if (value == null) return "";
        return value.replace("|", " ").replace("\n", " ").replace("\r", " ").trim();
    }
}
