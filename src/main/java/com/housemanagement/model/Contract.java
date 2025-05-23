package com.housemanagement.model;

import java.util.Date;

public class Contract {
    private int contractId;
    private int customerId;
    private int roomId;
    private Date startDate;
    private Date endDate;

    public Contract() {}

    public Contract(int customerId, int roomId, Date startDate, Date endDate) {
        this.customerId = customerId;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getContractId() { return contractId; }
    public void setContractId(int contractId) { this.contractId = contractId; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
}
