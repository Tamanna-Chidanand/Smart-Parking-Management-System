package com.smartparking.view;

import com.smartparking.controller.ParkingSpotController;
import com.smartparking.controller.ReservationController;
import com.smartparking.model.Reservation;
import com.smartparking.model.ParkingSpot;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationPanel extends JPanel {
    private final ParkingSpotController spotController;
    private final ReservationController reservationController;
    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private JTabbedPane tabbedPane;

    public ReservationPanel(ParkingSpotController spotController, ReservationController reservationController) {
        this.spotController = spotController;
        this.reservationController = reservationController;
        
        setLayout(new BorderLayout());

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create active reservations tab
        JPanel activeTab = createActiveReservationsTab();
        tabbedPane.addTab("Active Reservations", activeTab);
        
        // Create past reservations tab
        JPanel pastTab = createPastReservationsTab();
        tabbedPane.addTab("Past Reservations", pastTab);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createActiveReservationsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create top panel for controls
        JPanel topPanel = new JPanel(new FlowLayout());
        
        // Add buttons
        JButton viewActiveBtn = new JButton("View Active Reservations");
        JButton cancelBtn = new JButton("Cancel Reservation");
        JButton extendBtn = new JButton("Extend Reservation");
        
        topPanel.add(viewActiveBtn);
        topPanel.add(cancelBtn);
        topPanel.add(extendBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);

        // Create table model
        String[] columnNames = {"ID", "Spot Number", "User ID", "Vehicle ID", "Start Time", "End Time", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        reservationTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add button listeners
        viewActiveBtn.addActionListener(e -> viewActiveReservations());
        cancelBtn.addActionListener(e -> cancelReservation());
        extendBtn.addActionListener(e -> extendReservation());
        
        return panel;
    }
    
    private JPanel createPastReservationsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create top panel for controls
        JPanel topPanel = new JPanel(new FlowLayout());
        
        // Add button
        JButton viewPastBtn = new JButton("View Past Reservations");
        topPanel.add(viewPastBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);

        // Create table model
        String[] columnNames = {"ID", "Spot Number", "User ID", "Vehicle ID", "Start Time", "End Time", "Status"};
        DefaultTableModel pastTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        JTable pastReservationTable = new JTable(pastTableModel);
        JScrollPane scrollPane = new JScrollPane(pastReservationTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add button listener
        viewPastBtn.addActionListener(e -> viewPastReservations(pastTableModel));
        
        return panel;
    }
    
    private void viewPastReservations(DefaultTableModel tableModel) {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all reservations
        List<Reservation> allReservations = reservationController.getAllReservations();
        
        // Filter for completed and cancelled reservations
        List<Reservation> pastReservations = allReservations.stream()
            .filter(reservation -> "COMPLETED".equals(reservation.getStatus()) || 
                                "CANCELLED".equals(reservation.getStatus()))
            .collect(Collectors.toList());
        
        // Sort by end time in descending order (most recent first)
        pastReservations.sort((r1, r2) -> r2.getEndTime().compareTo(r1.getEndTime()));
        
        // Update the table
        updateTableWithReservations(tableModel, pastReservations);
        
        // Show message if no past reservations found
        if (pastReservations.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No past reservations found.", 
                "Past Reservations", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewActiveReservations() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get active reservations
        List<Reservation> activeReservations = reservationController.getActiveReservations();
        
        // Update the table
        updateTableWithReservations(tableModel, activeReservations);
    }

    private void cancelReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to cancel");
            return;
        }

        Long reservationId = (Long) tableModel.getValueAt(selectedRow, 0);
        Reservation reservation = reservationController.getReservation(reservationId);
        
        if (reservation != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this reservation?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                reservation.setStatus("CANCELLED");
                reservation.update(reservation);
                viewActiveReservations();
                JOptionPane.showMessageDialog(this, "Reservation cancelled successfully");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Reservation not found");
        }
    }

    private void extendReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to extend");
            return;
        }

        Long reservationId = (Long) tableModel.getValueAt(selectedRow, 0);
        Reservation reservation = reservationController.getReservation(reservationId);
        
        if (reservation != null) {
            String hoursStr = JOptionPane.showInputDialog(
                this,
                "Enter number of hours to extend:",
                "Extend Reservation",
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (hoursStr != null && !hoursStr.trim().isEmpty()) {
                try {
                    int hours = Integer.parseInt(hoursStr);
                    LocalDateTime currentEndTime = reservation.getEndTime();
                    LocalDateTime newEndTime = currentEndTime.plusHours(hours);
                    
                    reservation.setEndTime(newEndTime);
                    reservation.update(reservation);
                    viewActiveReservations();
                    JOptionPane.showMessageDialog(this, "Reservation extended successfully");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number of hours");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Reservation not found");
        }
    }

    private void updateTableWithReservations(DefaultTableModel model, List<Reservation> reservations) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Reservation reservation : reservations) {
            // Get the parking spot using spotController
            ParkingSpot spot = spotController.getSpotById(reservation.getSpotId());
            String spotNumber = spot != null ? spot.getSpotNumber() : "Unknown";
            
            Object[] rowData = {
                reservation.getId(),
                spotNumber,
                reservation.getUserId(),
                reservation.getVehicleId(),
                reservation.getStartTime().format(formatter),
                reservation.getEndTime().format(formatter),
                reservation.getStatus()
            };
            
            model.addRow(rowData);
        }
    }
} 