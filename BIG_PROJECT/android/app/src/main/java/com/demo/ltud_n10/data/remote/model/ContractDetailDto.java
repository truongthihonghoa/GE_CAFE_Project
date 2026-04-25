package com.demo.ltud_n10.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class ContractDetailDto {
    @SerializedName("ma_hd")
    private String maHd;

    @SerializedName("luong_co_ban")
    private double luongCoBan;

    @SerializedName("luong_theo_gio")
    private double luongTheoGio;

    @SerializedName("so_gio_lam")
    private double soGioLam;

    @SerializedName("che_do_thuong")
    private double cheDoThuong;

    @SerializedName("dieu_khoan")
    private String dieuKhoan;

    @SerializedName("trach_nhiem")
    private String trachNhiem;

    @SerializedName("ghi_chu")
    private String ghiChu;

    // Getters and Setters
    public String getMaHd() { return maHd; }
    public void setMaHd(String maHd) { this.maHd = maHd; }

    public double getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(double luongCoBan) { this.luongCoBan = luongCoBan; }

    public double getLuongTheoGio() { return luongTheoGio; }
    public void setLuongTheoGio(double luongTheoGio) { this.luongTheoGio = luongTheoGio; }

    public double getSoGioLam() { return soGioLam; }
    public void setSoGioLam(double soGioLam) { this.soGioLam = soGioLam; }

    public double getCheDoThuong() { return cheDoThuong; }
    public void setCheDoThuong(double cheDoThuong) { this.cheDoThuong = cheDoThuong; }

    public String getDieuKhoan() { return dieuKhoan; }
    public void setDieuKhoan(String dieuKhoan) { this.dieuKhoan = dieuKhoan; }

    public String getTrachNhiem() { return trachNhiem; }
    public void setTrachNhiem(String trachNhiem) { this.trachNhiem = trachNhiem; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
