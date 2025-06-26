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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class hopdong extends JPanel {
    private QLyHopDong qlyController;
    private TaoHopDong taoController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnSua, btnXoa, btnTaoHopDong;
    private JPanel formPanel, mainContentPanel, tablePanel;
    private CardLayout cardLayout;
    private boolean isAddMode = false;
    private int selectedContractId = -1;

    // Form components
    private JComboBox<Customer> cboKhachHang;
    private JComboBox<Room> cboPhong;
    private JTextField txtNgayBatDau, txtNgayKetThuc, txtTienCoc;
    private JButton btnLuu, btnHuy, btnTroVe;

    // Search components
    private JTextField txtSearch;
    private Timer searchTimer;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    // Color constants
    private static final Color PRIMARY_COLOR = new Color(74, 144, 226);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color WARNING_COLOR = new Color(251, 146, 60);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    // Font constants
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 12);

    public hopdong() {
        qlyController = new QLyHopDong();
        taoController = new TaoHopDong();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Create main content with CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(BACKGROUND_COLOR);

        // Create views
        tablePanel = createTableView();
        formPanel = createFormView();

        mainContentPanel.add(tablePanel, "TABLE_VIEW");
        mainContentPanel.add(formPanel, "FORM_VIEW");

        add(mainContentPanel, BorderLayout.CENTER);

        // Show table view by default
        cardLayout.show(mainContentPanel, "TABLE_VIEW");
    }

    private JPanel createTableView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = createHeaderPanel();
        panel.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = createContentPanel();
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Title with better styling
        JLabel titleLabel = new JLabel("Quản lý hợp đồng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        // Thêm subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Quản lý và theo dõi các hợp đồng thuê phòng");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        panel.add(titlePanel, BorderLayout.WEST);

        // Button panel with better layout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        // Chỉ giữ lại nút "Tạo hợp đồng"
        btnTaoHopDong = createSpecialButton("✚ Tạo hợp đồng", new Color(59, 130, 246));

        // Các nút sửa và xóa
        btnSua = createButton("Sửa", new Color(245, 158, 11));
        btnXoa = createButton("Xóa", new Color(239, 68, 68));

        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);

        // Layout buttons
        buttonPanel.add(btnTaoHopDong);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(btnSua);
        buttonPanel.add(Box.createHorizontalStrut(8));
        buttonPanel.add(btnXoa);

        panel.add(buttonPanel, BorderLayout.EAST);

        // Setup button events
        setupButtonEvents();

        return panel;
    }

    private JButton createSpecialButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.GRAY);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));

        button.setIcon(null);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setIconTextGap(8);

        button.addMouseListener(new MouseAdapter() {
            private Timer hoverTimer;

            @Override
            public void mouseEntered(MouseEvent e) {
                if (hoverTimer != null) hoverTimer.stop();

                Color targetColor = backgroundColor.brighter();
                button.setBackground(targetColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(targetColor.darker(), 2),
                        BorderFactory.createEmptyBorder(14, 29, 14, 29)
                ));

                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                                BorderFactory.createLineBorder(Color.WHITE, 1)
                        ),
                        BorderFactory.createEmptyBorder(13, 28, 13, 28)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                        BorderFactory.createEmptyBorder(15, 30, 15, 30)
                ));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(backgroundColor.darker().darker());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(backgroundColor.brighter());
            }
        });

        return button;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Search section
        JPanel searchPanel = createSearchPanel();
        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        JPanel tableSection = createTableSection();
        panel.add(tableSection, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Right side - Search only
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(CARD_COLOR);

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(LABEL_FONT);

        txtSearch = new JTextField(15);
        txtSearch.setFont(TEXT_FONT);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

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

        rightPanel.add(lblSearch);
        rightPanel.add(txtSearch);

        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTableSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);

        // Create table với các cột phù hợp với Contract model
        String[] columns = {"STT", "Khách hàng", "Phòng", "Ngày bắt đầu", "Ngày kết thúc", "Giá thuê"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(TEXT_FONT);
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(TEXT_PRIMARY);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(TEXT_PRIMARY);
        header.setFont(LABEL_FONT);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        // Selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.setBackground(CARD_COLOR);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Enhanced header with better styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Back button với style đẹp hơn
        btnTroVe = createButton("← Trở về", new Color(107, 114, 128));
        btnTroVe.setFont(new Font("Arial", Font.BOLD, 13));
        btnTroVe.addActionListener(e -> cancelForm());
        btnTroVe.setToolTipText("Trở về danh sách hợp đồng");

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backPanel.setBackground(BACKGROUND_COLOR);
        backPanel.add(btnTroVe);
        headerPanel.add(backPanel, BorderLayout.WEST);

        // Title section with icon and description
        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("📋 Thông tin hợp đồng", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleSection.add(titleLabel);

        JLabel descLabel = new JLabel("Vui lòng nhập đầy đủ thông tin hợp đồng", JLabel.CENTER);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(new Color(107, 114, 128));
        titleSection.add(Box.createVerticalStrut(5));
        titleSection.add(descLabel);

        headerPanel.add(titleSection, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Form content
        JPanel formContent = createFormContent();
        panel.add(formContent, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        // Form fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(CARD_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Khách hàng
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(createLabel("Khách hàng:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cboKhachHang = createCustomerComboBox();
        fieldsPanel.add(cboKhachHang, gbc);

        // Phòng
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(createLabel("Phòng:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cboPhong = createRoomComboBox();
        fieldsPanel.add(cboPhong, gbc);

        // Ngày bắt đầu
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(createLabel("Ngày bắt đầu:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNgayBatDau = createStyledTextField();
        txtNgayBatDau.setToolTipText("Định dạng: dd/MM/yyyy");
        fieldsPanel.add(txtNgayBatDau, gbc);

        // Ngày kết thúc
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(createLabel("Ngày kết thúc:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNgayKetThuc = createStyledTextField();
        txtNgayKetThuc.setToolTipText("Định dạng: dd/MM/yyyy");
        fieldsPanel.add(txtNgayKetThuc, gbc);

        panel.add(fieldsPanel, BorderLayout.CENTER);

        // Form buttons
        JPanel buttonPanel = createFormButtonPanel();
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(TEXT_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        textField.setPreferredSize(new Dimension(250, 35));
        return textField;
    }

    private JComboBox<Customer> createCustomerComboBox() {
        JComboBox<Customer> comboBox = new JComboBox<>();
        comboBox.setFont(TEXT_FONT);
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        comboBox.setPreferredSize(new Dimension(250, 35));

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Customer) {
                    Customer customer = (Customer) value;
                    setText(customer.getFullName() + " - " + customer.getPhone());
                }
                return this;
            }
        });

        return comboBox;
    }

    private JComboBox<Room> createRoomComboBox() {
        JComboBox<Room> comboBox = new JComboBox<>();
        comboBox.setFont(TEXT_FONT);
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        comboBox.setPreferredSize(new Dimension(250, 35));

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Room) {
                    Room room = (Room) value;
                    setText(room.getRoomName() + " - " + String.format("%,.0f VNĐ", room.getRent()));
                }
                return this;
            }
        });
        return comboBox;
    }

    private JPanel createFormButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        JPanel separatorPanel = new JPanel();
        separatorPanel.setBackground(CARD_COLOR);
        separatorPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        separatorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separatorPanel);
        panel.add(Box.createVerticalStrut(20));

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonContainer.setBackground(CARD_COLOR);

        btnLuu = createButton("💾 Lưu", new Color(34, 197, 94));
        btnHuy = createButton("✕ Hủy", new Color(107, 114, 128));

        btnLuu.setToolTipText("Lưu thông tin hợp đồng");
        btnHuy.setToolTipText("Hủy và trở về danh sách");

        btnLuu.setFont(new Font("Arial", Font.BOLD, 13));
        btnLuu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(34, 197, 94).darker(), 1),
                BorderFactory.createEmptyBorder(14, 28, 14, 28)
        ));

        btnHuy.setFont(new Font("Arial", Font.PLAIN, 13));
        btnHuy.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(107, 114, 128), 1),
                BorderFactory.createEmptyBorder(14, 28, 14, 28)
        ));

        btnLuu.addActionListener(e -> saveContract());
        btnHuy.addActionListener(e -> cancelForm());

        buttonContainer.add(btnLuu);
        buttonContainer.add(btnHuy);

        panel.add(buttonContainer);

        return panel;
    }

    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.GRAY);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(12, 24, 12, 24)
        ));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Color hoverColor = backgroundColor.brighter();
                button.setBackground(hoverColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(hoverColor.darker(), 1),
                        BorderFactory.createEmptyBorder(12, 24, 12, 24)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                        BorderFactory.createEmptyBorder(12, 24, 12, 24)
                ));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.getBounds().contains(e.getPoint())) {
                    button.setBackground(backgroundColor.brighter());
                } else {
                    button.setBackground(backgroundColor);
                }
            }
        });

        return button;
    }

    private void setupButtonEvents() {
        btnTaoHopDong.addActionListener(e -> showAddForm());
        btnSua.addActionListener(e -> editContract());
        btnXoa.addActionListener(e -> deleteContract());
    }

    private void showAddForm() {
        isAddMode = true;
        selectedContractId = -1;
        clearForm();

        SwingUtilities.invokeLater(() -> {
            loadCustomers();
            loadRooms();
        });

        cardLayout.show(mainContentPanel, "FORM_VIEW");
    }

    private void editContract() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Vui lòng chọn hợp đồng cần sửa!");
            return;
        }

        try {
            String customerName = (String) table.getValueAt(selectedRow, 1);
            String roomName = (String) table.getValueAt(selectedRow, 2);

            List<Contract> contracts = qlyController.getAllContracts();
            Contract contract = null;
            for (Contract c : contracts) {
                if (c.getCustomerName().equals(customerName) && c.getRoomName().equals(roomName)) {
                    contract = c;
                    break;
                }
            }

            if (contract != null) {
                selectedContractId = contract.getContractId();
                isAddMode = false;
                loadCustomers();
                loadRooms();
                populateForm(contract);
                cardLayout.show(mainContentPanel, "FORM_VIEW");
            }
        } catch (Exception e) {
            showError("Lỗi khi tải thông tin hợp đồng: " + e.getMessage());
        }
    }

    private void deleteContract() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Vui lòng chọn hợp đồng cần xóa!");
            return;
        }

        try {
            String customerName = (String) table.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn xóa hợp đồng của " + customerName + "?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                String roomName = (String) table.getValueAt(selectedRow, 2);
                List<Contract> contracts = qlyController.getAllContracts();

                for (Contract c : contracts) {
                    if (c.getCustomerName().equals(customerName) && c.getRoomName().equals(roomName)) {
                        boolean deleted = XoaHopDong.showDeleteDialog(
                                (Frame) SwingUtilities.getWindowAncestor(this), c.getContractId());
                        if (deleted) {
                            showSuccess("Xóa hợp đồng thành công!");
                            loadData();
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            showError("Lỗi khi xóa hợp đồng: " + e.getMessage());
        }
    }

    private void saveContract() {
        try {
            Contract contract = getContractFromForm();

            if (isAddMode) {
                qlyController.createContract(contract);
                showSuccess("Thêm hợp đồng thành công!");
            } else {
                contract.setContractId(selectedContractId);
                qlyController.updateContract(contract);
                showSuccess("Cập nhật hợp đồng thành công!");
            }

            cancelForm();
            loadData();

        } catch (Exception e) {
            showError("Lỗi khi lưu hợp đồng: " + e.getMessage());
        }
    }

    private void cancelForm() {
        cardLayout.show(mainContentPanel, "TABLE_VIEW");
        clearForm();
        isAddMode = false;
        selectedContractId = -1;
    }

    private Contract getContractFromForm() throws Exception {
        Contract contract = new Contract();

        // Validate customer
        Customer customer = (Customer) cboKhachHang.getSelectedItem();
        if (customer == null) {
            throw new Exception("Vui lòng chọn khách hàng!");
        }
        contract.setCustomerId(customer.getCustomerId());

        // Validate room
        Room room = (Room) cboPhong.getSelectedItem();
        if (room == null) {
            throw new Exception("Vui lòng chọn phòng!");
        }
        contract.setRoomId(room.getRoomId());

        // Validate and parse start date
        if (txtNgayBatDau.getText().trim().isEmpty()) {
            throw new Exception("Vui lòng nhập ngày bắt đầu!");
        }
        try {
            contract.setStartDate(dateFormat.parse(txtNgayBatDau.getText().trim()));
        } catch (ParseException e) {
            throw new Exception("Định dạng ngày bắt đầu không hợp lệ! (dd/MM/yyyy)");
        }

        // Validate and parse end date
        if (txtNgayKetThuc.getText().trim().isEmpty()) {
            throw new Exception("Vui lòng nhập ngày kết thúc!");
        }
        try {
            contract.setEndDate(dateFormat.parse(txtNgayKetThuc.getText().trim()));
        } catch (ParseException e) {
            throw new Exception("Định dạng ngày kết thúc không hợp lệ! (dd/MM/yyyy)");
        }

        // Validate date logic
        if (contract.getEndDate().before(contract.getStartDate())) {
            throw new Exception("Ngày kết thúc phải sau ngày bắt đầu!");
        }
        return contract;
    }

    private void populateForm(Contract contract) {
        // Set customer
        for (int i = 0; i < cboKhachHang.getItemCount(); i++) {
            if (cboKhachHang.getItemAt(i).getCustomerId() == contract.getCustomerId()) {
                cboKhachHang.setSelectedIndex(i);
                break;
            }
        }

        // Set room
        for (int i = 0; i < cboPhong.getItemCount(); i++) {
            if (cboPhong.getItemAt(i).getRoomId() == contract.getRoomId()) {
                cboPhong.setSelectedIndex(i);
                break;
            }
        }

        // Set dates
        if (contract.getStartDate() != null) {
            txtNgayBatDau.setText(dateFormat.format(contract.getStartDate()));
        }
        if (contract.getEndDate() != null) {
            txtNgayKetThuc.setText(dateFormat.format(contract.getEndDate()));
        }
    }

    private void clearForm() {
        if (cboKhachHang != null) cboKhachHang.setSelectedIndex(-1);
        if (cboPhong != null) cboPhong.setSelectedIndex(-1);
        if (txtNgayBatDau != null) txtNgayBatDau.setText("");
        if (txtNgayKetThuc != null) txtNgayKetThuc.setText("");
        if (txtTienCoc != null) txtTienCoc.setText("");
    }

    private void updateButtonStates() {
        boolean hasSelection = table.getSelectedRow() != -1;
        btnSua.setEnabled(hasSelection);
        btnXoa.setEnabled(hasSelection);
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        if (keyword.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword));
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            List<Contract> contracts = qlyController.getAllContracts();
            int stt = 1;

            for (Contract contract : contracts) {
                Object[] row = {
                        stt++,
                        contract.getCustomerName(),
                        contract.getRoomName(),
                        contract.getStartDate() != null ? dateFormat.format(contract.getStartDate()) : "",
                        contract.getEndDate() != null ? dateFormat.format(contract.getEndDate()) : "",
                        String.format("%,.0f VNĐ", contract.getRoomRent()),
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            showError("Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        if (cboKhachHang != null) {
            cboKhachHang.removeAllItems();
            try {
                List<Customer> customers = qlyController.getAllCustomers();
                for (Customer customer : customers) {
                    cboKhachHang.addItem(customer);
                }
                System.out.println("Loaded " + customers.size() + " customers");
            } catch (Exception e) {
                showError("Lỗi khi tải danh sách khách hàng: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadRooms() {
        if (cboPhong != null) {
            cboPhong.removeAllItems();
            try {
                List<Room> rooms;
                if (isAddMode) {
                    // Khi thêm hợp đồng mới, chỉ hiển thị phòng trống
                    rooms = qlyController.getAvailableRooms();
                } else {
                    // Khi sửa hợp đồng, hiển thị tất cả phòng
                    rooms = qlyController.getAllRooms();
                }

                for (Room room : rooms) {
                    cboPhong.addItem(room);
                }
                System.out.println("Loaded " + rooms.size() + " rooms");
            } catch (Exception e) {
                showError("Lỗi khi tải danh sách phòng: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}