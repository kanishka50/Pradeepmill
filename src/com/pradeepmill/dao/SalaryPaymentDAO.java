package com.pradeepmill.dao;

import com.pradeepmill.database.DatabaseConnection;
import com.pradeepmill.models.Staff;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalaryPaymentDAO {
    
    /**
     * Simple salary payment insertion - only basic salary and bonus
     */
    public boolean insertSimpleSalaryPayment(int staffId, String paymentMonth, double basicSalary, double bonus) {
        String sql = "INSERT INTO salary_payments (staff_id, payment_month, basic_salary, " +
                     "overtime_hours, overtime_amount, bonus, deductions, net_salary, " +
                     "payment_date, payment_status) VALUES (?, ?, ?, 0, 0, ?, 0, ?, CURDATE(), 'Paid')";
        
        double netSalary = basicSalary + bonus;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            pstmt.setString(2, paymentMonth);
            pstmt.setDouble(3, basicSalary);
            pstmt.setDouble(4, bonus);
            pstmt.setDouble(5, netSalary);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Salary payment saved - StaffID: " + staffId + 
                             ", Month: " + paymentMonth + 
                             ", Basic: " + basicSalary + 
                             ", Bonus: " + bonus + 
                             ", Net: " + netSalary);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting salary payment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Check if payment already exists for staff and month
     */
    public boolean paymentExists(int staffId, String paymentMonth) {
        String sql = "SELECT COUNT(*) FROM salary_payments WHERE staff_id = ? AND payment_month = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            pstmt.setString(2, paymentMonth);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking payment existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Process monthly payments for all active staff with simplified approach
     */
    public int processMonthlyPayments(String paymentMonth) {
        int processedCount = 0;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            
            // Get all active staff
            String staffSql = "SELECT staff_id, monthly_salary FROM staff WHERE status = 'Active'";
            PreparedStatement staffStmt = conn.prepareStatement(staffSql);
            ResultSet staffRs = staffStmt.executeQuery();
            
            while (staffRs.next()) {
                int staffId = staffRs.getInt("staff_id");
                double monthlySalary = staffRs.getDouble("monthly_salary");
                
                // Check if payment already exists
                if (!paymentExists(staffId, paymentMonth)) {
                    // Create simple payment record (only basic salary, no bonus)
                    if (insertSimpleSalaryPayment(staffId, paymentMonth, monthlySalary, 0.0)) {
                        processedCount++;
                    }
                }
            }
            
            staffStmt.close();
            conn.commit(); // Commit transaction
            conn.setAutoCommit(true);
            
        } catch (SQLException e) {
            System.err.println("Error processing monthly payments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return processedCount;
    }
    
    /**
     * Get salary payment details for a specific month
     */
    public List<Map<String, Object>> getSalaryPaymentsByMonth(String paymentMonth) {
        String sql = "SELECT sp.payment_id, sp.staff_id, sp.payment_month, " +
                     "sp.basic_salary, sp.bonus, sp.net_salary, sp.payment_date, sp.payment_status, " +
                     "st.employee_name, st.position, st.monthly_salary " +
                     "FROM salary_payments sp " +
                     "JOIN staff st ON sp.staff_id = st.staff_id " +
                     "WHERE sp.payment_month = ? " +
                     "ORDER BY st.employee_name";
        
        List<Map<String, Object>> payments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, paymentMonth);
            ResultSet rs = pstmt.executeQuery();
            
            System.out.println("DEBUG: Fetching salary payments for month: " + paymentMonth);
            
            while (rs.next()) {
                Map<String, Object> payment = new HashMap<>();
                payment.put("payment_id", rs.getInt("payment_id"));
                payment.put("staff_id", rs.getInt("staff_id"));
                payment.put("employee_name", rs.getString("employee_name"));
                payment.put("position", rs.getString("position"));
                payment.put("payment_month", rs.getString("payment_month"));
                payment.put("monthly_salary", rs.getDouble("monthly_salary"));
                payment.put("basic_salary", rs.getDouble("basic_salary"));
                payment.put("bonus", rs.getDouble("bonus"));
                payment.put("net_salary", rs.getDouble("net_salary"));
                payment.put("payment_date", rs.getDate("payment_date"));
                payment.put("payment_status", rs.getString("payment_status"));
                
                // Set overtime fields to 0 for simplified system
                payment.put("overtime_hours", 0.0);
                payment.put("overtime_amount", 0.0);
                payment.put("deductions", 0.0);
                
                System.out.println("DEBUG: Found payment - Staff ID: " + rs.getInt("staff_id") + 
                                 ", Name: " + rs.getString("employee_name") + 
                                 ", Basic: " + rs.getDouble("basic_salary") + 
                                 ", Bonus: " + rs.getDouble("bonus") + 
                                 ", Net: " + rs.getDouble("net_salary"));
                
                payments.add(payment);
            }
            
            System.out.println("DEBUG: Total payments found: " + payments.size());
            
        } catch (SQLException e) {
            System.err.println("Error getting salary payments by month: " + e.getMessage());
            e.printStackTrace();
        }
        
        return payments;
    }
    
    /**
     * Update salary payment - simplified version (bonus only)
     */
    public boolean updateSalaryPayment(int paymentId, double basicSalary, double bonus) {
        String sql = "UPDATE salary_payments SET basic_salary = ?, bonus = ?, net_salary = ?, " +
                     "overtime_hours = 0, overtime_amount = 0, deductions = 0 " +
                     "WHERE payment_id = ?";
        
        double netSalary = basicSalary + bonus;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, basicSalary);
            pstmt.setDouble(2, bonus);
            pstmt.setDouble(3, netSalary);
            pstmt.setInt(4, paymentId);
            
            boolean updated = pstmt.executeUpdate() > 0;
            if (updated) {
                System.out.println("DEBUG: Updated salary payment ID " + paymentId + 
                                 " - Basic: " + basicSalary + ", Bonus: " + bonus + ", Net: " + netSalary);
            }
            return updated;
            
        } catch (SQLException e) {
            System.err.println("Error updating salary payment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update existing salary payment by staff ID and month
     */
    public boolean updateSalaryPaymentByStaffAndMonth(int staffId, String paymentMonth, double basicSalary, double bonus) {
        String sql = "UPDATE salary_payments SET basic_salary = ?, bonus = ?, net_salary = ?, " +
                     "overtime_hours = 0, overtime_amount = 0, deductions = 0 " +
                     "WHERE staff_id = ? AND payment_month = ?";
        
        double netSalary = basicSalary + bonus;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, basicSalary);
            pstmt.setDouble(2, bonus);
            pstmt.setDouble(3, netSalary);
            pstmt.setInt(4, staffId);
            pstmt.setString(5, paymentMonth);
            
            boolean updated = pstmt.executeUpdate() > 0;
            if (updated) {
                System.out.println("DEBUG: Updated salary payment for staff ID " + staffId + 
                                 " month " + paymentMonth + 
                                 " - Basic: " + basicSalary + ", Bonus: " + bonus + ", Net: " + netSalary);
            }
            return updated;
            
        } catch (SQLException e) {
            System.err.println("Error updating salary payment by staff and month: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete salary payment
     */
    public boolean deleteSalaryPayment(int paymentId) {
        String sql = "DELETE FROM salary_payments WHERE payment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, paymentId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting salary payment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get total salary expense for a month
     */
    public double getTotalSalaryExpense(String paymentMonth) {
        String sql = "SELECT SUM(net_salary) FROM salary_payments WHERE payment_month = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, paymentMonth);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total salary expense: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
}