package com.housemanagement.ui.sidebar;

import com.housemanagement.controller.QLyHopDong;
import com.housemanagement.controller.TaoHopDong;
import com.housemanagement.model.Contract;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;

public class hopdong extends JPanel {
    private QLyHopDong qlyHopDongController;
    private TaoHopDong taoHopDongController; // Thêm controller tạo hợp đồng
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnThemHopDong, btnTaoHopDong, btnSuaHopDong, btnXoaHopDong, btnKetThuc;
    private JPanel formPanel;
    private boolean isAddMode = false;
    private boolean isCreateMode = false; // Thêm flag để phân biệt chế độ tạo mới
    private int selectedContractId = -1;

    // Form fields
    private JComboBox<QLyHopDong.Customer> cboKhachHang;
    private JComboBox<QLyHopDong.Room> cboPhong;
    private JTextField txtNgayBatDau, txtNgayKetThuc, txtGiaThue, txtTienCoc, txtGhiChu;
    private JComboBox<String> cboTrangThai;

    public hopdong() {
        qlyHopDongController = new QLyHopDong();
        taoHopDongController = new TaoHopDong(); // Khởi tạo controller tạo hợp đồng
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Form panel (hidden by default)
        formPanel = createFormPanel();
        formPanel.setVisible(false);
        mainPanel.add(formPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("QUẢN LÝ HỢP ĐỒNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        // Nút Tạo hợp đồng mới
        btnTaoHopDong = new JButton("+ Tạo hợp đồng");
        btnTaoHopDong.setBackground(new Color(76, 175, 80)); // Màu xanh lá
        btnTaoHopDong.setForeground(Color.WHITE);
        btnTaoHopDong.setFont(new Font("Arial", Font.BOLD, 14));
        btnTaoHopDong.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnTaoHopDong.setFocusPainted(false);
        btnTaoHopDong.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTaoHopDong.addActionListener(e -> toggleCreateMode());

        btnThemHopDong = new JButton("+ Thêm hợp đồng");
        btnThemHopDong.setBackground(new Color(255, 193, 86));
        btnThemHopDong.setForeground(Color.WHITE);
        btnThemHopDong.setFont(new Font("Arial", Font.BOLD, 14));
        btnThemHopDong.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnThemHopDong.setFocusPainted(false);
        btnThemHopDong.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThemHopDong.addActionListener(e -> toggleAddMode());

        buttonPanel.add(btnTaoHopDong);
        buttonPanel.add(Box.createHorizontalStrut(10)); // Khoảng cách giữa 2 nút
        buttonPanel.add(btnThemHopDong);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        int row = 0;

        // Khách hàng
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Khách hàng:"), gbc);

        gbc.gridx = 1;
        cboKhachHang = new JComboBox<>();
        panel.add(cboKhachHang, gbc);

        // Phòng
        gbc.gridx = 2;
        panel.add(new JLabel("Phòng:"), gbc);

        gbc.gridx = 3;
        cboPhong = new JComboBox<>();
        cboPhong.addActionListener(e -> updatePriceFromRoom());
        panel.add(cboPhong, gbc);
        row++;

        // Ngày bắt đầu
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Ngày bắt đầu:"), gbc);

        gbc.gridx = 1;
        txtNgayBatDau = new JTextField(15);
        txtNgayBatDau.setToolTipText("dd/MM/yyyy");
        panel.add(txtNgayBatDau, gbc);

        // Ngày kết thúc
        gbc.gridx = 2;
        panel.add(new JLabel("Ngày kết thúc:"), gbc);

        gbc.gridx = 3;
        txtNgayKetThuc = new JTextField(15);
        txtNgayKetThuc.setToolTipText("dd/MM/yyyy (có thể để trống)");
        panel.add(txtNgayKetThuc, gbc);
        row++;

        // Giá thuê
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Giá thuê/tháng:"), gbc);

        gbc.gridx = 1;
        txtGiaThue = new JTextField(15);
        panel.add(txtGiaThue, gbc);

        // Tiền cọc
        gbc.gridx = 2;
        panel.add(new JLabel("Tiền cọc:"), gbc);

        gbc.gridx = 3;
        txtTienCoc = new JTextField(15);
        panel.add(txtTienCoc, gbc);
        row++;

        // Trạng thái
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Trạng thái:"), gbc);

        gbc.gridx = 1;
        String[] trangThai = {"active", "expired", "terminated"};
        cboTrangThai = new JComboBox<>(trangThai);
        panel.add(cboTrangThai, gbc);

        // Ghi chú
        gbc.gridx = 2;
        panel.add(new JLabel("Ghi chú:"), gbc);

        gbc.gridx = 3;
        txtGhiChu = new JTextField(15);
        panel.add(txtGhiChu, gbc);
        row++;

        // Buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton btnLuu = new JButton("Lưu");
        btnLuu.setBackground(new Color(76, 175, 80));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setPreferredSize(new Dimension(100, 35));
        btnLuu.addActionListener(e -> saveContract());

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setBackground(new Color(244, 67, 54));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setPreferredSize(new Dimension(100, 35));
        btnHuy.addActionListener(e -> cancelForm());

        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBackground(Color.WHITE);

        btnSuaHopDong = new JButton("Sửa");
        btnSuaHopDong.setBackground(new Color(255, 193, 7));
        btnSuaHopDong.setForeground(Color.WHITE);
        btnSuaHopDong.setPreferredSize(new Dimension(100, 35));
        btnSuaHopDong.addActionListener(e -> editContract());

        btnXoaHopDong = new JButton("Xóa");
        btnXoaHopDong.setBackground(new Color(244, 67, 54));
        btnXoaHopDong.setForeground(Color.WHITE);
        btnXoaHopDong.setPreferredSize(new Dimension(100, 35));
        btnXoaHopDong.addActionListener(e -> deleteContract());

        btnKetThuc = new JButton("Kết thúc HĐ");
        btnKetThuc.setBackground(new Color(158, 158, 158));
        btnKetThuc.setForeground(Color.WHITE);
        btnKetThuc.setPreferredSize(new Dimension(120, 35));
        btnKetThuc.addActionListener(e -> terminateContract());

        actionPanel.add(btnSuaHopDong);
        actionPanel.add(btnXoaHopDong);
        actionPanel.add(btnKetThuc);

        panel.add(actionPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Phòng", "Khách hàng", "Bắt đầu", "Kết thúc",
                "Giá thuê", "Tiền cọc", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 245, 253));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedContractId = (int) tableModel.getValueAt(selectedRow, 0);
                    updateButtonStates();
                }
            }
        });

        // Custom renderer for status
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value != null) {
                    String status = value.toString();
                    switch (status) {
                        case "Đang hoạt động":
                            c.setForeground(new Color(76, 175, 80));
                            break;
                        case "Đã hết hạn":
                            c.setForeground(new Color(255, 193, 7));
                            break;
                        case "Đã kết thúc":
                            c.setForeground(new Color(244, 67, 54));
                            break;
                    }
                }
                return c;
            }
        });

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadData() {
        loadCustomers();
        loadRooms();
        loadContracts();
        updateButtonStates();
    }

    private void loadCustomers() {
        try {
            cboKhachHang.removeAllItems();
            List<QLyHopDong.Customer> customers = isCreateMode ?
                    taoHopDongController.getAllCustomers() : qlyHopDongController.getAllCustomers();
            for (QLyHopDong.Customer customer : customers) {
                cboKhachHang.addItem(customer);
            }
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách khách hàng: " + e.getMessage());
        }
    }

    private void loadRooms() {
        try {
            cboPhong.removeAllItems();
            List<QLyHopDong.Room> rooms = isCreateMode ?
                    taoHopDongController.getAvailableRooms() : qlyHopDongController.getAvailableRooms();
            for (QLyHopDong.Room room : rooms) {
                cboPhong.addItem(room);
            }
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách phòng: " + e.getMessage());
        }
    }

    private void loadContracts() {
        try {
            tableModel.setRowCount(0);
            List<Contract> contracts = qlyHopDongController.getAllContracts();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            for (Contract contract : contracts) {
                Object[] row = {
                        contract.getContractId(),
                        contract.getRoomName(),
                        contract.getCustomerName(),
                        sdf.format(contract.getStartDate()),
                        contract.getEndDate() != null ? sdf.format(contract.getEndDate()) : "",
                        contract.getDeposit(),
                        contract.getStatus()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách hợp đồng: " + e.getMessage());
        }
    }

    // Phương thức mới cho chế độ tạo hợp đồng
    private void toggleCreateMode() {
        isCreateMode = !isCreateMode;
        isAddMode = isCreateMode; // Hiển thị form khi ở chế độ tạo
        formPanel.setVisible(isAddMode);
        btnTaoHopDong.setText(isCreateMode ? "Đóng" : "+ Tạo hợp đồng");

        if (isCreateMode) {
            // Đóng chế độ thêm nếu đang mở
            if (btnThemHopDong.getText().equals("Đóng")) {
                btnThemHopDong.setText("+ Thêm hợp đồng");
            }
            clearForm();
            loadCustomers(); // Tải lại danh sách khách hàng
            loadRooms(); // Tải lại danh sách phòng trống
        } else {
            isAddMode = false;
        }
    }

    private void toggleAddMode() {
        if (isCreateMode) return; // Không cho phép chế độ thêm khi đang tạo hợp đồng

        isAddMode = !isAddMode;
        formPanel.setVisible(isAddMode);
        btnThemHopDong.setText(isAddMode ? "Đóng" : "+ Thêm hợp đồng");

        if (isAddMode) {
            clearForm();
            loadRooms(); // Reload available rooms
        }
    }

    private void cancelForm() {
        if (isCreateMode) {
            toggleCreateMode();
        } else {
            toggleAddMode();
        }
    }

    private void saveContract() {
        try {
            // Validate
            if (cboKhachHang.getSelectedItem() == null || cboPhong.getSelectedItem() == null) {
                showError("Vui lòng chọn khách hàng và phòng!");
                return;
            }

            if (txtNgayBatDau.getText().trim().isEmpty() || txtGiaThue.getText().trim().isEmpty()) {
                showError("Vui lòng nhập ngày bắt đầu và giá thuê!");
                return;
            }

            Contract contract = new Contract();
            QLyHopDong.Customer customer = (QLyHopDong.Customer) cboKhachHang.getSelectedItem();
            QLyHopDong.Room room = (QLyHopDong.Room) cboPhong.getSelectedItem();

            contract.setCustomerId(customer.getCustomerId());
            contract.setRoomId(room.getRoomId());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            contract.setStartDate(sdf.parse(txtNgayBatDau.getText()));

            if (!txtNgayKetThuc.getText().trim().isEmpty()) {
                contract.setEndDate(sdf.parse(txtNgayKetThuc.getText()));
            }

            contract.setDeposit(txtTienCoc.getText().trim().isEmpty() ? 0 :
                    Double.parseDouble(txtTienCoc.getText().trim()));
            contract.setNote(txtGhiChu.getText().trim());
            contract.setStatus((String) cboTrangThai.getSelectedItem());

            if (isCreateMode) {
                // Sử dụng TaoHopDong controller để tạo hợp đồng mới
                taoHopDongController.createContract(contract);
                showInfo("Tạo hợp đồng thành công!");
                toggleCreateMode();
            } else if (selectedContractId > 0) {
                // Update existing contract
                contract.setContractId(selectedContractId);
                qlyHopDongController.updateContract(contract);
                showInfo("Cập nhật hợp đồng thành công!");
                toggleAddMode();
            } else {
                // Create new contract using normal method
                qlyHopDongController.createContract(contract);
                showInfo("Thêm hợp đồng thành công!");
                toggleAddMode();
            }

            loadContracts();
            clearForm();

        } catch (Exception e) {
            showError("Lỗi khi lưu hợp đồng: " + e.getMessage());
        }
    }

    private void editContract() {
        if (selectedContractId <= 0) {
            showError("Vui lòng chọn hợp đồng cần sửa!");
            return;
        }

        // Không cho phép sửa khi đang ở chế độ tạo hợp đồng
        if (isCreateMode) {
            showError("Vui lòng đóng chế độ tạo hợp đồng trước!");
            return;
        }

        try {
            Contract contract = qlyHopDongController.getContractById(selectedContractId);
            if (contract == null) {
                showError("Không tìm thấy hợp đồng!");
                return;
            }

            // Load all rooms including occupied ones for editing
            cboPhong.removeAllItems();
            List<QLyHopDong.Room> rooms = qlyHopDongController.getAvailableRooms();
            for (QLyHopDong.Room room : rooms) {
                cboPhong.addItem(room);
            }

            // Add current room if not in available list
            boolean found = false;
            for (int i = 0; i < cboPhong.getItemCount(); i++) {
                if (cboPhong.getItemAt(i).getRoomId() == contract.getRoomId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                QLyHopDong.Room currentRoom = new QLyHopDong.Room();
                currentRoom.setRoomId(contract.getRoomId());
                currentRoom.setRoomName(contract.getRoomName());
                cboPhong.addItem(currentRoom);
            }

            // Set form values
            for (int i = 0; i < cboKhachHang.getItemCount(); i++) {
                if (cboKhachHang.getItemAt(i).getCustomerId() == contract.getCustomerId()) {
                    cboKhachHang.setSelectedIndex(i);
                    break;
                }
            }

            for (int i = 0; i < cboPhong.getItemCount(); i++) {
                if (cboPhong.getItemAt(i).getRoomId() == contract.getRoomId()) {
                    cboPhong.setSelectedIndex(i);
                    break;
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txtNgayBatDau.setText(sdf.format(contract.getStartDate()));
            if (contract.getEndDate() != null) {
                txtNgayKetThuc.setText(sdf.format(contract.getEndDate()));
            }

            txtTienCoc.setText(String.valueOf(contract.getDeposit()));
            txtGhiChu.setText(contract.getNote() != null ? contract.getNote() : "");
            cboTrangThai.setSelectedItem(contract.getStatus());

            isAddMode = true;
            formPanel.setVisible(true);
            btnThemHopDong.setText("Đóng");

        } catch (Exception e) {
            showError("Lỗi khi tải thông tin hợp đồng: " + e.getMessage());
        }
    }

    private void deleteContract() {
        if (selectedContractId <= 0) {
            showError("Vui lòng chọn hợp đồng cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa hợp đồng này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                qlyHopDongController.deleteContract(selectedContractId);
                showInfo("Xóa hợp đồng thành công!");
                loadContracts();
                selectedContractId = -1;
                updateButtonStates();
            } catch (Exception e) {
                showError("Lỗi khi xóa hợp đồng: " + e.getMessage());
            }
        }
    }

    private void terminateContract() {
        if (selectedContractId <= 0) {
            showError("Vui lòng chọn hợp đồng cần kết thúc!");
            return;
        }

        try {
            Contract contract = qlyHopDongController.getContractById(selectedContractId);
            if (contract == null) {
                showError("Không tìm thấy hợp đồng!");
                return;
            }

            if (!"active".equals(contract.getStatus())) {
                showError("Chỉ có thể kết thúc hợp đồng đang hoạt động!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn kết thúc hợp đồng này?",
                    "Xác nhận kết thúc",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                qlyHopDongController.terminateContract(selectedContractId, (java.sql.Date) new Date());
                showInfo("Kết thúc hợp đồng thành công!");
                loadContracts();
                selectedContractId = -1;
                updateButtonStates();
            }
        } catch (Exception e) {
            showError("Lỗi khi kết thúc hợp đồng: " + e.getMessage());
        }
    }

    private void updatePriceFromRoom() {
        QLyHopDong.Room room = (QLyHopDong.Room) cboPhong.getSelectedItem();
        if (room != null) {
            txtGiaThue.setText(String.valueOf(room.getPrice()));
        }
    }

    private void clearForm() {
        selectedContractId = -1;
        if (cboKhachHang.getItemCount() > 0) cboKhachHang.setSelectedIndex(0);
        if (cboPhong.getItemCount() > 0) cboPhong.setSelectedIndex(0);
        txtNgayBatDau.setText("");
        txtNgayKetThuc.setText("");
        txtGiaThue.setText("");
        txtTienCoc.setText("");
        txtGhiChu.setText("");
        cboTrangThai.setSelectedIndex(0);
    }

    private void updateButtonStates() {
        boolean hasSelection = selectedContractId > 0;
        btnSuaHopDong.setEnabled(hasSelection && !isCreateMode);
        btnXoaHopDong.setEnabled(hasSelection && !isCreateMode);
        btnKetThuc.setEnabled(hasSelection && !isCreateMode);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}