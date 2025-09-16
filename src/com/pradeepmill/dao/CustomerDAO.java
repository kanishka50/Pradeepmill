package com.pradeepmill.dao;

import com.pradeepmill.database.DatabaseConnection;
import com.pradeepmill.models.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    
    // Create new customer
    public boolean insertCustomer(Customer customer) {
        String sql = "INSERT INTO customers (customer_name, contact_person, phone, address, email, customer_type, credit_limit, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, customer.getCustomerName());
            pstmt.setString(2, customer.getContactPerson());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getEmail());
            pstmt.setString(6, customer.getCustomerType());
            pstmt.setDouble(7, customer.getCreditLimit());
            pstmt.setString(8, customer.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    customer.setCustomerId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Update existing customer
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET customer_name=?, contact_person=?, phone=?, address=?, email=?, customer_type=?, credit_limit=?, status=?, updated_at=CURRENT_TIMESTAMP WHERE customer_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getCustomerName());
            pstmt.setString(2, customer.getContactPerson());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getEmail());
            pstmt.setString(6, customer.getCustomerType());
            pstmt.setDouble(7, customer.getCreditLimit());
            pstmt.setString(8, customer.getStatus());
            pstmt.setInt(9, customer.getCustomerId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Delete customer (soft delete)
    public boolean deleteCustomer(int customerId) {
        String sql = "UPDATE customers SET status='Inactive', updated_at=CURRENT_TIMESTAMP WHERE customer_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Find customer by ID
    public Customer findCustomerById(int customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get all active customers
    public List<Customer> getAllActiveCustomers() {
        return getCustomersByStatus("Active");
    }
    
    // Get customers by type
    public List<Customer> getCustomersByType(String customerType) {
        String sql = "SELECT * FROM customers WHERE customer_type=? AND status='Active' ORDER BY customer_name";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
    
    // Get customers by status
    public List<Customer> getCustomersByStatus(String status) {
        String sql = "SELECT * FROM customers WHERE status=? ORDER BY customer_name";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
    
    // Search customers
    public List<Customer> searchCustomers(String keyword) {
        String sql = "SELECT * FROM customers WHERE (customer_name LIKE ? OR phone LIKE ? OR contact_person LIKE ?) AND status='Active' ORDER BY customer_name";
        List<Customer> customers = new ArrayList<>();
        String searchPattern = "%" + keyword + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
    
    // Get total customer count
    public int getTotalCustomerCount() {
        String sql = "SELECT COUNT(*) FROM customers WHERE status='Active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Get customer count by type
    public int getCustomerCountByType(String customerType) {
        String sql = "SELECT COUNT(*) FROM customers WHERE customer_type=? AND status='Active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Helper method to map ResultSet to Customer object
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setCustomerName(rs.getString("customer_name"));
        customer.setContactPerson(rs.getString("contact_person"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        customer.setEmail(rs.getString("email"));
        customer.setCustomerType(rs.getString("customer_type"));
        customer.setRegistrationDate(rs.getDate("registration_date"));
        customer.setStatus(rs.getString("status"));
        customer.setCreditLimit(rs.getDouble("credit_limit"));
        customer.setCreatedAt(rs.getTimestamp("created_at"));
        customer.setUpdatedAt(rs.getTimestamp("updated_at"));
        return customer;
    }
}