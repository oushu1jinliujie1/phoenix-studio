<template>
  <div class="oushudb-worksheet-execute-result">
    <!-- SQL执行中，展示 loading 动画-->
    <div v-if="status === 'show-loading'" class="oushudb-worksheet-execute-result-loading-container">
      <div class="oushudb-worksheet-execute-result-loading-description">
        <x-spin :spinning="true" font-size="4px"/>
        {{
          stateLoading === SQLExecResShowLoadingWithStartVC
            ? '正在启动虚拟计算集群，请稍候！'
            : `请求执行中${executeInfoRef
              ? `：${executeInfoRef.current_count} / ${executeInfoRef.total_count}`
              : '！'}`
        }}
      </div>
      <x-button :loading="cancelBtnLoadingRef" danger style="display: none; margin-top: 30px;"
                @click="handleCancelExecute">
        <icon color="primary" image name="worksheet/cancel"/>
        取消
      </x-button>
    </div>
    <!-- 未曾执行过SQL -->
    <div v-else-if="status === 'show-empty'" class="oushudb-worksheet-execute-result-empty">
      <icon image name="empty"/>
      <p>还没有查询结果哦</p>
    </div>
    <!-- SQL 执行结果 -->
    <div v-else class="oushudb-worksheet-execute-result-show-container">
      <!-- 执行结果展示 -->
      <div class="oushudb-worksheet-execute-result-show">
        <!-- 执行成功（查询语句）  -->
        <div v-if="status === 'show-exec-result'">
          <div class="div-table">
            <div class="div-table-header">
              <div class="div-table-header-row">
                <div v-for="col in columns" :key="col.key" class="div-table-header-cell">{{ col.title }}</div>
              </div>
            </div>
            <div class="div-table-body">
              <div v-for="(record, index) in dataSource" :key="record.rowKey" class="div-table-body-row">
                <!-- 显示序号 -->
                <div class="div-table-body-cell">{{ index + 1 }}</div>
                <!-- 显示数据 -->
                <div v-for="key in Object.keys(record).filter(key => key !== 'rowKey')"
                     :key="key"
                     :class="{ 'div-table-body-cell': true, 'matched': handleMatch(searchText, record[key].value) }"
                ><p :title="record[key].value" class="ppp-ppp">{{ handleResolveTableData(record, key) }}</p></div>
              </div>
            </div>
          </div>
        </div>
        <!-- 执行成功（非查询语句）  -->
        <div
          v-if="status === 'show-exec-success'"
          class="oushudb-worksheet-execute-result-no-data"
        >
          <icon image name="statistics-panel/exec-success"/>
          <p>{{ summary?.message }}</p>
        </div>
        <!-- 执行失败 -->
        <div
          v-if="status === 'show-exec-failure' || status === 'show-exec-server-error'"
          class="oushudb-worksheet-execute-result-no-data"
        >
          <template v-if="summary?.message === 'pq: canceling statement due to user request'">
            <icon color="comment" name="statistics-panel/cancel_warn" style="width: 80px; height: 80px;"/>
            <p>执行中的SQL脚本已取消</p>
          </template>
          <template v-else>
            <icon image name="statistics-panel/failure"/>
            <p>{{ summary?.message }}</p>
          </template>
        </div>
      </div>
      <!-- 执行相关信息展示 -->
      <div v-if="isShowSummaryRef"
           :class="{ 'oushudb-worksheet-execute-result-summary': true, 'hidden': isHideSummary }">
        <div class="summary-item">
          <div class="title">Query ID</div>
          <div class="value link">
            <span>{{ summary.query_id }}</span>
          </div>
          <icon class="copy" image name="statistics-panel/copy" @click="copyToClipboard(summary.query_id)"></icon>
        </div>
        <div class="summary-item">
          <div class="title">SQL</div>
          <div class="value link can_click">
            <!--TODO: 打开 SQL 详情抽屉-->
            <span @click="handleOpenSqlDetailDrawer(summary.sql_text)">{{ summary.sql_text }}</span>
          </div>
          <icon class="copy" image name="statistics-panel/copy" @click="copyToClipboard(summary.sql_text)"></icon>
        </div>
        <div class="summary-item">
          <div class="title">执行时间</div>
          <div class="value">{{ summary.duration + 'ms' }}</div>
        </div>
        <div class="summary-item">
          <div class="title">开始时间</div>
          <div class="value">{{ dayjs(summary.start).format('YYYY/MM/DD HH:mm:ss') }}</div>
        </div>
        <div class="summary-item">
          <div class="title">结束时间</div>
          <div class="value">{{ dayjs(summary.end).format('YYYY/MM/DD HH:mm:ss') }}</div>
        </div>
        <div class="summary-item">
          <div class="title">行数</div>
          <div class="value">{{ summary.rows }}</div>
        </div>
        <div class="summary-item">
          <div class="title">用户</div>
          <div class="value">
            <router-link :to="`/main/UserCenter/user/${summary?.creator_id}`">
              <!--TODO: 跳转到用户界面？-->
              <icon image name="avatar"></icon>
              <span>{{ summary?.creator_name ?? userInfo.username }}</span>
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
  <x-drawer
    v-model:visible="sqlDetailVisible"
    :bodyStyle="{ height: 'calc(100% - 75px)', overflow: 'hidden' }"
    title="SQL 详情"
    width="800"
  >
    <x-button class="sql-detail-copy-btn" @click="onCopy">
      <icon image name="statistics-panel/copy"></icon>
      <span>复制</span>
    </x-button>
    <div id="sql-detail-editor-container" class="sql-detail-editor-container"></div>
  </x-drawer>
</template>

<script lang="ts">
import { computed, ComputedRef, defineComponent, onUnmounted, PropType, reactive, ref, toRefs, watch } from 'vue'
import dayjs from 'dayjs'
import Icon from '@/components/Icon.vue'
import { ColumnProps } from 'ant-design-vue/lib/table/interface'
import { useStore } from 'vuex'
import { copyToClipboard } from '@/lib/copy'
import * as monaco from 'monaco-editor'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index'
import { message } from 'ant-design-vue-3'
import { useI18n } from 'vue-i18n'
import { translateErrorMessage } from 'lava-fe-lib/lib-common/i18n'
import {
  ExecuteInfo,
  SQLExecResLoadingType,
  SQLExecResShowEmpty,
  SQLExecResShowLoadingOnly,
  SQLExecResShowLoadingWithStartVC,
  SQLExecResShowMeaningless,
} from '@/store/statisticsPanelStore'

const createColumns = (headers: string[]): any[] => {
  const columns: any[] = headers.map((header: string, index: number) => ({
    title: header,
    dataIndex: `${header}${index}`,
    key: `${header}${index}`,
    ellipsis: true,
  }))
  columns.unshift({ title: '', dataIndex: 'id', key: 'id', align: 'right', width: '65px' })
  return columns
}

export const createTableData = (data: any): ExecResultTableItem[] => {
  const { header, origin_types, body } = data
  const tableData: ExecResultTableItem[] = []
  if (body) {
    body.forEach((item: any, i: number) => {
      const record: ExecResultTableItem = { rowKey: i + 1 }
      header.forEach((key: any, j: number) => {
        record[`${key}${j}`] = {
          type: origin_types[j].toLowerCase(),
          value: item[j],
        }
      })
      tableData.push(record)
    })
  }
  return tableData
}

export const handleResolveTableData = (record: ExecResultTableItem, key: string) => {
  const { type, value } = record[key]
  if (!value) return value
  if (type === 'date') {
    // 获取 YYYY-MM-DD
    return value.match(/(.*)T/i)[1]
  } else if (type === 'time') {
    // 获取 HH:mm:ss.SSSSSS
    return value.match(/T(.*)Z/i)[1]
  } else if (type === 'timetz') {
    // 获取 HH:mm:ss.SSSSSS+08:00
    return value.match(/T(.*)/i)[1]
  } else if (type === 'timestamp') {
    // 获取 YYYY-MM-DD HH:mm:ss.SSSSSS
    const regRes = value.match(/(.*)T(.*)Z/i)
    return regRes ? `${regRes[1]} ${regRes[2]}` : value
  } else if (type === 'timestamptz') {
    // 获取 YYYY-MM-DD HH:mm:ss.SSSSSS+08:00
    const regRes = value.match(/(.*)T(.*)/i)
    return regRes ? `${regRes[1]} ${regRes[2]}` : value
  } else {
    return value
  }
}

/**
 * show-empty: 未执行的空白页面，提示用户执行 SQL | 想查看一个执行历史的执行结果，但是在此期间它被别人删除了，那此次获取就会失败，于是在弹窗提示后显示空页面
 * show-loading: SQL 执行中，loading状态
 * show-exec-result: 查询类 SQL，展示查询结果
 * show-exec-success: 非查询类 SQL，展示执行成功
 * show-exec-failure：SQL 执行失败，展示失败原因
 * show-exec-server-error: SQL 执行，服务器端错误
 */
type ExecuteStatus =
  'show-empty'
  | 'show-loading'
  | 'show-exec-result'
  | 'show-exec-success'
  | 'show-exec-failure'
  | 'show-exec-server-error'

export interface ExecResultState {
  status: ExecuteStatus;
  stateLoading: SQLExecResLoadingType;
  summary: any | null;
  sqlDetailVisible: boolean;
  columns: any[];
  dataSource: ExecResultTableItem[];
  editor: any;
}

export interface ExecResultTableItem {
  rowKey: string | number;

  [key: string]: any;
}

/* eslint-disable */
let editor: any = undefined

const initEditor = () => {
  const domEditor = document.getElementById('sql-detail-editor-container')
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
  name: 'ExecResult',
  components: {
    Icon, ...smartUI,
  },
  props: {
    loading: {
      type: Number as PropType<SQLExecResLoadingType>,
      default: SQLExecResShowMeaningless,
    },
    searchResult: {
      type: Array as PropType<ExecResultTableItem[]>,
      default: () => [],
    },
    searchText: {
      type: String,
      default: '',
    },
  },
  setup(props) {
    const { t } = useI18n()
    const getError = translateErrorMessage(t)
    const store = useStore()
    const state = reactive<ExecResultState>({
      // show-empty、show-loading、show-exec-result、show-exec-success、show-exec-failure
      status: 'show-empty',
      stateLoading: props.loading,
      summary: null,
      sqlDetailVisible: false,
      columns: [],
      dataSource: [],
      editor: null,
    })
    const isShowLoadingRef = computed(() => state.status === 'show-loading')

    // 用于点击按钮隐藏卡片
    const isHideSummary = ref(false)

    const isShowSummaryRef = computed(() =>
      state.summary && state.status !== 'show-loading'
      && state.status !== 'show-exec-server-error'
      && state.status !== 'show-empty',
    )

    const cancelBtnLoadingRef = ref(false)

    const userInfo: any = JSON.parse(window.localStorage.getItem('userInfo') || '{}')

    // 复制 SQL 语句
    const onCopy = () => {
      const summary = store.getters['statisticsPanelStore/getSummary']
      copyToClipboard(summary.sql_text)
    }

    // 用于将匹配结果高亮显示
    const handleMatch = (source: string, target: string) => {
      // 去空格，变小写
      const sourceText = source.trim().toLowerCase()
      const targetText = target.trim().toLowerCase()
      return sourceText && targetText.indexOf(sourceText) > -1
    }

    /**
     * 取消当前正在执行的SQL
     */
    const handleCancelExecute = async() => {
      cancelBtnLoadingRef.value = true

      // 取消执行SQL

      // const res = await cancelExecuteSQL({
      //   instance_id: worksheetInfoRef.value.instanceId,
      //   database: worksheetInfoRef.value.databaseName,
      //   unique_key: `${uniqueKey}`,
      // })
      // if (res.meta.success) {
      //   if (!res.data) {
      //     message.error('取消执行失败')
      //   }
      // } else {
      //   message.error(`请求执行失败：${getError(res)}`)
      // }
      // "优化"
      setTimeout(() => {
        cancelBtnLoadingRef.value = false
      }, 1000)
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

    const executeInfoRef: ComputedRef<undefined | null | ExecuteInfo> = computed(() => store.getters['statisticsPanelStore/getExecuteInfo'])

    // 供父元素调用
    const handleHideSummary = (value: boolean) => {
      isHideSummary.value = value
    }

    watch(() => store.getters['statisticsPanelStore/getLoading'], (loading: SQLExecResLoadingType) => {
      state.stateLoading = loading
      if (loading === SQLExecResShowLoadingOnly || loading === SQLExecResShowLoadingWithStartVC) {
        state.status = 'show-loading'
      } else if (loading === SQLExecResShowEmpty) {
        state.status = 'show-empty'
      }
    })

    // 监听 statisticsPanelStore
    watch(
      () => {
        const summary = store.getters['statisticsPanelStore/getSummary']
        const result = store.getters['statisticsPanelStore/getTableResult']
        return { summary, result }
      },
      (data) => {
        const { summary, result } = data
        // TODO: BUG, setLoading 也触发了此 watch，待解决
        if (!summary) return
        state.summary = summary

        if (editor) {
          editor.setValue(summary.sql_text)
        }

        if (summary.status === 'true') {
          if (result) {
            state.status = 'show-exec-result'
            // @ts-ignore
            state.columns = createColumns(result.header)
            state.dataSource = createTableData(result)
          } else {
            state.status = 'show-exec-success'
            state.summary = summary
          }
        } else {
          // todo: 暂时硬编码，对应 worksheet/index.vue line 227
          if (summary.message === 'server internal error') {
            state.status = 'show-exec-server-error'
            state.summary = summary
          } else if (summary.status === '') {
            // 空数据、无效数据
            state.status = 'show-empty'
          } else {
            state.status = 'show-exec-failure'
            state.summary = summary
          }
        }
      },
    )

    // 监听 StatisticsPanel 组件的传值，用于搜索
    watch(() => props.searchResult, (searchResult) => {
      state.status = 'show-exec-result'
      state.dataSource = searchResult
    })

    onUnmounted(() => {
      // @eslint-disable-next-line
      if (editor !== undefined) {
        editor.dispose()
      }
    })

    return {
      SQLExecResShowLoadingWithStartVC,

      ...toRefs(state),
      isShowLoadingRef,
      isHideSummary,
      isShowSummaryRef,
      cancelBtnLoadingRef,
      executeInfoRef,

      dayjs,
      copyToClipboard,
      userInfo,

      onCopy,
      handleMatch,
      handleResolveTableData,
      handleCancelExecute,
      handleOpenSqlDetailDrawer,
      handleHideSummary,
    }
  },
})
</script>

<style lang="scss" scoped>
@import "style";

$summary-bg: $color-line-light;

.oushudb-worksheet-execute-result {
  height: 100%;
  padding-left: 10px;
  padding-right: 25px;
  margin-top: -15px;

  // loading
  .oushudb-worksheet-execute-result-loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;

    .oushudb-worksheet-execute-result-loading-description {
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      font-weight: 600;
      line-height: 24px;
    }
  }

  .oushudb-worksheet-execute-result-show-container {
    display: flex;
    height: 100%;
    @include font-normal();

    // 执行结果
    .oushudb-worksheet-execute-result-show {
      flex: 1;
      width: 0;
      height: 100%;
      padding-right: 10px;
      padding-left: 20px;
      overflow: auto;

      // 查询语句
      .table-container {
        height: 100%;
        padding-bottom: 10px;
        overflow-y: auto;
      }

      // 非查询语句
      .oushudb-worksheet-execute-result-no-data {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 100%;
        @include font-normal();
        color: $color-text-comment;
        text-align: center;

        // 空状态的 icon
        > .icon {
          width: 36px;
          height: 36px;
          margin-bottom: 10px;
        }
      }
    }

    // 执行信息
    .oushudb-worksheet-execute-result-summary {
      flex-basis: 240px;
      flex-shrink: 0;
      padding: 15px 10px;
      margin-top: 10px;
      margin-left: 10px;
      overflow: auto;
      background-color: $summary-bg;
      border-radius: 4px;
      transition: transform 0.3s;

      &.hidden {
        display: none;
      }

      .summary-item {
        position: relative;
        display: flex;
        height: 36px;
        padding-top: 7px;
        padding-bottom: 7px;
        font-size: $font-size-normal;
        border-bottom: 1px solid $color-line-bold;

        &:last-of-type {
          padding-bottom: 0;
          margin-bottom: 0;
          border-bottom-width: 0;
        }

        .title {
          flex-basis: 65px;
          flex-shrink: 0;
          color: $color-text-comment;
        }

        .value {
          padding-right: 15px;
          // 省略号
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;

          &.link {
            // 省略号默认是黑色，改成主色调
            color: $color-primary-blue;
          }

          &.can_click {
            cursor: pointer;
          }

          svg {
            margin-right: 5px;
            //stroke-width: 0;
          }
        }

        .copy {
          position: absolute;
          top: 8px;
          right: 0;
          display: none;
          cursor: pointer;
        }

        &:hover > .copy {
          display: block;
        }
      }
    }
  }

  // 空状态
  .oushudb-worksheet-execute-result-empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    @include font-normal();
    color: $color-primary-black;
    text-align: center;

    // 空状态的 icon
    > .icon {
      width: 180px;
      height: 160px;
    }
  }
}

.sql-detail-editor-container {
  height: 100%;
}

.sql-detail-copy-btn {
  position: absolute;
  top: 25px;
  left: 130px;
}

// 自定义 table
.div-table {
  display: table;
  width: 100%;
  height: 100%;
  padding-bottom: 2px;

  .div-table-header {
    display: table-header-group;
  }

  .div-table-header-row {
    position: relative;
    display: table-row;
  }

  .div-table-header-cell {
    position: relative;
    display: table-cell;
    height: 30px;
    font-size: $font-size-small;
    padding: 5px 10px;
    margin: 10px 0;
    // 右侧边
    color: #85888C;
    border-right: none;

    // column 之间的竖杠
    &:not(:last-of-type)::after {
      position: absolute;
      top: 50%;
      right: 0;
      height: 50%;
      content: '';
      border-right: 1px solid $color-line-bold;
      transform: translateY(-50%);
    }
  }

  .div-table-body {
    display: table-row-group;
  }

  .div-table-body-row {
    position: relative;
    display: table-row;

    &:hover {
      background-color: #F6F6F6;

      & > .div-table-body-cell {

        &:first-of-type {
          border-left: 1px solid $color-line-bold;
        }

        &:last-of-type {
          border-right: 1px solid $color-line-bold;
        }
      }
    }
  }

  .div-table-body-cell {
    position: relative;
    display: table-cell;
    height: 30px;
    padding: 5px 10px;
    color: $color-primary-black;
    font-size: $font-size-small;
    // 右侧边
    vertical-align: middle;
    border-right: none;
    border-top: 1px solid $color-line-bold;

    // column 之间的竖杠
    &:not(:last-of-type)::after {
      position: absolute;
      top: 50%;
      right: 0;
      height: 50%;
      content: '';
      border-right: 1px solid $color-line-bold;
      transform: translateY(-50%);
    }

    // 首列右对齐
    &:first-of-type {
      width: 80px;
      color: #85888C;
      text-align: right;
    }

    // 限制每列的 max-width 为 240px 后，table 的自动分配列宽不能填充满整个表格
    // 可以使用下面的方法临时解决这个问题，但是不知道为啥，试出来的 QAQ~
    &:last-of-type {
      width: 240px; // 也可以是 260px（去掉 20px 的 padding，内容就是 240px），大差不差
    }

    &.matched {
      &::after {
        position: absolute;
        bottom: -1px;
        left: -1px;
        z-index: 1;
        width: calc(100% + 1px);
        height: calc(100% + 2px);
        content: '';
        background-color: rgba(245, 153, 27, 0.3);
        border: 1px solid $color-primary-orange;
      }
    }
  }
}

// todo: 不换号
.ppp-ppp {
  max-width: 240px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
