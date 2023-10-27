<template>
  <a-modal dialogClass="x-modal">
    <template v-for="item in slots" v-slot:[item]>
      <slot :name="item"></slot>
    </template>
  </a-modal>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue'
import { Modal as AModal } from 'ant-design-vue'

export default defineComponent({
  name: 'XModal',
  components: { AModal },
  setup(props, context) {
    return {
      slots: computed(() => Object.keys(context.slots))
    }
  }
})
</script>

<style lang="scss">
@import '../styles/components/XModal/index';
</style>
