package com.pradeepmill.ui.inventory;

import com.pradeepmill.dao.*;
import com.pradeepmill.models.*;
import com.pradeepmill.services.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import com.pradeepmill.services.TransactionService;


public class AddSalesOrderDialog extends javax.swing.JDialog {

    private SalesOrderDAO salesOrderDAO;
    private CustomerDAO customerDAO;
    private ProductDAO productDAO;
    private InventoryService inventoryService;
    private StockInventoryDAO stockDAO;
    
    // UI Components
    private JComboBox<Customer> customerCombo;
    private JTextField saleNumberField;
    private JSpinner dateSpinner;
    private JTextField notesField;
    
    // Line Items Components
    private JComboBox<Product> productCombo;
    private JSpinner quantitySpinner;
    private JTextField unitPriceField;
    private JLabel availableStockLabel;
    private JButton addItemButton;
    private JTable itemsTable;
    private DefaultTableModel itemsTableModel;
    
    // Summary Components
    private JLabel totalQuantityLabel;
    private JLabel totalAmountLabel;
    
    // Data
    private List<SalesItem> orderItems;
    private boolean saved = false;

    public AddSalesOrderDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.salesOrderDAO = new SalesOrderDAO();
        this.customerDAO = new CustomerDAO();
        this.productDAO = new ProductDAO();
        this.inventoryService = new InventoryService();
        this.stockDAO = new StockInventoryDAO();
        this.orderItems = new ArrayList<>();
        
        initComponents();
        loadComboBoxData();
        setupComboBoxRenderers();
        generateSaleNumber();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create New Sales Order");
        setSize(850, 650);
        setLayout(new BorderLayout());
        
        // Title Panel - Green theme for sales
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(34, 139, 34));
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("New Sales Order", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Main Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        // Header Information Panel
        JPanel headerPanel = createHeaderPanel();
        
        // Line Items Panel
        JPanel itemsPanel = createItemsPanel();
        
        // Summary Panel
        JPanel summaryPanel = createSummaryPanel();
        
        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(itemsPanel, BorderLayout.CENTER);
        contentPanel.add(summaryPanel, BorderLayout.EAST);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Sales Order Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Sale Number
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Sale Number:"), gbc);
        gbc.gridx = 1;
        saleNumberField = new JTextField(15);
        saleNumberField.setEditable(false);
        panel.add(saleNumberField, gbc);
        
        // Customer
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1;
        customerCombo = new JComboBox<>();
        customerCombo.setPreferredSize(new Dimension(200, 25));
        panel.add(customerCombo, gbc);
        
        // Date
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 3;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        panel.add(dateSpinner, gbc);
        
        // Notes
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        notesField = new JTextField(15);
        panel.add(notesField, gbc);
        
        return panel;
    }
    
    private JPanel createItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Sales Order Items"));
        
        // Add Item Panel
        JPanel addItemPanel = new JPanel(new GridBagLayout());
        addItemPanel.setBackground(Color.WHITE);
        addItemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Product selection
        gbc.gridx = 0; gbc.gridy = 0;
        addItemPanel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1;
        productCombo = new JComboBox<>();
        productCombo.setPreferredSize(new Dimension(180, 25));
        productCombo.addActionListener(e -> updateAvailableStock());
        addItemPanel.add(productCombo, gbc);
        
        // Available Stock Display
        gbc.gridx = 0; gbc.gridy = 1;
        addItemPanel.add(new JLabel("Available Stock:"), gbc);
        gbc.gridx = 1;
        availableStockLabel = new JLabel("0.00 kg");
        availableStockLabel.setFont(new Font("Arial", Font.BOLD, 12));
        availableStockLabel.setForeground(new Color(0, 100, 0));
        addItemPanel.add(availableStockLabel, gbc);
        
        // Quantity
        gbc.gridx = 2; gbc.gridy = 0;
        addItemPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 3;
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 99999.0, 1.0));
        quantitySpinner.setPreferredSize(new Dimension(80, 25));
        addItemPanel.add(quantitySpinner, gbc);
        
        // Unit Price
        gbc.gridx = 4; gbc.gridy = 0;
        addItemPanel.add(new JLabel("Unit Price:"), gbc);
        gbc.gridx = 5;
        unitPriceField = new JTextField(10);
        addItemPanel.add(unitPriceField, gbc);
        
        // Add Button
        gbc.gridx = 6; gbc.gridy = 0;
        addItemButton = new JButton("Add Item");
        addItemButton.setBackground(new Color(34, 139, 34));
        addItemButton.setForeground(Color.WHITE);
        addItemButton.setPreferredSize(new Dimension(100, 25));
        addItemButton.addActionListener(this::addItemActionPerformed);
        addItemPanel.add(addItemButton, gbc);
        
        // Items Table
        String[] columnNames = {"Product", "Type", "Quantity", "Unit Price", "Total Price", "Action"};
        itemsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Action column editable
            }
        };
        
        itemsTable = new JTable(itemsTableModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsTable.setRowHeight(30);
        
        // Set column widths
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Product
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Type
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Quantity
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Unit Price
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Total Price
        itemsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Action
        
        // Add remove button column
        itemsTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        itemsTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        
        panel.add(addItemPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Order Summary"));
        panel.setPreferredSize(new Dimension(200, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Total Quantity:"), gbc);
        gbc.gridy = 1;
        totalQuantityLabel = new JLabel("0.00 kg");
        totalQuantityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(totalQuantityLabel, gbc);
        
        gbc.gridy = 3;
        panel.add(new JLabel("Total Amount:"), gbc);
        gbc.gridy = 4;
        totalAmountLabel = new JLabel("Rs. 0.00");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalAmountLabel.setForeground(new Color(0, 100, 0));
        panel.add(totalAmountLabel, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);
        
        JButton saveButton = new JButton("Save Sales Order");
        saveButton.setBackground(new Color(34, 139, 34));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(this::saveActionPerformed);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this::cancelActionPerformed);
        
        panel.add(cancelButton);
        panel.add(saveButton);
        
        return panel;
    }
    
    private void loadComboBoxData() {
        try {
            // Load customers
            List<Customer> customers = customerDAO.getAllActiveCustomers();
            customerCombo.removeAllItems();
            for (Customer customer : customers) {
                customerCombo.addItem(customer);
            }
            
            // Load products
            List<Product> products = productDAO.getAllActiveProducts();
            productCombo.removeAllItems();
            for (Product product : products) {
                productCombo.addItem(product);
            }
            
            // Initialize stock display
            updateAvailableStock();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateAvailableStock() {
        try {
            Product selectedProduct = (Product) productCombo.getSelectedItem();
            if (selectedProduct != null) {
                StockInventory stock = stockDAO.getStockByProductId(selectedProduct.getProductId());
                if (stock != null) {
                    availableStockLabel.setText(String.format("%.2f %s", stock.getCurrentQuantity(), selectedProduct.getUnit()));
                    
                    // Color code based on availability
                    if (stock.isOutOfStock()) {
                        availableStockLabel.setForeground(Color.RED);
                    } else if (stock.isLowStock()) {
                        availableStockLabel.setForeground(Color.ORANGE);
                    } else {
                        availableStockLabel.setForeground(new Color(0, 100, 0));
                    }
                } else {
                    availableStockLabel.setText("No stock info");
                    availableStockLabel.setForeground(Color.GRAY);
                }
            }
        } catch (Exception e) {
            availableStockLabel.setText("Error loading stock");
            availableStockLabel.setForeground(Color.RED);
        }
    }
    
    private void generateSaleNumber() {
        String saleNumber = "SO" + System.currentTimeMillis();
        saleNumberField.setText(saleNumber);
    }
    
    private void addItemActionPerformed(ActionEvent evt) {
        try {
            Product selectedProduct = (Product) productCombo.getSelectedItem();
            double quantity = (Double) quantitySpinner.getValue();
            String unitPriceText = unitPriceField.getText().trim();
            
            // Validation
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(this, "Please select a product.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                productCombo.requestFocus();
                return;
            }
            
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid quantity greater than 0.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                quantitySpinner.requestFocus();
                return;
            }
            
            if (unitPriceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a unit price.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                unitPriceField.requestFocus();
                return;
            }
            
            double unitPrice;
            try {
                unitPrice = Double.parseDouble(unitPriceText);
                if (unitPrice <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid unit price greater than 0.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    unitPriceField.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric unit price.", "Input Error", JOptionPane.ERROR_MESSAGE);
                unitPriceField.requestFocus();
                return;
            }
            
            // Check stock availability
            if (!inventoryService.isStockSufficientForSale(selectedProduct.getProductId(), quantity)) {
                StockInventory stock = stockDAO.getStockByProductId(selectedProduct.getProductId());
                double available = stock != null ? stock.getCurrentQuantity() : 0;
                JOptionPane.showMessageDialog(this, 
                    String.format("Insufficient stock for %s!\nRequired: %.2f %s\nAvailable: %.2f %s", 
                                selectedProduct.getProductName(), quantity, selectedProduct.getUnit(),
                                available, selectedProduct.getUnit()), 
                    "Stock Shortage", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if product already exists
            for (SalesItem existingItem : orderItems) {
                if (existingItem.getProductId() == selectedProduct.getProductId()) {
                    JOptionPane.showMessageDialog(this, "Product '" + selectedProduct.getProductName() + "' already exists in the order.\nPlease remove it first or modify the quantity.", "Duplicate Product", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            // Create new item
            SalesItem item = new SalesItem(selectedProduct.getProductId(), quantity, unitPrice);
            item.setProductName(selectedProduct.getProductName());
            item.setProductType(selectedProduct.getProductType());
            item.setUnit(selectedProduct.getUnit());
            
            orderItems.add(item);
            updateItemsTable();
            updateSummary();
            
            // Reset form
            if (productCombo.getItemCount() > 0) {
                productCombo.setSelectedIndex(0);
            }
            quantitySpinner.setValue(1.0);
            unitPriceField.setText("");
            updateAvailableStock();
            
            // Focus back to product selection for quick entry
            productCombo.requestFocus();
            
            JOptionPane.showMessageDialog(this, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateItemsTable() {
        itemsTableModel.setRowCount(0);
        
        for (SalesItem item : orderItems) {
            Object[] row = {
                item.getProductName(),
                item.getProductType(),
                String.format("%.2f %s", item.getQuantity(), item.getUnit()),
                String.format("Rs. %.2f", item.getUnitPrice()),
                String.format("Rs. %.2f", item.getTotalPrice()),
                "Remove"
            };
            itemsTableModel.addRow(row);
        }
    }
    
    private void updateSummary() {
        double totalQuantity = orderItems.stream().mapToDouble(SalesItem::getQuantity).sum();
        double totalAmount = orderItems.stream().mapToDouble(SalesItem::getTotalPrice).sum();
        
        totalQuantityLabel.setText(String.format("%.2f kg", totalQuantity));
        totalAmountLabel.setText(String.format("Rs. %.2f", totalAmount));
    }
    
    private void saveActionPerformed(ActionEvent evt) {
    try {
        // Validation
        if (supplierCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a supplier.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one item to the purchase order.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get selected supplier
        Supplier selectedSupplier = (Supplier) supplierCombo.getSelectedItem();
        
        // FIXED: Use TransactionService for proper stock and payment integration
        TransactionService transactionService = new TransactionService();
        boolean transactionSuccess = transactionService.processPurchaseTransaction(
            selectedSupplier.getSupplierId(), 
            orderItems, 
            notesField.getText(),
            "Current User"
        );

        if (transactionSuccess) {
            saved = true;
            JOptionPane.showMessageDialog(this, 
                "Purchase order created successfully!\n" +
                "Purchase Number: " + purchaseNumberField.getText() + "\n" +
                "Stock levels updated automatically.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error processing purchase transaction. Please try again.", 
                "Transaction Error", JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error creating purchase order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void cancelActionPerformed(ActionEvent evt) {
        int option = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel? All entered data will be lost.", 
            "Confirm Cancel", 
            JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            dispose();
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    // Button renderer and editor classes (same as Purchase Order)
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setBackground(new Color(220, 20, 60));
            setForeground(Color.WHITE);
            return this;
        }
    }
    
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            button.setBackground(new Color(220, 20, 60));
            button.setForeground(Color.WHITE);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int row = itemsTable.getSelectedRow();
                if (row >= 0 && row < orderItems.size()) {
                    orderItems.remove(row);
                    updateItemsTable();
                    updateSummary();
                    updateAvailableStock();
                    JOptionPane.showMessageDialog(AddSalesOrderDialog.this, "Item removed successfully.", "Item Removed", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    private void setupComboBoxRenderers() {
    // Custom renderer for Customer combo box
    customerCombo.setRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Customer) {
                setText(((Customer) value).getCustomerName());
            }
            return this;
        }
    });
    
    // Custom renderer for Product combo box
    productCombo.setRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Product) {
                setText(((Product) value).getProductName() + " (" + ((Product) value).getProductType() + ")");
            }
            return this;
        }
    });
}
}