package view;

import controller.VehicleController;
import model.Vehicle;

import javax.swing.*;
import java.awt.*;

public class VehiclePanel extends JPanel {
    private JTextField plateField, modelField;
    private JTextArea output;
    private VehicleController controller = new VehicleController();

    public VehiclePanel() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Register Vehicle"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        // Plate
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        form.add(new JLabel("License Plate:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        plateField = new JTextField(20);
        form.add(plateField, gbc);

        // Model
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Model:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        modelField = new JTextField(20);
        form.add(modelField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton registerBtn = new JButton("Register");
        JButton showBtn = new JButton("Show Vehicles");
        JButton clearBtn = new JButton("Clear");
        buttonPanel.add(registerBtn);
        buttonPanel.add(showBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        form.add(buttonPanel, gbc);

        output = new JTextArea(8, 40);
        output.setEditable(false);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);

        registerBtn.addActionListener(e -> {
            controller.registerVehicle(plateField.getText(), modelField.getText());
            JOptionPane.showMessageDialog(this, "Vehicle Registered!");
        });

        showBtn.addActionListener(e -> {
            output.setText("");
            for (Vehicle v : controller.getAllVehicles()) {
                output.append("Plate: " + v.getLicensePlate() + ", Model: " + v.getModel() + "\n");
            }
        });

        clearBtn.addActionListener(e -> {
            plateField.setText("");
            modelField.setText("");
            output.setText("");
        });
    }
}
