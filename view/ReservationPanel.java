package view;

import controller.ParkingSpotController;
import controller.ReservationController;
import model.ParkingSpot;
import model.Reservation;
import model.User;

import javax.swing.*;
import java.awt.*;

public class ReservationPanel extends JPanel {
    private JTextField nameField, spotIdField, timeField, durationField;
    private JTextArea output;
    private ReservationController reservationController;
    private ParkingSpotController spotController;

    public ReservationPanel(ParkingSpotController sharedSpotController, ReservationController sharedReservationController) {
        this.spotController = sharedSpotController;
        this.reservationController = sharedReservationController;

        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Reserve Spot"));

        form.add(new JLabel("User Name:"));
        nameField = new JTextField();
        form.add(nameField);

        form.add(new JLabel("Spot ID:"));
        spotIdField = new JTextField();
        form.add(spotIdField);

        form.add(new JLabel("Time Slot (e.g., 4-5 PM):"));
        timeField = new JTextField();
        form.add(timeField);

        form.add(new JLabel("Duration (in hours):"));
        durationField = new JTextField();
        form.add(durationField);

        JButton reserveBtn = new JButton("Reserve");
        JButton showBtn = new JButton("Show Reservations");
        JButton clearBtn = new JButton("Clear");

        form.add(reserveBtn);
        form.add(showBtn);
        form.add(clearBtn);

        output = new JTextArea(8, 40);
        output.setEditable(false);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);

        reserveBtn.addActionListener(e -> {
            String spotId = spotIdField.getText().trim();
            String timeSlot = timeField.getText().trim();
            String durationText = durationField.getText().trim();
            String name = nameField.getText().trim();

            if (spotId.isEmpty() || timeSlot.isEmpty() || durationText.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            int duration;
            try {
                duration = Integer.parseInt(durationText);
                if (duration <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid duration in hours.");
                return;
            }

            ParkingSpot spot = spotController.getSpotById(spotId);
            if (spot == null) {
                JOptionPane.showMessageDialog(this, "Invalid spot ID.");
                return;
            }

            if (!reservationController.isSpotAvailableForTime(spot, timeSlot)) {
                JOptionPane.showMessageDialog(this, "This spot is already reserved for that time slot.");
                return;
            }

            User user = new User(name, "N/A");
            boolean success = reservationController.createReservation(user, spot, timeSlot, duration);

            if (success) {
                JOptionPane.showMessageDialog(this, "Reservation made for " + duration + " hour(s)!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create reservation.");
            }
        });

        showBtn.addActionListener(e -> {
            output.setText("");
            for (Reservation r : reservationController.getAllReservations()) {
                output.append("User: " + r.getUser().getName()
                        + ", Spot: " + r.getSpot().getSpotId()
                        + ", Time: " + r.getTimeSlot()
                        + ", Duration: " + r.getDurationInHours() + " hour(s)\n");
            }
        });

        clearBtn.addActionListener(e -> {
            nameField.setText("");
            spotIdField.setText("");
            timeField.setText("");
            durationField.setText("");
            output.setText("");
        });
    }
}
