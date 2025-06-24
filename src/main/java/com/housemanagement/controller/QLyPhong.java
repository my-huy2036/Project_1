package com.housemanagement.controller;

import com.housemanagement.dao.RoomDAO;
import com.housemanagement.model.Room;

import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QLyPhong {
    private RoomDAO roomDAO;

    public QLyPhong(RoomDAO roomDAO) {
        this.roomDAO = roomDAO;
    }

    public boolean processAddRoom(String roomName, String rentStr, String maxStr) {
        if (roomName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tên phòng không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (rentStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Đơn giá là bắt buộc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            double rent = Double.parseDouble(rentStr);
            int max = maxStr.trim().isEmpty() ? 0 : Integer.parseInt(maxStr);

            if (rent < 0 || max < 0) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập giá trị số không âm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Room newRoom = new Room();
            newRoom.setRoomName(roomName);
            newRoom.setRent(rent);
            newRoom.setMax(max);
            newRoom.setStatus("available");

            roomDAO.addRoom(newRoom);
            return true;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số hợp lệ cho các trường số.", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Lỗi khi lưu phòng: " + ex.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        }
    }

    public List<Room> getAllRooms() {
        try {
            return roomDAO.getAllRooms();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải danh sách phòng: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }

    public int getVacantRoomsCount() {
        try {
            return roomDAO.getVacantRoomsCount();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Room> getVacantRooms() {
        try {
            return roomDAO.getVacantRooms();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean deleteRoomById(int roomId, String roomName) {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Bạn có chắc chắn muốn xóa phòng " + roomName + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                roomDAO.deleteRoom(roomId);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi xóa phòng: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return false;
    }

    public boolean processUpdateRoom(Room roomToUpdate) {
        try {
            // Thêm logic kiểm tra nghiệp vụ nếu cần
            roomDAO.updateRoom(roomToUpdate);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật phòng: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
