package com.pradeepmill.main;

import com.pradeepmill.ui.LoginForm;
import com.pradeepmill.ui.MainDashboard;
import com.pradeepmill.database.DatabaseConnection;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JOptionPane;

public class MainApplication {
    
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            System.out.println("Could not set look and feel: " + e.getMessage());
        }
        
        // Test database connection on startup
        SwingUtilities.invokeLater(() -> {
            System.out.println("Starting Pradeep Rice Mill Management System...");
            
            if (DatabaseConnection.testConnection()) {
                System.out.println("Database connected successfully!");
                
                // Option 1: Direct to Main Dashboard (for development/testing)
                // new MainDashboard().setVisible(true);
                
                // Option 2: Show Login Form first (recommended for final version)
                new LoginForm().setVisible(true);
                
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Could not connect to database.\n" +
                    "Please check:\n" +
                    "1. MySQL Server is running\n" +
                    "2. Database 'pradeep_rice_mill' exists\n" +
                    "3. Username and password are correct", 
                    "Database Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}