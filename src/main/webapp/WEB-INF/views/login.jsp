<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>超市积分管理系统 - 登录</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <!-- 通用样式 -->
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
        }
        .login-container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(10px);
        }
        .login-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 20px 20px 0 0;
        }
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }
        .btn-login {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 10px;
            padding: 12px;
            font-weight: 600;
        }
        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        .role-card {
            border: 2px solid #e9ecef;
            border-radius: 10px;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .role-card:hover {
            border-color: #667eea;
            transform: translateY(-2px);
        }
        .role-card.selected {
            border-color: #667eea;
            background-color: rgba(102, 126, 234, 0.1);
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="login-container">
                    <!-- 登录头部 -->
                    <div class="login-header text-center py-4">
                        <i class="fas fa-store fa-3x mb-3"></i>
                        <h3 class="mb-0">超市积分管理系统</h3>
                        <p class="mb-0 mt-2">请选择角色并登录</p>
                    </div>
                    
                    <!-- 登录表单 -->
                    <div class="p-4">
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty success}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="fas fa-check-circle me-2"></i>
                                ${success}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        
                        <form id="loginForm" method="post" action="${pageContext.request.contextPath}/login">
                            <!-- 角色选择 -->
                            <div class="mb-4">
                                <label class="form-label fw-bold">选择角色</label>
                                <div class="row g-2">
                                    <div class="col-6">
                                        <div class="role-card p-3 text-center" data-role="MEMBER">
                                            <i class="fas fa-user fa-2x text-primary mb-2"></i>
                                            <div class="fw-bold">会员</div>
                                            <small class="text-muted">积分查询兑换</small>
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div class="role-card p-3 text-center" data-role="CASHIER">
                                            <i class="fas fa-cash-register fa-2x text-success mb-2"></i>
                                            <div class="fw-bold">收银员</div>
                                            <small class="text-muted">收银结算</small>
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div class="role-card p-3 text-center" data-role="MANAGER">
                                            <i class="fas fa-user-tie fa-2x text-warning mb-2"></i>
                                            <div class="fw-bold">超市经理</div>
                                            <small class="text-muted">业务管理</small>
                                        </div>
                                    </div>
                                </div>
                                <input type="hidden" id="role" name="role" required>
                            </div>
                            
                            <!-- 用户名 -->
                            <div class="mb-3">
                                <label for="username" class="form-label fw-bold">
                                    <i class="fas fa-user me-2"></i>用户名
                                </label>
                                <input type="text" class="form-control" id="username" name="username" 
                                       placeholder="请输入用户名" required>
                            </div>
                            
                            <!-- 密码 -->
                            <div class="mb-4">
                                <label for="password" class="form-label fw-bold">
                                    <i class="fas fa-lock me-2"></i>密码
                                </label>
                                <input type="password" class="form-control" id="password" name="password" 
                                       placeholder="请输入密码" required>
                            </div>
                            
                            <!-- 登录按钮 -->
                            <button type="submit" class="btn btn-login btn-primary w-100 mb-3">
                                <i class="fas fa-sign-in-alt me-2"></i>登录
                            </button>
                            
                            <!-- 注册链接 -->
                            <div class="text-center">
                                <span class="text-muted">还没有账号？</span>
                                <a href="${pageContext.request.contextPath}/register" class="text-decoration-none">
                                    立即注册
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    
    <script>
        $(document).ready(function() {
            // 角色选择
            $('.role-card').click(function() {
                $('.role-card').removeClass('selected');
                $(this).addClass('selected');
                $('#role').val($(this).data('role'));
            });
            
            // 表单验证
            $('#loginForm').submit(function(e) {
                if (!$('#role').val()) {
                    e.preventDefault();
                    alert('请选择登录角色');
                    return false;
                }
            });
            
            // 自动消失的提示
            setTimeout(function() {
                $('.alert').fadeOut();
            }, 5000);
        });
    </script>
</body>
</html>
