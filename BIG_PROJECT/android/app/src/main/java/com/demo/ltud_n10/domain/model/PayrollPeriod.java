package com.demo.ltud_n10.domain.model;

import java.io.Serializable;
import java.util.List;

public class PayrollPeriod implements Serializable {
    private String id;
    private String month;
    private String year;
    private int employeeCount;
    private String employeeId;
    private String employeeName;
    private String createdAt;
    private double totalAmount;
    private String status; // "Đang chờ duyệt", "Đã duyệt", "Từ chối"
    private String approvedAt;
    private String note;

    public PayrollPeriod() {}

    public PayrollPeriod(String id, String month, String year, int employeeCount, String createdAt, double totalAmount, String status) {
        this.id = id;
        this.month = month;
        this.year = year;
        this.employeeCount = employeeCount;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public int getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(int employeeCount) { this.employeeCount = employeeCount; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getApprovedAt() { return approvedAt; }
    public void setApprovedAt(String approvedAt) { this.approvedAt = approvedAt; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
