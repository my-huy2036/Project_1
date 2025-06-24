package com.housemanagement.dao;

import com.housemanagement.DatabaseHelper;
import com.housemanagement.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private static final String ALL_COLUMNS = "customer_id, fullname, gender, phone, email, address, identity";

    // Thêm khách hàng
    public void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (fullname, gender, phone, email, address, identity) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getGender());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getEmail());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getIdentity());

            stmt.executeUpdate();
        }
    }

    // Lấy tất cả khách hàng
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT " + ALL_COLUMNS + " FROM customers";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(mapRowToCustomer(rs));
            }
        }

        return customers;
    }

    // Lấy khách hàng theo ID
    public Customer getCustomerById(int customerId) throws SQLException {
        String sql = "SELECT " + ALL_COLUMNS + " FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }
        }

        return null;
    }

    // Cập nhật khách hàng
    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET fullname = ?, gender = ?, phone = ?, email = ?, address = ?, identity = ? WHERE customer_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getGender());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getEmail());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getIdentity());
            stmt.setInt(7, customer.getCustomerId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Xóa khách hàng
    public boolean deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Kiểm tra CCCD đã tồn tại
    public boolean isIdentityExists(String identity) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE identity = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, identity);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Kiểm tra CCCD đã tồn tại (trừ 1 ID cụ thể)
    public boolean isIdentityExistsExceptCustomer(String identity, int customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE identity = ? AND customer_id != ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, identity);
            stmt.setInt(2, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Tìm kiếm khách hàng
    public List<Customer> searchCustomers(String keyword) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT " + ALL_COLUMNS + " FROM customers WHERE fullname LIKE ? OR phone LIKE ? OR email LIKE ? OR identity LIKE ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String search = "%" + keyword + "%";
            stmt.setString(1, search);
            stmt.setString(2, search);
            stmt.setString(3, search);
            stmt.setString(4, search);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapRowToCustomer(rs));
                }
            }
        }

        return customers;
    }

    // Lấy khách hàng theo giới tính
    public List<Customer> getCustomersByGender(String gender) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT " + ALL_COLUMNS + " FROM customers WHERE gender = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gender);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapRowToCustomer(rs));
                }
            }
        }

        return customers;
    }

    // Chuyển 1 dòng ResultSet thành 1 đối tượng Customer
    private Customer mapRowToCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setFullName(rs.getString("fullname"));
        c.setGender(rs.getString("gender"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        c.setIdentity(rs.getString("identity"));
        return c;
    }
}
