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

/**
 * 会员仪表板Servlet控制器
 */
@WebServlet("/member/dashboard")
public class MemberDashboardServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAOImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // 检查用户是否已登录
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        // 检查用户角色是否为会员
        if (user.getRole() != User.UserRole.MEMBER) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // 获取最新的用户信息（包括积分）
            User latestUser = userDAO.findById(user.getUserId());
            if (latestUser != null) {
                session.setAttribute("user", latestUser);
                request.setAttribute("user", latestUser);
            }
            
            // 转发到会员仪表板页面
            request.getRequestDispatcher("/WEB-INF/views/member/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "系统错误，请稍后重试");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}