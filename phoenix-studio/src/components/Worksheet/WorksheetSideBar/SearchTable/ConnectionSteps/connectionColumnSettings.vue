<template>
  <div class="connection-column-settings">
    <div class="connection-column-settings-buttons">
      <x-tooltip :visible="columnSettings.length >= columnsLimit?undefined:false">
        <template #title>
          <div style="color: #282B2E;white-space: nowrap;">关联数已达上限</div>
        </template>
        <x-button :disabled="columnSettings.length >= columnsLimit" @click="handleAddConnectionWithDebounce">
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
      :rowKey="`schema.${tables[0]?.schema}.table.${tables[0]?.name}`"
      auto-calc-empty-height
      emptyImage="empty"
    >
      <a-table-column v-for="table of tables" :key="`schema.${table.schema}.table.${table.name}`" :title="`${table.schema}: ${table.name}`" width="120px">
        <template #default="{ record, index }">
          <x-select
            :value="record[`schema.${table.schema}.table.${table.name}`]"
            show-search
            allowClear
            data-test-id="connection-columns-select"
            placeholder="请选择关联列"
            :fieldNames="{ label: 'name', value: 'name' }"
            :options="getColumnsOption(record, table)"
            @change="(value: any) => { changeColumn(index, table, value) }"
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
import { throttle, intersectionWith, cloneDeep, isEqual, unzip } from 'lodash'
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
      for (const settingItem of props.columnSettings) {
        const obj: any = {}
        for (const column of settingItem) {
          obj[`schema.${column.schemaName}.table.${column.tableName}`] = column.columnName
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
      for (const table of tables) {
        if (table.columns) continue
        if (`schema.${table.schema}.table.${table.name}` in state.columnsRecord) {
          table.columns = state.columnsRecord[`schema.${table.schema}.table.${table.name}`]
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
          state.columnsRecord[`schema.${table.schema}.table.${table.name}`] = table.columns
        } else {
          message.error(`获取表${table.name}的列信息失败：${result.meta.message}`)
        }
      }
    })

    const columnsLimit = computed(() => {
      let res = 0
      for (const each of props.tables) {
        if (res === 0) res = each.columns?.length || 0
        else res = res > each.columns?.length ? each.columns.length : res
      }
      return res
    })
    const getColumnsOption = computed(() => {
      return (row: any, table: any) => {
        let res = table.columns
        if (!res) return res
        for (const rowKey in row) {
          if (!row[rowKey]) continue
          if (`schema.${table.schema}.table.${table.name}` === rowKey) continue
          const keyTable = props.tables.find((tb: any) => `schema.${tb.schema}.table.${tb.name}` === rowKey)
          if (!keyTable) break
          const columnType = keyTable.columns?.find((col: any) => col.name === row[rowKey])?.type
          if (!columnType) break
          res = table.columns?.filter((column: any) => column.type === columnType)
          break
        }
        const settings = unzip(props.columnSettings).find(
          (items: any) => items[0]?.schemaName === table.schema && items[0]?.tableName === table.name
        )?.filter(
          (item: any) => item.columnName !== row[`schema.${table.schema}.table.${table.name}`]
        )
        if (!settings) return res
        res = res.filter((column: any) => settings.findIndex((item: any) => item.columnName === column.name) === -1)
        return res
      }
    })

    const changeColumn = (index: number, table: any, value: any) => {
      const settings = cloneDeep(props.columnSettings)
      const columnSelect = settings[index].find((column: any) => column.tableName === table.name && column.schemaName === table.schema)
      Object.assign(columnSelect, { columnName: value })
      context.emit('update:columnSettings', settings)
    }

    const handleAddConnectionWithDebounce = throttle(() => {
      const newConnection = props.tables.map((table: any) => ({ tableName: table.name, schemaName: table.schema, columnName: null }))
      context.emit('update:columnSettings', [...props.columnSettings, newConnection])
    }, 500)

    const handelShowConnectionTableSettings = () => {
      context.emit('showConnectionTableSettings')
    }

    const deleteConnection = (row: any) => {
      const connectionItem: any = []
      for (const rowKey in row) {
        const [schemaName, tableName] = rowKey.replace('schema.', '').split('.table.')
        connectionItem.push({
          schemaName,
          tableName,
          columnName: row[rowKey],
        })
      }
      const settings = props.columnSettings.filter((item: any) => !isEqual(item, connectionItem))
      context.emit('update:columnSettings', settings)
    }

    return {
      notFinishMsgRef,
      isFinishRef,

      columnsTableData,
      columnsLimit,
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
