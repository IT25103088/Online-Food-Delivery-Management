package com.example.model;

/**
 * Represents a user's feedback/review for a restaurant.
 */
public class Feedback {

    private String username;
    private String restaurantName;
    private String review;
    private int rating; // 1 to 5

    public Feedback(String username, String restaurantName, String review, int rating) {
        this.username = username;
        this.restaurantName = restaurantName;
        this.review = review;
        this.rating = rating;
    }

    public String getUsername()       { return username; }
    public String getRestaurantName() { return restaurantName; }
    public String getReview()         { return review; }
    public int getRating()            { return rating; }

    /**
     * Format: username|restaurantName|rating|review
     */
    public String toFileString() {
        return username + "|" + restaurantName + "|" + rating + "|" + review;
    }

    public static Feedback fromFileString(String line) {
        // We split into max 4 parts so review text (which may contain |) stays intact
        String[] parts = line.split("\\|", 4);
        if (parts.length == 4) {
            try {
                int rating = Integer.parseInt(parts[2]);
                return new Feedback(parts[0], parts[1], parts[3], rating);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
