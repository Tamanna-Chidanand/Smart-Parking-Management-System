package com.smartparking.view;

import com.smartparking.model.User;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserPanel extends JPanel {
    private JTextArea outputArea;

    public UserPanel() {
        setLayout(new BorderLayout());

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add User");
        JButton viewButton = new JButton("View Users");
        JButton updateButton = new JButton("Update User");
        JButton deleteButton = new JButton("Delete User");

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.NORTH);

        // Create output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        addButton.addActionListener(e -> showAddUserDialog());
        viewButton.addActionListener(e -> viewUsers());
        updateButton.addActionListener(e -> showUpdateUserDialog());
        deleteButton.addActionListener(e -> showDeleteUserDialog());
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add User", true);
        dialog.setLayout(new GridLayout(3, 2, 5, 5));

        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Contact:"));
        dialog.add(contactField);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();

            if (name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields");
                return;
            }

            try {
                // Check if contact number already exists
                List<User> existingUsers = User.readAll();
                boolean contactExists = existingUsers.stream()
                    .anyMatch(user -> user.getContact().equals(contact));
                
                if (contactExists) {
                    JOptionPane.showMessageDialog(dialog, 
                        "A user with this contact number already exists.\nPlease use a different contact number.");
                    return;
                }

                User user = new User(name, contact);
                User createdUser = User.create(user);
                if (createdUser != null) {
                    outputArea.append("User added successfully:\n");
                    outputArea.append("ID: " + createdUser.getId() + "\n");
                    outputArea.append("Name: " + createdUser.getName() + "\n");
                    outputArea.append("Contact: " + createdUser.getContact() + "\n\n");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add user");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding user: " + ex.getMessage());
            }
        });

        dialog.add(addButton);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void viewUsers() {
        List<User> users = User.readAll();
        outputArea.setText("All Users:\n");
        for (User user : users) {
            outputArea.append("ID: " + user.getId() + "\n");
            outputArea.append("Name: " + user.getName() + "\n");
            outputArea.append("Contact: " + user.getContact() + "\n\n");
        }
    }

    private void showUpdateUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update User", true);
        dialog.setLayout(new GridLayout(4, 2, 5, 5));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();

        dialog.add(new JLabel("User ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("New Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("New Contact:"));
        dialog.add(contactField);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            try {
                Long id = Long.parseLong(idField.getText().trim());
                String name = nameField.getText().trim();
                String contact = contactField.getText().trim();

                if (name.isEmpty() || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields");
                    return;
                }

                // Check if contact number already exists for a different user
                List<User> existingUsers = User.readAll();
                boolean contactExists = existingUsers.stream()
                    .anyMatch(user -> user.getContact().equals(contact) && !user.getId().equals(id));
                
                if (contactExists) {
                    JOptionPane.showMessageDialog(dialog, 
                        "A user with this contact number already exists.\nPlease use a different contact number.");
                    return;
                }

                User user = User.read(id);
                if (user != null) {
                    user.setName(name);
                    user.setContact(contact);
                    User updatedUser = User.update(user);
                    if (updatedUser != null) {
                        outputArea.append("User updated successfully:\n");
                        outputArea.append("ID: " + updatedUser.getId() + "\n");
                        outputArea.append("Name: " + updatedUser.getName() + "\n");
                        outputArea.append("Contact: " + updatedUser.getContact() + "\n\n");
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to update user");
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "User not found");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid ID format");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating user: " + ex.getMessage());
            }
        });

        dialog.add(updateButton);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showDeleteUserDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Enter user ID to delete:");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                Long id = Long.parseLong(idStr.trim());
                User user = User.read(id);
                if (user != null) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this user?\n" +
                        "Name: " + user.getName() + "\n" +
                        "Contact: " + user.getContact(),
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        User.delete(id);
                        outputArea.append("User deleted successfully\n");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User not found");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format");
            }
        }
    }
} 