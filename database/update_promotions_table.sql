-- 更新promotions表结构以匹配Servlet的期望
USE qimo;

-- 删除现有的promotions表
DROP TABLE IF EXISTS promotions;

-- 创建新的promotions表结构
CREATE TABLE promotions (
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
    rule_config JSON,
    min_purchase_amount DECIMAL(10,2) DEFAULT 0,
    max_usage_per_member INT DEFAULT NULL,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_promotion_type (promotion_type),
    INDEX idx_date_range (start_date, end_date),
    INDEX idx_is_active (is_active)
);

-- 插入示例数据
INSERT INTO promotions (promotion_name, promotion_type, description, start_date, end_date, rule_config, min_purchase_amount, created_by) VALUES
('双倍积分日', 'DOUBLE_POINTS', '每周六双倍积分活动', '2024-01-01', '2024-12-31', '{"multiplier": 2.0}', 10.00, 2),
('国庆大促销', 'BONUS_POINTS', '国庆期间所有商品积分三倍', '2024-10-01', '2024-10-07', '{"multiplier": 3.0}', 50.00, 2),
('新年特惠', 'DISCOUNT', '新年期间9折优惠', '2024-12-25', '2025-01-05', '{"discount": 0.1}', 100.00, 2);

COMMIT;