<template>
  <aside class="sidebar">
    <div class="brand">药用植物知识库</div>
    <nav class="nav">
      <router-link class="nav-link" to="/categories" active-class="active">
        分类管理
      </router-link>
      <router-link class="nav-link" to="/resources" active-class="active">
        资料管理
      </router-link>
    </nav>
  </aside>
</template>

<script setup></script>

<style scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  width: 280px;
  height: 100vh;
  background: linear-gradient(180deg, #1d4e2d 0%, #2d6a4f 100%);
  color: #fff;
  padding: 24px 16px;
  box-sizing: border-box;
  z-index: 100;
}

.brand {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 32px;
  padding: 0 8px;
  line-height: 1.4;
}

.nav {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-link {
  display: block;
  padding: 12px 16px;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  transition: background 0.2s;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.12);
}

.nav-link.active {
  background: rgba(255, 255, 255, 0.22);
  font-weight: 600;
}
</style>
