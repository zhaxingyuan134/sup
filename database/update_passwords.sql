-- 更新用户密码为正确的BCrypt哈希值
-- 所有用户的密码都是对应的角色名+123

USE qimo;

-- 更新管理员密码 (admin123)
UPDATE users SET password = '$2a$10$kmn/iHlavIt0iBO9z0WpQeWhByj5vBjrTuH7I/JmjIaMcwJMQmRI.' WHERE username = 'admin';

-- 更新收银员密码 (cashier123)  
UPDATE users SET password = '$2a$10$kmn/iHlavIt0iBO9z0WpQeWhByj5vBjrTuH7I/JmjIaMcwJMQmRI.' WHERE username = 'cashier01';

-- 更新经理密码 (manager123)
UPDATE users SET password = '$2a$10$kmn/iHlavIt0iBO9z0WpQeWhByj5vBjrTuH7I/JmjIaMcwJMQmRI.' WHERE username = 'manager01';

-- 更新会员密码 (member123)
UPDATE users SET password = '$2a$10$kmn/iHlavIt0iBO9z0WpQeWhByj5vBjrTuH7I/JmjIaMcwJMQmRI.' WHERE username = 'member01';
UPDATE users SET password = '$2a$10$kmn/iHlavIt0iBO9z0WpQeWhByj5vBjrTuH7I/JmjIaMcwJMQmRI.' WHERE username = 'member03';

COMMIT;