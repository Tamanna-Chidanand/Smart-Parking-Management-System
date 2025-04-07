package com.smartparking.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class User {
    @Id
    private String userId;
    private String name;
    private String email;
    private String contact;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Vehicle> vehicles;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Reservation> parkingHistory;

    // Constructors
    public User() {}
    
    public User(String userId, String name, String email, String contact) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.contact = contact;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public List<Vehicle> getVehicles() { return vehicles; }
    public void setVehicles(List<Vehicle> vehicles) { this.vehicles = vehicles; }
    public List<Reservation> getParkingHistory() { return parkingHistory; }
    public void setParkingHistory(List<Reservation> parkingHistory) { this.parkingHistory = parkingHistory; }
}