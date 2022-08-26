<template>
  <div class="drawer-steps-header">
    <x-steps :selectIndex="step" style="flex: 1;">
      <x-step v-for="title of titles" :key="title" :title="title"></x-step>
    </x-steps>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import XSteps from '@/smart-ui-vue/XSteps.vue'
import XStep from '@/smart-ui-vue/XStep.vue'

export default defineComponent({
  name: 'DrawerStepsHeader',
  components: { XStep, XSteps },
  props: {
    step: { type: Number, default: 0 },
    titles: { type: Array as PropType<string[]>, default: () => [] },
  },
  emits: [],
  setup() {
    return {}
  },
})
</script>

<style lang="scss">
.drawer-steps-header {
  position: absolute;
  top: 70px;
  right: 40px;
  left: 40px;
  overflow: auto;
  height: 20px;
}
</style>
