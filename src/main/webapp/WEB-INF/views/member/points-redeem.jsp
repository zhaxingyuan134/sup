<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>积分兑换 - 超市积分管理系统</title>
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
        .points-summary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 15px;
        }
        .redeem-item {
            border: 2px solid transparent;
            transition: all 0.3s ease;
        }
        .redeem-item:hover {
            border-color: #667eea;
            transform: translateY(-3px);
        }
        .redeem-item.selected {
            border-color: #667eea;
            background-color: #f8f9ff;
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
        .points-badge {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            border-radius: 20px;
            padding: 5px 15px;
            font-weight: bold;
        }
        .category-filter {
            border-radius: 25px;
            border: 2px solid #e9ecef;
            transition: all 0.3s ease;
        }
        .category-filter.active {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-color: #667eea;
        }
        .item-image {
            width: 100%;
            height: 150px;
            object-fit: cover;
            border-radius: 10px;
        }
        .insufficient-points {
            opacity: 0.6;
            pointer-events: none;
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
                <li class="breadcrumb-item">
                    <a href="${pageContext.request.contextPath}/member/points">
                        <i class="fas fa-coins me-1"></i>积分管理
                    </a>
                </li>
                <li class="breadcrumb-item active" aria-current="page">
                    <i class="fas fa-gift me-1"></i>积分兑换
                </li>
            </ol>
        </nav>

        <!-- 消息提示 -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i>${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- 积分概览 -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card points-summary">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <h3 class="card-title mb-2">
                                    <i class="fas fa-gift me-2"></i>积分兑换商城
                                </h3>
                                <p class="mb-0 opacity-75">使用您的积分兑换心仪的商品和优惠券</p>
                            </div>
                            <div class="col-md-4 text-end">
                                <div class="d-flex flex-column align-items-end">
                                    <small class="opacity-75 mb-1">可用积分</small>
                                    <h2 class="display-6 fw-bold mb-0">${user.totalPoints}</h2>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 分类筛选 -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-body">
                        <h6 class="card-title mb-3">
                            <i class="fas fa-filter me-2 text-primary"></i>商品分类
                        </h6>
                        <div class="d-flex flex-wrap gap-2">
                            <button type="button" class="btn category-filter active" data-category="all">
                                <i class="fas fa-th me-1"></i>全部商品
                            </button>
                            <button type="button" class="btn category-filter" data-category="coupon">
                                <i class="fas fa-ticket-alt me-1"></i>优惠券
                            </button>
                            <button type="button" class="btn category-filter" data-category="gift">
                                <i class="fas fa-gift me-1"></i>实物商品
                            </button>
                            <button type="button" class="btn category-filter" data-category="service">
                                <i class="fas fa-concierge-bell me-1"></i>服务类
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 兑换商品列表 -->
        <div class="row" id="redeemItems">
            <!-- 优惠券类 -->
            <c:if test="${not empty couponItems}">
                <c:forEach var="item" items="${couponItems}">
                    <div class="col-md-6 col-lg-4 mb-4 redeem-item-container" data-category="coupon">
                        <div class="card redeem-item h-100">
                            <div class="card-body text-center">
                                <div class="mb-3">
                                    <i class="fas fa-percentage fa-4x text-primary"></i>
                                </div>
                                <h5 class="card-title">${item.itemName}</h5>
                                <p class="card-text text-muted">${item.description}</p>
                                <div class="mb-3">
                                    <span class="points-badge">${item.pointsRequired}积分</span>
                                    <c:if test="${item.stockQuantity <= 10 && item.stockQuantity > 0}">
                                        <small class="text-warning d-block mt-1">仅剩${item.stockQuantity}件</small>
                                    </c:if>
                                    <c:if test="${item.stockQuantity <= 0}">
                                        <small class="text-danger d-block mt-1">暂时缺货</small>
                                    </c:if>
                                </div>
                                <c:choose>
                                    <c:when test="${item.stockQuantity <= 0}">
                                        <button type="button" class="btn btn-secondary w-100" disabled>
                                            <i class="fas fa-times me-2"></i>暂时缺货
                                        </button>
                                    </c:when>
                                    <c:when test="${user.totalPoints >= item.pointsRequired}">
                                        <button type="button" class="btn btn-primary w-100" 
                                                onclick="redeemItem(${item.itemId}, ${item.pointsRequired}, '${item.itemName}')">
                                            <i class="fas fa-exchange-alt me-2"></i>立即兑换
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="button" class="btn btn-secondary w-100" disabled>
                                            <i class="fas fa-lock me-2"></i>积分不足
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:if>

            <!-- 实物商品类 -->
            <c:if test="${not empty giftItems}">
                <c:forEach var="item" items="${giftItems}">
                    <div class="col-md-6 col-lg-4 mb-4 redeem-item-container" data-category="gift">
                        <div class="card redeem-item h-100">
                            <div class="card-body text-center">
                                <div class="mb-3">
                                    <i class="fas fa-gift fa-4x text-warning"></i>
                                </div>
                                <h5 class="card-title">${item.itemName}</h5>
                                <p class="card-text text-muted">${item.description}</p>
                                <div class="mb-3">
                                    <span class="points-badge">${item.pointsRequired}积分</span>
                                    <c:if test="${item.stockQuantity <= 10 && item.stockQuantity > 0}">
                                        <small class="text-warning d-block mt-1">仅剩${item.stockQuantity}件</small>
                                    </c:if>
                                    <c:if test="${item.stockQuantity <= 0}">
                                        <small class="text-danger d-block mt-1">暂时缺货</small>
                                    </c:if>
                                </div>
                                <c:choose>
                                    <c:when test="${item.stockQuantity <= 0}">
                                        <button type="button" class="btn btn-secondary w-100" disabled>
                                            <i class="fas fa-times me-2"></i>暂时缺货
                                        </button>
                                    </c:when>
                                    <c:when test="${user.totalPoints >= item.pointsRequired}">
                                        <button type="button" class="btn btn-primary w-100" 
                                                onclick="redeemItem(${item.itemId}, ${item.pointsRequired}, '${item.itemName}')">
                                            <i class="fas fa-exchange-alt me-2"></i>立即兑换
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="button" class="btn btn-secondary w-100" disabled>
                                            <i class="fas fa-lock me-2"></i>积分不足
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:if>

            <!-- 服务类 -->
            <c:if test="${not empty serviceItems}">
                <c:forEach var="item" items="${serviceItems}">
                    <div class="col-md-6 col-lg-4 mb-4 redeem-item-container" data-category="service">
                        <div class="card redeem-item h-100">
                            <div class="card-body text-center">
                                <div class="mb-3">
                                    <i class="fas fa-concierge-bell fa-4x text-danger"></i>
                                </div>
                                <h5 class="card-title">${item.itemName}</h5>
                                <p class="card-text text-muted">${item.description}</p>
                                <div class="mb-3">
                                    <span class="points-badge">${item.pointsRequired}积分</span>
                                    <c:if test="${item.stockQuantity <= 10 && item.stockQuantity > 0}">
                                        <small class="text-warning d-block mt-1">仅剩${item.stockQuantity}件</small>
                                    </c:if>
                                    <c:if test="${item.stockQuantity <= 0}">
                                        <small class="text-danger d-block mt-1">暂时缺货</small>
                                    </c:if>
                                </div>
                                <c:choose>
                                    <c:when test="${item.stockQuantity <= 0}">
                                        <button type="button" class="btn btn-secondary w-100" disabled>
                                            <i class="fas fa-times me-2"></i>暂时缺货
                                        </button>
                                    </c:when>
                                    <c:when test="${user.totalPoints >= item.pointsRequired}">
                                        <button type="button" class="btn btn-primary w-100" 
                                                onclick="redeemItem(${item.itemId}, ${item.pointsRequired}, '${item.itemName}')">
                                            <i class="fas fa-exchange-alt me-2"></i>立即兑换
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="button" class="btn btn-secondary w-100" disabled>
                                            <i class="fas fa-lock me-2"></i>积分不足
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:if>

            <!-- 如果没有可兑换商品 -->
            <c:if test="${empty couponItems && empty giftItems && empty serviceItems}">
                <div class="col-12">
                    <div class="text-center py-5">
                        <i class="fas fa-box-open fa-4x text-muted mb-3"></i>
                        <h5 class="text-muted">暂无可兑换商品</h5>
                        <p class="text-muted">请稍后再来查看</p>
                    </div>
                </div>
            </c:if>
        </div>

        <!-- 兑换说明 -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header bg-white">
                        <h6 class="card-title mb-0">
                            <i class="fas fa-info-circle me-2 text-primary"></i>兑换说明
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <ul class="list-unstyled">
                                    <li class="mb-2">
                                        <i class="fas fa-check text-success me-2"></i>
                                        积分兑换后不可退换
                                    </li>
                                    <li class="mb-2">
                                        <i class="fas fa-check text-success me-2"></i>
                                        优惠券有效期为30天
                                    </li>
                                    <li class="mb-2">
                                        <i class="fas fa-check text-success me-2"></i>
                                        实物商品7个工作日内发货
                                    </li>
                                </ul>
                            </div>
                            <div class="col-md-6">
                                <ul class="list-unstyled">
                                    <li class="mb-2">
                                        <i class="fas fa-check text-success me-2"></i>
                                        兑换记录可在积分明细中查看
                                    </li>
                                    <li class="mb-2">
                                        <i class="fas fa-check text-success me-2"></i>
                                        如有问题请联系客服
                                    </li>
                                    <li class="mb-2">
                                        <i class="fas fa-check text-success me-2"></i>
                                        积分获取请查看会员权益
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 兑换确认模态框 -->
    <div class="modal fade" id="redeemModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="fas fa-gift me-2 text-primary"></i>确认兑换
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="text-center">
                        <i class="fas fa-question-circle fa-3x text-warning mb-3"></i>
                        <h6>您确定要兑换以下商品吗？</h6>
                        <div class="alert alert-info mt-3">
                            <div class="row align-items-center">
                                <div class="col-8">
                                    <strong id="itemName"></strong>
                                </div>
                                <div class="col-4 text-end">
                                    <span class="badge bg-primary fs-6" id="itemPoints"></span>
                                </div>
                            </div>
                        </div>
                        <p class="text-muted">
                            兑换后您的积分余额将变为：
                            <strong class="text-primary" id="remainingPoints"></strong>
                        </p>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times me-2"></i>取消
                    </button>
                    <form method="post" action="${pageContext.request.contextPath}/member/points/redeem" style="display: inline;">
                        <input type="hidden" name="itemId" id="confirmItemId">
                        <input type="hidden" name="points" id="confirmPoints">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-check me-2"></i>确认兑换
                        </button>
                    </form>
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
            // 分类筛选
            $('.category-filter').click(function() {
                $('.category-filter').removeClass('active');
                $(this).addClass('active');
                
                const category = $(this).data('category');
                if (category === 'all') {
                    $('.redeem-item-container').show();
                } else {
                    $('.redeem-item-container').hide();
                    $(`.redeem-item-container[data-category="${category}"]`).show();
                }
            });
            
            // 卡片悬停效果
            $('.redeem-item').hover(
                function() {
                    $(this).addClass('shadow-lg');
                },
                function() {
                    $(this).removeClass('shadow-lg');
                }
            );
        });
        
        // 兑换商品
        function redeemItem(itemId, points, itemName) {
            const currentPoints = ${user.totalPoints};
            const remainingPoints = currentPoints - points;
            
            $('#itemName').text(itemName);
            $('#itemPoints').text(points + '积分');
            $('#remainingPoints').text(remainingPoints + '积分');
            $('#confirmItemId').val(itemId);
            $('#confirmPoints').val(points);
            
            const modal = new bootstrap.Modal(document.getElementById('redeemModal'));
            modal.show();
        }
    </script>
</body>
</html>