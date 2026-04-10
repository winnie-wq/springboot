import { createRouter, createWebHistory } from 'vue-router'
import CategoryManage from '@/views/CategoryManage.vue'
import KnowledgeResourceManage from '@/views/KnowledgeResourceManage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/categories' },
    { path: '/categories', name: 'categories', component: CategoryManage },
    { path: '/resources', name: 'resources', component: KnowledgeResourceManage },
  ],
})

export default router
