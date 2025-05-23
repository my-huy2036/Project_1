package com.housemanagement.ui;

import javax.swing.*;
import java.awt.*;

public class OwnerDashboardFrame extends JFrame {
    public OwnerDashboardFrame() {
        setTitle("Chủ Nhà Trọ");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.add(new JLabel("🏠 Xin chào!", SwingConstants.CENTER));

        JButton btnManageRooms = new JButton("Quản lý phòng trọ");
        JButton btnViewContracts = new JButton("Xem hợp đồng");
        JButton btnViewCustomers = new JButton("Xem thông tin khách");
        JButton btnSendBills = new JButton("Gửi hóa đơn tháng");

        btnManageRooms.addActionListener(e -> {
            new RoomManagementFrame().setVisible(true);
            dispose();
        });

        btnViewContracts.addActionListener(e -> {
            new ContractListFrame().setVisible(true);
            dispose();
        });

        btnViewCustomers.addActionListener(e -> {
            new CustomerListFrame().setVisible(true);
            dispose();
        });

        btnSendBills.addActionListener(e -> {
            new BillSendingFrame().setVisible(true);
            dispose();
        });

        panel.add(btnManageRooms);
        panel.add(btnViewContracts);
        panel.add(btnViewCustomers);
        panel.add(btnSendBills);

        add(panel);
    }
}
