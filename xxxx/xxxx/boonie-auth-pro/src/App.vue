<template>
  <div class="app">
    <!-- 已登录：首页 -->
    <HomePage v-if="session" :user="session.user" @logout="logout" />

    <!-- 未登录：登录 / 注册页 -->
    <div v-else class="auth-page">
      <ForestPanel />
      <AuthCard @login-success="onLogin" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ForestPanel from './components/ForestPanel.vue'
import AuthCard from './components/AuthCard.vue'
import HomePage from './views/HomePage.vue'

const TOKEN_KEY = 'boonie_token'

// 登录态：{ user, token }；null 表示未登录
const session = ref(null)

function onLogin(payload) {
  session.value = { user: payload.user, token: payload.token }
}

function logout() {
  localStorage.removeItem(TOKEN_KEY)
  session.value = null
}

// 仅用于前端预览：地址栏加 ?demo=1 可直接进入首页（无需启动后端）
onMounted(() => {
  const isDemo = new URLSearchParams(location.search).get('demo') === '1'
  if (isDemo && !session.value) {
    session.value = {
      user: { id: 1, name: '熊大', email: 'admin@boonie.com' },
      token: 'demo-token'
    }
  }
})
</script>

<style scoped>
.app {
  min-height: 100vh;
}

.auth-page {
  display: flex;
  min-height: 100vh;
}

/* 左侧森林区在窄屏隐藏，宽屏显示 */
@media (min-width: 860px) {
  :deep(.forest) {
    display: flex;
  }
  /* 桌面优化：宽屏时给卡片区更舒展的内边距 */
  :deep(.panel) {
    padding: 32px 48px;
  }
}
</style>
