package com.housemanagement.ui;

import javax.swing.*;

// Màn hình quản lý phòng trọ
public class RoomManagementFrame extends JFrame {
    public RoomManagementFrame() {
        setTitle("Quản lý phòng trọ");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Giao diện Quản lý phòng trọ đang phát triển...", SwingConstants.CENTER);
        add(label);
    }
}

// Màn hình xem hợp đồng
class ContractListFrame extends JFrame {
    public ContractListFrame() {
        setTitle("Danh sách hợp đồng");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Giao diện Xem hợp đồng đang phát triển...", SwingConstants.CENTER);
        add(label);
    }
}

// Màn hình xem thông tin khách
class CustomerListFrame extends JFrame {
    public CustomerListFrame() {
        setTitle("Danh sách khách hàng");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Giao diện Xem thông tin khách hàng đang phát triển...", SwingConstants.CENTER);
        add(label);
    }
}

class BillSendingFrame extends JFrame {
    public BillSendingFrame() {
        setTitle("Gửi hóa đơn tháng");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Giao diện Gửi hóa đơn tháng đang phát triển...", SwingConstants.CENTER);
        add(label);
    }
}
