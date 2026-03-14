package com.supermarket.servlet;

import com.supermarket.dao.UserDAO;
import com.supermarket.dao.impl.UserDAOImpl;
import com.supermarket.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员优惠活动管理Servlet
 * 处理优惠活动查看、参与等功能
 */
@WebServlet("/member/promotions")
public class MemberPromotionsServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 检查用户是否登录且为会员
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String role = (String) session.getAttribute("role");
        if (!"MEMBER".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "只有会员可以访问此页面");
            return;
        }
        
        // 获取用户信息
        Integer userId = (Integer) session.getAttribute("userId");
        User user = userDAO.findById(userId);
        if (user != null) {
            request.setAttribute("user", user);
        }
        
        // 获取当前可用的优惠活动
        List<Map<String, Object>> promotions = getCurrentPromotions();
        request.setAttribute("promotions", promotions);
        
        // 转发到优惠活动页面
        request.getRequestDispatcher("/WEB-INF/views/member/promotions.jsp")
                .forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 检查用户是否登录且为会员
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String role = (String) session.getAttribute("role");
        if (!"MEMBER".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "只有会员可以访问此页面");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            if ("participate".equals(action)) {
                participateInPromotion(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的操作");
            }
        } catch (SQLException e) {
            throw new ServletException("数据库操作失败", e);
        }
    }
    
    /**
     * 获取当前可用的优惠活动
     */
    private List<Map<String, Object>> getCurrentPromotions() {
        List<Map<String, Object>> promotions = new ArrayList<>();
        
        // 双倍积分活动
        Map<String, Object> doublePoints = new HashMap<>();
        doublePoints.put("id", 1);
        doublePoints.put("title", "双倍积分活动");
        doublePoints.put("description", "购物满100元即可获得双倍积分奖励！");
        doublePoints.put("type", "DOUBLE_POINTS");
        doublePoints.put("status", "active");
        doublePoints.put("requirement", "单次消费满100元");
        doublePoints.put("reward", "获得双倍积分");
        promotions.add(doublePoints);
        
        // 特价商品活动
        Map<String, Object> specialOffer = new HashMap<>();
        specialOffer.put("id", 2);
        specialOffer.put("title", "特价商品专区");
        specialOffer.put("description", "精选商品特价销售，最低5折起！");
        specialOffer.put("type", "SPECIAL_OFFER");
        specialOffer.put("status", "active");
        specialOffer.put("requirement", "购买指定特价商品");
        specialOffer.put("reward", "享受特价优惠");
        promotions.add(specialOffer);
        
        // 积分奖励活动
        Map<String, Object> pointsBonus = new HashMap<>();
        pointsBonus.put("id", 3);
        pointsBonus.put("title", "新会员积分奖励");
        pointsBonus.put("description", "新注册会员即可获得100积分奖励！");
        pointsBonus.put("type", "POINTS_BONUS");
        pointsBonus.put("status", "active");
        pointsBonus.put("requirement", "新注册会员");
        pointsBonus.put("reward", "获得100积分");
        promotions.add(pointsBonus);
        
        return promotions;
    }
    
    /**
     * 参与优惠活动
     */
    private void participateInPromotion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String promotionIdStr = request.getParameter("promotionId");
        
        if (promotionIdStr == null) {
            request.setAttribute("error", "活动ID不能为空");
            doGet(request, response);
            return;
        }
        
        try {
            int promotionId = Integer.parseInt(promotionIdStr);
            
            // 这里可以添加参与活动的业务逻辑
            // 例如：检查用户是否已参与、更新参与状态等
            
            switch (promotionId) {
                case 1: // 双倍积分活动
                    request.setAttribute("success", "您已成功参与双倍积分活动！下次购物满100元即可享受双倍积分。");
                    break;
                case 2: // 特价商品活动
                    request.setAttribute("success", "您已关注特价商品专区！可以随时查看最新特价商品。");
                    break;
                case 3: // 积分奖励活动
                    // 为新会员添加100积分
                    User user = userDAO.findById(userId);
                    if (user != null) {
                        int newPoints = user.getTotalPoints() + 100;
                        boolean success = userDAO.updateUserPoints(userId, newPoints);
                        if (success) {
                            session.setAttribute("totalPoints", newPoints);
                            request.setAttribute("success", "恭喜您获得100积分奖励！");
                        } else {
                            request.setAttribute("error", "积分发放失败，请稍后重试。");
                        }
                    }
                    break;
                default:
                    request.setAttribute("error", "无效的活动ID");
                    break;
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "活动ID格式错误");
        }
        
        doGet(request, response);
    }
}