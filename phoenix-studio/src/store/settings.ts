import { getConfig } from '@/api/config'
import { message } from 'ant-design-vue-3'
import { Commit } from 'vuex'
interface Settings {
  // 配置登录成功后的路由跳转
  routeAfterLogin: string,
  // 首页、登录页 logo 配置, 三个可选值:
  // 'custom': 使用用户自定义的 logo，logo 在 public 目录下，名为 logo.png，用户可覆盖之
  // 'oushu': 使用偶数的 logo（默认值）
  // '': 隐藏 logo
  logo: string,
  // 是否隐藏oushu相关信息
  // 目前包括：首页登录/注册里的偶数文案
  hideOushuMarks: boolean
}
export default {
  state: () => {
    return {
      // 默认值
      routeAfterLogin: '/',
      logo: ''
    }
  },
  mutations: {
    setData(state: Settings, data: Record<keyof Settings, any>) {
      Object.keys(data).forEach(key => {
        // @ts-ignore
        state[key] = data[key]
      })
    }
  },
  actions: {
    async getSettings({ commit }: { commit: Commit }) {
      const res = await getConfig({
        appName: 'phoenix-studio',
        group: 'default',
        key: 'settings'
      })
      if (res.meta.success) {
        try {
          commit('setData', JSON.parse(res.data || '{}'))
        } catch {
          message.error('phoenix-studio 配置参数不合法')
        }
      } else message.error('获取 phoenix-studio 配置失败')
    }
  }
}
