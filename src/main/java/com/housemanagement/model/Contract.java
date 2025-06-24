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

    // Extra fields từ JOIN
    private String customerName;
    private String roomName;
    private double roomRent;

    // Constructors
    public Contract() {
        this.deposit = 0.0;
        this.status = "pending";
        this.created = new Date();
    }

    public Contract(int customerId, int roomId, Date startDate, Date endDate, double deposit, String note, String status) {
        this.customerId = customerId;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deposit = deposit;
        this.note = note;
        this.status = status;
        this.created = new Date();
    }

    // Getters & Setters
    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    // Extra fields
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public double getRoomRent() {
        return roomRent;
    }

    public void setRoomRent(double roomRent) {
        this.roomRent = roomRent;
    }

    @Override
    public String toString() {
        return String.format("Contract{id=%d, customer='%s', room='%s', deposit=%.2f, status='%s'}",
                contractId, customerName, roomName, deposit, status);
    }

    public java.sql.Date getSqlStartDate() {
        return startDate != null ? new java.sql.Date(startDate.getTime()) : null;
    }

    public java.sql.Date getSqlEndDate() {
        return endDate != null ? new java.sql.Date(endDate.getTime()) : null;
    }

}
