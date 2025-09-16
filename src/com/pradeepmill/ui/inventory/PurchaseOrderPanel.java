package com.pradeepmill.ui.inventory;

import com.pradeepmill.dao.PurchaseOrderDAO;
import com.pradeepmill.models.PurchaseOrder;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class PurchaseOrderPanel extends javax.swing.JPanel {

    private PurchaseOrderDAO purchaseOrderDAO;
    private JTable purchaseTable;
    private DefaultTableModel tableModel;

    public PurchaseOrderPanel() {
        this.purchaseOrderDAO = new PurchaseOrderDAO();
        initComponents();
        loadPurchaseOrders();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(72, 61, 139)); // Dark Slate Blue
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Purchase Order Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton newOrderButton = new JButton("New Purchase Order");
        JButton viewOrderButton = new JButton("View Details");
        JButton paymentButton = new JButton("Update Payment");
        JButton refreshButton = new JButton("Refresh");
        JButton searchButton = new JButton("Search");
        
        newOrderButton.addActionListener(this::newOrderActionPerformed);
        viewOrderButton.addActionListener(this::viewOrderActionPerformed);
        paymentButton.addActionListener(this::paymentActionPerformed);
        refreshButton.addActionListener(this::refreshActionPerformed);
        searchButton.addActionListener(this::searchActionPerformed);
        
        buttonPanel.add(newOrderButton);
        buttonPanel.add(viewOrderButton);
        buttonPanel.add(paymentButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(searchButton);
        
        // Table setup
        String[] columnNames = {"Purchase #", "Date", "Supplier", "Total Qty", "Total Amount", "Paid Amount", "Outstanding", "Payment Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        purchaseTable = new JTable(tableModel);
        purchaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        purchaseTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        purchaseTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Purchase #
        purchaseTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Date
        purchaseTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Supplier
        purchaseTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Qty
        purchaseTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Amount
        purchaseTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Paid
        purchaseTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Outstanding
        purchaseTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Status
        
        JScrollPane scrollPane = new JScrollPane(purchaseTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Purchase Orders"));
        
        JLabel statusLabel = new JLabel("Payment Status:");
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{
            "All Status", 
            PurchaseOrder.PAYMENT_PENDING,
            PurchaseOrder.PAYMENT_PARTIAL,
            PurchaseOrder.PAYMENT_PAID
        });
        
        JButton filterButton = new JButton("Apply Filter");
        JButton resetButton = new JButton("Reset Filter");
        JButton outstandingButton = new JButton("Show Outstanding");
        
        filterButton.addActionListener(e -> applyStatusFilter((String) statusFilter.getSelectedItem()));
        resetButton.addActionListener(e -> {
            statusFilter.setSelectedIndex(0);
            loadPurchaseOrders();
        });
        outstandingButton.addActionListener(e -> showOutstandingOrders());
        
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilter);
        filterPanel.add(filterButton);
        filterPanel.add(resetButton);
        filterPanel.add(outstandingButton);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(filterPanel, BorderLayout.CENTER);
        contentPanel.add(scrollPane, BorderLayout.SOUTH);
        
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void loadPurchaseOrders() {
        try {
            tableModel.setRowCount(0);
            
            List<PurchaseOrder> orders = purchaseOrderDAO.getAllPurchaseOrders();
            
            for (PurchaseOrder order : orders) {
                Object[] row = {
                    order.getPurchaseNumber(),
                    order.getPurchaseDate().toString(),
                    order.getSupplierName() != null ? order.getSupplierName() : "Unknown Supplier",
                    String.format("%.2f kg", order.getTotalQuantity()),
                    String.format("Rs. %.2f", order.getTotalAmount()),
                    String.format("Rs. %.2f", order.getPaidAmount()),
                    String.format("Rs. %.2f", order.getOutstandingAmount()),
                    order.getPaymentStatus()
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading purchase orders: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void applyStatusFilter(String status) {
        if ("All Status".equals(status)) {
            loadPurchaseOrders();
            return;
        }
        
        try {
            tableModel.setRowCount(0);
            
            List<PurchaseOrder> orders = purchaseOrderDAO.getPurchaseOrdersByPaymentStatus(status);
            
            for (PurchaseOrder order : orders) {
                Object[] row = {
                    order.getPurchaseNumber(),
                    order.getPurchaseDate().toString(),
                    order.getSupplierName() != null ? order.getSupplierName() : "Unknown Supplier",
                    String.format("%.2f kg", order.getTotalQuantity()),
                    String.format("Rs. %.2f", order.getTotalAmount()),
                    String.format("Rs. %.2f", order.getPaidAmount()),
                    String.format("Rs. %.2f", order.getOutstandingAmount()),
                    order.getPaymentStatus()
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error filtering purchase orders: " + e.getMessage(), 
                "Filter Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showOutstandingOrders() {
        try {
            tableModel.setRowCount(0);
            
            List<PurchaseOrder> orders = purchaseOrderDAO.getOutstandingPurchases();
            
            for (PurchaseOrder order : orders) {
                Object[] row = {
                    order.getPurchaseNumber(),
                    order.getPurchaseDate().toString(),
                    order.getSupplierName() != null ? order.getSupplierName() : "Unknown Supplier",
                    String.format("%.2f kg", order.getTotalQuantity()),
                    String.format("Rs. %.2f", order.getTotalAmount()),
                    String.format("Rs. %.2f", order.getPaidAmount()),
                    String.format("Rs. %.2f", order.getOutstandingAmount()),
                    order.getPaymentStatus()
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading outstanding orders: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
       
    private void viewOrderActionPerformed(ActionEvent evt) {
        int selectedRow = purchaseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a purchase order to view.");
            return;
        }
        
        String purchaseNumber = (String) tableModel.getValueAt(selectedRow, 0);
        JOptionPane.showMessageDialog(this, 
            "View Purchase Order Details: " + purchaseNumber + "\n" +
            "This will show line items, supplier details, and payment history.",
            "Purchase Order Details", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void paymentActionPerformed(ActionEvent evt) {
        int selectedRow = purchaseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a purchase order to update payment.");
            return;
        }
        
        String purchaseNumber = (String) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);
        
        if ("Paid".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, 
                "This purchase order is already fully paid.",
                "Payment Complete", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this, 
            "Update Payment for: " + purchaseNumber + "\n" +
            "Current Status: " + currentStatus + "\n" +
            "Payment update functionality will be implemented next.",
            "Update Payment", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void searchActionPerformed(ActionEvent evt) {
        String keyword = JOptionPane.showInputDialog(this, 
            "Enter search keyword (Purchase #, Supplier name, or Notes):",
            "Search Purchase Orders",
            JOptionPane.QUESTION_MESSAGE);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            try {
                tableModel.setRowCount(0);
                
                List<PurchaseOrder> orders = purchaseOrderDAO.searchPurchaseOrders(keyword.trim());
                
                for (PurchaseOrder order : orders) {
                    Object[] row = {
                        order.getPurchaseNumber(),
                        order.getPurchaseDate().toString(),
                        order.getSupplierName() != null ? order.getSupplierName() : "Unknown Supplier",
                        String.format("%.2f kg", order.getTotalQuantity()),
                        String.format("Rs. %.2f", order.getTotalAmount()),
                        String.format("Rs. %.2f", order.getPaidAmount()),
                        String.format("Rs. %.2f", order.getOutstandingAmount()),
                        order.getPaymentStatus()
                    };
                    tableModel.addRow(row);
                }
                
                if (orders.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "No purchase orders found matching: " + keyword,
                        "Search Results", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error searching purchase orders: " + e.getMessage(), 
                    "Search Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshActionPerformed(ActionEvent evt) {
        loadPurchaseOrders();
        JOptionPane.showMessageDialog(this, "Purchase orders refreshed!");
        
    }
    
    private void newOrderActionPerformed(ActionEvent evt) {
    try {
        AddPurchaseOrderDialog dialog = new AddPurchaseOrderDialog(
            (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this), 
            true
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadPurchaseOrders(); // Refresh the table
            JOptionPane.showMessageDialog(this, "Purchase order created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error creating purchase order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }
}