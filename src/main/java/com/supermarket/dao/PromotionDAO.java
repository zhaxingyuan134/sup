package com.supermarket.dao;

import com.supermarket.model.Promotion;
import java.util.List;

/**
 * 促销活动数据访问对象接口
 */
public interface PromotionDAO {
    
    /**
     * 创建促销活动
     * @param promotion 促销活动对象
     * @return 是否创建成功
     */
    boolean createPromotion(Promotion promotion);
    
    /**
     * 根据ID获取促销活动
     * @param promotionId 促销活动ID
     * @return 促销活动对象，如果不存在则返回null
     */
    Promotion getPromotionById(int promotionId);
    
    /**
     * 获取所有促销活动
     * @return 促销活动列表
     */
    List<Promotion> getAllPromotions();
    
    /**
     * 获取当前有效的促销活动
     * @return 有效的促销活动列表
     */
    List<Promotion> getActivePromotions();
    
    /**
     * 根据类型获取促销活动
     * @param type 促销活动类型
     * @return 促销活动列表
     */
    List<Promotion> getPromotionsByType(Promotion.PromotionType type);
    
    /**
     * 更新促销活动
     * @param promotion 促销活动对象
     * @return 是否更新成功
     */
    boolean updatePromotion(Promotion promotion);
    
    /**
     * 删除促销活动
     * @param promotionId 促销活动ID
     * @return 是否删除成功
     */
    boolean deletePromotion(int promotionId);
    
    /**
     * 切换促销活动状态（启用/禁用）
     * @param promotionId 促销活动ID
     * @return 是否切换成功
     */
    boolean togglePromotionStatus(int promotionId);
    
    /**
     * 检查促销活动是否可用于指定会员
     * @param promotionId 促销活动ID
     * @param memberId 会员ID
     * @param purchaseAmount 购买金额
     * @return 是否可用
     */
    boolean isPromotionAvailableForMember(int promotionId, int memberId, double purchaseAmount);
    
    /**
     * 记录会员使用促销活动
     * @param promotionId 促销活动ID
     * @param memberId 会员ID
     * @param usageAmount 使用金额
     * @return 是否记录成功
     */
    boolean recordPromotionUsage(int promotionId, int memberId, double usageAmount);
    
    /**
     * 获取会员对指定促销活动的使用次数
     * @param promotionId 促销活动ID
     * @param memberId 会员ID
     * @return 使用次数
     */
    int getMemberPromotionUsageCount(int promotionId, int memberId);
    
    /**
     * 获取当前活跃的促销活动数量
     * @return 活跃促销活动数量
     */
    int getActivePromotionCount();
}