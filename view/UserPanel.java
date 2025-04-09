package view;

import controller.UserController;
import model.User;

import javax.swing.*;
import java.awt.*;

public class UserPanel extends JPanel {
    private JTextField nameField, contactField;
    private JTextArea output;
    private UserController controller = new UserController();

    public UserPanel() {
        setLayout(new BorderLayout());

        // FORM PANEL WITH GridBagLayout
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Register User"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);  // padding

        // Name Label + Field
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        form.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(20);
        form.add(nameField, gbc);

        // Contact Label + Field
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        form.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        contactField = new JTextField(20);
        form.add(contactField, gbc);

        // Buttons: Register, Show Users, Clear
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton registerBtn = new JButton("Register");
        JButton showBtn = new JButton("Show Users");
        JButton clearBtn = new JButton("Clear");
        buttonPanel.add(registerBtn);
        buttonPanel.add(showBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        form.add(buttonPanel, gbc);

        // Output area
        output = new JTextArea(8, 40);
        output.setEditable(false);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);

        // Button Listeners
        registerBtn.addActionListener(e -> {
            controller.registerUser(nameField.getText(), contactField.getText());
            JOptionPane.showMessageDialog(this, "User Registered!");
        });

        showBtn.addActionListener(e -> {
            output.setText("");
            for (User u : controller.getAllUsers()) {
                output.append("Name: " + u.getName() + ", Contact: " + u.getContact() + "\n");
            }
        });

        clearBtn.addActionListener(e -> {
            nameField.setText("");
            contactField.setText("");
            output.setText("");
        });
    }
}
