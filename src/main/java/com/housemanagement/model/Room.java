package com.housemanagement.model;

public class Room {
    private int roomId;  // thêm trường này
    private String roomName;
    private double rent;
    private double electricity;
    private double previousElectricity;
    private double serviceFee;

    // Constructor không tham số
    public Room() {
    }

    // Constructor có roomId
    public Room(int roomId, String roomName, double rent, double electricity, double previousElectricity, double serviceFee) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.rent = rent;
        this.electricity = electricity;
        this.previousElectricity = previousElectricity;
        this.serviceFee = serviceFee;
    }

    // Getter và Setter cho roomId
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    // Getter Setter còn lại giữ nguyên
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

    public double getElectricity() {
        return electricity;
    }

    public void setElectricity(double electricity) {
        this.electricity = electricity;
    }

    public double getPreviousElectricity() {
        return previousElectricity;
    }

    public void setPreviousElectricity(double previousElectricity) {
        this.previousElectricity = previousElectricity;
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }
}
