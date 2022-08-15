<template>
  <x-modal class="register-succ-modal" v-model:visible="visibleLocal" width="400px">
    <template #title>注册成功</template>
    <div class="desc">恭喜注册成功！只要在完成一步企业认证就可以尽享200元新手免费试用大礼包哦</div>
    <template #footer>
      <div class="operations">
        <x-button key="back" block @click="handleCancel">登录</x-button>
        <x-button type="primary" key="back" block @click="handleCancel">免费试用</x-button>
      </div>
    </template>
  </x-modal>
</template>

<script>
import { ref, toRefs, watch } from 'vue'
import { useRouter } from 'vue-router'
import XButton from '../../smart-ui-vue/XButton.vue'
import XModal from '../../smart-ui-vue/XModal.vue'
export default {
  props: {
    visible: {
      type: Boolean,
      default: false,
    }
  },
  components: {
    XButton,
    XModal
  },
  setup(props, { emit }) {
    const router = useRouter()
    const { visible } = toRefs(props)
    const visibleLocal = ref(false)
    watch(visible, () => {
      visibleLocal.value = visible.value
    }, { immediate: true })
    watch(visibleLocal, () => {
      emit('update:visible', visibleLocal.value)
    })

    const handleCancel = () => {
      router.push('/login')
    }
    return {
      visibleLocal,
      handleCancel
    }
  }
}
</script>

<style lang="scss">
.register-succ-modal {
  border-radius: 7px;
  .title{
    width: 48px;
    height: 16px;
    font-family: PingFangSC-Regular;
    font-size: 12px;
    color: #282B2E;
    text-align: left;
    line-height: 16px;
    font-weight: 400;
    margin: 0 auto;
    margin-bottom: 23px;
    padding-top: 48px;
  }
  .desc{
    width: 320px;
    height: 60px;
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #282B2E;
    text-align: center;
    line-height: 30px;
    font-weight: 400;
    margin: 0 auto;
    margin-bottom: 31px;
  }
  .#{$ant-prefix}-modal-body{
    padding: 0 !important;
  }
  .#{$ant-prefix}-modal-content{
    border-radius: 8px;
    background: #FFFFFF;
    box-shadow: 0px 10px 15px 0px rgba(0,0,0,0.1);
  }
  .#{$ant-prefix}-modal-footer{
    border-top: 0;
    padding: 0;
    padding-bottom: 40px;
    .operations {
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 40px;
      .#{$ant-prefix}-btn{
        width: 155px;
      }
    }
  }
}
</style>
