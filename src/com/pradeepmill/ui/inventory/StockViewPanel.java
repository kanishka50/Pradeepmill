package com.pradeepmill.ui.inventory;

import com.pradeepmill.dao.StockInventoryDAO;
import com.pradeepmill.models.StockInventory;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

public class StockViewPanel extends javax.swing.JPanel {

    private StockInventoryDAO stockDAO;
    private JTable stockTable;
    private DefaultTableModel tableModel;
    private JLabel totalValueLabel;
    private JLabel totalProductsLabel;
    private JLabel lowStockAlertLabel;
    private JLabel outOfStockAlertLabel;

    public StockViewPanel() {
        this.stockDAO = new StockInventoryDAO();
        initComponents();
        loadStockData();
        updateStatistics();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel - Red/Orange theme for alerts
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(255, 87, 34)); // Deep Orange
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Stock Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Statistics panel
        JPanel statsPanel = createStatisticsPanel();
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton refreshButton = new JButton("Refresh Stock");
        JButton lowStockButton = new JButton("Low Stock Items");
        JButton outOfStockButton = new JButton("Out of Stock");
        JButton searchButton = new JButton("Search");
        JButton adjustStockButton = new JButton("Adjust Stock Levels");
        JButton reorderButton = new JButton("Reorder Report");
        
        refreshButton.addActionListener(this::refreshActionPerformed);
        lowStockButton.addActionListener(this::lowStockActionPerformed);
        outOfStockButton.addActionListener(this::outOfStockActionPerformed);
        searchButton.addActionListener(this::searchActionPerformed);
        adjustStockButton.addActionListener(this::adjustStockActionPerformed);
        reorderButton.addActionListener(this::reorderActionPerformed);
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(lowStockButton);
        buttonPanel.add(outOfStockButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(adjustStockButton);
        buttonPanel.add(reorderButton);
        
        // Table setup
        String[] columnNames = {"Product Name", "Type", "Current Stock", "Min Level", "Max Level", "Unit Price", "Stock Value", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        stockTable = new JTable(tableModel);
        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stockTable.getTableHeader().setReorderingAllowed(false);
        stockTable.setRowHeight(25);
        
        // Set column widths
        stockTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Product Name
        stockTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Type
        stockTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Current Stock
        stockTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Min Level
        stockTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Max Level
        stockTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Unit Price
        stockTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Stock Value
        stockTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Status
        
        // Custom cell renderer for status column
        stockTable.getColumn("Status").setCellRenderer(new StatusCellRenderer());
        stockTable.getColumn("Current Stock").setCellRenderer(new StockLevelCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter by Product Type"));
        
        JComboBox<String> typeFilter = new JComboBox<>(new String[]{
            "All Types", "Raw_Paddy", "Finished_Rice", "By_Product"
        });
        
        JButton filterButton = new JButton("Apply Filter");
        JButton resetFilterButton = new JButton("Reset Filter");
        
        filterButton.addActionListener(e -> applyTypeFilter((String) typeFilter.getSelectedItem()));
        resetFilterButton.addActionListener(e -> {
            typeFilter.setSelectedIndex(0);
            loadStockData();
        });
        
        filterPanel.add(new JLabel("Product Type:"));
        filterPanel.add(typeFilter);
        filterPanel.add(filterButton);
        filterPanel.add(resetFilterButton);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(buttonPanel, BorderLayout.CENTER);
        contentPanel.add(filterPanel, BorderLayout.SOUTH);
        
        // Combined panel for content and table
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(contentPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Inventory Statistics"));
        panel.setPreferredSize(new Dimension(0, 100));
        
        // Total Products
        JPanel totalProductsPanel = createStatCard("Total Products", "0", new Color(33, 150, 243));
        totalProductsLabel = (JLabel) ((JPanel) totalProductsPanel.getComponent(1)).getComponent(0);
        
        // Total Value
        JPanel totalValuePanel = createStatCard("Total Value", "Rs. 0.00", new Color(76, 175, 80));
        totalValueLabel = (JLabel) ((JPanel) totalValuePanel.getComponent(1)).getComponent(0);
        
        // Low Stock Alert
        JPanel lowStockPanel = createStatCard("Low Stock Items", "0", new Color(255, 152, 0));
        lowStockAlertLabel = (JLabel) ((JPanel) lowStockPanel.getComponent(1)).getComponent(0);
        
        // Out of Stock Alert
        JPanel outOfStockPanel = createStatCard("Out of Stock", "0", new Color(244, 67, 54));
        outOfStockAlertLabel = (JLabel) ((JPanel) outOfStockPanel.getComponent(1)).getComponent(0);
        
        panel.add(totalProductsPanel);
        panel.add(totalValuePanel);
        panel.add(lowStockPanel);
        panel.add(outOfStockPanel);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);
        
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        valuePanel.setBackground(Color.WHITE);
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(color);
        valuePanel.add(valueLabel);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadStockData() {
        try {
            tableModel.setRowCount(0);
            
            List<StockInventory> stockItems = stockDAO.getAllStockItems();
            
            for (StockInventory stock : stockItems) {
                Object[] row = {
                    stock.getProductName(),
                    stock.getProductType(),
                    String.format("%.2f %s", stock.getCurrentQuantity(), stock.getUnit()),
                    String.format("%.2f", stock.getMinimumLevel()),
                    String.format("%.2f", stock.getMaximumLevel()),
                    String.format("Rs. %.2f", stock.getUnitPrice()),
                    String.format("Rs. %.2f", stock.getStockValue()),
                    stock.getStockStatus()
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading stock data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStatistics() {
        try {
            Map<String, Object> stats = stockDAO.getInventoryStatistics();
            
            totalProductsLabel.setText(stats.get("totalProducts").toString());
            totalValueLabel.setText(String.format("Rs. %.2f", (Double) stats.get("totalValue")));
            lowStockAlertLabel.setText(stats.get("lowStock").toString());
            outOfStockAlertLabel.setText(stats.get("outOfStock").toString());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void applyTypeFilter(String productType) {
        if ("All Types".equals(productType)) {
            loadStockData();
            return;
        }
        
        try {
            tableModel.setRowCount(0);
            
            List<StockInventory> stockItems = stockDAO.getStockByProductType(productType);
            
            for (StockInventory stock : stockItems) {
                Object[] row = {
                    stock.getProductName(),
                    stock.getProductType(),
                    String.format("%.2f %s", stock.getCurrentQuantity(), stock.getUnit()),
                    String.format("%.2f", stock.getMinimumLevel()),
                    String.format("%.2f", stock.getMaximumLevel()),
                    String.format("Rs. %.2f", stock.getUnitPrice()),
                    String.format("Rs. %.2f", stock.getStockValue()),
                    stock.getStockStatus()
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error filtering stock data: " + e.getMessage(), 
                "Filter Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshActionPerformed(ActionEvent evt) {
        loadStockData();
        updateStatistics();
        JOptionPane.showMessageDialog(this, "Stock data refreshed successfully!", "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void lowStockActionPerformed(ActionEvent evt) {
        try {
            tableModel.setRowCount(0);
            
            List<StockInventory> lowStockItems = stockDAO.getLowStockItems();
            
            for (StockInventory stock : lowStockItems) {
                Object[] row = {
                    stock.getProductName(),
                    stock.getProductType(),
                    String.format("%.2f %s", stock.getCurrentQuantity(), stock.getUnit()),
                    String.format("%.2f", stock.getMinimumLevel()),
                    String.format("%.2f", stock.getMaximumLevel()),
                    String.format("Rs. %.2f", stock.getUnitPrice()),
                    String.format("Rs. %.2f", stock.getStockValue()),
                    stock.getStockStatus()
                };
                tableModel.addRow(row);
            }
            
            if (lowStockItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No low stock items found!", "Good News", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    lowStockItems.size() + " low stock items found. Consider reordering soon.", 
                    "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading low stock items: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void outOfStockActionPerformed(ActionEvent evt) {
        try {
            tableModel.setRowCount(0);
            
            List<StockInventory> outOfStockItems = stockDAO.getOutOfStockItems();
            
            for (StockInventory stock : outOfStockItems) {
                Object[] row = {
                    stock.getProductName(),
                    stock.getProductType(),
                    String.format("%.2f %s", stock.getCurrentQuantity(), stock.getUnit()),
                    String.format("%.2f", stock.getMinimumLevel()),
                    String.format("%.2f", stock.getMaximumLevel()),
                    String.format("Rs. %.2f", stock.getUnitPrice()),
                    String.format("Rs. %.2f", stock.getStockValue()),
                    stock.getStockStatus()
                };
                tableModel.addRow(row);
            }
            
            if (outOfStockItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No out of stock items!", "All Good", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    outOfStockItems.size() + " items are out of stock. Urgent reordering required!", 
                    "Critical Stock Alert", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading out of stock items: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchActionPerformed(ActionEvent evt) {
        String keyword = JOptionPane.showInputDialog(this, 
            "Enter search keyword (Product name or type):",
            "Search Stock",
            JOptionPane.QUESTION_MESSAGE);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            try {
                tableModel.setRowCount(0);
                
                List<StockInventory> stockItems = stockDAO.searchStockItems(keyword.trim());
                
                for (StockInventory stock : stockItems) {
                    Object[] row = {
                        stock.getProductName(),
                        stock.getProductType(),
                        String.format("%.2f %s", stock.getCurrentQuantity(), stock.getUnit()),
                        String.format("%.2f", stock.getMinimumLevel()),
                        String.format("%.2f", stock.getMaximumLevel()),
                        String.format("Rs. %.2f", stock.getUnitPrice()),
                        String.format("Rs. %.2f", stock.getStockValue()),
                        stock.getStockStatus()
                    };
                    tableModel.addRow(row);
                }
                
                if (stockItems.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "No stock items found matching: " + keyword,
                        "Search Results", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error searching stock items: " + e.getMessage(), 
                    "Search Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void adjustStockActionPerformed(ActionEvent evt) {
        int selectedRow = stockTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a stock item to adjust levels.");
            return;
        }
        
        String productName = (String) tableModel.getValueAt(selectedRow, 0);
        JOptionPane.showMessageDialog(this, 
            "Adjust Stock Levels for: " + productName + "\n" +
            "Stock level adjustment functionality will be implemented next.\n" +
            "This will allow setting minimum and maximum stock levels.",
            "Adjust Stock Levels", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void reorderActionPerformed(ActionEvent evt) {
        try {
            List<StockInventory> lowStockItems = stockDAO.getLowStockItems();
            List<StockInventory> outOfStockItems = stockDAO.getOutOfStockItems();
            
            StringBuilder report = new StringBuilder("Reorder Report:\n\n");
            
            if (!outOfStockItems.isEmpty()) {
                report.append("URGENT - Out of Stock Items:\n");
                for (StockInventory item : outOfStockItems) {
                    report.append(String.format("- %s: %.2f %s (Min: %.2f)\n", 
                        item.getProductName(), item.getCurrentQuantity(), 
                        item.getUnit(), item.getMinimumLevel()));
                }
                report.append("\n");
            }
            
            if (!lowStockItems.isEmpty()) {
                report.append("Low Stock Items:\n");
                for (StockInventory item : lowStockItems) {
                    report.append(String.format("- %s: %.2f %s (Min: %.2f)\n", 
                        item.getProductName(), item.getCurrentQuantity(), 
                        item.getUnit(), item.getMinimumLevel()));
                }
            }
            
            if (outOfStockItems.isEmpty() && lowStockItems.isEmpty()) {
                report.append("All items are adequately stocked!");
            }
            
            JOptionPane.showMessageDialog(this, report.toString(), "Reorder Report", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating reorder report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Custom cell renderer for status column
    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                String status = (String) value;
                switch (status) {
                    case "Out of Stock":
                        c.setBackground(new Color(255, 205, 210)); // Light Red
                        c.setForeground(Color.RED);
                        break;
                    case "Low Stock":
                        c.setBackground(new Color(255, 243, 224)); // Light Orange
                        c.setForeground(new Color(255, 152, 0));
                        break;
                    case "Normal":
                        c.setBackground(new Color(232, 245, 233)); // Light Green
                        c.setForeground(new Color(76, 175, 80));
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                }
            }
            
            return c;
        }
    }
    
    // Custom cell renderer for stock level column
    class StockLevelCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                String status = (String) tableModel.getValueAt(row, 7); // Status column
                switch (status) {
                    case "Out of Stock":
                        c.setForeground(Color.RED);
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    case "Low Stock":
                        c.setForeground(new Color(255, 152, 0));
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    default:
                        c.setForeground(Color.BLACK);
                        setFont(getFont().deriveFont(Font.PLAIN));
                }
            }
            
            return c;
        }
    }
}