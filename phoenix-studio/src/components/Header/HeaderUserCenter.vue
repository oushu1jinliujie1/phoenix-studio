<template>
  <div class="v-common-header-user-center v-common-header-item">
    <x-dropdown
        :overlayStyle="{ top: '65x' }"
        placement="bottomCenter">
      <smart-avatar :userId='userInfoRef?.id'></smart-avatar>
      <template #overlay>
        <div class="v-common-header-user-center-overlay">
          <header class="v-common-header-user-center-overlay-header">
            <div class="v-common-header-user-center-user-name-type">
              <x-tooltip>
                <template #title>{{ userInfoRef.name_remark || '姓名' }}</template>
                <h2 :class="{ 'no-name': !userInfoRef.name_remark }">{{ userInfoRef.name_remark || '姓名' }}</h2>
              </x-tooltip>
            </div>
            <div class="v-common-header-user-center-info-root">
              <x-tooltip>
                <template #title>{{ userInfoRef.username }}</template>
                <span style="margin-right: 10px;">{{ userInfoRef.username }}</span>
              </x-tooltip>
              <x-tooltip>
                <template #title>复制</template>
                <Icon class="btn-copy" image name="copy-1" @click="handleCopyUserId"></Icon>
              </x-tooltip>
            </div>
          </header>
          <div class="v-common-header-user-center-overlay-divider" style="margin-bottom: 22px;"></div>
          <div>
            <x-button block @click="handleLogout">退出登录</x-button>
          </div>
        </div>
      </template>
    </x-dropdown>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue'
import XDropdown from '@/smart-ui-vue/XDropdown.vue'
import SmartAvatar from '@/components/SmartAvatar.vue'
import XTooltip from '@/smart-ui-vue/XTooltip.vue'
import Icon from '@/components/Icon.vue'
import { message, notification } from 'ant-design-vue-3'
import useClipboard from 'vue-clipboard3'
import { Router, useRoute, useRouter } from 'vue-router'
import XButton from '@/smart-ui-vue/XButton.vue'
import { logout } from '@/api/login'

export default defineComponent({
  name: 'HeaderUserCenter',
  components: { XButton, Icon, XTooltip, SmartAvatar, XDropdown },
  props: {
    userInfoRef: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: [],
  setup(props) {
    const route = useRoute()
    const router = useRouter()
    const { toClipboard } = useClipboard()
    const userTypeRef = computed(() => {
      return props.userInfoRef.userType === 'rootuser'
    })
    const copy = async(str: string) => {
      await toClipboard(str)
    }
    const handleCopyUserId = async() => {
      await copy(props.userInfoRef.username)
      message.success('已复制到剪贴板')
    }
    const handleLogout = () => _handleLogout(router)
    return {
      userTypeRef,
      handleCopyUserId,
      handleLogout,
    }
  }
})

function _handleLogout(router: Router) {
  logout().then((resp) => {
    if (resp.meta.success) {
      router.push('/login')
      window.localStorage.clear()
    } else {
      notification.error({
        message: 'Notification Title',
        description: resp.meta.status_code,
      })
    }
  })
}
</script>

<style lang="scss">
.v-common-header-user-center-overlay {
  width: 263px;
  height: 207px;
  background: #ffffff;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
  border-radius: 0 0 4px 4px;
  padding: 44px 40px 20px;

  .v-common-header-user-center-overlay-header {
    .v-common-header-user-center-user-name-type {
      display: flex;
      align-items: center;
      justify-content: space-between;

      h2 {
        margin-right: 20px;
        margin-bottom: 0;
        width: 100px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow:ellipsis;
        font-size: 18px;
      }
      .antv-tag.x-tag {
        margin-right: 0;
      }
    }

    .v-common-header-user-center-info-root {
      margin-top: 10px;
      display: flex;
      align-items: center;
      color: #85888C;
      font-size: 14px;
      white-space: nowrap;
      span {
        display: inline-block;
        width: 155px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow:ellipsis;
        font-size: 14px;
      }
    }
  }

  .v-common-header-user-center-overlay-divider {
    border: 1px solid #D5D8D8;
    margin: 22px 0 16px;
  }
}
</style>
