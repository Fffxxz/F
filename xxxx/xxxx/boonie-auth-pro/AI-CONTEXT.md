# AI-CONTEXT.md — 给 AI 助手的快速上手文件（读完即可开工，无需再探索项目）

> 电脑每次开机都会重置：本项目 `node_modules` 会被删除；后端项目 `../springBootTest` 的 `target` 也会被删除。
> **下次会话请先读本文件，不要重新分析项目结构。**

## 项目是什么
「熊出没 · 狗熊岭森林」：Vue 3 + Vite 前端（登录注册 + 登录后首页），后端为 **Java Spring Boot**（`../springBootTest`）。
- 前端：Vite 开发服务器，端口 **5173**，`vite.config.js` 里 `open: true` 会自动开浏览器
- 后端：`../springBootTest`（Spring Boot 3.4.5 + MyBatis-Plus + MySQL），端口 **8080**，详见 `../springBootTest/AI-CONTEXT.md`
- 前端所有请求走相对路径 `/api/*`，由 Vite 代理转发到 `http://localhost:8080`
- `server/index.js` 是旧的 Express 假后端，**已弃用**（不再启动），仅作参考保留
- 页面流程：未登录 → 登录/注册页（App.vue 里 `.auth-page`）；登录成功 → 首页 `src/views/HomePage.vue`（无 vue-router，用登录态 `session` 切换）
- 首页子页面（2026-11-04 已开发，纯前端示例数据）：快速入口 4 页已接入，由 `HomePage.vue` 里的 `currentView` 切换（非路由）：
  - `src/views/SquarePage.vue` 森林广场（发布/点赞/评论）
  - `src/views/ShopPage.vue` 蜂蜜商店（松果余额/满 50 减 10/兑换记录）
  - `src/views/TaskPage.vue` 任务中心（每日/每周/成就、签到、进度条领奖）
  - `src/views/RankPage.vue` 熊岭排行榜（领奖台 + 三榜切换）
  - 导航栏「森林广场/蜂蜜商店」同样切到子页面；「我的园地」仍是占位
- 首页推荐流/搜索接口路径已在 `HomePage.vue` 的 `API` 常量里预留（/api/home/feed、/api/search、/api/notice、/api/user/profile），后端尚未实现
- **后台管理系统（2026-11-04 已开发）**：仅 `user.role === 0` 的管理员能看到导航栏/移动抽屉的「🛠 后台管理」按钮；点击后 `emit('open-admin')` → `App.vue` 全屏切换到后台（不显示前台导航），普通用户（`role=1`）无入口且不能登录后台：
  - `src/views/AdminLogin.vue` 仅登录（无注册）：调用 `POST /api/admin/login`，由后端校验用户表 `role=0`；演示管理员 `admin@boonie.com` / `bear123456`
  - `src/views/AdminLayout.vue` 后台框架：左侧 7 个模块 + 顶栏（返回前台 / 退出登录），模块组件在 `src/views/admin/`
  - 模块：数据概览 / 广场动态 / 商店商品 / 任务配置 / 排行榜数据 / 首页内容（公告·推荐流·热搜词·快速入口）/ 系统设置（松果余额、恢复示例数据、导出 JSON）
  - 后台登录态存 `localStorage.boonie_admin_session`（含 role），刷新免登录；「退出」回登录页
- **共享数据仓库 `src/store/data.js`**（前后台共用，localStorage 持久化 key `boonie_mock_data_v1`）：
  `store.posts / goods / tasks / boards / feed / notices / hotWords / quickEntries / wallet / userState`
  后台增删改即时反映到前台各页面；接后端时只需替换 store 初始数据来源，页面无需改动
  （旧的 `src/store/homeData.js` 已合并删除，勿再引用）
- 纯前端预览首页：地址栏访问 `http://localhost:5173/?demo=1`（App.vue 里的 demo 模式，不依赖后端；演示用户设为 `role=0`，后台登录仍需调用真实后端接口）

## 一键脚本（根目录，GBK 编码，双击即可）
- `start.bat`：检查 node_modules → 缺失则自动 `npm ci`（国内镜像 npmmirror，失败降级 npm install）
  → 清理 `vite.config.js.timestamp-*.mjs` 垃圾文件 → 用相对路径 `..\springBootTest\start.bat` 启动 Java 后端(8080) → 启动前端(5173)
- `stop.bat`：按窗口标题 + 按端口(8080/5173) 双重方式杀掉所有相关进程
- 只想启动后端：双击 `..\springBootTest\start.bat`

## 手动启动（脚本不可用时的后备方案）
```bash
# 后端（MySQL 需在运行，库表用 ../springBootTest/db-init.sql 初始化）
cd ../springBootTest
set SERVER_PORT=8080 && mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8080
# 前端
cd ../boonie-auth-pro
npm ci --registry=https://registry.npmmirror.com
npx vite
```

## 快速验证清单
1. 后端日志出现 `Tomcat started on port 8080` → Java 后端就绪（首次编译要几十秒）
2. `curl http://localhost:8080/api/health` → `{"ok":true,...}`
3. 浏览器开 5173 → 能收到验证码、能注册登录 → 说明代理和端口都正常
4. 测试账号：熊大 `admin@boonie.com` / 密码 `bear123456`

## 文件地图
- `src/` — 前端（main.js / App.vue / components/AuthCard.vue / components/ForestPanel.vue / views/HomePage.vue / style.css）
  - `App.vue` — 登录态守卫：`session` 有值显示 HomePage，否则显示登录页；`?demo=1` 直接进首页；`adminRoute` 控制后台（login / panel）
  - `store/data.js` — 前后台共享数据仓库（localStorage 持久化）
  - `admin.css` — 后台专用样式（`a-` 前缀，由 AdminLayout 引入）
  - `views/admin/` — 后台 7 个模块组件
  - `AdminLogin.vue` / `AdminLayout.vue` — 后台登录页与框架
  - `AuthCard.vue` — 登录/注册卡片，登录成功后 `emit('login-success')` 给 App.vue，不再内嵌欢迎页
  - `HomePage.vue` — 首页（导航栏 + 搜索栏 + 示例内容），「退出登录」在右上角头像下拉菜单里
- `server/index.js` — 旧 Express 后端（已弃用，勿启动，会和 Java 后端抢 8080）
- `data/users.json` — 旧 Express 的用户数据（已弃用，真正数据在 MySQL boonie_auth.boonie_user）
- `vite.config.js` — 端口 5173、代理 /api → 8080、自动开浏览器
- `start.bat` / `stop.bat` — 启动/停止脚本
- `vite.config.js.timestamp-*.mjs` — vite 崩溃残留垃圾，start.bat 会自动清理，可无视

## 给未来 AI 的提醒
- 不要花时间分析项目结构、读源码细节 —— 本文件已覆盖全部要点
- 用户要求：脚本用 GBK 编码、路径用相对路径、脚本不能依赖 node_modules/target 已存在
- 沙箱执行启动命令时环境变量 SERVER_PORT 会被劫持导致端口错乱，务必显式指定 8080（详见后端 AI-CONTEXT 的踩坑记录）
- 如果 start.bat 报 npm 不存在 → 电脑重置把 Node 也删了，需先装 Node 22+
- **node_modules 残缺（2026-10-16 实测踩坑）**：目录在但 `node_modules\.bin\vite.cmd` 被删时，旧判断 `if not exist "node_modules"` 会误判“已安装”而跳过安装，
  随后 `npx vite` 找不到本地包会自动拉最新 vite（v7/rolldown），崩在 `Cannot find module './rolldown-binding.win32-x64-msvc.node'`。
  现在 start.bat 改为：校验 `.bin\vite.cmd` / `vue` / `vite` / `@vitejs\plugin-vue` 四个关键文件 → 缺失才装 → 装完再复检 → 用 `npm run dev` 走本地 vite（**别改回 npx vite**）。
- **重装提速（2026-10-16）**：npm 缓存在 `C:\Users\Administrator\AppData\Local\npm-cache`（不在项目里，重置不会被删）。
  start.bat 里的 npm 命令加了 `--prefer-offline --no-audit --no-fund`：缓存命中时重装 **128 个包约 6 秒**（冷缓存仍需约 5 分钟走网络）。
  别去掉这几个参数，否则每次开机重装都要等 5 分钟。
- **端口占用预检**：start.bat 启动前会查 8080/5173 是否已在监听，已占用则提示「可能已经启动过」并退出，避免重复双击导致 `Port already in use`。
