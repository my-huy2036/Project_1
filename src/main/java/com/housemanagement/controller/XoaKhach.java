package com.housemanagement.controller;

import com.housemanagement.model.Customer;
import com.housemanagement.ui.HouseUI;
import javax.swing.*;
import java.awt.*;

public class XoaKhach {

    public static boolean showDeleteConfirmDialog(Frame parent, Customer customer, QLyKhach qlyKhachController) {
        // Create custom dialog
        JDialog dialog = new JDialog(parent, "Xác nhận xóa khách hàng", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(parent);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Warning icon and message
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messagePanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel("⚠️");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        messagePanel.add(iconLabel);

        contentPanel.add(messagePanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Customer info
        JLabel lblMessage = new JLabel("Bạn có chắc muốn xóa khách hàng này?");
        lblMessage.setFont(new Font("Arial", Font.BOLD, 16));
        lblMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblMessage);

        contentPanel.add(Box.createVerticalStrut(15));

        // Customer details
        JPanel detailsPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setMaximumSize(new Dimension(350, 100));

        detailsPanel.add(createLabel("Họ tên:", Font.BOLD));
        detailsPanel.add(createLabel(customer.getFullName(), Font.PLAIN));

        detailsPanel.add(createLabel("CCCD:", Font.BOLD));
        detailsPanel.add(createLabel(customer.getIdentity(), Font.PLAIN));

        detailsPanel.add(createLabel("Điện thoại:", Font.BOLD));
        detailsPanel.add(createLabel(customer.getPhone() != null ? customer.getPhone() : "N/A", Font.PLAIN));

        detailsPanel.add(createLabel("Email:", Font.BOLD));
        detailsPanel.add(createLabel(customer.getEmail(), Font.PLAIN));

        contentPanel.add(detailsPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Warning text
        JLabel warningLabel = new JLabel("⚠️ Lưu ý: Thao tác này không thể hoàn tác!");
        warningLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        warningLabel.setForeground(HouseUI.DANGER_COLOR);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(warningLabel);

        dialog.add(contentPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.setBackground(HouseUI.LIGHT_GRAY_COLOR);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnDelete = new JButton("Xóa");
        btnDelete.setPreferredSize(new Dimension(100, 35));
        btnDelete.setBackground(HouseUI.DANGER_COLOR);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

        final boolean[] result = {false};

        btnCancel.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });

        btnDelete.addActionListener(e -> {
            // Perform delete
            boolean success = qlyKhachController.deleteCustomer(customer.getCustomerId());
            if (success) {
                JOptionPane.showMessageDialog(dialog,
                        "Xóa khách hàng thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                result[0] = true;
                dialog.dispose();
            } else {
                // Error message already shown by controller
                result[0] = false;
            }
        });

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnDelete);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Show dialog
        dialog.setVisible(true);

        return result[0];
    }

    private static JLabel createLabel(String text, int fontStyle) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", fontStyle, 13));
        return label;
    }
}