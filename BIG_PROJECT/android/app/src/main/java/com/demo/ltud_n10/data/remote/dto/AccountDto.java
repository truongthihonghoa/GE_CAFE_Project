package com.demo.ltud_n10.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AccountDto implements Serializable {
    @SerializedName("id")
    private Integer id;

    @SerializedName("ten_dang_nhap")
    private String username;

    @SerializedName("ho_ten")
    private String fullName;

    @SerializedName("vai_tro")
    private String role;

    @SerializedName("trang_thai")
    private String status;
    
    @SerializedName("ma_nv")
    private String maNv;

    @SerializedName("is_staff")
    private boolean isStaff;

    private String password;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
    public boolean isStaff() { return isStaff; }
    public void setStaff(boolean staff) { isStaff = staff; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
