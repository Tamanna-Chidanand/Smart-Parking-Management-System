package com.smartparking.model;

import jakarta.persistence.*;

@Entity
public class ParkingSpot {
    @Id
    private String spotId;
    private String location;
    private boolean isAvailable;
    private String type; // regular, handicapped

    // Constructors
    public ParkingSpot() {}
    
    public ParkingSpot(String spotId, String location, boolean isAvailable, String type) {
        this.spotId = spotId;
        this.location = location;
        this.isAvailable = isAvailable;
        this.type = type;
    }

    // Getters and Setters
    public String getSpotId() { return spotId; }
    public void setSpotId(String spotId) { this.spotId = spotId; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}