<template>
  <x-drawer
    v-model:visible="localVisibleRef"
    :title="`${isAdd ? '新增' : '编辑'}列`"
    fixed
    width="800"
  >
    <add-or-update-column-base
      :execLoading="execLoadingRef"
      :init-already-exist-name-list="initAlreadyExistNameList"
      :initial-column="initColumnRef"
      :is-add="isAdd"
      is-update-table
      :storage-format="table?.table_type"
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
import { useStore } from 'vuex'
import { useI18n } from 'vue-i18n'
import { translateErrorMessage } from 'lava-fe-lib/lib-common/i18n'
import { message } from 'ant-design-vue-3'
import { getDefaultColumn } from '@/components/Worksheet/WorksheetSideBar/addOrUpdateTable.vue'
import { ColumnResData, Table } from '@/components/Worksheet/type'

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
    initAlreadyExistNameList: {
      type: Array,
      required: true,
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
    const { t } = useI18n()
    const getError = translateErrorMessage(t)
    const store = useStore()

    const localVisibleRef = (typeof props.visible === 'boolean' && typeof props['onUpdate:visible'] === 'function')
      ? useModel('visible', props, context)
      : ref(false)

    const initColumnRef = ref({})
    const execLoadingRef = ref(false)

    const handleSubmit = async(column: any) => {
      execLoadingRef.value = true
      await (props.isAdd ? handleAddSubmit(column) : handleUpdateSubmit(column))
      execLoadingRef.value = false
    }

    const handleAddSubmit = (column: any) => {
      if (props.table === undefined) {
        if (!isProduction)
          console.error('编辑列时，表信息未传入，请检查')
        return
      }
      console.log('addSubmit')
      
      // try {
      //   const sqlRes = await getSQLForCreateColumn(worksheetInfoRef.value.instanceId, props.databaseName, {
      //     schema: props.schemaName,
      //     table: props.table.name,
      //     name: column.name,
      //     type: column.type,
      //     length: column.length,
      //     scale: column.scale,
      //     comment: column.comment,
      //     notnull: column.isNotNull,
      //     defaultValue: column.defaultValue,
      //     table_external: props.table.external,
      //   })

      //   if (sqlRes.meta.success) {
      //     const executeResult = await executeSql(
      //       {
      //         instanceId: worksheetInfoRef.value.instanceId,
      //         databaseName: props.databaseName,
      //         schema: props.schemaName,
      //         statement: sqlRes.data,
      //       })

      //     if (executeResult.meta.success && executeResult.data?.error === '') {
      //       message.success(`${props.isAdd ? '创建' : '修改'}字段成功`)
      //       localVisibleRef.value = false
      //       context.emit('success')
      //     } else {
      //       message.error(`${props.isAdd ? '创建' : '修改'}字段失败: ${ (executeResult.data?.error) || getError(executeResult) || '无失败提示'}`, 5)
      //     }
      //   } else {
      //     message.error(`${props.isAdd ? '创建' : '修改'}字段失败: ${getError(sqlRes)}`, 5)
      //   }
      // } catch (e) {
      //   message.error(`${props.isAdd ? '创建' : '修改'}字段失败: ${e}`, 5)
      // }
    }

    const handleUpdateSubmit = (column: any) => {
      if (props.table === undefined) {
        if (!isProduction)
          console.error('编辑列时，表信息未传入，请检查')
        return
      }
      if (props.initialColumn === undefined) {
        message.warning('字段初始数据丢失，请检查')
        return
      }
      console.log('updateSubmit')

      // try {
      //   const sqlRes = await getSQLForUpdateColumn(worksheetInfoRef.value.instanceId, props.databaseName, {
      //     schema: props.schemaName,
      //     table: props.table.name,
      //     name: column.name,
      //     type: column.type,
      //     length: column.length,
      //     scale: column.scale,
      //     comment: column.comment,
      //     notnull: column.isNotNull,
      //     // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      //     att_rel_id: props.initialColumn.att_rel_id!,
      //     // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      //     att_num: props.initialColumn.att_num!,
      //     defaultValue: column.defaultValue,
      //     table_external: props.table.external,
      //   })

      //   if (sqlRes.meta.success) {
      //     const executeResult = await executeSql(
      //       {
      //         instanceId: worksheetInfoRef.value.instanceId,
      //         databaseName: props.databaseName,
      //         schema: props.schemaName,
      //         statement: sqlRes.data,
      //       })

      //     if (executeResult.meta.success && executeResult.data?.error === '') {
      //       message.success(`${props.isAdd ? '修改' : '修改'}字段成功`)
      //       localVisibleRef.value = false
      //       context.emit('success')
      //     } else {
      //       message.error(`${props.isAdd ? '修改' : '修改'}字段失败: ${executeResult.data?.error || getError(executeResult) || '无失败提示'}`, 5)
      //     }
      //   } else {
      //     message.error(`${props.isAdd ? '修改' : '修改'}字段失败: ${getError(sqlRes)}`, 5)
      //   }
      // } catch (e) {
      //   message.error(`${props.isAdd ? '修改' : '修改'}字段失败: ${e}`, 5)
      // }
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
              length: props.initialColumn.length,
              scale: props.initialColumn.scale,
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