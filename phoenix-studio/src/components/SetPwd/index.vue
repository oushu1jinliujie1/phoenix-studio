<template>
  <CommonFuncFrame :title="title" />
  <div class="setPwd">
    <span class="user">您正在找回的账号是:{{coralrain}}</span>
    <x-card class="SetPwdCard">
      <span class="info">{{msg}}</span>
      <ConfirmPwd @info="info" />
    </x-card>
    <x-button class="done" :class="isDisable? '':'ff'" type="primary" :disabled="!isDisable" @click="done">完成</x-button>
  </div>
</template>

<script>
import ConfirmPwd from '../ConfirmPwd.vue'
import { ref, reactive } from 'vue'
import XCard from '../../smart-ui-vue/XCard.vue'
import XButton from '../../smart-ui-vue/XButton.vue'
export default {
  components: {
    ConfirmPwd,
    XCard,
    XButton
  },
  setup() {
    const msg = ref(null)
    const isDisable = ref(null)
    const modelRef = reactive({
      password: '',
      confirmPassWord: ''
    })
    const info = value => {
      msg.value = value.value
      isDisable.value = value.isDisable
      modelRef.password = value.password
      modelRef.confirmPassWord = value.confirmPassWord
    }
    const done = () => {
      if (modelRef.password !== modelRef.confirmPassWord) {
        console.log(1)
      }
      // console.log('修改成功')
    }
    return {
      msg,
      done,
      isDisable,
      modelRef,
      info,
    }
  }
}
</script>

<style lang='scss'>
.setPwd {
  width: 600px;
  margin: 0 auto;
  .user {
    display: inline-block;
    margin-bottom: 10px;
    margin-left: 2px;
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #282b2e;
    text-align: left;
    font-weight: 400;
  }
  .done{
    width: 600px;
    height: 30px;
    background: #336CFF !important;
    box-shadow: 0px 10px 15px 0px rgba(0, 0, 0, 0.1);
    border-radius: 4px;
    margin-top: 20px;
  }

  .ff {
    background: #a8c8ff !important;
  }
  .info {
    display: inline-block;
    margin-left: 10px;
    font-family: PingFangSC-Regular;
    font-size: 12px;
    color: #d74472;
    text-align: left;
    font-weight: 400;
  }
  .SetPwdCard {
    width: 600px;
    background: #ffffff;
    border-radius: 7px;
    box-shadow: 0px 10px 15px 0px rgba(0, 0, 0, 0.1);
  }
}
</style>