package view;

import controller.ParkingSpotController;
import controller.ReservationController;
import model.Reservation;

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
        tabbedPane.addTab("Parking", new ParkingPanel(sharedSpotController, sharedReservationController)); // ✅ updated
        tabbedPane.addTab("Reservations", new ReservationPanel(sharedSpotController, sharedReservationController));
        tabbedPane.addTab("Payment", new JPanel()); // Placeholder for dynamic panel

        // Add tab change listener to dynamically load PaymentPanel
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            String selectedTitle = tabbedPane.getTitleAt(selectedIndex);

            if (selectedTitle.equals("Payment")) {
                Reservation latestReservation = sharedReservationController.getLastReservation();
                if (latestReservation != null) {
                    PaymentPanel paymentPanel = new PaymentPanel(latestReservation);
                    tabbedPane.setComponentAt(selectedIndex, paymentPanel);
                } else {
                    JPanel emptyPanel = new JPanel();
                    emptyPanel.add(new JLabel("No reservation found. Please make a reservation first."));
                    tabbedPane.setComponentAt(selectedIndex, emptyPanel);
                }
            }
        });

        add(tabbedPane);
        setVisible(true);
    }
}
