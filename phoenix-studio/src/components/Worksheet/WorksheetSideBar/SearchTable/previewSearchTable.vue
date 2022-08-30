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

export default defineComponent({
  name: 'previewSearchTable',
  props: {
    schemaName: {
      type: String,
      default: ''
    },
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
        limit: undefined,
        searchValue: {}
      } as any,
      filterDrawerVisible: false
    })

    /** 
     * 提交表单
     */
    const handleConfirm = (filters: any) => {
      console.log(filters)
      state.filterOptions = filters
      state.resultVisible = true
      state.filterDrawerVisible = false
    }
    const handleCancel = () => {
      context.emit('close')
    }

    const removeFilter = (filterTag: any) => {
      switch (filterTag.type) {
        case 'index':
          state.filterOptions.secondaryIndex = undefined
          state.filterOptions.searchValue = {}
          break
        case 'column':
          state.filterOptions.returnColumns = state.filterOptions.returnColumns.filter((item: any) => item !== filterTag.value)
          break
        case 'limit':
          state.filterOptions.limit = undefined
          break
        case 'searchValue':
          delete state.filterOptions.searchValue[filterTag.key]
          break
        default:
          //
      }
      
    }

    onMounted(() => {
      state.tableIndexList = [
        { name: 'index1', columns: ['c1', 'c2'] },
        { name: 'index2', columns: ['c1', 'c2'] },
        { name: 'index3', columns: ['c1', 'c2'] },
        { name: 'index4', columns: ['c1', 'c2'] },
        { name: 'index5', columns: ['c1', 'c2'] },
        { name: 'index6', columns: ['c1', 'c2'] },
      ]
      state.tableColumns = [
        { name: 'column1' },
        { name: 'column2' },
        { name: 'column3' },
        { name: 'column4' },
        { name: 'column5' },
        { name: 'column6' },
        { name: 'column7' },
        { name: 'column8' }
      ]
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
