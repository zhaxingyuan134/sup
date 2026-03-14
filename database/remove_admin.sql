-- 删除管理员用户
DELETE FROM users WHERE role = 'ADMIN';

-- 修改 ENUM 定义，移除 'ADMIN' 选项
-- 注意：MySQL 不支持直接从 ENUM 中删除值，必须重新定义列
ALTER TABLE users MODIFY COLUMN role ENUM('MEMBER', 'CASHIER', 'MANAGER') NOT NULL DEFAULT 'MEMBER';
