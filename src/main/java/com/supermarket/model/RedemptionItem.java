package com.supermarket.model;

import java.time.LocalDateTime;

/**
 * 兑换商品模型类
 * 
 * @author SupermarketSystem
 * @version 1.0
 */
public class RedemptionItem {
    
    private int itemId;
    private String itemName;
    private String category;
    private int pointsRequired;
    private String description;
    private int stockQuantity;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 商品分类枚举
     */
    public enum Category {
        COUPON("COUPON", "优惠券"),
        GIFT("GIFT", "实物商品"),
        SERVICE("SERVICE", "服务类");
        
        private final String code;
        private final String displayName;
        
        Category(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static Category fromCode(String code) {
            for (Category category : values()) {
                if (category.code.equals(code)) {
                    return category;
                }
            }
            return null;
        }
    }
    
    // 默认构造函数
    public RedemptionItem() {}
    
    // 带参数的构造函数
    public RedemptionItem(String itemName, String category, int pointsRequired, 
                         String description, int stockQuantity) {
        this.itemName = itemName;
        this.category = category;
        this.pointsRequired = pointsRequired;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public int getItemId() {
        return itemId;
    }
    
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public int getPointsRequired() {
        return pointsRequired;
    }
    
    public void setPointsRequired(int pointsRequired) {
        this.pointsRequired = pointsRequired;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // 业务方法
    
    /**
     * 检查商品是否可用（有库存且状态为活跃）
     * @return 是否可用
     */
    public boolean isAvailable() {
        return isActive && stockQuantity > 0;
    }
    
    /**
     * 检查是否有足够库存
     * @param quantity 需要的数量
     * @return 是否有足够库存
     */
    public boolean hasEnoughStock(int quantity) {
        return stockQuantity >= quantity;
    }
    
    /**
     * 获取分类显示名称
     * @return 分类显示名称
     */
    public String getCategoryDisplayName() {
        Category cat = Category.fromCode(category);
        return cat != null ? cat.getDisplayName() : category;
    }
    
    /**
     * 获取库存状态描述
     * @return 库存状态描述
     */
    public String getStockStatus() {
        if (stockQuantity <= 0) {
            return "缺货";
        } else if (stockQuantity <= 5) {
            return "库存紧张";
        } else {
            return "库存充足";
        }
    }
    
    @Override
    public String toString() {
        return "RedemptionItem{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", category='" + category + '\'' +
                ", pointsRequired=" + pointsRequired +
                ", description='" + description + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RedemptionItem that = (RedemptionItem) obj;
        return itemId == that.itemId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(itemId);
    }
}