package com.housemanagement.model;

public class Customer {
    private int customerId;
    private String fullName;
    private String phone;
    private String email;       // thêm email
    private String hometown;    // thêm quê quán
    private String address;     // giữ lại nếu bạn muốn lưu địa chỉ cụ thể

    public Customer() {}

    public Customer(String fullName, String phone, String email, String hometown, String address) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.hometown = hometown;
        this.address = address;
    }

    // Getters và Setters đầy đủ
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getHometown() { return hometown; }
    public void setHometown(String hometown) { this.hometown = hometown; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
