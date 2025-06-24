package com.housemanagement.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Bill {
    private int billId;
    private Integer contractId;
    private Date month;
    private Integer oldE;
    private Integer newE;
    private BigDecimal electricity;
    private BigDecimal water;
    private BigDecimal total;

    // Thêm thông tin hiển thị
    private String roomName;        // từ bảng rooms
    private String customerName;    // từ bảng customers

    // Getters and Setters
    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public Integer getOldE() {
        return oldE;
    }

    public void setOldE(Integer oldE) {
        this.oldE = oldE;
    }

    public Integer getNewE() {
        return newE;
    }

    public void setNewE(Integer newE) {
        this.newE = newE;
    }

    public BigDecimal getElectricity() {
        return electricity;
    }

    public void setElectricity(BigDecimal electricity) {
        this.electricity = electricity;
    }

    public BigDecimal getWater() {
        return water;
    }

    public void setWater(BigDecimal water) {
        this.water = water;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
