<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>收银员工作台 - 超市积分管理系统</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            font-family: 'Microsoft YaHei', sans-serif;
        }
        
        .navbar {
            background: rgba(255, 255, 255, 0.95) !important;
            backdrop-filter: blur(10px);
            box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
        }
        
        .main-container {
            padding: 2rem 0;
        }
        
        .function-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.2);
            transition: all 0.3s ease;
            margin-bottom: 2rem;
            overflow: hidden;
        }
        
        .function-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
        }
        
        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 1.5rem;
        }
        
        .feature-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 15px;
            padding: 1rem 2rem;
            color: white;
            font-weight: 600;
            transition: all 0.3s ease;
            margin: 0.5rem;
        }
        
        .feature-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
            color: white;
        }
        
        .member-info-card {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 2rem;
        }
        
        .scan-area {
            background: rgba(255, 255, 255, 0.1);
            border: 2px dashed rgba(255, 255, 255, 0.3);
            border-radius: 15px;
            padding: 3rem;
            text-align: center;
            margin: 1rem 0;
        }
        
        .transaction-summary {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            border-radius: 15px;
            padding: 1.5rem;
        }
        
        .modal-content {
            border-radius: 20px;
            border: none;
        }
        
        .modal-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 20px 20px 0 0;
        }
        
        .video-container {
            position: relative;
            width: 100%;
            height: 300px;
            background: #000;
            border-radius: 10px;
            overflow: hidden;
        }
        
        #video {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        
        .scan-overlay {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 200px;
            height: 200px;
            border: 2px solid #00ff00;
            border-radius: 10px;
            pointer-events: none;
        }
        
        .receipt-preview {
            background: white;
            border-radius: 10px;
            padding: 1rem;
            font-family: 'Courier New', monospace;
            font-size: 12px;
            max-height: 400px;
            overflow-y: auto;
        }
    </style>
</head>
<body>
    <!-- 导航栏 -->
    <nav class="navbar navbar-expand-lg navbar-light">
        <div class="container">
            <a class="navbar-brand fw-bold" href="#">
                <i class="fas fa-cash-register me-2"></i>收银员工作台
            </a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text me-3">
                    <i class="bi bi-person-circle me-1"></i>
                    ${sessionScope.user.username}
                </span>
                <a class="btn btn-outline-danger btn-sm" href="${pageContext.request.contextPath}/logout">
                    <i class="bi bi-box-arrow-right me-1"></i>退出登录
                </a>
            </div>
        </div>
    </nav>

    <div class="container main-container">
        <!-- 会员识别区域 -->
        <div class="function-card">
            <div class="card-header">
                <h5 class="mb-0">
                    <i class="fas fa-user-check me-2"></i>会员识别
                </h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <div class="mb-3">
                            <label class="form-label">手机号</label>
                            <input type="text" class="form-control" id="memberPhone" placeholder="请输入会员手机号">
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="mb-3">
                            <label class="form-label">会员卡号</label>
                            <input type="text" class="form-control" id="memberCard" placeholder="请输入会员卡号">
                        </div>
                    </div>
                </div>
                <div class="text-center">
                    <button class="feature-btn" onclick="identifyMember()">
                        <i class="fas fa-search me-2"></i>识别会员
                    </button>
                    <button class="feature-btn" onclick="startVideoScan()">
                        <i class="fas fa-video me-2"></i>视频扫描
                    </button>
                </div>
                
                <!-- 会员信息显示区域 -->
                <div id="memberInfo" class="member-info-card" style="display: none;">
                    <div class="row">
                        <div class="col-md-8">
                            <h6 id="memberName">会员姓名</h6>
                            <p class="mb-1">手机号：<span id="memberPhoneDisplay"></span></p>
                            <p class="mb-1">会员等级：<span id="memberLevel"></span></p>
                            <p class="mb-0">当前积分：<span id="memberPoints"></span></p>
                        </div>
                        <div class="col-md-4 text-end">
                            <button class="btn btn-light btn-sm" onclick="clearMember()">
                                <i class="fas fa-times me-1"></i>清除
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 商品扫描和交易处理区域 -->
        <div class="function-card">
            <div class="card-header">
                <h5 class="mb-0">
                    <i class="fas fa-barcode me-2"></i>商品扫描与交易
                </h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-8">
                        <!-- 商品列表 -->
                        <div class="table-responsive">
                            <table class="table table-striped" id="productTable">
                                <thead>
                                    <tr>
                                        <th>商品名称</th>
                                        <th>单价</th>
                                        <th>数量</th>
                                        <th>小计</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody id="productList">
                                    <!-- 商品列表将通过JavaScript动态添加 -->
                                </tbody>
                            </table>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6">
                                <input type="text" class="form-control" id="productBarcode" placeholder="扫描或输入商品条码" onkeypress="handleBarcodeKeyPress(event)">
                            </div>
                            <div class="col-md-3">
                                <input type="number" class="form-control" id="productQuantity" value="1" min="1">
                            </div>
                            <div class="col-md-3">
                                <button class="btn btn-primary w-100" onclick="addProduct()">
                                    <i class="fas fa-plus me-1"></i>添加商品
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-4">
                        <!-- 交易汇总 -->
                        <div class="transaction-summary">
                            <h6>交易汇总</h6>
                            <div class="d-flex justify-content-between mb-2">
                                <span>商品总数：</span>
                                <span id="totalItems">0</span>
                            </div>
                            <div class="d-flex justify-content-between mb-2">
                                <span>商品总额：</span>
                                <span id="totalAmount">¥0.00</span>
                            </div>
                            <div class="d-flex justify-content-between mb-3">
                                <span>获得积分：</span>
                                <span id="earnedPoints">0</span>
                            </div>
                            <hr style="border-color: rgba(255,255,255,0.3);">
                            <div class="d-flex justify-content-between mb-3">
                                <strong>应付金额：</strong>
                                <strong id="finalAmount">¥0.00</strong>
                            </div>
                            
                            <div class="d-grid gap-2">
                                <button class="btn btn-light" onclick="processPayment()">
                                    <i class="fas fa-credit-card me-2"></i>结算付款
                                </button>
                                <button class="btn btn-outline-light" onclick="showRedemption()">
                                    <i class="fas fa-gift me-2"></i>积分兑换
                                </button>
                                <button class="btn btn-outline-light" onclick="clearTransaction()">
                                    <i class="fas fa-trash me-2"></i>清空交易
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 视频扫描模态框 -->
    <div class="modal fade" id="videoScanModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="fas fa-video me-2"></i>视频扫描
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="video-container">
                        <video id="video" autoplay></video>
                        <div class="scan-overlay"></div>
                    </div>
                    <div class="text-center mt-3">
                        <button class="btn btn-success" onclick="captureFrame()">
                            <i class="fas fa-camera me-2"></i>捕获识别
                        </button>
                        <button class="btn btn-secondary" onclick="stopVideoScan()">
                            <i class="fas fa-stop me-2"></i>停止扫描
                        </button>
                    </div>
                    <div id="scanResult" class="mt-3" style="display: none;">
                        <div class="alert alert-success">
                            <strong>识别结果：</strong><span id="scanResultText"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 积分兑换模态框 -->
    <div class="modal fade" id="redemptionModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="fas fa-gift me-2"></i>积分兑换
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row" id="redemptionProducts">
                        <!-- 兑换商品将通过JavaScript动态加载 -->
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 小票预览模态框 -->
    <div class="modal fade" id="receiptModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="fas fa-receipt me-2"></i>小票预览
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="receipt-preview" id="receiptContent">
                        <!-- 小票内容将通过JavaScript生成 -->
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" onclick="printReceipt()">
                        <i class="fas fa-print me-2"></i>打印小票
                    </button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // 全局变量
        let currentMember = null;
        let currentTransaction = {
            items: [],
            totalAmount: 0,
            totalItems: 0,
            earnedPoints: 0
        };
        let videoStream = null;

        // 会员识别功能
        function identifyMember() {
            const phone = document.getElementById('memberPhone').value.trim();
            const card = document.getElementById('memberCard').value.trim();
            
            if (!phone && !card) {
                alert('请输入手机号或会员卡号');
                return;
            }
            
            const data = {};
            if (phone) data.phone = phone;
            if (card) data.cardNumber = card;
            
            fetch('${pageContext.request.contextPath}/cashier/member/identify', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success && data.member) {
                    displayMemberInfo(data.member);
                } else {
                    alert(data.message || '未找到会员信息');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('识别失败，请重试');
            });
        }
        
        // 显示会员信息
        function displayMemberInfo(member) {
            currentMember = member;
            document.getElementById('memberName').textContent = member.realName || member.username;
            document.getElementById('memberPhoneDisplay').textContent = member.phone || '未设置';
            document.getElementById('memberLevel').textContent = getMemberLevelText(member.membershipLevel);
            document.getElementById('memberPoints').textContent = member.totalPoints || 0;
            document.getElementById('memberInfo').style.display = 'block';
        }
        
        // 获取会员等级文本
        function getMemberLevelText(level) {
            const levels = {
                'BRONZE': '铜卡会员',
                'SILVER': '银卡会员', 
                'GOLD': '金卡会员',
                'PLATINUM': '白金会员',
                'DIAMOND': '钻石会员'
            };
            return levels[level] || '普通会员';
        }
        
        // 清除会员信息
        function clearMember() {
            currentMember = null;
            document.getElementById('memberInfo').style.display = 'none';
            document.getElementById('memberPhone').value = '';
            document.getElementById('memberCard').value = '';
        }
        
        // 启动视频扫描
        function startVideoScan() {
            const modal = new bootstrap.Modal(document.getElementById('videoScanModal'));
            modal.show();
            
            navigator.mediaDevices.getUserMedia({ video: true })
                .then(stream => {
                    videoStream = stream;
                    document.getElementById('video').srcObject = stream;
                })
                .catch(error => {
                    console.error('Error accessing camera:', error);
                    alert('无法访问摄像头，请检查权限设置');
                });
        }
        
        // 停止视频扫描
        function stopVideoScan() {
            if (videoStream) {
                videoStream.getTracks().forEach(track => track.stop());
                videoStream = null;
            }
            bootstrap.Modal.getInstance(document.getElementById('videoScanModal')).hide();
        }
        
        // 捕获帧进行识别
        function captureFrame() {
            const video = document.getElementById('video');
            const canvas = document.createElement('canvas');
            canvas.width = video.videoWidth;
            canvas.height = video.videoHeight;
            
            const ctx = canvas.getContext('2d');
            ctx.drawImage(video, 0, 0);
            
            // 模拟识别结果（实际应用中需要调用OCR或条码识别API）
            const mockResult = 'CARD' + Math.floor(Math.random() * 1000).toString().padStart(3, '0');
            document.getElementById('scanResultText').textContent = mockResult;
            document.getElementById('scanResult').style.display = 'block';
            
            // 自动填入识别结果
            document.getElementById('memberCard').value = mockResult;
            
            setTimeout(() => {
                stopVideoScan();
                identifyMember();
            }, 2000);
        }
        
        // 添加商品
        function addProduct() {
            const barcode = document.getElementById('productBarcode').value.trim();
            const quantity = parseInt(document.getElementById('productQuantity').value) || 1;
            
            if (!barcode) {
                alert('请输入商品条码');
                return;
            }
            
            // 查询商品信息
            fetch('${pageContext.request.contextPath}/cashier/product/info', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ barcode: barcode })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success && data.product) {
                    addProductToTransaction(data.product, quantity);
                } else {
                    alert(data.message || '商品不存在');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('查询商品失败，请重试');
            });
        }
        
        // 添加商品到交易
        function addProductToTransaction(product, quantity) {
            const existingItem = currentTransaction.items.find(item => item.productId === product.productId);
            
            if (existingItem) {
                existingItem.quantity += quantity;
                existingItem.subtotal = existingItem.price * existingItem.quantity;
            } else {
                currentTransaction.items.push({
                    productId: product.productId,
                    name: product.name,
                    price: product.price,
                    quantity: quantity,
                    subtotal: product.price * quantity
                });
            }
            
            updateTransactionSummary();
            renderProductList();
            
            // 清空输入
            document.getElementById('productBarcode').value = '';
            document.getElementById('productQuantity').value = '1';
        }
        
        // 渲染商品列表
        function renderProductList() {
            const tbody = document.getElementById('productList');
            tbody.innerHTML = '';
            
            currentTransaction.items.forEach((item, index) => {
                const row = tbody.insertRow();
                row.innerHTML = 
                    '<td>' + item.name + '</td>' +
                    '<td>¥' + item.price.toFixed(2) + '</td>' +
                    '<td>' +
                        '<input type="number" class="form-control form-control-sm" ' +
                               'value="' + item.quantity + '" min="1" ' +
                               'onchange="updateQuantity(' + index + ', this.value)">' +
                    '</td>' +
                    '<td>¥' + item.subtotal.toFixed(2) + '</td>' +
                    '<td>' +
                        '<button class="btn btn-danger btn-sm" onclick="removeProduct(' + index + ')">' +
                            '<i class="fas fa-trash"></i>' +
                        '</button>' +
                    '</td>';
            });
        }
        
        // 处理条码输入框回车键事件
        function handleBarcodeKeyPress(event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                addProduct();
            }
        }
        
        // 更新商品数量
        function updateQuantity(index, newQuantity) {
            const quantity = parseInt(newQuantity) || 1;
            currentTransaction.items[index].quantity = quantity;
            currentTransaction.items[index].subtotal = currentTransaction.items[index].price * quantity;
            updateTransactionSummary();
            renderProductList();
        }
        
        // 移除商品
        function removeProduct(index) {
            currentTransaction.items.splice(index, 1);
            updateTransactionSummary();
            renderProductList();
        }
        
        // 更新交易汇总
        function updateTransactionSummary() {
            currentTransaction.totalItems = currentTransaction.items.reduce((sum, item) => sum + item.quantity, 0);
            currentTransaction.totalAmount = currentTransaction.items.reduce((sum, item) => sum + item.subtotal, 0);
            currentTransaction.earnedPoints = Math.floor(currentTransaction.totalAmount);
            
            document.getElementById('totalItems').textContent = currentTransaction.totalItems;
            document.getElementById('totalAmount').textContent = '¥' + currentTransaction.totalAmount.toFixed(2);
            document.getElementById('earnedPoints').textContent = currentTransaction.earnedPoints;
            document.getElementById('finalAmount').textContent = '¥' + currentTransaction.totalAmount.toFixed(2);
        }
        
        // 处理付款
        function processPayment() {
            if (currentTransaction.items.length === 0) {
                alert('请先添加商品');
                return;
            }
            
            const transactionData = {
                member: currentMember,
                items: currentTransaction.items,
                totalAmount: currentTransaction.totalAmount,
                earnedPoints: currentTransaction.earnedPoints
            };
            
            fetch('${pageContext.request.contextPath}/cashier/transaction/process', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(transactionData)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // 更新会员积分显示
                    if (currentMember && data.updatedPoints !== undefined) {
                        currentMember.points = data.updatedPoints;
                        document.getElementById('memberPoints').textContent = data.updatedPoints;
                    }
                    
                    // 显示小票预览
                    generateReceipt(data.transactionId);
                    
                    alert('交易成功！');
                } else {
                    alert(data.message || '交易失败');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('交易处理失败，请重试');
            });
        }
        
        // 显示积分兑换
        function showRedemption() {
            if (!currentMember) {
                alert('请先识别会员');
                return;
            }
            
            fetch('${pageContext.request.contextPath}/cashier/redemption/products')
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        renderRedemptionProducts(data.products);
                        const modal = new bootstrap.Modal(document.getElementById('redemptionModal'));
                        modal.show();
                    } else {
                        alert('获取兑换商品失败');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('获取兑换商品失败');
                });
        }
        
        // 渲染兑换商品
        function renderRedemptionProducts(products) {
            const container = document.getElementById('redemptionProducts');
            container.innerHTML = '';
            
            products.forEach(product => {
                const canRedeem = currentMember.points >= product.pointsRequired;
                const col = document.createElement('div');
                col.className = 'col-md-6 mb-3';
                col.innerHTML = 
                    '<div class="card ' + (canRedeem ? '' : 'opacity-50') + '">' +
                        '<div class="card-body">' +
                            '<h6 class="card-title">' + product.name + '</h6>' +
                            '<p class="card-text">所需积分：' + product.pointsRequired + '</p>' +
                            '<button class="btn btn-primary btn-sm" ' +
                                    (canRedeem ? '' : 'disabled') +
                                    ' onclick="redeemProduct(' + product.productId + ', ' + product.pointsRequired + ')">' +
                                (canRedeem ? '兑换' : '积分不足') +
                            '</button>' +
                        '</div>' +
                    '</div>';
                container.appendChild(col);
            });
        }
        
        // 兑换商品
        function redeemProduct(productId, pointsRequired) {
            if (currentMember.points < pointsRequired) {
                alert('积分不足');
                return;
            }
            
            fetch('${pageContext.request.contextPath}/cashier/redemption/redeem', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    memberId: currentMember.userId,
                    productId: productId,
                    pointsRequired: pointsRequired
                })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    currentMember.points = data.remainingPoints;
                    document.getElementById('memberPoints').textContent = data.remainingPoints;
                    alert('兑换成功！');
                    bootstrap.Modal.getInstance(document.getElementById('redemptionModal')).hide();
                } else {
                    alert(data.message || '兑换失败');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('兑换失败，请重试');
            });
        }
        
        // 清空交易
        function clearTransaction() {
            if (confirm('确定要清空当前交易吗？')) {
                currentTransaction = {
                    items: [],
                    totalAmount: 0,
                    totalItems: 0,
                    earnedPoints: 0
                };
                updateTransactionSummary();
                renderProductList();
            }
        }
        
        // 生成小票
        function generateReceipt(transactionId) {
            const now = new Date();
            let receiptContent = '<div class="text-center mb-3">' +
                '<h6>超市积分管理系统</h6>' +
                '<small>北京市朝阳区XX路XX号</small><br>' +
                '<small>电话：010-12345678</small>' +
                '</div>' +
                '<hr>' +
                '<div class="mb-2">' +
                '<strong>交易单号：</strong>' + transactionId + '<br>' +
                '<strong>收银员：</strong>${sessionScope.user.username}<br>' +
                '<strong>时间：</strong>' + now.toLocaleString() +
                '</div>';
                
            // 会员信息部分
            if (currentMember != null) {
                receiptContent += '<hr>' +
                    '<div class="mb-2">' +
                    '<strong>会员信息</strong><br>' +
                    '会员：' + (currentMember.realName || currentMember.username) + '<br>' +
                    '等级：' + getMemberLevelText(currentMember.memberLevel) + '<br>' +
                    '积分：' + currentMember.points +
                    '</div>';
            }
            
            // 商品明细部分
            receiptContent += '<hr>' +
                '<div class="mb-2">' +
                '<strong>商品明细</strong><br>' +
                currentTransaction.items.map(item => 
                    item.name + ' x' + item.quantity + ' = ¥' + item.subtotal.toFixed(2)
                ).join('<br>') +
                '</div>' +
                '<hr>' +
                '<div class="text-end">' +
                '<strong>总计：¥' + currentTransaction.totalAmount.toFixed(2) + '</strong><br>';
                
            // 积分信息部分
            if (currentMember != null) {
                receiptContent += '获得积分：' + currentTransaction.earnedPoints;
            }
            
            receiptContent += '</div>' +
                '<div class="text-center mt-3">' +
                '<small>谢谢惠顾，欢迎再次光临！</small>' +
                '</div>';
            
            document.getElementById('receiptContent').innerHTML = receiptContent;
            const modal = new bootstrap.Modal(document.getElementById('receiptModal'));
            modal.show();
        }
        
        // 打印小票
        function printReceipt() {
            const printContent = document.getElementById('receiptContent').innerHTML;
            const printWindow = window.open('', '_blank');
            printWindow.document.write(
                '<html>' +
                '<head>' +
                '<title>小票打印</title>' +
                '<style>' +
                'body { font-family: "Courier New", monospace; font-size: 12px; }' +
                '.text-center { text-align: center; }' +
                '.text-end { text-align: right; }' +
                '.mb-2 { margin-bottom: 0.5rem; }' +
                '.mb-3 { margin-bottom: 1rem; }' +
                '.mt-3 { margin-top: 1rem; }' +
                'hr { border: 1px dashed #999; }' +
                '</style>' +
                '</head>' +
                '<body>' +
                printContent +
                '</body>' +
                '</html>'
            );
            printWindow.document.close();
            printWindow.print();
            printWindow.close();
            
            // 打印后清空交易
            clearTransaction();
            bootstrap.Modal.getInstance(document.getElementById('receiptModal')).hide();
        }
        
        // 页面加载完成后的初始化
        document.addEventListener('DOMContentLoaded', function() {
            // 监听条码输入框的回车事件
            document.getElementById('productBarcode').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    addProduct();
                }
            });
            
            // 监听会员输入框的回车事件
            document.getElementById('memberPhone').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    identifyMember();
                }
            });
            
            document.getElementById('memberCard').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    identifyMember();
                }
            });
        });
    </script>
</body>
</html>