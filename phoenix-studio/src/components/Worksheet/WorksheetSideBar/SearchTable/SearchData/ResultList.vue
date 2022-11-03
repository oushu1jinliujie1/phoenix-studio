<template>
  <div class="search-result-list-container">
    <div class="search-result-show-filter-button" @click="showFilters()">
      <icon color="#336CFF" name="worksheet/secondary_index"/>
    </div>
    <div class="search-result-list-main">
      <x-spin :spinning="resultLoading">
        <div class="search-result-list-tags">
          <x-tag
            v-for="tag of filterTags" :key="tag.key + tag.value"
            :closable="isClosable(tag)"
            class="search-result-list-tag"
            :class="tag.type"
            @close="removeFilter(tag)">
            <template #icon>
              <icon :name="tag.icon"/>
            </template>
            <div class="search-result-list-tag-title">{{ tag.key }}</div>
            <div class="search-result-list-tag-content">{{ tag.value }}</div>
          </x-tag>
        </div>
        <div class="search-result-table-container">
          <x-table
            :data-source="searchResults"
            :divider="true"
            :pagination="false"
            :scroll="{ x: 720, y: null }"
            auto-calc-empty-height
          >
            <a-table-column key="index" title="序号" align="right" width="65px">
              <template #default="{ index }">
                {{ index + 1 }}
              </template>
            </a-table-column>
            <a-table-column v-for="column of filterOptions.returnColumns" :key="column.option.key" :title="column.option.key" width="160px">
              <template #default="{ record }">
                {{ record[column.option.key] }}
              </template>
            </a-table-column>
          </x-table>
        </div>
      </x-spin>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, toRefs, PropType, computed } from 'vue'
import Icon from '@/components/Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'

export default defineComponent({
  name: 'ResultList',
  props: {
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
    searchResults: {
      type: Array as PropType<any[]>,
      default: () => {
        return []
      }
    },
    resultLoading: Boolean
  },
  components: { Icon, ...smartUI },
  emits: ['showFilters', 'removeFilter'],
  setup(props, context) {

    const filterTags = computed(() => {
      const tags: any[] = []
      if (props.filterOptions.limit) tags.push({ type: 'limit', value: props.filterOptions.limit, key: '行数限制', color: '#CCA236', icon: 'worksheet/limit' })
      for (const column of props.filterOptions.returnColumns) {
        tags.push({ type: 'column', value: column.option.key, key: '返回列', color: '#CE7FFF', icon: 'worksheet/column' })
      }
      if (props.filterOptions.secondaryIndex) tags.push({ type: 'index', value: props.filterOptions.secondaryIndex, key: '二级索引', color: '#A0D744', icon: 'worksheet/secondary_index_new' })
      if (props.filterOptions.searchMode === 'primaryKey') tags.push({ type: 'mode', value: '', key: '主键检索', color: '#A0D744', icon: 'worksheet/column' })
      props.filterOptions.searchValue.forEach((value: any, key: string) => {
        if (value) {
          tags.push({ type: 'searchValue', value: value, key: key, color: '#336CFF', icon: 'worksheet/search' })
        }
      })
      return tags
    })

    const isClosable = (tag: any) => {
      switch (tag.type) {
        case 'mode':
          return false
        case 'index':
          return false
        case 'column':
          if (props.filterOptions.returnColumns.length > 1) return true
          return false
        case 'limit':
          return false
        case 'searchValue':
          if (Array.from(props.filterOptions.searchValue.keys())[0] === tag.key) return false
          return true
        default:
          return false
      }
    }

    const removeFilter = (tag: any) => {
      context.emit('removeFilter', tag)
    }

    const showFilters = () => {
      context.emit('showFilters')
    }

    return {
      filterTags,

      isClosable,

      removeFilter,
      showFilters
    }
  }
})

</script>

<style lang="scss">
.search-result-list-container {
  height: 100%;

  .search-result-show-filter-button {
    position: absolute;
    top: -38px;
    right: 38px;
    cursor: pointer;
  }

  .search-result-list-main {
    height: 100%;
    .antv-spin-nested-loading {
      height: 100%;
      .antv-spin-container {
        display: flex;
        flex-direction: column;
        height: 100%;
      }
    }

    .search-result-list-tags {
      flex-shrink: 0;
      display: flex;
      flex-wrap: wrap;
      justify-content: stretch;
      gap: 10px;

      .search-result-list-tag {
        display: flex;
        align-items: center;
        margin: 0;

        .search-result-list-tag-title {
          padding: 0 5px;

        }
        .search-result-list-tag-content {
          padding: 0 5px;
          font-weight: 600;
        }

        &.limit {
          background: rgba(248, 204, 91, 0.3);
          color: #F8CC5B;
        }
        &.column {
          background: rgba(206, 127, 255, 0.2);
          color: #CE7FFF;
        }
        &.index {
          background: rgba(160, 215, 68, 0.2);
          color: #A0D744;
        }
        &.searchValue {
          background: rgba(51, 108, 255, 0.2);
          color: #336CFF;
        }
      }
    }

    .search-result-table-container {
      margin-top: 10px;
      flex-grow: 1;
      height: 0;
      margin-bottom: 20px;
      overflow: auto;

      .smartui-table:not(.raw).antv-table-wrapper .antv-table-thead > tr > th,
      .smartui-table:not(.raw).antv-table-wrapper .antv-table-tbody > tr > td {
        position: relative;
        padding-right: 15px;
      }
      .smartui-table:not(.raw).antv-table-wrapper .antv-table-tbody > tr > td:after {
        content: '';
        height: calc(100% - 10px);
        width: 1px;
        background: #D5D8DB;
        position: absolute;
        top: 5px;
        right: 0;
      }
      .smartui-table:not(.raw).antv-table-wrapper .antv-table.antv-table-layout-fixed .antv-table-tbody > tr:hover > td:not(.antv-table-cell-fix-left):before {
        position: absolute;
        pointer-events: none;
        content: '';
        width: 100%;
        height: 100%;
        top: 0;
        left: 0px;
        border-top: 1px solid #85888c;
        border-bottom: 1px solid #85888c;
        border-radius: 0;
      }
      .smartui-table:not(.raw).antv-table-wrapper .antv-table.antv-table-layout-fixed .antv-table-tbody > tr:hover > td:not(.antv-table-cell-fix-left):first-of-type:before {
        position: absolute;
        pointer-events: none;
        content: '';
        width: 100%;
        height: 100%;
        top: 0;
        left: 0px;
        border-top: 1px solid #85888c;
        border-bottom: 1px solid #85888c;
        border-left: 1px solid #85888c;
        border-radius: 4px 0 0 4px;
      }
      .smartui-table:not(.raw).antv-table-wrapper .antv-table.antv-table-layout-fixed .antv-table-tbody > tr:hover > td:last-of-type:after {
        content: none;
      }
      .smartui-table:not(.raw).antv-table-wrapper .antv-table.antv-table-layout-fixed .antv-table-tbody > tr:hover > td:last-of-type:before {
        position: absolute;
        pointer-events: none;
        content: '';
        width: 100%;
        height: 100%;
        top: 0;
        left: 0px;
        border-top: 1px solid #85888c;
        border-bottom: 1px solid #85888c;
        border-right: 1px solid #85888c;
        border-radius: 0 4px 4px 0;
      }
    }
  }
}
</style>
