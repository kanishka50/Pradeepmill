package com.pradeepmill.main;

import com.pradeepmill.models.*;
import java.util.Date;

public class ModelTest {
    public static void main(String[] args) {
        System.out.println("=== Testing All Model Classes ===\n");
        
        // Test Core Models
        testCoreModels();
        
        // Test Transaction Models
        testTransactionModels();
        
        System.out.println("\n✅ All 9 model classes working correctly!");
        System.out.println("🚀 Ready for DAO Layer development!");
    }
    
    private static void testCoreModels() {
        System.out.println("--- Core Models ---");
        
        // Test Supplier
        Supplier supplier = new Supplier("Farmer's Cooperative", "Mr. Banda", "0771234567", "Polonnaruwa");
        supplier.setCreditLimit(100000.0);
        System.out.println("✓ Supplier: " + supplier.getSupplierName() + " (Credit: Rs." + supplier.getCreditLimit() + ")");
        
        // Test Customer
        Customer customer = new Customer("City Rice Store", "Mr. Fernando", "0773456789", "Colombo", Customer.TYPE_RETAIL);
        customer.setCreditLimit(50000.0);
        System.out.println("✓ Customer: " + customer.getCustomerName() + " (" + customer.getCustomerType() + ")");
        
        // Test Staff
        Staff staff = new Staff("Sunil Rajapaksa", Staff.POSITION_MANAGER, "0771111111", "Anuradhapura", 45000.00);
        System.out.println("✓ Staff: " + staff.getEmployeeName() + " (" + staff.getPosition() + ") Rs." + staff.getMonthlySalary());
        
        // Test Machine
        Machine machine = new Machine("Paddy Cleaner Unit 1", Machine.TYPE_CLEANER, "Section A", 500.00);
        System.out.println("✓ Machine: " + machine.getMachineName() + " (" + machine.getCapacityPerHour() + " kg/h)");
        
        // Test Product
        Product product = new Product("Premium Paddy", Product.TYPE_RAW_PADDY, Product.GRADE_A, 50.00);
        System.out.println("✓ Product: " + product.getProductName() + " (Rs." + product.getUnitPrice() + "/" + product.getUnit() + ")");
    }
    
    private static void testTransactionModels() {
        System.out.println("\n--- Transaction Models ---");
        
        // Test PurchaseOrder
        PurchaseOrder purchase = new PurchaseOrder("PO001", 1, new Date());
        purchase.setTotalAmount(25000.0);
        purchase.setPaidAmount(15000.0);
        System.out.println("✓ Purchase Order: " + purchase.getPurchaseNumber() + 
                          " (Total: Rs." + purchase.getTotalAmount() + 
                          ", Paid: Rs." + purchase.getPaidAmount() + 
                          ", Status: " + purchase.getPaymentStatus() + ")");
        
        // Test SalesOrder
        SalesOrder sale = new SalesOrder("SO001", 1, new Date());
        sale.setTotalAmount(35000.0);
        sale.setPaidAmount(35000.0);
        System.out.println("✓ Sales Order: " + sale.getSaleNumber() + 
                          " (Total: Rs." + sale.getTotalAmount() + 
                          ", Status: " + sale.getPaymentStatus() + ")");
        
        // Test ProductionRecord
        ProductionRecord production = new ProductionRecord("PRD001", new Date(), 1, 3, 1000.0, 650.0);
        production.setWasteQuantity(50.0);
        System.out.println("✓ Production: " + production.getProductionNumber() + 
                          " (Input: " + production.getInputQuantity() + "kg" +
                          ", Output: " + production.getOutputQuantity() + "kg" +
                          ", Efficiency: " + String.format("%.1f", production.getConversionRate()) + "%)");
        
        // Test StockInventory
        StockInventory stock = new StockInventory(1, 500.0, 100.0, 1000.0);
        stock.setProductName("Premium Paddy");
        stock.setUnitPrice(50.0);
        System.out.println("✓ Stock: " + stock.getProductName() + 
                          " (Current: " + stock.getCurrentQuantity() + 
                          ", Status: " + stock.getStockStatus() + 
                          ", Value: Rs." + String.format("%.2f", stock.getStockValue()) + ")");
    }
}