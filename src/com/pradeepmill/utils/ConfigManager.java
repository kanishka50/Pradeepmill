package com.pradeepmill.utils;

import java.io.*;
import java.util.Properties;
import javax.swing.JOptionPane;

public class ConfigManager {
    
    private static final String CONFIG_FILE = "system.properties";
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";
    
    // Load credentials from configuration file
    public static Properties loadCredentials() {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);
        
        try {
            if (configFile.exists()) {
                // Load existing configuration
                FileInputStream fis = new FileInputStream(configFile);
                props.load(fis);
                fis.close();
            } else {
                // Create default configuration file
                props.setProperty("username", DEFAULT_USERNAME);
                props.setProperty("password", DEFAULT_PASSWORD);
                saveCredentials(props);
                System.out.println("Created default configuration file: " + CONFIG_FILE);
            }
        } catch (IOException e) {
            System.out.println("Error loading configuration: " + e.getMessage());
            // Return default values if file operations fail
            props.setProperty("username", DEFAULT_USERNAME);
            props.setProperty("password", DEFAULT_PASSWORD);
        }
        
        return props;
    }
    
    // Save credentials to configuration file
    public static boolean saveCredentials(Properties props) {
        try {
            FileOutputStream fos = new FileOutputStream(CONFIG_FILE);
            props.store(fos, "Pradeep Rice Mill System Configuration");
            fos.close();
            return true;
        } catch (IOException e) {
            System.out.println("Error saving configuration: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error saving configuration: " + e.getMessage(), 
                "Configuration Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Get current username
    public static String getUsername() {
        Properties props = loadCredentials();
        return props.getProperty("username", DEFAULT_USERNAME);
    }
    
    // Get current password
    public static String getPassword() {
        Properties props = loadCredentials();
        return props.getProperty("password", DEFAULT_PASSWORD);
    }
    
    // Change password
    public static boolean changePassword(String currentPassword, String newPassword) {
        Properties props = loadCredentials();
        String storedPassword = props.getProperty("password", DEFAULT_PASSWORD);
        
        // Verify current password
        if (!storedPassword.equals(currentPassword)) {
            return false; // Current password is incorrect
        }
        
        // Update password
        props.setProperty("password", newPassword);
        return saveCredentials(props);
    }
    
    // Change username
    public static boolean changeUsername(String newUsername) {
        Properties props = loadCredentials();
        props.setProperty("username", newUsername);
        return saveCredentials(props);
    }
    
    // Authenticate user
    public static boolean authenticate(String username, String password) {
        Properties props = loadCredentials();
        String storedUsername = props.getProperty("username", DEFAULT_USERNAME);
        String storedPassword = props.getProperty("password", DEFAULT_PASSWORD);
        
        return storedUsername.equals(username) && storedPassword.equals(password);
    }
}