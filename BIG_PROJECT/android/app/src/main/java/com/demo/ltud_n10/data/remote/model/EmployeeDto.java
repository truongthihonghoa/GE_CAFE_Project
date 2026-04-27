package com.demo.ltud_n10.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class EmployeeDto {
    @SerializedName("ma_nv")
    private String maNv;

    @SerializedName("ho_ten")
    private String hoTen;

    @SerializedName("email")
    private String email;

    @SerializedName("so_dien_thoai")
    private String soDienThoai;

    @SerializedName("cccd")
    private String cccd;

    @SerializedName("gioi_tinh")
    private String gioiTinh;

    @SerializedName("ngay_sinh")
    private String ngaySinh;

    @SerializedName("dia_chi")
    private String diaChi;

    @SerializedName("chuc_vu")
    private String chucVu;

    @SerializedName("trang_thai")
    private String trangThai;

    @SerializedName("sdt")
    private String sdt;

    @SerializedName("tk_ngan_hang")
    private String tkNganHang;

    @SerializedName("ma_chi_nhanh")
    private String maChiNhanh;

    @SerializedName("is_staff")
    private Boolean isStaff; // SỬA: Đổi từ Integer sang Boolean

    // Getters and Setters
    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getTkNganHang() { return tkNganHang; }
    public void setTkNganHang(String tkNganHang) { this.tkNganHang = tkNganHang; }

    public String getMaChiNhanh() { return maChiNhanh; }
    public void setMaChiNhanh(String maChiNhanh) { this.maChiNhanh = maChiNhanh; }

    public Boolean getIsStaff() { return isStaff; }
    public void setIsStaff(Boolean isStaff) { this.isStaff = isStaff; }
}
