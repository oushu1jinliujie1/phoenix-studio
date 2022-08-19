/**
 * 0: 啥也不干，无意义
 * 1: loading
 * 2: loading + (vc启动中，请稍后）
 * 3: empty（空页面，例如想查看一个执行历史的执行结果，但是在此期间它被别人删除了，那此次获取就会失败，于是在弹窗提示后显示空页面）
 */
export const SQLExecResShowMeaningless = 0
export type SQLExecResShowMeaninglessType = 0
export const SQLExecResShowLoadingOnly = 1
export type SQLExecResShowLoadingOnlyType = 1
export const SQLExecResShowLoadingWithStartVC = 2
export type SQLExecResShowLoadingWithStartVCType = 2
export const SQLExecResShowEmpty = 3
export type SQLExecResShowEmptyType = 3
export type SQLExecResLoadingType =
  SQLExecResShowMeaninglessType
  | SQLExecResShowLoadingOnlyType
  | SQLExecResShowLoadingWithStartVCType
  | SQLExecResShowEmptyType

export interface ExecuteInfo {
  begin_timestamp?: number;
  current_count: number;
  current_timestamp?: number;
  total_count: number;
  is_finish: boolean;
  unique_key: string;
}

export interface SqlExecResult {
  // 0: false
  // 1: loading
  // 2: loading + (vc启动中，请稍后）
  // 3: empty（空页面，例如想查看一个执行历史的执行结果，但是在此期间它被别人删除了，那此次获取就会失败，于是在弹窗提示后显示空页面）
  loading: SQLExecResLoadingType;
  summary: any | null;
  tableResult: any | null;
  chartResult: string;
  executeInfo?: ExecuteInfo
}

export interface StatisticsPanelStoreState {
  // 一个工作簿，一个执行结果对象
  sqlExecResult: SqlExecResult;
}

const statisticsPanelStore = {
  namespaced: true,
  state: () => ({
    sqlExecResult: null,
  }),
  mutations: {
    setLoading(state: StatisticsPanelStoreState, payload: { loading: 0 | 1 | 2 }) {
      const { loading } = payload
      const sqlExecResult = state.sqlExecResult
      if (sqlExecResult) {
        sqlExecResult.loading = loading
      } else {
        state.sqlExecResult = { loading, summary: null, tableResult: null, chartResult: '' }
      }
    },
    setSummary(state: StatisticsPanelStoreState, payload: { summary: any }) {
      const { summary } = payload
      const sqlExecResult = state.sqlExecResult
      if (sqlExecResult) {
        sqlExecResult.summary = summary
      } else {
        state.sqlExecResult = { loading: 0, summary, tableResult: null, chartResult: '' }
      }
    },
    setTableResult(state: StatisticsPanelStoreState, payload: { tableResult: any }) {
      const { tableResult } = payload
      const sqlExecResult = state.sqlExecResult
      if (sqlExecResult) {
        sqlExecResult.tableResult = tableResult
      } else {
        state.sqlExecResult = { loading: 0, summary: null, tableResult, chartResult: '' }
      }
    },
    setChartResult(state: StatisticsPanelStoreState, payload: { chartResult: string }) {
      const { chartResult } = payload
      const sqlExecResult = state.sqlExecResult
      if (sqlExecResult) {
        sqlExecResult.chartResult = chartResult
      } else {
        state.sqlExecResult = { loading: 0, summary: null, tableResult: null, chartResult: '' }
      }
    },
    setExecuteInfo(state: StatisticsPanelStoreState, payload: { executeInfo: ExecuteInfo | undefined }) {
      const { executeInfo } = payload
      const sqlExecResult = state.sqlExecResult
      if (sqlExecResult) {
        if (executeInfo)
          sqlExecResult.executeInfo = {
            ...sqlExecResult.executeInfo,
            ...executeInfo,
          }
        else
          sqlExecResult.executeInfo = undefined
      } else {
        state.sqlExecResult = { loading: 0, summary: null, tableResult: null, chartResult: '' }
      }
    },
  },
  getters: {
    getLoading: (state: StatisticsPanelStoreState) => (): SQLExecResLoadingType => {
      const sqlExecResult = state.sqlExecResult
      if (sqlExecResult === undefined)
        if (process.env.NODE_ENV !== 'production')
          console.error(`未找到执行结果，请检查`)
      return sqlExecResult?.loading ?? 0
    },
    getSummary: (state: StatisticsPanelStoreState) => () => {
      const sqlExecResult = state.sqlExecResult
      return sqlExecResult ? sqlExecResult.summary : null
    },
    getTableResult: (state: StatisticsPanelStoreState) => () => {
      const sqlExecResult = state.sqlExecResult
      return sqlExecResult ? sqlExecResult.tableResult : null
    },
    getChartResult: (state: StatisticsPanelStoreState) => () => {
      const sqlExecResult = state.sqlExecResult
      return sqlExecResult ? sqlExecResult.chartResult : null
    },
    getExecuteInfo: (state: StatisticsPanelStoreState) => () => {
      const sqlExecResult = state.sqlExecResult
      return sqlExecResult ? sqlExecResult.executeInfo : null
    },
  },
}

export default statisticsPanelStore