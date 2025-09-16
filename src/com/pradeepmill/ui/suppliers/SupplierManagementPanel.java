package com.pradeepmill.ui.suppliers;

import com.pradeepmill.services.DashboardService;
import com.pradeepmill.dao.SupplierDAO;
import com.pradeepmill.models.Supplier;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SupplierManagementPanel extends javax.swing.JPanel {

    private SupplierDAO supplierDAO;
    private JTable supplierTable;
    private DefaultTableModel tableModel;

    public SupplierManagementPanel() {
        this.supplierDAO = new SupplierDAO();
        initComponents();
        loadSuppliers();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Supplier Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton addButton = new JButton("Add Supplier");
        JButton editButton = new JButton("Edit Supplier");
        JButton deleteButton = new JButton("Delete Supplier");
        JButton refreshButton = new JButton("Refresh");
        
        addButton.addActionListener(this::addSupplierActionPerformed);
        editButton.addActionListener(this::editSupplierActionPerformed);
        deleteButton.addActionListener(this::deleteSupplierActionPerformed);
        refreshButton.addActionListener(this::refreshActionPerformed);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Table setup
        String[] columnNames = {"ID", "Supplier Name", "Contact Person", "Phone", "Address", "Status", "Credit Limit"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        supplierTable = new JTable(tableModel);
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        supplierTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        supplierTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        supplierTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Contact
        supplierTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Phone
        supplierTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Address
        supplierTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Status
        supplierTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Credit
        
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void loadSuppliers() {
        try {
            tableModel.setRowCount(0); // Clear existing data
            
            List<Supplier> suppliers = supplierDAO.getAllActiveSuppliers();
            
            for (Supplier supplier : suppliers) {
                Object[] row = {
                    supplier.getSupplierId(),
                    supplier.getSupplierName(),
                    supplier.getContactPerson(),
                    supplier.getPhone(),
                    supplier.getAddress(),
                    supplier.getStatus(),
                    String.format("Rs. %.2f", supplier.getCreditLimit())
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading suppliers: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addSupplierActionPerformed(ActionEvent evt) {
        AddSupplierDialog dialog = new AddSupplierDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Supplier newSupplier = dialog.getSupplier();
            if (supplierDAO.insertSupplier(newSupplier)) {
                JOptionPane.showMessageDialog(this, "Supplier added successfully!");
                loadSuppliers(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add supplier!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editSupplierActionPerformed(ActionEvent evt) {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to edit.");
            return;
        }
        
        int supplierId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Supplier supplier = supplierDAO.findSupplierById(supplierId);
        
        if (supplier != null) {
            AddSupplierDialog dialog = new AddSupplierDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
            dialog.setSupplier(supplier); // Pre-populate dialog with supplier data
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Supplier updatedSupplier = dialog.getSupplier();
                updatedSupplier.setSupplierId(supplierId); // Ensure ID is set
                
                if (supplierDAO.updateSupplier(updatedSupplier)) {
                    JOptionPane.showMessageDialog(this, "Supplier updated successfully!");
                    loadSuppliers(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update supplier!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void deleteSupplierActionPerformed(ActionEvent evt) {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to delete.");
            return;
        }
        
        String supplierName = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete supplier: " + supplierName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int supplierId = (Integer) tableModel.getValueAt(selectedRow, 0);
            
            if (supplierDAO.deleteSupplier(supplierId)) {
                JOptionPane.showMessageDialog(this, "Supplier deleted successfully!");
                loadSuppliers(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete supplier!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshActionPerformed(ActionEvent evt) {
        loadSuppliers();
        JOptionPane.showMessageDialog(this, "Supplier list refreshed!");
    }
}