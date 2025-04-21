package com.smartparking.view;

import com.smartparking.controller.ParkingSpotController;
import com.smartparking.controller.ReservationController;
import com.smartparking.model.Reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationPanel extends JPanel {
    private final ParkingSpotController spotController;
    private final ReservationController reservationController;
    private JTable reservationTable;
    private DefaultTableModel tableModel;

    public ReservationPanel(ParkingSpotController spotController, ReservationController reservationController) {
        this.spotController = spotController;
        this.reservationController = reservationController;
        
        setLayout(new BorderLayout());

        // Create top panel for controls
        JPanel topPanel = new JPanel(new FlowLayout());
        
        // Add buttons
        JButton viewActiveBtn = new JButton("View Active Reservations");
        JButton cancelBtn = new JButton("Cancel Reservation");
        JButton extendBtn = new JButton("Extend Reservation");
        
        topPanel.add(viewActiveBtn);
        topPanel.add(cancelBtn);
        topPanel.add(extendBtn);
        
        add(topPanel, BorderLayout.NORTH);

        // Create table model
        String[] columnNames = {"ID", "Spot ID", "User ID", "Vehicle ID", "Start Time", "End Time", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        reservationTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(reservationTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // Add action listeners
        viewActiveBtn.addActionListener(e -> viewActiveReservations());
        cancelBtn.addActionListener(e -> cancelReservation());
        extendBtn.addActionListener(e -> extendReservation());
    }

    private void viewActiveReservations() {
        List<Reservation> activeReservations = reservationController.getActiveReservations();
        updateTable(activeReservations);
        JOptionPane.showMessageDialog(this,
            "Found " + activeReservations.size() + " active reservations",
            "Active Reservations",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a reservation to cancel",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long reservationId = (Long) tableModel.getValueAt(selectedRow, 0);
        Reservation reservation = reservationController.getReservation(reservationId);
        
        if (reservation != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this reservation?\n\n" +
                "Spot ID: " + reservation.getSpotId() + "\n" +
                "Start Time: " + reservation.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n" +
                "End Time: " + reservation.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (reservationController.cancelReservation(reservationId)) {
                    JOptionPane.showMessageDialog(this,
                        "Reservation cancelled successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    viewActiveReservations(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to cancel reservation. It may already be cancelled or completed.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void extendReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a reservation to extend",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long reservationId = (Long) tableModel.getValueAt(selectedRow, 0);
        Reservation reservation = reservationController.getReservation(reservationId);
        
        if (reservation != null) {
            // Create extension dialog
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Extend Reservation", true);
            dialog.setLayout(new GridLayout(3, 2, 5, 5));
            
            SpinnerNumberModel hoursModel = new SpinnerNumberModel(1, 1, 24, 1);
            JSpinner hoursSpinner = new JSpinner(hoursModel);
            
            dialog.add(new JLabel("Extension Hours:"));
            dialog.add(hoursSpinner);
            
            JButton extendButton = new JButton("Extend");
            extendButton.addActionListener(e -> {
                int hours = (Integer) hoursSpinner.getValue();
                LocalDateTime newEndTime = reservation.getEndTime().plusHours(hours);
                
                if (spotController.isSpotAvailable(reservation.getSpotId(), newEndTime)) {
                    if (reservationController.extendReservation(reservationId, newEndTime)) {
                        JOptionPane.showMessageDialog(dialog,
                            "Reservation extended successfully by " + hours + " hours",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        viewActiveReservations(); // Refresh the table
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "Failed to extend reservation. It may already be cancelled or completed.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Cannot extend reservation: Spot is not available for the requested duration",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            dialog.add(extendButton);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
    }

    private void updateTable(List<Reservation> reservations) {
        tableModel.setRowCount(0);
        for (Reservation reservation : reservations) {
            tableModel.addRow(new Object[]{
                reservation.getId(),
                reservation.getSpotId(),
                reservation.getUserId(),
                reservation.getVehicleId(),
                reservation.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                reservation.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                reservation.getStatus()
            });
        }
    }
} 