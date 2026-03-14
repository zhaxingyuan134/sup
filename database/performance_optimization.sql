-- 数据库性能优化脚本
-- 为超市管理系统添加必要的索引以提升查询性能

USE qimo;

-- 1. 用户表索引优化
-- 为经常查询的字段添加索引
ALTER TABLE users 
ADD INDEX idx_username (username),
ADD INDEX idx_email (email),
ADD INDEX idx_phone (phone),
ADD INDEX idx_membership_card_number (membership_card_number),
ADD INDEX idx_role (role),
ADD INDEX idx_is_active (is_active),
ADD INDEX idx_membership_level (membership_level),
ADD INDEX idx_total_points (total_points),
ADD INDEX idx_created_at (created_at),
ADD INDEX idx_role_active (role, is_active),
ADD INDEX idx_role_points (role, total_points),
ADD INDEX idx_role_created (role, created_at);

-- 2. 积分记录表索引优化
ALTER TABLE points_records 
ADD INDEX idx_user_id (user_id),
ADD INDEX idx_transaction_type (transaction_type),
ADD INDEX idx_created_at (created_at),
ADD INDEX idx_user_type (user_id, transaction_type),
ADD INDEX idx_user_created (user_id, created_at),
ADD INDEX idx_type_created (transaction_type, created_at),
ADD INDEX idx_points_change (points_change);

-- 3. 积分交易表索引优化（如果存在）
CREATE INDEX IF NOT EXISTS idx_point_transactions_user_id ON point_transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_point_transactions_type ON point_transactions(transaction_type);
CREATE INDEX IF NOT EXISTS idx_point_transactions_date ON point_transactions(transaction_date);
CREATE INDEX IF NOT EXISTS idx_point_transactions_user_type ON point_transactions(user_id, transaction_type);
CREATE INDEX IF NOT EXISTS idx_point_transactions_user_date ON point_transactions(user_id, transaction_date);

-- 4. 销售记录表索引优化
ALTER TABLE sales_records 
ADD INDEX idx_member_sale_date (member_id, sale_date),
ADD INDEX idx_cashier_sale_date (cashier_id, sale_date),
ADD INDEX idx_payment_method (payment_method),
ADD INDEX idx_promotion_id (promotion_id),
ADD INDEX idx_total_amount (total_amount),
ADD INDEX idx_sale_date_amount (sale_date, total_amount);

-- 5. 销售明细表索引优化
ALTER TABLE sale_items 
ADD INDEX idx_sale_product (sale_id, product_id),
ADD INDEX idx_product_quantity (product_id, quantity),
ADD INDEX idx_subtotal (subtotal);

-- 6. 商品表索引优化
ALTER TABLE products 
ADD INDEX idx_category_active (category, is_active),
ADD INDEX idx_price_range (price),
ADD INDEX idx_stock_quantity (stock_quantity),
ADD INDEX idx_points_rate (points_rate),
ADD INDEX idx_category_price (category, price);

-- 7. 促销活动表索引优化
ALTER TABLE promotions 
ADD INDEX idx_active_dates (is_active, start_date, end_date),
ADD INDEX idx_created_by (created_by),
ADD INDEX idx_promotion_type_active (promotion_type, is_active);

-- 8. 会员等级配置表索引优化
ALTER TABLE membership_levels 
ADD INDEX idx_points_range_active (min_points, max_points, is_active),
ADD INDEX idx_spending_active (min_spending, is_active);

-- 9. 会员活动参与记录表索引优化
ALTER TABLE member_promotion_usage 
ADD INDEX idx_member_promotion (member_id, promotion_id),
ADD INDEX idx_usage_date (usage_date),
ADD INDEX idx_member_date (member_id, usage_date);

-- 10. 会员等级升级历史表索引优化
ALTER TABLE member_level_history 
ADD INDEX idx_user_upgrade_date (user_id, upgrade_date),
ADD INDEX idx_old_new_level (old_level, new_level),
ADD INDEX idx_upgrade_date_desc (upgrade_date DESC);

-- 11. 系统配置表索引优化（已存在config_key索引）
-- 无需额外索引，config_key已有唯一索引

-- 12. 为统计查询创建复合索引
-- 会员活跃度分析
ALTER TABLE users 
ADD INDEX idx_member_activity (role, is_active, total_points, created_at);

-- 销售趋势分析
ALTER TABLE sales_records 
ADD INDEX idx_sales_trend (sale_date, total_amount, member_id);

-- 商品销售分析
ALTER TABLE sale_items 
ADD INDEX idx_product_sales (product_id, quantity, subtotal);

-- 积分行为分析
ALTER TABLE points_records 
ADD INDEX idx_points_behavior (user_id, transaction_type, points_change, created_at);

-- 13. 为分页查询优化
-- 用户管理分页
ALTER TABLE users 
ADD INDEX idx_role_id_desc (role, user_id DESC);

-- 积分记录分页
ALTER TABLE points_records 
ADD INDEX idx_user_created_desc (user_id, created_at DESC);

-- 14. 为日期范围查询优化
-- 按日期统计
ALTER TABLE sales_records 
ADD INDEX idx_date_stats (DATE(sale_date), total_amount);

-- 按月份统计
ALTER TABLE users 
ADD INDEX idx_monthly_registration (YEAR(created_at), MONTH(created_at));

-- 15. 为聚合查询优化
-- 会员等级分布
ALTER TABLE users 
ADD INDEX idx_level_distribution (membership_level, role, is_active);

-- 积分统计
ALTER TABLE users 
ADD INDEX idx_points_stats (role, total_points, is_active);

-- 16. 创建视图以优化复杂查询
-- 活跃会员视图
CREATE OR REPLACE VIEW active_members AS
SELECT u.*, 
       COALESCE(recent_points.recent_activity, 0) as has_recent_activity
FROM users u
LEFT JOIN (
    SELECT user_id, 1 as recent_activity
    FROM points_records 
    WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
    GROUP BY user_id
) recent_points ON u.user_id = recent_points.user_id
WHERE u.role = 'MEMBER' AND u.is_active = 1;

-- 会员消费统计视图
CREATE OR REPLACE VIEW member_consumption_stats AS
SELECT 
    u.user_id,
    u.username,
    u.membership_level,
    u.total_points,
    COALESCE(sales_stats.total_spent, 0) as total_spent,
    COALESCE(sales_stats.order_count, 0) as order_count,
    COALESCE(sales_stats.avg_order_value, 0) as avg_order_value,
    COALESCE(sales_stats.last_purchase_date, u.created_at) as last_purchase_date
FROM users u
LEFT JOIN (
    SELECT 
        member_id,
        SUM(total_amount) as total_spent,
        COUNT(*) as order_count,
        AVG(total_amount) as avg_order_value,
        MAX(sale_date) as last_purchase_date
    FROM sales_records 
    WHERE member_id IS NOT NULL
    GROUP BY member_id
) sales_stats ON u.user_id = sales_stats.member_id
WHERE u.role = 'MEMBER' AND u.is_active = 1;

-- 17. 分析表以更新统计信息
ANALYZE TABLE users;
ANALYZE TABLE points_records;
ANALYZE TABLE sales_records;
ANALYZE TABLE sale_items;
ANALYZE TABLE products;
ANALYZE TABLE promotions;
ANALYZE TABLE membership_levels;
ANALYZE TABLE member_promotion_usage;
ANALYZE TABLE member_level_history;
ANALYZE TABLE system_config;

-- 18. 优化MySQL配置建议（注释形式）
/*
建议的MySQL配置优化：

1. 增加缓冲池大小：
   innodb_buffer_pool_size = 1G (根据服务器内存调整)

2. 优化查询缓存：
   query_cache_size = 256M
   query_cache_type = 1

3. 增加连接数：
   max_connections = 200

4. 优化临时表：
   tmp_table_size = 256M
   max_heap_table_size = 256M

5. 优化排序缓冲：
   sort_buffer_size = 2M
   read_buffer_size = 2M

6. 启用慢查询日志：
   slow_query_log = 1
   long_query_time = 2
*/

COMMIT;

-- 显示索引创建结果
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'qimo' 
  AND TABLE_NAME IN ('users', 'points_records', 'sales_records', 'sale_items', 'products', 'promotions')
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;