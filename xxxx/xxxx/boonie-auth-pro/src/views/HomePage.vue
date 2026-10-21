<template>
  <div class="home">
    <!-- ===================== 顶部导航栏 ===================== -->
    <header class="nav" :class="{ scrolled }">
      <div class="nav-inner">
        <a class="logo" href="#" @click.prevent="goHome">
          <span class="logo-badge">🐻</span>
          <span class="logo-text">熊出没<em>狗熊岭</em></span>
        </a>

        <nav class="nav-links">
          <button
            v-for="m in menus"
            :key="m.key"
            type="button"
            class="nav-link"
            :class="{ active: activeMenu === m.key }"
            @click="switchMenu(m)"
          >
            <span class="ico">{{ m.icon }}</span>{{ m.label }}
          </button>
        </nav>

        <div class="nav-right">
          <button type="button" class="icon-btn" title="搜索" @click="focusSearch">🔍</button>
          <button type="button" class="icon-btn" title="通知" @click="notReady('通知中心')">
            🔔<i class="dot"></i>
          </button>

          <div class="user" :class="{ open: menuOpen }">
            <button type="button" class="user-btn" @click="menuOpen = !menuOpen">
              <span class="avatar">{{ avatarText }}</span>
              <span class="uname">{{ userName }}</span>
              <span class="caret">▾</span>
            </button>

            <transition name="drop">
              <div v-if="menuOpen" class="user-menu">
                <div class="um-head">
                  <span class="avatar big">{{ avatarText }}</span>
                  <div class="um-info">
                    <strong>{{ userName }}</strong>
                    <small>{{ userEmail }}</small>
                  </div>
                </div>
                <button type="button" @click="notReady('个人主页')">🧑‍🌾 我的主页</button>
                <button type="button" @click="notReady('设置')">⚙️ 账号设置</button>
                <button type="button" class="logout" @click="doLogout">🚪 退出登录</button>
              </div>
            </transition>
          </div>

          <button type="button" class="burger" title="菜单" @click="drawerOpen = true">☰</button>
        </div>
      </div>
    </header>

    <!-- ===================== Hero + 搜索栏 ===================== -->
    <div class="container hero-wrap">
      <section class="hero">
        <div class="sun"></div>
        <div class="clouds">
          <span
            v-for="c in clouds"
            :key="'cloud-' + c.id"
            class="cloud"
            :style="{
              top: c.top,
              fontSize: c.size,
              animationDuration: c.dur + 's',
              animationDelay: c.delay + 's'
            }"
            >☁️</span
          >
        </div>
        <div class="leaves">
          <span
            v-for="l in leaves"
            :key="'leaf-' + l.id"
            class="leaf"
            :style="{
              left: l.left,
              animationDuration: l.dur + 's',
              animationDelay: l.delay + 's'
            }"
            >{{ l.icon }}</span
          >
        </div>

        <div class="hero-inner">
          <p class="greet">{{ greeting }}，{{ userName }} 🐻</p>
          <h1>今天想在狗熊岭<span class="hl">找点什么？</span></h1>

          <!-- 搜索栏 -->
          <form class="searchbar" novalidate @submit.prevent="onSearch">
            <span class="s-ico">🔍</span>
            <input
              ref="searchEl"
              v-model.trim="keyword"
              type="search"
              name="keyword"
              placeholder="搜索树木、蜂蜜、冒险任务…"
              autocomplete="off"
            />
            <button
              v-if="keyword"
              type="button"
              class="s-clear"
              title="清空"
              @click="keyword = ''"
            >
              ✕
            </button>
            <button type="submit" class="s-btn">搜索</button>
          </form>

          <div class="hot">
            <span class="hot-label">🔥 热门搜索</span>
            <button
              v-for="h in hotWords"
              :key="h"
              type="button"
              class="hot-tag"
              @click="quickSearch(h)"
            >
              {{ h }}
            </button>
          </div>
        </div>
      </section>

      <!-- 搜索结果（接口未接入时的占位） -->
      <transition name="fade">
        <section v-if="searched" class="result">
          <div class="result-head">
            <span>搜索「<b>{{ searched }}</b>」的结果</span>
            <button type="button" class="result-close" @click="clearResult">收起 ✕</button>
          </div>
          <div class="result-empty">
            <span class="big">🍃</span>
            <p>这里暂时空空如也～</p>
            <small>
              搜索接口待接入：<code>GET {{ API.search }}?keyword={{ searched }}</code>
            </small>
          </div>
        </section>
      </transition>
    </div>

    <!-- ===================== 主体内容 ===================== -->
    <main class="container body">
      <div class="col-main">
        <section class="block">
          <div class="block-head">
            <h2>快速入口</h2>
            <span class="block-sub">其他页面暂未开发</span>
          </div>
          <div class="quick-grid">
            <button
              v-for="q in quickEntries"
              :key="q.title"
              type="button"
              class="quick"
              @click="notReady(q.title)"
            >
              <span class="quick-ico">{{ q.icon }}</span>
              <strong>{{ q.title }}</strong>
              <small>{{ q.desc }}</small>
              <em class="soon">敬请期待</em>
            </button>
          </div>
        </section>

        <section class="block">
          <div class="block-head">
            <h2>森林推荐</h2>
            <span class="block-sub">示例数据 · 待接入 {{ API.feed }}</span>
          </div>
          <ul class="feed">
            <li v-for="f in feedList" :key="f.id" class="feed-item" @click="notReady(f.title)">
              <span class="thumb">{{ f.icon }}</span>
              <div class="feed-main">
                <h3>{{ f.title }}</h3>
                <p>{{ f.desc }}</p>
                <div class="meta">
                  <span class="tag">{{ f.tag }}</span>
                  <span class="hot-num">🔥 {{ f.hot }}</span>
                </div>
              </div>
              <span class="arrow">›</span>
            </li>
          </ul>
        </section>
      </div>

      <aside class="col-side">
        <div class="side-card">
          <h3>📢 森林公告</h3>
          <ul class="notice">
            <li v-for="n in notices" :key="n"><i></i>{{ n }}</li>
          </ul>
        </div>

        <div class="side-card weather">
          <h3>🌤 森林天气</h3>
          <div class="w-now">
            <span class="w-ico">🌤</span>
            <div>
              <strong>18°C</strong>
              <small>狗熊岭 · 晴</small>
            </div>
          </div>
          <div class="w-meta">
            <span>湿度 62%</span><span>风力 2 级</span><span>适合巡逻</span>
          </div>
        </div>

        <div class="side-card api">
          <h3>🔧 后端接口预留</h3>
          <p class="api-tip">以下接口尚未实现，前端已按约定路径预留：</p>
          <ul>
            <li v-for="a in apiList" :key="a.path">
              <span class="method">{{ a.method }}</span>
              <code>{{ a.path }}</code>
              <em>{{ a.name }}</em>
            </li>
          </ul>
        </div>
      </aside>
    </main>

    <footer class="foot">
      🐻 熊出没 · 狗熊岭森林 — 当前为纯前端演示，数据接口待后端接入
    </footer>

    <!-- 移动端抽屉菜单 -->
    <transition name="mask">
      <div v-if="drawerOpen" class="drawer-mask" @click.self="drawerOpen = false">
        <aside class="drawer">
          <div class="drawer-head">
            <span class="avatar">{{ avatarText }}</span>
            <div class="um-info">
              <strong>{{ userName }}</strong>
              <small>{{ userEmail }}</small>
            </div>
          </div>
          <button
            v-for="m in menus"
            :key="'d-' + m.key"
            type="button"
            :class="{ active: activeMenu === m.key }"
            @click="switchMenu(m); drawerOpen = false"
          >
            <span>{{ m.icon }}</span>{{ m.label }}
          </button>
          <button type="button" class="logout" @click="doLogout">
            <span>🚪</span>退出登录
          </button>
        </aside>
      </div>
    </transition>

    <!-- 轻提示 -->
    <transition name="toast">
      <div v-if="toast" class="toast">{{ toast }}</div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  user: { type: Object, default: null }
})
const emit = defineEmits(['logout'])

/* ------------------------------------------------------------------
 * 后端接口预留：当前只做前端页面，接口路径先占位。
 * 接入时把对应区域的示例数据换成 fetch(API.xxx) 即可，无需改动结构。
 * ------------------------------------------------------------------ */
const API = {
  feed: '/api/home/feed',
  search: '/api/search',
  notice: '/api/notice',
  profile: '/api/user/profile'
}
const apiList = [
  { method: 'GET', path: '/api/home/feed', name: '首页推荐流' },
  { method: 'GET', path: '/api/search', name: '内容搜索' },
  { method: 'GET', path: '/api/notice', name: '公告列表' },
  { method: 'GET', path: '/api/user/profile', name: '我的资料' }
]

const userName = computed(() => props.user?.name || '森林访客')
const userEmail = computed(() => props.user?.email || '未获取到邮箱')
const avatarText = computed(() => (userName.value || '熊').slice(0, 1))

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '夜深了'
  if (h < 11) return '早上好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

/* 导航栏菜单（除首页外均为占位） */
const menus = [
  { key: 'home', icon: '🏡', label: '首页' },
  { key: 'square', icon: '🎋', label: '森林广场' },
  { key: 'shop', icon: '🍯', label: '蜂蜜商店' },
  { key: 'mine', icon: '🐾', label: '我的园地' }
]
const activeMenu = ref('home')
const menuOpen = ref(false)
const drawerOpen = ref(false)
const scrolled = ref(false)

/* 搜索栏 */
const keyword = ref('')
const searched = ref('')
const searchEl = ref(null)
const hotWords = ['蜂蜜', '古树巡护', '森林运动会', '光头强', '森林防火', '蘑菇采集']

/* 示例数据（待接口接入） */
const quickEntries = [
  { icon: '🎋', title: '森林广场', desc: '看看大家在聊什么' },
  { icon: '🍯', title: '蜂蜜商店', desc: '用松果兑换森林好物' },
  { icon: '📜', title: '任务中心', desc: '完成每日森林任务' },
  { icon: '🏆', title: '熊岭排行榜', desc: '最勤劳的小动物是谁' }
]
const feedList = ref([
  { id: 1, icon: '🍯', title: '光头强的蜂蜜搬运计划', desc: '森林北坡发现巨型蜂巢，熊二已经出发了…', tag: '冒险', hot: '2.1w' },
  { id: 2, icon: '🌳', title: '狗熊岭古树巡护日记', desc: '第 108 号古樟需要搭建防护围栏。', tag: '公益', hot: '8.6k' },
  { id: 3, icon: '🏆', title: '森林运动会报名中', desc: '拔河 / 攀树 / 采蜜三项赛，等你来挑战。', tag: '活动', hot: '5.3k' },
  { id: 4, icon: '🎣', title: '溪流垂钓小课堂', desc: '新手也能钓上大鲤鱼的三个小技巧。', tag: '教程', hot: '3.9k' }
])
const notices = [
  '森林运动会将于本周六在中央草坪开幕',
  '北坡蜂巢区域暂时封闭，注意绕行',
  '春季植树活动开始招募志愿者啦',
  '首页数据接口正在接入中，敬请期待'
]

/* 装饰：云朵 & 飘落的树叶 */
const clouds = [
  { id: 1, top: '10%', size: '34px', dur: 28, delay: 0 },
  { id: 2, top: '42%', size: '24px', dur: 36, delay: 9 }
]
const leaves = [
  { id: 1, left: '8%', icon: '🍃', dur: 10, delay: 0 },
  { id: 2, left: '28%', icon: '🍂', dur: 13, delay: 2 },
  { id: 3, left: '52%', icon: '🍃', dur: 11, delay: 4 },
  { id: 4, left: '74%', icon: '🍂', dur: 14, delay: 1 },
  { id: 5, left: '92%', icon: '🍃', dur: 12, delay: 6 }
]

/* 轻提示 */
const toast = ref('')
let toastTimer = null
function showToast(msg) {
  toast.value = msg
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => (toast.value = ''), 2600)
}

/* 交互 */
function goHome() {
  activeMenu.value = 'home'
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
function switchMenu(m) {
  if (m.key === 'home') {
    goHome()
    return
  }
  activeMenu.value = 'home'
  notReady(m.label)
}
function notReady(name) {
  menuOpen.value = false
  showToast(`🚧「${name}」页面 / 接口尚未开发～`)
}
function focusSearch() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
  searchEl.value && searchEl.value.focus()
}
function onSearch() {
  if (!keyword.value) {
    showToast('请先输入搜索内容哦 🔍')
    searchEl.value && searchEl.value.focus()
    return
  }
  searched.value = keyword.value
  // TODO: 接入后端后改为 fetch(`${API.search}?keyword=${encodeURIComponent(keyword.value)}`)
}
function quickSearch(word) {
  keyword.value = word
  searched.value = word
}
function clearResult() {
  searched.value = ''
  keyword.value = ''
}
function doLogout() {
  menuOpen.value = false
  drawerOpen.value = false
  emit('logout')
}

function onScroll() {
  scrolled.value = window.scrollY > 8
}
// 点击头像区域以外的地方时收起下拉菜单（点击菜单本身不收起，由菜单项自己处理）
function onDocClick(e) {
  if (!e.target.closest('.user')) {
    menuOpen.value = false
  }
}

onMounted(() => {
  window.addEventListener('scroll', onScroll, { passive: true })
  document.addEventListener('click', onDocClick)
  showToast(`🎉 欢迎来到狗熊岭，${userName.value}！`)
})
onBeforeUnmount(() => {
  window.removeEventListener('scroll', onScroll)
  document.removeEventListener('click', onDocClick)
  clearTimeout(toastTimer)
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding-bottom: 10px;
}
.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* ---------------- 导航栏 ---------------- */
.nav {
  position: sticky;
  top: 0;
  z-index: 30;
  background: rgba(255, 248, 236, 0.9);
  backdrop-filter: blur(12px);
  border-bottom: 2px solid rgba(200, 155, 99, 0.4);
  transition: box-shadow 0.25s ease;
}
.nav.scrolled {
  box-shadow: 0 8px 24px rgba(47, 125, 79, 0.12);
}
.nav-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 66px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 18px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 9px;
  text-decoration: none;
  flex-shrink: 0;
}
.logo-badge {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: var(--honey);
  display: grid;
  place-items: center;
  font-size: 22px;
  border: 3px solid #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
  animation: bob 4s ease-in-out infinite;
}
@keyframes bob {
  0%,
  100% {
    transform: translateY(0) rotate(-4deg);
  }
  50% {
    transform: translateY(-4px) rotate(4deg);
  }
}
.logo-text {
  font-size: 19px;
  font-weight: 800;
  color: var(--forest-deep);
  letter-spacing: 0.02em;
}
.logo-text em {
  font-style: normal;
  font-size: 12px;
  font-weight: 700;
  color: var(--wood);
  margin-left: 6px;
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(200, 155, 99, 0.16);
}

.nav-links {
  display: flex;
  gap: 4px;
  margin-left: 10px;
  flex: 1;
}
.nav-link {
  border: none;
  background: transparent;
  font-size: 14px;
  font-weight: 700;
  color: var(--muted);
  padding: 9px 14px;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.18s ease;
}
.nav-link .ico {
  font-size: 15px;
}
.nav-link:hover {
  background: rgba(47, 125, 79, 0.09);
  color: var(--forest-deep);
}
.nav-link.active {
  color: var(--forest-deep);
  background: rgba(47, 125, 79, 0.12);
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}
.icon-btn {
  position: relative;
  width: 38px;
  height: 38px;
  border-radius: 12px;
  border: 2px solid transparent;
  background: rgba(255, 255, 255, 0.7);
  font-size: 17px;
  cursor: pointer;
  transition: all 0.18s ease;
}
.icon-btn:hover {
  border-color: var(--wood-light);
  transform: translateY(-1px);
}
.icon-btn .dot {
  position: absolute;
  top: 7px;
  right: 8px;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--danger);
  border: 1.5px solid #fff;
}

.user {
  position: relative;
}
.user-btn {
  display: flex;
  align-items: center;
  gap: 7px;
  height: 40px;
  padding: 0 12px 0 6px;
  border-radius: 999px;
  border: 2px solid var(--wood-light);
  background: #fff;
  cursor: pointer;
  transition: all 0.18s ease;
}
.user-btn:hover,
.user.open .user-btn {
  border-color: var(--forest);
  box-shadow: 0 0 0 3px rgba(47, 125, 79, 0.14);
}
.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(160deg, var(--leaf), var(--forest));
  color: #fff;
  font-size: 14px;
  font-weight: 800;
  display: grid;
  place-items: center;
  flex-shrink: 0;
}
.avatar.big {
  width: 40px;
  height: 40px;
  font-size: 18px;
}
.uname {
  font-size: 14px;
  font-weight: 700;
  color: var(--forest-deep);
  max-width: 90px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.caret {
  font-size: 11px;
  color: var(--muted);
}
.user-menu {
  position: absolute;
  right: 0;
  top: 50px;
  width: 224px;
  background: #fff;
  border: 3px solid var(--wood-light);
  border-radius: 16px;
  box-shadow: 0 18px 40px rgba(47, 125, 79, 0.2);
  padding: 10px;
  z-index: 40;
}
.um-head {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 8px 12px;
  border-bottom: 2px dashed #f0e6d6;
  margin-bottom: 8px;
}
.um-info {
  min-width: 0;
}
.um-info strong {
  display: block;
  font-size: 14px;
  color: var(--forest-deep);
}
.um-info small {
  display: block;
  font-size: 12px;
  color: var(--muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.user-menu button,
.drawer button {
  width: 100%;
  text-align: left;
  border: none;
  background: transparent;
  font-size: 13px;
  font-weight: 600;
  color: var(--text);
  padding: 10px;
  border-radius: 10px;
  cursor: pointer;
  font-family: inherit;
  transition: background 0.15s ease;
}
.user-menu button:hover,
.drawer button:hover {
  background: rgba(47, 125, 79, 0.1);
}
.user-menu button.logout,
.drawer button.logout {
  color: var(--danger);
}
.user-menu button.logout:hover,
.drawer button.logout:hover {
  background: rgba(228, 87, 46, 0.1);
}
.drop-enter-active,
.drop-leave-active {
  transition: all 0.2s ease;
}
.drop-enter-from,
.drop-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
.burger {
  display: none;
  width: 38px;
  height: 38px;
  border-radius: 12px;
  border: 2px solid var(--wood-light);
  background: #fff;
  font-size: 17px;
  cursor: pointer;
}

/* ---------------- Hero + 搜索 ---------------- */
.hero-wrap {
  margin-top: 22px;
}
.hero {
  position: relative;
  overflow: hidden;
  border-radius: 26px;
  padding: 52px 44px 44px;
  background: linear-gradient(160deg, #3a9d5d 0%, #1f5c39 100%);
  color: #fff;
  box-shadow: 0 18px 46px rgba(31, 92, 57, 0.28);
}
.hero::after {
  content: "🌲🌲🌲🌲🌲🌲🌲";
  position: absolute;
  bottom: -6px;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 30px;
  letter-spacing: 8px;
  opacity: 0.4;
  pointer-events: none;
}
.hero-inner {
  position: relative;
  z-index: 2;
  max-width: 700px;
}
.greet {
  font-size: 15px;
  font-weight: 700;
  opacity: 0.95;
  margin-bottom: 10px;
}
.hero h1 {
  font-size: 32px;
  font-weight: 800;
  line-height: 1.35;
  text-shadow: 0 2px 0 rgba(0, 0, 0, 0.14);
}
.hero h1 .hl {
  color: #ffe083;
}

.searchbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 26px;
  background: #fff;
  border-radius: 999px;
  padding: 7px 7px 7px 18px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.18);
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}
.searchbar:focus-within {
  box-shadow: 0 16px 36px rgba(0, 0, 0, 0.24), 0 0 0 3px rgba(255, 183, 3, 0.5);
  transform: translateY(-2px);
}
.s-ico {
  font-size: 17px;
}
.searchbar input {
  flex: 1;
  min-width: 0;
  height: 44px;
  border: none;
  outline: none;
  background: transparent;
  font-size: 15px;
  color: var(--text);
  font-family: inherit;
}
.searchbar input::-webkit-search-cancel-button {
  display: none;
}
.s-clear {
  border: none;
  background: #f2ece0;
  color: var(--muted);
  width: 24px;
  height: 24px;
  border-radius: 50%;
  font-size: 11px;
  cursor: pointer;
  flex-shrink: 0;
}
.s-clear:hover {
  background: #e7dcc8;
}
.s-btn {
  flex-shrink: 0;
  height: 44px;
  padding: 0 30px;
  border: none;
  border-radius: 999px;
  background: linear-gradient(180deg, #ffc83d 0%, var(--honey) 100%);
  box-shadow: 0 4px 0 #e09b00;
  color: #5a3d00;
  font-size: 15px;
  font-weight: 800;
  font-family: inherit;
  cursor: pointer;
  transition: transform 0.08s ease, box-shadow 0.08s ease, filter 0.15s ease;
}
.s-btn:hover {
  filter: brightness(1.04);
}
.s-btn:active {
  transform: translateY(3px);
  box-shadow: 0 1px 0 #e09b00;
}

.hot {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 18px;
}
.hot-label {
  font-size: 13px;
  font-weight: 700;
  opacity: 0.92;
}
.hot-tag {
  border: 1.5px solid rgba(255, 255, 255, 0.5);
  background: rgba(255, 255, 255, 0.14);
  color: #fff;
  font-size: 12.5px;
  font-weight: 600;
  font-family: inherit;
  padding: 5px 13px;
  border-radius: 999px;
  cursor: pointer;
  transition: all 0.18s ease;
}
.hot-tag:hover {
  background: rgba(255, 255, 255, 0.28);
  transform: translateY(-1px);
}

/* Hero 装饰 */
.sun {
  position: absolute;
  top: 34px;
  right: 46px;
  width: 68px;
  height: 68px;
  border-radius: 50%;
  background: radial-gradient(circle at 50% 50%, #fff3b0 0%, #ffd23f 60%, #ffb703 100%);
  box-shadow: 0 0 40px 12px rgba(255, 211, 63, 0.5);
  animation: sunPulse 5s ease-in-out infinite;
}
@keyframes sunPulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.08);
  }
}
.clouds,
.leaves {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}
.cloud {
  position: absolute;
  opacity: 0.8;
  animation: drift linear infinite;
}
@keyframes drift {
  from {
    transform: translateX(-40%);
  }
  to {
    transform: translateX(140%);
  }
}
.leaf {
  position: absolute;
  top: -34px;
  font-size: 19px;
  animation: fall linear infinite;
}
@keyframes fall {
  0% {
    transform: translateY(-34px) rotate(0deg);
    opacity: 0;
  }
  12% {
    opacity: 1;
  }
  100% {
    transform: translateY(430px) rotate(340deg);
    opacity: 0.85;
  }
}

/* ---------------- 搜索结果 ---------------- */
.result {
  margin-top: 16px;
  background: #fff;
  border: 3px solid var(--wood-light);
  border-radius: var(--radius);
  padding: 18px 20px;
  box-shadow: 0 12px 30px rgba(47, 125, 79, 0.12);
}
.result-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  color: var(--forest-deep);
  font-weight: 700;
}
.result-head b {
  color: var(--forest);
}
.result-close {
  border: none;
  background: transparent;
  font-size: 12px;
  color: var(--muted);
  cursor: pointer;
  font-family: inherit;
}
.result-empty {
  text-align: center;
  padding: 22px 0 10px;
}
.result-empty .big {
  font-size: 34px;
}
.result-empty p {
  margin: 8px 0 6px;
  font-size: 14px;
  color: var(--text);
  font-weight: 600;
}
.result-empty code {
  font-size: 12px;
  color: var(--muted);
  background: #f7f1e6;
  padding: 3px 8px;
  border-radius: 6px;
  font-family: Consolas, Monaco, monospace;
}
.fade-enter-active,
.fade-leave-active {
  transition: all 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

/* ---------------- 主体 ---------------- */
.body {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 22px;
  align-items: start;
  margin-top: 26px;
}
.block {
  margin-bottom: 22px;
}
.block-head {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 12px;
}
.block-head h2 {
  font-size: 18px;
  font-weight: 800;
  color: var(--forest-deep);
}
.block-sub {
  font-size: 12px;
  color: var(--muted);
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}
.quick {
  position: relative;
  text-align: left;
  background: #fff;
  border: 3px solid var(--wood-light);
  border-radius: var(--radius);
  padding: 18px 16px 16px;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.22s cubic-bezier(0.22, 1, 0.36, 1);
}
.quick:hover {
  transform: translateY(-4px);
  border-color: var(--forest);
  box-shadow: 0 16px 34px rgba(47, 125, 79, 0.2);
}
.quick-ico {
  display: block;
  font-size: 28px;
  margin-bottom: 8px;
}
.quick strong {
  display: block;
  font-size: 15px;
  color: var(--forest-deep);
  margin-bottom: 3px;
}
.quick small {
  display: block;
  font-size: 12px;
  color: var(--muted);
  line-height: 1.5;
}
.soon {
  position: absolute;
  top: 12px;
  right: 12px;
  font-style: normal;
  font-size: 10px;
  font-weight: 700;
  color: var(--wood);
  background: rgba(200, 155, 99, 0.16);
  padding: 2px 7px;
  border-radius: 999px;
}

.feed {
  list-style: none;
}
.feed-item {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #fff;
  border: 3px solid var(--wood-light);
  border-radius: var(--radius);
  padding: 14px 16px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.22s cubic-bezier(0.22, 1, 0.36, 1);
}
.feed-item:hover {
  transform: translateX(4px);
  border-color: var(--forest);
  box-shadow: 0 14px 30px rgba(47, 125, 79, 0.18);
}
.feed-item .thumb {
  width: 54px;
  height: 54px;
  flex-shrink: 0;
  border-radius: 14px;
  background: linear-gradient(160deg, #e8f6ec, #d3ecdb);
  display: grid;
  place-items: center;
  font-size: 26px;
}
.feed-main {
  min-width: 0;
  flex: 1;
}
.feed-main h3 {
  font-size: 15px;
  font-weight: 800;
  color: var(--forest-deep);
  margin-bottom: 3px;
}
.feed-main p {
  font-size: 13px;
  color: var(--muted);
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 7px;
}
.tag {
  font-size: 11px;
  font-weight: 700;
  color: var(--forest);
  background: rgba(47, 125, 79, 0.12);
  padding: 2px 9px;
  border-radius: 999px;
}
.hot-num {
  font-size: 12px;
  color: var(--muted);
}
.arrow {
  font-size: 22px;
  color: var(--wood-light);
  flex-shrink: 0;
}

.col-side {
  display: grid;
  gap: 16px;
}
.side-card {
  background: #fff;
  border: 3px solid var(--wood-light);
  border-radius: var(--radius);
  padding: 18px;
  box-shadow: 0 10px 26px rgba(47, 125, 79, 0.1);
}
.side-card h3 {
  font-size: 15px;
  font-weight: 800;
  color: var(--forest-deep);
  margin-bottom: 12px;
}
.notice {
  list-style: none;
  display: grid;
  gap: 10px;
}
.notice li {
  display: flex;
  gap: 9px;
  font-size: 13px;
  color: var(--text);
  line-height: 1.6;
}
.notice li i {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--honey);
  margin-top: 8px;
  flex-shrink: 0;
}
.w-now {
  display: flex;
  align-items: center;
  gap: 12px;
}
.w-ico {
  font-size: 40px;
}
.w-now strong {
  display: block;
  font-size: 24px;
  font-weight: 800;
  color: var(--forest-deep);
}
.w-now small {
  font-size: 12px;
  color: var(--muted);
}
.w-meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 12px;
}
.w-meta span {
  font-size: 11.5px;
  color: var(--forest-deep);
  background: rgba(47, 125, 79, 0.1);
  padding: 3px 9px;
  border-radius: 999px;
}
.api .api-tip {
  font-size: 12px;
  color: var(--muted);
  line-height: 1.6;
  margin-bottom: 10px;
}
.api ul {
  list-style: none;
  display: grid;
  gap: 8px;
}
.api li {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 12px;
  flex-wrap: wrap;
}
.api .method {
  font-size: 10px;
  font-weight: 800;
  color: #fff;
  background: var(--forest);
  padding: 2px 6px;
  border-radius: 5px;
  letter-spacing: 0.02em;
}
.api code {
  font-family: Consolas, Monaco, monospace;
  color: var(--text);
  background: #f7f1e6;
  padding: 2px 7px;
  border-radius: 6px;
}
.api em {
  font-style: normal;
  color: var(--muted);
}

.foot {
  margin-top: 14px;
  text-align: center;
  font-size: 12.5px;
  color: var(--muted);
}

/* ---------------- 移动端抽屉 ---------------- */
.drawer-mask {
  position: fixed;
  inset: 0;
  background: rgba(31, 92, 57, 0.35);
  z-index: 60;
  display: flex;
  justify-content: flex-end;
}
.drawer {
  width: 260px;
  background: var(--cream);
  height: 100%;
  padding: 22px 16px;
  box-shadow: -12px 0 30px rgba(0, 0, 0, 0.2);
  display: grid;
  gap: 4px;
  align-content: start;
}
.drawer-head {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 14px;
  margin-bottom: 10px;
  border-bottom: 2px dashed var(--wood-light);
}
.drawer button {
  display: flex;
  align-items: center;
  gap: 9px;
  font-size: 14px;
}
.drawer button.active {
  background: rgba(47, 125, 79, 0.12);
  color: var(--forest-deep);
}
.mask-enter-active,
.mask-leave-active {
  transition: opacity 0.22s ease;
}
.mask-enter-from,
.mask-leave-to {
  opacity: 0;
}

/* ---------------- 轻提示 ---------------- */
.toast {
  position: fixed;
  left: 50%;
  bottom: 34px;
  transform: translateX(-50%);
  background: rgba(31, 92, 57, 0.95);
  color: #fff;
  padding: 12px 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
  z-index: 80;
  max-width: calc(100vw - 40px);
}
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(12px);
}

/* ---------------- 响应式 ---------------- */
@media (max-width: 1080px) {
  .quick-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .body {
    grid-template-columns: 1fr;
  }
}
@media (max-width: 760px) {
  .nav-inner {
    height: 60px;
  }
  .nav-links {
    display: none;
  }
  .burger {
    display: block;
  }
  .uname,
  .caret {
    display: none;
  }
  .user-btn {
    padding: 0 6px;
  }
  .hero {
    padding: 36px 22px 30px;
    border-radius: 20px;
  }
  .hero h1 {
    font-size: 23px;
  }
  .sun {
    width: 52px;
    height: 52px;
    right: 20px;
    top: 20px;
  }
  .searchbar {
    flex-wrap: wrap;
    border-radius: 20px;
    padding: 10px 12px;
  }
  .s-btn {
    width: 100%;
    padding: 0;
  }
  .quick-grid {
    grid-template-columns: 1fr 1fr;
    gap: 10px;
  }
}
</style>
