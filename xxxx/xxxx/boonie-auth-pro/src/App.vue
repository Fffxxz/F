<template>
  <div class="app">
    <!-- 后台管理系统（独立全屏布局，不显示前台导航） -->
    <template v-if="adminRoute === 'login'">
      <AdminLogin :user="session?.user" @success="onAdminSuccess" @back="closeAdmin" />
    </template>

    <template v-else-if="adminRoute === 'panel'">
      <AdminLayout :admin="adminSession" @exit="closeAdmin" @logout="adminLogout" />
    </template>

    <!-- 前台：已登录首页 -->
    <HomePage
      v-else-if="session"
      :user="session.user"
      @logout="logout"
      @open-admin="enterAdmin"
    />

    <!-- 前台：未登录登录 / 注册页 -->
    <div v-else class="auth-page">
      <ForestPanel />
      <AuthCard @login-success="onLogin" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import ForestPanel from './components/ForestPanel.vue'
import AuthCard from './components/AuthCard.vue'
import HomePage from './views/HomePage.vue'
import AdminLogin from './views/AdminLogin.vue'
import AdminLayout from './views/AdminLayout.vue'

const TOKEN_KEY = 'boonie_token'
const ADMIN_KEY = 'boonie_admin_session'

// 登录态：{ user, token }；null 表示未登录
const session = ref(null)

// 后台登录态：{ name, account, token }；adminRoute 控制后台显示哪个页面
const adminSession = ref(null)
const adminRoute = ref(null) // null | 'login' | 'panel'
const isAdmin = computed(() => session.value?.user?.role === 0)

function onLogin(payload) {
  session.value = { user: payload.user, token: payload.token }
}

function logout() {
  localStorage.removeItem(TOKEN_KEY)
  session.value = null
}

/* ---------------- 后台管理系统 ---------------- */
function enterAdmin() {
  if (!isAdmin.value) return
  // 已登录过后台则直接进入管理台，否则先到登录页
  adminRoute.value = adminSession.value ? 'panel' : 'login'
  window.scrollTo({ top: 0 })
}

function onAdminSuccess(admin) {
  if (admin?.role !== 0) return
  adminSession.value = admin
  localStorage.setItem(ADMIN_KEY, JSON.stringify(admin))
  localStorage.setItem('boonie_admin_token', admin.token || 'admin-token')
  adminRoute.value = 'panel'
  window.scrollTo({ top: 0 })
}

function closeAdmin() {
  adminRoute.value = null
  window.scrollTo({ top: 0 })
}

function adminLogout() {
  adminSession.value = null
  localStorage.removeItem(ADMIN_KEY)
  localStorage.removeItem('boonie_admin_token')
  adminRoute.value = 'login'
  window.scrollTo({ top: 0 })
}

// 仅用于前端预览：地址栏加 ?demo=1 可直接进入首页（无需启动后端）
onMounted(() => {
  const isDemo = new URLSearchParams(location.search).get('demo') === '1'
  if (isDemo && !session.value) {
    session.value = {
      user: { id: 1, name: '熊大', email: 'admin@boonie.com', role: 0 },
      token: 'demo-token'
    }
  }

  // 恢复后台登录态（刷新页面不用重新登录）
  try {
    const saved = JSON.parse(localStorage.getItem(ADMIN_KEY) || 'null')
    if (saved?.role === 0) adminSession.value = saved
    else localStorage.removeItem(ADMIN_KEY)
  } catch {
    /* 忽略损坏的缓存 */
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
