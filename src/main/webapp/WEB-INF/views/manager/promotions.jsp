<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>促销活动管理 - 超市经理系统</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            --success-gradient: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            --warning-gradient: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
            --danger-gradient: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
            --info-gradient: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
        }

        body {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            font-family: 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, sans-serif;
            min-height: 100vh;
        }

        .sidebar {
            background: var(--primary-gradient);
            min-height: 100vh;
            box-shadow: 4px 0 20px rgba(0,0,0,0.1);
            position: fixed;
            width: 280px;
            z-index: 1000;
        }

        .sidebar .nav-link {
            color: rgba(255,255,255,0.9);
            padding: 15px 25px;
            margin: 8px 15px;
            border-radius: 12px;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            font-weight: 500;
        }

        .sidebar .nav-link:hover,
        .sidebar .nav-link.active {
            color: white;
            background: rgba(255,255,255,0.2);
            transform: translateX(8px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }

        .main-content {
            margin-left: 280px;
            padding: 30px;
            min-height: 100vh;
        }

        .page-header {
            background: white;
            border-radius: 20px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            border: 1px solid rgba(255,255,255,0.2);
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 25px;
            margin-bottom: 30px;
        }

        .stats-card {
            background: white;
            border-radius: 20px;
            padding: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            border: 1px solid rgba(255,255,255,0.2);
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            position: relative;
            overflow: hidden;
        }

        .stats-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: var(--primary-gradient);
        }

        .stats-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 20px 40px rgba(0,0,0,0.15);
        }

        .stats-icon {
            width: 70px;
            height: 70px;
            border-radius: 18px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 28px;
            color: white;
            margin-bottom: 20px;
        }

        .promotion-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
            gap: 25px;
            margin-top: 30px;
        }

        .promotion-card {
            background: white;
            border-radius: 20px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            border: 1px solid rgba(255,255,255,0.2);
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            position: relative;
            overflow: hidden;
        }

        .promotion-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
        }

        .promotion-card.double-points::before {
            background: var(--warning-gradient);
        }

        .promotion-card.discount::before {
            background: var(--success-gradient);
        }

        .promotion-card.gift::before {
            background: var(--info-gradient);
        }

        .promotion-card.expired::before {
            background: #6c757d;
        }

        .promotion-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 35px rgba(0,0,0,0.15);
        }

        .btn-gradient {
            background: var(--primary-gradient);
            border: none;
            color: white;
            border-radius: 12px;
            padding: 12px 25px;
            font-weight: 500;
            transition: all 0.3s ease;
        }

        .btn-gradient:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.2);
            color: white;
        }

        .btn-success-gradient {
            background: var(--success-gradient);
            border: none;
            color: white;
        }

        .btn-warning-gradient {
            background: var(--warning-gradient);
            border: none;
            color: white;
        }

        .btn-danger-gradient {
            background: var(--danger-gradient);
            border: none;
            color: white;
        }

        .modal-content {
            border-radius: 20px;
            border: none;
            box-shadow: 0 20px 40px rgba(0,0,0,0.2);
        }

        .modal-header {
            background: var(--primary-gradient);
            color: white;
            border-radius: 20px 20px 0 0;
            border: none;
        }

        .form-control, .form-select {
            border-radius: 12px;
            border: 2px solid #e9ecef;
            padding: 12px 15px;
            transition: all 0.3s ease;
        }

        .form-control:focus, .form-select:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }

        .badge-status {
            padding: 8px 15px;
            border-radius: 20px;
            font-weight: 500;
        }

        .badge-active {
            background: var(--success-gradient);
            color: white;
        }

        .badge-inactive {
            background: #6c757d;
            color: white;
        }

        .badge-pending {
            background: var(--warning-gradient);
            color: white;
        }

        .badge-expired {
            background: var(--danger-gradient);
            color: white;
        }

        .quick-actions {
            background: white;
            border-radius: 20px;
            padding: 25px;
            margin-bottom: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }

        .action-btn {
            background: white;
            border: 2px solid #e9ecef;
            border-radius: 15px;
            padding: 20px;
            text-align: center;
            transition: all 0.3s ease;
            cursor: pointer;
            text-decoration: none;
            color: #495057;
            display: block;
        }

        .action-btn:hover {
            border-color: #667eea;
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
            color: #667eea;
            text-decoration: none;
        }

        .loading-spinner {
            display: none;
            text-align: center;
            padding: 50px;
        }

        .promotion-type-selector {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 20px;
        }

        .type-option {
            border: 2px solid #e9ecef;
            border-radius: 15px;
            padding: 20px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .type-option:hover,
        .type-option.selected {
            border-color: #667eea;
            background: rgba(102, 126, 234, 0.1);
        }

        .double-points-config {
            background: rgba(255, 193, 7, 0.1);
            border: 2px solid #ffc107;
            border-radius: 15px;
            padding: 20px;
            margin-top: 15px;
        }

        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }

        @keyframes slideOut {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }

        .notification {
            animation: slideIn 0.3s ease;
        }
    </style>
</head>
<body>
    <!-- 侧边栏 -->
    <nav class="sidebar">
        <div class="p-4">
            <div class="text-center mb-4">
                <i class="fas fa-store-alt fa-3x text-white mb-3"></i>
                <h4 class="text-white mb-1">超市经理系统</h4>
                <small class="text-white-50">促销活动管理</small>
            </div>
            
            <div class="nav flex-column">
                <a class="nav-link" href="${pageContext.request.contextPath}/manager/dashboard">
                    <i class="fas fa-tachometer-alt me-3"></i>仪表盘
                </a>
                <a class="nav-link active" href="${pageContext.request.contextPath}/manager/promotions">
                    <i class="fas fa-tags me-3"></i>促销活动管理
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/manager/data-analysis">
                    <i class="fas fa-chart-bar me-3"></i>数据统计分析
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/manager/member-levels">
                    <i class="fas fa-users me-3"></i>会员分级管理
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/manager/product-analysis">
                    <i class="fas fa-box me-3"></i>商品热销分析
                </a>
                <hr class="my-3" style="border-color: rgba(255,255,255,0.2);">
                <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                    <i class="fas fa-sign-out-alt me-3"></i>退出登录
                </a>
            </div>
        </div>
    </nav>

    <!-- 主内容区 -->
    <div class="main-content">
        <!-- 页面标题 -->
        <div class="page-header">
            <div class="row align-items-center">
                <div class="col">
                    <h2 class="mb-2">
                        <i class="fas fa-tags me-3" style="color: #667eea;"></i>
                        促销活动管理
                    </h2>
                    <p class="text-muted mb-0">管理超市促销活动，设置双倍积分日等特殊活动规则</p>
                </div>
                <div class="col-auto">
                    <span class="badge bg-primary fs-6" id="currentDateTime"></span>
                </div>
            </div>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-grid" id="statsGrid">
            <div class="stats-card">
                <div class="stats-icon" style="background: var(--primary-gradient);">
                    <i class="fas fa-tags"></i>
                </div>
                <h3 class="mb-1" id="totalPromotions">-</h3>
                <p class="text-muted mb-0">总促销活动</p>
            </div>
            <div class="stats-card">
                <div class="stats-icon" style="background: var(--success-gradient);">
                    <i class="fas fa-play-circle"></i>
                </div>
                <h3 class="mb-1" id="activePromotions">-</h3>
                <p class="text-muted mb-0">进行中活动</p>
            </div>
            <div class="stats-card">
                <div class="stats-icon" style="background: var(--warning-gradient);">
                    <i class="fas fa-star"></i>
                </div>
                <h3 class="mb-1" id="doublePointsPromotions">-</h3>
                <p class="text-muted mb-0">双倍积分日</p>
            </div>
            <div class="stats-card">
                <div class="stats-icon" style="background: var(--info-gradient);">
                    <i class="fas fa-users"></i>
                </div>
                <h3 class="mb-1" id="monthlyUsage">-</h3>
                <p class="text-muted mb-0">本月使用次数</p>
            </div>
        </div>

        <!-- 快捷操作 -->
        <div class="quick-actions">
            <h5 class="mb-4">
                <i class="fas fa-bolt me-2" style="color: #667eea;"></i>
                快捷操作
            </h5>
            <div class="row">
                <div class="col-md-3">
                    <div class="action-btn" onclick="showCreatePromotionModal()">
                        <i class="fas fa-plus-circle fa-2x mb-3" style="color: #667eea;"></i>
                        <h6>创建促销活动</h6>
                        <small class="text-muted">新建各类促销活动</small>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="action-btn" onclick="createDoublePointsDay()">
                        <i class="fas fa-star fa-2x mb-3" style="color: #ffc107;"></i>
                        <h6>设置双倍积分日</h6>
                        <small class="text-muted">快速创建积分翻倍活动</small>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="action-btn" onclick="viewPromotionStats()">
                        <i class="fas fa-chart-line fa-2x mb-3" style="color: #28a745;"></i>
                        <h6>查看统计报告</h6>
                        <small class="text-muted">分析促销活动效果</small>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="action-btn" onclick="managePromotionRules()">
                        <i class="fas fa-cogs fa-2x mb-3" style="color: #17a2b8;"></i>
                        <h6>管理促销规则</h6>
                        <small class="text-muted">配置活动规则模板</small>
                    </div>
                </div>
            </div>
        </div>

        <!-- 促销活动列表 -->
        <div class="card" style="border-radius: 20px; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.1);">
            <div class="card-header" style="background: white; border-radius: 20px 20px 0 0; border: none;">
                <div class="row align-items-center">
                    <div class="col">
                        <h5 class="mb-0">
                            <i class="fas fa-list me-2" style="color: #667eea;"></i>
                            促销活动列表
                        </h5>
                    </div>
                    <div class="col-auto">
                        <div class="btn-group">
                            <button class="btn btn-outline-primary btn-sm" onclick="filterPromotions('all')">
                                全部
                            </button>
                            <button class="btn btn-outline-success btn-sm" onclick="filterPromotions('active')">
                                进行中
                            </button>
                            <button class="btn btn-outline-warning btn-sm" onclick="filterPromotions('double_points')">
                                双倍积分
                            </button>
                            <button class="btn btn-outline-secondary btn-sm" onclick="filterPromotions('expired')">
                                已过期
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="card-body">
                <div class="loading-spinner" id="loadingSpinner">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">加载中...</span>
                    </div>
                    <p class="mt-3 text-muted">正在加载促销活动...</p>
                </div>
                <div class="promotion-grid" id="promotionGrid">
                    <!-- 促销活动卡片将通过JavaScript动态加载 -->
                </div>
            </div>
        </div>
    </div>

    <!-- 创建促销活动模态框 -->
    <div class="modal fade" id="createPromotionModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="fas fa-plus-circle me-2"></i>
                        创建促销活动
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form id="createPromotionForm">
                        <!-- 促销类型选择 -->
                        <div class="mb-4">
                            <label class="form-label fw-bold">选择促销类型</label>
                            <div class="promotion-type-selector">
                                <div class="type-option" data-type="DOUBLE_POINTS">
                                    <i class="fas fa-star fa-2x mb-2" style="color: #ffc107;"></i>
                                    <h6>双倍积分日</h6>
                                    <small class="text-muted">积分翻倍奖励</small>
                                </div>
                                <div class="type-option" data-type="DISCOUNT">
                                    <i class="fas fa-percentage fa-2x mb-2" style="color: #28a745;"></i>
                                    <h6>折扣优惠</h6>
                                    <small class="text-muted">商品打折销售</small>
                                </div>
                                <div class="type-option" data-type="SPECIAL_OFFER">
                                    <i class="fas fa-gift fa-2x mb-2" style="color: #17a2b8;"></i>
                                    <h6>特殊优惠</h6>
                                    <small class="text-muted">满额送礼品</small>
                                </div>
                            </div>
                            <input type="hidden" id="promotionType" name="promotionType" required>
                        </div>

                        <!-- 基本信息 -->
                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="promotionName" class="form-label">活动名称</label>
                                    <input type="text" class="form-control" id="promotionName" name="promotionName" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="minPurchaseAmount" class="form-label">最低消费金额</label>
                                    <input type="number" class="form-control" id="minPurchaseAmount" name="minPurchaseAmount" step="0.01" min="0">
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="startDate" class="form-label">开始日期</label>
                                    <input type="date" class="form-control" id="startDate" name="startDate" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="endDate" class="form-label">结束日期</label>
                                    <input type="date" class="form-control" id="endDate" name="endDate" required>
                                </div>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="description" class="form-label">活动描述</label>
                            <textarea class="form-control" id="description" name="description" rows="3"></textarea>
                        </div>

                        <!-- 双倍积分日特殊配置 -->
                        <div class="double-points-config" id="doublePointsConfig" style="display: none;">
                            <h6 class="mb-3">
                                <i class="fas fa-star me-2" style="color: #ffc107;"></i>
                                双倍积分日配置
                            </h6>
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label for="pointsMultiplier" class="form-label">积分倍数</label>
                                        <select class="form-select" id="pointsMultiplier" name="pointsMultiplier">
                                            <option value="2">2倍积分</option>
                                            <option value="3">3倍积分</option>
                                            <option value="5">5倍积分</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label for="memberLevel" class="form-label">适用会员等级</label>
                                        <select class="form-select" id="memberLevel" name="memberLevel">
                                            <option value="all">所有会员</option>
                                            <option value="bronze">青铜会员</option>
                                            <option value="silver">白银会员</option>
                                            <option value="gold">黄金会员</option>
                                            <option value="platinum">铂金会员</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label for="productCategory" class="form-label">适用商品类别</label>
                                        <select class="form-select" id="productCategory" name="productCategory">
                                            <option value="all">所有商品</option>
                                            <option value="food">食品饮料</option>
                                            <option value="daily">日用百货</option>
                                            <option value="fresh">生鲜食品</option>
                                            <option value="electronics">电子产品</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="alert alert-info">
                                <i class="fas fa-info-circle me-2"></i>
                                双倍积分日活动将在指定时间段内，为符合条件的会员提供积分倍数奖励。
                            </div>
                        </div>

                        <!-- 折扣优惠配置 -->
                        <div class="discount-config" id="discountConfig" style="display: none;">
                            <h6 class="mb-3">
                                <i class="fas fa-percentage me-2" style="color: #28a745;"></i>
                                折扣优惠配置
                            </h6>
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="discountRate" class="form-label">折扣率 (%)</label>
                                        <input type="number" class="form-control" id="discountRate" name="discountRate" min="1" max="99" step="0.1">
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="maxDiscountAmount" class="form-label">最大折扣金额</label>
                                        <input type="number" class="form-control" id="maxDiscountAmount" name="maxDiscountAmount" step="0.01" min="0">
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- 满赠活动配置 -->
                        <div class="gift-config" id="giftConfig" style="display: none;">
                            <h6 class="mb-3">
                                <i class="fas fa-gift me-2" style="color: #17a2b8;"></i>
                                满赠活动配置
                            </h6>
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="giftName" class="form-label">赠品名称</label>
                                        <input type="text" class="form-control" id="giftName" name="giftName">
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="triggerAmount" class="form-label">触发金额</label>
                                        <input type="number" class="form-control" id="triggerAmount" name="triggerAmount" step="0.01" min="0">
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="maxUsagePerMember" class="form-label">每人最大使用次数</label>
                            <input type="number" class="form-control" id="maxUsagePerMember" name="maxUsagePerMember" min="1" value="1">
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-gradient" onclick="createPromotion()">
                        <i class="fas fa-save me-2"></i>创建活动
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- JavaScript -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
    <script>
        // 页面初始化
        document.addEventListener('DOMContentLoaded', function() {
            updateDateTime();
            setInterval(updateDateTime, 1000);
            loadPromotionStatistics();
            loadPromotions();
            initializeDatePickers();
            initializePromotionTypeSelector();
        });

        // 更新日期时间
        function updateDateTime() {
            const now = new Date();
            const options = { 
                year: 'numeric', 
                month: '2-digit', 
                day: '2-digit', 
                hour: '2-digit', 
                minute: '2-digit',
                second: '2-digit'
            };
            document.getElementById('currentDateTime').textContent = now.toLocaleString('zh-CN', options);
        }

        // 初始化日期选择器
        function initializeDatePickers() {
            flatpickr("#startDate", {
                dateFormat: "Y-m-d",
                minDate: "today"
            });
            
            flatpickr("#endDate", {
                dateFormat: "Y-m-d",
                minDate: "today"
            });
        }

        // 初始化促销类型选择器
        function initializePromotionTypeSelector() {
            const typeOptions = document.querySelectorAll('.type-option');
            typeOptions.forEach(option => {
                option.addEventListener('click', function() {
                    // 移除其他选中状态
                    typeOptions.forEach(opt => opt.classList.remove('selected'));
                    // 添加选中状态
                    this.classList.add('selected');
                    
                    const type = this.dataset.type;
                    document.getElementById('promotionType').value = type;
                    
                    // 显示对应的配置区域
                    showPromotionConfig(type);
                });
            });
        }

        // 显示促销配置区域
        function showPromotionConfig(type) {
            // 隐藏所有配置区域
            document.getElementById('doublePointsConfig').style.display = 'none';
            document.getElementById('discountConfig').style.display = 'none';
            document.getElementById('giftConfig').style.display = 'none';
            
            // 显示对应的配置区域
            if (type === 'DOUBLE_POINTS') {
                document.getElementById('doublePointsConfig').style.display = 'block';
            } else if (type === 'DISCOUNT') {
                document.getElementById('discountConfig').style.display = 'block';
            } else if (type === 'SPECIAL_OFFER') {
                document.getElementById('giftConfig').style.display = 'block';
            }
        }

        // 加载促销统计信息
        function loadPromotionStatistics() {
            fetch('${pageContext.request.contextPath}/manager/promotions/statistics')
                .then(response => response.json())
                .then(data => {
                    document.getElementById('totalPromotions').textContent = data.totalCount || 0;
                    document.getElementById('activePromotions').textContent = data.activeCount || 0;
                    document.getElementById('doublePointsPromotions').textContent = data.doublePointsCount || 0;
                    document.getElementById('monthlyUsage').textContent = data.monthlyUsage || 0;
                })
                .catch(error => {
                    console.error('加载统计信息失败:', error);
                });
        }

        // 加载促销活动列表
        function loadPromotions() {
            const loadingSpinner = document.getElementById('loadingSpinner');
            const promotionGrid = document.getElementById('promotionGrid');
            
            loadingSpinner.style.display = 'block';
            promotionGrid.innerHTML = '';
            
            fetch('${pageContext.request.contextPath}/manager/promotions/list')
                .then(response => response.json())
                .then(data => {
                    loadingSpinner.style.display = 'none';
                    displayPromotions(data.promotions || []);
                })
                .catch(error => {
                    console.error('加载促销活动失败:', error);
                    loadingSpinner.style.display = 'none';
                    showNotification('加载促销活动失败', 'error');
                });
        }

        // 显示促销活动
        function displayPromotions(promotions) {
            const promotionGrid = document.getElementById('promotionGrid');
            
            if (promotions.length === 0) {
                promotionGrid.innerHTML = `
                    <div class="col-12 text-center py-5">
                        <i class="fas fa-tags fa-3x text-muted mb-3"></i>
                        <h5 class="text-muted">暂无促销活动</h5>
                        <p class="text-muted">点击"创建促销活动"开始添加新的促销活动</p>
                    </div>
                `;
                return;
            }
            
            promotionGrid.innerHTML = promotions.map(promotion => createPromotionCard(promotion)).join('');
        }

        // 创建促销活动卡片
        function createPromotionCard(promotion) {
            const statusClass = getPromotionStatusClass(promotion);
            const statusText = getPromotionStatusText(promotion);
            const typeIcon = getPromotionTypeIcon(promotion.promotionType);
            
            return `
                <div class="promotion-card ${promotion.promotionType.toLowerCase()}">
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <div>
                            <h6 class="mb-1">
                                <i class="${typeIcon} me-2"></i>
                                ${promotion.promotionName}
                            </h6>
                            <span class="badge badge-status ${statusClass}">${statusText}</span>
                        </div>
                        <div class="dropdown">
                            <button class="btn btn-sm btn-outline-secondary" data-bs-toggle="dropdown">
                                <i class="fas fa-ellipsis-v"></i>
                            </button>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="#" onclick="editPromotion(${promotion.promotionId})">
                                    <i class="fas fa-edit me-2"></i>编辑
                                </a></li>
                                <li><a class="dropdown-item" href="#" onclick="togglePromotion(${promotion.promotionId})">
                                    <i class="fas fa-power-off me-2"></i>切换状态
                                </a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="#" onclick="deletePromotion(${promotion.promotionId})">
                                    <i class="fas fa-trash me-2"></i>删除
                                </a></li>
                            </ul>
                        </div>
                    </div>
                    
                    <p class="text-muted small mb-3">${not empty promotion.description ? promotion.description : '暂无描述'}</p>
                    
                    <div class="row text-center mb-3">
                        <div class="col-6">
                            <small class="text-muted d-block">开始日期</small>
                            <strong>${promotion.startDate}</strong>
                        </div>
                        <div class="col-6">
                            <small class="text-muted d-block">结束日期</small>
                            <strong>${promotion.endDate}</strong>
                        </div>
                    </div>
                    
                    <c:if test="${promotion.minPurchaseAmount > 0}">
                        <div class="alert alert-info py-2 mb-3">
                            <small><i class="fas fa-info-circle me-1"></i>最低消费：¥${promotion.minPurchaseAmount}</small>
                        </div>
                    </c:if>
                    
                    <div class="d-flex justify-content-between align-items-center">
                        <small class="text-muted">
                            <i class="fas fa-user me-1"></i>
                            每人限用 ${promotion.maxUsagePerMember > 0 ? promotion.maxUsagePerMember : '不限'} 次
                        </small>
                        <button class="btn btn-sm btn-outline-primary" onclick="viewPromotionDetail(${promotion.promotionId})">
                            查看详情
                        </button>
                    </div>
                </div>
            `;
        }

        // 获取促销状态样式类
        function getPromotionStatusClass(promotion) {
            const now = new Date();
            const startDate = new Date(promotion.startDate);
            const endDate = new Date(promotion.endDate);
            
            if (!promotion.active) return 'badge-inactive';
            if (now < startDate) return 'badge-pending';
            if (now > endDate) return 'badge-expired';
            return 'badge-active';
        }

        // 获取促销状态文本
        function getPromotionStatusText(promotion) {
            const now = new Date();
            const startDate = new Date(promotion.startDate);
            const endDate = new Date(promotion.endDate);
            
            if (!promotion.active) return '已停用';
            if (now < startDate) return '未开始';
            if (now > endDate) return '已过期';
            return '进行中';
        }

        // 获取促销类型图标
        function getPromotionTypeIcon(type) {
            const icons = {
                'DOUBLE_POINTS': 'fas fa-star',
                'DISCOUNT': 'fas fa-percentage',
                'GIFT': 'fas fa-gift'
            };
            return icons[type] || 'fas fa-tag';
        }

        // 格式化日期
        function formatDate(dateString) {
            const date = new Date(dateString);
            return date.toLocaleDateString('zh-CN');
        }

        // 显示创建促销活动模态框
        function showCreatePromotionModal() {
            const modal = new bootstrap.Modal(document.getElementById('createPromotionModal'));
            modal.show();
        }

        // 创建促销活动
        function createPromotion() {
            const form = document.getElementById('createPromotionForm');
            const formData = new FormData(form);

            // 基础表单校验
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }

            // 促销类型必选校验
            const type = formData.get('promotionType');
            if (!type) {
                showNotification('请选择促销类型', 'error');
                return;
            }

            // 构建规则配置
            const ruleConfig = buildRuleConfig(formData);
            // 使用 x-www-form-urlencoded 方式提交，避免 multipart 造成参数获取失败
            const params = new URLSearchParams();
            for (const [key, value] of formData.entries()) {
                if (key === 'ruleConfig') continue;
                params.append(key, value);
            }
            params.append('ruleConfig', JSON.stringify(ruleConfig));

            fetch('${pageContext.request.contextPath}/manager/promotions', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                body: params.toString()
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showNotification('促销活动创建成功', 'success');
                    bootstrap.Modal.getInstance(document.getElementById('createPromotionModal')).hide();
                    form.reset();
                    loadPromotions();
                    loadPromotionStatistics();
                } else {
                    showNotification(data.message || '创建失败', 'error');
                }
            })
            .catch(error => {
                console.error('创建促销活动失败:', error);
                showNotification('创建失败，请重试', 'error');
            });
        }

        // 构建规则配置
        function buildRuleConfig(formData) {
            const type = formData.get('promotionType');
            const config = {};

            if (type === 'DOUBLE_POINTS') {
                config.multiplier = parseFloat(formData.get('pointsMultiplier')) || 2;
                config.memberLevel = formData.get('memberLevel') || 'all';
                config.productCategory = formData.get('productCategory') || 'all';
            } else if (type === 'DISCOUNT') {
                config.discountRate = parseFloat(formData.get('discountRate')) || 0;
                config.maxDiscountAmount = parseFloat(formData.get('maxDiscountAmount')) || 0;
            } else if (type === 'SPECIAL_OFFER') {
                config.giftName = formData.get('giftName') || '';
                config.triggerAmount = parseFloat(formData.get('triggerAmount')) || 0;
            }

            return config;
        }

        // 快速创建双倍积分日
        function createDoublePointsDay() {
            // 预填充双倍积分日表单
            document.querySelector('[data-type="DOUBLE_POINTS"]').click();
            document.getElementById('promotionName').value = '双倍积分日活动';
            document.getElementById('description').value = '在活动期间，所有消费均可获得双倍积分奖励！';
            
            const today = new Date();
            const tomorrow = new Date(today);
            tomorrow.setDate(tomorrow.getDate() + 1);
            
            document.getElementById('startDate').value = tomorrow.toISOString().split('T')[0];
            document.getElementById('endDate').value = tomorrow.toISOString().split('T')[0];
            
            showCreatePromotionModal();
        }

        // 编辑促销活动
        function editPromotion(id) {
            showNotification('编辑功能开发中', 'info');
        }

        // 切换促销活动状态
        function togglePromotion(id) {
            fetch(`${pageContext.request.contextPath}/manager/promotions/toggle`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `promotionId=${id}`
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showNotification('状态切换成功', 'success');
                    loadPromotions();
                    loadPromotionStatistics();
                } else {
                    showNotification(data.message || '操作失败', 'error');
                }
            })
            .catch(error => {
                console.error('切换状态失败:', error);
                showNotification('操作失败，请重试', 'error');
            });
        }

        // 删除促销活动
        function deletePromotion(id) {
            if (!confirm('确定要删除这个促销活动吗？此操作不可恢复。')) {
                return;
            }
            
            fetch(`${pageContext.request.contextPath}/manager/promotions/delete`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `promotionId=${id}`
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showNotification('删除成功', 'success');
                    loadPromotions();
                    loadPromotionStatistics();
                } else {
                    showNotification(data.message || '删除失败', 'error');
                }
            })
            .catch(error => {
                console.error('删除失败:', error);
                showNotification('删除失败，请重试', 'error');
            });
        }

        // 查看促销活动详情
        function viewPromotionDetail(id) {
            window.open(`${pageContext.request.contextPath}/manager/promotions/detail/${id}`, '_blank');
        }

        // 筛选促销活动
        function filterPromotions(filter) {
            // 实现筛选逻辑
            showNotification(`筛选功能开发中: ${filter}`, 'info');
        }

        // 查看促销统计
        function viewPromotionStats() {
            window.location.href = '${pageContext.request.contextPath}/manager/data-analysis';
        }

        // 管理促销规则
        function managePromotionRules() {
            showNotification('规则管理功能开发中', 'info');
        }

        // 显示通知
        function showNotification(message, type = 'info') {
            const colors = {
                success: '#28a745',
                error: '#dc3545',
                warning: '#ffc107',
                info: '#17a2b8'
            };

            const notification = document.createElement('div');
            notification.className = 'notification';
            notification.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                background: ${colors[type]};
                color: white;
                padding: 15px 20px;
                border-radius: 10px;
                box-shadow: 0 5px 15px rgba(0,0,0,0.2);
                z-index: 9999;
                font-weight: 500;
                max-width: 300px;
            `;
            notification.textContent = message;

            document.body.appendChild(notification);

            setTimeout(() => {
                notification.style.animation = 'slideOut 0.3s ease';
                setTimeout(() => notification.remove(), 300);
            }, 3000);
        }
    </script>
</body>
</html>
