package com.demo.ltud_n10.domain.model;

import java.io.Serializable;

public class PayrollDetail implements Serializable {
    private String id;
    private String periodId;
    private String employeeId;
    private String employeeName;
    private String month;
    private String year;
    private double baseSalary;
    private double hourlyRate;
    private double hoursWorked;
    private double factor;
    private double overtimeHours;
    private double allowance;
    private double bonus;
    private double penalty;
    private double totalSalary;
    private String status; // "Đang chờ duyệt", "Đã duyệt", "Từ chối"
    private String note;

    public PayrollDetail() {}

    // Mock constructor
    public PayrollDetail(String id, String employeeId, String employeeName, double baseSalary, double bonus, double penalty, String status) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.baseSalary = baseSalary;
        this.bonus = bonus;
        this.penalty = penalty;
        this.status = status;
        this.totalSalary = baseSalary + bonus - penalty;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPeriodId() { return periodId; }
    public void setPeriodId(String periodId) { this.periodId = periodId; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    public double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(double hoursWorked) { this.hoursWorked = hoursWorked; }
    public double getFactor() { return factor; }
    public void setFactor(double factor) { this.factor = factor; }
    public double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(double overtimeHours) { this.overtimeHours = overtimeHours; }
    public double getAllowance() { return allowance; }
    public void setAllowance(double allowance) { this.allowance = allowance; }
    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { this.bonus = bonus; }
    public double getPenalty() { return penalty; }
    public void setPenalty(double penalty) { this.penalty = penalty; }
    public double getTotalSalary() { return totalSalary; }
    public void setTotalSalary(double totalSalary) { this.totalSalary = totalSalary; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
