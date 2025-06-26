package com.housemanagement.controller;

import com.housemanagement.DatabaseHelper;
import com.housemanagement.model.Contract;
import java.sql.*;

public class SuaHopDong {
    public boolean updateContract(Contract contract) throws SQLException {
        validateContract(contract);

        String sql = "UPDATE contracts SET customer_id=?, room_id=?, start_date=?, end_date=?, updated_at=CURRENT_TIMESTAMP WHERE contract_id=?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contract.getCustomerId());
            stmt.setInt(2, contract.getRoomId());
            stmt.setDate(3, new java.sql.Date(contract.getStartDate().getTime()));

            // Handle end date - can be null
            if (contract.getEndDate() != null) {
                stmt.setDate(4, new java.sql.Date(contract.getEndDate().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setString(5, contract.getStatus());
            stmt.setInt(6, contract.getContractId());

            return stmt.executeUpdate() > 0;
        }
    }

    private void validateContract(Contract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Hợp đồng không được null!");
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

        if (contract.getStatus() == null || contract.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái hợp đồng không được để trống!");
        }

        // Validate end date if provided
        if (contract.getEndDate() != null && contract.getEndDate().before(contract.getStartDate())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu!");
        }
    }
}