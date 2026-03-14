package com.supermarket.servlet.cashier;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.supermarket.util.DatabaseUtil;
import com.supermarket.service.PromotionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 收银员交易处理功能Servlet
 * 处理交易处理和积分累计相关的请求
 */
@WebServlet("/cashier/transaction/*")
public class CashierTransactionServlet extends HttpServlet {
    
    private Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/process".equals(pathInfo)) {
                handleTransactionProcess(request, response);
            } else {
                sendErrorResponse(response, "不支持的操作");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, "系统错误：" + e.getMessage());
        }
    }
    
    /**
     * 处理交易处理请求
     */
    private void handleTransactionProcess(HttpServletRequest request, HttpServletResponse response) 
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
            Map<String, Object> member = (Map<String, Object>) requestData.get("member");
            String memberPhone = (String) requestData.get("memberPhone");
            List<Map<String, Object>> items = (List<Map<String, Object>>) requestData.get("items");
            Double totalAmount = (Double) requestData.get("totalAmount");
            Double earnedPoints = (Double) requestData.get("earnedPoints");
            
            // 如果没有直接提供member对象，但提供了memberPhone，则查找会员
            if (member == null && memberPhone != null && !memberPhone.trim().isEmpty()) {
                member = getMemberByPhone(memberPhone.trim());
            }
            
            // 计算积分（如果是会员且没有提供积分）
            if (member != null && earnedPoints == null) {
                // 获取会员信息
                int memberId = getMemberIdFromMap(member);
                String memberLevel = getMemberLevelFromMap(member);
                
                // 使用促销服务计算积分（包含双倍积分日逻辑）
                earnedPoints = (double) PromotionService.calculateMemberPoints(
                    memberId, totalAmount, "通用", memberLevel);
            }
            
            if (items == null || items.isEmpty()) {
                sendErrorResponse(response, "商品列表不能为空");
                return;
            }
            
            if (totalAmount == null || totalAmount <= 0) {
                sendErrorResponse(response, "交易金额无效");
                return;
            }
            
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务
            
            // 生成交易ID
            String transactionId = "T" + System.currentTimeMillis();
            
            // 1. 创建交易记录
            int transactionRecordId = createTransactionRecord(conn, transactionId, member, 
                    BigDecimal.valueOf(totalAmount), earnedPoints != null ? earnedPoints.intValue() : 0);
            
            // 2. 创建交易明细
            createTransactionItems(conn, transactionId, items);
            
            // 3. 更新库存
            updateProductStock(conn, items);
            
            // 4. 如果是会员，更新积分
            Integer updatedPoints = null;
            if (member != null && earnedPoints != null && earnedPoints > 0) {
                updatedPoints = updateMemberPoints(conn, member, earnedPoints.intValue());
            }
            
            conn.commit(); // 提交事务
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "交易处理成功");
            responseData.put("transactionId", transactionId);
            if (updatedPoints != null) {
                responseData.put("updatedPoints", updatedPoints);
            }
            
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
            sendErrorResponse(response, "交易处理失败：" + e.getMessage());
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
     * 创建交易记录
     */
    private int createTransactionRecord(Connection conn, String transactionId, 
            Map<String, Object> member, BigDecimal totalAmount, int earnedPoints) throws SQLException {
        
        String sql = "INSERT INTO transactions (transaction_id, member_id, cashier_id, " +
                    "total_amount, payment_method, payment_amount, change_amount, points_earned, transaction_date, status) " +
                    "VALUES (?, ?, ?, ?, 'CASH', ?, 0, ?, ?, 'COMPLETED')";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, transactionId);
            
            if (member != null) {
                Object userIdObj = member.get("userId");
                if (userIdObj instanceof Integer) {
                    stmt.setInt(2, (Integer) userIdObj);
                } else if (userIdObj instanceof Double) {
                    stmt.setInt(2, ((Double) userIdObj).intValue());
                } else {
                    stmt.setNull(2, java.sql.Types.INTEGER);
                }
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(3, 1); // 假设收银员ID为1，实际应从session获取
            stmt.setBigDecimal(4, totalAmount);
            stmt.setBigDecimal(5, totalAmount); // payment_amount
            stmt.setInt(6, earnedPoints); // points_earned
            stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("创建交易记录失败");
        }
    }
    
    /**
     * 创建交易明细
     */
    private void createTransactionItems(Connection conn, String transactionId, 
            List<Map<String, Object>> items) throws SQLException {
        
        String sql = "INSERT INTO transaction_items (transaction_id, product_id, product_name, barcode, quantity, unit_price, subtotal) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Map<String, Object> item : items) {
                Double productIdDouble = (Double) item.get("productId");
                String productName = (String) item.get("name");
                String barcode = (String) item.get("barcode");
                Double quantityDouble = (Double) item.get("quantity");
                Double priceDouble = (Double) item.get("price");
                Double subtotalDouble = (Double) item.get("subtotal");
                
                // 如果barcode为null，从数据库获取
                if (barcode == null && productIdDouble != null) {
                    String getBarcodeSQL = "SELECT barcode FROM products WHERE product_id = ?";
                    try (PreparedStatement barcodeStmt = conn.prepareStatement(getBarcodeSQL)) {
                        barcodeStmt.setInt(1, productIdDouble.intValue());
                        ResultSet rs = barcodeStmt.executeQuery();
                        if (rs.next()) {
                            barcode = rs.getString("barcode");
                        }
                    }
                }
                
                // 如果仍然为null，使用默认值
                if (barcode == null) {
                    barcode = "UNKNOWN";
                }
                
                stmt.setString(1, transactionId);
                stmt.setInt(2, productIdDouble.intValue());
                stmt.setString(3, productName);
                stmt.setString(4, barcode);
                stmt.setInt(5, quantityDouble.intValue());
                stmt.setBigDecimal(6, BigDecimal.valueOf(priceDouble));
                stmt.setBigDecimal(7, BigDecimal.valueOf(subtotalDouble));
                
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    /**
     * 更新商品库存
     */
    private void updateProductStock(Connection conn, List<Map<String, Object>> items) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Map<String, Object> item : items) {
                Double productIdDouble = (Double) item.get("productId");
                Double quantityDouble = (Double) item.get("quantity");
                
                stmt.setInt(1, quantityDouble.intValue());
                stmt.setInt(2, productIdDouble.intValue());
                
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    /**
     * 更新会员积分
     */
    private Integer updateMemberPoints(Connection conn, Map<String, Object> member, int earnedPoints) throws SQLException {
        Object userIdObj = member.get("userId");
        int memberId;
        if (userIdObj instanceof Integer) {
            memberId = (Integer) userIdObj;
        } else if (userIdObj instanceof Double) {
            memberId = ((Double) userIdObj).intValue();
        } else {
            throw new SQLException("Invalid userId type: " + userIdObj.getClass());
        }
        
        // 1. 更新会员积分
        String updateSql = "UPDATE users SET total_points = total_points + ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setInt(1, earnedPoints);
            stmt.setInt(2, memberId);
            stmt.executeUpdate();
        }
        
        // 2. 记录积分变动历史
        String historySql = "INSERT INTO point_transactions (user_id, transaction_type, points, " +
                          "description, transaction_date, cashier_id) VALUES (?, 'EARN', ?, '购物获得积分', ?, 1)";
        try (PreparedStatement stmt = conn.prepareStatement(historySql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, earnedPoints);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
        }
        
        // 3. 查询更新后的积分
        String querySql = "SELECT total_points FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(querySql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_points");
            }
        }
        
        return null;
    }
    
    /**
     * 通过手机号查找会员
     */
    private Map<String, Object> getMemberByPhone(String phone) {
        String sql = "SELECT user_id as userId, username, email, phone, real_name, " +
                    "membership_level, membership_card_number, total_points " +
                    "FROM users WHERE phone = ? AND role = 'MEMBER'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Map<String, Object> member = new HashMap<>();
                member.put("userId", rs.getInt("userId"));
                member.put("username", rs.getString("username"));
                member.put("email", rs.getString("email"));
                member.put("phone", rs.getString("phone"));
                member.put("realName", rs.getString("real_name"));
                member.put("membershipLevel", rs.getString("membership_level"));
                member.put("membershipCardNumber", rs.getString("membership_card_number"));
                member.put("points", rs.getInt("total_points"));
                
                return member;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
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
    
    /**
     * 从会员Map中获取会员ID
     */
    private int getMemberIdFromMap(Map<String, Object> member) {
        Object userIdObj = member.get("userId");
        if (userIdObj instanceof Integer) {
            return (Integer) userIdObj;
        } else if (userIdObj instanceof Double) {
            return ((Double) userIdObj).intValue();
        } else {
            return Integer.parseInt(userIdObj.toString());
        }
    }
    
    /**
     * 从会员Map中获取会员等级
     */
    private String getMemberLevelFromMap(Map<String, Object> member) {
        Object levelObj = member.get("membershipLevel");
        return levelObj != null ? levelObj.toString() : "BRONZE";
    }
}