<template>
  <div class="category-manage">
    <Sidebar />
    <div class="main-content">
      <div class="page-header">
        <h2>🏷️ 药用植物分类管理</h2>
        <el-button type="primary" @click="showAddDialog" :loading="loading">
          <span>➕</span> 添加分类
        </el-button>
      </div>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-cards">
        <el-col :span="8">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-emoji">🏷️</div>
              <div class="stat-info">
                <div class="stat-number">{{ categories.length }}</div>
                <div class="stat-label">总分类数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-emoji">🌿</div>
              <div class="stat-info">
                <div class="stat-number">{{ totalSpecies }}</div>
                <div class="stat-label">总植物数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-emoji">⭐</div>
              <div class="stat-info">
                <div class="stat-number">{{ avgRating }}</div>
                <div class="stat-label">平均评分</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 分类树形结构（包含药用植物） -->
      <el-card class="category-tree-card" v-loading="loading">
        <template #header>
          <div class="card-header">
            <span>📂 分类与药用植物管理</span>
            <el-button size="small" @click="loadCategories" :loading="loading">
              🔄 刷新
            </el-button>
          </div>
        </template>
        
        <el-empty v-if="!loading && categoryTree.length === 0" description="暂无数据，请先添加分类" />
        
        <div v-else class="tree-container">
          <!-- 分类节点 -->
          <div v-for="category in categoryTree" :key="category.id" class="category-wrapper">
            <div class="category-card">
              <div class="category-header">
                <div class="category-info">
                  <span class="category-emoji">{{ category.emoji || '📁' }}</span>
                  <span class="category-name">{{ category.name }}</span>
                  <el-tag size="small" type="info" class="species-badge">
                    {{ category.speciesCount || 0 }} 种药用植物
                  </el-tag>
                </div>
                <div class="category-actions">
                  <el-button size="small" @click="editCategory(category)">
                    ✏️ 编辑
                  </el-button>
                  <el-button size="small" type="danger" @click="deleteCategory(category)">
                    🗑️ 删除
                  </el-button>
                  <el-button size="small" type="primary" @click="showAddSpeciesDialog(category)">
                    ➕ 添加药用植物
                  </el-button>
                </div>
              </div>
              <div class="category-description" v-if="category.description">
                {{ category.description }}
              </div>
              
              <!-- 药用植物列表 -->
              <div class="species-list" v-if="category.children && category.children.length">
                <div class="species-title">
                  <span>🌿 药用植物列表</span>
                </div>
                <div class="species-grid">
                  <div
                    v-for="species in category.children.filter(c => c.isSpecies)"
                    :key="species.id"
                    class="species-card"
                  >
                    <div class="species-header">
                      <span class="species-emoji">{{ species.emoji || '🌿' }}</span>
                      <span class="species-name">{{ species.name }}</span>
                      <el-rate
                        v-if="species.rating"
                        v-model="species.rating"
                        disabled
                        :max="5"
                        size="small"
                        class="species-rating"
                      />
                    </div>
                    <div class="species-scientific" v-if="species.scientificName">
                      <span class="label">学名：</span>
                      <span class="value">{{ species.scientificName }}</span>
                    </div>
                    <div class="species-habitat" v-if="species.habitat">
                      <span class="label">生境：</span>
                      <span class="value">{{ species.habitat }}</span>
                    </div>
                    <div class="species-diet" v-if="species.diet">
                      <span class="label">药用部位：</span>
                      <span class="value">{{ species.diet }}</span>
                    </div>
                    <div class="species-features" v-if="species.features">
                      <span class="label">性状特点：</span>
                      <span class="value">{{ species.features.substring(0, 80) }}...</span>
                    </div>
                    <div class="species-status" v-if="species.conservationStatus">
                      <el-tag 
                        :type="getStatusType(species.conservationStatus)" 
                        size="small"
                        effect="light"
                      >
                        {{ species.conservationStatus }}
                      </el-tag>
                    </div>
                    <div class="species-actions">
                      <el-button size="small" @click="editSpecies(species)">
                        ✏️ 编辑
                      </el-button>
                      <el-button size="small" type="danger" @click="deleteSpecies(species)">
                        🗑️ 删除
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="empty-species">
                <el-empty description="暂无药用植物，点击上方按钮添加" :image-size="60" />
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 添加/编辑分类对话框 -->
      <el-dialog
        v-model="categoryDialogVisible"
        :title="categoryDialogTitle"
        width="500px"
        @closed="resetCategoryForm"
      >
        <el-form :model="categoryForm" :rules="categoryRules" ref="categoryFormRef" label-width="80px">
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
          </el-form-item>
          <el-form-item label="图标表情" prop="emoji">
            <el-input v-model="categoryForm.emoji" placeholder="例如：🌿、🍃、🌱" maxlength="2" />
          </el-form-item>
          <el-form-item label="父分类" prop="parentId">
            <el-select v-model="categoryForm.parentId" placeholder="请选择父分类" clearable>
              <el-option label="无（顶级分类）" :value="null" />
              <el-option
                v-for="cat in flatCategories"
                :key="cat.id"
                :label="cat.name"
                :value="cat.id"
                :disabled="cat.id === editingCategoryId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input
              v-model="categoryForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入分类描述"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="categoryDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCategory" :loading="saving">保存</el-button>
        </template>
      </el-dialog>

      <!-- 添加/编辑药用植物对话框 -->
      <el-dialog
        v-model="speciesDialogVisible"
        :title="speciesDialogTitle"
        width="700px"
        @closed="resetSpeciesForm"
      >
        <el-form :model="speciesForm" :rules="speciesRules" ref="speciesFormRef" label-width="100px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="药用植物名称" prop="name">
                <el-input v-model="speciesForm.name" placeholder="请输入药用植物名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="学名" prop="scientificName">
                <el-input v-model="speciesForm.scientificName" placeholder="请输入拉丁学名" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="所属分类" prop="categoryId">
            <el-input :value="currentCategory?.name" disabled />
          </el-form-item>
          <el-form-item label="生境" prop="habitat">
            <el-input v-model="speciesForm.habitat" type="textarea" :rows="2" placeholder="请输入生境与分布" />
          </el-form-item>
          <el-form-item label="药用部位" prop="diet">
            <el-input v-model="speciesForm.diet" placeholder="请输入药用部位" />
          </el-form-item>
          <el-form-item label="性状特点" prop="features">
            <el-input
              v-model="speciesForm.features"
              type="textarea"
              :rows="4"
              placeholder="请输入性状与功效等特点"
            />
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="保护状态" prop="conservationStatus">
                <el-select v-model="speciesForm.conservationStatus" placeholder="请选择保护状态">
                  <el-option label="无危" value="无危" />
                  <el-option label="近危" value="近危" />
                  <el-option label="易危" value="易危" />
                  <el-option label="濒危" value="濒危" />
                  <el-option label="极危" value="极危" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="评分" prop="rating">
                <el-rate v-model="speciesForm.rating" :max="5" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <template #footer>
          <el-button @click="speciesDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveSpecies" :loading="saving">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Sidebar from '@/components/Sidebar.vue'
import categoryApi from '@/api/category'
import speciesApi from '@/api/species'

// 状态变量
const categories = ref([])
const flatCategories = ref([])
const allSpecies = ref([])
const categoryDialogVisible = ref(false)
const speciesDialogVisible = ref(false)
const editingCategoryId = ref(null)
const editingSpeciesId = ref(null)
const currentCategory = ref(null)
const categoryFormRef = ref(null)
const speciesFormRef = ref(null)
const loading = ref(false)
const saving = ref(false)

// 表单数据
const categoryForm = ref({
  name: '',
  emoji: '🏷️',
  parentId: null,
  description: ''
})

const speciesForm = ref({
  name: '',
  scientificName: '',
  categoryId: null,
  habitat: '',
  diet: '',
  features: '',
  conservationStatus: '无危',
  rating: 0
})

// 验证规则
const categoryRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const speciesRules = {
  name: [{ required: true, message: '请输入药用植物名称', trigger: 'blur' }]
}

// 计算属性
const categoryDialogTitle = computed(() => editingCategoryId.value ? '编辑分类' : '添加分类')
const speciesDialogTitle = computed(() => editingSpeciesId.value ? '编辑药用植物' : '添加药用植物')

const totalSpecies = computed(() => allSpecies.value.length)

const avgRating = computed(() => {
  if (allSpecies.value.length === 0) return '0'
  const total = allSpecies.value.reduce((sum, s) => sum + (s.rating || 0), 0)
  return (total / allSpecies.value.length).toFixed(1)
})

// 构建分类树（包含物种）
const categoryTree = computed(() => {
  const buildTree = (parentId = null) => {
    const cats = flatCategories.value.filter(cat => cat.parentId === parentId)
    return cats.map(cat => {
      // 获取该分类下的物种
      const species = allSpecies.value
        .filter(s => s.categoryId === cat.id)
        .map(s => ({
          ...s,
          isSpecies: true,
          emoji: getSpeciesEmoji(s.name),
          parentId: cat.id
        }))
      
      return {
        ...cat,
        speciesCount: species.length,
        children: species
      }
    })
  }
  return buildTree(null)
})

// 根据药用植物名称获取表情
const getSpeciesEmoji = (name) => {
  const emojiMap = {
    人参: '🌿',
    黄芪: '🌿',
    当归: '🌸',
    枸杞: '🫐',
    金银花: '🌼',
    甘草: '🌿',
    黄连: '🌿',
    茯苓: '🍄',
    灵芝: '🍄',
    薄荷: '🌱',
  }
  return emojiMap[name] || '🌿'
}

// 获取保护状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    '无危': 'success',
    '近危': 'info',
    '易危': 'warning',
    '濒危': 'danger',
    '极危': 'danger'
  }
  return typeMap[status] || 'info'
}

// 加载分类数据
const loadCategories = async () => {
  loading.value = true
  try {
    const categoryRes = await categoryApi.getTree()
    console.log('分类数据:', categoryRes)
    
    if (categoryRes.code === 200 && categoryRes.data) {
      const flat = []
      const flatten = (items) => {
        items.forEach(item => {
          flat.push({
            id: item.id,
            name: item.name,
            emoji: item.emoji,
            parentId: item.parentId,
            description: item.description
          })
          if (item.children && item.children.length) {
            flatten(item.children)
          }
        })
      }
      flatten(categoryRes.data)
      flatCategories.value = flat
      categories.value = categoryRes.data
      
      await loadSpecies()
      ElMessage.success('加载成功')
    } else {
      ElMessage.warning('暂无分类数据')
    }
  } catch (error) {
    console.error('加载分类失败:', error)
    ElMessage.error('加载失败，请检查网络')
  } finally {
    loading.value = false
  }
}

// 加载物种数据
const loadSpecies = async () => {
  try {
    const speciesRes = await speciesApi.getAll()
    console.log('物种数据:', speciesRes)
    
    if (speciesRes.code === 200 && speciesRes.data) {
      allSpecies.value = speciesRes.data
    }
  } catch (error) {
    console.error('加载物种失败:', error)
  }
}

// 重置表单
const resetCategoryForm = () => {
  categoryForm.value = {
    name: '',
    emoji: '🏷️',
    parentId: null,
    description: ''
  }
  editingCategoryId.value = null
}

const resetSpeciesForm = () => {
  speciesForm.value = {
    name: '',
    scientificName: '',
    categoryId: null,
    habitat: '',
    diet: '',
    features: '',
    conservationStatus: '无危',
    rating: 0
  }
  editingSpeciesId.value = null
  currentCategory.value = null
}

// 显示添加分类对话框
const showAddDialog = () => {
  resetCategoryForm()
  categoryDialogVisible.value = true
}

// 编辑分类
const editCategory = (category) => {
  editingCategoryId.value = category.id
  categoryForm.value = {
    name: category.name,
    emoji: category.emoji || '🏷️',
    parentId: category.parentId,
    description: category.description || ''
  }
  categoryDialogVisible.value = true
}

// 保存分类
const saveCategory = async () => {
  try {
    await categoryFormRef.value?.validate()
    saving.value = true
    
    if (editingCategoryId.value) {
      const res = await categoryApi.update(editingCategoryId.value, categoryForm.value)
      if (res.code === 200) {
        ElMessage.success('更新成功')
        await loadCategories()
        categoryDialogVisible.value = false
      } else {
        ElMessage.error(res.message || '更新失败')
      }
    } else {
      const res = await categoryApi.create(categoryForm.value)
      if (res.code === 200) {
        ElMessage.success('添加成功')
        await loadCategories()
        categoryDialogVisible.value = false
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

// 删除分类
const deleteCategory = async (category) => {
  const hasSpecies = allSpecies.value.some(s => s.categoryId === category.id)
  if (hasSpecies) {
    ElMessage.warning('该分类下还有药用植物，请先删除其下所有药用植物')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除分类"${category.name}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await categoryApi.delete(category.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await loadCategories()
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

// 显示添加物种对话框
const showAddSpeciesDialog = (category) => {
  currentCategory.value = category
  editingSpeciesId.value = null
  speciesForm.value = {
    name: '',
    scientificName: '',
    categoryId: category.id,
    habitat: '',
    diet: '',
    features: '',
    conservationStatus: '无危',
    rating: 0
  }
  speciesDialogVisible.value = true
}

// 编辑物种
const editSpecies = (species) => {
  editingSpeciesId.value = species.id
  currentCategory.value = { 
    id: species.categoryId, 
    name: getCategoryName(species.categoryId) 
  }
  speciesForm.value = {
    name: species.name,
    scientificName: species.scientificName || '',
    categoryId: species.categoryId,
    habitat: species.habitat || '',
    diet: species.diet || '',
    features: species.features || '',
    conservationStatus: species.conservationStatus || '无危',
    rating: species.rating || 0
  }
  speciesDialogVisible.value = true
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  const cat = flatCategories.value.find(c => c.id === categoryId)
  return cat ? cat.name : '未知'
}

// 保存物种
const saveSpecies = async () => {
  try {
    await speciesFormRef.value?.validate()
    saving.value = true
    
    if (editingSpeciesId.value) {
      const res = await speciesApi.update(editingSpeciesId.value, speciesForm.value)
      if (res.code === 200) {
        ElMessage.success('更新成功')
        await loadCategories()
        speciesDialogVisible.value = false
      } else {
        ElMessage.error(res.message || '更新失败')
      }
    } else {
      const res = await speciesApi.create(speciesForm.value)
      if (res.code === 200) {
        ElMessage.success('添加成功')
        await loadCategories()
        speciesDialogVisible.value = false
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

// 删除物种
const deleteSpecies = async (species) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除药用植物「${species.name}」吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await speciesApi.delete(species.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await loadCategories()
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

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-manage {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.main-content {
  flex: 1;
  margin-left: 280px;
  padding: 24px;
  overflow-y: auto;
  background: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
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

.category-tree-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tree-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.category-wrapper {
  margin-bottom: 8px;
}

.category-card {
  background: white;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
  overflow: hidden;
  transition: all 0.3s;
}

.category-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 1px solid #e4e7ed;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-emoji {
  font-size: 28px;
}

.category-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.species-badge {
  margin-left: 8px;
}

.category-actions {
  display: flex;
  gap: 8px;
}

.category-description {
  padding: 12px 20px;
  background: #fafbfc;
  color: #606266;
  font-size: 14px;
  border-bottom: 1px solid #e4e7ed;
}

.species-list {
  padding: 20px;
}

.species-title {
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409EFF;
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
}

.species-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.species-card {
  background: #fafbfc;
  border-radius: 10px;
  padding: 16px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s;
}

.species-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #409EFF;
}

.species-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #e4e7ed;
}

.species-emoji {
  font-size: 24px;
}

.species-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.species-rating {
  margin-left: auto;
}

.species-scientific,
.species-habitat,
.species-diet,
.species-features {
  margin-bottom: 8px;
  font-size: 13px;
  line-height: 1.5;
}

.species-scientific .label,
.species-habitat .label,
.species-diet .label,
.species-features .label {
  color: #909399;
  font-weight: 500;
}

.species-scientific .value,
.species-habitat .value,
.species-diet .value,
.species-features .value {
  color: #606266;
}

.species-features {
  margin-bottom: 12px;
}

.species-status {
  margin-bottom: 12px;
}

.species-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
}

.empty-species {
  padding: 20px;
}

:deep(.el-rate) {
  height: auto;
}

:deep(.el-rate__icon) {
  font-size: 14px;
}
</style>