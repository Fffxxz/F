<script setup>
import { useToast } from '../composables/useToast.js'

const { toasts } = useToast()
</script>

<template>
  <div class="toast-wrap" role="status" aria-live="polite">
    <TransitionGroup name="toast">
      <div
        v-for="t in toasts"
        :key="t.id"
        class="toast"
        :class="[t.type === 'err' ? 'err' : 'ok']"
      >
        <span class="dot"></span>
        <span>{{ t.message }}</span>
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.toast-wrap { position: fixed; top: 22px; right: 22px; z-index: 50; display: grid; gap: 10px; }

.toast {
  display: flex; align-items: center; gap: 10px;
  min-width: 260px; padding: 13px 16px;
  background: #fff; border-radius: 13px;
  border: 1px solid var(--line);
  box-shadow: 0 18px 40px -16px rgba(23, 27, 48, .3);
  font-size: 13.5px; color: var(--ink);
}
.toast .dot { width: 8px; height: 8px; border-radius: 50%; flex: none; }
.toast.ok .dot { background: var(--ok); box-shadow: 0 0 0 4px rgba(18, 165, 148, .16); }
.toast.err .dot { background: var(--danger); box-shadow: 0 0 0 4px rgba(229, 72, 77, .16); }

.toast-enter-active { animation: toastIn .34s cubic-bezier(.2, .9, .25, 1) both; }
.toast-leave-active { animation: toastOut .28s ease forwards; }
.toast-move { transition: transform .28s ease; }
</style>
