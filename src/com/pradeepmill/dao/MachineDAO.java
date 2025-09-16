package com.pradeepmill.dao;

import com.pradeepmill.database.DatabaseConnection;
import com.pradeepmill.models.Machine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MachineDAO {
    
    // Create new machine
    public boolean insertMachine(Machine machine) {
        String sql = "INSERT INTO machines (machine_name, machine_type, location, installation_date, capacity_per_hour, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, machine.getMachineName());
            pstmt.setString(2, machine.getMachineType());
            pstmt.setString(3, machine.getLocation());
            pstmt.setDate(4, machine.getInstallationDate() != null ? new java.sql.Date(machine.getInstallationDate().getTime()) : new java.sql.Date(System.currentTimeMillis()));
            pstmt.setDouble(5, machine.getCapacityPerHour());
            pstmt.setString(6, machine.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    machine.setMachineId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Update existing machine
    public boolean updateMachine(Machine machine) {
        String sql = "UPDATE machines SET machine_name=?, machine_type=?, location=?, capacity_per_hour=?, status=?, updated_at=CURRENT_TIMESTAMP WHERE machine_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, machine.getMachineName());
            pstmt.setString(2, machine.getMachineType());
            pstmt.setString(3, machine.getLocation());
            pstmt.setDouble(4, machine.getCapacityPerHour());
            pstmt.setString(5, machine.getStatus());
            pstmt.setInt(6, machine.getMachineId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Delete machine (soft delete)
    public boolean deleteMachine(int machineId) {
        String sql = "UPDATE machines SET status='Broken', updated_at=CURRENT_TIMESTAMP WHERE machine_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, machineId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Find machine by ID
    public Machine findMachineById(int machineId) {
        String sql = "SELECT * FROM machines WHERE machine_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, machineId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMachine(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get all active machines
    public List<Machine> getAllActiveMachines() {
        return getMachinesByStatus("Active");
    }
    
    // Get machines by type
    public List<Machine> getMachinesByType(String machineType) {
        String sql = "SELECT * FROM machines WHERE machine_type=? ORDER BY machine_name";
        List<Machine> machines = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, machineType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                machines.add(mapResultSetToMachine(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return machines;
    }
    
    // Get machines by status
    public List<Machine> getMachinesByStatus(String status) {
        String sql = "SELECT * FROM machines WHERE status=? ORDER BY machine_name";
        List<Machine> machines = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                machines.add(mapResultSetToMachine(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return machines;
    }
    
    // Get machines by type and status
    public List<Machine> getMachinesByTypeAndStatus(String machineType, String status) {
        String sql = "SELECT * FROM machines WHERE machine_type=? AND status=? ORDER BY machine_name";
        List<Machine> machines = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, machineType);
            pstmt.setString(2, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                machines.add(mapResultSetToMachine(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return machines;
    }
    
    // Get operational machines (for production)
    public List<Machine> getOperationalMachines() {
        return getMachinesByStatus(Machine.STATUS_ACTIVE);
    }
    
    // Get machines by type for production
    public List<Machine> getCleanerMachines() {
        return getMachinesByType(Machine.TYPE_CLEANER);
    }
    
    public List<Machine> getDeHuskerMachines() {
        return getMachinesByType(Machine.TYPE_DEHUSKER);
    }
    
    public List<Machine> getPolisherMachines() {
        return getMachinesByType(Machine.TYPE_POLISHER);
    }
    
    public List<Machine> getGraderMachines() {
        return getMachinesByType(Machine.TYPE_GRADER);
    }
    
    public List<Machine> getPackerMachines() {
        return getMachinesByType(Machine.TYPE_PACKER);
    }
    
    // Search machines
    public List<Machine> searchMachines(String keyword) {
        String sql = "SELECT * FROM machines WHERE (machine_name LIKE ? OR machine_type LIKE ? OR location LIKE ?) ORDER BY machine_name";
        List<Machine> machines = new ArrayList<>();
        String searchPattern = "%" + keyword + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                machines.add(mapResultSetToMachine(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return machines;
    }
    
    // Get total machine count
    public int getTotalMachineCount() {
        String sql = "SELECT COUNT(*) FROM machines WHERE status='Active'";
        
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
    
    // Get total capacity
    public double getTotalCapacity() {
        String sql = "SELECT SUM(capacity_per_hour) FROM machines WHERE status='Active'";
        
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
    
    // Helper method to map ResultSet to Machine object
    private Machine mapResultSetToMachine(ResultSet rs) throws SQLException {
        Machine machine = new Machine();
        machine.setMachineId(rs.getInt("machine_id"));
        machine.setMachineName(rs.getString("machine_name"));
        machine.setMachineType(rs.getString("machine_type"));
        machine.setLocation(rs.getString("location"));
        machine.setInstallationDate(rs.getDate("installation_date"));
        machine.setCapacityPerHour(rs.getDouble("capacity_per_hour"));
        machine.setStatus(rs.getString("status"));
        machine.setCreatedAt(rs.getTimestamp("created_at"));
        machine.setUpdatedAt(rs.getTimestamp("updated_at"));
        return machine;
    }
}