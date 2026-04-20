package com.demo.ltud_n10.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ContractDto {
    @SerializedName("ma_hd")
    private String maHd;

    @SerializedName("ma_nv")
    private String maNv;

    @SerializedName("ma_chi_nhanh")
    private String maChiNhanh;

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

    @SerializedName("chi_tiet")
    private DetailDto chiTiet;

    // Helper for UI display (from SerializerMethodField if added, or handled in Repository)
    private String hoTenNv;

    public static class DetailDto {
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

    // Getters and Setters for ContractDto
    public String getMaHd() { return maHd; }
    public void setMaHd(String maHd) { this.maHd = maHd; }
    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
    public String getMaChiNhanh() { return maChiNhanh; }
    public void setMaChiNhanh(String maChiNhanh) { this.maChiNhanh = maChiNhanh; }
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
    public DetailDto getChiTiet() { return chiTiet; }
    public void setChiTiet(DetailDto chiTiet) { this.chiTiet = chiTiet; }
    public String getHoTenNv() { return hoTenNv; }
    public void setHoTenNv(String hoTenNv) { this.hoTenNv = hoTenNv; }
}
