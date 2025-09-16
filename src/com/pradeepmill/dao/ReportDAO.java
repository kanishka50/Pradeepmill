package com.pradeepmill.dao;

import com.pradeepmill.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {
    
    // ===== CUSTOMER DETAILS REPORT (Required Report #1) =====
    public List<Map<String, Object>> getCustomerDetailsReport() {
        String sql = "SELECT " +
                     "c.customer_id, c.customer_name, c.phone, c.address, c.customer_type, " +
                     "COUNT(so.sale_id) as total_orders, " +
                     "COALESCE(SUM(so.total_amount), 0) as total_purchases, " +
                     "COALESCE(SUM(so.total_amount - so.paid_amount), 0) as outstanding_balance, " +
                     "MAX(so.sale_date) as last_purchase_date " +
                     "FROM customers c " +
                     "LEFT JOIN sales_orders so ON c.customer_id = so.customer_id " +
                     "WHERE c.status = 'Active' " +
                     "GROUP BY c.customer_id, c.customer_name, c.phone, c.address, c.customer_type " +
                     "ORDER BY c.customer_name";
        
        return executeReportQuery(sql);
    }
    
    // ===== SUPPLIER DETAILS REPORT (Required Report #2) =====
    public List<Map<String, Object>> getSupplierDetailsReport() {
        String sql = "SELECT " +
                     "s.supplier_id, s.supplier_name, s.phone, s.address, " +
                     "COUNT(po.purchase_id) as total_orders, " +
                     "COALESCE(SUM(po.total_amount), 0) as total_purchases, " +
                     "COALESCE(SUM(po.total_amount - po.paid_amount), 0) as outstanding_payments, " +
                     "MAX(po.purchase_date) as last_purchase_date " +
                     "FROM suppliers s " +
                     "LEFT JOIN purchase_orders po ON s.supplier_id = po.supplier_id " +
                     "WHERE s.status = 'Active' " +
                     "GROUP BY s.supplier_id, s.supplier_name, s.phone, s.address " +
                     "ORDER BY s.supplier_name";
        
        return executeReportQuery(sql);
    }
    
    // ===== MONTHLY SALARY REPORT (Required Report #3) =====
    public List<Map<String, Object>> getMonthlySalaryReport(String month) {
        String sql = "SELECT " +
                     "st.staff_id, st.employee_name, st.position, st.monthly_salary, " +
                     "sp.basic_salary, sp.overtime_amount, sp.bonus, sp.deductions, sp.net_salary, " +
                     "sp.payment_date, sp.payment_status " +
                     "FROM staff st " +
                     "LEFT JOIN salary_payments sp ON st.staff_id = sp.staff_id AND sp.payment_month = ? " +
                     "WHERE st.status = 'Active' " +
                     "ORDER BY st.employee_name";
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, month); // Format: YYYY-MM
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("staff_id", rs.getInt("staff_id"));
                row.put("employee_name", rs.getString("employee_name"));
                row.put("position", rs.getString("position"));
                row.put("monthly_salary", rs.getDouble("monthly_salary"));
                row.put("basic_salary", rs.getDouble("basic_salary"));
                row.put("overtime_amount", rs.getDouble("overtime_amount"));
                row.put("bonus", rs.getDouble("bonus"));
                row.put("deductions", rs.getDouble("deductions"));
                row.put("net_salary", rs.getDouble("net_salary"));
                row.put("payment_date", rs.getDate("payment_date"));
                row.put("payment_status", rs.getString("payment_status"));
                results.add(row);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    
    // ===== STOCK REPORT =====
    // ===== STOCK REPORT =====
public List<Map<String, Object>> getStockReport() {
    String sql = "SELECT " +
                 "p.product_id, p.product_name, p.product_type, p.grade, p.unit_price, p.unit, " +
                 "si.current_quantity, si.minimum_level, si.maximum_level, " +
                 "(si.current_quantity * p.unit_price) as stock_value, " +
                 "CASE " +
                 "  WHEN si.current_quantity <= 0 THEN 'Out of Stock' " +
                 "  WHEN si.current_quantity <= si.minimum_level THEN 'Low Stock' " +
                 "  WHEN si.current_quantity >= si.maximum_level AND si.maximum_level > 0 THEN 'Overstock' " +
                 "  ELSE 'Normal' " +
                 "END as stock_status " +
                 "FROM products p " +
                 "JOIN stock_inventory si ON p.product_id = si.product_id " +
                 // Remove this line temporarily to test: "WHERE p.status = 'Active' " +
                 "ORDER BY p.product_type, p.product_name";
    
    System.out.println("DEBUG: Executing stock report query: " + sql); // Add this debug line
    List<Map<String, Object>> results = executeReportQuery(sql);
    System.out.println("DEBUG: Stock report returned " + results.size() + " rows"); // Add this debug line
    
    return results;
}
    
    // ===== DASHBOARD STATISTICS =====
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total suppliers
            stats.put("total_suppliers", getCount(conn, "SELECT COUNT(*) FROM suppliers WHERE status='Active'"));
            
            // Total customers
            stats.put("total_customers", getCount(conn, "SELECT COUNT(*) FROM customers WHERE status='Active'"));
            
            // Total products
            stats.put("total_products", getCount(conn, "SELECT COUNT(*) FROM products WHERE status='Active'"));
            
            // Total staff
            stats.put("total_staff", getCount(conn, "SELECT COUNT(*) FROM staff WHERE status='Active'"));
            
            // Total stock value
            stats.put("total_stock_value", getDouble(conn, 
                "SELECT SUM(si.current_quantity * p.unit_price) " +
                "FROM stock_inventory si JOIN products p ON si.product_id = p.product_id " +
                "WHERE p.status = 'Active'"));
            
            // Today's sales (if any sales records exist)
            stats.put("todays_sales", getDouble(conn,
                "SELECT COALESCE(SUM(total_amount), 0) FROM sales_orders WHERE DATE(sale_date) = CURDATE()"));
            
            // This month's purchases
            stats.put("monthly_purchases", getDouble(conn,
                "SELECT COALESCE(SUM(total_amount), 0) FROM purchase_orders " +
                "WHERE YEAR(purchase_date) = YEAR(CURDATE()) AND MONTH(purchase_date) = MONTH(CURDATE())"));
            
            // Low stock items count
            stats.put("low_stock_items", getCount(conn,
                "SELECT COUNT(*) FROM stock_inventory si JOIN products p ON si.product_id = p.product_id " +
                "WHERE si.current_quantity <= si.minimum_level AND p.status = 'Active'"));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    // ===== PRODUCTION SUMMARY REPORT =====
    public List<Map<String, Object>> getProductionSummaryReport(Date fromDate, Date toDate) {
        String sql = "SELECT " +
                     "pr.production_date, pr.production_number, " +
                     "rp.product_name as raw_product, fp.product_name as finished_product, " +
                     "pr.input_quantity, pr.output_quantity, pr.conversion_rate, pr.waste_quantity, " +
                     "m.machine_name, s.employee_name as operator_name " +
                     "FROM production_records pr " +
                     "JOIN products rp ON pr.raw_product_id = rp.product_id " +
                     "JOIN products fp ON pr.finished_product_id = fp.product_id " +
                     "LEFT JOIN machines m ON pr.machine_id = m.machine_id " +
                     "LEFT JOIN staff s ON pr.operator_id = s.staff_id " +
                     "WHERE pr.production_date BETWEEN ? AND ? " +
                     "ORDER BY pr.production_date DESC";
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, fromDate);
            pstmt.setDate(2, toDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("production_date", rs.getDate("production_date"));
                row.put("production_number", rs.getString("production_number"));
                row.put("raw_product", rs.getString("raw_product"));
                row.put("finished_product", rs.getString("finished_product"));
                row.put("input_quantity", rs.getDouble("input_quantity"));
                row.put("output_quantity", rs.getDouble("output_quantity"));
                row.put("conversion_rate", rs.getDouble("conversion_rate"));
                row.put("waste_quantity", rs.getDouble("waste_quantity"));
                row.put("machine_name", rs.getString("machine_name"));
                row.put("operator_name", rs.getString("operator_name"));
                results.add(row);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    
    // ===== HELPER METHODS =====
    
    // Execute general report query and return results as Map list
    private List<Map<String, Object>> executeReportQuery(String sql) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    
    // Get integer count from query
    private int getCount(Connection conn, String sql) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    // Get double value from query
    private double getDouble(Connection conn, String sql) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
}