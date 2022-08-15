import { useStore } from 'vuex'
import { computed, ComputedRef, Ref, ref } from 'vue'

export const LOGO_TYPE_NO = ''
export const LOGO_TYPE_OUSHU = 'oushu'
export const LOGO_TYPE_CUSTOM = 'custom'

// 返回 logo 的地址
export const useLogo = () => {
  // __webpack_public_path__ 是在 public-path.js 文件中动态设置的
  // __webpack_public_path__ 在此项目中应该是 http(s)://xxx.xxx.xxx.xxx:3001/
  const publicPath = __webpack_public_path__
  const store = useStore()
  const logoTypeRef: ComputedRef<string> = computed(() => store.state.settings.logo)
  const logoPathRef: Ref<string> = ref(LOGO_TYPE_OUSHU)

  if (logoTypeRef.value === LOGO_TYPE_OUSHU) {
    logoPathRef.value = 'login/logo'
  } else if (logoTypeRef.value === LOGO_TYPE_CUSTOM) {
    logoPathRef.value = `${publicPath}logo.png`
  } else {
    logoPathRef.value = LOGO_TYPE_NO
  }

  return {
    logoType: logoTypeRef.value,
    logoPath: logoPathRef.value
  }
}
