<template>
  <div class="drawer-steps-content">
    <slot></slot>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'DrawerStepsContent',
  components: {},
  props: {},
  emits: [],
  setup() {
    return {}
  },
})
</script>

<style lang="scss">
.drawer-steps-content {
  position: absolute;
  top: 100px;
  right: 40px;
  bottom: 60px;
  left: 40px;
  overflow: auto;
}
</style>