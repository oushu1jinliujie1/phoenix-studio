<template>
  <div class="header-main">
    <div class="main-module">
      <div class="logoCol">
        <span v-if="isPremise">
          <Icon v-if="logoType === LOGO_TYPE_OUSHU" image name="login/logo" class="logo-icon" @click='handleGoHome'/>
          <img v-if="logoType === LOGO_TYPE_CUSTOM" :src="logoPath" class="logo-img" alt="logo" @click='handleGoHome'>
        </span>
        <Icon v-else class="logo-icon" image name="login/logo" @click='handleGoHome'/>
      </div>
      <div class="logon-header-right">
      </div>
    </div>
  </div>
</template>


<script>
import { useRouter, useRoute } from 'vue-router'
import { ref, watch } from 'vue'
import Icon from '../Icon.vue'
import { useLogo, LOGO_TYPE_OUSHU, LOGO_TYPE_CUSTOM } from '../../hooks/useLogo'
export default {
  name: 'HeaderOuter',
  components: {
    Icon
  },
  setup() {
    const isPremise = process.env.VUE_APP_LAVA_MODE === 'premise'
    const router = useRouter()
    const route = useRoute()

    const { logoType, logoPath } = useLogo()


    // 用户没登录，点击 Logo 时会被路由守卫拦截，仍定向到当前路由，所以页面看起来没反应
    const handleGoHome = () => router.push('/')

    return {
      LOGO_TYPE_OUSHU,
      LOGO_TYPE_CUSTOM,
      isPremise,
      router,
      handleGoHome,
      logoType,
      logoPath
    }
  }
}

</script>
<style lang="scss">
.header-main{
  width: 100%;
  background: #FFFFFF;
  box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.05);
  position: fixed;
  top: 0;
  left: 0;
  z-index: 22;
  .main-module{
    width: 50%;
    margin:0 25%;
    display: flex;
    align-items: center;
    display: flex;
    align-items: center;
    justify-content: space-between;
    .logoCol {
      min-width: 140px;
      height: 36px;
      margin-right: 16px;
      margin-left: -10px;

      .logo-icon, .logo-img {
        width: 200px;
        height: 40px;
        cursor: pointer;
      }
    }
    .logo_name{
      margin-left: 5px;
      vertical-align: middle;
      width: 70px;
      margin-right: 34px;
      font-size: 14px;
      line-height: 20px;
      color: #282B2E;
      cursor: pointer;
    }
    .logo{
      //margin-left: 25%;
      cursor: pointer;
      width: 73.67px;
      margin-top: -4px;
    }
    .header-main-title{
      cursor: pointer;
      .icon{
        width: 70px;
        height: 13px;
      }
    }
    .logon-header-right, .register{
      display: flex;
      align-items: center;
      .separate-line{
        display: inline-block;
        width: 1px;
        height: 16px;
        margin: 0 23px;
        border: 1px solid #DCDDE0;
      }
      .header-main-list {
        cursor: pointer;
        height: 20px;
        width: 28px;
      }
      .header-main-active {
        color: #336CFF;
        height: 60px;
        line-height: 56px;
        border-top: 2px solid #336CFF;
        border-top-left-radius: 1px;
        border-top-right-radius: 1px;
        cursor: pointer;
      }
    }

    .logon-header-right {
      height: 60px;
    }
  }
}
</style>
