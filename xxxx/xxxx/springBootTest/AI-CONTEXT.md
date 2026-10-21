# AI-CONTEXT.md — 给 AI 助手的快速上手文件（读完即可开工，无需再探索项目）

> 电脑每次开机都会重置：`target` 文件夹会被删除（前端项目 boonie-auth-pro 的 `node_modules` 也会被删）。
> **下次会话请先读本文件，不要重新分析项目结构。**

## 项目是什么
「森林登录注册后端」：Spring Boot 3.4.5 + MyBatis-Plus 3.5.9 + MySQL 8 + Lombok + Thymeleaf。
- 端口 **8080**（dev profile，见 `src/main/resources/application-dev.properties`）
- 数据库：本地 MySQL `boonie_auth` 库 / `boonie_user` 表（root / 123456，端口 3306）
- 种子用户（表为空时 `DataInitializer` 自动写入）：熊大 `admin@boonie.com`、熊二 `bear2@boonie.com`，密码均为 `bear123456`
- 前端配套项目在 `../boonie-auth-pro`（Vue 3 + Vite，5173 端口，`/api` 代理到 8080）

## 一键脚本（根目录，GBK 编码，双击即可）
- `start.bat`：检查 Java → 检查 MySQL(3306) → 用 `db-init.sql` 幂等建库建表 → `mvnw spring-boot:run`（target 被删会自动重新编译，无需手动 build）
- `stop.bat`：按端口 8080 + 窗口标题杀掉后端进程
- `db-init.sql`：建库建表脚本，幂等可重复执行

## 历史踩坑（重要，勿再踩）
1. **端口被环境变量劫持**：AI 沙箱执行时会注入 `SERVER_PORT` 环境变量，覆盖 application.properties，
   导致 Tomcat 绑到随机端口（如 49549）并报 "Port already in use"。解决：start.bat 里已 `set SERVER_PORT=8080`
   并传 `-Dspring-boot.run.arguments=--server.port=8080`。AI 直接命令行启动时也要同样处理。
2. **数据库不存在**：`Unknown database 'boonie_auth'` → 跑一遍 `db-init.sql` 即可，表结构见脚本。
3. **MySQL 客户端路径**：`C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe`（PATH 里没有 mysql）。
4. **系统环境**：JDK 21（项目要求 17+，兼容）、无系统 mvn（必须用项目自带 mvnw.cmd）、MySQL 是 Windows 服务。

## 快速验证清单
1. 启动后看到 `Tomcat started on port 8080` + `Started SpringBootTestApplication`
2. `curl http://localhost:8080/api/health` → `{"ok":true,...,"users":N}`
3. `curl -X POST http://localhost:8080/api/login -H "Content-Type: application/json" -d '{"email":"admin@boonie.com","password":"bear123456"}'` → 返回 token

## 接口一览（controller 前缀 /api）
- `GET  /api/health` — 健康检查
- `POST /api/login` — 密码登录
- `POST /api/register` — 注册
- `POST /api/send-code` — 发验证码
- `GET  /api/me` — 当前用户（带 token）

## 文件地图
- `src/main/java/com/fenglin/springboottest/` — 主类 / AuthController / UserController / service / mapper / entity(User→boonie_user 表)
- `src/main/resources/application-dev.properties` — 端口 8080、MySQL 连接（本文件含数据库密码，勿外传）
- `start.bat` / `stop.bat` / `db-init.sql` — 运维脚本
- `log/xx.log` — 运行日志（排错先看这里）

## 给未来 AI 的提醒
- 不要花时间分析项目结构、读源码细节 —— 本文件已覆盖全部要点
- 用户要求：脚本用 GBK 编码、项目内路径用相对路径、脚本不能依赖 target/node_modules 已存在
- 启动整套（前端+后端）：双击 `../boonie-auth-pro/start.bat`（它会连带启动本项目）
- 如果 mvnw 报错说找不到 Java → 电脑重置删了 JDK，需先装 JDK 17+
