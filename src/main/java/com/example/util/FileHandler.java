package com.example.util;

import java.io.*;
import java.util.ArrayList;


public class FileHandler {

    private static final String BASE_DIR = System.getProperty("user.home") + "/FoodDeliveryData/";

    static {
        File dir = new File(BASE_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // Append a line to a file
    public static void writeToFile(String filename, String data) {
        String filePath = BASE_DIR + filename;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    // Read all lines from a file
    public static List<String> readFromFile(String filename) {
        List<String> lines = new ArrayList<>();
        String filePath = BASE_DIR + filename;
        File file = new File(filePath);
        if (!file.exists()) return lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return lines;
    }

    // Overwrite entire file with a new list of lines
    public static void writeAllLines(String filename, List<String> lines) {
        String filePath = BASE_DIR + filename;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error overwriting file: " + e.getMessage());
        }
    }

    // Delete a record by matching a value in a specific column
    public static void deleteByColumn(String filename, int columnIndex, String value) {
        List<String> lines = readFromFile(filename);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length > columnIndex && parts[columnIndex].equalsIgnoreCase(value)) {
                continue; // skip this line = delete it
            }
            updated.add(line);
        }
        writeAllLines(filename, updated);
    }

    // Update a record - finds line where columnIndex matches value, replaces whole line
    public static void updateLine(String filename, int columnIndex, String matchValue, String newLine) {
        List<String> lines = readFromFile(filename);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length > columnIndex && parts[columnIndex].equalsIgnoreCase(matchValue)) {
                updated.add(newLine); // replace with new line
            } else {
                updated.add(line);
            }
        }
        writeAllLines(filename, updated);
    }

    // Check if a value exists in a column
    public static boolean valueExistsInColumn(String filename, int columnIndex, String value) {
        List<String> lines = readFromFile(filename);
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length > columnIndex && parts[columnIndex].equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    // Find a single line where columnIndex matches value
    public static String findLine(String filename, int columnIndex, String value) {
        List<String> lines = readFromFile(filename);
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length > columnIndex && parts[columnIndex].equalsIgnoreCase(value)) {
                return line;
            }
        }
        return null;
    }

    public static String getBaseDir() { return BASE_DIR; }
}
