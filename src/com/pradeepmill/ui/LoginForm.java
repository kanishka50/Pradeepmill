package com.pradeepmill.ui;

import com.pradeepmill.database.DatabaseConnection;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import com.pradeepmill.utils.ConfigManager;

public class LoginForm extends javax.swing.JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JButton changePasswordButton;

    public LoginForm() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login - Pradeep Rice Mill Management System");
        setSize(500, 400);  // Increased size more to ensure all buttons fit
        setResizable(false);
        
        // Main panel with background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("Pradeep Rice Mill", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Management System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);
        
        titlePanel.setLayout(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Login panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 248, 255));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 12));
        loginPanel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 12));
        loginPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 12));
        loginPanel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordField.addActionListener(this::loginButtonActionPerformed);
        loginPanel.add(passwordField, gbc);
        
        // Create all buttons first
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(this::loginButtonActionPerformed);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.addActionListener(this::cancelButtonActionPerformed);
        
        changePasswordButton = new JButton("Change Password");
        changePasswordButton.setPreferredSize(new Dimension(150, 35));
        changePasswordButton.setFont(new Font("Arial", Font.BOLD, 11));
        changePasswordButton.setBackground(new Color(34, 139, 34));
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.addActionListener(this::changePasswordActionPerformed);
        
        // First button panel - Login and Cancel
        JPanel firstButtonPanel = new JPanel(new FlowLayout());
        firstButtonPanel.setBackground(new Color(240, 248, 255));
        firstButtonPanel.add(loginButton);
        firstButtonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        loginPanel.add(firstButtonPanel, gbc);
        
        // Second button panel - Change Password (on separate row)
        JPanel secondButtonPanel = new JPanel(new FlowLayout());
        secondButtonPanel.setBackground(new Color(240, 248, 255));
        secondButtonPanel.add(changePasswordButton);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(secondButtonPanel, gbc);
        
        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(240, 248, 255));
        
        // Get current username safely
        String currentUsername = "admin"; // default fallback
        try {
            currentUsername = ConfigManager.getUsername();
        } catch (Exception e) {
            System.out.println("ConfigManager not available yet, using default username");
        }
        
        JLabel infoLabel = new JLabel("<html><center>Current Username: " + 
            currentUsername + "<br>Click 'Change Password' to update credentials</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        infoLabel.setForeground(Color.GRAY);
        infoPanel.add(infoLabel);
        
        // Add panels to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Set focus
        usernameField.requestFocus();
        
        // Debug - print button count
        System.out.println("Buttons created: Login, Cancel, Change Password");
    }

    private void loginButtonActionPerformed(ActionEvent evt) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password.",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Try ConfigManager first, fallback to hardcoded if not available
        boolean authenticated = false;
        try {
            authenticated = ConfigManager.authenticate(username, password);
        } catch (Exception e) {
            System.out.println("ConfigManager not available, using hardcoded authentication");
            authenticated = "admin".equals(username) && "admin123".equals(password);
        }
        
        if (authenticated) {
            if (DatabaseConnection.testConnection()) {
                this.dispose();
                new MainDashboard().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Could not connect to database. Please check your MySQL server.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password!",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            usernameField.requestFocus();
        }
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    private void changePasswordActionPerformed(ActionEvent evt) {
        try {
            new ChangePasswordDialog(this).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Change Password feature is not available yet.\nPlease create the ConfigManager and ChangePasswordDialog classes first.",
                "Feature Not Available",
                JOptionPane.INFORMATION_MESSAGE);
            System.out.println("ChangePasswordDialog not available: " + e.getMessage());
        }
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}