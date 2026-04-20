package com.demo.ltud_n10.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class PayrollDto {
    @SerializedName("ma_luong")
    private String maLuong;

    @SerializedName("nhan_vien")
    private String maNv;

    @SerializedName("ten_nv")
    private String tenNv;

    @SerializedName("chi_nhanh")
    private String maChiNhanh;

    @SerializedName("thang")
    private int thang;

    @SerializedName("nam")
    private int nam;

    @SerializedName("trang_thai")
    private String trangThai;

    @SerializedName("luong_co_ban")
    private double luongCoBan;

    @SerializedName("luong_theo_gio")
    private double luongTheoGio;

    @SerializedName("so_ca_lam")
    private double soCaLam;

    @SerializedName("so_gio_lam")
    private double soGioLam;

    @SerializedName("thuong")
    private double thuong;

    @SerializedName("phat")
    private double phat;

    @SerializedName("tong_luong")
    private double tongLuong;

    // Getters and Setters
    public String getMaLuong() { return maLuong; }
    public void setMaLuong(String maLuong) { this.maLuong = maLuong; }
    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
    public String getTenNv() { return tenNv; }
    public void setTenNv(String tenNv) { this.tenNv = tenNv; }
    public String getMaChiNhanh() { return maChiNhanh; }
    public void setMaChiNhanh(String maChiNhanh) { this.maChiNhanh = maChiNhanh; }
    public int getThang() { return thang; }
    public void setThang(int thang) { this.thang = thang; }
    public int getNam() { return nam; }
    public void setNam(int nam) { this.nam = nam; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public double getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(double luongCoBan) { this.luongCoBan = luongCoBan; }
    public double getLuongTheoGio() { return luongTheoGio; }
    public void setLuongTheoGio(double luongTheoGio) { this.luongTheoGio = luongTheoGio; }
    public double getSoCaLam() { return soCaLam; }
    public void setSoCaLam(double soCaLam) { this.soCaLam = soCaLam; }
    public double getSoGioLam() { return soGioLam; }
    public void setSoGioLam(double soGioLam) { this.soGioLam = soGioLam; }
    public double getThuong() { return thuong; }
    public void setThuong(double thuong) { this.thuong = thuong; }
    public double getPhat() { return phat; }
    public void setPhat(double phat) { this.phat = phat; }
    public double getTongLuong() { return tongLuong; }
    public void setTongLuong(double tongLuong) { this.tongLuong = tongLuong; }
}
