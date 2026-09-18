import { ref } from 'vue'

const toasts = ref([])
let seed = 0

/** 全局轻提示（单例状态，任意组件调用都会渲染到同一个 ToastHost） */
export function useToast() {
  /** @param {'ok'|'err'} type */
  function push(message, type = 'ok', duration = 2600) {
    const id = ++seed
    toasts.value.push({ id, message, type })

    setTimeout(() => {
      toasts.value = toasts.value.filter((item) => item.id !== id)
    }, duration)
  }

  return {
    toasts,
    push,
    success: (msg) => push(msg, 'ok'),
    error: (msg) => push(msg, 'err')
  }
}
