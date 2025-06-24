package com.housemanagement.dao;

import com.housemanagement.DatabaseHelper;
import com.housemanagement.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    // Sử dụng status tiếng Việt để nhất quán với giao diện
    public static final String STATUS_AVAILABLE = "Còn trống";
    public static final String STATUS_UNAVAILABLE = "Đã cho thuê";

    // Thêm phòng mới
    public void addRoom(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (room_name, rent, max, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, room.getRoomName());
            stmt.setDouble(2, room.getRent());
            stmt.setInt(3, room.getMax());
            // Đảm bảo status luôn có giá trị mặc định
            String status = room.getStatus();
            if (status == null || status.trim().isEmpty()) {
                status = STATUS_AVAILABLE;
            }
            stmt.setString(4, status);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        room.setRoomId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    // Cập nhật phòng
    public void updateRoom(Room room) throws SQLException {
        String sql = "UPDATE rooms SET room_name = ?, rent = ?, max = ?, status = ? WHERE room_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, room.getRoomName());
            stmt.setDouble(2, room.getRent());
            stmt.setInt(3, room.getMax());
            // Đảm bảo status có giá trị hợp lệ
            String status = room.getStatus();
            if (status == null || status.trim().isEmpty()) {
                status = STATUS_AVAILABLE;
            }
            stmt.setString(4, status);
            stmt.setInt(5, room.getRoomId());

            stmt.executeUpdate();
        }
    }

    // Xóa phòng
    public void deleteRoom(int roomId) throws SQLException {
        String sql = "DELETE FROM rooms WHERE room_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.executeUpdate();
        }
    }

    // Lấy tất cả phòng
    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_id, room_name, rent, max, status FROM rooms ORDER BY room_id";

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomName(rs.getString("room_name"));
                room.setRent(rs.getDouble("rent"));
                room.setMax(rs.getInt("max"));
                // Sử dụng setStatus để chuẩn hóa status
                room.setStatus(rs.getString("status"));
                rooms.add(room);
            }
        }

        return rooms;
    }

    // Lấy phòng theo ID
    public Room getRoomById(int roomId) throws SQLException {
        String sql = "SELECT room_id, room_name, rent, max, status FROM rooms WHERE room_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Room room = new Room();
                    room.setRoomId(rs.getInt("room_id"));
                    room.setRoomName(rs.getString("room_name"));
                    room.setRent(rs.getDouble("rent"));
                    room.setMax(rs.getInt("max"));
                    room.setStatus(rs.getString("status"));
                    return room;
                }
            }
        }
        return null;
    }

    // Đếm số phòng đã cho thuê
    public int getRentedRoomsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM rooms WHERE status = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, STATUS_UNAVAILABLE);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // Đếm số phòng còn trống
    public int getVacantRoomsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM rooms WHERE status = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, STATUS_AVAILABLE);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // Lấy danh sách phòng còn trống
    public List<Room> getVacantRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_id, room_name, rent, max, status FROM rooms WHERE status = ? ORDER BY room_id";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, STATUS_AVAILABLE);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setRoomId(rs.getInt("room_id"));
                    room.setRoomName(rs.getString("room_name"));
                    room.setRent(rs.getDouble("rent"));
                    room.setMax(rs.getInt("max"));
                    room.setStatus(rs.getString("status"));
                    rooms.add(room);
                }
            }
        }

        return rooms;
    }

    // Lấy danh sách phòng đã cho thuê
    public List<Room> getRentedRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_id, room_name, rent, max, status FROM rooms WHERE status = ? ORDER BY room_id";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, STATUS_UNAVAILABLE);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setRoomId(rs.getInt("room_id"));
                    room.setRoomName(rs.getString("room_name"));
                    room.setRent(rs.getDouble("rent"));
                    room.setMax(rs.getInt("max"));
                    room.setStatus(rs.getString("status"));
                    rooms.add(room);
                }
            }
        }

        return rooms;
    }

    // Cập nhật trạng thái phòng
    public void updateRoomStatus(int roomId, String status) throws SQLException {
        String sql = "UPDATE rooms SET status = ? WHERE room_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Chuẩn hóa status
            String normalizedStatus = STATUS_AVAILABLE; // Mặc định
            if (STATUS_UNAVAILABLE.equals(status) || "Đã cho thuê".equals(status) || "unavailable".equalsIgnoreCase(status)) {
                normalizedStatus = STATUS_UNAVAILABLE;
            }

            stmt.setString(1, normalizedStatus);
            stmt.setInt(2, roomId);
            stmt.executeUpdate();
        }
    }

    public boolean roomExists(int roomId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rooms WHERE room_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Kiểm tra tên phòng có trùng không (trừ phòng hiện tại)
    public boolean isRoomNameExists(String roomName, int excludeRoomId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rooms WHERE LOWER(room_name) = LOWER(?) AND room_id != ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roomName);
            stmt.setInt(2, excludeRoomId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}