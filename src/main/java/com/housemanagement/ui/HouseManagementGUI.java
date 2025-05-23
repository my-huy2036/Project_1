package com.housemanagement.ui;

import com.housemanagement.dao.ContractDAO;
import com.housemanagement.dao.CustomerDAO;
import com.housemanagement.dao.RoomDAO;
import com.housemanagement.model.Room;
import com.housemanagement.model.User; // Giả sử có User model để lấy thông tin chủ nhà

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder; // Thêm import
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HouseManagementGUI extends JFrame {

    private RoomDAO roomDAO;
    private CustomerDAO customerDAO;
    private ContractDAO contractDAO;
    // private UserDAO userDAO;

    private JPanel roomCardsPanel;
    private JLabel lblUserGreeting;
    private JComboBox<String> cbRoomStatusFilter;
    private JLabel txtStatus;
    private JPanel sidebarPanel;
    private JButton btnToggleSidebar;
    private JButton selectedMenuItemButton = null; // Lưu nút menu đang được chọn
    private List<JButton> sidebarButtons = new ArrayList<>();


    private static final Color SIDEBAR_BACKGROUND = new Color(0x34, 0x3A, 0x40);
    private static final Color SIDEBAR_TEXT_COLOR = Color.WHITE;
    private static final Color SIDEBAR_SELECTED_BACKGROUND = new Color(0x3F, 0x45, 0x4D); // Màu nền hơi sáng hơn cho mục được chọn
    private static final Color SIDEBAR_SELECTED_INDICATOR_COLOR = new Color(0x20, 0xC9, 0x97); // Màu xanh teal cho indicator

    private static final Color CARD_BACKGROUND = new Color(0xE9, 0xEC, 0xEF);
    private static final Color CARD_HEADER_BACKGROUND = new Color(0x00, 0x7B, 0xFF);
    private static final Color CARD_HEADER_TEXT_COLOR = Color.WHITE;
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font SIDEBAR_FONT = new Font("Arial", Font.BOLD, 15);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);

    public HouseManagementGUI() {
        try {
            roomDAO = new RoomDAO();
            // customerDAO = new CustomerDAO();
            // contractDAO = new ContractDAO();
            // userDAO = new UserDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khởi tạo DAO: " + e.getMessage(), "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("QUẢN LÝ NHÀ TRỌ");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        createSidebar();
        createMainContentPanel();
        createStatusBar();
        loadOwnerInfo();

        // Đặt mục "Trang chủ" làm mục được chọn ban đầu
        if (!sidebarButtons.isEmpty()) {
            // Tìm nút "Trang chủ" hoặc chọn nút đầu tiên
            JButton defaultButton = sidebarButtons.get(0); // Mặc định là nút đầu tiên
            for(JButton btn : sidebarButtons){
                if("Trang chủ".equals(btn.getText())){
                    defaultButton = btn;
                    break;
                }
            }
            selectMenuItem(defaultButton);
        }

        loadRoomData();
    }

    private void loadOwnerInfo() {
        lblUserGreeting.setText("Xin chào, Chủ Nhà");
    }


    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(SIDEBAR_BACKGROUND);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(280, getHeight()));
        sidebarPanel.setBorder(new EmptyBorder(10, 0, 10, 0)); // Lề trên, trái, dưới, phải (trái 0 để indicator sát mép)

        JLabel lblTitle = new JLabel("QUẢN LÝ NHÀ TRỌ");
        lblTitle.setFont(HEADER_FONT);
        lblTitle.setForeground(SIDEBAR_TEXT_COLOR);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(0,15,0,0)); // Thêm lề trái cho tiêu đề

        JLabel lblSubTitle = new JLabel("SIMPLEZ HOUSE");
        lblSubTitle.setFont(DEFAULT_FONT);
        lblSubTitle.setForeground(new Color(0xAD, 0xB5, 0xBD));
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubTitle.setBorder(new EmptyBorder(0,15,0,0)); // Thêm lề trái cho tiêu đề phụ

        lblUserGreeting = new JLabel();
        lblUserGreeting.setFont(DEFAULT_FONT);
        lblUserGreeting.setForeground(SIDEBAR_TEXT_COLOR);
        lblUserGreeting.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblUserGreeting.setBorder(new EmptyBorder(0,15,0,0)); // Thêm lề trái cho lời chào

        sidebarPanel.add(lblTitle);
        sidebarPanel.add(lblSubTitle);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarPanel.add(lblUserGreeting);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] menuItems = {"Trang chủ", "Phòng", "Khách thuê", "Dịch vụ", "Chỉ số điện",
                "Phát sinh", "Tính tiền", "Phiếu chi", "Lịch sử gửi email/SMS",
                "Báo cáo"};

        sidebarButtons.clear(); // Xóa danh sách nút cũ (nếu có)
        for (String item : menuItems) {
            JButton menuItemButton = new JButton(item);
            menuItemButton.setFont(SIDEBAR_FONT);
            menuItemButton.setForeground(SIDEBAR_TEXT_COLOR);
            menuItemButton.setBackground(SIDEBAR_BACKGROUND);
            menuItemButton.setOpaque(true);
            menuItemButton.setBorderPainted(true); // Cho phép vẽ border tùy chỉnh
            menuItemButton.setFocusPainted(false);
            menuItemButton.setHorizontalAlignment(SwingConstants.LEFT);
            menuItemButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            menuItemButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, menuItemButton.getPreferredSize().height + 10));
            menuItemButton.setIconTextGap(10);
            // Đặt border mặc định (có không gian cho indicator nhưng không vẽ indicator)
            menuItemButton.setBorder(new EmptyBorder(5, 15, 5, 5)); // top, left (10 + 5 for indicator), bottom, right

            menuItemButton.addActionListener(e -> {
                selectMenuItem(menuItemButton);
                handleMenuAction(item);
            });
            sidebarPanel.add(menuItemButton);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            sidebarButtons.add(menuItemButton); // Thêm nút vào danh sách
        }

        add(sidebarPanel, BorderLayout.WEST);
    }

    private void selectMenuItem(JButton buttonToSelect) {
        if (selectedMenuItemButton != null) {
            // Đặt lại nút đã chọn trước đó về trạng thái bình thường
            selectedMenuItemButton.setBackground(SIDEBAR_BACKGROUND);
            selectedMenuItemButton.setBorder(new EmptyBorder(5, 15, 5, 5)); // Căn chỉnh text giống nhau
        }

        // Đặt nút mới được chọn
        buttonToSelect.setBackground(SIDEBAR_SELECTED_BACKGROUND);
        Border indicator = new MatteBorder(0, 5, 0, 0, SIDEBAR_SELECTED_INDICATOR_COLOR); // Thanh indicator bên trái 5px
        Border padding = new EmptyBorder(5, 10, 5, 5); // Padding bên trong (left 10 để text sau indicator)
        buttonToSelect.setBorder(BorderFactory.createCompoundBorder(indicator, padding));

        selectedMenuItemButton = buttonToSelect;
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }


    private void createMainContentPanel() {
        JPanel mainContentWrapper = new JPanel(new BorderLayout(10, 10));
        mainContentWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainContentWrapper.setBackground(Color.WHITE);

        JPanel topBarPanel = new JPanel(new BorderLayout(10, 5));
        topBarPanel.setBackground(Color.WHITE);

        btnToggleSidebar = new JButton("☰");
        btnToggleSidebar.setFont(new Font("Arial", Font.BOLD, 18));
        btnToggleSidebar.setMargin(new Insets(1, 4, 1, 4));
        btnToggleSidebar.setFocusPainted(false);
        btnToggleSidebar.addActionListener(e -> {
            sidebarPanel.setVisible(!sidebarPanel.isVisible());
            revalidate();
            repaint();
        });

        JPanel leftOfFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        leftOfFilterPanel.setBackground(Color.WHITE);
        leftOfFilterPanel.add(btnToggleSidebar);


        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);

        cbRoomStatusFilter = new JComboBox<>(new String[]{"Tất cả", "Còn trống", "Đã cho thuê", "Chưa thu phí"});
        cbRoomStatusFilter.setFont(DEFAULT_FONT);
        filterPanel.add(cbRoomStatusFilter);

        JPanel topBarLeftSection = new JPanel(new BorderLayout());
        topBarLeftSection.setBackground(Color.WHITE);
        topBarLeftSection.add(leftOfFilterPanel, BorderLayout.WEST);
        topBarLeftSection.add(filterPanel, BorderLayout.CENTER);


        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionButtonPanel.setBackground(Color.WHITE);
        actionButtonPanel.add(createStyledButton("Thêm nhà", new Color(0x00,0x7B,0xFF)));


        topBarPanel.add(topBarLeftSection, BorderLayout.WEST);
        topBarPanel.add(actionButtonPanel, BorderLayout.EAST);
        mainContentWrapper.add(topBarPanel, BorderLayout.NORTH);

        roomCardsPanel = new JPanel();
        roomCardsPanel.setLayout(new GridLayout(0, 5, 15, 15));
        roomCardsPanel.setBackground(Color.WHITE);
        roomCardsPanel.setBorder(new EmptyBorder(10,0,0,0));


        JScrollPane scrollPane = new JScrollPane(roomCardsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainContentWrapper.add(scrollPane, BorderLayout.CENTER);

        add(mainContentWrapper, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(DEFAULT_FONT);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setMargin(new Insets(5,10,5,10));
        return button;
    }


    private void loadRoomData() {
        roomCardsPanel.removeAll();
        List<Room> rooms;
        try {
            rooms = generateSampleRooms(2);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu phòng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            rooms = new ArrayList<>();
        }


        if (rooms.isEmpty()) {
            JLabel lblNoRooms = new JLabel("Hiện chưa có phòng nào được tạo.", SwingConstants.CENTER);
            lblNoRooms.setFont(HEADER_FONT);
            lblNoRooms.setForeground(Color.GRAY);
            roomCardsPanel.setLayout(new BorderLayout());
            roomCardsPanel.add(lblNoRooms, BorderLayout.CENTER);
        } else {
            roomCardsPanel.setLayout(new GridLayout(0, 5, 15, 15));
            for (Room room : rooms) {
                JPanel roomCard = createRoomCard(room);
                roomCardsPanel.add(roomCard);
            }
        }
        roomCardsPanel.revalidate();
        roomCardsPanel.repaint();
    }

    private List<Room> generateSampleRooms(int count) {
        List<Room> sampleRooms = new ArrayList<>();
        if (count == 0) return sampleRooms;

        Random random = new Random();
        for (int i = 1; i <= count; i++) {
            Room room = new Room();
            room.setRoomId(i);
            room.setRoomName("Phòng 1." + String.format("%02d", i));
            sampleRooms.add(room);
        }
        return sampleRooms;
    }


    private JPanel createRoomCard(Room room) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                new EtchedBorder(EtchedBorder.LOWERED),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(CARD_BACKGROUND);
        card.setPreferredSize(new Dimension(180, 200));


        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(CARD_HEADER_BACKGROUND);
        JLabel lblRoomName = new JLabel(room.getRoomName() != null ? room.getRoomName() : "N/A");
        lblRoomName.setFont(BOLD_FONT);
        lblRoomName.setForeground(CARD_HEADER_TEXT_COLOR);
        headerPanel.add(lblRoomName);
        card.add(headerPanel, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel lblTenant = new JLabel("Khách: " + (Math.random() > 0.3 ? "Nguyễn Văn A" : "Trống"));
        lblTenant.setFont(DEFAULT_FONT);
        JLabel lblRent = new JLabel("Giá: " + String.format("%,.0f VNĐ", (1500000 + Math.random() * 500000)));
        lblRent.setFont(DEFAULT_FONT);

        content.add(lblTenant);
        content.add(Box.createRigidArea(new Dimension(0,5)));
        content.add(lblRent);
        content.add(Box.createVerticalGlue());

        card.add(content, BorderLayout.CENTER);

        JPanel cardActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5,0));
        cardActionsPanel.setOpaque(false);
        JButton btnEditCard = createStyledButton("Sửa", new Color(0xFF,0xC1,0x07));
        JButton btnDeleteCard = createStyledButton("Xóa", new Color(0xDC,0x35,0x45));

        cardActionsPanel.add(btnEditCard);
        cardActionsPanel.add(btnDeleteCard);
        card.add(cardActionsPanel, BorderLayout.SOUTH);

        return card;
    }

    private void handleMenuAction(String menuItem) {
        System.out.println("Menu item selected: " + menuItem);
        txtStatus.setText("Đã chọn: " + menuItem);
    }

    private void createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtStatus = new JLabel("Sẵn sàng");
        txtStatus.setFont(DEFAULT_FONT);
        statusBar.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        add(statusBar, BorderLayout.SOUTH);
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            HouseManagementGUI frame = new HouseManagementGUI();
            frame.setVisible(true);
        });
    }
}
