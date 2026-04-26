package com.demo.ltud_n10.domain.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Branch implements Serializable {
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

    private String status; // Luôn ưu tiên dùng trường này để đổi màu UI

    public Branch() {}

    public Branch(String id, String name, String address, String phoneNumber, String managerName, String status) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.managerName = managerName;
        this.status = status;
    }

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
