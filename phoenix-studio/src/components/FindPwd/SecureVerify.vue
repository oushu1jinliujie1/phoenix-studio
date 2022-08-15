<template>
  <div class="SecureVerifyBox">
    <span class="accountq">您正在找回的账号是：{{ account }}</span>
    <div class="card-main">
      <phone-verify
          v-model:formProp="form.phoneVerify"
          :inline="false"
          type="RECOVER"
          width="520"
          @info="info"
      />
    </div>
  </div>
</template>
<script>
import PhoneVerify from '../PhoneVerify'
import { ref, reactive, computed } from 'vue'
// import XCard from '../../smart-ui-vue/XCard.vue'
import { useStore } from 'vuex'
export default {
  name: 'SecureVerify',
  components: {
    PhoneVerify,
    // XCard
  },
  emits: ['status'],
  setup(props, { emit }) {
    const status = ref(false)
    const message = ref(null)
    const store = useStore()
    const form = reactive({
      phoneVerify: reactive({
        phone: '',
        code: ''
      })
    })
    const account = computed(() => {
      return store.state.userName
    })
    /**
     * @handler
     * 验证表单
     * 处理错误显示
     */
    const info = (value) => {
      status.value = value.PhoneVerifyStatus
      message.value = value.value
      // console.log(value)
      emit('status', {
        type: 'SecureVerify',
        status: status.value,
        value: value.value,
        phone: value.phone,
        code: value.verify,
        form
      })
    }
    return {
      info,
      message,
      form,
      account,
    }
  }
}
</script>
<style lang="scss">
.SecureVerifyBox {
  width: 100%;
  margin: 0 auto;
  .accountq {
    display: inline-block;
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #282b2e;
    text-align: left;
    font-weight: 400;
  }
  .card-main {
    width: 100%;
    margin: 0 auto;
  }
  .error {
    span {
      display: inline-block;
      margin-left: 15px;
      font-family: PingFangSC-Regular;
      font-size: 12px;
      color: #d74472;
      text-align: left;
      font-weight: 400;
    }
  }
  .#{$ant-prefix}-card-bordered{
    border: none;
  }
  .#{$ant-prefix}-card-body{
    padding: 0;
  }
}
</style>
