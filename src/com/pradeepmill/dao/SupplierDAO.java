package com.pradeepmill.dao;

import com.pradeepmill.database.DatabaseConnection;
import com.pradeepmill.models.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    
    // Create new supplier
    public boolean insertSupplier(Supplier supplier) {
        String sql = "INSERT INTO suppliers (supplier_name, contact_person, phone, address, email, credit_limit, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, supplier.getSupplierName());
            pstmt.setString(2, supplier.getContactPerson());
            pstmt.setString(3, supplier.getPhone());
            pstmt.setString(4, supplier.getAddress());
            pstmt.setString(5, supplier.getEmail());
            pstmt.setDouble(6, supplier.getCreditLimit());
            pstmt.setString(7, supplier.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get generated ID
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    supplier.setSupplierId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Update existing supplier
    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE suppliers SET supplier_name=?, contact_person=?, phone=?, address=?, email=?, credit_limit=?, status=?, updated_at=CURRENT_TIMESTAMP WHERE supplier_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, supplier.getSupplierName());
            pstmt.setString(2, supplier.getContactPerson());
            pstmt.setString(3, supplier.getPhone());
            pstmt.setString(4, supplier.getAddress());
            pstmt.setString(5, supplier.getEmail());
            pstmt.setDouble(6, supplier.getCreditLimit());
            pstmt.setString(7, supplier.getStatus());
            pstmt.setInt(8, supplier.getSupplierId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Delete supplier (soft delete - set status to Inactive)
    public boolean deleteSupplier(int supplierId) {
        String sql = "UPDATE suppliers SET status='Inactive', updated_at=CURRENT_TIMESTAMP WHERE supplier_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, supplierId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Find supplier by ID
    public Supplier findSupplierById(int supplierId) {
        String sql = "SELECT * FROM suppliers WHERE supplier_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, supplierId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSupplier(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get all active suppliers
    public List<Supplier> getAllActiveSuppliers() {
        return getSuppliersByStatus("Active");
    }
    
    // Get all suppliers (including inactive)
    public List<Supplier> getAllSuppliers() {
        String sql = "SELECT * FROM suppliers ORDER BY supplier_name";
        List<Supplier> suppliers = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }
    
    // Get suppliers by status
    public List<Supplier> getSuppliersByStatus(String status) {
        String sql = "SELECT * FROM suppliers WHERE status=? ORDER BY supplier_name";
        List<Supplier> suppliers = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }
    
    // Search suppliers by name, phone, or contact person
    public List<Supplier> searchSuppliers(String keyword) {
        String sql = "SELECT * FROM suppliers WHERE (supplier_name LIKE ? OR phone LIKE ? OR contact_person LIKE ?) AND status='Active' ORDER BY supplier_name";
        List<Supplier> suppliers = new ArrayList<>();
        String searchPattern = "%" + keyword + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }
    
    // Check if supplier name already exists
    public boolean isSupplierNameExists(String supplierName, int excludeId) {
        String sql = "SELECT COUNT(*) FROM suppliers WHERE supplier_name=? AND supplier_id!=? AND status='Active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, supplierName);
            pstmt.setInt(2, excludeId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Get total supplier count
    public int getTotalSupplierCount() {
        String sql = "SELECT COUNT(*) FROM suppliers WHERE status='Active'";
        
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
    
    // Helper method to map ResultSet to Supplier object
    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(rs.getInt("supplier_id"));
        supplier.setSupplierName(rs.getString("supplier_name"));
        supplier.setContactPerson(rs.getString("contact_person"));
        supplier.setPhone(rs.getString("phone"));
        supplier.setAddress(rs.getString("address"));
        supplier.setEmail(rs.getString("email"));
        supplier.setRegistrationDate(rs.getDate("registration_date"));
        supplier.setStatus(rs.getString("status"));
        supplier.setCreditLimit(rs.getDouble("credit_limit"));
        supplier.setCreatedAt(rs.getTimestamp("created_at"));
        supplier.setUpdatedAt(rs.getTimestamp("updated_at"));
        return supplier;
    }
}