<template>
  <div class="drawer-steps-footer">
    <div class="drawer-steps-footer-button-group">
      <x-tooltip v-if="step!==0" :title="prevStepTooltip" :visible="nextStepTooltip===''?false:undefined">
        <x-button :disabled="disablePrevStep" style="margin-right: 10px;" @click="prevStep">
          <icon color="" name="prev"></icon>
          <span style="margin-left: 5px;">上一步</span>
        </x-button>
      </x-tooltip>
      <x-tooltip v-if="step<maxStep" :title="nextStepTooltip" :visible="nextStepTooltip===''?false:undefined">
        <x-button :disabled="disableNextStep" type="primary" @click="nextStep">
          <icon color="" name="next"></icon>
          <span style="margin-left: 5px;">下一步</span>
        </x-button>
      </x-tooltip>
      <slot name="option"></slot>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import XButton from '@/smart-ui-vue/XButton.vue'
import Icon from '@/components/Icon.vue'
import XTooltip from '@/smart-ui-vue/XTooltip.vue'

export default defineComponent({
  name: 'DrawerStepsFooter',
  components: { XTooltip, Icon, XButton },
  props: {
    step: { type: Number, default: 1 },
    maxStep: { type: Number, default: Number.MAX_SAFE_INTEGER },
    nextStepTooltip: { type: String, default: '' },
    prevStepTooltip: { type: String, default: '' },
    isCustomNextStep: { type: Boolean, default: false },
    isCustomPrevStep: { type: Boolean, default: false },
    disablePrevStep: { type: Boolean, default: false },
    disableNextStep: { type: Boolean, default: false },
  },
  emits: ['update:step', 'nextStep', 'prevStep'],
  setup(props, context) {
    return {
      nextStep: () => {
        if (props.isCustomNextStep) {
          context.emit('nextStep', props.step + 1)
        } else {
          context.emit('update:step', props.step + 1)
        }
      },
      prevStep: () => {
        if (props.isCustomNextStep) {
          context.emit('nextStep', props.step - 1)
        } else {
          context.emit('update:step', props.step - 1)
        }
      },
    }
  },
})
</script>

<style lang="scss">
.drawer-steps-footer {
  position: absolute;
  right: 0;
  bottom: 0;
  left: 0;
  display: flex;
  align-items: center;
  padding: 10px 40px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);

  .drawer-steps-footer-button-group {
    display: flex;
    align-items: center;
  }
}
</style>
