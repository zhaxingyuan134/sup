<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>会员分级管理 - 超市管理系统</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    
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
            --gold-gradient: linear-gradient(135deg, #ffd700 0%, #ffb347 100%);
            --silver-gradient: linear-gradient(135deg, #c0c0c0 0%, #a8a8a8 100%);
            --bronze-gradient: linear-gradient(135deg, #cd7f32 0%, #b8860b 100%);
            --diamond-gradient: linear-gradient(135deg, #b9f2ff 0%, #0099cc 100%);
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

        .level-card {
            background: white;
            border-radius: 20px;
            padding: 25px;
            margin-bottom: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            border: 1px solid rgba(255,255,255,0.2);
            transition: all 0.3s ease;
        }

        .level-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 40px rgba(0,0,0,0.15);
        }

        .level-badge {
            font-size: 1.1em;
            padding: 10px 20px;
            border-radius: 25px;
            font-weight: 600;
            color: white;
            display: inline-block;
            margin-bottom: 15px;
        }

        .level-bronze { background: var(--bronze-gradient); }
        .level-silver { background: var(--silver-gradient); }
        .level-gold { background: var(--gold-gradient); }
        .level-platinum { background: var(--silver-gradient); }
        .level-diamond { background: var(--diamond-gradient); }

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

        .content-section {
            display: none;
        }

        .content-section.active {
            display: block;
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

        .modal-content {
            border-radius: 20px;
            border: none;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
        }

        .modal-header {
            background: var(--purple-gradient);
            color: white;
            border-radius: 20px 20px 0 0;
            border-bottom: none;
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

        .upgrade-rule-card {
            background: white;
            border-radius: 20px;
            padding: 25px;
            margin-bottom: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            border: 1px solid rgba(255,255,255,0.2);
        }

        .benefit-item {
            background: #f8f9fa;
            border-radius: 12px;
            padding: 15px;
            margin-bottom: 10px;
            border-left: 4px solid var(--blue-gradient);
        }

        .progress-custom {
            height: 8px;
            border-radius: 10px;
            background-color: #e9ecef;
        }

        .progress-bar-custom {
            border-radius: 10px;
            background: var(--green-gradient);
        }

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
            <h4><i class="fas fa-users-cog me-2"></i>会员分级管理</h4>
        </div>
        
        <div class="p-3">
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a class="nav-link active" href="#" data-section="overview">
                        <i class="fas fa-tachometer-alt"></i>概览统计
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" data-section="levels">
                        <i class="fas fa-layer-group"></i>等级管理
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" data-section="upgrade-rules">
                        <i class="fas fa-arrow-up"></i>升级规则
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" data-section="benefits">
                        <i class="fas fa-gift"></i>权益配置
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" data-section="auto-upgrade">
                        <i class="fas fa-magic"></i>自动升级
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" data-section="member-list">
                        <i class="fas fa-users"></i>会员列表
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
                    <a class="nav-link" href="${pageContext.request.contextPath}/manager/data-analysis">
                        <i class="fas fa-chart-bar"></i>数据分析
                    </a>
                </li>
            </ul>
        </div>
    </nav>

    <!-- 主内容区 -->
    <div class="main-content">
        <!-- 概览统计 -->
        <div id="overview-section" class="content-section active">
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1><i class="fas fa-tachometer-alt me-3"></i>会员分级概览</h1>
                        <p class="text-muted mb-0">查看会员等级分布和升级趋势</p>
                    </div>
                    <button type="button" class="btn btn-primary-custom btn-custom" onclick="refreshOverview()">
                        <i class="fas fa-sync-alt me-2"></i>刷新数据
                    </button>
                </div>
            </div>

            <!-- 统计卡片 -->
            <div class="stats-grid">
                <div class="stats-card">
                    <div class="stats-icon" style="background: var(--blue-gradient);">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="stats-number text-primary" id="total-members">0</div>
                    <div class="stats-label">总会员数</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: var(--green-gradient);">
                        <i class="fas fa-user-check"></i>
                    </div>
                    <div class="stats-number text-success" id="active-members">0</div>
                    <div class="stats-label">活跃会员</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: var(--orange-gradient);">
                        <i class="fas fa-arrow-up"></i>
                    </div>
                    <div class="stats-number text-warning" id="monthly-upgrades">0</div>
                    <div class="stats-label">本月升级</div>
                </div>
                <div class="stats-card">
                    <div class="stats-icon" style="background: var(--red-gradient);">
                        <i class="fas fa-crown"></i>
                    </div>
                    <div class="stats-number text-danger" id="vip-members">0</div>
                    <div class="stats-label">VIP会员</div>
                </div>
            </div>

            <!-- 图表区域 -->
            <div class="row">
                <div class="col-lg-6">
                    <div class="chart-container">
                        <h5 class="chart-title">会员等级分布</h5>
                        <canvas id="levelDistributionChart" width="400" height="300"></canvas>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="chart-container">
                        <h5 class="chart-title">升级趋势（最近6个月）</h5>
                        <canvas id="upgradeTrendChart" width="400" height="300"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- 等级管理 -->
        <div id="levels-section" class="content-section">
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1><i class="fas fa-layer-group me-3"></i>等级管理</h1>
                        <p class="text-muted mb-0">管理会员等级设置和配置</p>
                    </div>
                    <div>
                        <button type="button" class="btn btn-success-custom btn-custom me-2" onclick="showCreateLevelModal()">
                            <i class="fas fa-plus me-2"></i>创建等级
                        </button>
                        <button type="button" class="btn btn-primary-custom btn-custom" onclick="refreshLevels()">
                            <i class="fas fa-sync-alt me-2"></i>刷新
                        </button>
                    </div>
                </div>
            </div>

            <!-- 等级列表 -->
            <div id="levels-container">
                <!-- 动态加载等级卡片 -->
            </div>
        </div>

        <!-- 升级规则 -->
        <div id="upgrade-rules-section" class="content-section">
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1><i class="fas fa-arrow-up me-3"></i>升级规则</h1>
                        <p class="text-muted mb-0">设置会员等级升级条件和规则</p>
                    </div>
                    <button type="button" class="btn btn-primary-custom btn-custom" onclick="saveUpgradeRules()">
                        <i class="fas fa-save me-2"></i>保存规则
                    </button>
                </div>
            </div>

            <!-- 升级规则配置 -->
            <div class="upgrade-rule-card">
                <h4><i class="fas fa-cogs me-2"></i>全局升级设置</h4>
                <div class="row">
                    <div class="col-md-6">
                        <div class="mb-3">
                            <label class="form-label">自动升级开关</label>
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="auto-upgrade-enabled" checked>
                                <label class="form-check-label" for="auto-upgrade-enabled">
                                    启用自动升级
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="mb-3">
                            <label class="form-label">升级检查频率</label>
                            <select class="form-select" id="upgrade-frequency">
                                <option value="daily">每日检查</option>
                                <option value="weekly">每周检查</option>
                                <option value="monthly">每月检查</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 等级升级条件 -->
            <div id="upgrade-rules-container">
                <!-- 动态加载升级规则 -->
            </div>
        </div>

        <!-- 权益配置 -->
        <div id="benefits-section" class="content-section">
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1><i class="fas fa-gift me-3"></i>权益配置</h1>
                        <p class="text-muted mb-0">为不同等级配置专属权益和优惠</p>
                    </div>
                    <button type="button" class="btn btn-primary-custom btn-custom" onclick="saveBenefits()">
                        <i class="fas fa-save me-2"></i>保存配置
                    </button>
                </div>
            </div>

            <!-- 权益配置列表 -->
            <div id="benefits-container">
                <!-- 动态加载权益配置 -->
            </div>
        </div>

        <!-- 自动升级 -->
        <div id="auto-upgrade-section" class="content-section">
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1><i class="fas fa-magic me-3"></i>自动升级管理</h1>
                        <p class="text-muted mb-0">管理自动升级任务和批量升级操作</p>
                    </div>
                    <div>
                        <button type="button" class="btn btn-warning-custom btn-custom me-2" onclick="runAutoUpgrade()">
                            <i class="fas fa-play me-2"></i>执行升级
                        </button>
                        <button type="button" class="btn btn-primary-custom btn-custom" onclick="refreshAutoUpgrade()">
                            <i class="fas fa-sync-alt me-2"></i>刷新
                        </button>
                    </div>
                </div>
            </div>

            <!-- 自动升级状态 -->
            <div class="upgrade-rule-card">
                <h4><i class="fas fa-info-circle me-2"></i>升级状态</h4>
                <div class="row">
                    <div class="col-md-3">
                        <div class="text-center">
                            <h3 class="text-primary" id="pending-upgrades">0</h3>
                            <p class="text-muted">待升级会员</p>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="text-center">
                            <h3 class="text-success" id="completed-upgrades">0</h3>
                            <p class="text-muted">已完成升级</p>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="text-center">
                            <h3 class="text-warning" id="failed-upgrades">0</h3>
                            <p class="text-muted">升级失败</p>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="text-center">
                            <h3 class="text-info" id="last-upgrade-time">--</h3>
                            <p class="text-muted">最后执行时间</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 升级日志 -->
            <div class="upgrade-rule-card">
                <h4><i class="fas fa-history me-2"></i>升级日志</h4>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>时间</th>
                                <th>会员</th>
                                <th>原等级</th>
                                <th>新等级</th>
                                <th>升级原因</th>
                                <th>状态</th>
                            </tr>
                        </thead>
                        <tbody id="upgrade-log-table">
                            <!-- 动态加载升级日志 -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- 会员列表 -->
        <div id="member-list-section" class="content-section">
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1><i class="fas fa-users me-3"></i>会员列表</h1>
                        <p class="text-muted mb-0">查看和管理所有会员信息</p>
                    </div>
                    <div>
                        <select class="form-select me-2" id="level-filter" style="display: inline-block; width: 150px;">
                            <option value="">所有等级</option>
                            <option value="BRONZE">铜卡会员</option>
                            <option value="SILVER">银卡会员</option>
                            <option value="GOLD">金卡会员</option>
                            <option value="PLATINUM">白金会员</option>
                            <option value="DIAMOND">钻石会员</option>
                        </select>
                        <button type="button" class="btn btn-primary-custom btn-custom" onclick="refreshMemberList()">
                            <i class="fas fa-sync-alt me-2"></i>刷新
                        </button>
                    </div>
                </div>
            </div>

            <!-- 会员列表表格 -->
            <div class="upgrade-rule-card">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>会员ID</th>
                                <th>姓名</th>
                                <th>手机号</th>
                                <th>等级</th>
                                <th>积分</th>
                                <th>消费金额</th>
                                <th>注册时间</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody id="member-list-table">
                            <!-- 动态加载会员列表 -->
                        </tbody>
                    </table>
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

    <script>
        // 全局变量
        let charts = {};
        let currentLevels = [];
        let currentMembers = [];
        
        // 页面加载完成后初始化
        $(document).ready(function() {
            initializePage();
            loadOverviewData();
        });

        // 初始化页面
        function initializePage() {
            // 侧边栏导航切换
            $('.sidebar .nav-link').click(function(e) {
                e.preventDefault();
                
                // 移除所有活跃状态
                $('.sidebar .nav-link').removeClass('active');
                $('.content-section').removeClass('active');
                
                // 添加当前活跃状态
                $(this).addClass('active');
                
                // 显示对应内容
                const sectionId = $(this).data('section');
                $('#' + sectionId + '-section').addClass('active');
                
                // 根据选择的部分加载对应数据
                loadSectionData(sectionId);
            });
        }

        // 根据选择的部分加载数据
        function loadSectionData(sectionId) {
            switch(sectionId) {
                case 'overview':
                    loadOverviewData();
                    break;
                case 'levels':
                    loadLevelsData();
                    break;
                case 'upgrade-rules':
                    loadUpgradeRulesData();
                    break;
                case 'benefits':
                    loadBenefitsData();
                    break;
                case 'auto-upgrade':
                    loadAutoUpgradeData();
                    break;
                case 'member-list':
                    loadMemberListData();
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
            
            // 使用模拟数据
            const mockData = {
                totalMembers: 1256,
                activeMembers: 892,
                monthlyUpgrades: 45,
                vipMembers: 123,
                levelDistribution: {
                    labels: ['铜卡会员', '银卡会员', '金卡会员', '白金会员', '钻石会员'],
                    data: [650, 350, 180, 56, 20]
                },
                upgradeTrend: {
                    labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
                    data: [25, 32, 28, 45, 38, 52]
                }
            };
            
            setTimeout(() => {
                updateOverviewStats(mockData);
                createOverviewCharts(mockData);
                hideLoading();
            }, 800);
        }

        // 更新概览统计数据
        function updateOverviewStats(data) {
            document.getElementById('total-members').textContent = data.totalMembers || 0;
            document.getElementById('active-members').textContent = data.activeMembers || 0;
            document.getElementById('monthly-upgrades').textContent = data.monthlyUpgrades || 0;
            document.getElementById('vip-members').textContent = data.vipMembers || 0;
        }

        // 创建概览图表
        function createOverviewCharts(data) {
            // 等级分布饼图
            const levelCtx = document.getElementById('levelDistributionChart').getContext('2d');
            if (charts.levelDistribution) {
                charts.levelDistribution.destroy();
            }
            charts.levelDistribution = new Chart(levelCtx, {
                type: 'doughnut',
                data: {
                    labels: data.levelDistribution.labels || [],
                    datasets: [{
                        data: data.levelDistribution.data || [],
                        backgroundColor: [
                            '#cd7f32',
                            '#c0c0c0',
                            '#ffd700',
                            '#e5e4e2',
                            '#b9f2ff'
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

            // 升级趋势图
            const trendCtx = document.getElementById('upgradeTrendChart').getContext('2d');
            if (charts.upgradeTrend) {
                charts.upgradeTrend.destroy();
            }
            charts.upgradeTrend = new Chart(trendCtx, {
                type: 'line',
                data: {
                    labels: data.upgradeTrend.labels || [],
                    datasets: [{
                        label: '升级人数',
                        data: data.upgradeTrend.data || [],
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
        }

        // 加载等级数据
        function loadLevelsData() {
            showLoading();
            
            // 模拟数据
            setTimeout(() => {
                const mockLevels = [
                    {
                        id: 1,
                        levelName: '铜卡会员',
                        levelCode: 'BRONZE',
                        minPoints: 0,
                        maxPoints: 999,
                        pointsMultiplier: 1.0,
                        discountRate: 0,
                        memberCount: 650,
                        description: '新注册会员的起始等级'
                    },
                    {
                        id: 2,
                        levelName: '银卡会员',
                        levelCode: 'SILVER',
                        minPoints: 1000,
                        maxPoints: 4999,
                        pointsMultiplier: 1.2,
                        discountRate: 5,
                        memberCount: 350,
                        description: '消费达到一定金额的会员等级'
                    },
                    {
                        id: 3,
                        levelName: '金卡会员',
                        levelCode: 'GOLD',
                        minPoints: 5000,
                        maxPoints: 19999,
                        pointsMultiplier: 1.5,
                        discountRate: 10,
                        memberCount: 180,
                        description: '高消费会员享受更多优惠'
                    },
                    {
                        id: 4,
                        levelName: '白金会员',
                        levelCode: 'PLATINUM',
                        minPoints: 20000,
                        maxPoints: 49999,
                        pointsMultiplier: 2.0,
                        discountRate: 15,
                        memberCount: 56,
                        description: 'VIP会员享受专属服务'
                    },
                    {
                        id: 5,
                        levelName: '钻石会员',
                        levelCode: 'DIAMOND',
                        minPoints: 50000,
                        maxPoints: null,
                        pointsMultiplier: 3.0,
                        discountRate: 20,
                        memberCount: 20,
                        description: '最高等级会员，享受所有特权'
                    }
                ];
                
                currentLevels = mockLevels;
                displayLevels(mockLevels);
                hideLoading();
            }, 1000);
        }

        // 显示等级列表
        function displayLevels(levels) {
            const container = document.getElementById('levels-container');
            container.innerHTML = '';
            
            levels.forEach(level => {
                const levelCard = document.createElement('div');
                levelCard.className = 'level-card';
                
                const levelClass = `level-${level.levelCode.toLowerCase()}`;
                
                levelCard.innerHTML = `
                    <div class="row align-items-center">
                        <div class="col-md-3">
                            <span class="level-badge ${levelClass}">${level.levelName}</span>
                            <div class="mt-2">
                                <small class="text-muted">代码: ${level.levelCode}</small>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div>
                                <strong>积分范围</strong><br>
                                <span class="text-muted">
                                    ${level.minPoints} - ${level.maxPoints || '无上限'}
                                </span>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div>
                                <strong>会员数量</strong><br>
                                <span class="text-primary">${level.memberCount || 0}</span>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div>
                                <strong>积分倍数</strong><br>
                                <span class="text-success">${level.pointsMultiplier}x</span>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div>
                                <strong>折扣率</strong><br>
                                <span class="text-warning">${level.discountRate}%</span>
                            </div>
                        </div>
                        <div class="col-md-1">
                            <div class="dropdown">
                                <button class="btn btn-outline-secondary btn-sm dropdown-toggle" type="button" data-bs-toggle="dropdown">
                                    操作
                                </button>
                                <ul class="dropdown-menu">
                                    <li><a class="dropdown-item" href="#" onclick="editLevel(${level.id})">
                                        <i class="fas fa-edit me-2"></i>编辑
                                    </a></li>
                                    <li><a class="dropdown-item text-danger" href="#" onclick="deleteLevel(${level.id})">
                                        <i class="fas fa-trash me-2"></i>删除
                                    </a></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    ${level.description ? `<div class="row mt-2"><div class="col-12"><small class="text-muted">${level.description}</small></div></div>` : ''}
                `;
                
                container.appendChild(levelCard);
            });
        }

        // 其他数据加载函数（模拟实现）
        function loadUpgradeRulesData() {
            showLoading();
            setTimeout(() => {
                displayUpgradeRules();
                hideLoading();
            }, 500);
        }

        function loadBenefitsData() {
            showLoading();
            setTimeout(() => {
                displayBenefits();
                hideLoading();
            }, 500);
        }

        function loadAutoUpgradeData() {
            showLoading();
            setTimeout(() => {
                updateAutoUpgradeStats();
                loadUpgradeLog();
                hideLoading();
            }, 500);
        }

        function loadMemberListData() {
            showLoading();
            setTimeout(() => {
                displayMemberList();
                hideLoading();
            }, 1000);
        }

        // 显示升级规则
        function displayUpgradeRules() {
            const container = document.getElementById('upgrade-rules-container');
            container.innerHTML = '';
            
            currentLevels.forEach(level => {
                if (level.levelCode === 'BRONZE') return; // 铜卡不需要升级规则
                
                const ruleCard = document.createElement('div');
                ruleCard.className = 'upgrade-rule-card';
                
                ruleCard.innerHTML = `
                    <h5><i class="fas fa-arrow-up me-2"></i>升级到 ${level.levelName}</h5>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label class="form-label">最低积分要求</label>
                                <input type="number" class="form-control" value="${level.minPoints}" readonly>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label class="form-label">最低消费金额</label>
                                <input type="number" class="form-control" value="${level.minPoints * 10}" readonly>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label class="form-label">升级周期（天）</label>
                                <input type="number" class="form-control" value="30">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label class="form-label">是否启用</label>
                                <div class="form-check form-switch">
                                    <input class="form-check-input" type="checkbox" checked>
                                    <label class="form-check-label">启用此升级规则</label>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                
                container.appendChild(ruleCard);
            });
        }

        // 显示权益配置
        function displayBenefits() {
            const container = document.getElementById('benefits-container');
            container.innerHTML = '';
            
            currentLevels.forEach(level => {
                const benefitCard = document.createElement('div');
                benefitCard.className = 'upgrade-rule-card';
                
                const levelClass = `level-${level.levelCode.toLowerCase()}`;
                
                benefitCard.innerHTML = `
                    <h5><span class="level-badge ${levelClass}">${level.levelName}</span> 专属权益</h5>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="benefit-item">
                                <strong><i class="fas fa-coins me-2"></i>积分倍数</strong>
                                <p class="mb-0">购物获得 ${level.pointsMultiplier}x 积分</p>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="benefit-item">
                                <strong><i class="fas fa-percentage me-2"></i>专属折扣</strong>
                                <p class="mb-0">享受 ${level.discountRate}% 购物折扣</p>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="benefit-item">
                                <strong><i class="fas fa-gift me-2"></i>生日特权</strong>
                                <p class="mb-0">生日月享受额外优惠</p>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="benefit-item">
                                <strong><i class="fas fa-shipping-fast me-2"></i>免费配送</strong>
                                <p class="mb-0">${level.levelCode === 'DIAMOND' || level.levelCode === 'PLATINUM' ? '享受免费配送服务' : '满额免费配送'}</p>
                            </div>
                        </div>
                    </div>
                `;
                
                container.appendChild(benefitCard);
            });
        }

        // 更新自动升级统计
        function updateAutoUpgradeStats() {
            document.getElementById('pending-upgrades').textContent = '23';
            document.getElementById('completed-upgrades').textContent = '156';
            document.getElementById('failed-upgrades').textContent = '2';
            document.getElementById('last-upgrade-time').textContent = '2小时前';
        }

        // 加载升级日志
        function loadUpgradeLog() {
            const tbody = document.getElementById('upgrade-log-table');
            tbody.innerHTML = '';
            
            const mockLogs = [
                { time: '2024-01-15 10:30', member: '张三', oldLevel: '铜卡', newLevel: '银卡', reason: '积分达标', status: '成功' },
                { time: '2024-01-15 09:15', member: '李四', oldLevel: '银卡', newLevel: '金卡', reason: '消费达标', status: '成功' },
                { time: '2024-01-14 16:45', member: '王五', oldLevel: '金卡', newLevel: '白金', reason: '手动升级', status: '成功' },
                { time: '2024-01-14 14:20', member: '赵六', oldLevel: '铜卡', newLevel: '银卡', reason: '积分达标', status: '失败' }
            ];
            
            mockLogs.forEach(log => {
                const row = document.createElement('tr');
                const statusClass = log.status === '成功' ? 'text-success' : 'text-danger';
                
                row.innerHTML = `
                    <td>${log.time}</td>
                    <td>${log.member}</td>
                    <td><span class="badge bg-secondary">${log.oldLevel}</span></td>
                    <td><span class="badge bg-primary">${log.newLevel}</span></td>
                    <td>${log.reason}</td>
                    <td><span class="${statusClass}">${log.status}</span></td>
                `;
                tbody.appendChild(row);
            });
        }

        // 显示会员列表
        function displayMemberList() {
            const tbody = document.getElementById('member-list-table');
            tbody.innerHTML = '';
            
            const mockMembers = [
                { id: 1001, name: '张三', phone: '138****1234', level: 'SILVER', points: 1580, spending: 15800, registerTime: '2023-06-15' },
                { id: 1002, name: '李四', phone: '139****5678', level: 'GOLD', points: 8900, spending: 89000, registerTime: '2023-03-20' },
                { id: 1003, name: '王五', phone: '136****9012', level: 'PLATINUM', points: 25600, spending: 256000, registerTime: '2022-12-10' },
                { id: 1004, name: '赵六', phone: '137****3456', level: 'BRONZE', points: 450, spending: 4500, registerTime: '2024-01-08' },
                { id: 1005, name: '钱七', phone: '135****7890', level: 'DIAMOND', points: 68900, spending: 689000, registerTime: '2022-08-15' }
            ];
            
            mockMembers.forEach(member => {
                const row = document.createElement('tr');
                const levelNames = {
                    'BRONZE': '铜卡会员',
                    'SILVER': '银卡会员',
                    'GOLD': '金卡会员',
                    'PLATINUM': '白金会员',
                    'DIAMOND': '钻石会员'
                };
                const levelClass = `level-${member.level.toLowerCase()}`;
                
                row.innerHTML = `
                    <td>${member.id}</td>
                    <td>${member.name}</td>
                    <td>${member.phone}</td>
                    <td><span class="level-badge ${levelClass}">${levelNames[member.level]}</span></td>
                    <td>${member.points}</td>
                    <td>¥${member.spending.toFixed(2)}</td>
                    <td>${member.registerTime}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="showUpgradeModal(${member.id})">
                            <i class="fas fa-arrow-up"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-info" onclick="viewMemberDetail(${member.id})">
                            <i class="fas fa-eye"></i>
                        </button>
                    </td>
                `;
                tbody.appendChild(row);
            });
        }

        // 刷新函数
        function refreshOverview() {
            loadOverviewData();
        }

        function refreshLevels() {
            loadLevelsData();
        }

        function refreshAutoUpgrade() {
            loadAutoUpgradeData();
        }

        function refreshMemberList() {
            loadMemberListData();
        }

        // 其他操作函数（模拟实现）
        function showCreateLevelModal() {
            alert('创建等级功能正在开发中...');
        }

        function editLevel(levelId) {
            alert('编辑等级功能正在开发中...');
        }

        function deleteLevel(levelId) {
            if (confirm('确定要删除这个等级吗？')) {
                alert('删除等级功能正在开发中...');
            }
        }

        function saveUpgradeRules() {
            alert('保存升级规则功能正在开发中...');
        }

        function saveBenefits() {
            alert('保存权益配置功能正在开发中...');
        }

        function runAutoUpgrade() {
            if (confirm('确定要执行自动升级吗？')) {
                alert('自动升级功能正在开发中...');
            }
        }

        function showUpgradeModal(memberId) {
            alert('手动升级功能正在开发中...');
        }

        function viewMemberDetail(memberId) {
            alert('查看会员详情功能正在开发中...');
        }
    </script>
</body>
</html>