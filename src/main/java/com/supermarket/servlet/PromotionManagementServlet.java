package com.supermarket.servlet;

import com.supermarket.dao.PromotionDAO;
import com.supermarket.dao.impl.PromotionDAOImpl;
import com.supermarket.model.Promotion;
import com.supermarket.model.User;
import com.supermarket.util.APIOptimizer;
import com.supermarket.util.DatabaseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

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
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 促销活动管理Servlet - 增强版
 * 处理促销活动的增删改查操作，特别支持双倍积分日活动
 */
@WebServlet("/manager/promotions/*")
public class PromotionManagementServlet extends HttpServlet {
    
    private PromotionDAO promotionDAO = new PromotionDAOImpl();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 验证经理权限
        if (!validateManagerPermission(request, response)) {
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 显示促销活动管理页面
            showPromotionManagementPage(request, response);
        } else if (pathInfo.equals("/list")) {
            // 获取促销活动列表
            getPromotionList(request, response);
        } else if (pathInfo.startsWith("/detail/")) {
            // 获取促销活动详情
            getPromotionDetail(request, response, pathInfo);
        } else if (pathInfo.equals("/statistics")) {
            // 获取促销活动统计信息
            getPromotionStatistics(request, response);
        } else if (pathInfo.equals("/double-points-config")) {
            // 获取双倍积分日配置
            getDoublePointsConfig(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 验证经理权限
        if (!validateManagerPermission(request, response)) {
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 创建新促销活动
            createPromotion(request, response);
        } else if (pathInfo.equals("/update")) {
            // 更新促销活动
            updatePromotion(request, response);
        } else if (pathInfo.equals("/delete")) {
            // 删除促销活动
            deletePromotion(request, response);
        } else if (pathInfo.equals("/toggle")) {
            // 切换促销活动状态
            togglePromotionStatus(request, response);
        } else if (pathInfo.equals("/batch-create-double-points")) {
            // 批量创建双倍积分日活动
            batchCreateDoublePointsDays(request, response);
        } else if (pathInfo.equals("/set-double-points-rule")) {
            // 设置双倍积分日规则
            setDoublePointsRule(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    /**
     * 验证经理权限
     */
    private boolean validateManagerPermission(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        
        User user = (User) session.getAttribute("user");
        if (user.getRole() != User.UserRole.MANAGER) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "权限不足");
            return false;
        }
        
        return true;
    }
    
    /**
     * 显示促销活动管理页面
     */
    private void showPromotionManagementPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/manager/promotions.jsp").forward(request, response);
    }
    
    /**
     * 获取促销活动列表
     */
    private void getPromotionList(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            List<Promotion> promotions = promotionDAO.getAllPromotions();
            
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", promotions);
            
            out.print(objectMapper.writeValueAsString(result));
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取促销活动列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取促销活动详情
     */
    private void getPromotionDetail(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws IOException {
        try {
            String[] parts = pathInfo.split("/");
            if (parts.length < 3) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的促销活动ID");
                return;
            }
            
            int promotionId = Integer.parseInt(parts[2]);
            Promotion promotion = promotionDAO.getPromotionById(promotionId);
            
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            Map<String, Object> result = new HashMap<>();
            if (promotion != null) {
                result.put("success", true);
                result.put("data", promotion);
            } else {
                result.put("success", false);
                result.put("message", "促销活动不存在");
            }
            
            out.print(objectMapper.writeValueAsString(result));
            out.flush();
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的促销活动ID");
        } catch (Exception e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取促销活动详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取促销活动统计信息
     */
    private void getPromotionStatistics(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            List<Promotion> allPromotions = promotionDAO.getAllPromotions();
            List<Promotion> activePromotions = promotionDAO.getActivePromotions();
            
            LocalDate today = LocalDate.now();
            int totalCount = allPromotions.size();
            int activeCount = activePromotions.size();
            int pendingCount = 0;
            int expiredCount = 0;
            int doublePointsCount = 0;
            
            for (Promotion promotion : allPromotions) {
                LocalDate startDate = promotion.getStartDate().toLocalDate();
                LocalDate endDate = promotion.getEndDate().toLocalDate();
                
                if (promotion.getPromotionType() == Promotion.PromotionType.DOUBLE_POINTS) {
                    doublePointsCount++;
                }
                
                if (promotion.isActive()) {
                    if (today.isBefore(startDate)) {
                        pendingCount++;
                    } else if (today.isAfter(endDate)) {
                        expiredCount++;
                    }
                } else {
                    expiredCount++;
                }
            }
            
            // 获取促销活动使用统计
            Map<String, Object> usageStats = getPromotionUsageStatistics();
            
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("total", totalCount);
            statistics.put("active", activeCount);
            statistics.put("pending", pendingCount);
            statistics.put("expired", expiredCount);
            statistics.put("doublePoints", doublePointsCount);
            
            // 添加使用统计数据
            statistics.put("usageStats", usageStats);
            
            result.put("data", statistics);
            
            out.print(objectMapper.writeValueAsString(result));
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取统计信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取促销活动使用统计
     */
    private Map<String, Object> getPromotionUsageStatistics() {
        Map<String, Object> usageStats = new HashMap<>();
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            // 总使用次数
            String totalUsageSql = "SELECT COUNT(*) as total_usage FROM member_promotion_usage";
            try (PreparedStatement stmt = conn.prepareStatement(totalUsageSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usageStats.put("totalUsage", rs.getInt("total_usage"));
                }
            }
            
            // 本月使用次数
            String monthlyUsageSql = "SELECT COUNT(*) as monthly_usage FROM member_promotion_usage " +
                                   "WHERE YEAR(usage_date) = YEAR(CURDATE()) AND MONTH(usage_date) = MONTH(CURDATE())";
            try (PreparedStatement stmt = conn.prepareStatement(monthlyUsageSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usageStats.put("monthlyUsage", rs.getInt("monthly_usage"));
                }
            }
            
            // 活跃用户数（本月使用过促销活动的用户）
            String activeUsersSql = "SELECT COUNT(DISTINCT member_id) as active_users FROM member_promotion_usage " +
                                  "WHERE YEAR(usage_date) = YEAR(CURDATE()) AND MONTH(usage_date) = MONTH(CURDATE())";
            try (PreparedStatement stmt = conn.prepareStatement(activeUsersSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usageStats.put("activeUsers", rs.getInt("active_users"));
                }
            }
            
            // 平均使用金额
            String avgAmountSql = "SELECT AVG(usage_amount) as avg_amount FROM member_promotion_usage";
            try (PreparedStatement stmt = conn.prepareStatement(avgAmountSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usageStats.put("avgUsageAmount", Math.round(rs.getDouble("avg_amount") * 100.0) / 100.0);
                }
            }
            
            // 最受欢迎的促销活动类型
            String popularTypeSql = "SELECT p.promotion_type, COUNT(*) as usage_count " +
                                  "FROM member_promotion_usage mpu " +
                                  "JOIN promotions p ON mpu.promotion_id = p.promotion_id " +
                                  "GROUP BY p.promotion_type " +
                                  "ORDER BY usage_count DESC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(popularTypeSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usageStats.put("popularType", rs.getString("promotion_type"));
                    usageStats.put("popularTypeCount", rs.getInt("usage_count"));
                }
            }
            
            // 每日使用趋势（最近7天）
            String dailyTrendSql = "SELECT DATE(usage_date) as usage_day, COUNT(*) as daily_count " +
                                 "FROM member_promotion_usage " +
                                 "WHERE usage_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                                 "GROUP BY DATE(usage_date) " +
                                 "ORDER BY usage_day";
            List<Map<String, Object>> dailyTrend = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(dailyTrendSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> dayData = new HashMap<>();
                    dayData.put("date", rs.getDate("usage_day").toString());
                    dayData.put("count", rs.getInt("daily_count"));
                    dailyTrend.add(dayData);
                }
            }
            usageStats.put("dailyTrend", dailyTrend);
            
        } catch (SQLException e) {
            e.printStackTrace();
            // 如果查询失败，返回默认值
            usageStats.put("totalUsage", 0);
            usageStats.put("monthlyUsage", 0);
            usageStats.put("activeUsers", 0);
            usageStats.put("avgUsageAmount", 0.0);
            usageStats.put("popularType", "UNKNOWN");
            usageStats.put("popularTypeCount", 0);
            usageStats.put("dailyTrend", new ArrayList<>());
        }
        
        return usageStats;
    }
    
    /**
     * 创建促销活动
     */
    private void createPromotion(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            
            // 获取表单参数
            String promotionName = request.getParameter("promotionName");
            String promotionType = request.getParameter("promotionType");
            String description = request.getParameter("description");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String ruleConfig = request.getParameter("ruleConfig");
            String minPurchaseAmount = request.getParameter("minPurchaseAmount");
            String maxUsagePerMember = request.getParameter("maxUsagePerMember");
            
            // 参数验证
            if (promotionName == null || promotionName.trim().isEmpty() ||
                promotionType == null || promotionType.trim().isEmpty() ||
                startDate == null || startDate.trim().isEmpty() ||
                endDate == null || endDate.trim().isEmpty()) {
                
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "请填写完整的促销活动信息");
                return;
            }
            Promotion.PromotionType type;
            try {
                type = Promotion.PromotionType.valueOf(promotionType.trim());
            } catch (Exception ex) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "促销类型无效");
                return;
            }

            Date start;
            Date end;
            try {
                start = Date.valueOf(startDate.trim());
                end = Date.valueOf(endDate.trim());
            } catch (Exception ex) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "日期格式无效");
                return;
            }
            if (start.after(end)) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "开始日期不能晚于结束日期");
                return;
            }

            Promotion promotion = new Promotion();
            promotion.setPromotionName(promotionName.trim());
            promotion.setPromotionType(type);
            promotion.setDescription(description != null ? description.trim() : "");
            promotion.setStartDate(start);
            promotion.setEndDate(end);

            if ("DOUBLE_POINTS".equals(promotionType)) {
                boolean needGenerate = (ruleConfig == null || ruleConfig.trim().isEmpty());
                if (needGenerate) {
                    ruleConfig = generateDoublePointsRuleConfig(request);
                }
            }
            promotion.setRuleConfig(ruleConfig != null ? ruleConfig.trim() : "{}");

            double minAmount = 0.0;
            if (minPurchaseAmount != null && !minPurchaseAmount.trim().isEmpty()) {
                try {
                    minAmount = Double.parseDouble(minPurchaseAmount.trim());
                } catch (Exception ex) {
                    APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "最低消费金额格式无效");
                    return;
                }
                if (minAmount < 0) {
                    APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "最低消费金额不能为负数");
                    return;
                }
            }
            promotion.setMinPurchaseAmount(minAmount);

            int maxUsage = 0;
            if (maxUsagePerMember != null && !maxUsagePerMember.trim().isEmpty()) {
                try {
                    maxUsage = Integer.parseInt(maxUsagePerMember.trim());
                } catch (Exception ex) {
                    APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "会员使用次数格式无效");
                    return;
                }
                if (maxUsage < 0) {
                    APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "会员使用次数不能为负数");
                    return;
                }
            }
            promotion.setMaxUsagePerMember(maxUsage);

            HttpSession session = request.getSession();
            User manager = (User) session.getAttribute("user");
            promotion.setCreatedBy(manager.getUserId());

            boolean success = promotionDAO.createPromotion(promotion);

            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();

            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "促销活动创建成功" : "促销活动创建失败");

            out.print(objectMapper.writeValueAsString(result));
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "创建促销活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成双倍积分日规则配置
     */
    private String generateDoublePointsRuleConfig(HttpServletRequest request) {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("multiplier", 2.0); // 积分倍数
            
            String timeRestriction = request.getParameter("timeRestriction");
            if (timeRestriction != null && !timeRestriction.trim().isEmpty()) {
                config.put("timeRestriction", timeRestriction);
            }
            
            String categoryRestriction = request.getParameter("categoryRestriction");
            if (categoryRestriction != null && !categoryRestriction.trim().isEmpty()) {
                config.put("categoryRestriction", categoryRestriction);
            }
            
            String memberLevelRestriction = request.getParameter("memberLevelRestriction");
            if (memberLevelRestriction != null && !memberLevelRestriction.trim().isEmpty()) {
                config.put("memberLevelRestriction", memberLevelRestriction);
            }
            
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            return "{\"multiplier\": 2.0}";
        }
    }
    
    /**
     * 批量创建双倍积分日活动
     */
    private void batchCreateDoublePointsDays(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            
            String datesJson = request.getParameter("dates");
            String ruleConfig = request.getParameter("ruleConfig");
            
            if (datesJson == null || datesJson.trim().isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "请选择日期");
                return;
            }
            
            JsonNode datesNode = objectMapper.readTree(datesJson);
            HttpSession session = request.getSession();
            User manager = (User) session.getAttribute("user");
            
            int successCount = 0;
            int totalCount = datesNode.size();
            
            for (JsonNode dateNode : datesNode) {
                String dateStr = dateNode.asText();
                LocalDate date = LocalDate.parse(dateStr);
                
                Promotion promotion = new Promotion();
                promotion.setPromotionName("双倍积分日 - " + date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
                promotion.setPromotionType(Promotion.PromotionType.DOUBLE_POINTS);
                promotion.setDescription("在此日期购物可获得双倍积分奖励");
                promotion.setStartDate(Date.valueOf(date));
                promotion.setEndDate(Date.valueOf(date));
                promotion.setRuleConfig(ruleConfig != null ? ruleConfig : "{\"multiplier\": 2.0}");
                promotion.setMinPurchaseAmount(0.0);
                promotion.setMaxUsagePerMember(1);
                promotion.setCreatedBy(manager.getUserId());
                
                if (promotionDAO.createPromotion(promotion)) {
                    successCount++;
                }
            }
            
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", successCount > 0);
            result.put("message", String.format("成功创建 %d/%d 个双倍积分日活动", successCount, totalCount));
            result.put("successCount", successCount);
            result.put("totalCount", totalCount);
            
            out.print(objectMapper.writeValueAsString(result));
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "批量创建双倍积分日失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新促销活动
     */
    private void updatePromotion(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            
            String promotionIdStr = request.getParameter("promotionId");
            if (promotionIdStr == null || promotionIdStr.trim().isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "促销活动ID不能为空");
                return;
            }
            
            int promotionId = Integer.parseInt(promotionIdStr);
            
            Promotion promotion = promotionDAO.getPromotionById(promotionId);
            if (promotion == null) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "促销活动不存在");
                return;
            }
            
            // 更新字段
            String promotionName = request.getParameter("promotionName");
            String promotionType = request.getParameter("promotionType");
            String description = request.getParameter("description");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String ruleConfig = request.getParameter("ruleConfig");
            String minPurchaseAmount = request.getParameter("minPurchaseAmount");
            String maxUsagePerMember = request.getParameter("maxUsagePerMember");
            
            if (promotionName != null && !promotionName.trim().isEmpty()) {
                promotion.setPromotionName(promotionName.trim());
            }
            if (promotionType != null && !promotionType.trim().isEmpty()) {
                promotion.setPromotionType(Promotion.PromotionType.valueOf(promotionType));
                // 如果改为双倍积分日，重新生成规则配置
                if ("DOUBLE_POINTS".equals(promotionType)) {
                    ruleConfig = generateDoublePointsRuleConfig(request);
                }
            }
            if (description != null) {
                promotion.setDescription(description.trim());
            }
            if (startDate != null && !startDate.trim().isEmpty()) {
                promotion.setStartDate(Date.valueOf(startDate));
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                promotion.setEndDate(Date.valueOf(endDate));
            }
            if (ruleConfig != null) {
                promotion.setRuleConfig(ruleConfig.trim());
            }
            if (minPurchaseAmount != null && !minPurchaseAmount.trim().isEmpty()) {
                promotion.setMinPurchaseAmount(Double.parseDouble(minPurchaseAmount));
            }
            if (maxUsagePerMember != null && !maxUsagePerMember.trim().isEmpty()) {
                promotion.setMaxUsagePerMember(Integer.parseInt(maxUsagePerMember));
            }
            
            boolean success = promotionDAO.updatePromotion(promotion);
            
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "促销活动更新成功" : "促销活动更新失败");
            
            out.print(objectMapper.writeValueAsString(result));
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "更新促销活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除促销活动
     */
    private void deletePromotion(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String promotionIdStr = request.getParameter("promotionId");
            if (promotionIdStr == null || promotionIdStr.trim().isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "促销活动ID不能为空");
                return;
            }
            
            int promotionId = Integer.parseInt(promotionIdStr);
            boolean success = promotionDAO.deletePromotion(promotionId);
            
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "促销活动删除成功" : "促销活动删除失败");
            
            out.print(objectMapper.writeValueAsString(result));
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "删除促销活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 切换促销活动状态
     */
    private void togglePromotionStatus(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String promotionIdStr = request.getParameter("promotionId");
            if (promotionIdStr == null || promotionIdStr.trim().isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "促销活动ID不能为空");
                return;
            }
            
            int promotionId = Integer.parseInt(promotionIdStr);
            boolean success = promotionDAO.togglePromotionStatus(promotionId);
            
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "状态切换成功" : "状态切换失败");
            
            out.print(objectMapper.writeValueAsString(result));
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "切换状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取双倍积分日配置
     */
    private void getDoublePointsConfig(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            List<Promotion> doublePointsPromotions = promotionDAO.getPromotionsByType(Promotion.PromotionType.DOUBLE_POINTS);
            
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", doublePointsPromotions);
            
            out.print(objectMapper.writeValueAsString(result));
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取双倍积分日配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置双倍积分日规则
     */
    private void setDoublePointsRule(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            
            String ruleConfigJson = request.getParameter("ruleConfig");
            if (ruleConfigJson == null || ruleConfigJson.trim().isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "规则配置不能为空");
                return;
            }
            
            // 验证JSON格式
            JsonNode ruleNode = objectMapper.readTree(ruleConfigJson);
            
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "双倍积分日规则设置成功");
            result.put("ruleConfig", ruleNode);
            
            out.print(objectMapper.writeValueAsString(result));
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "设置双倍积分日规则失败: " + e.getMessage());
        }
    }
}
