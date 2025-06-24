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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class tien extends JPanel {
    private TinhTien tinhTienController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboThang;
    private JComboBox<ContractItem> cboHopDong;
    private JTextField txtOldE, txtNewE, txtTienNuoc, txtTongTien, txtGhiChu;
    private JButton btnThemHoaDon, btnCapNhat, btnXoa, btnLamMoi, btnTinhTien, btnTimKiem;
    private JLabel lblTienPhong, lblTienDichVu, lblSoDienTieuThu, lblTienDien;
    private JTextField txtTimKiem;
    private int selectedBillId = -1;
    private DecimalFormat currencyFormat = new DecimalFormat("#,##0");

    public tien() {
        tinhTienController = new TinhTien();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setDividerSize(8);
        splitPane.setResizeWeight(0.4);

        // Left panel - Form
        JPanel formPanel = createFormPanel();
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(null);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        splitPane.setLeftComponent(formScrollPane);

        // Right panel - Table
        JPanel tablePanel = createTablePanel();
        splitPane.setRightComponent(tablePanel);

        add(splitPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 152, 219));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Title
        JLabel titleLabel = new JLabel("💰 QUẢN LÝ HÓA ĐƠN ĐIỆN NƯỚC");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Tính toán và quản lý các khoản thu hàng tháng");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 220, 220));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        panel.add(titlePanel, BorderLayout.WEST);

        // Quick actions
        JPanel quickActions = createQuickActions();
        panel.add(quickActions, BorderLayout.EAST);

        return panel;
    }

    private JPanel createQuickActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);

        JButton btnTinhToanNhanh = createHeaderButton("Tính toán nhanh");
        JButton btnBaoCao = createHeaderButton("Báo cáo");
        JButton btnXuatFile = createHeaderButton("Xuất file");

        btnTinhToanNhanh.addActionListener(e -> tinhToanNhanh());
        btnBaoCao.addActionListener(e -> xemBaoCao());
        btnXuatFile.addActionListener(e -> xuatFile());

        panel.add(btnTinhToanNhanh);
        panel.add(btnBaoCao);
        panel.add(btnXuatFile);

        return panel;
    }

    private JButton createHeaderButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setBorder(new RoundedBorder(8));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(31, 97, 141));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(41, 128, 185));
            }
        });

        return button;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new RoundedBorder(12),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        int row = 0;

        // Form title
        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2;
        JLabel formTitle = new JLabel("📝 THÔNG TIN HÓA ĐƠN");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        formTitle.setForeground(new Color(52, 152, 219));
        formTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(formTitle, gbc);

        gbc.gridwidth = 1;

        // Tháng selection
        addFormField(panel, gbc, row++, "📅 Tháng:", createMonthComboBox());

        // Hợp đồng selection
        cboHopDong = new JComboBox<>();
        cboHopDong.setRenderer(new ContractRenderer());
        cboHopDong.addActionListener(e -> loadContractInfo());
        addFormField(panel, gbc, row++, "📋 Hợp đồng:", cboHopDong);

        // Thông tin tiền
        lblTienPhong = createInfoLabel("0 VNĐ");
        addFormField(panel, gbc, row++, "🏠 Tiền phòng:", lblTienPhong);

        lblTienDichVu = createInfoLabel("0 VNĐ");
        addFormField(panel, gbc, row++, "🔧 Tiền dịch vụ:", lblTienDichVu);

        // Chỉ số điện
        txtOldE = createNumberField();
        txtOldE.addKeyListener(createCalculationKeyListener());
        addFormField(panel, gbc, row++, "⚡ Chỉ số điện cũ:", txtOldE);

        txtNewE = createNumberField();
        txtNewE.addKeyListener(createCalculationKeyListener());
        addFormField(panel, gbc, row++, "⚡ Chỉ số điện mới:", txtNewE);

        // Thông tin tính toán
        lblSoDienTieuThu = createCalculationLabel("0 kWh");
        addFormField(panel, gbc, row++, "📊 Điện tiêu thụ:", lblSoDienTieuThu);

        lblTienDien = createCalculationLabel("0 VNĐ");
        addFormField(panel, gbc, row++, "💡 Tiền điện:", lblTienDien);

        // Tiền nước
        txtTienNuoc = createCurrencyField("0");
        txtTienNuoc.addKeyListener(createCalculationKeyListener());
        addFormField(panel, gbc, row++, "💧 Tiền nước:", txtTienNuoc);

        // Tổng tiền
        txtTongTien = createTotalField();
        addFormField(panel, gbc, row++, "💰 TỔNG TIỀN:", txtTongTien);

        // Ghi chú
        txtGhiChu = new JTextField();
        txtGhiChu.setToolTipText("Nhập ghi chú cho hóa đơn");
        addFormField(panel, gbc, row++, "📝 Ghi chú:", txtGhiChu);

        // Action buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(createButtonPanel(), gbc);

        return panel;
    }

    private JComboBox<String> createMonthComboBox() {
        String[] months = new String[12];
        for (int i = 1; i <= 12; i++) {
            months[i-1] = String.format("%02d/2024", i);
        }
        cboThang = new JComboBox<>(months);
        cboThang.setSelectedIndex(getCurrentMonth() - 1);
        return cboThang;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent component) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblField = new JLabel(label);
        lblField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblField.setForeground(new Color(70, 70, 70));
        panel.add(lblField, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(component, gbc);
        gbc.weightx = 0;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(52, 152, 219));
        label.setOpaque(true);
        label.setBackground(new Color(235, 245, 255));
        label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return label;
    }

    private JLabel createCalculationLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(230, 126, 34));
        label.setOpaque(true);
        label.setBackground(new Color(255, 248, 220));
        label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return label;
    }

    private JTextField createNumberField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new CompoundBorder(
                new RoundedBorder(6),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JTextField createCurrencyField(String defaultValue) {
        JTextField field = createNumberField();
        field.setText(defaultValue);
        return field;
    }

    private JTextField createTotalField() {
        JTextField field = new JTextField();
        field.setEditable(false);
        field.setFont(new Font("Segoe UI", Font.BOLD, 16));
        field.setForeground(new Color(231, 76, 60));
        field.setBackground(new Color(255, 235, 235));
        field.setBorder(new CompoundBorder(
                new RoundedBorder(8),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        return field;
    }

    private KeyListener createCalculationKeyListener() {
        return new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                calculateElectricity();
                calculateTotal();
            }
        };
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);

        btnThemHoaDon = createActionButton("Thêm HĐ", new Color(46, 204, 113));
        btnCapNhat = createActionButton("Cập nhật", new Color(241, 196, 15));
        btnXoa = createActionButton("Xóa", new Color(231, 76, 60));
        btnLamMoi = createActionButton("Làm mới", new Color(149, 165, 166));
        btnTinhTien = createActionButton("Tính tiền", new Color(52, 152, 219));

        // Event listeners
        btnThemHoaDon.addActionListener(e -> themHoaDon());
        btnCapNhat.addActionListener(e -> capNhatHoaDon());
        btnXoa.addActionListener(e -> xoaHoaDon());
        btnLamMoi.addActionListener(e -> lamMoiForm());
        btnTinhTien.addActionListener(e -> calculateTotal());

        panel.add(btnThemHoaDon);
        panel.add(btnCapNhat);
        panel.add(btnXoa);
        panel.add(btnLamMoi);
        panel.add(btnTinhTien);

        return panel;
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(8));
        button.setPreferredSize(new Dimension(100, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        Color originalColor = bgColor;
        Color hoverColor = bgColor.darker();

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new RoundedBorder(12),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Search panel
        JPanel searchPanel = createSearchPanel();
        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        createTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new RoundedBorder(8));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel searchLabel = new JLabel("🔍 DANH SÁCH HÓA ĐƠN");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        searchLabel.setForeground(new Color(52, 152, 219));

        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchInputPanel.setOpaque(false);

        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimKiem.setBorder(new CompoundBorder(
                new RoundedBorder(6),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        txtTimKiem.setToolTipText("Tìm kiếm theo tên khách hàng, phòng...");

        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(new Color(52, 152, 219));
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setBorder(new RoundedBorder(6));
        btnTimKiem.setFocusPainted(false);
        btnTimKiem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnTimKiem.addActionListener(e -> timKiemHoaDon());

        searchInputPanel.add(new JLabel("Tìm kiếm:"));
        searchInputPanel.add(txtTimKiem);
        searchInputPanel.add(btnTimKiem);

        panel.add(searchLabel, BorderLayout.WEST);
        panel.add(searchInputPanel, BorderLayout.EAST);

        return panel;
    }

    private void createTable() {
        String[] columns = {"ID", "Phòng", "Khách hàng", "Tháng", "Chỉ số cũ", "Chỉ số mới",
                "Điện (kWh)", "Tiền điện", "Tiền nước", "Tổng tiền"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                return String.class;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 245, 253));
        table.setSelectionForeground(new Color(52, 152, 219));
        table.setGridColor(new Color(230, 230, 230));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Custom header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(52, 152, 219));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));

        // Column widths
        int[] columnWidths = {50, 80, 150, 80, 80, 80, 80, 100, 100, 120};
        for (int i = 0; i < columnWidths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedBill();
            }
        });
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel statusLabel = new JLabel("Sẵn sàng");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(127, 140, 141));

        JLabel recordCountLabel = new JLabel("Tổng: 0 hóa đơn");
        recordCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        recordCountLabel.setForeground(new Color(127, 140, 141));

        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(recordCountLabel, BorderLayout.EAST);

        return panel;
    }

    // Calculation methods
    private void calculateElectricity() {
        try {
            String oldEStr = txtOldE.getText().trim();
            String newEStr = txtNewE.getText().trim();

            if (!oldEStr.isEmpty() && !newEStr.isEmpty()) {
                int oldE = Integer.parseInt(oldEStr);
                int newE = Integer.parseInt(newEStr);
                int electricityUsed = Math.max(0, newE - oldE);

                lblSoDienTieuThu.setText(electricityUsed + " kWh");

                // Tính tiền điện (3500 VNĐ/kWh)
                BigDecimal electricityAmount = new BigDecimal(electricityUsed * 3500);
                lblTienDien.setText(currencyFormat.format(electricityAmount) + " VNĐ");
            } else {
                lblSoDienTieuThu.setText("0 kWh");
                lblTienDien.setText("0 VNĐ");
            }
        } catch (NumberFormatException e) {
            lblSoDienTieuThu.setText("0 kWh");
            lblTienDien.setText("0 VNĐ");
        }
    }

    private void calculateTotal() {
        try {
            ContractItem selected = (ContractItem) cboHopDong.getSelectedItem();
            if (selected == null) return;

            // Tiền điện
            BigDecimal electricityAmount = BigDecimal.ZERO;
            String oldEStr = txtOldE.getText().trim();
            String newEStr = txtNewE.getText().trim();

            if (!oldEStr.isEmpty() && !newEStr.isEmpty()) {
                int oldE = Integer.parseInt(oldEStr);
                int newE = Integer.parseInt(newEStr);
                int electricityUsed = Math.max(0, newE - oldE);
                electricityAmount = new BigDecimal(electricityUsed * 3500);
            }

            // Tiền nước
            BigDecimal waterAmount = new BigDecimal(
                    txtTienNuoc.getText().trim().isEmpty() ? "0" : txtTienNuoc.getText().trim()
            );

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
            txtTongTien.setText(currencyFormat.format(total) + " VNĐ");
        } catch (Exception e) {
            txtTongTien.setText("0 VNĐ");
        }
    }

    // Action methods
    private void tinhToanNhanh() {
        JOptionPane.showMessageDialog(this, "Tính toán nhanh - Chức năng đang phát triển",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xemBaoCao() {
        JOptionPane.showMessageDialog(this, "Xem báo cáo - Chức năng đang phát triển",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuatFile() {
        JOptionPane.showMessageDialog(this, "Xuất file - Chức năng đang phát triển",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void timKiemHoaDon() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            loadBills();
            return;
        }

        // Tìm kiếm trong dữ liệu hiện có của bảng
        tableModel.setRowCount(0);
        try {
            List<Bill> allBills = tinhTienController.getAllBills();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");

            for (Bill bill : allBills) {
                // Tìm kiếm theo tên khách hàng hoặc tên phòng
                boolean match = false;
                if (bill.getCustomerName() != null &&
                        bill.getCustomerName().toLowerCase().contains(keyword.toLowerCase())) {
                    match = true;
                }
                if (bill.getRoomName() != null &&
                        bill.getRoomName().toLowerCase().contains(keyword.toLowerCase())) {
                    match = true;
                }

                if (match) {
                    Object[] row = {
                            bill.getBillId(),
                            bill.getRoomName(),
                            bill.getCustomerName(),
                            bill.getMonth() != null ? sdf.format(bill.getMonth()) : "",
                            bill.getOldE() != null ? bill.getOldE() : 0,
                            bill.getNewE() != null ? bill.getNewE() : 0,
                            calculateElectricityConsumption(bill),
                            formatElectricityPrice(bill),
                            bill.getWater() != null ? currencyFormat.format(bill.getWater()) + " VNĐ" : "0 VNĐ",
                            bill.getTotal() != null ? currencyFormat.format(bill.getTotal()) + " VNĐ" : "0 VNĐ"
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            showError("Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    // Data loading methods
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

            if (cboHopDong.getItemCount() > 0) {
                cboHopDong.setSelectedIndex(0);
                loadContractInfo();
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
                        calculateElectricityConsumption(bill),
                        formatElectricityPrice(bill),
                        bill.getWater() != null ? currencyFormat.format(bill.getWater()) + " VNĐ" : "0 VNĐ",
                        bill.getTotal() != null ? currencyFormat.format(bill.getTotal()) + " VNĐ" : "0 VNĐ"
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách hóa đơn: " + e.getMessage());
        }
    }

    private String calculateElectricityConsumption(Bill bill) {
        if (bill.getOldE() != null && bill.getNewE() != null) {
            int consumption = Math.max(0, bill.getNewE() - bill.getOldE());
            return String.valueOf(consumption);
        }
        return "0";
    }

    private String formatElectricityPrice(Bill bill) {
        if (bill.getElectricity() != null) {
            return currencyFormat.format(bill.getElectricity()) + " VNĐ";
        }
        return "0 VNĐ";
    }

    private void loadContractInfo() {
        ContractItem selected = (ContractItem) cboHopDong.getSelectedItem();
        if (selected != null) {
            TinhTien.Contract contract = selected.contract;
            lblTienPhong.setText(currencyFormat.format(contract.getRoomPrice()) + " VNĐ");

            try {
                BigDecimal serviceTotal = tinhTienController.getTotalServicePrice(contract.getContractId());
                lblTienDichVu.setText(currencyFormat.format(serviceTotal) + " VNĐ");
            } catch (Exception e) {
                lblTienDichVu.setText("0 VNĐ");
            }

            calculateTotal();
        }
    }

    // CRUD methods
    private void themHoaDon() {
        try {
            ContractItem selected = (ContractItem) cboHopDong.getSelectedItem();
            if (selected == null) {
                showError("Vui lòng chọn hợp đồng!");
                return;
            }

            String monthStr = (String) cboThang.getSelectedItem();
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
                electricityAmount = new BigDecimal(electricityUsed * 3500);
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
            showInfo("✅ Thêm hóa đơn thành công!");
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
            showInfo("✅ Cập nhật hóa đơn thành công!");
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
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                tinhTienController.deleteBill(selectedBillId);
                showInfo("✅ Xóa hóa đơn thành công!");
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
                    txtTongTien.setText(bill.getTotal() != null ? currencyFormat.format(bill.getTotal()) + " VNĐ" : "0 VNĐ");

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
        txtGhiChu.setText("");
        lblSoDienTieuThu.setText("0 kWh");
        lblTienDien.setText("0 VNĐ");
        table.clearSelection();
        txtTimKiem.setText("");
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

    // Custom renderers and components
    private class ContractItem {
        TinhTien.Contract contract;

        ContractItem(TinhTien.Contract contract) {
            this.contract = contract;
        }

        @Override
        public String toString() {
            return String.format("Phòng %s - %s", contract.getRoomName(), contract.getCustomerName());
        }
    }

    private class ContractRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof ContractItem) {
                ContractItem item = (ContractItem) value;
                setText(String.format("🏠 %s - 👤 %s",
                        item.contract.getRoomName(),
                        item.contract.getCustomerName()));
            }

            return this;
        }
    }

    // Custom border class
    private class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
    }
}