package com.housemanagement.ui.sidebar;

import com.housemanagement.controller.*;
import com.housemanagement.model.Customer;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class khach extends JPanel {

    // Define colors and fonts
    private static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color INFO_COLOR = new Color(23, 162, 184);
    private static final Color LIGHT_GRAY_COLOR = new Color(248, 249, 250);
    private static final Color DARK_GRAY_COLOR = new Color(52, 58, 64);
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Color BORDER_COLOR = new Color(206, 212, 218);

    private static final Font DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

    // Components
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnThemKhach;
    private JTextField txtSearch;
    private JComboBox<String> cboGenderFilter;
    private JLabel lblTotal;

    // Controller and forms
    private QLyKhach qlyKhachController;
    private ThemKhach themKhachForm;
    private CardLayout mainCardLayout;
    private JPanel contentPanel;

    // Search timer for real-time search
    private Timer searchTimer;

    // Flag để track trạng thái hiện tại
    private boolean isShowingAddForm = false;

    public khach() {
        qlyKhachController = new QLyKhach();
        initializeUI();
        loadCustomerData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 10));
        setBackground(LIGHT_GRAY_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top section with header and tabs
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(createHeaderPanel(), BorderLayout.NORTH);
        topSection.add(createTabPanel(), BorderLayout.SOUTH);
        add(topSection, BorderLayout.NORTH);

        // Main content with CardLayout
        mainCardLayout = new CardLayout();
        contentPanel = new JPanel(mainCardLayout);
        contentPanel.setOpaque(false);

        // Customer list panel
        JPanel listPanel = createListPanel();
        contentPanel.add(listPanel, "CustomerList");

        // Add customer form
        themKhachForm = new ThemKhach(this, qlyKhachController);
        contentPanel.add(themKhachForm, "AddCustomer");

        add(contentPanel, BorderLayout.CENTER);

        // Show customer list by default
        mainCardLayout.show(contentPanel, "CustomerList");
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 20, 15, 20))
        );

        JLabel titleLabel = new JLabel("QUẢN LÝ KHÁCH THUÊ");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(DARK_GRAY_COLOR);

        // Tạo nút Thêm khách
        btnThemKhach = createStyledButton("+ Thêm khách", SUCCESS_COLOR, Color.WHITE, BOLD_FONT);
        btnThemKhach.setPreferredSize(new Dimension(150, 35));
        btnThemKhach.addActionListener(e -> {
            if (isShowingAddForm) {
                switchToCustomerList();
            } else {
                switchToAddForm();
            }
        });

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonContainer.setOpaque(false);
        buttonContainer.add(btnThemKhach);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonContainer, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createTabPanel() {
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setBackground(Color.WHITE);
        tabPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnTabThongTin = new JButton("Thông tin khách thuê");
        styleTabButton(btnTabThongTin);
        btnTabThongTin.setSelected(true);
        updateTabStyle(btnTabThongTin);
        tabPanel.add(btnTabThongTin);

        return tabPanel;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        // Search and filter panel
        panel.add(createSearchPanel(), BorderLayout.NORTH);

        // Table panel
        panel.add(createTablePanel(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Left side - Search box
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(DEFAULT_FONT);
        leftPanel.add(searchLabel);

        txtSearch = new JTextField(20);
        txtSearch.setFont(DEFAULT_FONT);
        txtSearch.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtSearch.setPreferredSize(new Dimension(200, 30));
        txtSearch.setToolTipText("Tìm theo tên, CCCD, email, điện thoại...");

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

        leftPanel.add(txtSearch);

        // Right side - Gender filter
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JLabel filterLabel = new JLabel("Giới tính:");
        filterLabel.setFont(DEFAULT_FONT);
        rightPanel.add(filterLabel);

        cboGenderFilter = new JComboBox<>(new String[]{"Tất cả", "Nam", "Nữ"});
        cboGenderFilter.setFont(DEFAULT_FONT);
        cboGenderFilter.addActionListener(e -> performSearch());
        rightPanel.add(cboGenderFilter);

        JButton btnRefresh = createStyledButton("Làm mới", INFO_COLOR, Color.WHITE, DEFAULT_FONT);
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cboGenderFilter.setSelectedIndex(0);
            loadCustomerData();
        });
        rightPanel.add(btnRefresh);

        searchPanel.add(leftPanel, BorderLayout.WEST);
        searchPanel.add(rightPanel, BorderLayout.EAST);

        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.setOpaque(false);

        // Create table with new columns
        String[] columns = {"ID", "Họ tên", "Giới tính", "CCCD/CMND", "Điện thoại", "Email", "Địa chỉ", "Hành động"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only action column is editable
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Integer.class; // ID column
                return Object.class;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(45);
        table.setFont(DEFAULT_FONT);
        table.setSelectionBackground(PRIMARY_COLOR.brighter().brighter());
        table.setSelectionForeground(DARK_GRAY_COLOR);
        table.setGridColor(new Color(224, 224, 224));
        table.setIntercellSpacing(new Dimension(0, 1));

        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Set column widths
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Họ tên
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Giới tính
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // CCCD
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Phone
        table.getColumnModel().getColumn(5).setPreferredWidth(150); // Email
        table.getColumnModel().getColumn(6).setPreferredWidth(200); // Địa chỉ

        // Center alignment for some columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Giới tính
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // CCCD
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Phone

        // Custom header
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(242, 242, 242));
        header.setForeground(DARK_GRAY_COLOR);
        header.setFont(BOLD_FONT);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Action column
        table.getColumnModel().getColumn(7).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new ActionButtonEditor(new JCheckBox()));
        table.getColumnModel().getColumn(7).setPreferredWidth(120);
        table.getColumnModel().getColumn(7).setMinWidth(120);
        table.getColumnModel().getColumn(7).setMaxWidth(130);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        lblTotal = new JLabel("Tổng số khách: 0");
        lblTotal.setFont(BOLD_FONT);
        summaryPanel.add(lblTotal);

        panel.add(summaryPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Load customer data from database
    public void loadCustomerData() {
        tableModel.setRowCount(0);
        List<Customer> customers = qlyKhachController.getAllCustomers();

        for (Customer customer : customers) {
            Object[] row = {
                    customer.getCustomerId(),
                    customer.getFullName(),
                    customer.getGenderDisplay(),
                    customer.getIdentity(),
                    customer.getPhone() != null ? customer.getPhone() : "",
                    customer.getEmail(),
                    customer.getAddress(),
                    "" // Action column
            };
            tableModel.addRow(row);
        }

        updateSummary(customers.size());
    }

    // Perform search/filter
    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        String genderFilter = (String) cboGenderFilter.getSelectedItem();

        List<Customer> customers;

        // Get all customers first
        if (!keyword.isEmpty()) {
            customers = qlyKhachController.searchCustomers(keyword);
        } else {
            customers = qlyKhachController.getAllCustomers();
        }

        // Apply gender filter if not "Tất cả"
        if (!"Tất cả".equals(genderFilter)) {
            String genderValue = genderFilter.equals("Nam") ? "M" : "F";
            List<Customer> filteredCustomers = new ArrayList<>();

            for (Customer customer : customers) {
                if (genderValue.equals(customer.getGender())) {
                    filteredCustomers.add(customer);
                }
            }
            customers = filteredCustomers;
        }

        // Update table
        tableModel.setRowCount(0);
        for (Customer customer : customers) {
            Object[] row = {
                    customer.getCustomerId(),
                    customer.getFullName(),
                    customer.getGenderDisplay(),
                    customer.getIdentity(),
                    customer.getPhone() != null ? customer.getPhone() : "",
                    customer.getEmail(),
                    customer.getAddress(),
                    ""
            };
            tableModel.addRow(row);
        }

        updateSummary(customers.size());
    }

    // Update summary label
    private void updateSummary(int count) {
        if (lblTotal != null) {
            lblTotal.setText("Tổng số khách: " + count);
        }
    }

    // Switch to add form
    public void switchToAddForm() {
        isShowingAddForm = true;
        themKhachForm.clearForm();
        mainCardLayout.show(contentPanel, "AddCustomer");
        btnThemKhach.setText("← Quay lại");
        btnThemKhach.setBackground(INFO_COLOR);
    }

    // Switch back to customer list
    public void switchToCustomerList() {
        isShowingAddForm = false;
        mainCardLayout.show(contentPanel, "CustomerList");
        btnThemKhach.setText("+ Thêm khách");
        btnThemKhach.setBackground(SUCCESS_COLOR);
        loadCustomerData(); // Reload data
    }

    // Helper methods for styling
    private void styleTabButton(JButton button) {
        button.setFont(DEFAULT_FONT);
        button.setForeground(DARK_GRAY_COLOR);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.isSelected()) {
                    button.setBackground(LIGHT_GRAY_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.isSelected()) {
                    button.setBackground(Color.WHITE);
                }
            }
        });
    }

    private void updateTabStyle(JButton tab) {
        if (tab.isSelected()) {
            tab.setFont(BOLD_FONT);
            tab.setBackground(LIGHT_GRAY_COLOR);
            tab.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
                    BorderFactory.createEmptyBorder(8, 15, 6, 15)
            ));
        } else {
            tab.setFont(DEFAULT_FONT);
            tab.setBackground(Color.WHITE);
            tab.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        }
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        // Hover effect
        Color hoverBgColor = bgColor.brighter();
        Color pressedBgColor = bgColor.darker();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBgColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(pressedBgColor);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(hoverBgColor);
            }
        });
        return button;
    }

    // --- Action Buttons for Table - FIXED VERSION ---
    class ActionButtonPanel extends JPanel {
        public JButton editButton;
        public JButton deleteButton;

        public ActionButtonPanel() {
            // Sử dụng FlowLayout với alignment CENTER và khoảng cách nhỏ
            super(new FlowLayout(FlowLayout.CENTER, 3, 2));
            setOpaque(true); // Đổi thành true để background hiển thị đúng
            setPreferredSize(new Dimension(120, 40)); // Đặt kích thước cố định

            editButton = new JButton("Sửa");
            styleActionButton(editButton, INFO_COLOR, Color.WHITE);
            editButton.setPreferredSize(new Dimension(50, 28)); // Kích thước cố định cho nút

            deleteButton = new JButton("Xóa");
            styleActionButton(deleteButton, DANGER_COLOR, Color.WHITE); // Đổi text color thành WHITE
            deleteButton.setPreferredSize(new Dimension(50, 28)); // Kích thước cố định cho nút

            add(editButton);
            add(deleteButton);
        }

        private void styleActionButton(JButton button, Color color, Color foregroundColor) {
            button.setFont(new Font("Segoe UI", Font.BOLD, 11)); // Giảm font size một chút
            button.setForeground(foregroundColor);
            button.setBackground(color);
            button.setFocusPainted(false);
            button.setMargin(new Insets(2, 6, 2, 6)); // Giảm margin
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6)); // Đặt border rõ ràng

            // Hover effects
            Color hoverBgColor = color.brighter();
            Color pressedBgColor = color.darker();
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setBackground(hoverBgColor);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setBackground(color);
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setBackground(pressedBgColor);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setBackground(hoverBgColor);
                    }
                }
            });
        }
    }

    class ActionButtonRenderer implements TableCellRenderer {
        private final ActionButtonPanel panel = new ActionButtonPanel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            // Đặt background color cho panel
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                // Alternating row colors
                Color bgColor = row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250);
                panel.setBackground(bgColor);
            }

            // Đảm bảo panel được repaint
            panel.revalidate();
            panel.repaint();

            return panel;
        }
    }

    class ActionButtonEditor extends DefaultCellEditor {
        private final ActionButtonPanel panel = new ActionButtonPanel();
        private int currentRow;

        public ActionButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            setClickCountToStart(1);

            panel.editButton.addActionListener(e -> {
                fireEditingStopped();

                // Get customer data from current row
                int modelRow = table.convertRowIndexToModel(currentRow);
                int customerId = (int) tableModel.getValueAt(modelRow, 0);

                // Get customer object
                Customer customer = qlyKhachController.getCustomerById(customerId);
                if (customer != null) {
                    // Show edit dialog
                    boolean updated = SuaKhach.showEditDialog(
                            (Frame) SwingUtilities.getWindowAncestor(table),
                            customer,
                            qlyKhachController,
                            khach.this
                    );

                    if (updated) {
                        loadCustomerData();
                    }
                }
            });

            panel.deleteButton.addActionListener(e -> {
                fireEditingStopped();

                int modelRow = table.convertRowIndexToModel(currentRow);
                int customerId = (int) tableModel.getValueAt(modelRow, 0);

                Customer customer = qlyKhachController.getCustomerById(customerId);
                if (customer != null) {
                    boolean deleted = XoaKhach.showDeleteConfirmDialog(
                            (Frame) SwingUtilities.getWindowAncestor(table),
                            customer,
                            qlyKhachController
                    );

                    if (deleted) {
                        loadCustomerData();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.currentRow = row;

            // Đặt background color cho panel
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                Color bgColor = row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250);
                panel.setBackground(bgColor);
            }

            // Đảm bảo panel được repaint
            panel.revalidate();
            panel.repaint();

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        @Override
        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}