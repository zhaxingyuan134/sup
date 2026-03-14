-- 超市积分管理系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS qimo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE qimo;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    membership_card_number VARCHAR(50) UNIQUE,
    role ENUM('MEMBER', 'CASHIER', 'MANAGER') NOT NULL DEFAULT 'MEMBER',
    membership_level VARCHAR(20) DEFAULT 'BRONZE',
    total_points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_membership_card_number (membership_card_number),
    INDEX idx_role (role),
    INDEX idx_membership_level (membership_level)
);

-- 创建积分交易记录表
CREATE TABLE IF NOT EXISTS point_transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    transaction_type ENUM('EARN', 'REDEEM', 'EXPIRE', 'ADJUST') NOT NULL,
    points INT NOT NULL,
    description VARCHAR(255),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cashier_id INT,
    order_id VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (cashier_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_transaction_date (transaction_date)
);

-- 创建兑换商品表
CREATE TABLE IF NOT EXISTS redemption_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    points_required INT NOT NULL,
    description TEXT,
    stock_quantity INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_points_required (points_required),
    INDEX idx_is_active (is_active)
);

-- 创建兑换记录表
CREATE TABLE IF NOT EXISTS redemption_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    item_id INT NOT NULL,
    points_used INT NOT NULL,
    quantity INT DEFAULT 1,
    redemption_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    cashier_id INT,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES redemption_items(item_id) ON DELETE CASCADE,
    FOREIGN KEY (cashier_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_item_id (item_id),
    INDEX idx_redemption_date (redemption_date),
    INDEX idx_status (status)
);

-- 插入测试数据

-- 插入收银员用户 (密码: cashier123)
INSERT INTO users (username, password, real_name, phone, email, membership_card_number, role, membership_level, total_points) VALUES
('cashier01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfMxJEPTIVmvaGwI7oK8owjO', '张收银', '13800138001', 'cashier01@supermarket.com', 'CASH001', 'CASHIER', 'SILVER', 0);

-- 插入经理用户 (密码: manager123)
INSERT INTO users (username, password, real_name, phone, email, membership_card_number, role, membership_level, total_points) VALUES
('manager01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfMxJEPTIVmvaGwI7oK8owjO', '李经理', '13800138002', 'manager01@supermarket.com', 'MGR001', 'MANAGER', 'GOLD', 0);
-- 插入会员用户 (密码: member123)
INSERT INTO users (username, password, real_name, phone, email, membership_card_number, role, membership_level, total_points) VALUES
('member01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfMxJEPTIVmvaGwI7oK8owjO', '王会员', '13800138003', 'member01@example.com', 'MEM001', 'MEMBER', 'BRONZE', 1500),
('member02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfMxJEPTIVmvaGwI7oK8owjO', '刘会员', '13800138004', 'member02@example.com', 'MEM002', 'MEMBER', 'SILVER', 3200),
('member03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfMxJEPTIVmvaGwI7oK8owjO', '陈会员', '13800138005', 'member03@example.com', 'MEM003', 'MEMBER', 'GOLD', 5800);

-- 插入兑换商品
INSERT INTO redemption_items (item_name, category, points_required, description, stock_quantity) VALUES
('10元购物券', 'COUPON', 100, '可在本超市使用的10元购物券', 100),
('20元购物券', 'COUPON', 200, '可在本超市使用的20元购物券', 50),
('50元购物券', 'COUPON', 500, '可在本超市使用的50元购物券', 30),
('保温杯', 'GIFT', 800, '不锈钢保温杯，容量500ml', 20),
('购物袋', 'GIFT', 300, '环保购物袋，可重复使用', 50),
('雨伞', 'GIFT', 600, '折叠雨伞，防风防雨', 15),
('免费停车券', 'SERVICE', 150, '免费停车2小时', 200),
('生日蛋糕券', 'SERVICE', 1000, '免费生日蛋糕一个（8寸）', 10);

-- 插入积分交易记录
INSERT INTO point_transactions (user_id, transaction_type, points, description, cashier_id) VALUES
(4, 'EARN', 200, '购物消费获得积分', 2),
(4, 'EARN', 150, '购物消费获得积分', 2),
(4, 'REDEEM', -100, '兑换10元购物券', 2),
(5, 'EARN', 300, '购物消费获得积分', 2),
(5, 'EARN', 250, '购物消费获得积分', 2),
(5, 'REDEEM', -200, '兑换20元购物券', 2),
(6, 'EARN', 400, '购物消费获得积分', 2),
(6, 'EARN', 350, '购物消费获得积分', 2),
(6, 'REDEEM', -500, '兑换50元购物券', 2);

-- 插入兑换记录
INSERT INTO redemption_records (user_id, item_id, points_used, quantity, status, cashier_id) VALUES
(4, 1, 100, 1, 'COMPLETED', 2),
(5, 2, 200, 1, 'COMPLETED', 2),
(6, 3, 500, 1, 'COMPLETED', 2);

COMMIT;