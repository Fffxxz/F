<template>
  <main class="panel">
    <!-- 登录 / 注册成功后的欢迎界面 -->
    <div v-if="status === 'success'" class="card welcome">
      <div class="confetti">
        <span v-for="c in confetti" :key="c.id" class="piece" :style="c.style">🍃</span>
      </div>
      <div class="bear-badge">🐻</div>
      <h2>{{ isRegister ? '注册成功！' : '登录成功！' }}</h2>
      <div class="sub">欢迎来到狗熊岭森林</div>
      <p class="welcome-text">
        🎉 你好，{{ user.name }}！<br />
        和熊大熊二一起守护森林吧～
      </p>
      <button class="submit" type="button" @click="logout">
        {{ isRegister ? '🚪 去登录' : '🚪 退出登录' }}
      </button>
    </div>

    <!-- 登录 / 注册表单（同一张卡片，顶部 Tab 切换） -->
    <form
      v-else
      class="card"
      :class="{ shake, [mode]: true }"
      novalidate
      @submit.prevent="onSubmit"
      @animationend="onAnimEnd"
    >
      <div class="bear-badge">🐻</div>

      <!-- 模式切换（带滑动指示条） -->
      <div class="tabs">
        <button
          type="button"
          :class="{ active: mode === 'login' }"
          @click="setMode('login')"
        >
          森林登录
        </button>
        <button
          type="button"
          :class="{ active: mode === 'register' }"
          @click="setMode('register')"
        >
          创建账号
        </button>
        <span class="tab-indicator" :class="mode"></span>
      </div>

      <h2>{{ mode === 'login' ? '欢迎回来' : '创建你的森林账号' }}</h2>
      <div class="sub">
        {{ mode === 'login' ? '和熊大熊二一起玩' : '和熊大熊二一起守护狗熊岭' }}
      </div>

      <!-- 登录：密码 / 验证码 子切换 -->
      <div v-if="mode === 'login'" class="seg">
        <button
          type="button"
          :class="{ active: loginType === 'password' }"
          @click="loginType = 'password'"
        >
          🔑 密码登录
        </button>
        <button
          type="button"
          :class="{ active: loginType === 'code' }"
          @click="loginType = 'code'"
        >
          📩 验证码登录
        </button>
      </div>

      <!-- 注册：用户名 -->
      <div v-if="mode === 'register'" class="field">
        <label for="username">🐾 用户名</label>
        <div class="input-wrap">
          <input
            id="username"
            v-model.trim="username"
            ref="usernameEl"
            class="input"
            :class="{ invalid: errors.username }"
            type="text"
            name="username"
            placeholder="例如：bear_fan"
            autocomplete="username"
            maxlength="20"
          />
        </div>
        <div class="err">{{ errors.username }}</div>
      </div>

      <!-- 邮箱 -->
      <div class="field">
        <label for="email">📮 邮箱</label>
        <div class="input-wrap">
          <input
            id="email"
            v-model.trim="email"
            ref="emailEl"
            class="input"
            :class="{ invalid: errors.email }"
            type="email"
            name="email"
            placeholder="you@example.com"
            autocomplete="email"
          />
        </div>
        <div class="err">{{ errors.email }}</div>
      </div>

      <!-- 密码登录：仅密码 -->
      <div v-if="mode === 'login' && loginType === 'password'" class="field">
        <label for="password">🔑 密码</label>
        <div class="input-wrap">
          <input
            id="password"
            v-model="password"
            ref="passwordEl"
            class="input"
            :class="{ invalid: errors.password }"
            :type="showPassword ? 'text' : 'password'"
            name="password"
            placeholder="请输入密码"
            autocomplete="current-password"
          />
          <button
            type="button"
            class="toggle-pwd"
            @click="showPassword = !showPassword"
          >
            {{ showPassword ? '隐藏' : '显示' }}
          </button>
        </div>
        <div class="err">{{ errors.password }}</div>
      </div>

      <!-- 验证码登录：验证码 + 获取按钮 -->
      <div v-if="mode === 'login' && loginType === 'code'" class="field">
        <label for="code">📩 验证码</label>
        <div class="input-wrap code-wrap">
          <input
            id="code"
            v-model.trim="code"
            ref="codeEl"
            class="input"
            :class="{ invalid: errors.code }"
            type="text"
            inputmode="numeric"
            name="code"
            placeholder="6 位验证码"
            maxlength="6"
            autocomplete="one-time-code"
          />
          <button
            type="button"
            class="send-code"
            :disabled="codeCountdown > 0 || sendingCode"
            @click="sendCode"
          >
            {{ sendingCode ? '发送中…' : codeCountdown > 0 ? codeCountdown + 's' : '获取验证码' }}
          </button>
        </div>
        <div class="err">{{ errors.code }}</div>
        <div class="hint">演示环境：点击「获取验证码」后，验证码会在提示中显示</div>
      </div>

      <!-- 注册：密码 + 强度 + 校验清单 -->
      <div v-if="mode === 'register'" class="field">
        <label for="password">🔑 密码</label>
        <div class="input-wrap">
          <input
            id="password"
            v-model="password"
            ref="passwordEl"
            class="input"
            :class="{ invalid: errors.password }"
            :type="showPassword ? 'text' : 'password'"
            name="password"
            placeholder="至少 6 位"
            autocomplete="new-password"
          />
          <button
            type="button"
            class="toggle-pwd"
            @click="showPassword = !showPassword"
          >
            {{ showPassword ? '隐藏' : '显示' }}
          </button>
        </div>
        <!-- 密码强度 -->
        <div v-if="password" class="strength">
          <span class="bar" :class="score >= 1 ? levelCls : ''"></span>
          <span class="bar" :class="score >= 2 ? levelCls : ''"></span>
          <span class="bar" :class="score >= 3 ? levelCls : ''"></span>
          <span class="bar" :class="score >= 4 ? levelCls : ''"></span>
          <em class="strength-text">{{ strengthLabel }}</em>
        </div>
        <!-- 密码校验清单（动态打勾） -->
        <ul class="pwd-check">
          <li
            v-for="(c, i) in pwdChecks"
            :key="i"
            :class="{ ok: c.ok }"
          >
            <span class="tick">{{ c.ok ? '✅' : '⬜' }}</span>{{ c.label }}
          </li>
        </ul>
        <div class="err">{{ errors.password }}</div>
      </div>

      <!-- 注册：确认密码 -->
      <div v-if="mode === 'register'" class="field">
        <label for="confirm">🔒 确认密码</label>
        <div class="input-wrap">
          <input
            id="confirm"
            v-model="confirm"
            class="input"
            :class="{ invalid: errors.confirm }"
            :type="showConfirm ? 'text' : 'password'"
            name="confirm"
            placeholder="再次输入密码"
            autocomplete="new-password"
          />
          <button
            type="button"
            class="toggle-pwd"
            @click="showConfirm = !showConfirm"
          >
            {{ showConfirm ? '隐藏' : '显示' }}
          </button>
        </div>
        <div class="err">{{ errors.confirm }}</div>
      </div>

      <!-- 注册：同意条款 -->
      <label v-if="mode === 'register'" class="terms">
        <input v-model="agree" type="checkbox" />
        <span>我已阅读并同意《<a href="#">森林公约</a>》</span>
      </label>
      <div v-if="mode === 'register'" class="err">{{ errors.agree }}</div>

      <!-- 登录（密码模式）：记住我 -->
      <div v-if="mode === 'login' && loginType === 'password'" class="row">
        <label><input v-model="remember" type="checkbox" /> 记住我</label>
        <a href="#">忘记密码？</a>
      </div>

      <div class="err login-err">{{ formError }}</div>

      <button type="submit" class="submit" :disabled="submitting">
        {{
          submitting
            ? submitText
            : mode === 'login'
              ? '🚪 进入森林'
              : '🌲 注册森林'
        }}
      </button>

      <div class="alt">
        <template v-if="mode === 'login'"
          >还没有账号？<a href="#" @click.prevent="setMode('register')">和熊二一起注册</a></template
        >
        <template v-else
          >已经有账号？<a href="#" @click.prevent="setMode('login')">和熊大一起登录</a></template
        >
      </div>
    </form>

    <!-- 轻提示（验证码等） -->
    <transition name="toast">
      <div v-if="toast" class="toast">{{ toast }}</div>
    </transition>
  </main>
</template>

<script setup>
import { ref, reactive, computed, nextTick, onMounted, onBeforeUnmount } from 'vue'

const mode = ref('login') // login | register
const loginType = ref('password') // password | code
const username = ref('')
const email = ref('')
const password = ref('')
const confirm = ref('')
const code = ref('')
const showPassword = ref(false)
const showConfirm = ref(false)
const agree = ref(false)
const remember = ref(false)
const submitting = ref(false)
const sendingCode = ref(false)
const shake = ref(false)
const codeCountdown = ref(0)
const toast = ref('')
const errors = reactive({ username: '', email: '', password: '', confirm: '', agree: '', code: '' })

const status = ref('idle') // idle | success
const formError = ref('')
const user = ref(null)
const token = ref('')

const emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const userRe = /^[a-zA-Z0-9_]{3,20}$/

const isRegister = computed(() => mode.value === 'register')
const submitText = computed(() =>
  mode.value === 'login'
    ? loginType.value === 'code'
      ? '验证中…'
      : '进入中…'
    : '🌱 注册中…'
)

// ---- 密码强度（仅注册模式使用） ----
const pwdChecks = computed(() => {
  const v = password.value
  return [
    { label: '至少 6 位', ok: v.length >= 6 },
    { label: '含大写 + 小写字母', ok: /[a-z]/.test(v) && /[A-Z]/.test(v) },
    { label: '含数字', ok: /\d/.test(v) },
    { label: '含特殊字符 (!@#$…)', ok: /[^A-Za-z0-9]/.test(v) }
  ]
})
const score = computed(() => pwdChecks.value.filter((c) => c.ok).length)
const strengthLabel = computed(() => {
  const s = score.value
  if (s <= 1) return '弱'
  if (s === 2) return '弱'
  if (s === 3) return '中'
  return '强'
})
const levelCls = computed(() => {
  const s = score.value
  if (s <= 2) return 'on-weak'
  if (s === 3) return 'on-mid'
  return 'on-strong'
})

// ---- 成功页彩带 ----
const confetti = Array.from({ length: 14 }, (_, i) => ({
  id: i,
  style: {
    left: Math.random() * 100 + '%',
    animationDuration: 1.6 + Math.random() * 1.4 + 's',
    animationDelay: Math.random() * 0.4 + 's',
    fontSize: 14 + Math.random() * 12 + 'px'
  }
}))

function setMode(m) {
  if (mode.value === m) return
  mode.value = m
  loginType.value = 'password'
  Object.keys(errors).forEach((k) => (errors[k] = ''))
  formError.value = ''
  code.value = ''
  nextTick(focusFirst)
}
function focusFirst() {
  const el = mode.value === 'register' ? usernameEl.value : emailEl.value
  el && el.focus()
}

const usernameEl = ref(null)
const emailEl = ref(null)
const passwordEl = ref(null)
const codeEl = ref(null)

let toastTimer = null
function showToast(msg) {
  toast.value = msg
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => (toast.value = ''), 6000)
}

let cdTimer = null
function startCountdown(sec) {
  codeCountdown.value = sec
  clearInterval(cdTimer)
  cdTimer = setInterval(() => {
    codeCountdown.value--
    if (codeCountdown.value <= 0) clearInterval(cdTimer)
  }, 1000)
}

async function sendCode() {
  if (!emailRe.test(email.value)) {
    errors.email = '请先填写有效的邮箱'
    triggerShake()
    return
  }
  errors.email = ''
  sendingCode.value = true
  try {
    const res = await fetch('/api/send-code', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email.value })
    })
    const data = await res.json()
    if (!res.ok) {
      formError.value = data.message || '发送失败'
      return
    }
    startCountdown(60)
    // 演示：把验证码展示出来（真实环境应通过邮件/短信下发）
    showToast('📩 演示验证码：' + data.demoCode + '（5 分钟内有效）')
  } catch (e) {
    formError.value = '网络异常，无法发送验证码'
  } finally {
    sendingCode.value = false
  }
}

function validate() {
  Object.keys(errors).forEach((k) => (errors[k] = ''))
  let ok = true

  if (mode.value === 'register') {
    const u = username.value
    if (!u) {
      errors.username = '请输入用户名'
      ok = false
    } else if (!userRe.test(u)) {
      errors.username = '用户名 3-20 位，仅限字母/数字/下划线'
      ok = false
    }
  }

  if (!email.value) {
    errors.email = '请输入邮箱'
    ok = false
  } else if (!emailRe.test(email.value)) {
    errors.email = '请输入有效的邮箱地址'
    ok = false
  }

  if (mode.value === 'login' && loginType.value === 'code') {
    if (!/^\d{6}$/.test(code.value)) {
      errors.code = '请输入 6 位验证码'
      ok = false
    }
  } else {
    if (!password.value) {
      errors.password = '请输入密码'
      ok = false
    } else if (password.value.length < 6) {
      errors.password = '密码至少 6 位哦'
      ok = false
    }
  }

  if (mode.value === 'register') {
    if (!confirm.value) {
      errors.confirm = '请再次输入密码'
      ok = false
    } else if (confirm.value !== password.value) {
      errors.confirm = '两次输入的密码不一致'
      ok = false
    }
    if (!agree.value) {
      errors.agree = '请先同意《森林公约》'
      ok = false
    }
  }
  return ok
}

function triggerShake() {
  shake.value = false
  nextTick(() => {
    shake.value = true
  })
}

async function onSubmit() {
  formError.value = ''
  if (!validate()) {
    triggerShake()
    return
  }

  submitting.value = true
  const isLogin = mode.value === 'login'
  let url = ''
  let body = {}
  if (isLogin) {
    url = '/api/login'
    body =
      loginType.value === 'code'
        ? { email: email.value, code: code.value }
        : { email: email.value, password: password.value }
  } else {
    url = '/api/register'
    body = { username: username.value, email: email.value, password: password.value }
  }

  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    })
    const data = await res.json()
    if (!res.ok) {
      formError.value = data.message || '操作失败，请重试'
      triggerShake()
      return
    }

    if (!isLogin) {
      // 注册成功：自动跳转到登录并预填邮箱，省去再次输入
      showToast('🎉 注册成功！已为你跳转到登录，请输入密码')
      setMode('login')
      email.value = data.user.email
      password.value = ''
      nextTick(() => passwordEl.value && passwordEl.value.focus())
      return
    }

    token.value = data.token
    user.value = data.user
    status.value = 'success'
    if (loginType.value === 'password' && remember.value) {
      localStorage.setItem('boonie_token', data.token)
    }
  } catch (e) {
    formError.value = '网络异常，无法连接服务器'
    triggerShake()
  } finally {
    submitting.value = false
  }
}

function logout() {
  const wasRegister = mode.value === 'register'
  status.value = 'idle'
  user.value = null
  token.value = ''
  password.value = ''
  confirm.value = ''
  code.value = ''
  if (wasRegister) {
    setMode('login')
    username.value = ''
  }
  email.value = ''
  agree.value = false
  formError.value = ''
  localStorage.removeItem('boonie_token')
}

function onAnimEnd(e) {
  if (e.animationName === 'shake') {
    shake.value = false
  }
}

// 页面加载时：若本地存有「记住我」token，则自动恢复登录态
onMounted(async () => {
  const saved = localStorage.getItem('boonie_token')
  if (!saved) return
  try {
    const res = await fetch('/api/me', {
      headers: { Authorization: 'Bearer ' + saved }
    })
    if (res.ok) {
      const data = await res.json()
      token.value = saved
      user.value = data.user
      status.value = 'success'
    } else {
      localStorage.removeItem('boonie_token')
    }
  } catch {
    localStorage.removeItem('boonie_token')
  }
})

onBeforeUnmount(() => {
  clearTimeout(toastTimer)
  clearInterval(cdTimer)
})
</script>

<style scoped>
.panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 22px;
  position: relative;
}
.card {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border: 3px solid var(--wood-light);
  border-radius: var(--radius);
  padding: 38px 32px 30px;
  box-shadow: 0 14px 40px rgba(47, 125, 79, 0.15);
  position: relative;
  animation: cardIn 0.7s cubic-bezier(0.22, 1, 0.36, 1) both;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}
/* 桌面优化：鼠标悬停时卡片轻微上浮 + 光晕 */
@media (hover: hover) and (min-width: 860px) {
  .card:hover {
    transform: translateY(-4px);
    box-shadow: 0 22px 56px rgba(47, 125, 79, 0.22);
  }
}
@keyframes cardIn {
  from {
    opacity: 0;
    transform: translateY(28px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
.card.shake {
  animation: shake 0.45s;
}
@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  20% {
    transform: translateX(-9px) rotate(-1deg);
  }
  40% {
    transform: translateX(9px) rotate(1deg);
  }
  60% {
    transform: translateX(-6px);
  }
  80% {
    transform: translateX(6px);
  }
}
.bear-badge {
  position: absolute;
  top: -42px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 56px;
  background: var(--honey);
  width: 84px;
  height: 84px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4px solid #fff;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  animation: bob 3s ease-in-out infinite;
}
@keyframes bob {
  0%,
  100% {
    transform: translateX(-50%) translateY(0) rotate(-4deg);
  }
  50% {
    transform: translateX(-50%) translateY(-9px) rotate(4deg);
  }
}
.card h2 {
  text-align: center;
  font-size: 22px;
  font-weight: 800;
  margin: 26px 0 4px;
  color: var(--forest-deep);
}
.card .sub {
  text-align: center;
  font-size: 13px;
  color: var(--muted);
  margin-bottom: 24px;
}

/* 模式切换 Tab（含滑动指示条） */
.tabs {
  display: flex;
  position: relative;
  gap: 8px;
  margin-bottom: 22px;
  border-bottom: 2px solid #f0e6d6;
}
.tabs button {
  flex: 1;
  height: 42px;
  border: none;
  background: transparent;
  color: var(--muted);
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: color 0.2s ease;
  z-index: 1;
}
.tabs button.active {
  color: var(--forest-deep);
}
.tab-indicator {
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 50%;
  height: 3px;
  border-radius: 3px;
  background: linear-gradient(90deg, var(--forest), var(--leaf));
  transition: transform 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}
.tab-indicator.register {
  transform: translateX(100%);
}

/* 登录子切换 */
.seg {
  display: flex;
  gap: 6px;
  margin-bottom: 18px;
}
.seg button {
  flex: 1;
  height: 36px;
  border: 2px solid var(--wood-light);
  background: #fffdf8;
  color: var(--muted);
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s ease;
}
.seg button.active {
  background: rgba(47, 125, 79, 0.1);
  border-color: var(--forest);
  color: var(--forest-deep);
}

.field {
  margin-bottom: 16px;
}
.field label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 6px;
  color: var(--forest-deep);
}
.input {
  width: 100%;
  height: 46px;
  padding: 0 14px;
  font-size: 15px;
  background: #fffdf8;
  color: var(--text);
  border: 2px solid var(--wood-light);
  border-radius: 12px;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}
.input:focus {
  outline: none;
  border-color: var(--forest);
  box-shadow: 0 0 0 3px rgba(47, 125, 79, 0.18);
  transform: translateY(-1px);
}
.input.invalid {
  border-color: var(--danger);
}
.input-wrap {
  position: relative;
}
.toggle-pwd {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  border: none;
  background: transparent;
  color: var(--muted);
  font-size: 13px;
  cursor: pointer;
  padding: 6px;
}
.toggle-pwd:hover {
  color: var(--forest);
}
.err {
  font-size: 12px;
  color: var(--danger);
  margin-top: 5px;
  min-height: 15px;
}

/* 验证码获取按钮 */
.code-wrap .send-code {
  position: absolute;
  right: 6px;
  top: 50%;
  transform: translateY(-50%);
  border: none;
  background: var(--forest);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  border-radius: 9px;
  padding: 8px 12px;
  cursor: pointer;
  white-space: nowrap;
  transition: filter 0.15s ease, transform 0.08s ease;
}
.code-wrap .send-code:hover:not(:disabled) {
  filter: brightness(1.08);
}
.code-wrap .send-code:active:not(:disabled) {
  transform: translateY(-50%) scale(0.96);
}
.code-wrap .send-code:disabled {
  background: var(--muted);
  cursor: not-allowed;
}
.code-wrap .input {
  padding-right: 104px;
}
.hint {
  font-size: 11px;
  color: var(--muted);
  margin-top: 4px;
}

/* 密码强度 */
.strength {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
}
.strength .bar {
  flex: 1;
  height: 6px;
  border-radius: 4px;
  background: #eee;
  transition: background 0.2s ease;
}
.strength .bar.on-weak {
  background: var(--danger);
}
.strength .bar.on-mid {
  background: var(--honey);
}
.strength .bar.on-strong {
  background: var(--forest);
}
.strength-text {
  font-size: 12px;
  color: var(--muted);
  margin-left: 4px;
}

/* 密码校验清单 */
.pwd-check {
  list-style: none;
  margin: 10px 0 2px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4px 10px;
}
.pwd-check li {
  font-size: 12px;
  color: var(--muted);
  display: flex;
  align-items: center;
  gap: 5px;
  transition: color 0.2s ease;
}
.pwd-check li.ok {
  color: var(--forest-deep);
  font-weight: 600;
}
.pwd-check .tick {
  font-size: 12px;
  transition: transform 0.2s ease;
}
.pwd-check li.ok .tick {
  animation: pop 0.35s ease;
}
@keyframes pop {
  0% {
    transform: scale(0.4);
  }
  60% {
    transform: scale(1.3);
  }
  100% {
    transform: scale(1);
  }
}

.terms {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 13px;
  color: var(--muted);
  margin: 4px 0 2px;
  cursor: pointer;
  line-height: 1.5;
}
.terms input {
  margin-top: 2px;
  accent-color: var(--forest);
}
.terms a {
  color: var(--forest);
  text-decoration: none;
  font-weight: 600;
}
.terms a:hover {
  text-decoration: underline;
}
.submit {
  width: 100%;
  height: 48px;
  margin-top: 8px;
  border: none;
  border-radius: 14px;
  background: linear-gradient(180deg, #ffc83d 0%, var(--honey) 100%);
  color: #5a3d00;
  font-size: 16px;
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 6px 0 #e09b00;
  transition: transform 0.08s ease, box-shadow 0.08s ease, filter 0.15s ease;
  position: relative;
  overflow: hidden;
}
.submit::after {
  content: "";
  position: absolute;
  top: 0;
  left: -60%;
  width: 50%;
  height: 100%;
  background: linear-gradient(
    120deg,
    transparent,
    rgba(255, 255, 255, 0.6),
    transparent
  );
  transform: skewX(-20deg);
  animation: shine 3.2s ease-in-out infinite;
}
@keyframes shine {
  0% {
    left: -60%;
  }
  55%,
  100% {
    left: 130%;
  }
}
.submit:hover {
  filter: brightness(1.03);
}
.submit:active {
  transform: translateY(4px);
  box-shadow: 0 2px 0 #e09b00;
}
.submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: 0 6px 0 #e09b00;
}
.alt {
  text-align: center;
  font-size: 13px;
  color: var(--muted);
  margin-top: 20px;
}
.alt a {
  color: var(--forest);
  text-decoration: none;
  font-weight: 600;
}
.alt a:hover {
  text-decoration: underline;
}
.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 2px 0 20px;
  font-size: 13px;
}
.row label {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--muted);
  cursor: pointer;
}
.row a {
  color: var(--forest);
  text-decoration: none;
  font-weight: 600;
}
.row a:hover {
  text-decoration: underline;
}
.login-err {
  text-align: center;
  min-height: 15px;
}
.welcome {
  text-align: center;
  overflow: hidden;
}
.welcome-text {
  margin: 8px 0 24px;
  font-size: 15px;
  line-height: 1.7;
  color: var(--text);
}

/* 成功页彩带 */
.confetti {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}
.confetti .piece {
  position: absolute;
  top: -20px;
  animation: confettiFall linear forwards;
}
@keyframes confettiFall {
  0% {
    transform: translateY(-20px) rotate(0deg);
    opacity: 0;
  }
  15% {
    opacity: 1;
  }
  100% {
    transform: translateY(420px) rotate(540deg);
    opacity: 0;
  }
}

/* 轻提示 toast */
.toast {
  position: fixed;
  left: 50%;
  bottom: 32px;
  transform: translateX(-50%);
  background: rgba(31, 92, 57, 0.95);
  color: #fff;
  padding: 12px 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
  z-index: 50;
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
</style>
