package com.housemanagement.controller;  // hiện đang bị lỗi+ko sử dụng

import com.housemanagement.model.Room;
import com.housemanagement.ui.HouseUI;
import com.housemanagement.ui.sidebar.phong;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ThemPhong extends JPanel {
    private JTextField txtRoomNameAdd, txtRentAdd, txtMaxAdd;

    private JButton btnSaveRoom;
    private JButton btnBackToList;

    private QLyPhong qLyPhongController;
    private phong parentPhongPanel;

    public ThemPhong(phong parentPhongPanel, QLyPhong qLyPhongController) {
        this.parentPhongPanel = parentPhongPanel;
        this.qLyPhongController = qLyPhongController;

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Thêm Phòng Mới");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        // Tên phòng
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Tên phòng*:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        txtRoomNameAdd = new JTextField(20);
        formPanel.add(txtRoomNameAdd, gbc);
        gbc.gridwidth = 1; gbc.weightx = 0;
        y++;

        // Đơn giá
        gbc.gridx = 0; gbc.gridy = y; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Đơn giá (VNĐ)*:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.WEST;
        txtRentAdd = new JTextField();
        formPanel.add(txtRentAdd, gbc);
        gbc.gridwidth = 1;
        y++;

        // Số lượng người tối đa
        gbc.gridx = 0; gbc.gridy = y; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Số người tối đa:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.WEST;
        txtMaxAdd = new JTextField();
        formPanel.add(txtMaxAdd, gbc);
        gbc.gridwidth = 1;
        y++;

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        btnSaveRoom = createStyledButtonInternal("Lưu", HouseUI.SUCCESS_COLOR);
        btnBackToList = createStyledButtonInternal("Quay về danh sách", HouseUI.LIGHT_GRAY_COLOR);

        btnSaveRoom.addActionListener(e -> {
            boolean success = qLyPhongController.processAddRoom(
                    txtRoomNameAdd.getText().trim(),
                    txtRentAdd.getText().trim(),
                    txtMaxAdd.getText().trim()
            );


            if (success) {
                JOptionPane.showMessageDialog(this, "Thêm phòng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                parentPhongPanel.switchToRoomList();
            }
        });

        btnBackToList.addActionListener(e -> parentPhongPanel.switchToRoomList());

        buttonPanel.add(btnBackToList);
        buttonPanel.add(btnSaveRoom);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void clearForm() {
        txtRoomNameAdd.setText("");
        txtRentAdd.setText("");
        txtMaxAdd.setText("");
    }

    private JButton createStyledButtonInternal(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(HouseUI.DEFAULT_FONT);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setMargin(new Insets(5,10,5,10));
        return button;
    }
}
