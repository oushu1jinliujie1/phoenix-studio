<template>
  <div class="fid-pwd-moudle">
    <div class="fid-pwd-main">
      <div class="fin-pwd-content">
        <!-- <Icon name='login/oushu-title'  class="oushu-title" image /> -->
        <div class="header">
          <h1>欢迎</h1>
          <p>使用找回密码</p>
        </div>
        <div class="step">
          <a-steps :current="current">
            <template #progressDot="{ index, status, prefixCls }">
              <x-popover>
                <template #content>
                  <span>step {{ index }} status: {{ status }}</span>
                </template>
                <span :class="`${prefixCls}-icon-dot`"/>
              </x-popover>
            </template>
            <a-step :disabled="true" title="1·确认账户"/>
            <a-step :disabled="true" title="2·安全验证"/>
            <a-step :disabled="true" title="3·重置密码"/>
            <a-step/>
          </a-steps>
        </div>
        <ResetPwd v-show="current === 3" @status="status"/>
        <ConfirmAccount v-if="current === 1" @status="status"/>
        <SecureVerify v-show="current === 2" @status="status"/>
        <x-button
            :disabled="!Bstatus"
            class="resetPwd"
            type="primary"
            @click="done"
        >{{ clickStatus }}
        </x-button
        >
        <Icon class="left-top" image name="login/left-top"/>
        <Icon class="left-bottom" image name="login/left-bottom"/>
        <Icon class="bottom-center" image name="login/bottom-center"/>
        <Icon class="top-center" image name="login/bottom-center"/>
        <Icon class="right-top" image name="login/right-top"/>
        <Icon class="right-bottom" image name="login/right-bottom"/>
      </div>

    </div>
    <div class="oushu-id">
      © 2021 cloud.oushu.com 版权所有 京ICP备20028197号 京公网安备 11010802032740号
    </div>
  </div>
</template>

<script>
import ResetPwd from './ResetPwd.vue'
import ConfirmAccount from './ConfirmAccount.vue'
import SecureVerify from './SecureVerify.vue'
import { reactive, ref, computed } from 'vue'
import { notification } from 'ant-design-vue-3'
import { useRouter } from 'vue-router'
import XButton from '../../smart-ui-vue/XButton.vue'
// import XSteps from '.././../smart-ui-vue/XSteps.vue'
// import XStep from '.././../smart-ui-vue/XStep.vue'
import XPopover from '../../smart-ui-vue/XPopover.vue'
import { useStore } from 'vuex'
import Icon from '../Icon.vue'
import { login, retrievePassword, process } from '../../api/login'

export default {
  name: 'FindPwd',
  components: {
    ResetPwd,
    ConfirmAccount,
    SecureVerify,
    XButton,
    // XSteps,
    // XStep,
    Icon,
    XPopover,
  },
  setup() {
    const router = useRouter()
    const current = ref(1)
    const Bstatus = ref(false)
    const clickStatus = ref('下一步')
    const type = ref(null)
    const store = useStore()
    const Secureverify = reactive({
      phone: null,
      code: null,
    })
    const ResetPwdData = reactive({
      password: null,
      confirmPassword: null,
      user_name: null,
      phone: null,
      verify_code: null,
    })
    const ConfirmAccountData = reactive({
      captchaId: null,
      name: null,
      verification: null,
    })
    const status = (value) => {
      Bstatus.value = value.status
      console.log(Bstatus.value, 'Bstatus')
      type.value = value.type
      if (value.type === 'ConfirmAccount') {
        ResetPwdData.user_name = value.formState.name
        store.commit('changeUserName', value.formState.name)
        ConfirmAccountData.captchaId = value.captchaId
        ConfirmAccountData.verification = value.formState.verification
      }
      if (value.type === 'SecureVerify') {
        Secureverify.phone = value.phone
        Secureverify.code = value.code
        ResetPwdData.phone = value.phone
        ResetPwdData.verify_code = value.code
      }
      if (value.type === 'ResetPwd') {
        ResetPwdData.password = value.Pwd.password
        ResetPwdData.confirmPassword = value.Pwd.confirmPassWord
      }
    }
    const done = async() => {
      Bstatus.value = false
      if (type.value === 'ConfirmAccount') {
        process({
          captcha_id: ConfirmAccountData.captchaId,
          captcha_code: ConfirmAccountData.verification,
        }).then((data) => {
          if (data.meta.success) {
            current.value++
          } else {
            notification.error({
              message: 'Notification Title',
              description: data.meta.status_code,
            })
          }
        })
      }
      if (type.value === 'SecureVerify') {
        await login({
          login_type: 2,
          phone: Secureverify.phone,
          verify_code: Secureverify.code,
        }).then((data) => {
          // console.log(data)
          if (data.meta.success) {
            current.value += 1
          } else {
            notification.error({
              message: 'Notification Title',
              description: data.meta.status_code,
            })
            // message.error(data.meta.status_code)
          }
        })
      }
      if (type.value === 'ResetPwd') {
        const res = {
          password: ResetPwdData.password,
          phone: ResetPwdData.phone,
          user_name: ResetPwdData.user_name,
          verify_code: ResetPwdData.verify_code,
        }
        // console.log(res)
        await retrievePassword(res).then((data) => {
          if (data.meta.success) {
            // message.success('密码重置成功')
            notification.success({
              message: 'Notification Title',
              description: '密码重置成功',
            })
            router.push('/login')
            current.value++
          }
        })
      }
      // console.log(current.value)
      if (current.value === 3) {
        clickStatus.value = '完成'
      } else {
        clickStatus.value = '下一步'
      }
    }
    const marginTop = computed(() => {
      return document.body.clientHeight > 900 ? '150px' : '20px'
    })
    return {
      title: '找回密码',
      current,
      clickStatus,
      Bstatus,
      status,
      done,
      ResetPwdData,
      marginTop,
    }
  },
}
</script>
<style lang="scss">
@media screen and (max-height: 900px) {
  .fid-pwd-moudle {
    padding-top: 125px;
    z-index: 23;
  }
  .fid-pwd-main {
    height: 700px !important;
    display: unset !important;
  }
  .left-bottom {
    position: absolute;
    left: -40px !important;
    bottom: -140px !important;
    width: 55px;
    height: 55px;
  }
  .bottom-center {
    position: absolute;
    right: 255px !important;
    bottom: -88px !important;
    width: 55px;
    height: 55px;
  }
  .top-center {
    position: absolute;
    left: 170px;
    top: calc(-155px + -55px) !important;
    width: 55px;
    height: 55px;
  }
}

.fid-pwd-moudle {
  width: 100vw;
  height: 100%;
  position: relative;

  .fid-pwd-main {
    width: 1300px;
    height: 100%;
    display: flex;
    align-items: center;
    margin: 0 auto;
    position: relative;

    .fin-pwd-content {
      margin: 0 auto;
      width: 600px;
      background: #FFFFFF;
      box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.15);
      border-radius: 20px;
      padding: 40px;
      position: relative;

      .left-top {
        position: absolute;
        left: calc(-130px + -148px);
        top: -65px;
        width: 130px;
        height: 130px;
      }

      .left-bottom {
        position: absolute;
        left: -25px;
        bottom: calc(-148px + -55px);
        width: 55px;
        height: 55px;
      }

      .bottom-center {
        position: absolute;
        right: 255px;
        bottom: calc(-88px + -55px);
        width: 55px;
        height: 55px;
      }

      .top-center {
        position: absolute;
        left: 170px;
        top: calc(-170px + -55px);
        width: 55px;
        height: 55px;
      }

      .right-top {
        width: 55px;
        height: 55px;
        position: absolute;
        top: -88px;
        right: -88px;
      }

      .right-bottom {
        width: 130px;
        height: 130px;
        position: absolute;
        right: calc(-130px + -57px);
        bottom: -140px;
      }

      .oushu-title {
        position: absolute;
        top: -102px;
        left: 50%;
        width: 200px;
        height: 82px;
        transform: translateX(-50%);
      }

      .header {
        font-family: PingFang SC;
        font-style: normal;
        font-weight: normal;

        h1 {
          font-size: 36px;
          line-height: 36px;
          color: #336CFF;
        }

        p {
          font-size: 18px;
          line-height: 25px;
          margin-top: 10px;
          color: #F5991B;
        }
      }
    }

    .step {
      width: 100%;
      margin: 20px auto 0;
      position: relative;
      padding-top: 30px;

      .#{$ant-prefix}-steps {
        .#{$ant-prefix}-steps-item-icon {
          margin-right: 1px;
        }
        .#{$ant-prefix}-steps-icon-dot{
          display: none;
        }

        .#{$ant-prefix}-steps-item:last-child {
          .#{$ant-prefix}-steps-item-icon {
            display: none;
          }
        }
      }

      .#{$ant-prefix}-steps-item-tail::after {
        display: inline-block;
        width: 167px !important;
        margin-right: 10px;
      }
    }

    .resetPwd {
      width: 100%;
      height: 30px;
      margin: 0 auto;
      background: #336cff;
      box-shadow: 0px 10px 15px 0px rgba(0, 0, 0, 0.1);
      border-radius: 4px;
      margin-top: 20px;
      display: block !important;
      align-items: center;
    }

    .#{$ant-prefix}-steps-item-title {
      position: absolute !important;
      top: -31px !important;
      font-family: PingFangSC-Light;
      font-size: 18px;
      color: #282b2e;
      font-weight: 200;
    }

    .#{$ant-prefix}-steps-item-tail {
      position: inherit;
    }

    .#{$ant-prefix}-steps-label-vertical {
      .#{$ant-prefix}-steps-item-tail {
        margin-right: 10px;
        width: 167px;
        margin-left: -10px;
      }

      .#{$ant-prefix}-steps-item-content {
        text-align: left;
      }
    }

    .#{$ant-prefix}-steps-item-container {
      position: relative;
    }

    .#{$ant-prefix}-steps-item-finish
    > .#{$ant-prefix}-steps-item-container
    > .#{$ant-prefix}-steps-item-tail::after {
      background: #282b2e !important;
    }

    .#{$ant-prefix}-steps-item-finish
    .#{$ant-prefix}-steps-item-icon
    > .#{$ant-prefix}-steps-icon
    .#{$ant-prefix}-steps-icon-dot {
      display: none;
    }

    .#{$ant-prefix}-steps-item-process
    .#{$ant-prefix}-steps-item-icon
    > .#{$ant-prefix}-steps-icon
    .#{$ant-prefix}-steps-icon-dot {
      background: #282b2e !important;
      margin-left: -5px;
    }

    .#{$ant-prefix}-steps-item-wait
    .#{$ant-prefix}-steps-item-icon
    > .#{$ant-prefix}-steps-icon
    .#{$ant-prefix}-steps-icon-dot {
      display: none;
    }

    .#{$ant-prefix}-steps-item-process
    > .#{$ant-prefix}-steps-item-container
    > .#{$ant-prefix}-steps-item-content
    > .#{$ant-prefix}-steps-item-title {
      color: #b1b2b5;
    }

    .#{$ant-prefix}-steps-item-wait
    > .#{$ant-prefix}-steps-item-container
    > .#{$ant-prefix}-steps-item-content
    > .#{$ant-prefix}-steps-item-title {
      color: #b1b2b5;
    }

    .#{$ant-prefix}-steps-dot .#{$ant-prefix}-steps-item-tail::after,
    .#{$ant-prefix}-steps-dot.#{$ant-prefix}-steps-small .#{$ant-prefix}-steps-item-tail::after {
      width: calc(100% - 20px);
      height: 2px;
      margin-left: 12px;
      margin-top: 10px;
    }

    .#{$ant-prefix}-steps-dot .#{$ant-prefix}-steps-item-icon .#{$ant-prefix}-steps-icon-dot::after,
    .#{$ant-prefix}-steps-dot.#{$ant-prefix}-steps-small
    .#{$ant-prefix}-steps-item-icon
    .#{$ant-prefix}-steps-icon-dot::after {
      margin-top: 10px;
    }

    .#{$ant-prefix}-steps-dot {
      .#{$ant-prefix}-steps-item-finish {
        .#{$ant-prefix}-steps-item-icon {
          // top: 10px;
          margin-top: -3.5px !important;
          background: #282B2E !important;
          width: 8px !important;
          height: 8px !important;
          border-radius: 10px !important;
          margin-left: 166px !important;
          display: inline-block !important;
        }
      }
    }
  }

  .oushu-id {
    position: absolute;
    bottom: 10px;
    left: 50%;
    opacity: 0.6;
    transform: translateX(-50%);
    font-family: PingFang SC;
    font-style: normal;
    font-weight: normal;
    font-size: 14px;
    line-height: 40px;
    text-align: right;
    color: #336CFF;
  }
}
</style>
