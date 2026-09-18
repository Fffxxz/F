/**
 * 登录接口（当前为演示用的 Mock）
 *
 * 接入真实后端时，把函数体替换为 fetch 即可：
 *
 *   export async function login(payload) {
 *     const res = await fetch('/api/login', {
 *       method: 'POST',
 *       headers: { 'Content-Type': 'application/json' },
 *       body: JSON.stringify(payload)
 *     })
 *     const data = await res.json()
 *     if (data.code !== 0) throw new Error(data.message || '账号或密码错误')
 *     return data.data   // { token, user }
 *   }
 */
export function login({ account, password, remember }) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      // 演示规则：密码 = 123456 视为成功
      if (password === '123456') {
        resolve({ token: 'demo-token', account, remember })
      } else {
        reject(new Error('账号或密码错误，请重试'))
      }
    }, 1100)
  })
}
