package com.supermarket.servlet;

import com.supermarket.dao.UserDAO;
import com.supermarket.dao.RedemptionItemDAO;
import com.supermarket.dao.RedemptionRecordDAO;
import com.supermarket.dao.PointTransactionDAO;
import com.supermarket.dao.impl.UserDAOImpl;
import com.supermarket.dao.impl.RedemptionItemDAOImpl;
import com.supermarket.dao.impl.RedemptionRecordDAOImpl;
import com.supermarket.dao.impl.PointTransactionDAOImpl;
import com.supermarket.model.User;
import com.supermarket.model.RedemptionItem;
import com.supermarket.model.RedemptionRecord;
import com.supermarket.model.PointTransaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 会员积分管理Servlet
 * 处理积分查询、兑换等功能
 */
@WebServlet("/member/points/*")
public class MemberPointsServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private RedemptionItemDAO redemptionItemDAO;
    private RedemptionRecordDAO redemptionRecordDAO;
    private PointTransactionDAO pointTransactionDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
        redemptionItemDAO = new RedemptionItemDAOImpl();
        redemptionRecordDAO = new RedemptionRecordDAOImpl();
        pointTransactionDAO = new PointTransactionDAOImpl();
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
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }
        
        try {
            switch (pathInfo) {
                case "/":
                case "/history":
                    showPointsHistory(request, response);
                    break;
                case "/redeem":
                    showRedeemPage(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("数据库操作失败", e);
        }
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
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }
        
        try {
            switch (pathInfo) {
                case "/redeem":
                    processRedemption(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("数据库操作失败", e);
        }
    }
    
    /**
     * 显示积分历史记录页面
     */
    private void showPointsHistory(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        
        // 获取最新的用户信息
        User user = userDAO.findById(userId);
        if (user != null) {
            request.setAttribute("user", user);
            // 更新session中的积分信息
            session.setAttribute("totalPoints", user.getTotalPoints());
        }
        
        // 获取用户的积分交易历史记录
        List<PointTransaction> historyList = pointTransactionDAO.findByUserId(userId);
        request.setAttribute("historyList", historyList);
        
        request.getRequestDispatcher("/WEB-INF/views/member/points-history.jsp")
                .forward(request, response);
    }
    
    /**
     * 显示积分兑换页面
     */
    private void showRedeemPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        
        // 获取最新的用户信息
        User user = userDAO.findById(userId);
        if (user != null) {
            request.setAttribute("user", user);
            // 更新session中的积分信息
            session.setAttribute("totalPoints", user.getTotalPoints());
        }
        
        // 获取可用的兑换商品
        List<RedemptionItem> availableItems = redemptionItemDAO.findAvailableItems();
        request.setAttribute("redeemItems", availableItems);
        
        // 按分类分组商品
        request.setAttribute("couponItems", 
            availableItems.stream()
                .filter(item -> "COUPON".equals(item.getCategory()))
                .collect(Collectors.toList()));
        request.setAttribute("giftItems", 
            availableItems.stream()
                .filter(item -> "GIFT".equals(item.getCategory()))
                .collect(Collectors.toList()));
        request.setAttribute("serviceItems", 
            availableItems.stream()
                .filter(item -> "SERVICE".equals(item.getCategory()))
                .collect(Collectors.toList()));
        
        request.getRequestDispatcher("/WEB-INF/views/member/points-redeem.jsp")
                .forward(request, response);
    }
    
    /**
     * 处理积分兑换请求
     */
    private void processRedemption(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        
        // 获取兑换参数
        String itemIdStr = request.getParameter("itemId");
        String pointsStr = request.getParameter("points");
        
        if (itemIdStr == null || pointsStr == null) {
            request.setAttribute("error", "参数不完整");
            showRedeemPage(request, response);
            return;
        }
        
        try {
            int itemId = Integer.parseInt(itemIdStr);
            int points = Integer.parseInt(pointsStr);
            
            // 验证兑换商品是否存在且可用
            RedemptionItem item = redemptionItemDAO.findById(itemId);
            if (item == null || !item.isAvailable()) {
                request.setAttribute("error", "兑换商品不存在或已下架");
                showRedeemPage(request, response);
                return;
            }
            
            // 验证积分是否匹配
            if (item.getPointsRequired() != points) {
                request.setAttribute("error", "积分数量不匹配");
                showRedeemPage(request, response);
                return;
            }
            
            // 检查库存
            if (item.getStockQuantity() <= 0) {
                request.setAttribute("error", "商品库存不足");
                showRedeemPage(request, response);
                return;
            }
            
            // 获取用户当前积分
            User user = userDAO.findById(userId);
            if (user == null) {
                request.setAttribute("error", "用户不存在");
                showRedeemPage(request, response);
                return;
            }
            
            // 检查积分是否足够
            if (user.getTotalPoints() < points) {
                request.setAttribute("error", "积分不足，无法兑换");
                showRedeemPage(request, response);
                return;
            }
            
            // 扣除积分
            int newPoints = user.getTotalPoints() - points;
            boolean pointsUpdated = userDAO.updateUserPoints(userId, newPoints);
            
            if (pointsUpdated) {
                // 减少商品库存
                boolean stockUpdated = redemptionItemDAO.decreaseStock(itemId, 1);
                
                if (stockUpdated) {
                    // 创建兑换记录
                    RedemptionRecord record = new RedemptionRecord();
                    record.setUserId(userId);
                    record.setItemId(itemId);
                    record.setPointsUsed(points);
                    record.setQuantity(1);
                    record.setRedemptionDate(LocalDateTime.now());
                    record.setStatus(RedemptionRecord.Status.COMPLETED.getCode());
                    
                    boolean recordCreated = redemptionRecordDAO.createRecord(record);
                    
                    if (recordCreated) {
                        try {
                            // 创建积分交易记录
                            PointTransaction pointTransaction = new PointTransaction(
                                userId, 
                                PointTransaction.TransactionType.REDEEM, 
                                -points, // 负数表示积分消费
                                "兑换商品：" + item.getItemName()
                            );
                            pointTransactionDAO.createTransaction(pointTransaction);
                        } catch (SQLException e) {
                            System.err.println("创建积分交易记录失败: " + e.getMessage());
                            // 积分交易记录创建失败不影响兑换流程，只记录日志
                        }
                        
                        // 更新session中的积分信息
                        session.setAttribute("totalPoints", newPoints);
                        user.setTotalPoints(newPoints);
                        session.setAttribute("user", user);
                        
                        request.setAttribute("success", "兑换成功！已扣除 " + points + " 积分，获得 " + item.getItemName());
                        // 兑换成功后跳转到积分历史页面
                        showPointsHistory(request, response);
                        return;
                    } else {
                        // 如果记录创建失败，需要回滚积分和库存
                        userDAO.updateUserPoints(userId, user.getTotalPoints());
                        redemptionItemDAO.updateStock(itemId, item.getStockQuantity());
                        request.setAttribute("error", "兑换记录创建失败，请稍后重试");
                    }
                } else {
                    // 如果库存更新失败，需要回滚积分
                    userDAO.updateUserPoints(userId, user.getTotalPoints());
                    request.setAttribute("error", "库存更新失败，请稍后重试");
                }
            } else {
                request.setAttribute("error", "积分扣除失败，请稍后重试");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "参数格式错误");
        }
        
        showRedeemPage(request, response);
    }
}