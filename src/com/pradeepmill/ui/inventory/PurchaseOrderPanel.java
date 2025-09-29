package com.pradeepmill.ui.inventory;

import com.pradeepmill.dao.PurchaseOrderDAO;
import com.pradeepmill.models.PurchaseOrder;
import com.pradeepmill.ui.dialogs.PaymentUpdateDialog;
import com.pradeepmill.ui.inventory.AddPurchaseOrderDialog;  // FIXED: Import your existing dialog
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
        
        // Title panel - Using your system's teal theme
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 128, 128)); // Teal - matching your screenshots
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Purchase Order Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Button panel - Simple clean buttons
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
        String[] columnNames = {
            "Purchase #", "Date", "Supplier", "Total Qty", 
            "Total Amount", "Paid Amount", "Outstanding", "Status"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        purchaseTable = new JTable(tableModel);
        purchaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        purchaseTable.setRowHeight(25);
        purchaseTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        purchaseTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Purchase #
        purchaseTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Date
        purchaseTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Supplier
        purchaseTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Total Qty
        purchaseTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Total Amount
        purchaseTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Paid Amount
        purchaseTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Outstanding
        purchaseTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Status
        
        JScrollPane scrollPane = new JScrollPane(purchaseTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        
        // Filter panel - Clean simple design
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        
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
    
    // FIXED: Proper integration with your existing AddPurchaseOrderDialog
    private void newOrderActionPerformed(ActionEvent evt) {
        try {
            // Create and show your existing AddPurchaseOrderDialog
            AddPurchaseOrderDialog dialog = new AddPurchaseOrderDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                true
            );
            dialog.setVisible(true);
            
            // Refresh table if order was saved
            if (dialog.isSaved()) {
                loadPurchaseOrders();
                JOptionPane.showMessageDialog(this,
                    "Purchase order created successfully!\nTable has been refreshed.",
                    "Order Created",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error opening purchase order dialog: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewOrderActionPerformed(ActionEvent evt) {
        int selectedRow = purchaseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a purchase order to view.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String purchaseNumber = (String) tableModel.getValueAt(selectedRow, 0);
        JOptionPane.showMessageDialog(this, 
            "View Purchase Order Details: " + purchaseNumber + "\n" +
            "This will show line items, supplier details, and payment history.\n" +
            "(Detailed view dialog can be implemented next)",
            "Purchase Order Details", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // FIXED: Proper payment update functionality
    private void paymentActionPerformed(ActionEvent evt) {
        int selectedRow = purchaseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a purchase order to update payment.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String purchaseNumber = (String) tableModel.getValueAt(selectedRow, 0);
            String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);
            
            if ("Paid".equals(currentStatus)) {
                int choice = JOptionPane.showConfirmDialog(this, 
                    "This purchase order is already fully paid.\n" +
                    "Do you want to view the payment details?",
                    "Payment Complete", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (choice == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(this,
                        "Payment history for " + purchaseNumber + " can be shown here.\n" +
                        "(Payment history dialog can be implemented later)",
                        "Payment History",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }
            
            // Get the full purchase order details
            PurchaseOrder order = purchaseOrderDAO.findPurchaseOrderByNumber(purchaseNumber);
            if (order == null) {
                JOptionPane.showMessageDialog(this,
                    "Purchase order not found: " + purchaseNumber,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Open payment dialog
            PaymentUpdateDialog dialog = new PaymentUpdateDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                order
            );
            dialog.setVisible(true);
            
            // Refresh table if payment was updated
            if (dialog.isUpdated()) {
                loadPurchaseOrders();
                JOptionPane.showMessageDialog(this,
                    "Payment updated successfully!\nPurchase order table has been refreshed.",
                    "Payment Updated",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "An error occurred while opening payment dialog: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
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
        JOptionPane.showMessageDialog(this, 
            "Purchase orders refreshed successfully!",
            "Refresh Complete", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}