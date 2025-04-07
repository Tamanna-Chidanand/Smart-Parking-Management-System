package com.smartparking.model;

import jakarta.persistence.*;

@Entity
public class Vehicle {
    @Id
    private String licensePlate;
    private String make;
    private String model;
    private String type;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors
    public Vehicle() {}
    
    public Vehicle(String licensePlate, String make, String model, String type) {
        this.licensePlate = licensePlate;
        this.make = make;
        this.model = model;
        this.type = type;
    }

    // Getters and Setters
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}