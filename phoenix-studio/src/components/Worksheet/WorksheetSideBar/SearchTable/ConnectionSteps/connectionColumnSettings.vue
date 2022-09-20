<template>
  <div class="connection-column-settings">
    <div class="connection-column-settings-buttons">
      <x-tooltip :visible="columnSettings.length >= columnsJoined.length?undefined:false">
        <template #title>
          <div style="color: #282B2E;white-space: nowrap;">关联数已达上限</div>
        </template>
        <x-button :disabled="columnSettings.length >= columnsJoined.length" @click="handleAddConnectionWithDebounce">
          <icon name="add_circle"/>
          新建关联
        </x-button>
      </x-tooltip>
      <x-button v-if="tableSettingsButton" @click="handelShowConnectionTableSettings">
        <icon name="worksheet/settings"/>
        设置关联表
      </x-button>
    </div>

    <x-table
      :data-source="columnsTableData"
      :pagination="false"
      :scroll="{ x: 920, y: null }"
      :rowKey="tables[0]?.name || ''"
      auto-calc-empty-height
      emptyImage="empty"
    >

      <a-table-column :key="tables[0]?.name || ''" :title="tables[0]?.name || ''" width="120px">
        <template #default="{ record, index }">
          <x-select
            :value="record[tables[0]?.name]"
            show-search
            data-test-id="connection-columns-select"
            placeholder="请选择关联列"
            :fieldNames="{ label: 'name', value: 'name' }"
            :options="getColumnsOption(record[tables[0]?.name])"
            @change="(value: any) => { changeColumn(index, value) }"
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
      <a-table-column v-for="table of tables.slice(1)" :key="table.name" :title="table.name" width="120px">
        <template #default="{ record }">
          <div>{{ record[table.name] }}</div>
        </template>
      </a-table-column>
      <a-table-column key="action" title="操作" align="center" width="40px">
        <template #default="{ record }">
          <x-tooltip placement="topLeft" title="删除">
            <icon style="cursor: pointer;" color="primary" name="minus_circle" @click="deleteConnection(record)"/>
          </x-tooltip>
        </template>
      </a-table-column>
      <template #emptyDescription>
        暂无关联
      </template>
    </x-table>
  </div>
</template>

<script lang="ts">
/* eslint-disable vue/no-side-effects-in-computed-properties */
import { defineComponent, ref, computed, reactive, toRefs, PropType, watch } from 'vue'
import Icon from '@/components/Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { message } from 'ant-design-vue-3'
import { throttle, intersectionWith } from 'lodash'
import { getColumnList } from '@/api'

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
    },
    tableSettingsButton: Boolean
  },
  components: { Icon, ...smartUI },
  emits: ['update:columnSettings', 'showConnectionTableSettings'],
  setup(props, context) {
    const notFinishMsgRef = ref('')
    const isFinishRef = computed(() => {
      if (props.columnSettings.length === 0) {
        notFinishMsgRef.value = '请添加关联信息'
        return false
      } else {
        for (const each of props.columnSettings) {
          if (!each.name) {
            notFinishMsgRef.value = '请确认每条关联都已选择关联列'
            return false
          }
        }
        notFinishMsgRef.value = ''
        return true
      }
    })

    const columnsTableData = computed(() => {
      const res: any[] = []
      for (const each of props.columnSettings) {
        const obj: any = {}
        for (const table of props.tables) {
          obj[table.name] = each.name
        }
        res.push(obj)
      }
      return res
    })

    const state = reactive({
      columnsRecord: {} as any
    })

    const tablesComputed = computed(() => {
      return props.tables
    })

    watch(tablesComputed, async(tables) => {
      for (const index in tables) {
        const table = tables[index]
        if (table.columns) continue
        if (`schema_${table.schema}_table_${table.name}` in state.columnsRecord) {
          table.columns = state.columnsRecord[`schema_${table.schema}_table_${table.name}`]
          continue
        }
        const result = await getColumnList({
          schemaName: table.schema,
          tableName: table.name,
          offset: 0,
          limit: -1
        })

        if (result.meta.success) {
          table.columns = result.data?.data ? result.data.data.sort((a: any, b: any) => a.ORDINAL_POSITION - b.ORDINAL_POSITION).map((column: any) => {
            return {
              name: column.COLUMN_NAME,
              schema: column.TABLE_SCHEM,
              table: column.TABLE_NAME,
              column_family: column.COLUMN_FAMILY,
              type: column.DATA_TYPE_NAME,
              order: column.ORDINAL_POSITION,
              primary: Boolean(column.KEY_SEQ)
            }
          }) : []
          state.columnsRecord[`schema_${table.schema}_table_${table.name}`] = table.columns
        } else {
          message.error(`获取表${table.name}的列信息失败：${result.meta.message}`)
        }
      }
    })

    const columnsJoined = computed(() => {
      let res: any[] = []
      const values: any[] = props.tables.map((table: any) => table.columns) || []
      if (values.length > 1) {
        res = intersectionWith(...values, (a: any, b: any) => {
          if (a.name === b.name && a.type === b.type) {
            return true
          }
          return false
        })
      } else {
        res = values[0] || []
      }
      return res
    })
    const getColumnsOption = (record: any) => {
      const res = columnsJoined.value.filter((column: any) => {
        if (props.columnSettings.find((setting: any) => setting.name === column.name && record !== column.name)) return false
        return true
      })
      return res
    }

    const changeColumn = (index: number, value: any) => {
      const settings = JSON.parse(JSON.stringify(props.columnSettings))
      settings[index] = columnsJoined.value.find((column: any) => column.name === value)
      context.emit('update:columnSettings', settings)
    }

    const handleAddConnectionWithDebounce = throttle(() => {
      context.emit('update:columnSettings', [...props.columnSettings, { name: '', type: '' }])
    }, 500)

    const handelShowConnectionTableSettings = () => {
      context.emit('showConnectionTableSettings')
    }

    const deleteConnection = (row: any) => {
      const settings = props.columnSettings.filter((item: any) => item.name !== row[props.tables[0]?.name])
      context.emit('update:columnSettings', settings)
    }

    return {
      notFinishMsgRef,
      isFinishRef,

      columnsTableData,
      columnsJoined,
      changeColumn,
      getColumnsOption,

      handelShowConnectionTableSettings,
      handleAddConnectionWithDebounce,
      deleteConnection
    }
  }
})

</script>

<style lang="scss">
.connection-column-settings {
  height: 100%;

  .connection-column-settings-buttons {
    display: flex;
    gap: 10px;
  }
}
</style>
