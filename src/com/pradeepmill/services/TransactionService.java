package com.pradeepmill.services;

import com.pradeepmill.dao.*;
import com.pradeepmill.models.*;
import java.util.List;
import java.util.Date;

public class TransactionService {
    
    private StockInventoryDAO stockDAO;
    private SupplierDAO supplierDAO;
    private CustomerDAO customerDAO;
    private ProductDAO productDAO;
    
    public TransactionService() {
        this.stockDAO = new StockInventoryDAO();
        this.supplierDAO = new SupplierDAO();
        this.customerDAO = new CustomerDAO();
        this.productDAO = new ProductDAO();
    }
    
    /**
     * Process a complete purchase transaction
     * This involves updating stock levels after purchase
     */
    public boolean processPurchaseTransaction(int supplierId, List<PurchaseItem> items) {
        try {
            // Validate supplier exists and is active
            Supplier supplier = supplierDAO.findSupplierById(supplierId);
            if (supplier == null || !supplier.isActive()) {
                return false;
            }
            
            // Create purchase order
            PurchaseOrder purchase = new PurchaseOrder();
            purchase.setPurchaseNumber(generatePurchaseNumber());
            purchase.setSupplierId(supplierId);
            purchase.setPurchaseDate(new Date());
            
            // Calculate totals
            double totalQuantity = 0;
            double totalAmount = 0;
            
            for (PurchaseItem item : items) {
                Product product = productDAO.findProductById(item.getProductId());
                if (product != null) {
                    totalQuantity += item.getQuantity();
                    totalAmount += item.getQuantity() * product.getUnitPrice();
                }
            }
            
            purchase.setTotalQuantity(totalQuantity);
            purchase.setTotalAmount(totalAmount);
            purchase.setCreatedBy("System"); // In real app, this would be current user
            
            // Process the purchase - update stock for each item
            boolean success = true;
            for (PurchaseItem item : items) {
                if (!stockDAO.updateStockQuantity(item.getProductId(), item.getQuantity())) {
                    success = false;
                    break;
                }
            }
            
            return success;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Process a complete sales transaction
     */
    public boolean processSalesTransaction(int customerId, List<SalesItem> items) {
        try {
            // Validate customer exists and is active
            Customer customer = customerDAO.findCustomerById(customerId);
            if (customer == null || !customer.isActive()) {
                return false;
            }
            
            // Check stock availability for all items first
            for (SalesItem item : items) {
                if (!stockDAO.isStockAvailable(item.getProductId(), item.getQuantity())) {
                    return false; // Insufficient stock
                }
            }
            
            // Create sales order
            SalesOrder sale = new SalesOrder();
            sale.setSaleNumber(generateSaleNumber());
            sale.setCustomerId(customerId);
            sale.setSaleDate(new Date());
            
            // Calculate totals
            double totalQuantity = 0;
            double totalAmount = 0;
            
            for (SalesItem item : items) {
                Product product = productDAO.findProductById(item.getProductId());
                if (product != null) {
                    totalQuantity += item.getQuantity();
                    totalAmount += item.getQuantity() * product.getUnitPrice();
                }
            }
            
            sale.setTotalQuantity(totalQuantity);
            sale.setTotalAmount(totalAmount);
            sale.setCreatedBy("System");
            
            // Process the sale - reduce stock for each item
            boolean success = true;
            for (SalesItem item : items) {
                // Reduce stock (negative quantity)
                if (!stockDAO.updateStockQuantity(item.getProductId(), -item.getQuantity())) {
                    success = false;
                    break;
                }
            }
            
            return success;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Validate purchase transaction before processing
     */
    public ValidationResult validatePurchaseTransaction(int supplierId, List<PurchaseItem> items) {
        ValidationResult result = new ValidationResult();
        
        try {
            // Check supplier
            Supplier supplier = supplierDAO.findSupplierById(supplierId);
            if (supplier == null) {
                result.addError("Supplier not found");
            } else if (!supplier.isActive()) {
                result.addError("Supplier is inactive");
            }
            
            // Check items
            if (items == null || items.isEmpty()) {
                result.addError("No items specified for purchase");
            } else {
                for (PurchaseItem item : items) {
                    Product product = productDAO.findProductById(item.getProductId());
                    if (product == null) {
                        result.addError("Product not found: ID " + item.getProductId());
                    } else if (!product.isActive()) {
                        result.addError("Product is inactive: " + product.getProductName());
                    }
                    
                    if (item.getQuantity() <= 0) {
                        result.addError("Invalid quantity for product: " + (product != null ? product.getProductName() : "ID " + item.getProductId()));
                    }
                }
            }
            
        } catch (Exception e) {
            result.addError("Validation error: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Validate sales transaction before processing
     */
    public ValidationResult validateSalesTransaction(int customerId, List<SalesItem> items) {
        ValidationResult result = new ValidationResult();
        
        try {
            // Check customer
            Customer customer = customerDAO.findCustomerById(customerId);
            if (customer == null) {
                result.addError("Customer not found");
            } else if (!customer.isActive()) {
                result.addError("Customer is inactive");
            }
            
            // Check items and stock availability
            if (items == null || items.isEmpty()) {
                result.addError("No items specified for sale");
            } else {
                for (SalesItem item : items) {
                    Product product = productDAO.findProductById(item.getProductId());
                    if (product == null) {
                        result.addError("Product not found: ID " + item.getProductId());
                        continue;
                    }
                    
                    if (!product.isActive()) {
                        result.addError("Product is inactive: " + product.getProductName());
                        continue;
                    }
                    
                    if (item.getQuantity() <= 0) {
                        result.addError("Invalid quantity for product: " + product.getProductName());
                        continue;
                    }
                    
                    // Check stock availability
                    if (!stockDAO.isStockAvailable(item.getProductId(), item.getQuantity())) {
                        StockInventory stock = stockDAO.getStockByProductId(item.getProductId());
                        double available = stock != null ? stock.getCurrentQuantity() : 0;
                        result.addError(String.format("Insufficient stock for %s. Required: %.2f, Available: %.2f", 
                                      product.getProductName(), item.getQuantity(), available));
                    }
                }
            }
            
        } catch (Exception e) {
            result.addError("Validation error: " + e.getMessage());
        }
        
        return result;
    }
    
    // Helper methods for generating transaction numbers
    private String generatePurchaseNumber() {
        return "PO" + System.currentTimeMillis();
    }
    
    private String generateSaleNumber() {
        return "SO" + System.currentTimeMillis();
    }
    
    // Inner class for validation results
    public static class ValidationResult {
        private List<String> errors = new java.util.ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public boolean isValid() {
            return errors.isEmpty();
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public String getErrorMessage() {
            return String.join("; ", errors);
        }
    }
    
    // Helper classes for transaction items (you may need to create these)
    public static class PurchaseItem {
        private int productId;
        private double quantity;
        
        public PurchaseItem(int productId, double quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
        
        public int getProductId() { return productId; }
        public double getQuantity() { return quantity; }
    }
    
    public static class SalesItem {
        private int productId;
        private double quantity;
        
        public SalesItem(int productId, double quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
        
        public int getProductId() { return productId; }
        public double getQuantity() { return quantity; }
    }
}