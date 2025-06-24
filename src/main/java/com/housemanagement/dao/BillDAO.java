package com.housemanagement.dao;

import com.housemanagement.model.Bill;
import com.housemanagement.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    // Thêm một bill mới
    public void insertBill(Bill bill) {
        String sql = "INSERT INTO bills (contract_id, month, old_e, new_e, electricity, water, total) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, bill.getContractId(), Types.INTEGER); // chấp nhận null
            stmt.setDate(2, bill.getMonth());
            stmt.setInt(3, bill.getOldE());
            stmt.setInt(4, bill.getNewE());
            stmt.setBigDecimal(5, bill.getElectricity());
            stmt.setBigDecimal(6, bill.getWater());
            stmt.setBigDecimal(7, bill.getTotal());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy danh sách tất cả bills
    public List<Bill> getAllBills() {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM bills";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Bill bill = new Bill();
                bill.setBillId(rs.getInt("bill_id"));
                bill.setContractId((Integer) rs.getObject("contract_id")); // có thể null
                bill.setMonth(rs.getDate("month"));
                bill.setOldE(rs.getInt("old_e"));
                bill.setNewE(rs.getInt("new_e"));
                bill.setElectricity(rs.getBigDecimal("electricity"));
                bill.setWater(rs.getBigDecimal("water"));
                bill.setTotal(rs.getBigDecimal("total"));
                list.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy một bill theo ID
    public Bill getBillById(int id) {
        Bill bill = null;
        String sql = "SELECT * FROM bills WHERE bill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    bill = new Bill();
                    bill.setBillId(rs.getInt("bill_id"));
                    bill.setContractId((Integer) rs.getObject("contract_id"));
                    bill.setMonth(rs.getDate("month"));
                    bill.setOldE(rs.getInt("old_e"));
                    bill.setNewE(rs.getInt("new_e"));
                    bill.setElectricity(rs.getBigDecimal("electricity"));
                    bill.setWater(rs.getBigDecimal("water"));
                    bill.setTotal(rs.getBigDecimal("total"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bill;
    }

    // Cập nhật bill
    public void updateBill(Bill bill) {
        String sql = "UPDATE bills SET contract_id = ?, month = ?, old_e = ?, new_e = ?, electricity = ?, water = ?, total = ? WHERE bill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, bill.getContractId(), Types.INTEGER);
            stmt.setDate(2, bill.getMonth());
            stmt.setInt(3, bill.getOldE());
            stmt.setInt(4, bill.getNewE());
            stmt.setBigDecimal(5, bill.getElectricity());
            stmt.setBigDecimal(6, bill.getWater());
            stmt.setBigDecimal(7, bill.getTotal());
            stmt.setInt(8, bill.getBillId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa bill
    public void deleteBill(int id) {
        String sql = "DELETE FROM bills WHERE bill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
