package com.demo.ltud_n10.domain.model;

import java.io.Serializable;

public class Employee implements Serializable {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String cccd;
    private String gender; // "Nam", "Nữ"
    private String dob; // Date of birth YYYY-MM-DD
    private String address;
    private String position; // "Quản lý", "Pha chế", "Phục vụ", v.v.
    private String status; // "Đang làm", "Ngừng hoạt động"

    // New fields required by API
    private String bankAccount;
    private String branchId;

    public Employee() {}

    public Employee(String id, String name, String email, String phone, String cccd, String gender, String dob, String address, String position, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cccd = cccd;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.position = position;
        this.status = status;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }
}