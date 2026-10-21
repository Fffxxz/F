# 熊出没 · 森林登录（Vue 3 + Vite + Express 后端）

将原本的单文件 `login-boonie.html` 改造为 **Vue 3 + Vite 前端 + Express 后端** 的全栈项目。

## 项目结构

```
boonie-login-vue/
├── index.html              # 入口 HTML
├── package.json
├── vite.config.js          # 含 /api 代理到后端 3001
├── server/
│   └── index.js            # Express 后端（登录 / 健康检查 / 会话）
└── src/
    ├── main.js            # 应用入口，挂载 App
    ├── style.css          # 全局样式（配色变量、背景动画）
    ├── App.vue            # 页面布局：左侧森林 + 右侧表单
    └── components/
        ├── ForestPanel.vue # 左侧森林区（云朵、落叶、熊大熊二）
        └── LoginForm.vue   # 右侧登录表单（校验、密码显隐、抖动、API 调用）
```

## 运行

```bash
npm install
npm run dev:all   # 同时启动前端(5173) + 后端(3001)，开发最方便
# 或分开启动：
npm run dev       # 仅前端，默认 http://localhost:5173
npm run server    # 仅后端，http://localhost:3001

npm run build     # 打包前端到 dist/
npm start         # 生产模式：用后端托管 dist/ 并对外提供接口
```

## 测试账号

| 邮箱 | 密码 |
| --- | --- |
| `admin@boonie.com` | `bear123456` |
| `bear2@boonie.com` | `bear123456` |

## 后端接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET`  | `/api/health` | 健康检查 |
| `POST` | `/api/login`  | `{ email, password }` → `{ token, user }` 或 401 |
| `GET`  | `/api/me`     | 携带 `Authorization: Bearer <token>` → 当前用户 |

- 密码使用 Node 内置 `crypto.scrypt` 加盐哈希，不存明文。
- 会话用内存 `Map`（token → userId），重启即失效（演示用；生产可换 Redis/JWT）。

## 改造要点

- 把内联 `<style>` 拆分为全局 `style.css`（主题变量、背景动画）与组件 `scoped` 样式。
- 把原生 DOM 操作改写为 Vue 响应式数据（`ref` / `v-model`）与事件绑定。
- 左侧森林区与右侧表单拆分为独立组件，数据通过 `v-for` 驱动飘云与落叶。
- 校验失败后的「抖动」动画用 `:class` + `nextTick` 重触发，动画结束自动复位。
- 前端通过 Vite 代理 `/api` 转发到 Express，登录改为真实 `fetch` 调用并支持「记住我」（token 存 localStorage）、成功欢迎页与退出登录。
