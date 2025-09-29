package com.pradeepmill.models;

import java.util.Date;

public class PaymentRecord {
    private int paymentId;
    private int purchaseId;
    private double paymentAmount;
    private String paymentMethod;
    private String referenceNumber;
    private Date paymentDate;
    private String notes;
    private Date createdAt;
    
    // Constructors
    public PaymentRecord() {}
    
    public PaymentRecord(int purchaseId, double paymentAmount, String paymentMethod, Date paymentDate) {
        this.purchaseId = purchaseId;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
    }
    
    // Getters and Setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    
    public int getPurchaseId() { return purchaseId; }
    public void setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }
    
    public double getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(double paymentAmount) { this.paymentAmount = paymentAmount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "PaymentRecord{" +
                "paymentId=" + paymentId +
                ", purchaseId=" + purchaseId +
                ", paymentAmount=" + paymentAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentDate=" + paymentDate +
                '}';
    }
}