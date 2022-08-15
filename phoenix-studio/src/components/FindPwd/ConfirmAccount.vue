<template>
  <div class="confirm">
    <div class="card-main">
      <div class="confirm-main">
        <!--        <span class="accountq">您正在找回的账号是：{{ account }}</span>-->
        <x-form :model="formState">
          <x-form-item :rules="rules.name" name="name">
            <x-input
                v-model:value="formState.name"
                placeholder="请输入您需要找回的账号用户名"
                @blur="confirmBlur()"
                @input='onInput'
            >
              <template #prefix>
                <Icon class="x-ant-input-icon-inactive" image name="login/accounte"/>
                <Icon class="x-ant-input-icon-active" image name="login/accounte_click"/>
              </template>
            </x-input>
          </x-form-item>
          <x-form-item :rules="rules.verification" name="verification">
            <x-input
                v-model:value="formState.verification"
                placeholder="请输入验证码"
                @input="confirmBlur()"
            >
              <template #prefix>
                <Icon class="x-ant-input-icon-inactive" image name="login/password"/>
                <Icon class="x-ant-input-icon-active" image name="login/password_click"/>
              </template>
              <template #suffix>
                <span class="change-img">
                  <img :src="imgCodeUrl"/>
                </span>
                <span class="change" @click="changeImg">换一张</span>
              </template>
            </x-input>
          </x-form-item>
        </x-form>
      </div>
    </div>
  </div>
</template>
<script>
import Icon from '../Icon'
import { reactive, ref, onMounted, computed } from 'vue'
import { toArray } from 'lodash-es'
import { Form } from 'ant-design-vue'
import { message } from 'ant-design-vue-3'
// import XCard from '../../smart-ui-vue/XCard.vue'
import XInput from '../../smart-ui-vue/XInput.vue'
import XForm from '../../smart-ui-vue/XForm.vue'
import XFormItem from '../../smart-ui-vue/XFormItem.vue'
import { useStore } from 'vuex'
import { captcha, captchaImage, isExistUser } from '../../api/login'

const useForm = Form.useForm

export default {
  name: 'ConFirmAccount',
  components: {
    Icon,
    // XCard,
    XForm,
    XInput,
    XFormItem,
  },
  emits: ['status'],
  setup(props, { emit }) {
    const next = reactive({
      userName: false,
      code: false,
    })
    const ConfirmAccountStatus = ref(false)
    const captchaId = ref(null)
    const store = useStore()
    const formState = reactive({
      name: '',
      verification: '',
    })
    const account = computed(() => {
      return store.state.userName
    })
    const verifyDisabled = ref(true)
    const num = ref('')
    const imgCodeUrl = ref('')
    const clickStatus = ref(false)
    const reg = /@/
    const validateName = async() => {
      if (formState.name.length !== 0) {
        const params = {
          strtype: 0,
          str: formState.name,
        }
        await isExistUser(params).then((resp) => {
          if (resp.meta && !resp.meta.success) {
            ConfirmAccountStatus.value = true
            next.userName = true
            return Promise.resolve()
          } else {
            return Promise.reject(
              (resp.meta && resp.meta.status_code) ||
                'common.error.internal',
            )
          }
        })
      }
      return true
    }
    const rules = reactive({
      name: [
        {
          // required: true,
          validator: validateName,
          trigger: 'blur',
        },
      ],
      verification: [
        {
          // required: true,
          message: '请输入正确的6位验证码\n',
          trigger: 'change',
        },
        {
          pattern: /^\d{6}$/,
          message: '请输入正确的6位验证码\n',
          trigger: 'change',
        },
      ],
    })

    onMounted(() => {
      clickStatus.value = false
      generateCaptchaId()
    })

    // 生成CaptchaId
    const generateCaptchaId = () => {
      captcha().then((resp) => {
        if (resp.meta.success) {
          // verParams.captcha_id = resp.data.captcha_id
          showPictureCaptcha(resp.data.captcha_id)
          captchaId.value = resp.data.captcha_id
        }
      })
    }

    // 获取验证码图片
    const showPictureCaptcha = (item) => {
      if (clickStatus.value) {
        captchaImage(`${item}.png?reload=${num.value}`)
          .then((resp) => {
            imgCodeUrl.value = window.URL.createObjectURL(resp.data)
          })
      } else {
        captchaImage(`${item}.png`).then((resp) => {
          imgCodeUrl.value = window.URL.createObjectURL(resp.data)
        })
      }
    }

    // 更换验证码图片
    const changeImg = () => {
      num.value = Math.floor(Math.random() * 100000)
      clickStatus.value = true
      generateCaptchaId()
      formState.verification = ''
    }

    // 表单工具函数
    const { validate, validateInfos, mergeValidateInfo } = useForm(
      formState,
      rules,
    )

    // 表单错误信息
    const errorInfos = computed(() => {
      return mergeValidateInfo(toArray(validateInfos))
    })

    const confirmBlur = () => {
      validate()
        .then(() => {
          verifyDisabled.value = false
        })
        .catch(() => {
          verifyDisabled.value = true
        })
      if (formState.name !== '') {
        const regStatus = reg.test(formState.name)
        if (regStatus) {
          message.warning('子用户无法使用找回密码功能，如果您忘记密码请联系主账号修改密码。')
          return
        }
      }
      if (formState.verification.length === 6 && formState.name) {
        emit('status', {
          type: 'ConfirmAccount',
          status: true,
          formState,
          captchaId: captchaId.value,
        })
      }
    }
    const onInput = (e) => {
      store.commit('changeUserName', e.target.value)
    }
    return {
      formState,
      num,
      imgCodeUrl,
      clickStatus,
      errorInfos,
      rules,
      verifyDisabled,
      account,
      confirmBlur,
      generateCaptchaId,
      changeImg,
      validateName,
      onInput,
    }
  },
}
</script>
<style lang="scss">
.confirm {
  width: 100%;
  margin: 0 auto;

  .#{$ant-prefix}-card-bordered {
    border: none;
  }

  .#{$ant-prefix}-card-body {
    padding: 0;
  }

  .card-main {
    width: 100%;

    .confirm-main {
      //padding: 40px;
      .#{$ant-prefix}-form-item {
        width: 100%;
      }

      .icon {
        width: 18px;
        height: 18px;
        margin-right: 10px;
        vertical-align: middle;
      }

      .change-img {
        display: inline-block;
        width: 100px;
        height: 30px;
        cursor: pointer;

        img {
          width: 100%;
        }
      }

      .change {
        width: 42px;
        height: 20px;
        font-family: PingFangSC-Regular;
        font-size: 14px;
        color: #336cff;
        text-align: right;
        font-weight: 400;
        margin-left: 10px;
        cursor: pointer;

        &:hover {
          transform: translate(0, -2px);
        }
      }

      // .#{$ant-prefix}-input-affix-wrapper{
      //   border: none;
      //   margin-bottom: 5px;
      // }
    }
  }
}
</style>
