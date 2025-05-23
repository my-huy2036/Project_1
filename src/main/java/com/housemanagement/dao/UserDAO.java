package com.housemanagement.dao;

import com.housemanagement.DatabaseHelper;
import com.housemanagement.model.User;

import java.sql.*;
// import java.util.Date; // Không cần import java.util.Date ở đây nếu chỉ dùng java.sql.Date

public class UserDAO {
    public User authenticate(String username, String password) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password")); // Cân nhắc không lưu mật khẩu vào đối tượng User sau khi xác thực
                    user.setRole(rs.getString("role"));
                    // Xử lý customer_id và house_owner_id có thể NULL
                    int customerIdInt = rs.getInt("customer_id");
                    if (!rs.wasNull()) {
                        user.setCustomerId(customerIdInt);
                    }
                    int houseOwnerIdInt = rs.getInt("house_owner_id");
                    if (!rs.wasNull()) {
                        user.setHouseOwnerId(houseOwnerIdInt);
                    }
                    user.setEmail(rs.getString("email"));
                    user.setHometown(rs.getString("hometown"));
                    user.setDateOfBirth(rs.getDate("date_of_birth")); // Đọc ngày sinh
                    return user;
                }
            }
        }
        return null;
    }

    public boolean registerUser(User user) throws SQLException {
        // Thay đổi câu lệnh SQL để bao gồm date_of_birth
        String sql = "INSERT INTO users (username, password, role, email, hometown, date_of_birth) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword()); // Cân nhắc mã hóa mật khẩu trước khi lưu
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getHometown());

            // Chuyển đổi java.util.Date sang java.sql.Date
            if (user.getDateOfBirth() != null) {
                stmt.setDate(6, new java.sql.Date(user.getDateOfBirth().getTime()));
            } else {
                stmt.setNull(6, java.sql.Types.DATE); // Cho phép ngày sinh là NULL
            }

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        }
    }

    public boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1"; // Tối ưu hơn bằng cách chỉ chọn 1 cột và giới hạn 1 dòng
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean updateUserProfile(User user) throws SQLException {
        String sql = "UPDATE users SET email = ?, hometown = ?, date_of_birth = ? WHERE user_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getHometown());
            if (user.getDateOfBirth() != null) {
                stmt.setDate(3, new java.sql.Date(user.getDateOfBirth().getTime()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }
            stmt.setInt(4, user.getUserId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }
}
