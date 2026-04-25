package com.demo.ltud_n10.domain.model;

import java.io.Serializable;

public class Account implements Serializable {
    private String id;
    private String username;
    private String employeeName;
    private String employeeId;
    private String role;
    private String status;
    private boolean isStaff;

    public Account() {
    }

    public Account(String id, String username, String employeeName, String employeeId, String role, String status) {
        this.id = id;
        this.username = username;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
        this.role = role;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isStaff() { return isStaff; }
    public void setStaff(boolean staff) { isStaff = staff; }
}
