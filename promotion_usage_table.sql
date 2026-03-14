-- 创建促销活动使用记录表
USE qimo;

-- 创建促销活动使用记录表
CREATE TABLE IF NOT EXISTS promotion_usage (
    usage_id INT AUTO_INCREMENT PRIMARY KEY,
    promotion_id INT NOT NULL,
    member_id INT NOT NULL,
    usage_date DATE NOT NULL,
    purchase_amount DECIMAL(10,2) NOT NULL,
    points_earned INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (promotion_id) REFERENCES promotions(promotion_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    INDEX idx_promotion_id (promotion_id),
    INDEX idx_member_id (member_id),
    INDEX idx_usage_date (usage_date)
);

-- 插入测试数据
INSERT INTO promotion_usage (promotion_id, member_id, usage_date, purchase_amount, points_earned) VALUES
(1, 1, '2024-01-15', 150.00, 300),
(1, 2, '2024-01-15', 200.00, 400),
(2, 3, '2024-01-20', 80.00, 160);