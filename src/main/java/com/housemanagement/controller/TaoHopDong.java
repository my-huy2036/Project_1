package com.housemanagement.controller;

import com.housemanagement.model.Contract;
import com.housemanagement.model.Customer;
import com.housemanagement.model.Room;

import java.sql.SQLException;
import java.util.List;

public class TaoHopDong {
    private final QLyHopDong qlyHopDong;

    public TaoHopDong() {
        this.qlyHopDong = new QLyHopDong();
    }

    public List<Customer> getAllCustomers() throws Exception {
        try {
            return qlyHopDong.getAllCustomers();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy danh sách khách hàng: " + e.getMessage(), e);
        }
    }

    public List<Room> getAvailableRooms() throws Exception {
        try {
            return qlyHopDong.getAvailableRooms();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy danh sách phòng trống: " + e.getMessage(), e);
        }
    }

    // Create contract method that uses QLyHopDong
    public void createContract(Contract contract) throws Exception {
        try {
            // Validate contract data before creating
            validateContract(contract);

            // Use QLyHopDong to create the contract
            qlyHopDong.createContract(contract);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi tạo hợp đồng: " + e.getMessage(), e);
        }
    }

    // Additional validation method for contract creation
    private void validateContract(Contract contract) throws Exception {
        if (contract == null) {
            throw new Exception("Thông tin hợp đồng không được để trống!");
        }

        if (contract.getCustomerId() <= 0) {
            throw new Exception("Vui lòng chọn khách hàng!");
        }

        if (contract.getRoomId() <= 0) {
            throw new Exception("Vui lòng chọn phòng!");
        }

        if (contract.getStartDate() == null) {
            throw new Exception("Ngày bắt đầu không được để trống!");
        }

        if (contract.getDeposit() < 0) {
            throw new Exception("Tiền cọc không được âm!");
        }

        // Check if end date is after start date (if provided)
        if (contract.getEndDate() != null && contract.getEndDate().before(contract.getStartDate())) {
            throw new Exception("Ngày kết thúc phải sau ngày bắt đầu!");
        }
    }

    // Get contract by ID (delegating to QLyHopDong)
    public Contract getContractById(int contractId) throws Exception {
        try {
            return qlyHopDong.getContractById(contractId);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy thông tin hợp đồng: " + e.getMessage(), e);
        }
    }

    // Get all contracts (delegating to QLyHopDong)
    public List<Contract> getAllContracts() throws Exception {
        try {
            return qlyHopDong.getAllContracts();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy danh sách hợp đồng: " + e.getMessage(), e);
        }
    }
}