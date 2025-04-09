package controller;

import model.Invoice;
import model.Payment;
import model.Reservation;

public class PaymentController {

    public Invoice processPayment(Reservation reservation, String discountCode, String paymentMethod, String cardNumber) {
        int duration = reservation.getDurationInHours();
        double amount = duration * 100;

        Payment payment = new Payment(amount);

        // Apply discount
        if (discountCode != null && discountCode.equalsIgnoreCase("discount20")) {
            payment.applyDiscount(20);
        }

        // Simulate processing delay
        boolean isSuccess = false;
        try {
            Thread.sleep(10000); // simulate 10-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (paymentMethod.equalsIgnoreCase("GPay")) {
            isSuccess = true;
        } else if ((paymentMethod.equalsIgnoreCase("Credit Card") || paymentMethod.equalsIgnoreCase("Debit Card"))
                && cardNumber != null && cardNumber.matches("\\d{13}")) {
            isSuccess = true;
        }

        if (isSuccess) {
            payment.markSuccess();
        } else {
            payment.markFailure();
        }

        return new Invoice(payment.getOriginalAmount(), payment.getDiscountedAmount(), payment.isSuccess());
    }
}
