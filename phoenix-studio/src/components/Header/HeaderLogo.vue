<template>
  <div class="v-common-header-logo">
    <span v-if="isPremise">
      <Icon v-if="logoType === LOGO_TYPE_OUSHU" image name="login/logo" class="logo-icon" @click='handleGoHome'/>
      <img v-if="logoType === LOGO_TYPE_CUSTOM" :src="logoPath" class="logo-img" alt="logo" @click='handleGoHome'>
    </span>
    <Icon v-else class="logo-icon" image name="login/logo" @click='handleGoHome'/>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { useRouter } from 'vue-router'
import Icon from '@/components/Icon.vue'
import { LOGO_TYPE_CUSTOM, LOGO_TYPE_OUSHU, useLogo } from '@/hooks/useLogo'

export default defineComponent({
  name: 'HeaderLogo',
  components: { Icon },
  props: {},
  emits: [],
  setup() {
    const router = useRouter()
    const isPremise = process.env.VUE_APP_LAVA_MODE === 'premise'
    const handleGoHome = () => router.push('/')
    const { logoType, logoPath } = useLogo()

    return {
      LOGO_TYPE_OUSHU,
      LOGO_TYPE_CUSTOM,
      isPremise,
      logoType,
      logoPath,
      handleGoHome,
    }
  }
})
</script>

<style lang="scss">
.v-common-header .v-common-header-logo {
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
</style>
