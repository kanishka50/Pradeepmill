package com.pradeepmill.models;

import java.util.Date;

public class Machine {
    
    // Private attributes (matching database table)
    private int machineId;
    private String machineName;
    private String machineType;
    private String location;
    private Date installationDate;
    private double capacityPerHour; // in kg/hour
    private String status;
    private Date createdAt;
    private Date updatedAt;
    
    // Machine Types Constants
    public static final String TYPE_CLEANER = "Cleaner";
    public static final String TYPE_DEHUSKER = "De-husker";
    public static final String TYPE_POLISHER = "Polisher";
    public static final String TYPE_GRADER = "Grader";
    public static final String TYPE_PACKER = "Packer";
    
    // Machine Status Constants
    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_MAINTENANCE = "Maintenance";
    public static final String STATUS_BROKEN = "Broken";
    
    // Default constructor
    public Machine() {
        this.status = STATUS_ACTIVE;
        this.capacityPerHour = 0.0;
        this.installationDate = new Date();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Constructor with essential fields
    public Machine(String machineName, String machineType, String location, double capacityPerHour) {
        this();
        this.machineName = machineName;
        this.machineType = machineType;
        this.location = location;
        this.capacityPerHour = capacityPerHour;
    }
    
    // Constructor with all fields (for database retrieval)
    public Machine(int machineId, String machineName, String machineType, String location, 
                  Date installationDate, double capacityPerHour, String status) {
        this.machineId = machineId;
        this.machineName = machineName;
        this.machineType = machineType;
        this.location = location;
        this.installationDate = installationDate;
        this.capacityPerHour = capacityPerHour;
        this.status = status;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Getters and Setters
    public int getMachineId() {
        return machineId;
    }
    
    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }
    
    public String getMachineName() {
        return machineName;
    }
    
    public void setMachineName(String machineName) {
        this.machineName = machineName;
        this.updatedAt = new Date();
    }
    
    public String getMachineType() {
        return machineType;
    }
    
    public void setMachineType(String machineType) {
        this.machineType = machineType;
        this.updatedAt = new Date();
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
        this.updatedAt = new Date();
    }
    
    public Date getInstallationDate() {
        return installationDate;
    }
    
    public void setInstallationDate(Date installationDate) {
        this.installationDate = installationDate;
    }
    
    public double getCapacityPerHour() {
        return capacityPerHour;
    }
    
    public void setCapacityPerHour(double capacityPerHour) {
        this.capacityPerHour = capacityPerHour;
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
        return STATUS_ACTIVE.equalsIgnoreCase(this.status);
    }
    
    public boolean isInMaintenance() {
        return STATUS_MAINTENANCE.equalsIgnoreCase(this.status);
    }
    
    public boolean isBroken() {
        return STATUS_BROKEN.equalsIgnoreCase(this.status);
    }
    
    public boolean isOperational() {
        return isActive();
    }
    
    public void setToMaintenance() {
        setStatus(STATUS_MAINTENANCE);
    }
    
    public void setToBroken() {
        setStatus(STATUS_BROKEN);
    }
    
    public void setToActive() {
        setStatus(STATUS_ACTIVE);
    }
    
    public double getDailyCapacity() {
        return capacityPerHour * 8; // Assuming 8 hours work per day
    }
    
    // Validation methods
    public boolean isValid() {
        return machineName != null && !machineName.trim().isEmpty() &&
               machineType != null && !machineType.trim().isEmpty() &&
               isValidMachineType() &&
               capacityPerHour > 0;
    }
    
    private boolean isValidMachineType() {
        return TYPE_CLEANER.equalsIgnoreCase(machineType) ||
               TYPE_DEHUSKER.equalsIgnoreCase(machineType) ||
               TYPE_POLISHER.equalsIgnoreCase(machineType) ||
               TYPE_GRADER.equalsIgnoreCase(machineType) ||
               TYPE_PACKER.equalsIgnoreCase(machineType);
    }
    
    // toString method for debugging
    @Override
    public String toString() {
        return "Machine{" +
                "machineId=" + machineId +
                ", machineName='" + machineName + '\'' +
                ", machineType='" + machineType + '\'' +
                ", location='" + location + '\'' +
                ", capacityPerHour=" + capacityPerHour +
                ", status='" + status + '\'' +
                '}';
    }
    
    // equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Machine machine = (Machine) obj;
        return machineId == machine.machineId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(machineId);
    }
}