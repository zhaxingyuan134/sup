package com.supermarket.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.dao.UserDAO;
import com.supermarket.dao.impl.UserDAOImpl;
import com.supermarket.model.User;
import com.supermarket.util.APIOptimizer;
import com.supermarket.util.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 增强版数据分析API接口
 * 提供全面的统计分析功能，包括促销活动效果分析、商品热销分析、会员行为分析等
 */
@WebServlet("/api/advanced-analytics/*")
public class AdvancedDataAnalysisServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAOImpl();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            if (!validateManagerPermission(request, response)) {
                return;
            }
            
            // 设置缓存头（数据分析结果缓存10分钟）
            APIOptimizer.setCacheHeaders(response, 10 * 60);
            
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "请指定分析类型");
            } else if (pathInfo.equals("/promotion-effectiveness")) {
                getPromotionEffectivenessAnalysis(request, response);
            } else if (pathInfo.equals("/product-performance")) {
                getProductPerformanceAnalysis(request, response);
            } else if (pathInfo.equals("/member-behavior")) {
                getMemberBehaviorAnalysis(request, response);
            } else if (pathInfo.equals("/sales-forecast")) {
                getSalesForecastAnalysis(request, response);
            } else if (pathInfo.equals("/inventory-optimization")) {
                getInventoryOptimizationAnalysis(request, response);
            } else if (pathInfo.equals("/profit-analysis")) {
                getProfitAnalysis(request, response);
            } else if (pathInfo.equals("/comparative-analysis")) {
                getComparativeAnalysis(request, response);
            } else if (pathInfo.equals("/real-time-metrics")) {
                getRealTimeMetrics(request, response);
            } else {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "分析接口不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误: " + e.getMessage());
        }
    }
    
    /**
     * 验证经理权限
     */
    private boolean validateManagerPermission(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "未登录");
            return false;
        }
        
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.MANAGER) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "权限不足");
            return false;
        }
        
        return true;
    }
    
    /**
     * 促销活动效果分析
     */
    private void getPromotionEffectivenessAnalysis(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String promotionId = request.getParameter("promotionId");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            
            Map<String, Object> analysis = new HashMap<>();
            
            // 促销活动基本信息
            String promotionInfoQuery = "SELECT p.*, " +
                                       "COUNT(pu.usage_id) as usage_count, " +
                                       "SUM(pu.usage_amount) as total_usage_amount, " +
                                       "AVG(pu.usage_amount) as avg_usage_amount " +
                                       "FROM promotions p " +
                                       "LEFT JOIN member_promotion_usage pu ON p.promotion_id = pu.promotion_id " +
                                       "WHERE p.promotion_id = ? " +
                                       "GROUP BY p.promotion_id";
            
            Map<String, Object> promotionInfo = new HashMap<>();
            try (PreparedStatement stmt = conn.prepareStatement(promotionInfoQuery)) {
                stmt.setString(1, promotionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        promotionInfo.put("promotionId", rs.getInt("promotion_id"));
                        promotionInfo.put("promotionName", rs.getString("promotion_name"));
                        promotionInfo.put("promotionType", rs.getString("promotion_type"));
                        promotionInfo.put("startDate", rs.getDate("start_date"));
                        promotionInfo.put("endDate", rs.getDate("end_date"));
                        promotionInfo.put("usageCount", rs.getInt("usage_count"));
                        promotionInfo.put("totalUsageAmount", rs.getDouble("total_usage_amount"));
                        promotionInfo.put("avgUsageAmount", rs.getDouble("avg_usage_amount"));
                    }
                }
            }
            analysis.put("promotionInfo", promotionInfo);
            
            // 促销活动参与度分析
            String participationQuery = "SELECT " +
                                      "COUNT(DISTINCT pu.member_id) as unique_participants, " +
                                      "COUNT(pu.usage_id) as total_usage, " +
                                      "AVG(pu.usage_amount) as avg_transaction_amount, " +
                                      "MAX(pu.usage_amount) as max_transaction_amount, " +
                                      "MIN(pu.usage_amount) as min_transaction_amount " +
                                      "FROM member_promotion_usage pu " +
                                      "WHERE pu.promotion_id = ?";
            
            Map<String, Object> participation = new HashMap<>();
            try (PreparedStatement stmt = conn.prepareStatement(participationQuery)) {
                stmt.setString(1, promotionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        participation.put("uniqueParticipants", rs.getInt("unique_participants"));
                        participation.put("totalUsage", rs.getInt("total_usage"));
                        participation.put("avgTransactionAmount", rs.getDouble("avg_transaction_amount"));
                        participation.put("maxTransactionAmount", rs.getDouble("max_transaction_amount"));
                        participation.put("minTransactionAmount", rs.getDouble("min_transaction_amount"));
                    }
                }
            }
            analysis.put("participation", participation);
            
            // 促销活动ROI分析（基于积分交易推算）
            String roiQuery = "SELECT " +
                            "SUM(CASE WHEN pt.transaction_type = 'EARN' THEN pt.points ELSE 0 END) as total_earned_points, " +
                            "SUM(CASE WHEN pt.transaction_type = 'REDEEM' THEN pt.points ELSE 0 END) as total_redeemed_points, " +
                            "COUNT(DISTINCT pt.user_id) as affected_members " +
                            "FROM point_transactions pt " +
                            "WHERE pt.transaction_date BETWEEN ? AND ? " +
                            "AND pt.description LIKE '%促销%'";
            
            Map<String, Object> roi = new HashMap<>();
            try (PreparedStatement stmt = conn.prepareStatement(roiQuery)) {
                stmt.setString(1, startDate != null ? startDate : "2024-01-01");
                stmt.setString(2, endDate != null ? endDate : LocalDate.now().toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int earnedPoints = rs.getInt("total_earned_points");
                        int redeemedPoints = rs.getInt("total_redeemed_points");
                        roi.put("totalEarnedPoints", earnedPoints);
                        roi.put("totalRedeemedPoints", redeemedPoints);
                        roi.put("netPointsImpact", earnedPoints - redeemedPoints);
                        roi.put("affectedMembers", rs.getInt("affected_members"));
                        roi.put("estimatedRevenue", earnedPoints * 1.0); // 假设1积分=1元消费
                    }
                }
            }
            analysis.put("roi", roi);
            
            APIOptimizer.sendSuccessResponse(response, analysis);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "促销效果分析失败", e.getMessage());
        }
    }
    
    /**
     * 商品性能分析
     */
    private void getProductPerformanceAnalysis(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String category = request.getParameter("category");
            String period = request.getParameter("period"); // week, month, quarter
            
            Map<String, Object> analysis = new HashMap<>();
            
            // 构建时间条件
            String timeCondition = "";
            switch (period != null ? period : "month") {
                case "week":
                    timeCondition = "AND pt.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
                    break;
                case "month":
                    timeCondition = "AND pt.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)";
                    break;
                case "quarter":
                    timeCondition = "AND pt.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 90 DAY)";
                    break;
            }
            
            // 商品性能指标
            String performanceQuery = "SELECT p.id, p.name, p.category, p.price, p.stock_quantity, " +
                                    "(100 - p.stock_quantity) as estimated_sales, " +
                                    "COUNT(pt.id) as transaction_frequency, " +
                                    "SUM(pt.points) as total_points_generated, " +
                                    "AVG(pt.points) as avg_points_per_transaction, " +
                                    "((100 - p.stock_quantity) * p.price) as estimated_revenue, " +
                                    "CASE " +
                                    "  WHEN p.stock_quantity < 10 THEN 'LOW' " +
                                    "  WHEN p.stock_quantity < 50 THEN 'MEDIUM' " +
                                    "  ELSE 'HIGH' " +
                                    "END as stock_status " +
                                    "FROM products p " +
                                    "LEFT JOIN point_transactions pt ON pt.description LIKE CONCAT('%', p.name, '%') " +
                                    "WHERE p.is_available = 1 " + timeCondition +
                                    (category != null && !category.isEmpty() && !"all".equals(category) ? " AND p.category = ?" : "") + " " +
                                    "GROUP BY p.id, p.name, p.category, p.price, p.stock_quantity " +
                                    "ORDER BY estimated_sales DESC, transaction_frequency DESC " +
                                    "LIMIT 50";
            
            List<Map<String, Object>> productPerformance = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(performanceQuery)) {
                int paramIndex = 1;
                if (category != null && !category.isEmpty() && !"all".equals(category)) {
                    stmt.setString(paramIndex++, category);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> product = new HashMap<>();
                        product.put("id", rs.getInt("id"));
                        product.put("name", rs.getString("name"));
                        product.put("category", rs.getString("category"));
                        product.put("price", rs.getDouble("price"));
                        product.put("stockQuantity", rs.getInt("stock_quantity"));
                        product.put("estimatedSales", rs.getInt("estimated_sales"));
                        product.put("transactionFrequency", rs.getInt("transaction_frequency"));
                        product.put("totalPointsGenerated", rs.getInt("total_points_generated"));
                        product.put("avgPointsPerTransaction", rs.getDouble("avg_points_per_transaction"));
                        product.put("estimatedRevenue", rs.getDouble("estimated_revenue"));
                        product.put("stockStatus", rs.getString("stock_status"));
                        
                        // 计算性能评分（综合销量、频次、收入）
                        double performanceScore = (rs.getInt("estimated_sales") * 0.4) + 
                                                (rs.getInt("transaction_frequency") * 0.3) + 
                                                (rs.getDouble("estimated_revenue") / 100 * 0.3);
                        product.put("performanceScore", Math.round(performanceScore * 100.0) / 100.0);
                        
                        productPerformance.add(product);
                    }
                }
            }
            analysis.put("productPerformance", productPerformance);
            
            // 分类性能汇总
            String categoryStatsQuery = "SELECT p.category, " +
                                      "COUNT(p.id) as product_count, " +
                                      "SUM(100 - p.stock_quantity) as total_estimated_sales, " +
                                      "AVG(p.price) as avg_price, " +
                                      "SUM((100 - p.stock_quantity) * p.price) as total_estimated_revenue " +
                                      "FROM products p " +
                                      "WHERE p.is_available = 1 " +
                                      "GROUP BY p.category " +
                                      "ORDER BY total_estimated_revenue DESC";
            
            List<Map<String, Object>> categoryStats = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(categoryStatsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> category_stat = new HashMap<>();
                    category_stat.put("category", rs.getString("category"));
                    category_stat.put("productCount", rs.getInt("product_count"));
                    category_stat.put("totalEstimatedSales", rs.getInt("total_estimated_sales"));
                    category_stat.put("avgPrice", rs.getDouble("avg_price"));
                    category_stat.put("totalEstimatedRevenue", rs.getDouble("total_estimated_revenue"));
                    categoryStats.add(category_stat);
                }
            }
            analysis.put("categoryStats", categoryStats);
            
            APIOptimizer.sendSuccessResponse(response, analysis);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "商品性能分析失败", e.getMessage());
        }
    }
    
    /**
     * 会员行为分析
     */
    private void getMemberBehaviorAnalysis(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String memberLevel = request.getParameter("memberLevel");
            String period = request.getParameter("period");
            
            Map<String, Object> analysis = new HashMap<>();
            
            // 会员活跃度分析
            String activityQuery = "SELECT u.membership_level, " +
                                 "COUNT(DISTINCT u.id) as member_count, " +
                                 "COUNT(DISTINCT CASE WHEN pt.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN u.id END) as active_last_week, " +
                                 "COUNT(DISTINCT CASE WHEN pt.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) THEN u.id END) as active_last_month, " +
                                 "AVG(u.points_balance) as avg_points_balance, " +
                                 "SUM(CASE WHEN pt.transaction_type = 'EARN' THEN pt.points ELSE 0 END) as total_earned_points, " +
                                 "SUM(CASE WHEN pt.transaction_type = 'REDEEM' THEN pt.points ELSE 0 END) as total_redeemed_points " +
                                 "FROM users u " +
                                 "LEFT JOIN point_transactions pt ON u.id = pt.user_id " +
                                 "WHERE u.role = 'MEMBER' " +
                                 (memberLevel != null && !memberLevel.isEmpty() && !"all".equals(memberLevel) ? " AND u.membership_level = ?" : "") + " " +
                                 "GROUP BY u.membership_level " +
                                 "ORDER BY member_count DESC";
            
            List<Map<String, Object>> memberActivity = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(activityQuery)) {
                int paramIndex = 1;
                if (memberLevel != null && !memberLevel.isEmpty() && !"all".equals(memberLevel)) {
                    stmt.setString(paramIndex++, memberLevel);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> activity = new HashMap<>();
                        activity.put("membershipLevel", rs.getString("membership_level"));
                        activity.put("memberCount", rs.getInt("member_count"));
                        activity.put("activeLastWeek", rs.getInt("active_last_week"));
                        activity.put("activeLastMonth", rs.getInt("active_last_month"));
                        activity.put("avgPointsBalance", rs.getDouble("avg_points_balance"));
                        activity.put("totalEarnedPoints", rs.getInt("total_earned_points"));
                        activity.put("totalRedeemedPoints", rs.getInt("total_redeemed_points"));
                        
                        // 计算活跃率
                        int memberCount = rs.getInt("member_count");
                        if (memberCount > 0) {
                            activity.put("weeklyActiveRate", Math.round((rs.getInt("active_last_week") * 100.0 / memberCount) * 100.0) / 100.0);
                            activity.put("monthlyActiveRate", Math.round((rs.getInt("active_last_month") * 100.0 / memberCount) * 100.0) / 100.0);
                        }
                        
                        memberActivity.add(activity);
                    }
                }
            }
            analysis.put("memberActivity", memberActivity);
            
            // 消费行为模式分析
            String behaviorQuery = "SELECT " +
                                 "HOUR(pt.transaction_date) as hour_of_day, " +
                                 "DAYOFWEEK(pt.transaction_date) as day_of_week, " +
                                 "COUNT(pt.id) as transaction_count, " +
                                 "AVG(pt.points) as avg_transaction_points " +
                                 "FROM point_transactions pt " +
                                 "WHERE pt.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                                 "AND pt.transaction_type = 'EARN' " +
                                 "GROUP BY HOUR(pt.transaction_date), DAYOFWEEK(pt.transaction_date) " +
                                 "ORDER BY transaction_count DESC " +
                                 "LIMIT 50";
            
            List<Map<String, Object>> behaviorPatterns = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(behaviorQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> pattern = new HashMap<>();
                    pattern.put("hourOfDay", rs.getInt("hour_of_day"));
                    pattern.put("dayOfWeek", rs.getInt("day_of_week"));
                    pattern.put("transactionCount", rs.getInt("transaction_count"));
                    pattern.put("avgTransactionPoints", rs.getDouble("avg_transaction_points"));
                    behaviorPatterns.add(pattern);
                }
            }
            analysis.put("behaviorPatterns", behaviorPatterns);
            
            APIOptimizer.sendSuccessResponse(response, analysis);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "会员行为分析失败", e.getMessage());
        }
    }
    
    /**
     * 销售预测分析
     */
    private void getSalesForecastAnalysis(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            Map<String, Object> forecast = new HashMap<>();
            
            // 基于历史数据的简单线性预测
            String historicalQuery = "SELECT " +
                                   "DATE(pt.transaction_date) as date, " +
                                   "SUM(CASE WHEN pt.transaction_type = 'EARN' THEN pt.points ELSE 0 END) as daily_sales_points, " +
                                   "COUNT(DISTINCT pt.user_id) as daily_customers " +
                                   "FROM point_transactions pt " +
                                   "WHERE pt.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                                   "GROUP BY DATE(pt.transaction_date) " +
                                   "ORDER BY date";
            
            List<Map<String, Object>> historicalData = new ArrayList<>();
            double totalSales = 0;
            int dataPoints = 0;
            
            try (PreparedStatement stmt = conn.prepareStatement(historicalQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> dailyData = new HashMap<>();
                    dailyData.put("date", rs.getDate("date"));
                    dailyData.put("salesPoints", rs.getInt("daily_sales_points"));
                    dailyData.put("customers", rs.getInt("daily_customers"));
                    historicalData.add(dailyData);
                    
                    totalSales += rs.getInt("daily_sales_points");
                    dataPoints++;
                }
            }
            
            // 简单预测（基于平均值和趋势）
            if (dataPoints > 0) {
                double avgDailySales = totalSales / dataPoints;
                
                // 生成未来7天预测
                List<Map<String, Object>> predictions = new ArrayList<>();
                LocalDate today = LocalDate.now();
                
                for (int i = 1; i <= 7; i++) {
                    Map<String, Object> prediction = new HashMap<>();
                    LocalDate futureDate = today.plusDays(i);
                    
                    // 简单的季节性调整（周末+10%，工作日-5%）
                    double seasonalFactor = (futureDate.getDayOfWeek().getValue() >= 6) ? 1.1 : 0.95;
                    double predictedSales = avgDailySales * seasonalFactor;
                    
                    prediction.put("date", futureDate.toString());
                    prediction.put("predictedSales", Math.round(predictedSales));
                    prediction.put("confidence", "medium");
                    predictions.add(prediction);
                }
                
                forecast.put("predictions", predictions);
                forecast.put("avgDailySales", Math.round(avgDailySales));
                forecast.put("dataPoints", dataPoints);
            }
            
            forecast.put("historicalData", historicalData);
            
            APIOptimizer.sendSuccessResponse(response, forecast);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "销售预测分析失败", e.getMessage());
        }
    }
    
    /**
     * 库存优化分析
     */
    private void getInventoryOptimizationAnalysis(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            Map<String, Object> optimization = new HashMap<>();
            
            // 库存状态分析
            String inventoryQuery = "SELECT p.id, p.name, p.category, p.price, p.stock_quantity, " +
                                  "(100 - p.stock_quantity) as estimated_sales, " +
                                  "CASE " +
                                  "  WHEN p.stock_quantity <= 5 THEN 'CRITICAL' " +
                                  "  WHEN p.stock_quantity <= 20 THEN 'LOW' " +
                                  "  WHEN p.stock_quantity <= 50 THEN 'MEDIUM' " +
                                  "  ELSE 'HIGH' " +
                                  "END as stock_level, " +
                                  "CASE " +
                                  "  WHEN (100 - p.stock_quantity) > 80 THEN 'FAST_MOVING' " +
                                  "  WHEN (100 - p.stock_quantity) > 50 THEN 'MEDIUM_MOVING' " +
                                  "  ELSE 'SLOW_MOVING' " +
                                  "END as movement_category " +
                                  "FROM products p " +
                                  "WHERE p.is_available = 1 " +
                                  "ORDER BY p.stock_quantity ASC, estimated_sales DESC";
            
            List<Map<String, Object>> inventoryAnalysis = new ArrayList<>();
            Map<String, Integer> stockLevelCounts = new HashMap<>();
            Map<String, Integer> movementCounts = new HashMap<>();
            
            try (PreparedStatement stmt = conn.prepareStatement(inventoryQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("id", rs.getInt("id"));
                    product.put("name", rs.getString("name"));
                    product.put("category", rs.getString("category"));
                    product.put("price", rs.getDouble("price"));
                    product.put("stockQuantity", rs.getInt("stock_quantity"));
                    product.put("estimatedSales", rs.getInt("estimated_sales"));
                    product.put("stockLevel", rs.getString("stock_level"));
                    product.put("movementCategory", rs.getString("movement_category"));
                    
                    // 计算建议补货量
                    int currentStock = rs.getInt("stock_quantity");
                    int estimatedSales = rs.getInt("estimated_sales");
                    int suggestedReorder = 0;
                    
                    if (currentStock <= 5) {
                        suggestedReorder = Math.max(100 - currentStock, estimatedSales * 2);
                    } else if (currentStock <= 20 && estimatedSales > 50) {
                        suggestedReorder = 80 - currentStock;
                    }
                    
                    product.put("suggestedReorder", suggestedReorder);
                    inventoryAnalysis.add(product);
                    
                    // 统计计数
                    String stockLevel = rs.getString("stock_level");
                    String movement = rs.getString("movement_category");
                    stockLevelCounts.put(stockLevel, stockLevelCounts.getOrDefault(stockLevel, 0) + 1);
                    movementCounts.put(movement, movementCounts.getOrDefault(movement, 0) + 1);
                }
            }
            
            optimization.put("inventoryAnalysis", inventoryAnalysis);
            optimization.put("stockLevelDistribution", stockLevelCounts);
            optimization.put("movementDistribution", movementCounts);
            
            APIOptimizer.sendSuccessResponse(response, optimization);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "库存优化分析失败", e.getMessage());
        }
    }
    
    /**
     * 利润分析
     */
    private void getProfitAnalysis(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            Map<String, Object> profitAnalysis = new HashMap<>();
            
            // 基于估算的利润分析（假设成本为售价的60%）
            String profitQuery = "SELECT p.category, " +
                               "COUNT(p.id) as product_count, " +
                               "SUM(100 - p.stock_quantity) as total_estimated_sales, " +
                               "SUM((100 - p.stock_quantity) * p.price) as total_revenue, " +
                               "SUM((100 - p.stock_quantity) * p.price * 0.4) as estimated_profit, " +
                               "AVG(p.price) as avg_price " +
                               "FROM products p " +
                               "WHERE p.is_available = 1 " +
                               "GROUP BY p.category " +
                               "ORDER BY estimated_profit DESC";
            
            List<Map<String, Object>> categoryProfits = new ArrayList<>();
            double totalRevenue = 0;
            double totalProfit = 0;
            
            try (PreparedStatement stmt = conn.prepareStatement(profitQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> categoryProfit = new HashMap<>();
                    categoryProfit.put("category", rs.getString("category"));
                    categoryProfit.put("productCount", rs.getInt("product_count"));
                    categoryProfit.put("totalSales", rs.getInt("total_estimated_sales"));
                    categoryProfit.put("totalRevenue", rs.getDouble("total_revenue"));
                    categoryProfit.put("estimatedProfit", rs.getDouble("estimated_profit"));
                    categoryProfit.put("avgPrice", rs.getDouble("avg_price"));
                    
                    double revenue = rs.getDouble("total_revenue");
                    double profit = rs.getDouble("estimated_profit");
                    if (revenue > 0) {
                        categoryProfit.put("profitMargin", Math.round((profit / revenue * 100) * 100.0) / 100.0);
                    }
                    
                    categoryProfits.add(categoryProfit);
                    totalRevenue += revenue;
                    totalProfit += profit;
                }
            }
            
            profitAnalysis.put("categoryProfits", categoryProfits);
            profitAnalysis.put("totalRevenue", Math.round(totalRevenue * 100.0) / 100.0);
            profitAnalysis.put("totalProfit", Math.round(totalProfit * 100.0) / 100.0);
            if (totalRevenue > 0) {
                profitAnalysis.put("overallProfitMargin", Math.round((totalProfit / totalRevenue * 100) * 100.0) / 100.0);
            }
            
            APIOptimizer.sendSuccessResponse(response, profitAnalysis);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "利润分析失败", e.getMessage());
        }
    }
    
    /**
     * 对比分析
     */
    private void getComparativeAnalysis(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String compareType = request.getParameter("compareType"); // period, category, member_level
            
            Map<String, Object> comparison = new HashMap<>();
            
            if ("period".equals(compareType)) {
                // 时期对比分析
                String periodCompareQuery = "SELECT " +
                                          "'本周' as period, " +
                                          "COUNT(DISTINCT CASE WHEN pt.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN pt.user_id END) as active_users, " +
                                          "SUM(CASE WHEN pt.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND pt.transaction_type = 'EARN' THEN pt.points ELSE 0 END) as total_points " +
                                          "FROM point_transactions pt " +
                                          "UNION ALL " +
                                          "SELECT " +
                                          "'上周' as period, " +
                                          "COUNT(DISTINCT CASE WHEN pt.transaction_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 14 DAY) AND DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN pt.user_id END) as active_users, " +
                                          "SUM(CASE WHEN pt.transaction_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 14 DAY) AND DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND pt.transaction_type = 'EARN' THEN pt.points ELSE 0 END) as total_points " +
                                          "FROM point_transactions pt";
                
                List<Map<String, Object>> periodComparison = new ArrayList<>();
                try (PreparedStatement stmt = conn.prepareStatement(periodCompareQuery);
                     ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> period = new HashMap<>();
                        period.put("period", rs.getString("period"));
                        period.put("activeUsers", rs.getInt("active_users"));
                        period.put("totalPoints", rs.getInt("total_points"));
                        periodComparison.add(period);
                    }
                }
                comparison.put("periodComparison", periodComparison);
            }
            
            APIOptimizer.sendSuccessResponse(response, comparison);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "对比分析失败", e.getMessage());
        }
    }
    
    /**
     * 实时指标
     */
    private void getRealTimeMetrics(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            Map<String, Object> metrics = new HashMap<>();
            
            // 今日实时数据
            String todayMetricsQuery = "SELECT " +
                                     "COUNT(DISTINCT CASE WHEN pt.transaction_type = 'EARN' AND DATE(pt.transaction_date) = CURDATE() THEN pt.user_id END) as today_active_customers, " +
                                     "SUM(CASE WHEN pt.transaction_type = 'EARN' AND DATE(pt.transaction_date) = CURDATE() THEN pt.points ELSE 0 END) as today_sales_points, " +
                                     "COUNT(CASE WHEN pt.transaction_type = 'EARN' AND DATE(pt.transaction_date) = CURDATE() THEN pt.id END) as today_transactions, " +
                                     "AVG(CASE WHEN pt.transaction_type = 'EARN' AND DATE(pt.transaction_date) = CURDATE() THEN pt.points END) as today_avg_transaction " +
                                     "FROM point_transactions pt";
            
            try (PreparedStatement stmt = conn.prepareStatement(todayMetricsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    metrics.put("todayActiveCustomers", rs.getInt("today_active_customers"));
                    metrics.put("todaySalesPoints", rs.getInt("today_sales_points"));
                    metrics.put("todayTransactions", rs.getInt("today_transactions"));
                    metrics.put("todayAvgTransaction", Math.round(rs.getDouble("today_avg_transaction") * 100.0) / 100.0);
                }
            }
            
            // 当前库存警报
            String lowStockQuery = "SELECT COUNT(*) as low_stock_count " +
                                 "FROM products " +
                                 "WHERE is_available = 1 AND stock_quantity <= 10";
            
            try (PreparedStatement stmt = conn.prepareStatement(lowStockQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    metrics.put("lowStockAlerts", rs.getInt("low_stock_count"));
                }
            }
            
            // 系统状态
            metrics.put("systemStatus", "正常");
            metrics.put("lastUpdated", new Date());
            
            APIOptimizer.sendSuccessResponse(response, metrics);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取实时指标失败", e.getMessage());
        }
    }
}
