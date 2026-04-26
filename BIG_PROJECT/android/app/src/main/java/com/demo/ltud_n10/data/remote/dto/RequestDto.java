package com.demo.ltud_n10.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class RequestDto implements Serializable {
    @SerializedName("ma_yc")
    private String ma_yc;

    @SerializedName("loai_yeu_cau")
    private String loai_yeu_cau;

    @SerializedName("ngay_bd")
    private String ngay_bd;

    @SerializedName("ngay_kt")
    private String ngay_kt;

    @SerializedName("ly_do")
    private String ly_do;

    @SerializedName("trang_thai")
    private String trang_thai;

    @SerializedName("ma_nv_id") // Đổi sang ma_nv_id để khớp chính xác với Database Supabase
    private String ma_nv_id;

    // Getters and Setters
    public String getMa_yc() { return ma_yc; }
    public void setMa_yc(String ma_yc) { this.ma_yc = ma_yc; }

    public String getLoai_yeu_cau() { return loai_yeu_cau; }
    public void setLoai_yeu_cau(String loai_yeu_cau) { this.loai_yeu_cau = loai_yeu_cau; }

    public String getNgay_bd() { return ngay_bd; }
    public void setNgay_bd(String ngay_bd) { this.ngay_bd = ngay_bd; }

    public String getNgay_kt() { return ngay_kt; }
    public void setNgay_kt(String ngay_kt) { this.ngay_kt = ngay_kt; }

    public String getLy_do() { return ly_do; }
    public void setLy_do(String ly_do) { this.ly_do = ly_do; }

    public String getTrang_thai() { return trang_thai; }
    public void setTrang_thai(String trang_thai) { this.trang_thai = trang_thai; }

    public String getMa_nv_id() { return ma_nv_id; }
    public void setMa_nv_id(String ma_nv_id) { this.ma_nv_id = ma_nv_id; }

    // Alias methods for compatibility with UI code
    public String getId() { return ma_yc; }
    public void setId(String id) { this.ma_yc = id; }
    public String getType() { return loai_yeu_cau; }
    public void setType(String type) { this.loai_yeu_cau = type; }
    public String getStartDate() { return ngay_bd; }
    public void setStartDate(String startDate) { this.ngay_bd = startDate; }
    public String getEndDate() { return ngay_kt; }
    public void setEndDate(String endDate) { this.ngay_kt = endDate; }
    public String getReason() { return ly_do; }
    public void setReason(String reason) { this.ly_do = reason; }
    public String getStatus() { return trang_thai; }
    public void setStatus(String status) { this.trang_thai = status; }
    public String getEmployeeId() { return ma_nv_id; }
    public void setEmployeeId(String employeeId) { this.ma_nv_id = employeeId; }
}
