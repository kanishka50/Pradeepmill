package com.pradeepmill.services;

import com.pradeepmill.dao.*;
import com.pradeepmill.models.*;
import com.pradeepmill.database.DatabaseConnection;
import java.util.List;
import java.util.Date;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class TransactionService {
    
    private StockInventoryDAO stockDAO;
    private SupplierDAO supplierDAO;
    private CustomerDAO customerDAO;
    private ProductDAO productDAO;
    private PurchaseOrderDAO purchaseOrderDAO;
    private SalesOrderDAO salesOrderDAO;
    
    public TransactionService() {
        this.stockDAO = new StockInventoryDAO();
        this.supplierDAO = new SupplierDAO();
        this.customerDAO = new CustomerDAO();
        this.productDAO = new ProductDAO();
        this.purchaseOrderDAO = new PurchaseOrderDAO();
        this.salesOrderDAO = new SalesOrderDAO();
    }
    
    /**
     * FIXED: Process a complete purchase transaction with proper stock updates
     * This involves creating the purchase order AND updating stock levels
     */
    public boolean processPurchaseTransaction(int supplierId, List<PurchaseItem> items, String notes, String createdBy) {
        Connection conn = null;
        try {
            // Start database transaction
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Validate supplier exists and is active
            Supplier supplier = supplierDAO.findSupplierById(supplierId);
            if (supplier == null || !supplier.isActive()) {
                throw new Exception("Supplier not found or inactive: " + supplierId);
            }
            
            // Validate all products exist and calculate totals
            double totalQuantity = 0;
            double totalAmount = 0;
            
            for (PurchaseItem item : items) {
                Product product = productDAO.findProductById(item.getProductId());
                if (product == null || !"Active".equals(product.getStatus())) {
                    throw new Exception("Product not found or inactive: " + item.getProductId());
                }
                
                // Set unit price from product if not set in item
                if (item.getUnitPrice() <= 0) {
                    item.setUnitPrice(product.getUnitPrice());
                }
                
                totalQuantity += item.getQuantity();
                totalAmount += item.getQuantity() * item.getUnitPrice();
            }
            
            // Create purchase order
            PurchaseOrder purchase = new PurchaseOrder();
            purchase.setPurchaseNumber(generatePurchaseNumber());
            purchase.setSupplierId(supplierId);
            purchase.setPurchaseDate(new Date());
            purchase.setTotalQuantity(totalQuantity);
            purchase.setTotalAmount(totalAmount);
            purchase.setPaidAmount(0.0); // Initially unpaid
            purchase.setPaymentStatus(PurchaseOrder.PAYMENT_PENDING);
            purchase.setNotes(notes);
            purchase.setCreatedBy(createdBy);
            
            // Insert purchase order
            boolean orderCreated = purchaseOrderDAO.insertPurchaseOrder(purchase);
            if (!orderCreated) {
                throw new Exception("Failed to create purchase order");
            }
            
            // Insert purchase items
            boolean itemsInserted = purchaseOrderDAO.insertPurchaseItems(purchase.getPurchaseId(), items);
            if (!itemsInserted) {
                throw new Exception("Failed to insert purchase items");
            }
            
            // CRITICAL FIX: Update stock quantities for each purchased item
            for (PurchaseItem item : items) {
                boolean stockUpdated = stockDAO.updateStockQuantity(
                    item.getProductId(), 
                    item.getQuantity() // Positive quantity increases stock
                );
                
                if (!stockUpdated) {
                    throw new Exception("Failed to update stock for product: " + item.getProductId());
                }
                
                System.out.println("✓ Stock updated: Product " + item.getProductId() + 
                                 " increased by " + item.getQuantity());
            }
            
            // Commit transaction
            conn.commit();
            System.out.println("✅ Purchase transaction completed successfully: " + purchase.getPurchaseNumber());
            return true;
            
        } catch (Exception e) {
            // Rollback transaction on error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("❌ Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Restore auto-commit
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * FIXED: Process a complete sales transaction with proper stock reduction
     */
    public boolean processSalesTransaction(int customerId, List<SalesItem> items, String notes, String createdBy) {
        Connection conn = null;
        try {
            // Start database transaction
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Validate customer exists and is active
            Customer customer = customerDAO.findCustomerById(customerId);
            if (customer == null || !customer.isActive()) {
                throw new Exception("Customer not found or inactive: " + customerId);
            }
            
            // CRITICAL: Check stock availability for all items FIRST
            for (SalesItem item : items) {
                if (!stockDAO.isStockAvailable(item.getProductId(), item.getQuantity())) {
                    Product product = productDAO.findProductById(item.getProductId());
                    String productName = product != null ? product.getProductName() : "Product " + item.getProductId();
                    throw new Exception("Insufficient stock for " + productName + ". Required: " + 
                                      item.getQuantity() + ", Available: " + 
                                      stockDAO.getStockByProductId(item.getProductId()).getCurrentQuantity());
                }
            }
            
            // Calculate totals
            double totalQuantity = 0;
            double totalAmount = 0;
            
            for (SalesItem item : items) {
                Product product = productDAO.findProductById(item.getProductId());
                if (product == null || !"Active".equals(product.getStatus())) {
                    throw new Exception("Product not found or inactive: " + item.getProductId());
                }
                
                // Set unit price from product if not set in item
                if (item.getUnitPrice() <= 0) {
                    item.setUnitPrice(product.getUnitPrice());
                }
                
                totalQuantity += item.getQuantity();
                totalAmount += item.getQuantity() * item.getUnitPrice();
            }
            
            // Create sales order
            SalesOrder sale = new SalesOrder();
            sale.setSaleNumber(generateSaleNumber());
            sale.setCustomerId(customerId);
            sale.setSaleDate(new Date());
            sale.setTotalQuantity(totalQuantity);
            sale.setTotalAmount(totalAmount);
            sale.setPaidAmount(0.0); // Initially unpaid
            sale.setPaymentStatus(SalesOrder.PAYMENT_PENDING);
            sale.setNotes(notes);
            sale.setCreatedBy(createdBy);
            
            // Insert sales order
            boolean orderCreated = salesOrderDAO.insertSalesOrder(sale);
            if (!orderCreated) {
                throw new Exception("Failed to create sales order");
            }
            
            // Insert sales items
            boolean itemsInserted = salesOrderDAO.insertSalesItems(sale.getSaleId(), items);
            if (!itemsInserted) {
                throw new Exception("Failed to insert sales items");
            }
            
            // CRITICAL FIX: Reduce stock quantities for each sold item
            for (SalesItem item : items) {
                boolean stockReduced = stockDAO.updateStockQuantity(
                    item.getProductId(), 
                    -item.getQuantity() // Negative quantity reduces stock
                );
                
                if (!stockReduced) {
                    throw new Exception("Failed to reduce stock for product: " + item.getProductId());
                }
                
                System.out.println("✓ Stock reduced: Product " + item.getProductId() + 
                                 " decreased by " + item.getQuantity());
            }
            
            // Commit transaction
            conn.commit();
            System.out.println("✅ Sales transaction completed successfully: " + sale.getSaleNumber());
            return true;
            
        } catch (Exception e) {
            // Rollback transaction on error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("❌ Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Restore auto-commit
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * FIXED: Process production transaction (converts raw paddy to rice)
     * This reduces raw paddy stock and increases finished rice stock
     */
    public boolean processProductionTransaction(int rawProductId, int finishedProductId, 
                                              double inputQuantity, double outputQuantity, 
                                              int machineId, int operatorId, String notes) {
        Connection conn = null;
        try {
            // Start database transaction
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Validate raw material availability
            if (!stockDAO.isStockAvailable(rawProductId, inputQuantity)) {
                throw new Exception("Insufficient raw material stock. Required: " + inputQuantity);
            }
            
            // Create production record
            ProductionRecord production = new ProductionRecord();
            production.setProductionNumber(generateProductionNumber());
            production.setProductionDate(new Date());
            production.setRawProductId(rawProductId);
            production.setFinishedProductId(finishedProductId);
            production.setInputQuantity(inputQuantity);
            production.setOutputQuantity(outputQuantity);
            production.setMachineId(machineId);
            production.setOperatorId(operatorId);
            production.setNotes(notes);
            
            // Calculate conversion rate
            if (inputQuantity > 0) {
                production.setConversionRate((outputQuantity / inputQuantity) * 100);
            }
            
            // Insert production record
            ProductionRecordDAO productionDAO = new ProductionRecordDAO();
            boolean productionCreated = productionDAO.insertProductionRecord(production);
            if (!productionCreated) {
                throw new Exception("Failed to create production record");
            }
            
            // CRITICAL FIX: Update stock levels
            // Reduce raw material stock
            boolean rawStockReduced = stockDAO.updateStockQuantity(rawProductId, -inputQuantity);
            if (!rawStockReduced) {
                throw new Exception("Failed to reduce raw material stock");
            }
            
            // Increase finished product stock
            boolean finishedStockIncreased = stockDAO.updateStockQuantity(finishedProductId, outputQuantity);
            if (!finishedStockIncreased) {
                throw new Exception("Failed to increase finished product stock");
            }
            
            // Commit transaction
            conn.commit();
            System.out.println("✅ Production transaction completed: " + production.getProductionNumber());
            return true;
            
        } catch (Exception e) {
            // Rollback transaction on error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("❌ Production transaction rolled back: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Restore auto-commit
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Generate unique purchase number
     */
    private String generatePurchaseNumber() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateStr = dateFormat.format(new Date());
        long timestamp = System.currentTimeMillis() % 10000; // Last 4 digits
        return "PO" + dateStr + String.format("%04d", timestamp);
    }
    
    /**
     * Generate unique sale number
     */
    private String generateSaleNumber() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateStr = dateFormat.format(new Date());
        long timestamp = System.currentTimeMillis() % 10000; // Last 4 digits
        return "SO" + dateStr + String.format("%04d", timestamp);
    }
    
    /**
     * Generate unique production number
     */
    private String generateProductionNumber() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateStr = dateFormat.format(new Date());
        long timestamp = System.currentTimeMillis() % 10000; // Last 4 digits
        return "PRD" + dateStr + String.format("%04d", timestamp);
    }
    
    /**
     * Check if stock is sufficient for a sale
     */
    public boolean validateStockForSale(List<SalesItem> items) {
        try {
            for (SalesItem item : items) {
                if (!stockDAO.isStockAvailable(item.getProductId(), item.getQuantity())) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get current stock status for a product
     */
    public StockInventory getProductStock(int productId) {
        try {
            return stockDAO.getStockByProductId(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}