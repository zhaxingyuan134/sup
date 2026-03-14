package com.supermarket.dao;

import com.supermarket.model.RedemptionRecord;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 兑换记录数据访问对象接口
 * 
 * @author SupermarketSystem
 * @version 1.0
 */
public interface RedemptionRecordDAO {
    
    /**
     * 创建兑换记录
     * @param record 兑换记录对象
     * @return 是否创建成功
     * @throws SQLException 数据库操作异常
     */
    boolean createRecord(RedemptionRecord record) throws SQLException;
    
    /**
     * 根据ID获取兑换记录
     * @param recordId 记录ID
     * @return 兑换记录对象，如果不存在则返回null
     * @throws SQLException 数据库操作异常
     */
    RedemptionRecord findById(int recordId) throws SQLException;
    
    /**
     * 获取所有兑换记录
     * @return 兑换记录列表
     * @throws SQLException 数据库操作异常
     */
    List<RedemptionRecord> findAll() throws SQLException;
    
    /**
     * 根据用户ID获取兑换记录
     * @param userId 用户ID
     * @return 用户的兑换记录列表
     * @throws SQLException 数据库操作异常
     */
    List<RedemptionRecord> findByUserId(int userId) throws SQLException;
    
    /**
     * 根据商品ID获取兑换记录
     * @param itemId 商品ID
     * @return 商品的兑换记录列表
     * @throws SQLException 数据库操作异常
     */
    List<RedemptionRecord> findByItemId(int itemId) throws SQLException;
    
    /**
     * 根据状态获取兑换记录
     * @param status 兑换状态
     * @return 指定状态的兑换记录列表
     * @throws SQLException 数据库操作异常
     */
    List<RedemptionRecord> findByStatus(String status) throws SQLException;
    
    /**
     * 根据时间范围获取兑换记录
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指定时间范围的兑换记录列表
     * @throws SQLException 数据库操作异常
     */
    List<RedemptionRecord> findByDateRange(LocalDateTime startTime, LocalDateTime endTime) throws SQLException;
    
    /**
     * 根据收银员ID获取兑换记录
     * @param cashierId 收银员ID
     * @return 收银员处理的兑换记录列表
     * @throws SQLException 数据库操作异常
     */
    List<RedemptionRecord> findByCashierId(int cashierId) throws SQLException;
    
    /**
     * 更新兑换记录状态
     * @param recordId 记录ID
     * @param status 新状态
     * @return 是否更新成功
     * @throws SQLException 数据库操作异常
     */
    boolean updateStatus(int recordId, String status) throws SQLException;
    
    /**
     * 更新兑换记录备注
     * @param recordId 记录ID
     * @param notes 备注信息
     * @return 是否更新成功
     * @throws SQLException 数据库操作异常
     */
    boolean updateNotes(int recordId, String notes) throws SQLException;
    
    /**
     * 删除兑换记录
     * @param recordId 记录ID
     * @return 是否删除成功
     * @throws SQLException 数据库操作异常
     */
    boolean deleteRecord(int recordId) throws SQLException;
    
    /**
     * 获取兑换记录总数
     * @return 记录总数
     * @throws SQLException 数据库操作异常
     */
    int getTotalCount() throws SQLException;
    
    /**
     * 获取用户兑换记录总数
     * @param userId 用户ID
     * @return 用户兑换记录总数
     * @throws SQLException 数据库操作异常
     */
    int getCountByUserId(int userId) throws SQLException;
    
    /**
     * 获取指定时间段内的兑换统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 兑换统计信息（总次数、总积分等）
     * @throws SQLException 数据库操作异常
     */
    RedemptionStatistics getStatistics(LocalDateTime startTime, LocalDateTime endTime) throws SQLException;
    
    /**
     * 根据多个条件筛选兑换记录
     * @param status 状态筛选
     * @param category 商品类别筛选
     * @param fromDate 开始日期
     * @param toDate 结束日期
     * @param member 会员搜索（用户名或姓名）
     * @return 筛选后的兑换记录列表
     * @throws SQLException 数据库操作异常
     */
    List<RedemptionRecord> findByFilters(String status, String category, 
                                       java.util.Date fromDate, java.util.Date toDate, 
                                       String member) throws SQLException;
    
    /**
     * 获取记录总数
     * @return 记录总数
     * @throws SQLException 数据库操作异常
     */
    int countAll() throws SQLException;
    
    /**
     * 根据状态统计记录数
     * @param status 状态
     * @return 指定状态的记录数
     * @throws SQLException 数据库操作异常
     */
    int countByStatus(String status) throws SQLException;
    
    /**
     * 根据日期范围统计记录数
     * @param fromDate 开始日期
     * @param toDate 结束日期
     * @return 指定日期范围的记录数
     * @throws SQLException 数据库操作异常
     */
    int countByDateRange(java.util.Date fromDate, java.util.Date toDate) throws SQLException;
    
    /**
     * 兑换统计信息内部类
     */
    public static class RedemptionStatistics {
        private int totalRecords;
        private int totalPointsUsed;
        private int completedRecords;
        private int pendingRecords;
        private int cancelledRecords;
        
        // 构造函数
        public RedemptionStatistics(int totalRecords, int totalPointsUsed, 
                                  int completedRecords, int pendingRecords, int cancelledRecords) {
            this.totalRecords = totalRecords;
            this.totalPointsUsed = totalPointsUsed;
            this.completedRecords = completedRecords;
            this.pendingRecords = pendingRecords;
            this.cancelledRecords = cancelledRecords;
        }
        
        // Getter方法
        public int getTotalRecords() { return totalRecords; }
        public int getTotalPointsUsed() { return totalPointsUsed; }
        public int getCompletedRecords() { return completedRecords; }
        public int getPendingRecords() { return pendingRecords; }
        public int getCancelledRecords() { return cancelledRecords; }
    }
}