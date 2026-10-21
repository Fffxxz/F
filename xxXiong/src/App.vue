<template>
  <div class="auth-page">
    <ForestPanel />
    <AuthCard />
  </div>
</template>

<script setup>
import ForestPanel from './components/ForestPanel.vue'
import AuthCard from './components/AuthCard.vue'
</script>

<style scoped>
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
