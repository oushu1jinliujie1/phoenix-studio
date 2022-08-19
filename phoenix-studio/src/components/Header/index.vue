<template>
  <nav class="v-common-header">
    <section class="v-common-header-section">
      <header-logo></header-logo>
    </section>
    <section class="v-common-header-section">
      <header-user-center :userInfoRef="userInfoRef">
      </header-user-center>
    </section>
  </nav>
</template>

<script lang="ts">
/* eslint-disable no-return-assign */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable no-magic-numbers */
/* eslint-disable max-len */
import { computed, defineComponent, ref, watch } from 'vue'
import HeaderLogo from '@/components/Header/HeaderLogo.vue'
import HeaderUserCenter from '@/components/Header/HeaderUserCenter.vue'
import { useStore } from 'vuex'

export default defineComponent({
  name: 'Header',
  components: {
    HeaderUserCenter,
    HeaderLogo,
  },
  props: {},
  emits: [],
  setup() {
    const store = useStore()
    const userInfoRef = ref(JSON.parse(window.localStorage.getItem('userInfo') || '{}'))
    // 1. 用户登录后会将 userInfo 同步到 GlobalState
    // 2. 已经登录过，当刷新页面时，走这段逻辑，将用户信息同步到 GlobalState
    store.commit('global/setGlobalState', { userInfo: userInfoRef.value })

    const userInfoState = computed(() => store.state.global.data?.userInfo)

    const isShowMsgCenterDrawerRef = computed(() => {
      const globalState = store.state.global.data
      const globalSettings = globalState.settings
      return globalSettings && globalSettings.visible && globalSettings.visible.msgCenterDrawerVisible
    })

    watch(userInfoState, (userInfo) => {
      if (userInfo) {
        userInfoRef.value = userInfo
      }
    }, { immediate: true })
    return {
      lavaMode: process.env.VUE_APP_LAVA_MODE || 'cloud',
      userInfoRef,
      isShowMsgCenterDrawerRef,
    }
  }
})

</script>

<style lang="scss">
.v-common-header {
  position: fixed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  top: 0;
  // message z-index: 1010
  z-index: 1000;
  width: 100%;
  padding: 0 20px;
  white-space: nowrap;
  background: #ffffff;
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.15);
  height: 60px;

  .v-common-header-section {
    display: flex;
    align-items: center;
  }

  .v-common-header-section:last-child {
    justify-content: flex-end;
  }
}

.v-common-header-item {
  position: relative;
  height: 60px;
  display: flex;
  align-items: center;
  margin-left: 48px;
  cursor: pointer;

  &:hover {
    color: #336CFF;
  }
}


.v-common-header-item.active {
  color: #336CFF;
  cursor: pointer;
  border-top: 4px solid #336CFF;
  margin-top: -4px;

  span {
    color: #336CFF
  }
}

.v-common-header-item-link {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  cursor: pointer;
  height: 32px;
  margin: 10px -20px;
  padding: 0 20px;
  border-radius: 4px;

  .v-common-header-item-icon {
    margin-right: 10px;
  }
}

.v-common-header-item-link.active {
  color: #336CFF;
}

.v-common-header-item-link:hover {
  background: rgba(51, 108, 255, 0.2);
}

.v-common-header-right-line {
  position: relative;
}

.v-common-header-right-line::after {
  position: absolute;
  content: '';
  top: 22px;
  right: -24px;
  height: 16px;
  width: 1px;
  background: #DCDDE0;
}

.v-common-header-item:hover, .v-common-header-item.active {
  .v-common-header-item-icon {
    display: none;

    .icon {
      margin-top: -4px;
    }

    &.active {
      display: inline-block !important;
    }
  }
}

.v-common-header-item-icon {
  &.active {
    display: none;
  }

  .icon {
    width: 18px;
    height: 18px;
    margin-top: -4px;
    vertical-align: middle;
  }
}
</style>
