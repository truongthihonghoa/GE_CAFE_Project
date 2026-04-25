package com.demo.ltud_n10.domain.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String email;
    private String name;
    private String role; 
    private String status; 
    private String password;
    private boolean isSuperuser;
    private boolean isStaff;

    // QUAN TRỌNG: Constructor không tham số để fix lỗi "new User()"
    public User() {
    }

    public User(String id, String email, String name, String role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    // Getter và Setter đầy đủ
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { 
        return (status == null || status.isEmpty()) ? "Đang hoạt động" : status; 
    }
    public void setStatus(String status) { this.status = status; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public boolean isSuperuser() { return isSuperuser; }
    public void setSuperuser(boolean superuser) { this.isSuperuser = superuser; }
    
    public boolean isStaff() { return isStaff; }
    public void setStaff(boolean staff) { this.isStaff = staff; }

    // Alias để tương thích với AccountDetailFragment
    public String getUsername() { return email; }
    public void setUsername(String username) { this.email = username; }
}
