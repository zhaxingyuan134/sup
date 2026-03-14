package com.supermarket.dao;

import com.supermarket.model.PointTransaction;
import java.sql.SQLException;
import java.util.List;

/**
 * 积分交易记录数据访问对象接口
 */
public interface PointTransactionDAO {
    
    /**
     * 创建积分交易记录
     * @param transaction 积分交易记录对象
     * @return 是否创建成功
     * @throws SQLException 数据库操作异常
     */
    boolean createTransaction(PointTransaction transaction) throws SQLException;
    
    /**
     * 根据用户ID查询积分交易记录
     * @param userId 用户ID
     * @return 积分交易记录列表
     * @throws SQLException 数据库操作异常
     */
    List<PointTransaction> findByUserId(int userId) throws SQLException;
    
    /**
     * 根据用户ID和交易类型查询积分交易记录
     * @param userId 用户ID
     * @param transactionType 交易类型
     * @return 积分交易记录列表
     * @throws SQLException 数据库操作异常
     */
    List<PointTransaction> findByUserIdAndType(int userId, PointTransaction.TransactionType transactionType) throws SQLException;
    
    /**
     * 根据交易ID查询积分交易记录
     * @param transactionId 交易ID
     * @return 积分交易记录对象，如果不存在则返回null
     * @throws SQLException 数据库操作异常
     */
    PointTransaction findById(int transactionId) throws SQLException;
    
    /**
     * 查询用户最近的积分交易记录
     * @param userId 用户ID
     * @param limit 记录数量限制
     * @return 积分交易记录列表
     * @throws SQLException 数据库操作异常
     */
    List<PointTransaction> findRecentByUserId(int userId, int limit) throws SQLException;
    
    /**
     * 删除积分交易记录
     * @param transactionId 交易ID
     * @return 是否删除成功
     * @throws SQLException 数据库操作异常
     */
    boolean deleteTransaction(int transactionId) throws SQLException;
}