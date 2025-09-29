package com.pradeepmill.dao;

import com.pradeepmill.database.DatabaseConnection;
import com.pradeepmill.models.PurchaseOrder;
import com.pradeepmill.models.PaymentRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.pradeepmill.models.PurchaseItem;

public class PurchaseOrderDAO {
    
    // Create new purchase order
    public boolean insertPurchaseOrder(PurchaseOrder purchaseOrder) {
        String sql = "INSERT INTO purchase_orders (purchase_number, supplier_id, purchase_date, total_quantity, total_amount, paid_amount, payment_status, notes, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, purchaseOrder.getPurchaseNumber());
            pstmt.setInt(2, purchaseOrder.getSupplierId());
            pstmt.setDate(3, new java.sql.Date(purchaseOrder.getPurchaseDate().getTime()));
            pstmt.setDouble(4, purchaseOrder.getTotalQuantity());
            pstmt.setDouble(5, purchaseOrder.getTotalAmount());
            pstmt.setDouble(6, purchaseOrder.getPaidAmount());
            pstmt.setString(7, purchaseOrder.getPaymentStatus());
            pstmt.setString(8, purchaseOrder.getNotes());
            pstmt.setString(9, purchaseOrder.getCreatedBy());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    purchaseOrder.setPurchaseId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Update existing purchase order
    public boolean updatePurchaseOrder(PurchaseOrder purchaseOrder) {
        String sql = "UPDATE purchase_orders SET supplier_id=?, purchase_date=?, total_quantity=?, total_amount=?, paid_amount=?, payment_status=?, notes=?, updated_at=CURRENT_TIMESTAMP WHERE purchase_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, purchaseOrder.getSupplierId());
            pstmt.setDate(2, new java.sql.Date(purchaseOrder.getPurchaseDate().getTime()));
            pstmt.setDouble(3, purchaseOrder.getTotalQuantity());
            pstmt.setDouble(4, purchaseOrder.getTotalAmount());
            pstmt.setDouble(5, purchaseOrder.getPaidAmount());
            pstmt.setString(6, purchaseOrder.getPaymentStatus());
            pstmt.setString(7, purchaseOrder.getNotes());
            pstmt.setInt(8, purchaseOrder.getPurchaseId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // FIXED: Enhanced payment update with validation
    public boolean updatePayment(int purchaseId, double paidAmount, String paymentStatus) {
        String sql = "UPDATE purchase_orders SET paid_amount=?, payment_status=?, updated_at=CURRENT_TIMESTAMP WHERE purchase_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, paidAmount);
            pstmt.setString(2, paymentStatus);
            pstmt.setInt(3, purchaseId);
            
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Payment updated successfully for purchase ID: " + purchaseId);
                return true;
            } else {
                System.out.println("❌ No purchase order found with ID: " + purchaseId);
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error updating payment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // NEW: Insert payment record for history tracking
    public boolean insertPaymentRecord(PaymentRecord paymentRecord) {
        String sql = "INSERT INTO payment_records (purchase_id, payment_amount, payment_method, reference_number, payment_date, notes) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, paymentRecord.getPurchaseId());
            pstmt.setDouble(2, paymentRecord.getPaymentAmount());
            pstmt.setString(3, paymentRecord.getPaymentMethod());
            pstmt.setString(4, paymentRecord.getReferenceNumber());
            pstmt.setDate(5, new java.sql.Date(paymentRecord.getPaymentDate().getTime()));
            pstmt.setString(6, paymentRecord.getNotes());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    paymentRecord.setPaymentId(generatedKeys.getInt(1));
                }
                System.out.println("✅ Payment record saved for tracking");
                return true;
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error saving payment record: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Find purchase order by ID
    public PurchaseOrder findPurchaseOrderById(int purchaseId) {
        String sql = "SELECT po.*, s.supplier_name FROM purchase_orders po " +
                     "LEFT JOIN suppliers s ON po.supplier_id = s.supplier_id " +
                     "WHERE po.purchase_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, purchaseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPurchaseOrder(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // NEW: Find purchase order by number (needed for payment dialog)
    public PurchaseOrder findPurchaseOrderByNumber(String purchaseNumber) {
        String sql = "SELECT po.*, s.supplier_name FROM purchase_orders po " +
                     "LEFT JOIN suppliers s ON po.supplier_id = s.supplier_id " +
                     "WHERE po.purchase_number=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, purchaseNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPurchaseOrder(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get all purchase orders with supplier names
    public List<PurchaseOrder> getAllPurchaseOrders() {
        String sql = "SELECT po.*, s.supplier_name FROM purchase_orders po " +
                     "LEFT JOIN suppliers s ON po.supplier_id = s.supplier_id " +
                     "ORDER BY po.purchase_date DESC, po.purchase_id DESC";
        
        List<PurchaseOrder> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                orders.add(mapResultSetToPurchaseOrder(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    // Get purchase orders by payment status
    public List<PurchaseOrder> getPurchaseOrdersByPaymentStatus(String paymentStatus) {
        String sql = "SELECT po.*, s.supplier_name FROM purchase_orders po " +
                     "LEFT JOIN suppliers s ON po.supplier_id = s.supplier_id " +
                     "WHERE po.payment_status = ? " +
                     "ORDER BY po.purchase_date DESC";
        
        List<PurchaseOrder> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, paymentStatus);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToPurchaseOrder(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    // Get outstanding purchase orders (Pending or Partial payments)
    public List<PurchaseOrder> getOutstandingPurchases() {
        String sql = "SELECT po.*, s.supplier_name FROM purchase_orders po " +
                     "LEFT JOIN suppliers s ON po.supplier_id = s.supplier_id " +
                     "WHERE po.payment_status IN ('Pending', 'Partial') " +
                     "ORDER BY po.purchase_date ASC";
        
        List<PurchaseOrder> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                orders.add(mapResultSetToPurchaseOrder(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    // Search purchase orders
    public List<PurchaseOrder> searchPurchaseOrders(String keyword) {
        String sql = "SELECT po.*, s.supplier_name FROM purchase_orders po " +
                     "LEFT JOIN suppliers s ON po.supplier_id = s.supplier_id " +
                     "WHERE (po.purchase_number LIKE ? OR s.supplier_name LIKE ? OR po.notes LIKE ?) " +
                     "ORDER BY po.purchase_date DESC";
        
        List<PurchaseOrder> orders = new ArrayList<>();
        String searchPattern = "%" + keyword + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToPurchaseOrder(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    // Get purchase order statistics
    public Map<String, Object> getPurchaseOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        String sql = "SELECT " +
                     "COUNT(*) as total_orders, " +
                     "SUM(total_amount) as total_amount, " +
                     "SUM(paid_amount) as total_paid, " +
                     "SUM(total_amount - paid_amount) as outstanding_amount, " +
                     "SUM(CASE WHEN payment_status = 'Pending' THEN 1 ELSE 0 END) as pending_orders, " +
                     "SUM(CASE WHEN payment_status = 'Partial' THEN 1 ELSE 0 END) as partial_orders, " +
                     "SUM(CASE WHEN payment_status = 'Paid' THEN 1 ELSE 0 END) as paid_orders " +
                     "FROM purchase_orders";
        
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
    
    // Helper method to map ResultSet to PurchaseOrder object
    private PurchaseOrder mapResultSetToPurchaseOrder(ResultSet rs) throws SQLException {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseId(rs.getInt("purchase_id"));
        purchaseOrder.setPurchaseNumber(rs.getString("purchase_number"));
        purchaseOrder.setSupplierId(rs.getInt("supplier_id"));
        purchaseOrder.setPurchaseDate(rs.getDate("purchase_date"));
        purchaseOrder.setTotalQuantity(rs.getDouble("total_quantity"));
        purchaseOrder.setTotalAmount(rs.getDouble("total_amount"));
        purchaseOrder.setPaidAmount(rs.getDouble("paid_amount"));
        purchaseOrder.setPaymentStatus(rs.getString("payment_status"));
        purchaseOrder.setNotes(rs.getString("notes"));
        purchaseOrder.setCreatedBy(rs.getString("created_by"));
        purchaseOrder.setCreatedAt(rs.getTimestamp("created_at"));
        purchaseOrder.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Set supplier name if available
        try {
            purchaseOrder.setSupplierName(rs.getString("supplier_name"));
        } catch (SQLException e) {
            // Supplier name not available in this query
        }
        
        return purchaseOrder;
    }
    
    // Purchase Item Operations
    public boolean insertPurchaseItems(int purchaseId, List<PurchaseItem> items) {
        String sql = "INSERT INTO purchase_items (purchase_id, product_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (PurchaseItem item : items) {
                pstmt.setInt(1, purchaseId);
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
    
    // Get purchase items for a specific order
    public List<PurchaseItem> getPurchaseItems(int purchaseId) {
        String sql = "SELECT pi.*, p.product_name FROM purchase_items pi " +
                     "LEFT JOIN products p ON pi.product_id = p.product_id " +
                     "WHERE pi.purchase_id = ?";
        
        List<PurchaseItem> items = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, purchaseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PurchaseItem item = new PurchaseItem();
                item.setItemId(rs.getInt("item_id"));
                item.setPurchaseId(rs.getInt("purchase_id"));
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
    
    // Delete purchase order and its items
    public boolean deletePurchaseOrder(int purchaseId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // First delete purchase items
            String deleteItemsSql = "DELETE FROM purchase_items WHERE purchase_id = ?";
            try (PreparedStatement pstmt1 = conn.prepareStatement(deleteItemsSql)) {
                pstmt1.setInt(1, purchaseId);
                pstmt1.executeUpdate();
            }
            
            // Then delete purchase order
            String deleteOrderSql = "DELETE FROM purchase_orders WHERE purchase_id = ?";
            try (PreparedStatement pstmt2 = conn.prepareStatement(deleteOrderSql)) {
                pstmt2.setInt(1, purchaseId);
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