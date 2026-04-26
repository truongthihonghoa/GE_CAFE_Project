package com.demo.ltud_n10.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ScheduleDto implements Serializable {
    @SerializedName("ma_llv")
    private String id;

    @SerializedName("ngay_lam")
    private String workDate;

    @SerializedName("ca_lam")
    private String shift;

    @SerializedName("trang_thai")
    private String status;

    @SerializedName("ma_nv")
    private String employeeId;

    @SerializedName("ten_nv")
    private String employeeName;

    @SerializedName("ma_chi_nhanh")
    private String branchId;

    @SerializedName("ghi_chu")
    private String note;

    @SerializedName("ngay_tao")
    private String createdAt;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getWorkDate() { return workDate; }
    public void setWorkDate(String workDate) { this.workDate = workDate; }
    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
