package com.supermarket.dao.impl;

import com.supermarket.dao.PointTransactionDAO;
import com.supermarket.model.PointTransaction;
import com.supermarket.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 积分交易记录数据访问对象实现类
 */
public class PointTransactionDAOImpl implements PointTransactionDAO {
    
    @Override
    public boolean createTransaction(PointTransaction transaction) throws SQLException {
        String sql = "INSERT INTO point_transactions (user_id, transaction_type, points, description, transaction_date, cashier_id, order_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, transaction.getUserId());
            stmt.setString(2, transaction.getTransactionType().getCode());
            stmt.setInt(3, transaction.getPoints());
            stmt.setString(4, transaction.getDescription());
            stmt.setTimestamp(5, Timestamp.valueOf(transaction.getTransactionDate()));
            
            if (transaction.getCashierId() != null) {
                stmt.setInt(6, transaction.getCashierId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            if (transaction.getOrderId() != null) {
                stmt.setString(7, transaction.getOrderId());
            } else {
                stmt.setNull(7, Types.VARCHAR);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaction.setTransactionId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
            return false;
        }
    }
    
    @Override
    public List<PointTransaction> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM point_transactions WHERE user_id = ? ORDER BY transaction_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return mapResultSetToList(rs);
            }
        }
    }
    
    @Override
    public List<PointTransaction> findByUserIdAndType(int userId, PointTransaction.TransactionType transactionType) throws SQLException {
        String sql = "SELECT * FROM point_transactions WHERE user_id = ? AND transaction_type = ? ORDER BY transaction_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, transactionType.getCode());
            
            try (ResultSet rs = stmt.executeQuery()) {
                return mapResultSetToList(rs);
            }
        }
    }
    
    @Override
    public PointTransaction findById(int transactionId) throws SQLException {
        String sql = "SELECT * FROM point_transactions WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, transactionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToObject(rs);
                }
                return null;
            }
        }
    }
    
    @Override
    public List<PointTransaction> findRecentByUserId(int userId, int limit) throws SQLException {
        String sql = "SELECT * FROM point_transactions WHERE user_id = ? ORDER BY transaction_date DESC LIMIT ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return mapResultSetToList(rs);
            }
        }
    }
    
    @Override
    public boolean deleteTransaction(int transactionId) throws SQLException {
        String sql = "DELETE FROM point_transactions WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, transactionId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * 将ResultSet映射为PointTransaction对象列表
     */
    private List<PointTransaction> mapResultSetToList(ResultSet rs) throws SQLException {
        List<PointTransaction> transactions = new ArrayList<>();
        
        while (rs.next()) {
            transactions.add(mapResultSetToObject(rs));
        }
        
        return transactions;
    }
    
    /**
     * 将ResultSet的当前行映射为PointTransaction对象
     */
    private PointTransaction mapResultSetToObject(ResultSet rs) throws SQLException {
        PointTransaction transaction = new PointTransaction();
        
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setUserId(rs.getInt("user_id"));
        transaction.setTransactionType(PointTransaction.TransactionType.fromCode(rs.getString("transaction_type")));
        transaction.setPoints(rs.getInt("points"));
        transaction.setDescription(rs.getString("description"));
        
        Timestamp timestamp = rs.getTimestamp("transaction_date");
        if (timestamp != null) {
            transaction.setTransactionDate(timestamp.toLocalDateTime());
        }
        
        int cashierId = rs.getInt("cashier_id");
        if (rs.wasNull()) {
            transaction.setCashierId(null);
        } else {
            transaction.setCashierId(cashierId);
        }
        
        transaction.setOrderId(rs.getString("order_id"));
        
        return transaction;
    }
}
