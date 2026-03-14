package com.supermarket.servlet.cashier;

import com.google.gson.Gson;
import com.supermarket.dao.impl.UserDAOImpl;
import com.supermarket.model.User;
import com.supermarket.util.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 收银员会员识别功能Servlet
 * 处理会员识别相关的请求
 */
@WebServlet("/cashier/member/*")
public class CashierMemberServlet extends HttpServlet {
    
    private UserDAOImpl userDAO = new UserDAOImpl();
    private Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        
        try {
            if ("/identify".equals(pathInfo)) {
                handleMemberIdentify(request, response);
            } else {
                sendErrorResponse(response, "不支持的操作");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, "系统错误：" + e.getMessage());
        }
    }
    
    /**
     * 处理会员识别请求
     */
    private void handleMemberIdentify(HttpServletRequest request, HttpServletResponse response) 
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
        
        try {
            // 解析JSON数据
            Map<String, String> requestData = gson.fromJson(jsonString, Map.class);
            String phone = requestData.get("phone");
            String cardNumber = requestData.get("cardNumber");
            
            if ((phone == null || phone.trim().isEmpty()) && 
                (cardNumber == null || cardNumber.trim().isEmpty())) {
                sendErrorResponse(response, "请提供手机号或会员卡号");
                return;
            }
            
            User member = null;
            
            // 优先通过手机号查找
            if (phone != null && !phone.trim().isEmpty()) {
                member = findMemberByPhone(phone.trim());
            }
            
            // 如果通过手机号未找到，尝试通过会员卡号查找
            if (member == null && cardNumber != null && !cardNumber.trim().isEmpty()) {
                member = findMemberByCardNumber(cardNumber.trim());
            }
            
            Map<String, Object> responseData = new HashMap<>();
            if (member != null) {
                responseData.put("success", true);
                responseData.put("message", "会员识别成功");
                responseData.put("member", member);
            } else {
                responseData.put("success", false);
                responseData.put("message", "未找到对应的会员信息");
            }
            
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, "数据解析错误：" + e.getMessage());
        }
    }
    
    /**
     * 通过手机号查找会员
     */
    private User findMemberByPhone(String phone) {
        String sql = "SELECT * FROM users " +
                    "WHERE phone = ? AND role = 'MEMBER'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setRealName(rs.getString("real_name"));
                user.setRole(User.UserRole.valueOf(rs.getString("role")));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                
                // 设置会员信息
                String memberLevel = rs.getString("membership_level");
                if (memberLevel != null) {
                    user.setMembershipLevel(memberLevel);
                }
                user.setTotalPoints(rs.getInt("total_points"));
                
                // 设置会员卡号
                String membershipCardNumber = rs.getString("membership_card_number");
                if (membershipCardNumber != null) {
                    user.setMembershipCardNumber(membershipCardNumber);
                }
                
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 通过会员卡号查找会员
     */
    private User findMemberByCardNumber(String cardNumber) {
        String sql = "SELECT * FROM users " +
                    "WHERE membership_card_number = ? AND role = 'MEMBER'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cardNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setRealName(rs.getString("real_name"));
                user.setRole(User.UserRole.valueOf(rs.getString("role")));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                
                // 设置会员信息
                String memberLevel = rs.getString("membership_level");
                if (memberLevel != null) {
                    user.setMembershipLevel(memberLevel);
                }
                user.setTotalPoints(rs.getInt("total_points"));
                
                // 设置会员卡号
                String membershipCardNumber = rs.getString("membership_card_number");
                if (membershipCardNumber != null) {
                    user.setMembershipCardNumber(membershipCardNumber);
                }
                
                return user;
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
}