package com.housemanagement.model;

public class Room {
    private int roomId;
    private int userId;  // Thêm field userId
    private String roomName;
    private double rent;
    private int max;
    private String status;

    // Constructor mặc định
    public Room() {
        this.status = "Còn trống"; // Giá trị mặc định
    }

    // Constructor đầy đủ
    public Room(int userId, String roomName, double rent, int max) {
        this.userId = userId;
        this.roomName = roomName;
        this.rent = rent;
        this.max = max;
        this.status = "Còn trống";
    }

    // Getters and Setters
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public double getRent() {
        return rent;
    }

    public void setRent(double rent) {
        this.rent = rent;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        // Chuẩn hóa status
        if (status == null || status.trim().isEmpty()) {
            this.status = "Còn trống";
        } else if ("available".equalsIgnoreCase(status) || "Còn trống".equals(status)) {
            this.status = "Còn trống";
        } else if ("unavailable".equalsIgnoreCase(status) || "Đã cho thuê".equals(status)) {
            this.status = "Đã cho thuê";
        } else {
            this.status = status;
        }
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", userId=" + userId +
                ", roomName='" + roomName + '\'' +
                ", rent=" + rent +
                ", max=" + max +
                ", status='" + status + '\'' +
                '}';
    }
}