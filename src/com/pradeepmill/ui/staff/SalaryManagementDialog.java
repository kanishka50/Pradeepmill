package com.pradeepmill.ui.staff;

import com.pradeepmill.dao.StaffDAO;
import com.pradeepmill.dao.SalaryPaymentDAO;
import com.pradeepmill.models.Staff;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SalaryManagementDialog extends JDialog {
    
    private StaffDAO staffDAO;
    private SalaryPaymentDAO salaryDAO;
    private String paymentMonth;
    private boolean processed = false;
    
    public SalaryManagementDialog(Frame parent, String paymentMonth) {
        super(parent, "Process Monthly Salary - " + paymentMonth, true);
        this.paymentMonth = paymentMonth;
        this.staffDAO = new StaffDAO();
        this.salaryDAO = new SalaryPaymentDAO();
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(500, 300);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(184, 134, 11));
        titlePanel.setPreferredSize(new Dimension(0, 50));
        
        JLabel titleLabel = new JLabel("Process Monthly Salary Payments", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Month info
        JLabel monthLabel = new JLabel("Processing salary payments for: " + paymentMonth);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(monthLabel, gbc);
        
        // Staff count info
        int activeStaffCount = staffDAO.getTotalStaffCount();
        double totalSalaryExpense = staffDAO.getTotalMonthlySalaryExpense();
        
        JLabel staffCountLabel = new JLabel("Active staff members: " + activeStaffCount);
        gbc.gridy = 1;
        contentPanel.add(staffCountLabel, gbc);
        
        JLabel expenseLabel = new JLabel("Total monthly expense: Rs. " + String.format("%.2f", totalSalaryExpense));
        gbc.gridy = 2;
        contentPanel.add(expenseLabel, gbc);
        
        // Warning message
        JLabel warningLabel = new JLabel("<html><center>This will create salary payment records<br>for all active employees.</center></html>");
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 3;
        contentPanel.add(warningLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        JButton processButton = new JButton("Process Payments");
        JButton cancelButton = new JButton("Cancel");
        
        processButton.addActionListener(this::processPaymentsActionPerformed);
        cancelButton.addActionListener(this::cancelActionPerformed);
        
        processButton.setBackground(new Color(76, 175, 80));
        processButton.setForeground(Color.WHITE);
        processButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        buttonPanel.add(processButton);
        buttonPanel.add(cancelButton);
        
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void processPaymentsActionPerformed(ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to process salary payments for " + paymentMonth + "?",
            "Confirm Processing",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int processedCount = salaryDAO.processMonthlyPayments(paymentMonth);
                
                if (processedCount > 0) {
                    processed = true;
                    JOptionPane.showMessageDialog(this,
                        "Successfully processed salary payments!\n" +
                        "Processed: " + processedCount + " employees",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No new payments were processed.\n" +
                        "Payments may already exist for this month.",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error processing payments: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cancelActionPerformed(ActionEvent evt) {
        dispose();
    }
    
    public boolean isProcessed() {
        return processed;
    }
}