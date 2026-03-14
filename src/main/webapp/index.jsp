<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>超市积分管理系统</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
        }
        .welcome-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(10px);
        }
        .feature-icon {
            width: 80px;
            height: 80px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 2rem;
            margin: 0 auto 20px;
        }
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 10px;
            padding: 12px 30px;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
        }
        .btn-outline-primary {
            border: 2px solid #667eea;
            color: #667eea;
            border-radius: 10px;
            padding: 12px 30px;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn-outline-primary:hover {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-color: #667eea;
            transform: translateY(-3px);
        }
        .system-title {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            font-weight: bold;
        }
        .floating-animation {
            animation: floating 3s ease-in-out infinite;
        }
        @keyframes floating {
            0%, 100% { transform: translateY(0px); }
            50% { transform: translateY(-10px); }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-10">
                <div class="welcome-card p-5">
                    <div class="text-center mb-5">
                        <div class="floating-animation">
                            <i class="fas fa-store fa-4x text-primary mb-3"></i>
                        </div>
                        <h1 class="system-title display-4 mb-3">超市积分管理系统</h1>
                        <p class="lead text-muted">智能化积分管理，提升购物体验</p>
                    </div>

                    <div class="row mb-5">
                        <div class="col-md-3 mb-4">
                            <div class="text-center">
                                <div class="feature-icon">
                                    <i class="fas fa-users"></i>
                                </div>
                                <h5>会员管理</h5>
                                <p class="text-muted small">积分查询、兑换、活动参与</p>
                            </div>
                        </div>
                        <div class="col-md-3 mb-4">
                            <div class="text-center">
                                <div class="feature-icon">
                                    <i class="fas fa-cash-register"></i>
                                </div>
                                <h5>收银管理</h5>
                                <p class="text-muted small">商品销售、积分发放</p>
                            </div>
                        </div>
                        <div class="col-md-3 mb-4">
                            <div class="text-center">
                                <div class="feature-icon">
                                    <i class="fas fa-chart-line"></i>
                                </div>
                                <h5>经理管理</h5>
                                <p class="text-muted small">数据统计、业务分析</p>
                            </div>
                        </div>
                        <div class="col-md-3 mb-4">
                            <div class="text-center">
                                <div class="feature-icon">
                                    <i class="fas fa-cog"></i>
                                </div>
                                <h5>系统管理</h5>
                                <p class="text-muted small">用户管理、系统配置</p>
                            </div>
                        </div>
                    </div>

                    <div class="text-center">
                        <div class="d-flex flex-column flex-sm-row gap-3 justify-content-center">
                            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-lg">
                                <i class="fas fa-sign-in-alt me-2"></i>立即登录
                            </a>
                            <a href="${pageContext.request.contextPath}/register" class="btn btn-outline-primary btn-lg">
                                <i class="fas fa-user-plus me-2"></i>注册账号
                            </a>
                        </div>
                        <p class="text-muted mt-3 small">
                            <i class="fas fa-shield-alt me-1"></i>
                            安全可靠的积分管理平台
                        </p>
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
            // 添加页面加载动画
            $('.welcome-card').hide().fadeIn(1000);
            
            // 按钮悬停效果
            $('.btn').hover(
                function() {
                    $(this).addClass('shadow-lg');
                },
                function() {
                    $(this).removeClass('shadow-lg');
                }
            );
        });
    </script>
</body>
</html>
