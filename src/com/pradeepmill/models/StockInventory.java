package com.pradeepmill.models;

import java.util.Date;

public class StockInventory {
    
    // Private attributes (matching database table)
    private int stockId;
    private int productId;
    private double currentQuantity;
    private double minimumLevel;
    private double maximumLevel;
    private Date lastUpdated;
    
    // Related objects (for display purposes)
    private String productName;
    private String productType;
    private double unitPrice;
    private String unit;
    
    // Stock Status Constants
    public static final String STATUS_NORMAL = "Normal";
    public static final String STATUS_LOW_STOCK = "Low Stock";
    public static final String STATUS_OUT_OF_STOCK = "Out of Stock";
    public static final String STATUS_OVERSTOCK = "Overstock";
    
    // Default constructor
    public StockInventory() {
        this.currentQuantity = 0.0;
        this.minimumLevel = 0.0;
        this.maximumLevel = 0.0;
        this.lastUpdated = new Date();
    }
    
    // Constructor with essential fields
    public StockInventory(int productId, double currentQuantity, double minimumLevel, double maximumLevel) {
        this();
        this.productId = productId;
        this.currentQuantity = currentQuantity;
        this.minimumLevel = minimumLevel;
        this.maximumLevel = maximumLevel;
    }
    
    // Constructor with all fields (for database retrieval)
    public StockInventory(int stockId, int productId, double currentQuantity, 
                         double minimumLevel, double maximumLevel, Date lastUpdated) {
        this.stockId = stockId;
        this.productId = productId;
        this.currentQuantity = currentQuantity;
        this.minimumLevel = minimumLevel;
        this.maximumLevel = maximumLevel;
        this.lastUpdated = lastUpdated;
    }
    
    // Getters and Setters
    public int getStockId() {
        return stockId;
    }
    
    public void setStockId(int stockId) {
        this.stockId = stockId;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public double getCurrentQuantity() {
        return currentQuantity;
    }
    
    public void setCurrentQuantity(double currentQuantity) {
        this.currentQuantity = currentQuantity;
        this.lastUpdated = new Date();
    }
    
    public double getMinimumLevel() {
        return minimumLevel;
    }
    
    public void setMinimumLevel(double minimumLevel) {
        this.minimumLevel = minimumLevel;
    }
    
    public double getMaximumLevel() {
        return maximumLevel;
    }
    
    public void setMaximumLevel(double maximumLevel) {
        this.maximumLevel = maximumLevel;
    }
    
    public Date getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    // Related object getters/setters
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductType() {
        return productType;
    }
    
    public void setProductType(String productType) {
        this.productType = productType;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    // Business logic methods
    public String getStockStatus() {
        if (currentQuantity <= 0) {
            return STATUS_OUT_OF_STOCK;
        } else if (currentQuantity <= minimumLevel) {
            return STATUS_LOW_STOCK;
        } else if (currentQuantity >= maximumLevel && maximumLevel > 0) {
            return STATUS_OVERSTOCK;
        } else {
            return STATUS_NORMAL;
        }
    }
    
    public boolean isLowStock() {
        return currentQuantity <= minimumLevel && currentQuantity > 0;
    }
    
    public boolean isOutOfStock() {
        return currentQuantity <= 0;
    }
    
    public boolean isOverstock() {
        return maximumLevel > 0 && currentQuantity >= maximumLevel;
    }
    
    public boolean isNormalStock() {
        return currentQuantity > minimumLevel && (maximumLevel <= 0 || currentQuantity < maximumLevel);
    }
    
    public double getStockValue() {
        return currentQuantity * unitPrice;
    }
    
    public double getReorderQuantity() {
        if (maximumLevel > 0) {
            return maximumLevel - currentQuantity;
        }
        return minimumLevel * 2 - currentQuantity; // Reorder to double minimum level
    }
    
    public boolean needsReorder() {
        return isLowStock() || isOutOfStock();
    }
    
    // Stock movement methods
    public void addStock(double quantity) {
        if (quantity > 0) {
            this.currentQuantity += quantity;
            this.lastUpdated = new Date();
        }
    }
    
    public boolean removeStock(double quantity) {
        if (quantity > 0 && quantity <= currentQuantity) {
            this.currentQuantity -= quantity;
            this.lastUpdated = new Date();
            return true;
        }
        return false; // Insufficient stock
    }
    
    public boolean canFulfillOrder(double requestedQuantity) {
        return currentQuantity >= requestedQuantity;
    }
    
    // Validation methods
    public boolean isValid() {
        return productId > 0 && currentQuantity >= 0 && minimumLevel >= 0;
    }
    
    @Override
    public String toString() {
        return "StockInventory{" +
                "stockId=" + stockId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", currentQuantity=" + currentQuantity +
                ", minimumLevel=" + minimumLevel +
                ", maximumLevel=" + maximumLevel +
                ", stockStatus='" + getStockStatus() + '\'' +
                ", stockValue=" + getStockValue() +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StockInventory that = (StockInventory) obj;
        return stockId == that.stockId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(stockId);
    }
}