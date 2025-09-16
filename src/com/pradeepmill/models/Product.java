package com.pradeepmill.models;

import java.util.Date;

public class Product {
    
    // Private attributes (matching database table)
    private int productId;
    private String productName;
    private String productType;
    private String grade;
    private double unitPrice;
    private String unit;
    private String description;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    
    // Product Types Constants
    public static final String TYPE_RAW_PADDY = "Raw_Paddy";
    public static final String TYPE_FINISHED_RICE = "Finished_Rice";
    public static final String TYPE_BY_PRODUCT = "By_Product";
    
    // Units Constants
    public static final String UNIT_KG = "kg";
    public static final String UNIT_BAG = "bag";
    public static final String UNIT_TON = "ton";
    
    // Grade Constants
    public static final String GRADE_A = "A";
    public static final String GRADE_B = "B";
    public static final String GRADE_C = "C";
    public static final String GRADE_PREMIUM = "Premium";
    public static final String GRADE_REGULAR = "Regular";
    
    // Default constructor
    public Product() {
        this.status = "Active";
        this.unit = UNIT_KG;
        this.unitPrice = 0.0;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Constructor with essential fields
    public Product(String productName, String productType, String grade, double unitPrice) {
        this();
        this.productName = productName;
        this.productType = productType;
        this.grade = grade;
        this.unitPrice = unitPrice;
    }
    
    // Constructor with all fields (for database retrieval)
    public Product(int productId, String productName, String productType, String grade, 
                  double unitPrice, String unit, String description, String status) {
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.grade = grade;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.description = description;
        this.status = status;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Getters and Setters
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
        this.updatedAt = new Date();
    }
    
    public String getProductType() {
        return productType;
    }
    
    public void setProductType(String productType) {
        this.productType = productType;
        this.updatedAt = new Date();
    }
    
    public String getGrade() {
        return grade;
    }
    
    public void setGrade(String grade) {
        this.grade = grade;
        this.updatedAt = new Date();
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.updatedAt = new Date();
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
        this.updatedAt = new Date();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = new Date();
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = new Date();
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business logic methods
    public boolean isActive() {
        return "Active".equalsIgnoreCase(this.status);
    }
    
    public void activate() {
        setStatus("Active");
    }
    
    public void deactivate() {
        setStatus("Inactive");
    }
    
    public boolean isRawPaddy() {
        return TYPE_RAW_PADDY.equalsIgnoreCase(this.productType);
    }
    
    public boolean isFinishedRice() {
        return TYPE_FINISHED_RICE.equalsIgnoreCase(this.productType);
    }
    
    public boolean isByProduct() {
        return TYPE_BY_PRODUCT.equalsIgnoreCase(this.productType);
    }
    
    public boolean isPremiumGrade() {
        return GRADE_A.equalsIgnoreCase(this.grade) || GRADE_PREMIUM.equalsIgnoreCase(this.grade);
    }
    
    // Price calculation methods
    public double calculateAmount(double quantity) {
        return quantity * unitPrice;
    }
    
    // Validation methods
    public boolean isValid() {
        return productName != null && !productName.trim().isEmpty() &&
               productType != null && !productType.trim().isEmpty() &&
               isValidProductType() &&
               unitPrice >= 0;
    }
    
    private boolean isValidProductType() {
        return TYPE_RAW_PADDY.equalsIgnoreCase(productType) ||
               TYPE_FINISHED_RICE.equalsIgnoreCase(productType) ||
               TYPE_BY_PRODUCT.equalsIgnoreCase(productType);
    }
    
    // toString method for debugging
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productType='" + productType + '\'' +
                ", grade='" + grade + '\'' +
                ", unitPrice=" + unitPrice +
                ", unit='" + unit + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
    
    // equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return productId == product.productId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(productId);
    }
}