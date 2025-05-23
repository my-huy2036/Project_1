package com.housemanagement.dao;

import com.housemanagement.DatabaseHelper;
import com.housemanagement.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // Method hiện có, ví dụ addCustomer
    public void addCustomer(Customer customer) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "INSERT INTO customers (full_name, phone, address) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, customer.getFullName());
                stmt.setString(2, customer.getPhone());
                stmt.setString(3, customer.getAddress());
                stmt.executeUpdate();
            }
        }
    }

    // Thêm method lấy tất cả khách hàng
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT customer_id, full_name, phone, address FROM customers";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Customer c = new Customer();
                c.setCustomerId(rs.getInt("customer_id"));
                c.setFullName(rs.getString("full_name"));
                c.setPhone(rs.getString("phone"));
                c.setAddress(rs.getString("address"));
                customers.add(c);
            }
        }
        return customers;
    }
}
