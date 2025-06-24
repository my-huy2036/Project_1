package com.housemanagement.dao;

import com.housemanagement.DatabaseHelper;
import com.housemanagement.model.User;

import java.sql.*;

public class UserDAO {

    // Kiểm tra username đã tồn tại
    public boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Kiểm tra email đã tồn tại
    public boolean isEmailTaken(String email) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Đăng ký người dùng mới
    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword()); // Gợi ý: nên mã hoá mật khẩu trước
            stmt.setString(3, user.getEmail());
            return stmt.executeUpdate() > 0;
        }
    }

    // Đăng nhập (username hoặc email + password)
    public User authenticate(String usernameOrEmail, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, password); // Gợi ý: so sánh với mật khẩu đã mã hoá
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            }
        }
        return null;
    }
}
