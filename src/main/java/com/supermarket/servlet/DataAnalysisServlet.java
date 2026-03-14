package com.supermarket.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.dao.UserDAO;
import com.supermarket.dao.impl.UserDAOImpl;
import com.supermarket.model.User;
import com.supermarket.util.APIOptimizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.supermarket.util.DatabaseUtil;

/**
 * 数据分析Servlet - 重新生成版本
 * 提供全面的数据统计分析功能，包括商品热销分析、销售趋势、会员分析等
 */
@WebServlet("/manager/data-analysis/*")
public class DataAnalysisServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAOImpl();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            if (!validateManagerPermission(request, response)) {
                return;
            }
            
            // 设置缓存头（数据分析结果缓存15分钟）
            APIOptimizer.setCacheHeaders(response, 15 * 60);
            
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // 显示数据分析页面
                request.getRequestDispatcher("/WEB-INF/views/manager/data-analysis.jsp")
                       .forward(request, response);
            } else if (pathInfo.equals("/dashboard-overview")) {
                getDashboardOverview(request, response);
            } else if (pathInfo.equals("/hot-products")) {
                getHotProductsAnalysis(request, response);
            } else if (pathInfo.equals("/sales-trend")) {
                getSalesTrendAnalysis(request, response);
            } else if (pathInfo.equals("/member-analysis")) {
                getMemberAnalysis(request, response);
            } else if (pathInfo.equals("/category-analysis")) {
                getCategoryAnalysis(request, response);
            } else if (pathInfo.equals("/points-analysis")) {
                getPointsAnalysis(request, response);
            } else if (pathInfo.equals("/inventory-analysis")) {
                getInventoryAnalysis(request, response);
            } else if (pathInfo.equals("/export-report")) {
                exportAnalysisReport(request, response);
            } else {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "接口不存在");
            }
        } catch (Exception e) {
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
     * 获取仪表板概览数据
     */
    private void getDashboardOverview(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            Map<String, Object> overview = new HashMap<>();
            
            // 今日销售数据（基于积分交易记录估算）
            String todayQuery = "SELECT " +
                              "COALESCE(SUM(CASE WHEN transaction_type = 'EARN' THEN points ELSE 0 END), 0) as today_sales, " +
                              "COUNT(CASE WHEN transaction_type = 'EARN' THEN 1 END) as today_orders, " +
                              "COUNT(DISTINCT user_id) as today_customers " +
                              "FROM point_transactions WHERE DATE(transaction_date) = CURDATE()";
            
            try (PreparedStatement stmt = conn.prepareStatement(todayQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    overview.put("todaySales", rs.getDouble("today_sales"));
                    overview.put("todayOrders", rs.getInt("today_orders"));
                    overview.put("todayCustomers", rs.getInt("today_customers"));
                }
            }
            
            // 本月销售数据
            String monthQuery = "SELECT " +
                              "COALESCE(SUM(CASE WHEN transaction_type = 'EARN' THEN points ELSE 0 END), 0) as month_sales, " +
                              "COUNT(CASE WHEN transaction_type = 'EARN' THEN 1 END) as month_orders " +
                              "FROM point_transactions WHERE YEAR(transaction_date) = YEAR(CURDATE()) " +
                              "AND MONTH(transaction_date) = MONTH(CURDATE())";
            
            try (PreparedStatement stmt = conn.prepareStatement(monthQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    overview.put("monthSales", rs.getDouble("month_sales"));
                    overview.put("monthOrders", rs.getInt("month_orders"));
                }
            }
            
            // 会员统计
            String memberQuery = "SELECT " +
                               "COUNT(*) as total_members, " +
                               "COUNT(CASE WHEN membership_level = 'GOLD' THEN 1 END) as gold_members, " +
                               "COUNT(CASE WHEN membership_level = 'SILVER' THEN 1 END) as silver_members, " +
                               "COUNT(CASE WHEN membership_level = 'BRONZE' THEN 1 END) as bronze_members " +
                               "FROM users WHERE role = 'MEMBER'";
            
            try (PreparedStatement stmt = conn.prepareStatement(memberQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> memberStats = new HashMap<>();
                    memberStats.put("total", rs.getInt("total_members"));
                    memberStats.put("gold", rs.getInt("gold_members"));
                    memberStats.put("silver", rs.getInt("silver_members"));
                    memberStats.put("bronze", rs.getInt("bronze_members"));
                    overview.put("memberStats", memberStats);
                }
            }
            
            // 商品库存警告
            String inventoryQuery = "SELECT COUNT(*) as low_stock_count " +
                                  "FROM products WHERE stock_quantity < 10 AND is_available = 1";
            
            try (PreparedStatement stmt = conn.prepareStatement(inventoryQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    overview.put("lowStockCount", rs.getInt("low_stock_count"));
                }
            }
            
            APIOptimizer.sendSuccessResponse(response, overview);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取概览数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取商品热销分析数据
     */
    private void getHotProductsAnalysis(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String period = request.getParameter("period"); // today, week, month
            String category = request.getParameter("category");
            
            Map<String, Object> analysis = new HashMap<>();
            
            // 构建时间条件
            String timeCondition = "";
            switch (period != null ? period : "week") {
                case "today":
                    timeCondition = "AND DATE(pt.transaction_date) = CURDATE()";
                    break;
                case "week":
                    timeCondition = "AND pt.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
                    break;
                case "month":
                    timeCondition = "AND pt.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)";
                    break;
            }
            
            // 构建分类条件
            String categoryCondition = "";
            if (category != null && !category.isEmpty() && !"all".equals(category)) {
                categoryCondition = "AND p.category = ?";
            }
            
            // 热销商品排行（基于积分交易频次和库存变化推算）
            String hotProductsQuery = "SELECT p.id, p.name, p.category, p.price, " +
                                    "p.stock_quantity, " +
                                    "(100 - p.stock_quantity) as estimated_sales, " +
                                    "COUNT(pt.id) as transaction_count, " +
                                    "SUM(pt.points) as total_points " +
                                    "FROM products p " +
                                    "LEFT JOIN point_transactions pt ON pt.description LIKE CONCAT('%', p.name, '%') " +
                                    "WHERE p.is_available = 1 " + timeCondition + categoryCondition + " " +
                                    "GROUP BY p.id, p.name, p.category, p.price, p.stock_quantity " +
                                    "ORDER BY estimated_sales DESC, transaction_count DESC " +
                                    "LIMIT 20";
            
            List<Map<String, Object>> hotProducts = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(hotProductsQuery)) {
                int paramIndex = 1;
                if (category != null && !category.isEmpty() && !"all".equals(category)) {
                    stmt.setString(paramIndex++, category);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    int rank = 1;
                    while (rs.next()) {
                        Map<String, Object> product = new HashMap<>();
                        product.put("rank", rank++);
                        product.put("id", rs.getInt("id"));
                        product.put("name", rs.getString("name"));
                        product.put("category", rs.getString("category"));
                        product.put("price", rs.getDouble("price"));
                        product.put("stockQuantity", rs.getInt("stock_quantity"));
                        product.put("estimatedSales", rs.getInt("estimated_sales"));
                        product.put("transactionCount", rs.getInt("transaction_count"));
                        product.put("totalRevenue", rs.getInt("estimated_sales") * rs.getDouble("price"));
                        hotProducts.add(product);
                    }
                }
            }
            analysis.put("hotProducts", hotProducts);
            
            // 分类销售统计
            String categoryStatsQuery = "SELECT p.category, " +
                                      "COUNT(p.id) as product_count, " +
                                      "SUM(100 - p.stock_quantity) as total_sales, " +
                                      "AVG(p.price) as avg_price " +
                                      "FROM products p " +
                                      "WHERE p.is_available = 1 " +
                                      "GROUP BY p.category " +
                                      "ORDER BY total_sales DESC";
            
            List<Map<String, Object>> categoryStats = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(categoryStatsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> category_stat = new HashMap<>();
                    category_stat.put("category", rs.getString("category"));
                    category_stat.put("productCount", rs.getInt("product_count"));
                    category_stat.put("totalSales", rs.getInt("total_sales"));
                    category_stat.put("avgPrice", rs.getDouble("avg_price"));
                    categoryStats.add(category_stat);
                }
            }
            analysis.put("categoryStats", categoryStats);
            
            // 库存预警商品
            String lowStockQuery = "SELECT id, name, category, stock_quantity, price " +
                                 "FROM products WHERE stock_quantity < 10 AND is_available = 1 " +
                                 "ORDER BY stock_quantity ASC LIMIT 10";
            
            List<Map<String, Object>> lowStockProducts = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(lowStockQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("id", rs.getInt("id"));
                    product.put("name", rs.getString("name"));
                    product.put("category", rs.getString("category"));
                    product.put("stockQuantity", rs.getInt("stock_quantity"));
                    product.put("price", rs.getDouble("price"));
                    lowStockProducts.add(product);
                }
            }
            analysis.put("lowStockProducts", lowStockProducts);
            
            APIOptimizer.sendSuccessResponse(response, analysis);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取热销分析失败", e.getMessage());
        }
    }
    
    /**
     * 获取销售趋势分析
     */
    private void getSalesTrendAnalysis(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String period = request.getParameter("period"); // week, month, quarter
            
            Map<String, Object> trendData = new HashMap<>();
            
            String trendQuery;
            if ("month".equals(period)) {
                // 最近30天趋势
                trendQuery = "SELECT DATE(transaction_date) as date, " +
                           "COALESCE(SUM(CASE WHEN transaction_type = 'EARN' THEN points ELSE 0 END), 0) as sales, " +
                           "COUNT(CASE WHEN transaction_type = 'EARN' THEN 1 END) as orders, " +
                           "COUNT(DISTINCT user_id) as customers " +
                           "FROM point_transactions " +
                           "WHERE transaction_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                           "GROUP BY DATE(transaction_date) " +
                           "ORDER BY date";
            } else if ("quarter".equals(period)) {
                // 最近3个月趋势（按周统计）
                trendQuery = "SELECT YEARWEEK(transaction_date) as week, " +
                           "MIN(DATE(transaction_date)) as date, " +
                           "COALESCE(SUM(CASE WHEN transaction_type = 'EARN' THEN points ELSE 0 END), 0) as sales, " +
                           "COUNT(CASE WHEN transaction_type = 'EARN' THEN 1 END) as orders, " +
                           "COUNT(DISTINCT user_id) as customers " +
                           "FROM point_transactions " +
                           "WHERE transaction_date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH) " +
                           "GROUP BY YEARWEEK(transaction_date) " +
                           "ORDER BY week";
            } else {
                // 默认最近7天趋势
                trendQuery = "SELECT DATE(transaction_date) as date, " +
                           "COALESCE(SUM(CASE WHEN transaction_type = 'EARN' THEN points ELSE 0 END), 0) as sales, " +
                           "COUNT(CASE WHEN transaction_type = 'EARN' THEN 1 END) as orders, " +
                           "COUNT(DISTINCT user_id) as customers " +
                           "FROM point_transactions " +
                           "WHERE transaction_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                           "GROUP BY DATE(transaction_date) " +
                           "ORDER BY date";
            }
            
            List<Map<String, Object>> trendList = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(trendQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> dayData = new HashMap<>();
                    dayData.put("date", rs.getString("date"));
                    dayData.put("sales", rs.getDouble("sales"));
                    dayData.put("orders", rs.getInt("orders"));
                    dayData.put("customers", rs.getInt("customers"));
                    trendList.add(dayData);
                }
            }
            trendData.put("trend", trendList);
            
            // 同比增长率计算
            String growthQuery = "SELECT " +
                               "COALESCE(SUM(CASE WHEN transaction_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                               "AND transaction_type = 'EARN' THEN points ELSE 0 END), 0) as current_week, " +
                               "COALESCE(SUM(CASE WHEN transaction_date >= DATE_SUB(CURDATE(), INTERVAL 14 DAY) " +
                               "AND transaction_date < DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                               "AND transaction_type = 'EARN' THEN points ELSE 0 END), 0) as last_week " +
                               "FROM point_transactions";
            
            try (PreparedStatement stmt = conn.prepareStatement(growthQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double currentWeek = rs.getDouble("current_week");
                    double lastWeek = rs.getDouble("last_week");
                    double growthRate = lastWeek > 0 ? ((currentWeek - lastWeek) / lastWeek) * 100 : 0;
                    trendData.put("weeklyGrowthRate", growthRate);
                }
            }
            
            APIOptimizer.sendSuccessResponse(response, trendData);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取销售趋势失败", e.getMessage());
        }
    }
    
    /**
     * 获取会员分析数据
     */
    private void getMemberAnalysis(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            Map<String, Object> memberAnalysis = new HashMap<>();
            
            // 会员等级分布
            String levelDistQuery = "SELECT membership_level, COUNT(*) as count, " +
                                  "AVG(points_balance) as avg_points " +
                                  "FROM users WHERE role = 'MEMBER' " +
                                  "GROUP BY membership_level";
            
            List<Map<String, Object>> levelDistribution = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(levelDistQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> level = new HashMap<>();
                    level.put("level", rs.getString("membership_level"));
                    level.put("count", rs.getInt("count"));
                    level.put("avgPoints", rs.getDouble("avg_points"));
                    levelDistribution.add(level);
                }
            }
            memberAnalysis.put("levelDistribution", levelDistribution);
            
            // 活跃会员分析（最近30天有积分变动）
            String activeMembersQuery = "SELECT u.membership_level, COUNT(DISTINCT u.id) as active_count " +
                                      "FROM users u " +
                                      "INNER JOIN point_transactions pt ON u.id = pt.user_id " +
                                      "WHERE u.role = 'MEMBER' " +
                                      "AND pt.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                                      "GROUP BY u.membership_level";
            
            List<Map<String, Object>> activeMembers = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(activeMembersQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> active = new HashMap<>();
                    active.put("level", rs.getString("membership_level"));
                    active.put("activeCount", rs.getInt("active_count"));
                    activeMembers.add(active);
                }
            }
            memberAnalysis.put("activeMembers", activeMembers);
            
            // 新增会员趋势（最近30天）
            String newMembersQuery = "SELECT DATE(created_at) as date, COUNT(*) as count " +
                                   "FROM users WHERE role = 'MEMBER' " +
                                   "AND created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                                   "GROUP BY DATE(created_at) ORDER BY date";
            
            List<Map<String, Object>> newMembersTrend = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(newMembersQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> day = new HashMap<>();
                    day.put("date", rs.getString("date"));
                    day.put("count", rs.getInt("count"));
                    newMembersTrend.add(day);
                }
            }
            memberAnalysis.put("newMembersTrend", newMembersTrend);
            
            // 高价值会员（积分余额前20）
            String highValueMembersQuery = "SELECT username, membership_level, points_balance, " +
                                         "phone_number, created_at " +
                                         "FROM users WHERE role = 'MEMBER' " +
                                         "ORDER BY points_balance DESC LIMIT 20";
            
            List<Map<String, Object>> highValueMembers = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(highValueMembersQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> member = new HashMap<>();
                    member.put("username", rs.getString("username"));
                    member.put("level", rs.getString("membership_level"));
                    member.put("pointsBalance", rs.getInt("points_balance"));
                    member.put("phoneNumber", rs.getString("phone_number"));
                    member.put("joinDate", rs.getTimestamp("created_at"));
                    highValueMembers.add(member);
                }
            }
            memberAnalysis.put("highValueMembers", highValueMembers);
            
            APIOptimizer.sendSuccessResponse(response, memberAnalysis);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取会员分析失败", e.getMessage());
        }
    }
    
    /**
     * 获取分类分析数据
     */
    private void getCategoryAnalysis(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            Map<String, Object> categoryAnalysis = new HashMap<>();
            
            // 分类销售占比
            String categoryShareQuery = "SELECT category, " +
                                      "COUNT(*) as product_count, " +
                                      "SUM(100 - stock_quantity) as estimated_sales, " +
                                      "AVG(price) as avg_price, " +
                                      "SUM((100 - stock_quantity) * price) as estimated_revenue " +
                                      "FROM products WHERE is_available = 1 " +
                                      "GROUP BY category " +
                                      "ORDER BY estimated_revenue DESC";
            
            List<Map<String, Object>> categoryShare = new ArrayList<>();
            double totalRevenue = 0;
            
            try (PreparedStatement stmt = conn.prepareStatement(categoryShareQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> category = new HashMap<>();
                    category.put("category", rs.getString("category"));
                    category.put("productCount", rs.getInt("product_count"));
                    category.put("estimatedSales", rs.getInt("estimated_sales"));
                    category.put("avgPrice", rs.getDouble("avg_price"));
                    double revenue = rs.getDouble("estimated_revenue");
                    category.put("estimatedRevenue", revenue);
                    totalRevenue += revenue;
                    categoryShare.add(category);
                }
            }
            
            // 计算占比
            for (Map<String, Object> category : categoryShare) {
                double revenue = (Double) category.get("estimatedRevenue");
                double percentage = totalRevenue > 0 ? (revenue / totalRevenue) * 100 : 0;
                category.put("revenuePercentage", percentage);
            }
            
            categoryAnalysis.put("categoryShare", categoryShare);
            categoryAnalysis.put("totalRevenue", totalRevenue);
            
            APIOptimizer.sendSuccessResponse(response, categoryAnalysis);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取分类分析失败", e.getMessage());
        }
    }
    
    /**
     * 获取积分分析数据
     */
    private void getPointsAnalysis(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            Map<String, Object> pointsAnalysis = new HashMap<>();
            
            // 积分发放统计
            String pointsStatsQuery = "SELECT " +
                                    "SUM(CASE WHEN transaction_type = 'EARN' THEN points ELSE 0 END) as total_earned, " +
                                    "SUM(CASE WHEN transaction_type = 'REDEEM' THEN points ELSE 0 END) as total_redeemed, " +
                                    "COUNT(CASE WHEN transaction_type = 'EARN' THEN 1 END) as earn_transactions, " +
                                    "COUNT(CASE WHEN transaction_type = 'REDEEM' THEN 1 END) as redeem_transactions " +
                                    "FROM point_transactions " +
                                    "WHERE transaction_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)";
            
            try (PreparedStatement stmt = conn.prepareStatement(pointsStatsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pointsAnalysis.put("totalEarned", rs.getInt("total_earned"));
                    pointsAnalysis.put("totalRedeemed", rs.getInt("total_redeemed"));
                    pointsAnalysis.put("earnTransactions", rs.getInt("earn_transactions"));
                    pointsAnalysis.put("redeemTransactions", rs.getInt("redeem_transactions"));
                }
            }
            
            // 积分趋势（最近7天）
            String pointsTrendQuery = "SELECT DATE(transaction_date) as date, " +
                                    "SUM(CASE WHEN transaction_type = 'EARN' THEN points ELSE 0 END) as earned, " +
                                    "SUM(CASE WHEN transaction_type = 'REDEEM' THEN points ELSE 0 END) as redeemed " +
                                    "FROM point_transactions " +
                                    "WHERE transaction_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                                    "GROUP BY DATE(transaction_date) ORDER BY date";
            
            List<Map<String, Object>> pointsTrend = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(pointsTrendQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> day = new HashMap<>();
                    day.put("date", rs.getString("date"));
                    day.put("earned", rs.getInt("earned"));
                    day.put("redeemed", rs.getInt("redeemed"));
                    pointsTrend.add(day);
                }
            }
            pointsAnalysis.put("pointsTrend", pointsTrend);
            
            APIOptimizer.sendSuccessResponse(response, pointsAnalysis);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取积分分析失败", e.getMessage());
        }
    }
    
    /**
     * 获取库存分析数据
     */
    private void getInventoryAnalysis(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            Map<String, Object> inventoryAnalysis = new HashMap<>();
            
            // 库存状态统计
            String inventoryStatsQuery = "SELECT " +
                                       "COUNT(CASE WHEN stock_quantity = 0 THEN 1 END) as out_of_stock, " +
                                       "COUNT(CASE WHEN stock_quantity > 0 AND stock_quantity < 10 THEN 1 END) as low_stock, " +
                                       "COUNT(CASE WHEN stock_quantity >= 10 AND stock_quantity < 50 THEN 1 END) as normal_stock, " +
                                       "COUNT(CASE WHEN stock_quantity >= 50 THEN 1 END) as high_stock, " +
                                       "AVG(stock_quantity) as avg_stock, " +
                                       "SUM(stock_quantity * price) as total_inventory_value " +
                                       "FROM products WHERE is_available = 1";
            
            try (PreparedStatement stmt = conn.prepareStatement(inventoryStatsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    inventoryAnalysis.put("outOfStock", rs.getInt("out_of_stock"));
                    inventoryAnalysis.put("lowStock", rs.getInt("low_stock"));
                    inventoryAnalysis.put("normalStock", rs.getInt("normal_stock"));
                    inventoryAnalysis.put("highStock", rs.getInt("high_stock"));
                    inventoryAnalysis.put("avgStock", rs.getDouble("avg_stock"));
                    inventoryAnalysis.put("totalInventoryValue", rs.getDouble("total_inventory_value"));
                }
            }
            
            // 分类库存分布
            String categoryInventoryQuery = "SELECT category, " +
                                          "COUNT(*) as product_count, " +
                                          "SUM(stock_quantity) as total_stock, " +
                                          "AVG(stock_quantity) as avg_stock, " +
                                          "SUM(stock_quantity * price) as category_value " +
                                          "FROM products WHERE is_available = 1 " +
                                          "GROUP BY category ORDER BY category_value DESC";
            
            List<Map<String, Object>> categoryInventory = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(categoryInventoryQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> category = new HashMap<>();
                    category.put("category", rs.getString("category"));
                    category.put("productCount", rs.getInt("product_count"));
                    category.put("totalStock", rs.getInt("total_stock"));
                    category.put("avgStock", rs.getDouble("avg_stock"));
                    category.put("categoryValue", rs.getDouble("category_value"));
                    categoryInventory.add(category);
                }
            }
            inventoryAnalysis.put("categoryInventory", categoryInventory);
            
            APIOptimizer.sendSuccessResponse(response, inventoryAnalysis);
            
        } catch (SQLException e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取库存分析失败", e.getMessage());
        }
    }
    
    /**
     * 导出分析报告
     */
    private void exportAnalysisReport(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String reportType = request.getParameter("type"); // sales, products, members
        String format = request.getParameter("format"); // csv, excel
        
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=analysis_report_" + 
                          LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                          ("csv".equals(format) ? ".csv" : ".xlsx"));
        
        try (PrintWriter writer = response.getWriter()) {
            writer.println("数据分析报告导出功能");
            writer.println("报告类型: " + reportType);
            writer.println("导出格式: " + format);
            writer.println("导出时间: " + LocalDate.now());
            writer.println("注意: 完整的导出功能需要集成Apache POI等库来生成Excel文件");
        }
    }
}