package com.pradeepmill.ui.staff;

import com.pradeepmill.dao.StaffDAO;
import com.pradeepmill.dao.SalaryPaymentDAO;
import com.pradeepmill.models.Staff;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MonthlySalaryEntryDialog extends JDialog {
    
    private StaffDAO staffDAO;
    private SalaryPaymentDAO salaryDAO;
    private String paymentMonth;
    private boolean processed = false;
    
    private JTable salaryTable;
    private DefaultTableModel tableModel;
    private JLabel totalSalaryLabel;
    private JLabel employeeCountLabel;
    
    public MonthlySalaryEntryDialog(Frame parent, String paymentMonth) {
        super(parent, "Monthly Salary Entry - " + paymentMonth, true);
        this.paymentMonth = paymentMonth;
        this.staffDAO = new StaffDAO();
        this.salaryDAO = new SalaryPaymentDAO();
        initComponents();
        loadEmployeeData();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(900, 500);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(184, 134, 11));
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Monthly Salary Entry - " + paymentMonth, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Instructions
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setBackground(Color.WHITE);
        JLabel instructionsLabel = new JLabel("<html><b>Instructions:</b> Basic salary is pre-filled. " +
            "Add bonus amounts if applicable. Net salary calculates automatically.</html>");
        instructionsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        instructionsPanel.add(instructionsLabel);
        
        // Summary
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(Color.WHITE);
        
        employeeCountLabel = new JLabel("Employees: 0");
        employeeCountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        totalSalaryLabel = new JLabel("Total: Rs. 0.00");
        totalSalaryLabel.setFont(new Font("Arial", Font.BOLD, 12));
        totalSalaryLabel.setForeground(new Color(184, 134, 11));
        
        summaryPanel.add(employeeCountLabel);
        summaryPanel.add(Box.createHorizontalStrut(20));
        summaryPanel.add(totalSalaryLabel);
        
        infoPanel.add(instructionsPanel, BorderLayout.WEST);
        infoPanel.add(summaryPanel, BorderLayout.EAST);
        
        // Table setup - SIMPLIFIED COLUMNS
        String[] columnNames = {"Employee Name", "Position", "Basic Salary", "Bonus", "Net Salary", "Status"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 2: case 3: case 4: // Basic Salary, Bonus, Net Salary
                        return Double.class;
                    default:
                        return String.class;
                }
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only allow editing of Bonus column (index 3) and only if not already paid
                if (column == 3) {
                    String status = (String) getValueAt(row, 5);
                    return !"Already Paid".equals(status);
                }
                return false;
            }
        };
        
        salaryTable = new JTable(tableModel);
        salaryTable.setRowHeight(25);
        salaryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths
        salaryTable.getColumnModel().getColumn(0).setPreferredWidth(180); // Employee Name
        salaryTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Position
        salaryTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Basic Salary
        salaryTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Bonus
        salaryTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Net Salary
        salaryTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        
        // FIXED: Custom cell editor for bonus column with proper validation
        JTextField bonusField = new JTextField();
        bonusField.setHorizontalAlignment(JTextField.RIGHT);
        
        DefaultCellEditor bonusEditor = new DefaultCellEditor(bonusField) {
            @Override
            public boolean stopCellEditing() {
                String value = (String) getCellEditorValue();
                try {
                    if (value == null || value.trim().isEmpty()) {
                        // Set to 0 if empty
                        super.getCellEditorValue();
                        return true;
                    }
                    double bonusAmount = Double.parseDouble(value.trim());
                    if (bonusAmount < 0) {
                        JOptionPane.showMessageDialog(MonthlySalaryEntryDialog.this,
                            "Bonus amount cannot be negative",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(MonthlySalaryEntryDialog.this,
                        "Please enter a valid numeric bonus amount",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                return super.stopCellEditing();
            }
        };
        salaryTable.getColumnModel().getColumn(3).setCellEditor(bonusEditor);
        
        // FIXED: Add proper table model listener
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 3) {
                    // Bonus column was updated
                    SwingUtilities.invokeLater(() -> {
                        calculateRowNetSalary(e.getFirstRow());
                        updateTotalSalary();
                    });
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(salaryTable);
        scrollPane.setPreferredSize(new Dimension(850, 300));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        JButton recalculateButton = new JButton("Recalculate All");
        JButton saveAllButton = new JButton("Save All Payments");
        JButton cancelButton = new JButton("Cancel");
        
        recalculateButton.addActionListener(this::recalculateAllActionPerformed);
        saveAllButton.addActionListener(this::saveAllActionPerformed);
        cancelButton.addActionListener(this::cancelActionPerformed);
        
        recalculateButton.setBackground(new Color(33, 150, 243));
        recalculateButton.setForeground(Color.WHITE);
        
        saveAllButton.setBackground(new Color(76, 175, 80));
        saveAllButton.setForeground(Color.WHITE);
        saveAllButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        buttonPanel.add(recalculateButton);
        buttonPanel.add(saveAllButton);
        buttonPanel.add(cancelButton);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadEmployeeData() {
        try {
            tableModel.setRowCount(0);
            
            List<Staff> activeStaff = staffDAO.getAllActiveStaff();
            System.out.println("DEBUG: Loading " + activeStaff.size() + " active staff members");
            
            // Get existing salary payments for this month
            java.util.Map<Integer, java.util.Map<String, Object>> existingPayments = new java.util.HashMap<>();
            List<java.util.Map<String, Object>> paymentsList = salaryDAO.getSalaryPaymentsByMonth(paymentMonth);
            
            // Create a map for quick lookup by staff_id
            for (java.util.Map<String, Object> payment : paymentsList) {
                Integer staffId = (Integer) payment.get("staff_id");
                existingPayments.put(staffId, payment);
            }
            
            for (Staff staff : activeStaff) {
                double basicSalary = staff.getMonthlySalary();
                double bonus = 0.0;
                double netSalary = basicSalary;
                String status = "Pending";
                
                // Check if payment already exists and load actual values
                if (existingPayments.containsKey(staff.getStaffId())) {
                    java.util.Map<String, Object> payment = existingPayments.get(staff.getStaffId());
                    
                    // Use actual saved values
                    basicSalary = ((Number) payment.getOrDefault("basic_salary", staff.getMonthlySalary())).doubleValue();
                    bonus = ((Number) payment.getOrDefault("bonus", 0.0)).doubleValue();
                    netSalary = ((Number) payment.getOrDefault("net_salary", basicSalary + bonus)).doubleValue();
                    status = "Already Paid";
                    
                    System.out.println("DEBUG: Loaded existing payment for " + staff.getEmployeeName() + 
                                     " - Basic: " + basicSalary + ", Bonus: " + bonus + ", Net: " + netSalary);
                } else {
                    System.out.println("DEBUG: No existing payment for " + staff.getEmployeeName() + 
                                     " - Using defaults");
                }
                
                Object[] row = {
                    staff.getEmployeeName(),
                    staff.getPosition(),
                    basicSalary,   // Basic Salary (from DB if exists, else monthly salary)
                    bonus,         // Bonus (from DB if exists, else 0)
                    netSalary,     // Net Salary (from DB if exists, else basic salary)
                    status
                };
                tableModel.addRow(row);
            }
            
            updateEmployeeCount();
            updateTotalSalary();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading employee data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void calculateRowNetSalary(int row) {
        try {
            System.out.println("DEBUG: Calculating net salary for row " + row);
            
            Object basicSalaryObj = tableModel.getValueAt(row, 2);
            Object bonusObj = tableModel.getValueAt(row, 3);
            
            double basicSalary = parseDouble(basicSalaryObj);
            double bonus = parseDouble(bonusObj);
            
            System.out.println("DEBUG: Basic salary: " + basicSalary + ", Bonus: " + bonus);
            
            // Simple calculation: Net Salary = Basic Salary + Bonus
            double netSalary = basicSalary + bonus;
            
            System.out.println("DEBUG: Calculated net salary: " + netSalary);
            
            // Update table - IMPORTANT: Use fireTableCellUpdated to prevent infinite loop
            tableModel.removeTableModelListener(tableModel.getTableModelListeners()[0]);
            tableModel.setValueAt(netSalary, row, 4); // Net Salary column
            tableModel.addTableModelListener(tableModel.getTableModelListeners()[0]);
            
        } catch (Exception e) {
            System.err.println("Error calculating net salary for row " + row + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private double parseDouble(Object value) {
        if (value == null) return 0.0;
        try {
            if (value instanceof Double) return (Double) value;
            if (value instanceof String) {
                String str = ((String) value).trim();
                return str.isEmpty() ? 0.0 : Double.parseDouble(str);
            }
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            return Double.parseDouble(value.toString().trim());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing double from: " + value + " (" + (value != null ? value.getClass().getSimpleName() : "null") + ")");
            return 0.0;
        }
    }
    
    private void commitTableEdits() {
        if (salaryTable.isEditing()) {
            salaryTable.getCellEditor().stopCellEditing();
        }
    }
    
    private void recalculateAllActionPerformed(ActionEvent evt) {
        commitTableEdits();
        
        System.out.println("DEBUG: Recalculating all rows");
        
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            calculateRowNetSalary(row);
        }
        updateTotalSalary();
        
        JOptionPane.showMessageDialog(this, 
            "All salary calculations updated!", 
            "Recalculated", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void saveAllActionPerformed(ActionEvent evt) {
        commitTableEdits();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Save/Update salary payments for all employees for " + paymentMonth + "?",
            "Confirm Save",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int savedCount = 0;
                int updatedCount = 0;
                int skippedCount = 0;
                List<Staff> activeStaff = staffDAO.getAllActiveStaff();
                
                // Get existing payments for quick lookup
                java.util.Map<Integer, java.util.Map<String, Object>> existingPayments = new java.util.HashMap<>();
                List<java.util.Map<String, Object>> paymentsList = salaryDAO.getSalaryPaymentsByMonth(paymentMonth);
                for (java.util.Map<String, Object> payment : paymentsList) {
                    Integer staffId = (Integer) payment.get("staff_id");
                    existingPayments.put(staffId, payment);
                }
                
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    String status = (String) tableModel.getValueAt(row, 5);
                    
                    // Get employee data
                    String employeeName = (String) tableModel.getValueAt(row, 0);
                    Staff employee = activeStaff.stream()
                        .filter(s -> s.getEmployeeName().equals(employeeName))
                        .findFirst()
                        .orElse(null);
                    
                    if (employee != null) {
                        double basicSalary = parseDouble(tableModel.getValueAt(row, 2));
                        double bonus = parseDouble(tableModel.getValueAt(row, 3));
                        
                        System.out.println("DEBUG: Processing " + employeeName + 
                                         " (ID: " + employee.getStaffId() + 
                                         ") - Basic: " + basicSalary + 
                                         ", Bonus: " + bonus +
                                         ", Status: " + status);
                        
                        boolean success = false;
                        
                        if ("Already Paid".equals(status)) {
                            // Update existing payment
                            success = salaryDAO.updateSalaryPaymentByStaffAndMonth(
                                employee.getStaffId(), paymentMonth, basicSalary, bonus);
                            if (success) {
                                updatedCount++;
                                tableModel.setValueAt("Updated", row, 5);
                            }
                        } else {
                            // Create new payment
                            success = salaryDAO.insertSimpleSalaryPayment(
                                employee.getStaffId(), paymentMonth, basicSalary, bonus);
                            if (success) {
                                savedCount++;
                                tableModel.setValueAt("Saved", row, 5);
                            }
                        }
                        
                        if (!success) {
                            System.err.println("Failed to save/update payment for " + employeeName);
                            skippedCount++;
                        }
                        
                    } else {
                        System.err.println("Employee not found: " + employeeName);
                        skippedCount++;
                    }
                }
                
                processed = true;
                JOptionPane.showMessageDialog(this,
                    "Salary processing completed!\n" +
                    "New payments saved: " + savedCount + "\n" +
                    "Existing payments updated: " + updatedCount + "\n" +
                    "Skipped: " + skippedCount,
                    "Save Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error saving salary payments: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cancelActionPerformed(ActionEvent evt) {
        dispose();
    }
    
    private void updateEmployeeCount() {
        employeeCountLabel.setText("Employees: " + tableModel.getRowCount());
    }
    
    private void updateTotalSalary() {
        double total = 0.0;
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            total += parseDouble(tableModel.getValueAt(row, 4)); // Net Salary column
        }
        totalSalaryLabel.setText("Total: Rs. " + String.format("%,.2f", total));
        System.out.println("DEBUG: Updated total salary to: Rs. " + String.format("%,.2f", total));
    }
    
    public boolean isProcessed() {
        return processed;
    }
}