package com.demo.ltud_n10.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("refresh")
    private String refresh;
    
    @SerializedName("access")
    private String access;

    public String getRefresh() { return refresh; }
    public void setRefresh(String refresh) { this.refresh = refresh; }
    public String getAccess() { return access; }
    public void setAccess(String access) { this.access = access; }
}
