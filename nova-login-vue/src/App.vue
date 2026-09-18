<script setup>
import BackgroundDecor from './components/BackgroundDecor.vue'
import BrandPanel from './components/BrandPanel.vue'
import LoginForm from './components/LoginForm.vue'
import ToastHost from './components/ToastHost.vue'
</script>

<template>
  <BackgroundDecor />
  <ToastHost />

  <div class="shell">
    <BrandPanel />
    <LoginForm />
  </div>
</template>

<style scoped>
.shell {
  position: relative;
  z-index: 1;
  width: min(1080px, 100%);
  min-height: 620px;
  display: grid;
  grid-template-columns: 1.05fr 1fr;
  background: var(--surface);
  border-radius: 26px;
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  animation: rise .6s cubic-bezier(.2, .8, .2, 1) both;
}

@media (max-width: 900px) {
  .shell { grid-template-columns: 1fr; min-height: 0; }
}
@media (max-width: 420px) {
  .shell { border-radius: 20px; }
}
</style>
