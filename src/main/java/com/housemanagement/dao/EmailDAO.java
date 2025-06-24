package com.housemanagement.dao;

import com.housemanagement.DatabaseHelper;
import com.housemanagement.model.Email;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmailDAO {

    // Thêm email mới
    public void addEmail(Email email) throws SQLException {
        String sql = "INSERT INTO email (contract_id, customer_id, title, content, time, status, error, file) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (email.getContractId() != null) {
                stmt.setInt(1, email.getContractId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            if (email.getCustomerId() != null) {
                stmt.setInt(2, email.getCustomerId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, email.getTitle());
            stmt.setString(4, email.getContent());

            if (email.getTime() != null) {
                stmt.setTimestamp(5, new Timestamp(email.getTime().getTime()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }

            stmt.setString(6, email.getStatus());
            stmt.setString(7, email.getError());
            stmt.setString(8, email.getFile());

            stmt.executeUpdate();
        }
    }

    // Lấy tất cả emails
    public List<Email> getAllEmails() throws SQLException {
        List<Email> emails = new ArrayList<>();
        String sql = "SELECT * FROM email ORDER BY time DESC";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                emails.add(mapRowToEmail(rs));
            }
        }

        return emails;
    }

    // Lấy email theo ID
    public Email getEmailById(int emailId) throws SQLException {
        String sql = "SELECT * FROM email WHERE email_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emailId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEmail(rs);
                }
            }
        }

        return null;
    }

    // Xóa email
    public boolean deleteEmail(int emailId) throws SQLException {
        String sql = "DELETE FROM email WHERE email_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emailId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Tìm kiếm email theo từ khóa trong title hoặc content
    public List<Email> searchEmails(String keyword) throws SQLException {
        List<Email> emails = new ArrayList<>();
        String sql = "SELECT * FROM email WHERE title LIKE ? OR content LIKE ? ORDER BY time DESC";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String search = "%" + keyword + "%";
            stmt.setString(1, search);
            stmt.setString(2, search);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emails.add(mapRowToEmail(rs));
                }
            }
        }

        return emails;
    }

    // Ánh xạ từ ResultSet sang đối tượng Email
    private Email mapRowToEmail(ResultSet rs) throws SQLException {
        Email e = new Email();
        e.setEmailId(rs.getInt("email_id"));
        e.setContractId(rs.getObject("contract_id") != null ? rs.getInt("contract_id") : null);
        e.setCustomerId(rs.getObject("customer_id") != null ? rs.getInt("customer_id") : null);
        e.setTitle(rs.getString("title"));
        e.setContent(rs.getString("content"));
        e.setTime(rs.getTimestamp("time"));
        e.setStatus(rs.getString("status"));
        e.setError(rs.getString("error"));
        e.setFile(rs.getString("file"));
        return e;
    }
}
