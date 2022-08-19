import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/pages/Logon.vue'

import Worksheet from './pages/worksheet/index.vue'

const routes = [
  {
    path: '/',
    name: 'Worksheet',
    component: Worksheet
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
