package com.supermarket.servlet;

import com.supermarket.dao.UserDAO;
import com.supermarket.dao.impl.UserDAOImpl;
import com.supermarket.dao.OrderDAO;
import com.supermarket.dao.impl.OrderDAOImpl;
import com.supermarket.dao.PromotionDAO;
import com.supermarket.dao.impl.PromotionDAOImpl;
import com.supermarket.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 超市经理主控制台Servlet
 * 处理经理模块的主要功能导航和权限验证
 */
@WebServlet({"/manager/dashboard", "/manager/main", "/manager/dashboard/*"})
public class ManagerDashboardServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAOImpl();
    private OrderDAO orderDAO = new OrderDAOImpl();
    private PromotionDAO promotionDAO = new PromotionDAOImpl();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 验证用户登录状态和权限
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        // 验证是否为经理角色
        if (user.getRole() != User.UserRole.MANAGER) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/stats")) {
            // 处理统计数据API请求
            getDashboardStats(request, response);
            return;
        }
        
        // 获取经理信息并设置到request中
        request.setAttribute("manager", user);
        request.setAttribute("managerName", user.getRealName());
        request.setAttribute("managerId", user.getUserId());
        
        // 转发到经理主控制台页面
        request.getRequestDispatcher("/WEB-INF/views/manager/dashboard.jsp").forward(request, response);
    }
    
    /**
     * 获取仪表盘统计数据
     */
    private void getDashboardStats(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            // 获取总会员数
            int totalMembers = userDAO.getTotalMemberCount();
            
            // 获取今日销售额
            BigDecimal todaySales = orderDAO.getTodaySales();
            
            // 获取总积分发放数
            long totalPoints = userDAO.getTotalPointsIssued();
            
            // 获取活跃促销活动数
            int activePromotions = promotionDAO.getActivePromotionCount();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalMembers", totalMembers);
            stats.put("todaySales", todaySales);
            stats.put("totalPoints", totalPoints);
            stats.put("activePromotions", activePromotions);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", stats);
            
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(result));
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取统计数据失败: " + e.getMessage());
            
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(result));
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}