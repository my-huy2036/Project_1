package com.housemanagement.controller;

import com.housemanagement.model.Contract;
import com.housemanagement.model.Customer;
import com.housemanagement.model.Room;

import java.sql.SQLException;
import java.util.List;
import java.util.Date;

public class TaoHopDong {
    private final QLyHopDong qlyHopDong;

    public TaoHopDong() {
        this.qlyHopDong = new QLyHopDong();
    }

    /**
     * Get all customers for ComboBox
     */
    public List<Customer> getAllCustomers() throws Exception {
        try {
            List<Customer> customers = qlyHopDong.getAllCustomers();
            if (customers == null || customers.isEmpty()) {
                throw new Exception("Không có khách hàng nào trong hệ thống!");
            }
            return customers;
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy danh sách khách hàng: " + e.getMessage(), e);
        }
    }

    /**
     * Get available rooms for new contract
     */
    public List<Room> getAvailableRooms() throws Exception {
        try {
            List<Room> rooms = qlyHopDong.getAvailableRooms();
            if (rooms == null || rooms.isEmpty()) {
                throw new Exception("Không có phòng trống nào!");
            }
            return rooms;
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy danh sách phòng trống: " + e.getMessage(), e);
        }
    }

    /**
     * Get all rooms (for editing existing contracts)
     */
    public List<Room> getAllRooms() throws Exception {
        try {
            return qlyHopDong.getAllRooms();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy danh sách phòng: " + e.getMessage(), e);
        }
    }

    /**
     * Create new contract with enhanced validation
     */
    public void createContract(Contract contract) throws Exception {
        try {
            // Enhanced validation
            validateContract(contract);

            // Check room availability
            if (!qlyHopDong.isRoomAvailable(contract.getRoomId())) {
                throw new Exception("Phòng đã được thuê, vui lòng chọn phòng khác!");
            }

            // Create the contract
            qlyHopDong.createContract(contract);

        } catch (SQLException e) {
            throw new Exception("Lỗi khi tạo hợp đồng: " + e.getMessage(), e);
        }
    }

    /**
     * Enhanced validation method for contract creation
     */
    private void validateContract(Contract contract) throws Exception {
        if (contract == null) {
            throw new Exception("Thông tin hợp đồng không được để trống!");
        }

        // Validate customer
        if (contract.getCustomerId() <= 0) {
            throw new Exception("Vui lòng chọn khách hàng!");
        }

        // Validate room
        if (contract.getRoomId() <= 0) {
            throw new Exception("Vui lòng chọn phòng!");
        }

        // Validate start date
        if (contract.getStartDate() == null) {
            throw new Exception("Ngày bắt đầu không được để trống!");
        }

        // Validate end date
        if (contract.getEndDate() == null) {
            throw new Exception("Ngày kết thúc không được để trống!");
        }

        // Check if end date is after start date
        if (contract.getEndDate().before(contract.getStartDate())) {
            throw new Exception("Ngày kết thúc phải sau ngày bắt đầu!");
        }

        // Check if start date is not too far in the past
        Date now = new Date();
        long daysDiff = (now.getTime() - contract.getStartDate().getTime()) / (1000 * 60 * 60 * 24);
        if (daysDiff > 30) {
            throw new Exception("Ngày bắt đầu không được quá 30 ngày trước ngày hiện tại!");
        }
    }

    public void updateContract(Contract contract) throws Exception {
        try {
            validateContract(contract);
            qlyHopDong.updateContract(contract);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi cập nhật hợp đồng: " + e.getMessage(), e);
        }
    }

    /**
     * Get contract by ID
     */
    public Contract getContractById(int contractId) throws Exception {
        try {
            Contract contract = qlyHopDong.getContractById(contractId);
            if (contract == null) {
                throw new Exception("Không tìm thấy hợp đồng với ID: " + contractId);
            }
            return contract;
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy thông tin hợp đồng: " + e.getMessage(), e);
        }
    }

    /**
     * Get all contracts
     */
    public List<Contract> getAllContracts() throws Exception {
        try {
            return qlyHopDong.getAllContracts();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy danh sách hợp đồng: " + e.getMessage(), e);
        }
    }

    /**
     * Check if room is available
     */
    public boolean isRoomAvailable(int roomId) throws Exception {
        try {
            return qlyHopDong.isRoomAvailable(roomId);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi kiểm tra phòng: " + e.getMessage(), e);
        }
    }

    /**
     * Get customer by ID
     */
    public Customer getCustomerById(int customerId) throws Exception {
        try {
            List<Customer> customers = qlyHopDong.getAllCustomers();
            for (Customer customer : customers) {
                if (customer.getCustomerId() == customerId) {
                    return customer;
                }
            }
            throw new Exception("Không tìm thấy khách hàng với ID: " + customerId);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy thông tin khách hàng: " + e.getMessage(), e);
        }
    }

    /**
     * Get room by ID
     */
    public Room getRoomById(int roomId) throws Exception {
        try {
            List<Room> rooms = qlyHopDong.getAllRooms();
            for (Room room : rooms) {
                if (room.getRoomId() == roomId) {
                    return room;
                }
            }
            throw new Exception("Không tìm thấy phòng với ID: " + roomId);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy thông tin phòng: " + e.getMessage(), e);
        }
    }

    /**
     * Validate contract dates
     */
    public void validateContractDates(Date startDate, Date endDate) throws Exception {
        if (startDate == null || endDate == null) {
            throw new Exception("Ngày bắt đầu và ngày kết thúc không được để trống!");
        }

        if (endDate.before(startDate)) {
            throw new Exception("Ngày kết thúc phải sau ngày bắt đầu!");
        }

        // Check minimum contract duration (e.g., at least 1 month)
        long diffInMillis = endDate.getTime() - startDate.getTime();
        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

        if (diffInDays < 30) {
            throw new Exception("Thời hạn hợp đồng phải ít nhất 30 ngày!");
        }

        // Check maximum contract duration (e.g., no more than 5 years)
        if (diffInDays > 365 * 5) {
            throw new Exception("Thời hạn hợp đồng không được quá 5 năm!");
        }
    }
}