package com.onlinefooddeliverymanagement.OnlineFoodDeliveryManagement.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileHelper {
    public static List<String> readLines(String filePath) throws IOException {
        File f = new File(filePath);
        if (!f.exists()) return new ArrayList<>();
        return Files.readAllLines(Paths.get(filePath));
    }
    public static void writeLines(String filePath, List<String> lines) throws IOException {
        Files.write(Paths.get(filePath), lines);
    }
    public static void appendLine(String filePath, String line) throws IOException {
        Files.write(Paths.get(filePath),
                (line + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
