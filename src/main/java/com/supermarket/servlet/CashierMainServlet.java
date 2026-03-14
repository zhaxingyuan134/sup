package com.supermarket.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
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

/**
 * 收银员主界面控制器
 * 处理收银员登录后的主界面显示和功能路由
 * 
 * 功能包括：
 * - 会员识别
 * - 视频扫描
 * - 积分累计
 * - 商品兑换
 * - 小票打印
 * 
 * @author SupermarketSystem
 * @version 3.0
 */
@WebServlet("/cashier/main")
public class CashierMainServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private UserDAOImpl userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.userDAO = new UserDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 验证收银员身份
        User cashier = validateCashier(request, response);
        if (cashier == null) return;
        
        // 设置收银员信息到request
        request.setAttribute("cashier", cashier);
        
        // 跳转到收银员主界面
        request.getRequestDispatcher("/WEB-INF/views/cashier/main.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    /**
     * 验证收银员身份
     */
    private User validateCashier(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.CASHIER) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        
        return user;
    }
}