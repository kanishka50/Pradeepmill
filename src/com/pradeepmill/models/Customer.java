package com.pradeepmill.models;

import java.util.Date;

public class Customer {
    
    // Private attributes (matching database table)
    private int customerId;
    private String customerName;
    private String contactPerson;
    private String phone;
    private String address;
    private String email;
    private String customerType; // Wholesale, Retail, Direct
    private Date registrationDate;
    private String status;
    private double creditLimit;
    private Date createdAt;
    private Date updatedAt;
    
    // Customer Types Constants
    public static final String TYPE_WHOLESALE = "Wholesale";
    public static final String TYPE_RETAIL = "Retail";
    public static final String TYPE_DIRECT = "Direct";
    
    // Default constructor
    public Customer() {
        this.status = "Active";
        this.customerType = TYPE_RETAIL;
        this.creditLimit = 0.0;
        this.registrationDate = new Date();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Constructor with essential fields
    public Customer(String customerName, String contactPerson, String phone, String address, String customerType) {
        this();
        this.customerName = customerName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.address = address;
        this.customerType = customerType;
    }
    
    // Constructor with all fields (for database retrieval)
    public Customer(int customerId, String customerName, String contactPerson, String phone, 
                   String address, String email, String customerType, Date registrationDate, 
                   String status, double creditLimit) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.customerType = customerType;
        this.registrationDate = registrationDate;
        this.status = status;
        this.creditLimit = creditLimit;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
    
    public String getCustomerType() {
        return customerType;
    }
    
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
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
    
    public boolean isWholesale() {
        return TYPE_WHOLESALE.equalsIgnoreCase(this.customerType);
    }
    
    public boolean isRetail() {
        return TYPE_RETAIL.equalsIgnoreCase(this.customerType);
    }
    
    public boolean isDirect() {
        return TYPE_DIRECT.equalsIgnoreCase(this.customerType);
    }
    
    // Validation methods
    public boolean isValid() {
        return customerName != null && !customerName.trim().isEmpty() &&
               phone != null && !phone.trim().isEmpty() &&
               isValidCustomerType();
    }
    
    private boolean isValidCustomerType() {
        return TYPE_WHOLESALE.equalsIgnoreCase(customerType) ||
               TYPE_RETAIL.equalsIgnoreCase(customerType) ||
               TYPE_DIRECT.equalsIgnoreCase(customerType);
    }
    
    // toString method for debugging
    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", phone='" + phone + '\'' +
                ", customerType='" + customerType + '\'' +
                ", status='" + status + '\'' +
                ", creditLimit=" + creditLimit +
                '}';
    }
    
    // equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return customerId == customer.customerId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(customerId);
    }
}