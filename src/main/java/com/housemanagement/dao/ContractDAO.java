package com.housemanagement.dao;

import com.housemanagement.DatabaseHelper;
import com.housemanagement.model.Contract;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractDAO {

    public void addContract(Contract contract) throws SQLException {
        String sql = "INSERT INTO contracts (customer_id, room_id, start_date, end_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contract.getCustomerId());
            stmt.setInt(2, contract.getRoomId());
            stmt.setDate(3, new java.sql.Date(contract.getStartDate().getTime()));
            stmt.setDate(4, new java.sql.Date(contract.getEndDate().getTime()));

            stmt.executeUpdate();
        }
    }

    public List<Contract> getContractsByCustomerId(int customerId) throws SQLException {
        String sql = "SELECT * FROM contracts WHERE customer_id = ?";
        List<Contract> contracts = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Contract contract = new Contract();
                    contract.setContractId(rs.getInt("contract_id"));
                    contract.setCustomerId(rs.getInt("customer_id"));
                    contract.setRoomId(rs.getInt("room_id"));
                    contract.setStartDate(rs.getDate("start_date"));
                    contract.setEndDate(rs.getDate("end_date"));

                    contracts.add(contract);
                }
            }
        }
        return contracts;
    }

    // Bạn có thể thêm các hàm update, delete, getAll... tùy theo nhu cầu
}
