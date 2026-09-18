<script setup>
/**
 * 纯装饰性背景：网格线 + 三个漂浮光斑
 */
</script>

<template>
  <div class="bg" aria-hidden="true">
    <div class="grid-lines"></div>
    <div class="blob a"></div>
    <div class="blob b"></div>
    <div class="blob c"></div>
  </div>
</template>

<style scoped>
.bg { position: fixed; inset: 0; z-index: 0; overflow: hidden; pointer-events: none; }

.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(70px);
  opacity: .55;
  animation: float 18s ease-in-out infinite;
}
.blob.a { width: 460px; height: 460px; left: -140px; top: -160px; background: #b9b6ff; }
.blob.b { width: 400px; height: 400px; right: -120px; top: 10%; background: #9fe0ff; animation-delay: -6s; }
.blob.c { width: 380px; height: 380px; left: 22%; bottom: -180px; background: #ffc9e4; animation-delay: -12s; }

.grid-lines {
  position: absolute; inset: 0;
  background-image: linear-gradient(rgba(20, 22, 31, .035) 1px, transparent 1px),
                    linear-gradient(90deg, rgba(20, 22, 31, .035) 1px, transparent 1px);
  background-size: 44px 44px;
  mask-image: radial-gradient(ellipse at center, #000 30%, transparent 78%);
}
</style>
