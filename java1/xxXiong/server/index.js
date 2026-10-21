import express from 'express'
import { fileURLToPath } from 'url'
import { dirname, join } from 'path'
import { randomBytes, scryptSync, timingSafeEqual } from 'crypto'
import fs from 'fs'

const __dirname = dirname(fileURLToPath(import.meta.url))
const app = express()
const PORT = process.env.PORT || 3001

const dataDir = join(__dirname, '..', 'data')
const usersFile = join(dataDir, 'users.json')
app.use(express.json())

// ---------- 密码哈希工具（使用内置 crypto，零额外依赖） ----------
function hashPassword(password, salt = randomBytes(16).toString('hex')) {
  const derived = scryptSync(password, salt, 64).toString('hex')
  return `${salt}:${derived}`
}
function verifyPassword(stored, password) {
  const [salt, key] = stored.split(':')
  const derived = scryptSync(password, salt, 64).toString('hex')
  const keyBuf = Buffer.from(key, 'hex')
  const derivedBuf = Buffer.from(derived, 'hex')
  return (
    keyBuf.length === derivedBuf.length && timingSafeEqual(keyBuf, derivedBuf)
  )
}

// ---------- 用户数据（持久化到 data/users.json） ----------
function ensureStore() {
  if (!fs.existsSync(dataDir)) fs.mkdirSync(dataDir, { recursive: true })
  if (!fs.existsSync(usersFile)) {
    const seed = 'bear123456'
    const seedUsers = [
      { id: 1, name: '熊大', email: 'admin@boonie.com', passwordHash: hashPassword(seed) },
      { id: 2, name: '熊二', email: 'bear2@boonie.com', passwordHash: hashPassword(seed) }
    ]
    fs.writeFileSync(usersFile, JSON.stringify(seedUsers, null, 2))
    return seedUsers
  }
  try {
    return JSON.parse(fs.readFileSync(usersFile, 'utf-8'))
  } catch {
    return []
  }
}

let users = ensureStore()
const sessions = new Map() // token -> userId
const codeStore = new Map() // email(lower) -> { code, exp }

function saveUsers() {
  fs.writeFileSync(usersFile, JSON.stringify(users, null, 2))
}

function publicUser(u) {
  return { id: u.id, name: u.name, email: u.email }
}

// ---------- 路由 ----------
app.get('/api/health', (_req, res) => {
  res.json({ ok: true, time: new Date().toISOString(), users: users.length })
})

// 发送登录验证码（演示：直接返回 demoCode；生产环境应改为邮件/短信下发）
app.post('/api/send-code', (req, res) => {
  const { email } = req.body || {}
  if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    return res.status(400).json({ message: '请先填写有效的邮箱' })
  }
  const code = String(randomBytes(3).readUIntBE(0, 3) % 1000000).padStart(6, '0')
  codeStore.set(String(email).trim().toLowerCase(), {
    code,
    exp: Date.now() + 5 * 60 * 1000
  })
  // 演示日志，方便本地查看
  console.log(`📩 [demo] 验证码已发送至 ${email}：`, code)
  res.json({ ok: true, expire: 300, demoCode: code })
})

// 登录（支持「密码登录」或「验证码登录」）
app.post('/api/login', (req, res) => {
  const { email, password, code } = req.body || {}
  if (!email) {
    return res.status(400).json({ message: '请输入邮箱' })
  }
  const user = users.find(
    (u) => u.email.toLowerCase() === String(email).trim().toLowerCase()
  )
  if (!user) {
    return res.status(401).json({ message: '该邮箱尚未注册' })
  }

  // 验证码登录
  if (code) {
    const rec = codeStore.get(String(email).trim().toLowerCase())
    if (!rec || rec.code !== String(code)) {
      return res.status(401).json({ message: '验证码不正确' })
    }
    if (rec.exp < Date.now()) {
      codeStore.delete(String(email).trim().toLowerCase())
      return res.status(401).json({ message: '验证码已过期，请重新获取' })
    }
    codeStore.delete(String(email).trim().toLowerCase())
    const token = randomBytes(24).toString('hex')
    sessions.set(token, user.id)
    return res.json({ token, user: publicUser(user), method: 'code' })
  }

  // 密码登录
  if (!password) {
    return res.status(400).json({ message: '请输入密码' })
  }
  if (!verifyPassword(user.passwordHash, password)) {
    return res.status(401).json({ message: '邮箱或密码不正确' })
  }
  const token = randomBytes(24).toString('hex')
  sessions.set(token, user.id)
  res.json({ token, user: publicUser(user), method: 'password' })
})

// 注册（持久化）
app.post('/api/register', (req, res) => {
  const { username, email, password } = req.body || {}
  if (!username || !email || !password) {
    return res.status(400).json({ message: '请填写完整的注册信息' })
  }
  if (!/^[a-zA-Z0-9_]{3,20}$/.test(username)) {
    return res.status(400).json({ message: '用户名 3-20 位，仅限字母/数字/下划线' })
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    return res.status(400).json({ message: '邮箱格式不正确' })
  }
  if (password.length < 6) {
    return res.status(400).json({ message: '密码至少 6 位哦' })
  }
  if (users.some((u) => u.email.toLowerCase() === String(email).trim().toLowerCase())) {
    return res.status(409).json({ message: '该邮箱已经注册过啦' })
  }
  if (users.some((u) => u.name.toLowerCase() === username.toLowerCase())) {
    return res.status(409).json({ message: '该用户名已被占用' })
  }

  const id = users.reduce((m, u) => Math.max(m, u.id), 0) + 1
  const user = {
    id,
    name: username,
    email: String(email).trim(),
    passwordHash: hashPassword(password)
  }
  users.push(user)
  saveUsers() // 落盘，重启后仍可登录

  const token = randomBytes(24).toString('hex')
  sessions.set(token, user.id)
  res.json({ token, user: publicUser(user) })
})

// 获取当前登录用户
app.get('/api/me', (req, res) => {
  const auth = req.headers.authorization || ''
  const token = auth.startsWith('Bearer ') ? auth.slice(7) : ''
  const userId = sessions.get(token)
  if (!userId) {
    return res.status(401).json({ message: '未登录或登录已过期' })
  }
  const user = users.find((u) => u.id === userId)
  res.json({ user: publicUser(user) })
})

// ---------- 生产环境：托管前端构建产物 ----------
const distDir = join(__dirname, '..', 'dist')
if (fs.existsSync(distDir)) {
  app.use(express.static(distDir))
  app.get('*', (_req, res) => res.sendFile(join(distDir, 'index.html')))
}

app.listen(PORT, () => {
  console.log(`🐻 后端已启动: http://localhost:${PORT}（已持久化 ${users.length} 个用户）`)
})
