<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>会员中心 - 超市积分管理系统</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <!-- 通用样式 -->
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .navbar-brand {
            font-weight: bold;
        }
        .card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
            transition: transform 0.3s ease;
        }
        .card:hover {
            transform: translateY(-5px);
        }
        .points-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        .level-card {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
        }
        .feature-card {
            background: white;
            border-left: 4px solid #667eea;
        }
        .feature-icon {
            width: 60px;
            height: 60px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 24px;
        }
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 10px;
        }
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        .welcome-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 15px;
            margin-bottom: 30px;
        }
    </style>
</head>
<body>
    <!-- 导航栏 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="#">
                <i class="fas fa-store me-2"></i>超市积分管理系统
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/member/dashboard">
                            <i class="fas fa-tachometer-alt me-1"></i>会员中心
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/member/points">
                            <i class="fas fa-coins me-1"></i>积分管理
                        </a>
                    </li>
                </ul>
                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
                            <i class="fas fa-user me-1"></i>${sessionScope.realName}
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="#"><i class="fas fa-user-edit me-2"></i>个人信息</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                <i class="fas fa-sign-out-alt me-2"></i>退出登录</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <!-- 欢迎区域 -->
        <div class="welcome-section p-4">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h2 class="mb-2">
                        <i class="fas fa-hand-wave me-2"></i>
                        欢迎回来，${user.realName}！
                    </h2>
                    <p class="mb-0 opacity-75">
                        <i class="fas fa-calendar me-2"></i>
                        今天是 <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy年MM月dd日"/>
                    </p>
                </div>
                <div class="col-md-4 text-end">
                    <div class="d-flex align-items-center justify-content-end">
                        <div class="me-3">
                            <small class="opacity-75">会员等级</small>
                            <div class="fw-bold">${user.membershipLevel}</div>
                        </div>
                        <i class="fas fa-crown fa-3x opacity-75"></i>
                    </div>
                </div>
            </div>
        </div>

        <!-- 积分概览 -->
        <div class="row mb-4">
            <div class="col-md-6 mb-3">
                <div class="card points-card h-100">
                    <div class="card-body text-center">
                        <i class="fas fa-coins fa-3x mb-3 opacity-75"></i>
                        <h3 class="card-title">当前积分</h3>
                        <h1 class="display-4 fw-bold">${user.totalPoints}</h1>
                        <p class="mb-0 opacity-75">可用于兑换商品或优惠</p>
                    </div>
                </div>
            </div>
            <div class="col-md-6 mb-3">
                <div class="card level-card h-100">
                    <div class="card-body text-center">
                        <i class="fas fa-star fa-3x mb-3 opacity-75"></i>
                        <h3 class="card-title">会员等级</h3>
                        <h2 class="display-6 fw-bold">${user.membershipLevel}</h2>
                        <p class="mb-0 opacity-75">享受专属会员权益</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- 功能菜单 -->
        <div class="row">
            <div class="col-md-4 mb-4">
                <div class="card feature-card h-100">
                    <div class="card-body text-center">
                        <div class="feature-icon mx-auto mb-3">
                            <i class="fas fa-search"></i>
                        </div>
                        <h5 class="card-title">积分查询</h5>
                        <p class="card-text text-muted">查看积分明细和交易记录</p>
                        <a href="${pageContext.request.contextPath}/member/points/history" class="btn btn-primary">
                            <i class="fas fa-eye me-2"></i>查看详情
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4 mb-4">
                <div class="card feature-card h-100">
                    <div class="card-body text-center">
                        <div class="feature-icon mx-auto mb-3">
                            <i class="fas fa-gift"></i>
                        </div>
                        <h5 class="card-title">积分兑换</h5>
                        <p class="card-text text-muted">使用积分兑换商品和优惠券</p>
                        <a href="${pageContext.request.contextPath}/member/points/redeem" class="btn btn-primary">
                            <i class="fas fa-exchange-alt me-2"></i>立即兑换
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4 mb-4">
                <div class="card feature-card h-100">
                    <div class="card-body text-center">
                        <div class="feature-icon mx-auto mb-3">
                            <i class="fas fa-percentage"></i>
                        </div>
                        <h5 class="card-title">优惠活动</h5>
                        <p class="card-text text-muted">参与积分翻倍等优惠活动</p>
                        <a href="${pageContext.request.contextPath}/member/promotions" class="btn btn-primary">
                            <i class="fas fa-tags me-2"></i>查看活动
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- 快速统计 -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header bg-white">
                        <h5 class="card-title mb-0">
                            <i class="fas fa-chart-line me-2 text-primary"></i>积分统计
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row text-center">
                            <div class="col-md-3">
                                <div class="border-end">
                                    <h4 class="text-primary">${user.totalPoints}</h4>
                                    <small class="text-muted">总积分</small>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="border-end">
                                    <h4 class="text-success">0</h4>
                                    <small class="text-muted">本月获得</small>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="border-end">
                                    <h4 class="text-warning">0</h4>
                                    <small class="text-muted">本月消费</small>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <h4 class="text-info">${user.membershipLevel}</h4>
                                <small class="text-muted">会员等级</small>
                            </div>
                        </div>
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
            // 卡片悬停效果
            $('.card').hover(
                function() {
                    $(this).addClass('shadow-lg');
                },
                function() {
                    $(this).removeClass('shadow-lg');
                }
            );
            
            // 定时刷新积分信息
            setInterval(function() {
                // 这里可以添加AJAX请求来刷新积分信息
                console.log('刷新积分信息...');
            }, 60000); // 每分钟刷新一次
        });
    </script>
</body>
</html>