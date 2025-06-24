package com.housemanagement.ui.sidebar;

import com.housemanagement.controller.TinhTien;
import com.housemanagement.model.Bill;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class tien extends JPanel {
    private TinhTien tinhTienController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboThang;
    private JComboBox<ContractItem> cboHopDong;
    private JTextField txtOldE, txtNewE, txtTienNuoc, txtTongTien, txtGhiChu;
    private JComboBox<String> cboTrangThai, cboHinhThuc;
    private JButton btnThemHoaDon, btnCapNhat, btnXoa, btnLamMoi, btnTinhTien;
    private JLabel lblTienPhong, lblTienDichVu, lblSoDienTieuThu;
    private int selectedBillId = -1;

    public tien() {
        tinhTienController = new TinhTien();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel with split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);
        splitPane.setDividerSize(5);

        // Left panel - Form
        JPanel formPanel = createFormPanel();
        splitPane.setLeftComponent(new JScrollPane(formPanel));

        // Right panel - Table
        JPanel tablePanel = createTablePanel();
        splitPane.setRightComponent(tablePanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("QUẢN LÝ HÓA ĐƠN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));

        panel.add(titleLabel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;

        // Tiêu đề form
        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2;
        JLabel formTitle = new JLabel("THÔNG TIN HÓA ĐƠN");
        formTitle.setFont(new Font("Arial", Font.BOLD, 18));
        formTitle.setForeground(new Color(0, 123, 255));
        panel.add(formTitle, gbc);

        gbc.gridwidth = 1;

        // Chọn tháng
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Tháng:"), gbc);

        gbc.gridx = 1;
        String[] months = {"01/2024", "02/2024", "03/2024", "04/2024", "05/2024", "06/2024",
                "07/2024", "08/2024", "09/2024", "10/2024", "11/2024", "12/2024"};
        cboThang = new JComboBox<>(months);
        cboThang.setSelectedIndex(getCurrentMonth() - 1);
        panel.add(cboThang, gbc);
        row++;

        // Chọn hợp đồng
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Hợp đồng:"), gbc);

        gbc.gridx = 1;
        cboHopDong = new JComboBox<>();
        cboHopDong.addActionListener(e -> loadContractInfo());
        panel.add(cboHopDong, gbc);
        row++;

        // Tiền phòng
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Tiền phòng:"), gbc);

        gbc.gridx = 1;
        lblTienPhong = new JLabel("0 VNĐ");
        lblTienPhong.setFont(new Font("Arial", Font.BOLD, 14));
        lblTienPhong.setForeground(new Color(0, 123, 255));
        panel.add(lblTienPhong, gbc);
        row++;

        // Tiền dịch vụ
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Tiền dịch vụ:"), gbc);

        gbc.gridx = 1;
        lblTienDichVu = new JLabel("0 VNĐ");
        lblTienDichVu.setFont(new Font("Arial", Font.BOLD, 14));
        lblTienDichVu.setForeground(new Color(0, 123, 255));
        panel.add(lblTienDichVu, gbc);
        row++;

        // Chỉ số điện cũ
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Chỉ số điện cũ:"), gbc);

        gbc.gridx = 1;
        txtOldE = new JTextField(15);
        txtOldE.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                calculateElectricity();
                calculateTotal();
            }
        });
        panel.add(txtOldE, gbc);
        row++;

        // Chỉ số điện mới
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Chỉ số điện mới:"), gbc);

        gbc.gridx = 1;
        txtNewE = new JTextField(15);
        txtNewE.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                calculateElectricity();
                calculateTotal();
            }
        });
        panel.add(txtNewE, gbc);
        row++;

        // Số điện tiêu thụ (hiển thị)
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Điện tiêu thụ:"), gbc);

        gbc.gridx = 1;
        lblSoDienTieuThu = new JLabel("0 kWh");
        lblSoDienTieuThu.setFont(new Font("Arial", Font.BOLD, 14));
        lblSoDienTieuThu.setForeground(new Color(255, 87, 34));
        panel.add(lblSoDienTieuThu, gbc);
        row++;

        // Tiền nước
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Tiền nước:"), gbc);

        gbc.gridx = 1;
        txtTienNuoc = new JTextField("0");
        txtTienNuoc.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                calculateTotal();
            }
        });
        panel.add(txtTienNuoc, gbc);
        row++;

        // Tổng tiền
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Tổng tiền:"), gbc);

        gbc.gridx = 1;
        txtTongTien = new JTextField();
        txtTongTien.setEditable(false);
        txtTongTien.setFont(new Font("Arial", Font.BOLD, 14));
        txtTongTien.setForeground(Color.RED);
        panel.add(txtTongTien, gbc);
        row++;

        // Trạng thái
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Trạng thái:"), gbc);

        gbc.gridx = 1;
        String[] trangThai = {"Chưa thanh toán", "Đã thanh toán"};
        cboTrangThai = new JComboBox<>(trangThai);
        panel.add(cboTrangThai, gbc);
        row++;

        // Hình thức thanh toán
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Hình thức:"), gbc);

        gbc.gridx = 1;
        String[] hinhThuc = {"", "Tiền mặt", "Chuyển khoản"};
        cboHinhThuc = new JComboBox<>(hinhThuc);
        panel.add(cboHinhThuc, gbc);
        row++;

        // Ghi chú
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Ghi chú:"), gbc);

        gbc.gridx = 1;
        txtGhiChu = new JTextField();
        panel.add(txtGhiChu, gbc);
        row++;

        // Buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);

        btnThemHoaDon = createButton("Thêm HĐ", new Color(76, 175, 80));
        btnCapNhat = createButton("Cập nhật", new Color(255, 193, 7));
        btnXoa = createButton("Xóa", new Color(244, 67, 54));
        btnLamMoi = createButton("Làm mới", new Color(158, 158, 158));
        btnTinhTien = createButton("Tính tiền", new Color(33, 150, 243));

        btnThemHoaDon.addActionListener(e -> themHoaDon());
        btnCapNhat.addActionListener(e -> capNhatHoaDon());
        btnXoa.addActionListener(e -> xoaHoaDon());
        btnLamMoi.addActionListener(e -> lamMoiForm());
        btnTinhTien.addActionListener(e -> calculateTotal());

        buttonPanel.add(btnThemHoaDon);
        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);
        buttonPanel.add(btnTinhTien);

        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table
        String[] columns = {"ID", "Phòng", "Khách", "Tháng", "Chỉ số cũ", "Chỉ số mới", "Điện", "Nước", "Tổng tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 245, 253));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedBill();
            }
        });

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // Phòng
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Khách
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Tháng
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Chỉ số cũ
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Chỉ số mới
        table.getColumnModel().getColumn(6).setPreferredWidth(80);  // Điện
        table.getColumnModel().getColumn(7).setPreferredWidth(80);  // Nước
        table.getColumnModel().getColumn(8).setPreferredWidth(100); // Tổng tiền

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadData() {
        loadContracts();
        loadBills();
    }

    private void loadContracts() {
        try {
            cboHopDong.removeAllItems();
            List<TinhTien.Contract> contracts = tinhTienController.getActiveContracts();
            for (TinhTien.Contract contract : contracts) {
                cboHopDong.addItem(new ContractItem(contract));
            }
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách hợp đồng: " + e.getMessage());
        }
    }

    private void loadBills() {
        try {
            tableModel.setRowCount(0);
            List<Bill> bills = tinhTienController.getAllBills();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");

            for (Bill bill : bills) {
                Object[] row = {
                        bill.getBillId(),
                        bill.getRoomName(),
                        bill.getCustomerName(),
                        bill.getMonth() != null ? sdf.format(bill.getMonth()) : "",
                        bill.getOldE() != null ? bill.getOldE() : 0,
                        bill.getNewE() != null ? bill.getNewE() : 0,
                        bill.getElectricity() != null ? String.format("%.0f", bill.getElectricity()) : "0",
                        bill.getWater() != null ? String.format("%.0f", bill.getWater()) : "0",
                        bill.getTotal() != null ? String.format("%,.0f VNĐ", bill.getTotal()) : "0 VNĐ"
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách hóa đơn: " + e.getMessage());
        }
    }

    private void loadContractInfo() {
        ContractItem selected = (ContractItem) cboHopDong.getSelectedItem();
        if (selected != null) {
            TinhTien.Contract contract = selected.contract;
            lblTienPhong.setText(String.format("%,.0f VNĐ", contract.getRoomPrice()));

            try {
                BigDecimal serviceTotal = tinhTienController.getTotalServicePrice(contract.getContractId());
                lblTienDichVu.setText(String.format("%,.0f VNĐ", serviceTotal));
            } catch (Exception e) {
                lblTienDichVu.setText("0 VNĐ");
            }

            calculateTotal();
        }
    }

    private void calculateElectricity() {
        try {
            String oldEStr = txtOldE.getText().trim();
            String newEStr = txtNewE.getText().trim();

            if (!oldEStr.isEmpty() && !newEStr.isEmpty()) {
                int oldE = Integer.parseInt(oldEStr);
                int newE = Integer.parseInt(newEStr);
                int electricityUsed = newE - oldE;

                if (electricityUsed >= 0) {
                    lblSoDienTieuThu.setText(electricityUsed + " kWh");
                } else {
                    lblSoDienTieuThu.setText("0 kWh");
                }
            } else {
                lblSoDienTieuThu.setText("0 kWh");
            }
        } catch (NumberFormatException e) {
            lblSoDienTieuThu.setText("0 kWh");
        }
    }

    private void calculateTotal() {
        try {
            ContractItem selected = (ContractItem) cboHopDong.getSelectedItem();
            if (selected == null) return;

            // Tính tiền điện
            BigDecimal electricityAmount = BigDecimal.ZERO;
            String oldEStr = txtOldE.getText().trim();
            String newEStr = txtNewE.getText().trim();

            if (!oldEStr.isEmpty() && !newEStr.isEmpty()) {
                int oldE = Integer.parseInt(oldEStr);
                int newE = Integer.parseInt(newEStr);
                int electricityUsed = Math.max(0, newE - oldE);

                // Giả sử giá điện là 3500 VNĐ/kWh
                electricityAmount = new BigDecimal(electricityUsed * 3500);
            }

            // Tiền nước
            BigDecimal waterAmount = new BigDecimal(txtTienNuoc.getText().trim().isEmpty() ? "0" : txtTienNuoc.getText().trim());

            // Tiền phòng và dịch vụ
            BigDecimal roomPrice = selected.contract.getRoomPrice();
            BigDecimal servicePrice = BigDecimal.ZERO;
            try {
                servicePrice = tinhTienController.getTotalServicePrice(selected.contract.getContractId());
            } catch (Exception e) {
                // Ignore error, use 0
            }

            // Tổng tiền
            BigDecimal total = roomPrice.add(servicePrice).add(electricityAmount).add(waterAmount);
            txtTongTien.setText(String.format("%,.0f VNĐ", total));
        } catch (Exception e) {
            txtTongTien.setText("0 VNĐ");
        }
    }

    private void themHoaDon() {
        try {
            ContractItem selected = (ContractItem) cboHopDong.getSelectedItem();
            if (selected == null) {
                showError("Vui lòng chọn hợp đồng!");
                return;
            }

            String monthStr = (String) cboThang.getSelectedItem();

            // Convert month string to Date
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
            Date month = new Date(sdf.parse(monthStr).getTime());

            // Kiểm tra hóa đơn đã tồn tại
            Bill existingBill = tinhTienController.getBillByMonthAndContract(monthStr, selected.contract.getContractId());
            if (existingBill != null) {
                showError("Hóa đơn cho tháng này đã tồn tại!");
                return;
            }

            Bill bill = new Bill();
            bill.setContractId(selected.contract.getContractId());
            bill.setMonth(month);

            // Set chỉ số điện
            String oldEStr = txtOldE.getText().trim();
            String newEStr = txtNewE.getText().trim();
            if (!oldEStr.isEmpty()) {
                bill.setOldE(Integer.parseInt(oldEStr));
            }
            if (!newEStr.isEmpty()) {
                bill.setNewE(Integer.parseInt(newEStr));
            }

            // Tính tiền điện
            BigDecimal electricityAmount = BigDecimal.ZERO;
            if (bill.getOldE() != null && bill.getNewE() != null) {
                int electricityUsed = Math.max(0, bill.getNewE() - bill.getOldE());
                electricityAmount = new BigDecimal(electricityUsed * 3500); // 3500 VNĐ/kWh
            }
            bill.setElectricity(electricityAmount);

            // Tiền nước
            BigDecimal waterAmount = new BigDecimal(txtTienNuoc.getText().trim().isEmpty() ? "0" : txtTienNuoc.getText().trim());
            bill.setWater(waterAmount);

            // Tính tổng tiền
            BigDecimal roomPrice = selected.contract.getRoomPrice();
            BigDecimal servicePrice = BigDecimal.ZERO;
            try {
                servicePrice = tinhTienController.getTotalServicePrice(selected.contract.getContractId());
            } catch (Exception e) {
                // Ignore
            }

            BigDecimal total = roomPrice.add(servicePrice).add(electricityAmount).add(waterAmount);
            bill.setTotal(total);

            tinhTienController.createBill(bill);
            showInfo("Thêm hóa đơn thành công!");
            loadBills();
            lamMoiForm();
        } catch (Exception e) {
            showError("Lỗi khi thêm hóa đơn: " + e.getMessage());
        }
    }

    private void capNhatHoaDon() {
        if (selectedBillId <= 0) {
            showError("Vui lòng chọn hóa đơn cần cập nhật!");
            return;
        }

        try {
            Bill bill = tinhTienController.getBillById(selectedBillId);
            if (bill == null) {
                showError("Không tìm thấy hóa đơn!");
                return;
            }

            // Cập nhật chỉ số điện
            String oldEStr = txtOldE.getText().trim();
            String newEStr = txtNewE.getText().trim();
            if (!oldEStr.isEmpty()) {
                bill.setOldE(Integer.parseInt(oldEStr));
            }
            if (!newEStr.isEmpty()) {
                bill.setNewE(Integer.parseInt(newEStr));
            }

            // Tính lại tiền điện
            BigDecimal electricityAmount = BigDecimal.ZERO;
            if (bill.getOldE() != null && bill.getNewE() != null) {
                int electricityUsed = Math.max(0, bill.getNewE() - bill.getOldE());
                electricityAmount = new BigDecimal(electricityUsed * 3500);
            }
            bill.setElectricity(electricityAmount);

            // Cập nhật tiền nước
            BigDecimal waterAmount = new BigDecimal(txtTienNuoc.getText().trim().isEmpty() ? "0" : txtTienNuoc.getText().trim());
            bill.setWater(waterAmount);

            // Tính lại tổng tiền
            ContractItem selected = (ContractItem) cboHopDong.getSelectedItem();
            if (selected != null) {
                BigDecimal roomPrice = selected.contract.getRoomPrice();
                BigDecimal servicePrice = BigDecimal.ZERO;
                try {
                    servicePrice = tinhTienController.getTotalServicePrice(selected.contract.getContractId());
                } catch (Exception e) {
                    // Ignore
                }

                BigDecimal total = roomPrice.add(servicePrice).add(electricityAmount).add(waterAmount);
                bill.setTotal(total);
            }

            tinhTienController.updateBill(bill);
            showInfo("Cập nhật hóa đơn thành công!");
            loadBills();
            lamMoiForm();
        } catch (Exception e) {
            showError("Lỗi khi cập nhật hóa đơn: " + e.getMessage());
        }
    }

    private void xoaHoaDon() {
        if (selectedBillId <= 0) {
            showError("Vui lòng chọn hóa đơn cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa hóa đơn này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                tinhTienController.deleteBill(selectedBillId);
                showInfo("Xóa hóa đơn thành công!");
                loadBills();
                lamMoiForm();
            } catch (Exception e) {
                showError("Lỗi khi xóa hóa đơn: " + e.getMessage());
            }
        }
    }

    private void loadSelectedBill() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            selectedBillId = (int) tableModel.getValueAt(selectedRow, 0);

            try {
                Bill bill = tinhTienController.getBillById(selectedBillId);
                if (bill != null) {
                    // Set tháng
                    if (bill.getMonth() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
                        String monthStr = sdf.format(bill.getMonth());
                        cboThang.setSelectedItem(monthStr);
                    }

                    // Set hợp đồng
                    for (int i = 0; i < cboHopDong.getItemCount(); i++) {
                        ContractItem item = cboHopDong.getItemAt(i);
                        if (item.contract.getContractId() == bill.getContractId()) {
                            cboHopDong.setSelectedIndex(i);
                            break;
                        }
                    }

                    txtOldE.setText(bill.getOldE() != null ? bill.getOldE().toString() : "");
                    txtNewE.setText(bill.getNewE() != null ? bill.getNewE().toString() : "");
                    txtTienNuoc.setText(bill.getWater() != null ? bill.getWater().toString() : "0");
                    txtTongTien.setText(bill.getTotal() != null ? String.format("%,.0f VNĐ", bill.getTotal()) : "0 VNĐ");

                    calculateElectricity();
                }
            } catch (Exception e) {
                showError("Lỗi khi tải thông tin hóa đơn: " + e.getMessage());
            }
        }
    }

    private void lamMoiForm() {
        selectedBillId = -1;
        cboThang.setSelectedIndex(getCurrentMonth() - 1);
        if (cboHopDong.getItemCount() > 0) {
            cboHopDong.setSelectedIndex(0);
        }
        txtOldE.setText("");
        txtNewE.setText("");
        txtTienNuoc.setText("0");
        txtTongTien.setText("0 VNĐ");
        cboTrangThai.setSelectedIndex(0);
        cboHinhThuc.setSelectedIndex(0);
        txtGhiChu.setText("");
        lblSoDienTieuThu.setText("0 kWh");
        table.clearSelection();
    }

    private int getCurrentMonth() {
        return java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1;
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    // Inner class for ComboBox items
    private class ContractItem {
        TinhTien.Contract contract;

        ContractItem(TinhTien.Contract contract) {
            this.contract = contract;
        }

        @Override
        public String toString() {
            return contract.getRoomName() + " - " + contract.getCustomerName();
        }
    }
}