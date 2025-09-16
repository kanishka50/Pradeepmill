package com.pradeepmill.ui.reports;

import com.pradeepmill.services.ReportService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

public class SupplierReportPanel extends javax.swing.JPanel {

    private ReportService reportService;
    private JTable reportTable;
    private DefaultTableModel tableModel;

    public SupplierReportPanel() {
        this.reportService = new ReportService();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 128, 128)); // Teal color (matching your screenshot)
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Supplier Details Report", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton generateButton = new JButton("Generate Report");
        JButton refreshButton = new JButton("Refresh");
        
        generateButton.addActionListener(this::generateReportActionPerformed);
        refreshButton.addActionListener(this::generateReportActionPerformed);
        
        buttonPanel.add(generateButton);
        buttonPanel.add(refreshButton);
        
        // Table setup
        String[] columnNames = {"Supplier ID", "Supplier Name", "Phone", "Address", 
                               "Total Orders", "Total Purchases", "Outstanding Payments", "Last Purchase"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reportTable = new JTable(tableModel);
        reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setPreferredSize(new Dimension(900, 500));
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        
        // Auto-generate report on creation
        generateReport();
    }
    
    private void generateReportActionPerformed(ActionEvent evt) {
        generateReport();
    }
    
    private void generateReport() {
        try {
            tableModel.setRowCount(0); // Clear existing data
            
            List<Map<String, Object>> supplierData = reportService.generateSupplierDetailsReport();
            
            for (Map<String, Object> supplier : supplierData) {
                Object[] row = {
                    supplier.get("supplier_id"),
                    supplier.get("supplier_name"),
                    supplier.get("phone"),
                    supplier.get("address"),
                    supplier.get("total_orders"),
                    supplier.get("formatted_total_purchases"),
                    supplier.get("formatted_outstanding_payments"),
                    supplier.get("formatted_last_purchase_date")
                };
                tableModel.addRow(row);
            }
            
            // Show summary
            JOptionPane.showMessageDialog(this, 
                "Supplier report generated successfully!\nTotal suppliers: " + supplierData.size(),
                "Report Generated", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error generating supplier report: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}