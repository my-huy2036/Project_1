package com.housemanagement.controller;

import com.housemanagement.dao.CustomerDAO;
import com.housemanagement.model.Customer;
import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QLyKhach {
    private CustomerDAO customerDAO;

    public QLyKhach() {
        this.customerDAO = new CustomerDAO();
    }

    // Lấy danh sách tất cả khách hàng
    public List<Customer> getAllCustomers() {
        try {
            return customerDAO.getAllCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Lỗi khi tải danh sách khách: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Thêm khách hàng mới
    public boolean addCustomer(Customer customer) {
        try {
            // Validate dữ liệu
            String validationError = validateCustomer(customer);
            if (validationError != null) {
                showError(validationError);
                return false;
            }

            // Kiểm tra CCCD đã tồn tại
            if (customerDAO.isIdentityExists(customer.getIdentity())) {
                showError("Số CCCD/CMND đã tồn tại trong hệ thống!");
                return false;
            }

            customerDAO.addCustomer(customer);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("identity")) {
                    showError("Số CCCD/CMND đã tồn tại!");
                } else if (e.getMessage().contains("email")) {
                    showError("Email đã được sử dụng!");
                } else {
                    showError("Thông tin đã tồn tại trong hệ thống!");
                }
            } else {
                showError("Lỗi khi thêm khách: " + e.getMessage());
            }
            return false;
        }
    }

    // Cập nhật thông tin khách hàng
    public boolean updateCustomer(Customer customer) {
        try {
            // Validate dữ liệu
            String validationError = validateCustomer(customer);
            if (validationError != null) {
                showError(validationError);
                return false;
            }

            // Kiểm tra CCCD đã tồn tại (ngoại trừ khách hiện tại)
            if (customerDAO.isIdentityExistsExceptCustomer(customer.getIdentity(), customer.getCustomerId())) {
                showError("Số CCCD/CMND đã được sử dụng bởi khách khác!");
                return false;
            }

            return customerDAO.updateCustomer(customer);

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Lỗi khi cập nhật khách: " + e.getMessage());
            return false;
        }
    }

    // Xóa khách hàng
    public boolean deleteCustomer(int customerId) {
        try {
            // Kiểm tra xem khách có hợp đồng đang hoạt động không
            if (hasActiveContract(customerId)) {
                showError("Không thể xóa khách đang có hợp đồng hoạt động!");
                return false;
            }

            int confirm = JOptionPane.showConfirmDialog(null,
                    "Bạn có chắc muốn xóa khách này?\nThao tác này không thể hoàn tác!",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                return customerDAO.deleteCustomer(customerId);
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Lỗi khi xóa khách: " + e.getMessage());
            return false;
        }
    }

    // Lấy khách hàng theo ID
    public Customer getCustomerById(int customerId) {
        try {
            return customerDAO.getCustomerById(customerId);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Lỗi khi tải thông tin khách: " + e.getMessage());
            return null;
        }
    }

    // Tìm kiếm khách hàng
    public List<Customer> searchCustomers(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllCustomers();
            }
            return customerDAO.searchCustomers(keyword.trim());
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Lỗi khi tìm kiếm: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Lọc khách theo giới tính
    public List<Customer> getCustomersByGender(String gender) {
        try {
            if (gender == null || gender.equals("Tất cả")) {
                return getAllCustomers();
            }
            // Chuyển đổi từ display sang database value
            String genderValue = gender.equals("Nam") ? "M" : "F";
            return customerDAO.getCustomersByGender(genderValue);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Lỗi khi lọc theo giới tính: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Validate thông tin khách hàng
    private String validateCustomer(Customer customer) {
        // Kiểm tra họ tên
        if (customer.getFullName() == null || customer.getFullName().trim().isEmpty()) {
            return "Vui lòng nhập họ tên!";
        }
        if (customer.getFullName().trim().length() < 3) {
            return "Họ tên phải có ít nhất 3 ký tự!";
        }

        // Kiểm tra CCCD
        if (customer.getIdentity() == null || customer.getIdentity().trim().isEmpty()) {
            return "Vui lòng nhập số CCCD/CMND!";
        }
        if (!customer.getIdentity().matches("\\d{9,12}")) {
            return "CCCD/CMND phải là số và có 9-12 chữ số!";
        }

        // Kiểm tra email
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            return "Vui lòng nhập email!";
        }
        if (!isValidEmail(customer.getEmail())) {
            return "Email không hợp lệ!";
        }

        // Kiểm tra số điện thoại (optional nhưng nếu có thì phải đúng format)
        if (customer.getPhone() != null && !customer.getPhone().trim().isEmpty()) {
            if (!customer.getPhone().matches("\\d{10,11}")) {
                return "Số điện thoại phải có 10-11 chữ số!";
            }
        }

        // Kiểm tra địa chỉ
        if (customer.getAddress() == null || customer.getAddress().trim().isEmpty()) {
            return "Vui lòng nhập địa chỉ!";
        }

        // Kiểm tra giới tính
        if (customer.getGender() == null ||
                (!customer.getGender().equals("M") && !customer.getGender().equals("F"))) {
            return "Vui lòng chọn giới tính!";
        }

        return null; // Validation passed
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private boolean hasActiveContract(int customerId) {

        return false;
    }

    public Customer prepareCustomerData(String fullName, String gender, String phone,
                                        String email, String address, String identity) {
        Customer customer = new Customer();
        customer.setFullName(fullName.trim());
        customer.setGender(gender); // "M" hoặc "F"
        customer.setPhone(phone != null ? phone.trim() : "");
        customer.setEmail(email.trim());
        customer.setAddress(address.trim());
        customer.setIdentity(identity.trim());
        return customer;
    }

    // Hiển thị thông báo lỗi
    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    // Hiển thị thông báo thành công
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    // Lấy tổng số khách hàng
    public int getTotalCustomers() {
        try {
            return getAllCustomers().size();
        } catch (Exception e) {
            return 0;
        }
    }

    // Lấy danh sách khách hàng mới nhất
    public List<Customer> getRecentCustomers(int limit) {
        List<Customer> allCustomers = getAllCustomers();
        if (allCustomers.size() <= limit) {
            return allCustomers;
        }
        // Lấy n khách cuối cùng (mới nhất)
        return allCustomers.subList(allCustomers.size() - limit, allCustomers.size());
    }
}