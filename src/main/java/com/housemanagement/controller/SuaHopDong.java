package com.housemanagement.controller;

import com.housemanagement.DatabaseHelper;
import com.housemanagement.model.Contract;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuaHopDong {

    /**
     * Cập nhật thông tin hợp đồng
     * @param contract Thông tin hợp đồng cần cập nhật
     * @throws SQLException Nếu có lỗi khi thực hiện truy vấn database
     * @throws IllegalArgumentException Nếu dữ liệu đầu vào không hợp lệ
     */
    public void updateContract(Contract contract) throws SQLException, IllegalArgumentException {
        // Validate dữ liệu đầu vào
        validateContractData(contract);

        // Kiểm tra hợp đồng có tồn tại không
        Contract existingContract = getContractById(contract.getContractId());
        if (existingContract == null) {
            throw new IllegalArgumentException("Hợp đồng không tồn tại!");
        }

        // Kiểm tra nếu thay đổi phòng, phòng mới có đang được thuê không (trừ chính phòng này)
        if (contract.getRoomId() != existingContract.getRoomId()) {
            if (isRoomOccupiedByOtherContract(contract.getRoomId(), contract.getContractId())) {
                throw new IllegalArgumentException("Phòng này đã được thuê bởi hợp đồng khác!");
            }
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

            // Cập nhật thông tin hợp đồng
            String sql = "UPDATE contracts SET customer_id = ?, room_id = ?, start_date = ?, " +
                    "end_date = ?, deposit = ?, note = ?, status = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE contract_id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, contract.getCustomerId());
            stmt.setInt(2, contract.getRoomId());
            stmt.setDate(3, contract.getStartDate());
            stmt.setDate(4, contract.getEndDate());
            stmt.setDouble(5, contract.getDeposit() != null ? contract.getDeposit() : 0.0);
            stmt.setString(6, contract.getNote());
            stmt.setString(7, contract.getStatus());
            stmt.setInt(8, contract.getContractId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không thể cập nhật hợp đồng, hợp đồng không tồn tại!");
            }

            // Xử lý thay đổi phòng
            if (contract.getRoomId() != existingContract.getRoomId()) {
                // Cập nhật phòng cũ thành "Còn trống" (nếu hợp đồng đang active)
                if ("active".equals(existingContract.getStatus())) {
                    updateRoomStatus(conn, existingContract.getRoomId(), "Còn trống");
                }

                // Cập nhật phòng mới thành "Đã thuê" (nếu hợp đồng đang active)
                if ("active".equals(contract.getStatus())) {
                    updateRoomStatus(conn, contract.getRoomId(), "Đã thuê");
                }
            } else {
                // Nếu không thay đổi phòng nhưng có thay đổi status
                if (!contract.getStatus().equals(existingContract.getStatus())) {
                    if ("active".equals(contract.getStatus())) {
                        updateRoomStatus(conn, contract.getRoomId(), "Đã thuê");
                    } else if ("active".equals(existingContract.getStatus()) &&
                            ("terminated".equals(contract.getStatus()) || "expired".equals(contract.getStatus()))) {
                        updateRoomStatus(conn, contract.getRoomId(), "Còn trống");
                    }
                }
            }

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
     * Lấy thông tin hợp đồng theo ID
     */
    public Contract getContractById(int contractId) throws SQLException {
        String sql = "SELECT c.*, cu.full_name, r.room_name, r.rent " +
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

    /**
     * Validate dữ liệu hợp đồng
     */
    private void validateContractData(Contract contract) throws IllegalArgumentException {
        if (contract == null) {
            throw new IllegalArgumentException("Thông tin hợp đồng không được để trống!");
        }

        if (contract.getContractId() <= 0) {
            throw new IllegalArgumentException("ID hợp đồng không hợp lệ!");
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

        // Kiểm tra status hợp lệ
        if (contract.getStatus() == null || contract.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái hợp đồng không được để trống!");
        }

        String[] validStatuses = {"active", "expired", "terminated"};
        boolean validStatus = false;
        for (String status : validStatuses) {
            if (status.equals(contract.getStatus())) {
                validStatus = true;
                break;
            }
        }
        if (!validStatus) {
            throw new IllegalArgumentException("Trạng thái hợp đồng không hợp lệ!");
        }
    }

    /**
     * Kiểm tra phòng có đang được thuê bởi hợp đồng khác không
     */
    private boolean isRoomOccupiedByOtherContract(int roomId, int excludeContractId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM contracts WHERE room_id = ? AND status = 'active' AND contract_id != ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.setInt(2, excludeContractId);
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
     * Lấy danh sách tất cả phòng (bao gồm cả phòng đang thuê) để sử dụng khi sửa
     */
    public List<QLyHopDong.Room> getAllRooms() throws SQLException {
        List<QLyHopDong.Room> rooms = new ArrayList<>();
        String sql = "SELECT room_id, room_name, rent as price FROM rooms ORDER BY room_name";

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
     * Lấy danh sách khách hàng
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

    /**
     * Map ResultSet thành Contract object
     */
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

    /**
     * Kết thúc hợp đồng sớm
     */
    public void terminateContract(int contractId, Date endDate) throws SQLException, IllegalArgumentException {
        Contract contract = getContractById(contractId);
        if (contract == null) {
            throw new IllegalArgumentException("Hợp đồng không tồn tại!");
        }

        if (!"active".equals(contract.getStatus())) {
            throw new IllegalArgumentException("Chỉ có thể kết thúc hợp đồng đang hoạt động!");
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false);

            // Cập nhật trạng thái hợp đồng
            String sql = "UPDATE contracts SET status = 'terminated', end_date = ?, updated_at = CURRENT_TIMESTAMP WHERE contract_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, endDate != null ? new java.sql.Date(endDate.getTime()) : new java.sql.Date(System.currentTimeMillis()));
            stmt.setInt(2, contractId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không thể kết thúc hợp đồng!");
            }

            // Cập nhật phòng thành "Còn trống"
            updateRoomStatus(conn, contract.getRoomId(), "Còn trống");

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw e;
        } finally {
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
}