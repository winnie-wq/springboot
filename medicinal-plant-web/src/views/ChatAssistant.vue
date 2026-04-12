<script setup>
import { inject, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserId } from '@/utils/auth'

const dialogueRounds = inject('dialogueRounds', ref(0))
const input = ref('')
const loading = ref(false)
const messages = ref([])

function showSamples() {
  ElMessage.info('示例：人参的功效？黄芪和当归有什么区别？')
}

function onKeydown(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    send()
  }
}

/** 解析 Spring MVC 返回的 text/event-stream（常见为按块 data: ...\n\n） */
function appendSsePayload(buffer, onText) {
  const parts = buffer.split(/\r?\n\r?\n/)
  const remainder = parts.pop() ?? ''
  for (const block of parts) {
    if (!block.trim()) continue
    const lines = block.split(/\r?\n/)
    let hasData = false
    for (const line of lines) {
      if (line.startsWith('data:')) {
        onText(line.slice(5).trimStart())
        hasData = true
      }
    }
    if (!hasData && block.trim()) {
      onText(block)
    }
  }
  return remainder
}

async function send() {
  const text = input.value.trim()
  if (!text || loading.value) return
  loading.value = true
  const uid = getUserId()
  messages.value.push({ role: 'user', content: text })
  input.value = ''

  messages.value.push({ role: 'assistant', content: '' })
  const assistantIndex = messages.value.length - 1

  try {
    const q = new URLSearchParams({ message: text, userId: String(uid) })
    const res = await fetch(`/ai/memory_stream_chat?${q.toString()}`)
    if (!res.ok) {
      const errBody = await res.text().catch(() => '')
      throw new Error(errBody || res.statusText)
    }

    const reader = res.body?.getReader()
    if (!reader) {
      const full = await res.text()
      messages.value[assistantIndex].content = full
      dialogueRounds.value += 1
      return
    }

    const decoder = new TextDecoder()
    let carry = ''
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      carry += decoder.decode(value, { stream: true })
      carry = appendSsePayload(carry, (chunk) => {
        messages.value[assistantIndex].content += chunk
      })
    }
    if (carry.trim()) {
      for (const line of carry.split(/\r?\n/)) {
        if (line.startsWith('data:')) {
          messages.value[assistantIndex].content += line.slice(5).trimStart()
        }
      }
    }

    dialogueRounds.value += 1
  } catch (e) {
    ElMessage.error('请求失败，请确认后端已启动')
    if (!messages.value[assistantIndex]?.content) {
      messages.value.splice(assistantIndex, 1)
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="chat-page">
    <div v-if="messages.length === 0" class="welcome">
      <div class="welcome-icon">🌿</div>
      <h1>开始探索药用植物世界</h1>
      <p class="welcome-sub">向我提问任何关于药用植物的问题，我会为你解答</p>
      <el-button type="primary" @click="showSamples">
        <span style="margin-right: 6px">💡</span>查看示例问题
      </el-button>
    </div>

    <div v-else class="chat-log">
      <div v-for="(m, i) in messages" :key="i" :class="['bubble', m.role]">
        {{ m.content }}
      </div>
    </div>

    <div class="input-bar">
      <el-input
        v-model="input"
        type="textarea"
        :rows="3"
        resize="none"
        placeholder="向药用植物助手提问..."
        @keydown="onKeydown"
      />
      <div class="input-meta">Enter 发送，Shift+Enter 换行</div>
      <el-button
        class="send-btn"
        type="primary"
        :loading="loading"
        @click="send"
      >
        <span style="margin-right: 4px">✈</span>发送
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.chat-page {
  min-height: 100vh;
  padding: 24px 32px 120px;
  box-sizing: border-box;
  position: relative;
  background: #f5f7fa;
}

.welcome {
  max-width: 560px;
  margin: 80px auto 0;
  text-align: center;
}

.welcome-icon {
  font-size: 72px;
  margin-bottom: 16px;
}

.welcome h1 {
  font-size: 22px;
  color: #303133;
  margin: 0 0 12px;
  font-weight: 600;
}

.welcome-sub {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 24px;
}

.chat-log {
  max-width: 800px;
  margin: 0 auto;
}

.bubble {
  padding: 12px 16px;
  border-radius: 10px;
  margin-bottom: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
  font-size: 14px;
}

.bubble.user {
  background: #ecf5ff;
  color: #303133;
  margin-left: 48px;
}

.bubble.assistant {
  background: #fff;
  border: 1px solid #ebeef5;
  margin-right: 48px;
}

.input-bar {
  position: fixed;
  left: 280px;
  right: 0;
  bottom: 0;
  padding: 16px 32px 20px;
  background: #fff;
  border-top: 1px solid #e4e7ed;
  box-sizing: border-box;
}

.input-meta {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
}

.send-btn {
  position: absolute;
  right: 40px;
  bottom: 28px;
}
</style>
