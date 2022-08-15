<template>
  <a-config-provider :locale="locale" :prefixCls="modifyVars['@ant-prefix']">
    <router-view></router-view>
  </a-config-provider>
</template>

<script lang="ts">
import { defineComponent, provide } from 'vue'
import zhCN from 'ant-design-vue/es/locale/zh_CN'
/* eslint @typescript-eslint/no-var-requires: "off" */
// @ts-ignore
const modifyVars = require('/src/smart-ui-vue/modifyVars')

export default defineComponent({
  name: 'App',
  setup() {
    provide('$moduleName', 'phoenix-studio')
    console.log('oushudb mode: ', process.env.VUE_APP_LAVA_MODE)
    return {
      locale: zhCN,
      modifyVars,
    }
  },
})

</script>

<style lang="scss">
@font-face {
  font-family: 'codicon';
  src: url("/fonts/codicon.ttf") format("truetype");
}
@font-face {
  font-family: 'codicon1';
  src: url("/fonts/codicon.ttf") format("truetype");
}

.app-container {
  width: 100%;
  min-height: calc(100% - 60px);
  background: linear-gradient(0deg, #F1F1F1 57.89%, #FFFFFF 92.8%, #FFFFFF 95.08%);
}

p {
  margin-bottom: 0;
}
</style>
