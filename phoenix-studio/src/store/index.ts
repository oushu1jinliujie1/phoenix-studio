import { createStore } from 'vuex'
import global from './global'
import settings from '@/store/settings'

const store = createStore({
  modules: {
    global,
    settings
  },
  state: {
  },
  mutations: {
  },
  getters: {
  },
  actions: {
  }
})

export default store
