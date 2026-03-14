<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>商品热销分析 - 超市管理系统</title>
    
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
            --primary-color: #667eea;
            --secondary-color: #764ba2;
            --success-color: #28a745;
            --warning-color: #ffc107;
            --danger-color: #dc3545;
            --info-color: #17a2b8;
            --light-color: #f8f9fa;
            --dark-color: #343a40;
            --primary-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            --success-gradient: linear-gradient(135deg, #56ab2f 0%, #a8e6cf 100%);
            --warning-gradient: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            --info-gradient: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        body {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
        }

        .main-container {
            display: flex;
            min-height: 100vh;
        }

        /* 侧边栏样式 */
        .sidebar {
            width: 280px;
            background: var(--primary-gradient);
            color: white;
            padding: 0;
            box-shadow: 2px 0 10px rgba(0,0,0,0.1);
            position: fixed;
            height: 100vh;
            overflow-y: auto;
        }

        .sidebar-header {
            padding: 2rem 1.5rem;
            border-bottom: 1px solid rgba(255,255,255,0.2);
            text-align: center;
        }

        .sidebar-header h3 {
            margin: 0;
            font-weight: 600;
            font-size: 1.4rem;
        }

        .sidebar-nav {
            padding: 1rem 0;
        }

        .nav-link {
            color: rgba(255,255,255,0.8) !important;
            padding: 0.8rem 1.5rem;
            border: none;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
        }

        .nav-link:hover, .nav-link.active {
            background: rgba(255,255,255,0.1);
            color: white !important;
            transform: translateX(5px);
        }

        .nav-link i {
            margin-right: 0.8rem;
            width: 20px;
        }

        /* 主内容区样式 */
        .main-content {
            flex: 1;
            margin-left: 280px;
            padding: 2rem;
        }

        .page-header {
            background: white;
            padding: 2rem;
            border-radius: 15px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
            border-left: 5px solid var(--primary-color);
        }

        .page-header h1 {
            color: var(--dark-color);
            font-weight: 600;
            margin-bottom: 0.5rem;
        }

        /* 统计卡片样式 */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }

        .stats-card {
            background: white;
            padding: 2rem;
            border-radius: 15px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            position: relative;
            overflow: hidden;
        }

        .stats-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 30px rgba(0,0,0,0.15);
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

        .stats-icon {
            width: 60px;
            height: 60px;
            border-radius: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 1rem;
            font-size: 1.5rem;
            color: white;
        }

        .stats-number {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }

        .stats-label {
            color: #6c757d;
            font-size: 0.9rem;
            font-weight: 500;
        }

        .stats-change {
            font-size: 0.8rem;
            margin-top: 0.5rem;
        }

        /* 图表容器样式 */
        .chart-container {
            background: white;
            padding: 2rem;
            border-radius: 15px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
        }

        .chart-title {
            color: var(--dark-color);
            font-weight: 600;
            margin-bottom: 1.5rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid #f8f9fa;
        }

        /* 数据表格样式 */
        .data-table {
            background: white;
            border-radius: 15px;
            overflow: hidden;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        }

        .table {
            margin-bottom: 0;
        }

        .table thead th {
            background: var(--primary-gradient);
            color: white;
            border: none;
            font-weight: 600;
            padding: 1rem;
        }

        .table tbody td {
            padding: 1rem;
            vertical-align: middle;
            border-bottom: 1px solid #f8f9fa;
        }

        .table tbody tr:hover {
            background-color: #f8f9fa;
        }

        /* 排名徽章样式 */
        .rank-badge {
            width: 30px;
            height: 30px;
            border-radius: 50%;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            color: white;
        }

        .rank-1 { background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%); }
        .rank-2 { background: linear-gradient(135deg, #c0c0c0 0%, #e8e8e8 100%); }
        .rank-3 { background: linear-gradient(135deg, #cd7f32 0%, #daa520 100%); }
        .rank-other { background: linear-gradient(135deg, #6c757d 0%, #adb5bd 100%); }

        /* 趋势指标样式 */
        .trend-indicator {
            display: inline-flex;
            align-items: center;
            padding: 0.25rem 0.5rem;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 600;
        }

        .trend-up {
            background: rgba(40, 167, 69, 0.1);
            color: var(--success-color);
        }

        .trend-down {
            background: rgba(220, 53, 69, 0.1);
            color: var(--danger-color);
        }

        .trend-stable {
            background: rgba(108, 117, 125, 0.1);
            color: var(--dark-color);
        }

        /* 筛选器样式 */
        .filter-section {
            background: white;
            padding: 1.5rem;
            border-radius: 15px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
        }

        .filter-title {
            font-weight: 600;
            margin-bottom: 1rem;
            color: var(--dark-color);
        }

        /* 按钮样式 */
        .btn-primary-custom {
            background: var(--primary-gradient);
            border: none;
            border-radius: 10px;
            padding: 0.6rem 1.5rem;
            font-weight: 600;
            transition: all 0.3s ease;
        }

        .btn-primary-custom:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        }

        /* 加载动画 */
        .loading-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(255, 255, 255, 0.9);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 9999;
        }

        .loading-spinner {
            width: 50px;
            height: 50px;
            border: 4px solid #f3f3f3;
            border-top: 4px solid var(--primary-color);
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        /* 响应式设计 */
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
                padding: 1rem;
            }
            
            .stats-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <!-- 加载动画 -->
    <div id="loadingOverlay" class="loading-overlay" style="display: none;">
        <div class="loading-spinner"></div>
    </div>

    <div class="main-container">
        <!-- 侧边栏 -->
        <nav class="sidebar">
            <div class="sidebar-header">
                <h3><i class="fas fa-chart-line me-2"></i>商品分析</h3>
                <p class="mb-0" style="font-size: 0.9rem; opacity: 0.8;">Product Analysis</p>
            </div>
            
            <div class="sidebar-nav">
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link active" href="#overview" data-section="overview">
                            <i class="fas fa-tachometer-alt"></i>分析概览
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#hot-products" data-section="hot-products">
                            <i class="fas fa-fire"></i>热销排行
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#category-analysis" data-section="category-analysis">
                            <i class="fas fa-tags"></i>分类分析
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#sales-trend" data-section="sales-trend">
                            <i class="fas fa-chart-area"></i>销售趋势
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#inventory-analysis" data-section="inventory-analysis">
                            <i class="fas fa-boxes"></i>库存分析
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#profit-analysis" data-section="profit-analysis">
                            <i class="fas fa-dollar-sign"></i>利润分析
                        </a>
                    </li>
                </ul>
                
                <hr style="border-color: rgba(255,255,255,0.2); margin: 1.5rem;">
                
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/manager/dashboard">
                            <i class="fas fa-arrow-left"></i>返回仪表盘
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/manager/data-analysis">
                            <i class="fas fa-chart-bar"></i>数据分析
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
            <!-- 分析概览 -->
            <div id="overview-section" class="content-section">
                <div class="page-header">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h1><i class="fas fa-tachometer-alt me-3"></i>商品销售分析概览</h1>
                            <p class="text-muted mb-0">全面分析商品销售表现和市场趋势</p>
                        </div>
                        <div>
                            <select class="form-select me-3" id="overviewPeriod" style="display: inline-block; width: auto;">
                                <option value="today">今日</option>
                                <option value="week">本周</option>
                                <option value="month" selected>本月</option>
                                <option value="quarter">本季度</option>
                            </select>
                            <button type="button" class="btn btn-primary-custom" onclick="refreshOverview()">
                                <i class="fas fa-sync-alt me-2"></i>刷新数据
                            </button>
                        </div>
                    </div>
                </div>

                <!-- 关键指标统计 -->
                <div class="stats-grid">
                    <div class="stats-card">
                        <div class="stats-icon" style="background: var(--success-gradient);">
                            <i class="fas fa-shopping-cart"></i>
                        </div>
                        <div class="stats-number text-success" id="totalSales">0</div>
                        <div class="stats-label">总销售额</div>
                        <div class="stats-change">
                            <span class="trend-indicator trend-up" id="salesTrend">
                                <i class="fas fa-arrow-up me-1"></i>+12.5%
                            </span>
                        </div>
                    </div>
                    <div class="stats-card">
                        <div class="stats-icon" style="background: var(--info-gradient);">
                            <i class="fas fa-box"></i>
                        </div>
                        <div class="stats-number text-info" id="totalProducts">0</div>
                        <div class="stats-label">销售商品数</div>
                        <div class="stats-change">
                            <span class="trend-indicator trend-up" id="productsTrend">
                                <i class="fas fa-arrow-up me-1"></i>+8.3%
                            </span>
                        </div>
                    </div>
                    <div class="stats-card">
                        <div class="stats-icon" style="background: var(--warning-gradient);">
                            <i class="fas fa-crown"></i>
                        </div>
                        <div class="stats-number text-warning" id="topProductSales">0</div>
                        <div class="stats-label">热销冠军销量</div>
                        <div class="stats-change">
                            <span class="trend-indicator trend-up" id="topProductTrend">
                                <i class="fas fa-arrow-up me-1"></i>+15.2%
                            </span>
                        </div>
                    </div>
                    <div class="stats-card">
                        <div class="stats-icon" style="background: var(--primary-gradient);">
                            <i class="fas fa-percentage"></i>
                        </div>
                        <div class="stats-number text-primary" id="avgMargin">0%</div>
                        <div class="stats-label">平均利润率</div>
                        <div class="stats-change">
                            <span class="trend-indicator trend-stable" id="marginTrend">
                                <i class="fas fa-minus me-1"></i>0.0%
                            </span>
                        </div>
                    </div>
                </div>

                <!-- 图表区域 -->
                <div class="row">
                    <div class="col-lg-8">
                        <div class="chart-container">
                            <h5 class="chart-title">销售趋势分析</h5>
                            <canvas id="salesOverviewChart" width="400" height="200"></canvas>
                        </div>
                    </div>
                    <div class="col-lg-4">
                        <div class="chart-container">
                            <h5 class="chart-title">商品分类占比</h5>
                            <canvas id="categoryPieChart" width="400" height="200"></canvas>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 热销排行 -->
            <div id="hot-products-section" class="content-section" style="display: none;">
                <div class="page-header">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h1><i class="fas fa-fire me-3"></i>热销商品排行榜</h1>
                            <p class="text-muted mb-0">实时追踪最受欢迎的商品销售情况</p>
                        </div>
                        <div>
                            <select class="form-select me-3" id="hotProductsPeriod" style="display: inline-block; width: auto;">
                                <option value="today">今日</option>
                                <option value="week" selected>本周</option>
                                <option value="month">本月</option>
                            </select>
                            <button type="button" class="btn btn-primary-custom" onclick="refreshHotProducts()">
                                <i class="fas fa-sync-alt me-2"></i>刷新数据
                            </button>
                        </div>
                    </div>
                </div>

                <!-- 筛选器 -->
                <div class="filter-section">
                    <div class="filter-title">筛选条件</div>
                    <div class="row">
                        <div class="col-md-3">
                            <label class="form-label">商品分类</label>
                            <select class="form-select" id="categoryFilter">
                                <option value="">全部分类</option>
                                <option value="食品">食品</option>
                                <option value="饮料">饮料</option>
                                <option value="日用品">日用品</option>
                                <option value="生鲜">生鲜</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">价格区间</label>
                            <select class="form-select" id="priceFilter">
                                <option value="">全部价格</option>
                                <option value="0-10">0-10元</option>
                                <option value="10-50">10-50元</option>
                                <option value="50-100">50-100元</option>
                                <option value="100+">100元以上</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">排序方式</label>
                            <select class="form-select" id="sortFilter">
                                <option value="sales">按销量</option>
                                <option value="revenue">按销售额</option>
                                <option value="growth">按增长率</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">&nbsp;</label>
                            <button type="button" class="btn btn-primary-custom d-block w-100" onclick="applyFilters()">
                                <i class="fas fa-filter me-2"></i>应用筛选
                            </button>
                        </div>
                    </div>
                </div>

                <!-- 热销商品表格 -->
                <div class="data-table">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>排名</th>
                                <th>商品信息</th>
                                <th>分类</th>
                                <th>销量</th>
                                <th>销售额</th>
                                <th>库存</th>
                                <th>增长率</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody id="hotProductsTable">
                            <!-- 动态加载 -->
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- 分类分析 -->
            <div id="category-analysis-section" class="content-section" style="display: none;">
                <div class="page-header">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h1><i class="fas fa-tags me-3"></i>商品分类分析</h1>
                            <p class="text-muted mb-0">深入分析各商品分类的销售表现</p>
                        </div>
                        <button type="button" class="btn btn-primary-custom" onclick="refreshCategoryAnalysis()">
                            <i class="fas fa-sync-alt me-2"></i>刷新数据
                        </button>
                    </div>
                </div>

                <!-- 分类统计图表 -->
                <div class="row">
                    <div class="col-lg-6">
                        <div class="chart-container">
                            <h5 class="chart-title">分类销售额对比</h5>
                            <canvas id="categoryRevenueChart" width="400" height="300"></canvas>
                        </div>
                    </div>
                    <div class="col-lg-6">
                        <div class="chart-container">
                            <h5 class="chart-title">分类销量对比</h5>
                            <canvas id="categorySalesChart" width="400" height="300"></canvas>
                        </div>
                    </div>
                </div>

                <!-- 分类详细数据 -->
                <div class="data-table">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>分类名称</th>
                                <th>商品数量</th>
                                <th>总销量</th>
                                <th>销售额</th>
                                <th>平均价格</th>
                                <th>利润率</th>
                                <th>市场占比</th>
                            </tr>
                        </thead>
                        <tbody id="categoryAnalysisTable">
                            <!-- 动态加载 -->
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- 销售趋势 -->
            <div id="sales-trend-section" class="content-section" style="display: none;">
                <div class="page-header">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h1><i class="fas fa-chart-area me-3"></i>销售趋势分析</h1>
                            <p class="text-muted mb-0">分析商品销售的时间趋势和周期性规律</p>
                        </div>
                        <div>
                            <input type="text" class="form-control me-3" id="trendDateRange" placeholder="选择日期范围" style="display: inline-block; width: 200px;">
                            <button type="button" class="btn btn-primary-custom" onclick="refreshSalesTrend()">
                                <i class="fas fa-sync-alt me-2"></i>刷新数据
                            </button>
                        </div>
                    </div>
                </div>

                <!-- 趋势图表 -->
                <div class="chart-container">
                    <h5 class="chart-title">销售趋势图</h5>
                    <canvas id="salesTrendChart" width="400" height="200"></canvas>
                </div>

                <div class="row">
                    <div class="col-lg-6">
                        <div class="chart-container">
                            <h5 class="chart-title">时段销售分布</h5>
                            <canvas id="hourlyDistributionChart" width="400" height="200"></canvas>
                        </div>
                    </div>
                    <div class="col-lg-6">
                        <div class="chart-container">
                            <h5 class="chart-title">周销售模式</h5>
                            <canvas id="weeklyPatternChart" width="400" height="200"></canvas>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 库存分析 -->
            <div id="inventory-analysis-section" class="content-section" style="display: none;">
                <div class="page-header">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h1><i class="fas fa-boxes me-3"></i>库存分析</h1>
                            <p class="text-muted mb-0">监控库存状况，优化商品配置</p>
                        </div>
                        <button type="button" class="btn btn-primary-custom" onclick="refreshInventoryAnalysis()">
                            <i class="fas fa-sync-alt me-2"></i>刷新数据
                        </button>
                    </div>
                </div>

                <!-- 库存统计 -->
                <div class="stats-grid">
                    <div class="stats-card">
                        <div class="stats-icon" style="background: var(--danger-color);">
                            <i class="fas fa-exclamation-triangle"></i>
                        </div>
                        <div class="stats-number text-danger" id="lowStockCount">0</div>
                        <div class="stats-label">低库存商品</div>
                    </div>
                    <div class="stats-card">
                        <div class="stats-icon" style="background: var(--warning-color);">
                            <i class="fas fa-clock"></i>
                        </div>
                        <div class="stats-number text-warning" id="slowMovingCount">0</div>
                        <div class="stats-label">滞销商品</div>
                    </div>
                    <div class="stats-card">
                        <div class="stats-icon" style="background: var(--success-color);">
                            <i class="fas fa-check-circle"></i>
                        </div>
                        <div class="stats-number text-success" id="optimalStockCount">0</div>
                        <div class="stats-label">库存正常</div>
                    </div>
                    <div class="stats-card">
                        <div class="stats-icon" style="background: var(--info-color);">
                            <i class="fas fa-chart-pie"></i>
                        </div>
                        <div class="stats-number text-info" id="turnoverRate">0</div>
                        <div class="stats-label">平均周转率</div>
                    </div>
                </div>

                <!-- 库存分析表格 -->
                <div class="data-table">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>商品名称</th>
                                <th>分类</th>
                                <th>当前库存</th>
                                <th>安全库存</th>
                                <th>周转率</th>
                                <th>状态</th>
                                <th>建议</th>
                            </tr>
                        </thead>
                        <tbody id="inventoryAnalysisTable">
                            <!-- 动态加载 -->
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- 利润分析 -->
            <div id="profit-analysis-section" class="content-section" style="display: none;">
                <div class="page-header">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h1><i class="fas fa-dollar-sign me-3"></i>利润分析</h1>
                            <p class="text-muted mb-0">分析商品盈利能力和利润贡献</p>
                        </div>
                        <button type="button" class="btn btn-primary-custom" onclick="refreshProfitAnalysis()">
                            <i class="fas fa-sync-alt me-2"></i>刷新数据
                        </button>
                    </div>
                </div>

                <!-- 利润统计 -->
                <div class="row">
                    <div class="col-lg-8">
                        <div class="chart-container">
                            <h5 class="chart-title">利润贡献分析</h5>
                            <canvas id="profitContributionChart" width="400" height="200"></canvas>
                        </div>
                    </div>
                    <div class="col-lg-4">
                        <div class="chart-container">
                            <h5 class="chart-title">利润率分布</h5>
                            <canvas id="profitMarginChart" width="400" height="200"></canvas>
                        </div>
                    </div>
                </div>

                <!-- 利润分析表格 -->
                <div class="data-table">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>商品名称</th>
                                <th>销售额</th>
                                <th>成本</th>
                                <th>毛利润</th>
                                <th>利润率</th>
                                <th>利润贡献</th>
                                <th>评级</th>
                            </tr>
                        </thead>
                        <tbody id="profitAnalysisTable">
                            <!-- 动态加载 -->
                        </tbody>
                    </table>
                </div>
            </div>
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
        let currentSection = 'overview';
        let charts = {};

        // 页面初始化
        $(document).ready(function() {
            initializePage();
            initializeDateRangePicker();
            loadOverviewData();
        });

        // 初始化页面
        function initializePage() {
            // 侧边栏导航
            $('.sidebar .nav-link').click(function(e) {
                e.preventDefault();
                const section = $(this).data('section');
                if (section) {
                    switchSection(section);
                }
            });
        }

        // 切换内容区域
        function switchSection(section) {
            // 更新导航状态
            $('.sidebar .nav-link').removeClass('active');
            $(`.sidebar .nav-link[data-section="${section}"]`).addClass('active');
            
            // 切换内容区域
            $('.content-section').hide();
            $(`#${section}-section`).show();
            
            currentSection = section;
            
            // 加载对应数据
            switch(section) {
                case 'overview':
                    loadOverviewData();
                    break;
                case 'hot-products':
                    loadHotProductsData();
                    break;
                case 'category-analysis':
                    loadCategoryAnalysisData();
                    break;
                case 'sales-trend':
                    loadSalesTrendData();
                    break;
                case 'inventory-analysis':
                    loadInventoryAnalysisData();
                    break;
                case 'profit-analysis':
                    loadProfitAnalysisData();
                    break;
            }
        }

        // 初始化日期范围选择器
        function initializeDateRangePicker() {
            $('#trendDateRange').daterangepicker({
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
                    monthNames: ['一月', '二月', '三月', '四月', '五月', '六月',
                               '七月', '八月', '九月', '十月', '十一月', '十二月'],
                    firstDay: 1
                }
            });
        }

        // 显示加载动画
        function showLoading() {
            $('#loadingOverlay').show();
        }

        // 隐藏加载动画
        function hideLoading() {
            $('#loadingOverlay').hide();
        }

        // 加载概览数据
        function loadOverviewData() {
            showLoading();
            
            // 模拟数据加载
            setTimeout(() => {
                const mockData = {
                    totalSales: 125680.50,
                    totalProducts: 1248,
                    topProductSales: 245,
                    avgMargin: 28.5,
                    salesTrend: [
                        { date: '2024-01-01', sales: 12500 },
                        { date: '2024-01-02', sales: 13200 },
                        { date: '2024-01-03', sales: 11800 },
                        { date: '2024-01-04', sales: 14500 },
                        { date: '2024-01-05', sales: 13900 },
                        { date: '2024-01-06', sales: 15200 },
                        { date: '2024-01-07', sales: 16800 }
                    ],
                    categoryData: [
                        { name: '食品', value: 35 },
                        { name: '饮料', value: 25 },
                        { name: '日用品', value: 20 },
                        { name: '生鲜', value: 15 },
                        { name: '其他', value: 5 }
                    ]
                };
                
                updateOverviewStats(mockData);
                createSalesOverviewChart(mockData.salesTrend);
                createCategoryPieChart(mockData.categoryData);
                hideLoading();
            }, 1000);
        }

        // 更新概览统计
        function updateOverviewStats(data) {
            $('#totalSales').text('¥' + data.totalSales.toLocaleString());
            $('#totalProducts').text(data.totalProducts.toLocaleString());
            $('#topProductSales').text(data.topProductSales);
            $('#avgMargin').text(data.avgMargin + '%');
        }

        // 创建销售概览图表
        function createSalesOverviewChart(data) {
            const ctx = document.getElementById('salesOverviewChart').getContext('2d');
            
            if (charts.salesOverview) {
                charts.salesOverview.destroy();
            }
            
            charts.salesOverview = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: data.map(item => moment(item.date).format('MM-DD')),
                    datasets: [{
                        label: '销售额',
                        data: data.map(item => item.sales),
                        borderColor: '#667eea',
                        backgroundColor: 'rgba(102, 126, 234, 0.1)',
                        borderWidth: 3,
                        fill: true,
                        tension: 0.4
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            display: false
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function(value) {
                                    return '¥' + value.toLocaleString();
                                }
                            }
                        }
                    }
                }
            });
        }

        // 创建分类饼图
        function createCategoryPieChart(data) {
            const ctx = document.getElementById('categoryPieChart').getContext('2d');
            
            if (charts.categoryPie) {
                charts.categoryPie.destroy();
            }
            
            charts.categoryPie = new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: data.map(item => item.name),
                    datasets: [{
                        data: data.map(item => item.value),
                        backgroundColor: [
                            '#667eea',
                            '#764ba2',
                            '#f093fb',
                            '#f5576c',
                            '#4facfe'
                        ]
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'bottom'
                        }
                    }
                }
            });
        }

        // 加载热销商品数据
        function loadHotProductsData() {
            showLoading();
            
            setTimeout(() => {
                const mockProducts = [
                    { rank: 1, name: '可口可乐 330ml', category: '饮料', sales: 245, revenue: 612.5, stock: 156, growth: 15.2 },
                    { rank: 2, name: '康师傅方便面', category: '食品', sales: 198, revenue: 594.0, stock: 89, growth: 12.8 },
                    { rank: 3, name: '农夫山泉 550ml', category: '饮料', sales: 187, revenue: 374.0, stock: 234, growth: 8.9 },
                    { rank: 4, name: '奥利奥饼干', category: '食品', sales: 156, revenue: 468.0, stock: 67, growth: -2.1 },
                    { rank: 5, name: '海飞丝洗发水', category: '日用品', sales: 134, revenue: 1072.0, stock: 45, growth: 22.1 }
                ];
                
                updateHotProductsTable(mockProducts);
                hideLoading();
            }, 800);
        }

        // 更新热销商品表格
        function updateHotProductsTable(products) {
            const tbody = $('#hotProductsTable');
            tbody.empty();
            
            products.forEach(product => {
                const rankClass = product.rank <= 3 ? `rank-${product.rank}` : 'rank-other';
                const trendClass = product.growth > 0 ? 'trend-up' : (product.growth < 0 ? 'trend-down' : 'trend-stable');
                const trendIcon = product.growth > 0 ? 'fa-arrow-up' : (product.growth < 0 ? 'fa-arrow-down' : 'fa-minus');
                
                const row = `
                    <tr>
                        <td>
                            <span class="rank-badge ${rankClass}">${product.rank}</span>
                        </td>
                        <td>
                            <div>
                                <strong>${product.name}</strong>
                                <br><small class="text-muted">ID: P${String(product.rank).padStart(3, '0')}</small>
                            </div>
                        </td>
                        <td><span class="badge bg-secondary">${product.category}</span></td>
                        <td><strong>${product.sales}</strong></td>
                        <td>¥${product.revenue.toFixed(2)}</td>
                        <td>${product.stock}</td>
                        <td>
                            <span class="trend-indicator ${trendClass}">
                                <i class="fas ${trendIcon} me-1"></i>${Math.abs(product.growth)}%
                            </span>
                        </td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary" onclick="viewProductDetail(${product.rank})">
                                <i class="fas fa-eye"></i>
                            </button>
                        </td>
                    </tr>
                `;
                tbody.append(row);
            });
        }

        // 加载分类分析数据
        function loadCategoryAnalysisData() {
            showLoading();
            
            setTimeout(() => {
                const mockData = {
                    categories: [
                        { name: '食品', products: 156, sales: 2450, revenue: 12500, avgPrice: 5.10, margin: 25.5, share: 35.2 },
                        { name: '饮料', products: 89, sales: 1890, revenue: 9450, avgPrice: 5.00, margin: 30.2, share: 26.6 },
                        { name: '日用品', products: 67, sales: 890, revenue: 8900, avgPrice: 10.00, margin: 35.8, share: 25.1 },
                        { name: '生鲜', products: 45, sales: 567, revenue: 4536, avgPrice: 8.00, margin: 20.1, share: 12.8 }
                    ]
                };
                
                updateCategoryAnalysisTable(mockData.categories);
                createCategoryRevenueChart(mockData.categories);
                createCategorySalesChart(mockData.categories);
                hideLoading();
            }, 1000);
        }

        // 更新分类分析表格
        function updateCategoryAnalysisTable(categories) {
            const tbody = $('#categoryAnalysisTable');
            tbody.empty();
            
            categories.forEach(category => {
                const row = `
                    <tr>
                        <td><strong>${category.name}</strong></td>
                        <td>${category.products}</td>
                        <td>${category.sales.toLocaleString()}</td>
                        <td>¥${category.revenue.toLocaleString()}</td>
                        <td>¥${category.avgPrice.toFixed(2)}</td>
                        <td>${category.margin}%</td>
                        <td>${category.share}%</td>
                    </tr>
                `;
                tbody.append(row);
            });
        }

        // 创建分类销售额图表
        function createCategoryRevenueChart(data) {
            const ctx = document.getElementById('categoryRevenueChart').getContext('2d');
            
            if (charts.categoryRevenue) {
                charts.categoryRevenue.destroy();
            }
            
            charts.categoryRevenue = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: data.map(item => item.name),
                    datasets: [{
                        label: '销售额',
                        data: data.map(item => item.revenue),
                        backgroundColor: ['#667eea', '#764ba2', '#f093fb', '#f5576c']
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            display: false
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function(value) {
                                    return '¥' + value.toLocaleString();
                                }
                            }
                        }
                    }
                }
            });
        }

        // 创建分类销量图表
        function createCategorySalesChart(data) {
            const ctx = document.getElementById('categorySalesChart').getContext('2d');
            
            if (charts.categorySales) {
                charts.categorySales.destroy();
            }
            
            charts.categorySales = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: data.map(item => item.name),
                    datasets: [{
                        label: '销量',
                        data: data.map(item => item.sales),
                        backgroundColor: ['#4facfe', '#00f2fe', '#43e97b', '#38f9d7']
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            display: false
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
        }

        // 其他数据加载函数（简化实现）
        function loadSalesTrendData() {
            showLoading();
            setTimeout(() => {
                // 模拟加载销售趋势数据
                hideLoading();
            }, 800);
        }

        function loadInventoryAnalysisData() {
            showLoading();
            setTimeout(() => {
                // 模拟加载库存分析数据
                $('#lowStockCount').text('12');
                $('#slowMovingCount').text('8');
                $('#optimalStockCount').text('156');
                $('#turnoverRate').text('4.2');
                hideLoading();
            }, 800);
        }

        function loadProfitAnalysisData() {
            showLoading();
            setTimeout(() => {
                // 模拟加载利润分析数据
                hideLoading();
            }, 800);
        }

        // 刷新函数
        function refreshOverview() {
            loadOverviewData();
        }

        function refreshHotProducts() {
            loadHotProductsData();
        }

        function refreshCategoryAnalysis() {
            loadCategoryAnalysisData();
        }

        function refreshSalesTrend() {
            loadSalesTrendData();
        }

        function refreshInventoryAnalysis() {
            loadInventoryAnalysisData();
        }

        function refreshProfitAnalysis() {
            loadProfitAnalysisData();
        }

        // 应用筛选
        function applyFilters() {
            const category = $('#categoryFilter').val();
            const price = $('#priceFilter').val();
            const sort = $('#sortFilter').val();
            
            // 根据筛选条件重新加载数据
            loadHotProductsData();
        }

        // 查看商品详情
        function viewProductDetail(productId) {
            alert('查看商品详情功能开发中...');
        }
    </script>
</body>
</html>