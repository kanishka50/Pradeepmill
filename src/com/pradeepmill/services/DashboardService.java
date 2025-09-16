package com.pradeepmill.services;

import com.pradeepmill.dao.*;
import java.util.HashMap;
import java.util.Map;

public class DashboardService {
    
    private SupplierDAO supplierDAO;
    private CustomerDAO customerDAO;
    private ProductDAO productDAO;
    private StaffDAO staffDAO;
    private StockInventoryDAO stockDAO;
    private ReportDAO reportDAO;
    
    public DashboardService() {
        this.supplierDAO = new SupplierDAO();
        this.customerDAO = new CustomerDAO();
        this.productDAO = new ProductDAO();
        this.staffDAO = new StaffDAO();
        this.stockDAO = new StockInventoryDAO();
        this.reportDAO = new ReportDAO();
    }
    
    /**
     * Get comprehensive dashboard statistics
     */
    public Map<String, Object> getAllDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            // Basic counts
            metrics.put("totalSuppliers", supplierDAO.getTotalSupplierCount());
            metrics.put("totalCustomers", customerDAO.getTotalCustomerCount());
            metrics.put("totalProducts", productDAO.getTotalProductCount());
            metrics.put("totalStaff", staffDAO.getTotalStaffCount());
            
            // Financial metrics
            double totalStockValue = stockDAO.getTotalStockValue();
            double monthlyExpense = staffDAO.getTotalMonthlySalaryExpense();
            
            metrics.put("totalStockValue", totalStockValue);
            metrics.put("monthlyExpense", monthlyExpense);
            
            // Stock alerts
            int lowStockCount = stockDAO.getLowStockItems().size();
            int outOfStockCount = stockDAO.getOutOfStockItems().size();
            
            metrics.put("lowStockItems", lowStockCount);
            metrics.put("outOfStockItems", outOfStockCount);
            
            // Status indicators
            metrics.put("stockStatus", getStockStatusSummary());
            metrics.put("systemHealth", getSystemHealthStatus());
            
        } catch (Exception e) {
            e.printStackTrace();
            // Return default values in case of error
            setDefaultMetrics(metrics);
        }
        
        return metrics;
    }
    
    /**
     * Get stock status summary
     */
    private String getStockStatusSummary() {
        try {
            int lowStock = stockDAO.getLowStockItems().size();
            int outOfStock = stockDAO.getOutOfStockItems().size();
            
            if (outOfStock > 0) {
                return "Critical - " + outOfStock + " items out of stock";
            } else if (lowStock > 0) {
                return "Warning - " + lowStock + " items low stock";
            } else {
                return "Normal - All items adequately stocked";
            }
        } catch (Exception e) {
            return "Unable to determine stock status";
        }
    }
    
    /**
     * Get system health status
     */
    private String getSystemHealthStatus() {
        try {
            int suppliers = supplierDAO.getTotalSupplierCount();
            int customers = customerDAO.getTotalCustomerCount();
            int products = productDAO.getTotalProductCount();
            
            if (suppliers > 0 && customers > 0 && products > 0) {
                return "System Operational";
            } else {
                return "System Setup Incomplete";
            }
        } catch (Exception e) {
            return "System Status Unknown";
        }
    }
    
    /**
     * Set default metrics in case of database errors
     */
    private void setDefaultMetrics(Map<String, Object> metrics) {
        metrics.put("totalSuppliers", 0);
        metrics.put("totalCustomers", 0);
        metrics.put("totalProducts", 0);
        metrics.put("totalStaff", 0);
        metrics.put("totalStockValue", 0.0);
        metrics.put("monthlyExpense", 0.0);
        metrics.put("lowStockItems", 0);
        metrics.put("outOfStockItems", 0);
        metrics.put("stockStatus", "Data unavailable");
        metrics.put("systemHealth", "Database connection error");
    }
    
    /**
     * Get recent activity summary
     */
    public Map<String, Object> getRecentActivitySummary() {
        Map<String, Object> activity = new HashMap<>();
        
        try {
            // Get recent statistics from ReportDAO
            Map<String, Object> stats = reportDAO.getDashboardStats();
            
            activity.put("todaysSales", stats.get("todays_sales"));
            activity.put("monthlyPurchases", stats.get("monthly_purchases"));
            activity.put("activeSuppliers", supplierDAO.getSuppliersByStatus("Active").size());
            activity.put("activeCustomers", customerDAO.getCustomersByStatus("Active").size());
            
        } catch (Exception e) {
            e.printStackTrace();
            activity.put("todaysSales", 0.0);
            activity.put("monthlyPurchases", 0.0);
            activity.put("activeSuppliers", 0);
            activity.put("activeCustomers", 0);
        }
        
        return activity;
    }
}