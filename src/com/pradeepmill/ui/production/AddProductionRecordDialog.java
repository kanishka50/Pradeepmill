package com.pradeepmill.ui.production;

import com.pradeepmill.dao.*;
import com.pradeepmill.models.*;
import com.pradeepmill.services.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Date;

public class AddProductionRecordDialog extends javax.swing.JDialog {

    private ProductionRecordDAO productionRecordDAO;
    private ProductDAO productDAO;
    private MachineDAO machineDAO;
    private StaffDAO staffDAO;
    private InventoryService inventoryService;
    private StockInventoryDAO stockDAO;
    
    // UI Components
    private JTextField productionNumberField;
    private JSpinner dateSpinner;
    private JComboBox<Product> rawProductCombo;
    private JComboBox<Product> finishedProductCombo;
    private JSpinner inputQuantitySpinner;
    private JSpinner outputQuantitySpinner;
    private JSpinner wasteQuantitySpinner;
    private JLabel conversionRateLabel;
    private JComboBox<Machine> machineCombo;
    private JComboBox<Staff> operatorCombo;
    private JTextArea notesArea;
    
    // Stock info labels
    private JLabel rawStockLabel;
    private JLabel finishedStockLabel;
    
    private boolean saved = false;

    public AddProductionRecordDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.productionRecordDAO = new ProductionRecordDAO();
        this.productDAO = new ProductDAO();
        this.machineDAO = new MachineDAO();
        this.staffDAO = new StaffDAO();
        this.inventoryService = new InventoryService();
        this.stockDAO = new StockInventoryDAO();
        
        initComponents();
        loadComboBoxData();
        generateProductionNumber();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create New Production Record");
        setSize(700, 600);
        setLayout(new BorderLayout());
        
        // Title Panel - Orange theme for production
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(255, 140, 0));
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("New Production Record", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Main Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        
        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Production Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Production Number
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Production Number:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        productionNumberField = new JTextField(20);
        productionNumberField.setEditable(false);
        panel.add(productionNumberField, gbc);
        
        // Date
        gbc.gridx = 2; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 3;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        panel.add(dateSpinner, gbc);
        
        // Raw Product (Input)
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Raw Product:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        rawProductCombo = new JComboBox<>();
        rawProductCombo.addActionListener(e -> updateRawStockInfo());
        panel.add(rawProductCombo, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Available:"), gbc);
        gbc.gridx = 3;
        rawStockLabel = new JLabel("0.00 kg");
        rawStockLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(rawStockLabel, gbc);
        
        // Finished Product (Output)
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Finished Product:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        finishedProductCombo = new JComboBox<>();
        finishedProductCombo.addActionListener(e -> updateFinishedStockInfo());
        panel.add(finishedProductCombo, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Current Stock:"), gbc);
        gbc.gridx = 3;
        finishedStockLabel = new JLabel("0.00 kg");
        finishedStockLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(finishedStockLabel, gbc);
        
        // Input Quantity
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Input Quantity (kg):"), gbc);
        gbc.gridx = 1;
        inputQuantitySpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 99999.0, 1.0));
        inputQuantitySpinner.addChangeListener(e -> calculateConversionRate());
        panel.add(inputQuantitySpinner, gbc);
        
        // Output Quantity
        gbc.gridx = 2;
        panel.add(new JLabel("Output Quantity (kg):"), gbc);
        gbc.gridx = 3;
        outputQuantitySpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 99999.0, 1.0));
        outputQuantitySpinner.addChangeListener(e -> calculateConversionRate());
        panel.add(outputQuantitySpinner, gbc);
        
        // Waste Quantity
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Waste Quantity (kg):"), gbc);
        gbc.gridx = 1;
        wasteQuantitySpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 99999.0, 1.0));
        panel.add(wasteQuantitySpinner, gbc);
        
        // Conversion Rate (calculated)
        gbc.gridx = 2;
        panel.add(new JLabel("Conversion Rate:"), gbc);
        gbc.gridx = 3;
        conversionRateLabel = new JLabel("0.0%");
        conversionRateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        conversionRateLabel.setForeground(new Color(0, 100, 0));
        panel.add(conversionRateLabel, gbc);
        
        // Machine
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Machine:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        machineCombo = new JComboBox<>();
        panel.add(machineCombo, gbc);
        
        // Operator
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Operator:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        operatorCombo = new JComboBox<>();
        panel.add(operatorCombo, gbc);
        
        // Notes
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH;
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        panel.add(notesScroll, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);
        
        JButton saveButton = new JButton("Save Production Record");
        saveButton.setBackground(new Color(255, 140, 0));
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
            // Load raw products (paddy)
            List<Product> rawProducts = productDAO.getProductsByType("Raw_Paddy");
            rawProductCombo.removeAllItems();
            rawProductCombo.addItem(null); // Allow empty selection
            for (Product product : rawProducts) {
                rawProductCombo.addItem(product);
            }
            
            // Load finished products (rice)
            List<Product> finishedProducts = productDAO.getProductsByType("Finished_Rice");
            finishedProductCombo.removeAllItems();
            finishedProductCombo.addItem(null); // Allow empty selection
            for (Product product : finishedProducts) {
                finishedProductCombo.addItem(product);
            }
            
            // Load machines
            List<Machine> machines = machineDAO.getAllActiveMachines();
            machineCombo.removeAllItems();
            machineCombo.addItem(null); // Allow manual processing
            for (Machine machine : machines) {
                machineCombo.addItem(machine);
            }
            
            // Load staff (operators)
            List<Staff> staff = staffDAO.getAllActiveStaff();
            operatorCombo.removeAllItems();
            operatorCombo.addItem(null); // Allow empty selection
            for (Staff employee : staff) {
                operatorCombo.addItem(employee);
            }
            
            // Setup custom renderers
            setupComboBoxRenderers();
            
            // Initialize stock displays
            updateRawStockInfo();
            updateFinishedStockInfo();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setupComboBoxRenderers() {
        // Product renderers
        rawProductCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) {
                    setText(((Product) value).getProductName());
                } else if (value == null) {
                    setText("Select Raw Product");
                }
                return this;
            }
        });
        
        finishedProductCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) {
                    setText(((Product) value).getProductName());
                } else if (value == null) {
                    setText("Select Finished Product");
                }
                return this;
            }
        });
        
        // Machine renderer
        machineCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Machine) {
                    setText(((Machine) value).getMachineName());
                } else if (value == null) {
                    setText("Manual Processing");
                }
                return this;
            }
        });
        
        // Staff renderer
        operatorCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Staff) {
                    setText(((Staff) value).getEmployeeName());
                } else if (value == null) {
                    setText("No Operator Assigned");
                }
                return this;
            }
        });
    }
    
    private void updateRawStockInfo() {
        try {
            Product selectedProduct = (Product) rawProductCombo.getSelectedItem();
            if (selectedProduct != null) {
                StockInventory stock = stockDAO.getStockByProductId(selectedProduct.getProductId());
                if (stock != null) {
                    rawStockLabel.setText(String.format("%.2f kg", stock.getCurrentQuantity()));
                    
                    // Color code based on availability
                    if (stock.isOutOfStock()) {
                        rawStockLabel.setForeground(Color.RED);
                    } else if (stock.isLowStock()) {
                        rawStockLabel.setForeground(Color.ORANGE);
                    } else {
                        rawStockLabel.setForeground(new Color(0, 100, 0));
                    }
                } else {
                    rawStockLabel.setText("No stock info");
                    rawStockLabel.setForeground(Color.GRAY);
                }
            } else {
                rawStockLabel.setText("Select product");
                rawStockLabel.setForeground(Color.GRAY);
            }
        } catch (Exception e) {
            rawStockLabel.setText("Error loading stock");
            rawStockLabel.setForeground(Color.RED);
        }
    }
    
    private void updateFinishedStockInfo() {
        try {
            Product selectedProduct = (Product) finishedProductCombo.getSelectedItem();
            if (selectedProduct != null) {
                StockInventory stock = stockDAO.getStockByProductId(selectedProduct.getProductId());
                if (stock != null) {
                    finishedStockLabel.setText(String.format("%.2f kg", stock.getCurrentQuantity()));
                    finishedStockLabel.setForeground(new Color(0, 100, 0));
                } else {
                    finishedStockLabel.setText("No stock info");
                    finishedStockLabel.setForeground(Color.GRAY);
                }
            } else {
                finishedStockLabel.setText("Select product");
                finishedStockLabel.setForeground(Color.GRAY);
            }
        } catch (Exception e) {
            finishedStockLabel.setText("Error loading stock");
            finishedStockLabel.setForeground(Color.RED);
        }
    }
    
    private void calculateConversionRate() {
        try {
            double input = (Double) inputQuantitySpinner.getValue();
            double output = (Double) outputQuantitySpinner.getValue();
            
            if (input > 0) {
                double rate = (output / input) * 100;
                conversionRateLabel.setText(String.format("%.1f%%", rate));
                
                // Color code based on efficiency
                if (rate >= 70) {
                    conversionRateLabel.setForeground(new Color(0, 150, 0)); // Good
                } else if (rate >= 50) {
                    conversionRateLabel.setForeground(Color.ORANGE); // Average
                } else {
                    conversionRateLabel.setForeground(Color.RED); // Poor
                }
            } else {
                conversionRateLabel.setText("0.0%");
                conversionRateLabel.setForeground(Color.GRAY);
            }
        } catch (Exception e) {
            conversionRateLabel.setText("Error");
            conversionRateLabel.setForeground(Color.RED);
        }
    }
    
    private void generateProductionNumber() {
        String productionNumber = "PRD" + System.currentTimeMillis();
        productionNumberField.setText(productionNumber);
    }
    
    private void saveActionPerformed(ActionEvent evt) {
        try {
            // Validation
            Product rawProduct = (Product) rawProductCombo.getSelectedItem();
            Product finishedProduct = (Product) finishedProductCombo.getSelectedItem();
            double inputQuantity = (Double) inputQuantitySpinner.getValue();
            double outputQuantity = (Double) outputQuantitySpinner.getValue();
            double wasteQuantity = (Double) wasteQuantitySpinner.getValue();
            
            if (rawProduct == null) {
                JOptionPane.showMessageDialog(this, "Please select a raw product.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (finishedProduct == null) {
                JOptionPane.showMessageDialog(this, "Please select a finished product.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (inputQuantity <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid input quantity.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (outputQuantity <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid output quantity.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Check raw material availability
            if (!inventoryService.isStockSufficientForSale(rawProduct.getProductId(), inputQuantity)) {
                JOptionPane.showMessageDialog(this, 
                    "Insufficient raw material stock!\nRequired: " + inputQuantity + " kg\nAvailable: Check stock levels", 
                    "Stock Shortage", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create production record
            ProductionRecord productionRecord = new ProductionRecord();
            productionRecord.setProductionNumber(productionNumberField.getText());
            productionRecord.setProductionDate((Date) dateSpinner.getValue());
            productionRecord.setRawProductId(rawProduct.getProductId());
            productionRecord.setFinishedProductId(finishedProduct.getProductId());
            productionRecord.setInputQuantity(inputQuantity);
            productionRecord.setOutputQuantity(outputQuantity);
            productionRecord.setWasteQuantity(wasteQuantity);
            
            // Calculate conversion rate
            double conversionRate = (outputQuantity / inputQuantity) * 100;
            productionRecord.setConversionRate(conversionRate);
            
            // Optional machine and operator
            Machine selectedMachine = (Machine) machineCombo.getSelectedItem();
            if (selectedMachine != null) {
                productionRecord.setMachineId(selectedMachine.getMachineId());
            }
            
            Staff selectedOperator = (Staff) operatorCombo.getSelectedItem();
            if (selectedOperator != null) {
                productionRecord.setOperatorId(selectedOperator.getStaffId());
            }
            
            productionRecord.setNotes(notesArea.getText());
            
            // Save production record
            if (productionRecordDAO.insertProductionRecord(productionRecord)) {
                // Update stock levels
                // Reduce raw material stock
                inventoryService.updateStockAfterSales(rawProduct.getProductId(), inputQuantity);
                
                // Increase finished product stock
                inventoryService.updateStockAfterPurchase(finishedProduct.getProductId(), outputQuantity);
                
                saved = true;
                JOptionPane.showMessageDialog(this, 
                    "Production record saved successfully!\nProduction Number: " + productionRecord.getProductionNumber() + 
                    "\nConversion Rate: " + String.format("%.1f%%", conversionRate), 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error saving production record.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving production record: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
}