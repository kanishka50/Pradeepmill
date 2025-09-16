package com.pradeepmill.ui.inventory;

import com.pradeepmill.dao.SalesOrderDAO;
import com.pradeepmill.models.SalesOrder;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SalesOrderPanel extends javax.swing.JPanel {

    private SalesOrderDAO salesOrderDAO;
    private JTable salesTable;
    private DefaultTableModel tableModel;

    public SalesOrderPanel() {
        this.salesOrderDAO = new SalesOrderDAO();
        initComponents();
        loadSalesOrders();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel - Green theme for sales
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(34, 139, 34)); // Forest Green
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Sales Order Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton newOrderButton = new JButton("New Sales Order");
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
        String[] columnNames = {"Sales #", "Date", "Customer", "Total Qty", "Total Amount", "Paid Amount", "Outstanding", "Payment Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        salesTable = new JTable(tableModel);
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        salesTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Sales #
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Date
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Customer
        salesTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Qty
        salesTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Amount
        salesTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Paid
        salesTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Outstanding
        salesTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Status
        
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Sales Orders"));
        
        JLabel statusLabel = new JLabel("Payment Status:");
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{
            "All Status", 
            SalesOrder.PAYMENT_PENDING,
            SalesOrder.PAYMENT_PARTIAL,
            SalesOrder.PAYMENT_PAID
        });
        
        JButton filterButton = new JButton("Apply Filter");
        JButton resetButton = new JButton("Reset Filter");
        JButton outstandingButton = new JButton("Show Outstanding");
        
        filterButton.addActionListener(e -> applyStatusFilter((String) statusFilter.getSelectedItem()));
        resetButton.addActionListener(e -> {
            statusFilter.setSelectedIndex(0);
            loadSalesOrders();
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
    
    private void loadSalesOrders() {
        try {
            tableModel.setRowCount(0);
            
            List<SalesOrder> orders = salesOrderDAO.getAllSalesOrders();
            
            for (SalesOrder order : orders) {
                Object[] row = {
                    order.getSaleNumber(),
                    order.getSaleDate().toString(),
                    order.getCustomerName() != null ? order.getCustomerName() : "Unknown Customer",
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
                "Error loading sales orders: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void applyStatusFilter(String status) {
        if ("All Status".equals(status)) {
            loadSalesOrders();
            return;
        }
        
        try {
            tableModel.setRowCount(0);
            
            List<SalesOrder> orders = salesOrderDAO.getSalesOrdersByPaymentStatus(status);
            
            for (SalesOrder order : orders) {
                Object[] row = {
                    order.getSaleNumber(),
                    order.getSaleDate().toString(),
                    order.getCustomerName() != null ? order.getCustomerName() : "Unknown Customer",
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
                "Error filtering sales orders: " + e.getMessage(), 
                "Filter Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showOutstandingOrders() {
        try {
            tableModel.setRowCount(0);
            
            List<SalesOrder> orders = salesOrderDAO.getOutstandingSales();
            
            for (SalesOrder order : orders) {
                Object[] row = {
                    order.getSaleNumber(),
                    order.getSaleDate().toString(),
                    order.getCustomerName() != null ? order.getCustomerName() : "Unknown Customer",
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
    
    private void newOrderActionPerformed(ActionEvent evt) {
        try {
            AddSalesOrderDialog dialog = new AddSalesOrderDialog(
                (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this), 
                true
            );
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                loadSalesOrders(); // Refresh the table
                JOptionPane.showMessageDialog(this, "Sales order created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating sales order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewOrderActionPerformed(ActionEvent evt) {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a sales order to view.");
            return;
        }
        
        String saleNumber = (String) tableModel.getValueAt(selectedRow, 0);
        JOptionPane.showMessageDialog(this, 
            "View Sales Order Details: " + saleNumber + "\n" +
            "This will show line items, customer details, and payment history.",
            "Sales Order Details", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void paymentActionPerformed(ActionEvent evt) {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a sales order to update payment.");
            return;
        }
        
        String saleNumber = (String) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);
        
        if ("Paid".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, 
                "This sales order is already fully paid.",
                "Payment Complete", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this, 
            "Update Payment for: " + saleNumber + "\n" +
            "Current Status: " + currentStatus + "\n" +
            "Payment update functionality will be implemented next.",
            "Update Payment", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void searchActionPerformed(ActionEvent evt) {
        String keyword = JOptionPane.showInputDialog(this, 
            "Enter search keyword (Sales #, Customer name, or Notes):",
            "Search Sales Orders",
            JOptionPane.QUESTION_MESSAGE);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            try {
                tableModel.setRowCount(0);
                
                List<SalesOrder> orders = salesOrderDAO.searchSalesOrders(keyword.trim());
                
                for (SalesOrder order : orders) {
                    Object[] row = {
                        order.getSaleNumber(),
                        order.getSaleDate().toString(),
                        order.getCustomerName() != null ? order.getCustomerName() : "Unknown Customer",
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
                        "No sales orders found matching: " + keyword,
                        "Search Results", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error searching sales orders: " + e.getMessage(), 
                    "Search Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshActionPerformed(ActionEvent evt) {
        loadSalesOrders();
        JOptionPane.showMessageDialog(this, "Sales orders refreshed!");
    }
}