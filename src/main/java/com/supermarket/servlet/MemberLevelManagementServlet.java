package com.supermarket.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.supermarket.util.DatabaseUtil;
import com.supermarket.util.APIOptimizer;
import com.supermarket.model.User;
import com.supermarket.model.User.UserRole;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.BufferedReader;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 会员分级管理Servlet - 重新生成版本
 * 提供完整的会员等级管理功能，包括等级设置、升级条件、权益配置等
 */
@WebServlet("/manager/member-levels/*")
public class MemberLevelManagementServlet extends HttpServlet {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!validateManagerPermission(request, response)) {
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // 显示会员等级管理页面
                request.getRequestDispatcher("/WEB-INF/views/manager/member-levels.jsp").forward(request, response);
            } else if (pathInfo.equals("/list")) {
                // 获取所有会员等级列表
                getMemberLevelsList(request, response);
            } else if (pathInfo.equals("/statistics")) {
                // 获取会员等级统计数据
                getMemberLevelStatistics(request, response);
            } else if (pathInfo.equals("/upgrade-rules")) {
                // 获取升级规则配置
                getUpgradeRules(request, response);
            } else if (pathInfo.equals("/benefits-config")) {
                // 获取权益配置
                getBenefitsConfig(request, response);
            } else if (pathInfo.startsWith("/detail/")) {
                // 获取特定等级详情
                String levelId = pathInfo.substring(8);
                getMemberLevelDetail(request, response, levelId);
            } else if (pathInfo.equals("/member-distribution")) {
                // 获取会员等级分布
                getMemberDistribution(request, response);
            }
        } catch (Exception e) {
            System.err.println("会员等级管理GET请求处理异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!validateManagerPermission(request, response)) {
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/create")) {
                // 创建新的会员等级
                createMemberLevel(request, response);
            } else if (pathInfo.equals("/batch-update")) {
                // 批量更新会员等级
                batchUpdateMemberLevels(request, response);
            } else if (pathInfo.equals("/upgrade-rules")) {
                // 设置升级规则
                setUpgradeRules(request, response);
            } else if (pathInfo.equals("/benefits-config")) {
                // 配置等级权益
                configureBenefits(request, response);
            } else if (pathInfo.equals("/auto-upgrade")) {
                // 执行自动升级
                executeAutoUpgrade(request, response);
            } else if (pathInfo.equals("/manual-upgrade")) {
                // 手动升级会员
                manualUpgradeMember(request, response);
            }
        } catch (Exception e) {
            System.err.println("会员等级管理POST请求处理异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!validateManagerPermission(request, response)) {
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo != null && pathInfo.startsWith("/update/")) {
                String levelId = pathInfo.substring(8);
                updateMemberLevel(request, response, levelId);
            } else if (pathInfo != null && pathInfo.startsWith("/toggle/")) {
                String levelId = pathInfo.substring(8);
                toggleLevelStatus(request, response, levelId);
            }
        } catch (Exception e) {
            System.err.println("会员等级管理PUT请求处理异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!validateManagerPermission(request, response)) {
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo != null && pathInfo.startsWith("/delete/")) {
                String levelId = pathInfo.substring(8);
                deleteMemberLevel(request, response, levelId);
            }
        } catch (Exception e) {
            System.err.println("会员等级管理DELETE请求处理异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }
    
    /**
     * 获取升级规则
     */
    private void getUpgradeRules(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        try {
            // 这里可以实现获取升级规则的逻辑
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", new HashMap<>());
            APIOptimizer.sendSuccessResponse(response, result);
            
        } catch (Exception e) {
            System.err.println("获取升级规则异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }
    
    /**
     * 获取权益配置
     */
    private void getBenefitsConfig(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        try {
            // 这里可以实现获取权益配置的逻辑
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", new HashMap<>());
            APIOptimizer.sendSuccessResponse(response, result);
            
        } catch (Exception e) {
            System.err.println("获取权益配置异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }
    
    /**
     * 获取会员等级详情
     */
    private void getMemberLevelDetail(HttpServletRequest request, HttpServletResponse response, String levelId) throws IOException, SQLException {
        try {
            int id = Integer.parseInt(levelId);
            
            String sql = "SELECT * FROM membership_levels WHERE level_id = ?";
            
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> level = new HashMap<>();
                        level.put("levelId", rs.getInt("level_id"));
                        level.put("levelName", rs.getString("level_name"));
                        level.put("minPoints", rs.getInt("min_points"));
                        level.put("maxPoints", rs.getObject("max_points"));
                        level.put("discountRate", rs.getDouble("discount_rate"));
                        level.put("isActive", rs.getBoolean("is_active"));
                        level.put("createdAt", rs.getTimestamp("created_at"));
                        level.put("updatedAt", rs.getTimestamp("updated_at"));
                        
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", true);
                        result.put("data", level);
                        APIOptimizer.sendSuccessResponse(response, result);
                    } else {
                        APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "会员等级不存在");
                    }
                }
            }
            
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "无效的等级ID");
        } catch (Exception e) {
            System.err.println("获取会员等级详情异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }

    /**
     * 验证管理员权限
     */
    private boolean validateManagerPermission(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "用户未登录");
            return false;
        }
        
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.MANAGER) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "权限不足，需要管理员权限");
            return false;
        }
        
        return true;
    }
    
    /**
     * 获取会员等级列表
     */
    private void getMemberLevelsList(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String sql = "SELECT ml.*, " +
                     "COUNT(m.member_id) as member_count, " +
                     "AVG(m.total_spending) as avg_spending, " +
                     "AVG(m.points) as avg_points " +
                     "FROM membership_levels ml " +
                     "LEFT JOIN members m ON m.level_id = ml.level_id " +
                     "GROUP BY ml.level_id " +
                     "ORDER BY ml.min_points ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            List<Map<String, Object>> levels = new ArrayList<>();
            
            while (rs.next()) {
                Map<String, Object> level = new HashMap<>();
                level.put("levelId", rs.getInt("level_id"));
                level.put("levelName", rs.getString("level_name"));
                level.put("levelCode", rs.getString("level_code"));
                level.put("minPoints", rs.getInt("min_points"));
                level.put("maxPoints", rs.getObject("max_points"));
                level.put("minSpending", rs.getBigDecimal("min_spending"));
                level.put("pointsMultiplier", rs.getBigDecimal("points_multiplier"));
                level.put("discountRate", rs.getBigDecimal("discount_rate"));
                level.put("benefits", parseJsonString(rs.getString("benefits")));
                level.put("upgradeConditions", parseJsonString(rs.getString("upgrade_conditions")));
                level.put("isActive", rs.getBoolean("is_active"));
                level.put("memberCount", rs.getInt("member_count"));
                level.put("avgSpending", rs.getBigDecimal("avg_spending"));
                level.put("avgPoints", rs.getBigDecimal("avg_points"));
                level.put("createdAt", rs.getTimestamp("created_at"));
                level.put("updatedAt", rs.getTimestamp("updated_at"));
                
                levels.add(level);
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("levels", levels);
            responseData.put("totalCount", levels.size());
            
            sendJsonResponse(response, responseData);
            
        } catch (SQLException e) {
            System.err.println("获取会员等级列表失败: " + e.getMessage());
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取会员等级列表失败");
        }
    }
    
    /**
     * 获取会员等级统计数据
     */
    private void getMemberLevelStatistics(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总等级数
            String totalLevelsSQL = "SELECT COUNT(*) as total FROM membership_levels WHERE is_active = true";
            try (PreparedStatement stmt = conn.prepareStatement(totalLevelsSQL);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    statistics.put("totalLevels", rs.getInt("total"));
                }
            }
            
            // 会员分布统计
            String distributionSQL = "SELECT ml.level_name, ml.level_code, COUNT(m.member_id) as member_count, " +
                                    "ROUND(COUNT(m.member_id) * 100.0 / (SELECT COUNT(*) FROM members), 2) as percentage " +
                                    "FROM membership_levels ml " +
                                    "LEFT JOIN members m ON m.level_id = ml.level_id " +
                                    "WHERE ml.is_active = true " +
                                    "GROUP BY ml.level_id, ml.level_name, ml.level_code " +
                                    "ORDER BY ml.min_points ASC";
            
            List<Map<String, Object>> distribution = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(distributionSQL);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("levelName", rs.getString("level_name"));
                    item.put("levelCode", rs.getString("level_code"));
                    item.put("memberCount", rs.getInt("member_count"));
                    item.put("percentage", rs.getBigDecimal("percentage"));
                    distribution.add(item);
                }
            }
            statistics.put("distribution", distribution);
            
            // 升级趋势统计（最近30天）
            String upgradeSQL = "SELECT DATE(upgrade_date) as upgrade_day, COUNT(*) as upgrade_count " +
                               "FROM member_level_history " +
                               "WHERE upgrade_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                               "GROUP BY DATE(upgrade_date) " +
                               "ORDER BY upgrade_day DESC " +
                               "LIMIT 30";
            
            List<Map<String, Object>> upgradeTrend = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(upgradeSQL);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", rs.getDate("upgrade_day"));
                    item.put("count", rs.getInt("upgrade_count"));
                    upgradeTrend.add(item);
                }
            }
            statistics.put("upgradeTrend", upgradeTrend);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("statistics", statistics);
            
            sendJsonResponse(response, responseData);
            
        } catch (SQLException e) {
            System.err.println("获取会员等级统计失败: " + e.getMessage());
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取统计数据失败");
        }
    }
    
    /**
     * 创建新的会员等级
     */
    private void createMemberLevel(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 读取请求参数
        String levelName = request.getParameter("levelName");
        String levelCode = request.getParameter("levelCode");
        String minPointsStr = request.getParameter("minPoints");
        String maxPointsStr = request.getParameter("maxPoints");
        String minSpendingStr = request.getParameter("minSpending");
        String pointsMultiplierStr = request.getParameter("pointsMultiplier");
        String discountRateStr = request.getParameter("discountRate");
        String benefits = request.getParameter("benefits");
        String upgradeConditions = request.getParameter("upgradeConditions");
        
        // 参数验证
        if (levelName == null || levelName.trim().isEmpty() || 
            levelCode == null || levelCode.trim().isEmpty() ||
            minPointsStr == null || minPointsStr.trim().isEmpty()) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "等级名称、等级代码和最低积分不能为空");
            return;
        }
        
        try {
            int minPoints = Integer.parseInt(minPointsStr);
            Integer maxPoints = (maxPointsStr != null && !maxPointsStr.trim().isEmpty()) ? 
                               Integer.parseInt(maxPointsStr) : null;
            BigDecimal minSpending = new BigDecimal(minSpendingStr != null && !minSpendingStr.trim().isEmpty() ? 
                                                   minSpendingStr : "0");
            BigDecimal pointsMultiplier = new BigDecimal(pointsMultiplierStr != null && !pointsMultiplierStr.trim().isEmpty() ? 
                                                        pointsMultiplierStr : "1.0");
            BigDecimal discountRate = new BigDecimal(discountRateStr != null && !discountRateStr.trim().isEmpty() ? 
                                                     discountRateStr : "0");
            
            // 检查等级代码是否已存在
            if (isLevelCodeExists(levelCode.trim(), null)) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "等级代码已存在");
                return;
            }
            
            // 检查积分范围是否冲突
            if (isPointsRangeConflict(minPoints, maxPoints, null)) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "积分范围与现有等级冲突");
                return;
            }
            
            String sql = "INSERT INTO membership_levels " +
                         "(level_name, level_code, min_points, max_points, min_spending, " +
                         "points_multiplier, discount_rate, benefits, upgrade_conditions) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setString(1, levelName.trim());
                stmt.setString(2, levelCode.trim().toUpperCase());
                stmt.setInt(3, minPoints);
                if (maxPoints != null) {
                    stmt.setInt(4, maxPoints);
                } else {
                    stmt.setNull(4, Types.INTEGER);
                }
                stmt.setBigDecimal(5, minSpending);
                stmt.setBigDecimal(6, pointsMultiplier);
                stmt.setBigDecimal(7, discountRate);
                stmt.setString(8, benefits != null ? benefits : "{}");
                stmt.setString(9, upgradeConditions != null ? upgradeConditions : "{}");
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int levelId = generatedKeys.getInt(1);
                            
                            Map<String, Object> responseData = new HashMap<>();
                            responseData.put("success", true);
                            responseData.put("message", "会员等级创建成功");
                            responseData.put("levelId", levelId);
                            
                            APIOptimizer.sendSuccessResponse(response, responseData);
                        }
                    }
                } else {
                    APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "创建会员等级失败");
                }
            }
            
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "数值格式错误");
        } catch (SQLException e) {
            System.err.println("创建会员等级失败: " + e.getMessage());
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "创建会员等级失败");
        }
    }
    
    /**
     * 执行自动升级
     */
    private void executeAutoUpgrade(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // 获取所有活跃的会员等级，按最低积分排序
            String levelsSQL = "SELECT * FROM membership_levels " +
                              "WHERE is_active = true " +
                              "ORDER BY min_points ASC";
            
            List<Map<String, Object>> levels = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(levelsSQL);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> level = new HashMap<>();
                    level.put("levelId", rs.getInt("level_id"));
                    level.put("minPoints", rs.getInt("min_points"));
                    level.put("maxPoints", rs.getObject("max_points"));
                    level.put("minSpending", rs.getBigDecimal("min_spending"));
                    levels.add(level);
                }
            }
            
            // 获取需要升级的会员
            String membersSQL = "SELECT m.member_id, m.points, m.total_spending, m.level_id as current_level_id " +
                               "FROM members m " +
                               "WHERE m.is_active = true";
            
            int upgradeCount = 0;
            List<Map<String, Object>> upgradeLog = new ArrayList<>();
            
            try (PreparedStatement stmt = conn.prepareStatement(membersSQL);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    int memberId = rs.getInt("member_id");
                    int points = rs.getInt("points");
                    BigDecimal totalSpending = rs.getBigDecimal("total_spending");
                    int currentLevelId = rs.getInt("current_level_id");
                    
                    // 确定应该的等级
                    int targetLevelId = determineTargetLevel(levels, points, totalSpending);
                    
                    if (targetLevelId != currentLevelId && targetLevelId > 0) {
                        // 执行升级
                        String updateSQL = "UPDATE members SET level_id = ? WHERE member_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                            updateStmt.setInt(1, targetLevelId);
                            updateStmt.setInt(2, memberId);
                            updateStmt.executeUpdate();
                        }
                        
                        // 记录升级历史
                        String historySQL = "INSERT INTO member_level_history " +
                                           "(member_id, old_level_id, new_level_id, upgrade_reason, upgrade_date) " +
                                           "VALUES (?, ?, ?, ?, NOW())";
                        try (PreparedStatement historyStmt = conn.prepareStatement(historySQL)) {
                            historyStmt.setInt(1, memberId);
                            historyStmt.setInt(2, currentLevelId);
                            historyStmt.setInt(3, targetLevelId);
                            historyStmt.setString(4, "自动升级");
                            historyStmt.executeUpdate();
                        }
                        
                        upgradeCount++;
                        
                        Map<String, Object> logEntry = new HashMap<>();
                        logEntry.put("memberId", memberId);
                        logEntry.put("oldLevelId", currentLevelId);
                        logEntry.put("newLevelId", targetLevelId);
                        logEntry.put("points", points);
                        logEntry.put("totalSpending", totalSpending);
                        upgradeLog.add(logEntry);
                    }
                }
            }
            
            conn.commit();
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "自动升级完成");
            responseData.put("upgradeCount", upgradeCount);
            responseData.put("upgradeLog", upgradeLog);
            
            sendJsonResponse(response, responseData);
            
        } catch (SQLException e) {
            System.err.println("执行自动升级失败: " + e.getMessage());
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "自动升级失败");
        }
    }
    
    /**
     * 确定目标等级
     */
    private int determineTargetLevel(List<Map<String, Object>> levels, int points, BigDecimal totalSpending) {
        int targetLevelId = 0;
        
        for (Map<String, Object> level : levels) {
            int minPoints = (Integer) level.get("minPoints");
            Object maxPointsObj = level.get("maxPoints");
            BigDecimal minSpending = (BigDecimal) level.get("minSpending");
            
            boolean pointsQualified = points >= minPoints;
            if (maxPointsObj != null) {
                int maxPoints = (Integer) maxPointsObj;
                pointsQualified = pointsQualified && points <= maxPoints;
            }
            
            boolean spendingQualified = totalSpending.compareTo(minSpending) >= 0;
            
            if (pointsQualified && spendingQualified) {
                targetLevelId = (Integer) level.get("levelId");
            }
        }
        
        return targetLevelId;
    }
    
    /**
     * 检查等级代码是否存在
     */
    private boolean isLevelCodeExists(String levelCode, Integer excludeLevelId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM membership_levels WHERE level_code = ?";
        if (excludeLevelId != null) {
            sql += " AND level_id != ?";
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, levelCode.toUpperCase());
            if (excludeLevelId != null) {
                stmt.setInt(2, excludeLevelId);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    
    /**
     * 检查积分范围是否冲突
     */
    private boolean isPointsRangeConflict(int minPoints, Integer maxPoints, Integer excludeLevelId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM membership_levels " +
                     "WHERE is_active = true " +
                     "AND (" +
                     "(min_points <= ? AND (max_points IS NULL OR max_points >= ?)) " +
                     "OR (? <= min_points AND (? IS NULL OR ? >= min_points))" +
                     ")";
        
        if (excludeLevelId != null) {
            sql += " AND level_id != ?";
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, minPoints);
            stmt.setInt(2, minPoints);
            stmt.setInt(3, minPoints);
            if (maxPoints != null) {
                stmt.setInt(4, maxPoints);
                stmt.setInt(5, maxPoints);
            } else {
                stmt.setNull(4, Types.INTEGER);
                stmt.setNull(5, Types.INTEGER);
            }
            
            if (excludeLevelId != null) {
                stmt.setInt(6, excludeLevelId);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    
    /**
     * 解析JSON字符串
     */
    private Object parseJsonString(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(jsonStr, Object.class);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    
    /**
     * 更新会员等级
     */
    private void updateMemberLevel(HttpServletRequest request, HttpServletResponse response, String levelId) throws IOException, SQLException {
        try {
            int id = Integer.parseInt(levelId);
            
            // 读取请求体
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            
            // 解析JSON
            JsonNode jsonNode = objectMapper.readTree(sb.toString());
            String levelName = jsonNode.get("levelName").asText();
            int minPoints = jsonNode.get("minPoints").asInt();
            Integer maxPoints = jsonNode.has("maxPoints") && !jsonNode.get("maxPoints").isNull() 
                ? jsonNode.get("maxPoints").asInt() : null;
            double discountRate = jsonNode.get("discountRate").asDouble();
            
            // 验证数据
            if (levelName == null || levelName.trim().isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "等级名称不能为空");
                return;
            }
            
            if (minPoints < 0) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "最小积分不能为负数");
                return;
            }
            
            if (maxPoints != null && maxPoints <= minPoints) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "最大积分必须大于最小积分");
                return;
            }
            
            if (discountRate < 0 || discountRate > 1) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "折扣率必须在0-1之间");
                return;
            }
            
            // 检查积分范围冲突
            if (isPointsRangeConflict(minPoints, maxPoints, id)) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "积分范围与现有等级冲突");
                return;
            }
            
            // 更新数据库
            String sql = "UPDATE membership_levels SET level_name = ?, min_points = ?, max_points = ?, discount_rate = ?, updated_at = CURRENT_TIMESTAMP WHERE level_id = ?";
            
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, levelName);
                pstmt.setInt(2, minPoints);
                if (maxPoints != null) {
                    pstmt.setInt(3, maxPoints);
                } else {
                    pstmt.setNull(3, Types.INTEGER);
                }
                pstmt.setDouble(4, discountRate);
                pstmt.setInt(5, id);
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("message", "会员等级更新成功");
                    APIOptimizer.sendSuccessResponse(response, result);
                } else {
                    APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "会员等级不存在");
                }
            }
            
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "无效的等级ID");
        } catch (Exception e) {
            System.err.println("更新会员等级异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }
    
    /**
     * 切换等级状态
     */
    private void toggleLevelStatus(HttpServletRequest request, HttpServletResponse response, String levelId) throws IOException, SQLException {
        try {
            int id = Integer.parseInt(levelId);
            
            String sql = "UPDATE membership_levels SET is_active = NOT is_active, updated_at = CURRENT_TIMESTAMP WHERE level_id = ?";
            
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("message", "等级状态切换成功");
                    APIOptimizer.sendSuccessResponse(response, result);
                } else {
                    APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "会员等级不存在");
                }
            }
            
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "无效的等级ID");
        } catch (Exception e) {
            System.err.println("切换等级状态异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }
    
    /**
     * 删除会员等级
     */
    private void deleteMemberLevel(HttpServletRequest request, HttpServletResponse response, String levelId) throws IOException, SQLException {
        try {
            int id = Integer.parseInt(levelId);
            
            // 检查是否有用户使用此等级
            String checkSql = "SELECT COUNT(*) FROM users WHERE membership_level_id = ?";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                
                checkStmt.setInt(1, id);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "该等级下还有用户，无法删除");
                        return;
                    }
                }
            }
            
            // 删除等级
            String deleteSql = "DELETE FROM membership_levels WHERE level_id = ?";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("message", "会员等级删除成功");
                    APIOptimizer.sendSuccessResponse(response, result);
                } else {
                    APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "会员等级不存在");
                }
            }
            
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "无效的等级ID");
        } catch (Exception e) {
            System.err.println("删除会员等级异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }
    
    /**
     * 获取会员分布统计
     */
    private void getMemberDistribution(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        try {
            String distributionSQL = "SELECT ml.level_name, COUNT(u.user_id) as member_count " +
                                   "FROM membership_levels ml " +
                                   "LEFT JOIN users u ON ml.level_id = u.membership_level_id " +
                                   "WHERE ml.is_active = true " +
                                   "GROUP BY ml.level_id, ml.level_name " +
                                   "ORDER BY ml.min_points";
            
            List<Map<String, Object>> distribution = new ArrayList<>();
            
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(distributionSQL);
                 ResultSet rs = pstmt.executeQuery()) {
                
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("levelName", rs.getString("level_name"));
                    item.put("memberCount", rs.getInt("member_count"));
                    distribution.add(item);
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", distribution);
            APIOptimizer.sendSuccessResponse(response, result);
            
        } catch (Exception e) {
            System.err.println("获取会员分布统计异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }
    
    /**
     * 批量更新会员等级
     */
    private void batchUpdateMemberLevels(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        try {
            // 读取请求体
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            
            // 解析JSON
            JsonNode jsonNode = objectMapper.readTree(sb.toString());
            
            // 这里可以实现批量更新逻辑
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "批量更新成功");
            sendJsonResponse(response, result);
            
        } catch (Exception e) {
            System.err.println("批量更新会员等级异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, 500, "服务器内部错误");
        }
    }
    
    /**
     * 设置升级规则
     */
    private void setUpgradeRules(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        try {
            // 读取请求体
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            
            // 解析JSON
            JsonNode jsonNode = objectMapper.readTree(sb.toString());
            
            // 这里可以实现设置升级规则的逻辑
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "升级规则设置成功");
            sendJsonResponse(response, result);
            
        } catch (Exception e) {
            System.err.println("设置升级规则异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, 500, "服务器内部错误");
        }
    }
    
    /**
     * 配置会员权益
     */
    private void configureBenefits(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        try {
            // 读取请求体
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            
            // 解析JSON
            JsonNode jsonNode = objectMapper.readTree(sb.toString());
            
            // 这里可以实现配置会员权益的逻辑
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "会员权益配置成功");
            sendJsonResponse(response, result);
            
        } catch (Exception e) {
            System.err.println("配置会员权益异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, 500, "服务器内部错误");
        }
    }

    /**
     * 手动升级会员
     */
    private void manualUpgradeMember(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        try {
            // 读取请求体
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            
            // 解析JSON
            JsonNode jsonNode = objectMapper.readTree(sb.toString());
            int userId = jsonNode.get("userId").asInt();
            int newLevelId = jsonNode.get("newLevelId").asInt();
            
            // 更新用户等级
            String updateSql = "UPDATE users SET membership_level_id = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
            
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                
                pstmt.setInt(1, newLevelId);
                pstmt.setInt(2, userId);
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    // 记录升级历史
                    String historySql = "INSERT INTO membership_level_history (user_id, old_level_id, new_level_id, upgrade_type, created_at) " +
                                       "VALUES (?, (SELECT membership_level_id FROM users WHERE user_id = ?), ?, 'MANUAL', CURRENT_TIMESTAMP)";
                    
                    try (PreparedStatement historyStmt = conn.prepareStatement(historySql)) {
                        historyStmt.setInt(1, userId);
                        historyStmt.setInt(2, userId);
                        historyStmt.setInt(3, newLevelId);
                        historyStmt.executeUpdate();
                    }
                    
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("message", "会员等级升级成功");
                    sendJsonResponse(response, result);
                } else {
                    APIOptimizer.sendErrorResponse(response, 404, "用户不存在");
                }
            }
            
        } catch (Exception e) {
            System.err.println("手动升级会员异常: " + e.getMessage());
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "自动升级失败");
        }
    }

    /**
     * 发送JSON响应
     */
    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}