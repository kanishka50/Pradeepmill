package com.pradeepmill.ui;

import com.pradeepmill.database.DatabaseConnection;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import com.pradeepmill.services.DashboardService;
import java.util.Map;
import javax.swing.UIManager;

public class MainDashboard extends javax.swing.JFrame {

    private JPanel mainContentPanel;
    private JLabel statusLabel;
    private JLabel dateTimeLabel;
    private JLabel welcomeLabel;
    
    // Dashboard cards - moved to class level
    private JPanel suppliersCard;
    private JPanel customersCard;
    private JPanel stockCard;
    private JPanel salesCard;

    public MainDashboard() {
        initComponents();
        initializeDashboard();
    }

    private void initComponents() {
        // Set up the main frame
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Pradeep Rice Mill Management System");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Create menu bar
        createMenuBar();
        
        // Create main content area
        createMainContent();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Master Data Menu
        JMenu masterDataMenu = new JMenu("Master Data");
        masterDataMenu.setFont(new Font("Arial", Font.BOLD, 14));
        
        JMenuItem suppliersItem = new JMenuItem("Suppliers");
        suppliersItem.addActionListener(this::suppliersMenuActionPerformed);
        
        JMenuItem customersItem = new JMenuItem("Customers");
        customersItem.addActionListener(this::customersMenuActionPerformed);
        
        JMenuItem productsItem = new JMenuItem("Products");
        productsItem.addActionListener(this::productsMenuActionPerformed);
        
        JMenuItem staffItem = new JMenuItem("Staff");
        staffItem.addActionListener(this::staffMenuActionPerformed);
        
        JMenuItem machinesItem = new JMenuItem("Machines");
        machinesItem.addActionListener(this::machinesMenuActionPerformed);
        
        masterDataMenu.add(suppliersItem);
        masterDataMenu.add(customersItem);
        masterDataMenu.add(productsItem);
        masterDataMenu.add(staffItem);
        masterDataMenu.add(machinesItem);
        
        // Transactions Menu
        JMenu transactionsMenu = new JMenu("Transactions");
        transactionsMenu.setFont(new Font("Arial", Font.BOLD, 14));
        
        JMenuItem purchaseOrdersItem = new JMenuItem("Purchase Orders");
        purchaseOrdersItem.addActionListener(this::purchaseOrdersMenuActionPerformed);
        
        JMenuItem salesOrdersItem = new JMenuItem("Sales Orders");
        salesOrdersItem.addActionListener(this::salesOrdersMenuActionPerformed);
        
        JMenuItem productionItem = new JMenuItem("Production Records");
        productionItem.addActionListener(this::productionMenuActionPerformed);
        
        JMenuItem stockItem = new JMenuItem("Stock Management");
        stockItem.addActionListener(this::stockMenuActionPerformed);
        
        transactionsMenu.add(purchaseOrdersItem);
        transactionsMenu.add(salesOrdersItem);
        transactionsMenu.add(productionItem);
        transactionsMenu.add(stockItem);
        
        // Reports Menu
        JMenu reportsMenu = new JMenu("Reports");
        reportsMenu.setFont(new Font("Arial", Font.BOLD, 14));
        
        JMenuItem customerReportItem = new JMenuItem("Customer Details Report");
        customerReportItem.addActionListener(this::customerReportMenuActionPerformed);
        
        JMenuItem supplierReportItem = new JMenuItem("Supplier Details Report");
        supplierReportItem.addActionListener(this::supplierReportMenuActionPerformed);
        
        JMenuItem salaryReportItem = new JMenuItem("Monthly Salary Report");
        salaryReportItem.addActionListener(this::salaryReportMenuActionPerformed);
        
        JMenuItem stockReportItem = new JMenuItem("Stock Report");
        stockReportItem.addActionListener(this::stockReportMenuActionPerformed);
        
        reportsMenu.add(customerReportItem);
        reportsMenu.add(supplierReportItem);
        reportsMenu.add(salaryReportItem);
        reportsMenu.add(stockReportItem);
        
        // Settings Menu
        JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.setFont(new Font("Arial", Font.BOLD, 14));
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(this::aboutMenuActionPerformed);
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(this::exitMenuActionPerformed);
        
        settingsMenu.add(aboutItem);
        settingsMenu.addSeparator();
        settingsMenu.add(exitItem);
        
        // Add menus to menu bar
        menuBar.add(masterDataMenu);
        menuBar.add(transactionsMenu);
        menuBar.add(reportsMenu);
        menuBar.add(settingsMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createMainContent() {
        // Create main panel with BorderLayout
        setLayout(new BorderLayout());
        
        // Status bar at bottom
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.setPreferredSize(new Dimension(0, 30));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        dateTimeLabel = new JLabel();
        dateTimeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        dateTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(dateTimeLabel, BorderLayout.EAST);
        
        // Main content panel
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Welcome panel
        createWelcomePanel();
        
        // Add components to frame
        add(statusPanel, BorderLayout.SOUTH);
        add(mainContentPanel, BorderLayout.CENTER);
    }
    
    private void createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        
        // Title
        welcomeLabel = new JLabel("Welcome to Pradeep Rice Mill Management System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(50, 20, 30, 20));
        
        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Create stats cards and assign to class-level variables
        suppliersCard = createStatCard("Total Suppliers", "0", Color.BLUE);
        customersCard = createStatCard("Total Customers", "0", Color.GREEN);
        stockCard = createStatCard("Stock Items", "0", Color.ORANGE);
        salesCard = createStatCard("Stock Value", "Rs. 0.00", Color.RED);
        
        statsPanel.add(suppliersCard);
        statsPanel.add(customersCard);
        statsPanel.add(stockCard);
        statsPanel.add(salesCard);
        
        // Quick actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout());
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        
        JButton newPurchaseBtn = new JButton("New Purchase Order");
        JButton newSalesBtn = new JButton("New Sales Order");
        JButton viewStockBtn = new JButton("View Stock");
        JButton generateReportBtn = new JButton("Generate Report");
        
        // Style buttons
        newPurchaseBtn.setPreferredSize(new Dimension(150, 40));
        newSalesBtn.setPreferredSize(new Dimension(150, 40));
        viewStockBtn.setPreferredSize(new Dimension(150, 40));
        generateReportBtn.setPreferredSize(new Dimension(150, 40));
        
        actionsPanel.add(newPurchaseBtn);
        actionsPanel.add(newSalesBtn);
        actionsPanel.add(viewStockBtn);
        actionsPanel.add(generateReportBtn);
        
        // Add components to welcome panel
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        welcomePanel.add(statsPanel, BorderLayout.CENTER);
        welcomePanel.add(actionsPanel, BorderLayout.SOUTH);
        
        mainContentPanel.add(welcomePanel, BorderLayout.CENTER);
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(color);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void initializeDashboard() {
        updateDateTime();
        updateStatusBar("Application started successfully");
        loadDashboardStats();
    }
    
    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dateTimeLabel.setText(formatter.format(now));
    }
    
    private void updateStatusBar(String message) {
        statusLabel.setText(message);
    }
    
    private void loadDashboardStats() {
        try {
            DashboardService dashboardService = new DashboardService();
            Map<String, Object> stats = dashboardService.getAllDashboardMetrics();
            
            // Update dashboard cards with real data
            updateStatCard(suppliersCard, stats.get("totalSuppliers").toString());
            updateStatCard(customersCard, stats.get("totalCustomers").toString());
            updateStatCard(stockCard, stats.get("totalProducts").toString());
            
            // Format stock value properly
            double stockValue = (Double) stats.get("totalStockValue");
            updateStatCard(salesCard, String.format("Rs. %.2f", stockValue));
            
            // Update status bar with system health
            String systemHealth = (String) stats.get("systemHealth");
            String stockStatus = (String) stats.get("stockStatus");
            updateStatusBar(systemHealth + " | " + stockStatus);
            
        } catch (Exception e) {
            e.printStackTrace();
            updateStatusBar("Error loading dashboard data: " + e.getMessage());
            
            // Set default values on error
            updateStatCard(suppliersCard, "Error");
            updateStatCard(customersCard, "Error");
            updateStatCard(stockCard, "Error");
            updateStatCard(salesCard, "Error");
        }
    }
    
    private void updateStatCard(JPanel card, String newValue) {
        Component[] components = card.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                // Update the value label (larger font, bottom component)
                if (label.getFont().getSize() == 20) {
                    label.setText(newValue);
                    break;
                }
            }
        }
        card.revalidate();
        card.repaint();
    }
    
    private void showPanel(JPanel panel, String title) {
        mainContentPanel.removeAll();
        
        // Add title label
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        mainContentPanel.add(titleLabel, BorderLayout.NORTH);
        mainContentPanel.add(panel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
        
        updateStatusBar("Showing " + title);
    }
    
    // Menu Action Handlers
    private void suppliersMenuActionPerformed(ActionEvent evt) {
        try {
            showPanel(new com.pradeepmill.ui.suppliers.SupplierManagementPanel(), "Supplier Management");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Supplier Management module error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void customersMenuActionPerformed(ActionEvent evt) {
    try {
        showPanel(new com.pradeepmill.ui.customers.CustomerManagementPanel(), "Customer Management");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Customer Management module error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }
    
    private void productsMenuActionPerformed(ActionEvent evt) {
    try {
        showPanel(new com.pradeepmill.ui.inventory.InventoryManagementPanel(), "Product Management");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Product Management module error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }
    
private void staffMenuActionPerformed(ActionEvent evt) {
    try {
        showPanel(new com.pradeepmill.ui.staff.StaffManagementPanel(), "Staff Management");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Staff Management module error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
    
    private void machinesMenuActionPerformed(ActionEvent evt) {
        try {
            showPanel(new com.pradeepmill.ui.machines.MachineManagementPanel(), "Machine Management");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Machine Management module is not available yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
   private void purchaseOrdersMenuActionPerformed(ActionEvent evt) {
    try {
        showPanel(new com.pradeepmill.ui.inventory.PurchaseOrderPanel(), "Purchase Orders");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Purchase Orders module error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
    
    private void salesOrdersMenuActionPerformed(ActionEvent evt) {
    try {
        showPanel(new com.pradeepmill.ui.inventory.SalesOrderPanel(), "Sales Orders");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Sales Orders module error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }
    
    private void productionMenuActionPerformed(ActionEvent evt) {
    try {
        showPanel(new com.pradeepmill.ui.production.ProductionManagementPanel(), "Production Management");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Production Management module error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
    
    private void stockMenuActionPerformed(ActionEvent evt) {
    try {
        showPanel(new com.pradeepmill.ui.inventory.StockViewPanel(), "Stock Management");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Stock Management module error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
    
    private void customerReportMenuActionPerformed(ActionEvent evt) {
        try {
            showPanel(new com.pradeepmill.ui.reports.CustomerReportPanel(), "Customer Details Report");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Customer Report module error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void supplierReportMenuActionPerformed(ActionEvent evt) {
        try {
            showPanel(new com.pradeepmill.ui.reports.SupplierReportPanel(), "Supplier Details Report");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Supplier Report module is not available yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void salaryReportMenuActionPerformed(ActionEvent evt) {
        try {
            showPanel(new com.pradeepmill.ui.reports.SalaryReportPanel(), "Monthly Salary Report");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Salary Report module is not available yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void stockReportMenuActionPerformed(ActionEvent evt) {
        try {
            showPanel(new com.pradeepmill.ui.reports.StockReportPanel(), "Stock Report");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Stock Report module is not available yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void aboutMenuActionPerformed(ActionEvent evt) {
        JOptionPane.showMessageDialog(this,
            "Pradeep Rice Mill Management System\n" +
            "Version 1.0\n" +
            "Developed for NVQ 5 ICT Assignment\n" +
            "© 2024",
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exitMenuActionPerformed(ActionEvent evt) {
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            DatabaseConnection.closeConnection();
            System.exit(0);
        }
    }

    public static void main(String args[]) {
        try {
            // Use cross-platform look and feel instead of system look and feel
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeel());
        } catch (Exception ex) {
            // If that fails, use the default look and feel
            System.out.println("Could not set look and feel, using default");
        }

        SwingUtilities.invokeLater(() -> {
            new MainDashboard().setVisible(true);
        });
    }
}