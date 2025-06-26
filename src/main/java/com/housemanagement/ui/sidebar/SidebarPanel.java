package com.housemanagement.ui.sidebar;

import com.housemanagement.ui.HouseUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SidebarPanel extends JPanel {

    private HouseUI parentFrame; // Tham chiếu đến HouseUI để gọi lại handleMenuAction
    private JButton selectedMenuItemButton = null;
    private List<JButton> menuButtons = new ArrayList<>();
    private JLabel lblUserGreetingComponent;

    // Màu sắc và Font chữ cho Sidebar
    private static final Color SIDEBAR_BACKGROUND = new Color(0x34, 0x3A, 0x40);
    private static final Color SIDEBAR_TEXT_COLOR = Color.WHITE;
    private static final Color SIDEBAR_SELECTED_BACKGROUND = new Color(0x3F, 0x45, 0x4D);
    private static final Color SIDEBAR_SELECTED_INDICATOR_COLOR = new Color(0x20, 0xC9, 0x97);
    private static final Font DEFAULT_FONT_SIDEBAR = new Font("Arial", Font.PLAIN, 14);
    private static final Font SIDEBAR_FONT_ITEMS = new Font("Arial", Font.BOLD, 15);
    private static final Font HEADER_FONT_SIDEBAR = new Font("Arial", Font.BOLD, 16);

    public SidebarPanel(HouseUI parentFrame, String initialUserGreeting) {
        this.parentFrame = parentFrame;
        setBackground(SIDEBAR_BACKGROUND);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(280, parentFrame.getHeight()));
        setBorder(new EmptyBorder(10, 0, 10, 0));

        initializeUI(initialUserGreeting);
    }

    private void initializeUI(String initialUserGreeting) {
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÀ TRỌ");
        lblTitle.setFont(HEADER_FONT_SIDEBAR);
        lblTitle.setForeground(SIDEBAR_TEXT_COLOR);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel lblSubTitle = new JLabel("SIMPLEZ HOUSE");
        lblSubTitle.setFont(DEFAULT_FONT_SIDEBAR);
        lblSubTitle.setForeground(new Color(0xAD, 0xB5, 0xBD));
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubTitle.setBorder(new EmptyBorder(0, 15, 0, 0));

        lblUserGreetingComponent = new JLabel(initialUserGreeting);
        lblUserGreetingComponent.setFont(DEFAULT_FONT_SIDEBAR);
        lblUserGreetingComponent.setForeground(SIDEBAR_TEXT_COLOR);
        lblUserGreetingComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblUserGreetingComponent.setBorder(new EmptyBorder(0, 15, 0, 0));

        add(lblTitle);
        add(lblSubTitle);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(lblUserGreetingComponent);
        add(Box.createRigidArea(new Dimension(0, 20)));

        String[] menuItems = {"Trang chủ", "Phòng", "Khách thuê", "Hợp đồng",
                "Tính tiền"};

        menuButtons.clear();
        for (String item : menuItems) {
            JButton menuItemButton = new JButton(item);
            menuItemButton.setFont(SIDEBAR_FONT_ITEMS);
            menuItemButton.setForeground(SIDEBAR_TEXT_COLOR);
            menuItemButton.setBackground(SIDEBAR_BACKGROUND);
            menuItemButton.setOpaque(true);
            menuItemButton.setBorderPainted(true);
            menuItemButton.setFocusPainted(false);
            menuItemButton.setHorizontalAlignment(SwingConstants.LEFT);
            menuItemButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            menuItemButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, menuItemButton.getPreferredSize().height + 10));
            menuItemButton.setIconTextGap(10);
            menuItemButton.setBorder(new EmptyBorder(5, 15, 5, 5));

            menuItemButton.addActionListener(e -> {
                selectMenuItem(menuItemButton); // Thay đổi giao diện nút được chọn
                parentFrame.handleMenuAction(item); // Thông báo cho HouseUI để chuyển panel nội dung
            });
            add(menuItemButton);
            add(Box.createRigidArea(new Dimension(0, 5)));
            menuButtons.add(menuItemButton);
        }
    }

    public void selectMenuItem(JButton buttonToSelect) {
        if (selectedMenuItemButton != null) {
            selectedMenuItemButton.setBackground(SIDEBAR_BACKGROUND);
            selectedMenuItemButton.setBorder(new EmptyBorder(5, 15, 5, 5));
        }

        buttonToSelect.setBackground(SIDEBAR_SELECTED_BACKGROUND);
        Border indicator = new MatteBorder(0, 5, 0, 0, SIDEBAR_SELECTED_INDICATOR_COLOR);
        Border padding = new EmptyBorder(5, 10, 5, 5); // left 10 để text không đè lên indicator
        buttonToSelect.setBorder(BorderFactory.createCompoundBorder(indicator, padding));

        selectedMenuItemButton = buttonToSelect;
        revalidate();
        repaint();
    }

    public JButton getDefaultButton() {
        if (!menuButtons.isEmpty()) {
            for (JButton btn : menuButtons) {
                if ("Trang chủ".equals(btn.getText())) {
                    return btn;
                }
            }
            return menuButtons.get(0);
        }
        return null;
    }

    public void setUserGreeting(String greeting) {
        if (lblUserGreetingComponent != null) {
            lblUserGreetingComponent.setText(greeting);
        }
    }
}
