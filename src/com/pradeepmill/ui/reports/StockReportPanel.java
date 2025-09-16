package com.pradeepmill.ui.reports;

import com.pradeepmill.services.ReportService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

public class StockReportPanel extends javax.swing.JPanel {

    private ReportService reportService;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JLabel totalValueLabel;
    private JLabel lowStockCountLabel;

    public StockReportPanel() {
        this.reportService = new ReportService();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(139, 169, 19)); // Olive Green (matching your screenshot)
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Stock Report", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Button and summary panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton generateButton = new JButton("Generate Report");
        JButton refreshButton = new JButton("Refresh");
        JButton lowStockButton = new JButton("Low Stock Alert");
        JButton exportButton = new JButton("Export to PDF");
        
        generateButton.addActionListener(this::generateReportActionPerformed);
        refreshButton.addActionListener(this::generateReportActionPerformed);
        lowStockButton.addActionListener(this::showLowStockAlert);
        exportButton.addActionListener(this::exportToPDF);
        
        buttonPanel.add(generateButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(lowStockButton);
        buttonPanel.add(exportButton);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(Color.WHITE);
        
        totalValueLabel = new JLabel("Total Stock Value: Rs. 0.00");
        totalValueLabel.setFont(new Font("Arial", Font.BOLD, 12));
        totalValueLabel.setForeground(new Color(139, 169, 19));
        
        lowStockCountLabel = new JLabel("Low Stock Items: 0");
        lowStockCountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        lowStockCountLabel.setForeground(Color.RED);
        
        summaryPanel.add(totalValueLabel);
        summaryPanel.add(Box.createHorizontalStrut(20));
        summaryPanel.add(lowStockCountLabel);
        
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(summaryPanel, BorderLayout.EAST);
        
        // Table setup
        String[] columnNames = {"Product ID", "Product Name", "Type", "Grade", "Unit Price", 
                               "Current Quantity", "Min Level", "Max Level", "Stock Value", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reportTable = new JTable(tableModel);
        reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        reportTable.setRowHeight(25);
        
        // Custom cell renderer for status column
        reportTable.getColumnModel().getColumn(9).setCellRenderer(new StatusCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setPreferredSize(new Dimension(1000, 500));
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(topPanel, BorderLayout.NORTH);
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
            
            List<Map<String, Object>> stockData = reportService.generateStockReport();
            
            double totalStockValue = 0.0;
            int lowStockCount = 0;
            
            for (Map<String, Object> stock : stockData) {
                Object[] row = {
                    stock.get("product_id"),
                    stock.get("product_name"),
                    stock.get("product_type"),
                    stock.get("grade"),
                    stock.get("formatted_unit_price"),
                    stock.get("formatted_quantity") + " " + stock.get("unit"),
                    stock.get("minimum_level"),
                    stock.get("maximum_level"),
                    stock.get("formatted_stock_value"),
                    stock.get("status_indicator")
                };
                tableModel.addRow(row);
                
                // Calculate totals
                totalStockValue += (Double) stock.getOrDefault("stock_value", 0.0);
                if ("Low Stock".equals(stock.get("stock_status")) || "Out of Stock".equals(stock.get("stock_status"))) {
                    lowStockCount++;
                }
            }
            
            // Update summary labels
            totalValueLabel.setText("Total Stock Value: Rs. " + String.format("%,.2f", totalStockValue));
            lowStockCountLabel.setText("Low Stock Items: " + lowStockCount);
            
            // Show success message
            JOptionPane.showMessageDialog(this, 
                "Stock report generated successfully!\nTotal items: " + stockData.size() +
                "\nTotal value: Rs. " + String.format("%,.2f", totalStockValue),
                "Report Generated", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error generating stock report: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showLowStockAlert(ActionEvent evt) {
        try {
            // Filter and show only low stock items
            StringBuilder lowStockMessage = new StringBuilder();
            lowStockMessage.append("🔴 LOW STOCK ALERT:\n\n");
            
            List<Map<String, Object>> stockData = reportService.generateStockReport();
            boolean hasLowStock = false;
            
            for (Map<String, Object> stock : stockData) {
                String status = (String) stock.get("stock_status");
                if ("Low Stock".equals(status) || "Out of Stock".equals(status)) {
                    hasLowStock = true;
                    lowStockMessage.append("• ").append(stock.get("product_name"))
                                  .append(" (").append(stock.get("product_type")).append(")")
                                  .append(" - Current: ").append(stock.get("formatted_quantity"))
                                  .append(", Min: ").append(stock.get("minimum_level"))
                                  .append("\n");
                }
            }
            
            if (!hasLowStock) {
                lowStockMessage.append("✅ All items are adequately stocked!");
            }
            
            JOptionPane.showMessageDialog(this, 
                lowStockMessage.toString(),
                "Low Stock Alert", 
                hasLowStock ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error checking stock levels: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportToPDF(ActionEvent evt) {
        JOptionPane.showMessageDialog(this, 
            "PDF Export functionality will be implemented in future version.", 
            "Export to PDF", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Custom cell renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString();
                if (status.contains("Out of Stock")) {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (status.contains("Low Stock")) {
                    c.setForeground(new Color(255, 140, 0)); // Orange
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (status.contains("Overstock")) {
                    c.setForeground(new Color(255, 165, 0)); // Dark Orange
                } else {
                    c.setForeground(new Color(34, 139, 34)); // Green
                }
            }
            
            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
            } else {
                c.setBackground(Color.WHITE);
            }
            
            return c;
        }
    }
}