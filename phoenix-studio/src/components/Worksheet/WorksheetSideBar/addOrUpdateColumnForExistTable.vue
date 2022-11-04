<template>
  <x-drawer
    v-model:visible="localVisibleRef"
    :title="`${isAdd ? '新增' : '查看'}列`"
    fixed
    width="800"
  >
    <add-or-update-column-base
      :execLoading="execLoadingRef"
      :initial-column="initColumnRef"
      :is-add="isAdd"
      :schemaName="schemaName"
      :table="table"
      is-update-table
      @close="() => localVisibleRef = false"
      @confirm="handleSubmit"
    />
  </x-drawer>
</template>
<script lang="ts">
import { computed, defineComponent, PropType, ref, watch } from 'vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { isProduction, useModel } from '@/smart-ui-vue/utils'
import AddOrUpdateColumnBase from '@/components/Worksheet/WorksheetSideBar/addOrUpdateColumnBase.vue'
import { message } from 'ant-design-vue-3'
import { getDefaultColumn } from '@/components/Worksheet/WorksheetSideBar/addOrUpdateTable.vue'
import { ColumnResData, Table } from '@/components/Worksheet/type'
import { createColumn, executeSql, getSqlForCreateColumn } from '@/api'
import { TYPE_WITH_PRECISION, TYPE_WITH_SCALE } from './constant'

export default defineComponent({
  name: 'addOrUpdateColumnForExistTable',
  props: {
    schemaName: {
      type: String,
      required: true,
    },
    // drawer visible
    visible: {
      type: Boolean,
      default: false,
    },
    table: {
      type: Object as PropType<Table | undefined>,
      required: true,
    },
    initialColumn: {
      type: Object as PropType<ColumnResData>,
    },
    isAdd: {
      type: Boolean,
      default: false,
    },
    'onUpdate:visible': {
      type: Function,
    },
    'onSuccess': {
      type: Function
    }
  },
  components: { AddOrUpdateColumnBase, ...smartUI },
  emits: ['success', 'confirm', 'update:value'],
  setup(props, context) {

    const localVisibleRef = (typeof props.visible === 'boolean' && typeof props['onUpdate:visible'] === 'function')
      ? useModel('visible', props, context)
      : ref(false)

    const initColumnRef = ref({})
    const execLoadingRef = ref(false)

    const handleSubmit = async(column: any) => {
      execLoadingRef.value = true
      await handleAddSubmit(column)
      execLoadingRef.value = false
    }

    const handleAddSubmit = async(column: any) => {
      if (props.table === undefined) {
        if (!isProduction)
          console.error('编辑列时，表信息未传入，请检查')
        return
      }
      
      try {
        
        const sqlRes = await createColumn({
          schemaName: props.schemaName,
          tableName: props.table.name,
          columnName: column.name,
          dataType: column.type,
          pk: column.isPrimary,
          scale: TYPE_WITH_SCALE.indexOf(column.type) !== -1 ? column.scale : 0,
          precision: TYPE_WITH_PRECISION.indexOf(column.type) !== -1 ? column.precision : 0,
          familyName: column.columnFamily,
          comment: column.comment
        })

        if (sqlRes.meta.success) {
          message.success(`${props.isAdd ? '创建' : '修改'}字段成功`)
          localVisibleRef.value = false
          context.emit('success')
        } else {
          message.error(`${props.isAdd ? '创建' : '修改'}字段失败: ${(sqlRes.meta?.message) || '无失败提示'}`, 5)
        }
      } catch (e) {
        message.error(`${props.isAdd ? '创建' : '修改'}字段失败: ${e}`, 5)
      }
    }

    watch(localVisibleRef, () => {
      if (localVisibleRef.value === true) {
        initColumnRef.value = (() => {
          if (props.isAdd) {
            return getDefaultColumn()
          } else {
            if (props.initialColumn === undefined) {
              // eslint-disable-next-line no-console
              console.warn('字段数据未成功传入')
              return {}
            }

            return {
              name: props.initialColumn.name,
              type: props.initialColumn.type,
              columnFamily: props.initialColumn.column_family,
              comment: props.initialColumn.comment,
              isPrimary: props.initialColumn.primary,
              scale: props.initialColumn.scale,
              precision: props.initialColumn.precision,
            }
          }
        })()
      }
    },
    {
      immediate: true,
    },
    )

    return {
      localVisibleRef,
      initColumnRef,
      execLoadingRef,

      handleSubmit,
    }
  },
})
</script>
<style lang="scss">
</style>