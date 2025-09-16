package com.pradeepmill.ui.staff;

import com.pradeepmill.models.Staff;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddStaffDialog extends JDialog {
    
    private JTextField nameField;
    private JComboBox<String> positionCombo;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField salaryField;
    private JComboBox<String> statusCombo;
    
    private boolean confirmed = false;
    private Staff staff;
    
    public AddStaffDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setTitle("Add/Edit Employee");
        setSize(500, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Employee Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Employee Name *:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Position - using your actual constants
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Position *:"), gbc);
        gbc.gridx = 1;
        positionCombo = new JComboBox<>(new String[]{
            Staff.POSITION_MANAGER,           // "Mill Manager"
            Staff.POSITION_OPERATOR,          // "Machine Operator"
            Staff.POSITION_QUALITY_CONTROLLER,// "Quality Controller"
            Staff.POSITION_ACCOUNTANT,        // "Accountant"
            Staff.POSITION_SUPERVISOR,        // "Supervisor"
            "Helper"                          // Manual entry
        });
        formPanel.add(positionCombo, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Phone *:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);
        
        // Monthly Salary
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Monthly Salary (Rs.) *:"), gbc);
        gbc.gridx = 1;
        salaryField = new JTextField("0.00", 20);
        formPanel.add(salaryField, gbc);
        
        // Address
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(addressArea), gbc);
        
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
        
        // Help panel
        JPanel helpPanel = new JPanel();
        helpPanel.setBorder(BorderFactory.createTitledBorder("Position Descriptions"));
        helpPanel.setBackground(new Color(245, 245, 245));
        
        JLabel helpText = new JLabel("<html>" +
            "<b>Mill Manager:</b> Overall mill operations, staff supervision<br>" +
            "<b>Machine Operator:</b> Operate rice processing machines<br>" +
            "<b>Quality Controller:</b> Inspect rice quality and grades<br>" +
            "<b>Supervisor:</b> Section supervision and coordination<br>" +
            "<b>Accountant:</b> Financial records and reporting" +
            "</html>");
        helpText.setFont(new Font("Arial", Font.PLAIN, 11));
        helpPanel.add(helpText);
        
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(helpPanel, BorderLayout.NORTH);
    }
    
    private void saveActionPerformed(ActionEvent evt) {
        if (validateForm()) {
            createStaffFromForm();
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
            JOptionPane.showMessageDialog(this, "Employee name is required!");
            nameField.requestFocus();
            return false;
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number is required!");
            phoneField.requestFocus();
            return false;
        }
        
        try {
            double salary = Double.parseDouble(salaryField.getText());
            if (salary < 0) {
                JOptionPane.showMessageDialog(this, "Monthly salary cannot be negative!");
                salaryField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Monthly salary must be a valid number!");
            salaryField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void createStaffFromForm() {
        staff = new Staff();
        staff.setEmployeeName(nameField.getText().trim());
        staff.setPosition((String) positionCombo.getSelectedItem());
        staff.setPhone(phoneField.getText().trim());
        staff.setAddress(addressArea.getText().trim());
        staff.setMonthlySalary(Double.parseDouble(salaryField.getText()));
        staff.setStatus((String) statusCombo.getSelectedItem());
    }
    
    public void setStaff(Staff staff) {
        nameField.setText(staff.getEmployeeName());
        positionCombo.setSelectedItem(staff.getPosition());
        phoneField.setText(staff.getPhone());
        addressArea.setText(staff.getAddress());
        salaryField.setText(String.valueOf(staff.getMonthlySalary()));
        statusCombo.setSelectedItem(staff.getStatus());
    }
    
    public Staff getStaff() {
        return staff;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}