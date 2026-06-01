package com.example.luxevistaresort;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String name;
    private String email;
    private final String password;
    private final String role;
    private String preferredRoomType;
    private String preferredService;

    public User(int id, String name, String email, String password, String role, String preferredRoomType, String preferredService) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.preferredRoomType = preferredRoomType;
        this.preferredService = preferredService;
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getPreferredRoomType() { return preferredRoomType; }
    public String getPreferredService() { return preferredService; }

    // --- Setters ---
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPreferredRoomType(String preferredRoomType) {
        this.preferredRoomType = preferredRoomType;
    }
    public void setPreferredService(String preferredService) {
        this.preferredService = preferredService;
    }
}
