package com.pradeepmill.ui.machines;

import com.pradeepmill.dao.MachineDAO;
import com.pradeepmill.models.Machine;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MachineManagementPanel extends javax.swing.JPanel {

    private MachineDAO machineDAO;
    private JTable machineTable;
    private DefaultTableModel tableModel;

    public MachineManagementPanel() {
        this.machineDAO = new MachineDAO();
        initComponents();
        loadMachines();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(220, 20, 60)); // Crimson for machines
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Machine Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton addButton = new JButton("Add Machine");
        JButton editButton = new JButton("Edit Machine");
        JButton deleteButton = new JButton("Delete Machine");
        JButton refreshButton = new JButton("Refresh");
        JButton usageButton = new JButton("Usage Records");
        JButton maintenanceButton = new JButton("Maintenance Log");
        
        addButton.addActionListener(this::addMachineActionPerformed);
        editButton.addActionListener(this::editMachineActionPerformed);
        deleteButton.addActionListener(this::deleteMachineActionPerformed);
        refreshButton.addActionListener(this::refreshActionPerformed);
        usageButton.addActionListener(this::usageRecordsActionPerformed);
        maintenanceButton.addActionListener(this::maintenanceLogActionPerformed);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(usageButton);
        buttonPanel.add(maintenanceButton);
        
        // Table setup
        String[] columnNames = {"ID", "Machine Name", "Type", "Location", "Capacity/Hour", "Status", "Installation Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        machineTable = new JTable(tableModel);
        machineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        machineTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        machineTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        machineTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        machineTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        machineTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        machineTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        machineTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        machineTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(machineTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        
        // Filter panel - using your actual constants
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Machines"));
        
        JLabel typeLabel = new JLabel("Machine Type:");
        JComboBox<String> typeFilter = new JComboBox<>(new String[]{
            "All Types", 
            Machine.TYPE_CLEANER,    // "Cleaner"
            Machine.TYPE_DEHUSKER,   // "De-husker" - Fixed to match your model
            Machine.TYPE_POLISHER,   // "Polisher"
            Machine.TYPE_GRADER,     // "Grader"
            Machine.TYPE_PACKER      // "Packer"
        });
        
        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{
            "All Status", 
            Machine.STATUS_ACTIVE,      // "Active"
            Machine.STATUS_MAINTENANCE, // "Maintenance"
            Machine.STATUS_BROKEN       // "Broken"
        });
        
        JButton filterButton = new JButton("Apply Filter");
        JButton resetButton = new JButton("Reset Filter");
        
        filterButton.addActionListener(e -> applyFilter(
            (String) typeFilter.getSelectedItem(),
            (String) statusFilter.getSelectedItem()
        ));
        resetButton.addActionListener(e -> {
            typeFilter.setSelectedIndex(0);
            statusFilter.setSelectedIndex(0);
            loadMachines();
        });
        
        filterPanel.add(typeLabel);
        filterPanel.add(typeFilter);
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilter);
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
    
    private void loadMachines() {
        try {
            tableModel.setRowCount(0);
            
            List<Machine> machines = machineDAO.getAllActiveMachines();
            
            for (Machine machine : machines) {
                Object[] row = {
                    machine.getMachineId(),
                    machine.getMachineName(),
                    machine.getMachineType(),
                    machine.getLocation(),
                    String.format("%.0f kg/hr", machine.getCapacityPerHour()),
                    machine.getStatus(),
                    machine.getInstallationDate() != null ? machine.getInstallationDate().toString() : "N/A"
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading machines: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void applyFilter(String type, String status) {
        try {
            tableModel.setRowCount(0);
            
            List<Machine> machines;
            
            if ("All Types".equals(type) && "All Status".equals(status)) {
                machines = machineDAO.getAllActiveMachines();
            } else if (!"All Types".equals(type) && "All Status".equals(status)) {
                machines = machineDAO.getMachinesByType(type);
            } else if ("All Types".equals(type) && !"All Status".equals(status)) {
                machines = machineDAO.getMachinesByStatus(status);
            } else {
                machines = machineDAO.getMachinesByTypeAndStatus(type, status);
            }
            
            for (Machine machine : machines) {
                Object[] row = {
                    machine.getMachineId(),
                    machine.getMachineName(),
                    machine.getMachineType(),
                    machine.getLocation(),
                    String.format("%.0f kg/hr", machine.getCapacityPerHour()),
                    machine.getStatus(),
                    machine.getInstallationDate() != null ? machine.getInstallationDate().toString() : "N/A"
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
    
    private void addMachineActionPerformed(ActionEvent evt) {
        AddMachineDialog dialog = new AddMachineDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Machine newMachine = dialog.getMachine();
            if (machineDAO.insertMachine(newMachine)) {
                JOptionPane.showMessageDialog(this, "Machine added successfully!");
                loadMachines();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add machine!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editMachineActionPerformed(ActionEvent evt) {
        int selectedRow = machineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a machine to edit.");
            return;
        }
        
        int machineId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Machine machine = machineDAO.findMachineById(machineId);
        
        if (machine != null) {
            AddMachineDialog dialog = new AddMachineDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
            dialog.setMachine(machine);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Machine updatedMachine = dialog.getMachine();
                updatedMachine.setMachineId(machineId);
                
                if (machineDAO.updateMachine(updatedMachine)) {
                    JOptionPane.showMessageDialog(this, "Machine updated successfully!");
                    loadMachines();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update machine!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void deleteMachineActionPerformed(ActionEvent evt) {
        int selectedRow = machineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a machine to delete.");
            return;
        }
        
        String machineName = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete machine: " + machineName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int machineId = (Integer) tableModel.getValueAt(selectedRow, 0);
            
            if (machineDAO.deleteMachine(machineId)) {
                JOptionPane.showMessageDialog(this, "Machine deleted successfully!");
                loadMachines();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete machine!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void usageRecordsActionPerformed(ActionEvent evt) {
        JOptionPane.showMessageDialog(this, 
            "Usage Records functionality will be implemented in Production module.",
            "Coming Soon", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void maintenanceLogActionPerformed(ActionEvent evt) {
        JOptionPane.showMessageDialog(this, 
            "Maintenance Log functionality will be implemented later.",
            "Coming Soon", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshActionPerformed(ActionEvent evt) {
        loadMachines();
        JOptionPane.showMessageDialog(this, "Machine list refreshed!");
    }
}