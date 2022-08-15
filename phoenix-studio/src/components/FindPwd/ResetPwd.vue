<template>
  <div class="resetPwdBox">
    <span class="account">您正在找回的账号是：{{ account }}</span>
    <div class="RestPwdCard">
      <x-form>
        <x-form-item
          v-bind="validateInfos.phoneVerify"
          validateFirst
          style="width:550px"
        >
          <confirm-pwd
            @info="info"
            style="width: 520px"
            v-model:formProp="form.ConfirmPwd"
            :rules="rules.ConfirmPwd[0].fields"
            @validate="handleValidate"
          />
        </x-form-item>
      </x-form>
    </div>
  </div>
</template>

<script>
import ConfirmPwd from '../ConfirmPwd.vue'
import { ref, reactive, computed } from 'vue'
import { toArray } from 'lodash-es'
import { Form } from 'ant-design-vue'
// import XCard from '../../smart-ui-vue/XCard.vue'
import XForm from '../../smart-ui-vue/XForm.vue'
import XFormItem from '../../smart-ui-vue/XFormItem.vue'
const useForm = Form.useForm
import { useStore } from 'vuex'
export default {
  components: {
    ConfirmPwd,
    // XCard,
    XForm,
    XFormItem
  },
  emits: ['status'],
  setup(props, { emit }) {
    const isDone = ref(true)
    const message = ref(null)
    const status = ref(false)
    const store = useStore()
    const account = computed(() => {
      return store.state.userName
    })
    const Pwd = reactive({
      password: '',
      confirmPassWord: ''
    })
    const form = reactive({
      ConfirmPwd: reactive({
        password: '',
        confirmPwd: ''
      })
    })
    const confirm = () => {
      // if (!form.ConfirmPwd.confirmPwd) {
      //   return Promise.reject('')
      // }
      if (form.ConfirmPwd.password !== form.ConfirmPwd.confirmPwd) {
        return Promise.reject('密码不一致，请检查后重新输入')
      }
      if (form.ConfirmPwd.password === form.ConfirmPwd.confirmPwd) {
        return Promise.resolve()
      }
      return null
    }
    const rules = reactive({
      ConfirmPwd: [
        {
          type: 'object',
          // required: true,
          trigger: 'blur',
          fields: {
            password: [
              {
                required: true,
                pattern: /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,50}$/,
                message:
                  '请输入正确的8-50位由数字， 字母（包含大小写）和符号组合的密码\n',
                trigger: 'blur'
              }
            ],
            confirmPwd: [
              {
                required: false,
                validator: confirm,
                trigger: 'blur'
              }
            ]
          }
        }
      ]
    })
    const info = (value) => {
      Pwd.password = value.password
      message.value = value.value
      Pwd.confirmPassWord = value.confirmPassWord
      if (value.ConfirmPwdStatus) {
        // isDone.value = false
        status.value = true
        emit('status', {
          type: 'ResetPwd',
          status: value.status,
          Pwd
        })
      }
    }
    const { validate, validateInfos, mergeValidateInfo } = useForm(form, rules)
    // 表单错误信息
    const errorInfos = computed(() => {
      return mergeValidateInfo(toArray(validateInfos))
    })

    /**
     * @handler
     * 验证表单
     * 处理错误显示
     */
    const handleValidate = () => {
      validate()
    }
    return {
      account,
      isDone,
      info,
      message,
      errorInfos,
      form,
      rules,
      handleValidate,
      validateInfos
    }
  }
}
</script>

<style lang="scss">
.resetPwdBox {
  width: 100%;
  margin: 0 auto;
  .account {
    display: inline-block;
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #282b2e;
    text-align: left;
    font-weight: 400;
  }
  .RestPwdCard {
    width: 100%;
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
  .#{$ant-prefix}-card-body{
    padding:  0;
  }
}
</style>
