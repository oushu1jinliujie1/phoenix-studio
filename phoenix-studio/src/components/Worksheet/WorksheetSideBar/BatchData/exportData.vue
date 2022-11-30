<template>
  <x-spin :spinning="spinLoading" tip="正在导出">
    <div class="download-tables-container">
      <div class="download-tables-search-container">
        <x-input-search
          v-model:value="searchValue"
          allow-clear
          class="raw"
          data-test-id="download-tables-search-input"
          placeholder="请输入表名，按回车搜索"
          @search="handleTableOnSearch"
          @keyup.enter="handleTableOnSearch"
        />
      </div>
      <x-table
        :columns="exportFromBasic ? exportColumns : exportSearchColumns"
        :data-source="tableList"
        :loading="tableLoading"
        :customPageSize="true"
        :pagination="{
          pageSize: pageSize,
          showQuickJumper:true,
          showSizeChanger:true,
          total: total,
        }"
        :row-selection="{ columnWidth: 30, selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        divider
        rowKey="name"
        style="margin-top: 10px;"
        @change="onTableChange"
      >
        <template #name="{ record }">
          <div>{{ record.name }}</div>
        </template>
        <template #primary_columns="{ record }">
          <div class="download-tables-primary-columns">
            <x-tag
              v-for="column of record.primary_columns"
              :key="`${column.name}(${column.type})`"
              color-type="gray"
            >
              {{ column.name }}({{ column.type }})
            </x-tag>
          </div>
        </template>
      </x-table>
    </div>
    <!-- 提交、取消按钮组 -->
    <div class="download-table-btn-container">
      <x-button :disabled="!tableList.length || spinLoading" type="primary" @click="handleSubmit">
        <icon name="worksheet/submit"/>
        提交
      </x-button>
      <x-button style="margin-left: 10px;" @click="handleCancel">
        <icon color="inherit" name="worksheet/cancel"/>
        取消
      </x-button>
    </div>
  </x-spin>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, onUnmounted, PropType, reactive, ref, toRefs, watch } from 'vue'
import XSpin from '@/smart-ui-vue/XSpin.vue'
import XButton from '@/smart-ui-vue/XButton.vue'
import XTable from '@/smart-ui-vue/XTable.vue'
import XTag from '@/smart-ui-vue/XTag.vue'
import XInputSearch from '@/smart-ui-vue/XInputSearch.vue'
import Icon from '@/smart-ui-vue/helper/Icon.vue'
import { exportSearchTable, exportTable, getSearchTableList, getTableList } from '@/api'
import { message } from 'ant-design-vue-3'
import { resolveExportExcel } from '@/lib/common'

export default defineComponent({
  name: 'ExportData',
  components: {
    XSpin,
    XButton,
    XTable,
    XTag,
    XInputSearch,
    Icon,
  },
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
  setup(props: any, context: any) {
    const exportColumns = [
      {
        title: '表名',
        width: '200px',
        dataIndex: 'name',
        key: 'name',
        slots: { customRender: 'name' },
      },
      {
        title: '主键列',
        width: '440px',
        dataIndex: 'primary_columns',
        key: 'primary_columns',
        slots: { customRender: 'primary_columns' },
      },
    ]
    const exportSearchColumns = [
      {
        title: '查询表名',
        dataIndex: 'name',
        width: '200px',
        key: 'name',
      },
      {
        title: '关联表',
        dataIndex: 'tables',
        width: '440px',
        key: 'tables',
      },
    ]
    const state = reactive({
      spinLoading: false,
      tableLoading: false,
      searchValue: '',
      currentPage: 1,
      pageSize: 20,
      total: 0,
      tableList: [] as any[],
      selectedRowKeys: [] as any[]
    })
    const handleSubmit = async() => {
      if (props.exportFromBasic && !props.schemaName) return
      state.spinLoading = true
      try {
        const exportFunc = props.exportFromBasic ? exportTable : exportSearchTable
        const resp = await exportFunc(props.schemaName, state.selectedRowKeys)
        if (!resp.data) {
          message.error('导出失败')
          return
        }
        const title = decodeURI(resp.headers['content-disposition'].replace('attachment;filename*=utf-8\'\'', ''))
        resolveExportExcel(title, resp.data)
        message.success('开始导出')
      } catch (e: any) {
        message.error('导出失败')
      }

      state.spinLoading = false
      context.emit('close')
    }

    const handleCancel = () => {
      context.emit('close')
    }

    const handleInitTables = async() => {
      state.tableLoading = true
      if (props.exportFromBasic) {
        const result = await getTableList({
          schemaName: props.schemaName,
          tableName: state.searchValue || '',
          offset: (state.currentPage - 1) * state.pageSize,
          limit: state.pageSize,
        })

        if (result.meta.success) {
          state.total = result.data.count
          state.tableList = (result.data.data ?? []).map((table: any) => ({
            name: table.TABLE_NAME,
            schema: props.schemaName,
            primary_columns: table.PK_COLUMNS.sort((a: any, b: any) => a.ORDINAL_POSITION - b.ORDINAL_POSITION).map((column: any) => {
              return {
                name: column.COLUMN_NAME,
                comment: column.COMMENT,
                type: column.DATA_TYPE_NAME,
                schema: column.TABLE_SCHEM,
                table: column.TABLE_NAME,
                column_family: column.COLUMN_FAMILY,
                order: column.ORDINAL_POSITION,
                primary: true
              }
            })
          }))
        } else {
          message.error(`获取表失败：${result.meta.message}`)
        }
      } else {
        const result = await getSearchTableList({
          queryName: state.searchValue || '',
          offset: (state.currentPage - 1) * state.pageSize,
          limit: state.pageSize,
        })
        if (result.meta.success) {
          // 全部数据
          state.tableList = result.data ? result.data.data.map((item: any) => {
            return {
              name: item.QUERYNAME,
              comment: item.CHINESENAME,
              description: item.DESCRIPTION,
              tables: item.TABLENAMES,
              connections: item.CONNECTIONS
            }
          }) : []
          state.total = result.data.count
        } else {
          message.error(`获取查询表失败：${result.meta.message}`)
        }
      }
      state.tableLoading = false
    }

    const onTableChange = async(pagination: any) => {
      state.currentPage = pagination.current
      state.pageSize = pagination.pageSize
      await handleInitTables()
    }

    const handleTableOnSearch = async() => {
      state.currentPage = 1
      await handleInitTables()
    }

    const onSelectChange = (rowKeys: any[]) => {
      state.selectedRowKeys = rowKeys
    }

    onMounted(() => {
      handleInitTables()
    })
    return {
      exportColumns,
      exportSearchColumns,
      ...toRefs(state),
      handleSubmit,
      handleCancel,
      onTableChange,
      handleTableOnSearch,
      onSelectChange,
    }
  }
})
</script>

<style lang="scss">
.antv-drawer-body {
  height: calc(100% - 74px);
  .antv-spin-nested-loading {
    height: 100%;
    .antv-spin-container {
      height: 100%;
      position: relative;
      .download-tables-container {
        height: calc(100% - 162px);
        .download-tables-search-container {
          width: 200px;
        }
        .download-tables-primary-columns {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
          .x-tag:not(.raw).antv-tag.x-tag-gray {
            margin: 0;
            color: #85888c;
            background-color: #e5e5e5;
            border-color: #e5e5e5;
          }
        }
      }
      .download-table-btn-container {
        position: absolute;
        right: 0;
        bottom: 0;
        z-index: 1;
        width: 100%;
        padding: 20px 0;
      }
    }
  }
}
</style>