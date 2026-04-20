package com.demo.ltud_n10.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkShift implements Serializable {
    private String id;
    private List<EmployeeAssignment> employeeAssignments = new ArrayList<>();
    private String employeeName; // Display string
    private String date; // dd/MM/yyyy
    private String startTime; // HH:mm
    private String endTime; // HH:mm
    private String position; // Primary position or note
    private boolean isSent;
    private String status; // Chờ duyệt, Đã duyệt, Bị từ chối
    private String type; // Đăng ký ca, Nghỉ phép

    public static class EmployeeAssignment implements Serializable {
        private String employeeId;
        private String employeeName;
        private String position;

        public EmployeeAssignment(String employeeId, String employeeName, String position) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.position = position;
        }

        public String getEmployeeId() { return employeeId; }
        public String getEmployeeName() { return employeeName; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
    }

    public WorkShift() {
        this.status = "Chờ duyệt";
        this.type = "Đăng ký ca";
    }

    public WorkShift(String id, List<EmployeeAssignment> employeeAssignments, String employeeName, String date, String startTime, String endTime, String position, boolean isSent, String status, String type) {
        this.id = id;
        this.employeeAssignments = employeeAssignments;
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
    public List<EmployeeAssignment> getEmployeeAssignments() { return employeeAssignments; }
    public void setEmployeeAssignments(List<EmployeeAssignment> employeeAssignments) { this.employeeAssignments = employeeAssignments; }
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
}
