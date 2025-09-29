package com.pradeepmill.ui.dialogs;

import com.pradeepmill.dao.PurchaseOrderDAO;
import com.pradeepmill.models.PurchaseOrder;
import com.pradeepmill.models.PaymentRecord;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Date;

public class PaymentUpdateDialog extends JDialog {
    
    private PurchaseOrderDAO purchaseOrderDAO;
    private PurchaseOrder purchaseOrder;
    private boolean updated = false;
    
    // UI Components
    private JLabel orderNumberLabel;
    private JLabel supplierLabel;
    private JLabel totalAmountLabel;
    private JLabel paidAmountLabel;
    private JLabel outstandingLabel;
    private JLabel currentStatusLabel;
    
    private JTextField paymentAmountField;
    private JComboBox<String> paymentMethodCombo;
    private JTextField referenceNumberField;
    private JTextArea notesArea;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    
    public PaymentUpdateDialog(Frame parent, PurchaseOrder order) {
        super(parent, "Update Payment - " + order.getPurchaseNumber(), true);
        this.purchaseOrderDAO = new PurchaseOrderDAO();
        this.purchaseOrder = order;
        
        initComponents();
        populateOrderDetails();
        setupEventListeners();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(500, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(63, 81, 181)); // Indigo
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Payment Update", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Order Details Panel
        JPanel orderDetailsPanel = createOrderDetailsPanel();
        
        // Payment Entry Panel
        JPanel paymentPanel = createPaymentEntryPanel();
        
        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        
        // Main Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(orderDetailsPanel, BorderLayout.NORTH);
        contentPanel.add(paymentPanel, BorderLayout.CENTER);
        
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createOrderDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), 
            "Purchase Order Details"
        ));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Order Number
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Order Number:"), gbc);
        gbc.gridx = 1;
        orderNumberLabel = new JLabel();
        orderNumberLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(orderNumberLabel, gbc);
        
        // Supplier
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Supplier:"), gbc);
        gbc.gridx = 1;
        supplierLabel = new JLabel();
        panel.add(supplierLabel, gbc);
        
        // Total Amount
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Total Amount:"), gbc);
        gbc.gridx = 1;
        totalAmountLabel = new JLabel();
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        totalAmountLabel.setForeground(new Color(33, 150, 243)); // Blue
        panel.add(totalAmountLabel, gbc);
        
        // Paid Amount
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Already Paid:"), gbc);
        gbc.gridx = 1;
        paidAmountLabel = new JLabel();
        paidAmountLabel.setForeground(new Color(76, 175, 80)); // Green
        panel.add(paidAmountLabel, gbc);
        
        // Outstanding Amount
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Outstanding:"), gbc);
        gbc.gridx = 1;
        outstandingLabel = new JLabel();
        outstandingLabel.setFont(new Font("Arial", Font.BOLD, 12));
        outstandingLabel.setForeground(new Color(244, 67, 54)); // Red
        panel.add(outstandingLabel, gbc);
        
        // Current Status
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Current Status:"), gbc);
        gbc.gridx = 1;
        currentStatusLabel = new JLabel();
        currentStatusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(currentStatusLabel, gbc);
        
        return panel;
    }
    
    private JPanel createPaymentEntryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), 
            "New Payment Entry"
        ));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Payment Amount
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Payment Amount:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        paymentAmountField = new JTextField(15);
        paymentAmountField.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(paymentAmountField, gbc);
        
        // Payment Method
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        paymentMethodCombo = new JComboBox<>(new String[]{
            "Cash", "Bank Transfer", "Cheque", "Credit Card", "Other"
        });
        panel.add(paymentMethodCombo, gbc);
        
        // Reference Number
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Reference Number:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        referenceNumberField = new JTextField(15);
        panel.add(referenceNumberField, gbc);
        
        // Notes
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        notesArea = new JTextArea(4, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane notesScroll = new JScrollPane(notesArea);
        panel.add(notesScroll, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        saveButton = new JButton("Save Payment");
        saveButton.setBackground(new Color(76, 175, 80)); // Green
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(120, 35));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(158, 158, 158)); // Gray
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        
        panel.add(cancelButton);
        panel.add(saveButton);
        
        return panel;
    }
    
    private void populateOrderDetails() {
        orderNumberLabel.setText(purchaseOrder.getPurchaseNumber());
        supplierLabel.setText(purchaseOrder.getSupplierName());
        totalAmountLabel.setText("Rs. " + currencyFormat.format(purchaseOrder.getTotalAmount()));
        paidAmountLabel.setText("Rs. " + currencyFormat.format(purchaseOrder.getPaidAmount()));
        outstandingLabel.setText("Rs. " + currencyFormat.format(purchaseOrder.getOutstandingAmount()));
        
        // Set status color
        String status = purchaseOrder.getPaymentStatus();
        currentStatusLabel.setText(status);
        
        if ("Paid".equals(status)) {
            currentStatusLabel.setForeground(new Color(76, 175, 80)); // Green
        } else if ("Partial".equals(status)) {
            currentStatusLabel.setForeground(new Color(255, 152, 0)); // Orange
        } else {
            currentStatusLabel.setForeground(new Color(244, 67, 54)); // Red
        }
        
        // Set default payment amount to outstanding amount
        paymentAmountField.setText(String.valueOf(purchaseOrder.getOutstandingAmount()));
    }
    
    private void setupEventListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePayment();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Validate payment amount on input
        paymentAmountField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                validatePaymentAmount();
            }
        });
    }
    
    private void validatePaymentAmount() {
        try {
            double amount = Double.parseDouble(paymentAmountField.getText());
            if (amount <= 0) {
                paymentAmountField.setBackground(new Color(255, 235, 238)); // Light red
                saveButton.setEnabled(false);
            } else if (amount > purchaseOrder.getOutstandingAmount()) {
                paymentAmountField.setBackground(new Color(255, 243, 224)); // Light orange
                saveButton.setEnabled(true);
            } else {
                paymentAmountField.setBackground(Color.WHITE);
                saveButton.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            paymentAmountField.setBackground(new Color(255, 235, 238)); // Light red
            saveButton.setEnabled(false);
        }
    }
    
    private void savePayment() {
        try {
            // Validate input
            double paymentAmount = Double.parseDouble(paymentAmountField.getText());
            if (paymentAmount <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid payment amount greater than zero.",
                    "Invalid Amount", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Calculate new totals
            double newPaidAmount = purchaseOrder.getPaidAmount() + paymentAmount;
            String newStatus;
            
            if (newPaidAmount >= purchaseOrder.getTotalAmount()) {
                newStatus = "Paid";
                newPaidAmount = purchaseOrder.getTotalAmount(); // Don't exceed total
            } else if (newPaidAmount > 0) {
                newStatus = "Partial";
            } else {
                newStatus = "Pending";
            }
            
            // Create payment record
            PaymentRecord paymentRecord = new PaymentRecord();
            paymentRecord.setPurchaseId(purchaseOrder.getPurchaseId());
            paymentRecord.setPaymentAmount(paymentAmount);
            paymentRecord.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());
            paymentRecord.setReferenceNumber(referenceNumberField.getText());
            paymentRecord.setPaymentDate(new Date());
            paymentRecord.setNotes(notesArea.getText());
            
            // Update purchase order
            boolean success = purchaseOrderDAO.updatePayment(
                purchaseOrder.getPurchaseId(), 
                newPaidAmount, 
                newStatus
            );
            
            if (success) {
                // Also save payment record for history
                purchaseOrderDAO.insertPaymentRecord(paymentRecord);
                
                updated = true;
                JOptionPane.showMessageDialog(this,
                    "Payment of Rs. " + currencyFormat.format(paymentAmount) + 
                    " has been recorded successfully.\n" +
                    "New Status: " + newStatus,
                    "Payment Saved", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to save payment. Please try again.",
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid numeric amount.",
                "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "An error occurred while saving payment: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isUpdated() {
        return updated;
    }
}