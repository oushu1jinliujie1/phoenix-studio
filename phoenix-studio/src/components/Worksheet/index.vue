<template>
  <div class="worksheet">
    <!-- worksheet 主要部分 -->
    <div class="worksheet-main">
      <splitpanes
          style="border-bottom-left-radius: 4px;border-bottom-right-radius: 4px;"
          @resize="handleMainPanesResize($event)"
          @resized="handleMainPanesResized()"
      >
        <pane
            :max-size="`${sidebarWidthRatioMax}`"
            :size="sidebarWidthRatio"
            style="overflow:hidden; border-bottom-left-radius: 4px;"
        >
          <!-- 左侧 -->
          <worksheet-side-bar />
        </pane>
        <pane :size="100-sidebarWidthRatio" style="border-bottom-right-radius: 4px;">
          <!-- 右侧 -->
          <div class="worksheet-main-right">
            <splitpanes
                :style="{height: `100%`} "
                horizontal
                @resize="handleRightPanesResize($event)"
                @resized="handleRightPanesResized()"
            >
              <pane :size="editorHeightRatio">
                <!-- 右上 -->
                <div class="worksheet-main-right-top">
                  <!-- 编辑器上的 header -->
                  <div class="worksheet-main-right-top-header">
                    <div style="display: inline-flex; align-items: center;">
                      <!-- 执行按钮 -->
                      <x-tooltip placement="bottomLeft" title="执行">
                        <x-button
                            :disabled="isExecuting"
                            class="worksheet-header-right-execute"
                            data-test-id="oushudb-worksheet-execute-btn"
                            icon-name="worksheet/execute"
                            type="text"
                            @click="handleExecute"
                        />
                      </x-tooltip>
                      <span class="worksheet-header-right-query">
                        <span style="font-size: 14px">最大查询</span>
                        <x-select
                          v-model:value="queryValue"
                          :bordered-normal="false"
                          :dropdownMatchSelectWidth="false"
                          :options="queryOptions"
                          data-test-id="oushudb-worksheet-query-select"
                          style="margin-right: 20px; font-size: 14px;"
                        />
                      </span>
                    </div>
                  </div>
                  <editor />
                </div>
              </pane>
              <pane :size="100-editorHeightRatio">
                <!-- 右下 -->
                <div class="worksheet-main-right-bottom">
                  <statistics-panel />
                </div>
              </pane>
            </splitpanes>
          </div>
        </pane>
      </splitpanes>
    </div>
  </div>
</template>
<script lang="ts">
import {
  computed,
  ComputedRef,
  defineComponent,
  nextTick,
  onBeforeMount,
  onMounted,
  onUnmounted,
  reactive,
  toRefs,
  watch,
} from 'vue'
import { mockData } from '@/api/mock'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { useStore } from 'vuex'
import dayjs from 'dayjs'
// @ts-ignore
import { Pane, Splitpanes } from 'splitpanes'
import 'splitpanes/dist/splitpanes.css'
import { message } from 'ant-design-vue-3'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { translateErrorMessage } from 'lava-fe-lib/lib-common/i18n'
import { debounce } from 'lodash'
import WorksheetSideBar from './WorksheetSideBar/index.vue'
import Editor from './Editor/index.vue'
import StatisticsPanel from '@/components/Worksheet/StatisticsPanel/StatisticsPanel.vue'

/* eslint-disable camelcase */

const ONE_TWO_THREE = {
  key: 'ONE_TWO_THREE',
  iconPath: 'worksheet/arrangement_one_two_three',
}
const ONE_TWO = {
  key: 'ONE_TWO',
  iconPath: 'worksheet/arrangement_one_two',
}
const ONE_THREE = {
  key: 'ONE_THREE',
  iconPath: 'worksheet/arrangement_one_three',
}
const TWO_THREE = {
  key: 'TWO_THREE',
  iconPath: 'worksheet/arrangement_two_three',
}
const TWO = {
  key: 'TWO',
  iconPath: 'worksheet/arrangement_two',
}
const THREE = {
  key: 'THREE',
  iconPath: 'worksheet/arrangement_three',
}

export default defineComponent({
  name: 'Worksheet',
  components: {
    WorksheetSideBar,
    Editor,
    StatisticsPanel,
    Splitpanes, Pane,
    ...smartUI,
    // eslint-disable-next-line vue/no-unused-components
    VNodes: (_, { attrs }) => {
      return attrs.vnodes
    },
  },
  props: {
  },
  setup(props) {
    const { t } = useI18n()
    const getError = translateErrorMessage(t)
    const store = useStore()
    const router = useRouter()
    const state = reactive({
      // sqlValue: '',
      arrangement: {
        iconList: [
          ONE_TWO_THREE,
          ONE_TWO,
          ONE_THREE,
          TWO_THREE,
          TWO,
          THREE
        ],
        // index of iconList
        activeKey: ONE_TWO_THREE.key,
      },
      queryValue: 200,
      queryOptions: [
        {
          label: '200',
          value: 200,
        },
        {
          label: '500',
          value: 500,
        },
        {
          label: '1000',
          value: 1000,
        },
        {
          label: '2000',
          value: 2000,
        },
        {
          label: '5000',
          value: 5000,
        },
      ],

      screenWidth: 0,
      firstFlag: true,
      // 侧边栏宽带占比百分比
      sidebarWidthRatio: 0,
      // 侧边栏宽带占比最大百分比
      sidebarWidthRatioMax: 90,
      // 侧边栏 collapsed 后的标志，用于 collapse 后第一次打开时的行为交互
      sidebarCollapsedFlag: false,
      // 编辑器高度占比百分比
      editorHeightRatio: 50,
      // worksheet 宽度，单位：px
      worksheetElementClientWidth: 0,

      // 实例
      instanceListLoading: false,
      instanceList: [] as any[],
      instanceSelectedId: undefined as number | undefined,

      // vc
      vcListLoading: false,
      vcList: [] as any[],
      vcSelectedId: undefined as number | undefined,
      vcNameInitial: undefined as string | undefined,

      // 是否正在执行
      isExecuting: false,

      // 检查实例状态中
      checkInstanceLoading: true,
      hasInstanceCanRun: false,
    })

    /**
     * 执行SQL
     */
    const handleExecute = async() => {
      state.isExecuting = true

      if (!await checkInstanceAndVCStatus()) {
        // 检查不通过
        message.error('操作失败！请检查实例以及虚拟计算集群状态！')
        state.isExecuting = false
        return
      }

      /**
       * 可能选中的时候还在执行，但当前状态已经拜年了
       * 所以在执行 SQL 前，再判断一遍实例和VC状态
       */
      async function checkInstanceAndVCStatus(): Promise<boolean> {

        try {
          const res = await mockData()

          if (res.every((item: any) => item.meta.success)) {
            //
          } else return false
        } catch (e) {
          return false
        }

        return true
      }

      try {
        const uniqueKey = dayjs().valueOf().toString()
        // 执行 SQL
        const result = await mockData()
        if (result.meta.success) {
          //
        } else {
          message.error(`执行SQL失败：${getError(result)}`)
        }
      } catch (e) {
        message.error(`执行SQL失败：${e}`)
      }

      // state.isExecuting = false
    }

    /**
     * 若 sidebar pane 上次的状态为 collapse，则本次拉长，在未达到阈值，此时收缩不触发自动 collapse
     * 其它状态下，收缩达到阈值时，会自动 collapse
     * @param event
     */
    const handleMainPanesResize = (event: any) => {
      // shrinking
      const THRESHOLD_VALUE = 300
      const sidebarWidth = state.worksheetElementClientWidth * state.sidebarWidthRatio / 100
      if (event[0].size < state.sidebarWidthRatio && sidebarWidth < THRESHOLD_VALUE && !state.sidebarCollapsedFlag) {
        state.sidebarWidthRatio = 0
        state.sidebarWidthRatioMax = 0
      } else {
        state.sidebarWidthRatio = event[0].size
        // 此时宽度已大于阈值，此时收缩至小于阈值时，会触发自动 collapse
        if (sidebarWidth >= THRESHOLD_VALUE) {
          state.sidebarCollapsedFlag = false
        }
      }
    }

    const handleMainPanesResized = () => {
      // 上一次拉拽出发了自动 collapse
      if (state.sidebarWidthRatioMax === 0) {
        state.sidebarCollapsedFlag = true
        state.sidebarWidthRatioMax = 90
      }
      const sidebarWidthRatio = state.sidebarWidthRatio
      const editorHeightRatio = state.editorHeightRatio

      if (sidebarWidthRatio === 0) {
        if (editorHeightRatio === 0) {
          state.arrangement.activeKey = THREE.key
        } else if (editorHeightRatio === 100) {
          state.arrangement.activeKey = TWO.key
        } else {
          state.arrangement.activeKey = TWO_THREE.key
        }
      } else {
        if (editorHeightRatio === 0) {
          state.arrangement.activeKey = ONE_THREE.key
        } else if (editorHeightRatio === 100) {
          state.arrangement.activeKey = ONE_TWO.key
        } else {
          state.arrangement.activeKey = ONE_TWO_THREE.key
        }
      }
    }

    /**
     *拖拽中
     * @param event
     */
    const handleRightPanesResize = (event: any) => {
      state.editorHeightRatio = event[0].size
    }

    /**
     * 拖拽完
     */
    const handleRightPanesResized = () => {
      const sidebarWidthRatio = state.sidebarWidthRatio
      const editorHeightRatio = state.editorHeightRatio

      if (sidebarWidthRatio === 0) {
        if (editorHeightRatio === 0) {
          state.arrangement.activeKey = THREE.key
        } else if (editorHeightRatio === 100) {
          state.arrangement.activeKey = TWO.key
        } else {
          state.arrangement.activeKey = TWO_THREE.key
        }
      } else {
        if (editorHeightRatio === 0) {
          state.arrangement.activeKey = ONE_THREE.key
        } else if (editorHeightRatio === 100) {
          state.arrangement.activeKey = ONE_TWO.key
        } else {
          state.arrangement.activeKey = ONE_TWO_THREE.key
        }
      }
    }

    /**
     * 布局按钮点击
     * @param key
     */
    const handleLayoutIconClick = (key: string) => {
      const SIDEBAR_MIN_WIDTH = 360
      const worksheetElementClientWidth = state.worksheetElementClientWidth
      state.arrangement.activeKey = key
      switch (key) {
        case 'ONE_TWO_THREE': {
          state.sidebarWidthRatio = SIDEBAR_MIN_WIDTH / worksheetElementClientWidth * 100
          state.editorHeightRatio = 50
          break
        }
        case 'ONE_TWO': {
          state.sidebarWidthRatio = SIDEBAR_MIN_WIDTH / worksheetElementClientWidth * 100
          state.editorHeightRatio = 100
          break
        }
        case 'ONE_THREE': {
          state.sidebarWidthRatio = SIDEBAR_MIN_WIDTH / worksheetElementClientWidth * 100
          state.editorHeightRatio = 0
          break
        }
        case 'TWO_THREE': {
          state.sidebarWidthRatio = 0
          state.sidebarCollapsedFlag = true
          state.editorHeightRatio = 50
          break
        }
        case 'TWO': {
          state.sidebarWidthRatio = 0
          state.sidebarCollapsedFlag = true
          state.editorHeightRatio = 100
          break
        }
        case 'THREE': {
          state.sidebarWidthRatio = 0
          state.sidebarCollapsedFlag = true
          state.editorHeightRatio = 0
          break
        }
        default:
          // do nothing
      }
    }

    const handleSaveWorksheetInfo = () => {
      console.log('save')
    }

    const handleResize = () => {
      nextTick(() => {
        const SIDEBAR_MIN_WIDTH = 360
        state.worksheetElementClientWidth = document.getElementsByClassName('worksheet')[0].clientWidth
        state.sidebarWidthRatio = SIDEBAR_MIN_WIDTH / state.worksheetElementClientWidth * 100
        nextTick(() => {
          handleRightPanesResized()
          handleMainPanesResized()
        })
      })
    }

    const handleResizeWithDebounce = debounce(handleResize, 300)

    /**
     * 获取工作簿基本信息
     */
    const handleInitWorksheet = async() => {
      try {
        const result = await mockData()

        if (result.meta.success) {

          // 仅新建的 worksheet 其 instance_id 为 0，此时无任何有效信息
          if (result.data?.sheet?.instance_id === -1) {
            // store存获取到的工作簿
          } else {
            // store存获取到的工作簿

            // 提供数据供可视化组件使用
            // store.commit('statisticsPanelStore/setSummary', {
            //   worksheetId: props.worksheetId,
            //   summary: {
            //     ...result.data.history,
            //   },
            // })
            // store.commit('statisticsPanelStore/setTableResult', {
            //   worksheetId: props.worksheetId,
            //   tableResult: tableResult,
            // })
          }
        }
      } catch (e) {
        message.error(`工作簿信息获取失败：${e}`)
      }
    }

    onBeforeMount(async() => {
      // 初始化 worksheet
      await handleInitWorksheet()
    })

    /**
     * 挂载时
     * 1. 获取工作簿原始信息
     * 2. 信息 commit 到 vuex，通知其它组件进行相应操作
     */
    onMounted(() => {
      handleResize()
      window.addEventListener('resize', handleResizeWithDebounce)

      // 初始化 实例 vc 选择
      // await handleGetInstanceList()
    })

    /**
     * 卸载时：
     * 1. 自动保存工作簿信息
     * 2. 清除 vuex 中信息
     */
    onUnmounted(() => {
      window.removeEventListener('resize', handleResizeWithDebounce)

      handleSaveWorksheetInfo()

      // store.commit('worksheet/disposeWorksheet', {
      //   worksheetId: props.worksheetId,
      // })
    })

    return {

      ...toRefs(state),
      handleExecute,
      handleMainPanesResize,
      handleMainPanesResized,
      handleRightPanesResize,
      handleRightPanesResized,
      handleLayoutIconClick,
    }
  },
})
</script>
<style lang="scss">
p {
  margin-bottom: 0;
}

.worksheet {
  height: 100%;
  max-height: 100%;
  color: $color-primary-black;
  background: linear-gradient(0deg, #F1F1F1 57.89%, #FFFFFF 92.8%, #FFFFFF 95.08%);
  padding: 14px 20px 10px;

  .worksheet-no-instance {
    height: 100%;

    .worksheet-no-instance-tooltip {
      margin-top: 4px;
      margin-bottom: 10px;
      display: flex;
      justify-content: center;
      position: relative;

      > p {
        background-color: $color-warn-light;
        color: $color-warn;
        @include font-normal;
        padding: 4px 8px;
        border-radius: 2px;

        > span {
          cursor: pointer;
          color: $color-primary-blue;
        }
      }

      .history-moudle {
        position: absolute;
        right: 58px;
        top: 1px
      }
    }

    .editor-component-container {
      height: calc(100% - 48px);
    }
  }
}

.worksheet-main {
  display: flex;
  height: 100%;
  border: 1px solid rgba($color-line-bold, .5);
  border-radius: 4px;
  background: #FFFFFF;

  .worksheet-main-right {
    flex: 1;
    height: 100%;

    .worksheet-main-right-top {
      height: 100%;
      //padding: 10px 0;
      padding-top: 4.6px;
      //padding-bottom: 10px;
      //border-bottom: 1px solid $color-text-comment;

      .worksheet-main-right-top-header {
        display: flex;
        align-items: center;
        justify-content: space-between;

        padding-right: 20px;
        padding-bottom: 5px;
        padding-left: 20px;

        .worksheet-header-right-execute {
          width: 38px;
          height: 38px;
          vertical-align: bottom;

          > .icon {
            width: 30px;
            height: 30px;
          }
        }

        .worksheet-header-right-query {
          margin-left: 16px;
          @include font-small();

          .#{$ant-prefix}-select {
            margin-left: 5px;
          }
        }
      }
    }

    .worksheet-main-right-bottom {
      height: 100%;
    }
  }
}

.splitpanes {
  background-color: #FFF;
}

.splitpanes__splitter {
  position: relative;
  background-color: $color-text-comment;
}

.splitpanes__splitter:before {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
  content: '';
  background-color: transparent;
  opacity: 0;
  transition: opacity 0.4s;
}

.splitpanes__splitter:hover:before {
  opacity: 1;
}

.splitpanes--vertical > .splitpanes__splitter:before {
  right: -10px;
  left: -10px;
  height: 100%;
}

.splitpanes--horizontal > .splitpanes__splitter:before {
  top: -10px;
  bottom: -10px;
  width: 100%;
}

.worksheet-header-instance-select-dropdown {
  .#{$ant-prefix}-select-item-option-content {
    display: flex;
    align-items: center;
    justify-content: space-between;

    > p {
      margin-right: 15px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: normal;
    }
  }
}

.worksheet-vc-select-not-found-content {
  display: flex;
  flex-direction: column;
  align-items: center;

  > span {
    @include font-small();
    margin: 20px 0 40px;
  }

  .#{$ant-prefix}-btn {
    width: 200px;
  }
}

.worksheet-header-instance-select,
.worksheet-header-vc-select {
  .#{$ant-prefix}-select-selection-item,
  .#{$ant-prefix}-select-selection-placeholder {
    @include font-normal()
  }

  .#{$ant-prefix}-select-selection-item {
    font-weight: 600;
  }

  .#{$ant-prefix}-select-selection-placeholder {

  }
}
</style>
