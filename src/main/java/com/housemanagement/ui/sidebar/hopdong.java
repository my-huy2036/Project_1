package com.housemanagement.ui.sidebar;

import com.housemanagement.controller.QLyHopDong;
import com.housemanagement.controller.TaoHopDong;
import com.housemanagement.controller.XoaHopDong;
import com.housemanagement.model.Contract;
import com.housemanagement.model.Customer;
import com.housemanagement.model.Room;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class hopdong extends JPanel {
    private QLyHopDong qlyHopDongController;
    private TaoHopDong taoHopDongController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnThemHopDong, btnTaoHopDong, btnSuaHopDong, btnXoaHopDong, btnKetThuc, btnHuy, btnLuu;
    private JPanel formPanel, buttonPanel, mainContentPanel, tablePanel;
    private CardLayout cardLayout;
    private boolean isAddMode = false;
    private boolean isCreateMode = false;
    private int selectedContractId = -1;

    // Form components
    private JComboBox<Customer> cboKhachHang;
    private JComboBox<Room> cboPhong;
    private JTextField txtNgayBatDau, txtNgayKetThuc, txtGiaThue, txtTienCoc;
    private JComboBox<String> cboTrangThai;

    // Search components
    private JTextField txtSearch;
    private Timer searchTimer;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    // Modern Color Palette
    private static final Color PRIMARY_COLOR = new Color(74, 144, 226);
    private static final Color PRIMARY_DARK = new Color(54, 124, 206);
    private static final Color PRIMARY_LIGHT = new Color(94, 164, 246);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color WARNING_COLOR = new Color(251, 146, 60);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color INFO_COLOR = new Color(59, 130, 246);

    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    // Modern Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);

    public hopdong() {
        qlyHopDongController = new QLyHopDong();
        taoHopDongController = new TaoHopDong();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Create header
        JPanel headerPanel = createModernHeader();
        add(headerPanel, BorderLayout.NORTH);

        // Create main content with CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(BACKGROUND_COLOR);

        // Create table view
        tablePanel = createTableView();
        mainContentPanel.add(tablePanel, "TABLE_VIEW");

        // Create form view
        formPanel = createFormView();
        mainContentPanel.add(formPanel, "FORM_VIEW");

        add(mainContentPanel, BorderLayout.CENTER);

        // Show table view by default
        cardLayout.show(mainContentPanel, "TABLE_VIEW");
    }

    private JPanel createModernHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_COLOR);

        // Top header with white background
        JPanel topHeader = new JPanel();
        topHeader.setBackground(Color.WHITE);
        topHeader.setLayout(new BorderLayout());
        topHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        topHeader.setPreferredSize(new Dimension(0, 80));

        // Title section
        JPanel titleSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleSection.setOpaque(false);

        JLabel titleLabel = new JLabel("Quản Lý Hợp Đồng");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleSection.add(titleLabel);

        // Actions section
        JPanel actionsSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsSection.setOpaque(false);

        // Create action buttons (removed "Tạo Mới")
        btnThemHopDong = createHeaderButton("Thêm Hợp Đồng", "➕");
        btnSuaHopDong = createHeaderButton("Sửa", "✏️");
        btnXoaHopDong = createHeaderButton("Xóa", "🗑️");
        btnKetThuc = createHeaderButton("Kết Thúc", "⏹️");

        // Form buttons
        btnLuu = createHeaderButton("Lưu", "💾");
        btnHuy = createHeaderButton("Hủy", "❌");
        btnLuu.setVisible(false);
        btnHuy.setVisible(false);

        actionsSection.add(btnThemHopDong);
        actionsSection.add(btnSuaHopDong);
        actionsSection.add(btnXoaHopDong);
        actionsSection.add(btnKetThuc);
        actionsSection.add(btnLuu);
        actionsSection.add(btnHuy);

        topHeader.add(titleSection, BorderLayout.WEST);
        topHeader.add(actionsSection, BorderLayout.EAST);

        headerPanel.add(topHeader);

        setupButtonEvents();
        return headerPanel;
    }

    private JButton createHeaderButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(PRIMARY_DARK);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                } else if (getModel().isRollover()) {
                    g2d.setColor(BACKGROUND_COLOR);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }

                super.paintComponent(g);
            }
        };

        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JPanel createTableView() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Search section
        JPanel searchSection = createSearchSection();
        panel.add(searchSection, BorderLayout.NORTH);

        // Table section
        JPanel tableSection = createTableSection();
        panel.add(tableSection, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSearchSection() {
        JPanel searchCard = new JPanel(new BorderLayout());
        searchCard.setBackground(CARD_COLOR);
        searchCard.setBorder(new CompoundBorder(
                new RoundedBorder(12, BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Left side - Search input
        JPanel searchPanel = new JPanel(new BorderLayout(15, 0));
        searchPanel.setBackground(CARD_COLOR);

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Arial", Font.PLAIN, 20));
        searchPanel.add(searchIcon, BorderLayout.WEST);

        txtSearch = new JTextField();
        txtSearch.setFont(TEXT_FONT);
        txtSearch.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtSearch.setBackground(BACKGROUND_COLOR);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm kiếm theo tên khách hàng, phòng, mã hợp đồng...");

        // Real-time search
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (searchTimer != null) {
                    searchTimer.stop();
                }
                searchTimer = new Timer(300, evt -> performSearch());
                searchTimer.setRepeats(false);
                searchTimer.start();
            }
        });

        searchPanel.add(txtSearch, BorderLayout.CENTER);

        // Right side - Filter dropdown
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(CARD_COLOR);

        JLabel filterLabel = new JLabel("Lọc theo:");
        filterLabel.setFont(LABEL_FONT);
        filterLabel.setForeground(TEXT_SECONDARY);
        filterPanel.add(filterLabel);

        String[] filterOptions = {"Tất cả", "Đang hoạt động", "Chờ xử lý", "Đã hết hạn", "Đã kết thúc"};
        JComboBox<String> cboFilter = createStyledComboBox();
        cboFilter.setModel(new DefaultComboBoxModel<>(filterOptions));
        cboFilter.setPreferredSize(new Dimension(160, 35));
        cboFilter.addActionListener(e -> filterByStatus((String) cboFilter.getSelectedItem()));

        filterPanel.add(cboFilter);

        searchCard.add(searchPanel, BorderLayout.CENTER);
        searchCard.add(filterPanel, BorderLayout.EAST);

        return searchCard;
    }

    private void filterByStatus(String status) {
        String searchText = txtSearch.getText().trim().toLowerCase();

        try {
            List<Contract> allContracts = qlyHopDongController.getAllContracts();
            tableModel.setRowCount(0);

            for (Contract contract : allContracts) {
                String contractStatus = getStatusInVietnamese(contract.getStatus());

                // Check status filter
                boolean statusMatch = status.equals("Tất cả") || contractStatus.equals(status);

                // Check search text
                boolean searchMatch = searchText.isEmpty() ||
                        contract.getCustomerName().toLowerCase().contains(searchText) ||
                        contract.getRoomName().toLowerCase().contains(searchText) ||
                        String.valueOf(contract.getContractId()).contains(searchText);

                if (statusMatch && searchMatch) {
                    Object[] row = {
                            contract.getContractId(),
                            contract.getCustomerName(),
                            contract.getRoomName(),
                            contract.getStartDate() != null ? dateFormat.format(contract.getStartDate()) : "",
                            contract.getEndDate() != null ? dateFormat.format(contract.getEndDate()) : "",
                            String.format("%,.0f", contract.getDeposit()),
                            contractStatus,
                            "" // No note column
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            showError("Lỗi khi lọc dữ liệu: " + e.getMessage());
        }
    }

    private JPanel createTableSection() {
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD_COLOR);
        tableCard.setBorder(new RoundedBorder(12, BORDER_COLOR));

        // Create modern table
        String[] columns = {"ID", "Khách Hàng", "Phòng", "Ngày BĐ", "Ngày KT", "Tiền Cọc", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                }
                return comp;
            }
        };

        table.setFont(TEXT_FONT);
        table.setRowHeight(50);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TEXT_PRIMARY);

        // Modern header
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new ModernHeaderRenderer());
        header.setPreferredSize(new Dimension(0, 45));
        header.setReorderingAllowed(false);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(180); // Khách hàng
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Phòng
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Ngày BĐ
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Ngày KT
        table.getColumnModel().getColumn(5).setPreferredWidth(120); // Tiền cọc
        table.getColumnModel().getColumn(6).setPreferredWidth(130); // Trạng thái

        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Custom renderers
        table.getColumnModel().getColumn(6).setCellRenderer(new ModernStatusRenderer());
        table.setDefaultRenderer(Object.class, new ModernCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Remove vertical lines
        table.setShowVerticalLines(false);

        // Add selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedContractId = (Integer) table.getValueAt(selectedRow, 0);
                    updateButtonStates();
                }
            }
        });

        tableCard.add(scrollPane, BorderLayout.CENTER);

        return tableCard;
    }

    private JPanel createFormView() {
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBackground(BACKGROUND_COLOR);
        formContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form card
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(CARD_COLOR);
        formCard.setBorder(new RoundedBorder(12, BORDER_COLOR));

        // Form header
        JPanel formHeader = new JPanel(new BorderLayout());
        formHeader.setBackground(BACKGROUND_COLOR);
        formHeader.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel formTitle = new JLabel("Thông Tin Hợp Đồng");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        formTitle.setForeground(TEXT_PRIMARY);
        formHeader.add(formTitle, BorderLayout.WEST);

        // Form content
        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setBackground(CARD_COLOR);
        formContent.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Row 1: Customer and Room
        gbc.gridx = 0; gbc.gridy = 0;
        formContent.add(createLabel("Khách Hàng"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        cboKhachHang = createStyledComboBox();
        cboKhachHang.setPreferredSize(new Dimension(300, 40));
        formContent.add(cboKhachHang, gbc);

        gbc.gridx = 3; gbc.gridwidth = 1;
        formContent.add(createLabel("Phòng"), gbc);

        gbc.gridx = 4; gbc.gridwidth = 2;
        cboPhong = createStyledComboBox();
        cboPhong.setPreferredSize(new Dimension(250, 40));
        cboPhong.addActionListener(e -> updateRentFromRoom());
        formContent.add(cboPhong, gbc);

        // Row 2: Dates
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formContent.add(createLabel("Ngày Bắt Đầu"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        txtNgayBatDau = createStyledTextField();
        txtNgayBatDau.setText(dateFormat.format(new Date()));
        formContent.add(txtNgayBatDau, gbc);

        gbc.gridx = 3; gbc.gridwidth = 1;
        formContent.add(createLabel("Ngày Kết Thúc"), gbc);

        gbc.gridx = 4; gbc.gridwidth = 2;
        txtNgayKetThuc = createStyledTextField();
        formContent.add(txtNgayKetThuc, gbc);

        // Row 3: Money and Status
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formContent.add(createLabel("Giá Thuê"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        txtGiaThue = createStyledTextField();
        txtGiaThue.setEditable(false);
        txtGiaThue.setBackground(BACKGROUND_COLOR);
        formContent.add(txtGiaThue, gbc);

        gbc.gridx = 3; gbc.gridwidth = 1;
        formContent.add(createLabel("Tiền Cọc"), gbc);

        gbc.gridx = 4; gbc.gridwidth = 2;
        txtTienCoc = createStyledTextField();
        formContent.add(txtTienCoc, gbc);

        // Row 4: Status
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formContent.add(createLabel("Trạng Thái"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        cboTrangThai = createStyledComboBox();
        cboTrangThai.setModel(new DefaultComboBoxModel<>(new String[]{"active", "pending", "expired", "terminated"}));
        formContent.add(cboTrangThai, gbc);

        formCard.add(formHeader, BorderLayout.NORTH);
        formCard.add(formContent, BorderLayout.CENTER);

        formContainer.add(formCard, BorderLayout.CENTER);

        return formContainer;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(TEXT_FONT);
        field.setBorder(new CompoundBorder(
                new RoundedBorder(8, BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(200, 40));
        return field;
    }

    private <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setFont(TEXT_FONT);
        combo.setBackground(Color.WHITE);
        combo.setBorder(new RoundedBorder(8, BORDER_COLOR));
        combo.setPreferredSize(new Dimension(200, 40));
        return combo;
    }

    // Custom table renderers
    private class ModernHeaderRenderer extends DefaultTableCellRenderer {
        public ModernHeaderRenderer() {
            setHorizontalAlignment(JLabel.LEFT);
            setFont(LABEL_FONT);
            setForeground(TEXT_SECONDARY);
            setBackground(BACKGROUND_COLOR);
            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        }
    }

    private class ModernCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setFont(TEXT_FONT);
            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

            if (isSelected) {
                setBackground(new Color(239, 246, 255));
                setForeground(TEXT_PRIMARY);
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                setForeground(TEXT_PRIMARY);
            }

            return this;
        }
    }

    private class ModernStatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setHorizontalAlignment(CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            if (value != null) {
                String status = value.toString();

                // Create pill-style status
                JPanel statusPanel = new JPanel();
                statusPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
                statusPanel.setOpaque(false);

                JLabel statusLabel = new JLabel(status);
                statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
                statusLabel.setOpaque(true);

                switch (status) {
                    case "Đang hoạt động":
                        statusLabel.setBackground(new Color(220, 252, 231));
                        statusLabel.setForeground(new Color(22, 101, 52));
                        break;
                    case "Chờ xử lý":
                        statusLabel.setBackground(new Color(254, 243, 199));
                        statusLabel.setForeground(new Color(146, 64, 14));
                        break;
                    case "Đã hết hạn":
                        statusLabel.setBackground(new Color(254, 226, 226));
                        statusLabel.setForeground(new Color(153, 27, 27));
                        break;
                    case "Đã kết thúc":
                        statusLabel.setBackground(new Color(243, 244, 246));
                        statusLabel.setForeground(new Color(55, 65, 81));
                        break;
                }

                return statusLabel;
            }

            return this;
        }
    }

    // Custom rounded border
    private class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }
    }

    // Button events and other methods remain the same...
    private void setupButtonEvents() {
        btnThemHopDong.addActionListener(e -> showAddForm(false));
        btnSuaHopDong.addActionListener(e -> editContract());
        btnXoaHopDong.addActionListener(e -> deleteContract());
        btnKetThuc.addActionListener(e -> terminateContract());
        btnLuu.addActionListener(e -> saveContract());
        btnHuy.addActionListener(e -> cancelForm());
    }

    private void showAddForm(boolean isCreate) {
        this.isCreateMode = isCreate;
        this.isAddMode = true;
        this.selectedContractId = -1;

        clearForm();
        loadCustomers();
        loadRooms();

        cardLayout.show(mainContentPanel, "FORM_VIEW");
        showFormButtons();

        if (isCreate) {
            cboTrangThai.setSelectedItem("pending");
        }
    }

    private void editContract() {
        if (selectedContractId == -1) {
            showError("Vui lòng chọn hợp đồng cần sửa!");
            return;
        }

        try {
            Contract contract = qlyHopDongController.getContractById(selectedContractId);
            if (contract == null) {
                showError("Không tìm thấy hợp đồng!");
                return;
            }

            this.isAddMode = false;
            this.isCreateMode = false;

            loadCustomers();
            loadRooms();
            populateForm(contract);

            cardLayout.show(mainContentPanel, "FORM_VIEW");
            showFormButtons();

        } catch (Exception e) {
            showError("Lỗi khi tải thông tin hợp đồng: " + e.getMessage());
        }
    }

    private void deleteContract() {
        if (selectedContractId == -1) {
            showError("Vui lòng chọn hợp đồng cần xóa!");
            return;
        }

        boolean deleted = XoaHopDong.showDeleteDialog((Frame) SwingUtilities.getWindowAncestor(this), selectedContractId);
        if (deleted) {
            loadData();
            selectedContractId = -1;
            updateButtonStates();
        }
    }

    private void terminateContract() {
        if (selectedContractId == -1) {
            showError("Vui lòng chọn hợp đồng cần kết thúc!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn kết thúc hợp đồng này?",
                "Xác Nhận",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                qlyHopDongController.terminateContract(selectedContractId, new Date());
                showSuccess("Kết thúc hợp đồng thành công!");
                loadData();
                selectedContractId = -1;
                updateButtonStates();
            } catch (Exception e) {
                showError("Lỗi khi kết thúc hợp đồng: " + e.getMessage());
            }
        }
    }

    private void saveContract() {
        try {
            Contract contract = getContractFromForm();

            if (isAddMode) {
                if (isCreateMode) {
                    taoHopDongController.createContract(contract);
                    showSuccess("Tạo hợp đồng mới thành công!");
                } else {
                    qlyHopDongController.createContract(contract);
                    showSuccess("Thêm hợp đồng thành công!");
                }
            } else {
                contract.setContractId(selectedContractId);
                qlyHopDongController.updateContract(contract);
                showSuccess("Cập nhật hợp đồng thành công!");
            }

            cancelForm();
            loadData();
            selectedContractId = -1;
            updateButtonStates();

        } catch (Exception e) {
            showError("Lỗi khi lưu hợp đồng: " + e.getMessage());
        }
    }

    private void cancelForm() {
        cardLayout.show(mainContentPanel, "TABLE_VIEW");
        hideFormButtons();
        clearForm();
        isAddMode = false;
        isCreateMode = false;
    }

    private Contract getContractFromForm() throws Exception {
        Contract contract = new Contract();

        Customer customer = (Customer) cboKhachHang.getSelectedItem();
        if (customer == null) throw new Exception("Vui lòng chọn khách hàng!");
        contract.setCustomerId(customer.getCustomerId());

        Room room = (Room) cboPhong.getSelectedItem();
        if (room == null) throw new Exception("Vui lòng chọn phòng!");
        contract.setRoomId(room.getRoomId());

        if (txtNgayBatDau.getText().trim().isEmpty()) {
            throw new Exception("Vui lòng nhập ngày bắt đầu!");
        }
        try {
            contract.setStartDate(dateFormat.parse(txtNgayBatDau.getText().trim()));
        } catch (ParseException e) {
            throw new Exception("Định dạng ngày bắt đầu không hợp lệ! (dd/MM/yyyy)");
        }

        if (!txtNgayKetThuc.getText().trim().isEmpty()) {
            try {
                contract.setEndDate(dateFormat.parse(txtNgayKetThuc.getText().trim()));
            } catch (ParseException e) {
                throw new Exception("Định dạng ngày kết thúc không hợp lệ! (dd/MM/yyyy)");
            }
        }

        try {
            String depositText = txtTienCoc.getText().trim();
            if (!depositText.isEmpty()) {
                contract.setDeposit(Double.parseDouble(depositText));
            }
        } catch (NumberFormatException e) {
            throw new Exception("Tiền cọc phải là số!");
        }

        contract.setNote("");
        contract.setStatus((String) cboTrangThai.getSelectedItem());

        return contract;
    }

    private void populateForm(Contract contract) {
        for (int i = 0; i < cboKhachHang.getItemCount(); i++) {
            Customer customer = cboKhachHang.getItemAt(i);
            if (customer.getCustomerId() == contract.getCustomerId()) {
                cboKhachHang.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < cboPhong.getItemCount(); i++) {
            Room room = cboPhong.getItemAt(i);
            if (room.getRoomId() == contract.getRoomId()) {
                cboPhong.setSelectedIndex(i);
                break;
            }
        }

        if (contract.getStartDate() != null) {
            txtNgayBatDau.setText(dateFormat.format(contract.getStartDate()));
        }
        if (contract.getEndDate() != null) {
            txtNgayKetThuc.setText(dateFormat.format(contract.getEndDate()));
        }

        txtGiaThue.setText(String.valueOf(contract.getRoomRent()));
        txtTienCoc.setText(String.valueOf(contract.getDeposit()));
        cboTrangThai.setSelectedItem(contract.getStatus());
    }

    private void clearForm() {
        if (cboKhachHang != null) cboKhachHang.removeAllItems();
        if (cboPhong != null) cboPhong.removeAllItems();
        txtNgayBatDau.setText(dateFormat.format(new Date()));
        txtNgayKetThuc.setText("");
        txtGiaThue.setText("");
        txtTienCoc.setText("");
        cboTrangThai.setSelectedItem("active");
    }

    private void showFormButtons() {
        btnThemHopDong.setVisible(false);
        btnSuaHopDong.setVisible(false);
        btnXoaHopDong.setVisible(false);
        btnKetThuc.setVisible(false);
        btnLuu.setVisible(true);
        btnHuy.setVisible(true);
    }

    private void hideFormButtons() {
        btnThemHopDong.setVisible(true);
        btnSuaHopDong.setVisible(true);
        btnXoaHopDong.setVisible(true);
        btnKetThuc.setVisible(true);
        btnLuu.setVisible(false);
        btnHuy.setVisible(false);
    }

    private void updateButtonStates() {
        boolean hasSelection = selectedContractId != -1;
        btnSuaHopDong.setEnabled(hasSelection);
        btnXoaHopDong.setEnabled(hasSelection);
        btnKetThuc.setEnabled(hasSelection);
    }

    private void performSearch() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadData();
            return;
        }

        try {
            List<Contract> allContracts = qlyHopDongController.getAllContracts();
            tableModel.setRowCount(0);

            for (Contract contract : allContracts) {
                boolean matches = contract.getCustomerName().toLowerCase().contains(searchText) ||
                        contract.getRoomName().toLowerCase().contains(searchText) ||
                        String.valueOf(contract.getContractId()).contains(searchText) ||
                        (contract.getNote() != null && contract.getNote().toLowerCase().contains(searchText));

                if (matches) {
                    Object[] row = {
                            contract.getContractId(),
                            contract.getCustomerName(),
                            contract.getRoomName(),
                            contract.getStartDate() != null ? dateFormat.format(contract.getStartDate()) : "",
                            contract.getEndDate() != null ? dateFormat.format(contract.getEndDate()) : "",
                            String.format("%,.0f", contract.getDeposit()),
                            getStatusInVietnamese(contract.getStatus()),
                            contract.getNote()
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            showError("Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            List<Contract> contracts = qlyHopDongController.getAllContracts();
            tableModel.setRowCount(0);

            for (Contract contract : contracts) {
                Object[] row = {
                        contract.getContractId(),
                        contract.getCustomerName(),
                        contract.getRoomName(),
                        contract.getStartDate() != null ? dateFormat.format(contract.getStartDate()) : "",
                        contract.getEndDate() != null ? dateFormat.format(contract.getEndDate()) : "",
                        String.format("%,.0f", contract.getDeposit()),
                        getStatusInVietnamese(contract.getStatus()),
                        contract.getNote()
                };
                tableModel.addRow(row);
            }

            updateButtonStates();

        } catch (Exception e) {
            showError("Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        try {
            cboKhachHang.removeAllItems();
            List<Customer> customers = isCreateMode ?
                    taoHopDongController.getAllCustomers() : qlyHopDongController.getAllCustomers();
            for (Customer customer : customers) {
                cboKhachHang.addItem(customer);
            }
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách khách hàng: " + e.getMessage());
        }
    }

    private void loadRooms() {
        try {
            cboPhong.removeAllItems();
            List<Room> rooms = isCreateMode ?
                    taoHopDongController.getAvailableRooms() : qlyHopDongController.getAvailableRooms();
            for (Room room : rooms) {
                cboPhong.addItem(room);
            }
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách phòng: " + e.getMessage());
        }
    }

    private void updateRentFromRoom() {
        Room room = (Room) cboPhong.getSelectedItem();
        if (room != null) {
            txtGiaThue.setText(String.valueOf(room.getRent()));
        }
    }

    private String getStatusInVietnamese(String status) {
        if (status == null) return "";
        switch (status.toLowerCase()) {
            case "active": return "Đang hoạt động";
            case "pending": return "Chờ xử lý";
            case "expired": return "Đã hết hạn";
            case "terminated": return "Đã kết thúc";
            default: return status;
        }
    }

    private void showSuccess(String message) {
        JOptionPane optionPane = new JOptionPane(
                message,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{},
                null
        );

        JDialog dialog = optionPane.createDialog(this, "Thành Công");
        dialog.setModal(false);

        // Auto close after 2 seconds
        Timer timer = new Timer(2000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Thông Báo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}