<template>
  <main class="panel">
    <!-- 登录成功后的欢迎界面 -->
    <div v-if="status === 'success'" class="card welcome">
      <div class="bear-badge">🐻</div>
      <h2>登录成功！</h2>
      <div class="sub">欢迎来到狗熊岭森林</div>
      <p class="welcome-text">
        🎉 你好，{{ loggedUser.name }}！<br />
        和熊大熊二一起守护森林吧～
      </p>
      <button class="submit" type="button" @click="logout">🚪 退出登录</button>
    </div>

    <!-- 登录表单 -->
    <form
      v-else
      class="card"
      :class="{ shake }"
      novalidate
      @submit.prevent="onSubmit"
      @animationend="onAnimEnd"
    >
      <div class="bear-badge">🐻</div>
      <h2>森林登录</h2>
      <div class="sub">和熊大熊二一起玩</div>

      <div class="field">
        <label for="email">📮 邮箱</label>
        <div class="input-wrap">
          <input
            id="email"
            v-model.trim="email"
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

      <div class="field">
        <label for="password">🔑 密码</label>
        <div class="input-wrap">
          <input
            id="password"
            v-model="password"
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

      <div class="row">
        <label><input v-model="remember" type="checkbox" /> 记住我</label>
        <a href="#">忘记密码？</a>
      </div>

      <div class="err login-err">{{ loginError }}</div>

      <button type="submit" class="submit" :disabled="submitting">
        {{ submitting ? '进入中…' : '🚪 进入森林' }}
      </button>

      <div class="alt">还没有账号？<a href="#">和熊二一起注册</a></div>
    </form>
  </main>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue'

const email = ref('')
const password = ref('')
const showPassword = ref(false)
const remember = ref(false)
const submitting = ref(false)
const shake = ref(false)
const errors = reactive({ email: '', password: '' })

const status = ref('idle') // idle | success
const loginError = ref('')
const loggedUser = ref(null)
const token = ref('')

function validate() {
  errors.email = ''
  errors.password = ''
  let ok = true

  const emailOk = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)
  if (!emailOk) {
    errors.email = '请输入有效的邮箱地址'
    ok = false
  }
  if (password.value.length < 6) {
    errors.password = '密码至少 6 位哦'
    ok = false
  }
  return ok
}

// 重启动画：先移除 class，下一帧再加回
function triggerShake() {
  shake.value = false
  nextTick(() => {
    shake.value = true
  })
}

async function onSubmit() {
  loginError.value = ''
  if (!validate()) {
    triggerShake()
    return
  }

  submitting.value = true
  try {
    const res = await fetch('/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email.value, password: password.value })
    })
    const data = await res.json()
    if (!res.ok) {
      loginError.value = data.message || '登录失败，请重试'
      triggerShake()
      return
    }
    token.value = data.token
    loggedUser.value = data.user
    status.value = 'success'
    if (remember.value) {
      localStorage.setItem('boonie_token', data.token)
    }
  } catch (e) {
    loginError.value = '网络异常，无法连接服务器'
    triggerShake()
  } finally {
    submitting.value = false
  }
}

function logout() {
  status.value = 'idle'
  loggedUser.value = null
  token.value = ''
  email.value = ''
  password.value = ''
  loginError.value = ''
  localStorage.removeItem('boonie_token')
}

// 动画结束后移除 shake class，方便下次触发
function onAnimEnd(e) {
  if (e.animationName === 'shake') {
    shake.value = false
  }
}
</script>

<style scoped>
.panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 22px;
}
.card {
  width: 100%;
  max-width: 380px;
  background: #fff;
  border: 3px solid var(--wood-light);
  border-radius: var(--radius);
  padding: 34px 30px 30px;
  box-shadow: 0 14px 40px rgba(47, 125, 79, 0.15);
  position: relative;
  animation: cardIn 0.7s cubic-bezier(0.22, 1, 0.36, 1) both;
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

.submit {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 14px;
  background: linear-gradient(180deg, #ffc83d 0%, var(--honey) 100%);
  color: #5a3d00;
  font-size: 16px;
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 6px 0 #e09b00;
  transition: transform 0.08s ease, box-shadow 0.08s ease;
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

.login-err {
  text-align: center;
  min-height: 15px;
}
.welcome {
  text-align: center;
}
.welcome-text {
  margin: 8px 0 24px;
  font-size: 15px;
  line-height: 1.7;
  color: var(--text);
}
</style>
