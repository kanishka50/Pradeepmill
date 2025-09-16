package com.pradeepmill.ui.reports;

import com.pradeepmill.services.ReportService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SalaryReportPanel extends javax.swing.JPanel {

    private ReportService reportService;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> monthComboBox;
    private JLabel totalSalaryLabel;
    private JLabel paidCountLabel;
    private JLabel pendingCountLabel;

    public SalaryReportPanel() {
        this.reportService = new ReportService();
        initComponents();
        loadCurrentMonthReport();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(184, 134, 11));
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Monthly Salary Report", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Control panel for month selection and summary
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        // Month selection panel
        JPanel monthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        monthPanel.setBackground(Color.WHITE);
        
        JLabel monthLabel = new JLabel("Select Month:");
        monthLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        monthComboBox = new JComboBox<>();
        populateMonthComboBox();
        monthComboBox.addActionListener(this::monthSelectionChanged);
        
        JButton generateButton = new JButton("Generate Report");
        JButton refreshButton = new JButton("Refresh");
        
        generateButton.addActionListener(this::generateReportActionPerformed);
        refreshButton.addActionListener(this::generateReportActionPerformed);
        
        monthPanel.add(monthLabel);
        monthPanel.add(monthComboBox);
        monthPanel.add(generateButton);
        monthPanel.add(refreshButton);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(Color.WHITE);
        
        totalSalaryLabel = new JLabel("Total Salary: Rs. 0.00");
        totalSalaryLabel.setFont(new Font("Arial", Font.BOLD, 12));
        totalSalaryLabel.setForeground(new Color(184, 134, 11));
        
        paidCountLabel = new JLabel("Paid: 0");
        paidCountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        paidCountLabel.setForeground(new Color(76, 175, 80));
        
        pendingCountLabel = new JLabel("Pending: 0");
        pendingCountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        pendingCountLabel.setForeground(Color.RED);
        
        summaryPanel.add(totalSalaryLabel);
        summaryPanel.add(Box.createHorizontalStrut(20));
        summaryPanel.add(paidCountLabel);
        summaryPanel.add(Box.createHorizontalStrut(10));
        summaryPanel.add(pendingCountLabel);
        
        controlPanel.add(monthPanel, BorderLayout.WEST);
        controlPanel.add(summaryPanel, BorderLayout.EAST);
        
        // Table setup - SIMPLIFIED COLUMNS (removed OT, deductions columns)
        String[] columnNames = {"Staff ID", "Employee Name", "Position", "Monthly Salary", 
                               "Basic Salary", "Bonus", "Net Salary", "Payment Date", "Status"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reportTable = new JTable(tableModel);
        reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        reportTable.setRowHeight(25);
        
        // Set column widths - adjusted for simplified columns
        reportTable.getColumnModel().getColumn(0).setPreferredWidth(70);   // Staff ID
        reportTable.getColumnModel().getColumn(1).setPreferredWidth(180);  // Name
        reportTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Position
        reportTable.getColumnModel().getColumn(3).setPreferredWidth(120);  // Monthly Salary
        reportTable.getColumnModel().getColumn(4).setPreferredWidth(120);  // Basic Salary
        reportTable.getColumnModel().getColumn(5).setPreferredWidth(100);  // Bonus
        reportTable.getColumnModel().getColumn(6).setPreferredWidth(120);  // Net Salary
        reportTable.getColumnModel().getColumn(7).setPreferredWidth(120);  // Payment Date
        reportTable.getColumnModel().getColumn(8).setPreferredWidth(100);  // Status
        
        // Custom cell renderer for status column
        reportTable.getColumn("Status").setCellRenderer(new StatusCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setPreferredSize(new Dimension(1100, 500));
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(controlPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void populateMonthComboBox() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        // Add current month and previous 11 months
        for (int i = 0; i < 12; i++) {
            LocalDate monthDate = currentDate.minusMonths(i);
            String monthString = monthDate.format(formatter);
            String displayString = monthDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
            
            monthComboBox.addItem(displayString + " (" + monthString + ")");
        }
        
        // Select current month by default
        monthComboBox.setSelectedIndex(0);
    }
    
    private String getSelectedMonth() {
        String selected = (String) monthComboBox.getSelectedItem();
        if (selected != null) {
            // Extract YYYY-MM from "Month Year (YYYY-MM)"
            int startIndex = selected.lastIndexOf("(") + 1;
            int endIndex = selected.lastIndexOf(")");
            return selected.substring(startIndex, endIndex);
        }
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }
    
    private void loadCurrentMonthReport() {
        generateReport();
    }
    
    private void monthSelectionChanged(ActionEvent evt) {
        generateReport();
    }
    
    private void generateReportActionPerformed(ActionEvent evt) {
        generateReport();
    }
    
    private void generateReport() {
        try {
            tableModel.setRowCount(0); // Clear existing data
            
            String selectedMonth = getSelectedMonth();
            List<Map<String, Object>> salaryData = reportService.generateMonthlySalaryReport(selectedMonth);
            
            double totalSalary = 0.0;
            int paidCount = 0;
            int pendingCount = 0;
            
            for (Map<String, Object> salary : salaryData) {
                // Simplified row data - removed OT and deductions columns
                Object[] row = {
                    salary.get("staff_id"),
                    salary.get("employee_name"),
                    salary.get("position"),
                    salary.get("formatted_monthly_salary"),
                    salary.get("formatted_basic_salary"),
                    salary.get("formatted_bonus"),
                    salary.get("formatted_net_salary"),
                    salary.get("payment_date"),
                    salary.get("payment_status")
                };
                tableModel.addRow(row);
                
                // Calculate totals
                double actualPayment = (Double) salary.getOrDefault("actual_payment", 0.0);
                totalSalary += actualPayment;
                
                String paymentStatus = (String) salary.get("payment_status");
                if ("Paid".equalsIgnoreCase(paymentStatus)) {
                    paidCount++;
                } else {
                    pendingCount++;
                }
            }
            
            // Update summary labels
            totalSalaryLabel.setText("Total Salary: Rs. " + String.format("%,.2f", totalSalary));
            paidCountLabel.setText("Paid: " + paidCount);
            pendingCountLabel.setText("Pending: " + pendingCount);
            
            // Show success message only if report has data
            if (!salaryData.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Salary report generated for " + selectedMonth + 
                    "\nTotal employees: " + salaryData.size() +
                    "\nTotal salary expense: Rs. " + String.format("%,.2f", totalSalary),
                    "Report Generated", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No salary data found for " + selectedMonth,
                    "No Data",
                    JOptionPane.INFORMATION_MESSAGE);
            }
                
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error generating salary report: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Custom cell renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected && value != null) {
                String status = value.toString();
                if ("Paid".equalsIgnoreCase(status)) {
                    c.setForeground(new Color(76, 175, 80)); // Green
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.RED); // Red for pending
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                }
            } else if (isSelected) {
                c.setForeground(table.getSelectionForeground());
            }
            
            return c;
        }
    }
}