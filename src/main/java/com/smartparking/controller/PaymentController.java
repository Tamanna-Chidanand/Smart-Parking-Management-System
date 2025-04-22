package com.smartparking.controller;

import com.smartparking.model.Payment;
import com.smartparking.model.Reservation;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

public class PaymentController {
    private static final double HOURLY_RATE = 5.0; // $5 per hour

    // Discount codes and their corresponding discount rates
    private static final Map<String, Double> DISCOUNT_CODES = new HashMap<>();
    static {
        DISCOUNT_CODES.put("SAVE10", 0.10);
        DISCOUNT_CODES.put("SAVE20", 0.20);
        DISCOUNT_CODES.put("SAVE30", 0.30);
    }

    public Payment createPayment(Reservation reservation, String paymentMethod) {
        Payment payment = new Payment();
        payment.setReservationId(reservation.getId());
        payment.setUserId(reservation.getUserId());
        payment.setAmount(calculateAmount(reservation));
        payment.setPaymentMethod(paymentMethod);
        payment.setQrCodeData(generateQRCodeData(payment));
        
        return payment.create();
    }

    /**
     * Returns the discount rate (0.0 - 1.0) for a given discount code, or 0 if invalid.
     */
    public double getDiscountRate(String code) {
        if (code == null) {
            return 0.0;
        }
        Double rate = DISCOUNT_CODES.get(code.toUpperCase());
        return rate != null ? rate : 0.0;
    }

    public Payment processPayment(Payment payment) {
        // Simulate payment processing
        payment.setStatus("COMPLETED");
        return payment.update();
    }

    public double calculateAmount(Reservation reservation) {
        if (reservation == null) {
            System.out.println("Reservation is null");
            return 0.0;
        }
        
        if (reservation.getStartTime() == null) {
            System.out.println("Start time is null");
            return 0.0;
        }
        
        if (reservation.getEndTime() == null) {
            System.out.println("End time is null");
            return 0.0;
        }
        
        // Ensure end time is after start time
        LocalDateTime startTime = reservation.getStartTime();
        LocalDateTime endTime = reservation.getEndTime();
        
        System.out.println("Start time: " + startTime);
        System.out.println("End time: " + endTime);
        
        if (endTime.isBefore(startTime)) {
            System.out.println("End time is before start time, using current time");
            endTime = LocalDateTime.now();
        }
        
        // Calculate duration in hours, ensuring at least 1 hour
        long hours = Math.max(1, java.time.Duration.between(startTime, endTime).toHours());
        double amount = hours * HOURLY_RATE;
        
        System.out.println("Hours: " + hours);
        System.out.println("Amount: " + amount);
        
        return amount;
    }

    private String generateQRCodeData(Payment payment) {
        // Generate a unique payment reference
        String paymentRef = UUID.randomUUID().toString();
        return String.format(
            "Payment Reference: %s\nAmount: $%.2f\nDate: %s\nReservation ID: %d",
            paymentRef,
            payment.getAmount(),
            LocalDateTime.now(),
            payment.getReservationId()
        );
    }

    public Payment getPayment(Long paymentId) {
        Payment payment = new Payment();
        return payment.read(paymentId);
    }

    public List<Payment> getPaymentHistory(Long userId) {
        return Payment.readAll().stream()
            .filter(p -> p.getUserId().equals(userId))
            .collect(Collectors.toList());
    }
} 