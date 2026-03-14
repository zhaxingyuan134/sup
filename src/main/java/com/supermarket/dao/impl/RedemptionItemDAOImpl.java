package com.supermarket.dao.impl;

import com.supermarket.dao.RedemptionItemDAO;
import com.supermarket.model.RedemptionItem;
import com.supermarket.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 兑换商品DAO实现类
 * 
 * @author SupermarketSystem
 * @version 1.0
 */
public class RedemptionItemDAOImpl implements RedemptionItemDAO {
    
    @Override
    public boolean createItem(RedemptionItem item) throws SQLException {
        String sql = "INSERT INTO redemption_items (item_name, category, points_required, " +
                    "description, stock_quantity, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getCategory());
            stmt.setInt(3, item.getPointsRequired());
            stmt.setString(4, item.getDescription());
            stmt.setInt(5, item.getStockQuantity());
            stmt.setBoolean(6, item.isActive());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        item.setItemId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    @Override
    public RedemptionItem findById(int itemId) throws SQLException {
        String sql = "SELECT * FROM redemption_items WHERE item_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, itemId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToItem(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public List<RedemptionItem> findAll() throws SQLException {
        String sql = "SELECT * FROM redemption_items ORDER BY created_at DESC";
        List<RedemptionItem> items = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        }
        return items;
    }
    
    @Override
    public List<RedemptionItem> findAvailableItems() throws SQLException {
        String sql = "SELECT * FROM redemption_items WHERE is_active = TRUE AND stock_quantity > 0 " +
                    "ORDER BY points_required ASC";
        List<RedemptionItem> items = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        }
        return items;
    }
    
    @Override
    public List<RedemptionItem> findByCategory(String category) throws SQLException {
        String sql = "SELECT * FROM redemption_items WHERE category = ? AND is_active = TRUE " +
                    "ORDER BY points_required ASC";
        List<RedemptionItem> items = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        }
        return items;
    }
    
    @Override
    public List<RedemptionItem> findByPointsRange(int minPoints, int maxPoints) throws SQLException {
        String sql = "SELECT * FROM redemption_items WHERE points_required BETWEEN ? AND ? " +
                    "AND is_active = TRUE ORDER BY points_required ASC";
        List<RedemptionItem> items = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, minPoints);
            stmt.setInt(2, maxPoints);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        }
        return items;
    }
    
    @Override
    public boolean updateItem(RedemptionItem item) throws SQLException {
        String sql = "UPDATE redemption_items SET item_name = ?, category = ?, points_required = ?, " +
                    "description = ?, stock_quantity = ?, is_active = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE item_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getCategory());
            stmt.setInt(3, item.getPointsRequired());
            stmt.setString(4, item.getDescription());
            stmt.setInt(5, item.getStockQuantity());
            stmt.setBoolean(6, item.isActive());
            stmt.setInt(7, item.getItemId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean updateStock(int itemId, int quantity) throws SQLException {
        String sql = "UPDATE redemption_items SET stock_quantity = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE item_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantity);
            stmt.setInt(2, itemId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean decreaseStock(int itemId, int quantity) throws SQLException {
        String sql = "UPDATE redemption_items SET stock_quantity = stock_quantity - ?, " +
                    "updated_at = CURRENT_TIMESTAMP WHERE item_id = ? AND stock_quantity >= ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantity);
            stmt.setInt(2, itemId);
            stmt.setInt(3, quantity);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean toggleStatus(int itemId) throws SQLException {
        String sql = "UPDATE redemption_items SET is_active = NOT is_active, " +
                    "updated_at = CURRENT_TIMESTAMP WHERE item_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, itemId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean deleteItem(int itemId) throws SQLException {
        String sql = "DELETE FROM redemption_items WHERE item_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, itemId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM redemption_items";
        
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
    public int getAvailableCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM redemption_items WHERE is_active = TRUE AND stock_quantity > 0";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * 将ResultSet映射为RedemptionItem对象
     */
    private RedemptionItem mapResultSetToItem(ResultSet rs) throws SQLException {
        RedemptionItem item = new RedemptionItem();
        item.setItemId(rs.getInt("item_id"));
        item.setItemName(rs.getString("item_name"));
        item.setCategory(rs.getString("category"));
        item.setPointsRequired(rs.getInt("points_required"));
        item.setDescription(rs.getString("description"));
        item.setStockQuantity(rs.getInt("stock_quantity"));
        item.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            item.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            item.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return item;
    }
}