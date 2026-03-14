<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>优惠活动 - 超市积分管理系统</title>
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
            transition: transform 0.3s ease;
        }
        .card:hover {
            transform: translateY(-5px);
        }
        .promotion-card {
            background: white;
            border-left: 4px solid #667eea;
            margin-bottom: 20px;
        }
        .promotion-card.double-points {
            border-left-color: #28a745;
        }
        .promotion-card.special-offer {
            border-left-color: #dc3545;
        }
        .promotion-card.points-bonus {
            border-left-color: #ffc107;
        }
        .promotion-icon {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 24px;
        }
        .promotion-icon.double-points {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
        }
        .promotion-icon.special-offer {
            background: linear-gradient(135deg, #dc3545 0%, #fd7e14 100%);
        }
        .promotion-icon.points-bonus {
            background: linear-gradient(135deg, #ffc107 0%, #fd7e14 100%);
        }
        .btn-participate {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 25px;
            padding: 10px 30px;
            color: white;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        .btn-participate:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            color: white;
        }
        .alert {
            border-radius: 10px;
            border: none;
        }
        .breadcrumb {
            background: transparent;
            padding: 0;
        }
        .breadcrumb-item a {
            color: #667eea;
            text-decoration: none;
        }
        .breadcrumb-item.active {
            color: #6c757d;
        }
    </style>
</head>
<body>
    <!-- 导航栏 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/member/dashboard">
                <i class="fas fa-store me-2"></i>超市积分管理系统
            </a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text me-3">
                    <i class="fas fa-user me-1"></i>欢迎，${sessionScope.user.realName}
                </span>
                <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                    <i class="fas fa-sign-out-alt me-1"></i>退出登录
                </a>
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
                    <i class="fas fa-tags me-1"></i>优惠活动
                </li>
            </ol>
        </nav>

        <!-- 页面标题 -->
        <div class="row mb-4">
            <div class="col-12">
                <h2 class="text-primary">
                    <i class="fas fa-tags me-2"></i>优惠活动
                </h2>
                <p class="text-muted">参与各种优惠活动，享受更多购物福利</p>
            </div>
        </div>

        <!-- 消息提示 -->
        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i>${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- 优惠活动列表 -->
        <div class="row">
            <c:forEach var="promotion" items="${promotions}">
                <div class="col-md-6 col-lg-4 mb-4">
                    <div class="card promotion-card ${promotion.type.toLowerCase().replace('_', '-')}">
                        <div class="card-body">
                            <div class="d-flex align-items-center mb-3">
                                <div class="promotion-icon ${promotion.type.toLowerCase().replace('_', '-')} me-3">
                                    <c:choose>
                                        <c:when test="${promotion.type == 'DOUBLE_POINTS'}">
                                            <i class="fas fa-coins"></i>
                                        </c:when>
                                        <c:when test="${promotion.type == 'SPECIAL_OFFER'}">
                                            <i class="fas fa-percentage"></i>
                                        </c:when>
                                        <c:when test="${promotion.type == 'POINTS_BONUS'}">
                                            <i class="fas fa-gift"></i>
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fas fa-star"></i>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div>
                                    <h5 class="card-title mb-1">${promotion.title}</h5>
                                    <span class="badge bg-success">进行中</span>
                                </div>
                            </div>
                            
                            <p class="card-text text-muted mb-3">${promotion.description}</p>
                            
                            <div class="mb-3">
                                <div class="row">
                                    <div class="col-6">
                                        <small class="text-muted">参与条件</small>
                                        <div class="fw-bold">${promotion.requirement}</div>
                                    </div>
                                    <div class="col-6">
                                        <small class="text-muted">活动奖励</small>
                                        <div class="fw-bold text-success">${promotion.reward}</div>
                                    </div>
                                </div>
                            </div>
                            
                            <form method="post" action="${pageContext.request.contextPath}/member/promotions">
                                <input type="hidden" name="action" value="participate">
                                <input type="hidden" name="promotionId" value="${promotion.id}">
                                <button type="submit" class="btn btn-participate w-100">
                                    <i class="fas fa-hand-point-right me-2"></i>立即参与
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- 如果没有活动 -->
        <c:if test="${empty promotions}">
            <div class="row">
                <div class="col-12">
                    <div class="card text-center py-5">
                        <div class="card-body">
                            <i class="fas fa-tags fa-3x text-muted mb-3"></i>
                            <h5 class="text-muted">暂无优惠活动</h5>
                            <p class="text-muted">请稍后再来查看最新的优惠活动</p>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- 返回按钮 -->
        <div class="row mt-4">
            <div class="col-12 text-center">
                <a href="${pageContext.request.contextPath}/member/dashboard" class="btn btn-outline-primary">
                    <i class="fas fa-arrow-left me-2"></i>返回会员中心
                </a>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // 自动隐藏提示消息
        setTimeout(function() {
            var alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                var bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
    </script>
</body>
</html>