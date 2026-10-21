<template>
  <div class="login-page">
    <ForestPanel />
    <LoginForm />
  </div>
</template>

<script setup>
import ForestPanel from './components/ForestPanel.vue'
import LoginForm from './components/LoginForm.vue'
</script>

<style scoped>
.login-page {
  display: flex;
  min-height: 100vh;
}

/* 左侧森林区在窄屏隐藏，宽屏显示 */
@media (min-width: 860px) {
  :deep(.forest) {
    display: flex;
  }
}
</style>
