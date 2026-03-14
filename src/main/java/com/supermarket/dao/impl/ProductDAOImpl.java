package com.supermarket.dao.impl;

import com.supermarket.dao.ProductDAO;
import com.supermarket.model.Product;
import com.supermarket.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品数据访问层实现类
 * 
 * @author SupermarketSystem
 * @version 1.0
 */
public class ProductDAOImpl implements ProductDAO {
    
    @Override
    public Product findByBarcode(String barcode) throws SQLException {
        String sql = "SELECT * FROM products WHERE barcode = ? AND is_available = 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, barcode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public Product findById(int productId) throws SQLException {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public List<Product> findAllAvailable() throws SQLException {
        String sql = "SELECT * FROM products WHERE is_available = 1 ORDER BY name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }
    
    @Override
    public List<Product> findByCategory(String category) throws SQLException {
        String sql = "SELECT * FROM products WHERE category = ? AND is_available = 1 ORDER BY name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }
        return products;
    }
    
    @Override
    public List<Product> searchByName(String keyword) throws SQLException {
        String sql = "SELECT * FROM products WHERE name LIKE ? AND is_available = 1 ORDER BY name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + keyword + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }
        return products;
    }
    
    @Override
    public boolean updateStock(int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = ? WHERE product_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean decreaseStock(int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? " +
                    "WHERE product_id = ? AND stock_quantity >= ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean checkStockAvailability(int productId, int requiredQuantity) throws SQLException {
        String sql = "SELECT stock_quantity FROM products WHERE product_id = ? AND is_available = 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int currentStock = rs.getInt("stock_quantity");
                    return currentStock >= requiredQuantity;
                }
            }
        }
        return false;
    }
    
    @Override
    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM products";
        
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
        String sql = "SELECT COUNT(*) FROM products WHERE is_available = 1";
        
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
     * 将ResultSet映射为Product对象
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setName(rs.getString("name"));
        product.setBarcode(rs.getString("barcode"));
        product.setCategory(rs.getString("category"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setAvailable(rs.getBoolean("is_available"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            product.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        // 根据商品分类设置积分倍率
        setPointsMultiplierByCategory(product);
        
        return product;
    }
    
    /**
     * 根据商品分类设置积分倍率
     */
    private void setPointsMultiplierByCategory(Product product) {
        String category = product.getCategory();
        if (category != null) {
            switch (category.toLowerCase()) {
                case "食品":
                case "饮料":
                    product.setPointsMultiplier(1.0); // 基础倍率
                    break;
                case "日用品":
                    product.setPointsMultiplier(1.2); // 1.2倍积分
                    break;
                case "服装":
                case "电子产品":
                    product.setPointsMultiplier(1.5); // 1.5倍积分
                    break;
                case "奢侈品":
                    product.setPointsMultiplier(2.0); // 2倍积分
                    break;
                default:
                    product.setPointsMultiplier(1.0); // 默认基础倍率
                    break;
            }
        } else {
            product.setPointsMultiplier(1.0);
        }
    }
}