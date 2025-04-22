package com.smartparking.view;

import com.smartparking.controller.ParkingSpotController;
import com.smartparking.controller.ReservationController;
import com.smartparking.model.Reservation;

import javax.swing.*;

public class MainFrame extends JFrame {

    private ParkingSpotController sharedSpotController;
    private ReservationController sharedReservationController;
    private JTabbedPane tabbedPane;

    public MainFrame() {
        setTitle("Smart Parking System");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize shared controllers
        sharedSpotController = new ParkingSpotController();
        sharedReservationController = new ReservationController();

        tabbedPane = new JTabbedPane();

        // Add tabs with shared controllers
        tabbedPane.addTab("Users", new UserPanel());
        tabbedPane.addTab("Vehicles", new VehiclePanel());
        tabbedPane.addTab("Parking Map", new ParkingMapPanel(sharedSpotController));
        tabbedPane.addTab("Parking", new ParkingPanel(sharedSpotController, sharedReservationController));
        tabbedPane.addTab("Reservations", new ReservationPanel(sharedSpotController, sharedReservationController));
        tabbedPane.addTab("Payment", new PaymentPanel()); // Create PaymentPanel directly

        // Add the tabbed pane to the frame
        add(tabbedPane);
        setVisible(true);
    }
} 