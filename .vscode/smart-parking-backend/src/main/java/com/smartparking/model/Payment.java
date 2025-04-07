package com.smartparking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentId;
    private double amount;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private String status;
    
    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    // Default constructor
    public Payment() {
        this.paymentDate = LocalDateTime.now();
        this.status = "COMPLETED";
        this.paymentId = "PAY-" + System.currentTimeMillis();
    }

    // Parameterized constructor
    public Payment(Reservation reservation, double amount, String paymentMethod) {
        this();
        this.reservation = reservation;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
}