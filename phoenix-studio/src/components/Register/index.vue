<template>
  <div class="register-box">
    <div class="register-moudle">
      <div class="register-card">
        <div class="box">
          <!-- <Icon class="oushu-title" image name="login/oushu-title" /> -->
          <div class="RegisterCard">
            <!-- <x-spin v-if="loading"></x-spin> -->
            <div class="header">
              <h1>欢迎</h1>
              <p>注册{{ $store.state.settings.hideOushuMarks ? '' : 'PHOENIX数据平台' }}</p>
            </div>
            <x-form :model="form">
              <x-form-item
                  :rules="rules.userName"
                  :validateFirst="true"
                  name="userName"
                  style="width: 100%"
              >
                <x-input
                    v-model:value="form.userName"
                    :class="isRedBuleUser ? 'x-form-item-input' : ''"
                    class="Registeruser"
                    placeholder="请输入您的用户名"
                    @blur="loseFocused"
                    @focus="getFocus"
                >
                  <template #prefix>
                    <Icon
                        class="x-ant-input-icon-inactive"
                        image
                        name="login/accounte"
                    />
                    <Icon
                        class="x-ant-input-icon-active"
                        image
                        name="login/accounte_click"
                    />
                  </template>
                </x-input>
              </x-form-item>
            </x-form>
            <ConfirmPwd
                v-model:formProp="form.ConfirmPwd"
                @info="info"
                @validate="handleValidate"
            />
            <PhoneVerify
                v-model:formProp="form.phoneVerify"
                :inline="true"
                type="REGISTER"
                @info="info"
            />
            <div class="serve">
              <div class="cloud-vendors">
                <p>
                  云厂商（设置默认云厂商和区域，登录后可以在“个人中心/设置”中修改）
                </p>
                <div class="cloud-vendors-item">
                  <div
                      v-for="(item, index) in cloudVendors"
                      :key="index"
                      :class="{'selected': item.name === selectedCloud.name}"
                      class="cloud-item"
                      @click="handleClick(item, 'cloud')"
                  >
                    <Icon
                        :class="{[`cloud-item-${item.name}`]: true,}"
                        :name="`region_select/${item.name}`"
                        image
                    />
                    <!-- <Icon
                      v-show="item.select"
                      :name="`region_select/${item.icon}_click`"
                      image
                    /> -->
                    {{ item.tag }}
                  </div>
                </div>
              </div>
              <div class="regional">
                <p>地域</p>
                <div class="regional-box">
                  <div
                      v-for="(item, index) in selectedCloud.region"
                      :key="index"
                      :class="{'selected': item.chosen}"
                      class="cloud-item"
                      @click="handleClick(item, 'regional')"
                  >
                    {{ item.tag }}
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="explain">
            <div>
              <x-checkbox :checked='checked' @change='readChange'></x-checkbox>
              <span class="read" style="vertical-align: inherit;margin-left:13px;">我已阅读并接受<span class="regulations">《偶数用户条例》</span></span>
            </div>
            <div class="other">
              <span class="regulations" @click="goLogin">登录</span>
              <span class="regulations" @click="goRetrievePassword">找回密码</span>
            </div>
          </div>
          <x-button :disabled="isRegister" class="register" @click="done"
          >注册
          </x-button
          >
        </div>
        <div class="login-icon">
          <Icon class="left-top" image name="login/left-top"/>
          <Icon class="left-bottom" image name="login/left-bottom"/>
          <Icon class="bottom-center" image name='login/bottom-center'/>
          <Icon class="top-center" image name='login/bottom-center'/>
          <Icon class="right-top" image name="login/right-top"/>
          <Icon class="right-bottom" image name="login/right-bottom"/>
        </div>
      </div>
      <RegisterSuccModel v-model:visible="showModel"/>
    </div>
    <div class="oushu-id">
      © 2021 cloud.oushu.com 版权所有 京ICP备20028197号 京公网安备 11010802032740号
    </div>
  </div>
</template>

<script>
import PhoneVerify from '../PhoneVerify.vue'
import ConfirmPwd from '../ConfirmPwd.vue'
import Icon from '../Icon.vue'
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import RegisterSuccModel from './RegisterSuccModel.vue'
import { Form } from 'ant-design-vue'
import { message, notification } from 'ant-design-vue-3'
import { toArray } from 'lodash-es'
import { getRegionList } from '../../api/getRegionList'
import { getCloudPlatforms } from '../../api/getCloudPlatforms'
const useForm = Form.useForm
import XInput from '../../smart-ui-vue/XInput.vue'
import XForm from '../../smart-ui-vue/XForm.vue'
import XFormItem from '../../smart-ui-vue/XFormItem.vue'
import XButton from '../../smart-ui-vue/XButton.vue'
// import XCard from '../../smart-ui-vue/XCard.vue'
import XCheckbox from '../../smart-ui-vue/XCheckbox.vue'
import { isExistUser, registerUser } from '../../api/login'
// import XSpin from '../../smart-ui-vue/XSpin.vue'
export default {
  components: {
    PhoneVerify,
    ConfirmPwd,
    Icon,
    RegisterSuccModel,
    XInput,
    XForm,
    XFormItem,
    XButton,
    // XCard,
    XCheckbox,
    // XSpin
  },
  setup() {
    const router = useRouter()
    // vuex
    // 选中的云厂商
    const selectedCloud = ref({})
    // 选中的地域
    const selectedRegion = ref({})
    // loading
    const loading = ref(true)
    const status = reactive({
      userNameStatus: false,
      PhoneVerifyStatus: false,
      ConfirmPwdStatus: false,
    })
    const userInfo = reactive({
      password: '',
      confirmPassWord: '',
      phone: '',
      code: '',
    })
    const form = reactive({
      userName: '',
      ConfirmPwd: reactive({
        password: '',
        confirmPwd: '',
      }),
      phoneVerify: reactive({
        phone: '',
        code: '',
      }),
    })
    // 检测状态
    const registerButtonState = computed(() => {
      return status.userNameStatus && status.PhoneVerifyStatus && status.ConfirmPwdStatus
    })
    const checked = ref(false)
    const showModel = ref(false)
    const userName = ref(null)
    const signinAccounte = ref(false)
    // 云厂商
    const cloudVendors = ref([])
    // 地域
    const regional = ref([])
    // const cloudVendorsItem = ref(cloudVendors.value[0].vendor)
    // const regionalItem = ref(regional.value[0].vendor)
    const msg = ref(null)
    const isRegister = ref(true)
    const isRedBuleUser = ref(false)
    const loseFocus = async() => {
      signinAccounte.value = false
      if (form.userName !== '') {
        await isExistUser({
          str: form.userName,
          str_type: 0,
        })
          .then((data) => {
            if (
              Object.getOwnPropertyNames(data.data).length &&
                  data.meta.status_code !== 'lava.error.exist.userDuplicate'
            ) {
              status.userNameStatus = true
              isRedBuleUser.value = false
              return Promise.resolve()
            } else {
              status.userNameStatus = false
              isRedBuleUser.value = true
              return Promise.reject(
                (data.meta && data.meta.status_code) ||
                    'common.error.internal',
              )
            }
          })
      } else {
        status.userNameStatus = false
        isRedBuleUser.value = true
        // return Promise.reject('请输入用户名')
      }
      if (checked.value && registerButtonState.value) {
        isRegister.value = false
      } else {
        isRegister.value = true
      }
      return true
    }

    function info(value) {
      if (value.type === 'ConfirmPwd') {
        status.ConfirmPwdStatus = value.ConfirmPwdStatus
        userInfo.password = value.password
        userInfo.confirmPassWord = value.confirmPassWord
      }
      if (value.type === 'PhoneVerify') {
        status.PhoneVerifyStatus = value.PhoneVerifyStatus
        userInfo.phone = value.phone
        userInfo.code = value.verify
      }
      msg.value = value.value
      if (checked.value && registerButtonState.value) {
        isRegister.value = false
      } else {
        isRegister.value = true
      }
    }

    const rules = reactive({
      userName: [
        {
          required: true,
          validator: loseFocus,
          trigger: 'blur',
        },
      ],
    })
    const { validate, validateInfos, mergeValidateInfo } = useForm(form, rules)
    const errorInfos = computed(() => {
      return mergeValidateInfo(toArray(validateInfos))
    })
    const loseFocused = () => {
      validate('userName', { trigger: 'blur' })
    }
    const change = () => {
      if (checked.value && registerButtonState.value) {
        isRegister.value = false
      } else {
        isRegister.value = true
      }
    }

    function getFocus() {
      signinAccounte.value = true
    }

    function done() {
      console.log(selectedCloud.value, selectedRegion.value, '123123123')
      registerUser({
        user_name: form.userName,
        password: userInfo.password,
        phone: userInfo.phone,
        verify_code: userInfo.code,
        default_region: selectedRegion.value.name,
        default_platform: selectedCloud.value.name,
      })
        .then((data) => {
          if (data.meta.success) {
            showModel.value = true
          } else {
            message.error(data.meta.status_code)
          }
        })
    }

    // 表单错误信息
    /**
     * @handler
     * 验证表单
     * 处理错误显示
     */
    const handleValidate = (value) => {
      validate(value.type, { trigger: 'blur' })
    }

    function goLogin() {
      // sessionStorage.setItem('activeKey', 1)
      router.push('/login')
    }

    const goRetrievePassword = () => {
      router.push('/settings/findpwd')
      // sessionStorage.setItem('activeKey', 3)
    }
    const readChange = () => {
      checked.value = !checked.value
      if (checked.value && registerButtonState.value) {
        isRegister.value = false
      } else {
        isRegister.value = true
      }
    }
    const handleClick = (item, type) => {
      if (type === 'regional') {
        for (const val of selectedCloud.value.region) val.chosen = false
        item.chosen = true
        selectedRegion.value = item
      } else {
        selectedCloud.value = item
        selectedRegion.value = selectedCloud.value.region.find(val => val.chosen)
      }
    }
    const marginTop = computed(() => {
      return document.body.clientHeight > 900 ? '150px' : '20px'
    })
    const handleGetCurrentRegion = (cloudListValue) => {
      const res = cloudListValue
      selectedCloud.value = res.find(item => item.chosen)
      selectedRegion.value = selectedCloud.value.region.find(item => item.chosen)
    }
    onMounted(async() => {
      const res = await fetchCloudAndRegions()
      loading.value = false
      cloudVendors.value = res
      handleGetCurrentRegion(res)
    })
    return {
      title: '欢迎注册偶数实时湖仓数据平台',
      signinAccounte,
      cloudVendors,
      loading,
      regional,
      msg,
      isRegister,
      userName,
      change,
      getFocus,
      loseFocused,
      isRedBuleUser,
      goRetrievePassword,
      info,
      goLogin,
      done,
      showModel,
      form,
      handleValidate,
      errorInfos,
      handleClick,
      rules,
      validateInfos,
      checked,
      readChange,
      marginTop,
      selectedCloud,
      selectedRegion,
    }
  },
}
/**
 * @api getCloudPlatforms, getRegionList
 * @async
 * 从远端获取云平台和区域列表，以及默认云平台和区域
 * @return {Object} 云平台和配置列表
 */
export const fetchCloudAndRegions = async() => {
  let cloudPlatforms = await getCloudPlatforms()
  cloudPlatforms = cloudPlatforms.data.sort((a, b) => a.orders - b.orders)
  cloudPlatforms = await Promise.all(cloudPlatforms.map(platform => {
    return getRegionList(platform.name).then(res => {
      return {
        ...platform,
        region: res.data.map(item => ({ ...item, platform: platform.name })),
      }
    })
  }))
  return cloudPlatforms
}
</script>

<style lang="scss">
.register-box {
  display: flex;
  align-items: center;
  width: 100vw;
  height: 100%;
  margin: 0 !important;
  background: #fff;
  // position: relative;
  .oushu-id {
    position: absolute;
    bottom: 10px;
    left: 50%;
    font-family: PingFang SC;
    font-size: 14px;
    font-style: normal;
    font-weight: normal;
    line-height: 40px;
    color: #336CFF;
    opacity: 0.6;
    text-align: right;
    transform: translateX(-50%);
  }
}

@media screen and (max-height: 900px) {
  .register-box {
    display: block;
    // z-index: 23;
  }
  .register-moudle {
    // width: 100%;
    min-height: 700px !important;
    padding-top: 125px;
    padding-bottom: 90px;
    z-index: 23;
  }
  .register-box {
    .oushu-id {
      bottom: -100px !important;
    }
  }
  .register-card {
    // position: relative;
    .left-top {
      position: absolute;
      top: 55px !important;
      left: calc(-130px + -113px);
      width: 130px;
      height: 130px;
    }

    .top-center {
      position: absolute;
      left: 100px;
      top: -85px !important;
      z-index: -1;
      width: 55px;
      height: 55px;
    }
  }
}

.register-moudle {
  position: relative;
  width: 100%;
  margin: 0 auto;

  .register-card {
    .login-icon {
      position: absolute;
      top: 0;
      left: 50%;
      width: 600px;
      // z-index: -1;
      height: 604px;
      margin: 0 auto;
      transform: translateX(-50%);

      .left-top {
        position: absolute;
        top: calc(10px);
        left: calc(-130px + -148px);
        width: 130px;
        height: 130px;
      }

      .left-bottom {
        position: absolute;
        bottom: -78px;
        left: -55px;
        width: 55px;
        height: 55px;
      }

      .bottom-center {
        position: absolute;
        left: 319px;
        bottom: -10px;
        z-index: -1;
        width: 55px;
        height: 55px;
      }

      .top-center {
        position: absolute;
        left: 167px;
        top: calc(-55px + -73px);
        z-index: -1;
        width: 55px;
        height: 55px;
      }

      .right-top {
        position: absolute;
        top: -10px;
        right: -110px;
        width: 55px;
        height: 55px;
      }

      .right-bottom {
        position: absolute;
        right: calc(-56px + -130px);
        bottom: 0;
        width: 130px;
        height: 130px;
      }
    }

    .box {
      position: relative;
      z-index: 2;
      width: 600px;
      padding: 40px;
      margin: 0 auto;
      background: #fff;
      border-radius: 20px;
      box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.15);

      .oushu-title {
        position: absolute;
        top: -102px;
        left: 50%;
        width: 200px;
        height: 82px;
        transform: translateX(-50%);
      }

      .#{$ant-prefix}-card-bordered {
        border: none;
      }

      .#{$ant-prefix}-card-body {
        padding: 0;
      }

      .RegisterCard {
        .header {
          h1 {
            font-family: PingFang SC;
            font-size: 36px;
            font-style: normal;
            font-weight: normal;
            line-height: 36px;
            color: #336cff;
          }

          p {
            margin-top: 10px;
            font-family: PingFang SC;
            font-size: 18px;
            font-style: normal;
            font-weight: normal;
            line-height: 25px;
            color: #f5991b;
          }
        }

        .icon {
          float: left;
          width: 18px;
          height: 18px;
          margin-right: 10px;
        }

        .serve {
          .cloud-vendors-item {
            display: flex;
            min-height: 30px;

            .selected {
              .cloud-item {
                &-tencent {
                  color: #336CFF;
                }

                &-huawei {
                  color: #d74472;
                }

                &-ali {
                  color: #F5991B;
                }
              }
            }
          }

          .cloud-item {
            box-sizing: border-box;
            display: flex;
            align-items: center;
            justify-content: center;
            width: 122.5px;
            height: 30px;
            color: #282b2e;
            cursor: pointer;
            border: 1px solid #D5D8DB;
            border-radius: 4px;

            &:hover {
              border: 1px solid #d9d9d9;
            }
          }

          .cloud-item:not(:last-child) {
            margin-right: 10px;
          }

          p {
            margin-bottom: 10px;
            font-family: PingFang SC;
            font-size: 12px;
            font-style: normal;
            font-weight: normal;
            line-height: 17px;
            color: #b1b2b5;
          }

          .selected {
            border: 1px solid #336cff !important;
          }

          .cloud-vendors {
            margin-top: 20px;
          }

          .regional {
            margin-top: 10px;

            .regional-box {
              display: flex;
              min-height: 30px;
            }
          }
        }

        .haveUser {
          width: 98px;
          height: 20px;
          font-family: PingFangSC-Regular;
          font-size: 14px;
          font-weight: 400;
          line-height: 20px;
          color: #282b2e;
        }

        .signIn {
          width: 56px;
          height: 20px;
          font-family: PingFangSC-Regular;
          font-size: 14px;
          font-weight: 400;
          line-height: 20px;
          color: #336cff;
          cursor: pointer;

          &:hover {
            transform: translate(0, -2px);
          }
        }
      }
    }

    .explain {
      display: flex;
      justify-content: space-between;
      width: 100%;
      margin-top: 20px;

      .read {
        font-family: PingFang SC;
        font-size: 14px;
        font-style: normal;
        font-weight: normal;
        line-height: 20px;
      }

      .other {
        span {
          margin-left: 10px;
        }
      }

      .register-icon {
        width: 18px;
        height: 18px;
        vertical-align: bottom !important;
        cursor: pointer;
      }

      .read-main {
        display: inline-block;
        width: 12px;
        height: 12px;
        margin-right: 13px;
        line-height: 8px;
        text-align: center;
        vertical-align: middle;
        cursor: pointer;
        border: 1px solid #282b2e;
        border-radius: 2px;

        .read-main-checked {
          display: inline-block;
          width: 8px;
          height: 8px;
          cursor: pointer;
          background: #282b2e;
          border-radius: 1px;
        }
      }

      .regulations {
        font-family: PingFangSC-Regular;
        font-size: 14px;
        font-weight: 400;
        color: #336cff;
        cursor: pointer;

        &:hover {
          transform: translate(0, -2px);
        }
      }
    }

    .register {
      width: 100%;
      height: 30px;
      margin-top: 30px;
      font-family: PingFang SC;
      font-size: 14px;
      font-style: normal;
      font-weight: normal;
      line-height: 20px;
      color: #ffffff !important;
      background: #336cff !important;
      border-radius: 4px;
    }

    .register[disabled] {
      color: #FFF;
      background-color: #336CFF;
      border-color: #336CFF;
      opacity: 0.3;

      &:hover {
        color: #b1b2b5;
        background-color: #D5D8DB;
        border-color: #D5D8DB;
      }
    }
  }
}
</style>
