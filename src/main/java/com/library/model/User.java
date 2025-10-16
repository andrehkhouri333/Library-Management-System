package com.library.model;

/**
 * Represents a user in the library system
 * @author Library Team
 * @version 1.0
 */
public class User {
    private String userId;
    private String name;
    private String email;

    /**
     * Constructor for creating a new user
     * @param userId the unique user ID
     * @param name the name of the user
     * @param email the email of the user
     */
    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return String.format("ID: %-10s | Name: %-20s | Email: %-25s", userId, name, email);
    }
}