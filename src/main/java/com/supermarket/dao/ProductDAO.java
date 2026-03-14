package com.supermarket.dao;

import com.supermarket.model.Product;
import java.sql.SQLException;
import java.util.List;

/**
 * 商品数据访问层接口
 * 
 * @author SupermarketSystem
 * @version 1.0
 */
public interface ProductDAO {
    
    /**
     * 根据条码查询商品
     * @param barcode 商品条码
     * @return 商品对象，如果不存在则返回null
     * @throws SQLException 数据库操作异常
     */
    Product findByBarcode(String barcode) throws SQLException;
    
    /**
     * 根据ID获取商品
     * @param productId 商品ID
     * @return 商品对象，如果不存在则返回null
     * @throws SQLException 数据库操作异常
     */
    Product findById(int productId) throws SQLException;
    
    /**
     * 获取所有可用商品
     * @return 可用商品列表
     * @throws SQLException 数据库操作异常
     */
    List<Product> findAllAvailable() throws SQLException;
    
    /**
     * 根据分类获取商品
     * @param category 商品分类
     * @return 指定分类的商品列表
     * @throws SQLException 数据库操作异常
     */
    List<Product> findByCategory(String category) throws SQLException;
    
    /**
     * 搜索商品（按名称模糊查询）
     * @param keyword 搜索关键词
     * @return 匹配的商品列表
     * @throws SQLException 数据库操作异常
     */
    List<Product> searchByName(String keyword) throws SQLException;
    
    /**
     * 更新商品库存
     * @param productId 商品ID
     * @param quantity 新的库存数量
     * @return 是否更新成功
     * @throws SQLException 数据库操作异常
     */
    boolean updateStock(int productId, int quantity) throws SQLException;
    
    /**
     * 减少商品库存（用于销售时）
     * @param productId 商品ID
     * @param quantity 减少的数量
     * @return 是否减少成功
     * @throws SQLException 数据库操作异常
     */
    boolean decreaseStock(int productId, int quantity) throws SQLException;
    
    /**
     * 检查商品库存是否充足
     * @param productId 商品ID
     * @param requiredQuantity 需要的数量
     * @return 库存是否充足
     * @throws SQLException 数据库操作异常
     */
    boolean checkStockAvailability(int productId, int requiredQuantity) throws SQLException;
    
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