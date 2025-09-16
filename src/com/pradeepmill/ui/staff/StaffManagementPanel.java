package com.pradeepmill.ui.staff;

import com.pradeepmill.dao.StaffDAO;
import com.pradeepmill.models.Staff;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import com.pradeepmill.ui.staff.SalaryManagementDialog;
import com.pradeepmill.ui.staff.MonthlySalaryEntryDialog;


public class StaffManagementPanel extends javax.swing.JPanel {

    private StaffDAO staffDAO;
    private JTable staffTable;
    private DefaultTableModel tableModel;

    public StaffManagementPanel() {
        this.staffDAO = new StaffDAO();
        initComponents();
        loadStaff();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(138, 43, 226)); // Blue Violet for staff
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Staff Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton addButton = new JButton("Add Employee");
        JButton editButton = new JButton("Edit Employee");
        JButton deleteButton = new JButton("Delete Employee");
        JButton refreshButton = new JButton("Refresh");
        JButton salaryButton = new JButton("Salary Management");
        
        addButton.addActionListener(this::addStaffActionPerformed);
        editButton.addActionListener(this::editStaffActionPerformed);
        deleteButton.addActionListener(this::deleteStaffActionPerformed);
        refreshButton.addActionListener(this::refreshActionPerformed);
        salaryButton.addActionListener(this::salaryManagementActionPerformed);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(salaryButton);
        
        // Table setup
        String[] columnNames = {"ID", "Employee Name", "Position", "Phone", "Address", "Monthly Salary", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        staffTable = new JTable(tableModel);
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        staffTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        staffTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        staffTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        staffTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        staffTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        staffTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        staffTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        staffTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        
        // Filter panel - using your actual constants
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Staff"));
        
        JLabel positionLabel = new JLabel("Position:");
        JComboBox<String> positionFilter = new JComboBox<>(new String[]{
            "All Positions", 
            Staff.POSITION_MANAGER,           // "Mill Manager"
            Staff.POSITION_OPERATOR,          // "Machine Operator"  
            Staff.POSITION_QUALITY_CONTROLLER,// "Quality Controller"
            Staff.POSITION_ACCOUNTANT,        // "Accountant"
            Staff.POSITION_SUPERVISOR,        // "Supervisor"
            "Helper"                          // Manual entry since no constant
        });
        
        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{
            "All Status", "Active", "Inactive"
        });
        
        JButton filterButton = new JButton("Apply Filter");
        JButton resetButton = new JButton("Reset Filter");
        
        filterButton.addActionListener(e -> applyFilter(
            (String) positionFilter.getSelectedItem(),
            (String) statusFilter.getSelectedItem()
        ));
        resetButton.addActionListener(e -> {
            positionFilter.setSelectedIndex(0);
            statusFilter.setSelectedIndex(0);
            loadStaff();
        });
        
        filterPanel.add(positionLabel);
        filterPanel.add(positionFilter);
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilter);
        filterPanel.add(filterButton);
        filterPanel.add(resetButton);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(filterPanel, BorderLayout.CENTER);
        contentPanel.add(scrollPane, BorderLayout.SOUTH);
        
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void loadStaff() {
        try {
            tableModel.setRowCount(0);
            
            List<Staff> staffList = staffDAO.getAllActiveStaff();
            
            for (Staff staff : staffList) {
                Object[] row = {
                    staff.getStaffId(),
                    staff.getEmployeeName(),
                    staff.getPosition(),
                    staff.getPhone(),
                    staff.getAddress(),
                    String.format("Rs. %.2f", staff.getMonthlySalary()),
                    staff.getStatus()
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading staff: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void applyFilter(String position, String status) {
        try {
            tableModel.setRowCount(0);
            
            List<Staff> staffList;
            
            if ("All Positions".equals(position) && "All Status".equals(status)) {
                staffList = staffDAO.getAllActiveStaff();
            } else if (!"All Positions".equals(position) && "All Status".equals(status)) {
                staffList = staffDAO.getStaffByPosition(position);
            } else if ("All Positions".equals(position) && !"All Status".equals(status)) {
                staffList = staffDAO.getStaffByStatus(status);
            } else {
                staffList = staffDAO.getStaffByPositionAndStatus(position, status);
            }
            
            for (Staff staff : staffList) {
                Object[] row = {
                    staff.getStaffId(),
                    staff.getEmployeeName(),
                    staff.getPosition(),
                    staff.getPhone(),
                    staff.getAddress(),
                    String.format("Rs. %.2f", staff.getMonthlySalary()),
                    staff.getStatus()
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error applying filter: " + e.getMessage(), 
                "Filter Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addStaffActionPerformed(ActionEvent evt) {
        AddStaffDialog dialog = new AddStaffDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Staff newStaff = dialog.getStaff();
            if (staffDAO.insertStaff(newStaff)) {
                JOptionPane.showMessageDialog(this, "Employee added successfully!");
                loadStaff();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add employee!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editStaffActionPerformed(ActionEvent evt) {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit.");
            return;
        }
        
        int staffId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Staff staff = staffDAO.findStaffById(staffId);
        
        if (staff != null) {
            AddStaffDialog dialog = new AddStaffDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
            dialog.setStaff(staff);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Staff updatedStaff = dialog.getStaff();
                updatedStaff.setStaffId(staffId);
                
                if (staffDAO.updateStaff(updatedStaff)) {
                    JOptionPane.showMessageDialog(this, "Employee updated successfully!");
                    loadStaff();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update employee!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void deleteStaffActionPerformed(ActionEvent evt) {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
            return;
        }
        
        String employeeName = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete employee: " + employeeName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int staffId = (Integer) tableModel.getValueAt(selectedRow, 0);
            
            if (staffDAO.deleteStaff(staffId)) {
                JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                loadStaff();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete employee!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    private void refreshActionPerformed(ActionEvent evt) {
        loadStaff();
        JOptionPane.showMessageDialog(this, "Staff list refreshed!");
    }
    
    private void salaryManagementActionPerformed(ActionEvent evt) {
    try {
        // Ask user to select month
        String[] months = new String[12];
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy (yyyy-MM)");
        
        for (int i = 0; i < 12; i++) {
            java.time.LocalDate monthDate = now.minusMonths(i);
            months[i] = monthDate.format(formatter);
        }
        
        String selectedMonth = (String) JOptionPane.showInputDialog(this,
            "Select month for salary processing:",
            "Month Selection",
            JOptionPane.QUESTION_MESSAGE,
            null,
            months,
            months[0]);
        
        if (selectedMonth != null) {
            // Extract YYYY-MM from selection
            String paymentMonth = selectedMonth.substring(selectedMonth.lastIndexOf("(") + 1, selectedMonth.lastIndexOf(")"));
            
            MonthlySalaryEntryDialog dialog = new MonthlySalaryEntryDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), paymentMonth);
            dialog.setVisible(true);
            
            if (dialog.isProcessed()) {
                JOptionPane.showMessageDialog(this,
                    "Salary payments processed successfully!\nYou can view the report in Reports → Monthly Salary Report.",
                    "Processing Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error accessing salary management: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
}