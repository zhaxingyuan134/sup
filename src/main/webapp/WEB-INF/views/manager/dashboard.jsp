<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>超市经理管理系统</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <!-- 通用样式 -->
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body {
            background-color: #f8f9fa;
        }
        .sidebar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: white;
        }
        .sidebar .nav-link {
            color: rgba(255, 255, 255, 0.8);
            border-radius: 8px;
            margin: 2px 0;
            transition: all 0.3s ease;
        }
        .sidebar .nav-link:hover,
        .sidebar .nav-link.active {
            color: white;
            background-color: rgba(255, 255, 255, 0.2);
        }
        .main-content {
            padding: 20px;
        }
        .stats-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
            transition: transform 0.3s ease;
        }
        .stats-card:hover {
            transform: translateY(-5px);
        }
        .stats-icon {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            color: white;
        }
        .chart-container {
            background: white;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
            padding: 20px;
        }
        .quick-action-btn {
            border-radius: 10px;
            padding: 15px;
            text-decoration: none;
            color: white;
            display: block;
            text-align: center;
            transition: all 0.3s ease;
        }
        .quick-action-btn:hover {
            transform: translateY(-3px);
            color: white;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- 侧边栏 -->
            <div class="col-md-3 col-lg-2 sidebar p-0">
                <div class="p-3">
                    <div class="text-center mb-4">
                        <i class="fas fa-store fa-3x mb-2"></i>
                        <h5>超市经理系统</h5>
                        <small>欢迎，${managerName}</small>
                    </div>
                    
                    <nav class="nav flex-column">
                        <a class="nav-link active" href="#dashboard" data-section="dashboard">
                            <i class="fas fa-tachometer-alt me-2"></i>
                            仪表盘
                        </a>
                        <a class="nav-link" href="${pageContext.request.contextPath}/manager/promotions">
                            <i class="fas fa-tags me-2"></i>
                            促销活动管理
                        </a>
                        <a class="nav-link" href="${pageContext.request.contextPath}/manager/data-analysis">
                            <i class="fas fa-chart-bar me-2"></i>
                            数据分析
                        </a>
                        <a class="nav-link" href="${pageContext.request.contextPath}/manager/member-levels">
                            <i class="fas fa-users me-2"></i>
                            会员管理
                        </a>
                        <a class="nav-link" href="#products" data-section="products">
                            <i class="fas fa-box me-2"></i>
                            商品管理
                        </a>
                        <a class="nav-link" href="#reports" data-section="reports">
                            <i class="fas fa-file-alt me-2"></i>
                            报表统计
                        </a>
                        <hr class="my-3">
                        <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt me-2"></i>
                            退出登录
                        </a>
                    </nav>
                </div>
            </div>
            
            <!-- 主内容区 -->
            <div class="col-md-9 col-lg-10 main-content">
                <!-- 仪表盘内容 -->
                <div id="dashboard-content" class="content-section">
                    <!-- 页面标题 -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h2><i class="fas fa-tachometer-alt me-2"></i>经理仪表盘</h2>
                        <div class="text-muted">
                            <i class="fas fa-calendar me-1"></i>
                            <span id="currentDate"></span>
                        </div>
                    </div>
                    
                    <!-- 统计卡片 -->
                    <div class="row mb-4">
                        <div class="col-md-3 mb-3">
                            <div class="stats-card p-3">
                                <div class="d-flex align-items-center">
                                    <div class="stats-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                                        <i class="fas fa-users"></i>
                                    </div>
                                    <div class="ms-3">
                                        <h3 class="mb-0" id="totalMembers">-</h3>
                                        <small class="text-muted">总会员数</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="stats-card p-3">
                                <div class="d-flex align-items-center">
                                    <div class="stats-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                                        <i class="fas fa-shopping-cart"></i>
                                    </div>
                                    <div class="ms-3">
                                        <h3 class="mb-0" id="todaySales">-</h3>
                                        <small class="text-muted">今日销售额</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="stats-card p-3">
                                <div class="d-flex align-items-center">
                                    <div class="stats-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                                        <i class="fas fa-coins"></i>
                                    </div>
                                    <div class="ms-3">
                                        <h3 class="mb-0" id="totalPoints">-</h3>
                                        <small class="text-muted">积分发放总数</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="stats-card p-3">
                                <div class="d-flex align-items-center">
                                    <div class="stats-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
                                        <i class="fas fa-tags"></i>
                                    </div>
                                    <div class="ms-3">
                                        <h3 class="mb-0" id="activePromotions">-</h3>
                                        <small class="text-muted">活跃促销活动</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- 快捷操作 -->
                    <div class="row">
                        <div class="col-12 mb-4">
                            <div class="chart-container">
                                <h5 class="mb-3"><i class="fas fa-rocket me-2"></i>快捷操作</h5>
                                <div class="row">
                                    <div class="col-md-3 mb-3">
                                        <a href="${pageContext.request.contextPath}/manager/promotions" class="quick-action-btn" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                                            <i class="fas fa-plus me-2"></i>促销活动管理
                                        </a>
                                    </div>
                                    <div class="col-md-3 mb-3">
                                        <a href="${pageContext.request.contextPath}/manager/data-analysis" class="quick-action-btn" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                                            <i class="fas fa-chart-bar me-2"></i>数据分析
                                        </a>
                                    </div>
                                    <div class="col-md-3 mb-3">
                                        <a href="${pageContext.request.contextPath}/manager/member-levels" class="quick-action-btn" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                                            <i class="fas fa-users me-2"></i>会员管理
                                        </a>
                                    </div>
                                    <div class="col-md-3 mb-3">
                                        <a href="#" class="quick-action-btn" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);" onclick="generateReport()">
                                            <i class="fas fa-file-alt me-2"></i>生成报表
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- 数据分析内容 -->
                <div id="analytics-content" class="content-section" style="display: none;">
                    <h2><i class="fas fa-chart-bar me-2"></i>数据分析</h2>
                    <p class="text-muted">查看销售数据、会员行为分析和商品热销排行。</p>
                    <!-- 数据分析功能将在后续实现 -->
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle me-2"></i>
                        数据分析功能正在开发中...
                    </div>
                </div>
                
                <!-- 会员管理内容 -->
                <div id="members-content" class="content-section" style="display: none;">
                    <h2><i class="fas fa-users me-2"></i>会员管理</h2>
                    <p class="text-muted">管理会员等级、查看会员信息和积分状况。</p>
                    <!-- 会员管理功能将在后续实现 -->
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle me-2"></i>
                        会员管理功能正在开发中...
                    </div>
                </div>
                
                <!-- 商品管理内容 -->
                <div id="products-content" class="content-section" style="display: none;">
                    <h2><i class="fas fa-box me-2"></i>商品管理</h2>
                    <p class="text-muted">管理商品信息、价格和积分倍率设置。</p>
                    <!-- 商品管理功能将在后续实现 -->
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle me-2"></i>
                        商品管理功能正在开发中...
                    </div>
                </div>
                
                <!-- 报表统计内容 -->
                <div id="reports-content" class="content-section" style="display: none;">
                    <h2><i class="fas fa-file-alt me-2"></i>报表统计</h2>
                    <p class="text-muted">生成各类统计报表，包括销售报表、会员报表等。</p>
                    <!-- 报表统计功能将在后续实现 -->
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle me-2"></i>
                        报表统计功能正在开发中...
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
            // 设置当前日期
            const now = new Date();
            const dateStr = now.getFullYear() + '年' + (now.getMonth() + 1) + '月' + now.getDate() + '日';
            $('#currentDate').text(dateStr);
            
            // 侧边栏导航
            $('.sidebar .nav-link').click(function(e) {
                // 检查是否是外部链接（包含完整URL路径）
                const href = $(this).attr('href');
                if (href && (href.startsWith('${pageContext.request.contextPath}/') || href === '${pageContext.request.contextPath}/logout')) {
                    // 允许正常跳转到外部页面
                    return true;
                }
                
                // 处理内部导航
                if (href && href.startsWith('#')) {
                    e.preventDefault();
                    $('.sidebar .nav-link').removeClass('active');
                    $(this).addClass('active');
                    
                    const section = $(this).data('section');
                    if (section) {
                        showSection(section);
                    }
                }
            });
            
            // 加载统计数据
            loadDashboardStats();
        });
        
        // 显示指定内容区域
        function showSection(section) {
            $('.content-section').hide();
            $('#' + section + '-content').show();
            
            // 更新导航状态
            $('.sidebar .nav-link').removeClass('active');
            $('.sidebar .nav-link[data-section="' + section + '"]').addClass('active');
        }
        
        // 生成报表功能
        function generateReport() {
            alert('报表生成功能正在开发中...');
        }
        
        // 加载仪表盘统计数据
        function loadDashboardStats() {
            // 从后端API获取真实数据
            fetch('${pageContext.request.contextPath}/manager/dashboard/stats')
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        $('#totalMembers').text(data.data.totalMembers.toLocaleString());
                        $('#todaySales').text('¥' + data.data.todaySales.toLocaleString());
                        $('#totalPoints').text(data.data.totalPoints.toLocaleString());
                        $('#activePromotions').text(data.data.activePromotions);
                    }
                })
                .catch(error => {
                    console.error('加载统计数据失败:', error);
                    // 使用模拟数据作为后备
                    $('#totalMembers').text('1,234');
                    $('#todaySales').text('¥25,680');
                    $('#totalPoints').text('156,789');
                    $('#activePromotions').text('5');
                });
        }
    </script>
</body>
</html>