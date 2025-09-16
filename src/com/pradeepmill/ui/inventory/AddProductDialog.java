package com.pradeepmill.ui.inventory;

import com.pradeepmill.models.Product;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddProductDialog extends JDialog {
    
    private JTextField nameField;
    private JComboBox<String> productTypeCombo;  // Fixed: was categoryCombo
    private JComboBox<String> gradeCombo;
    private JTextField unitPriceField;
    private JTextArea descriptionArea;
    private JComboBox<String> statusCombo;
    
    private boolean confirmed = false;
    private Product product;
    
    public AddProductDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setTitle("Add/Edit Product");
        setSize(500, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Product Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Product Name *:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Product Type - Using your model constants
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Product Type *:"), gbc);
        gbc.gridx = 1;
        productTypeCombo = new JComboBox<>(new String[]{
            Product.TYPE_RAW_PADDY,      // "Raw_Paddy"
            Product.TYPE_FINISHED_RICE,  // "Finished_Rice" 
            Product.TYPE_BY_PRODUCT      // "By_Product"
        });
        formPanel.add(productTypeCombo, gbc);
        
        // Grade - Using your model constants
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Grade *:"), gbc);
        gbc.gridx = 1;
        gradeCombo = new JComboBox<>(new String[]{
            Product.GRADE_A,           // "A"
            Product.GRADE_B,           // "B"
            Product.GRADE_C,           // "C"
            Product.GRADE_PREMIUM,     // "Premium"
            Product.GRADE_REGULAR      // "Regular"
        });
        formPanel.add(gradeCombo, gbc);
        
        // Unit Price
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Unit Price (Rs.) *:"), gbc);
        gbc.gridx = 1;
        unitPriceField = new JTextField("0.00", 20);
        formPanel.add(unitPriceField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
        formPanel.add(statusCombo, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(this::saveActionPerformed);
        cancelButton.addActionListener(this::cancelActionPerformed);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Help panel with rice mill specific examples
        JPanel helpPanel = new JPanel();
        helpPanel.setBorder(BorderFactory.createTitledBorder("Product Examples"));
        helpPanel.setBackground(new Color(245, 245, 245));
        
        JLabel helpText = new JLabel("<html>" +
            "<b>Raw_Paddy:</b> Nadu Rice Paddy, Samba Paddy, Red Rice Paddy<br>" +
            "<b>Finished_Rice:</b> White Rice (A Grade), Parboiled Rice, Red Rice<br>" +
            "<b>By_Product:</b> Rice Bran, Broken Rice, Husk" +
            "</html>");
        helpText.setFont(new Font("Arial", Font.PLAIN, 11));
        helpPanel.add(helpText);
        
        // Layout
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(helpPanel, BorderLayout.NORTH);
    }
    
    private void saveActionPerformed(ActionEvent evt) {
        if (validateForm()) {
            createProductFromForm();
            confirmed = true;
            dispose();
        }
    }
    
    private void cancelActionPerformed(ActionEvent evt) {
        confirmed = false;
        dispose();
    }
    
    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product name is required!");
            nameField.requestFocus();
            return false;
        }
        
        // Validate unit price
        try {
            double price = Double.parseDouble(unitPriceField.getText());
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Unit price cannot be negative!");
                unitPriceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Unit price must be a valid number!");
            unitPriceField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void createProductFromForm() {
        product = new Product();
        product.setProductName(nameField.getText().trim());
        product.setProductType((String) productTypeCombo.getSelectedItem());  // Fixed: was setCategory
        product.setGrade((String) gradeCombo.getSelectedItem());
        product.setUnitPrice(Double.parseDouble(unitPriceField.getText()));
        product.setDescription(descriptionArea.getText().trim());
        product.setStatus((String) statusCombo.getSelectedItem());
    }
    
    public void setProduct(Product product) {
        // Pre-populate form for editing
        nameField.setText(product.getProductName());
        productTypeCombo.setSelectedItem(product.getProductType());  // Fixed: was getCategory
        gradeCombo.setSelectedItem(product.getGrade());
        unitPriceField.setText(String.valueOf(product.getUnitPrice()));
        descriptionArea.setText(product.getDescription());
        statusCombo.setSelectedItem(product.getStatus());
    }
    
    public Product getProduct() {
        return product;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}