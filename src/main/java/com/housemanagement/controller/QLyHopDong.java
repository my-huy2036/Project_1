package com.housemanagement.controller;

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
            customer.setFullName(rs.getString("fullname")); // Fixed: setFullName instead of setFullname
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
        Connection conn = DBConnection.getConnection(); // Fixed: consistent connection usage
        String sql = "SELECT * FROM rooms WHERE status = 'available'";
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
        Connection conn = DBConnection.getConnection(); // Fixed: consistent connection usage
        String sql = "SELECT c.*, cu.fullname AS customer_name, r.room_name, r.rent AS room_rent FROM contracts c JOIN customers cu ON c.customer_id = cu.customer_id JOIN rooms r ON c.room_id = r.room_id";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Contract contract = new Contract();
            contract.setContractId(rs.getInt("contract_id"));
            contract.setCustomerId(rs.getInt("customer_id"));
            contract.setRoomId(rs.getInt("room_id"));
            contract.setStartDate(rs.getDate("start_date"));
            contract.setEndDate(rs.getDate("end_date"));
            contract.setDeposit(rs.getDouble("deposit"));
            contract.setNote(rs.getString("note"));
            contract.setStatus(rs.getString("status"));
            contract.setCreated(rs.getDate("created"));
            contract.setCustomerName(rs.getString("customer_name"));
            contract.setRoomName(rs.getString("room_name"));
            contract.setRoomRent(rs.getDouble("room_rent"));
            list.add(contract);
        }
        return list;
    }

    public Contract getContractById(int contractId) throws SQLException {
        Connection conn = DBConnection.getConnection(); // Fixed: consistent connection usage
        String sql = "SELECT c.*, cu.fullname AS customer_name, r.room_name, r.rent AS room_rent FROM contracts c JOIN customers cu ON c.customer_id = cu.customer_id JOIN rooms r ON c.room_id = r.room_id WHERE contract_id = ?";
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
            contract.setDeposit(rs.getDouble("deposit"));
            contract.setNote(rs.getString("note"));
            contract.setStatus(rs.getString("status"));
            contract.setCreated(rs.getDate("created"));
            contract.setCustomerName(rs.getString("customer_name"));
            contract.setRoomName(rs.getString("room_name"));
            contract.setRoomRent(rs.getDouble("room_rent"));
            return contract;
        }
        return null;
    }

    public void createContract(Contract contract) throws SQLException {
        Connection conn = DBConnection.getConnection(); // Fixed: consistent connection usage
        String sql = "INSERT INTO contracts (customer_id, room_id, start_date, end_date, deposit, note, status, created) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, contract.getCustomerId());
        ps.setInt(2, contract.getRoomId());
        ps.setDate(3, new java.sql.Date(contract.getStartDate().getTime()));
        if (contract.getEndDate() != null) {
            ps.setDate(4, new java.sql.Date(contract.getEndDate().getTime()));
        } else {
            ps.setNull(4, Types.DATE);
        }
        ps.setDouble(5, contract.getDeposit());
        ps.setString(6, contract.getNote());
        ps.setString(7, contract.getStatus());
        ps.executeUpdate();

        // Update room status
        PreparedStatement ps2 = conn.prepareStatement("UPDATE rooms SET status = 'occupied' WHERE room_id = ?");
        ps2.setInt(1, contract.getRoomId());
        ps2.executeUpdate();
    }

    public void updateContract(Contract contract) throws SQLException {
        Connection conn = DBConnection.getConnection(); // Fixed: consistent connection usage
        String sql = "UPDATE contracts SET customer_id = ?, room_id = ?, start_date = ?, end_date = ?, deposit = ?, note = ?, status = ? WHERE contract_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, contract.getCustomerId());
        ps.setInt(2, contract.getRoomId());
        ps.setDate(3, new java.sql.Date(contract.getStartDate().getTime()));
        if (contract.getEndDate() != null) {
            ps.setDate(4, new java.sql.Date(contract.getEndDate().getTime()));
        } else {
            ps.setNull(4, Types.DATE);
        }
        ps.setDouble(5, contract.getDeposit());
        ps.setString(6, contract.getNote());
        ps.setString(7, contract.getStatus());
        ps.setInt(8, contract.getContractId());
        ps.executeUpdate();
    }

    public void deleteContract(int contractId) throws SQLException {
        Connection conn = DBConnection.getConnection(); // Fixed: consistent connection usage
        Contract contract = getContractById(contractId);
        if (contract != null) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM contracts WHERE contract_id = ?");
            ps.setInt(1, contractId);
            ps.executeUpdate();

            // Cập nhật trạng thái phòng lại available
            PreparedStatement ps2 = conn.prepareStatement("UPDATE rooms SET status = 'available' WHERE room_id = ?");
            ps2.setInt(1, contract.getRoomId());
            ps2.executeUpdate();
        }
    }

    public void terminateContract(int contractId, Date terminatedDate) throws SQLException {
        Connection conn = DBConnection.getConnection(); // Fixed: consistent connection usage
        PreparedStatement ps = conn.prepareStatement("UPDATE contracts SET status = 'terminated', end_date = ? WHERE contract_id = ?");
        ps.setDate(1, new java.sql.Date(terminatedDate.getTime()));
        ps.setInt(2, contractId);
        ps.executeUpdate();

        // Cập nhật phòng về available
        Contract contract = getContractById(contractId);
        if (contract != null) {
            PreparedStatement ps2 = conn.prepareStatement("UPDATE rooms SET status = 'available' WHERE room_id = ?");
            ps2.setInt(1, contract.getRoomId());
            ps2.executeUpdate();
        }
    }
}