<template>
  <aside class="forest">
    <div class="clouds">
      <span
        v-for="c in clouds"
        :key="'cloud-' + c.id"
        class="cloud"
        :style="{
          top: c.top,
          fontSize: c.size,
          animationDuration: c.dur + 's',
          animationDelay: c.delay + 's'
        }"
        >☁️</span
      >
    </div>

    <div class="leaves">
      <span
        v-for="l in leaves"
        :key="'leaf-' + l.id"
        class="leaf"
        :style="{
          left: l.left,
          animationDuration: l.dur + 's',
          animationDelay: l.delay + 's'
        }"
        >{{ l.icon }}</span
      >
    </div>

    <div class="brand">🐻 熊出没</div>
    <div class="hero-bear">🐻🌳</div>
    <div>
      <h1>欢迎来到<br />狗熊岭森林</h1>
      <p>和熊大熊二一起守护森林！登录后开启你的冒险之旅。</p>
    </div>
  </aside>
</template>

<script setup>
// 飘动的云朵配置
const clouds = [
  { id: 1, top: '12%', size: '40px', dur: 26, delay: 0 },
  { id: 2, top: '34%', size: '28px', dur: 34, delay: 8 }
]

// 飘落的树叶配置
const leaves = [
  { id: 1, left: '10%', icon: '🍃', dur: 9, delay: 0 },
  { id: 2, left: '30%', icon: '🍂', dur: 12, delay: 2 },
  { id: 3, left: '52%', icon: '🍃', dur: 10, delay: 4 },
  { id: 4, left: '72%', icon: '🍂', dur: 13, delay: 1 },
  { id: 5, left: '90%', icon: '🍃', dur: 11, delay: 6 }
]
</script>

<style scoped>
.forest {
  display: none; /* 窄屏隐藏，由 App.vue 的媒体查询在宽屏显示 */
  flex: 1;
  background: linear-gradient(160deg, #3a9d5d 0%, #1f5c39 100%);
  color: #fff;
  padding: 48px;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
}
.forest::after {
  content: "🌲🌲🌲";
  position: absolute;
  bottom: 16px;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 34px;
  letter-spacing: 6px;
  opacity: 0.55;
}
.forest .brand {
  font-size: 26px;
  font-weight: 800;
  letter-spacing: 0.04em;
}
.forest .hero-bear {
  font-size: 120px;
  text-align: center;
  line-height: 1;
  animation: floatY 4s ease-in-out infinite;
}
@keyframes floatY {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-14px);
  }
}
.forest h1 {
  font-size: 34px;
  font-weight: 800;
  text-shadow: 0 2px 0 rgba(0, 0, 0, 0.15);
}
.forest p {
  font-size: 16px;
  opacity: 0.92;
  max-width: 320px;
  margin-top: 10px;
}

/* 飘落叶 & 云 */
.leaves,
.clouds {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}
.leaf {
  position: absolute;
  top: -40px;
  font-size: 22px;
  animation: fall linear infinite;
}
@keyframes fall {
  0% {
    transform: translateY(-40px) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  100% {
    transform: translateY(105vh) rotate(360deg);
    opacity: 0.9;
  }
}
.cloud {
  position: absolute;
  font-size: 40px;
  opacity: 0.85;
  animation: drift linear infinite;
}
@keyframes drift {
  from {
    transform: translateX(-30%);
  }
  to {
    transform: translateX(130%);
  }
}
</style>
