package com.demo.ltud_n10.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

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

    @SerializedName("ho_ten_nv")
    private String hoTenNv;

    @SerializedName("chi_tiet_nhan_vien")
    private List<ScheduleDetailDto> chiTietNhanVien;

    public static class ScheduleDetailDto {
        @SerializedName("ma_nv")
        private String maNv;

        @SerializedName("vi_tri")
        private String viTri;

        public ScheduleDetailDto(String maNv, String viTri) {
            this.maNv = maNv;
            this.viTri = viTri;
        }

        public String getMaNv() { return maNv; }
        public void setMaNv(String maNv) { this.maNv = maNv; }
        public String getViTri() { return viTri; }
        public void setViTri(String viTri) { this.viTri = viTri; }
    }

    // Getters and Setters
    public String getMaLlv() { return maLlv; }
    public void setMaLlv(String maLlv) { this.maLlv = maLlv; }
    
    public String getHoTenNv() { return hoTenNv; }
    public void setHoTenNv(String hoTenNv) { this.hoTenNv = hoTenNv; }

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

    public List<ScheduleDetailDto> getChiTietNhanVien() { return chiTietNhanVien; }
    public void setChiTietNhanVien(List<ScheduleDetailDto> chiTietNhanVien) { this.chiTietNhanVien = chiTietNhanVien; }
}
