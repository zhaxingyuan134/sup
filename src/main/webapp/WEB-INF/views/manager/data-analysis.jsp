<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>数据统计分析 - 超市管理系统</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <!-- Date Range Picker -->
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css" />
    
    <style>
        :root {
            --primary-color: #2c3e50;
            --secondary-color: #3498db;
            --success-color: #27ae60;
            --warning-color: #f39c12;
            --danger-color: #e74c3c;
            --info-color: #17a2b8;
            --light-bg: #f8f9fa;
            --dark-bg: #343a40;
            --purple-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            --blue-gradient: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
            --green-gradient: linear-gradient(135deg, #00b894 0%, #00a085 100%);
            --orange-gradient: linear-gradient(135deg, #fdcb6e 0%, #e17055 100%);
            --red-gradient: linear-gradient(135deg, #fd79a8 0%, #e84393 100%);
        }

        body {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            min-height: 100vh;
        }

        .sidebar {
            background: var(--purple-gradient);
            min-height: 100vh;
            box-shadow: 2px 0 20px rgba(0,0,0,0.1);
            position: fixed;
            top: 0;
            left: 0;
            width: 280px;
            z-index: 1000;
            overflow-y: auto;
        }

        .sidebar-header {
            padding: 25px 20px;
            border-bottom: 1px solid rgba(255,255,255,0.1);
            text-align: center;
        }

        .sidebar-header h4 {
            color: white;
            margin: 0;
            font-weight: 600;
        }

        .sidebar .nav-link {
            color: rgba(255,255,255,0.8);
            padding: 15px 20px;
            margin: 5px 15px;
            border-radius: 12px;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            font-weight: 500;
        }

        .sidebar .nav-link:hover,
        .sidebar .nav-link.active {
            background-color: rgba(255,255,255,0.15);
            color: white;
            transform: translateX(5px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        .sidebar .nav-link i {
            width: 20px;
            margin-right: 12px;
            font-size: 16px;
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

        .page-header h1 {
            margin: 0;
            color: var(--primary-color);
            font-weight: 700;
            font-size: 2.2rem;
        }

        .page-header p {
            margin: 10px 0 0 0;
            color: #6c757d;
            font-size: 1.1rem;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 25px;
            margin-bottom: 30px;
        }

        .stats-card {
            background: white;
            border-radius: 20px;
            padding: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            border: 1px solid rgba(255,255,255,0.2);
            transition: all 0.3s ease;
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
            background: var(--blue-gradient);
        }

        .stats-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 40px rgba(0,0,0,0.15);
        }

        .stats-icon {
            width: 60px;
            height: 60px;
            border-radius: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 20px;
            font-size: 24px;
            color: white;
        }

        .stats-number {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 5px;
        }

        .stats-label {
            color: #6c757d;
            font-size: 1rem;
            font-weight: 500;
        }

        .chart-container {
            background: white;
            border-radius: 20px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            border: 1px solid rgba(255,255,255,0.2);
        }

        .chart-title {
            color: var(--primary-color);
            font-weight: 600;
            margin-bottom: 25px;
            font-size: 1.3rem;
        }

        .tab-content {
            display: none;
        }

        .tab-content.active {
            display: block;
        }

        .data-table {
            background: white;
            border-radius: 20px;
            padding: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            border: 1px solid rgba(255,255,255,0.2);
            margin-bottom: 30px;
        }

        .table {
            margin-bottom: 0;
        }

        .table th {
            background-color: #f8f9fa;
            border: none;
            font-weight: 600;
            color: var(--primary-color);
            padding: 15px;
        }

        .table td {
            border: none;
            padding: 15px;
            vertical-align: middle;
        }

        .table tbody tr {
            border-bottom: 1px solid #eee;
            transition: background-color 0.3s ease;
        }

        .table tbody tr:hover {
            background-color: #f8f9fa;
        }

        .btn-custom {
            border-radius: 12px;
            padding: 12px 25px;
            font-weight: 600;
            transition: all 0.3s ease;
            border: none;
        }

        .btn-primary-custom {
            background: var(--blue-gradient);
            color: white;
        }

        .btn-primary-custom:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(116, 185, 255, 0.4);
        }

        .btn-success-custom {
            background: var(--green-gradient);
            color: white;
        }

        .btn-warning-custom {
            background: var(--orange-gradient);
            color: white;
        }

        .btn-danger-custom {
            background: var(--red-gradient);
            color: white;
        }

        .form-select, .form-control {
            border-radius: 12px;
            border: 2px solid #e9ecef;
            padding: 12px 15px;
            transition: all 0.3s ease;
        }

        .form-select:focus, .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }

        .badge {
            border-radius: 8px;
            padding: 8px 12px;
            font-weight: 600;
        }

        .loading-spinner {
            display: none;
            text-align: center;
            padding: 50px;
        }

        .loading-spinner .spinner-border {
            width: 3rem;
            height: 3rem;
        }

        .promotion-effect-card {
            background: white;
            border-radius: 20px;
            padding: 25px;
            margin-bottom: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            border: 1px solid rgba(255,255,255,0.2);
        }

        .promotion-effect-card h5 {
            color: var(--primary-color);
            font-weight: 600;
            margin-bottom: 20px;
        }

        .effect-metric {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #eee;
        }

        .effect-metric:last-child {
            border-bottom: none;
        }

        .metric-value {
            font-weight: 700;
            font-size: 1.2rem;
            color: var(--success-color);
        }

        .rank-badge {
            width: 30px;
            height: 30px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            color: white;
        }

        .rank-1 { background: linear-gradient(135deg, #FFD700, #FFA500); }
        .rank-2 { background: linear-gradient(135deg, #C0C0C0, #A9A9A9); }
        .rank-3 { background: linear-gradient(135deg, #CD7F32, #B8860B); }
        .rank-other { background: linear-gradient(135deg, #6c757d, #495057); }

        @media (max-width: 768px) {
            .sidebar {
                transform: translateX(-100%);
                transition: transform 0.3s ease;
            }
            
            .sidebar.show {
                transform: translateX(0);
            }
            
            .main-content {
                margin-left: 0;
                padding: 20px;
            }
            
            .stats-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <!-- 侧边栏 -->
    <nav class="sidebar">
        <div class="sidebar-header">
            <h4><i class="fas fa-chart-bar me-2"></i>数据分析中心</h4>
        </div>
        
        <div class="p-3">
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a class="nav-link active" href="#" data-tab="overview">
                        <i class="fas fa-tachometer-alt"></i>数据概览
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" data-tab="promotion-analysis">
                        <i class="fas fa-bullhorn"></i>促销效果分析
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" data-tab="hot-products">
                        <i class="fas fa-fire"></i>商品热销分析
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" data-tab="sales-trend">
                        <i class="fas fa-chart-area"></i>销售趋势分析
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" data-tab="member-analysis">
                        <i class="fas fa-users"></i>会员行为分析
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" data-tab="category-analysis">
                        <i class="fas fa-tags"></i>商品分类分析
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" data-tab="points-analysis">
                        <i class="fas fa-coins"></i>积分系统分析
                    </a>
                </li>
            </ul>
            
            <hr class="text-white-50 mx-3">
            
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/manager/dashboard">
                        <i class="fas fa-arrow-left"></i>返回仪表盘
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/manager/promotions">
                        <i class="fas fa-tags"></i>促销管理
                    </a>
                </li>
            </ul>
        </div>
    </nav>

    <!-- 主内容区 -->
    <div class="main-content">
        <!-- 数据概览 -->
        <div id="overview-content" class="tab-content active">
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1><i class="fas fa-tachometer-alt me-3"></i>数据概览</h1>
                        <p class="text-muted mb-0">实时监控超市运营关键指标</p>
                    </div>
                    <button type="button" class="btn btn-primary-custom btn-custom" onclick="refreshOverview()">
                        <i class="fas fa-sync-alt me-2"></i>刷新数据
                    </button>
                </div>
            </div>

            <!-- 统计卡片 -->
            <div class="stats-grid">
                <div class="stats-card">
                    <div class="stats-icon" style="background: var(--green-gradient);">
                        <i class="fas fa-dollar-sign"></i>
                    </div>
                    <div class="stats-number text-success" id="today-sales">¥0.00</div>
                    <div class="stats-label">今日销售额</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: var(--blue-gradient);">
                        <i class="fas fa-shopping-cart"></i>
                    </div>
                    <div class="stats-number text-info" id="today-orders">0</div>
                    <div class="stats-label">今日订单数</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: var(--orange-gradient);">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="stats-number text-warning" id="today-customers">0</div>
                    <div class="stats-label">今日客户数</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: var(--red-gradient);">
                        <i class="fas fa-coins"></i>
                    </div>
                    <div class="stats-number text-danger" id="today-points">0</div>
                    <div class="stats-label">今日积分发放</div>
                </div>
            </div>

            <!-- 图表区域 -->
            <div class="row">
                <div class="col-lg-8">
                    <div class="chart-container">
                        <h5 class="chart-title">销售趋势（最近7天）</h5>
                        <canvas id="salesOverviewChart" width="400" height="200"></canvas>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="chart-container">
                        <h5 class="chart-title">商品分类销售占比</h5>
                        <canvas id="categoryPieChart" width="400" height="200"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- 促销效果分析 -->
        <div id="promotion-analysis-content" class="tab-content">
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1><i class="fas fa-bullhorn me-3"></i>促销效果分析</h1>
                        <p class="text-muted mb-0">分析各类促销活动的效果和ROI</p>
                    </div>
                    <button type="button" class="btn btn-primary-custom btn-custom" onclick="refreshPromotionAnalysis()">
                        <i class="fas fa-sync-alt me-2"></i>刷新数据
                    </button>
                </div>
            </div>

            <!-- 促销活动效果卡片 -->
            <div class="row mb-4">
                <div class="col-lg-6">
                    <div class="promotion-effect-card">
                        <h5><i class="fas fa-star me-2"></i>双倍积分日效果</h5>
                        <div class="effect-metric">
                            <span>参与人数</span>
                            <span class="metric-value" id="double-points-participants">0</span>
                        </div>
                        <div class="effect-metric">
                            <span>销售额提升</span>
                            <span class="metric-value" id="double-points-sales-increase">0%</span>
                        </div>
                        <div class="effect-metric">
                            <span>积分发放量</span>
                            <span class="metric-value" id="double-points-issued">0</span>
                        </div>
                        <div class="effect-metric">
                            <span>ROI</span>
                            <span class="metric-value" id="double-points-roi">0%</span>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="promotion-effect-card">
                        <h5><i class="fas fa-percentage me-2"></i>折扣促销效果</h5>
                        <div class="effect-metric">
                            <span>参与人数</span>
                            <span class="metric-value" id="discount-participants">0</span>
                        </div>
                        <div class="effect-metric">
                            <span>销售额提升</span>
                            <span class="metric-value" id="discount-sales-increase">0%</span>
                        </div>
                        <div class="effect-metric">
                            <span>平均折扣率</span>
                            <span class="metric-value" id="avg-discount-rate">0%</span>
                        </div>
                        <div class="effect-metric">
                            <span>ROI</span>
                            <span class="metric-value" id="discount-roi">0%</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 促销活动对比图表 -->
            <div class="chart-container">
                <h5 class="chart-title">促销活动效果对比</h5>
                <canvas id="promotionComparisonChart" width="400" height="200"></canvas>
            </div>
        </div>

        <!-- 商品热销分析 -->
        <div id="hot-products-content" class="tab-content">
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1><i class="fas fa-fire me-3"></i>商品热销分析</h1>
                        <p class="text-muted mb-0">分析商品销售表现和热销趋势</p>
                    </div>
                    <div>
                        <select class="form-select me-3" id="hot-products-period" style="display: inline-block; width: auto;">
                            <option value="today">今日</option>
                            <option value="week">本周</option>
                            <option value="month" selected>本月</option>
                        </select>
                        <button type="button" class="btn btn-primary-custom btn-custom" onclick="refreshHotProducts()">
                            <i class="fas fa-sync-alt me-2"></i>刷新数据
                        </button>
                    </div>
                </div>
            </div>

            <!-- 热销商品统计 -->
            <div class="stats-grid">
                <div class="stats-card">
                    <div class="stats-icon" style="background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);">
                        <i class="fas fa-crown"></i>
                    </div>
                    <div class="stats-number text-danger" id="top-product-sales">0</div>
                    <div class="stats-label">热销冠军销量</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);">
                        <i class="fas fa-chart-bar"></i>
                    </div>
                    <div class="stats-number text-info" id="avg-product-sales">0</div>
                    <div class="stats-label">平均销量</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);">
                        <i class="fas fa-boxes"></i>
                    </div>
                    <div class="stats-number text-warning" id="total-categories">0</div>
                    <div class="stats-label">涉及分类数</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: linear-gradient(135deg, #d299c2 0%, #fef9d7 100%);">
                        <i class="fas fa-trending-up"></i>
                    </div>
                    <div class="stats-number text-success" id="growth-rate">0%</div>
                    <div class="stats-label">销量增长率</div>
                </div>
            </div>

            <!-- 热销商品排行榜 -->
            <div class="data-table">
                <h5 class="chart-title">热销商品排行榜</h5>
                <table class="table table-hover mb-0">
                    <thead>
                        <tr>
                            <th>排名</th>
                            <th>商品名称</th>
                            <th>分类</th>
                            <th>销量</th>
                            <th>销售额</th>
                            <th>库存</th>
                            <th>增长率</th>
                        </tr>
                    </thead>
                    <tbody id="hot-products-table">
                        <!-- 动态加载 -->
                    </tbody>
                </table>
            </div>
        </div>

        <!-- 销售趋势分析 -->
        <div id="sales-trend-content" class="tab-content">
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1><i class="fas fa-chart-area me-3"></i>销售趋势分析</h1>
                        <p class="text-muted mb-0">分析销售数据的时间趋势和周期性规律</p>
                    </div>
                    <div>
                        <input type="text" class="form-control me-3" id="sales-date-range" placeholder="选择日期范围" style="display: inline-block; width: 200px;">
                        <button type="button" class="btn btn-primary-custom btn-custom" onclick="refreshSalesTrend()">
                            <i class="fas fa-sync-alt me-2"></i>刷新数据
                        </button>
                    </div>
                </div>
            </div>

            <!-- 销售趋势图表 -->
            <div class="row">
                <div class="col-lg-8">
                    <div class="chart-container">
                        <h5 class="chart-title">销售额趋势</h5>
                        <canvas id="salesTrendChart" width="400" height="200"></canvas>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="chart-container">
                        <h5 class="chart-title">订单量趋势</h5>
                        <canvas id="orderTrendChart" width="400" height="200"></canvas>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-lg-6">
                    <div class="chart-container">
                        <h5 class="chart-title">客单价趋势</h5>
                        <canvas id="avgOrderValueChart" width="400" height="200"></canvas>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="chart-container">
                        <h5 class="chart-title">时段销售分布</h5>
                        <canvas id="hourlyDistributionChart" width="400" height="200"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- 会员行为分析 -->
        <div id="member-analysis-content" class="tab-content">
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1><i class="fas fa-users me-3"></i>会员行为分析</h1>
                        <p class="text-muted mb-0">分析会员消费行为和活跃度</p>
                    </div>
                    <button type="button" class="btn btn-primary-custom btn-custom" onclick="refreshMemberAnalysis()">
                        <i class="fas fa-sync-alt me-2"></i>刷新数据
                    </button>
                </div>
            </div>

            <!-- 会员统计 -->
            <div class="stats-grid">
                <div class="stats-card">
                    <div class="stats-icon" style="background: var(--blue-gradient);">
                        <i class="fas fa-user-plus"></i>
                    </div>
                    <div class="stats-number text-primary" id="total-members">0</div>
                    <div class="stats-label">总会员数</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: var(--green-gradient);">
                        <i class="fas fa-user-check"></i>
                    </div>
                    <div class="stats-number text-success" id="active-members">0</div>
                    <div class="stats-label">活跃会员数</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: var(--orange-gradient);">
                        <i class="fas fa-shopping-bag"></i>
                    </div>
                    <div class="stats-number text-warning" id="avg-member-orders">0</div>
                    <div class="stats-label">人均订单数</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: var(--red-gradient);">
                        <i class="fas fa-coins"></i>
                    </div>
                    <div class="stats-number text-danger" id="avg-member-points">0</div>
                    <div class="stats-label">人均积分</div>
                </div>
            </div>

            <!-- 会员分析图表 -->
            <div class="row">
                <div class="col-lg-6">
                    <div class="chart-container">
                        <h5 class="chart-title">会员等级分布</h5>
                        <canvas id="memberLevelChart" width="400" height="200"></canvas>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="chart-container">
                        <h5 class="chart-title">会员消费分布</h5>
                        <canvas id="memberSpendingChart" width="400" height="200"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- 商品分类分析 -->
        <div id="category-analysis-content" class="tab-content">
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1><i class="fas fa-tags me-3"></i>商品分类分析</h1>
                        <p class="text-muted mb-0">分析各商品分类的销售表现</p>
                    </div>
                    <button type="button" class="btn btn-primary-custom btn-custom" onclick="refreshCategoryAnalysis()">
                        <i class="fas fa-sync-alt me-2"></i>刷新数据
                    </button>
                </div>
            </div>

            <!-- 分类销售统计 -->
            <div class="chart-container">
                <h5 class="chart-title">分类销售统计</h5>
                <canvas id="categoryStatsChart" width="400" height="200"></canvas>
            </div>
        </div>

        <!-- 积分系统分析 -->
        <div id="points-analysis-content" class="tab-content">
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1><i class="fas fa-coins me-3"></i>积分系统分析</h1>
                        <p class="text-muted mb-0">分析积分发放、使用和促销效果</p>
                    </div>
                    <button type="button" class="btn btn-primary-custom btn-custom" onclick="refreshPointsAnalysis()">
                        <i class="fas fa-sync-alt me-2"></i>刷新数据
                    </button>
                </div>
            </div>

            <!-- 积分统计指标 -->
            <div class="stats-grid">
                <div class="stats-card">
                    <div class="stats-icon" style="background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);">
                        <i class="fas fa-plus-circle"></i>
                    </div>
                    <div class="stats-number text-warning" id="total-points-issued">0</div>
                    <div class="stats-label">累计发放积分</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);">
                        <i class="fas fa-minus-circle"></i>
                    </div>
                    <div class="stats-number text-info" id="total-points-used">0</div>
                    <div class="stats-label">累计使用积分</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: linear-gradient(135deg, #d299c2 0%, #fef9d7 100%);">
                        <i class="fas fa-wallet"></i>
                    </div>
                    <div class="stats-number text-success" id="points-balance">0</div>
                    <div class="stats-label">积分余额</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);">
                        <i class="fas fa-percentage"></i>
                    </div>
                    <div class="stats-number text-danger" id="points-usage-rate">0%</div>
                    <div class="stats-label">积分使用率</div>
                </div>
            </div>

            <!-- 积分分析图表 -->
            <div class="row">
                <div class="col-lg-8">
                    <div class="chart-container">
                        <h5 class="chart-title">积分发放与使用趋势</h5>
                        <canvas id="pointsTrendChart" width="400" height="200"></canvas>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="chart-container">
                        <h5 class="chart-title">积分来源分布</h5>
                        <canvas id="pointsSourceChart" width="400" height="200"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- 加载动画 -->
        <div class="loading-spinner" id="loadingSpinner">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">加载中...</span>
            </div>
            <p class="mt-3 text-muted">正在加载数据...</p>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- Date Range Picker -->
    <script type="text/javascript" src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>

    <script>
        // 全局变量
        let charts = {};
        
        // 页面加载完成后初始化
        $(document).ready(function() {
            initializePage();
            loadOverviewData();
            
            // 初始化日期选择器
            $('#sales-date-range').daterangepicker({
                startDate: moment().subtract(29, 'days'),
                endDate: moment(),
                locale: {
                    format: 'YYYY-MM-DD',
                    separator: ' 至 ',
                    applyLabel: '确定',
                    cancelLabel: '取消',
                    fromLabel: '从',
                    toLabel: '到',
                    customRangeLabel: '自定义',
                    weekLabel: 'W',
                    daysOfWeek: ['日', '一', '二', '三', '四', '五', '六'],
                    monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
                    firstDay: 1
                }
            });
        });

        // 初始化页面
        function initializePage() {
            // 标签页切换
            $('.sidebar .nav-link').click(function(e) {
                e.preventDefault();
                
                // 移除所有活跃状态
                $('.sidebar .nav-link').removeClass('active');
                $('.tab-content').removeClass('active');
                
                // 添加当前活跃状态
                $(this).addClass('active');
                
                // 显示对应内容
                const tabId = $(this).data('tab');
                $('#' + tabId + '-content').addClass('active');
                
                // 根据标签页加载对应数据
                loadTabData(tabId);
            });
        }

        // 根据标签页加载数据
        function loadTabData(tabId) {
            switch(tabId) {
                case 'overview':
                    loadOverviewData();
                    break;
                case 'promotion-analysis':
                    loadPromotionAnalysis();
                    break;
                case 'hot-products':
                    loadHotProductsData();
                    break;
                case 'sales-trend':
                    loadSalesTrendData();
                    break;
                case 'member-analysis':
                    loadMemberAnalysisData();
                    break;
                case 'category-analysis':
                    loadCategoryAnalysisData();
                    break;
                case 'points-analysis':
                    loadPointsAnalysisData();
                    break;
            }
        }

        // 显示加载动画
        function showLoading() {
            $('#loadingSpinner').show();
        }

        // 隐藏加载动画
        function hideLoading() {
            $('#loadingSpinner').hide();
        }

        // 加载概览数据
        function loadOverviewData() {
            showLoading();
            
            fetch('${pageContext.request.contextPath}/manager/data-analysis/overview')
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        updateOverviewStats(data.data);
                        createOverviewCharts(data.data);
                    } else {
                        console.error('加载概览数据失败:', data.message);
                        // 使用模拟数据
                        const mockData = {
                            todaySales: 15680.50,
                            todayOrders: 128,
                            todayCustomers: 95,
                            todayPoints: 3240,
                            salesTrendLabels: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
                            salesTrendData: [12000, 15000, 13500, 18000, 16500, 22000, 19500],
                            categoryLabels: ['食品', '饮料', '日用品', '服装', '电子产品'],
                            categoryData: [35, 25, 20, 12, 8]
                        };
                        updateOverviewStats(mockData);
                        createOverviewCharts(mockData);
                    }
                })
                .catch(error => {
                    console.error('请求失败:', error);
                    // 使用模拟数据
                    const mockData = {
                        todaySales: 15680.50,
                        todayOrders: 128,
                        todayCustomers: 95,
                        todayPoints: 3240,
                        salesTrendLabels: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
                        salesTrendData: [12000, 15000, 13500, 18000, 16500, 22000, 19500],
                        categoryLabels: ['食品', '饮料', '日用品', '服装', '电子产品'],
                        categoryData: [35, 25, 20, 12, 8]
                    };
                    updateOverviewStats(mockData);
                    createOverviewCharts(mockData);
                })
                .finally(() => {
                    hideLoading();
                });
        }

        // 更新概览统计数据
        function updateOverviewStats(data) {
            document.getElementById('today-sales').textContent = '¥' + (data.todaySales || 0).toFixed(2);
            document.getElementById('today-orders').textContent = data.todayOrders || 0;
            document.getElementById('today-customers').textContent = data.todayCustomers || 0;
            document.getElementById('today-points').textContent = data.todayPoints || 0;
        }

        // 创建概览图表
        function createOverviewCharts(data) {
            // 销售趋势图
            const salesCtx = document.getElementById('salesOverviewChart').getContext('2d');
            if (charts.salesOverview) {
                charts.salesOverview.destroy();
            }
            charts.salesOverview = new Chart(salesCtx, {
                type: 'line',
                data: {
                    labels: data.salesTrendLabels || [],
                    datasets: [{
                        label: '销售额',
                        data: data.salesTrendData || [],
                        borderColor: 'rgb(75, 192, 192)',
                        backgroundColor: 'rgba(75, 192, 192, 0.2)',
                        tension: 0.1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'top',
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });

            // 分类饼图
            const categoryCtx = document.getElementById('categoryPieChart').getContext('2d');
            if (charts.categoryPie) {
                charts.categoryPie.destroy();
            }
            charts.categoryPie = new Chart(categoryCtx, {
                type: 'doughnut',
                data: {
                    labels: data.categoryLabels || [],
                    datasets: [{
                        data: data.categoryData || [],
                        backgroundColor: [
                            '#FF6384',
                            '#36A2EB',
                            '#FFCE56',
                            '#4BC0C0',
                            '#9966FF'
                        ]
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'bottom',
                        }
                    }
                }
            });
        }

        // 加载促销分析数据
        function loadPromotionAnalysis() {
            showLoading();
            
            // 模拟数据
            setTimeout(() => {
                const mockData = {
                    doublePointsParticipants: 156,
                    doublePointsSalesIncrease: 23.5,
                    doublePointsIssued: 4580,
                    doublePointsRoi: 185.2,
                    discountParticipants: 89,
                    discountSalesIncrease: 18.7,
                    avgDiscountRate: 15.2,
                    discountRoi: 142.8
                };
                
                updatePromotionStats(mockData);
                createPromotionChart();
                hideLoading();
            }, 1000);
        }

        // 更新促销统计数据
        function updatePromotionStats(data) {
            document.getElementById('double-points-participants').textContent = data.doublePointsParticipants || 0;
            document.getElementById('double-points-sales-increase').textContent = (data.doublePointsSalesIncrease || 0) + '%';
            document.getElementById('double-points-issued').textContent = data.doublePointsIssued || 0;
            document.getElementById('double-points-roi').textContent = (data.doublePointsRoi || 0) + '%';
            
            document.getElementById('discount-participants').textContent = data.discountParticipants || 0;
            document.getElementById('discount-sales-increase').textContent = (data.discountSalesIncrease || 0) + '%';
            document.getElementById('avg-discount-rate').textContent = (data.avgDiscountRate || 0) + '%';
            document.getElementById('discount-roi').textContent = (data.discountRoi || 0) + '%';
        }

        // 创建促销对比图表
        function createPromotionChart() {
            const ctx = document.getElementById('promotionComparisonChart').getContext('2d');
            if (charts.promotionComparison) {
                charts.promotionComparison.destroy();
            }
            charts.promotionComparison = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: ['双倍积分日', '折扣促销', '满赠活动', '新品推广'],
                    datasets: [{
                        label: '参与人数',
                        data: [156, 89, 67, 45],
                        backgroundColor: 'rgba(54, 162, 235, 0.8)'
                    }, {
                        label: 'ROI (%)',
                        data: [185.2, 142.8, 98.5, 76.3],
                        backgroundColor: 'rgba(255, 99, 132, 0.8)',
                        yAxisID: 'y1'
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            type: 'linear',
                            display: true,
                            position: 'left',
                        },
                        y1: {
                            type: 'linear',
                            display: true,
                            position: 'right',
                            grid: {
                                drawOnChartArea: false,
                            },
                        }
                    }
                }
            });
        }

        // 加载热销商品数据
        function loadHotProductsData() {
            showLoading();
            
            // 模拟数据
            setTimeout(() => {
                const mockData = {
                    topProductSales: 245,
                    avgProductSales: 67,
                    totalCategories: 8,
                    growthRate: 12.5,
                    hotProducts: [
                        { rank: 1, name: '可口可乐 330ml', category: '饮料', sales: 245, revenue: 612.5, stock: 156, growth: 15.2 },
                        { rank: 2, name: '康师傅方便面', category: '食品', sales: 198, revenue: 594.0, stock: 89, growth: 12.8 },
                        { rank: 3, name: '农夫山泉 550ml', category: '饮料', sales: 187, revenue: 374.0, stock: 234, growth: 8.9 },
                        { rank: 4, name: '奥利奥饼干', category: '食品', sales: 156, revenue: 468.0, stock: 67, growth: 18.5 },
                        { rank: 5, name: '海飞丝洗发水', category: '日用品', sales: 134, revenue: 1072.0, stock: 45, growth: 22.1 }
                    ]
                };
                
                updateHotProductsStats(mockData);
                updateHotProductsTable(mockData.hotProducts);
                hideLoading();
            }, 1000);
        }

        // 更新热销商品统计
        function updateHotProductsStats(data) {
            document.getElementById('top-product-sales').textContent = data.topProductSales || 0;
            document.getElementById('avg-product-sales').textContent = data.avgProductSales || 0;
            document.getElementById('total-categories').textContent = data.totalCategories || 0;
            document.getElementById('growth-rate').textContent = (data.growthRate || 0) + '%';
        }

        // 更新热销商品表格
        function updateHotProductsTable(products) {
            const tbody = document.getElementById('hot-products-table');
            tbody.innerHTML = '';
            
            products.forEach(product => {
                const row = document.createElement('tr');
                const rankClass = product.rank <= 3 ? `rank-${product.rank}` : 'rank-other';
                
                row.innerHTML = `
                    <td><span class="rank-badge ${rankClass}">${product.rank}</span></td>
                    <td><strong>${product.name}</strong></td>
                    <td><span class="badge bg-secondary">${product.category}</span></td>
                    <td>${product.sales}</td>
                    <td>¥${product.revenue.toFixed(2)}</td>
                    <td>${product.stock}</td>
                    <td><span class="text-success">+${product.growth}%</span></td>
                `;
                tbody.appendChild(row);
            });
        }

        // 其他数据加载函数（模拟实现）
        function loadSalesTrendData() {
            showLoading();
            setTimeout(() => {
                // 创建销售趋势图表
                createSalesTrendCharts();
                hideLoading();
            }, 1000);
        }

        function loadMemberAnalysisData() {
            showLoading();
            setTimeout(() => {
                // 更新会员统计
                document.getElementById('total-members').textContent = '1,256';
                document.getElementById('active-members').textContent = '892';
                document.getElementById('avg-member-orders').textContent = '3.2';
                document.getElementById('avg-member-points').textContent = '1,580';
                
                // 创建会员分析图表
                createMemberAnalysisCharts();
                hideLoading();
            }, 1000);
        }

        function loadCategoryAnalysisData() {
            showLoading();
            setTimeout(() => {
                createCategoryAnalysisChart();
                hideLoading();
            }, 1000);
        }

        function loadPointsAnalysisData() {
            showLoading();
            setTimeout(() => {
                // 更新积分统计
                document.getElementById('total-points-issued').textContent = '125,680';
                document.getElementById('total-points-used').textContent = '89,450';
                document.getElementById('points-balance').textContent = '36,230';
                document.getElementById('points-usage-rate').textContent = '71.2%';
                
                // 创建积分分析图表
                createPointsAnalysisCharts();
                hideLoading();
            }, 1000);
        }

        // 创建各种图表的函数
        function createSalesTrendCharts() {
            // 销售额趋势图
            const salesCtx = document.getElementById('salesTrendChart').getContext('2d');
            if (charts.salesTrend) charts.salesTrend.destroy();
            charts.salesTrend = new Chart(salesCtx, {
                type: 'line',
                data: {
                    labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
                    datasets: [{
                        label: '销售额',
                        data: [65000, 59000, 80000, 81000, 56000, 95000],
                        borderColor: 'rgb(75, 192, 192)',
                        tension: 0.1
                    }]
                },
                options: { responsive: true }
            });

            // 订单量趋势图
            const orderCtx = document.getElementById('orderTrendChart').getContext('2d');
            if (charts.orderTrend) charts.orderTrend.destroy();
            charts.orderTrend = new Chart(orderCtx, {
                type: 'bar',
                data: {
                    labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
                    datasets: [{
                        label: '订单量',
                        data: [1200, 1900, 3000, 2500, 2200, 3000],
                        backgroundColor: 'rgba(54, 162, 235, 0.8)'
                    }]
                },
                options: { responsive: true }
            });

            // 客单价趋势图
            const avgCtx = document.getElementById('avgOrderValueChart').getContext('2d');
            if (charts.avgOrderValue) charts.avgOrderValue.destroy();
            charts.avgOrderValue = new Chart(avgCtx, {
                type: 'line',
                data: {
                    labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
                    datasets: [{
                        label: '客单价',
                        data: [54.2, 31.1, 26.7, 32.4, 25.5, 31.7],
                        borderColor: 'rgb(255, 99, 132)',
                        tension: 0.1
                    }]
                },
                options: { responsive: true }
            });

            // 时段分布图
            const hourlyCtx = document.getElementById('hourlyDistributionChart').getContext('2d');
            if (charts.hourlyDistribution) charts.hourlyDistribution.destroy();
            charts.hourlyDistribution = new Chart(hourlyCtx, {
                type: 'radar',
                data: {
                    labels: ['8-10时', '10-12时', '12-14时', '14-16时', '16-18时', '18-20时', '20-22时'],
                    datasets: [{
                        label: '销售分布',
                        data: [15, 25, 35, 20, 30, 45, 25],
                        backgroundColor: 'rgba(255, 99, 132, 0.2)',
                        borderColor: 'rgb(255, 99, 132)'
                    }]
                },
                options: { responsive: true }
            });
        }

        function createMemberAnalysisCharts() {
            // 会员等级分布
            const levelCtx = document.getElementById('memberLevelChart').getContext('2d');
            if (charts.memberLevel) charts.memberLevel.destroy();
            charts.memberLevel = new Chart(levelCtx, {
                type: 'pie',
                data: {
                    labels: ['普通会员', '银卡会员', '金卡会员', '钻石会员'],
                    datasets: [{
                        data: [60, 25, 12, 3],
                        backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0']
                    }]
                },
                options: { responsive: true }
            });

            // 会员消费分布
            const spendingCtx = document.getElementById('memberSpendingChart').getContext('2d');
            if (charts.memberSpending) charts.memberSpending.destroy();
            charts.memberSpending = new Chart(spendingCtx, {
                type: 'bar',
                data: {
                    labels: ['0-100', '100-500', '500-1000', '1000-2000', '2000+'],
                    datasets: [{
                        label: '会员数量',
                        data: [320, 450, 280, 150, 56],
                        backgroundColor: 'rgba(75, 192, 192, 0.8)'
                    }]
                },
                options: { responsive: true }
            });
        }

        function createCategoryAnalysisChart() {
            const ctx = document.getElementById('categoryStatsChart').getContext('2d');
            if (charts.categoryStats) charts.categoryStats.destroy();
            charts.categoryStats = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: ['食品', '饮料', '日用品', '服装', '电子产品', '图书', '玩具', '其他'],
                    datasets: [{
                        label: '销售额',
                        data: [45000, 32000, 28000, 15000, 12000, 8000, 6000, 4000],
                        backgroundColor: 'rgba(54, 162, 235, 0.8)'
                    }]
                },
                options: { responsive: true }
            });
        }

        function createPointsAnalysisCharts() {
            // 积分趋势图
            const trendCtx = document.getElementById('pointsTrendChart').getContext('2d');
            if (charts.pointsTrend) charts.pointsTrend.destroy();
            charts.pointsTrend = new Chart(trendCtx, {
                type: 'line',
                data: {
                    labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
                    datasets: [{
                        label: '积分发放',
                        data: [12000, 19000, 15000, 25000, 22000, 30000],
                        borderColor: 'rgb(75, 192, 192)',
                        tension: 0.1
                    }, {
                        label: '积分使用',
                        data: [8000, 12000, 10000, 18000, 16000, 21000],
                        borderColor: 'rgb(255, 99, 132)',
                        tension: 0.1
                    }]
                },
                options: { responsive: true }
            });

            // 积分来源分布
            const sourceCtx = document.getElementById('pointsSourceChart').getContext('2d');
            if (charts.pointsSource) charts.pointsSource.destroy();
            charts.pointsSource = new Chart(sourceCtx, {
                type: 'doughnut',
                data: {
                    labels: ['购物获得', '双倍积分日', '签到奖励', '推荐奖励', '其他'],
                    datasets: [{
                        data: [65, 20, 8, 5, 2],
                        backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF']
                    }]
                },
                options: { responsive: true }
            });
        }

        // 刷新函数
        function refreshOverview() {
            loadOverviewData();
        }

        function refreshPromotionAnalysis() {
            loadPromotionAnalysis();
        }

        function refreshHotProducts() {
            loadHotProductsData();
        }

        function refreshSalesTrend() {
            loadSalesTrendData();
        }

        function refreshMemberAnalysis() {
            loadMemberAnalysisData();
        }

        function refreshCategoryAnalysis() {
            loadCategoryAnalysisData();
        }

        function refreshPointsAnalysis() {
            loadPointsAnalysisData();
        }
    </script>
</body>
</html>