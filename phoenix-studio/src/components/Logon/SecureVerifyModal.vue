<template>
  <x-modal :maskClosable="maskClosable" class="logon-modal" visible="showModal" width="400px" @cancel="cancel">
    <template #title>安全验证</template>
    <div class="login-again">您密码输入错误次数过多，请输入验证码后在尝试重新登录</div>
    <div class="verification-main">
        <span class="verification-img">
          <img :src="imgCodeUrl">
        </span>
      <span class="change-img" @click="changeImg">换一张</span>
    </div>
    <div class="input-main">
      <x-form
          ref="formRef"
          :model="formState"
          :rules="rules"
          name="custom-validation"
      >
<!--        <x-form-item v-bind="errorInfos" class="x-ant-error-infos x-ant-error-infos-out"></x-form-item>-->
        <x-form-item name="verification">
          <x-input
              v-model:value="formState.verification"
              class="ver-input"
              placeholder="请输入验证码"
              @blur="lockBlur"
          >
            <template #prefix>
              <Icon class="x-ant-input-icon-inactive" image name="code"/>
              <Icon class="x-ant-input-icon-active" image name="code_click"/>
            </template>
          </x-input>
        </x-form-item>
      </x-form>
    </div>
    <template #footer>
      <x-button type="primary"  @click="submit" class="submit-btn" :disabled="formState.verification ==''">提交</x-button>
    </template>
  </x-modal>
</template>
<script>
import { computed, reactive, ref, onMounted } from 'vue'
import Icon from '../Icon'
import { toArray } from 'lodash-es'
import { Form, notification } from 'ant-design-vue-3'
import XButton from '../../smart-ui-vue/XButton.vue'
import XForm from '../../smart-ui-vue/XForm.vue'
import XFormItem from '../../smart-ui-vue/XFormItem.vue'
import XModal from '../../smart-ui-vue/XModal.vue'
import XInput from '../../smart-ui-vue/XInput.vue'
import { captcha, captchaImage, process } from '../../api/login'

const useForm = Form.useForm
export default {
  name: 'SecureVerifyModal',
  components: {
    Icon,
    XButton,
    XForm,
    XFormItem,
    XModal,
    XInput,
  },
  props: {
    showModal: Boolean,
  },
  setup(props, { emit }) {
    const visible = ref(false)
    const formState = reactive({
      verification: '',
    })
    const imgCodeUrl = ref('')
    const clickStatus = ref(false)
    const num = ref('')
    const maskClosable = ref(false)
    const verParams = reactive({
      'captcha_id': '',
      'captcha_code': '',
    })

    const rules = {
      verification: [
        {
          required: true,
          message: '请输入验证码\n',
          trigger: 'blur',
        },
        {
          pattern: /^\d{6}$/,
          message: '请输入正确的6位验证码\n',
          trigger: 'blur',
        },
      ],
    }

    const changeImg = () => {
      num.value = Math.floor(Math.random() * 100000)
      clickStatus.value = true
      generateCaptchaId()
      formState.verification = ''
    }

    const submit = () => {
      process(verParams).then((resp) => {
        if (resp.meta.success) {
          // router.push('/main')
          emit('showModalStatus', false)
        } else {
          generateCaptchaId()
          formState.verification = ''
          notification.error({
            message: 'Notification Title',
            description: resp.meta.status_code,
          })
          // message.error(resp.meta.status_code)
        }
      })
    }

    onMounted(() => {
      clickStatus.value = false
      generateCaptchaId()
    })

    const generateCaptchaId = () => {
      captcha().then((resp) => {
        if (resp.meta.success) {
          verParams.captcha_id = resp.data.captcha_id
          showPictureCaptcha(resp.data.captcha_id)
        }
      })
    }
    const showPictureCaptcha = (item) => {
      if (clickStatus.value) {
        captchaImage(`${item}.png?reload=${num.value}`).then((resp) => {
          imgCodeUrl.value = window.URL.createObjectURL(resp.data)
        })
      } else {
        captchaImage(`${item}.png`).then((resp) => {
          imgCodeUrl.value = window.URL.createObjectURL(resp.data)
        })
      }
    }

    const { validate, validateInfos, mergeValidateInfo } = useForm(formState, rules)

    const errorInfos = computed(() => {
      return mergeValidateInfo(toArray(validateInfos))
    })

    const lockBlur = () => {
      validate()
      verParams.captcha_code = formState.verification
    }

    const cancel = () => {
      emit('showModalStatus', false)
    }

    return {
      visible,
      formState,
      rules,
      errorInfos,
      imgCodeUrl,
      clickStatus,
      num,
      verParams,
      maskClosable,
      changeImg,
      lockBlur,
      submit,
      cancel,
    }
  },
}
</script>

<style lang="scss">
.logon-modal {
  .login-again {
    width: 320px;
    height: 60px;
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #282B2E;
    text-align: center;
    line-height: 30px;
    font-weight: 400;
    margin-bottom: 22px;
  }

  .verification-main {
    display: flex;
    align-items: center;
    width: 300px;
    margin-left: 45px;
    //justify-content: center;

    .verification-img {
      display: inline-block;
      width: 200px;
      height: 60px;

      img {
        width: 100%;
      }
    }

    .change-img {
      width: 42px;
      height: 20px;
      font-family: PingFangSC-Regular;
      font-size: 14px;
      color: #336CFF;
      text-align: right;
      font-weight: 400;
      margin-left: 10px;
      display: inline-block;
      cursor: pointer;

      &:hover {
        transform: translate(0, -2px);
      }
    }
  }

  .#{$ant-prefix}-modal-footer {
    div {
      display: flex;
      align-items: center;
      justify-content: flex-start;
    }
  }

  .submit-btn {
    width: 320px;
    height: 30px;
    background: #336CFF;
    border-radius: 4px;
    margin: 0 auto;
    color: #FFFFFF;
  }

  .input-main {
    width: 320px;
    margin: 0 auto;

    .#{$ant-prefix}-form-item {
      width: 100%;

      .icon {
        vertical-align: middle;
      }
    }
  }

  .#{$ant-prefix}-modal-footer {
    border-top: 0px;
    padding: 0;
    padding-bottom: 40px;
    padding-top: 39.5px;
  }

  .#{$ant-prefix}-modal-body {
    padding: 0;
  }
}


</style>
