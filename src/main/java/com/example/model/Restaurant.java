package com.example.model;

/**
 * Represents a Restaurant in the system.
 */
public class Restaurant {

    private String name;
    private String location;
    private String cuisine;

    public Restaurant(String name, String location, String cuisine) {
        this.name = name;
        this.location = location;
        this.cuisine = cuisine;
    }

    public String getName()     { return name; }
    public String getLocation() { return location; }
    public String getCuisine()  { return cuisine; }

    public void setName(String name)         { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setCuisine(String cuisine)   { this.cuisine = cuisine; }

    /**
     * Format: name|location|cuisine
     */
    public String toFileString() {
        return name + "|" + location + "|" + cuisine;
    }

    public static Restaurant fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length == 3) {
            return new Restaurant(parts[0], parts[1], parts[2]);
        }
        return null;
    }
}
