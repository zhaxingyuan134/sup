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
import java.util.HashMap;
import java.util.Map;

/**
 * 收银员商品查询功能Servlet
 * 处理商品信息查询相关的请求
 */
@WebServlet("/cashier/product/*")
public class CashierProductServlet extends HttpServlet {
    
    private Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/info".equals(pathInfo)) {
                handleProductInfo(request, response);
            } else {
                sendErrorResponse(response, "不支持的操作");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, "系统错误：" + e.getMessage());
        }
    }
    
    /**
     * 处理商品信息查询请求
     */
    private void handleProductInfo(HttpServletRequest request, HttpServletResponse response) 
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
            String barcode = requestData.get("barcode");
            
            if (barcode == null || barcode.trim().isEmpty()) {
                sendErrorResponse(response, "请提供商品条码");
                return;
            }
            
            Map<String, Object> product = findProductByBarcode(barcode.trim());
            
            Map<String, Object> responseData = new HashMap<>();
            if (product != null) {
                responseData.put("success", true);
                responseData.put("message", "商品查询成功");
                responseData.put("product", product);
            } else {
                responseData.put("success", false);
                responseData.put("message", "未找到对应的商品信息");
            }
            
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, "数据解析错误：" + e.getMessage());
        }
    }
    
    /**
     * 通过条码查找商品信息
     */
    private Map<String, Object> findProductByBarcode(String barcode) {
        String sql = "SELECT product_id, name, price, stock_quantity, category " +
                    "FROM products WHERE barcode = ? AND is_available = 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, barcode);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("productId", rs.getInt("product_id"));
                product.put("name", rs.getString("name"));
                product.put("price", rs.getBigDecimal("price"));
                product.put("stockQuantity", rs.getInt("stock_quantity"));
                product.put("category", rs.getString("category"));
                product.put("barcode", barcode);
                
                return product;
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