package com.pradeepmill.ui.production;

import com.pradeepmill.dao.ProductionRecordDAO;
import com.pradeepmill.models.ProductionRecord;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

public class ProductionManagementPanel extends javax.swing.JPanel {

    private ProductionRecordDAO productionRecordDAO;
    private JTable productionTable;
    private DefaultTableModel tableModel;

    public ProductionManagementPanel() {
        this.productionRecordDAO = new ProductionRecordDAO();
        initComponents();
        loadProductionRecords();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel - Orange theme for production
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(255, 140, 0)); // Dark Orange
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Production Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton newProductionButton = new JButton("New Production Record");
        JButton viewProductionButton = new JButton("View Details");
        JButton editProductionButton = new JButton("Edit Record");
        JButton refreshButton = new JButton("Refresh");
        JButton searchButton = new JButton("Search");
        JButton efficiencyButton = new JButton("Machine Efficiency");
        
        newProductionButton.addActionListener(this::newProductionActionPerformed);
        viewProductionButton.addActionListener(this::viewProductionActionPerformed);
        editProductionButton.addActionListener(this::editProductionActionPerformed);
        refreshButton.addActionListener(this::refreshActionPerformed);
        searchButton.addActionListener(this::searchActionPerformed);
        efficiencyButton.addActionListener(this::efficiencyActionPerformed);
        
        buttonPanel.add(newProductionButton);
        buttonPanel.add(viewProductionButton);
        buttonPanel.add(editProductionButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(efficiencyButton);
        
        // Table setup
        String[] columnNames = {"Production #", "Date", "Raw Material", "Finished Product", "Input (kg)", "Output (kg)", "Waste (kg)", "Efficiency (%)", "Machine", "Operator"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        productionTable = new JTable(tableModel);
        productionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productionTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        productionTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Production #
        productionTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Date
        productionTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Raw Material
        productionTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Finished Product
        productionTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Input
        productionTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Output
        productionTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Waste
        productionTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Efficiency
        productionTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Machine
        productionTable.getColumnModel().getColumn(9).setPreferredWidth(100); // Operator
        
        JScrollPane scrollPane = new JScrollPane(productionTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Production Records"));
        
        JLabel dateLabel = new JLabel("Date Range:");
        JSpinner fromDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner toDateSpinner = new JSpinner(new SpinnerDateModel());
        
        JSpinner.DateEditor fromEditor = new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd");
        JSpinner.DateEditor toEditor = new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd");
        fromDateSpinner.setEditor(fromEditor);
        toDateSpinner.setEditor(toEditor);
        
        JButton filterButton = new JButton("Apply Date Filter");
        JButton resetButton = new JButton("Reset Filter");
        JButton todayButton = new JButton("Today's Production");
        
        filterButton.addActionListener(e -> applyDateFilter(
            (java.util.Date) fromDateSpinner.getValue(),
            (java.util.Date) toDateSpinner.getValue()
        ));
        resetButton.addActionListener(e -> {
            loadProductionRecords();
        });
        todayButton.addActionListener(e -> showTodaysProduction());
        
        filterPanel.add(dateLabel);
        filterPanel.add(new JLabel("From:"));
        filterPanel.add(fromDateSpinner);
        filterPanel.add(new JLabel("To:"));
        filterPanel.add(toDateSpinner);
        filterPanel.add(filterButton);
        filterPanel.add(resetButton);
        filterPanel.add(todayButton);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(filterPanel, BorderLayout.CENTER);
        contentPanel.add(scrollPane, BorderLayout.SOUTH);
        
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void loadProductionRecords() {
        try {
            tableModel.setRowCount(0);
            
            List<ProductionRecord> records = productionRecordDAO.getAllProductionRecords();
            
            for (ProductionRecord record : records) {
                Object[] row = {
                    record.getProductionNumber(),
                    record.getProductionDate().toString(),
                    record.getRawProductName() != null ? record.getRawProductName() : "Unknown Product",
                    record.getFinishedProductName() != null ? record.getFinishedProductName() : "Unknown Product",
                    String.format("%.2f", record.getInputQuantity()),
                    String.format("%.2f", record.getOutputQuantity()),
                    String.format("%.2f", record.getWasteQuantity()),
                    String.format("%.1f%%", record.getConversionRate()),
                    record.getMachineName() != null ? record.getMachineName() : "Manual",
                    record.getOperatorName() != null ? record.getOperatorName() : "Unknown"
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading production records: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void applyDateFilter(java.util.Date fromDate, java.util.Date toDate) {
        try {
            tableModel.setRowCount(0);
            
            List<ProductionRecord> records = productionRecordDAO.getProductionRecordsByDateRange(fromDate, toDate);
            
            for (ProductionRecord record : records) {
                Object[] row = {
                    record.getProductionNumber(),
                    record.getProductionDate().toString(),
                    record.getRawProductName() != null ? record.getRawProductName() : "Unknown Product",
                    record.getFinishedProductName() != null ? record.getFinishedProductName() : "Unknown Product",
                    String.format("%.2f", record.getInputQuantity()),
                    String.format("%.2f", record.getOutputQuantity()),
                    String.format("%.2f", record.getWasteQuantity()),
                    String.format("%.1f%%", record.getConversionRate()),
                    record.getMachineName() != null ? record.getMachineName() : "Manual",
                    record.getOperatorName() != null ? record.getOperatorName() : "Unknown"
                };
                tableModel.addRow(row);
            }
            
            if (records.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No production records found in the selected date range.",
                    "No Records", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error filtering production records: " + e.getMessage(), 
                "Filter Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showTodaysProduction() {
        java.util.Date today = new java.util.Date();
        applyDateFilter(today, today);
    }
    
    private void newProductionActionPerformed(ActionEvent evt) {
        try {
            AddProductionRecordDialog dialog = new AddProductionRecordDialog(
                (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this), 
                true
            );
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                loadProductionRecords();
                JOptionPane.showMessageDialog(this, "Production record created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating production record: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewProductionActionPerformed(ActionEvent evt) {
        int selectedRow = productionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a production record to view.");
            return;
        }
        
        String productionNumber = (String) tableModel.getValueAt(selectedRow, 0);
        JOptionPane.showMessageDialog(this, 
            "View Production Record Details: " + productionNumber + "\n" +
            "This will show complete production details, machine usage, and efficiency metrics.",
            "Production Record Details", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void editProductionActionPerformed(ActionEvent evt) {
        int selectedRow = productionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a production record to edit.");
            return;
        }
        
        String productionNumber = (String) tableModel.getValueAt(selectedRow, 0);
        JOptionPane.showMessageDialog(this, 
            "Edit Production Record: " + productionNumber + "\n" +
            "Production record editing functionality will be implemented next.",
            "Edit Production Record", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void efficiencyActionPerformed(ActionEvent evt) {
        try {
            List<Map<String, Object>> efficiency = productionRecordDAO.getProductionEfficiencyByMachine();
            
            if (efficiency.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No production data available for efficiency analysis.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            StringBuilder report = new StringBuilder("Machine Efficiency Report:\n\n");
            for (Map<String, Object> machine : efficiency) {
                report.append(String.format("Machine: %s\n", machine.get("machineName")));
                report.append(String.format("Productions: %d\n", machine.get("productionCount")));
                report.append(String.format("Average Efficiency: %.1f%%\n", (Double) machine.get("avgEfficiency")));
                report.append(String.format("Total Processed: %.2f kg\n\n", (Double) machine.get("totalProcessed")));
            }
            
            JOptionPane.showMessageDialog(this, report.toString(), "Machine Efficiency Report", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating efficiency report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchActionPerformed(ActionEvent evt) {
        String keyword = JOptionPane.showInputDialog(this, 
            "Enter search keyword (Production #, Product name, Machine, Operator, or Notes):",
            "Search Production Records",
            JOptionPane.QUESTION_MESSAGE);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            try {
                tableModel.setRowCount(0);
                
                List<ProductionRecord> records = productionRecordDAO.searchProductionRecords(keyword.trim());
                
                for (ProductionRecord record : records) {
                    Object[] row = {
                        record.getProductionNumber(),
                        record.getProductionDate().toString(),
                        record.getRawProductName() != null ? record.getRawProductName() : "Unknown Product",
                        record.getFinishedProductName() != null ? record.getFinishedProductName() : "Unknown Product",
                        String.format("%.2f", record.getInputQuantity()),
                        String.format("%.2f", record.getOutputQuantity()),
                        String.format("%.2f", record.getWasteQuantity()),
                        String.format("%.1f%%", record.getConversionRate()),
                        record.getMachineName() != null ? record.getMachineName() : "Manual",
                        record.getOperatorName() != null ? record.getOperatorName() : "Unknown"
                    };
                    tableModel.addRow(row);
                }
                
                if (records.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "No production records found matching: " + keyword,
                        "Search Results", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error searching production records: " + e.getMessage(), 
                    "Search Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshActionPerformed(ActionEvent evt) {
        loadProductionRecords();
        JOptionPane.showMessageDialog(this, "Production records refreshed!");
    }
}