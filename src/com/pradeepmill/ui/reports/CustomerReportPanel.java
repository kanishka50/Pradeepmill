package com.pradeepmill.ui.reports;

import com.pradeepmill.services.ReportService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

public class CustomerReportPanel extends javax.swing.JPanel {

    private ReportService reportService;
    private JTable reportTable;
    private DefaultTableModel tableModel;

    public CustomerReportPanel() {
        this.reportService = new ReportService();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(25, 25, 112));
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Customer Details Report", SwingConstants.CENTER);
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
        String[] columnNames = {"Customer ID", "Customer Name", "Phone", "Address", "Type", 
                               "Total Orders", "Total Purchases", "Outstanding Balance", "Last Purchase"};
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
            
            List<Map<String, Object>> customerData = reportService.generateCustomerDetailsReport();
            
            for (Map<String, Object> customer : customerData) {
                Object[] row = {
                    customer.get("customer_id"),
                    customer.get("customer_name"),
                    customer.get("phone"),
                    customer.get("address"),
                    customer.get("customer_type"),
                    customer.get("total_orders"),
                    customer.get("formatted_total_purchases"),
                    customer.get("formatted_outstanding_balance"),
                    customer.get("last_purchase_date")
                };
                tableModel.addRow(row);
            }
            
            // Show summary
            JOptionPane.showMessageDialog(this, 
                "Customer report generated successfully!\nTotal customers: " + customerData.size(),
                "Report Generated", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error generating customer report: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}