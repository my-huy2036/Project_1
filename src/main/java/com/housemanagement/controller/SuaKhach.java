package com.housemanagement.controller;

import com.housemanagement.model.Customer;
import com.housemanagement.ui.HouseUI;
import com.housemanagement.ui.sidebar.khach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SuaKhach {

    public static boolean showEditDialog(Frame parent, Customer customer, QLyKhach qlyKhachController, khach khachPanel) {
        // Create dialog
        JDialog dialog = new JDialog(parent, "Sửa thông tin khách hàng", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(parent);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel lblTitle = new JLabel("Sửa thông tin khách hàng");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(0, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;

        // Họ tên
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Họ tên*:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField txtHoTen = new JTextField(customer.getFullName(), 20);
        formPanel.add(txtHoTen, gbc);
        gbc.weightx = 0;
        y++;

        // Giới tính
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Giới tính*:"), gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setBackground(Color.WHITE);
        JRadioButton rbNam = new JRadioButton("Nam");
        JRadioButton rbNu = new JRadioButton("Nữ");
        rbNam.setBackground(Color.WHITE);
        rbNu.setBackground(Color.WHITE);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rbNam);
        genderGroup.add(rbNu);

        // Set selected based on current gender
        if ("M".equals(customer.getGender())) {
            rbNam.setSelected(true);
        } else {
            rbNu.setSelected(true);
        }

        genderPanel.add(rbNam);
        genderPanel.add(rbNu);
        formPanel.add(genderPanel, gbc);
        y++;

        // CCCD
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("CCCD/CMND*:"), gbc);
        gbc.gridx = 1;
        JTextField txtCCCD = new JTextField(customer.getIdentity(), 20);
        formPanel.add(txtCCCD, gbc);
        y++;

        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        JTextField txtPhone = new JTextField(customer.getPhone() != null ? customer.getPhone() : "", 20);
        formPanel.add(txtPhone, gbc);
        y++;

        // Email
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Email*:"), gbc);
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(customer.getEmail(), 20);
        formPanel.add(txtEmail, gbc);
        y++;

        // Địa chỉ
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Địa chỉ*:"), gbc);
        gbc.gridx = 1;
        JTextField txtDiaChi = new JTextField(customer.getAddress(), 20);
        formPanel.add(txtDiaChi, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 15, 10));

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.setBackground(HouseUI.LIGHT_GRAY_COLOR);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnSave = new JButton("Lưu");
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnSave.setBackground(HouseUI.SUCCESS_COLOR);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        final boolean[] result = {false};

        btnCancel.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });

        btnSave.addActionListener(e -> {
            // Get updated data
            String hoTen = txtHoTen.getText().trim();
            String gender = rbNam.isSelected() ? "M" : "F";
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String diaChi = txtDiaChi.getText().trim();
            String cccd = txtCCCD.getText().trim();

            // Basic validation
            if (hoTen.isEmpty() || email.isEmpty() || diaChi.isEmpty() || cccd.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập đầy đủ thông tin bắt buộc (*)",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update customer object
            customer.setFullName(hoTen);
            customer.setGender(gender);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setAddress(diaChi);
            customer.setIdentity(cccd);

            // Save changes
            if (qlyKhachController.updateCustomer(customer)) {
                JOptionPane.showMessageDialog(dialog,
                        "Cập nhật thông tin khách hàng thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                result[0] = true;
                dialog.dispose();

                // Refresh the list
                if (khachPanel != null) {
                    khachPanel.loadCustomerData();
                }
            }
        });

        // Add Enter key support
        JRootPane rootPane = dialog.getRootPane();
        rootPane.setDefaultButton(btnSave);

        // ESC key to cancel
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        rootPane.getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                btnCancel.doClick();
            }
        });

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);

        return result[0];
    }
}