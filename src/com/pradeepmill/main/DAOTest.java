package com.pradeepmill.main;

import com.pradeepmill.dao.*;
import com.pradeepmill.models.*;
import java.util.List;
import java.util.Map;

public class DAOTest {
    public static void main(String[] args) {
        System.out.println("=== Testing Complete DAO Layer ===\n");
        
        // Test Core DAOs
        testCoreDAOs();
        
        // Test Advanced DAOs
        testAdvancedDAOs();
        
        // Test Reports
        testReports();
        
        System.out.println("\n✅ Complete DAO layer testing completed!");
        System.out.println("🚀 Ready for Service layer and UI integration!");
    }
    
    private static void testCoreDAOs() {
        System.out.println("--- Core DAOs ---");
        
        SupplierDAO supplierDAO = new SupplierDAO();
        CustomerDAO customerDAO = new CustomerDAO();
        ProductDAO productDAO = new ProductDAO();
        
        System.out.println("✓ Suppliers: " + supplierDAO.getTotalSupplierCount());
        System.out.println("✓ Customers: " + customerDAO.getTotalCustomerCount());
        System.out.println("✓ Products: " + productDAO.getTotalProductCount());
    }
    
    private static void testAdvancedDAOs() {
        System.out.println("\n--- Advanced DAOs ---");
        
        // Test StaffDAO
        StaffDAO staffDAO = new StaffDAO();
        List<Staff> staffList = staffDAO.getAllActiveStaff();
        double totalSalary = staffDAO.getTotalMonthlySalaryExpense();
        System.out.println("✓ Staff: " + staffList.size() + " employees (Monthly expense: Rs." + String.format("%.2f", totalSalary) + ")");
        
        // Test StockInventoryDAO
        StockInventoryDAO stockDAO = new StockInventoryDAO();
        List<StockInventory> allStock = stockDAO.getAllStockItems();
        List<StockInventory> lowStock = stockDAO.getLowStockItems();
        double totalValue = stockDAO.getTotalStockValue();
        
        System.out.println("✓ Stock Items: " + allStock.size() + " (Total value: Rs." + String.format("%.2f", totalValue) + ")");
        System.out.println("✓ Low Stock Items: " + lowStock.size());
        
        // Display low stock items
        if (!lowStock.isEmpty()) {
            System.out.println("  ⚠️ Low stock alerts:");
            for (StockInventory stock : lowStock) {
                System.out.println("    - " + stock.getProductName() + 
                                 " (Current: " + stock.getCurrentQuantity() + 
                                 ", Min: " + stock.getMinimumLevel() + ")");
            }
        }
    }
    
    private static void testReports() {
        System.out.println("\n--- Report Generation ---");
        
        ReportDAO reportDAO = new ReportDAO();
        
        // Test Dashboard Stats
        Map<String, Object> stats = reportDAO.getDashboardStats();
        System.out.println("✓ Dashboard Statistics:");
        System.out.println("  - Total Suppliers: " + stats.get("total_suppliers"));
        System.out.println("  - Total Customers: " + stats.get("total_customers"));
        System.out.println("  - Total Products: " + stats.get("total_products"));
        System.out.println("  - Total Staff: " + stats.get("total_staff"));
        System.out.println("  - Stock Value: Rs." + String.format("%.2f", (Double) stats.get("total_stock_value")));
        System.out.println("  - Low Stock Items: " + stats.get("low_stock_items"));
        
        // Test Required Reports
        List<Map<String, Object>> customerReport = reportDAO.getCustomerDetailsReport();
        List<Map<String, Object>> supplierReport = reportDAO.getSupplierDetailsReport();
        
        System.out.println("\n✓ Required Reports:");
        System.out.println("  - Customer Details Report: " + customerReport.size() + " customers");
        System.out.println("  - Supplier Details Report: " + supplierReport.size() + " suppliers");
        
        // Display sample customer data
        if (!customerReport.isEmpty()) {
            Map<String, Object> firstCustomer = customerReport.get(0);
            System.out.println("  - Sample Customer: " + firstCustomer.get("customer_name") + 
                             " (Total Purchases: Rs." + firstCustomer.get("total_purchases") + ")");
        }
        
        // Test Stock Report
        List<Map<String, Object>> stockReport = reportDAO.getStockReport();
        System.out.println("  - Stock Report: " + stockReport.size() + " items");
    }
}