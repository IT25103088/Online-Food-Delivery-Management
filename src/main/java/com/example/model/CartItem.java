package com.example.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String restaurantName;
    private String itemName;
    private double price;
    private int quantity;
    private String imageUrl;

    public CartItem(String restaurantName, String itemName, double price, int quantity, String imageUrl) {
        this.restaurantName = restaurantName;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;

    }

    public String getRestaurantName() { return restaurantName; }
    public String getItemName() { return itemName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getSubtotal() { return price * quantity; }

}
