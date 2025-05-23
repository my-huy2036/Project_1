package com.housemanagement.ui;

import com.housemanagement.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class CustomerPortalFrame extends JFrame {

    private User currentUser;
    private JLabel lblWelcome;
    private JButton btnViewMyProfile;
    private JButton btnViewMyContracts;
    private JButton btnViewMyBills;
    private JButton btnSubmitRequest;
    private JButton btnLogout;
    private JTextArea txtDetailsArea;
    private JScrollPane scrollPaneForDetails;
    private JButton btnConfirmEditProfile;
    private Font monospacedFont;

    public CustomerPortalFrame(User user) {
        this.currentUser = user;

        setTitle("🏡 Cổng Thông Tin Khách Thuê - " + currentUser.getUsername());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        Font commonButtonFont = new Font("Arial", Font.PLAIN, 14);
        monospacedFont = new Font("Monospaced", Font.PLAIN, 14);
        Font titleFont = new Font("Arial", Font.BOLD, 16);

        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblWelcome = new JLabel("Chào mừng, " + currentUser.getUsername() + "!");
        lblWelcome.setFont(titleFont);
        welcomePanel.add(lblWelcome);
        mainPanel.add(welcomePanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));

        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));

        btnViewMyProfile = new JButton("👤 Xem Thông Tin Cá Nhân");
        btnViewMyContracts = new JButton("📄 Xem Hợp Đồng");
        btnViewMyBills = new JButton("💰 Xem Hóa Đơn");
        btnSubmitRequest = new JButton("📝 Gửi Yêu Cầu");
        btnLogout = new JButton("🚪 Đăng Xuất");

        Dimension buttonPreferredSize = new Dimension(220, 35);

        configureButton(btnViewMyProfile, buttonPreferredSize, commonButtonFont);
        configureButton(btnViewMyContracts, buttonPreferredSize, commonButtonFont);
        configureButton(btnViewMyBills, buttonPreferredSize, commonButtonFont);
        configureButton(btnSubmitRequest, buttonPreferredSize, commonButtonFont);
        configureButton(btnLogout, buttonPreferredSize, commonButtonFont);

        actionsPanel.add(btnViewMyProfile);
        actionsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        actionsPanel.add(btnViewMyContracts);
        actionsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        actionsPanel.add(btnViewMyBills);
        actionsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        actionsPanel.add(btnSubmitRequest);
        actionsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        actionsPanel.add(btnLogout);

        contentPanel.add(actionsPanel, BorderLayout.NORTH);

        txtDetailsArea = new JTextArea(15, 40);
        txtDetailsArea.setFont(monospacedFont);
        txtDetailsArea.setEditable(false);
        txtDetailsArea.setLineWrap(true);
        txtDetailsArea.setWrapStyleWord(true);
        scrollPaneForDetails = new JScrollPane(txtDetailsArea);
        scrollPaneForDetails.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Chi Tiết",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                Color.BLUE
        ));
        scrollPaneForDetails.setVisible(false);
        contentPanel.add(scrollPaneForDetails, BorderLayout.CENTER);

        JPanel editButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnConfirmEditProfile = new JButton("✏️ Sửa Thông Tin Hiển Thị");
        btnConfirmEditProfile.setFont(commonButtonFont);
        btnConfirmEditProfile.setVisible(false);
        editButtonPanel.add(btnConfirmEditProfile);
        contentPanel.add(editButtonPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        initEventHandlers();
    }

    private void configureButton(JButton button, Dimension preferredSize, Font font) {
        button.setPreferredSize(preferredSize);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredSize.height));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(font);
    }

    private void initEventHandlers() {
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
                CustomerPortalFrame.this.dispose();
            }
        });

        btnViewMyProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayMyProfile();
                scrollPaneForDetails.setVisible(true);
                btnConfirmEditProfile.setVisible(true);
            }
        });

        btnConfirmEditProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEditProfile();
            }
        });

        btnViewMyContracts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtDetailsArea.setText("Chức năng xem hợp đồng đang được phát triển.\n" +
                        "Tại đây sẽ hiển thị danh sách hợp đồng của bạn.");
                txtDetailsArea.setCaretPosition(0);
                scrollPaneForDetails.setVisible(true);
                btnConfirmEditProfile.setVisible(false);
            }
        });

        btnViewMyBills.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtDetailsArea.setText("Chức năng xem hóa đơn đang được phát triển.\n" +
                        "Tại đây sẽ hiển thị các hóa đơn tiền nhà, điện, nước...");
                txtDetailsArea.setCaretPosition(0);
                scrollPaneForDetails.setVisible(true);
                btnConfirmEditProfile.setVisible(false);
            }
        });

        btnSubmitRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String request = JOptionPane.showInputDialog(CustomerPortalFrame.this,
                        "Nhập yêu cầu/phản hồi của bạn:",
                        "Gửi Yêu Cầu",
                        JOptionPane.PLAIN_MESSAGE);
                if (request != null && !request.trim().isEmpty()) {
                    txtDetailsArea.setText("Yêu cầu của bạn đã được ghi nhận:\n" + request +
                            "\n\nChúng tôi sẽ sớm phản hồi.");
                    txtDetailsArea.setCaretPosition(0);
                    scrollPaneForDetails.setVisible(true);
                    btnConfirmEditProfile.setVisible(false);
                }
            }
        });
    }

    private void handleEditProfile() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(CustomerPortalFrame.this,
                    "Không có thông tin người dùng để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(CustomerPortalFrame.this,
                "Chức năng thay đổi thông tin cá nhân đang được phát triển!\n" +
                        "Tại đây sẽ mở một cửa sổ mới để bạn cập nhật thông tin.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void displayMyProfile() {
        if (currentUser == null) {
            txtDetailsArea.setText("Không có thông tin người dùng để hiển thị.");
            return;
        }

        StringBuilder profileInfo = new StringBuilder();
        profileInfo.append("THÔNG TIN CÁ NHÂN\n");
        profileInfo.append("------------------------------------\n");
        profileInfo.append("Tên đăng nhập  : " + currentUser.getUsername() + "\n");
        profileInfo.append("Email          : " + (currentUser.getEmail() != null ? currentUser.getEmail() : "Chưa cập nhật") + "\n");
        profileInfo.append("Quê quán       : " + (currentUser.getHometown() != null ? currentUser.getHometown() : "Chưa cập nhật") + "\n");

        if (currentUser.getDateOfBirth() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            profileInfo.append("Ngày sinh      : " + sdf.format(currentUser.getDateOfBirth()) + "\n");
        } else {
            profileInfo.append("Ngày sinh      : Chưa cập nhật\n");
        }

        profileInfo.append("Vai trò        : " + (currentUser.getRole() != null ? currentUser.getRole() : "Chưa cập nhật") + "\n");

        txtDetailsArea.setText(profileInfo.toString());
        txtDetailsArea.setCaretPosition(0);
    }

    public static void main(String[] args) {
        User testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("khachhangtest");
        testUser.setRole("customer");
        testUser.setEmail("test@example.com");
        testUser.setHometown("Hà Nội");
        testUser.setCustomerId(101);
        try {
            testUser.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd").parse("1995-08-15"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new CustomerPortalFrame(testUser).setVisible(true));
    }
}
