<template>
  <div class="connection-column-settings">
    <x-button @click="handleAddConnectionWithDebounce">
      <icon name="add_circle"/>
      新建关联
    </x-button>
    <x-table
      :data-source="columnSettings"
      :pagination="false"
      :scroll="{ x: 920, y: null }"
      rowKey="name"
      auto-calc-empty-height
    >
      <a-table-column v-for="table of tables" :key="table.name" :title="table.name" width="120px">
        <template #default="{ record }">
          <x-select
            v-model:value="record[table.name]"
            show-search
            data-test-id="connection-columns-select"
            placeholder="请选择关联列"
            :fieldNames="{ label: 'name', value: 'name' }"
            :options="table.columns"
            style="width: 100%;">
            <template #option="{ name }">
              <icon name="worksheet/column" style="margin-right: 5px;"/>{{ name }}
            </template>
            <template #prefixIcon>
              <icon
                image
                name="worksheet/column_two_color"
              />
            </template>

          </x-select>
        </template>
      </a-table-column>
      <a-table-column key="action" title="操作" align="center" width="40px">
        <template #default="{ record }">
          <x-tooltip placement="topLeft" title="删除">
            <icon style="cursor: pointer;" color="primary" name="minus_circle" @click="deleteConnection(record)"/>
          </x-tooltip>
        </template>
      </a-table-column>
    </x-table>
  </div>
</template>

<script lang="ts">
/* eslint-disable vue/no-side-effects-in-computed-properties */
import { defineComponent, ref, computed, reactive, toRefs, PropType } from 'vue'
import Icon from '@/components/Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { message } from 'ant-design-vue-3'
import { RuleObject } from 'ant-design-vue/es/form/interface'
import { throttle } from 'lodash'

export default defineComponent({
  name: 'connectionColumnSettings',
  props: {
    tables: {
      type: Array as PropType<any[]>,
      required: true
    },
    columnSettings: {
      type: Array as PropType<any[]>,
      required: true
    }
  },
  components: { Icon, ...smartUI },
  emits: ['update:columnSettings'],
  setup(props, context) {
    const notFinishMsgRef = ref('')
    const isFinishRef = computed(() => {
      if (props.columnSettings.length === 0) {
        notFinishMsgRef.value = '请添加关联信息'
        return false
      } else {
        for (const each of props.columnSettings) {
          for (const key in each) {
            if (!each[key]) {
              notFinishMsgRef.value = '请确认每条关联中所有表都已选择关联列'
              return false
            }
          }
        }
        notFinishMsgRef.value = ''
        return true
      }
    })

    const handleAddConnectionWithDebounce = throttle(() => {
      const item: any = {}
      props.tables.forEach((table: any) => {
        item[table.name] = ''
      })
      context.emit('update:columnSettings', [...props.columnSettings, item])
      console.log('add connection')
    }, 500)

    const deleteConnection = (row: any) => {
      const settings = props.columnSettings.filter((item: any) => item !== row)
      context.emit('update:columnSettings', settings)
      console.log('delete connection')
    }

    return {
      notFinishMsgRef,
      isFinishRef,

      handleAddConnectionWithDebounce,
      deleteConnection
    }
  }
})

</script>

<style lang="scss">
.connection-column-settings {
  height: 100%;

}
</style>
