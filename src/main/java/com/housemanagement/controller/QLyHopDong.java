package com.housemanagement.controller;

import com.housemanagement.DatabaseHelper;
import com.housemanagement.model.Contract;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QLyHopDong {

    // Lấy danh sách tất cả hợp đồng
    public List<Contract> getAllContracts() throws SQLException {
        List<Contract> contracts = new ArrayList<>();
        // Trong getAllContracts() và getContractById()
        String sql = "SELECT c.*, cu.full_name, r.room_name, r.rent " +
                "FROM contracts c " +
                "JOIN customers cu ON c.customer_id = cu.customer_id " +
                "JOIN rooms r ON c.room_id = r.room_id " +
                "ORDER BY c.created_at DESC";


        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Contract contract = mapResultSetToContract(rs);
                contracts.add(contract);
            }
        }
        return contracts;
    }

    // Lấy hợp đồng theo ID
    public Contract getContractById(int contractId) throws SQLException {
        String sql = "SELECT c.*, cu.full_name, r.room_name " +
                "FROM contracts c " +
                "JOIN customers cu ON c.customer_id = cu.customer_id " +
                "JOIN rooms r ON c.room_id = r.room_id " +
                "WHERE c.contract_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contractId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToContract(rs);
            }
        }
        return null;
    }

    // Tạo hợp đồng mới
    public void createContract(Contract contract) throws SQLException {
        String sql = "INSERT INTO contracts (customer_id, room_id, start_date, end_date, deposit, note, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, contract.getCustomerId());
            stmt.setInt(2, contract.getRoomId());
            stmt.setDate(3, contract.getStartDate());
            stmt.setDate(4, contract.getEndDate());
            stmt.setDouble(5, contract.getDeposit());
            stmt.setString(6, contract.getNote());
            stmt.setString(7, contract.getStatus());

            stmt.executeUpdate();

            // Lấy ID của hợp đồng vừa tạo
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                contract.setContractId(rs.getInt(1));
            }

            // Cập nhật trạng thái phòng thành "Đã thuê"
            updateRoomStatus(contract.getRoomId(), "Đã thuê");
        }
    }

    // Cập nhật hợp đồng
    public void updateContract(Contract contract) throws SQLException {
        String sql = "UPDATE contracts SET customer_id = ?, room_id = ?, start_date = ?, end_date = ?, deposit = ?, note = ?, status = ? WHERE contract_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contract.getCustomerId());
            stmt.setInt(2, contract.getRoomId());
            stmt.setDate(3, contract.getStartDate());
            stmt.setDate(4, contract.getEndDate());
            stmt.setDouble(5, contract.getDeposit());
            stmt.setString(6, contract.getNote());
            stmt.setString(7, contract.getStatus());
            stmt.setInt(8, contract.getContractId());

            stmt.executeUpdate();
        }
    }

    // Xóa hợp đồng
    public void deleteContract(int contractId) throws SQLException {
        // Lấy thông tin hợp đồng trước khi xóa
        Contract contract = getContractById(contractId);
        if (contract == null) {
            throw new SQLException("Không tìm thấy hợp đồng!");
        }

        String sql = "DELETE FROM contracts WHERE contract_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contractId);
            stmt.executeUpdate();

            // Nếu hợp đồng đang active, cập nhật phòng thành "Còn trống"
            if ("active".equals(contract.getStatus())) {
                updateRoomStatus(contract.getRoomId(), "Còn trống");
            }
        }
    }

    // Kết thúc hợp đồng
    public void terminateContract(int contractId, Date endDate) throws SQLException {
        Contract contract = getContractById(contractId);
        if (contract == null) {
            throw new SQLException("Không tìm thấy hợp đồng!");
        }

        String sql = "UPDATE contracts SET status = 'terminated', end_date = ? WHERE contract_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, endDate != null ? endDate : new Date(System.currentTimeMillis()));
            stmt.setInt(2, contractId);
            stmt.executeUpdate();

            // Cập nhật phòng thành "Còn trống"
            updateRoomStatus(contract.getRoomId(), "Còn trống");
        }
    }

    // Lấy danh sách phòng còn trống
    public List<Room> getAvailableRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_id, room_name, price FROM rooms WHERE status = 'Còn trống'";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomName(rs.getString("room_name"));
                room.setPrice(rs.getDouble("price"));
                rooms.add(room);
            }
        }
        return rooms;
    }

    // Lấy danh sách khách hàng
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, full_name, phone FROM customers ORDER BY full_name";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setFullName(rs.getString("full_name"));
                customer.setPhone(rs.getString("phone"));
                customers.add(customer);
            }
        }
        return customers;
    }

    // Kiểm tra phòng đã có hợp đồng active chưa
    public boolean isRoomHasActiveContract(int roomId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM contracts WHERE room_id = ? AND status = 'active'";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Helper method để map ResultSet thành Contract
    private Contract mapResultSetToContract(ResultSet rs) throws SQLException {
        Contract contract = new Contract();
        contract.setContractId(rs.getInt("contract_id"));
        contract.setCustomerId(rs.getInt("customer_id"));
        contract.setRoomId(rs.getInt("room_id"));
        contract.setStartDate(rs.getDate("start_date"));
        contract.setEndDate(rs.getDate("end_date"));
        contract.setDeposit(rs.getDouble("deposit"));
        contract.setNote(rs.getString("note"));
        contract.setStatus(rs.getString("status"));
        contract.setCreated(rs.getTimestamp("created_at"));

        // Additional fields
        contract.setCustomerName(rs.getString("full_name"));
        contract.setRoomName(rs.getString("room_name"));

        contract.setRoomRent(rs.getDouble("rent"));

        return contract;
    }

    // Cập nhật trạng thái phòng
    private void updateRoomStatus(int roomId, String status) throws SQLException {
        String sql = "UPDATE rooms SET status = ? WHERE room_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, roomId);
            stmt.executeUpdate();
        }
    }

    // Inner classes cho Room và Customer
    public static class Room {
        private int roomId;
        private String roomName;
        private double price;

        // Getters and Setters
        public int getRoomId() { return roomId; }
        public void setRoomId(int roomId) { this.roomId = roomId; }

        public String getRoomName() { return roomName; }
        public void setRoomName(String roomName) { this.roomName = roomName; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        @Override
        public String toString() {
            return roomName + " - " + String.format("%,.0f VNĐ", price);
        }
    }

    public static class Customer {
        private int customerId;
        private String fullName;
        private String phone;

        // Getters and Setters
        public int getCustomerId() { return customerId; }
        public void setCustomerId(int customerId) { this.customerId = customerId; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        @Override
        public String toString() {
            return fullName + " - " + phone;
        }
    }
}