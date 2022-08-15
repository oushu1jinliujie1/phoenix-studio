import { createRouter, createWebHistory } from 'vue-router'
import Settings from '@/pages/Settings/index.vue'
import FindPwd from '@/pages/Settings/FindPwd.vue'
import Register from '@/pages/Settings/Register.vue'
import SetPwd from '@/pages/Settings/SetPwd.vue'
import Login from '@/pages/Logon.vue'

const routes = [
  // {
  //   path: '/',
  //   name: 'home',
  //   component: 
  // },
  {
    path: '/login',
    name: 'Login',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: Login
  },
  {
    path: '/settings', component: Settings, children: [
      { path: 'register', component: Register, name: 'register' },
      { path: 'findpwd', component: FindPwd, name: 'findpwd' },
      { path: 'setpwd', component: SetPwd, name: 'setpwd' },
    ],
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
