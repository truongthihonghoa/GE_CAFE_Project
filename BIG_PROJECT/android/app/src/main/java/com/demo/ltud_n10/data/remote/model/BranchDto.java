package com.demo.ltud_n10.data.remote.model;

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

    // Getters and Setters
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
}
