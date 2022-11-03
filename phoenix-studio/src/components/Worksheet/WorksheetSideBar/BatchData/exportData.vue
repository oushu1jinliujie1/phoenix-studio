<template>
  <div>123</div>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, onUnmounted, PropType, reactive, ref, toRefs, watch } from 'vue'

export default defineComponent({
  name: 'ExportData',
  props: {
    exportFromBasic: {
      type: Boolean,
      default: false
    },
    schemaName: {
      type: String,
      required: true,
    },
  },
  setup() {
    return {
    }
  }
})
</script>

<style lang="scss">
</style>