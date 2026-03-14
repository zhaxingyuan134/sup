<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>超市积分管理系统 - 注册</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px 0;
        }
        .register-container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(10px);
        }
        .register-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 20px 20px 0 0;
        }
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }
        .btn-register {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 10px;
            padding: 12px;
            font-weight: 600;
        }
        .btn-register:hover {
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
            <div class="col-md-8 col-lg-6">
                <div class="register-container">
                    <!-- 注册头部 -->
                    <div class="register-header text-center py-4">
                        <i class="fas fa-user-plus fa-3x mb-3"></i>
                        <h3 class="mb-0">用户注册</h3>
                        <p class="mb-0 mt-2">创建您的账户</p>
                    </div>
                    
                    <!-- 注册表单 -->
                    <div class="p-4">
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        
                        <form id="registerForm" method="post" action="${pageContext.request.contextPath}/register">
                            <!-- 角色选择 -->
                            <div class="mb-4">
                                <label class="form-label fw-bold">选择角色 <span class="text-danger">*</span></label>
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
                            
                            <div class="row">
                                <!-- 用户名 -->
                                <div class="col-md-6 mb-3">
                                    <label for="username" class="form-label fw-bold">
                                        <i class="fas fa-user me-2"></i>用户名 <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="username" name="username" 
                                           placeholder="请输入用户名" required>
                                </div>
                                
                                <!-- 真实姓名 -->
                                <div class="col-md-6 mb-3">
                                    <label for="realName" class="form-label fw-bold">
                                        <i class="fas fa-id-card me-2"></i>真实姓名 <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="realName" name="realName" 
                                           placeholder="请输入真实姓名" required>
                                </div>
                            </div>
                            
                            <div class="row">
                                <!-- 密码 -->
                                <div class="col-md-6 mb-3">
                                    <label for="password" class="form-label fw-bold">
                                        <i class="fas fa-lock me-2"></i>密码 <span class="text-danger">*</span>
                                    </label>
                                    <input type="password" class="form-control" id="password" name="password" 
                                           placeholder="至少6位密码" required minlength="6">
                                </div>
                                
                                <!-- 确认密码 -->
                                <div class="col-md-6 mb-3">
                                    <label for="confirmPassword" class="form-label fw-bold">
                                        <i class="fas fa-lock me-2"></i>确认密码 <span class="text-danger">*</span>
                                    </label>
                                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" 
                                           placeholder="再次输入密码" required>
                                </div>
                            </div>
                            
                            <div class="row">
                                <!-- 手机号 -->
                                <div class="col-md-6 mb-3">
                                    <label for="phone" class="form-label fw-bold">
                                        <i class="fas fa-phone me-2"></i>手机号
                                    </label>
                                    <input type="tel" class="form-control" id="phone" name="phone" 
                                           placeholder="请输入手机号">
                                </div>
                                
                                <!-- 邮箱 -->
                                <div class="col-md-6 mb-4">
                                    <label for="email" class="form-label fw-bold">
                                        <i class="fas fa-envelope me-2"></i>邮箱
                                    </label>
                                    <input type="email" class="form-control" id="email" name="email" 
                                           placeholder="请输入邮箱">
                                </div>
                            </div>
                            
                            <!-- 注册按钮 -->
                            <button type="submit" class="btn btn-register btn-primary w-100 mb-3">
                                <i class="fas fa-user-plus me-2"></i>注册
                            </button>
                            
                            <!-- 登录链接 -->
                            <div class="text-center">
                                <span class="text-muted">已有账号？</span>
                                <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">
                                    立即登录
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
            
            // 密码确认验证
            $('#confirmPassword').on('input', function() {
                var password = $('#password').val();
                var confirmPassword = $(this).val();
                
                if (password !== confirmPassword) {
                    $(this)[0].setCustomValidity('密码不匹配');
                } else {
                    $(this)[0].setCustomValidity('');
                }
            });
            
            // 表单验证
            $('#registerForm').submit(function(e) {
                if (!$('#role').val()) {
                    e.preventDefault();
                    alert('请选择注册角色');
                    return false;
                }
                
                var password = $('#password').val();
                var confirmPassword = $('#confirmPassword').val();
                
                if (password !== confirmPassword) {
                    e.preventDefault();
                    alert('两次输入的密码不一致');
                    return false;
                }
                
                if (password.length < 6) {
                    e.preventDefault();
                    alert('密码长度不能少于6位');
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
