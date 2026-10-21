import express from 'express'
import { fileURLToPath } from 'url'
import { dirname, join } from 'path'
import { randomBytes, scryptSync, timingSafeEqual } from 'crypto'
import fs from 'fs'

const __dirname = dirname(fileURLToPath(import.meta.url))
const app = express()
const PORT = process.env.PORT || 3001

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

// ---------- 用户数据（演示用：内存存储 + 种子用户） ----------
// 默认测试账号：admin@boonie.com / bear123456 ，bear2@boonie.com / bear123456
const seed = 'bear123456'
const users = [
  { id: 1, name: '熊大', email: 'admin@boonie.com', passwordHash: hashPassword(seed) },
  { id: 2, name: '熊二', email: 'bear2@boonie.com', passwordHash: hashPassword(seed) }
]
const sessions = new Map() // token -> userId

// ---------- 路由 ----------
app.get('/api/health', (_req, res) => {
  res.json({ ok: true, time: new Date().toISOString() })
})

app.post('/api/login', (req, res) => {
  const { email, password } = req.body || {}
  if (!email || !password) {
    return res.status(400).json({ message: '请输入邮箱和密码' })
  }
  const user = users.find(
    (u) => u.email.toLowerCase() === String(email).trim().toLowerCase()
  )
  if (!user || !verifyPassword(user.passwordHash, password)) {
    return res.status(401).json({ message: '邮箱或密码不正确' })
  }
  const token = randomBytes(24).toString('hex')
  sessions.set(token, user.id)
  res.json({
    token,
    user: { id: user.id, name: user.name, email: user.email }
  })
})

app.get('/api/me', (req, res) => {
  const auth = req.headers.authorization || ''
  const token = auth.startsWith('Bearer ') ? auth.slice(7) : ''
  const userId = sessions.get(token)
  if (!userId) {
    return res.status(401).json({ message: '未登录或登录已过期' })
  }
  const user = users.find((u) => u.id === userId)
  res.json({ user: { id: user.id, name: user.name, email: user.email } })
})

// ---------- 生产环境：托管前端构建产物 ----------
const distDir = join(__dirname, '..', 'dist')
if (fs.existsSync(distDir)) {
  app.use(express.static(distDir))
  app.get('*', (_req, res) => res.sendFile(join(distDir, 'index.html')))
}

app.listen(PORT, () => {
  console.log(`🐻 后端已启动: http://localhost:${PORT}`)
})
