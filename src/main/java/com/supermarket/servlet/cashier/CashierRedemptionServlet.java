package com.supermarket.servlet.cashier;

import com.google.gson.Gson;
import com.supermarket.util.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收银员积分兑换功能Servlet
 * 处理积分兑换相关的请求
 */
@WebServlet("/cashier/redemption/*")
public class CashierRedemptionServlet extends HttpServlet {
    
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/products".equals(pathInfo)) {
                handleGetRedemptionProducts(request, response);
            } else {
                sendErrorResponse(response, "不支持的操作");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, "系统错误：" + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/redeem".equals(pathInfo)) {
                handleRedemption(request, response);
            } else {
                sendErrorResponse(response, "不支持的操作");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, "系统错误：" + e.getMessage());
        }
    }
    
    /**
     * 获取可兑换商品列表
     */
    private void handleGetRedemptionProducts(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        List<Map<String, Object>> products = getRedemptionProducts();
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("products", products);
        
        response.getWriter().write(gson.toJson(responseData));
    }
    
    /**
     * 处理积分兑换请求
     */
    private void handleRedemption(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 读取JSON请求体
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            jsonBuilder.append(line);
        }
        
        String jsonString = jsonBuilder.toString();
        if (jsonString.isEmpty()) {
            sendErrorResponse(response, "请求数据为空");
            return;
        }
        
        Connection conn = null;
        try {
            // 解析JSON数据
            Map<String, Object> requestData = gson.fromJson(jsonString, Map.class);
            Double memberIdDouble = (Double) requestData.get("memberId");
            Double productIdDouble = (Double) requestData.get("productId");
            Double pointsRequiredDouble = (Double) requestData.get("pointsRequired");
            
            if (memberIdDouble == null || productIdDouble == null || pointsRequiredDouble == null) {
                sendErrorResponse(response, "请求参数不完整");
                return;
            }
            
            int memberId = memberIdDouble.intValue();
            int productId = productIdDouble.intValue();
            int pointsRequired = pointsRequiredDouble.intValue();
            
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务
            
            // 1. 检查会员积分是否足够
            int currentPoints = getMemberPoints(conn, memberId);
            if (currentPoints < pointsRequired) {
                sendErrorResponse(response, "积分不足，无法兑换");
                return;
            }
            
            // 2. 检查商品是否可兑换
            Map<String, Object> product = getRedemptionProduct(conn, productId);
            if (product == null) {
                sendErrorResponse(response, "商品不存在或不可兑换");
                return;
            }
            
            // 3. 扣除积分
            deductMemberPoints(conn, memberId, pointsRequired);
            
            // 4. 记录兑换历史
            recordRedemption(conn, memberId, productId, pointsRequired);
            
            // 5. 记录积分变动历史
            recordPointHistory(conn, memberId, -pointsRequired, "REDEEM", "积分兑换商品");
            
            conn.commit(); // 提交事务
            
            // 6. 获取剩余积分
            int remainingPoints = currentPoints - pointsRequired;
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "兑换成功");
            responseData.put("remainingPoints", remainingPoints);
            
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // 回滚事务
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            sendErrorResponse(response, "兑换失败：" + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 获取可兑换商品列表
     */
    private List<Map<String, Object>> getRedemptionProducts() {
        List<Map<String, Object>> products = new ArrayList<>();
        
        String sql = "SELECT ri.item_id as product_id, ri.points_required, ri.item_name as name, ri.description " +
                    "FROM redemption_items ri " +
                    "WHERE ri.is_active = 1 " +
                    "ORDER BY ri.points_required";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("productId", rs.getInt("product_id"));
                product.put("pointsRequired", rs.getInt("points_required"));
                product.put("name", rs.getString("name"));
                product.put("description", rs.getString("description"));
                
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return products;
    }
    
    /**
     * 获取指定商品的兑换信息
     */
    private Map<String, Object> getRedemptionProduct(Connection conn, int productId) throws SQLException {
        String sql = "SELECT ri.item_id as product_id, ri.points_required, ri.item_name as name, ri.description " +
                    "FROM redemption_items ri " +
                    "WHERE ri.item_id = ? AND ri.is_active = 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("productId", rs.getInt("product_id"));
                product.put("pointsRequired", rs.getInt("points_required"));
                product.put("name", rs.getString("name"));
                product.put("description", rs.getString("description"));
                
                return product;
            }
        }
        
        return null;
    }
    
    /**
     * 获取会员当前积分
     */
    private int getMemberPoints(Connection conn, int memberId) throws SQLException {
        String sql = "SELECT total_points FROM users WHERE user_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total_points");
            }
        }
        
        return 0;
    }
    
    /**
     * 扣除会员积分
     */
    private void deductMemberPoints(Connection conn, int memberId, int points) throws SQLException {
        String sql = "UPDATE users SET total_points = total_points - ? WHERE user_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, points);
            stmt.setInt(2, memberId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * 记录兑换历史
     */
    private void recordRedemption(Connection conn, int memberId, int productId, int pointsUsed) throws SQLException {
        String sql = "INSERT INTO redemption_records (user_id, item_id, points_used, redemption_date) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, productId);
            stmt.setInt(3, pointsUsed);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
        }
    }
    
    /**
     * 记录积分变动历史
     */
    private void recordPointHistory(Connection conn, int memberId, int pointsChange, 
            String changeType, String description) throws SQLException {
        
        // 暂时注释掉积分历史记录功能，因为point_history表不存在
        // String sql = "INSERT INTO point_history (member_id, points_change, change_type, " +
        //             "description, created_at) VALUES (?, ?, ?, ?, ?)";
        
        // try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        //     stmt.setInt(1, memberId);
        //     stmt.setInt(2, pointsChange);
        //     stmt.setString(3, changeType);
        //     stmt.setString(4, description);
        //     stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
        //     stmt.executeUpdate();
        // }
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        response.getWriter().write(gson.toJson(errorResponse));
    }
}