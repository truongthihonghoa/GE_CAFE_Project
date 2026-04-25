package com.demo.ltud_n10.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class AttendanceDto {
    @SerializedName("ma_cc")
    private String maCc;

    @SerializedName("ma_nv")
    private String maNv;

    @SerializedName("ngay_lam")
    private String ngayLam;

    @SerializedName("gio_vao")
    private String gioVao;

    @SerializedName("gio_ra")
    private String gioRa;

    @SerializedName("so_gio_lam")
    private double soGioLam;

    @SerializedName("trang_thai")
    private String trangThai;

    @SerializedName("ghi_chu")
    private String ghiChu;

    // Getters and Setters
    public String getMaCc() { return maCc; }
    public void setMaCc(String maCc) { this.maCc = maCc; }

    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }

    public String getNgayLam() { return ngayLam; }
    public void setNgayLam(String ngayLam) { this.ngayLam = ngayLam; }

    public String getGioVao() { return gioVao; }
    public void setGioVao(String gioVao) { this.gioVao = gioVao; }

    public String getGioRa() { return gioRa; }
    public void setGioRa(String gioRa) { this.gioRa = gioRa; }

    public double getSoGioLam() { return soGioLam; }
    public void setSoGioLam(double soGioLam) { this.soGioLam = soGioLam; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
