package com.housemanagement.dao;

import com.housemanagement.model.Room;
import com.housemanagement.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    public static final double DEFAULT_ELECTRICITY_PRICE = 3500.0;

    public void addRoom(Room room) throws SQLException {
        String sql = "SELECT room_id, room_name, rent, electricity, previousElectricity, service_fee FROM rooms";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, room.getRoomName());
            stmt.setDouble(2, room.getRent());
            stmt.setDouble(3, room.getElectricity());
            stmt.setDouble(4, room.getServiceFee());
            stmt.executeUpdate();
        }
    }

    public void updateRoom(Room room) throws SQLException {
        String sql = "UPDATE rooms SET room_name=?, rent=?, electricity=?, previousElectricity=?, service_fee=? WHERE room_id=?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, room.getRoomName());
            pstmt.setDouble(2, room.getRent());
            pstmt.setDouble(3, room.getElectricity());
            pstmt.setDouble(4, room.getPreviousElectricity());
            pstmt.setDouble(5, room.getServiceFee());
            pstmt.setInt(6, room.getRoomId());

            pstmt.executeUpdate();
        }
    }

    public void deleteRoom(int roomId) throws SQLException {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.executeUpdate();
        }
    }

    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_id, room_name, rent, electricity, previousElectricity, service_fee FROM rooms";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomName(rs.getString("room_name"));
                room.setRent(rs.getDouble("rent"));
                room.setElectricity(rs.getDouble("electricity"));
                room.setPreviousElectricity(rs.getDouble("previousElectricity"));
                room.setServiceFee(rs.getDouble("service_fee"));
                rooms.add(room);
            }
        }
        return rooms;
    }

    public double calculateBill(Room room) {
        return room.getRent() +
                (room.getElectricity() * DEFAULT_ELECTRICITY_PRICE) +
                room.getServiceFee();
    }
}
