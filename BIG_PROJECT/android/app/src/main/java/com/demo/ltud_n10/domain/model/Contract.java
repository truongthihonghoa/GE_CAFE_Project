package com.demo.ltud_n10.domain.model;

import java.io.Serializable;

public class Contract implements Serializable {
    private String id;
    private String employeeId;
    private String employeeName;
    private String type; // Part-time, Full-time, v.v.
    private String startDate; // dd/MM/yyyy
    private String endDate; // dd/MM/yyyy
    private double salary;
    private String position;
    private String status; // Hiệu lực, Chờ hiệu lực, Hết hạn

    public Contract() {}

    public Contract(String id, String employeeId, String employeeName, String type, String startDate, String endDate, double salary, String position, String status) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.salary = salary;
        this.position = position;
        this.status = status;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
