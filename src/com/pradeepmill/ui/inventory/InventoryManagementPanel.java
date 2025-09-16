package com.pradeepmill.ui.inventory;

import com.pradeepmill.dao.ProductDAO;
import com.pradeepmill.models.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class InventoryManagementPanel extends javax.swing.JPanel {

    private ProductDAO productDAO;
    private JTable productTable;
    private DefaultTableModel tableModel;

    public InventoryManagementPanel() {
        this.productDAO = new ProductDAO();
        initComponents();
        loadProducts();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(255, 140, 0)); // Dark Orange for products
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Product Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton addButton = new JButton("Add Product");
        JButton editButton = new JButton("Edit Product");
        JButton deleteButton = new JButton("Delete Product");
        JButton refreshButton = new JButton("Refresh");
        JButton viewStockButton = new JButton("View Stock Levels");
        
        addButton.addActionListener(this::addProductActionPerformed);
        editButton.addActionListener(this::editProductActionPerformed);
        deleteButton.addActionListener(this::deleteProductActionPerformed);
        refreshButton.addActionListener(this::refreshActionPerformed);
        viewStockButton.addActionListener(this::viewStockActionPerformed);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewStockButton);
        
        // Table setup - Rice Mill specific columns
        String[] columnNames = {"ID", "Product Name", "Category", "Grade", "Unit Price", "Description", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        productTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        productTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        productTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Category
        productTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Grade
        productTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Price
        productTable.getColumnModel().getColumn(5).setPreferredWidth(200); // Description
        productTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Status
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        
        // Filter panel for rice mill categories
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Products"));

        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryFilter = new JComboBox<>(new String[]{
            "All Categories", 
            Product.TYPE_RAW_PADDY,      // "Raw_Paddy"
            Product.TYPE_FINISHED_RICE,  // "Finished_Rice"
            Product.TYPE_BY_PRODUCT      // "By_Product"
        });

        JLabel gradeLabel = new JLabel("Grade:");
        JComboBox<String> gradeFilter = new JComboBox<>(new String[]{
            "All Grades", 
            Product.GRADE_A,           // "A"
            Product.GRADE_B,           // "B" 
            Product.GRADE_C,           // "C"
            Product.GRADE_PREMIUM,     // "Premium"
            Product.GRADE_REGULAR      // "Regular"
        });

        JButton filterButton = new JButton("Apply Filter");
        JButton resetButton = new JButton("Reset Filter");

        filterButton.addActionListener(e -> applyFilter(
            (String) categoryFilter.getSelectedItem(),
            (String) gradeFilter.getSelectedItem()
        ));
        resetButton.addActionListener(e -> {
            categoryFilter.setSelectedIndex(0);
            gradeFilter.setSelectedIndex(0);
            loadProducts();
        });

        filterPanel.add(categoryLabel);
        filterPanel.add(categoryFilter);
        filterPanel.add(gradeLabel);
        filterPanel.add(gradeFilter);
        filterPanel.add(filterButton);
        filterPanel.add(resetButton);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(filterPanel, BorderLayout.CENTER);
        contentPanel.add(scrollPane, BorderLayout.SOUTH);
        
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void loadProducts() {
    try {
        tableModel.setRowCount(0); // Clear existing data
        
        List<Product> products = productDAO.getAllActiveProducts();
        
        for (Product product : products) {
            Object[] row = {
                product.getProductId(),
                product.getProductName(),
                product.getProductType(),  // Fixed: was getCategory()
                product.getGrade(),
                String.format("Rs. %.2f", product.getUnitPrice()),
                product.getDescription(),
                product.getStatus()
            };
            tableModel.addRow(row);
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            "Error loading products: " + e.getMessage(), 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    }
    
   private void applyFilter(String category, String grade) {
    try {
        tableModel.setRowCount(0);
        
        List<Product> products;
        
        if ("All Categories".equals(category) && "All Grades".equals(grade)) {
            products = productDAO.getAllActiveProducts();
        } else if (!"All Categories".equals(category) && "All Grades".equals(grade)) {
            // Use the existing method name from your DAO
            products = productDAO.getProductsByType(category);
        } else if ("All Categories".equals(category) && !"All Grades".equals(grade)) {
            // Use the new method we just added
            products = productDAO.getProductsByGrade(grade);
        } else {
            // Use the new method we just added
            products = productDAO.getProductsByTypeAndGrade(category, grade);
        }
        
        for (Product product : products) {
            Object[] row = {
                product.getProductId(),
                product.getProductName(),
                product.getProductType(),
                product.getGrade(),
                String.format("Rs. %.2f", product.getUnitPrice()),
                product.getDescription(),
                product.getStatus()
            };
            tableModel.addRow(row);
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            "Error applying filter: " + e.getMessage(), 
            "Filter Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    }
    
    private void addProductActionPerformed(ActionEvent evt) {
        AddProductDialog dialog = new AddProductDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Product newProduct = dialog.getProduct();
            if (productDAO.insertProduct(newProduct)) {
                JOptionPane.showMessageDialog(this, "Product added successfully!");
                loadProducts(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add product!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editProductActionPerformed(ActionEvent evt) {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit.");
            return;
        }
        
        int productId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Product product = productDAO.findProductById(productId);
        
        if (product != null) {
            AddProductDialog dialog = new AddProductDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
            dialog.setProduct(product); // Pre-populate dialog with product data
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Product updatedProduct = dialog.getProduct();
                updatedProduct.setProductId(productId); // Ensure ID is set
                
                if (productDAO.updateProduct(updatedProduct)) {
                    JOptionPane.showMessageDialog(this, "Product updated successfully!");
                    loadProducts(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update product!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void deleteProductActionPerformed(ActionEvent evt) {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
            return;
        }
        
        String productName = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete product: " + productName + "?\n" +
            "This will also affect stock inventory records.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int productId = (Integer) tableModel.getValueAt(selectedRow, 0);
            
            if (productDAO.deleteProduct(productId)) {
                JOptionPane.showMessageDialog(this, "Product deleted successfully!");
                loadProducts(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete product!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viewStockActionPerformed(ActionEvent evt) {
        JOptionPane.showMessageDialog(this, 
            "Stock View functionality will be implemented in Stock Management module.",
            "Coming Soon", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshActionPerformed(ActionEvent evt) {
        loadProducts();
        JOptionPane.showMessageDialog(this, "Product list refreshed!");
    }
}