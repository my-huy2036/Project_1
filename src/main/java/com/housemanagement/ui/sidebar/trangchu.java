package com.housemanagement.ui.sidebar;

import com.housemanagement.controller.QLyPhong;
import com.housemanagement.dao.RoomDAO;
import com.housemanagement.model.Room;
import com.housemanagement.ui.HouseUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class trangchu extends JPanel {
    private QLyPhong qLyPhongController;
    private RoomDAO roomDAO;

    // Simplified stats labels - keeping only essential ones
    private JLabel lblRentedRoomsCount;
    private JLabel lblVacantRoomsCount;
    private JLabel lblOccupancyRate;

    // Tables - keeping only vacant rooms table
    private JTable vacantRoomsTable;
    private VacantRoomTableModel vacantRoomTableModel;

    // Notification area
    private JTextArea notificationArea;

    // Status bar label
    private JLabel statusLabel;

    // Data
    private int rentedCount = 0;
    private int vacantCount = 0;
    private double occupancyRate = 0;

    public trangchu(RoomDAO roomDAO, QLyPhong qLyPhongController) {
        this.roomDAO = roomDAO;
        this.qLyPhongController = qLyPhongController;
        initComponents();
        loadHomePageData();
        startAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);

        // Top section: Stats + Notifications
        JPanel topSection = new JPanel(new BorderLayout(10, 10));
        topSection.setBackground(Color.WHITE);

        // Stats panel
        topSection.add(createStatsPanel(), BorderLayout.CENTER);

        // Notifications panel on the right
        topSection.add(createNotificationPanel(), BorderLayout.EAST);

        mainPanel.add(topSection, BorderLayout.NORTH);

        // Center section: Vacant Rooms Table
        JPanel centerSection = createVacantRoomsSection();
        mainPanel.add(centerSection, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Status bar
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createStatsPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Thống Kê Tổng Quan",
                TitledBorder.LEFT, TitledBorder.TOP, HouseUI.SECTION_TITLE_FONT, new Color(70, 130, 180)
        ));
        outerPanel.setBackground(Color.WHITE);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 10));
        statsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        statsPanel.setBackground(Color.WHITE);

        // Initialize simplified stat labels
        lblRentedRoomsCount = new JLabel("0");
        lblVacantRoomsCount = new JLabel("0");
        lblOccupancyRate = new JLabel("0%");

        // Create simplified stat cards
        statsPanel.add(createStatCard("Phòng Đã Thuê", lblRentedRoomsCount, new Color(34, 139, 34), "👥"));
        statsPanel.add(createStatCard("Phòng Trống", lblVacantRoomsCount, new Color(255, 140, 0), "🏠"));
        statsPanel.add(createStatCard("Tỷ Lệ Lấp Đầy", lblOccupancyRate, new Color(138, 43, 226), "📊"));

        outerPanel.add(statsPanel, BorderLayout.CENTER);
        return outerPanel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);

        // Icon and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JLabel titleLabel = new JLabel(" " + title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(Color.DARK_GRAY);

        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);

        // Value
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(248, 249, 250));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    private JPanel createNotificationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "🔔 Thông Báo Quan Trọng",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), Color.RED
        ));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(350, 120));

        notificationArea = new JTextArea(4, 0);
        notificationArea.setEditable(false);
        notificationArea.setBackground(new Color(255, 248, 220));
        notificationArea.setFont(new Font("Arial", Font.PLAIN, 11));
        notificationArea.setLineWrap(true);
        notificationArea.setWrapStyleWord(true);
        notificationArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(notificationArea);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createVacantRoomsSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);

        // Vacant rooms table
        vacantRoomTableModel = new VacantRoomTableModel();
        vacantRoomsTable = new JTable(vacantRoomTableModel);
        styleTable(vacantRoomsTable);

        JScrollPane vacantScrollPane = new JScrollPane(vacantRoomsTable);
        vacantScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "🏠 Danh Sách Phòng Trống",
                TitledBorder.LEFT, TitledBorder.TOP, HouseUI.SECTION_TITLE_FONT, new Color(70, 130, 180)
        ));

        section.add(vacantScrollPane, BorderLayout.CENTER);

        return section;
    }

    private void styleTable(JTable table) {
        table.setFont(HouseUI.DEFAULT_FONT);
        table.setRowHeight(30);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(184, 207, 229));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
                new EmptyBorder(5, 10, 5, 10)
        ));

        statusLabel = new JLabel("📊 Cập nhật lần cuối: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setForeground(Color.DARK_GRAY);

        JLabel refreshLabel = new JLabel(" | 🔄 Tự động làm mới mỗi 5 phút");
        refreshLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        refreshLabel.setForeground(Color.DARK_GRAY);

        statusBar.add(statusLabel);
        statusBar.add(refreshLabel);

        return statusBar;
    }

    public void loadHomePageData() {
        try {
            List<Room> allRooms = qLyPhongController.getAllRooms();
            if(allRooms == null) allRooms = new ArrayList<>();

            // Reset counters
            rentedCount = 0;
            vacantCount = 0;

            List<Room> vacantRoomsList = new ArrayList<>();

            // Process rooms
            for(Room room : allRooms){
                if(room.getStatus() != null) {
                    switch(room.getStatus().toLowerCase()) {
                        case "đã cho thuê":
                        case "rented":
                            rentedCount++;
                            break;
                        default:
                            vacantRoomsList.add(room);
                            vacantCount++;
                            break;
                    }
                }
            }

            // Calculate occupancy rate
            int totalRooms = allRooms.size();
            occupancyRate = totalRooms > 0 ? (double)rentedCount / totalRooms * 100 : 0;

            // Update UI
            updateStatsLabels();
            updateNotifications();

            // Update vacant rooms table
            if (vacantRoomTableModel != null) {
                vacantRoomTableModel.setRooms(vacantRoomsList);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu trang chủ: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatsLabels() {
        lblRentedRoomsCount.setText(String.valueOf(rentedCount));
        lblVacantRoomsCount.setText(String.valueOf(vacantCount));
        lblOccupancyRate.setText(String.format("%.1f%%", occupancyRate));
    }

    private void updateNotifications() {
        StringBuilder notifications = new StringBuilder();

        if (rentedCount == 0 && vacantCount > 0) {
            notifications.append("• Không có phòng nào được thuê.\n");
        }

        if (vacantCount == 0 && rentedCount > 0) {
            notifications.append("• Tất cả phòng đã được thuê hết!\n");
        }

        if (rentedCount > 0 && vacantCount > 0) {
            notifications.append("• Hệ thống hoạt động bình thường\n");
            notifications.append("• Có ").append(vacantCount).append(" phòng trống sẵn sàng cho thuê");
        }

        if (rentedCount == 0 && vacantCount == 0) {
            notifications.append("• Chưa có dữ liệu phòng trong hệ thống");
        }

        notificationArea.setText(notifications.toString());
    }

    private void startAutoRefresh() {
        Timer timer = new Timer(300000, e -> { // Refresh every 5 minutes
            loadHomePageData();
            if (statusLabel != null) {
                statusLabel.setText("📊 Cập nhật lần cuối: " +
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            }
        });
        timer.start();
    }

    // Simplified Table Model for Vacant Rooms
    private static class VacantRoomTableModel extends AbstractTableModel {
        private List<Room> rooms = new ArrayList<>();
        private final String[] columns = {"Tên Phòng", "Tiền Thuê (VNĐ)", "Trạng Thái"};

        public void setRooms(List<Room> rooms) {
            this.rooms = new ArrayList<>(rooms);
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return rooms.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            Room r = rooms.get(rowIndex);
            DecimalFormat formatter = new DecimalFormat("#,###");
            switch (columnIndex) {
                case 0: return r.getRoomName();
                case 1: return formatter.format(r.getRent());
                case 2: return r.getStatus() != null ? r.getStatus() : "Trống";
                default: return "";
            }
        }
    }

    // Public method to refresh data from outside
    public void refreshData() {
        loadHomePageData();
    }
}