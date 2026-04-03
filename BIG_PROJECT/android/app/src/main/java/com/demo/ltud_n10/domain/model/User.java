package com.demo.ltud_n10.domain.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String username;
    private String password;
    private String name;
    private String role; // "ADMIN" or "EMPLOYEE"
    private String status; // "Đang hoạt động" or "Ngưng hoạt động"

    public User() {
        this.status = "Đang hoạt động";
        this.role = "EMPLOYEE";
    }

    public User(String id, String username, String name, String role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
        this.status = "Đang hoạt động";
    }

    public User(String id, String username, String password, String name, String role, String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
