package com.demo.ltud_n10.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class BranchDto implements Serializable {
    @SerializedName("ma_chi_nhanh")
    private String id;

    @SerializedName("ten_chi_nhanh")
    private String name;

    @SerializedName("dia_chi")
    private String address;

    @SerializedName("sdt")
    private String phoneNumber;

    @SerializedName("ma_nv_ql")
    private String managerName;

    @SerializedName("trang_thai") // CỘT MỚI TRÊN CSDL CỦA BẠN
    private String status;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    
    public String getStatus() { 
        return (status == null || status.isEmpty()) ? "Đang hoạt động" : status; 
    }
    public void setStatus(String status) { this.status = status; }
}
