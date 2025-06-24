package com.housemanagement.controller;

import com.housemanagement.DatabaseHelper;
import com.housemanagement.model.Contract;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaoHopDong {

    /**
     * Tạo hợp đồng mới
     * @param contract Thông tin hợp đồng cần tạo
     * @throws SQLException Nếu có lỗi khi thực hiện truy vấn database
     * @throws IllegalArgumentException Nếu dữ liệu đầu vào không hợp lệ
     */
    public void createContract(Contract contract) throws SQLException, IllegalArgumentException {
        // Validate dữ liệu đầu vào
        validateContractData(contract);

        // Kiểm tra phòng có đang được thuê không
        if (isRoomOccupied(contract.getRoomId())) {
            throw new IllegalArgumentException("Phòng này đã được thuê bởi hợp đồng khác!");
        }

        // Kiểm tra khách hàng có tồn tại không
        if (!isCustomerExists(contract.getCustomerId())) {
            throw new IllegalArgumentException("Khách hàng không tồn tại!");
        }

        // Kiểm tra phòng có tồn tại không
        if (!isRoomExists(contract.getRoomId())) {
            throw new IllegalArgumentException("Phòng không tồn tại!");
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Insert hợp đồng mới
            String sql = "INSERT INTO contracts (customer_id, room_id, start_date, end_date, deposit, note, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, contract.getCustomerId());
            stmt.setInt(2, contract.getRoomId());
            stmt.setDate(3, contract.getStartDate());
            stmt.setDate(4, contract.getEndDate());
            stmt.setDouble(5, contract.getDeposit() != null ? contract.getDeposit() : 0.0);
            stmt.setString(6, contract.getNote());
            stmt.setString(7, contract.getStatus() != null ? contract.getStatus() : "active");

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không thể tạo hợp đồng, không có dòng nào được thêm!");
            }

            // Lấy ID của hợp đồng vừa tạo
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                contract.setContractId(rs.getInt(1));
            } else {
                throw new SQLException("Không thể lấy ID của hợp đồng vừa tạo!");
            }

            // Cập nhật trạng thái phòng thành "Đã thuê"
            updateRoomStatus(conn, contract.getRoomId(), "Đã thuê");

            // Commit transaction
            conn.commit();

        } catch (SQLException e) {
            // Rollback nếu có lỗi
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw e;
        } finally {
            // Đóng resources
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Validate dữ liệu hợp đồng
     */
    private void validateContractData(Contract contract) throws IllegalArgumentException {
        if (contract == null) {
            throw new IllegalArgumentException("Thông tin hợp đồng không được để trống!");
        }

        if (contract.getCustomerId() <= 0) {
            throw new IllegalArgumentException("ID khách hàng không hợp lệ!");
        }

        if (contract.getRoomId() <= 0) {
            throw new IllegalArgumentException("ID phòng không hợp lệ!");
        }

        if (contract.getStartDate() == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được để trống!");
        }

        // Kiểm tra ngày kết thúc phải sau ngày bắt đầu (nếu có)
        if (contract.getEndDate() != null && contract.getEndDate().before(contract.getStartDate())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu!");
        }

        // Kiểm tra tiền cọc không được âm
        if (contract.getDeposit() != null && contract.getDeposit() < 0) {
            throw new IllegalArgumentException("Tiền cọc không được âm!");
        }
    }

    /**
     * Kiểm tra phòng có đang được thuê không
     */
    private boolean isRoomOccupied(int roomId) throws SQLException {
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

    /**
     * Kiểm tra khách hàng có tồn tại không
     */
    private boolean isCustomerExists(int customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * Kiểm tra phòng có tồn tại không
     */
    private boolean isRoomExists(int roomId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rooms WHERE room_id = ?";

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

    /**
     * Cập nhật trạng thái phòng
     */
    private void updateRoomStatus(Connection conn, int roomId, String status) throws SQLException {
        String sql = "UPDATE rooms SET status = ? WHERE room_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, roomId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không thể cập nhật trạng thái phòng!");
            }
        }
    }

    /**
     * Lấy thông tin phòng để hiển thị trong form
     */
    public List<QLyHopDong.Room> getAvailableRooms() throws SQLException {
        List<QLyHopDong.Room> rooms = new ArrayList<>();
        String sql = "SELECT room_id, room_name, rent as price FROM rooms WHERE status = 'Còn trống' ORDER BY room_name";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                QLyHopDong.Room room = new QLyHopDong.Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomName(rs.getString("room_name"));
                room.setPrice(rs.getDouble("price"));
                rooms.add(room);
            }
        }
        return rooms;
    }

    /**
     * Lấy danh sách khách hàng để hiển thị trong form
     */
    public List<QLyHopDong.Customer> getAllCustomers() throws SQLException {
        List<QLyHopDong.Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, full_name, phone FROM customers ORDER BY full_name";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                QLyHopDong.Customer customer = new QLyHopDong.Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setFullName(rs.getString("full_name"));
                customer.setPhone(rs.getString("phone"));
                customers.add(customer);
            }
        }
        return customers;
    }

    public double getRoomPrice(int roomId) throws SQLException {
        String sql = "SELECT rent FROM rooms WHERE room_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("rent");
            }
        }
        return 0.0;
    }
}