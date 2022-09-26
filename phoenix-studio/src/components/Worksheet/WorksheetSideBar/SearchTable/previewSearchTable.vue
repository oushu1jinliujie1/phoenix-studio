<template>
  <div class="search-data-preview-drawer-content">
    <DataFilter
      v-if="!resultVisible"
      :table-index-list="tableIndexList"
      :table-columns="tableColumns"
      @confirm="handleConfirm"
      @close="handleCancel"
    />
    <ResultList
      v-if="resultVisible"
      :filter-options="filterOptions"
      :search-results="searchResults"
      @show-filters="() => { filterDrawerVisible = true }"
      @remove-filter="removeFilter"
    />
    <x-drawer
      :visible="filterDrawerVisible"
      class="v-oushudb-edit-column-drawer"
      destroyOnClose
      fixed
      title="查询条件"
      width="800"
      @close="() => { filterDrawerVisible = false }"
    >
      <div class="search-data-preview-drawer-content">
        <DataFilter
          :table-index-list="tableIndexList"
          :table-columns="tableColumns"
          :filter-options="filterOptions"
          @confirm="handleConfirm"
          @close="() => { filterDrawerVisible = false }"
        />
      </div>
    </x-drawer>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, toRefs, PropType, onMounted } from 'vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'

import DataFilter from '@/components/Worksheet/WorksheetSideBar/SearchTable/SearchData/DataFilter.vue'
import ResultList from '@/components/Worksheet/WorksheetSideBar/SearchTable/SearchData/ResultList.vue'
import { filterData, getColumnList, getSecondaryIndexList } from '@/api'
import { message } from 'ant-design-vue-3'
import { isEmpty, unionWith } from 'lodash'

export default defineComponent({
  name: 'previewSearchTable',
  props: {
    searchTable: {
      type: Object as PropType<any>,
      default: () => {
        return {}
      }
    }
  },
  components: { DataFilter, ResultList, ...smartUI },
  emits: ['close'],
  setup(props, context) {
    const state = reactive({
      resultVisible: false,
      tableIndexList: [] as any[],
      tableColumns: [] as any[],
      filterOptions: {
        secondaryIndex: undefined,
        returnColumns: [],
        limit: 100,
        searchValue: new Map() as Map<string, string>
      },
      filterDrawerVisible: false,
      searchResults: [] as any[]
    })

    /** 
     * 提交表单
     */
    const handleConfirm = async(filters: any) => {
      state.filterOptions = filters
      if (!state.filterOptions.secondaryIndex) {
        message.error('请选择二级索引')
        return
      }
      if (isEmpty(state.filterOptions.returnColumns)) {
        message.error('请至少选择一个返回列')
        return
      }
      if (!state.filterOptions.limit) {
        message.error('请选择行数限制')
        return
      }
      if (!Array.from(state.filterOptions.searchValue.values())[0]) {
        message.error('请输入第一索引列的搜索内容')
        return
      }
      console.log(filters)
      const resp = await filterData({
        secondaryIndex: state.filterOptions.secondaryIndex || '',
        returnColumns: state.filterOptions.returnColumns.map((item: any) => {
          return {
            columnName: item.option.name,
            dataType: item.option.type
          }
        }),
        limit: state.filterOptions.limit,
        searchValue: [...state.filterOptions.searchValue.entries()].reduce((obj: any, [key, value]) => {
          obj[key] = value
          return obj
        }, {})
      })
      if (resp.meta.success) {
        state.searchResults = resp.data
        state.resultVisible = true
        state.filterDrawerVisible = false
      } else {
        message.error(`查询数据失败: ${resp.meta.message}`)
      }
    }
    const handleCancel = () => {
      context.emit('close')
    }

    const removeFilter = (filterTag: any) => {
      switch (filterTag.type) {
        case 'index':
          break
        case 'column':
          state.filterOptions.returnColumns = state.filterOptions.returnColumns.filter((item: any) => item.option.key !== filterTag.value)
          break
        case 'limit':
          break
        case 'searchValue':
          state.filterOptions.searchValue.delete(filterTag.key)
          break
        default:
          //
      }
      handleConfirm(state.filterOptions)
    }

    const getSecondaryIndexOfSearchTable = async() => {
      const tableStrList = props.searchTable.tables.split(',')
      for (const str of tableStrList) {
        const resp = await getSecondaryIndexList({
          schemaName: str.split('.')[0],
          tableName: str.split('.')[1],
          offset: 0,
          limit: -1
        })
        if (resp.meta.success) {
          state.tableIndexList = [
            ...state.tableIndexList,
            ...resp.data.data.map((item: any) => {
              return {
                name: item.indexName,
                columns: item.idxAttrs,
                extra: item.includesAttrs,
                status: item.indexState
              }
            })
          ]
        } else {
          message.error(`获取${str}的二级索引失败: ${(resp.meta?.message) || '无失败提示'}`, 5)
        }
      }
    }

    const getColumnsOfSearchTable = async() => {
      const tableStrList = props.searchTable.tables.split(',')
      for (const str of tableStrList) {
        const resp = await getColumnList({
          schemaName: str.split('.')[0],
          tableName: str.split('.')[1],
          offset: 0,
          limit: -1
        })
        if (resp.meta.success) {
          state.tableColumns = unionWith(
            state.tableColumns,
            resp.data?.data ? resp.data.data.sort((a: any, b: any) => a.ORDINAL_POSITION - b.ORDINAL_POSITION).map((column: any) => {
              return {
                key: `${column.COLUMN_NAME}(${column.DATA_TYPE_NAME})`,
                name: column.COLUMN_NAME,
                type: column.DATA_TYPE_NAME,
                schema: column.TABLE_SCHEM,
                table: column.TABLE_NAME,
                column_family: column.COLUMN_FAMILY,
                order: column.ORDINAL_POSITION,
                primary: Boolean(column.KEY_SEQ)
              }
            }) : [],
            (a: any, b: any) => {
              if (a.key === b.key) {
                return true
              }
              return false
            }
          )
        } else {
          message.error(`获取${str}的列失败: ${(resp.meta?.message) || '无失败提示'}`, 5)
        }
      }
    }

    onMounted(async() => {
      await getSecondaryIndexOfSearchTable()
      await getColumnsOfSearchTable()
    })

    return {
      ...toRefs(state),

      removeFilter,
      handleConfirm,
      handleCancel
    }
  }
})

</script>

<style lang="scss">
.search-data-preview-drawer-content {
  position: absolute;
  top: 70px;
  right: 40px;
  bottom: 0;
  left: 40px;
}
</style>
