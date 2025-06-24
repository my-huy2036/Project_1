package com.housemanagement.model;

import java.util.Date;

public class Contract {
    private int contractId;
    private int customerId;
    private int roomId;
    private Date startDate;
    private Date endDate;
    private Double deposit;
    private String note;
    private String status;
    private Date created;

    // Extra fields
    private String customerName;
    private String roomName;
    private double roomRent; // <-- Giá thuê lấy từ bảng rooms

    public Contract() {}

    public Contract(int customerId, int roomId, Date startDate, Date endDate) {
        this.customerId = customerId;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters & Setters
    public int getContractId() { return contractId; }
    public void setContractId(int contractId) { this.contractId = contractId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public java.sql.Date getStartDate() {
        return (startDate != null) ? new java.sql.Date(startDate.getTime()) : null;
    }

    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public java.sql.Date getEndDate() {
        return (endDate != null) ? new java.sql.Date(endDate.getTime()) : null;
    }

    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Double getDeposit() { return deposit; }
    public void setDeposit(Double deposit) { this.deposit = deposit; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreated() { return created; }
    public void setCreated(Date created) { this.created = created; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public double getRoomRent() { return roomRent; }
    public void setRoomRent(double roomRent) { this.roomRent = roomRent; }
}
