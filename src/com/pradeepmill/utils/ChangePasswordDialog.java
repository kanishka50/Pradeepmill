package com.pradeepmill.ui;

import com.pradeepmill.utils.ConfigManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class ChangePasswordDialog extends JDialog {
    
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton changeButton;
    private JButton cancelButton;
    
    public ChangePasswordDialog(Frame parent) {
        super(parent, "Change Password", true);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 280);
        setResizable(false);
        
        // Main panel with background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Change Password", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Current Password
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel currentPasswordLabel = new JLabel("Current Password:");
        currentPasswordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(currentPasswordLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        currentPasswordField = new JPasswordField(15);
        currentPasswordField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(currentPasswordField, gbc);
        
        // New Password
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(newPasswordLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        newPasswordField = new JPasswordField(15);
        newPasswordField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(newPasswordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(confirmPasswordLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(confirmPasswordField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        changeButton = new JButton("Change Password");
        changeButton.setPreferredSize(new Dimension(140, 35));
        changeButton.setFont(new Font("Arial", Font.BOLD, 12));
        changeButton.setBackground(new Color(70, 130, 180));
        changeButton.setForeground(Color.WHITE);
        changeButton.addActionListener(this::changePasswordActionPerformed);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(80, 35));
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.addActionListener(this::cancelActionPerformed);
        
        buttonPanel.add(changeButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(buttonPanel, gbc);
        
        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(240, 248, 255));
        JLabel infoLabel = new JLabel("<html><center>Password should be at least 4 characters long.<br>Keep your password secure and memorable.</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        infoLabel.setForeground(Color.GRAY);
        infoPanel.add(infoLabel);
        
        // Add panels to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Set focus
        currentPasswordField.requestFocus();
    }
    
    private void changePasswordActionPerformed(ActionEvent evt) {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validation
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all password fields.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPassword.length() < 4) {
            JOptionPane.showMessageDialog(this,
                "New password must be at least 4 characters long.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            newPasswordField.requestFocus();
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "New password and confirm password do not match.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return;
        }
        
        if (currentPassword.equals(newPassword)) {
            JOptionPane.showMessageDialog(this,
                "New password must be different from current password.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            newPasswordField.requestFocus();
            return;
        }
        
        // Attempt to change password
        boolean success = ConfigManager.changePassword(currentPassword, newPassword);
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Password changed successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Current password is incorrect. Please try again.",
                "Authentication Error",
                JOptionPane.ERROR_MESSAGE);
            currentPasswordField.setText("");
            currentPasswordField.requestFocus();
        }
    }
    
    private void cancelActionPerformed(ActionEvent evt) {
        dispose();
    }
}