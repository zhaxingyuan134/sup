package com.example.supermarket.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.supermarket.dao.ProductDAO;
import com.supermarket.dao.impl.ProductDAOImpl;
import com.supermarket.model.Product;
import com.supermarket.util.APIOptimizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * 收银员商品扫描Servlet
 * 处理商品条码扫描和名称搜索功能
 */
@WebServlet("/cashier/scan")
public class CashierProductScanServlet extends HttpServlet {
    private ProductDAO productDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        productDAO = new ProductDAOImpl();
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();
        
        try {
            // 读取请求数据
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            JsonObject requestData = gson.fromJson(sb.toString(), JsonObject.class);
            String barcode = requestData.has("barcode") ? requestData.get("barcode").getAsString() : null;
            String name = requestData.has("name") ? requestData.get("name").getAsString() : null;
            
            Product product = null;
            
            // 优先通过条码查询
            if (barcode != null && !barcode.trim().isEmpty()) {
                product = productDAO.findByBarcode(barcode.trim());
            }
            
            // 如果条码查询失败，尝试名称搜索
            if (product == null && name != null && !name.trim().isEmpty()) {
                List<Product> products = productDAO.searchByName(name.trim());
                if (!products.isEmpty()) {
                    product = products.get(0); // 取第一个匹配的商品
                }
            }
            
            if (product != null) {
                result.addProperty("success", true);
                result.addProperty("message", "商品查询成功");
                
                // 构建商品信息
                JsonObject productInfo = new JsonObject();
                productInfo.addProperty("id", product.getProductId());
                productInfo.addProperty("name", product.getName());
                productInfo.addProperty("barcode", product.getBarcode());
                productInfo.addProperty("price", product.getPrice().doubleValue());
                productInfo.addProperty("unit", "个"); // 默认单位
                productInfo.addProperty("stockQuantity", product.getStockQuantity());
                productInfo.addProperty("isAvailable", product.isAvailable());
                productInfo.addProperty("pointsMultiplier", getPointsMultiplier(product));
                
                result.add("product", productInfo);
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "未找到匹配的商品");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            result.addProperty("success", false);
            result.addProperty("message", "数据库查询失败：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            result.addProperty("success", false);
            result.addProperty("message", "系统错误：" + e.getMessage());
        }
        
        out.print(result.toString());
        out.flush();
    }
    
    /**
     * 获取商品积分倍率
     * 根据商品类别或特殊标记计算积分倍率
     */
    private double getPointsMultiplier(Product product) {
        // 默认积分倍率为1.0
        double multiplier = 1.0;
        
        try {
            // 根据商品类别设置不同的积分倍率
            String category = product.getCategory();
            if (category != null) {
                switch (category.toLowerCase()) {
                    case "生鲜":
                    case "蔬菜":
                    case "水果":
                        multiplier = 1.5; // 生鲜类商品1.5倍积分
                        break;
                    case "进口":
                    case "有机":
                        multiplier = 2.0; // 进口/有机商品2倍积分
                        break;
                    case "促销":
                        multiplier = 0.5; // 促销商品0.5倍积分
                        break;
                    default:
                        multiplier = 1.0;
                }
            }
            
            // 检查是否有特殊积分活动
            if (isSpecialPointsPromotion(product)) {
                multiplier *= 1.2; // 特殊活动额外20%积分
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            multiplier = 1.0; // 出错时使用默认倍率
        }
        
        return multiplier;
    }
    
    /**
     * 检查是否参与特殊积分促销活动
     */
    private boolean isSpecialPointsPromotion(Product product) {
        // 这里可以实现复杂的促销规则判断
        // 例如：检查商品是否在促销列表中，是否在促销时间内等
        
        // 简单示例：价格超过50元的商品参与特殊积分活动
        return product.getPrice().doubleValue() > 50.0;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();
        result.addProperty("success", false);
        result.addProperty("message", "请使用POST方法进行商品扫描");
        
        out.print(result.toString());
        out.flush();
    }
}