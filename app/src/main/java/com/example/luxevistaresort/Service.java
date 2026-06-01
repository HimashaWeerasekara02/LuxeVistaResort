package com.example.luxevistaresort;

import java.io.Serializable;

public class Service implements Serializable {
    private int id;
    private String name;
    private String description;
    private int price;
    private int capacity;
    private String imageUri;

    public Service(int id, String name, String description, int price, int capacity, String imageUri) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.capacity = capacity;
        this.imageUri = imageUri;
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public int getCapacity() { return capacity; }
    public String getImageUri() { return imageUri; }
}