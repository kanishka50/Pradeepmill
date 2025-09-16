package com.pradeepmill.models;

import java.util.Date;

public class Staff {
    
    // Private attributes (matching database table)
    private int staffId;
    private String employeeName;
    private String position;
    private String phone;
    private String address;
    private Date hireDate;
    private double monthlySalary;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    
    // Position Constants
    public static final String POSITION_MANAGER = "Mill Manager";
    public static final String POSITION_OPERATOR = "Machine Operator";
    public static final String POSITION_QUALITY_CONTROLLER = "Quality Controller";
    public static final String POSITION_ACCOUNTANT = "Accountant";
    public static final String POSITION_SUPERVISOR = "Supervisor";
    public static final String POSITION_MACHINE_OPERATOR = "Machine Operator"; 
    public static final String POSITION_HELPER = "Helper"; 
    
    
    // Default constructor
    public Staff() {
        this.status = "Active";
        this.monthlySalary = 0.0;
        this.hireDate = new Date();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Constructor with essential fields
    public Staff(String employeeName, String position, String phone, String address, double monthlySalary) {
        this();
        this.employeeName = employeeName;
        this.position = position;
        this.phone = phone;
        this.address = address;
        this.monthlySalary = monthlySalary;
    }
    
    // Constructor with all fields (for database retrieval)
    public Staff(int staffId, String employeeName, String position, String phone, 
                String address, Date hireDate, double monthlySalary, String status) {
        this.staffId = staffId;
        this.employeeName = employeeName;
        this.position = position;
        this.phone = phone;
        this.address = address;
        this.hireDate = hireDate;
        this.monthlySalary = monthlySalary;
        this.status = status;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Getters and Setters
    public int getStaffId() {
        return staffId;
    }
    
    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }
    
    public String getEmployeeName() {
        return employeeName;
    }
    
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
        this.updatedAt = new Date();
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
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
    
    public Date getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }
    
    public double getMonthlySalary() {
        return monthlySalary;
    }
    
    public void setMonthlySalary(double monthlySalary) {
        this.monthlySalary = monthlySalary;
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
    
    public boolean isManager() {
        return POSITION_MANAGER.equalsIgnoreCase(this.position);
    }
    
    public boolean isOperator() {
        return POSITION_OPERATOR.equalsIgnoreCase(this.position);
    }
    
    public double getYearlySalary() {
        return monthlySalary * 12;
    }
    
    // Validation methods
    public boolean isValid() {
        return employeeName != null && !employeeName.trim().isEmpty() &&
               position != null && !position.trim().isEmpty() &&
               monthlySalary > 0;
    }
    
    // toString method for debugging
    @Override
    public String toString() {
        return "Staff{" +
                "staffId=" + staffId +
                ", employeeName='" + employeeName + '\'' +
                ", position='" + position + '\'' +
                ", phone='" + phone + '\'' +
                ", monthlySalary=" + monthlySalary +
                ", status='" + status + '\'' +
                '}';
    }
    
    // equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Staff staff = (Staff) obj;
        return staffId == staff.staffId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(staffId);
    }
}