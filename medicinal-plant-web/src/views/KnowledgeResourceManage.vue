<template>
  <div class="book-page">
    <div class="main-content">
      <div class="page-header">
        <h2>📚 药用植物知识库资料管理</h2>
        <el-button type="primary" @click="showAddDialog" :loading="loading">
          <span>➕</span> 上传资料
        </el-button>
      </div>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-cards">
        <el-col :span="8">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-emoji">📚</div>
              <div class="stat-info">
                <div class="stat-number">{{ books.length }}</div>
                <div class="stat-label">资料总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-emoji">📖</div>
              <div class="stat-info">
                <div class="stat-number">{{ totalPages }}</div>
                <div class="stat-label">总页数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-emoji">👀</div>
              <div class="stat-info">
                <div class="stat-number">{{ totalViews }}</div>
                <div class="stat-label">总阅读量</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 搜索栏 -->
      <el-card class="search-card">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索资料标题、作者..."
          clearable
          @clear="loadBooks"
          @keyup.enter="searchBooks"
        >
          <template #append>
            <el-button @click="searchBooks" :loading="loading">搜索</el-button>
          </template>
        </el-input>
      </el-card>

      <!-- 资料列表 -->
      <div class="book-grid" v-loading="loading">
        <el-card
          v-for="book in filteredBooks"
          :key="book.id"
          class="book-card"
          shadow="hover"
        >
          <div class="book-cover">
            <div class="cover-emoji">{{ book.coverEmoji || '📖' }}</div>
          </div>
          <div class="book-info">
            <h3>{{ book.title }}</h3>
            <p class="author">作者：{{ book.author }}</p>
            <p class="description">{{ book.description }}</p>
            <div class="book-meta">
              <span>📄 {{ book.pages }}页</span>
              <span>👀 {{ book.views }}次阅读</span>
              <span>📅 {{ formatDate(book.createdAt) }}</span>
            </div>
            <div class="book-actions">
              <el-button size="small" @click="readBook(book)">
                <span>📖</span> 查阅
              </el-button>
              <el-button size="small" type="primary" @click="editBook(book)">
                <span>✏️</span> 编辑
              </el-button>
              <el-button size="small" type="danger" @click="deleteBook(book)">
                <span>🗑️</span> 删除
              </el-button>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && filteredBooks.length === 0" description="暂无资料，请上传" />

      <!-- 添加/编辑对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="600px"
        @closed="resetForm"
      >
        <el-form :model="formData" :rules="formRules" ref="formRef" label-width="80px">
          <el-form-item label="资料标题" prop="title">
            <el-input v-model="formData.title" placeholder="请输入资料标题" />
          </el-form-item>
          <el-form-item label="作者" prop="author">
            <el-input v-model="formData.author" placeholder="请输入作者" />
          </el-form-item>
          <el-form-item label="封面表情" prop="coverEmoji">
            <el-input v-model="formData.coverEmoji" placeholder="例如：🌿、🍃、🌱" maxlength="2" />
            <div class="form-tip">可选择表情符号作为封面</div>
          </el-form-item>
          <el-form-item label="页数" prop="pages">
            <el-input-number v-model="formData.pages" :min="1" :max="10000" />
          </el-form-item>
          <el-form-item label="简介" prop="description">
            <el-input
              v-model="formData.description"
              type="textarea"
              :rows="3"
              placeholder="请输入资料简介"
            />
          </el-form-item>
          <el-form-item label="内容" prop="content">
            <el-input
              v-model="formData.content"
              type="textarea"
              :rows="10"
              placeholder="请输入资料正文（支持 Markdown 格式）"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveBook" :loading="saving">保存</el-button>
        </template>
      </el-dialog>

      <!-- 查阅对话框 -->
      <el-dialog
        v-model="readDialogVisible"
        :title="readingBook?.title"
        width="800px"
        class="read-dialog"
      >
        <div class="book-content">
          <div class="book-meta-info">
            <div class="meta-item">📝 作者：{{ readingBook?.author }}</div>
            <div class="meta-item">📄 页数：{{ readingBook?.pages }}页</div>
            <div class="meta-item">👀 阅读量：{{ readingBook?.views }}次</div>
          </div>
          <el-divider />
          <div class="content-text" v-html="formattedContent"></div>
        </div>
        <template #footer>
          <el-button @click="readDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { marked } from 'marked'
import bookApi from '@/api/book'

// 数据
const books = ref([])
const searchKeyword = ref('')
const dialogVisible = ref(false)
const readDialogVisible = ref(false)
const editingId = ref(null)
const readingBook = ref(null)
const formRef = ref(null)
const loading = ref(false)
const saving = ref(false)

// 表单数据
const formData = ref({
  title: '',
  author: '',
  coverEmoji: '📖',
  pages: 0,
  description: '',
  content: ''
})

// 表单验证规则
const formRules = {
  title: [{ required: true, message: '请输入资料标题', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  pages: [{ required: true, message: '请输入页数', trigger: 'blur' }]
}

// 计算属性
const dialogTitle = computed(() => editingId.value ? '编辑资料' : '添加资料')
const totalPages = computed(() => books.value.reduce((sum, book) => sum + (book.pages || 0), 0))
const totalViews = computed(() => books.value.reduce((sum, book) => sum + (book.views || 0), 0))
const filteredBooks = computed(() => {
  if (!searchKeyword.value) return books.value
  return books.value.filter(book =>
    book.title?.includes(searchKeyword.value) ||
    book.author?.includes(searchKeyword.value) ||
    book.description?.includes(searchKeyword.value)
  )
})
const formattedContent = computed(() => {
  if (!readingBook.value?.content) return ''
  return marked(readingBook.value.content)
})

// 加载资料列表
const loadBooks = async () => {
  loading.value = true
  try {
    const res = await bookApi.getAll()
    console.log('资料列表响应:', res)
    
    if (res.code === 200 && res.data) {
      books.value = res.data
      ElMessage.success(`加载成功，共 ${books.value.length} 条资料`)
    } else {
      ElMessage.warning(res.message || '暂无资料数据')
      books.value = []
    }
  } catch (error) {
    console.error('加载资料失败:', error)
    ElMessage.error('加载失败，请检查网络连接')
    books.value = []
  } finally {
    loading.value = false
  }
}

// 搜索资料
const searchBooks = async () => {
  if (!searchKeyword.value) {
    loadBooks()
    return
  }
  
  loading.value = true
  try {
    const res = await bookApi.search(searchKeyword.value)
    if (res.code === 200 && res.data) {
      books.value = res.data
      ElMessage.info(`找到 ${books.value.length} 条相关资料`)
    } else {
      ElMessage.info('没有找到相关资料')
      books.value = []
    }
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请重试')
  } finally {
    loading.value = false
  }
}

// 显示添加对话框
const showAddDialog = () => {
  editingId.value = null
  formData.value = {
    title: '',
    author: '',
    coverEmoji: '📖',
    pages: 0,
    description: '',
    content: ''
  }
  dialogVisible.value = true
}

// 编辑资料
const editBook = (book) => {
  editingId.value = book.id
  formData.value = {
    title: book.title,
    author: book.author,
    coverEmoji: book.coverEmoji || '📖',
    pages: book.pages,
    description: book.description || '',
    content: book.content || ''
  }
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
}

// 保存资料
const saveBook = async () => {
  try {
    await formRef.value?.validate()
    saving.value = true
    
    if (editingId.value) {
      // 编辑
      const res = await bookApi.update(editingId.value, formData.value)
      if (res.code === 200) {
        ElMessage.success('更新成功')
        await loadBooks()
        dialogVisible.value = false
      } else {
        ElMessage.error(res.message || '更新失败')
      }
    } else {
      // 添加
      const res = await bookApi.create(formData.value)
      if (res.code === 200) {
        ElMessage.success('添加成功')
        await loadBooks()
        dialogVisible.value = false
      } else {
        ElMessage.error(res.message || '添加失败')
      }
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

// 删除资料
const deleteBook = async (book) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除资料《${book.title}》吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await bookApi.delete(book.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await loadBooks()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

// 查阅资料
const readBook = async (book) => {
  readingBook.value = book
  readDialogVisible.value = true
  
  // 增加阅读量
  try {
    await bookApi.getById(book.id)
    await loadBooks() // 刷新列表更新阅读量
  } catch (error) {
    console.error('更新阅读量失败:', error)
  }
}

// 格式化日期
const formatDate = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
}

onMounted(() => {
  loadBooks()
})
</script>

<style scoped>
.book-page {
  min-height: 100vh;
}

.main-content {
  padding: 24px;
  overflow-y: auto;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.stats-cards {
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-emoji {
  font-size: 48px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.search-card {
  margin-bottom: 24px;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.book-card {
  transition: transform 0.3s;
}

.book-card:hover {
  transform: translateY(-4px);
}

.book-cover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  margin-bottom: 16px;
}

.cover-emoji {
  font-size: 64px;
}

.book-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #303133;
}

.author {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}

.description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
}

.book-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.read-dialog .book-content {
  max-height: 60vh;
  overflow-y: auto;
}

.book-meta-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.meta-item {
  font-size: 14px;
  color: #606266;
}

.content-text {
  font-size: 15px;
  line-height: 1.7;
  color: #303133;
}

.content-text :deep(p) {
  margin: 0 0 0.75em;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>