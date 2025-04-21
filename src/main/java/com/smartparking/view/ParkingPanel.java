package com.smartparking.view;

import com.smartparking.controller.ParkingSpotController;
import com.smartparking.controller.ReservationController;
import com.smartparking.model.ParkingSpot;
import com.smartparking.model.Reservation;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ParkingPanel extends JPanel {
    private final ParkingSpotController spotController;
    private final ReservationController reservationController;
    private JTextArea outputArea;
    private JComboBox<String> spotTypeComboBox;
    private JSpinner durationSpinner;

    public ParkingPanel(ParkingSpotController spotController, ReservationController reservationController) {
        this.spotController = spotController;
        this.reservationController = reservationController;
        
        setLayout(new BorderLayout());

        // Create top panel for controls
        JPanel topPanel = new JPanel(new FlowLayout());
        
        // Add spot type selection
        spotTypeComboBox = new JComboBox<>(new String[]{"Standard", "Handicapped", "Electric"});
        topPanel.add(new JLabel("Spot Type:"));
        topPanel.add(spotTypeComboBox);
        
        // Add duration selection
        SpinnerNumberModel durationModel = new SpinnerNumberModel(1, 1, 24, 1);
        durationSpinner = new JSpinner(durationModel);
        topPanel.add(new JLabel("Duration (hours):"));
        topPanel.add(durationSpinner);
        
        // Add buttons
        JButton findSpotBtn = new JButton("Find Available Spot");
        JButton viewSpotsBtn = new JButton("View All Spots");
        JButton viewReservationsBtn = new JButton("View Reservations");
        
        topPanel.add(findSpotBtn);
        topPanel.add(viewSpotsBtn);
        topPanel.add(viewReservationsBtn);
        
        add(topPanel, BorderLayout.NORTH);

        // Add output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        findSpotBtn.addActionListener(e -> findAvailableSpot());
        viewSpotsBtn.addActionListener(e -> viewAllSpots());
        viewReservationsBtn.addActionListener(e -> viewReservations());
    }

    private void findAvailableSpot() {
        String spotType = (String) spotTypeComboBox.getSelectedItem();
        int duration = (Integer) durationSpinner.getValue();
        
        ParkingSpot availableSpot = spotController.findAvailableSpot(spotType);
        
        if (availableSpot != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Available spot found: " + availableSpot.getSpotNumber() + 
                "\nType: " + availableSpot.getType() +
                "\nDuration: " + duration + " hours" +
                "\nWould you like to make a reservation?",
                "Spot Available", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                showReservationDialog(availableSpot, duration);
            }
        } else {
            outputArea.append("No available spots of type " + spotType + " found.\n");
        }
    }

    private void showReservationDialog(ParkingSpot spot, int duration) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Make Reservation", true);
        dialog.setLayout(new GridLayout(4, 2, 5, 5));
        
        JTextField userIdField = new JTextField();
        JTextField vehicleIdField = new JTextField();
        
        dialog.add(new JLabel("User ID:"));
        dialog.add(userIdField);
        dialog.add(new JLabel("Vehicle ID:"));
        dialog.add(vehicleIdField);
        
        JButton reserveButton = new JButton("Reserve");
        reserveButton.addActionListener(e -> {
            try {
                Long userId = Long.parseLong(userIdField.getText());
                Long vehicleId = Long.parseLong(vehicleIdField.getText());
                
                LocalDateTime startTime = LocalDateTime.now();
                LocalDateTime endTime = startTime.plusHours(duration);
                
                try {
                    Reservation createdReservation = reservationController.createReservation(spot.getId(), userId, vehicleId, startTime, endTime);
                    
                    if (createdReservation != null) {
                        JOptionPane.showMessageDialog(dialog,
                            "Reservation created successfully!\n" +
                            "Spot: " + spot.getSpotNumber() + "\n" +
                            "Start: " + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n" +
                            "End: " + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "Failed to create reservation. Spot might not be available.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        ex.getMessage(),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Invalid ID format. Please enter valid numbers for User ID and Vehicle ID.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(reserveButton);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void viewAllSpots() {
        List<ParkingSpot> spots = spotController.getAllSpots();
        outputArea.setText("All Parking Spots:\n");
        for (ParkingSpot spot : spots) {
            outputArea.append("Spot #" + spot.getSpotNumber() + 
                            ", Type: " + spot.getType() + 
                            ", Status: " + (spot.isAvailable() ? "Available" : "Occupied") + "\n");
        }
    }

    private void viewReservations() {
        List<Reservation> reservations = reservationController.getAllReservations();
        outputArea.setText("All Reservations:\n");
        for (Reservation reservation : reservations) {
            outputArea.append("Reservation ID: " + reservation.getId() +
                            "\nSpot ID: " + reservation.getSpotId() +
                            "\nUser ID: " + reservation.getUserId() +
                            "\nVehicle ID: " + reservation.getVehicleId() +
                            "\nStart: " + reservation.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) +
                            "\nEnd: " + reservation.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) +
                            "\nStatus: " + reservation.getStatus() + "\n\n");
        }
    }
} 