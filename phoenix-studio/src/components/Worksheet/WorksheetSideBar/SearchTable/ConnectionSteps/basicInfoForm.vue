<template>
  <div>
    <x-form layout="vertical" :label-col="{ style: { height: '30px' } }">
      <x-form-item label="查询表名">
        <x-input :value="basicForm.name" :rules="basicNameRules" placeholder="请填写查询表名称" @update:value="(value: string) => { updateBasicForm(value, 'name') }">
          <template #prefix>
            <icon image name="worksheet/search_table_two_color"/>
          </template>
        </x-input>
      </x-form-item>
      <x-form-item label="中文名（选填）">
        <x-input :value="basicForm.comment" :rules="[]" placeholder="请填写中文名" @update:value="(value: string) => { updateBasicForm(value, 'comment') }">
          <template #prefix>
            <icon image name="worksheet/search_table_two_color"/>
          </template>
        </x-input>
      </x-form-item>
      <x-form-item label="描述（选填）" :label-col="{ style: { height: '16px' } }">
        <x-textarea :value="basicForm.description" autoSize placeholder="请填写描述" @update:value="(value: string) => { updateBasicForm(value, 'description') }">
          <template #prefixIcon>
            <icon image name="worksheet/search_table_desc_two_color"/>
          </template>
        </x-textarea>
      </x-form-item>
    </x-form>
  </div>
</template>

<script lang="ts">
/* eslint-disable vue/no-side-effects-in-computed-properties */

import { defineComponent, computed, ref } from 'vue'
import Icon from '@/components/Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { message } from 'ant-design-vue-3'
import { RuleObject } from 'ant-design-vue/es/form/interface'

export default defineComponent({
  name: 'addSearchTable',
  props: {
    basicForm: {
      type: Object,
      required: true,
    },
    initAlreadyExistNameList: {
      type: Array,
      required: true,
    },
  },
  components: { ...smartUI, Icon },
  emits: ['update:basicForm'],
  setup(props, context) {
    const notFinishMsgRef = ref('')
    const isFinishRef = computed(() => {
      if (props.basicForm.name === '') {
        notFinishMsgRef.value = '请填写查询表名称'
        return false
      } else {
        if (props.initAlreadyExistNameList?.includes(props.basicForm.name)) {
          notFinishMsgRef.value = '该查询表名称名已存在'
          return false
        }
        notFinishMsgRef.value = ''
        return true
      }
    })

    const validatePass = (rule: RuleObject, value: string) => {
      if (value === '') {
        return Promise.reject('请填写查询表名称')
      } else {
        if (props.initAlreadyExistNameList?.includes(props.basicForm.name)) {
          return Promise.reject('该查询表名称名已存在')
        }

        return Promise.resolve('')
      }
    }

    const basicNameRules = [
      // 非空
      { required: true, validator: validatePass },
    ]

    const updateBasicForm = (value: string, key: string) => {
      context.emit('update:basicForm', { ...props.basicForm, ...{ [key]: value } })
    }

    return {
      notFinishMsgRef,
      isFinishRef,

      basicNameRules,

      updateBasicForm,
    }

  }
})

</script>

<style lang="scss">

</style>
