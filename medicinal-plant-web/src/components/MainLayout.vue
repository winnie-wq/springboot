<script setup>
import { provide, ref } from 'vue'
import AppSidebar from '@/components/AppSidebar.vue'

const dialogueRounds = ref(0)
provide('dialogueRounds', dialogueRounds)
</script>

<template>
  <div class="app-shell">
    <AppSidebar />
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: #f5f7fa;
}

.app-main {
  margin-left: 280px;
  min-height: 100vh;
  box-sizing: border-box;
}
</style>
