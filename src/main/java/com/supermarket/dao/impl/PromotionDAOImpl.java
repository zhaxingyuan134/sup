package com.supermarket.dao.impl;

import com.supermarket.dao.PromotionDAO;
import com.supermarket.model.Promotion;
import com.supermarket.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 促销活动数据访问对象实现类
 */
public class PromotionDAOImpl implements PromotionDAO {
    
    @Override
    public boolean createPromotion(Promotion promotion) {
        String sql = "INSERT INTO promotions (promotion_name, promotion_type, description, " +
                    "start_date, end_date, rule_config, min_purchase_amount, max_usage_per_member, " +
                    "is_active, created_by, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, promotion.getPromotionName());
            stmt.setString(2, promotion.getPromotionType().name());
            stmt.setString(3, promotion.getDescription());
            stmt.setDate(4, promotion.getStartDate());
            stmt.setDate(5, promotion.getEndDate());
            stmt.setString(6, promotion.getRuleConfig());
            stmt.setDouble(7, promotion.getMinPurchaseAmount());
            stmt.setInt(8, promotion.getMaxUsagePerMember());
            stmt.setBoolean(9, promotion.isActive());
            stmt.setInt(10, promotion.getCreatedBy());
            stmt.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Promotion getPromotionById(int promotionId) {
        String sql = "SELECT * FROM promotions WHERE promotion_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, promotionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPromotion(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<Promotion> getAllPromotions() {
        String sql = "SELECT * FROM promotions ORDER BY created_at DESC";
        List<Promotion> promotions = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                promotions.add(mapResultSetToPromotion(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return promotions;
    }
    
    @Override
    public List<Promotion> getActivePromotions() {
        String sql = "SELECT * FROM promotions WHERE is_active = true " +
                    "AND start_date <= CURRENT_DATE AND end_date >= CURRENT_DATE " +
                    "ORDER BY created_at DESC";
        List<Promotion> promotions = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                promotions.add(mapResultSetToPromotion(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return promotions;
    }
    
    @Override
    public List<Promotion> getPromotionsByType(Promotion.PromotionType type) {
        String sql = "SELECT * FROM promotions WHERE promotion_type = ? ORDER BY created_at DESC";
        List<Promotion> promotions = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                promotions.add(mapResultSetToPromotion(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return promotions;
    }
    
    @Override
    public boolean updatePromotion(Promotion promotion) {
        String sql = "UPDATE promotions SET promotion_name = ?, promotion_type = ?, " +
                    "description = ?, start_date = ?, end_date = ?, rule_config = ?, " +
                    "min_purchase_amount = ?, max_usage_per_member = ?, is_active = ?, " +
                    "updated_at = ? WHERE promotion_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, promotion.getPromotionName());
            stmt.setString(2, promotion.getPromotionType().name());
            stmt.setString(3, promotion.getDescription());
            stmt.setDate(4, promotion.getStartDate());
            stmt.setDate(5, promotion.getEndDate());
            stmt.setString(6, promotion.getRuleConfig());
            stmt.setDouble(7, promotion.getMinPurchaseAmount());
            stmt.setInt(8, promotion.getMaxUsagePerMember());
            stmt.setBoolean(9, promotion.isActive());
            stmt.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(11, promotion.getPromotionId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deletePromotion(int promotionId) {
        String sql = "DELETE FROM promotions WHERE promotion_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, promotionId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean togglePromotionStatus(int promotionId) {
        String sql = "UPDATE promotions SET is_active = NOT is_active, updated_at = ? WHERE promotion_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(2, promotionId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean isPromotionAvailableForMember(int promotionId, int memberId, double purchaseAmount) {
        // 检查促销活动是否存在且有效
        Promotion promotion = getPromotionById(promotionId);
        if (promotion == null || !promotion.isActive()) {
            return false;
        }
        
        // 检查日期范围
        Date currentDate = new Date(System.currentTimeMillis());
        if (currentDate.before(promotion.getStartDate()) || currentDate.after(promotion.getEndDate())) {
            return false;
        }
        
        // 检查最低购买金额
        if (purchaseAmount < promotion.getMinPurchaseAmount()) {
            return false;
        }
        
        // 检查会员使用次数限制
        if (promotion.getMaxUsagePerMember() > 0) {
            int usageCount = getMemberPromotionUsageCount(promotionId, memberId);
            if (usageCount >= promotion.getMaxUsagePerMember()) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public boolean recordPromotionUsage(int promotionId, int memberId, double usageAmount) {
        String sql = "INSERT INTO member_promotion_usage (promotion_id, member_id, usage_amount, usage_date) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, promotionId);
            stmt.setInt(2, memberId);
            stmt.setDouble(3, usageAmount);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public int getMemberPromotionUsageCount(int promotionId, int memberId) {
        String sql = "SELECT COUNT(*) FROM member_promotion_usage WHERE promotion_id = ? AND member_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, promotionId);
            stmt.setInt(2, memberId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public int getActivePromotionCount() {
        String sql = "SELECT COUNT(*) FROM promotions WHERE is_active = 1 AND start_date <= CURDATE() AND end_date >= CURDATE()";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * 将ResultSet映射为Promotion对象
     */
    private Promotion mapResultSetToPromotion(ResultSet rs) throws SQLException {
        Promotion promotion = new Promotion();
        promotion.setPromotionId(rs.getInt("promotion_id"));
        promotion.setPromotionName(rs.getString("promotion_name"));
        promotion.setPromotionType(Promotion.PromotionType.valueOf(rs.getString("promotion_type")));
        promotion.setDescription(rs.getString("description"));
        promotion.setStartDate(rs.getDate("start_date"));
        promotion.setEndDate(rs.getDate("end_date"));
        promotion.setRuleConfig(rs.getString("rule_config"));
        promotion.setMinPurchaseAmount(rs.getDouble("min_purchase_amount"));
        promotion.setMaxUsagePerMember(rs.getInt("max_usage_per_member"));
        promotion.setActive(rs.getBoolean("is_active"));
        promotion.setCreatedBy(rs.getInt("created_by"));
        promotion.setCreatedAt(rs.getTimestamp("created_at"));
        promotion.setUpdatedAt(rs.getTimestamp("updated_at"));
        return promotion;
    }
}