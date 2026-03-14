# 超市积分管理系统

一个基于JavaWeb技术栈开发的超市积分管理系统，支持用户注册登录、积分管理、商品兑换等功能。

## 技术栈

- **后端**: Java 8, Servlet, JSP, JSTL
- **前端**: Bootstrap 5, jQuery, Font Awesome
- **数据库**: MySQL 8.0
- **构建工具**: Maven
- **服务器**: Jetty (开发环境)
- **密码加密**: BCrypt

## 功能特性

### 用户管理
- 用户注册与登录
- 多角色支持（会员、收银员、经理、管理员）
- 密码加密存储
- 会员等级管理（铜牌、银牌、金牌、白金）

### 积分系统
- 积分获取与消费记录
- 积分历史查询
- 积分统计分析
- 会员等级自动升级

### 商品兑换
- 兑换商品管理
- 积分兑换功能
- 兑换记录追踪
- 库存管理

### 系统管理
- 用户权限管理
- 数据统计报表
- 系统配置管理

## 项目结构

```
sup/
├── src/main/java/com/supermarket/
│   ├── dao/                    # 数据访问层
│   │   ├── UserDAO.java
│   │   └── impl/
│   │       └── UserDAOImpl.java
│   ├── model/                  # 实体类
│   │   └── User.java
│   ├── service/                # 业务逻辑层
│   ├── servlet/                # 控制器层
│   │   ├── LoginServlet.java
│   │   ├── RegisterServlet.java
│   │   ├── LogoutServlet.java
│   │   └── MemberDashboardServlet.java
│   └── util/                   # 工具类
│       ├── DatabaseUtil.java
│       └── PasswordUtil.java
├── src/main/webapp/
│   ├── WEB-INF/
│   │   ├── views/              # JSP页面
│   │   │   ├── login.jsp
│   │   │   ├── register.jsp
│   │   │   └── member/
│   │   │       ├── dashboard.jsp
│   │   │       ├── points-history.jsp
│   │   │       └── points-redeem.jsp
│   │   └── web.xml
│   └── index.jsp
├── database/
│   └── init.sql                # 数据库初始化脚本
└── pom.xml
```

## 安装部署

### 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+

### 数据库配置

1. 创建MySQL数据库：
```sql
CREATE DATABASE supermarket_points CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p supermarket_points < database/init.sql
```

3. 修改数据库连接配置（如需要）：
编辑 `src/main/java/com/supermarket/util/DatabaseUtil.java` 中的数据库连接参数。

### 运行项目

1. 克隆项目到本地：
```bash
git clone <repository-url>
cd sup
```

2. 编译并启动项目：
```bash
mvn clean compile jetty:run
```

3. 访问系统：
打开浏览器访问 `http://localhost:8080`

## 测试账号

系统预置了以下测试账号（密码格式：角色名123）：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | 管理员 | 系统管理员账号 |
| manager01 | manager123 | 经理 | 经理账号 |
| cashier01 | cashier123 | 收银员 | 收银员账号 |
| member01 | member123 | 会员 | 普通会员（1500积分） |
| member02 | member123 | 会员 | 银牌会员（3200积分） |
| member03 | member123 | 会员 | 金牌会员（5800积分） |

## 使用说明

### 会员功能
1. **注册登录**: 新用户可以注册会员账号，已有用户直接登录
2. **积分查询**: 查看当前积分余额和积分历史记录
3. **商品兑换**: 使用积分兑换优惠券、礼品或服务
4. **个人信息**: 管理个人资料和会员等级信息

### 管理功能
1. **用户管理**: 管理员可以查看和管理所有用户信息
2. **积分管理**: 为用户手动调整积分，查看积分统计
3. **商品管理**: 管理兑换商品的信息和库存
4. **报表统计**: 查看系统运营数据和统计报表

## 开发说明

### 数据库设计
- `users`: 用户信息表
- `point_transactions`: 积分交易记录表
- `redemption_items`: 兑换商品表
- `redemption_records`: 兑换记录表

### 安全特性
- 密码使用BCrypt加密存储
- Session管理防止未授权访问
- SQL注入防护
- XSS攻击防护

### 扩展功能
系统设计支持以下扩展：
- 多店铺支持
- 积分过期机制
- 优惠活动管理
- 移动端适配
- API接口开发

## 许可证

本项目仅供学习和研究使用。

## 联系方式

如有问题或建议，请联系开发团队。