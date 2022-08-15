<template>
  <x-modal class="account-locked-modal" :visible="showModalStatusLock" title="安全验证" @cancel="cancel" :maskClosable="maskClosable">
    <div class="content">
      <p class="desc">由于您密码输入错误次数过多，该账户已锁定暂时无法登录；请“找回密码”后重新登录。</p>
    </div>
    <template #footer>
      <x-button key="back" block @click="handleCancel">找回密码</x-button>
    </template>
  </x-modal>
</template>

<script>
import { ref, toRefs, watch } from 'vue'
import { useRouter } from 'vue-router'
import XButton from '../../smart-ui-vue/XButton.vue'
import XModal from '../../smart-ui-vue/XModal.vue'
import { useStore } from 'vuex'
export default {
  components: {
    XButton,
    XModal
  },
  props: {
    showModalStatusLock: Boolean
  },
  setup(props, { emit }) {
    const router = useRouter()
    const { visible } = toRefs(props)
    const visibleLocal = ref(false)
    const maskClosable = ref(false)
    const store = useStore()
    watch(visible, () => {
      visibleLocal.value = visible.value
    })
    watch(visibleLocal, () => {
      emit('update:visible', visibleLocal.value)
    })

    const handleCancel = () => {
      router.push('/settings/findpwd')
      store.commit('changeNavNum', 3)
      emit('closeModal', false)
    }

    const cancel = () => {
      emit('closeModal', false)
    }

    return {
      handleCancel,
      cancel,
      maskClosable
    }
  }
}
</script>
