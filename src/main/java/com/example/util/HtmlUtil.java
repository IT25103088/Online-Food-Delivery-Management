package com.example.util;

public class HtmlUtil {
    public static String esc(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public static String money(double value) {
        return String.format("LKR %.2f", value);
    }
}
