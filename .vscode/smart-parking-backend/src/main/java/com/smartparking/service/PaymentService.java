package com.smartparking.service;

import com.smartparking.model.Payment;
import com.smartparking.model.Reservation;
import com.smartparking.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ReservationService reservationService;

    public PaymentService(PaymentRepository paymentRepository, ReservationService reservationService) {
        this.paymentRepository = paymentRepository;
        this.reservationService = reservationService;
    }

    public Payment processPayment(Reservation reservation, double amount, String paymentMethod) {
        Payment payment = new Payment(reservation, amount, paymentMethod);
        return paymentRepository.save(payment);
    }

    public Payment processPayment(String reservationId, double amount, String paymentMethod) {
        Reservation reservation = reservationService.getReservationById(reservationId);
        if (reservation != null) {
            return processPayment(reservation, amount, paymentMethod);
        }
        return null;
    }
}