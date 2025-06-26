package com.housemanagement.controller;

import com.housemanagement.controller.QLyPhong;
import com.housemanagement.model.Contract;
import com.housemanagement.model.Customer;
import com.housemanagement.model.Room;
import com.housemanagement.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QLyHopDong {

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM customers";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Customer customer = new Customer();
            customer.setCustomerId(rs.getInt("customer_id"));
            customer.setFullName(rs.getString("fullname"));
            customer.setGender(rs.getString("gender"));
            customer.setPhone(rs.getString("phone"));
            customer.setEmail(rs.getString("email"));
            customer.setAddress(rs.getString("address"));
            customer.setIdentity(rs.getString("identity"));
            list.add(customer);
        }
        return list;
    }

    public List<Room> getAvailableRooms() throws SQLException {
        List<Room> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        // Lấy các phòng chưa có hợp đồng hoặc hợp đồng đã kết thúc
        String sql = "SELECT r.* FROM rooms r " +
                "LEFT JOIN contracts c ON r.room_id = c.room_id " +
                "AND (c.end_date IS NULL OR c.end_date >= CURDATE()) " +
                "WHERE c.room_id IS NULL";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Room room = new Room();
            room.setRoomId(rs.getInt("room_id"));
            room.setRoomName(rs.getString("room_name"));
            room.setRent(rs.getDouble("rent"));
            list.add(room);
        }
        return list;
    }

    public List<Room> getAllRooms() throws SQLException {
        List<Room> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM rooms";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Room room = new Room();
            room.setRoomId(rs.getInt("room_id"));
            room.setRoomName(rs.getString("room_name"));
            room.setRent(rs.getDouble("rent"));
            list.add(room);
        }
        return list;
    }

    public List<Contract> getAllContracts() throws SQLException {
        List<Contract> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT c.*, cu.fullname AS customer_name, r.room_name, r.rent AS room_rent " +
                "FROM contracts c " +
                "JOIN customers cu ON c.customer_id = cu.customer_id " +
                "JOIN rooms r ON c.room_id = r.room_id " +
                "ORDER BY c.created DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Contract contract = new Contract();
            contract.setContractId(rs.getInt("contract_id"));
            contract.setCustomerId(rs.getInt("customer_id"));
            contract.setRoomId(rs.getInt("room_id"));
            contract.setStartDate(rs.getDate("start_date"));
            contract.setEndDate(rs.getDate("end_date"));
            contract.setCreated(rs.getDate("created"));
            contract.setCustomerName(rs.getString("customer_name"));
            contract.setRoomName(rs.getString("room_name"));
            contract.setRoomRent(rs.getDouble("room_rent"));
            list.add(contract);
        }
        return list;
    }

    public List<Contract> getActiveContracts() throws SQLException {
        List<Contract> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT c.*, cu.fullname AS customer_name, r.room_name, r.rent AS room_rent " +
                "FROM contracts c " +
                "JOIN customers cu ON c.customer_id = cu.customer_id " +
                "JOIN rooms r ON c.room_id = r.room_id " +
                "WHERE c.start_date <= CURDATE() AND (c.end_date IS NULL OR c.end_date >= CURDATE()) " +
                "ORDER BY c.created DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Contract contract = new Contract();
            contract.setContractId(rs.getInt("contract_id"));
            contract.setCustomerId(rs.getInt("customer_id"));
            contract.setRoomId(rs.getInt("room_id"));
            contract.setStartDate(rs.getDate("start_date"));
            contract.setEndDate(rs.getDate("end_date"));
            contract.setCreated(rs.getDate("created"));
            contract.setCustomerName(rs.getString("customer_name"));
            contract.setRoomName(rs.getString("room_name"));
            contract.setRoomRent(rs.getDouble("room_rent"));
            list.add(contract);
        }
        return list;
    }

    public Contract getContractById(int contractId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT c.*, cu.fullname AS customer_name, r.room_name, r.rent AS room_rent " +
                "FROM contracts c " +
                "JOIN customers cu ON c.customer_id = cu.customer_id " +
                "JOIN rooms r ON c.room_id = r.room_id " +
                "WHERE c.contract_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, contractId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Contract contract = new Contract();
            contract.setContractId(rs.getInt("contract_id"));
            contract.setCustomerId(rs.getInt("customer_id"));
            contract.setRoomId(rs.getInt("room_id"));
            contract.setStartDate(rs.getDate("start_date"));
            contract.setEndDate(rs.getDate("end_date"));
            contract.setCreated(rs.getDate("created"));
            contract.setCustomerName(rs.getString("customer_name"));
            contract.setRoomName(rs.getString("room_name"));
            contract.setRoomRent(rs.getDouble("room_rent"));
            return contract;
        }
        return null;
    }

    public void createContract(Contract contract) throws SQLException {
        Connection conn = DBConnection.getConnection();

        // Kiểm tra xem phòng đã có hợp đồng chưa kết thúc hay không
        String checkSql = "SELECT COUNT(*) FROM contracts WHERE room_id = ? " +
                "AND (end_date IS NULL OR end_date >= CURDATE())";
        PreparedStatement checkPs = conn.prepareStatement(checkSql);
        checkPs.setInt(1, contract.getRoomId());
        ResultSet checkRs = checkPs.executeQuery();

        if (checkRs.next() && checkRs.getInt(1) > 0) {
            throw new SQLException("Phòng này đã có hợp đồng đang hiệu lực!");
        }

        // Tạo hợp đồng mới
        String sql = "INSERT INTO contracts (customer_id, room_id, start_date, end_date, created) " +
                "VALUES (?, ?, ?, ?, NOW())";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, contract.getCustomerId());
        ps.setInt(2, contract.getRoomId());
        ps.setDate(3, new java.sql.Date(contract.getStartDate().getTime()));
        if (contract.getEndDate() != null) {
            ps.setDate(4, new java.sql.Date(contract.getEndDate().getTime()));
        } else {
            ps.setNull(4, Types.DATE);
        }
        ps.executeUpdate();
    }

    public void updateContract(Contract contract) throws SQLException {
        Connection conn = DBConnection.getConnection();

        // Kiểm tra xem phòng mới đã có hợp đồng khác chưa kết thúc hay không (nếu thay đổi phòng)
        String checkSql = "SELECT COUNT(*) FROM contracts WHERE room_id = ? " +
                "AND contract_id != ? " +
                "AND (end_date IS NULL OR end_date >= CURDATE())";
        PreparedStatement checkPs = conn.prepareStatement(checkSql);
        checkPs.setInt(1, contract.getRoomId());
        checkPs.setInt(2, contract.getContractId());
        ResultSet checkRs = checkPs.executeQuery();

        if (checkRs.next() && checkRs.getInt(1) > 0) {
            throw new SQLException("Phòng này đã có hợp đồng đang hiệu lực!");
        }

        String sql = "UPDATE contracts SET customer_id = ?, room_id = ?, start_date = ?, " +
                "end_date = ? WHERE contract_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, contract.getCustomerId());
        ps.setInt(2, contract.getRoomId());
        ps.setDate(3, new java.sql.Date(contract.getStartDate().getTime()));
        if (contract.getEndDate() != null) {
            ps.setDate(4, new java.sql.Date(contract.getEndDate().getTime()));
        } else {
            ps.setNull(4, Types.DATE);
        }
        ps.setInt(5, contract.getContractId());
        ps.executeUpdate();
    }

    public void deleteContract(int contractId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "DELETE FROM contracts WHERE contract_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, contractId);
        ps.executeUpdate();
    }

    public void terminateContract(int contractId, Date terminatedDate) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE contracts SET end_date = ? WHERE contract_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setDate(1, new java.sql.Date(terminatedDate.getTime()));
        ps.setInt(2, contractId);
        ps.executeUpdate();
    }

    public boolean isRoomAvailable(int roomId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT COUNT(*) FROM contracts WHERE room_id = ? " +
                "AND (end_date IS NULL OR end_date >= CURDATE())";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, roomId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt(1) == 0;
        }
        return true;
    }

    public List<Contract> getExpiringSoonContracts(int days) throws SQLException {
        List<Contract> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT c.*, cu.fullname AS customer_name, r.room_name, r.rent AS room_rent " +
                "FROM contracts c " +
                "JOIN customers cu ON c.customer_id = cu.customer_id " +
                "JOIN rooms r ON c.room_id = r.room_id " +
                "WHERE c.end_date IS NOT NULL " +
                "AND c.end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                "ORDER BY c.end_date ASC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, days);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Contract contract = new Contract();
            contract.setContractId(rs.getInt("contract_id"));
            contract.setCustomerId(rs.getInt("customer_id"));
            contract.setRoomId(rs.getInt("room_id"));
            contract.setStartDate(rs.getDate("start_date"));
            contract.setEndDate(rs.getDate("end_date"));
            contract.setCreated(rs.getDate("created"));
            contract.setCustomerName(rs.getString("customer_name"));
            contract.setRoomName(rs.getString("room_name"));
            contract.setRoomRent(rs.getDouble("room_rent"));
            list.add(contract);
        }
        return list;
    }
}