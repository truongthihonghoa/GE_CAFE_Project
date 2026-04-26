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

    @SerializedName("ho_ten") // Lấy tên nhân viên từ join bảng nếu có
    private String employeeName;

    @SerializedName("ma_nv")
    private String employeeId;

    @SerializedName("chuc_vu")
    private String position;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getWorkDate() { return workDate; }
    public void setWorkDate(String workDate) { this.workDate = workDate; }
    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    // Các hàm bổ trợ để Repository gọi không bị lỗi
    public String getDate() { return workDate; }
    public String getStartTime() {
        if (shift != null && shift.contains("-")) return shift.split("-")[0].trim();
        return shift;
    }
    public String getEndTime() {
        if (shift != null && shift.contains("-")) return shift.split("-")[1].trim();
        return "";
    }
}
