package com.supermarket.dao.impl;

import com.supermarket.dao.RedemptionRecordDAO;
import com.supermarket.model.RedemptionRecord;
import com.supermarket.util.DatabaseUtil;
import com.supermarket.util.CacheManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 兑换记录DAO实现类
 * 集成缓存机制以提升查询性能
 * 
 * @author SupermarketSystem
 * @version 1.0
 */
public class RedemptionRecordDAOImpl implements RedemptionRecordDAO {
    
    private final CacheManager cacheManager = CacheManager.getInstance();
    
    // 缓存TTL配置（毫秒）
    private static final long RECORD_CACHE_TTL = 300000; // 5分钟
    private static final long STATS_CACHE_TTL = 60000;   // 1分钟
    
    @Override
    public boolean createRecord(RedemptionRecord record) throws SQLException {
        String sql = "INSERT INTO redemption_records (user_id, item_id, points_used, quantity, " +
                    "redemption_date, status, cashier_id, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, record.getUserId());
            stmt.setInt(2, record.getItemId());
            stmt.setInt(3, record.getPointsUsed());
            stmt.setInt(4, record.getQuantity());
            stmt.setTimestamp(5, Timestamp.valueOf(record.getRedemptionDate()));
            stmt.setString(6, record.getStatus());
            
            if (record.getCashierId() != null) {
                stmt.setInt(7, record.getCashierId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            stmt.setString(8, record.getNotes());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        record.setRecordId(generatedKeys.getInt(1));
                    }
                }
                
                // 清除相关缓存
                cacheManager.clearGroup(CacheManager.Groups.REDEMPTION_RECORDS);
                cacheManager.clearGroup(CacheManager.Groups.STATISTICS);
                return true;
            }
            
            return false;
        }
    }
    
    @Override
    public RedemptionRecord findById(int recordId) throws SQLException {
        String cacheKey = "redemption_record_" + recordId;
        
        // 尝试从缓存获取
        RedemptionRecord cached = (RedemptionRecord) cacheManager.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        String sql = "SELECT rr.*, u.username as user_name, ri.item_name, " +
                    "c.username as cashier_name FROM redemption_records rr " +
                    "LEFT JOIN users u ON rr.user_id = u.user_id " +
                    "LEFT JOIN redemption_items ri ON rr.item_id = ri.item_id " +
                    "LEFT JOIN users c ON rr.cashier_id = c.user_id " +
                    "WHERE rr.record_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, recordId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    RedemptionRecord record = mapResultSetToRecord(rs);
                    // 缓存结果
                    cacheManager.putInGroup(CacheManager.Groups.REDEMPTION_RECORDS, cacheKey, record, RECORD_CACHE_TTL);
                    return record;
                }
            }
        }
        return null;
    }
    
    @Override
    public List<RedemptionRecord> findAll() throws SQLException {
        String cacheKey = "all_redemption_records";
        
        // 尝试从缓存获取
        @SuppressWarnings("unchecked")
        List<RedemptionRecord> cached = (List<RedemptionRecord>) cacheManager.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        String sql = "SELECT rr.*, u.username as user_name, ri.item_name, " +
                    "c.username as cashier_name FROM redemption_records rr " +
                    "LEFT JOIN users u ON rr.user_id = u.user_id " +
                    "LEFT JOIN redemption_items ri ON rr.item_id = ri.item_id " +
                    "LEFT JOIN users c ON rr.cashier_id = c.user_id " +
                    "ORDER BY rr.redemption_date DESC";
        List<RedemptionRecord> records = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                records.add(mapResultSetToRecord(rs));
            }
        }
        
        // 缓存结果
        cacheManager.putInGroup(CacheManager.Groups.REDEMPTION_RECORDS, cacheKey, records, RECORD_CACHE_TTL);
        return records;
    }
    
    @Override
    public List<RedemptionRecord> findByUserId(int userId) throws SQLException {
        String cacheKey = "redemption_records_user_" + userId;
        
        // 尝试从缓存获取
        @SuppressWarnings("unchecked")
        List<RedemptionRecord> cached = (List<RedemptionRecord>) cacheManager.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        String sql = "SELECT rr.*, u.username as user_name, ri.item_name, " +
                    "c.username as cashier_name FROM redemption_records rr " +
                    "LEFT JOIN users u ON rr.user_id = u.user_id " +
                    "LEFT JOIN redemption_items ri ON rr.item_id = ri.item_id " +
                    "LEFT JOIN users c ON rr.cashier_id = c.user_id " +
                    "WHERE rr.user_id = ? ORDER BY rr.redemption_date DESC";
        List<RedemptionRecord> records = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToRecord(rs));
                }
            }
        }
        
        // 缓存结果
        cacheManager.putInGroup(CacheManager.Groups.REDEMPTION_RECORDS, cacheKey, records, RECORD_CACHE_TTL);
        return records;
    }

    @Override
    public List<RedemptionRecord> findByItemId(int itemId) throws SQLException {
        String sql = "SELECT rr.*, u.username as user_name, ri.item_name, " +
                    "c.username as cashier_name FROM redemption_records rr " +
                    "LEFT JOIN users u ON rr.user_id = u.user_id " +
                    "LEFT JOIN redemption_items ri ON rr.item_id = ri.item_id " +
                    "LEFT JOIN users c ON rr.cashier_id = c.user_id " +
                    "WHERE rr.item_id = ? ORDER BY rr.redemption_date DESC";
        List<RedemptionRecord> records = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, itemId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToRecord(rs));
                }
            }
        }
        return records;
    }
    
    @Override
    public List<RedemptionRecord> findByStatus(String status) throws SQLException {
        String sql = "SELECT rr.*, u.username as user_name, ri.item_name, " +
                    "c.username as cashier_name FROM redemption_records rr " +
                    "LEFT JOIN users u ON rr.user_id = u.user_id " +
                    "LEFT JOIN redemption_items ri ON rr.item_id = ri.item_id " +
                    "LEFT JOIN users c ON rr.cashier_id = c.user_id " +
                    "WHERE rr.status = ? ORDER BY rr.redemption_date DESC";
        List<RedemptionRecord> records = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToRecord(rs));
                }
            }
        }
        return records;
    }
    
    @Override
    public List<RedemptionRecord> findByDateRange(LocalDateTime startTime, LocalDateTime endTime) throws SQLException {
        String sql = "SELECT rr.*, u.username as user_name, ri.item_name, " +
                    "c.username as cashier_name FROM redemption_records rr " +
                    "LEFT JOIN users u ON rr.user_id = u.user_id " +
                    "LEFT JOIN redemption_items ri ON rr.item_id = ri.item_id " +
                    "LEFT JOIN users c ON rr.cashier_id = c.user_id " +
                    "WHERE rr.redemption_date BETWEEN ? AND ? ORDER BY rr.redemption_date DESC";
        List<RedemptionRecord> records = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(startTime));
            stmt.setTimestamp(2, Timestamp.valueOf(endTime));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToRecord(rs));
                }
            }
        }
        return records;
    }
    
    @Override
    public List<RedemptionRecord> findByCashierId(int cashierId) throws SQLException {
        String sql = "SELECT rr.*, u.username as user_name, ri.item_name, " +
                    "c.username as cashier_name FROM redemption_records rr " +
                    "LEFT JOIN users u ON rr.user_id = u.user_id " +
                    "LEFT JOIN redemption_items ri ON rr.item_id = ri.item_id " +
                    "LEFT JOIN users c ON rr.cashier_id = c.user_id " +
                    "WHERE rr.cashier_id = ? ORDER BY rr.redemption_date DESC";
        List<RedemptionRecord> records = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cashierId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToRecord(rs));
                }
            }
        }
        return records;
    }
    
    @Override
    public boolean updateStatus(int recordId, String status) throws SQLException {
        String sql = "UPDATE redemption_records SET status = ? WHERE record_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, recordId);
            
            boolean result = stmt.executeUpdate() > 0;
            if (result) {
                // 清除相关缓存
                cacheManager.clearGroup(CacheManager.Groups.REDEMPTION_RECORDS);
                cacheManager.clearGroup(CacheManager.Groups.STATISTICS);
            }
            return result;
        }
    }
    
    @Override
    public boolean updateNotes(int recordId, String notes) throws SQLException {
        String sql = "UPDATE redemption_records SET notes = ? WHERE record_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, notes);
            stmt.setInt(2, recordId);
            
            boolean result = stmt.executeUpdate() > 0;
            if (result) {
                // 清除相关缓存
                cacheManager.clearGroup(CacheManager.Groups.REDEMPTION_RECORDS);
            }
            return result;
        }
    }
    
    @Override
    public boolean deleteRecord(int recordId) throws SQLException {
        String sql = "DELETE FROM redemption_records WHERE record_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, recordId);
            
            boolean result = stmt.executeUpdate() > 0;
            if (result) {
                // 清除相关缓存
                cacheManager.clearGroup(CacheManager.Groups.REDEMPTION_RECORDS);
                cacheManager.clearGroup(CacheManager.Groups.STATISTICS);
            }
            return result;
        }
    }
    
    @Override
    public int getTotalCount() throws SQLException {
        String cacheKey = "redemption_total_count";
        
        // 尝试从缓存获取
        Integer cached = cacheManager.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        String sql = "SELECT COUNT(*) FROM redemption_records";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt(1);
                // 缓存结果
                cacheManager.putInGroup(CacheManager.Groups.STATISTICS, cacheKey, count, STATS_CACHE_TTL);
                return count;
            }
        }
        return 0;
    }
    
    @Override
    public int getCountByUserId(int userId) throws SQLException {
        String cacheKey = "redemption_count_user_" + userId;
        
        // 尝试从缓存获取
        Integer cached = cacheManager.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        String sql = "SELECT COUNT(*) FROM redemption_records WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    // 缓存结果
                    cacheManager.putInGroup(CacheManager.Groups.STATISTICS, cacheKey, count, STATS_CACHE_TTL);
                    return count;
                }
            }
        }
        return 0;
    }
    
    @Override
    public RedemptionStatistics getStatistics(LocalDateTime startTime, LocalDateTime endTime) throws SQLException {
        String sql = "SELECT " +
                    "COUNT(*) as total_records, " +
                    "COALESCE(SUM(points_used * quantity), 0) as total_points_used, " +
                    "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_records, " +
                    "SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) as pending_records, " +
                    "SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled_records " +
                    "FROM redemption_records WHERE redemption_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(startTime));
            stmt.setTimestamp(2, Timestamp.valueOf(endTime));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new RedemptionStatistics(
                        rs.getInt("total_records"),
                        rs.getInt("total_points_used"),
                        rs.getInt("completed_records"),
                        rs.getInt("pending_records"),
                        rs.getInt("cancelled_records")
                    );
                }
            }
        }
        return new RedemptionStatistics(0, 0, 0, 0, 0);
    }
    
    @Override
    public List<RedemptionRecord> findByFilters(String status, String category, 
                                              java.util.Date fromDate, java.util.Date toDate, 
                                              String member) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT rr.*, u.username as user_name, u.name as member_name, ");
        sql.append("ri.item_name, ri.category as item_category, ");
        sql.append("c.username as cashier_name FROM redemption_records rr ");
        sql.append("LEFT JOIN users u ON rr.user_id = u.user_id ");
        sql.append("LEFT JOIN redemption_items ri ON rr.item_id = ri.item_id ");
        sql.append("LEFT JOIN users c ON rr.cashier_id = c.user_id ");
        sql.append("WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (status != null && !status.isEmpty()) {
            sql.append("AND rr.status = ? ");
            params.add(status);
        }
        
        if (category != null && !category.isEmpty()) {
            sql.append("AND ri.category = ? ");
            params.add(category);
        }
        
        if (fromDate != null) {
            sql.append("AND rr.redemption_date >= ? ");
            params.add(new Timestamp(fromDate.getTime()));
        }
        
        if (toDate != null) {
            sql.append("AND rr.redemption_date <= ? ");
            params.add(new Timestamp(toDate.getTime()));
        }
        
        if (member != null && !member.isEmpty()) {
            sql.append("AND (u.username LIKE ? OR u.name LIKE ?) ");
            String searchPattern = "%" + member + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        sql.append("ORDER BY rr.redemption_date DESC");
        
        List<RedemptionRecord> records = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToRecordWithDetails(rs));
                }
            }
        }
        
        return records;
    }
    
    @Override
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM redemption_records";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
    
    @Override
    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM redemption_records WHERE status = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }
    
    @Override
    public int countByDateRange(java.util.Date fromDate, java.util.Date toDate) throws SQLException {
        String sql = "SELECT COUNT(*) FROM redemption_records WHERE redemption_date >= ? AND redemption_date <= ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, new Timestamp(fromDate.getTime()));
            stmt.setTimestamp(2, new Timestamp(toDate.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }
    
    /**
     * 将ResultSet映射为RedemptionRecord对象（包含详细信息）
     */
    private RedemptionRecord mapResultSetToRecordWithDetails(ResultSet rs) throws SQLException {
        RedemptionRecord record = new RedemptionRecord();
        record.setRecordId(rs.getInt("record_id"));
        record.setUserId(rs.getInt("user_id"));
        record.setItemId(rs.getInt("item_id"));
        record.setPointsUsed(rs.getInt("points_used"));
        record.setQuantity(rs.getInt("quantity"));
        record.setStatus(rs.getString("status"));
        record.setNotes(rs.getString("notes"));
        
        Timestamp redemptionDate = rs.getTimestamp("redemption_date");
        if (redemptionDate != null) {
            record.setRedemptionDate(redemptionDate.toLocalDateTime());
        }
        
        int cashierId = rs.getInt("cashier_id");
        if (!rs.wasNull()) {
            record.setCashierId(cashierId);
        }
        
        // 设置关联对象信息
        record.setUserName(rs.getString("user_name"));
        record.setItemName(rs.getString("item_name"));
        record.setCashierName(rs.getString("cashier_name"));
        
        return record;
    }
    
    /**
     * 将ResultSet映射为RedemptionRecord对象
     */
    private RedemptionRecord mapResultSetToRecord(ResultSet rs) throws SQLException {
        RedemptionRecord record = new RedemptionRecord();
        record.setRecordId(rs.getInt("record_id"));
        record.setUserId(rs.getInt("user_id"));
        record.setItemId(rs.getInt("item_id"));
        record.setPointsUsed(rs.getInt("points_used"));
        record.setQuantity(rs.getInt("quantity"));
        record.setStatus(rs.getString("status"));
        record.setNotes(rs.getString("notes"));
        
        Timestamp redemptionDate = rs.getTimestamp("redemption_date");
        if (redemptionDate != null) {
            record.setRedemptionDate(redemptionDate.toLocalDateTime());
        }
        
        int cashierId = rs.getInt("cashier_id");
        if (!rs.wasNull()) {
            record.setCashierId(cashierId);
        }
        
        // 设置关联对象信息
        record.setUserName(rs.getString("user_name"));
        record.setItemName(rs.getString("item_name"));
        record.setCashierName(rs.getString("cashier_name"));
        
        return record;
    }
}