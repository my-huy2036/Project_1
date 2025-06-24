package com.housemanagement.controller;

import com.housemanagement.model.Customer;
import com.housemanagement.ui.HouseUI;
import com.housemanagement.ui.sidebar.khach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ThemKhach extends JPanel {
    private JTextField txtHoTen, txtPhone, txtEmail, txtDiaChi, txtCCCD;
    private JRadioButton rbNam, rbNu;
    private ButtonGroup genderGroup;

    private JButton btnSave;
    private JButton btnBackToList;

    private QLyKhach qlyKhachController;
    private khach parentKhachPanel;

    public ThemKhach(khach parentKhachPanel, QLyKhach qlyKhachController) {
        this.parentKhachPanel = parentKhachPanel;
        this.qlyKhachController = qlyKhachController;

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Title
        JLabel lblTitle = new JLabel("Thêm Khách Hàng Mới");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;

        // Họ tên
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Họ tên*:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtHoTen = new JTextField(20);
        formPanel.add(txtHoTen, gbc);
        gbc.weightx = 0;
        y++;

        // Giới tính
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Giới tính*:"), gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setBackground(Color.WHITE);
        rbNam = new JRadioButton("Nam");
        rbNu = new JRadioButton("Nữ");
        rbNam.setBackground(Color.WHITE);
        rbNu.setBackground(Color.WHITE);
        genderGroup = new ButtonGroup();
        genderGroup.add(rbNam);
        genderGroup.add(rbNu);
        rbNam.setSelected(true);
        genderPanel.add(rbNam);
        genderPanel.add(rbNu);
        formPanel.add(genderPanel, gbc);
        y++;

        // CCCD
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("CCCD/CMND*:"), gbc);
        gbc.gridx = 1;
        txtCCCD = new JTextField(20);
        formPanel.add(txtCCCD, gbc);
        y++;

        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField(20);
        formPanel.add(txtPhone, gbc);
        y++;

        // Email
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Email*:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail, gbc);
        y++;

        // Địa chỉ
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Địa chỉ*:"), gbc);
        gbc.gridx = 1;
        txtDiaChi = new JTextField(20);
        formPanel.add(txtDiaChi, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnSave = createStyledButton("Lưu", HouseUI.SUCCESS_COLOR);
        btnBackToList = createStyledButton("Quay về danh sách", HouseUI.LIGHT_GRAY_COLOR);

        btnSave.addActionListener(e -> saveCustomer());
        btnBackToList.addActionListener(e -> parentKhachPanel.switchToCustomerList());

        buttonPanel.add(btnBackToList);
        buttonPanel.add(btnSave);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveCustomer() {
        // Get data
        String hoTen = txtHoTen.getText().trim();
        String gender = rbNam.isSelected() ? "M" : "F";
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String cccd = txtCCCD.getText().trim();

        // Basic validation
        if (hoTen.isEmpty() || email.isEmpty() || diaChi.isEmpty() || cccd.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin bắt buộc (*)",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create customer object
        Customer customer = qlyKhachController.prepareCustomerData(
                hoTen, gender, phone, email, diaChi, cccd
        );

        // Save
        if (qlyKhachController.addCustomer(customer)) {
            JOptionPane.showMessageDialog(this,
                    "Thêm khách hàng thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            parentKhachPanel.switchToCustomerList();
        }
    }

    public void clearForm() {
        txtHoTen.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        txtCCCD.setText("");
        rbNam.setSelected(true);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(HouseUI.DEFAULT_FONT);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setMargin(new Insets(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}