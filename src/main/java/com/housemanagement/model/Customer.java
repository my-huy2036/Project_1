package com.housemanagement.model;

public class Customer {
    private int customerId;
    private String fullName;
    private String gender;     // 'M' hoặc 'F'
    private String phone;
    private String email;
    private String address;
    private String identity;   // CCCD

    public Customer() {}

    public Customer(String fullName, String gender, String phone, String email, String address, String identity) {
        this.fullName = fullName;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.identity = identity;
    }

    // Getters và Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getIdentity() { return identity; }
    public void setIdentity(String identity) { this.identity = identity; }

    // Phương thức helper
    public String getGenderDisplay() {
        return "M".equals(gender) ? "Nam" : "Nữ";
    }

    @Override
    public String toString() {
        return fullName + " - " + phone;
    }
}