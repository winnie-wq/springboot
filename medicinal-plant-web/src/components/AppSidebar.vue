<script setup>
import { computed, inject, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { clearAuth, getUserId, setUserId } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const dialogueRounds = inject('dialogueRounds', ref(0))

const memoryId = ref(getUserId())

watch(memoryId, (v) => {
  if (v != null && v !== '') setUserId(v)
})

const statusText = computed(() => '就绪')

function logout() {
  clearAuth()
  router.push('/login')
}
</script>

<template>
  <aside class="sidebar">
    <div class="brand-block">
      <div class="brand-icon">🌿</div>
      <div class="brand-title">药用植物知识库</div>
      <div class="brand-sub">智能问答助手</div>
    </div>

    <div class="memory-block">
      <div class="memory-label">🧠 记忆ID</div>
      <el-input-number
        v-model="memoryId"
        :min="1"
        :step="1"
        controls-position="right"
        class="memory-input"
      />
      <p class="memory-hint">登录成功后对应用户ID，也可手动调整用于测试</p>
      <p class="memory-sub">不同ID对应独立对话记忆</p>
    </div>

    <nav class="nav">
      <router-link class="nav-link" to="/app/chat" active-class="active">对话助手</router-link>
      <router-link class="nav-link" to="/app/resources" active-class="active">资料管理</router-link>
      <router-link class="nav-link" to="/app/categories" active-class="active">分类管理</router-link>
    </nav>

    <div class="side-footer">
      <div class="footer-row">
        <span>对话轮次</span>
        <span class="footer-num">{{ dialogueRounds }}</span>
      </div>
      <div class="footer-row">
        <span>状态</span>
        <span class="footer-ok">✓ {{ statusText }}</span>
      </div>
      <el-button class="logout-btn" size="small" @click="logout">退出登录</el-button>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  width: 280px;
  height: 100vh;
  background: #ffffff;
  border-right: 1px solid #e4e7ed;
  padding: 20px 16px;
  box-sizing: border-box;
  z-index: 100;
  display: flex;
  flex-direction: column;
}

.brand-block {
  text-align: center;
  margin-bottom: 20px;
}

.brand-icon {
  font-size: 40px;
  margin-bottom: 8px;
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
  line-height: 1.3;
}

.brand-sub {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.memory-block {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 16px;
}

.memory-label {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
  font-weight: 500;
}

.memory-input {
  width: 100%;
}

.memory-hint {
  font-size: 12px;
  color: #e6a23c;
  margin: 8px 0 4px;
  line-height: 1.4;
}

.memory-sub {
  font-size: 11px;
  color: #909399;
  margin: 0;
}

.nav {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.nav-link {
  display: block;
  padding: 12px 14px;
  border-radius: 8px;
  color: #606266;
  text-decoration: none;
  font-size: 14px;
  transition: background 0.2s, color 0.2s;
}

.nav-link:hover {
  background: #ecf5ff;
  color: #409eff;
}

.nav-link.active {
  background: #ecf5ff;
  color: #409eff;
  font-weight: 600;
}

.side-footer {
  border-top: 1px solid #ebeef5;
  padding-top: 12px;
  font-size: 13px;
  color: #606266;
}

.footer-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.footer-num {
  font-weight: 600;
  color: #409eff;
}

.footer-ok {
  color: #67c23a;
}

.logout-btn {
  width: 100%;
  margin-top: 8px;
}
</style>
