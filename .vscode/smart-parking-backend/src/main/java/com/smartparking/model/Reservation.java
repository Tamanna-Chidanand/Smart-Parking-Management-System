package com.smartparking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reservationId;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "spot_id")
    private ParkingSpot parkingSpot;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    // Default constructor
    public Reservation() {
        this.reservationId = "RES-" + System.currentTimeMillis();
        this.status = "CONFIRMED";
    }

    // Parameterized constructor
    public Reservation(User user, ParkingSpot parkingSpot, LocalDateTime startTime, LocalDateTime endTime) {
        this();
        this.user = user;
        this.parkingSpot = parkingSpot;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public ParkingSpot getParkingSpot() { return parkingSpot; }
    public void setParkingSpot(ParkingSpot parkingSpot) { this.parkingSpot = parkingSpot; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}