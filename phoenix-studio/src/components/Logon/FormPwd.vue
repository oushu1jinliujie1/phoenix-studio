<template>
  <x-form
    layout="inline"
    :model="formState"
    :rules="rules"
    class="form-list"
    validateOnRuleChange="false"
  >
    <x-form-item name="name" validateFirst>
      <x-input
        placeholder="请输入用户名/手机号/子用户名@主账号ID"
        v-model:value="formState.name"
        type="text"
        @change="verBlur"
        data-comp-id="INPUT-USERNAME"
      >
        <template #prefix>
          <Icon
            image
            name="login/accounte"
            class="x-ant-input-icon-inactive"
          />
          <Icon
            image
            name="login/accounte_click"
            class="x-ant-input-icon-active"
          />
        </template>
      </x-input>
    </x-form-item>
    <x-form-item name="password" validateFirst>
      <x-input
        placeholder="请输入密码"
        v-model:value="formState.password"
        :type="pwdType"
        @change="verBlur"
        data-comp-id="INPUT-PWD"
        class="input-password v-common-login-password"
      >
        <template #prefix>
          <Icon
            image
            name="login/password"
            class="x-ant-input-icon-inactive"
          />
          <Icon
            image
            name="login/password_click"
            class="x-ant-input-icon-active"
          />
        </template>
        <template #suffix>
          <span class="pwd-eye" @click="changeEye" v-if="!pwdEyeStatus">
            <Icon image name="display" class="x-ant-input-icon-inactive" />
            <Icon image class="x-ant-input-icon-active" name="display_click" />
          </span>
          <span class="pwd-eye" @click="changeEye" v-else>
            <Icon image name="nodisplay" class="x-ant-input-icon-inactive" />
            <Icon
              image
              class="x-ant-input-icon-active"
              name="nodisplay_click"
            />
          </span>
        </template>
      </x-input>
    </x-form-item>
  </x-form>
</template>

<script>
import Icon from '../Icon'
import { reactive, ref, computed, onMounted } from 'vue'
import { Form } from 'ant-design-vue'
import { toArray, flattenDeep } from 'lodash-es'
import XInput from '../../smart-ui-vue/XInput.vue'
import XForm from '../../smart-ui-vue/XForm.vue'
import XFormItem from '../../smart-ui-vue/XFormItem.vue'

const useForm = Form.useForm
export default {
  name: 'FormPwd',
  components: {
    Icon,
    XInput,
    XForm,
    XFormItem
  },
  setup(props, { emit }) {
    const userBoolean = ref(true)
    const pwdBoolean = ref(true)
    // 表单是否验证通过
    const verifyDisabled = ref(true)
    const pwdType = ref('password')
    const pwdEyeStatus = ref(true)
    const autofillTimer = ref(null)
    const formState = reactive({
      name: '',
      password: ''
    })
    // 错误tooltip显示
    const errorTipVisible = ref(false)
    const rules = reactive({
      name: [
        {
          required: true,
          message: '请输入用户名',
          trigger: 'blur'
        }
      ],
      password: [
        {
          // required: true,
          message: '请输入密码',
          trigger: 'blur'
        }
      ]
    })
    const { validate, validateInfos, mergeValidateInfo } = useForm(
      formState,
      rules
    )

    const errorInfos = computed(() => {
      return flattenDeep(mergeValidateInfo(toArray(validateInfos)).help)
    })
    onMounted(() => {
      let autofillTime = 0
      if (formState.password) emit('autofill', { status: false })
      else autofillTimer.value = setInterval(() => {
        autofillTime++
        if (autofillTime > 20) clearInterval(autofillTimer.value)
        if (document.querySelectorAll('.v-common-login-password > input:-internal-autofill-selected')[0]) {
          emit('autofill', {
            status: false
          })
          clearInterval(autofillTimer.value)
        }
      }, 50)
    })
    const verBlur = () => {
      clearInterval(autofillTimer.value)
      if (formState.password !== '' && formState.name !== '') {
        emit('sendParams', {
          status: false,
          value: formState
        })
      } else {
        emit('sendParams', {
          status: true,
          value: ''
        })
      }
      validate()
        .then(() => {
          verifyDisabled.value = false
        })
        .catch(() => {
          verifyDisabled.value = true
        })
    }

    // 更换密码显示隐藏图标
    const changeEye = () => {
      pwdType.value = pwdType.value === 'password' ? 'text' : 'password'
      pwdEyeStatus.value = !pwdEyeStatus.value
    }

    return {
      formState,
      userBoolean,
      pwdBoolean,
      rules,
      errorTipVisible,
      errorInfos,
      verifyDisabled,
      pwdType,
      pwdEyeStatus,
      verBlur,
      changeEye
    }
  }
}
</script>
<style lang="scss">
.form-list {
  .pwd-eye {
    cursor: pointer;
  }
  .icon {
    width: 18px;
    height: 100%;
    margin-right: 10px;
  }
}
</style>
