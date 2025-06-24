package com.housemanagement.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import com.housemanagement.dao.UserDAO;
import com.housemanagement.model.User;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;

public class LoginFrame extends JFrame {
    private JTextField txtEmailOrUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color TEXT_FIELD_BORDER_COLOR = new Color(220, 220, 220);
    private static final Color BUTTON_SIGN_IN_BG_COLOR = new Color(0x3A, 0x42, 0x6A);
    private static final Color BUTTON_SIGN_IN_FG_COLOR = Color.WHITE;
    private static final Color BUTTON_REGISTER_BG_COLOR = new Color(0xF0, 0xF0, 0xF0);
    private static final Color BUTTON_REGISTER_FG_COLOR = new Color(0x60, 0x60, 0x60);
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    private static final Color CLOSE_BUTTON_COLOR = new Color(0xFF5F57);
    private static final Color MINIMIZE_BUTTON_COLOR = new Color(0xFFBD2E);

    private Point initialClick;

    public LoginFrame() {
        setTitle("Đăng nhập");
        setUndecorated(true);
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JPanel customTitleBar = createCustomTitleBar();
        mainPanel.add(customTitleBar, BorderLayout.NORTH);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(BACKGROUND_COLOR);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 10));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel lblLoginTitle = new JLabel("ĐĂNG NHẬP");
        lblLoginTitle.setFont(TITLE_FONT);
        lblLoginTitle.setForeground(new Color(0x30, 0x30, 0x30));
        titlePanel.add(lblLoginTitle);
        contentWrapper.add(titlePanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(50, 40, 50, 40));

        txtEmailOrUsername = new JTextField("EMAIL HOẶC TÊN ĐĂNG NHẬP");
        txtPassword = new JPasswordField("MẬT KHẨU");

        customizeTextField(txtEmailOrUsername, "EMAIL HOẶC TÊN ĐĂNG NHẬP");
        customizeTextField(txtPassword, "MẬT KHẨU");

        centerPanel.add(txtEmailOrUsername);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        centerPanel.add(txtPassword);
        centerPanel.add(Box.createVerticalGlue());

        contentWrapper.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        bottomPanel.setPreferredSize(new Dimension(getWidth(), 60));

        btnRegister = new JButton("ĐĂNG KÝ");
        btnRegister.setFont(BUTTON_FONT);
        btnRegister.setBackground(BUTTON_REGISTER_BG_COLOR);
        btnRegister.setForeground(BUTTON_REGISTER_FG_COLOR);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setOpaque(true);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setFont(BUTTON_FONT);
        btnLogin.setBackground(BUTTON_SIGN_IN_BG_COLOR);
        btnLogin.setForeground(BUTTON_SIGN_IN_FG_COLOR);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setOpaque(true);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomPanel.add(btnRegister);
        bottomPanel.add(btnLogin);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Xử lý sự kiện Enter
        txtEmailOrUsername.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addActionListener(e -> handleLogin());

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginFrame.this.dispose();
                new RegisterFrame().setVisible(true);
            }
        });
    }

    private void handleLogin() {
        String emailOrUsername = txtEmailOrUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if ("EMAIL HOẶC TÊN ĐĂNG NHẬP".equals(emailOrUsername) || emailOrUsername.isEmpty() ||
                "MẬT KHẨU".equals(password) || password.isEmpty()) {
            JOptionPane.showMessageDialog(LoginFrame.this,
                    "Vui lòng nhập tài khoản và mật khẩu.",
                    "Thông tin trống",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        try {
            User user = userDAO.authenticate(emailOrUsername, password);
            if (user != null) {
                openDashboard(user);
                LoginFrame.this.dispose();
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Tài khoản hoặc mật khẩu không đúng.",
                        "Đăng nhập thất bại",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(LoginFrame.this,
                    "Lỗi kết nối hoặc xử lý dữ liệu: " + ex.getMessage(),
                    "Lỗi hệ thống",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createCustomTitleBar() {
        JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        titleBar.setBackground(new Color(0xECECEC));
        titleBar.setPreferredSize(new Dimension(getWidth(), 30));
        titleBar.setBorder(new MatteBorder(0,0,1,0, Color.LIGHT_GRAY));

        JButton btnClose = createCircularButton(CLOSE_BUTTON_COLOR, "Đóng");
        btnClose.addActionListener(e -> System.exit(0));

        JButton btnMinimize = createCircularButton(MINIMIZE_BUTTON_COLOR, "Thu nhỏ");
        btnMinimize.addActionListener(e -> setState(JFrame.ICONIFIED));

        titleBar.add(Box.createHorizontalStrut(5));
        titleBar.add(btnClose);
        titleBar.add(btnMinimize);

        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            }
        });

        return titleBar;
    }

    private JButton createCircularButton(Color color, String tooltip) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(12, 12));
        button.setBackground(color);

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();

                if (model.isPressed()) {
                    g2.setColor(color.darker());
                } else if (model.isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                g2.fill(new Ellipse2D.Float(0, 0, c.getWidth(), c.getHeight()));
                g2.dispose();
            }
        });
        return button;
    }

    private void customizeTextField(JTextField textField, String placeholder) {
        textField.setFont(DEFAULT_FONT);
        textField.setForeground(Color.GRAY);
        textField.setText(placeholder);
        textField.setBorder(new MatteBorder(0, 0, 1, 0, TEXT_FIELD_BORDER_COLOR));
        textField.setPreferredSize(new Dimension(300, 40));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                    if (textField instanceof JPasswordField) {
                        ((JPasswordField) textField).setEchoChar('●');
                    }
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                    if (textField instanceof JPasswordField) {
                        ((JPasswordField) textField).setEchoChar((char)0);
                    }
                }
            }
        });
        if (textField instanceof JPasswordField && textField.getText().equals(placeholder)) {
            ((JPasswordField) textField).setEchoChar((char)0);
        }
    }

    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> new HouseUI().setVisible(true));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}