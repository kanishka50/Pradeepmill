package com.pradeepmill.ui.machines;

import com.pradeepmill.models.Machine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;

public class AddMachineDialog extends JDialog {
    
    private JTextField nameField;
    private JComboBox<String> typeCombo;
    private JTextField locationField;
    private JTextField capacityField;
    private JComboBox<String> statusCombo;
    
    private boolean confirmed = false;
    private Machine machine;
    
    public AddMachineDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setTitle("Add/Edit Machine");
        setSize(500, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Machine Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Machine Name *:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Machine Type - using your actual constants
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Machine Type *:"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[]{
            Machine.TYPE_CLEANER,    // "Cleaner"
            Machine.TYPE_DEHUSKER,   // "De-husker" - Fixed to match your model
            Machine.TYPE_POLISHER,   // "Polisher"
            Machine.TYPE_GRADER,     // "Grader"
            Machine.TYPE_PACKER      // "Packer"
        });
        formPanel.add(typeCombo, gbc);
        
        // Location
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        locationField = new JTextField(20);
        formPanel.add(locationField, gbc);
        
        // Capacity Per Hour
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Capacity (kg/hour) *:"), gbc);
        gbc.gridx = 1;
        capacityField = new JTextField("0.0", 20);
        formPanel.add(capacityField, gbc);
        
        // Status - using your actual constants
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{
            Machine.STATUS_ACTIVE,      // "Active"
            Machine.STATUS_MAINTENANCE, // "Maintenance"
            Machine.STATUS_BROKEN       // "Broken"
        });
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
        helpPanel.setBorder(BorderFactory.createTitledBorder("Machine Types"));
        helpPanel.setBackground(new Color(245, 245, 245));
        
        JLabel helpText = new JLabel("<html>" +
            "<b>Cleaner:</b> Removes impurities from raw paddy<br>" +
            "<b>De-husker:</b> Removes husk from paddy to produce brown rice<br>" +
            "<b>Polisher:</b> Polishes brown rice to produce white rice<br>" +
            "<b>Grader:</b> Sorts rice by size and quality<br>" +
            "<b>Packer:</b> Packages rice into bags for distribution" +
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
            createMachineFromForm();
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
            JOptionPane.showMessageDialog(this, "Machine name is required!");
            nameField.requestFocus();
            return false;
        }
        
        try {
            double capacity = Double.parseDouble(capacityField.getText());
            if (capacity < 0) {
                JOptionPane.showMessageDialog(this, "Capacity cannot be negative!");
                capacityField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacity must be a valid number!");
            capacityField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void createMachineFromForm() {
        machine = new Machine();
        machine.setMachineName(nameField.getText().trim());
        machine.setMachineType((String) typeCombo.getSelectedItem());
        machine.setLocation(locationField.getText().trim());
        machine.setCapacityPerHour(Double.parseDouble(capacityField.getText()));
        machine.setStatus((String) statusCombo.getSelectedItem());
        machine.setInstallationDate(new Date());
    }
    
    public void setMachine(Machine machine) {
        nameField.setText(machine.getMachineName());
        typeCombo.setSelectedItem(machine.getMachineType());
        locationField.setText(machine.getLocation());
        capacityField.setText(String.valueOf(machine.getCapacityPerHour()));
        statusCombo.setSelectedItem(machine.getStatus());
    }
    
    public Machine getMachine() {
        return machine;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}