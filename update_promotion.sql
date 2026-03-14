USE qimo;

-- 更新双倍积分日活动为今天的日期
UPDATE promotions 
SET start_date = CURDATE(), 
    end_date = CURDATE(), 
    rule_config = '{"multiplier": 2, "minAmount": 0, "categories": ["all"], "memberLevels": ["普通会员", "银卡会员", "金卡会员"]}' 
WHERE promotion_id = 1;

-- 查看更新后的数据
SELECT * FROM promotions WHERE promotion_id = 1;