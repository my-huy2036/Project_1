package com.housemanagement.ui;

import javax.swing.*;
import com.housemanagement.dao.UserDAO;
import com.housemanagement.model.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {
    private JTextField txtUsername, txtEmail;
    private JPasswordField txtPassword;
    private JButton btnRegister, btnBack;

    public RegisterFrame() {
        setTitle("Đăng ký tài khoản");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        int yPos = 30;
        int spacing = 40;
        int labelWidth = 120;
        int fieldX = 140;
        int fieldWidth = 200;

        // Tên đăng nhập
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setBounds(30, yPos, labelWidth, 25);
        panel.add(lblUsername);
        txtUsername = new JTextField();
        txtUsername.setBounds(fieldX, yPos, fieldWidth, 25);
        panel.add(txtUsername);
        yPos += spacing;

        // Email
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(30, yPos, labelWidth, 25);
        panel.add(lblEmail);
        txtEmail = new JTextField();
        txtEmail.setBounds(fieldX, yPos, fieldWidth, 25);
        panel.add(txtEmail);
        yPos += spacing;

        // Mật khẩu (chuyển xuống cuối)
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setBounds(30, yPos, labelWidth, 25);
        panel.add(lblPassword);
        txtPassword = new JPasswordField();
        txtPassword.setBounds(fieldX, yPos, fieldWidth, 25);
        panel.add(txtPassword);
        yPos += spacing + 20;

        // Buttons
        btnRegister = new JButton("Đăng ký");
        btnRegister.setBounds(80, yPos, 100, 30);
        panel.add(btnRegister);

        btnBack = new JButton("Quay lại");
        btnBack.setBounds(220, yPos, 100, 30);
        panel.add(btnBack);

        add(panel);

        // Xử lý sự kiện Enter
        txtUsername.addActionListener(e -> txtEmail.requestFocus());
        txtEmail.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addActionListener(e -> handleRegister());

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterFrame.this.dispose();
                new LoginFrame().setVisible(true);
            }
        });
    }

    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        // Validate input
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(RegisterFrame.this,
                    "Vui lòng nhập đầy đủ thông tin.",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(RegisterFrame.this,
                    "Email không hợp lệ.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate password length
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(RegisterFrame.this,
                    "Mật khẩu phải có ít nhất 6 ký tự.",
                    "Mật khẩu yếu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        UserDAO userDAO = new UserDAO();
        try {
            // Kiểm tra username đã tồn tại
            if (userDAO.isUsernameTaken(username)) {
                JOptionPane.showMessageDialog(RegisterFrame.this,
                        "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.",
                        "Đăng ký thất bại",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra email đã tồn tại
            if (userDAO.isEmailTaken(email)) {
                JOptionPane.showMessageDialog(RegisterFrame.this,
                        "Email đã được sử dụng. Vui lòng dùng email khác.",
                        "Đăng ký thất bại",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Thực hiện đăng ký
            boolean success = userDAO.registerUser(user);
            if (success) {
                JOptionPane.showMessageDialog(RegisterFrame.this,
                        "Đăng ký thành công! Vui lòng đăng nhập.",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);

                // Đóng form đăng ký và mở form đăng nhập
                RegisterFrame.this.dispose();
                new LoginFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(RegisterFrame.this,
                        "Đăng ký thất bại. Vui lòng thử lại.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            if (ex.getMessage().toLowerCase().contains("duplicate entry")) {
                if (ex.getMessage().contains("username")) {
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                            "Tên đăng nhập đã tồn tại.",
                            "Đăng ký thất bại",
                            JOptionPane.ERROR_MESSAGE);
                } else if (ex.getMessage().contains("email")) {
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                            "Email đã được sử dụng.",
                            "Đăng ký thất bại",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                            "Thông tin đã tồn tại trong hệ thống.",
                            "Đăng ký thất bại",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(RegisterFrame.this,
                        "Lỗi hệ thống: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(RegisterFrame.this,
                    "Lỗi không xác định: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true));
    }
}