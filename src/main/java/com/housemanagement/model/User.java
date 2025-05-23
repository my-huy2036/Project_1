package com.housemanagement.model;

import java.util.Date; // Thêm import cho Date

public class User {
    private int userId;
    private String username;
    private String password;
    private String role;
    private Integer customerId;
    private Integer houseOwnerId;
    private String email;
    private String hometown;
    private Date dateOfBirth; // Trường mới: Ngày tháng năm sinh

    public User() {
    }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters và Setters cũ
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    public Integer getHouseOwnerId() {
        return houseOwnerId;
    }
    public void setHouseOwnerId(Integer houseOwnerId) {
        this.houseOwnerId = houseOwnerId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getHometown() {
        return hometown;
    }
    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    // Getter và Setter cho dateOfBirth
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
