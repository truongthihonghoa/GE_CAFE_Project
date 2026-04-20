package com.demo.ltud_n10.domain.model;

import java.io.Serializable;

public class WorkShift implements Serializable {
    private String id;
    private String employeeId;
    private String employeeName;
    private String date; // dd/MM/yyyy
    private String startTime; // HH:mm
    private String endTime; // HH:mm
    private String position; // Phục vụ, Pha chế, Giữ xe
    private boolean isSent;
    private String status; // Chờ duyệt, Đã duyệt, Bị từ chối
    private String type; // Đăng ký ca, Nghỉ phép
    private String sentTime; // HH:mm:ss dd/MM/yyyy

    public WorkShift() {
        this.status = "Chờ duyệt";
        this.type = "Đăng ký ca";
    }

    public WorkShift(String id, String employeeId, String employeeName, String date, String startTime, String endTime, String position, boolean isSent, String status, String type) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.position = position;
        this.isSent = isSent;
        this.status = status;
        this.type = type;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public boolean isSent() { return isSent; }
    public void setSent(boolean sent) { isSent = sent; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSentTime() { return sentTime; }
    public void setSentTime(String sentTime) { this.sentTime = sentTime; }
}
