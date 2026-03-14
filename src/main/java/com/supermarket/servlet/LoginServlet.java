package com.supermarket.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.dao.UserDAO;
import com.supermarket.dao.impl.UserDAOImpl;
import com.supermarket.model.User;
import com.supermarket.util.APIOptimizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录Servlet控制器
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAOImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 转发到登录页面
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 检查Content-Type，支持JSON和表单提交
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            handleJsonLogin(request, response);
        } else {
            handleFormLogin(request, response);
        }
    }
    
    /**
     * 处理JSON格式的登录请求
     */
    private void handleJsonLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            // 读取JSON请求体
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                jsonBuilder.append(line);
            }
            
            @SuppressWarnings("unchecked")
            Map<String, String> loginData = objectMapper.readValue(jsonBuilder.toString(), Map.class);
            
            String username = loginData.get("username");
            String password = loginData.get("password");
            String role = loginData.get("role");
            
            // 详细日志记录
            System.out.println("=== JSON登录请求开始 ===");
            System.out.println("用户名: " + username);
            System.out.println("密码长度: " + (password != null ? password.length() : "null"));
            System.out.println("角色: " + role);
            
            // 参数验证
            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                role == null || role.trim().isEmpty()) {
                
                System.out.println("参数验证失败 - 缺少必要参数");
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "请填写完整的登录信息");
                return;
            }

            if ("ADMIN".equalsIgnoreCase(role) || "管理员".equals(role)) {
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "管理员模块已下线，请选择其他角色登录");
                return;
            }
            
            System.out.println("参数验证通过，开始验证登录...");
            
            // 验证登录
            User user = userDAO.validateLogin(username.trim(), password);
            
            System.out.println("validateLogin结果: " + (user != null ? "找到用户" : "用户不存在或密码错误"));
            
            if (user != null) {
                System.out.println("用户信息:");
                System.out.println("  用户ID: " + user.getUserId());
                System.out.println("  用户名: " + user.getUsername());
                System.out.println("  角色枚举: " + user.getRole());
                System.out.println("  角色显示名: " + user.getRole().getDisplayName());
                System.out.println("  请求角色: " + role);
                
                // 支持英文枚举名和中文显示名的匹配
                boolean roleMatches = user.getRole().name().equals(role) || user.getRole().getDisplayName().equals(role);
                System.out.println("  角色匹配: " + roleMatches);
                
                if (roleMatches) {
                    System.out.println("登录成功，设置session...");
                    
                    // 登录成功，设置session
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    session.setAttribute("userId", user.getUserId());
                    session.setAttribute("username", user.getUsername());
                    session.setAttribute("role", user.getRole().name());
                    
                    System.out.println("Session设置完成，Session ID: " + session.getId());
                    
                    // 返回成功响应
                    Map<String, Object> responseData = new HashMap<>();
                    responseData.put("success", true);
                    responseData.put("message", "登录成功");
                    responseData.put("user", createUserResponse(user));
                    
                    response.getWriter().write(objectMapper.writeValueAsString(responseData));
                } else {
                    System.out.println("角色不匹配，登录失败");
                    APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "用户名、密码或角色不正确");
                }
            } else {
                System.out.println("用户验证失败");
                APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "用户名、密码或角色不正确");
            }
            
            System.out.println("=== JSON登录请求结束 ===");
            
        } catch (Exception e) {
            System.err.println("=== JSON登录异常 ===");
            System.err.println("异常类型: " + e.getClass().getSimpleName());
            System.err.println("异常消息: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== 异常结束 ===");
            
            APIOptimizer.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "系统错误: " + e.getMessage());
        }
    }
    
    /**
     * 处理表单格式的登录请求
     */
    private void handleFormLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 设置请求编码
            request.setCharacterEncoding("UTF-8");
            
            // 获取登录参数
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String role = request.getParameter("role");
            
            // 详细日志记录
            System.out.println("=== 登录请求开始 ===");
            System.out.println("用户名: " + username);
            System.out.println("密码长度: " + (password != null ? password.length() : "null"));
            System.out.println("角色: " + role);
            
            // 参数验证
            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                role == null || role.trim().isEmpty()) {
                
                System.out.println("参数验证失败 - 缺少必要参数");
                request.setAttribute("error", "请填写完整的登录信息");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }
            
            System.out.println("参数验证通过，开始验证登录...");
            
            // 验证登录
            User user = userDAO.validateLogin(username.trim(), password);
            
            System.out.println("validateLogin结果: " + (user != null ? "找到用户" : "用户不存在或密码错误"));
            
            if (user != null) {
                System.out.println("用户信息:");
                System.out.println("  用户ID: " + user.getUserId());
                System.out.println("  用户名: " + user.getUsername());
                System.out.println("  角色枚举: " + user.getRole());
                System.out.println("  角色显示名: " + user.getRole().getDisplayName());
                System.out.println("  请求角色: " + role);
                // 支持英文枚举名和中文显示名的匹配
                boolean roleMatches = user.getRole().name().equals(role) || user.getRole().getDisplayName().equals(role);
                System.out.println("  角色匹配: " + roleMatches);
                
                if (roleMatches) {
                    System.out.println("登录成功，设置session并跳转...");
                    
                    // 登录成功，设置session
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    session.setAttribute("userId", user.getUserId());
                    session.setAttribute("username", user.getUsername());
                    session.setAttribute("role", user.getRole().name());
                    
                    // 根据角色跳转到相应页面
                    String redirectUrl = "";
                    switch (user.getRole()) {
                        case MEMBER:
                            redirectUrl = request.getContextPath() + "/member/dashboard";
                            break;
                        case CASHIER:
                            redirectUrl = request.getContextPath() + "/cashier/main";
                            break;
                        case MANAGER:
                            redirectUrl = request.getContextPath() + "/manager/dashboard";
                            break;
                        default:
                            redirectUrl = request.getContextPath() + "/";
                            break;
                    }
                    
                    System.out.println("跳转URL: " + redirectUrl);
                    response.sendRedirect(redirectUrl);
                } else {
                    System.out.println("角色不匹配，登录失败");
                    request.setAttribute("error", "用户名、密码或角色不正确");
                    request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                }
            } else {
                System.out.println("用户验证失败");
                request.setAttribute("error", "用户名、密码或角色不正确");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }
            
            System.out.println("=== 登录请求结束 ===");
            
        } catch (Exception e) {
            // 记录详细错误信息
            System.err.println("=== 登录异常 ===");
            System.err.println("异常类型: " + e.getClass().getSimpleName());
            System.err.println("异常消息: " + e.getMessage());
            System.err.println("用户名: " + request.getParameter("username"));
            System.err.println("角色: " + request.getParameter("role"));
            System.err.println("堆栈跟踪:");
            e.printStackTrace();
            System.err.println("=== 异常结束 ===");
            
            request.setAttribute("error", "系统错误: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
    
    /**
     * 创建用户响应对象
     */
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("userId", user.getUserId());
        userResponse.put("username", user.getUsername());
        userResponse.put("realName", user.getRealName());
        userResponse.put("role", user.getRole().name());
        userResponse.put("roleDisplayName", user.getRole().getDisplayName());
        return userResponse;
    }
}
