package com.supermarket.model;

import java.sql.Timestamp;

/**
 * 用户实体类
 * 对应数据库中的users表
 */
public class User {
    
    private int userId;
    private String membershipCardNumber;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private UserRole role;
    private String membershipLevel;
    private int totalPoints;
    private Timestamp createdAt;
    private boolean isActive;
    
    /**
     * 用户角色枚举
     */
    public enum UserRole {
        MEMBER("会员"),
        CASHIER("收银员"),
        MANAGER("超市经理");
        
        private final String displayName;
        
        UserRole(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static UserRole fromString(String role) {
            for (UserRole userRole : UserRole.values()) {
                if (userRole.name().equals(role) || userRole.getDisplayName().equals(role)) {
                    return userRole;
                }
            }
            return MEMBER; // 默认为会员
        }
    }
    
    // 构造函数
    public User() {}
    
    public User(String username, String password, String realName, UserRole role) {
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.role = role;
        this.membershipLevel = "普通会员";
        this.totalPoints = 0;
        this.isActive = true;
    }
    
    // Getter和Setter方法
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getMembershipCardNumber() {
        return membershipCardNumber;
    }
    
    public void setMembershipCardNumber(String membershipCardNumber) {
        this.membershipCardNumber = membershipCardNumber;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public String getMembershipLevel() {
        return membershipLevel;
    }
    
    public void setMembershipLevel(String membershipLevel) {
        this.membershipLevel = membershipLevel;
    }
    
    public int getTotalPoints() {
        return totalPoints;
    }
    
    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", role=" + role +
                ", membershipLevel='" + membershipLevel + '\'' +
                ", totalPoints=" + totalPoints +
                ", isActive=" + isActive +
                '}';
    }
}