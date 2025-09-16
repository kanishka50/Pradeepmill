package com.pradeepmill.models;

public class SalesItem {
    
    private int itemId;
    private int saleId;
    private int productId;
    private String productName;
    private String productType;
    private double quantity;
    private double unitPrice;
    private double totalPrice;
    private String unit;
    
    // Constructors
    public SalesItem() {
        this.quantity = 0.0;
        this.unitPrice = 0.0;
        this.totalPrice = 0.0;
    }
    
    public SalesItem(int productId, double quantity, double unitPrice) {
        this();
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }
    
    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    
    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }
    
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { 
        this.quantity = quantity;
        this.totalPrice = this.quantity * this.unitPrice;
    }
    
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { 
        this.unitPrice = unitPrice;
        this.totalPrice = this.quantity * this.unitPrice;
    }
    
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    // Business logic
    public void calculateTotal() {
        this.totalPrice = this.quantity * this.unitPrice;
    }
    
    public boolean isValid() {
        return productId > 0 && quantity > 0 && unitPrice > 0;
    }
    
    @Override
    public String toString() {
        return "SalesItem{" +
                "productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }
}