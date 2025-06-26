package com.housemanagement.ui.sidebar;

import com.housemanagement.controller.QLyPhong;
import com.housemanagement.controller.ThemPhong;
import com.housemanagement.controller.XoaPhong;
import com.housemanagement.controller.SuaPhong;
import com.housemanagement.model.Room;
import com.housemanagement.ui.HouseUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class phong extends JPanel {
    private HouseUI parentFrame;
    private QLyPhong qLyPhongController;
    private JPanel roomCardsPanel;
    private ThemPhong addRoomFormPanelInstance;
    private CardLayout mainCardLayout;
    private JPanel contentPanelInternal;
    private JComboBox<String> cbRoomStateFilter;
    private JComboBox<String> cbPaymentStatusFilter;
    private JPanel topBarPanelInternal;
    private JButton btnShowAddRoomForm;
    private JTextField searchField;
    private boolean roomStateFilterPlaceholderActive = true;
    private boolean paymentStatusFilterPlaceholderActive = true;
    private DecimalFormat currencyFormatter = new DecimalFormat("#,###");

    public phong(HouseUI parentFrame, QLyPhong qLyPhongController) {
        this.parentFrame = parentFrame;
        this.qLyPhongController = qLyPhongController;

        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        createTopBar();
        createContentArea();

        add(topBarPanelInternal, BorderLayout.NORTH);
        add(contentPanelInternal, BorderLayout.CENTER);
    }

    // Simplified status display method
    private String getDisplayStatus(String status) {
        return status != null ? status : "Còn trống";
    }

    private void createTopBar() {
        topBarPanelInternal = new JPanel(new BorderLayout(10, 5));
        topBarPanelInternal.setBackground(Color.WHITE);
        topBarPanelInternal.setBorder(new EmptyBorder(10, 15, 15, 15));

        JPanel leftAndCenterPanel = new JPanel(new BorderLayout(15, 0));
        leftAndCenterPanel.setBackground(Color.WHITE);

        // Toggle button section
        JButton toggleBtnFromParent = parentFrame.getBtnToggleSidebar();
        if (toggleBtnFromParent != null) {
            JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            togglePanel.setBackground(Color.WHITE);
            JButton localToggleBtn = new JButton("☰");
            localToggleBtn.setFont(new Font("Arial", Font.BOLD, 16));
            localToggleBtn.setMargin(new Insets(8, 12, 8, 12));
            localToggleBtn.setFocusPainted(false);
            localToggleBtn.setBackground(new Color(248, 249, 250));
            localToggleBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            localToggleBtn.addActionListener(l -> parentFrame.getBtnToggleSidebar().doClick());
            addHoverEffect(localToggleBtn, new Color(248, 249, 250), new Color(233, 236, 239));
            togglePanel.add(localToggleBtn);
            leftAndCenterPanel.add(togglePanel, BorderLayout.WEST);
        }

        // Search and filter section
        JPanel searchAndFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchAndFilterPanel.setBackground(Color.WHITE);

        // Search field
        searchField = new JTextField(15);
        searchField.setFont(HouseUI.DEFAULT_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        searchField.setBackground(Color.WHITE);
        searchField.addActionListener(e -> loadRoomData());

        // Add placeholder text effect
        setupSearchFieldPlaceholder();

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        searchAndFilterPanel.add(searchPanel);

        String roomStatePlaceholder = "-Trạng thái phòng-";
        cbRoomStateFilter = new JComboBox<>(new String[]{roomStatePlaceholder, "Tất cả", "Còn trống", "Đã cho thuê"});
        cbRoomStateFilter.setPreferredSize(new Dimension(180, 30));
        cbRoomStateFilter.setMaximumSize(new Dimension(180, 30));
        cbRoomStateFilter.setFont(HouseUI.DEFAULT_FONT);
        cbRoomStateFilter.setBackground(Color.WHITE);

        // Custom renderer để hiển thị đầy đủ text
        cbRoomStateFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });

        addPlaceholderLogic(cbRoomStateFilter, roomStatePlaceholder, () -> roomStateFilterPlaceholderActive, val -> roomStateFilterPlaceholderActive = val);
        searchAndFilterPanel.add(cbRoomStateFilter);

        String paymentStatusPlaceholder = "-Trạng thái phí-";
        cbPaymentStatusFilter = new JComboBox<>(new String[]{paymentStatusPlaceholder, "Tất cả", "Đã thanh toán", "Chưa thanh toán"});
        cbPaymentStatusFilter.setPreferredSize(new Dimension(160, 30));
        cbPaymentStatusFilter.setMaximumSize(new Dimension(160, 30));
        cbPaymentStatusFilter.setFont(HouseUI.DEFAULT_FONT);
        cbPaymentStatusFilter.setBackground(Color.WHITE);

        // Custom renderer cho payment status
        cbPaymentStatusFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });

        addPlaceholderLogic(cbPaymentStatusFilter, paymentStatusPlaceholder, () -> paymentStatusFilterPlaceholderActive, val -> paymentStatusFilterPlaceholderActive = val);
        searchAndFilterPanel.add(cbPaymentStatusFilter);

        ActionListener filterListener = e -> loadRoomData();
        cbRoomStateFilter.addActionListener(filterListener);
        cbPaymentStatusFilter.addActionListener(filterListener);

        leftAndCenterPanel.add(searchAndFilterPanel, BorderLayout.CENTER);
        topBarPanelInternal.add(leftAndCenterPanel, BorderLayout.CENTER);

        // Action buttons section
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtonPanel.setBackground(Color.WHITE);

        JButton btnRefresh = createStyledButtonInternal("🔄 Làm mới", new Color(108, 117, 125));
        btnRefresh.addActionListener(e -> {
            clearFilters();
            loadRoomData();
            JOptionPane.showMessageDialog(this, "Đã làm mới danh sách phòng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
        actionButtonPanel.add(btnRefresh);

        btnShowAddRoomForm = createStyledButtonInternal("➕ Thêm phòng", HouseUI.SUCCESS_COLOR);
        btnShowAddRoomForm.addActionListener(e -> {
            addRoomFormPanelInstance.clearForm();
            mainCardLayout.show(contentPanelInternal, "AddRoomForm");
            setTopBarVisible(false);
        });
        actionButtonPanel.add(btnShowAddRoomForm);

        topBarPanelInternal.add(actionButtonPanel, BorderLayout.EAST);
    }

    private void setupSearchFieldPlaceholder() {
        String placeholder = "Tìm kiếm theo tên phòng...";
        searchField.setText(placeholder);
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(placeholder);
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // Add document listener for real-time search
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private Timer searchTimer = new Timer(300, e -> loadRoomData());

            {
                searchTimer.setRepeats(false);
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                if (!searchField.getText().equals(placeholder)) {
                    searchTimer.restart();
                }
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                if (!searchField.getText().equals(placeholder)) {
                    searchTimer.restart();
                }
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                if (!searchField.getText().equals(placeholder)) {
                    searchTimer.restart();
                }
            }
        });
    }

    private void clearFilters() {
        searchField.setText("Tìm kiếm theo tên phòng...");
        searchField.setForeground(Color.GRAY);

        cbRoomStateFilter.setSelectedIndex(0);
        cbPaymentStatusFilter.setSelectedIndex(0);
        roomStateFilterPlaceholderActive = true;
        paymentStatusFilterPlaceholderActive = true;
    }

    public void setTopBarVisible(boolean visible) {
        if (topBarPanelInternal != null) {
            topBarPanelInternal.setVisible(visible);
        }
    }

    private void createContentArea() {
        mainCardLayout = new CardLayout();
        contentPanelInternal = new JPanel(mainCardLayout);
        contentPanelInternal.setBackground(Color.WHITE);

        // Room list panel với BoxLayout theo chiều dọc
        roomCardsPanel = new JPanel();
        roomCardsPanel.setLayout(new BoxLayout(roomCardsPanel, BoxLayout.Y_AXIS));
        roomCardsPanel.setBackground(Color.WHITE);
        roomCardsPanel.setBorder(new EmptyBorder(10, 15, 15, 15));

        JScrollPane roomsViewScrollPane = new JScrollPane(roomCardsPanel);
        roomsViewScrollPane.setBorder(BorderFactory.createEmptyBorder());
        roomsViewScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanelInternal.add(roomsViewScrollPane, "RoomsList");

        addRoomFormPanelInstance = new ThemPhong(this, qLyPhongController);
        contentPanelInternal.add(addRoomFormPanelInstance, "AddRoomForm");
    }

    public void switchToRoomList() {
        mainCardLayout.show(contentPanelInternal, "RoomsList");
        setTopBarVisible(true);
        loadRoomData();
        parentFrame.refreshHomePageData();
    }

    public void updateRoomCardsLayoutPublic() {
    }

    public void loadRoomData() {
        roomCardsPanel.removeAll();
        List<Room> rooms = qLyPhongController.getAllRooms();

        rooms = applyFilters(rooms);

        if (rooms.isEmpty()) {
            showEmptyState();
        } else {
            showRoomCards(rooms);
        }

        roomCardsPanel.revalidate();
        roomCardsPanel.repaint();
    }

    private List<Room> applyFilters(List<Room> rooms) {
        List<Room> filteredRooms = new ArrayList<>(rooms);

        String searchText = searchField.getText();
        if (!searchText.isEmpty() && !searchText.equals("Tìm kiếm theo tên phòng...")) {
            filteredRooms.removeIf(room ->
                    room.getRoomName() == null ||
                            !room.getRoomName().toLowerCase().contains(searchText.toLowerCase())
            );
        }

        if (!roomStateFilterPlaceholderActive && cbRoomStateFilter.getSelectedItem() != null) {
            String stateFilter = cbRoomStateFilter.getSelectedItem().toString();
            if (!"Tất cả".equals(stateFilter)) {
                filteredRooms.removeIf(room -> {
                    String roomStatus = room.getStatus();
                    return roomStatus == null || !roomStatus.equals(stateFilter);
                });
            }
        }

        if (!paymentStatusFilterPlaceholderActive && cbPaymentStatusFilter.getSelectedItem() != null) {
            String paymentFilter = cbPaymentStatusFilter.getSelectedItem().toString();
            if (!"Tất cả".equals(paymentFilter)) {
            }
        }

        return filteredRooms;
    }

    private void showEmptyState() {
        roomCardsPanel.setLayout(new BorderLayout());

        JPanel emptyStatePanel = new JPanel();
        emptyStatePanel.setLayout(new BoxLayout(emptyStatePanel, BoxLayout.Y_AXIS));
        emptyStatePanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel("🏠");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel("Không tìm thấy phòng nào");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messageLabel.setForeground(Color.GRAY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subMessageLabel = new JLabel("Thử thay đổi bộ lọc hoặc thêm phòng mới");
        subMessageLabel.setFont(HouseUI.DEFAULT_FONT);
        subMessageLabel.setForeground(Color.GRAY);
        subMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyStatePanel.add(Box.createVerticalGlue());
        emptyStatePanel.add(iconLabel);
        emptyStatePanel.add(Box.createVerticalStrut(10));
        emptyStatePanel.add(messageLabel);
        emptyStatePanel.add(Box.createVerticalStrut(5));
        emptyStatePanel.add(subMessageLabel);
        emptyStatePanel.add(Box.createVerticalGlue());

        roomCardsPanel.add(emptyStatePanel, BorderLayout.CENTER);
    }

    private void showRoomCards(List<Room> rooms) {
        roomCardsPanel.setLayout(new BoxLayout(roomCardsPanel, BoxLayout.Y_AXIS));

        for (Room room : rooms) {
            JPanel roomCard = createRoomCardHorizontal(room);
            roomCardsPanel.add(roomCard);
            roomCardsPanel.add(Box.createVerticalStrut(10));
        }
    }

    private JPanel createRoomCardHorizontal(Room room) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(15, 20, 15, 20)
        ));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setPreferredSize(new Dimension(800, 120));

        // Add hover effect
        addCardHoverEffect(card);

        // Left section - Room info
        JPanel leftPanel = new JPanel(new BorderLayout(10, 5));
        leftPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel lblRoomName = new JLabel(room.getRoomName() != null ? room.getRoomName() : "N/A");
        lblRoomName.setFont(new Font("Arial", Font.BOLD, 18));
        lblRoomName.setForeground(new Color(33, 37, 41));

        JLabel statusBadge = createStatusBadge(room.getStatus());

        headerPanel.add(lblRoomName, BorderLayout.WEST);
        headerPanel.add(statusBadge, BorderLayout.EAST);

        leftPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        detailsPanel.setBackground(Color.WHITE);

        if (room.getRent() > 0) {
            JLabel lblRent = new JLabel("💰 " + currencyFormatter.format(room.getRent()) + " VNĐ/tháng");
            lblRent.setFont(new Font("Arial", Font.BOLD, 14));
            lblRent.setForeground(HouseUI.SUCCESS_COLOR);
            detailsPanel.add(lblRent);
        }

        if (room.getMax() > 0) {
            JLabel lblCapacity = new JLabel("👥 Tối đa " + room.getMax() + " người");
            lblCapacity.setFont(new Font("Arial", Font.PLAIN, 14));
            lblCapacity.setForeground(new Color(108, 117, 125));
            detailsPanel.add(lblCapacity);
        }

        leftPanel.add(detailsPanel, BorderLayout.CENTER);

        card.add(leftPanel, BorderLayout.CENTER);

        // Right section - Action buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(200, 100));

        JButton btnEditCard = createStyledButtonInternal("Sửa", HouseUI.WARNING_COLOR);
        JButton btnDeleteCard = createStyledButtonInternal("Xóa", HouseUI.DANGER_COLOR);

        btnEditCard.setPreferredSize(new Dimension(90, 35));
        btnDeleteCard.setPreferredSize(new Dimension(90, 35));

        btnEditCard.addActionListener(e -> {
            boolean updated = SuaPhong.showEditDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    room,
                    qLyPhongController,
                    this
            );

            if (updated) {
                loadRoomData();
                parentFrame.refreshHomePageData();
            }
        });

        btnDeleteCard.addActionListener(e -> {
            boolean confirmed = XoaPhong.showDeleteConfirmDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    room,
                    qLyPhongController
            );

            if (confirmed) {
                loadRoomData();
                parentFrame.refreshHomePageData();
            }
        });

        rightPanel.add(btnEditCard);
        rightPanel.add(btnDeleteCard);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    // Simplified createStatusBadge method
    private JLabel createStatusBadge(String status) {
        JLabel badge = new JLabel(status != null ? status : "Còn trống");
        badge.setFont(new Font("Arial", Font.BOLD, 12));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(5, 15, 5, 15));

        if ("Còn trống".equals(status)) {
            badge.setBackground(new Color(212, 237, 218));
            badge.setForeground(new Color(21, 87, 36));
        } else if ("Đã cho thuê".equals(status)) {
            badge.setBackground(new Color(254, 229, 217));
            badge.setForeground(new Color(133, 77, 14));
        } else {
            badge.setBackground(new Color(233, 236, 239));
            badge.setForeground(new Color(73, 80, 87));
        }

        return badge;
    }

    private void addCardHoverEffect(JPanel card) {
        Color originalBorder = new Color(222, 226, 230);
        Color hoverBorder = new Color(0, 123, 255);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(hoverBorder, 2),
                        new EmptyBorder(14, 19, 14, 19)
                ));
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(originalBorder, 1),
                        new EmptyBorder(15, 20, 15, 20)
                ));
                card.repaint();
            }
        });
    }

    private void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(normalColor);
                }
            }
        });
    }

    private void addPlaceholderLogic(JComboBox<String> comboBox, String placeholder,
                                     java.util.function.Supplier<Boolean> isActiveGetter,
                                     java.util.function.Consumer<Boolean> isActiveSetter) {
        comboBox.addPopupMenuListener(new PopupMenuListener() {
            private boolean firstTime = true;

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (isActiveGetter.get() && firstTime) {
                    List<String> items = new ArrayList<>();
                    for (int i = 0; i < comboBox.getItemCount(); i++) {
                        if (!comboBox.getItemAt(i).equals(placeholder)) {
                            items.add(comboBox.getItemAt(i));
                        }
                    }
                    comboBox.removeAllItems();
                    for (String item : items) {
                        comboBox.addItem(item);
                    }
                    if (items.contains("Tất cả")) {
                        comboBox.setSelectedItem("Tất cả");
                    } else if (!items.isEmpty()) {
                        comboBox.setSelectedIndex(0);
                    }
                    isActiveSetter.accept(false);
                    firstTime = false;
                }
            }

            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });
    }

    private JButton createStyledButtonInternal(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setMargin(new Insets(6, 12, 6, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        addHoverEffect(button, backgroundColor, backgroundColor.darker());

        return button;
    }
}