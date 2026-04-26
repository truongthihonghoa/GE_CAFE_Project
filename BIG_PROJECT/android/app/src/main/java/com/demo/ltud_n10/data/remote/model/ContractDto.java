package com.demo.ltud_n10.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class ContractDto {
    @SerializedName("ma_hd")
    private String maHd;

    @SerializedName("du_lieu_luong")
    private ContractDetailDto duLieuLuong;

    @SerializedName("loai_hd")
    private String loaiHd;

    @SerializedName("chuc_vu")
    private String chucVu;

    @SerializedName("ngay_bat_dau")
    private String ngayBatDau;

    @SerializedName("ngay_ket_thuc")
    private String ngayKetThuc;

    @SerializedName("trang_thai")
    private String trangThai;

    @SerializedName("ma_nv")
    private String maNv;

    @SerializedName("ma_chi_nhanh")
    private String maChiNhanh;

    // Getters and Setters
    public String getMaHd() { return maHd; }
    public void setMaHd(String maHd) { this.maHd = maHd; }

    public ContractDetailDto getChiTiet() { return duLieuLuong; }
    public void setChiTiet(ContractDetailDto chiTiet) { 
        this.duLieuLuong = chiTiet;
    }

    public String getLoaiHd() { return loaiHd; }
    public void setLoaiHd(String loaiHd) { this.loaiHd = loaiHd; }

    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }

    public String getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(String ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public String getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(String ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }

    public String getMaChiNhanh() { return maChiNhanh; }
    public void setMaChiNhanh(String maChiNhanh) { this.maChiNhanh = maChiNhanh; }
}
