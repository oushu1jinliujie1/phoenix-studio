<template>
  <div :style="{ width: width + 'px' }" class="verify">
    <x-form :class="{ inline }" :model="form">
      <x-form-item
          :rules="rules.phone"
          :style="{
          width: !inline ? width + 'px' : width / 2 - 30 + 'px'
        }"
          :validateFirst="true"
          class="form-input input-phone-from-item"
          name="phone"
      >
        <x-input
            v-model:value="form.phone"
            :style="{
            width: !inline ? width + 'px' : width / 2 - 30 + 'px'
          }"
            placeholder="请输入你的手机号"
            @blur="handleValidate('phone')"
            @input="chagePhone"
        >
          <template #prefix>
            <Icon class="x-ant-input-icon-inactive" image name="login/phone"/>
            <Icon class="x-ant-input-icon-active" image name="login/phone_click"/>
          </template>
        </x-input>
      </x-form-item>
      <br v-show="inline"/>
      <x-form-item
          :rules="rules.code"
          :style="{
          width: !inline ? width + 'px' : width / 2 - 20 + 'px'
        }"
          :validateFirst="true"
          class="form-input input-code-form-item"
          name="code"
      >
        <x-input
            v-model:value="form.code"
            :style="{
            width: !inline ? width + 'px' : width / 2 - 30 + 'px'
          }"
            placeholder="请输入验证码"
            @input="handleValidate('code')"
        >
          <template #prefix>
            <Icon class="x-ant-input-icon-inactive" image name="login/code"/>
            <Icon class="x-ant-input-icon-active" image name="login/code_click"/>
          </template>
          <template #suffix>
            <span class="v-common-verfycode">
              <x-button
                  :class="{'v-common-veriycode-countdown':istime}"
                  :disabled="verCodeDisabled || istime"
                  style="width: 100px"
                  @click="handleVerify"
              >{{ !istime ? '获取验证码' : timer + 's' }}
              </x-button>
            </span>
          </template>
        </x-input>
      </x-form-item>
    </x-form>
  </div>
</template>

<script>
import Icon from './Icon.vue'
import { ref, reactive, watch, onMounted, onUnmounted } from 'vue'
import { Form } from 'ant-design-vue'
import { message } from 'ant-design-vue-3'
import XInput from '../smart-ui-vue/XInput'
import XForm from '../smart-ui-vue/XForm'
import XFormItem from '../smart-ui-vue/XFormItem'

const useForm = Form.useForm
import XButton from '../smart-ui-vue/XButton.vue'
import { getMessage } from '../api/message'

export default {
  components: {
    Icon,
    XInput,
    XForm,
    XFormItem,
    XButton,
  },
  props: {
    // 是否为行内显示
    inline: {
      type: Boolean,
      default: false,
    },
    // 表单项prop
    formProp: {
      type: Object,
      default: () => ({}),
    },
    width: {
      type: String,
      default: '',
    },
    type: {
      type: String,
      required: true,
    },
  },
  emits: [
    // 组件内表单验证事件
    'validate',
    'info',
  ],
  setup(props, { emit }) {
    // 是否开始倒计时
    const istime = ref(false)
    // 倒计时秒数
    const localTime = sessionStorage.getItem('verifyTime')
      ? sessionStorage.getItem('verifyTime')
      : 60
    const timer = ref(localTime)
    // 表单是否验证通过
    const verifyDisabled = ref(true)

    const verCodeDisabled = ref(true)

    // 表单项
    const form = reactive({
      // 电话号码
      phone: '',
      // 验证码
      code: '',
    })
    const messageParams = ref({
      msgTarget: '',
      type: props.type,
    })
    const rules = reactive({
      phone: [
        {
          // required: true,
          message: '请输入正确的11位手机号\n',
          trigger: 'blur',
        },
        {
          pattern:
              /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/,
          message: '请输入正确的11位手机号\n',
          trigger: 'blur',
        },
      ],
      code: [
        {
          // required: true,
          message: '请输入验证码\n',
          trigger: 'blur',
        },
        {
          pattern: /^\d{6}$/,
          message: '请输入正确的6位验证码\n',
          trigger: 'change',
        },
      ],
    })
    // 验证码倒计时
    const setTimeoutTimer = ref(null)
    onUnmounted(() => {
      sessionStorage.removeItem('verifyTime')
      clearTimeout(setTimeoutTimer.value)
    })
    // 正则验证手机号
    const reg =
        /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/
    // const codeReg = /^\d{6}$/
    // 表单工具函数
    const { validate } = useForm(form, rules)
    // 同步改动到表单props
    watch(form, () => {
      emit('update:formProp', form)
    })

    /**
     * @handler
     */
    onMounted(() => {
      // console.log(validateInfos.code)
      handleValidate()
    })
    const chagePhone = () => {
      if (reg.test(form.phone)) {
        verCodeDisabled.value = false
      } else {
        verCodeDisabled.value = true
      }
    }
    const handleValidate = () => {
      emit('validate', form)
      Object.assign(form, { type: 'phoneVerify' })
      validate()
        .then(() => {
          verifyDisabled.value = false
        })
        .catch(() => {
          verifyDisabled.value = true
        })
      if (form.code.length === 6 && form.phone) {
        emit('info', {
          type: 'PhoneVerify',
          PhoneVerifyStatus: true,
          phone: form.phone,
          verify: form.code,
        })
      }

    }
    // 验证码倒计时
    const codeCountdown = (time) => {
      let t = time
      setTimeoutTimer.value = setTimeout(() => {
        t--
        sessionStorage.setItem('verifyTime', t)
        timer.value = t
        if (t < 1) {
          timer.value = 60
          istime.value = false
          sessionStorage.removeItem('verifyTime')
          clearTimeout(setTimeoutTimer.value)
        }
        codeCountdown(timer.value)
      }, 1000)
    }
    /**
     * @handler
     * 点击验证码发送事件
     */
    const handleVerify = () => {
      messageParams.value.msgTarget = form.phone
      getMessage(messageParams.value).then((resp) => {
        if (!resp.meta.success && resp.meta.status_code === 'lava.error.userPhone.notMatch') {
          message.error('手机号与账号绑定的不匹配，请检查后重新填写')
          istime.value = false
        } else {
          istime.value = true
          codeCountdown(timer.value)
        }
      })
    }
    return {
      verifyDisabled,
      verCodeDisabled,
      istime,
      timer,
      form,
      rules,
      messageParams,
      handleVerify,
      chagePhone,
      handleValidate,
    }
  },
}
</script>

<style lang="scss">
.verify {
  font-size: 14px;

  .tDis {
    background: #a8c8ff !important;
  }

  .#{$ant-prefix}-form-item {
    vertical-align: baseline;
    display: inline-block;
    margin-bottom: 0 !important;
  }

  .inline {
    display: flex;

    .form-input {
      flex: 1;
    }

    .input-phone-from-item {
      margin-right: 10px;
    }

    .input-code-form-item {
      .v-common-verfycode {
        .#{$ant-prefix}-btn[disabled] {
          color: #336CFF;
          background-color: transparent;
          border-color: #336CFF;
          opacity: 0.3;
        }

        .v-common-veriycode-countdown {
          color: #85888c !important;
          background-color: transparent;
          border-color: #85888c !important;
        }
      }
    }
  }

  .error-info {
    float: left;
    margin-top: -5px;

    .#{$ant-prefix}-form-item-control-input {
      min-height: 0px !important;
    }
  }

  .icon {
    width: 18px;
    height: 18px;
    float: left;
    margin-right: 10px;
  }

  .components-input-demo-presuffix {
    :nth-child(1) {
      margin-right: 10px;
    }

    .#{$ant-prefix}-input-prefix {
      margin: 0 !important;
    }

    .icon-component-global {
      margin: 0 !important;
    }
  }

  .transverse {
    width: 100%;
    height: 30px;

    .#{$ant-prefix}-input-affix-wrapper {
      float: left;
      width: 40%;
      // margin-left: 10px;
      margin-right: 5px;

      button {
        width: 100px;
        height: 30px;
        background: #336CFF;
        border-radius: 4px;
      }
    }
  }
}
</style>
