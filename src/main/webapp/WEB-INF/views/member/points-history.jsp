<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>积分查询 - 超市积分管理系统</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
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
        }
        .points-summary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 15px;
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
        .table th {
            background-color: #f8f9fa;
            border: none;
            font-weight: 600;
        }
        .badge-success {
            background-color: #28a745;
        }
        .badge-danger {
            background-color: #dc3545;
        }
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }
        .empty-state i {
            font-size: 4rem;
            margin-bottom: 20px;
            opacity: 0.5;
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/member/dashboard">
                            <i class="fas fa-tachometer-alt me-1"></i>会员中心
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/member/points">
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
        <!-- 面包屑导航 -->
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item">
                    <a href="${pageContext.request.contextPath}/member/dashboard">
                        <i class="fas fa-home me-1"></i>会员中心
                    </a>
                </li>
                <li class="breadcrumb-item active" aria-current="page">
                    <i class="fas fa-coins me-1"></i>积分查询
                </li>
            </ol>
        </nav>

        <!-- 积分概览 -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card points-summary">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <h3 class="card-title mb-2">
                                    <i class="fas fa-coins me-2"></i>我的积分
                                </h3>
                                <div class="row">
                                    <div class="col-sm-4">
                                        <div class="text-center">
                                            <h2 class="display-6 fw-bold mb-0">${user.totalPoints}</h2>
                                            <small class="opacity-75">当前积分</small>
                                        </div>
                                    </div>
                                    <div class="col-sm-4">
                                        <div class="text-center border-start border-light border-opacity-25">
                                            <h4 class="mb-0">0</h4>
                                            <small class="opacity-75">本月获得</small>
                                        </div>
                                    </div>
                                    <div class="col-sm-4">
                                        <div class="text-center border-start border-light border-opacity-25">
                                            <h4 class="mb-0">0</h4>
                                            <small class="opacity-75">本月消费</small>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4 text-end">
                                <div class="d-flex flex-column align-items-end">
                                    <div class="mb-2">
                                        <span class="badge bg-light text-dark fs-6">${user.membershipLevel}</span>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/member/points/redeem" 
                                       class="btn btn-light btn-sm">
                                        <i class="fas fa-gift me-1"></i>立即兑换
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 功能按钮 -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-body">
                        <div class="d-flex flex-wrap gap-2">
                            <a href="${pageContext.request.contextPath}/member/points/history" 
                               class="btn btn-primary">
                                <i class="fas fa-history me-2"></i>积分明细
                            </a>
                            <a href="${pageContext.request.contextPath}/member/points/redeem" 
                               class="btn btn-outline-primary">
                                <i class="fas fa-gift me-2"></i>积分兑换
                            </a>
                            <button type="button" class="btn btn-outline-secondary" onclick="refreshPoints()">
                                <i class="fas fa-sync-alt me-2"></i>刷新积分
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 积分明细 -->
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header bg-white">
                        <div class="d-flex justify-content-between align-items-center">
                            <h5 class="card-title mb-0">
                                <i class="fas fa-list me-2 text-primary"></i>积分明细
                            </h5>
                            <div class="d-flex gap-2">
                                <select class="form-select form-select-sm" id="timeFilter" style="width: auto;">
                                    <option value="all">全部时间</option>
                                    <option value="today">今天</option>
                                    <option value="week">本周</option>
                                    <option value="month">本月</option>
                                    <option value="year">本年</option>
                                </select>
                                <select class="form-select form-select-sm" id="typeFilter" style="width: auto;">
                                    <option value="all">全部类型</option>
                                    <option value="earn">积分获得</option>
                                    <option value="redeem">积分兑换</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="card-body p-0">
                        <!-- 这里显示积分历史记录，目前为演示数据 -->
                        <c:choose>
                            <c:when test="${empty historyList}">
                                <div class="empty-state">
                                    <i class="fas fa-inbox"></i>
                                    <h5>暂无积分记录</h5>
                                    <p class="text-muted">您还没有任何积分交易记录</p>
                                    <a href="${pageContext.request.contextPath}/member/dashboard" 
                                       class="btn btn-primary">
                                        <i class="fas fa-arrow-left me-2"></i>返回会员中心
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover mb-0">
                                        <thead>
                                            <tr>
                                                <th>时间</th>
                                                <th>类型</th>
                                                <th>描述</th>
                                                <th>积分变动</th>
                                                <th>余额</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="history" items="${historyList}">
                                                <tr>
                                                    <td>
                                                        ${history.transactionDate.toString().substring(0, 16).replace('T', ' ')}
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${history.points > 0}">
                                                                <span class="badge badge-success">积分获得</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge badge-danger">积分消费</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>${history.description}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${history.points > 0}">
                                                                <span class="text-success fw-bold">
                                                                    +${history.points}
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-danger fw-bold">
                                                                    ${history.points}
                                                                </span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td class="fw-bold">${user.totalPoints}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>
                        
                        <!-- 演示数据 -->
                        <div class="table-responsive">
                            <table class="table table-hover mb-0">
                                <thead>
                                    <tr>
                                        <th>时间</th>
                                        <th>类型</th>
                                        <th>描述</th>
                                        <th>积分变动</th>
                                        <th>余额</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td><fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy-MM-dd HH:mm"/></td>
                                        <td><span class="badge badge-success">积分获得</span></td>
                                        <td>购物消费获得积分</td>
                                        <td><span class="text-success fw-bold">+50</span></td>
                                        <td class="fw-bold">${user.totalPoints}</td>
                                    </tr>
                                    <tr>
                                        <td>2024-01-15 14:30</td>
                                        <td><span class="badge badge-danger">积分消费</span></td>
                                        <td>兑换优惠券</td>
                                        <td><span class="text-danger fw-bold">-100</span></td>
                                        <td class="fw-bold">${user.totalPoints - 50}</td>
                                    </tr>
                                    <tr>
                                        <td>2024-01-10 10:15</td>
                                        <td><span class="badge badge-success">积分获得</span></td>
                                        <td>会员注册奖励</td>
                                        <td><span class="text-success fw-bold">+100</span></td>
                                        <td class="fw-bold">${user.totalPoints - 100}</td>
                                    </tr>
                                </tbody>
                            </table>
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
            // 筛选功能
            $('#timeFilter, #typeFilter').change(function() {
                // 这里可以添加AJAX请求来筛选数据
                console.log('筛选条件改变:', {
                    time: $('#timeFilter').val(),
                    type: $('#typeFilter').val()
                });
            });
        });
        
        // 刷新积分
        function refreshPoints() {
            // 显示加载状态
            const btn = event.target;
            const originalText = btn.innerHTML;
            btn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>刷新中...';
            btn.disabled = true;
            
            // 模拟刷新
            setTimeout(function() {
                location.reload();
            }, 1000);
        }
    </script>
</body>
</html>