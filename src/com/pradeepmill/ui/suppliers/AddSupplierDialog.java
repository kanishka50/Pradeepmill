package com.pradeepmill.ui.suppliers;

import com.pradeepmill.models.Supplier;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddSupplierDialog extends JDialog {
    
    private JTextField nameField;
    private JTextField contactPersonField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField emailField;
    private JTextField creditLimitField;
    private JComboBox<String> statusCombo;
    
    private boolean confirmed = false;
    private Supplier supplier;
    
    public AddSupplierDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setTitle("Add/Edit Supplier");
        setSize(500, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Supplier Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Supplier Name *:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Contact Person
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Contact Person:"), gbc);
        gbc.gridx = 1;
        contactPersonField = new JTextField(20);
        formPanel.add(contactPersonField, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Phone *:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        
        // Address
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        formPanel.add(new JScrollPane(addressArea), gbc);
        
        // Credit Limit
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Credit Limit:"), gbc);
        gbc.gridx = 1;
        creditLimitField = new JTextField("0.00", 20);
        formPanel.add(creditLimitField, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 6;
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
        
        // Layout
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void saveActionPerformed(ActionEvent evt) {
        if (validateForm()) {
            createSupplierFromForm();
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
            JOptionPane.showMessageDialog(this, "Supplier name is required!");
            nameField.requestFocus();
            return false;
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number is required!");
            phoneField.requestFocus();
            return false;
        }
        
        // Validate credit limit
        try {
            Double.parseDouble(creditLimitField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Credit limit must be a valid number!");
            creditLimitField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void createSupplierFromForm() {
        supplier = new Supplier();
        supplier.setSupplierName(nameField.getText().trim());
        supplier.setContactPerson(contactPersonField.getText().trim());
        supplier.setPhone(phoneField.getText().trim());
        supplier.setAddress(addressArea.getText().trim());
        supplier.setEmail(emailField.getText().trim());
        supplier.setCreditLimit(Double.parseDouble(creditLimitField.getText()));
        supplier.setStatus((String) statusCombo.getSelectedItem());
    }
    
    public void setSupplier(Supplier supplier) {
        // Pre-populate form for editing
        nameField.setText(supplier.getSupplierName());
        contactPersonField.setText(supplier.getContactPerson());
        phoneField.setText(supplier.getPhone());
        addressArea.setText(supplier.getAddress());
        emailField.setText(supplier.getEmail());
        creditLimitField.setText(String.valueOf(supplier.getCreditLimit()));
        statusCombo.setSelectedItem(supplier.getStatus());
    }
    
    public Supplier getSupplier() {
        return supplier;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}