# AGENTS.md —— 给 AI 助手的项目速查

> 写给下一次接手本项目的 AI。本机每次开机重置，请先读这份，不要重新探索。

## 这个项目是什么

Vue 3 + Vite 前端，登录注册页（`src/components/AuthCard.vue`）。

**后端已经不在本目录了。** 原来自带的 Express mock 后端（`server/index.js`）已被
`D:\workSpace\springBootTest` 的 **Spring Boot 3 + MyBatis-Plus** 后端**取代**，
接口契约逐字保持一致，所以本项目代码一行都不用改。

👉 **详细契约、环境路径、启动方式、已知坑：请读 `D:\workSpace\springBootTest\AGENTS.md`**

## 关键点（省得你翻）

- `/api` 由 `vite.config.js` 代理到 **`http://localhost:8080`**（后端端口必须是 8080）
- 前端读取的字段：`data.token`、`data.user.name`（**注意是 `name`，后端用 VO 把 `username` 映射过来**）、
  `data.demoCode`、`data.message`
- 种子账号：`admin@boonie.com` / `bear2@boonie.com`，密码 `bear123456`
- 启动：优先用 `D:\workSpace\springBootTest\scripts\启动全部.bat`（会先起后端再起前端）

## 本目录的垃圾文件（可删，但先问用户）

- `server/`、`data/` —— 已被取代的 mock 后端
- `dist/`、`vite.log`、`vite.config.js.timestamp-*.mjs` —— 旧构建与残留

## 待补

- **本目录没有 `.gitignore`**，也没有 git 仓库。`node_modules/` 会被误提交，属于该补的东西。
