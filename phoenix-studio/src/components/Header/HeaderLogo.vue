<template>
  <div class="v-common-header-logo">
    <span class="header-logo-span">Phoenix Studio</span>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'HeaderLogo',
  components: { },
  props: {},
  emits: [],
  setup() {

    return {
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

  .header-logo-span {
    display: inline-block;
    height: 40px;
    font-weight: 500;
    font-size: 24px;
    line-height: 40px;
    color: #3D71F5;
    user-select: none;
  }
}
</style>
