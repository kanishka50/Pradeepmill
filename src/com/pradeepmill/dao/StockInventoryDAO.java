package com.pradeepmill.dao;

import com.pradeepmill.database.DatabaseConnection;
import com.pradeepmill.models.StockInventory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class StockInventoryDAO {
    
    // Get stock by product ID
    public StockInventory getStockByProductId(int productId) {
        String sql = "SELECT si.*, p.product_name, p.product_type, p.unit_price, p.unit " +
                     "FROM stock_inventory si " +
                     "JOIN products p ON si.product_id = p.product_id " +
                     "WHERE si.product_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStockInventory(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get all stock items with product details
    public List<StockInventory> getAllStockItems() {
        String sql = "SELECT si.*, p.product_name, p.product_type, p.unit_price, p.unit " +
                     "FROM stock_inventory si " +
                     "JOIN products p ON si.product_id = p.product_id " +
                     "WHERE p.status = 'Active' " +
                     "ORDER BY p.product_type, p.product_name";
        
        List<StockInventory> stockItems = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                stockItems.add(mapResultSetToStockInventory(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stockItems;
    }
    
    // Get stock by product type
    public List<StockInventory> getStockByProductType(String productType) {
        String sql = "SELECT si.*, p.product_name, p.product_type, p.unit_price, p.unit " +
                     "FROM stock_inventory si " +
                     "JOIN products p ON si.product_id = p.product_id " +
                     "WHERE p.product_type = ? AND p.status = 'Active' " +
                     "ORDER BY p.product_name";
        
        List<StockInventory> stockItems = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, productType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                stockItems.add(mapResultSetToStockInventory(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stockItems;
    }
    
    // Get low stock items
    public List<StockInventory> getLowStockItems() {
        String sql = "SELECT si.*, p.product_name, p.product_type, p.unit_price, p.unit " +
                     "FROM stock_inventory si " +
                     "JOIN products p ON si.product_id = p.product_id " +
                     "WHERE si.current_quantity <= si.minimum_level AND si.current_quantity > 0 " +
                     "AND p.status = 'Active' " +
                     "ORDER BY (si.current_quantity / si.minimum_level) ASC";
        
        List<StockInventory> stockItems = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                stockItems.add(mapResultSetToStockInventory(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stockItems;
    }
    
    // Get out of stock items
    public List<StockInventory> getOutOfStockItems() {
        String sql = "SELECT si.*, p.product_name, p.product_type, p.unit_price, p.unit " +
                     "FROM stock_inventory si " +
                     "JOIN products p ON si.product_id = p.product_id " +
                     "WHERE si.current_quantity <= 0 AND p.status = 'Active' " +
                     "ORDER BY p.product_name";
        
        List<StockInventory> stockItems = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                stockItems.add(mapResultSetToStockInventory(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stockItems;
    }
    
    // Update stock quantity (for transactions)
    public boolean updateStockQuantity(int productId, double quantityChange) {
        String sql = "UPDATE stock_inventory SET current_quantity = current_quantity + ?, last_updated = CURRENT_TIMESTAMP WHERE product_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, quantityChange);
            pstmt.setInt(2, productId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Update stock levels (minimum/maximum)
    public boolean updateStockLevels(int productId, double minimumLevel, double maximumLevel) {
        String sql = "UPDATE stock_inventory SET minimum_level = ?, maximum_level = ?, last_updated = CURRENT_TIMESTAMP WHERE product_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, minimumLevel);
            pstmt.setDouble(2, maximumLevel);
            pstmt.setInt(3, productId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Check stock availability for sales
    public boolean isStockAvailable(int productId, double requiredQuantity) {
        String sql = "SELECT current_quantity FROM stock_inventory WHERE product_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double currentQuantity = rs.getDouble("current_quantity");
                return currentQuantity >= requiredQuantity;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Get total stock value
    public double getTotalStockValue() {
        String sql = "SELECT SUM(si.current_quantity * p.unit_price) as total_value " +
                     "FROM stock_inventory si " +
                     "JOIN products p ON si.product_id = p.product_id " +
                     "WHERE p.status = 'Active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble("total_value");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    // Get stock value by product type
    public double getStockValueByType(String productType) {
        String sql = "SELECT SUM(si.current_quantity * p.unit_price) as total_value " +
                     "FROM stock_inventory si " +
                     "JOIN products p ON si.product_id = p.product_id " +
                     "WHERE p.product_type = ? AND p.status = 'Active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, productType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total_value");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    // Get inventory statistics
    public Map<String, Object> getInventoryStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        String sql = "SELECT " +
                     "COUNT(*) as total_products, " +
                     "SUM(CASE WHEN si.current_quantity <= 0 THEN 1 ELSE 0 END) as out_of_stock, " +
                     "SUM(CASE WHEN si.current_quantity <= si.minimum_level AND si.current_quantity > 0 THEN 1 ELSE 0 END) as low_stock, " +
                     "SUM(CASE WHEN si.current_quantity >= si.maximum_level AND si.maximum_level > 0 THEN 1 ELSE 0 END) as overstock, " +
                     "SUM(si.current_quantity * p.unit_price) as total_value " +
                     "FROM stock_inventory si " +
                     "JOIN products p ON si.product_id = p.product_id " +
                     "WHERE p.status = 'Active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                stats.put("totalProducts", rs.getInt("total_products"));
                stats.put("outOfStock", rs.getInt("out_of_stock"));
                stats.put("lowStock", rs.getInt("low_stock"));
                stats.put("overstock", rs.getInt("overstock"));
                stats.put("totalValue", rs.getDouble("total_value"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    // Search stock items
    public List<StockInventory> searchStockItems(String keyword) {
        String sql = "SELECT si.*, p.product_name, p.product_type, p.unit_price, p.unit " +
                     "FROM stock_inventory si " +
                     "JOIN products p ON si.product_id = p.product_id " +
                     "WHERE (p.product_name LIKE ? OR p.product_type LIKE ?) " +
                     "AND p.status = 'Active' " +
                     "ORDER BY p.product_name";
        
        List<StockInventory> stockItems = new ArrayList<>();
        String searchPattern = "%" + keyword + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                stockItems.add(mapResultSetToStockInventory(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stockItems;
    }
    
    // Helper method to map ResultSet to StockInventory object
    private StockInventory mapResultSetToStockInventory(ResultSet rs) throws SQLException {
        StockInventory stockInventory = new StockInventory();
        stockInventory.setStockId(rs.getInt("stock_id"));
        stockInventory.setProductId(rs.getInt("product_id"));
        stockInventory.setCurrentQuantity(rs.getDouble("current_quantity"));
        stockInventory.setMinimumLevel(rs.getDouble("minimum_level"));
        stockInventory.setMaximumLevel(rs.getDouble("maximum_level"));
        stockInventory.setLastUpdated(rs.getTimestamp("last_updated"));
        
        // Set product information
        try {
            stockInventory.setProductName(rs.getString("product_name"));
            stockInventory.setProductType(rs.getString("product_type"));
            stockInventory.setUnitPrice(rs.getDouble("unit_price"));
            stockInventory.setUnit(rs.getString("unit"));
        } catch (SQLException e) {
            // Some fields might not be available in certain queries
        }
        
        return stockInventory;
    }
}