-- 超市经理模块数据库表结构
-- 在现有数据库基础上添加经理模块所需的表

USE qimo;

-- 1. 促销活动表
CREATE TABLE IF NOT EXISTS promotions (
    promotion_id INT AUTO_INCREMENT PRIMARY KEY,
    promotion_name VARCHAR(100) NOT NULL,
    promotion_type ENUM('DOUBLE_POINTS', 'BONUS_POINTS', 'DISCOUNT', 'SPECIAL_OFFER') NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- 促销规则配置（JSON格式存储）
    rule_config JSON,
    -- 适用条件
    min_purchase_amount DECIMAL(10,2) DEFAULT 0,
    max_usage_per_member INT DEFAULT NULL,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_promotion_type (promotion_type),
    INDEX idx_date_range (start_date, end_date),
    INDEX idx_is_active (is_active)
);

-- 2. 商品信息表（用于销售统计）
CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL UNIQUE,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    cost DECIMAL(10,2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    points_rate DECIMAL(5,2) DEFAULT 1.0, -- 积分倍率
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_code (product_code),
    INDEX idx_category (category),
    INDEX idx_is_active (is_active)
);

-- 3. 销售记录表
CREATE TABLE IF NOT EXISTS sales_records (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    member_id INT,
    cashier_id INT NOT NULL,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    total_points_earned INT DEFAULT 0,
    payment_method ENUM('CASH', 'CARD', 'MOBILE', 'POINTS') NOT NULL,
    promotion_id INT,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (member_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (cashier_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (promotion_id) REFERENCES promotions(promotion_id) ON DELETE SET NULL,
    INDEX idx_order_number (order_number),
    INDEX idx_member_id (member_id),
    INDEX idx_sale_date (sale_date),
    INDEX idx_cashier_id (cashier_id)
);

-- 4. 销售明细表
CREATE TABLE IF NOT EXISTS sale_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    points_earned INT DEFAULT 0,
    FOREIGN KEY (sale_id) REFERENCES sales_records(sale_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_sale_id (sale_id),
    INDEX idx_product_id (product_id)
);

-- 5. 系统参数配置表
CREATE TABLE IF NOT EXISTS system_config (
    config_id INT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    config_type ENUM('STRING', 'NUMBER', 'BOOLEAN', 'JSON') DEFAULT 'STRING',
    description VARCHAR(255),
    updated_by INT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (updated_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_config_key (config_key)
);

-- 6. 会员等级配置表
CREATE TABLE IF NOT EXISTS membership_levels (
    level_id INT AUTO_INCREMENT PRIMARY KEY,
    level_name VARCHAR(50) NOT NULL UNIQUE,
    min_points INT NOT NULL,
    max_points INT,
    min_spending DECIMAL(10,2) DEFAULT 0,
    benefits JSON, -- 会员权益配置
    points_multiplier DECIMAL(3,2) DEFAULT 1.0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_level_name (level_name),
    INDEX idx_points_range (min_points, max_points)
);

-- 7. 会员活动参与记录表
CREATE TABLE IF NOT EXISTS member_promotion_usage (
    usage_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    promotion_id INT NOT NULL,
    usage_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    points_earned INT DEFAULT 0,
    order_number VARCHAR(50),
    FOREIGN KEY (member_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (promotion_id) REFERENCES promotions(promotion_id) ON DELETE CASCADE,
    INDEX idx_member_id (member_id),
    INDEX idx_promotion_id (promotion_id),
    INDEX idx_usage_date (usage_date)
);

-- 8. 会员等级升级历史表
CREATE TABLE IF NOT EXISTS member_level_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    old_level VARCHAR(50) NOT NULL,
    new_level VARCHAR(50) NOT NULL,
    upgrade_reason VARCHAR(255),
    upgrade_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    points_at_upgrade INT DEFAULT 0,
    spending_at_upgrade DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_upgrade_date (upgrade_date),
    INDEX idx_old_level (old_level),
    INDEX idx_new_level (new_level)
);

-- 插入初始系统配置
INSERT INTO system_config (config_key, config_value, config_type, description) VALUES
('points_per_yuan', '1', 'NUMBER', '每消费1元获得的积分数'),
('min_purchase_for_points', '10', 'NUMBER', '获得积分的最低消费金额'),
('points_expiry_months', '12', 'NUMBER', '积分过期月数'),
('max_points_per_transaction', '1000', 'NUMBER', '单次交易最大积分获得数'),
('member_upgrade_auto', 'true', 'BOOLEAN', '是否自动升级会员等级');

-- 插入会员等级配置
INSERT INTO membership_levels (level_name, min_points, max_points, min_spending, benefits, points_multiplier) VALUES
('BRONZE', 0, 999, 0, '{"discount": 0, "birthday_bonus": 50, "free_parking": false}', 1.0),
('SILVER', 1000, 2999, 500, '{"discount": 0.02, "birthday_bonus": 100, "free_parking": true}', 1.2),
('GOLD', 3000, 9999, 1500, '{"discount": 0.05, "birthday_bonus": 200, "free_parking": true}', 1.5),
('PLATINUM', 10000, NULL, 5000, '{"discount": 0.1, "birthday_bonus": 500, "free_parking": true}', 2.0);

-- 插入示例商品数据
INSERT INTO products (product_code, product_name, category, price, cost, stock_quantity, points_rate) VALUES
('P001', '可口可乐 330ml', '饮料', 3.50, 2.00, 100, 1.0),
('P002', '康师傅方便面', '食品', 4.50, 2.50, 80, 1.0),
('P003', '农夫山泉 550ml', '饮料', 2.00, 1.20, 150, 1.0),
('P004', '奥利奥饼干', '零食', 8.90, 5.50, 60, 1.2),
('P005', '蒙牛纯牛奶 250ml', '乳制品', 3.20, 2.10, 90, 1.1);

-- 插入示例促销活动
INSERT INTO promotions (promotion_name, promotion_type, description, start_date, end_date, created_by, rule_config) VALUES
('双倍积分周末', 'DOUBLE_POINTS', '周末购物享受双倍积分', '2024-01-01', '2024-12-31', 3, 
 '{"applicable_days": ["SATURDAY", "SUNDAY"], "multiplier": 2.0}'),
('新年特惠', 'BONUS_POINTS', '消费满100元额外获得50积分', '2024-01-01', '2024-01-31', 3,
 '{"min_amount": 100, "bonus_points": 50}');

COMMIT;