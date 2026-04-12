import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/components/MainLayout.vue'
import CategoryManage from '@/views/CategoryManage.vue'
import KnowledgeResourceManage from '@/views/KnowledgeResourceManage.vue'
import ChatAssistant from '@/views/ChatAssistant.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import { isLoggedIn } from '@/utils/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/login', name: 'login', component: LoginView, meta: { guest: true } },
    { path: '/register', name: 'register', component: RegisterView, meta: { guest: true } },
    {
      path: '/app',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/app/chat' },
        { path: 'chat', name: 'chat', component: ChatAssistant },
        { path: 'categories', name: 'categories', component: CategoryManage },
        { path: 'resources', name: 'resources', component: KnowledgeResourceManage },
      ],
    },
    { path: '/', redirect: '/app/chat' },
  ],
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !isLoggedIn()) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.guest && isLoggedIn()) {
    return '/app/chat'
  }
  return true
})

export default router
