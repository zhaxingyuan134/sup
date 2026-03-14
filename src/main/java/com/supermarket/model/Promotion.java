package com.supermarket.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * 促销活动模型类
 */
public class Promotion {
    
    /**
     * 促销活动类型枚举
     */
    public enum PromotionType {
        DOUBLE_POINTS("双倍积分"),
        BONUS_POINTS("积分奖励"),
        DISCOUNT("折扣优惠"),
        SPECIAL_OFFER("特殊优惠");
        
        private final String displayName;
        
        PromotionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private int promotionId;
    private String promotionName;
    private PromotionType promotionType;
    private String description;
    private Date startDate;
    private Date endDate;
    private String ruleConfig; // JSON格式的规则配置
    private double minPurchaseAmount;
    private int maxUsagePerMember;
    private boolean isActive;
    private int createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // 构造函数
    public Promotion() {
        this.isActive = true;
        this.minPurchaseAmount = 0.0;
        this.maxUsagePerMember = 0; // 0表示无限制
        this.ruleConfig = "{}";
    }
    
    public Promotion(String promotionName, PromotionType promotionType, String description,
                    Date startDate, Date endDate) {
        this();
        this.promotionName = promotionName;
        this.promotionType = promotionType;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Getter和Setter方法
    public int getPromotionId() {
        return promotionId;
    }
    
    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }
    
    public String getPromotionName() {
        return promotionName;
    }
    
    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }
    
    public PromotionType getPromotionType() {
        return promotionType;
    }
    
    public void setPromotionType(PromotionType promotionType) {
        this.promotionType = promotionType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public String getRuleConfig() {
        return ruleConfig;
    }
    
    public void setRuleConfig(String ruleConfig) {
        this.ruleConfig = ruleConfig;
    }
    
    public double getMinPurchaseAmount() {
        return minPurchaseAmount;
    }
    
    public void setMinPurchaseAmount(double minPurchaseAmount) {
        this.minPurchaseAmount = minPurchaseAmount;
    }
    
    public int getMaxUsagePerMember() {
        return maxUsagePerMember;
    }
    
    public void setMaxUsagePerMember(int maxUsagePerMember) {
        this.maxUsagePerMember = maxUsagePerMember;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public int getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * 检查促销活动是否在有效期内
     */
    public boolean isInValidPeriod() {
        Date currentDate = new Date(System.currentTimeMillis());
        return !currentDate.before(startDate) && !currentDate.after(endDate);
    }
    
    /**
     * 检查促销活动是否可用
     */
    public boolean isAvailable() {
        return isActive && isInValidPeriod();
    }
    
    /**
     * 获取促销活动状态描述
     */
    public String getStatusDescription() {
        if (!isActive) {
            return "已禁用";
        }
        
        Date currentDate = new Date(System.currentTimeMillis());
        if (currentDate.before(startDate)) {
            return "未开始";
        } else if (currentDate.after(endDate)) {
            return "已结束";
        } else {
            return "进行中";
        }
    }
    
    @Override
    public String toString() {
        return "Promotion{" +
                "promotionId=" + promotionId +
                ", promotionName='" + promotionName + '\'' +
                ", promotionType=" + promotionType +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", ruleConfig='" + ruleConfig + '\'' +
                ", minPurchaseAmount=" + minPurchaseAmount +
                ", maxUsagePerMember=" + maxUsagePerMember +
                ", isActive=" + isActive +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Promotion promotion = (Promotion) obj;
        return promotionId == promotion.promotionId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(promotionId);
    }
}