package com.pradeepmill.models;

import java.util.Date;

public class ProductionRecord {
    
    // Private attributes (matching database table)
    private int productionId;
    private String productionNumber;
    private Date productionDate;
    private int rawProductId; // Paddy type used
    private int finishedProductId; // Rice type produced
    private double inputQuantity; // Paddy used (kg)
    private double outputQuantity; // Rice produced (kg)
    private double conversionRate; // Output/Input ratio
    private double wasteQuantity;
    private int machineId;
    private int operatorId;
    private String notes;
    private Date createdAt;
    private Date updatedAt;
    
    // Related objects (for display purposes)
    private String rawProductName;
    private String finishedProductName;
    private String machineName;
    private String operatorName;
    
    // Default constructor
    public ProductionRecord() {
        this.productionDate = new Date();
        this.inputQuantity = 0.0;
        this.outputQuantity = 0.0;
        this.conversionRate = 0.0;
        this.wasteQuantity = 0.0;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Constructor with essential fields
    public ProductionRecord(String productionNumber, Date productionDate, int rawProductId, 
                           int finishedProductId, double inputQuantity, double outputQuantity) {
        this();
        this.productionNumber = productionNumber;
        this.productionDate = productionDate;
        this.rawProductId = rawProductId;
        this.finishedProductId = finishedProductId;
        this.inputQuantity = inputQuantity;
        this.outputQuantity = outputQuantity;
        calculateConversionRate();
    }
    
    // Constructor with all fields (for database retrieval)
    public ProductionRecord(int productionId, String productionNumber, Date productionDate,
                           int rawProductId, int finishedProductId, double inputQuantity,
                           double outputQuantity, double conversionRate, double wasteQuantity,
                           int machineId, int operatorId, String notes) {
        this.productionId = productionId;
        this.productionNumber = productionNumber;
        this.productionDate = productionDate;
        this.rawProductId = rawProductId;
        this.finishedProductId = finishedProductId;
        this.inputQuantity = inputQuantity;
        this.outputQuantity = outputQuantity;
        this.conversionRate = conversionRate;
        this.wasteQuantity = wasteQuantity;
        this.machineId = machineId;
        this.operatorId = operatorId;
        this.notes = notes;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Getters and Setters
    public int getProductionId() {
        return productionId;
    }
    
    public void setProductionId(int productionId) {
        this.productionId = productionId;
    }
    
    public String getProductionNumber() {
        return productionNumber;
    }
    
    public void setProductionNumber(String productionNumber) {
        this.productionNumber = productionNumber;
        this.updatedAt = new Date();
    }
    
    public Date getProductionDate() {
        return productionDate;
    }
    
    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
        this.updatedAt = new Date();
    }
    
    public int getRawProductId() {
        return rawProductId;
    }
    
    public void setRawProductId(int rawProductId) {
        this.rawProductId = rawProductId;
        this.updatedAt = new Date();
    }
    
    public int getFinishedProductId() {
        return finishedProductId;
    }
    
    public void setFinishedProductId(int finishedProductId) {
        this.finishedProductId = finishedProductId;
        this.updatedAt = new Date();
    }
    
    public double getInputQuantity() {
        return inputQuantity;
    }
    
    public void setInputQuantity(double inputQuantity) {
        this.inputQuantity = inputQuantity;
        calculateConversionRate();
        this.updatedAt = new Date();
    }
    
    public double getOutputQuantity() {
        return outputQuantity;
    }
    
    public void setOutputQuantity(double outputQuantity) {
        this.outputQuantity = outputQuantity;
        calculateConversionRate();
        this.updatedAt = new Date();
    }
    
    public double getConversionRate() {
        return conversionRate;
    }
    
    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }
    
    public double getWasteQuantity() {
        return wasteQuantity;
    }
    
    public void setWasteQuantity(double wasteQuantity) {
        this.wasteQuantity = wasteQuantity;
        this.updatedAt = new Date();
    }
    
    public int getMachineId() {
        return machineId;
    }
    
    public void setMachineId(int machineId) {
        this.machineId = machineId;
        this.updatedAt = new Date();
    }
    
    public int getOperatorId() {
        return operatorId;
    }
    
    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
        this.updatedAt = new Date();
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = new Date();
    }
    
    // Related object getters/setters
    public String getRawProductName() {
        return rawProductName;
    }
    
    public void setRawProductName(String rawProductName) {
        this.rawProductName = rawProductName;
    }
    
    public String getFinishedProductName() {
        return finishedProductName;
    }
    
    public void setFinishedProductName(String finishedProductName) {
        this.finishedProductName = finishedProductName;
    }
    
    public String getMachineName() {
        return machineName;
    }
    
    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }
    
    public String getOperatorName() {
        return operatorName;
    }
    
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
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
    private void calculateConversionRate() {
        if (inputQuantity > 0) {
            this.conversionRate = (outputQuantity / inputQuantity) * 100;
        } else {
            this.conversionRate = 0.0;
        }
    }
    
    public double getEfficiencyPercentage() {
        return conversionRate;
    }
    
    public boolean isEfficientProduction() {
        return conversionRate >= 65.0; // Assuming 65% is good efficiency for rice milling
    }
    
    public double getTotalProcessedQuantity() {
        return outputQuantity + wasteQuantity;
    }
    
    public double getWastePercentage() {
        if (inputQuantity > 0) {
            return (wasteQuantity / inputQuantity) * 100;
        }
        return 0.0;
    }
    
    // Generate production number
    public static String generateProductionNumber() {
        return "PRD" + System.currentTimeMillis();
    }
    
    // Validation methods
    public boolean isValid() {
        return productionNumber != null && !productionNumber.trim().isEmpty() &&
               rawProductId > 0 &&
               finishedProductId > 0 &&
               inputQuantity > 0 &&
               outputQuantity >= 0 &&
               productionDate != null;
    }
    
    @Override
    public String toString() {
        return "ProductionRecord{" +
                "productionId=" + productionId +
                ", productionNumber='" + productionNumber + '\'' +
                ", productionDate=" + productionDate +
                ", rawProductName='" + rawProductName + '\'' +
                ", finishedProductName='" + finishedProductName + '\'' +
                ", inputQuantity=" + inputQuantity +
                ", outputQuantity=" + outputQuantity +
                ", conversionRate=" + conversionRate + "%" +
                ", wasteQuantity=" + wasteQuantity +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProductionRecord that = (ProductionRecord) obj;
        return productionId == that.productionId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(productionId);
    }
}