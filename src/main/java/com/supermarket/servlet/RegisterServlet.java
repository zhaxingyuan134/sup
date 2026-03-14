package com.supermarket.servlet;

import com.supermarket.dao.UserDAO;
import com.supermarket.dao.impl.UserDAOImpl;
import com.supermarket.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 注册Servlet控制器
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAOImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 转发到注册页面
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String realName = request.getParameter("realName");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String role = request.getParameter("role");
        
        // 验证输入参数
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty() ||
            realName == null || realName.trim().isEmpty() ||
            role == null || role.trim().isEmpty()) {
            
            request.setAttribute("error", "请填写完整的注册信息");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        // 验证密码确认
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "两次输入的密码不一致");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        // 验证密码长度
        if (password.length() < 6) {
            request.setAttribute("error", "密码长度不能少于6位");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        if ("ADMIN".equalsIgnoreCase(role) || "管理员".equals(role)) {
            request.setAttribute("error", "管理员模块已下线，暂不支持注册管理员账号");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        try {
            // 检查用户名是否已存在
            if (userDAO.isUsernameExists(username.trim())) {
                request.setAttribute("error", "用户名已存在，请选择其他用户名");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                return;
            }
            
            // 创建用户对象
            User user = new User();
            user.setUsername(username.trim());
            user.setPassword(password); // 密码会在DAO层进行加密
            user.setRealName(realName.trim());
            user.setPhone(phone != null ? phone.trim() : null);
            user.setEmail(email != null ? email.trim() : null);
            user.setRole(User.UserRole.fromString(role));
            user.setMembershipLevel("普通会员");
            user.setTotalPoints(0);
            user.setActive(true);
            
            // 创建用户
            if (userDAO.createUser(user)) {
                // 注册成功
                request.setAttribute("success", "注册成功！请登录");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            } else {
                // 注册失败
                request.setAttribute("error", "注册失败，请稍后重试");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "系统错误，请稍后重试");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
}
