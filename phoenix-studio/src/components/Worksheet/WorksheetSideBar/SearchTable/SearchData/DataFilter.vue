<template>
  <div class="search-data-filter-container">
    <div class="reset-data-filter-button">
      <x-button
        :disabled="resetDisabled"
        @click="resetFilters()"
      >
        <icon name="worksheet/cancel"/>重置查询
      </x-button>
    </div>
    <div class="search-data-filter-form">
      <div class="search-data-filter-form-item">
        <div class="search-data-filter-form-label">查询方式</div>
        <div class="search-data-filter-form-value">
          <x-radio-group
            v-model:value="searchMode"
          >
            <x-radio value="secondaryIndex">二级索引</x-radio>
            <x-radio value="primaryKey">主键列</x-radio>
          </x-radio-group>
        </div>
      </div>
      <div v-if="searchMode === 'secondaryIndex'" class="search-data-filter-form-item">
        <div class="search-data-filter-form-label">二级索引</div>
        <div class="search-data-filter-form-value">
          <x-select
            class="search-data-filter-form-select"
            show-search
            allow-clear
            data-test-id="search-data-filter-form-index-select"
            placeholder="请选择二级索引"
            :options="tableIndexList"
            :fieldNames="{ label: 'name', value: 'name' }"
            v-model:value="secondaryIndex"
          ></x-select>
        </div>
      </div>
      <div class="search-data-filter-form-item">
        <div class="search-data-filter-form-label">返回列(多选)</div>
        <div class="search-data-filter-form-value">
          <x-select
            class="search-data-filter-form-select"
            mode="multiple"
            show-search
            allow-clear
            label-in-value
            placeholder="请选择返回列"
            :options="tableColumns"
            :fieldNames="{ label: 'key', value: 'key' }"
            v-model:value="returnColumns"
          ></x-select>
        </div>
      </div>
      <div class="search-data-filter-form-item">
        <div class="search-data-filter-form-label">行数限制</div>
        <div class="search-data-filter-form-value">
          <x-select
            class="search-data-filter-form-select-short"
            allow-clear
            placeholder="请选择行数限制"
            :options="limitList"
            v-model:value="limit"
          ></x-select>
        </div>
      </div>
      <div class="search-data-filter-form-item">
        <div class="search-data-filter-form-label" style="margin-bottom: 5px;">搜索内容筛选</div>
        <div class="search-data-filter-form-value">
          <div v-if="searchMode === 'secondaryIndex' && !secondaryIndex" class="search-data-filter-form-value-item">
            <div class="search-data-filter-form-value-label" style="width: auto;padding-left: 10px;color: #336CFF;">请选择二级索引以确定筛选条件的列</div>
          </div>
          <div
            class="search-data-filter-form-value-item"
            v-for="column of (filterColumns)"
            :key="(searchMode === 'secondaryIndex' ? secondaryIndex : 'primary') + column">
            <div class="search-data-filter-form-value-label">{{ column }}</div>
            <x-input-search
              :id="'searchValue_' + (searchMode === 'secondaryIndex' ? secondaryIndex : 'primary') + column"
              allow-clear
              class="raw search-data-filter-form-input-short"
              data-test-id="search-data-filter-form-search-input"
              placeholder="请输入您要寻找的内容"
              :value="searchValue.get(column)"
              @change="(e: any) => { changeSearchValue(column, e)}"
            />
          </div>
        </div>
      </div>
    </div>
    <div class="search-data-filter-form-btn-container">
      <x-button
        :loading="execLoading"
        type="primary"
        @click="handleConfirm"
      >
        <icon name="worksheet/submit"/>
        确认
      </x-button>
      <x-button @click="handleCancel">
        <icon name="worksheet/cancel"/>
        取消
      </x-button>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, toRefs, PropType, watch, onMounted, computed } from 'vue'
import Icon from '@/components/Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { forIn, isEmpty, times } from 'lodash'
 
export default defineComponent({
  name: 'DataFilter',
  props: {
    tableIndexList: {
      type: Array as PropType<any[]>,
      default: () => {
        return []
      }
    },
    primaryColumns: {
      type: Array as PropType<any[]>,
      default: () => {
        return []
      }
    },
    tableColumns: {
      type: Array as PropType<any[]>,
      default: () => {
        return []
      }
    },
    filterOptions: {
      type: Object as PropType<any>,
      default: () => {
        return {
          searchMode: 'secondaryIndex',
          secondaryIndex: undefined,
          returnColumns: [],
          limit: undefined,
          searchValue: new Map() as Map<string, string>,
          searchTableName: undefined as string | undefined,
          schemaName: undefined as string | undefined,
          tableName: undefined as string | undefined
        }
      }
    },
    execLoading: Boolean
  },
  components: { Icon, ...smartUI },
  emits: ['confirm', 'close'],
  setup(props, context) {
    const state = reactive({
      searchMode: 'secondaryIndex',
      secondaryIndex: undefined as string | undefined,
      returnColumns: [] as any[],
      limitList: times(10, (index: number) => {
        return { label: index * 100 + 100, value: index * 100 + 100 }
      }),
      limit: undefined as number | undefined,
      searchValue: new Map() as Map<string, string>,
      searchTableName: undefined as string | undefined,
      schemaName: undefined as string | undefined,
      tableName: undefined as string | undefined,
    })

    const resetFilters = () => {
      state.searchMode = props.filterOptions.searchMode
      setTimeout(() => {
        if (props.filterOptions.secondaryIndex === state.secondaryIndex) {
          for (const key of filterColumns.value) {
            const itemId = 'searchValue_' + (state.searchMode === 'secondaryIndex' ? state.secondaryIndex : 'primary') + key
            const _ref = document.getElementById(itemId)?.getElementsByTagName('input')[0]
            if (_ref) {
              _ref.value = props.filterOptions.searchValue.get(key) || ''
              _ref.dispatchEvent(new Event('input'))
            }
          }
        }
        state.secondaryIndex = props.filterOptions.secondaryIndex
        state.returnColumns = props.filterOptions.returnColumns
        state.limit = props.filterOptions.limit
      })
    }

    onMounted(() => {
      resetFilters()
    })

    const filterColumns = computed(() => {
      let res: any[] = []
      if (state.searchMode === 'secondaryIndex') {
        res = props.tableIndexList.find((item: any) => item.name === state.secondaryIndex)?.columns || []
      } else if (state.searchMode === 'primaryKey') {
        res = props.primaryColumns
      }
      return res
    })

    const resetDisabled = computed(() => {
      if (!state.secondaryIndex && isEmpty(state.returnColumns) && isEmpty(state.searchValue)) return true
      return false
    })

    watch(() => state.secondaryIndex, () => {
      const item = props.tableIndexList.find((item: any) => item.name === state.secondaryIndex)
      state.schemaName = item?.schemaName
      state.tableName = item?.tableName
      state.searchValue = new Map()
      let _searchValue: Map<string, string> = new Map()
      if (props.filterOptions.secondaryIndex === state.secondaryIndex) {
        _searchValue = props.filterOptions.searchValue
      }
      for (const each of filterColumns.value) {
        state.searchValue.set(each, _searchValue.get(each) || '')
      }
    })

    watch(() => state.searchMode, () => {
      state.secondaryIndex = undefined
    })

    const changeSearchValue = (column: any, e: any) => {
      state.searchValue.set(column, e.target.value)
    }

    /**
     * 提交表单
     */
    const handleConfirm = () => {
      console.log(filterColumns.value, state)
      context.emit('confirm', {
        searchMode: state.searchMode,
        secondaryIndex: state.secondaryIndex,
        returnColumns: state.returnColumns,
        limit: state.limit,
        searchValue: state.searchValue,
        schemaName: state.schemaName,
        tableName: state.tableName,
      })
    }
    const handleCancel = () => {
      context.emit('close')
    }

    return {
      ...toRefs(state),

      filterColumns,

      resetDisabled,
      resetFilters,
      changeSearchValue,
      handleConfirm,
      handleCancel
    }
  }
})

</script>

<style lang="scss">
.search-data-filter-container {
  height: 100%;
  padding-bottom: 60px;

  .reset-data-filter-button {
    position: absolute;
    top: -43px;
    left: 92px;
  }

  .search-data-filter-form {
    height: 100%;
    overflow: auto;

    .search-data-filter-form-item {
      margin-bottom: 20px;

      .search-data-filter-form-label {
        height: 22px;
        padding-bottom: 5px;
        color: #85888C;
        font-size: 12px;
        line-height: 17px;
      }

      .search-data-filter-form-value {

        .search-data-filter-form-select {
          width: 100%;
        }
        .search-data-filter-form-select-short {
          width: 300px;
        }
        .search-data-filter-form-input-short {
          width: 300px;
        }

        .search-data-filter-form-value-item {
          display: flex;
          width: 100%;
          align-items: center;
          margin-bottom: 10px;

          .search-data-filter-form-value-label {
            flex-shrink: 0;
            margin-right: 10px;
            width: 80px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            color: #85888C;
            font-size: 12px;
          }
        }
      }
    }
  }

  .search-data-filter-form-btn-container {
    position: absolute;
    left: 0;
    bottom: 15px;

    .#{$ant-prefix}-btn:not(:last-child) {
      margin-right: 10px;
    }
  }
}
</style>
