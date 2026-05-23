package com.example.util;

/**
 * Server-side input validation utility.
 * Called from servlets before saving any data.
 */
public class ValidationUtil {

    // Pipe character breaks our file format — must be blocked
    public static boolean containsPipe(String value) {
        return value != null && value.contains("|");
    }

    // Basic email format check — must have @ and a dot after it
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) return false;                    // must have something before @
        int dotIndex = email.lastIndexOf('.');
        return dotIndex > atIndex + 1                      // dot must be after @
            && dotIndex < email.length() - 1;              // something must be after dot
    }

    // Price must be a positive number
    public static boolean isValidPrice(String priceStr) {
        try {
            double price = Double.parseDouble(priceStr);
            return price > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Quantity must be a positive integer
    public static boolean isValidQuantity(String qtyStr) {
        try {
            int qty = Integer.parseInt(qtyStr);
            return qty > 0 && qty <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Remove any HTML tags from input (basic XSS prevention)
    public static String sanitize(String input) {
        if (input == null) return "";
        return input.replaceAll("<[^>]*>", "").trim();
    }
}
