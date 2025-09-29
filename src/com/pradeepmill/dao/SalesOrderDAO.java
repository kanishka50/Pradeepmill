package com.pradeepmill.dao;

import com.pradeepmill.database.DatabaseConnection;
import com.pradeepmill.models.SalesOrder;
import com.pradeepmill.models.SalesItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SalesOrderDAO {
    
    // Create new sales order
    public boolean insertSalesOrder(SalesOrder salesOrder) {
        String sql = "INSERT INTO sales_orders (sale_number, customer_id, sale_date, total_quantity, total_amount, paid_amount, payment_status, notes, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, salesOrder.getSaleNumber());
            pstmt.setInt(2, salesOrder.getCustomerId());
            pstmt.setDate(3, new java.sql.Date(salesOrder.getSaleDate().getTime()));
            pstmt.setDouble(4, salesOrder.getTotalQuantity());
            pstmt.setDouble(5, salesOrder.getTotalAmount());
            pstmt.setDouble(6, salesOrder.getPaidAmount());
            pstmt.setString(7, salesOrder.getPaymentStatus());
            pstmt.setString(8, salesOrder.getNotes());
            pstmt.setString(9, salesOrder.getCreatedBy());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    salesOrder.setSaleId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Update existing sales order
    public boolean updateSalesOrder(SalesOrder salesOrder) {
        String sql = "UPDATE sales_orders SET customer_id=?, sale_date=?, total_quantity=?, total_amount=?, paid_amount=?, payment_status=?, notes=?, updated_at=CURRENT_TIMESTAMP WHERE sale_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, salesOrder.getCustomerId());
            pstmt.setDate(2, new java.sql.Date(salesOrder.getSaleDate().getTime()));
            pstmt.setDouble(3, salesOrder.getTotalQuantity());
            pstmt.setDouble(4, salesOrder.getTotalAmount());
            pstmt.setDouble(5, salesOrder.getPaidAmount());
            pstmt.setString(6, salesOrder.getPaymentStatus());
            pstmt.setString(7, salesOrder.getNotes());
            pstmt.setInt(8, salesOrder.getSaleId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // FIXED: Enhanced payment update for sales
    public boolean updatePayment(int saleId, double paidAmount, String paymentStatus) {
        String sql = "UPDATE sales_orders SET paid_amount=?, payment_status=?, updated_at=CURRENT_TIMESTAMP WHERE sale_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, paidAmount);
            pstmt.setString(2, paymentStatus);
            pstmt.setInt(3, saleId);
            
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Sales payment updated successfully for sale ID: " + saleId);
                return true;
            } else {
                System.out.println("❌ No sales order found with ID: " + saleId);
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error updating sales payment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Find sales order by ID
    public SalesOrder findSalesOrderById(int saleId) {
        String sql = "SELECT so.*, c.customer_name FROM sales_orders so " +
                     "LEFT JOIN customers c ON so.customer_id = c.customer_id " +
                     "WHERE so.sale_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, saleId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSalesOrder(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // NEW: Find sales order by number
    public SalesOrder findSalesOrderByNumber(String saleNumber) {
        String sql = "SELECT so.*, c.customer_name FROM sales_orders so " +
                     "LEFT JOIN customers c ON so.customer_id = c.customer_id " +
                     "WHERE so.sale_number=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, saleNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSalesOrder(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get all sales orders with customer names
    public List<SalesOrder> getAllSalesOrders() {
        String sql = "SELECT so.*, c.customer_name FROM sales_orders so " +
                     "LEFT JOIN customers c ON so.customer_id = c.customer_id " +
                     "ORDER BY so.sale_date DESC, so.sale_id DESC";
        
        List<SalesOrder> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                orders.add(mapResultSetToSalesOrder(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    // Get sales orders by payment status
    public List<SalesOrder> getSalesOrdersByPaymentStatus(String paymentStatus) {
        String sql = "SELECT so.*, c.customer_name FROM sales_orders so " +
                     "LEFT JOIN customers c ON so.customer_id = c.customer_id " +
                     "WHERE so.payment_status = ? " +
                     "ORDER BY so.sale_date DESC";
        
        List<SalesOrder> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, paymentStatus);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToSalesOrder(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    // Get outstanding sales orders
    public List<SalesOrder> getOutstandingSales() {
        String sql = "SELECT so.*, c.customer_name FROM sales_orders so " +
                     "LEFT JOIN customers c ON so.customer_id = c.customer_id " +
                     "WHERE so.payment_status IN ('Pending', 'Partial') " +
                     "ORDER BY so.sale_date ASC";
        
        List<SalesOrder> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                orders.add(mapResultSetToSalesOrder(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    // Search sales orders
    public List<SalesOrder> searchSalesOrders(String keyword) {
        String sql = "SELECT so.*, c.customer_name FROM sales_orders so " +
                     "LEFT JOIN customers c ON so.customer_id = c.customer_id " +
                     "WHERE (so.sale_number LIKE ? OR c.customer_name LIKE ? OR so.notes LIKE ?) " +
                     "ORDER BY so.sale_date DESC";
        
        List<SalesOrder> orders = new ArrayList<>();
        String searchPattern = "%" + keyword + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToSalesOrder(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    // Get sales order statistics
    public Map<String, Object> getSalesOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        String sql = "SELECT " +
                     "COUNT(*) as total_orders, " +
                     "SUM(total_amount) as total_amount, " +
                     "SUM(paid_amount) as total_paid, " +
                     "SUM(total_amount - paid_amount) as outstanding_amount, " +
                     "SUM(CASE WHEN payment_status = 'Pending' THEN 1 ELSE 0 END) as pending_orders, " +
                     "SUM(CASE WHEN payment_status = 'Partial' THEN 1 ELSE 0 END) as partial_orders, " +
                     "SUM(CASE WHEN payment_status = 'Paid' THEN 1 ELSE 0 END) as paid_orders " +
                     "FROM sales_orders";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                stats.put("totalOrders", rs.getInt("total_orders"));
                stats.put("totalAmount", rs.getDouble("total_amount"));
                stats.put("totalPaid", rs.getDouble("total_paid"));
                stats.put("outstandingAmount", rs.getDouble("outstanding_amount"));
                stats.put("pendingOrders", rs.getInt("pending_orders"));
                stats.put("partialOrders", rs.getInt("partial_orders"));
                stats.put("paidOrders", rs.getInt("paid_orders"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    // Helper method to map ResultSet to SalesOrder object
    private SalesOrder mapResultSetToSalesOrder(ResultSet rs) throws SQLException {
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setSaleId(rs.getInt("sale_id"));
        salesOrder.setSaleNumber(rs.getString("sale_number"));
        salesOrder.setCustomerId(rs.getInt("customer_id"));
        salesOrder.setSaleDate(rs.getDate("sale_date"));
        salesOrder.setTotalQuantity(rs.getDouble("total_quantity"));
        salesOrder.setTotalAmount(rs.getDouble("total_amount"));
        salesOrder.setPaidAmount(rs.getDouble("paid_amount"));
        salesOrder.setPaymentStatus(rs.getString("payment_status"));
        salesOrder.setNotes(rs.getString("notes"));
        salesOrder.setCreatedBy(rs.getString("created_by"));
        salesOrder.setCreatedAt(rs.getTimestamp("created_at"));
        salesOrder.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Set customer name if available
        try {
            salesOrder.setCustomerName(rs.getString("customer_name"));
        } catch (SQLException e) {
            // Customer name not available in this query
        }
        
        return salesOrder;
    }
    
    // Sales Item Operations
    public boolean insertSalesItems(int saleId, List<SalesItem> items) {
        String sql = "INSERT INTO sales_items (sale_id, product_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (SalesItem item : items) {
                pstmt.setInt(1, saleId);
                pstmt.setInt(2, item.getProductId());
                pstmt.setDouble(3, item.getQuantity());
                pstmt.setDouble(4, item.getUnitPrice());
                pstmt.setDouble(5, item.getQuantity() * item.getUnitPrice());
                pstmt.addBatch();
            }
            
            int[] results = pstmt.executeBatch();
            
            // Check if all items were inserted
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Get sales items for a specific order
    public List<SalesItem> getSalesItems(int saleId) {
        String sql = "SELECT si.*, p.product_name FROM sales_items si " +
                     "LEFT JOIN products p ON si.product_id = p.product_id " +
                     "WHERE si.sale_id = ?";
        
        List<SalesItem> items = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, saleId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                SalesItem item = new SalesItem();
                item.setItemId(rs.getInt("item_id"));
                item.setSaleId(rs.getInt("sale_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getDouble("quantity"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setTotalPrice(rs.getDouble("total_price"));
                
                // Set product name if available
                try {
                    item.setProductName(rs.getString("product_name"));
                } catch (SQLException e) {
                    // Product name not available
                }
                
                items.add(item);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    // Delete sales order and its items
    public boolean deleteSalesOrder(int saleId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // First delete sales items
            String deleteItemsSql = "DELETE FROM sales_items WHERE sale_id = ?";
            try (PreparedStatement pstmt1 = conn.prepareStatement(deleteItemsSql)) {
                pstmt1.setInt(1, saleId);
                pstmt1.executeUpdate();
            }
            
            // Then delete sales order
            String deleteOrderSql = "DELETE FROM sales_orders WHERE sale_id = ?";
            try (PreparedStatement pstmt2 = conn.prepareStatement(deleteOrderSql)) {
                pstmt2.setInt(1, saleId);
                int rowsAffected = pstmt2.executeUpdate();
                
                if (rowsAffected > 0) {
                    conn.commit();
                    return true;
                }
            }
            
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}