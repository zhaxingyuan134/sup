
# 开发规范指南

为保证代码质量、可维护性、安全性与可扩展性，请在开发过程中严格遵循以下规范。

## 一、项目基础信息

- **工作目录**：`C:\Users\HP\OneDrive\桌面\sup`
- **操作系统**：Windows 11
- **构建工具**：Maven
- **SDK 版本**：JDK 1.8.0_462
- **代码作者**：HP
- **第一语言注释**：中文

---

## 二、技术栈要求

- **主框架**：Servlet + JSP（传统 Java Web）
- **语言版本**：Java 8
- **核心依赖**：
  - `javax.servlet-api`
  - `javax.servlet.jsp-api`
  - `jstl`
  - `mysql-connector-java`
  - `jbcrypt`
  - `jackson-databind`
  - `junit`

## 三、目录结构说明

```
sup
└── src
    └── main
        ├── java
        │   └── com
        │       └── supermarket
        │           ├── dao
        │           │   └── impl
        │           ├── model
        │           ├── service
        │           │   └── impl
        │           ├── servlet
        │           └── util
        ├── resources
        └── webapp
            └── WEB-INF
                └── views
                    └── dashboard
```

### 分层结构说明：

| 层级     | 职责说明                         | 对应包路径              |
|----------|----------------------------------|-------------------------|
| DAO      | 数据访问层，负责数据库交互         | `com.supermarket.dao` 及其子包 |
| Model    | 实体类或数据模型                 | `com.supermarket.model` |
| Service  | 业务逻辑处理                     | `com.supermarket.service` 及其子包 |
| Servlet  | 控制器，处理请求和响应           | `com.supermarket.servlet` |
| Util     | 工具类集合                       | `com.supermarket.util` |

## 四、分层架构规范

### Controller 层（Servlet）

- 所有 HTTP 请求由 Servlet 处理。
- 不得直接操作数据库，必须调用 Service 层。
- 页面跳转通过转发或重定向完成。

### Service 层

- 实现主要业务逻辑。
- 调用 DAO 进行数据访问。
- 返回 DTO 或其他封装对象给 Servlet。

### DAO 层

- 提供对数据库的增删改查操作。
- 使用 JDBC 或 MyBatis 等 ORM 框架进行数据库交互。
- 推荐使用接口+实现的方式组织代码。

### Model 层

- 映射数据库表结构。
- 应避免暴露敏感字段至前端。
- 使用标准 POJO 结构。

### Util 层

- 封装通用工具方法，如加密解密、日期处理等。

## 五、安全与性能规范

### 输入校验

- 在接收用户输入时进行合法性检查。
- 防止 SQL 注入攻击，所有查询语句需采用预编译方式执行。
- 使用 `BCrypt` 加密密码存储。

### 日志记录

- 使用 `java.util.logging.Logger` 或 Log4j 记录关键操作日志。
- 不要在生产环境中打印敏感信息。

### 编码规范

- 所有字符串常量及配置项建议统一管理于 `.properties` 文件中。
- 定义全局异常处理器以提升用户体验。

## 六、代码风格规范

### 命名规范

| 类型       | 命名方式             | 示例                  |
|------------|----------------------|-----------------------|
| 类名       | UpperCamelCase       | `UserDaoImpl`         |
| 方法/变量  | lowerCamelCase       | `getUserById()`       |
| 常量       | UPPER_SNAKE_CASE     | `MAX_LOGIN_ATTEMPTS`  |

### 注释规范

- 所有公共类、方法均需添加 Javadoc 注释。
- 关键业务流程需要补充行内注释以便后期维护。

### 类型命名规范（阿里巴巴风格）

| 后缀 | 用途说明                     | 示例         |
|------|------------------------------|--------------|
| DTO  | 数据传输对象                 | `UserDTO`    |
| DO   | 数据库实体对象               | `UserDO`     |
| BO   | 业务逻辑封装对象             | `UserBO`     |
| VO   | 视图展示对象                 | `UserVO`     |
| Query| 查询参数封装对象             | `UserQuery`  |

## 七、编码原则总结

| 原则       | 说明                                       |
|------------|--------------------------------------------|
| **SOLID**  | 高内聚、低耦合，增强可维护性与可扩展性     |
| **DRY**    | 避免重复代码，提高复用性                   |
| **KISS**   | 保持代码简洁易懂                           |
| **YAGNI**  | 不实现当前不需要的功能                     |
| **OWASP**  | 防范常见安全漏洞，如 SQL 注入、XSS 等      |
