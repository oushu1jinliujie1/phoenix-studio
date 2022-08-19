<template>
  <div class="statistics-panel-container">
    <div class="statistics-panel">
      <div class="data">
        <x-tabs v-model:activeKey="activeKey">
          <x-tab-pane key="exec-result">
            <template #tab>
              <span data-test-id="oushudb-worksheet-execute-result-tab-title">
                <span><icon image name="statistics-panel/exec-result-icon" style="color: inherit;"/></span>
                执行结果
              </span>
            </template>
            <exec-result
              ref="execResultRef"
              :loading="execResultLoading"
              :search-result="searchResult"
              :search-text="search"
            ></exec-result>
          </x-tab-pane>
        </x-tabs>
      </div>
      <div class="tools">
        <input-search
          v-show="isShowSearchRef"
          v-model:value="search"
          :allow-clear="true"
          placeholder="请输入需要搜索的数据"
          @search="onSearch"
        />
        <x-tooltip placement="bottomLeft">
          <template #title>显示右侧卡片</template>
          <x-button
            :class="{ 'layout-btn': true, 'selected': !isHideRightCard }"
            icon-name="statistics-panel/layout-all"
            type="text"
            @click="handleHideRightCard(false)"
          ></x-button>
        </x-tooltip>
        <x-tooltip placement="bottomLeft">
          <template #title>收起右侧卡片</template>
          <x-button
            :class="{ 'layout-btn': true, 'selected': isHideRightCard }"
            icon-name="statistics-panel/layout-left"
            type="text"
            @click="handleHideRightCard(true)"
          ></x-button>
        </x-tooltip>
      </div>
    </div>
  </div>
  <!-- SQL 详情抽屉 -->
  <x-drawer
    v-model:visible="sqlDetailVisible"
    :bodyStyle="{ height: 'calc(100% - 75px)', overflow: 'hidden' }"
    title="SQL 详情"
    width="800"
    wrapClassName="sql-detail-drawer"
  >
    <x-button class="sql-detail-copy-btn" @click="onCopy">
      <icon image name="statistics-panel/copy"></icon>
      <span>复制</span>
    </x-button>
    <div id="sql-detail-editor-outer-container" class="sql-detail-editor-outer-container"></div>
  </x-drawer>
</template>

<script lang="ts">
import { computed, ComputedRef, defineComponent, onUnmounted, reactive, ref, toRefs, watch } from 'vue'
import dayjs from 'dayjs'
import Icon from '@/components/Icon.vue'
import ExecResult, {
  createTableData,
  ExecResultTableItem,
  handleResolveTableData,
} from '@/components/Worksheet/StatisticsPanel/ExecResult.vue'
import InputSearch from '@/components/Worksheet/StatisticsPanel/InputSearch.vue'
import { message } from 'ant-design-vue-3'
import { ColumnProps } from 'ant-design-vue/lib/table/interface'
import { useStore } from 'vuex'
import { copyToClipboard } from '@/lib/copy'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import * as monaco from 'monaco-editor'
import { useRoute } from 'vue-router'
import {
  SQLExecResLoadingType,
  SQLExecResShowEmpty,
  SQLExecResShowLoadingOnly,
  SQLExecResShowLoadingWithStartVC,
  SQLExecResShowMeaningless,
} from '@/store/statisticsPanelStore'

const execHistoryColumns: ColumnProps[] = [
  { title: '状态', dataIndex: 'status', key: 'status', ellipsis: true, slots: { customRender: 'status' }, width: '80px' },
  {
    title: '执行时间',
    dataIndex: 'duration',
    key: 'duration',
    ellipsis: true,
    slots: { customRender: 'duration' },
    width: '100px',
  },
  { title: '开始时间', dataIndex: 'start', key: 'start', ellipsis: true, slots: { customRender: 'date' } },
  { title: 'Query ID', dataIndex: 'query_id', key: 'query_id', ellipsis: true, slots: { customRender: 'queryid' } },
  { title: '结果行数', dataIndex: 'rows', key: 'rows', ellipsis: true, width: '80px' },
  { title: 'SQL', dataIndex: 'sql_text', key: 'sql_text', ellipsis: true, slots: { customRender: 'sqltext' } },
  { title: '结束时间', dataIndex: 'end', key: 'end', ellipsis: true, slots: { customRender: 'date' } },
  {
    title: '用户',
    dataIndex: 'creator_name',
    key: 'creator_name',
    ellipsis: true,
    slots: { customRender: 'creatorname' },
  },
]

export interface StatisticsPanelState {
  activeKey: string;
  isHideRightCard: boolean;
  exportModalVisible: boolean;
  execHistoryVisible: boolean;
  exportFileType: string;
  exportFileContent: string;
  search: string;
  execHistoryOptions: { label: string, value: string }[];
  execHistoryList: any[];
  customFields: string[];
  pageNum: number;
  pageSize: number;
  execResultLoading: SQLExecResLoadingType;
  execHistoryLoading: boolean;
  execHistoryPageNum: number;
  execHistoryPageSize: number;
  execHistoryListLength: number;
  searchResult: ExecResultTableItem[];
  sqlDetailVisible: boolean;
  isShowedAllHistory: boolean; // 当前页面是否显示了所有 "查询历史"
  editor: any;
  canChart: boolean;
  isFirstLoadExecHistory: boolean;
}

/* eslint-disable */
let editor: any = undefined

const initEditor = () => {
  const domEditor = document.getElementById('sql-detail-editor-outer-container')
  // 初始化配置
  if (!domEditor) return
  editor = monaco.editor.create(domEditor, {
    theme: 'vs',
    readOnly: true,
    automaticLayout: true,
    language: 'sql',
    value: '',
  })
}

export default defineComponent({
  name: 'StatisticsPanel',
  components: {
    Icon,
    ExecResult,
    InputSearch,
    ...smartUI,
  },
  props: {
  },
  setup(props) {
    const state = reactive<StatisticsPanelState>({
      activeKey: 'exec-result', // exec-result、data-visualization
      isHideRightCard: false,
      exportModalVisible: false,
      execHistoryVisible: false,
      exportFileType: 'csv',
      exportFileContent: 'current',
      search: '',
      execHistoryOptions: [
        { label: '状态', value: 'status' },
        { label: '执行时间', value: 'duration' },
        { label: '开始时间', value: 'start' },
        { label: 'Query ID', value: 'query_id' },
        { label: '结果行数', value: 'rows' },
        { label: 'SQL', value: 'sql_text' },
        { label: '结束时间', value: 'end' },
        { label: '用户', value: 'creator_name' },
      ],
      execHistoryList: [],
      // 默认不显示 "结束时间" / "用户"
      customFields: ['status', 'duration', 'start', 'query_id', 'rows', 'sql_text'],
      pageNum: 1,
      pageSize: 10,
      execResultLoading: SQLExecResShowMeaningless,
      execHistoryLoading: false,
      execHistoryPageNum: 1,
      execHistoryPageSize: 30,
      execHistoryListLength: 0,
      searchResult: [],
      sqlDetailVisible: false,
      isShowedAllHistory: false,
      editor: null,
      canChart: false,
      isFirstLoadExecHistory: true,
    })
    const execResultRef = ref()
    const dataVisualizationRef = ref()

    const store = useStore()
    const route = useRoute()

    const columns = computed(() => {
      const cols: ColumnProps[] = []
      state.customFields.forEach(field => {
        const index = execHistoryColumns.findIndex(col => col.key === field)
        if (index > -1) cols[index] = execHistoryColumns[index]
      })
      return cols.filter(col => col !== undefined)
    })

    const summaryRef = computed(() => store.getters['statisticsPanelStore/getSummary'])
    const resultRef = computed(() => store.getters['statisticsPanelStore/getTableResult'])
    const worksheetInfoRef: ComputedRef<any | undefined> = computed(() => store.getters['worksheet/getWorkSheet'])

    const isShowSearchRef = computed(() => {
      return summaryRef.value?.status === 'true' && resultRef.value && state.activeKey === 'exec-result'
    })

    // 搜索
    const onSearch = (text: string) => {
      state.execResultLoading = SQLExecResShowLoadingOnly
      const tableResultRaw = store.getters['statisticsPanelStore/getTableResult']
      if (!tableResultRaw) return
      const list: ExecResultTableItem[] = []
      const searchResult = createTableData(tableResultRaw)
      searchResult.forEach(record => {
        const keys = Object.keys(record)
        let isMatched = false
        keys.forEach(key => {
          // 去空格，变小写
          const sourceText = text.trim().toLowerCase()
          const targetText = (record[key].value + '').trim().toLowerCase()
          const index = targetText.indexOf(sourceText)
          if (index > -1) {
            isMatched = true
          }
        })
        if (isMatched) {
          list.push(record)
        }
      })
      state.searchResult = list
      state.execResultLoading = SQLExecResShowMeaningless
    }

    // 复制 SQL 语句
    const onCopy = () => {
      const summary = store.getters['statisticsPanelStore/getSummary']
      copyToClipboard(summary.sql_text)
    }

    const handleOpenSqlDetailDrawer = (sql: string) => {
      state.sqlDetailVisible = true
      setTimeout(() => {
        if (!editor) {
          initEditor()
        }
        editor.setValue(sql)
      })
    }

    const handleHideRightCard = (visible: boolean) => {
      state.isHideRightCard = visible
      if (execResultRef.value) {
        execResultRef.value.handleHideSummary(visible)
      }
      if (dataVisualizationRef.value) {
        dataVisualizationRef.value.handleHideVisualConfig(visible)
      }
    }

    onUnmounted(() => {
      // @eslint-disable-next-line
      if (editor !== undefined) {
        editor.dispose()
      }
    })

    return {
      ...toRefs(state),
      execResultRef,
      dataVisualizationRef,
      isShowSearchRef,
      columns,

      onSearch,
      copyToClipboard,
      dayjs,
      onCopy,
      handleOpenSqlDetailDrawer,
      handleHideRightCard,
    }
  },
})
</script>

<style lang="scss">
@import "../../../styles/variables.scss";
@import "./style.scss";

@keyframes rotate {
  to {
    transform: rotate(360deg);
  }
}

.statistics-panel-container {
  height: 100%;

  .statistics-panel {
    position: relative;
    height: 100%;

    .data {
      height: 100%;

      .#{$ant-prefix}-tabs {
        height: 100%;

        .#{$ant-prefix}-tabs-bar {
          padding-left: 30px;
          margin: 0;

          .#{$ant-prefix}-tabs-tab {
            padding-top: 10px;
            padding-bottom: 12px;
          }
        }

        .#{$ant-prefix}-tabs-top-content {
          // - (height - border - margin) of ant-tabs-header
          //height: calc(100% - 40px - 1px - 16px);
          height: calc(100% - 46px);
          padding-top: 16px;
          padding-bottom: 10px;

          .#{$ant-prefix}-tabs-tabpane-active {
            height: 100%;

            // execute
            .#{$ant-prefix}-spin-nested-loading {
              height: 100%;

              .#{$ant-prefix}-spin-container {
                height: 100%;

                .exec-result {
                  height: 100%;
                  overflow: hidden;

                  .result {
                    height: 100%;
                    padding-right: 10px;
                    padding-left: 20px;
                  }

                  .summary {
                    height: 100%;
                    overflow-y: auto;

                    > div {
                      min-height: 360px;
                    }
                  }
                }
              }
            }

            // chart visualization
            .data-visualization {
              height: 100%;
            }
          }
        }
      }

      svg {
        //stroke-width: 0;

        &.active-icon {
          fill: #336CFF;
        }
      }
    }

    .tools {
      position: absolute;
      top: 0;
      right: 0;
      display: flex;
      align-items: center;
      height: 40px;
      margin-right: 30px;
      line-height: 40px;

      > button {
        margin-left: 10px;
      }

      :deep(input) {
        height: auto;
        box-shadow: none;
      }

      .layout-btn {
        color: $color-primary-black;

        &.selected {
          color: $color-primary-blue;
        }
      }

      .layout-btn-disable {
        color: $color-text-comment !important;
        cursor: not-allowed;
      }
    }

    :deep(.#{$ant-prefix}-tabs-nav) {
      height: 42px;
      line-height: 18px;
    }
  }
}

// 自定义 field 选择器
.custom-field-select {
  position: absolute;
  top: 27px;
  left: 130px;
  z-index: 2;
}

// 刷新按钮
.v-oushudb-refresh {
  position: absolute;
  top: 33px;
  right: 70px;
  z-index: 2;
  cursor: pointer;

  &.rotate {
    animation: rotate 0.3s;
  }
}

.export-option {
  text-align: left;

  &:first-child {
    margin-bottom: 30px;
  }

  &:last-child {
    margin-bottom: 10px;
  }

  p {
    margin-bottom: 10px;
    font-size: $font-size-small;
  }

  > div {
    display: flex;
    justify-content: space-between;

    div {
      width: 155px;
      margin-right: 10px;
      line-height: 30px;
      text-align: center;
      cursor: pointer;
      border: 1px solid $color-line-bold;
      border-radius: 4px;

      &.selected {
        border: 1px solid $color-primary-blue;
      }
    }
  }
}

.link {
  color: $color-primary-blue;
  cursor: pointer;
}

.loadmore {
  line-height: 33px;
  text-align: center;
  background-color: #FFF;
  border-top: 1px solid $color-line-bold;
  border-bottom: 1px solid $color-line-bold;
}

// 定制 tabs 样式
:deep(.#{$ant-prefix}-tabs) {
  .#{$ant-prefix}-tabs-bar {
    margin: 0;
  }

  .#{$ant-prefix}-tabs-top-content {
    // - (height - border - margin) of ant-tabs-header
    //height: calc(100% - 40px - 1px - 16px);
    height: 100%;
  }
}

// 执行历史抽屉
.sql-exec-history-drawer {
  // 定制 table 样式
  @include add-border-to-th(#D5D8DB);
  @include add-border-to-td(#D5D8DB);

  .smartui-table:not(.raw).antv-table-wrapper .antv-table {
    border-bottom: none;
  }

  .delete-icon {
    display: none;
    padding: 1.5px;
    margin-left: 5px;
    cursor: pointer;
    border-radius: 4px;

    &:hover {
      background-color: rgba(213, 216, 219, 0.5);
    }
  }

  .#{$ant-prefix}-table .#{$ant-prefix}-table-content {
    padding-bottom: 1px;
  }

  .#{$ant-prefix}-table .#{$ant-prefix}-table-tbody > tr:hover .delete-icon {
    display: inline-block;
  }

  .#{$ant-prefix}-table .#{$ant-prefix}-table-thead > tr > th {
    padding-top: 10px;
    padding-bottom: 10px;
    font-size: $font-size-small;
    font-weight: normal;
  }

  .#{$ant-prefix}-table .#{$ant-prefix}-table-tbody > tr {
    position: relative;
    cursor: pointer;

    // 实现悬浮表格行时 ::after 的框和 td 的 boder-top 重合且无圆角的问题
    @include on-tr-hover-td-border-top-radius();

    // 去掉 x-table 样式带来的影响
    &:hover td:first-of-type::before {
      content: none !important;
    }

    // 给最后一个 td 加 ::after
    &:hover td:last-of-type::after {
      position: absolute;
      top: 0;
      left: 0;
      z-index: 1;
      width: 100%;
      height: calc(100% + 1px);
      pointer-events: none;
      content: '';
      border: 1px solid $color-text-comment;
      border-radius: 4px;
    }
  }

  .#{$ant-prefix}-table .#{$ant-prefix}-table-tbody > tr > td {
    height: 40px;
    padding-top: 10px;
    padding-bottom: 10px;
    white-space: nowrap;
    background: none !important;
  }

  .#{$ant-prefix}-table .#{$ant-prefix}-table-placeholder {
    border: none;
  }

  .#{$ant-prefix}-drawer-body {
    padding-bottom: 65px !important;
  }

  .#{$ant-prefix}-drawer-close {
    right: 18px;
    width: 18px;
    height: 18px;
  }

  .#{$ant-prefix}-table-footer {

    .#{$ant-prefix}-empty {
      position: fixed;
      top: 50%;
      transform: translateY(-50%);

      .#{$ant-prefix}-empty-image {
        margin-bottom: 20px;
      }

      .#{$ant-prefix}-empty-description {
        width: 255px;
        font-size: $font-size-large;
        line-height: 24px;
      }
    }
  }
}

// sql 详情弹窗
.sql-detail-drawer {
  .sql-detail-editor-outer-container {
    height: 100%;
  }

  .sql-detail-copy-btn {
    position: absolute;
    top: 25px;
    left: 130px;
  }
}
</style>
