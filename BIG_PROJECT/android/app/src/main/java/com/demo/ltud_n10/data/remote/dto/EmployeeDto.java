package com.demo.ltud_n10.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class EmployeeDto {
    @SerializedName("ma_nv")
    private String maNv;

    @SerializedName("ho_ten")
    private String hoTen;

    @SerializedName("ngay_sinh")
    private String ngaySinh;

    @SerializedName("cccd")
    private String cccd;

    @SerializedName("sdt")
    private String sdt;

    @SerializedName("chuc_vu")
    private String chucVu;

    @SerializedName("dia_chi")
    private String diaChi;

    @SerializedName("gioi_tinh")
    private String gioiTinh;

    @SerializedName("ma_chi_nhanh")
    private String maChiNhanh;

    // Getters and Setters
    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public String getMaChiNhanh() { return maChiNhanh; }
    public void setMaChiNhanh(String maChiNhanh) { this.maChiNhanh = maChiNhanh; }
}
