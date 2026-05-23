package com.example.model;

public class MenuItem {

    private String restaurantName;
    private String itemName;
    private double price;
    private String description;
    private String imageUrl;

    public MenuItem(String restaurantName, String itemName, double price, String description, String imageUrl) {
        this.restaurantName = restaurantName;
        this.itemName = itemName;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getRestaurantName() { return restaurantName; }
    public String getItemName()       { return itemName; }
    public double getPrice()          { return price; }
    public String getDescription()    { return description; }
    public String getImageUrl()       { return imageUrl; }

    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public void setItemName(String itemName)             { this.itemName = itemName; }
    public void setPrice(double price)                   { this.price = price; }
    public void setDescription(String description)       { this.description = description; }
    public void setImageUrl(String imageUrl)             { this.imageUrl = imageUrl; }

    public String toFileString() {
        return restaurantName + "|" + itemName + "|" + price + "|" + description + "|" + imageUrl;
    }

    public static MenuItem fromFileString(String line) {
        String[] parts = line.split("\\|", 5);
        if (parts.length >= 4) {
            try {
                double price = Double.parseDouble(parts[2]);
                String imageUrl = parts.length == 5 ? parts[4] : "";
                return new MenuItem(parts[0], parts[1], price, parts[3], imageUrl);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
