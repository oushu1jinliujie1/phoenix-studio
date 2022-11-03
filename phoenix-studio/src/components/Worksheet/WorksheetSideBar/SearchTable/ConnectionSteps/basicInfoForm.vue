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

import { defineComponent, computed, ref, reactive, watch } from 'vue'
import Icon from '@/components/Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { message } from 'ant-design-vue-3'
import { RuleObject } from 'ant-design-vue/es/form/interface'
import { checkIsInstanceName } from '@/lib/regexp'
import { duplicateSearchTable } from '@/api'

export default defineComponent({
  name: 'BasicInfoForm',
  props: {
    basicForm: {
      type: Object,
      required: true,
    },
    initialName: {
      type: String,
      required: true,
    },
  },
  components: { ...smartUI, Icon },
  emits: ['update:basicForm'],
  setup(props, context) {
    const state = reactive({
      nameDisabled: false
    })
    const notFinishMsgRef = ref('')
    const nameComputed = computed(() => props.basicForm.name)
    watch(nameComputed, async() => {
      if (nameComputed.value === '') {
        notFinishMsgRef.value = '请填写查询表名称'
        state.nameDisabled = true
        return
      }
      if (!checkIsInstanceName(nameComputed.value)) {
        notFinishMsgRef.value = '请输入不以数字作为开始的由字母/数字/下划线组成的50位以内的查询表名称'
        state.nameDisabled = true
        return
      }
      if (props.basicForm.name !== props.initialName) {
        const resp = await duplicateSearchTable(props.basicForm.name || '')
        if (!resp.meta.success) {
          notFinishMsgRef.value = '该查询表名称名已存在'
          state.nameDisabled = true
          return
        }
      }
      notFinishMsgRef.value = ''
      state.nameDisabled = false
    })
    const isFinishRef = computed(() => !state.nameDisabled)

    const validatePass = (rule: RuleObject, value: string) => {
      if (value === '') {
        return Promise.reject('请填写查询表名称')
      } else {
        if (!checkIsInstanceName(value)) {
          return Promise.reject('请输入不以数字作为开始的由字母/数字/下划线组成的50位以内的字符串')
        }
        if (value !== props.initialName) {
          return new Promise((resolve, reject) => {
            duplicateSearchTable(value || '').then((resp) => {
              if (resp.meta.success) {
                resolve('')
              } else {
                reject('该查询表名称名已存在，请检查后重新填写')
              }
            }).catch(() => {
              reject('查询表名称查重失败')
            })
          })
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
