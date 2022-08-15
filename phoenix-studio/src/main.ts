import './public-path'
import { createApp } from 'vue'
import App from './App.vue'
import 'normalize.css'
import router from './routes'
// @ts-ignore
import store from './store/index.ts'
import {
  Avatar,
  Card,
  Checkbox,
  Collapse,
  Drawer,
  Dropdown,
  Form,
  Input,
  InputNumber,
  Menu,
  Pagination,
  Popover,
  Radio,
  Slider,
  Spin,
  Switch,
  Table,
  Tabs,
  Tag,
  Tooltip,
  Tree,
  Upload, Divider,
  Typography,
  DatePicker,
  TimePicker,
  Empty, Anchor, Popconfirm, Row, Col,
  Button,
  ConfigProvider
} from 'ant-design-vue'
// @ts-ignore
import { Select, ConfigProvider as ConfigProviderV3, message, Modal } from 'ant-design-vue-3'
import SvgIcon from './smart-ui-vue/helper/Icon.vue'
import DefaultSpin from '@/smart-ui-vue/helper/DefaultSpin.vue'
import i18n from 'lava-fe-lib/lib-vue-plugin/i18n'
import 'dayjs/locale/zh-cn'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
/* eslint @typescript-eslint/no-var-requires: "off" */
// @ts-ignore
const modifyVars = require('/src/smart-ui-vue/modifyVars')
dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

interface GlobalProps {
  container: any,
  onGlobalStateChange: (state?: any, prev?: any) => void,
  setGlobalState: (state?: any) => void
}

let instance: any = null

Spin.setDefaultIndicator({
  indicator: DefaultSpin
})

ConfigProviderV3.config({
  prefixCls: modifyVars['@ant-prefix'],
})

function render(props: GlobalProps = { container: null, onGlobalStateChange: () => 0, setGlobalState: () => 0 }) {
  const { container, onGlobalStateChange } = props

  // 1. 监听全局状态的变化
  onGlobalStateChange((state: any) => {
    console.log('[PHOENIX-STUDIO] 监听到 Global State 变化：', state)
    store.commit('global/getGlobalState', state)
  })

  // 2. 初始化来自主应用的 globalProps
  store.commit('global/initGlobalProps', props)

  instance = createApp(App)
  instance.use(router)
  instance.use(Button)
  instance.use(Input)
  instance.use(Table)
  instance.use(Avatar)
  instance.use(Pagination)
  instance.use(Dropdown)
  instance.use(Menu)
  instance.use(Card)
  instance.use(Popover)
  instance.use(Drawer)
  instance.use(Checkbox)
  instance.use(Modal)
  instance.use(Card)
  instance.use(Tooltip)
  instance.use(Spin)
  instance.use(Drawer)
  instance.use(Switch)
  instance.use(Form)
  instance.use(Tabs)
  instance.use(Select)
  instance.use(Radio)
  instance.use(InputNumber)
  instance.use(Slider)
  instance.use(Divider)
  instance.use(Tag)
  instance.use(Tree)
  instance.use(Collapse)
  instance.use(Popover)
  instance.use(Typography)
  instance.use(ConfigProvider)
  instance.use(DatePicker)
  instance.use(TimePicker)
  instance.use(Empty)
  instance.use(Upload)
  instance.use(message)
  instance.use(Anchor)
  instance.use(Popconfirm)
  instance.use(Row)
  instance.use(Col)
  // instance.use(Textarea)
  // 统一注册svg-icon组件
  instance.component('svg-icon', SvgIcon)
  // 注册 vuex
  instance.use(store)

  // i18n
  instance.use(i18n)

  // 遍历require.context的返回模块，并导入
  const requireAll = (requireContext: any) => requireContext.keys().map(requireContext)

  // import所有符合条件的svg 三个参数：文件夹、是否使用子文件夹、正则
  const req: any = require.context('./assets/icons', true, /\.svg$/)
  const req2: any = require.context('./smart-ui-vue/assets/icons', true, /\.svg$/)
  requireAll(req)
  requireAll(req2)
  // 挂载实例
  instance.mount(container ? container.querySelector('#app') : '#app')
}

// const win: any = window
// 独立运行时
if (!(window as any).__POWERED_BY_QIANKUN__) {
  render()
}


// eslint-disable-next-line @typescript-eslint/no-empty-function,require-await
export async function bootstrap() {
}

// eslint-disable-next-line require-await
export async function mount(props: any) {
  render(props)
}

// eslint-disable-next-line require-await
export async function unmount() {
  instance.unmount()
  instance = null
}
