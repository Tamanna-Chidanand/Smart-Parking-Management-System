package com.smartparking.controller;

import com.smartparking.model.Payment;
import com.smartparking.model.Reservation;
import com.smartparking.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment processPayment(@RequestParam String reservationId,
                                @RequestParam double amount,
                                @RequestParam String paymentMethod) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationId);
        return paymentService.processPayment(reservation, amount, paymentMethod);
    }
}