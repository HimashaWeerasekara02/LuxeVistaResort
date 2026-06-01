package com.example.luxevistaresort;

import java.io.Serializable;

public class Room implements Serializable {
    private int id;
    private String name;
    private String description;
    private int price;
    private String amenities;
    private int capacity;
    private String imageUri;

    public Room(int id, String name, String description, int price, String amenities, int capacity, String imageUri) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.amenities = amenities;
        this.capacity = capacity;
        this.imageUri = imageUri;
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public String getAmenities() { return amenities; }
    public int getCapacity() { return capacity; }
    public String getImageUri() { return imageUri; }
}