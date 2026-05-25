package com.example.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String BASE_DIR = System.getProperty("user.home") + File.separator + "FoodDeliveryData" + File.separator;

    static {
        File dir = new File(BASE_DIR);
        if (!dir.exists()) dir.mkdirs();

        
    }

    public static synchronized void appendLine(String filename, String data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BASE_DIR + filename, true))) {
            writer.write(data);
            writer.newLine();
        }
    }

    public static synchronized List<String> readLines(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        File file = new File(BASE_DIR + filename);
        if (!file.exists()) return lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) lines.add(line);
            }
        }
        return lines;


        
    }

    public static synchronized void writeLines(String filename, List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BASE_DIR + filename, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public static String getBaseDir() {
        return BASE_DIR;
    }
}
