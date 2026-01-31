---
trigger: always_on
---

# Antigravity 核心指令集

## 1. 语言与交互规范 (Core Interaction)
- **强制语言**：所有回答、代码注释、文档描述必须使用 **简体中文**。
- **术语处理**：专业技术术语（如 Proxy, Middleware, Bean）在解释时可保留英文，但主体叙述需为中文。
- **思考模型**：在执行复杂任务前，必须输出 `[思考过程]` 和 `[执行计划]`，确保逻辑可见且可控。
- **操作确认**：涉及 `rm`, `drop table`, `force push` 等具有破坏性的终端指令，必须先解释影响并停顿请求确认。

## 2. 公共环境安全 (Security & Environment)
- **零信任硬编码**：严禁在代码中出现 API Key、数据库密码或任何 Secret。默认使用 `.env` 或 `application.yml` 占位，并提醒用户配置。
- **依赖检查**：在执行安装指令前，优先检查当前环境（如 `pom.xml`, `package.json`），避免引入冲突的包版本。
- **沙盒意识**：所有操作应局限在当前项目目录下，禁止触碰系统级目录（/etc, /usr 等）。

## 3. 前端规范：Vue 3 (Composition API)
- **技术栈**：Vue 3 + Vite + TypeScript + Pinia。
- **编码风格**：
  - 必须使用 `<script setup>` 语法。
  - 优先使用 Composition API (组合式 API) 而非 Options API。
  - 组件命名遵循 PascalCase (如 `UserCard.vue`)，内部变量使用 camelCase。
- **响应式处理**：
  - 基础类型优先用 `ref`，复杂对象/表单优先用 `reactive`。
  - 组件间通信：Props 必须定义类型（Interface），Event 使用 `defineEmits`。
- **样式规范**：默认使用 Scoped CSS，推荐使用 CSS 变量或 Tailwind 类名（如项目已安装）。
- **接口封装**：所有 API 请求需封装在 `src/api` 目录下，并定义请求/响应的 TypeScript 类型。

## 4. 后端规范：Spring Boot (JDK 8)
- **核心约束**：JDK 8 兼容语法（Stream API, Optional），Spring Boot 2.x 规范。
- **架构分层**：
  - 严格遵守 `Controller -> Service -> Mapper -> Entity` 分层。
  - **DTO 隔离**：外部请求使用 `DTO`，内部持久化使用 `Entity`，严禁将 Entity 直接作为 API 返回值。
- **接口标准**：
  - 遵循 RESTful API 设计准则。
  - 必须包含全局异常处理（@RestControllerAdvice）和统一响应体（Result<T>）。
- **日志规范**：使用 SLF4J + Logback，严禁使用 `System.out.println`。重要业务逻辑必须记录 INFO 级别日志。

## 5. 数据库规范：MySQL + MyBatis-Plus
- **命名规范**：数据库表名/字段使用 `snake_case`（下划线），Java 实体类使用 `camelCase`（驼峰）。
- **ORM 最佳实践**：
  - 继承 `BaseMapper<T>` 和 `IService<T>` 以利用 MP 的内置 CRUD。
  - 复杂查询优先使用 `LambdaQueryWrapper` 确保类型安全。
  - 分页必须使用 MP 的 `IPage` 插件。
- **SQL 安全**：严禁直接拼接 SQL 字符串，必须使用参数化查询（Wrapper 或 XML 占位符）防止 SQL 注入。
- **审计字段**：所有表默认包含 `create_time`, `update_time`, `is_deleted` (逻辑删除)，并建议使用 MP 的自动填充功能。

## 6. 专业工程化要求 (Professional Excellence)
- **代码注释**：复杂逻辑必须提供 Javadoc (后端) 或 JSDoc (前端) 注释。
- **错误处理**：前端请求必须有 `.catch` 或 `try-catch` 捕获；后端必须对空对象进行 `Optional` 或空检查。
- **幂等性**：涉及写操作的接口，应考虑基本的重复提交防范意识。
- **注释即文档**：生成的 Controller 建议带上基本的 Swagger/Springdoc 注解（如 @Tag, @Operation）。