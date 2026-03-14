package com.supermarket.model;

import java.time.LocalDateTime;

/**
 * 积分交易记录模型类
 * 对应数据库中的point_transactions表
 */
public class PointTransaction {
    
    public enum TransactionType {
        EARN("EARN", "积分获得"),
        REDEEM("REDEEM", "积分兑换"),
        EXPIRE("EXPIRE", "积分过期"),
        ADJUST("ADJUST", "积分调整");
        
        private final String code;
        private final String description;
        
        TransactionType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static TransactionType fromCode(String code) {
            for (TransactionType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return null;
        }
    }
    
    private int transactionId;
    private int userId;
    private TransactionType transactionType;
    private int points;
    private String description;
    private LocalDateTime transactionDate;
    private Integer cashierId;
    private String orderId;
    
    // 构造函数
    public PointTransaction() {}
    
    public PointTransaction(int userId, TransactionType transactionType, int points, String description) {
        this.userId = userId;
        this.transactionType = transactionType;
        this.points = points;
        this.description = description;
        this.transactionDate = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public int getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    public int getPoints() {
        return points;
    }
    
    public void setPoints(int points) {
        this.points = points;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public Integer getCashierId() {
        return cashierId;
    }
    
    public void setCashierId(Integer cashierId) {
        this.cashierId = cashierId;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    @Override
    public String toString() {
        return "PointTransaction{" +
                "transactionId=" + transactionId +
                ", userId=" + userId +
                ", transactionType=" + transactionType +
                ", points=" + points +
                ", description='" + description + '\'' +
                ", transactionDate=" + transactionDate +
                ", cashierId=" + cashierId +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}