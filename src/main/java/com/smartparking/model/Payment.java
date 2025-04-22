package com.smartparking.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Payment {
    private static final AtomicLong idGenerator = new AtomicLong(1);
    private Long id;
    private Long reservationId;
    private Long userId;
    private double amount;
    private String paymentMethod;
    private String status;
    private LocalDateTime paymentDate;
    private String qrCodeData;
    
    // In-memory storage for payments
    private static final List<Payment> payments = new ArrayList<>();

    public Payment() {
        this.status = "PENDING";
        this.paymentDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    
    public String getQrCodeData() { return qrCodeData; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }

    // CRUD Operations
    public Payment create() {
        // Generate a new ID
        this.id = idGenerator.getAndIncrement();
        // Add to in-memory storage
        payments.add(this);
        return this;
    }

    public Payment read(Long id) {
        // Find payment by ID in in-memory storage
        return payments.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Payment update() {
        // Update payment in in-memory storage
        for (int i = 0; i < payments.size(); i++) {
            if (payments.get(i).getId().equals(this.id)) {
                payments.set(i, this);
                break;
            }
        }
        return this;
    }

    public void delete() {
        // Remove payment from in-memory storage
        payments.removeIf(p -> p.getId().equals(this.id));
    }

    public static List<Payment> readAll() {
        // Return all payments from in-memory storage
        return new ArrayList<>(payments);
    }
} 