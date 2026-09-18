# Nova 登录页 · Vue 3 版本

由 `index.html`（单文件登录页）改造而来的 Vue 3 + Vite 项目。

## 运行

```bash
cd nova-login-vue
npm install
npm run dev      # 开发预览 http://127.0.0.1:5173
npm run build    # 打包到 dist/（已配置相对路径，dist/index.html 可直接双击打开）
```

## 目录结构

```
nova-login-vue/
├─ index.html                     # Vite 入口
├─ package.json
├─ vite.config.js
└─ src/
   ├─ main.js                     # 应用入口
   ├─ App.vue                     # 页面骨架（左品牌 + 右表单）
   ├─ styles/global.css           # 设计令牌（CSS 变量）、全局重置、共用动画
   ├─ components/
   │  ├─ BackgroundDecor.vue      # 背景网格 + 漂浮光斑
   │  ├─ BrandPanel.vue           # 左侧品牌宣传区
   │  ├─ LoginForm.vue            # 右侧登录表单（校验、提交、密码可见性）
   │  └─ ToastHost.vue            # 右上角全局轻提示容器
   ├─ composables/useToast.js     # 全局 toast 状态（单例）
   └─ services/auth.js            # 登录接口（Mock，注释内有真实 fetch 示例）
```

## 改造要点

| 原 HTML | Vue 实现 |
| --- | --- |
| 内联 `<style>` | 拆为 `global.css`（变量/重置/动画）+ 各组件 `<style scoped>` |
| `document.getElementById` + `addEventListener` | `v-model` 双向绑定 + `@click/@blur/@input` 事件 |
| 手工 `classList.add('invalid')` | `:class` 响应式绑定 `errors` / `shake` 状态 |
| 字符串拼接 `innerHTML` 图标 | `v-if / v-else` 切换 SVG 图标组件 |
| `document.createElement` 生成 toast | `useToast` 组合式函数 + `<TransitionGroup>` 动画 |
| 内联 `setTimeout` 模拟接口 | `src/services/auth.js` 独立服务层，便于替换为真实后端 |

## 演示账号

邮箱任意合法格式（或 11 位手机号），密码 `123456` 即登录成功，其余密码报错。
