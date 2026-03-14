package com.supermarket.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.supermarket.dao.RedemptionItemDAO;
import com.supermarket.dao.RedemptionRecordDAO;
import com.supermarket.dao.impl.RedemptionItemDAOImpl;
import com.supermarket.dao.impl.RedemptionRecordDAOImpl;
import com.supermarket.model.RedemptionItem;
import com.supermarket.model.RedemptionRecord;
import com.supermarket.model.User;
import com.supermarket.util.APIOptimizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.time.LocalDateTime;

/**
 * 兑换商品管理Servlet
 * 处理兑换商品的增删改查操作和兑换记录管理
 * 支持经理权限访问
 */
@WebServlet({"/manager/redemption-items", "/manager/redemption-items/*"})
public class RedemptionManagementServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(RedemptionManagementServlet.class.getName());
    private final Gson gson = new Gson();
    private final RedemptionItemDAO redemptionItemDAO = new RedemptionItemDAOImpl();
    private final RedemptionRecordDAO redemptionRecordDAO = new RedemptionRecordDAOImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 权限验证
        if (!isAuthorized(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String action = request.getParameter("action");
        
        try {
            // 设置JSON响应格式
            response.setContentType("application/json;charset=UTF-8");
            
            if (servletPath.contains("redemption-items")) {
                handleItemsRequest(request, response, pathInfo);
            } else if (servletPath.contains("redemption-records")) {
                handleRecordsRequest(request, response, pathInfo);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!isAuthorized(request)) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "权限不足");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            if ("adjustStock".equals(action)) {
                handleAdjustStock(request, response);
            } else if ("toggleStatus".equals(action)) {
                handleToggleStatus(request, response);
            } else {
                // 创建新商品
                handleCreateItem(request, response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "处理POST请求失败", e);
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!isAuthorized(request)) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "权限不足");
            return;
        }
        
        try {
            handleUpdateItem(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "处理PUT请求失败", e);
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!isAuthorized(request)) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "权限不足");
            return;
        }
        
        try {
            handleDeleteItem(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "处理DELETE请求失败", e);
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }
    
    /**
     * 处理兑换商品相关请求
     */
    private void handleItemsRequest(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws SQLException, IOException {
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 获取兑换商品列表
            getRedemptionItems(request, response);
        } else if (pathInfo.equals("/categories")) {
            // 获取商品分类列表
            getItemCategories(response);
        } else if (pathInfo.equals("/available")) {
            // 获取可用的兑换商品
            getAvailableItems(request, response);
        } else {
            // 获取特定商品详情
            String itemId = pathInfo.substring(1);
            getRedemptionItemById(response, itemId);
        }
    }
    
    /**
     * 处理兑换记录相关请求
     */
    private void handleRecordsRequest(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws SQLException, IOException {
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 获取兑换记录列表
            getRedemptionRecords(request, response);
        } else if (pathInfo.equals("/statistics")) {
            // 获取兑换统计数据
            getRedemptionStatistics(response);
        } else {
            // 获取特定记录详情
            String recordId = pathInfo.substring(1);
            getRedemptionRecordById(response, recordId);
        }
    }
    
    /**
     * 获取兑换商品列表
     */
    private void getRedemptionItems(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        String category = request.getParameter("category");
        String minPoints = request.getParameter("minPoints");
        String maxPoints = request.getParameter("maxPoints");
        String activeOnly = request.getParameter("activeOnly");
        
        List<RedemptionItem> items;
        
        if (category != null && !category.isEmpty()) {
            items = redemptionItemDAO.findByCategory(category);
        } else if (minPoints != null && maxPoints != null) {
            int min = Integer.parseInt(minPoints);
            int max = Integer.parseInt(maxPoints);
            items = redemptionItemDAO.findByPointsRange(min, max);
        } else if ("true".equals(activeOnly)) {
            items = redemptionItemDAO.findAvailableItems();
        } else {
            items = redemptionItemDAO.findAll();
        }
        
        // 转换为DataTables格式
        Map<String, Object> result = new HashMap<>();
        result.put("data", items.stream().map(this::convertItemToMap).collect(Collectors.toList()));
        result.put("recordsTotal", items.size());
        result.put("recordsFiltered", items.size());
        
        APIOptimizer.sendSuccessResponse(response, result);
    }
    
    /**
     * 获取商品分类列表
     */
    private void getItemCategories(HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", RedemptionItem.Category.values());
        APIOptimizer.sendSuccessResponse(response, result);
    }
    
    /**
     * 获取可用的兑换商品
     */
    private void getAvailableItems(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        List<RedemptionItem> items = redemptionItemDAO.findAvailableItems();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", items.stream().map(this::convertItemToMap).collect(Collectors.toList()));
        
        APIOptimizer.sendSuccessResponse(response, result);
    }
    
    /**
     * 根据ID获取兑换商品
     */
    private void getRedemptionItemById(HttpServletResponse response, String itemId) 
            throws SQLException, IOException {
        
        try {
            int id = Integer.parseInt(itemId);
            RedemptionItem item = redemptionItemDAO.findById(id);
            
            if (item != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("data", convertItemToMap(item));
                APIOptimizer.sendSuccessResponse(response, result);
            } else {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "兑换商品不存在");
            }
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "无效的商品ID");
        }
    }
    
    /**
     * 创建兑换商品
     */
    private void createRedemptionItem(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        String requestBody = getRequestBody(request);
        JsonObject json = new JsonParser().parse(requestBody).getAsJsonObject();
        
        try {
            RedemptionItem item = new RedemptionItem();
            item.setItemName(json.get("itemName").getAsString());
            item.setCategory(json.get("category").getAsString());
            item.setPointsRequired(json.get("pointsRequired").getAsInt());
            item.setDescription(json.get("description").getAsString());
            item.setStockQuantity(json.get("stockQuantity").getAsInt());
            item.setActive(json.has("isActive") ? json.get("isActive").getAsBoolean() : true);
            
            boolean success = redemptionItemDAO.createItem(item);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "兑换商品创建成功");
                APIOptimizer.sendSuccessResponse(response, result);
            } else {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "创建兑换商品失败");
            }
        } catch (Exception e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "请求数据格式错误: " + e.getMessage());
        }
    }
    
    /**
     * 更新兑换商品
     */
    private void updateRedemptionItem(HttpServletRequest request, HttpServletResponse response, String itemId) 
            throws SQLException, IOException {
        
        try {
            int id = Integer.parseInt(itemId);
            RedemptionItem existingItem = redemptionItemDAO.findById(id);
            
            if (existingItem == null) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "兑换商品不存在");
                return;
            }
            
            String requestBody = getRequestBody(request);
            JsonObject json = new JsonParser().parse(requestBody).getAsJsonObject();
            
            // 更新商品信息
            if (json.has("itemName")) {
                existingItem.setItemName(json.get("itemName").getAsString());
            }
            if (json.has("category")) {
                existingItem.setCategory(json.get("category").getAsString());
            }
            if (json.has("pointsRequired")) {
                existingItem.setPointsRequired(json.get("pointsRequired").getAsInt());
            }
            if (json.has("description")) {
                existingItem.setDescription(json.get("description").getAsString());
            }
            if (json.has("stockQuantity")) {
                existingItem.setStockQuantity(json.get("stockQuantity").getAsInt());
            }
            if (json.has("isActive")) {
                existingItem.setActive(json.get("isActive").getAsBoolean());
            }
            
            boolean success = redemptionItemDAO.updateItem(existingItem);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "兑换商品更新成功");
                APIOptimizer.sendSuccessResponse(response, result);
            } else {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "更新兑换商品失败");
            }
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "无效的商品ID");
        } catch (IllegalArgumentException e) {
            APIOptimizer.sendErrorResponse(response, "商品分类无效");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "更新商品失败", e);
            APIOptimizer.sendErrorResponse(response, "更新商品失败");
        }
    }
    
    /**
     * 删除兑换商品
     */
    private void deleteRedemptionItem(HttpServletResponse response, String itemId) 
            throws SQLException, IOException {
        
        try {
            int id = Integer.parseInt(itemId);
            boolean success = redemptionItemDAO.deleteItem(id);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "兑换商品删除成功");
                APIOptimizer.sendSuccessResponse(response, result);
            } else {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "删除兑换商品失败");
            }
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "无效的商品ID");
        }
    }
    
    /**
     * 获取兑换记录列表
     */
    private void getRedemptionRecords(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        String userId = request.getParameter("userId");
        String itemId = request.getParameter("itemId");
        String status = request.getParameter("status");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        
        List<RedemptionRecord> records;
        
        if (userId != null && !userId.isEmpty()) {
            records = redemptionRecordDAO.findByUserId(Integer.parseInt(userId));
        } else if (itemId != null && !itemId.isEmpty()) {
            records = redemptionRecordDAO.findByItemId(Integer.parseInt(itemId));
        } else if (status != null && !status.isEmpty()) {
            records = redemptionRecordDAO.findByStatus(status);
        } else if (startDate != null && endDate != null) {
            LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);
            records = redemptionRecordDAO.findByDateRange(start, end);
        } else {
            records = redemptionRecordDAO.findAll();
        }
        
        // 转换为DataTables格式
        Map<String, Object> result = new HashMap<>();
        result.put("data", records.stream().map(this::convertRecordToMap).collect(Collectors.toList()));
        result.put("recordsTotal", records.size());
        result.put("recordsFiltered", records.size());
        
        APIOptimizer.sendSuccessResponse(response, result);
    }
    
    /**
     * 获取兑换统计数据
     */
    private void getRedemptionStatistics(HttpServletResponse response) 
            throws SQLException, IOException {
        
        // 获取当前时间范围的统计数据
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfYear = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        
        RedemptionRecordDAO.RedemptionStatistics stats = redemptionRecordDAO.getStatistics(startOfYear, now);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", new HashMap<String, Object>() {{
            put("totalRedemptions", stats.getTotalRecords());
            put("totalPointsUsed", stats.getTotalPointsUsed());
            put("todayRedemptions", 0); // 暂时设为0，需要实现具体逻辑
            put("todayPointsUsed", 0); // 暂时设为0，需要实现具体逻辑
            put("monthlyRedemptions", 0); // 暂时设为0，需要实现具体逻辑
            put("monthlyPointsUsed", 0); // 暂时设为0，需要实现具体逻辑
        }});
        
        APIOptimizer.sendSuccessResponse(response, result);
    }
    
    /**
     * 根据ID获取兑换记录
     */
    private void getRedemptionRecordById(HttpServletResponse response, String recordId) 
            throws SQLException, IOException {
        
        try {
            int id = Integer.parseInt(recordId);
            RedemptionRecord record = redemptionRecordDAO.findById(id);
            
            if (record != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("data", convertRecordToMap(record));
                APIOptimizer.sendSuccessResponse(response, result);
            } else {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "兑换记录不存在");
            }
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "无效的记录ID");
        }
    }
    
    /**
     * 更新兑换记录
     */
    private void updateRedemptionRecord(HttpServletRequest request, HttpServletResponse response, String recordId) 
            throws SQLException, IOException {
        
        try {
            int id = Integer.parseInt(recordId);
            String requestBody = getRequestBody(request);
            JsonObject json = new JsonParser().parse(requestBody).getAsJsonObject();
            
            if (json.has("status")) {
                String status = json.get("status").getAsString();
                String notes = json.has("notes") ? json.get("notes").getAsString() : null;
                
                boolean success = redemptionRecordDAO.updateStatus(id, status);
                
                if (success) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("message", "兑换记录状态更新成功");
                    APIOptimizer.sendSuccessResponse(response, result);
                } else {
                    APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "更新兑换记录失败");
                }
            } else {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "缺少必要的更新参数");
            }
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "无效的记录ID");
        } catch (Exception e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "请求数据格式错误: " + e.getMessage());
        }
    }
    
    /**
     * 将RedemptionItem转换为Map
     */
    private Map<String, Object> convertItemToMap(RedemptionItem item) {
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", item.getItemId());
        map.put("itemName", item.getItemName());
        map.put("category", item.getCategory());
        map.put("categoryDisplay", item.getCategory());
        map.put("pointsRequired", item.getPointsRequired());
        map.put("description", item.getDescription());
        map.put("stockQuantity", item.getStockQuantity());
        map.put("isActive", item.isActive());
        map.put("status", item.isActive() ? "ACTIVE" : "INACTIVE");
        map.put("stockStatus", item.getStockStatus());
        map.put("available", item.isAvailable());
        map.put("createdAt", item.getCreatedAt());
        map.put("updatedAt", item.getUpdatedAt());
        return map;
    }
    
    /**
     * 将RedemptionRecord转换为Map
     */
    private Map<String, Object> convertRecordToMap(RedemptionRecord record) {
        Map<String, Object> map = new HashMap<>();
        map.put("recordId", record.getRecordId());
        map.put("userId", record.getUserId());
        map.put("itemId", record.getItemId());
        map.put("pointsUsed", record.getPointsUsed());
        map.put("quantity", record.getQuantity());
        map.put("redemptionDate", record.getRedemptionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        map.put("status", record.getStatus());
        map.put("statusDisplay", record.getStatus());
        map.put("cashierId", record.getCashierId());
        map.put("notes", record.getNotes());
        return map;
    }
    
    /**
     * 权限验证 - 支持经理
     */
    private boolean isAuthorized(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return false;
        }
        
        User user = (User) session.getAttribute("user");
        return user.getRole() == User.UserRole.MANAGER;
    }
    
    /**
     * 获取请求体内容
     */
    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
    
    /**
     * 发送JSON响应
     */
    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(data));
            out.flush();
        }
    }
    
    /**
     * 处理获取单个商品信息
     */
    private void handleGetItem(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        String itemIdStr = request.getParameter("itemId");
        
        if (itemIdStr == null || itemIdStr.trim().isEmpty()) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "商品ID不能为空");
            return;
        }
        
        try {
            int itemId = Integer.parseInt(itemIdStr);
            RedemptionItem item = redemptionItemDAO.findById(itemId);
            
            if (item == null) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "商品不存在");
                return;
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", convertItemToMap(item));
            APIOptimizer.sendSuccessResponse(response, result);
            
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "商品ID格式错误");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "获取商品信息失败", e);
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取商品信息失败");
        }
    }
    
    /**
     * 处理创建商品
     */
    private void handleCreateItem(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            // 获取参数
            String itemName = request.getParameter("itemName");
            String categoryStr = request.getParameter("category");
            String pointsRequiredStr = request.getParameter("pointsRequired");
            String stockQuantityStr = request.getParameter("stockQuantity");
            String description = request.getParameter("description");
            String isActiveStr = request.getParameter("isActive");
            
            // 验证必填参数
            if (itemName == null || itemName.trim().isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "商品名称不能为空");
                return;
            }
            
            if (categoryStr == null || categoryStr.trim().isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "商品分类不能为空");
                return;
            }
            
            if (pointsRequiredStr == null || pointsRequiredStr.trim().isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "所需积分不能为空");
                return;
            }
            
            if (stockQuantityStr == null || stockQuantityStr.trim().isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "库存数量不能为空");
                return;
            }
            
            // 解析参数
            String category = categoryStr; // 直接使用字符串，不转换为枚举
            
            int pointsRequired = Integer.parseInt(pointsRequiredStr);
            int stockQuantity = Integer.parseInt(stockQuantityStr);
            boolean isActive = "on".equals(isActiveStr) || "true".equals(isActiveStr);
            
            if (pointsRequired <= 0) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "所需积分必须大于0");
                return;
            }
            
            if (stockQuantity < 0) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "库存数量不能为负数");
                return;
            }
            
            // 创建商品对象
            RedemptionItem item = new RedemptionItem();
            item.setItemName(itemName.trim());
            item.setCategory(category);
            item.setPointsRequired(pointsRequired);
            item.setStockQuantity(stockQuantity);
            item.setDescription(description != null ? description.trim() : null);
            item.setActive(isActive);
            item.setCreatedAt(LocalDateTime.now());
            item.setUpdatedAt(LocalDateTime.now());
            
            // 保存到数据库
            boolean success = redemptionItemDAO.createItem(item);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "商品创建成功");
                APIOptimizer.sendSuccessResponse(response, result);
            } else {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "商品创建失败");
            }
            
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "数字格式错误");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "创建商品失败", e);
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "创建商品失败");
        }
    }
    
    /**
     * 处理更新商品
     */
    private void handleUpdateItem(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            // 获取参数
            String itemIdStr = request.getParameter("itemId");
            String itemName = request.getParameter("itemName");
            String categoryStr = request.getParameter("category");
            String pointsRequiredStr = request.getParameter("pointsRequired");
            String description = request.getParameter("description");
            String isActiveStr = request.getParameter("isActive");
            
            // 验证必填参数
            if (itemIdStr == null || itemIdStr.trim().isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "商品ID不能为空");
                return;
            }
            
            if (itemName == null || itemName.trim().isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "商品名称不能为空");
                return;
            }
            
            if (categoryStr == null || categoryStr.trim().isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "商品分类不能为空");
                return;
            }
            
            if (pointsRequiredStr == null || pointsRequiredStr.trim().isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "所需积分不能为空");
                return;
            }
            
            // 解析参数
            int itemId = Integer.parseInt(itemIdStr);
            String category = categoryStr; // 直接使用字符串，不转换为枚举
            int pointsRequired = Integer.parseInt(pointsRequiredStr);
            boolean isActive = "on".equals(isActiveStr) || "true".equals(isActiveStr);
            
            if (pointsRequired <= 0) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "所需积分必须大于0");
                return;
            }
            
            // 获取现有商品
            RedemptionItem existingItem = redemptionItemDAO.findById(itemId);
            if (existingItem == null) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "商品不存在");
                return;
            }
            
            // 更新商品信息
            existingItem.setItemName(itemName.trim());
            existingItem.setCategory(category);
            existingItem.setPointsRequired(pointsRequired);
            existingItem.setDescription(description != null ? description.trim() : null);
            existingItem.setActive(isActive);
            existingItem.setUpdatedAt(LocalDateTime.now());
            
            // 保存更新
            boolean success = redemptionItemDAO.updateItem(existingItem);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "商品更新成功");
                APIOptimizer.sendSuccessResponse(response, result);
            } else {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "商品更新失败");
            }
            
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "数字格式错误");
        } catch (IllegalArgumentException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "商品分类无效");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "更新商品失败", e);
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "更新商品失败");
        }
    }
    
    /**
     * 处理删除商品
     */
    private void handleDeleteItem(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        String itemIdStr = request.getParameter("itemId");
        
        if (itemIdStr == null || itemIdStr.trim().isEmpty()) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "商品ID不能为空");
            return;
        }
        
        try {
            int itemId = Integer.parseInt(itemIdStr);
            
            // 检查商品是否存在
            RedemptionItem item = redemptionItemDAO.findById(itemId);
            if (item == null) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "商品不存在");
                return;
            }
            
            // 检查是否有相关的兑换记录
            List<RedemptionRecord> records = redemptionRecordDAO.findByItemId(itemId);
            if (!records.isEmpty()) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, "该商品存在兑换记录，无法删除");
                return;
            }
            
            // 删除商品
            boolean success = redemptionItemDAO.deleteItem(itemId);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "商品删除成功");
                APIOptimizer.sendSuccessResponse(response, result);
            } else {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "商品删除失败");
            }
            
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "商品ID格式错误");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "删除商品失败", e);
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "删除商品失败");
        }
    }
    
    /**
     * 处理库存调整
     */
    private void handleAdjustStock(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            String itemIdStr = request.getParameter("itemId");
            String adjustmentTypeStr = request.getParameter("adjustmentType");
            String adjustmentStr = request.getParameter("adjustment");
            String reason = request.getParameter("reason");
            
            if (itemIdStr == null || adjustmentTypeStr == null || adjustmentStr == null) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "参数不完整");
                return;
            }
            
            int itemId = Integer.parseInt(itemIdStr);
            int adjustment = Integer.parseInt(adjustmentStr);
            
            if (adjustment < 0) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "调整数量不能为负数");
                return;
            }
            
            // 获取商品
            RedemptionItem item = redemptionItemDAO.findById(itemId);
            if (item == null) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "商品不存在");
                return;
            }
            
            int newStock;
            switch (adjustmentTypeStr) {
                case "add":
                    newStock = item.getStockQuantity() + adjustment;
                    break;
                case "subtract":
                    newStock = item.getStockQuantity() - adjustment;
                    if (newStock < 0) {
                        APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "库存不足，无法减少" + adjustment + "个");
                        return;
                    }
                    break;
                case "set":
                    newStock = adjustment;
                    break;
                default:
                    APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "无效的调整类型");
                    return;
            }
            
            // 更新库存
            boolean success = redemptionItemDAO.updateStock(itemId, newStock);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "库存调整成功");
                APIOptimizer.sendSuccessResponse(response, result);
            } else {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "库存调整失败");
            }
            
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "数字格式错误");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "库存调整失败", e);
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "库存调整失败");
        }
    }
    
    /**
     * 处理状态切换
     */
    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        String itemIdStr = request.getParameter("itemId");
        
        if (itemIdStr == null || itemIdStr.trim().isEmpty()) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "商品ID不能为空");
            return;
        }
        
        try {
            int itemId = Integer.parseInt(itemIdStr);
            
            // 检查商品是否存在
            RedemptionItem item = redemptionItemDAO.findById(itemId);
            if (item == null) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "商品不存在");
                return;
            }
            
            // 切换状态
            boolean success = redemptionItemDAO.toggleStatus(itemId);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "状态切换成功");
                APIOptimizer.sendSuccessResponse(response, result);
            } else {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "状态切换失败");
            }
            
        } catch (NumberFormatException e) {
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "商品ID格式错误");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "状态切换失败", e);
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "状态切换失败");
        }
    }
    
    /**
     * 转义JSON字符串
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
}
