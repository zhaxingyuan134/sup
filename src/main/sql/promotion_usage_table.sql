-- 创建促销活动使用记录表
CREATE TABLE IF NOT EXISTS promotion_usage (
    id INT AUTO_INCREMENT PRIMARY KEY,
    promotion_id INT NOT NULL,
    member_id INT NOT NULL,
    usage_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    points_earned INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_promotion_id (promotion_id),
    INDEX idx_member_id (member_id),
    INDEX idx_usage_date (usage_date),
    
    FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='促销活动使用记录表';

-- 插入一些测试数据
INSERT INTO promotion_usage (promotion_id, member_id, usage_date, amount, points_earned) VALUES
(1, 4, CURDATE(), 150.00, 300),
(1, 5, CURDATE(), 200.00, 400),
(1, 4, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 120.00, 240);