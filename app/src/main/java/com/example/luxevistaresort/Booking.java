package com.example.luxevistaresort;

import java.io.Serializable;


public class Booking implements Serializable {


    public static final String EXTRA_IS_MODIFY_MODE = "IS_MODIFY_MODE";
    public static final String EXTRA_BOOKING_ID = "BOOKING_ID";
    public static final String EXTRA_ITEM_NAME = "ITEM_NAME";
    public static final String EXTRA_ITEM_IMAGE_URI = "ITEM_IMAGE_URI";
    public static final String EXTRA_ROOM_PRICE = "ROOM_PRICE";
    public static final String EXTRA_START_DATE = "START_DATE";
    public static final String EXTRA_END_DATE = "END_DATE";

    private int id;
    private String userEmail;
    private String itemName;
    private long startDate;
    private long endDate;
    private String itemType;
    private String status;
    private String imageUri;

    public Booking(int id, String userEmail, String itemName, long startDate, long endDate, String itemType, String status, String imageUri) {
        this.id = id;
        this.userEmail = userEmail;
        this.itemName = itemName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.itemType = itemType;
        this.status = status;
        this.imageUri = imageUri;
    }

    // --- Getters for all fields ---
    public int getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getItemName() { return itemName; }
    public long getStartDate() { return startDate; }
    public long getEndDate() { return endDate; }
    public String getItemType() { return itemType; }
    public String getStatus() { return status; }
    public String getImageUri() { return imageUri; }
}