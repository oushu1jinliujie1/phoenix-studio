import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/pages/Logon.vue'

import Worksheet from './pages/worksheet/index.vue'

// 需要登录状态的页面跳转时统一操作
const beforeSessionPagesRouteEnter = (to: any, from: any) => {
  const is401 = sessionStorage.getItem('is401')
  if (is401) {
    console.log('未登录', to.fullPath)
    sessionStorage.setItem('LAST_VISITED_PAGE', to.fullPath)
    return { path: '/login' }
  }
  return true
}

const routes = [
  {
    path: '/',
    name: 'Worksheet',
    component: Worksheet,
    beforeEnter: beforeSessionPagesRouteEnter
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})
router.beforeEach((to, from) => {
  // @ts-ignore
  window.onhashchange = () => {
    history.replaceState(history.state, '', location.href)
  }
})

export default router
