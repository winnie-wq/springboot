<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { loginApi } from '@/api/auth'
import { setAuth } from '@/utils/auth'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
})

async function onSubmit() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await loginApi({
      username: form.username,
      password: form.password,
    })
    if (res.code === 200 && res.data) {
      setAuth(res.data)
      ElMessage.success('登录成功')
      const next = typeof route.query.redirect === 'string' ? route.query.redirect : '/app/chat'
      router.push(next || '/app/chat')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="card">
      <div class="logo">🌿</div>
      <h1>药用植物知识库</h1>
      <p class="sub">登录后使用智能问答与资料管理</p>
      <el-form label-position="top" @submit.prevent="onSubmit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password autocomplete="current-password" />
        </el-form-item>
        <el-button type="primary" class="btn-full" native-type="submit" :loading="loading">登录</el-button>
      </el-form>
      <router-link class="link" to="/register">没有账号？去注册</router-link>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8f5e9 0%, #f5f7fa 100%);
}
.card {
  width: 400px;
  padding: 32px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  text-align: center;
}
.logo {
  font-size: 48px;
  margin-bottom: 8px;
}
h1 {
  font-size: 20px;
  margin: 0 0 8px;
  color: #303133;
}
.sub {
  color: #909399;
  font-size: 13px;
  margin-bottom: 24px;
}
.btn-full {
  width: 100%;
  margin-top: 8px;
}
.link {
  display: inline-block;
  margin-top: 16px;
  font-size: 14px;
  color: #409eff;
  text-decoration: none;
}
</style>
