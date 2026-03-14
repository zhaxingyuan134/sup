package com.supermarket.dao;

import com.supermarket.model.RedemptionItem;
import java.sql.SQLException;
import java.util.List;

/**
 * 兑换商品数据访问对象接口
 * 
 * @author SupermarketSystem
 * @version 1.0
 */
public interface RedemptionItemDAO {
    
    /**
     * 创建兑换商品
     * @param item 兑换商品对象
     * @return 是否创建成功
     * @throws SQLException 数据库操作异常
     */
    boolean createItem(RedemptionItem item) throws SQLException;
    
    /**
     * 根据ID获取兑换商品
     * @param itemId 商品ID
     * @return 兑换商品对象，如果不存在则返回null
     * @throws SQLException 数据库操作异常
     */
    RedemptionItem findById(int itemId) throws SQLException;
    
    /**
     * 获取所有兑换商品
     * @return 兑换商品列表
     * @throws SQLException 数据库操作异常
     */
    List<RedemptionItem> findAll() throws SQLException;
    
    /**
     * 获取所有可用的兑换商品（库存大于0且状态为活跃）
     * @return 可用的兑换商品列表
     * @throws SQLException 数据库操作异常
     */
    List<RedemptionItem> findAvailableItems() throws SQLException;
    
    /**
     * 根据分类获取兑换商品
     * @param category 商品分类
     * @return 指定分类的兑换商品列表
     * @throws SQLException 数据库操作异常
     */
    List<RedemptionItem> findByCategory(String category) throws SQLException;
    
    /**
     * 根据积分范围获取兑换商品
     * @param minPoints 最小积分
     * @param maxPoints 最大积分
     * @return 指定积分范围的兑换商品列表
     * @throws SQLException 数据库操作异常
     */
    List<RedemptionItem> findByPointsRange(int minPoints, int maxPoints) throws SQLException;
    
    /**
     * 更新兑换商品信息
     * @param item 兑换商品对象
     * @return 是否更新成功
     * @throws SQLException 数据库操作异常
     */
    boolean updateItem(RedemptionItem item) throws SQLException;
    
    /**
     * 更新商品库存
     * @param itemId 商品ID
     * @param quantity 库存数量
     * @return 是否更新成功
     * @throws SQLException 数据库操作异常
     */
    boolean updateStock(int itemId, int quantity) throws SQLException;
    
    /**
     * 减少商品库存（用于兑换时）
     * @param itemId 商品ID
     * @param quantity 减少的数量
     * @return 是否减少成功
     * @throws SQLException 数据库操作异常
     */
    boolean decreaseStock(int itemId, int quantity) throws SQLException;
    
    /**
     * 切换商品状态（启用/禁用）
     * @param itemId 商品ID
     * @return 是否切换成功
     * @throws SQLException 数据库操作异常
     */
    boolean toggleStatus(int itemId) throws SQLException;
    
    /**
     * 删除兑换商品
     * @param itemId 商品ID
     * @return 是否删除成功
     * @throws SQLException 数据库操作异常
     */
    boolean deleteItem(int itemId) throws SQLException;
    
    /**
     * 获取商品总数
     * @return 商品总数
     * @throws SQLException 数据库操作异常
     */
    int getTotalCount() throws SQLException;
    
    /**
     * 获取可用商品总数
     * @return 可用商品总数
     * @throws SQLException 数据库操作异常
     */
    int getAvailableCount() throws SQLException;
}