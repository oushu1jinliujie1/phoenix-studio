<template>
  <x-drawer
    v-model:visible="visibleLocal"
    title="关联查询表"
    fixed
    width="800"
  >
    <div class="v-oushudb-worksheet-table-detail-table-struct">
      <x-spin :spinning="getTableConnectionsLoading" tip="数据加载中">
        <x-table
          :dataSource="tableData"
          :columns="columns"
          :pagination="false"
          auto-calc-empty-height
          divider
          emptyImage="empty"
        >
          <!-- <template #action="{ record }">
            <x-tooltip placement="topLeft" title="移除">
              <icon style="cursor: pointer;" color="primary" name="minus_circle" @click="deleteConnection(record)"/>
            </x-tooltip>
          </template> -->
          <template #emptyDescription>
            暂无关联查询表
          </template>
        </x-table>
      </x-spin>
    </div>
  </x-drawer>
</template>

<script lang="ts">
import { defineComponent, PropType, reactive, ref, toRefs, watch } from 'vue'
// import Icon from '@/components/Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { useModel } from '@/smart-ui-vue/utils'
import { message } from 'ant-design-vue-3'
import { getConnectionList } from '@/api'
import { Table } from '@/components/Worksheet/type'

export default defineComponent({
  name: 'tableConnectionInfo',
  props: {
    // 表数据
    table: {
      type: Object as PropType<Table>,
      default: () => ({})
    },
    // drawer visible
    visible: {
      type: Boolean,
      default: false,
    },
    schema: {
      type: String,
      default: ''
    }
  },
  components: { ...smartUI },
  emits: ['success', 'confirm', 'update:value'],
  setup(props, context) {
    const state = reactive({
      getTableConnectionsLoading: false
    })
    // 表格数据
    const tableData: any = ref([])

    const columns = [
      {
        title: '查询表名',
        dataIndex: 'name',
        width: '200px',
        key: 'name',
      },
      {
        title: '关联表',
        dataIndex: 'tables',
        width: '200px',
        key: 'tables',
      },
      {
        title: '关联列',
        dataIndex: 'columns',
        width: '200px',
        key: 'columns',
      },
      {
        title: '关联列数',
        dataIndex: 'count',
        width: '100px',
        key: 'count',
      },
      // {
      //   title: '操作',
      //   key: 'action',
      //   align: 'center',
      //   width: '60px',
      //   slots: {
      //     customRender: 'action'
      //   }
      // },
    ]

    // const deleteConnection = (record: any) => {
    //   console.log('del')
    // }

    const handleGetConnectionList = async() => {
      const resp = await getConnectionList({
        schemaName: props.schema,
        tableName: props.table.name
      })
      if (resp.meta.success) {
        tableData.value = resp.data.map((item: any) => {
          return {
            name: item.QUERYNAME,
            tables: item.TABLENAMES,
            columns: item.CONNECTION,
            count: item.CONNECTION?.split(',').length || 0,
          }
        })
      } else {
        message.error(`获取关联信息失败: ${(resp.meta?.message) || '无失败提示'}`, 5)
      }
    }

    const visibleLocal = useModel('visible', props, context)

    watch(visibleLocal, (visible) => {
      if (visible) {
        handleGetConnectionList()
      }
    })

    return {
      ...toRefs(state),
      visibleLocal,
      tableData,
      columns,

      // deleteConnection
    }
  }
})
</script>

<style lang="scss">

</style>
