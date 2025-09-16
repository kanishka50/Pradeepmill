package com.pradeepmill.main;

import com.pradeepmill.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        
        // Test connection
        if (DatabaseConnection.testConnection()) {
            System.out.println("✓ Database connection successful!");
            
            // Test data retrieval
            try {
                Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM products");
                
                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("✓ Products in database: " + count);
                }
                
                rs.close();
                stmt.close();
                
            } catch (Exception e) {
                System.out.println("✗ Error testing data: " + e.getMessage());
            }
            
        } else {
            System.out.println("✗ Database connection failed!");
        }
        
        DatabaseConnection.closeConnection();
    }
}