package com.supermarket.model;

import java.time.LocalDateTime;

/**
 * 兑换记录模型类
 * 
 * @author SupermarketSystem
 * @version 1.0
 */
public class RedemptionRecord {
    
    private int recordId;
    private int userId;
    private int itemId;
    private int pointsUsed;
    private int quantity;
    private LocalDateTime redemptionDate;
    private String status;
    private Integer cashierId;
    private String notes;
    
    // 关联对象（用于查询时的连接）
    private String userName;
    private String itemName;
    private String cashierName;
    
    /**
     * 兑换状态枚举
     */
    public enum Status {
        PENDING("PENDING", "待处理"),
        COMPLETED("COMPLETED", "已完成"),
        CANCELLED("CANCELLED", "已取消");
        
        private final String code;
        private final String displayName;
        
        Status(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static Status fromCode(String code) {
            for (Status status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            return null;
        }
    }
    
    // 默认构造函数
    public RedemptionRecord() {}
    
    // 带参数的构造函数
    public RedemptionRecord(int userId, int itemId, int pointsUsed, int quantity) {
        this.userId = userId;
        this.itemId = itemId;
        this.pointsUsed = pointsUsed;
        this.quantity = quantity;
        this.redemptionDate = LocalDateTime.now();
        this.status = Status.PENDING.getCode();
    }
    
    // Getter和Setter方法
    public int getRecordId() {
        return recordId;
    }
    
    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getItemId() {
        return itemId;
    }
    
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    
    public int getPointsUsed() {
        return pointsUsed;
    }
    
    public void setPointsUsed(int pointsUsed) {
        this.pointsUsed = pointsUsed;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public LocalDateTime getRedemptionDate() {
        return redemptionDate;
    }
    
    public void setRedemptionDate(LocalDateTime redemptionDate) {
        this.redemptionDate = redemptionDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getCashierId() {
        return cashierId;
    }
    
    public void setCashierId(Integer cashierId) {
        this.cashierId = cashierId;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // 关联对象的Getter和Setter
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getCashierName() {
        return cashierName;
    }
    
    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }
    
    // 业务方法
    
    /**
     * 获取状态显示名称
     * @return 状态显示名称
     */
    public String getStatusDisplayName() {
        Status statusEnum = Status.fromCode(status);
        return statusEnum != null ? statusEnum.getDisplayName() : status;
    }
    
    /**
     * 检查记录是否可以取消
     * @return 是否可以取消
     */
    public boolean canCancel() {
        return Status.PENDING.getCode().equals(status);
    }
    
    /**
     * 检查记录是否已完成
     * @return 是否已完成
     */
    public boolean isCompleted() {
        return Status.COMPLETED.getCode().equals(status);
    }
    
    /**
     * 检查记录是否已取消
     * @return 是否已取消
     */
    public boolean isCancelled() {
        return Status.CANCELLED.getCode().equals(status);
    }
    
    /**
     * 计算总积分消耗
     * @return 总积分消耗
     */
    public int getTotalPointsUsed() {
        return pointsUsed * quantity;
    }
    
    @Override
    public String toString() {
        return "RedemptionRecord{" +
                "recordId=" + recordId +
                ", userId=" + userId +
                ", itemId=" + itemId +
                ", pointsUsed=" + pointsUsed +
                ", quantity=" + quantity +
                ", redemptionDate=" + redemptionDate +
                ", status='" + status + '\'' +
                ", cashierId=" + cashierId +
                ", notes='" + notes + '\'' +
                ", userName='" + userName + '\'' +
                ", itemName='" + itemName + '\'' +
                ", cashierName='" + cashierName + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RedemptionRecord that = (RedemptionRecord) obj;
        return recordId == that.recordId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(recordId);
    }
}