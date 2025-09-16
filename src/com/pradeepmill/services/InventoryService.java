package com.pradeepmill.services;

import com.pradeepmill.dao.*;
import com.pradeepmill.models.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InventoryService {
    
    private StockInventoryDAO stockDAO;
    private ProductDAO productDAO;
    
    public InventoryService() {
        this.stockDAO = new StockInventoryDAO();
        this.productDAO = new ProductDAO();
    }
    
    /**
     * Add a new product and initialize its stock
     */
    public boolean addProductWithInitialStock(Product product, double initialQuantity, 
                                            double minimumLevel, double maximumLevel) {
        try {
            // Add product first
            if (productDAO.insertProduct(product)) {
                // Initialize stock for the new product
                StockInventory stock = new StockInventory(product.getProductId(), initialQuantity, 
                                                        minimumLevel, maximumLevel);
                return initializeProductStock(stock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Initialize stock for a product
     */
    private boolean initializeProductStock(StockInventory stock) {
        // This would typically be handled by database triggers or manual initialization
        // For now, we'll assume stock is initialized when product is created
        return true;
    }
    
    /**
     * Update stock after purchase
     */
    public boolean updateStockAfterPurchase(int productId, double quantity) {
        try {
            return stockDAO.updateStockQuantity(productId, quantity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update stock after sales (reduce stock)
     */
    public boolean updateStockAfterSales(int productId, double quantity) {
        try {
            // Check if sufficient stock is available
            if (!stockDAO.isStockAvailable(productId, quantity)) {
                return false;
            }
            
            // Reduce stock (negative quantity)
            return stockDAO.updateStockQuantity(productId, -quantity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Check stock availability for sale
     */
    public boolean isStockSufficientForSale(int productId, double requiredQuantity) {
        try {
            return stockDAO.isStockAvailable(productId, requiredQuantity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all products with current stock information
     */
    public List<StockInventory> getAllProductsWithStock() {
        try {
            return stockDAO.getAllStockItems();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Return empty list on error
        }
    }
    
    /**
     * Get products by type with stock info
     */
    public List<StockInventory> getProductsByTypeWithStock(String productType) {
        try {
            return stockDAO.getStockByProductType(productType);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Get inventory alerts (low stock, out of stock)
     */
    public Map<String, List<StockInventory>> getInventoryAlerts() {
        Map<String, List<StockInventory>> alerts = new HashMap<>();
        
        try {
            alerts.put("lowStock", stockDAO.getLowStockItems());
            alerts.put("outOfStock", stockDAO.getOutOfStockItems());
        } catch (Exception e) {
            e.printStackTrace();
            alerts.put("lowStock", List.of());
            alerts.put("outOfStock", List.of());
        }
        
        return alerts;
    }
    
    /**
     * Get inventory valuation summary
     */
    public Map<String, Double> getInventoryValuation() {
        Map<String, Double> valuation = new HashMap<>();
        
        try {
            valuation.put("totalValue", stockDAO.getTotalStockValue());
            valuation.put("rawPaddyValue", stockDAO.getStockValueByType(Product.TYPE_RAW_PADDY));
            valuation.put("finishedRiceValue", stockDAO.getStockValueByType(Product.TYPE_FINISHED_RICE));
            valuation.put("byProductValue", stockDAO.getStockValueByType(Product.TYPE_BY_PRODUCT));
        } catch (Exception e) {
            e.printStackTrace();
            valuation.put("totalValue", 0.0);
            valuation.put("rawPaddyValue", 0.0);
            valuation.put("finishedRiceValue", 0.0);
            valuation.put("byProductValue", 0.0);
        }
        
        return valuation;
    }
    
    /**
     * Update stock levels for a product
     */
    public boolean updateStockLevels(int productId, double minimumLevel, double maximumLevel) {
        try {
            return stockDAO.updateStockLevels(productId, minimumLevel, maximumLevel);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get reorder recommendations
     */
    public List<Map<String, Object>> getReorderRecommendations() {
        List<Map<String, Object>> recommendations = new java.util.ArrayList<>();
        
        try {
            List<StockInventory> lowStockItems = stockDAO.getLowStockItems();
            
            for (StockInventory stock : lowStockItems) {
                Map<String, Object> recommendation = new HashMap<>();
                recommendation.put("productId", stock.getProductId());
                recommendation.put("productName", stock.getProductName());
                recommendation.put("currentQuantity", stock.getCurrentQuantity());
                recommendation.put("minimumLevel", stock.getMinimumLevel());
                recommendation.put("recommendedOrderQuantity", stock.getReorderQuantity());
                recommendation.put("urgency", stock.isOutOfStock() ? "High" : "Medium");
                
                recommendations.add(recommendation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return recommendations;
    }
}