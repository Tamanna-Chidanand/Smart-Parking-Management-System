package view;

import controller.PaymentController;
import model.Invoice;
import model.Reservation;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.awt.image.BufferedImage;

public class PaymentPanel extends JPanel {

    private JTextField amountField, discountCodeField, cardNumberField;
    private JComboBox<String> paymentMethodCombo;
    private JLabel cardNumberLabel, qrLabel;
    private JTextArea outputArea;
    private JButton payButton, clearButton;

    private PaymentController paymentController;
    private Reservation reservation;

    public PaymentPanel(Reservation reservation) {
        this.reservation = reservation;
        this.paymentController = new PaymentController();

        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Amount (auto-calculated)
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1;
        int duration = reservation.getDurationInHours();
        int amount = duration * 100;
        amountField = new JTextField(String.valueOf(amount), 15);
        amountField.setEditable(false); // not user-editable
        formPanel.add(amountField, gbc);

        // Discount Code
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Discount Code:"), gbc);

        gbc.gridx = 1;
        discountCodeField = new JTextField(15);
        formPanel.add(discountCodeField, gbc);

        // Payment Method
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Payment Method:"), gbc);

        gbc.gridx = 1;
        paymentMethodCombo = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "GPay"});
        formPanel.add(paymentMethodCombo, gbc);

        // Card Number
        gbc.gridx = 0;
        gbc.gridy = 3;
        cardNumberLabel = new JLabel("Card Number:");
        formPanel.add(cardNumberLabel, gbc);

        gbc.gridx = 1;
        cardNumberField = new JTextField(15);
        formPanel.add(cardNumberField, gbc);

        // QR Label (hidden initially)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        qrLabel = new JLabel();
        qrLabel.setVisible(false);
        formPanel.add(qrLabel, gbc);

        // Buttons
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        payButton = new JButton("Pay");
        formPanel.add(payButton, gbc);

        gbc.gridx = 1;
        clearButton = new JButton("Clear");
        formPanel.add(clearButton, gbc);

        // Output Area
        outputArea = new JTextArea(6, 30);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Listeners
        paymentMethodCombo.addActionListener(e -> {
            String selected = (String) paymentMethodCombo.getSelectedItem();
            boolean isCard = selected.equalsIgnoreCase("Credit Card") || selected.equalsIgnoreCase("Debit Card");
            cardNumberLabel.setVisible(isCard);
            cardNumberField.setVisible(isCard);

            qrLabel.setVisible(selected.equalsIgnoreCase("GPay"));
            if (selected.equalsIgnoreCase("GPay")) {
                qrLabel.setIcon(generateQRPlaceholder());
            }
        });

        payButton.addActionListener(e -> {
            new Thread(() -> processPayment()).start();
        });

        clearButton.addActionListener(e -> clearFields());

        // Initial state
        cardNumberLabel.setVisible(true);
        cardNumberField.setVisible(true);
    }

    private void processPayment() {
        String discountCode = discountCodeField.getText().trim();
        String method = (String) paymentMethodCombo.getSelectedItem();
        String cardNumber = cardNumberField.getText().trim();

        showOutput("Processing payment... Please wait...");

        Invoice invoice = paymentController.processPayment(reservation, discountCode, method, cardNumber);

        SwingUtilities.invokeLater(() -> {
            if (invoice.isSuccess()) {
                showOutput("Payment Successful \nOriginal Amount: ₹" + invoice.getOriginalAmount() +
                        "\nAmount After Discount: ₹" + invoice.getFinalAmount());
            } else {
                showOutput("Payment Unsuccessful \nPlease check card number or try GPay.");
            }
        });
    }

    private void showOutput(String message) {
        outputArea.setText(message);
    }

    private void clearFields() {
        discountCodeField.setText("");
        cardNumberField.setText("");
        outputArea.setText("");
        qrLabel.setIcon(null);
        qrLabel.setVisible(false);
    }

    private ImageIcon generateQRPlaceholder() {
        try {
            java.net.URL imageURL = getClass().getResource("/view/qr.png");
            if (imageURL == null) {
                showOutput("QR image not found.");
                return new ImageIcon();
            }

            Image originalImage = new ImageIcon(imageURL).getImage();
            Image scaledImage = originalImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);

        } catch (Exception e) {
            showOutput("Failed to load QR image.");
            e.printStackTrace();
            return new ImageIcon();
        }
    }
}
