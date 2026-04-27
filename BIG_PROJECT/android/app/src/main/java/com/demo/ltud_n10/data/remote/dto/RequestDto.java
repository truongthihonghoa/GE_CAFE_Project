package com.demo.ltud_n10.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class RequestDto implements Serializable {
    @SerializedName("ma_yc")
    private String id;

    @SerializedName("loai_yeu_cau")
    private String type;

    @SerializedName("ngay_bd")
    private String startDate;

    @SerializedName("ngay_kt")
    private String endDate;

    @SerializedName("ly_do")
    private String reason;

    @SerializedName("trang_thai")
    private String status;

    @SerializedName("ma_nv")
    private String employeeId;

    @SerializedName("ten_nv")
    private String employeeName;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
}
