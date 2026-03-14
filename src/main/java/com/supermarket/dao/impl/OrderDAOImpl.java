package com.supermarket.dao.impl;

import com.supermarket.dao.OrderDAO;
import com.supermarket.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单数据访问层实现类
 */
public class OrderDAOImpl implements OrderDAO {
    
    @Override
    public BigDecimal getTodaySales() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE DATE(created_at) = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, today);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal getSalesByDateRange(String startDate, String endDate) {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE DATE(created_at) BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }
    
    @Override
    public int getTodayOrderCount() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String sql = "SELECT COUNT(*) FROM orders WHERE DATE(created_at) = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, today);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public List<Map<String, Object>> getSalesTrend(int days) {
        List<Map<String, Object>> trendData = new ArrayList<>();
        String sql = "SELECT DATE(created_at) as sale_date, COALESCE(SUM(total_amount), 0) as daily_sales " +
                    "FROM orders WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                    "GROUP BY DATE(created_at) ORDER BY sale_date";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, days);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> dayData = new HashMap<>();
                    dayData.put("date", rs.getString("sale_date"));
                    dayData.put("sales", rs.getBigDecimal("daily_sales"));
                    trendData.add(dayData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return trendData;
    }
}