package com.demo.ltud_n10.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class ScheduleDto {
    @SerializedName("ma_llv")
    private String maLlv;

    @SerializedName("ngay_lam")
    private String ngayLam;

    @SerializedName("ca_lam")
    private String caLam;

    @SerializedName("trang_thai")
    private String trangThai;

    @SerializedName("ngay_tao")
    private String ngayTao;

    @SerializedName("ghi_chu")
    private String ghiChu;

    @SerializedName("ma_chi_nhanh")
    private String maChiNhanh;

    @SerializedName("ma_nv")
    private String maNv;

    // Getters and Setters
    public String getMaLlv() { return maLlv; }
    public void setMaLlv(String maLlv) { this.maLlv = maLlv; }

    public String getNgayLam() { return ngayLam; }
    public void setNgayLam(String ngayLam) { this.ngayLam = ngayLam; }

    public String getCaLam() { return caLam; }
    public void setCaLam(String caLam) { this.caLam = caLam; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getNgayTao() { return ngayTao; }
    public void setNgayTao(String ngayTao) { this.ngayTao = ngayTao; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getMaChiNhanh() { return maChiNhanh; }
    public void setMaChiNhanh(String maChiNhanh) { this.maChiNhanh = maChiNhanh; }

    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
}
