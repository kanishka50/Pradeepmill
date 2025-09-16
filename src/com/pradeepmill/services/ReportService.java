package com.pradeepmill.services;

import com.pradeepmill.dao.ReportDAO;
import com.pradeepmill.dao.StockInventoryDAO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import com.pradeepmill.models.*;

public class ReportService {
    
    private ReportDAO reportDAO;
    
    public ReportService() {
        this.reportDAO = new ReportDAO();
    }
    
    /**
     * Generate Customer Details Report (Required Report #1)
     */
    public List<Map<String, Object>> generateCustomerDetailsReport() {
        try {
            List<Map<String, Object>> customerData = reportDAO.getCustomerDetailsReport();
            
            // Process data for better presentation
            for (Map<String, Object> customer : customerData) {
                // Format currency values - handle BigDecimal
                Object totalPurchases = customer.get("total_purchases");
                Object outstandingBalance = customer.get("outstanding_balance");
                
                customer.put("formatted_total_purchases", formatCurrency(totalPurchases));
                customer.put("formatted_outstanding_balance", formatCurrency(outstandingBalance));
                
                // Add status indicators - convert to double for comparison
                double outstanding = convertToDouble(outstandingBalance);
                customer.put("payment_status", getCustomerPaymentStatus(outstanding));
            }
            
            return customerData;
            
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Return empty list on error
        }
    }
    
    /**
     * Generate Supplier Details Report (Required Report #2)
     */
    public List<Map<String, Object>> generateSupplierDetailsReport() {
        try {
            List<Map<String, Object>> supplierData = reportDAO.getSupplierDetailsReport();
            
            // Process data for better presentation
            for (Map<String, Object> supplier : supplierData) {
                // Format currency values - handle BigDecimal
                Object totalPurchases = supplier.get("total_purchases");
                Object outstandingPayments = supplier.get("outstanding_payments");
                
                supplier.put("formatted_total_purchases", formatCurrency(totalPurchases));
                supplier.put("formatted_outstanding_payments", formatCurrency(outstandingPayments));
                
                // Add status indicators
                double outstanding = convertToDouble(outstandingPayments);
                supplier.put("payment_status", getSupplierPaymentStatus(outstanding));
            }
            
            return supplierData;
            
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Generate Monthly Salary Report (Required Report #3) - SIMPLIFIED VERSION
     */
    public List<Map<String, Object>> generateMonthlySalaryReport(String month) {
        try {
            List<Map<String, Object>> salaryData = reportDAO.getMonthlySalaryReport(month);
            
            // Process data for better presentation
            for (Map<String, Object> salary : salaryData) {
                // Format currency values - handle BigDecimal (simplified - no OT/deductions)
                Object monthlySalary = salary.get("monthly_salary");
                Object basicSalary = salary.get("basic_salary");
                Object bonus = salary.get("bonus");
                Object netSalary = salary.get("net_salary");
                
                // Format all currency fields
                salary.put("formatted_monthly_salary", formatCurrency(monthlySalary));
                salary.put("formatted_basic_salary", formatCurrency(basicSalary));
                salary.put("formatted_bonus", formatCurrency(bonus));
                salary.put("formatted_net_salary", formatCurrency(netSalary));
                
                // Set simplified fields to 0 (not used in simplified system)
                salary.put("overtime_hours", 0.0);
                salary.put("overtime_amount", 0.0);
                salary.put("deductions", 0.0);
                salary.put("formatted_overtime_amount", "Rs. 0.00");
                salary.put("formatted_deductions", "Rs. 0.00");
                
                // Calculate actual payment amount (simplified)
                double netValue = convertToDouble(netSalary);
                double basicValue = convertToDouble(basicSalary);
                double bonusValue = convertToDouble(bonus);
                
                // If no payment record exists, use monthly salary, otherwise use net salary
                double actualPayment;
                String paymentStatus = (String) salary.get("payment_status");
                
                if (paymentStatus == null) {
                    // No payment record - use monthly salary
                    actualPayment = convertToDouble(monthlySalary);
                    salary.put("payment_status", "Pending");
                } else if ("Paid".equals(paymentStatus)) {
                    // Payment exists - use net salary (basic + bonus)
                    actualPayment = netValue > 0 ? netValue : (basicValue + bonusValue);
                } else {
                    // Pending payment - use monthly salary
                    actualPayment = convertToDouble(monthlySalary);
                }
                
                salary.put("actual_payment", actualPayment);
                salary.put("formatted_actual_payment", formatCurrency(actualPayment));
            }
            
            return salaryData;
            
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Generate Stock Report
     */
    public List<Map<String, Object>> generateStockReport() {
        try {
            // Use StockInventoryDAO
            StockInventoryDAO stockDAO = new StockInventoryDAO();
            List<StockInventory> stockItems = stockDAO.getAllStockItems();
            
            List<Map<String, Object>> stockData = new ArrayList<>();
            
            // Convert StockInventory objects to Map format
            for (StockInventory stock : stockItems) {
                Map<String, Object> stockMap = new HashMap<>();
                stockMap.put("product_id", stock.getProductId());
                stockMap.put("product_name", stock.getProductName());
                stockMap.put("product_type", stock.getProductType());
                stockMap.put("grade", ""); // Add if available in your StockInventory model
                stockMap.put("unit_price", stock.getUnitPrice());
                stockMap.put("unit", stock.getUnit());
                stockMap.put("current_quantity", stock.getCurrentQuantity());
                stockMap.put("minimum_level", stock.getMinimumLevel());
                stockMap.put("maximum_level", stock.getMaximumLevel());
                stockMap.put("stock_value", stock.getCurrentQuantity() * stock.getUnitPrice());
                stockMap.put("stock_status", stock.getStockStatus());
                
                // Format data for display
                stockMap.put("formatted_unit_price", "Rs. " + String.format("%.2f", stock.getUnitPrice()));
                stockMap.put("formatted_stock_value", "Rs. " + String.format("%,.2f", stock.getCurrentQuantity() * stock.getUnitPrice()));
                stockMap.put("formatted_quantity", String.format("%.2f", stock.getCurrentQuantity()));
                
                // Add status indicators
                String stockStatus = stock.getStockStatus();
                if (stockStatus != null) {
                    switch (stockStatus) {
                        case "Out of Stock":
                            stockMap.put("status_indicator", "🔴 " + stockStatus);
                            break;
                        case "Low Stock":
                            stockMap.put("status_indicator", "🟡 " + stockStatus);
                            break;
                        case "Overstock":
                            stockMap.put("status_indicator", "🟠 " + stockStatus);
                            break;
                        default:
                            stockMap.put("status_indicator", "🟢 " + stockStatus);
                            break;
                    }
                }
                
                stockData.add(stockMap);
            }
            
            return stockData;
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Get report summary statistics - SIMPLIFIED
     */
    public Map<String, Object> getReportSummaryStatistics() {
        Map<String, Object> summary = new HashMap<>();
        
        try {
            // Get dashboard statistics
            Map<String, Object> dashboardStats = reportDAO.getDashboardStats();
            
            // Customer statistics
            List<Map<String, Object>> customers = reportDAO.getCustomerDetailsReport();
            summary.put("total_customers", customers.size());
            
            double totalCustomerPurchases = customers.stream()
                .mapToDouble(c -> convertToDouble(c.get("total_purchases")))
                .sum();
            summary.put("total_customer_purchases", totalCustomerPurchases);
            
            // Supplier statistics
            List<Map<String, Object>> suppliers = reportDAO.getSupplierDetailsReport();
            summary.put("total_suppliers", suppliers.size());
            
            double totalSupplierPayments = suppliers.stream()
                .mapToDouble(s -> convertToDouble(s.get("total_purchases")))
                .sum();
            summary.put("total_supplier_payments", totalSupplierPayments);
            
            // Stock statistics - handle BigDecimal
            summary.put("total_stock_value", convertToDouble(dashboardStats.get("total_stock_value")));
            summary.put("low_stock_items", dashboardStats.get("low_stock_items"));
            
            // Current month salary expense (simplified calculation)
            String currentMonth = getCurrentMonth();
            List<Map<String, Object>> salaries = reportDAO.getMonthlySalaryReport(currentMonth);
            double totalSalaryExpense = salaries.stream()
                .mapToDouble(s -> {
                    // Simplified: basic salary + bonus (no OT or deductions)
                    double basicSalary = convertToDouble(s.get("basic_salary"));
                    double bonus = convertToDouble(s.get("bonus"));
                    double monthlySalary = convertToDouble(s.get("monthly_salary"));
                    
                    // If payment exists, use basic + bonus, otherwise use monthly salary
                    String paymentStatus = (String) s.get("payment_status");
                    if ("Paid".equals(paymentStatus)) {
                        return basicSalary + bonus;
                    } else {
                        return monthlySalary;
                    }
                })
                .sum();
            summary.put("monthly_salary_expense", totalSalaryExpense);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return summary;
    }
    
    // Helper method to safely convert various numeric types to double
    private double convertToDouble(Object value) {
        if (value == null) return 0.0;
        
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof Float) {
            return ((Float) value).doubleValue();
        }
        
        // Try to parse as string if it's a string representation
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        
        return 0.0;
    }
    
    // Helper method to format currency values handling multiple types
    private String formatCurrency(Object amount) {
        if (amount == null) return "Rs. 0.00";
        
        double value = convertToDouble(amount);
        return String.format("Rs. %.2f", value);
    }
    
    private String getCustomerPaymentStatus(double outstandingBalance) {
        if (outstandingBalance <= 0) {
            return "Paid";
        } else {
            return "Outstanding";
        }
    }
    
    private String getSupplierPaymentStatus(double outstandingPayments) {
        if (outstandingPayments <= 0) {
            return "Paid";
        } else {
            return "Pending";
        }
    }
    
    private String getCurrentMonth() {
        java.time.LocalDate now = java.time.LocalDate.now();
        return String.format("%04d-%02d", now.getYear(), now.getMonthValue());
    }
}