package com.pradeepmill.ui.customers;

import com.pradeepmill.dao.CustomerDAO;
import com.pradeepmill.models.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CustomerManagementPanel extends javax.swing.JPanel {

    private CustomerDAO customerDAO;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    public CustomerManagementPanel() {
        this.customerDAO = new CustomerDAO();
        initComponents();
        loadCustomers();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(34, 139, 34)); // Forest Green for customers
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Customer Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton addButton = new JButton("Add Customer");
        JButton editButton = new JButton("Edit Customer");
        JButton deleteButton = new JButton("Delete Customer");
        JButton refreshButton = new JButton("Refresh");
        
        addButton.addActionListener(this::addCustomerActionPerformed);
        editButton.addActionListener(this::editCustomerActionPerformed);
        deleteButton.addActionListener(this::deleteCustomerActionPerformed);
        refreshButton.addActionListener(this::refreshActionPerformed);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Table setup
        String[] columnNames = {"ID", "Customer Name", "Contact Person", "Phone", "Address", "Type", "Status", "Credit Limit"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Contact
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Phone
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Address
        customerTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Type
        customerTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Status
        customerTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Credit
        
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void loadCustomers() {
        try {
            tableModel.setRowCount(0); // Clear existing data
            
            List<Customer> customers = customerDAO.getAllActiveCustomers();
            
            for (Customer customer : customers) {
                Object[] row = {
                    customer.getCustomerId(),
                    customer.getCustomerName(),
                    customer.getContactPerson(),
                    customer.getPhone(),
                    customer.getAddress(),
                    customer.getCustomerType(),
                    customer.getStatus(),
                    String.format("Rs. %.2f", customer.getCreditLimit())
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading customers: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addCustomerActionPerformed(ActionEvent evt) {
        AddCustomerDialog dialog = new AddCustomerDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Customer newCustomer = dialog.getCustomer();
            if (customerDAO.insertCustomer(newCustomer)) {
                JOptionPane.showMessageDialog(this, "Customer added successfully!");
                loadCustomers(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add customer!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editCustomerActionPerformed(ActionEvent evt) {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to edit.");
            return;
        }
        
        int customerId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Customer customer = customerDAO.findCustomerById(customerId);
        
        if (customer != null) {
            AddCustomerDialog dialog = new AddCustomerDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
            dialog.setCustomer(customer); // Pre-populate dialog with customer data
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Customer updatedCustomer = dialog.getCustomer();
                updatedCustomer.setCustomerId(customerId); // Ensure ID is set
                
                if (customerDAO.updateCustomer(updatedCustomer)) {
                    JOptionPane.showMessageDialog(this, "Customer updated successfully!");
                    loadCustomers(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update customer!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void deleteCustomerActionPerformed(ActionEvent evt) {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
            return;
        }
        
        String customerName = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete customer: " + customerName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int customerId = (Integer) tableModel.getValueAt(selectedRow, 0);
            
            if (customerDAO.deleteCustomer(customerId)) {
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
                loadCustomers(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete customer!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshActionPerformed(ActionEvent evt) {
        loadCustomers();
        JOptionPane.showMessageDialog(this, "Customer list refreshed!");
    }
}