package com.demo.ltud_n10.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class BranchDto {
    @SerializedName("ma_chi_nhanh")
    private String maChiNhanh;

    @SerializedName("ten_chi_nhanh")
    private String tenChiNhanh;

    @SerializedName("dia_chi")
    private String diaChi;

    @SerializedName("sdt")
    private String sdt;

    @SerializedName("ma_nv_ql")
    private String maNvQl;

    @SerializedName("manager_name")
    private String managerName;

    @SerializedName("trang_thai")
    private String trangThai;

    public String getMaChiNhanh() { return maChiNhanh; }
    public void setMaChiNhanh(String maChiNhanh) { this.maChiNhanh = maChiNhanh; }

    public String getTenChiNhanh() { return tenChiNhanh; }
    public void setTenChiNhanh(String tenChiNhanh) { this.tenChiNhanh = tenChiNhanh; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getMaNvQl() { return maNvQl; }
    public void setMaNvQl(String maNvQl) { this.maNvQl = maNvQl; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return tenChiNhanh;
    }
}
