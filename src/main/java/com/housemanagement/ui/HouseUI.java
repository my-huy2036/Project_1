package com.housemanagement.ui;

import com.housemanagement.dao.RoomDAO;
import com.housemanagement.controller.QLyPhong;
import com.housemanagement.ui.sidebar.SidebarPanel;
import com.housemanagement.ui.sidebar.trangchu;
import com.housemanagement.ui.sidebar.phong;
import com.housemanagement.ui.sidebar.khach;
import com.housemanagement.ui.sidebar.email;
import com.housemanagement.ui.sidebar.hopdong;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HouseUI extends JFrame {

    private RoomDAO roomDAO;
    private QLyPhong qLyPhongController;
    private Connection connection; // Add connection field

    private JPanel contentDisplayPanel;
    private CardLayout cardLayout;

    private SidebarPanel sidebarInstance;
    private JButton btnToggleSidebar;

    // Your existing color and font constants...
    public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font BOLD_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font SECTION_TITLE_FONT = new Font("Arial", Font.BOLD, 18);

    public static final Color SUCCESS_COLOR = new Color(0x28,0xA7,0x45);
    public static final Color INFO_COLOR = new Color(0x17,0xA2,0xB8);
    public static final Color WARNING_COLOR = new Color(0xFF,0xC1,0x07);
    public static final Color DANGER_COLOR = new Color(0xDC,0x35,0x45);
    public static final Color LIGHT_GRAY_COLOR = new Color(0x6C,0x75,0x7D);

    public static final Color CARD_BACKGROUND = new Color(0xE9, 0xEC, 0xEF);
    public static final Color CARD_HEADER_BACKGROUND = new Color(0x00, 0x7B, 0xFF);
    public static final Color CARD_HEADER_TEXT_COLOR = Color.WHITE;
    public static final int CARD_WIDTH = 180;
    public static final int CARD_HGAP = 15;
    public static final int CARD_VGAP = 15;

    public HouseUI() {
        try {
            // Initialize database connection
            initializeDatabase();

            roomDAO = new RoomDAO();
            qLyPhongController = new QLyPhong(roomDAO);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khởi tạo: " + e.getMessage(), "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("QUẢN LÝ NHÀ TRỌ");
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        sidebarInstance = new SidebarPanel(this, "Xin chào, Chủ Nhà");
        add(sidebarInstance, BorderLayout.WEST);

        createMainContentArea();
        loadOwnerInfo();

        JButton defaultButton = sidebarInstance.getDefaultButton();
        if (defaultButton != null) {
            sidebarInstance.selectMenuItem(defaultButton);
            handleMenuAction(defaultButton.getText());
        }

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component visibleCard = getVisibleCard();
                if (visibleCard instanceof phong) {
                    ((phong) visibleCard).updateRoomCardsLayoutPublic();
                }
            }
        });

        // Add shutdown hook to close database connection
        Runtime.getRuntime().addShutdownHook(new Thread(this::closeDatabase));
    }

    private void initializeDatabase() throws SQLException {
        try {
            String url = "jdbc:mysql://localhost:3306/boarding_house";
            String username = "root";
            String password = "quanghuy";

            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new SQLException("Không thể kết nối database: " + e.getMessage(), e);
        }
    }

    private void closeDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadOwnerInfo() {
        sidebarInstance.setUserGreeting("Xin chào, Chủ Nhà");
    }

    private Component getVisibleCard() {
        for (Component comp : contentDisplayPanel.getComponents()) {
            if (comp.isVisible()) {
                return comp;
            }
        }
        return null;
    }

    private void createMainContentArea() {
        JPanel mainContentWrapper = new JPanel(new BorderLayout(0,0));
        mainContentWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainContentWrapper.setBackground(Color.WHITE);

        btnToggleSidebar = new JButton("☰");
        btnToggleSidebar.setFont(new Font("Arial", Font.BOLD, 18));
        btnToggleSidebar.setMargin(new Insets(1, 4, 1, 4));
        btnToggleSidebar.setFocusPainted(false);
        btnToggleSidebar.addActionListener(e -> {
            sidebarInstance.setVisible(!sidebarInstance.isVisible());
            revalidate();
            repaint();
            Component visibleCard = getVisibleCard();
            if (visibleCard instanceof phong) {
                SwingUtilities.invokeLater(((phong)visibleCard)::updateRoomCardsLayoutPublic);
            }
        });

        cardLayout = new CardLayout();
        contentDisplayPanel = new JPanel(cardLayout);
        contentDisplayPanel.setBackground(Color.WHITE);

        trangchu trangChuPanel = new trangchu(roomDAO, qLyPhongController);
        contentDisplayPanel.add(trangChuPanel, "Trang chủ");

        phong phongPanel = new phong(this, qLyPhongController);
        contentDisplayPanel.add(phongPanel, "Phòng");

        khach khachPanel = new khach();
        contentDisplayPanel.add(khachPanel, "Khách thuê");

        hopdong hopdongPanel = new hopdong();
        contentDisplayPanel.add(hopdongPanel, "Hợp đồng");

        email emailPanel = new email();
        contentDisplayPanel.add(emailPanel, "Lịch sử gửi email");

        mainContentWrapper.add(contentDisplayPanel, BorderLayout.CENTER);
        add(mainContentWrapper, BorderLayout.CENTER);
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Chức năng '" + title + "' đang được phát triển.", SwingConstants.CENTER);
        label.setFont(SECTION_TITLE_FONT);
        panel.add(label, BorderLayout.CENTER);
        panel.setName(title);
        return panel;
    }

    public void handleMenuAction(String menuItem) {
        Component currentVisibleCard = getVisibleCard();
        if (currentVisibleCard instanceof phong) {
            ((phong)currentVisibleCard).setTopBarVisible(false);
        }

        cardLayout.show(contentDisplayPanel, menuItem);

        Component newVisibleCard = getVisibleCard();
        if (newVisibleCard instanceof phong) {
            phong phongPanelCasted = (phong) newVisibleCard;
            phongPanelCasted.setTopBarVisible(true);
            phongPanelCasted.loadRoomData();
            SwingUtilities.invokeLater(phongPanelCasted::updateRoomCardsLayoutPublic);
        } else if (newVisibleCard instanceof trangchu) {
            ((trangchu)newVisibleCard).loadHomePageData();
        }
    }

    public void refreshHomePageData() {
        Component currentCard = getVisibleCard();
        if (currentCard instanceof trangchu) {
            ((trangchu) currentCard).loadHomePageData();
        } else {
            for(Component comp : contentDisplayPanel.getComponents()){
                if(comp instanceof trangchu){
                    ((trangchu)comp).loadHomePageData();
                    break;
                }
            }
        }
    }

    public JButton getBtnToggleSidebar() {
        return this.btnToggleSidebar;
    }

    @Override
    public void dispose() {
        closeDatabase();
        super.dispose();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            HouseUI frame = new HouseUI();
            frame.setVisible(true);
        });
    }
}