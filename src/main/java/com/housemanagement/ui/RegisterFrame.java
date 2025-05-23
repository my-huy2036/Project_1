package com.housemanagement.ui;

import javax.swing.*;
import com.housemanagement.dao.UserDAO;
import com.housemanagement.model.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {
    private JTextField txtUsername, txtEmail, txtHometown;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JComboBox<String> cbDay, cbMonth, cbYear; // Cho ngày sinh
    private JButton btnRegister, btnBack;

    public RegisterFrame() {
        setTitle("Đăng ký tài khoản");
        setSize(450, 420); // Tăng chiều rộng và chiều cao của Frame
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        int yPos = 30;
        int spacing = 40;
        int labelWidth = 120;
        int fieldX = 160;
        int fieldWidth = 220; // Tăng chiều rộng chung của các trường

        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setBounds(30, yPos, labelWidth, 25);
        panel.add(lblUsername);
        txtUsername = new JTextField();
        txtUsername.setBounds(fieldX, yPos, fieldWidth, 25);
        panel.add(txtUsername);
        yPos += spacing;

        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setBounds(30, yPos, labelWidth, 25);
        panel.add(lblPassword);
        txtPassword = new JPasswordField();
        txtPassword.setBounds(fieldX, yPos, fieldWidth, 25);
        panel.add(txtPassword);
        yPos += spacing;

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(30, yPos, labelWidth, 25);
        panel.add(lblEmail);
        txtEmail = new JTextField();
        txtEmail.setBounds(fieldX, yPos, fieldWidth, 25);
        panel.add(txtEmail);
        yPos += spacing;

        JLabel lblHometown = new JLabel("Quê quán:");
        lblHometown.setBounds(30, yPos, labelWidth, 25);
        panel.add(lblHometown);
        txtHometown = new JTextField();
        txtHometown.setBounds(fieldX, yPos, fieldWidth, 25);
        panel.add(txtHometown);
        yPos += spacing;

        JLabel lblDob = new JLabel("Ngày sinh:");
        lblDob.setBounds(30, yPos, labelWidth, 25);
        panel.add(lblDob);

        JPanel dobPanel = new JPanel(null);
        // Đảm bảo dobPanel đủ rộng: 70 (ngày) + 5 (cách) + 70 (tháng) + 5 (cách) + 85 (năm) = 235
        dobPanel.setBounds(fieldX, yPos, 235, 25);
        int dobFieldX = 0;
        int dobSpacing = 5;

        cbDay = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            cbDay.addItem(String.format("%02d", i));
        }
        cbDay.setBounds(dobFieldX, 0, 70, 25); // Tăng chiều rộng cbDay
        dobPanel.add(cbDay);
        dobFieldX += 70 + dobSpacing;

        cbMonth = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cbMonth.addItem(String.format("%02d", i));
        }
        cbMonth.setBounds(dobFieldX, 0, 70, 25); // Tăng chiều rộng cbMonth và điều chỉnh vị trí
        dobPanel.add(cbMonth);
        dobFieldX += 70 + dobSpacing;

        cbYear = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 100; i--) {
            cbYear.addItem(String.valueOf(i));
        }
        cbYear.setBounds(dobFieldX, 0, 85, 25); // Tăng chiều rộng cbYear và điều chỉnh vị trí
        dobPanel.add(cbYear);
        panel.add(dobPanel);
        yPos += spacing;

        btnRegister = new JButton("Đăng ký");
        btnRegister.setBounds(100, yPos, 100, 30);
        panel.add(btnRegister);

        btnBack = new JButton("Quay lại");
        btnBack.setBounds(240, yPos, 100, 30);
        panel.add(btnBack);

        add(panel);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();
                String email = txtEmail.getText().trim();
                String hometown = txtHometown.getText().trim();
                String selectedRoleDisplay = (String) cbRole.getSelectedItem();
                String roleToStore = "";

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                            "Tên đăng nhập và mật khẩu không được để trống.",
                            "Thiếu thông tin",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if ("Khách hàng".equals(selectedRoleDisplay)) {
                    roleToStore = "customer";
                } else if ("Chủ nhà".equals(selectedRoleDisplay)) {
                    roleToStore = "owner";
                } else {
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                            "Vai trò không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String day = (String) cbDay.getSelectedItem();
                String month = (String) cbMonth.getSelectedItem();
                String year = (String) cbYear.getSelectedItem();
                Date dateOfBirth = null;
                if (day != null && month != null && year != null) {
                    String dobString = year + "-" + month + "-" + day;
                    try {
                        dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse(dobString);
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(RegisterFrame.this,
                                "Ngày sinh không hợp lệ. Vui lòng kiểm tra lại.",
                                "Lỗi định dạng ngày", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }


                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setHometown(hometown);
                user.setRole(roleToStore);
                user.setDateOfBirth(dateOfBirth);

                UserDAO userDAO = new UserDAO();
                try {
                    if (userDAO.isUsernameTaken(username)) {
                        JOptionPane.showMessageDialog(RegisterFrame.this,
                                "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.",
                                "Đăng ký thất bại", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    boolean success = userDAO.registerUser(user);
                    if (success) {
                        JOptionPane.showMessageDialog(RegisterFrame.this,
                                "Đăng ký thành công! Vui lòng đăng nhập.",
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        RegisterFrame.this.dispose();
                        new LoginFrame().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(RegisterFrame.this,
                                "Đăng ký thất bại do lỗi không xác định. Vui lòng thử lại.",
                                "Đăng ký thất bại", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    if(ex.getMessage().toLowerCase().contains("duplicate entry")){
                        JOptionPane.showMessageDialog(RegisterFrame.this,
                                "Tên đăng nhập hoặc email đã tồn tại.",
                                "Đăng ký thất bại", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(RegisterFrame.this,
                                "Lỗi hệ thống trong quá trình đăng ký: " + ex.getMessage(),
                                "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                            "Lỗi không xác định: " + ex.getMessage(),
                            "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RegisterFrame().setVisible(true);
            }
        });
    }
}
