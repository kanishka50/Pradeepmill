package com.pradeepmill.dao;

import com.pradeepmill.database.DatabaseConnection;
import com.pradeepmill.models.ProductionRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ProductionRecordDAO {
    
    // Create new production record
    public boolean insertProductionRecord(ProductionRecord productionRecord) {
        String sql = "INSERT INTO production_records (production_number, production_date, raw_product_id, finished_product_id, input_quantity, output_quantity, conversion_rate, waste_quantity, machine_id, operator_id, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, productionRecord.getProductionNumber());
            pstmt.setDate(2, new java.sql.Date(productionRecord.getProductionDate().getTime()));
            pstmt.setInt(3, productionRecord.getRawProductId());
            pstmt.setInt(4, productionRecord.getFinishedProductId());
            pstmt.setDouble(5, productionRecord.getInputQuantity());
            pstmt.setDouble(6, productionRecord.getOutputQuantity());
            pstmt.setDouble(7, productionRecord.getConversionRate());
            pstmt.setDouble(8, productionRecord.getWasteQuantity());
            
            // Handle optional machine and operator
            if (productionRecord.getMachineId() > 0) {
                pstmt.setInt(9, productionRecord.getMachineId());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }
            
            if (productionRecord.getOperatorId() > 0) {
                pstmt.setInt(10, productionRecord.getOperatorId());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }
            
            pstmt.setString(11, productionRecord.getNotes());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    productionRecord.setProductionId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Update existing production record
    public boolean updateProductionRecord(ProductionRecord productionRecord) {
        String sql = "UPDATE production_records SET production_date=?, raw_product_id=?, finished_product_id=?, input_quantity=?, output_quantity=?, conversion_rate=?, waste_quantity=?, machine_id=?, operator_id=?, notes=?, updated_at=CURRENT_TIMESTAMP WHERE production_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, new java.sql.Date(productionRecord.getProductionDate().getTime()));
            pstmt.setInt(2, productionRecord.getRawProductId());
            pstmt.setInt(3, productionRecord.getFinishedProductId());
            pstmt.setDouble(4, productionRecord.getInputQuantity());
            pstmt.setDouble(5, productionRecord.getOutputQuantity());
            pstmt.setDouble(6, productionRecord.getConversionRate());
            pstmt.setDouble(7, productionRecord.getWasteQuantity());
            
            if (productionRecord.getMachineId() > 0) {
                pstmt.setInt(8, productionRecord.getMachineId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            
            if (productionRecord.getOperatorId() > 0) {
                pstmt.setInt(9, productionRecord.getOperatorId());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }
            
            pstmt.setString(10, productionRecord.getNotes());
            pstmt.setInt(11, productionRecord.getProductionId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Find production record by ID
    public ProductionRecord findProductionRecordById(int productionId) {
        String sql = "SELECT pr.*, rp.product_name as raw_product_name, fp.product_name as finished_product_name, " +
                     "m.machine_name, s.employee_name as operator_name " +
                     "FROM production_records pr " +
                     "LEFT JOIN products rp ON pr.raw_product_id = rp.product_id " +
                     "LEFT JOIN products fp ON pr.finished_product_id = fp.product_id " +
                     "LEFT JOIN machines m ON pr.machine_id = m.machine_id " +
                     "LEFT JOIN staff s ON pr.operator_id = s.staff_id " +
                     "WHERE pr.production_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProductionRecord(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get all production records with related data
    public List<ProductionRecord> getAllProductionRecords() {
        String sql = "SELECT pr.*, rp.product_name as raw_product_name, fp.product_name as finished_product_name, " +
                     "m.machine_name, s.employee_name as operator_name " +
                     "FROM production_records pr " +
                     "LEFT JOIN products rp ON pr.raw_product_id = rp.product_id " +
                     "LEFT JOIN products fp ON pr.finished_product_id = fp.product_id " +
                     "LEFT JOIN machines m ON pr.machine_id = m.machine_id " +
                     "LEFT JOIN staff s ON pr.operator_id = s.staff_id " +
                     "ORDER BY pr.production_date DESC, pr.production_id DESC";
        
        List<ProductionRecord> productionRecords = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                productionRecords.add(mapResultSetToProductionRecord(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productionRecords;
    }
    
    // Get production records by date range
    public List<ProductionRecord> getProductionRecordsByDateRange(java.util.Date startDate, java.util.Date endDate) {
        String sql = "SELECT pr.*, rp.product_name as raw_product_name, fp.product_name as finished_product_name, " +
                     "m.machine_name, s.employee_name as operator_name " +
                     "FROM production_records pr " +
                     "LEFT JOIN products rp ON pr.raw_product_id = rp.product_id " +
                     "LEFT JOIN products fp ON pr.finished_product_id = fp.product_id " +
                     "LEFT JOIN machines m ON pr.machine_id = m.machine_id " +
                     "LEFT JOIN staff s ON pr.operator_id = s.staff_id " +
                     "WHERE pr.production_date BETWEEN ? AND ? " +
                     "ORDER BY pr.production_date DESC";
        
        List<ProductionRecord> productionRecords = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(2, new java.sql.Date(endDate.getTime()));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                productionRecords.add(mapResultSetToProductionRecord(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productionRecords;
    }
    
    // Get production records by machine
    public List<ProductionRecord> getProductionRecordsByMachine(int machineId) {
        String sql = "SELECT pr.*, rp.product_name as raw_product_name, fp.product_name as finished_product_name, " +
                     "m.machine_name, s.employee_name as operator_name " +
                     "FROM production_records pr " +
                     "LEFT JOIN products rp ON pr.raw_product_id = rp.product_id " +
                     "LEFT JOIN products fp ON pr.finished_product_id = fp.product_id " +
                     "LEFT JOIN machines m ON pr.machine_id = m.machine_id " +
                     "LEFT JOIN staff s ON pr.operator_id = s.staff_id " +
                     "WHERE pr.machine_id=? ORDER BY pr.production_date DESC";
        
        List<ProductionRecord> productionRecords = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, machineId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                productionRecords.add(mapResultSetToProductionRecord(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productionRecords;
    }
    
    // Search production records
    public List<ProductionRecord> searchProductionRecords(String keyword) {
        String sql = "SELECT pr.*, rp.product_name as raw_product_name, fp.product_name as finished_product_name, " +
                     "m.machine_name, s.employee_name as operator_name " +
                     "FROM production_records pr " +
                     "LEFT JOIN products rp ON pr.raw_product_id = rp.product_id " +
                     "LEFT JOIN products fp ON pr.finished_product_id = fp.product_id " +
                     "LEFT JOIN machines m ON pr.machine_id = m.machine_id " +
                     "LEFT JOIN staff s ON pr.operator_id = s.staff_id " +
                     "WHERE (pr.production_number LIKE ? OR rp.product_name LIKE ? OR fp.product_name LIKE ? OR m.machine_name LIKE ? OR s.employee_name LIKE ? OR pr.notes LIKE ?) " +
                     "ORDER BY pr.production_date DESC";
        
        List<ProductionRecord> productionRecords = new ArrayList<>();
        String searchPattern = "%" + keyword + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setString(5, searchPattern);
            pstmt.setString(6, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                productionRecords.add(mapResultSetToProductionRecord(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productionRecords;
    }
    
    // Get production statistics
    public Map<String, Object> getProductionStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        String sql = "SELECT " +
                     "COUNT(*) as total_productions, " +
                     "SUM(input_quantity) as total_input, " +
                     "SUM(output_quantity) as total_output, " +
                     "SUM(waste_quantity) as total_waste, " +
                     "AVG(conversion_rate) as avg_conversion_rate " +
                     "FROM production_records";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                stats.put("totalProductions", rs.getInt("total_productions"));
                stats.put("totalInput", rs.getDouble("total_input"));
                stats.put("totalOutput", rs.getDouble("total_output"));
                stats.put("totalWaste", rs.getDouble("total_waste"));
                stats.put("avgConversionRate", rs.getDouble("avg_conversion_rate"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    // Get production efficiency by machine
    public List<Map<String, Object>> getProductionEfficiencyByMachine() {
        String sql = "SELECT m.machine_name, m.machine_id, " +
                     "COUNT(pr.production_id) as production_count, " +
                     "AVG(pr.conversion_rate) as avg_efficiency, " +
                     "SUM(pr.input_quantity) as total_processed " +
                     "FROM machines m " +
                     "LEFT JOIN production_records pr ON m.machine_id = pr.machine_id " +
                     "WHERE m.status = 'Active' " +
                     "GROUP BY m.machine_id, m.machine_name " +
                     "ORDER BY avg_efficiency DESC";
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("machineName", rs.getString("machine_name"));
                row.put("machineId", rs.getInt("machine_id"));
                row.put("productionCount", rs.getInt("production_count"));
                row.put("avgEfficiency", rs.getDouble("avg_efficiency"));
                row.put("totalProcessed", rs.getDouble("total_processed"));
                results.add(row);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    
    // Helper method to map ResultSet to ProductionRecord object
    private ProductionRecord mapResultSetToProductionRecord(ResultSet rs) throws SQLException {
        ProductionRecord productionRecord = new ProductionRecord();
        productionRecord.setProductionId(rs.getInt("production_id"));
        productionRecord.setProductionNumber(rs.getString("production_number"));
        productionRecord.setProductionDate(rs.getDate("production_date"));
        productionRecord.setRawProductId(rs.getInt("raw_product_id"));
        productionRecord.setFinishedProductId(rs.getInt("finished_product_id"));
        productionRecord.setInputQuantity(rs.getDouble("input_quantity"));
        productionRecord.setOutputQuantity(rs.getDouble("output_quantity"));
        productionRecord.setConversionRate(rs.getDouble("conversion_rate"));
        productionRecord.setWasteQuantity(rs.getDouble("waste_quantity"));
        productionRecord.setMachineId(rs.getInt("machine_id"));
        productionRecord.setOperatorId(rs.getInt("operator_id"));
        productionRecord.setNotes(rs.getString("notes"));
        productionRecord.setCreatedAt(rs.getTimestamp("created_at"));
        productionRecord.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Set related object names
        try {
            productionRecord.setRawProductName(rs.getString("raw_product_name"));
            productionRecord.setFinishedProductName(rs.getString("finished_product_name"));
            productionRecord.setMachineName(rs.getString("machine_name"));
            productionRecord.setOperatorName(rs.getString("operator_name"));
        } catch (SQLException e) {
            // Some fields might not be available
        }
        
        return productionRecord;
    }
}