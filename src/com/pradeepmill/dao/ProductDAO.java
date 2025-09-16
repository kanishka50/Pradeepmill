package com.pradeepmill.dao;

import com.pradeepmill.database.DatabaseConnection;
import com.pradeepmill.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    // Create new product
    public boolean insertProduct(Product product) {
        String sql = "INSERT INTO products (product_name, product_type, grade, unit_price, unit, description, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, product.getProductName());
            pstmt.setString(2, product.getProductType());
            pstmt.setString(3, product.getGrade());
            pstmt.setDouble(4, product.getUnitPrice());
            pstmt.setString(5, product.getUnit());
            pstmt.setString(6, product.getDescription());
            pstmt.setString(7, product.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    product.setProductId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Update existing product
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET product_name=?, product_type=?, grade=?, unit_price=?, unit=?, description=?, status=?, updated_at=CURRENT_TIMESTAMP WHERE product_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getProductName());
            pstmt.setString(2, product.getProductType());
            pstmt.setString(3, product.getGrade());
            pstmt.setDouble(4, product.getUnitPrice());
            pstmt.setString(5, product.getUnit());
            pstmt.setString(6, product.getDescription());
            pstmt.setString(7, product.getStatus());
            pstmt.setInt(8, product.getProductId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Delete product (soft delete)
    public boolean deleteProduct(int productId) {
        String sql = "UPDATE products SET status='Inactive', updated_at=CURRENT_TIMESTAMP WHERE product_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Find product by ID
    public Product findProductById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get all active products
    public List<Product> getAllActiveProducts() {
        return getProductsByStatus("Active");
    }
    
    // Get products by type (Raw_Paddy, Finished_Rice, By_Product)
    public List<Product> getProductsByType(String productType) {
        String sql = "SELECT * FROM products WHERE product_type=? AND status='Active' ORDER BY product_name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, productType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // Get raw paddy products (for production input)
    public List<Product> getRawPaddyProducts() {
        return getProductsByType(Product.TYPE_RAW_PADDY);
    }
    
    // Get finished rice products (for sales)
    public List<Product> getFinishedRiceProducts() {
        return getProductsByType(Product.TYPE_FINISHED_RICE);
    }
    
    // Get by-products
    public List<Product> getByProducts() {
        return getProductsByType(Product.TYPE_BY_PRODUCT);
    }
    
    // Get products by status
    public List<Product> getProductsByStatus(String status) {
        String sql = "SELECT * FROM products WHERE status=? ORDER BY product_name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // Search products
    public List<Product> searchProducts(String keyword) {
        String sql = "SELECT * FROM products WHERE (product_name LIKE ? OR description LIKE ?) AND status='Active' ORDER BY product_name";
        List<Product> products = new ArrayList<>();
        String searchPattern = "%" + keyword + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // Get total product count
    public int getTotalProductCount() {
        String sql = "SELECT COUNT(*) FROM products WHERE status='Active'";
        
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
    
    // Helper method to map ResultSet to Product object
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));
        product.setProductType(rs.getString("product_type"));
        product.setGrade(rs.getString("grade"));
        product.setUnitPrice(rs.getDouble("unit_price"));
        product.setUnit(rs.getString("unit"));
        product.setDescription(rs.getString("description"));
        product.setStatus(rs.getString("status"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        product.setUpdatedAt(rs.getTimestamp("updated_at"));
        return product;
    }
    
    // Get products by grade
public List<Product> getProductsByGrade(String grade) {
    String sql = "SELECT * FROM products WHERE grade=? AND status='Active' ORDER BY product_name";
    List<Product> products = new ArrayList<>();
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, grade);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            products.add(mapResultSetToProduct(rs));
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
}

// Get products by both type and grade
public List<Product> getProductsByTypeAndGrade(String productType, String grade) {
    String sql = "SELECT * FROM products WHERE product_type=? AND grade=? AND status='Active' ORDER BY product_name";
    List<Product> products = new ArrayList<>();
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, productType);
        pstmt.setString(2, grade);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            products.add(mapResultSetToProduct(rs));
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
    }
}