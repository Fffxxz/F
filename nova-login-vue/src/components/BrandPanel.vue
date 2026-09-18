<script setup>
/** 左侧品牌宣传区（数据抽成数组，便于维护/国际化） */
const features = [
  '端到端加密，数据仅你可见',
  '多设备实时同步，随时切换',
  '团队空间与精细化权限管理'
]
</script>

<template>
  <aside class="aside">
    <div class="brand">
      <div class="logo">N</div>
      <div class="brand-name">Nova</div>
    </div>

    <div class="pitch">
      <h1>欢迎回来<br />继续你的工作</h1>
      <p>一个账号，打通项目协作、数据看板与自动化流程。登录后即可接着上次的进度继续。</p>
    </div>

    <div>
      <div class="feats">
        <div v-for="f in features" :key="f" class="feat">
          <i>✓</i> {{ f }}
        </div>
      </div>
      <div class="quote">
        “<b>把重复的事交出去</b>，把时间留给真正重要的判断。”
      </div>
    </div>
  </aside>
</template>

<style scoped>
.aside {
  position: relative;
  color: #fff;
  padding: 44px 44px 36px;
  background:
    radial-gradient(120% 90% at 10% 0%, rgba(255, 255, 255, .28), transparent 55%),
    linear-gradient(155deg, var(--brand-1) 0%, var(--brand-2) 52%, #5a34c9 100%);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
}
.aside::after {
  content: "";
  position: absolute; width: 480px; height: 480px; right: -220px; bottom: -220px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(56, 189, 248, .55), transparent 62%);
}

.brand { display: flex; align-items: center; gap: 12px; position: relative; z-index: 1; }
.logo {
  width: 40px; height: 40px; border-radius: 12px;
  display: grid; place-items: center;
  background: rgba(255, 255, 255, .18);
  border: 1px solid rgba(255, 255, 255, .4);
  backdrop-filter: blur(6px);
  font-weight: 800; font-size: 19px;
}
.brand-name { font-weight: 700; font-size: 19px; letter-spacing: .5px; }

.pitch { position: relative; z-index: 1; margin: 40px 0; }
.pitch h1 {
  font-size: clamp(26px, 2.6vw, 34px);
  line-height: 1.32;
  margin: 0 0 14px;
  font-weight: 700;
  letter-spacing: -.3px;
}
.pitch p { margin: 0; color: rgba(255, 255, 255, .82); font-size: 14.5px; line-height: 1.75; max-width: 34ch; }

.feats { position: relative; z-index: 1; display: grid; gap: 12px; }
.feat { display: flex; align-items: center; gap: 11px; font-size: 13.6px; color: rgba(255, 255, 255, .9); }
.feat i {
  flex: none; width: 22px; height: 22px; border-radius: 7px;
  background: rgba(255, 255, 255, .16); border: 1px solid rgba(255, 255, 255, .3);
  display: grid; place-items: center; font-style: normal; font-size: 11px;
}

.quote {
  position: relative; z-index: 1; margin-top: 32px;
  padding-top: 22px; border-top: 1px solid rgba(255, 255, 255, .22);
  font-size: 13px; color: rgba(255, 255, 255, .78); line-height: 1.7;
}
.quote b { color: #fff; font-weight: 600; }

@media (max-width: 900px) {
  .aside { padding: 32px 30px 28px; }
  .pitch { margin: 26px 0 0; }
  .pitch h1 { font-size: 24px; }
  .feats, .quote { display: none; }
}
</style>
