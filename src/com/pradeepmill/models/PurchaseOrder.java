package com.pradeepmill.models;

import java.util.Date;

public class PurchaseOrder {
    
    // Private attributes (matching database table)
    private int purchaseId;
    private String purchaseNumber;
    private int supplierId;
    private Date purchaseDate;
    private double totalQuantity;
    private double totalAmount;
    private double paidAmount;
    private String paymentStatus; // Pending, Partial, Paid
    private String notes;
    private String createdBy;
    private Date createdAt;
    private Date updatedAt;
    
    // Related objects (for display purposes)
    private String supplierName; // Not in database, loaded separately
    
    // Payment Status Constants
    public static final String PAYMENT_PENDING = "Pending";
    public static final String PAYMENT_PARTIAL = "Partial";
    public static final String PAYMENT_PAID = "Paid";
    
    // Default constructor
    public PurchaseOrder() {
        this.purchaseDate = new Date();
        this.totalQuantity = 0.0;
        this.totalAmount = 0.0;
        this.paidAmount = 0.0;
        this.paymentStatus = PAYMENT_PENDING;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Constructor with essential fields
    public PurchaseOrder(String purchaseNumber, int supplierId, Date purchaseDate) {
        this();
        this.purchaseNumber = purchaseNumber;
        this.supplierId = supplierId;
        this.purchaseDate = purchaseDate;
    }
    
    // Constructor with all fields (for database retrieval)
    public PurchaseOrder(int purchaseId, String purchaseNumber, int supplierId, Date purchaseDate,
                        double totalQuantity, double totalAmount, double paidAmount, 
                        String paymentStatus, String notes, String createdBy) {
        this.purchaseId = purchaseId;
        this.purchaseNumber = purchaseNumber;
        this.supplierId = supplierId;
        this.purchaseDate = purchaseDate;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.paymentStatus = paymentStatus;
        this.notes = notes;
        this.createdBy = createdBy;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Getters and Setters
    public int getPurchaseId() {
        return purchaseId;
    }
    
    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }
    
    public String getPurchaseNumber() {
        return purchaseNumber;
    }
    
    public void setPurchaseNumber(String purchaseNumber) {
        this.purchaseNumber = purchaseNumber;
        this.updatedAt = new Date();
    }
    
    public int getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
        this.updatedAt = new Date();
    }
    
    public Date getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
        this.updatedAt = new Date();
    }
    
    public double getTotalQuantity() {
        return totalQuantity;
    }
    
    public void setTotalQuantity(double totalQuantity) {
        this.totalQuantity = totalQuantity;
        this.updatedAt = new Date();
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        this.updatedAt = new Date();
    }
    
    public double getPaidAmount() {
        return paidAmount;
    }
    
    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
        updatePaymentStatus();
        this.updatedAt = new Date();
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
        this.updatedAt = new Date();
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = new Date();
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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
    public double getOutstandingAmount() {
        return totalAmount - paidAmount;
    }
    
    public boolean isFullyPaid() {
        return paidAmount >= totalAmount && totalAmount > 0;
    }
    
    public boolean isPending() {
        return PAYMENT_PENDING.equalsIgnoreCase(this.paymentStatus);
    }
    
    public boolean isPartiallyPaid() {
        return PAYMENT_PARTIAL.equalsIgnoreCase(this.paymentStatus);
    }
    
    public double getPaymentPercentage() {
        if (totalAmount <= 0) return 0;
        return (paidAmount / totalAmount) * 100;
    }
    
    // Auto-update payment status based on amounts
    private void updatePaymentStatus() {
        if (paidAmount <= 0) {
            this.paymentStatus = PAYMENT_PENDING;
        } else if (paidAmount >= totalAmount) {
            this.paymentStatus = PAYMENT_PAID;
        } else {
            this.paymentStatus = PAYMENT_PARTIAL;
        }
    }
    
    // Generate purchase number (can be customized)
    public static String generatePurchaseNumber() {
        return "PO" + System.currentTimeMillis();
    }
    
    // Validation methods
    public boolean isValid() {
        return purchaseNumber != null && !purchaseNumber.trim().isEmpty() &&
               supplierId > 0 &&
               purchaseDate != null &&
               totalAmount >= 0;
    }
    
    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "purchaseId=" + purchaseId +
                ", purchaseNumber='" + purchaseNumber + '\'' +
                ", supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                ", purchaseDate=" + purchaseDate +
                ", totalAmount=" + totalAmount +
                ", paidAmount=" + paidAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PurchaseOrder that = (PurchaseOrder) obj;
        return purchaseId == that.purchaseId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(purchaseId);
    }
}