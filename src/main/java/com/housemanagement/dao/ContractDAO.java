package com.housemanagement.dao;

import com.housemanagement.DatabaseHelper;
import com.housemanagement.model.Contract;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractDAO {
    public List<Contract> findByCustomerId(int customerId) throws SQLException {
        List<Contract> list = new ArrayList<>();
        String sql = "SELECT * FROM contracts WHERE customer_id = ?";

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
                    contract.setDeposit(rs.getDouble("deposit"));
                    contract.setStatus(rs.getString("status"));
                    contract.setCreated(rs.getTimestamp("created_at"));
                    list.add(contract);
                }
            }
        }

        return list;
    }

    public List<Contract> findActiveContracts() throws SQLException {
        List<Contract> list = new ArrayList<>();
        String sql = "SELECT * FROM contracts WHERE status = 'active'";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Contract contract = new Contract();
                contract.setContractId(rs.getInt("contract_id"));
                contract.setCustomerId(rs.getInt("customer_id"));
                contract.setRoomId(rs.getInt("room_id"));
                contract.setStartDate(rs.getDate("start_date"));
                contract.setEndDate(rs.getDate("end_date"));
                contract.setDeposit(rs.getDouble("deposit"));
                contract.setStatus(rs.getString("status"));
                contract.setCreated(rs.getTimestamp("created_at"));
                list.add(contract);
            }
        }

        return list;
    }
}
