-- 更新现有数据库表结构，添加membership_card_number字段
USE qimo;

-- 添加membership_card_number字段
ALTER TABLE users ADD COLUMN membership_card_number VARCHAR(50) UNIQUE AFTER email;

-- 添加索引
CREATE INDEX idx_phone ON users(phone);
CREATE INDEX idx_membership_card_number ON users(membership_card_number);

-- 为现有用户添加会员卡号
UPDATE users SET membership_card_number = 'ADMIN001' WHERE username = 'admin';
UPDATE users SET membership_card_number = 'CASH001' WHERE username = 'cashier01';
UPDATE users SET membership_card_number = 'MGR001' WHERE username = 'manager01';
UPDATE users SET membership_card_number = 'MEM001' WHERE username = 'member01';
UPDATE users SET membership_card_number = 'MEM002' WHERE username = 'member02';
UPDATE users SET membership_card_number = 'MEM003' WHERE username = 'member03';