package view;

import controller.ParkingSpotController;
import controller.ReservationController;
import model.ParkingSpot;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ParkingPanel extends JPanel {
    private JTextArea output;
    private ParkingSpotController controller;
    private ReservationController reservationController;

    public ParkingPanel(ParkingSpotController sharedController, ReservationController sharedReservationController) {
        this.controller = sharedController;
        this.reservationController = sharedReservationController;

        setLayout(new BorderLayout());

        JPanel buttons = new JPanel();
        JButton showBtn = new JButton("Show Available Spots");
        JButton clearBtn = new JButton("Clear");
        buttons.add(showBtn);
        buttons.add(clearBtn);

        output = new JTextArea(12, 50);
        output.setEditable(false);

        JTextArea map = new JTextArea(8, 50);
        map.setEditable(false);
        map.setFont(new Font("Monospaced", Font.BOLD, 14));

        add(buttons, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
        add(new JScrollPane(map), BorderLayout.SOUTH);

        showBtn.addActionListener(e -> {
            output.setText("Available Spots:\n");
            List<ParkingSpot> allSpots = controller.getAllSpots();

            // List truly available spots
            for (ParkingSpot s : allSpots) {
                List<String> times = reservationController.getReservedTimeSlotsForSpot(s);
                if (times.isEmpty()) {
                    output.append("• " + s.getSpotId() + "\n");
                }
            }

            // Build parking map
            StringBuilder mapBuilder = new StringBuilder();
            for (char row = 'A'; row <= 'C'; row++) {
                for (int col = 1; col <= 5; col++) {
                    String id = row + String.valueOf(col);
                    ParkingSpot spot = controller.getSpotById(id);
                    if (spot != null) {
                        List<String> reservedTimes = reservationController.getReservedTimeSlotsForSpot(spot);
                        if (reservedTimes.isEmpty()) {
                            mapBuilder.append("[").append(id).append("] ");
                        } else {
                            mapBuilder.append("[XX] ");
                        }
                    }
                }
                mapBuilder.append("\n");
            }

            map.setText("Parking Map:\n" + mapBuilder.toString());

            // Tooltip on hover to show reserved times
            map.setToolTipText(null); // clear old tooltips
            map.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    int lineHeight = map.getFontMetrics(map.getFont()).getHeight();
                    int lineIndex = e.getY() / lineHeight;

                    if (lineIndex >= 1 && lineIndex <= 3) { // rows A–C
                        char row = (char) ('A' + (lineIndex - 1));
                        int x = e.getX();
                        int charWidth = map.getFontMetrics(map.getFont()).charWidth('X');
                        int columnIndex = x / (charWidth * 5); // estimate block width
                        int col = columnIndex + 1;

                        String spotId = row + String.valueOf(col);
                        ParkingSpot spot = controller.getSpotById(spotId);

                        if (spot != null) {
                            List<String> reservedTimes = reservationController.getReservedTimeSlotsForSpot(spot);
                            if (!reservedTimes.isEmpty()) {
                                map.setToolTipText("Reserved Times: " + String.join(", ", reservedTimes));
                            } else {
                                map.setToolTipText("Available");
                            }
                        }
                    }
                }
            });
        });

        clearBtn.addActionListener(e -> {
            output.setText("");
        });
    }
}
