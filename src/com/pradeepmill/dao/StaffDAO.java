package com.pradeepmill.dao;

import com.pradeepmill.database.DatabaseConnection;
import com.pradeepmill.models.Staff;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    
    // Create new staff
    public boolean insertStaff(Staff staff) {
        String sql = "INSERT INTO staff (employee_name, position, phone, address, monthly_salary, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, staff.getEmployeeName());
            pstmt.setString(2, staff.getPosition());
            pstmt.setString(3, staff.getPhone());
            pstmt.setString(4, staff.getAddress());
            pstmt.setDouble(5, staff.getMonthlySalary());
            pstmt.setString(6, staff.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    staff.setStaffId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Update existing staff
    public boolean updateStaff(Staff staff) {
        String sql = "UPDATE staff SET employee_name=?, position=?, phone=?, address=?, monthly_salary=?, status=?, updated_at=CURRENT_TIMESTAMP WHERE staff_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, staff.getEmployeeName());
            pstmt.setString(2, staff.getPosition());
            pstmt.setString(3, staff.getPhone());
            pstmt.setString(4, staff.getAddress());
            pstmt.setDouble(5, staff.getMonthlySalary());
            pstmt.setString(6, staff.getStatus());
            pstmt.setInt(7, staff.getStaffId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Delete staff (soft delete)
    public boolean deleteStaff(int staffId) {
        String sql = "UPDATE staff SET status='Inactive', updated_at=CURRENT_TIMESTAMP WHERE staff_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Find staff by ID
    public Staff findStaffById(int staffId) {
        String sql = "SELECT * FROM staff WHERE staff_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStaff(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get all active staff
    public List<Staff> getAllActiveStaff() {
        return getStaffByStatus("Active");
    }
    
    // Get staff by status
    public List<Staff> getStaffByStatus(String status) {
        String sql = "SELECT * FROM staff WHERE status=? ORDER BY employee_name";
        List<Staff> staffList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffList;
    }
    
    // Get staff by position
    public List<Staff> getStaffByPosition(String position) {
        String sql = "SELECT * FROM staff WHERE position=? AND status='Active' ORDER BY employee_name";
        List<Staff> staffList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, position);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffList;
    }
    
    // Search staff
    public List<Staff> searchStaff(String keyword) {
        String sql = "SELECT * FROM staff WHERE (employee_name LIKE ? OR phone LIKE ? OR position LIKE ?) AND status='Active' ORDER BY employee_name";
        List<Staff> staffList = new ArrayList<>();
        String searchPattern = "%" + keyword + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffList;
    }
    
    // Get total staff count
    public int getTotalStaffCount() {
        String sql = "SELECT COUNT(*) FROM staff WHERE status='Active'";
        
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
    
    // Get total monthly salary expense
    public double getTotalMonthlySalaryExpense() {
        String sql = "SELECT SUM(monthly_salary) FROM staff WHERE status='Active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    // Get machine operators (for production records)
    public List<Staff> getMachineOperators() {
        return getStaffByPosition(Staff.POSITION_OPERATOR);
    }
    
    // Helper method to map ResultSet to Staff object
    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setStaffId(rs.getInt("staff_id"));
        staff.setEmployeeName(rs.getString("employee_name"));
        staff.setPosition(rs.getString("position"));
        staff.setPhone(rs.getString("phone"));
        staff.setAddress(rs.getString("address"));
        staff.setHireDate(rs.getDate("hire_date"));
        staff.setMonthlySalary(rs.getDouble("monthly_salary"));
        staff.setStatus(rs.getString("status"));
        staff.setCreatedAt(rs.getTimestamp("created_at"));
        staff.setUpdatedAt(rs.getTimestamp("updated_at"));
        return staff;
    }
    // Get staff by position and status
public List<Staff> getStaffByPositionAndStatus(String position, String status) {
    String sql = "SELECT * FROM staff WHERE position=? AND status=? ORDER BY employee_name";
    List<Staff> staffList = new ArrayList<>();
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, position);
        pstmt.setString(2, status);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            staffList.add(mapResultSetToStaff(rs));
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return staffList;
    }
}