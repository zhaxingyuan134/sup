USE qimo;

-- 更新双倍积分日活动
UPDATE promotions 
SET start_date = CURDATE(), 
    end_date = CURDATE(), 
    rule_config = '{"multiplier": 2}' 
WHERE promotion_id = 1;

-- 查看更新结果
SELECT promotion_id, promotion_name, promotion_type, start_date, end_date, rule_config, is_active 
FROM promotions 
WHERE promotion_id = 1;