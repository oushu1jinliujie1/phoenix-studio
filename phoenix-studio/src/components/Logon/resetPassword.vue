<template>
  <div class="reset-pwd-moudle">
    <div class="fid-pwd-main">
      <div class="fin-pwd-content">
        <!-- <Icon name='login/oushu-title'  class="oushu-title" image /> -->
        <div class="header">
          <h1>欢迎</h1>
          <p>使用重置密码</p>
        </div>
        <slot></slot>
        <Icon name="login/left-top" class="left-top" image />
        <Icon name="login/left-bottom" class="left-bottom" image />
        <Icon name="login/bottom-center" class="bottom-center" image />
        <Icon name="login/bottom-center" class="top-center" image />
        <Icon name="login/right-top" class="right-top" image />
        <Icon name="login/right-bottom" class="right-bottom" image />
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import Icon from '../Icon.vue'
export default {
  name: 'FindPwd',
  components: {
    Icon
  },
  setup() {
    const marginTop = computed(() => {
      return document.body.clientHeight > 900 ? '150px' : '20px'
    })
    return {
      marginTop
    }
  }
}
</script>
<style lang="scss">
@media screen and (max-height: 900px) {
  .reset-pwd-moudle{
    z-index: 23;
    .fid-pwd-main {
      height: 700px !important;
      display: flex !important;
      align-items: center;
      .top-center{
        position: absolute;
        left: 75px;
        top: calc(-220px + -55px) !important;
        width: 55px;
        height: 55px;
      }
    }
  }
}
.reset-pwd-moudle {
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
      background: #ffffff;
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
        top: calc(-186px + -55px);
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
          line-height: 50px;
          color: #336cff;
        }
        p {
          font-size: 18px;
          line-height: 25px;
          margin-top: 10px;
          color: #f5991b;
        }
      }
    }
  }
}
</style>
