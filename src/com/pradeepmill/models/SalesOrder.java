package com.pradeepmill.models;

import java.util.Date;

public class SalesOrder {
    
    // Private attributes (matching database table)
    private int saleId;
    private String saleNumber;
    private int customerId;
    private Date saleDate;
    private double totalQuantity;
    private double totalAmount;
    private double paidAmount;
    private String paymentStatus; // Pending, Partial, Paid
    private String notes;
    private String createdBy;
    private Date createdAt;
    private Date updatedAt;
    
    // Related objects (for display purposes)
    private String customerName; // Not in database, loaded separately
    
    // Payment Status Constants
    public static final String PAYMENT_PENDING = "Pending";
    public static final String PAYMENT_PARTIAL = "Partial";
    public static final String PAYMENT_PAID = "Paid";
    
    // Default constructor
    public SalesOrder() {
        this.saleDate = new Date();
        this.totalQuantity = 0.0;
        this.totalAmount = 0.0;
        this.paidAmount = 0.0;
        this.paymentStatus = PAYMENT_PENDING;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Constructor with essential fields
    public SalesOrder(String saleNumber, int customerId, Date saleDate) {
        this();
        this.saleNumber = saleNumber;
        this.customerId = customerId;
        this.saleDate = saleDate;
    }
    
    // Constructor with all fields (for database retrieval)
    public SalesOrder(int saleId, String saleNumber, int customerId, Date saleDate,
                     double totalQuantity, double totalAmount, double paidAmount, 
                     String paymentStatus, String notes, String createdBy) {
        this.saleId = saleId;
        this.saleNumber = saleNumber;
        this.customerId = customerId;
        this.saleDate = saleDate;
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
    public int getSaleId() {
        return saleId;
    }
    
    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }
    
    public String getSaleNumber() {
        return saleNumber;
    }
    
    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
        this.updatedAt = new Date();
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
        this.updatedAt = new Date();
    }
    
    public Date getSaleDate() {
        return saleDate;
    }
    
    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
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
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
    
    // Generate sales number (can be customized)
    public static String generateSaleNumber() {
        return "SO" + System.currentTimeMillis();
    }
    
    // Validation methods
    public boolean isValid() {
        return saleNumber != null && !saleNumber.trim().isEmpty() &&
               customerId > 0 &&
               saleDate != null &&
               totalAmount >= 0;
    }
    
    @Override
    public String toString() {
        return "SalesOrder{" +
                "saleId=" + saleId +
                ", saleNumber='" + saleNumber + '\'' +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", saleDate=" + saleDate +
                ", totalAmount=" + totalAmount +
                ", paidAmount=" + paidAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SalesOrder that = (SalesOrder) obj;
        return saleId == that.saleId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(saleId);
    }
}