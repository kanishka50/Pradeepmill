package com.pradeepmill.models;

import java.util.Date;

public class Supplier {
    
    // Private attributes (matching database table)
    private int supplierId;
    private String supplierName;
    private String contactPerson;
    private String phone;
    private String address;
    private String email;
    private Date registrationDate;
    private String status;
    private double creditLimit;
    private Date createdAt;
    private Date updatedAt;
    
    // Default constructor
    public Supplier() {
        this.status = "Active";
        this.creditLimit = 0.0;
        this.registrationDate = new Date();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Constructor with essential fields
    public Supplier(String supplierName, String contactPerson, String phone, String address) {
        this();
        this.supplierName = supplierName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.address = address;
    }
    
    // Constructor with all fields (for database retrieval)
    public Supplier(int supplierId, String supplierName, String contactPerson, String phone, 
                   String address, String email, Date registrationDate, String status, double creditLimit) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.registrationDate = registrationDate;
        this.status = status;
        this.creditLimit = creditLimit;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Getters and Setters
    public int getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
        this.updatedAt = new Date();
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
        this.updatedAt = new Date();
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
        this.updatedAt = new Date();
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
        this.updatedAt = new Date();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = new Date();
    }
    
    public Date getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = new Date();
    }
    
    public double getCreditLimit() {
        return creditLimit;
    }
    
    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
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
    
    // Validation methods
    public boolean isValid() {
        return supplierName != null && !supplierName.trim().isEmpty() &&
               phone != null && !phone.trim().isEmpty();
    }
    
    // toString method for debugging
    @Override
    public String toString() {
        return "Supplier{" +
                "supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                ", creditLimit=" + creditLimit +
                '}';
    }
    
    // equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Supplier supplier = (Supplier) obj;
        return supplierId == supplier.supplierId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(supplierId);
    }
}