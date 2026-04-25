package com.demo.ltud_n10.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AccountDto implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("ten_dang_nhap")
    private String username;

    @SerializedName("ho_ten")
    private String fullName;

    @SerializedName("vai_tro")
    private String role;

    @SerializedName("trang_thai")
    private String status;
    
    // Thêm trường password phục vụ thêm/sửa
    private String password;
    
    @SerializedName("ma_nv")
    private String maNvId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getMaNvId() { return maNvId; }
    public void setMaNvId(String maNvId) { this.maNvId = maNvId; }

    // Các trường hỗ trợ UI nếu cần
    public String getDisplayUsername() { return username; }
    public Boolean getIsStaff() { return "Quản lý".equals(role); }
    public Boolean getIsActive() { return "Đang hoạt động".equals(status); }
}
