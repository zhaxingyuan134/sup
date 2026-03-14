package com.supermarket.service;

import com.google.gson.Gson;
import com.supermarket.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 促销活动服务类
 * 处理双倍积分日等促销活动的业务逻辑
 */
public class PromotionService {
    
    /**
     * 计算会员购物积分（考虑双倍积分日等促销活动）
     * @param userId 用户ID
     * @param totalAmount 消费总金额
     * @param productCategory 商品类别
     * @param memberLevel 会员等级
     * @return 计算后的积分
     */
    public static int calculateMemberPoints(int userId, double totalAmount, String productCategory, String memberLevel) {
        try {
            // 基础积分计算（1元=1积分）
            int basePoints = (int) Math.floor(totalAmount);
            
            // 获取当前有效的双倍积分日活动
            DoublePointsPromotion promotion = getCurrentDoublePointsPromotion();
            
            if (promotion != null && isPromotionApplicable(promotion, productCategory, memberLevel)) {
                // 应用双倍积分日倍率
                basePoints = (int) Math.floor(basePoints * promotion.getMultiplier());
                
                // 记录促销活动使用
                recordPromotionUsage(userId, promotion.getPromotionId(), totalAmount, basePoints);
            }
            
            return basePoints;
            
        } catch (Exception e) {
            e.printStackTrace();
            // 出错时返回基础积分
            return (int) Math.floor(totalAmount);
        }
    }
    
    /**
     * 获取当前有效的双倍积分日活动
     */
    private static DoublePointsPromotion getCurrentDoublePointsPromotion() {
        String sql = "SELECT promotion_id, promotion_name, rule_config, start_date, end_date " +
                    "FROM promotions " +
                    "WHERE promotion_type = 'DOUBLE_POINTS' " +
                    "AND is_active = 1 " +
                    "AND start_date <= CURDATE() " +
                    "AND end_date >= CURDATE() " +
                    "ORDER BY created_at DESC " +
                    "LIMIT 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                DoublePointsPromotion promotion = new DoublePointsPromotion();
                promotion.setPromotionId(rs.getInt("promotion_id"));
                promotion.setTitle(rs.getString("promotion_name"));
                promotion.setStartDate(rs.getDate("start_date").toLocalDate());
                promotion.setEndDate(rs.getDate("end_date").toLocalDate());
                
                // 解析规则配置
                String ruleConfig = rs.getString("rule_config");
                parseRuleConfig(promotion, ruleConfig);
                
                return promotion;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 解析促销活动规则配置
     */
    private static void parseRuleConfig(DoublePointsPromotion promotion, String ruleConfig) {
        try {
            if (ruleConfig != null && !ruleConfig.trim().isEmpty() && !ruleConfig.equals("NULL")) {
                // 使用Gson解析JSON
                Gson gson = new Gson();
                Map<String, Object> config = gson.fromJson(ruleConfig, Map.class);
                
                // 设置积分倍数
                if (config.containsKey("multiplier")) {
                    Object multiplierObj = config.get("multiplier");
                    if (multiplierObj instanceof Number) {
                        promotion.setMultiplier(((Number) multiplierObj).doubleValue());
                    } else {
                        promotion.setMultiplier(2.0);
                    }
                } else {
                    promotion.setMultiplier(2.0);
                }
                
                // 设置时间限制
                if (config.containsKey("timeRestriction")) {
                    promotion.setTimeRestriction(config.get("timeRestriction").toString());
                }
                
                // 设置商品类别限制
                if (config.containsKey("categoryRestriction")) {
                    promotion.setCategoryRestriction(config.get("categoryRestriction").toString());
                }
                
                // 设置会员等级限制
                if (config.containsKey("memberLevelRestriction")) {
                    promotion.setMemberLevelRestriction(config.get("memberLevelRestriction").toString());
                }
            } else {
                promotion.setMultiplier(2.0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            promotion.setMultiplier(2.0);
        }
    }
    
    /**
     * 检查促销活动是否适用于当前购物
     */
    private static boolean isPromotionApplicable(DoublePointsPromotion promotion, String productCategory, String memberLevel) {
        // 检查时间限制
        if (promotion.getTimeRestriction() != null && !promotion.getTimeRestriction().isEmpty()) {
            if (!isTimeRestrictionMet(promotion.getTimeRestriction())) {
                return false;
            }
        }
        
        // 检查商品类别限制
        if (promotion.getCategoryRestriction() != null && !promotion.getCategoryRestriction().isEmpty()) {
            if (!isCategoryRestrictionMet(promotion.getCategoryRestriction(), productCategory)) {
                return false;
            }
        }
        
        // 检查会员等级限制
        if (promotion.getMemberLevelRestriction() != null && !promotion.getMemberLevelRestriction().isEmpty()) {
            if (!isMemberLevelRestrictionMet(promotion.getMemberLevelRestriction(), memberLevel)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 检查时间限制
     */
    private static boolean isTimeRestrictionMet(String timeRestriction) {
        LocalTime currentTime = LocalTime.now();
        
        switch (timeRestriction.toLowerCase()) {
            case "morning":
                return currentTime.isAfter(LocalTime.of(6, 0)) && currentTime.isBefore(LocalTime.of(12, 0));
            case "afternoon":
                return currentTime.isAfter(LocalTime.of(12, 0)) && currentTime.isBefore(LocalTime.of(18, 0));
            case "evening":
                return currentTime.isAfter(LocalTime.of(18, 0)) && currentTime.isBefore(LocalTime.of(23, 59));
            default:
                return true; // 全天有效
        }
    }
    
    /**
     * 检查商品类别限制
     */
    private static boolean isCategoryRestrictionMet(String categoryRestriction, String productCategory) {
        if (productCategory == null) {
            return true;
        }
        
        String[] allowedCategories = categoryRestriction.split(",");
        for (String category : allowedCategories) {
            if (productCategory.toLowerCase().contains(category.trim().toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查会员等级限制
     */
    private static boolean isMemberLevelRestrictionMet(String memberLevelRestriction, String memberLevel) {
        if (memberLevel == null) {
            return true;
        }
        
        String[] allowedLevels = memberLevelRestriction.split(",");
        for (String level : allowedLevels) {
            if (memberLevel.equalsIgnoreCase(level.trim())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 记录促销活动使用情况
     * @param userId 用户ID
     * @param promotionId 促销活动ID
     * @param purchaseAmount 消费金额
     * @param pointsEarned 获得积分
     */
    private static void recordPromotionUsage(int userId, int promotionId, double purchaseAmount, int pointsEarned) {
        String sql = "INSERT INTO member_promotion_usage (promotion_id, member_id, usage_amount, usage_date) " +
                    "VALUES (?, ?, ?, CURDATE())";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, promotionId);
            stmt.setInt(2, userId);
            stmt.setDouble(3, purchaseAmount);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("记录促销活动使用情况失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取促销活动统计信息
     */
    public static Map<String, Object> getPromotionStatistics(int promotionId) {
        Map<String, Object> stats = new HashMap<>();
        
        String sql = "SELECT " +
                    "COUNT(*) as usage_count, " +
                    "COUNT(DISTINCT member_id) as unique_members, " +
                    "COALESCE(SUM(usage_amount), 0) as total_amount, " +
                    "COALESCE(AVG(usage_amount), 0) as avg_amount " +
                    "FROM member_promotion_usage " +
                    "WHERE promotion_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, promotionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("usageCount", rs.getInt("usage_count"));
                    stats.put("uniqueMembers", rs.getInt("unique_members"));
                    stats.put("totalAmount", rs.getDouble("total_amount"));
                    stats.put("avgAmount", rs.getDouble("avg_amount"));
                    stats.put("totalPointsEarned", 0);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    /**
     * 双倍积分日促销活动数据类
     */
    public static class DoublePointsPromotion {
        private int promotionId;
        private String title;
        private double multiplier = 2.0;
        private String timeRestriction;
        private String categoryRestriction;
        private String memberLevelRestriction;
        private LocalDate startDate;
        private LocalDate endDate;
        
        // Getters and Setters
        public int getPromotionId() { return promotionId; }
        public void setPromotionId(int promotionId) { this.promotionId = promotionId; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public double getMultiplier() { return multiplier; }
        public void setMultiplier(double multiplier) { this.multiplier = multiplier; }
        
        public String getTimeRestriction() { return timeRestriction; }
        public void setTimeRestriction(String timeRestriction) { this.timeRestriction = timeRestriction; }
        
        public String getCategoryRestriction() { return categoryRestriction; }
        public void setCategoryRestriction(String categoryRestriction) { this.categoryRestriction = categoryRestriction; }
        
        public String getMemberLevelRestriction() { return memberLevelRestriction; }
        public void setMemberLevelRestriction(String memberLevelRestriction) { this.memberLevelRestriction = memberLevelRestriction; }
        
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    }
}
