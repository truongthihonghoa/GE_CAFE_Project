package com.demo.ltud_n10.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class RequestDto {
    @SerializedName("ma_yc")
    private String maYc;

    @SerializedName("loai_yeu_cau")
    private String loaiYeuCau;

    @SerializedName("ngay_bd")
    private String ngayBd;

    @SerializedName("ngay_kt")
    private String ngayKt;

    @SerializedName("ly_do")
    private String lyDo;

    @SerializedName("trang_thai")
    private String trangThai;

    @SerializedName("ma_nv")
    private String maNv;

    // Getters and Setters
    public String getMaYc() { return maYc; }
    public void setMaYc(String maYc) { this.maYc = maYc; }

    public String getLoaiYeuCau() { return loaiYeuCau; }
    public void setLoaiYeuCau(String loaiYeuCau) { this.loaiYeuCau = loaiYeuCau; }

    public String getNgayBd() { return ngayBd; }
    public void setNgayBd(String ngayBd) { this.ngayBd = ngayBd; }

    public String getNgayKt() { return ngayKt; }
    public void setNgayKt(String ngayKt) { this.ngayKt = ngayKt; }

    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
}
