package com.demo.ltud_n10.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class AccountDto {
    @SerializedName("id")
    private String id;
    
    @SerializedName("ten_dang_nhap")
    private String displayUsername;
    
    @SerializedName("ho_ten")
    private String fullName;
    
    @SerializedName("vai_tro")
    private String role;
    
    @SerializedName("trang_thai")
    private String status;
    
    // Fields for write operations
    @SerializedName("username")
    private String username;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("ma_nv_id")
    private String maNvId;
    
    @SerializedName("is_staff")
    private Boolean isStaff;
    
    @SerializedName("is_active")
    private Boolean isActive;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDisplayUsername() { return displayUsername; }
    public void setDisplayUsername(String displayUsername) { this.displayUsername = displayUsername; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getMaNvId() { return maNvId; }
    public void setMaNvId(String maNvId) { this.maNvId = maNvId; }
    public Boolean getIsStaff() { return isStaff; }
    public void setIsStaff(Boolean isStaff) { this.isStaff = isStaff; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
