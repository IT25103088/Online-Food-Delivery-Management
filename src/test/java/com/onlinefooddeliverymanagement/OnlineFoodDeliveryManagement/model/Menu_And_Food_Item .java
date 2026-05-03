package com.example.model;

/**
 * Represents a food item on a restaurant's menu.
 */
public class MenuItem {

    private String restaurantName; // which restaurant this item belongs to
    private String itemName;
    private double price;
    private String description;

    public MenuItem(String restaurantName, String itemName, double price, String description) {
        this.restaurantName = restaurantName;
        this.itemName = itemName;
        this.price = price;
        this.description = description;
    }

    public String getRestaurantName() { return restaurantName; }
    public String getItemName()       { return itemName; }
    public double getPrice()          { return price; }
    public String getDescription()    { return description; }

    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public void setItemName(String itemName)             { this.itemName = itemName; }
    public void setPrice(double price)                   { this.price = price; }
    public void setDescription(String description)       { this.description = description; }

    /**
     * Format: restaurantName|itemName|price|description
     */
    public String toFileString() {
        return restaurantName + "|" + itemName + "|" + price + "|" + description;
    }

    public static MenuItem fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length == 4) {
            try {
                double price = Double.parseDouble(parts[2]);
                return new MenuItem(parts[0], parts[1], price, parts[3]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
