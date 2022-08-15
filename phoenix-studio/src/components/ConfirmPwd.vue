<template>
  <div class="conFirm">
    <x-form :model="form">
      <x-form-item
        :style="{ width: '100%' }"
        :rules="rules.password"
        :validateFirst="true"
        class="form-input input-password"
        name="password"
      >
        <x-input
          :type="pwdType"
          v-model:value="form.password"
          @change="pasChange"
          @blur="loseFocus('password')"
          placeholder="请输入正确的8-50位由数字，字母（包含大小写）和符号组合的密码"
        >
          <template #prefix>
            <Icon  name="login/password" class="x-ant-input-icon-inactive" image/>
            <Icon  name="login/password_click" class="x-ant-input-icon-active" image/>
          </template>
          <template #suffix>
            <span class="pwd-eye" @click="changeEye" v-if="!pwdEyeStatus">
              <Icon image name="display" class="x-ant-input-icon-inactive" />
              <Icon
                class="x-ant-input-icon-active"
                name="display_click"
                image
              />
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
      <x-form-item
        :style="{ width: '100%' }"
        :rules="rules.confirmPwd"
        :validateFirst="true"
        name="confirmPwd"
      >
        <x-input
          :type="subPwdType"
          @change="pasChange"
          v-model:value="form.confirmPwd"
          @blur="loseFocus('confirmPwd')"
          placeholder="请确认密码"
        >
          <template #prefix>
            <Icon  name="login/password" class="x-ant-input-icon-inactive" image/>
            <Icon  name="login/password_click" class="x-ant-input-icon-active" image/>
          </template>
          <template #suffix>
            <span class="pwd-eye" @click="subChangeEye" v-if="!subPwdStatus">
              <Icon image name="display" class="x-ant-input-icon-inactive" />
              <Icon
                image
                class="x-ant-input-icon-active"
                name="display_click"
              />
            </span>
            <span class="pwd-eye" @click="subChangeEye" v-else>
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
  </div>
</template>
<script>
import { reactive, ref, computed, onMounted, watch } from 'vue'
import Icon from './Icon.vue'
import { Form } from 'ant-design-vue'
import { toArray } from 'lodash-es'
import XInput from '../smart-ui-vue/XInput.vue'
const useForm = Form.useForm
import XForm from '../smart-ui-vue/XForm.vue'
import XFormItem from '../smart-ui-vue/XFormItem.vue'
export default {
  components: {
    Icon,
    XInput,
    XForm,
    XFormItem
  },
  emits: ['info', 'update:formProp'],
  props: {
    formProp: {
      type: Object,
      default: () => ({})
    }
  },
  setup(props, { emit }) {
    const form = reactive({
      password: '',
      confirmPwd: ''
    })
    const err = ref(null)
    const ConfirmPwdStatus = ref(false)
    const isDisable = ref(false)
    const isRedBulePwd = ref(false)
    // const conF = ref(true)
    const isRedBuleConFPwd = ref(false)
    const pwdEyeStatus = ref(true)
    const subPwdStatus = ref(true)
    const pwdType = ref('password')
    const subPwdType = ref('password')
    const confirm = () => {
      // if (!form.confirmPwd) {
      //   return Promise.reject('请输入密码')
      // }
      if (form.password !== form.confirmPwd) {
        return Promise.reject('密码不一致，请检查后重新输入')
      }
      if (form.password === form.confirmPwd) {
        return Promise.resolve()
      }
      return Promise.reject('请输入密码')
    }
    const rules = reactive({
      password: [
        {
          // required: true,
          message: window.localStorage.getItem('ErrorMessage') || '请输入正确的8-50个字符，且同时包含数字、字母的密码',
          trigger: 'blur'
        },
        {
          pattern: new RegExp(window.localStorage.getItem('RegExp')) || /^(?=.*\d)(?=.*[a-zA-Z]).{8,50}$/,
          message: window.localStorage.getItem('ErrorMessage') || '请输入正确的8-50个字符，且同时包含数字、字母的密码',
          trigger: 'blur'
        }
      ],
      confirmPwd: [
        {
          // required: true,
          validator: confirm,
          trigger: 'blur'
        }
      ]
    })
    const { validate, validateInfos, mergeValidateInfo } = useForm(form, rules)
    const errorInfos = computed(() => {
      return mergeValidateInfo(toArray(validateInfos))
    })
    watch(form, () => {
      emit('update:formProp', form)
    })
    onMounted(() => {
      // console.log(validateInfos.password)
    })
    async function loseFocus() {
      await validate()
        .then(() => {
          ConfirmPwdStatus.value = true
          isDisable.value = true
        })
        .catch(() => {
          ConfirmPwdStatus.value = false
          isDisable.value = false
        })
      emit('info', {
        type: 'ConfirmPwd',
        status: form.password === form.confirmPwd,
        ConfirmPwdStatus: ConfirmPwdStatus.value,
        isDisable: isDisable.value,
        password: form.password,
        confirmPassWord: form.confirmPwd
      })
    }
    const changeEye = () => {
      pwdType.value = pwdType.value === 'password' ? 'text' : 'password'
      pwdEyeStatus.value = !pwdEyeStatus.value
    }

    const subChangeEye = () => {
      subPwdType.value = subPwdType.value === 'password' ? 'text' : 'password'
      subPwdStatus.value = !subPwdStatus.value
    }

    return {
      form,
      err,
      rules,
      loseFocus,
      changeEye,
      subChangeEye,
      isRedBulePwd,
      isRedBuleConFPwd,
      errorInfos,
      validateInfos,
      pwdEyeStatus,
      pwdType,
      subPwdType,
      subPwdStatus
    }
  }
}
</script>
<style lang="scss">
.conFirm {
  position: relative;
  .errinfo {
    height: 17px;
    position: absolute;
    top: 0;
    left: 37px;
    font-family: PingFangSC-Regular;
    font-size: 12px;
    color: #d74472;
    text-align: left;
    font-weight: 400;
  }
  .icon {
    width: 18px;
    height: 18px;
    float: left;
    margin-right: 10px;
  }
  .pwd-eye {
    cursor: pointer;
    height: 18px;
  }
}
</style>
