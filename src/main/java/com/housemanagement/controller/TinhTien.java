package com.housemanagement.controller;

import com.housemanagement.DatabaseHelper;
import com.housemanagement.model.Bill;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TinhTien {

    // Lấy danh sách tất cả hóa đơn
    public List<Bill> getAllBills() throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT b.*, c.room_id, r.room_name, cu.full_name " +
                "FROM bills b " +
                "LEFT JOIN contracts c ON b.contract_id = c.contract_id " +
                "LEFT JOIN rooms r ON c.room_id = r.room_id " +
                "LEFT JOIN customers cu ON c.customer_id = cu.customer_id " +
                "ORDER BY b.bill_id DESC";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Bill bill = new Bill();
                bill.setBillId(rs.getInt("bill_id"));
                bill.setContractId(rs.getInt("contract_id"));
                bill.setMonth(rs.getDate("month"));
                bill.setOldE(rs.getInt("old_e"));
                bill.setNewE(rs.getInt("new_e"));
                bill.setElectricity(rs.getBigDecimal("electricity"));
                bill.setWater(rs.getBigDecimal("water"));
                bill.setTotal(rs.getBigDecimal("total"));

                // Thông tin bổ sung
                bill.setRoomName(rs.getString("room_name"));
                bill.setCustomerName(rs.getString("full_name"));

                bills.add(bill);
            }
        }
        return bills;
    }

    // Lấy hóa đơn theo ID
    public Bill getBillById(int billId) throws SQLException {
        String sql = "SELECT b.*, c.room_id, r.room_name, cu.full_name FROM bills b " +
                "LEFT JOIN contracts c ON b.contract_id = c.contract_id " +
                "LEFT JOIN rooms r ON c.room_id = r.room_id " +
                "LEFT JOIN customers cu ON c.customer_id = cu.customer_id " +
                "WHERE b.bill_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, billId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Bill bill = new Bill();
                bill.setBillId(rs.getInt("bill_id"));
                bill.setContractId(rs.getInt("contract_id"));
                bill.setMonth(rs.getDate("month"));
                bill.setOldE(rs.getInt("old_e"));
                bill.setNewE(rs.getInt("new_e"));
                bill.setElectricity(rs.getBigDecimal("electricity"));
                bill.setWater(rs.getBigDecimal("water"));
                bill.setTotal(rs.getBigDecimal("total"));
                bill.setRoomName(rs.getString("room_name"));
                bill.setCustomerName(rs.getString("full_name"));
                return bill;
            }
        }
        return null;
    }

    // Lấy danh sách hợp đồng đang hoạt động
    public List<Contract> getActiveContracts() throws SQLException {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT c.contract_id, c.room_id, c.customer_id, " +
                "r.room_name, r.price as room_price, cu.full_name " +
                "FROM contracts c " +
                "JOIN rooms r ON c.room_id = r.room_id " +
                "JOIN customers cu ON c.customer_id = cu.customer_id " +
                "WHERE c.status = 'active' OR c.status = 'Đang thuê' " +
                "ORDER BY r.room_name";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Contract contract = new Contract();
                contract.setContractId(rs.getInt("contract_id"));
                contract.setRoomId(rs.getInt("room_id"));
                contract.setCustomerId(rs.getInt("customer_id"));
                contract.setRoomName(rs.getString("room_name"));
                contract.setRoomPrice(rs.getBigDecimal("room_price"));
                contract.setCustomerName(rs.getString("full_name"));
                contracts.add(contract);
            }
        }
        return contracts;
    }

    // Lấy tổng giá dịch vụ theo hợp đồng
    public BigDecimal getTotalServicePrice(int contractId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(cs.price), 0) as total_service " +
                "FROM contract_services cs " +
                "WHERE cs.contract_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contractId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("total_service");
            }
        }
        return BigDecimal.ZERO;
    }

    // Tính tổng tiền hóa đơn
    public BigDecimal calculateTotalAmount(BigDecimal electricityUsed, BigDecimal electricityPrice,
                                           BigDecimal roomPrice, BigDecimal servicePrice) {
        BigDecimal electricityAmount = electricityUsed.multiply(electricityPrice);
        return roomPrice.add(servicePrice).add(electricityAmount);
    }

    // Kiểm tra hóa đơn đã tồn tại theo tháng và hợp đồng
    public Bill getBillByMonthAndContract(String month, int contractId) throws SQLException {
        String sql = "SELECT * FROM bills WHERE contract_id = ? AND month = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contractId);
            // Chuyển đổi từ MM/yyyy sang yyyy-MM-01
            String[] parts = month.split("/");
            String sqlDate = parts[1] + "-" + parts[0] + "-01";
            stmt.setDate(2, Date.valueOf(sqlDate));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Bill bill = new Bill();
                bill.setBillId(rs.getInt("bill_id"));
                bill.setContractId(rs.getInt("contract_id"));
                bill.setMonth(rs.getDate("month"));
                bill.setOldE(rs.getInt("old_e"));
                bill.setNewE(rs.getInt("new_e"));
                bill.setElectricity(rs.getBigDecimal("electricity"));
                bill.setWater(rs.getBigDecimal("water"));
                bill.setTotal(rs.getBigDecimal("total"));
                return bill;
            }
        }
        return null;
    }

    // Tạo hóa đơn mới - Updated to work with UI
    public void createBill(Bill bill) throws SQLException {
        String sql = "INSERT INTO bills (contract_id, month, old_e, new_e, electricity, water, total) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, bill.getContractId());
            stmt.setDate(2, bill.getMonth());
            stmt.setInt(3, bill.getOldE());
            stmt.setInt(4, bill.getNewE());
            stmt.setBigDecimal(5, bill.getElectricity());
            stmt.setBigDecimal(6, bill.getWater());
            stmt.setBigDecimal(7, bill.getTotal());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                bill.setBillId(rs.getInt(1));
            }
        }
    }

    // Cập nhật hóa đơn
    public void updateBill(Bill bill) throws SQLException {
        String sql = "UPDATE bills SET contract_id = ?, month = ?, old_e = ?, new_e = ?, " +
                "electricity = ?, water = ?, total = ? WHERE bill_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bill.getContractId());
            stmt.setDate(2, bill.getMonth());
            stmt.setInt(3, bill.getOldE());
            stmt.setInt(4, bill.getNewE());
            stmt.setBigDecimal(5, bill.getElectricity());
            stmt.setBigDecimal(6, bill.getWater());
            stmt.setBigDecimal(7, bill.getTotal());
            stmt.setInt(8, bill.getBillId());

            stmt.executeUpdate();
        }
    }

    // Xóa hóa đơn
    public void deleteBill(int billId) throws SQLException {
        String sql = "DELETE FROM bills WHERE bill_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, billId);
            stmt.executeUpdate();
        }
    }

    // Inner class để lưu thông tin hợp đồng
    public static class Contract {
        private int contractId;
        private int roomId;
        private int customerId;
        private String roomName;
        private BigDecimal roomPrice;
        private String customerName;

        public int getContractId() { return contractId; }
        public void setContractId(int contractId) { this.contractId = contractId; }

        public int getRoomId() { return roomId; }
        public void setRoomId(int roomId) { this.roomId = roomId; }

        public int getCustomerId() { return customerId; }
        public void setCustomerId(int customerId) { this.customerId = customerId; }

        public String getRoomName() { return roomName; }
        public void setRoomName(String roomName) { this.roomName = roomName; }

        public BigDecimal getRoomPrice() { return roomPrice; }
        public void setRoomPrice(BigDecimal roomPrice) { this.roomPrice = roomPrice; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
    }
}