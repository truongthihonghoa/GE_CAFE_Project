package com.demo.ltud_n10.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("refresh")
    private String refresh;
    
    @SerializedName("access")
    private String access;

    @SerializedName("is_staff")
    private boolean isStaff;

    @SerializedName("ma_nv")
    private String maNv;

    public String getRefresh() { return refresh; }
    public void setRefresh(String refresh) { this.refresh = refresh; }
    public String getAccess() { return access; }
    public void setAccess(String access) { this.access = access; }
    public boolean isStaff() { return isStaff; }
    public void setStaff(boolean staff) { isStaff = staff; }
    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
}
