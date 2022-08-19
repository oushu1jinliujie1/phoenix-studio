<template>
  <div class="logon-container">
    <div class="logon-main">
      <div class="logon-left">
        <div class="title">欢迎</div>
        <div class="logon-title">登录{{ hideOushuMarksRef ? '' : 'PHOENIX数据平台' }}</div>
        <div class="login-container">
          <FormPwd @autofill='autofill' @sendParams="sendParams" @keydown.enter='confirmLogin' />
          <div class="logon-buttom">
            <x-button :class="!disabledBoolean ? '' : 'activeBtn'" :disabled="disabledBoolean" :loading="loginLoadding"
              block class="logon-btn" type="primary" @click="confirmLogin" data-comp-id="BTN-LOGON">
              <div v-if="!loginLoadding">登录</div>
              <div v-else>
                <!-- <a-spin class="raw login-spin"></a-spin> -->
                正在登录
              </div>
            </x-button>
          </div>
        </div>
        <Icon class="left-top" image name='login/left-top' />
        <Icon class="left-bottom" image name='login/left-bottom' />
        <Icon class="top-center" image name='login/bottom-center' />
        <Icon class="bottom-center" image name='login/bottom-center' />
        <Icon class="right-top" image name='login/right-top' />
        <Icon class="right-bottom" image name='login/right-bottom' />
      </div>
      <SecureVerifyModal v-if="showModal" :showModalStatus="showModal" @showModalStatus="showModalStatus" />
    </div>
    <div class="oushu-id" v-if="!hideOushuMarksRef">
      © 2021 cloud.oushu.com 版权所有 京ICP备20028197号 京公网安备 11010802032740号
    </div>
  </div>
</template>
<script>
import { useRouter } from 'vue-router'
import { reactive, ref, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue-3'
import { useStore } from 'vuex'
import { useI18n } from 'vue-i18n'
import { translateErrorMessage } from 'lava-fe-lib/lib-common/i18n'
import { login, toGetInitialPasswordFormat } from '../../api/login'
import XButton from '../../smart-ui-vue/XButton.vue'
import Icon from '../Icon.vue'
import FormPwd from './FormPwd'
import SecureVerifyModal from './SecureVerifyModal'

export default {
  name: 'Logon',
  components: {
    XButton,
    Icon,
    FormPwd,
    SecureVerifyModal,
  },
  setup() {
    const store = useStore()
    const router = useRouter()
    const { t } = useI18n()
    const showModal = ref(false)
    const disabledBoolean = ref(true)
    const loginErroe = ref(0)
    const logonParams = reactive({
      params: {
        login_type: 1,
        name: '',
        password: '',
      }
    })
    // 登录中
    const loginLoadding = ref(false)
    const hideOushuMarksRef = computed(() => store.state.settings.hideOushuMarks)
    const reset401Status = () => {
      window.sessionStorage.removeItem('is401')
    }
    // 登录
    const confirmLogin = () => {
      loginLoadding.value = true
      disabledBoolean.value = true
      login(logonParams.params).then(resp => {
        loginLoadding.value = false
        disabledBoolean.value = false
        if (resp.meta.success) {
          reset401Status()
          window.localStorage.setItem('userId', resp.data.id)
          window.localStorage.setItem('userInfo', JSON.stringify(resp.data))
          // 将用户信息同步到 GlobalState
          store.commit('setGlobalState', { userInfo: resp.data })
          router.push(store.state.settings.routeAfterLogin || '/main')
          return
        }
        if (resp.meta.success === false) {
          message.error({
            content: translateErrorMessage(t)(resp),
            duration: 3,
          })
          if (resp.meta.params !== null) loginErroe.value = resp.meta.params[0]
          if (loginErroe.value >= 3) showModal.value = true
          if (resp.meta.status_code === 'lava.error.login.passwordReset') {
            window.localStorage.setItem('userId', resp.data.id)
          }
        }
      })

    }
    // 接收组件传递来的账号密码
    const sendParams = (item) => {
      disabledBoolean.value = item.status
      logonParams.params.name = item.value.name
      logonParams.params.password = item.value.password
    }

    const showModalStatus = (item) => {
      showModal.value = item
    }
    // 是否自动填充
    const autofill = (value) => {
      disabledBoolean.value = value.status
    }

    const initialPasswordFormat = async() => {
      const { meta, data } = await toGetInitialPasswordFormat()
      if (meta.success) {
        window.localStorage.setItem('RegExp', data.RegExp)
        window.localStorage.setItem('ErrorMessage', data.ErrorMessage)
      }
    }

    onMounted(initialPasswordFormat())

    return {
      autofill,
      confirmLogin,
      sendParams,
      showModalStatus,
      showModal,
      loginErroe,
      disabledBoolean,
      logonParams,
      loginLoadding,
      hideOushuMarksRef,
    }
  },
}
</script>
<style lang="scss">
.logon-container {
  position: absolute;
  width: 100vw;
  height: calc(100% - 60px);
  background-image: linear-gradient(0deg,
      #f1f1f1 58%,
      #ffffff 93%,
      #ffffff 95%);

  .activeBtn {
    background: #336CFF;
    opacity: 0.5;
  }

  @media screen and (max-height: 900px) {
    .logon-main {
      height: 700px !important;
      z-index: 23;

      .left-bottom {
        position: absolute;
        bottom: -90px !important;
        left: calc(-104px + -55px) !important;
        width: 55px;
        height: 55px;
      }

      .top-center {
        position: absolute;
        left: 75px;
        top: calc(-178px + -55px) !important;
        width: 55px;
        height: 55px;
      }
    }
  }

  .oushu-id {
    position: absolute;
    bottom: 10px;
    left: 50%;
    font-family: PingFang SC;
    font-size: 14px;
    font-style: normal;
    font-weight: normal;
    opacity: 0.6;
    line-height: 40px;
    color: #336CFF;
    text-align: right;
    transform: translateX(-50%);
  }

  .logon-main {
    position: relative;
    display: flex;
    align-items: center;
    width: 1300px;
    height: 100%;
    margin: 0 auto;

    .oushu-title {
      position: absolute;
      top: -102px;
      left: 50%;
      width: 200px;
      height: 82px;
      transform: translateX(-50%);
    }

    .left-top {
      position: absolute;
      top: -65px;
      left: calc(-248px + -130px);
      width: 130px;
      height: 130px;
    }

    .left-bottom {
      position: absolute;
      bottom: calc(-118px + -55px);
      left: calc(-104px + -55px);
      width: 55px;
      height: 55px;
    }

    .bottom-center {
      position: absolute;
      bottom: calc(-58px + -55px);
      left: 175px;
      width: 55px;
      height: 55px;
    }

    .right-top {
      position: absolute;
      top: calc(-66px + -55px);
      right: calc(-150px + -55px);
      width: 55px;
      height: 55px;
    }

    .right-bottom {
      position: absolute;
      right: calc(-157px + -130px);
      bottom: calc(-130px + 40px);
      width: 130px;
      height: 130px;
    }

    .top-center {
      position: absolute;
      left: 75px;
      top: calc(-168px + -55px);
      width: 55px;
      height: 55px;
    }

    .oushu-title {
      position: absolute;
    }

    .logon-left {
      position: relative;
      width: 400px;
      padding: 40px 40px 20px 40px;
      // height: 414px;
      margin: 0 auto;
      background: #FFFFFF;
      border-radius: 20px;
      box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.15);

      .title {
        width: 72px;
        height: 50px;
        font-family: PingFangSC-Regular;
        font-size: 36px;
        font-weight: 400;
        color: #336CFF;
        text-align: left;
      }

      .logon-title {
        height: 25px;
        margin-top: 10px;
        font-family: PingFang SC;
        font-size: 18px;
        font-style: normal;
        font-weight: normal;
        line-height: 25px;
        color: #F5991B;
      }

      .login-container {
        width: 596px;
        // background-image: url('../../assets/images/signin_cloud_bg.webp');
        background-repeat: no-repeat;
        background-size: 100% 100%;

        .#{$ant-prefix}-form-item-control {
          width: 320px;
        }

        .icon {
          vertical-align: middle;
        }

        .logon-new {
          display: flex;
          align-items: flex-end;
          justify-content: space-between;
          width: 320px;
          height: 40px;
          margin-top: 20px;
          border-top: 1px solid #D5D8DB;

          button {
            width: 50%;
            height: 20px;
            padding: 0;
            font-family: PingFang SC;
            font-size: 14px;
            font-style: normal;
            font-weight: normal;
            line-height: 20px;
            color: #336CFF;
          }

          .btn-register {
            justify-content: flex-start;
            border-right: 1px solid #D5D8DB;
            border-radius: unset;
          }

          .btn-forget {
            justify-content: flex-end;
          }

          .x-divider {
            width: auto;
          }
        }

        .logon-buttom {
          display: flex;
          margin-top: 20px;
          margin-bottom: 20px;

          .logon-btn {
            width: 320px;
            // background: #336CFF;
            border-radius: 4px;

            .anticon-loading {
              margin-right: 10px;
            }
          }

          .btn-register {
            //margin-right: 23px;
            margin-left: 60px;

            &:hover {
              transform: translate(0, -2px);
            }
          }

          .btn-forget {
            &:hover {
              transform: translate(0, -2px);
            }
          }
        }

        .#{$ant-prefix}-tabs-nav .#{$ant-prefix}-tabs-tab {
          padding: 12px 0;
        }
      }

      .tabs-list {
        width: 320px;
        height: 150px;

        .#{$ant-prefix}-tabs-bar {
          display: flex;
          align-items: flex-end;
          height: 30px;
          margin: 20px 0 0 !important;
        }
      }
    }
  }
}
</style>
