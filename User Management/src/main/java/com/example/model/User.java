package com.example.model;

public class User {
    private String username;
    private String email;
    private String password;
    private String role; // "admin", "worker", "user"

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }

    public boolean isAdmin()  { return "admin".equalsIgnoreCase(role); }
    public boolean isWorker() { return "worker".equalsIgnoreCase(role); }
    public boolean isUser()   { return "user".equalsIgnoreCase(role); }

    public String toFileString() { return username+"|"+email+"|"+password+"|"+role; }

    public static User fromFileString(String line) {
        String[] p = line.split("\\|");
        if (p.length == 4) return new User(p[0], p[1], p[2], p[3]);
        if (p.length == 3) return new User(p[0], p[1], p[2], "user");
        return null;
    }
}
