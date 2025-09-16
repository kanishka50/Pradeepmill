package com.pradeepmill.main;

import com.pradeepmill.services.*;
import com.pradeepmill.models.StockInventory;
import java.util.List;
import java.util.Map;

public class ServiceTest {
    public static void main(String[] args) {
        System.out.println("=== Testing Service Layer ===\n");
        
        // Test Dashboard Service
        testDashboardService();
        
        // Test Inventory Service
        testInventoryService();
        
        // Test Report Service
        testReportService();
        
        System.out.println("\n✅ Service layer testing completed!");
        System.out.println("🚀 Ready for UI Integration!");
    }
    
    private static void testDashboardService() {
        System.out.println("--- Testing DashboardService ---");
        DashboardService dashboardService = new DashboardService();
        
        try {
            Map<String, Object> metrics = dashboardService.getAllDashboardMetrics();
            System.out.println("✓ Dashboard metrics loaded:");
            System.out.println("  - Total Suppliers: " + metrics.get("totalSuppliers"));
            System.out.println("  - Total Customers: " + metrics.get("totalCustomers"));
            System.out.println("  - Total Products: " + metrics.get("totalProducts"));
            System.out.println("  - Stock Value: Rs." + String.format("%.2f", (Double) metrics.get("totalStockValue")));
            System.out.println("  - Stock Status: " + metrics.get("stockStatus"));
            System.out.println("  - System Health: " + metrics.get("systemHealth"));
            
        } catch (Exception e) {
            System.out.println("✗ Dashboard service error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testInventoryService() {
        System.out.println("\n--- Testing InventoryService ---");
        InventoryService inventoryService = new InventoryService();
        
        try {
            // Test inventory valuation
            Map<String, Double> valuation = inventoryService.getInventoryValuation();
            System.out.println("✓ Inventory valuation:");
            System.out.println("  - Total Value: Rs." + String.format("%.2f", valuation.get("totalValue")));
            System.out.println("  - Raw Paddy Value: Rs." + String.format("%.2f", valuation.get("rawPaddyValue")));
            System.out.println("  - Finished Rice Value: Rs." + String.format("%.2f", valuation.get("finishedRiceValue")));
            System.out.println("  - By-Product Value: Rs." + String.format("%.2f", valuation.get("byProductValue")));
            
            // Test inventory alerts - Fixed generic type
            Map<String, List<StockInventory>> alerts = inventoryService.getInventoryAlerts();
            System.out.println("  - Low Stock Items: " + alerts.get("lowStock").size());
            System.out.println("  - Out of Stock Items: " + alerts.get("outOfStock").size());
            
        } catch (Exception e) {
            System.out.println("✗ Inventory service error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testReportService() {
        System.out.println("\n--- Testing ReportService ---");
        ReportService reportService = new ReportService();
        
        try {
            // Test customer report
            List<Map<String, Object>> customerReport = reportService.generateCustomerDetailsReport();
            System.out.println("✓ Customer Details Report: " + customerReport.size() + " customers");
            
            // Test supplier report
            List<Map<String, Object>> supplierReport = reportService.generateSupplierDetailsReport();
            System.out.println("✓ Supplier Details Report: " + supplierReport.size() + " suppliers");
            
            // Test salary report (current month)
            String currentMonth = java.time.LocalDate.now().toString().substring(0, 7); // YYYY-MM
            List<Map<String, Object>> salaryReport = reportService.generateMonthlySalaryReport(currentMonth);
            System.out.println("✓ Monthly Salary Report: " + salaryReport.size() + " staff records");
            
            // Test summary statistics
            Map<String, Object> summary = reportService.getReportSummaryStatistics();
            System.out.println("✓ Report Summary Statistics:");
            System.out.println("  - Total Customers: " + summary.get("total_customers"));
            System.out.println("  - Total Suppliers: " + summary.get("total_suppliers"));
            System.out.println("  - Monthly Salary Expense: Rs." + String.format("%.2f", (Double) summary.get("monthly_salary_expense")));
            
        } catch (Exception e) {
            System.out.println("✗ Report service error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}