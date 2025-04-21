package com.smartparking.view;

import com.smartparking.model.Vehicle;
import com.smartparking.controller.VehicleController;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VehiclePanel extends JPanel {
    private JTextArea outputArea;
    private VehicleController vehicleController;

    public VehiclePanel() {
        vehicleController = new VehicleController();
        setLayout(new BorderLayout());

        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        
        // Add Vehicle Management buttons
        JButton addVehicleBtn = new JButton("Add Vehicle");
        JButton viewVehiclesBtn = new JButton("View Vehicles");
        JButton updateVehicleBtn = new JButton("Update Vehicle");
        JButton deleteVehicleBtn = new JButton("Delete Vehicle");

        buttonsPanel.add(addVehicleBtn);
        buttonsPanel.add(viewVehiclesBtn);
        buttonsPanel.add(updateVehicleBtn);
        buttonsPanel.add(deleteVehicleBtn);

        add(buttonsPanel, BorderLayout.NORTH);

        // Add output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        addVehicleBtn.addActionListener(e -> showAddVehicleDialog());
        viewVehiclesBtn.addActionListener(e -> viewAllVehicles());
        updateVehicleBtn.addActionListener(e -> showUpdateVehicleDialog());
        deleteVehicleBtn.addActionListener(e -> showDeleteVehicleDialog());
    }

    private void showAddVehicleDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Vehicle", true);
        dialog.setLayout(new GridLayout(4, 2, 5, 5));
        
        JTextField licensePlateField = new JTextField();
        JTextField makeField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField userIdField = new JTextField();
        
        dialog.add(new JLabel("License Plate:"));
        dialog.add(licensePlateField);
        dialog.add(new JLabel("Make:"));
        dialog.add(makeField);
        dialog.add(new JLabel("Model:"));
        dialog.add(modelField);
        dialog.add(new JLabel("User ID:"));
        dialog.add(userIdField);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String licensePlate = licensePlateField.getText();
            String make = makeField.getText();
            String model = modelField.getText();
            String userId = userIdField.getText();
            
            if (!licensePlate.isEmpty() && !make.isEmpty() && !model.isEmpty() && !userId.isEmpty()) {
                try {
                    Long userIdLong = Long.parseLong(userId);
                    try {
                        Vehicle createdVehicle = vehicleController.createVehicle(licensePlate, make, model, userIdLong);
                        JOptionPane.showMessageDialog(dialog, 
                            "Vehicle created successfully!\nLicense Plate: " + createdVehicle.getLicensePlate(),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(dialog, 
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Invalid User ID format. Please enter a valid number.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    "Please fill all fields",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(saveButton);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void viewAllVehicles() {
        List<Vehicle> vehicles = vehicleController.getAllVehicles();
        outputArea.setText("All Vehicles:\n");
        for (Vehicle vehicle : vehicles) {
            outputArea.append("ID: " + vehicle.getId() + 
                            ", License Plate: " + vehicle.getLicensePlate() + 
                            ", Make: " + vehicle.getMake() + 
                            ", Model: " + vehicle.getModel() + 
                            ", User ID: " + vehicle.getUserId() + "\n");
        }
    }

    private void showUpdateVehicleDialog() {
        String vehicleId = JOptionPane.showInputDialog(this, "Enter Vehicle ID to update:");
        if (vehicleId != null && !vehicleId.isEmpty()) {
            try {
                Long id = Long.parseLong(vehicleId);
                Vehicle vehicle = vehicleController.getVehicle(id);
                if (vehicle != null) {
                    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Vehicle", true);
                    dialog.setLayout(new GridLayout(4, 2, 5, 5));
                    
                    JTextField licensePlateField = new JTextField(vehicle.getLicensePlate());
                    JTextField makeField = new JTextField(vehicle.getMake());
                    JTextField modelField = new JTextField(vehicle.getModel());
                    JTextField userIdField = new JTextField(vehicle.getUserId().toString());
                    
                    dialog.add(new JLabel("License Plate:"));
                    dialog.add(licensePlateField);
                    dialog.add(new JLabel("Make:"));
                    dialog.add(makeField);
                    dialog.add(new JLabel("Model:"));
                    dialog.add(modelField);
                    dialog.add(new JLabel("User ID:"));
                    dialog.add(userIdField);
                    
                    JButton updateButton = new JButton("Update");
                    updateButton.addActionListener(e -> {
                        try {
                            Long userId = Long.parseLong(userIdField.getText());
                            vehicle.setLicensePlate(licensePlateField.getText());
                            vehicle.setMake(makeField.getText());
                            vehicle.setModel(modelField.getText());
                            vehicle.setUserId(userId);
                            try {
                                Vehicle updatedVehicle = vehicleController.updateVehicle(vehicle);
                                JOptionPane.showMessageDialog(dialog, 
                                    "Vehicle updated successfully!\nLicense Plate: " + updatedVehicle.getLicensePlate(),
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                                dialog.dispose();
                            } catch (IllegalArgumentException ex) {
                                JOptionPane.showMessageDialog(dialog, 
                                    ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(dialog, 
                                "Invalid User ID format. Please enter a valid number.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    
                    dialog.add(updateButton);
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Vehicle not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid ID format. Please enter a valid number.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDeleteVehicleDialog() {
        String vehicleId = JOptionPane.showInputDialog(this, "Enter Vehicle ID to delete:");
        if (vehicleId != null && !vehicleId.isEmpty()) {
            try {
                Long id = Long.parseLong(vehicleId);
                Vehicle vehicle = vehicleController.getVehicle(id);
                if (vehicle != null) {
                    int confirm = JOptionPane.showConfirmDialog(this, 
                        "Are you sure you want to delete vehicle: " + vehicle.getLicensePlate() + "?",
                        "Confirm Delete", 
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        vehicleController.deleteVehicle(id);
                        JOptionPane.showMessageDialog(this,
                            "Vehicle deleted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Vehicle not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid ID format. Please enter a valid number.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 