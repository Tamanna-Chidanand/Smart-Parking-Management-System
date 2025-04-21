package com.smartparking.view;

import com.smartparking.model.Reservation;
import com.smartparking.model.Payment;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class PaymentPanel extends JPanel {
    private final Reservation reservation;
    private JTextArea outputArea;
    private JLabel totalAmountLabel;
    private double hourlyRate = 5.0; // Default hourly rate

    public PaymentPanel(Reservation reservation) {
        this.reservation = reservation;
        
        setLayout(new BorderLayout());

        // Create top panel for payment details
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add reservation details
        gbc.gridx = 0; gbc.gridy = 0;
        detailsPanel.add(new JLabel("Reservation ID:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.valueOf(reservation.getId())), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        detailsPanel.add(new JLabel("Spot ID:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.valueOf(reservation.getSpotId())), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        detailsPanel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(reservation.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        detailsPanel.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(reservation.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), gbc);

        // Calculate duration and amount
        long hours = ChronoUnit.HOURS.between(reservation.getStartTime(), reservation.getEndTime());
        double totalAmount = hours * hourlyRate;

        gbc.gridx = 0; gbc.gridy = 4;
        detailsPanel.add(new JLabel("Duration (hours):"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.valueOf(hours)), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        detailsPanel.add(new JLabel("Hourly Rate:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel("$" + String.format("%.2f", hourlyRate)), gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        detailsPanel.add(new JLabel("Total Amount:"), gbc);
        gbc.gridx = 1;
        totalAmountLabel = new JLabel("$" + String.format("%.2f", totalAmount));
        totalAmountLabel.setFont(totalAmountLabel.getFont().deriveFont(Font.BOLD));
        detailsPanel.add(totalAmountLabel, gbc);

        add(detailsPanel, BorderLayout.NORTH);

        // Create payment form
        JPanel paymentForm = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Payment method selection
        gbc.gridx = 0; gbc.gridy = 0;
        paymentForm.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        String[] paymentMethods = {"Credit Card", "Debit Card", "Cash"};
        JComboBox<String> paymentMethodCombo = new JComboBox<>(paymentMethods);
        paymentForm.add(paymentMethodCombo, gbc);

        // Card number field (for card payments)
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel cardNumberLabel = new JLabel("Card Number:");
        paymentForm.add(cardNumberLabel, gbc);
        gbc.gridx = 1;
        JTextField cardNumberField = new JTextField(20);
        paymentForm.add(cardNumberField, gbc);

        // Expiry date field
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel expiryLabel = new JLabel("Expiry Date (MM/YY):");
        paymentForm.add(expiryLabel, gbc);
        gbc.gridx = 1;
        JTextField expiryField = new JTextField(5);
        paymentForm.add(expiryField, gbc);

        // CVV field
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel cvvLabel = new JLabel("CVV:");
        paymentForm.add(cvvLabel, gbc);
        gbc.gridx = 1;
        JTextField cvvField = new JTextField(4);
        paymentForm.add(cvvField, gbc);

        // Add payment form to center
        add(paymentForm, BorderLayout.CENTER);

        // Create bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton processPaymentBtn = new JButton("Process Payment");
        JButton cancelBtn = new JButton("Cancel");

        processPaymentBtn.addActionListener(e -> processPayment(
            (String) paymentMethodCombo.getSelectedItem(),
            cardNumberField.getText(),
            expiryField.getText(),
            cvvField.getText(),
            totalAmount
        ));

        cancelBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });

        buttonPanel.add(processPaymentBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add output area for messages
        outputArea = new JTextArea(3, 40);
        outputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        add(outputScrollPane, BorderLayout.SOUTH);

        // Show/hide card fields based on payment method
        paymentMethodCombo.addActionListener(e -> {
            boolean isCardPayment = paymentMethodCombo.getSelectedItem().toString().contains("Card");
            cardNumberLabel.setVisible(isCardPayment);
            cardNumberField.setVisible(isCardPayment);
            expiryLabel.setVisible(isCardPayment);
            expiryField.setVisible(isCardPayment);
            cvvLabel.setVisible(isCardPayment);
            cvvField.setVisible(isCardPayment);
            paymentForm.revalidate();
            paymentForm.repaint();
        });
    }

    private void processPayment(String paymentMethod, String cardNumber, String expiry, String cvv, double amount) {
        // Validate payment details
        if (paymentMethod.contains("Card")) {
            if (cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all card details");
                return;
            }
            if (!validateCardNumber(cardNumber)) {
                JOptionPane.showMessageDialog(this, "Invalid card number");
                return;
            }
            if (!validateExpiry(expiry)) {
                JOptionPane.showMessageDialog(this, "Invalid expiry date");
                return;
            }
            if (!validateCVV(cvv)) {
                JOptionPane.showMessageDialog(this, "Invalid CVV");
                return;
            }
        }

        // Create payment record
        Payment payment = new Payment();
        payment.setReservationId(reservation.getId());
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("Completed");
        payment.setTimestamp(LocalDateTime.now());

        // Save payment
        Payment savedPayment = Payment.create(payment);
        
        if (savedPayment != null) {
            outputArea.append("Payment processed successfully!\n");
            outputArea.append("Payment ID: " + savedPayment.getId() + "\n");
            outputArea.append("Amount: $" + String.format("%.2f", amount) + "\n");
            
            // Close the payment window after successful payment
            Timer timer = new Timer(2000, e -> {
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.dispose();
                }
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            outputArea.append("Payment processing failed. Please try again.\n");
        }
    }

    private boolean validateCardNumber(String cardNumber) {
        // Basic card number validation (Luhn algorithm)
        return cardNumber.matches("\\d{16}");
    }

    private boolean validateExpiry(String expiry) {
        // Basic expiry date validation (MM/YY format)
        return expiry.matches("(0[1-9]|1[0-2])/([0-9]{2})");
    }

    private boolean validateCVV(String cvv) {
        // Basic CVV validation (3-4 digits)
        return cvv.matches("\\d{3,4}");
    }
} 