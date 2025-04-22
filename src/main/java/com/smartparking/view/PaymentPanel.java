package com.smartparking.view;

import com.smartparking.controller.PaymentController;
import com.smartparking.controller.ParkingSpotController;
import com.smartparking.controller.ReservationController;
import com.smartparking.model.Payment;
import com.smartparking.model.Reservation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class PaymentPanel extends JPanel {
    private final PaymentController paymentController;
    private final ReservationController reservationController;
    private final ParkingSpotController spotController;
    private JTextArea outputArea;
    private JPanel qrCodePanel;
    private JLabel qrCodeLabel;
    private boolean qrCodeAvailable = false;
    private JTextField reservationIdField;

    public PaymentPanel() {
        paymentController = new PaymentController();
        reservationController = new ReservationController();
        spotController = new ParkingSpotController();
        setLayout(new BorderLayout());
        initializeComponents();
        checkQRCodeAvailability();
    }

    private void checkQRCodeAvailability() {
        try {
            Class.forName("com.google.zxing.qrcode.QRCodeWriter");
            qrCodeAvailable = true;
        } catch (ClassNotFoundException e) {
            qrCodeAvailable = false;
            outputArea.append("QR Code generation is not available. Please ensure ZXing dependencies are properly configured.\n\n");
        }
    }

    private void initializeComponents() {
        // Create buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton processPaymentButton = new JButton("Process Payment");
        JButton viewPaymentHistoryButton = new JButton("View Payment History");
        JButton clearButton = new JButton("Clear");

        processPaymentButton.addActionListener(e -> showProcessPaymentDialog());
        viewPaymentHistoryButton.addActionListener(e -> viewPaymentHistory());
        clearButton.addActionListener(e -> clearOutput());

        buttonPanel.add(processPaymentButton);
        buttonPanel.add(viewPaymentHistoryButton);
        buttonPanel.add(clearButton);

        // Create output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Create QR code panel
        qrCodePanel = new JPanel();
        qrCodeLabel = new JLabel();
        qrCodePanel.add(qrCodeLabel);

        // Add components to the panel
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(qrCodePanel, BorderLayout.SOUTH);
    }

    private void showProcessPaymentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Process Payment", true);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Reservation ID input
        JPanel reservationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        reservationIdField = new JTextField(10);
        reservationPanel.add(new JLabel("Reservation ID:"));
        reservationPanel.add(reservationIdField);
        mainPanel.add(reservationPanel, BorderLayout.NORTH);

        // Discount bill layout: code entry, summary button, and billing lines
        JPanel discountPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField discountField = new JTextField(10);
        JButton showSummaryBtn = new JButton("Show Summary");
        JLabel originalAmountLabel = new JLabel("$0.00");
        JLabel discountAmountLabel = new JLabel("$0.00");
        JLabel finalAmountLabel = new JLabel("$0.00");

        // Row 1: Discount code input
        discountPanel.add(new JLabel("Discount Code:"));
        discountPanel.add(discountField);
        // Row 2: Show summary button
        discountPanel.add(showSummaryBtn);
        discountPanel.add(new JLabel());
        // Row 3: Original amount
        discountPanel.add(new JLabel("Original Amount:"));
        discountPanel.add(originalAmountLabel);
        // Row 4: Discount amount
        discountPanel.add(new JLabel("Discount Amount:"));
        discountPanel.add(discountAmountLabel);
        // Row 5: Final amount
        discountPanel.add(new JLabel("Final Amount:"));
        discountPanel.add(finalAmountLabel);

        // Hold the discount rate and final discounted amount
        final double[] discountRateHolder = new double[]{0.0};
        final double[] finalAmountHolder = new double[]{0.0};
        
        // Update bill summary when reservation ID changes
        reservationIdField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateBill(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateBill(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateBill(); }
            private void updateBill() {
                try {
                    Long resId = Long.parseLong(reservationIdField.getText());
                    Reservation reservation = reservationController.getReservation(resId);
                    if (reservation != null) {
                        double origAmt = paymentController.calculateAmount(reservation);
                        originalAmountLabel.setText(String.format("$%.2f", origAmt));
                        discountAmountLabel.setText("$0.00");
                        finalAmountLabel.setText(String.format("$%.2f", origAmt));
                        discountRateHolder[0] = 0.0;
                        finalAmountHolder[0] = origAmt;
                    } else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    originalAmountLabel.setText("$0.00");
                    discountAmountLabel.setText("$0.00");
                    finalAmountLabel.setText("$0.00");
                    discountRateHolder[0] = 0.0;
                    finalAmountHolder[0] = 0.0;
                }
            }
        });

        // Show summary button logic: calculate and display bill in labels
        showSummaryBtn.addActionListener(e -> {
            try {
                Long resId = Long.parseLong(reservationIdField.getText());
                Reservation reservation = reservationController.getReservation(resId);
                if (reservation == null) {
                    JOptionPane.showMessageDialog(dialog, "Reservation not found");
                    return;
                }
                double originalAmt = paymentController.calculateAmount(reservation);
                String code = discountField.getText().trim();
                double rate = paymentController.getDiscountRate(code);
                if (rate <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Invalid discount code");
                    return;
                }
                double discountAmt = originalAmt * rate;
                double finalAmt = originalAmt - discountAmt;
                discountRateHolder[0] = rate;
                finalAmountHolder[0] = finalAmt;
                originalAmountLabel.setText(String.format("$%.2f", originalAmt));
                discountAmountLabel.setText(String.format("-$%.2f", discountAmt));
                finalAmountLabel.setText(String.format("$%.2f", finalAmt));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid reservation ID");
            }
        });

        // Combine discount and payment method into a vertical panel
        JPanel topCenterPanel = new JPanel(new BorderLayout());
        topCenterPanel.add(discountPanel, BorderLayout.NORTH);

        // Payment method selection
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] paymentMethods = {"CREDIT_CARD", "DEBIT_CARD", "CASH", "UPI"};
        JComboBox<String> paymentMethodCombo = new JComboBox<>(paymentMethods);
        methodPanel.add(new JLabel("Payment Method:"));
        methodPanel.add(paymentMethodCombo);
        topCenterPanel.add(methodPanel);
        
        mainPanel.add(topCenterPanel, BorderLayout.CENTER);

        // Payment details panel (will be updated based on selected method)
        JPanel detailsPanel = new JPanel(new BorderLayout());
        mainPanel.add(detailsPanel, BorderLayout.SOUTH);

        // Initialize with default payment panel (Credit Card) and attach DocumentListener
        JPanel defaultPanel = createCardPaymentPanel();
        detailsPanel.add(defaultPanel, BorderLayout.CENTER);

        // Process button panel
        JPanel processButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton processButton = new JButton("Process");
        processButtonPanel.add(processButton);
        
        // Update details panel when payment method changes
        paymentMethodCombo.addActionListener(e -> {
            String selectedMethod = (String) paymentMethodCombo.getSelectedItem();
            detailsPanel.removeAll();
            
            switch (selectedMethod) {
                case "CREDIT_CARD":
                case "DEBIT_CARD":
                    detailsPanel.add(createCardPaymentPanel(), BorderLayout.CENTER);
                    break;
                case "CASH":
                    detailsPanel.add(createCashPaymentPanel(), BorderLayout.CENTER);
                    break;
                case "UPI":
                    detailsPanel.add(createUPIPaymentPanel(), BorderLayout.CENTER);
                    break;
            }
            
            detailsPanel.revalidate();
            detailsPanel.repaint();
            dialog.pack();
        });

        // Pre-fill reservation ID with the last reservation if available, then trigger amount update
        Reservation lastReservation = reservationController.getLastReservation();
        if (lastReservation != null) {
            String lastId = String.valueOf(lastReservation.getId());
            reservationIdField.setText(lastId);
            // Trigger DocumentListener to update amount
            // Clear and reset text to fire listener
            reservationIdField.setText(lastId);
        }

        // Process button action (use final discounted amount if set)
        processButton.addActionListener(e -> {
            try {
                Long reservationId = Long.parseLong(reservationIdField.getText());
                String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
                
                // Check if reservation exists
                Reservation reservation = reservationController.getReservation(reservationId);
                if (reservation == null) {
                    JOptionPane.showMessageDialog(dialog, "Reservation not found");
                    return;
                }
                
                // Create payment with base amount
                Payment payment = paymentController.createPayment(reservation, paymentMethod);
                
                // Override amount only if a discount code was applied
                if (discountRateHolder[0] > 0) {
                    payment.setAmount(finalAmountHolder[0]);
                    outputArea.append(String.format("Applied %.0f%% discount. Final amount: $%.2f\n",
                        discountRateHolder[0] * 100, finalAmountHolder[0]));
                }
                
                // Process (complete) the payment
                processPayment(payment);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid reservation ID");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error processing payment: " + ex.getMessage());
            }
        });

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(processButtonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void processPayment(Payment payment) {
        // Process the payment
        payment = paymentController.processPayment(payment);
        
        if (payment != null && "COMPLETED".equals(payment.getStatus())) {
            // Get the reservation from the payment
            Reservation reservation = reservationController.getReservation(payment.getReservationId());
            if (reservation != null) {
                // Update reservation status to COMPLETED
                reservation.setStatus("COMPLETED");
                reservation.update(reservation);
                
                // Mark spot as available
                spotController.markSpotAsAvailable(reservation.getSpotId());
                
                // Update the output area with payment details
                outputArea.append("Payment processed successfully!\n");
                outputArea.append("Payment ID: " + payment.getId() + "\n");
                outputArea.append("Amount: $" + payment.getAmount() + "\n");
                outputArea.append("Status: " + payment.getStatus() + "\n");
                outputArea.append("Reservation Status: COMPLETED\n\n");
                
                JOptionPane.showMessageDialog(this, "Payment processed successfully!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to process payment.");
        }
    }

    // Add a helper to update a given amount label based on current reservation ID
    private void updateAmountLabel(JLabel amountLabel) {
        try {
            Long reservationId = Long.parseLong(reservationIdField.getText());
            Reservation reservation = reservationController.getReservation(reservationId);
            if (reservation != null) {
                double amount = paymentController.calculateAmount(reservation);
                amountLabel.setText(String.format("Amount: $%.2f", amount));
            } else {
                amountLabel.setText("Amount: $0.00");
            }
        } catch (NumberFormatException ex) {
            amountLabel.setText("Amount: $0.00");
        }
    }

    private JPanel createCardPaymentPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Card Details"));
        
        // Card input fields
        JTextField cardNumberField = new JTextField(16);
        JTextField expiryDateField = new JTextField(5);
        JTextField cvvField = new JTextField(3);
        
        panel.add(new JLabel("Card Number:"));
        panel.add(cardNumberField);
        panel.add(new JLabel("Expiry Date (MM/YY):"));
        panel.add(expiryDateField);
        panel.add(new JLabel("CVV:"));
        panel.add(cvvField);
        
        return panel;
    }

    private JPanel createCashPaymentPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Cash Payment"));
        
        JLabel totalAmountLabel = new JLabel("Total Amount: $0.00");
        JTextField amountGivenField = new JTextField(10);
        JLabel changeLabel = new JLabel("Change: $0.00");
        
        // Calculate total amount when reservation ID changes
        reservationIdField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            private void update() {
                try {
                    Long reservationId = Long.parseLong(reservationIdField.getText());
                    Reservation reservation = reservationController.getReservation(reservationId);
                    if (reservation != null) {
                        double amount = paymentController.calculateAmount(reservation);
                        totalAmountLabel.setText(String.format("Total Amount: $%.2f", amount));
                        if (!amountGivenField.getText().isEmpty()) {
                            try {
                                double amountGiven = Double.parseDouble(amountGivenField.getText());
                                double change = amountGiven - amount;
                                changeLabel.setText(String.format("Change: $%.2f", change));
                            } catch (NumberFormatException ex) {
                                // Ignore invalid input
                            }
                        }
                    } else {
                        totalAmountLabel.setText("Total Amount: $0.00");
                        changeLabel.setText("Change: $0.00");
                    }
                } catch (NumberFormatException ex) {
                    totalAmountLabel.setText("Total Amount: $0.00");
                    changeLabel.setText("Change: $0.00");
                }
            }
        });
        
        // Initialize total amount immediately
        updateAmountLabel(totalAmountLabel);
        
        // Calculate change when amount given changes
        amountGivenField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateChange(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateChange(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateChange(); }
            
            private void updateChange() {
                try {
                    Long reservationId = Long.parseLong(reservationIdField.getText());
                    Reservation reservation = reservationController.getReservation(reservationId);
                    if (reservation != null) {
                        double amount = paymentController.calculateAmount(reservation);
                        double amountGiven = Double.parseDouble(amountGivenField.getText());
                        double change = amountGiven - amount;
                        changeLabel.setText(String.format("Change: $%.2f", change));
                    }
                } catch (NumberFormatException ex) {
                    // Ignore invalid input
                }
            }
        });
        
        panel.add(new JLabel("Total Amount:"));
        panel.add(totalAmountLabel);
        panel.add(new JLabel("Amount Given:"));
        panel.add(amountGivenField);
        panel.add(new JLabel("Change:"));
        panel.add(changeLabel);
        
        return panel;
    }

    private JPanel createUPIPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("UPI Payment"));
        
        JPanel qrPanel = new JPanel(new BorderLayout());
        JLabel qrCodeLabel = new JLabel("UPI QR Code will be generated here");
        qrCodeLabel.setHorizontalAlignment(JLabel.CENTER);
        qrPanel.add(qrCodeLabel, BorderLayout.CENTER);
        
        JLabel amountLabel = new JLabel("Amount: $0.00");
        amountLabel.setHorizontalAlignment(JLabel.CENTER);
        qrPanel.add(amountLabel, BorderLayout.NORTH);
        
        // Update amount when reservation ID changes
        reservationIdField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateAmount(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateAmount(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateAmount(); }
            
            private void updateAmount() {
                updateAmountLabel(amountLabel);
            }
        });
        
        // Initialize UPI amount immediately
        updateAmountLabel(amountLabel);
        
        JButton generateButton = new JButton("Generate QR Code");
        generateButton.addActionListener(e -> {
            if (qrCodeAvailable) {
                try {
                    Long reservationId = Long.parseLong(reservationIdField.getText());
                    Reservation reservation = reservationController.getReservation(reservationId);
                    if (reservation != null) {
                        double amount = paymentController.calculateAmount(reservation);
                        // Generate a sample UPI QR code data with amount
                        String upiData = String.format("upi://pay?pa=smartparking@upi&pn=Smart%%20Parking&am=%.2f&cu=USD&tn=Parking%%20Payment", amount);
                        
                        // Generate QR code using ZXing
                        QRCodeWriter qrCodeWriter = new QRCodeWriter();
                        BitMatrix bitMatrix = qrCodeWriter.encode(upiData, BarcodeFormat.QR_CODE, 150, 150);
                        
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
                        
                        byte[] qrCodeBytes = outputStream.toByteArray();
                        ImageIcon qrCodeIcon = new ImageIcon(qrCodeBytes);
                        
                        qrCodeLabel.setIcon(qrCodeIcon);
                        qrCodeLabel.setText("");
                        
                        outputArea.append("QR Code generated successfully!\n\n");
                    }
                } catch (Exception ex) {
                    outputArea.append("Error generating QR code: " + ex.getMessage() + "\n\n");
                    qrCodeLabel.setText("Error generating QR code");
                }
            } else {
                qrCodeLabel.setText("QR Code generation is not available");
            }
        });
        
        qrPanel.add(generateButton, BorderLayout.SOUTH);
        panel.add(qrPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void viewPaymentHistory() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Payment History", true);
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField userIdField = new JTextField(10);
        JButton viewButton = new JButton("View History");

        inputPanel.add(new JLabel("User ID:"));
        inputPanel.add(userIdField);
        inputPanel.add(viewButton);

        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);

        viewButton.addActionListener(e -> {
            try {
                Long userId = Long.parseLong(userIdField.getText());
                List<Payment> payments = paymentController.getPaymentHistory(userId);
                
                historyArea.setText("");
                for (Payment payment : payments) {
                    historyArea.append("Payment ID: " + payment.getId() + "\n");
                    historyArea.append("Amount: $" + payment.getAmount() + "\n");
                    historyArea.append("Status: " + payment.getStatus() + "\n");
                    historyArea.append("Date: " + payment.getPaymentDate() + "\n\n");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid user ID");
            }
        });

        dialog.add(inputPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void clearOutput() {
        outputArea.setText("");
        qrCodeLabel.setIcon(null);
        qrCodeLabel.setText("");
    }
} 