<template>
  <div>previewSearchTable</div>
    <div class="v-oushudb-edit-column-form-btn-container">
    <x-button
      :loading="execLoading"
      type="primary"
      @click="handleConfirm"
    >
      <icon name="worksheet/submit"/>
      确认
    </x-button>
    <x-button @click="handleCancel">
      <icon name="worksheet/cancel"/>
      取消
    </x-button>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, toRefs } from 'vue'
import Icon from '@/components/Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { message } from 'ant-design-vue-3'
import { RuleObject } from 'ant-design-vue/es/form/interface'

export default defineComponent({
  name: 'previewSearchTable',
  props: {
    execLoading: Boolean
  },
  components: { Icon, ...smartUI },
  emits: ['confirm', 'close'],
  setup(props, context) {

    /**
     * 提交表单
     */
    const handleConfirm = () => {
      context.emit('confirm', {

      })
    }
    const handleCancel = () => {
      context.emit('close')
    }

    return {
      handleConfirm,
      handleCancel
    }
  }
})

</script>

<style lang="scss">

</style>
