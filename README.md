# 🚀 Backend Template (Spring Boot + Vue 3)

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://docs.oracle.com/en/java/javase/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-green.svg)](https://spring.io/projects/spring-boot)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.13-blue.svg)](https://baomidou.com/)
[![License](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)

> 一个现代化的、企业级 Spring Boot 后端快速开发脚手架。
> 集成了 **JWT/UUID 鉴权**、**Redis 缓存**、**全局异常处理**、**统一响应体**、**Knife4j 接口文档** 等核心功能，开箱即用。

---

## ✨ 核心特性

- **🔐 安全认证**：
    - 采用 **UUID Token + Redis** 方案（比 JWT 更可控、更安全）。
    - 密码采用 **BCrypt 加盐哈希** 存储，拒绝明文。
    - 完整的 **登录拦截器** 与 **ThreadLocal 用户上下文** 管理。

- **🛠️ 最佳实践**：
    - **统一响应体** (`BaseResponse`)：规范的前后端交互格式。
    - **全局异常处理** (`GlobalExceptionHandler`)：
      - 🛡️ **生产环境脱敏**：用户端只显示“系统繁忙”，保护敏感堆栈信息。
      - 📝 **开发环境友好**：后台日志记录完整异常堆栈，方便排查。
      - 🚦 **日志分级**：业务异常 WARN，系统异常 ERROR。
    - **AOP 日志**：自动记录接口请求耗时、参数、IP 等信息。
    - **MyBatis-Plus 增强**：使用 `LambdaQueryWrapper` 防止魔法值，自动填充创建/更新时间，逻辑删除。

- **📚 开发体验**：
    - 集成 **Knife4j (Swagger 3)**：接口文档自动生成，在线调试。
    - 集成 **Hutool**：常用工具类库（JSON、加密、BeanCopier）。
    - 统一 **DTO/VO** 模型，参数校验 (`@Valid`)，规避空指针与非法参数。

---

## 🏗️ 技术栈

| 技术 | 说明 | 版本 |
| :--- | :--- | :--- |
| **Spring Boot** | 核心框架 | 3.2.4 |
| **JDK** | 编程语言 | 17+ |
| **MySQL** | 数据库 | 8.0+ |
| **Redis** | 缓存与 Session 管理 | 5.0+ |
| **MyBatis-Plus** | ORM 框架 | 3.5.13 |
| **Knife4j** | 接口文档 UI | 4.4.0 |
| **Hutool** | 工具类库 | 5.8.25 |
| **Lombok** | 简化对象封装 | 1.18.30 |

---

## 🚀 快速开始

### 1. 环境准备
确保你的本地环境已安装：
- JDK 17+
- MySQL 8.0+
- Redis 5.0+
- Maven 3.6+

### 2. 数据库初始化
执行 `sql/create_table.sql` 脚本：
```sql
-- 创建库
CREATE DATABASE IF NOT EXISTS backend;
USE backend;
-- 创建表（脚本会自动创建 user 表）
```

### 3. 修改配置
修改 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    # 修改为你的数据库账号密码
    username: root
    password: your_password 
  data:
    redis:
      # 修改为你的 Redis 地址（默认 localhost:6379）
      host: localhost
      port: 6379
```

### 4. 启动项目
运行 `BackendTemplateApplication.java` 的 `main` 方法。
- **启动成功后**：访问接口文档 [http://localhost:8080/doc.html](http://localhost:8080/doc.html)

---

## 📂 项目结构

```
src/main/java/com/acha/project
├── aspectj          # AOP 切面 (日志、权限)
├── common           # 通用模块 (统一响应 BaseResponse, ResultUtils)
├── config           # 配置类 (WebMvc, Swagger, Security)
├── constant         # 常量定义
├── controller       # 控制层 (接口入口)
├── exception        # 全局异常处理
├── interceptor      # 拦截器 (登录校验)
├── mapper           # DAO 层 (MyBatis-Plus Mapper)
├── model            # 数据模型
│   ├── dto          # 数据传输对象 (前端 -> 后端)
│   ├── entity       # 数据库实体
│   └── vo           # 视图对象 (后端 -> 前端，脱敏)
├── service          # 业务逻辑层
└── utils            # 工具类
```

---

## 📝 接口规范

### 统一响应格式
```json
{
  "code": 0,           // 业务状态码 (0 表示成功)
  "data": { ... },     // 业务数据
  "message": "ok"      // 提示信息
}
```

### 异常响应格式
```json
{
  "code": 401,
  "data": null,
  "message": "参数错误"
}
```

---

## 🤝 贡献与支持

欢迎提交 Issue 或 Pull Request！如果你觉得这个项目对你有帮助，请给一个 ⭐️ Star！

## 📄 License

[MIT](LICENSE) © 2026 Acha
