<template>
  <div class="search-data-edit-drawer-content">
    <x-spin :spinning="getTableDetailLoading" tip="数据加载中">
      <x-tabs v-model:active-key="tabsActiveKey" class="v-oushudb-add-table-form-container">
        <x-tab-pane key="basicInformation" forceRender>
          <!-- title -->
          <template #tab>
            <div class="v-oushudb-add-or-update-table-tab-title">
              基本信息
            </div>
          </template>
          <BasicInfoForm
            ref="basicRef"
            :init-already-exist-name-list="[]"
            v-model:basic-form="basicForm"
          />
        </x-tab-pane>
        <x-tab-pane key="connectionInformation" forceRender>
          <!-- title -->
          <template #tab>
            <div class="v-oushudb-add-or-update-table-tab-title">
              关联信息
            </div>
          </template>
          <ConnectionColumnSettings
            ref="columnSettingsRef"
            :tables="selectTableList"
            :table-settings-button="true"
            v-model:column-settings="columnSettings"
            @show-connection-table-settings="() => { connectionTableDrawerVisible = true }"
          />
        </x-tab-pane>
      </x-tabs>
    </x-spin>
    <div class="search-data-filter-form-btn-container">
      <x-tooltip
        :title="notFinishMsg"
        :visible="isDisableRef?undefined:false">
        <x-button
          :disabled="isDisableRef"
          :loading="execLoading"
          type="primary"
          @click="handleConfirm"
        >
          <icon name="worksheet/submit"/>
          确认
        </x-button>
      </x-tooltip>
      <x-button @click="handleCancel">
        <icon name="worksheet/cancel"/>
        取消
      </x-button>
    </div>
    <x-drawer
      :visible="connectionTableDrawerVisible"
      class="v-oushudb-add-search-table-drawer"
      destroyOnClose
      title="设置关联表"
      width="1000"
      @close="() => { connectionTableDrawerVisible = false }"
    >
      <div class="search-data-edit-drawer-content">
        <div style="height: 100%;padding-bottom: 60px;">
          <ConnectionTableSettings
            ref="tableSettingsRef"
            v-model:select-table-list="selectTableList"
          />
        </div>
        <div class="search-data-filter-form-btn-container">
          <x-tooltip
            :title="notFinishTableMsg"
            :visible="isDisableTableRef?undefined:false">
            <x-button
              :disabled="isDisableTableRef"
              :loading="execLoading"
              type="primary"
              @click="handleConfirmTable"
            >
              <icon name="worksheet/submit"/>
              确认
            </x-button>
          </x-tooltip>
          <x-button @click="() => { connectionTableDrawerVisible = false }">
            <icon name="worksheet/cancel"/>
            取消
          </x-button>
        </div>
      </div>
    </x-drawer>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, toRefs, onMounted, computed } from 'vue'
import Icon from '@/components/Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { message } from 'ant-design-vue-3'
import { RuleObject } from 'ant-design-vue/es/form/interface'
import BasicInfoForm from '@/components/Worksheet/WorksheetSideBar/SearchTable/ConnectionSteps/basicInfoForm.vue'
import ConnectionTableSettings from '@/components/Worksheet/WorksheetSideBar/SearchTable/ConnectionSteps/connectionTableSettings.vue'
import ConnectionColumnSettings from '@/components/Worksheet/WorksheetSideBar/SearchTable/ConnectionSteps/connectionColumnSettings.vue'
import { FinishCheckItem, getIsFinish } from '@/lib/common'

export default defineComponent({
  name: 'editSearchTable',
  props: {
    execLoading: Boolean
  },
  components: { Icon, ...smartUI, BasicInfoForm, ConnectionTableSettings, ConnectionColumnSettings },
  emits: ['confirm', 'close', 'confirmTable'],
  setup(props, context) {
    const state = reactive({
      // tab key
      tabsActiveKey: 'basicInformation' as 'basicInformation' | 'connectionInformation',

      // 编辑表，请求数据时 loading 状态
      getTableDetailLoading: false,
      basicForm: {
        name: '',
        comment: '',
        description: ''
      },
      notFinishMsg: '',
      notFinishTableMsg: '',
      selectTableList: [] as any[],
      columnSettings: [] as any[],
      connectionTableDrawerVisible: false
    })

    const basicRef: any = ref(null)
    const tableSettingsRef: any = ref(null)
    const columnSettingsRef: any = ref(null)

    const isDisableRef = computed(() => getIsDisableRef(state, basicRef, tableSettingsRef, columnSettingsRef, ['basicRef', 'columnSettingsRef']))
    const isDisableTableRef = computed(() => getIsDisableRef(state, basicRef, tableSettingsRef, columnSettingsRef, ['tableSettingsRef']))

    const handleConfirmTable = () => {
      context.emit('confirmTable', {

      })
      state.connectionTableDrawerVisible = false
    }

    /**
     * 提交表单
     */
    const handleConfirm = () => {
      context.emit('confirm', {

      })
    }
    const handleCancel = () => {
      context.emit('close')
    }

    onMounted(() => {
      state.getTableDetailLoading = true
      setTimeout(() => {
        state.basicForm = {
          name: 'basic',
          comment: 'basic',
          description: 'basic'
        }
        state.selectTableList = [
          { name: 'table1', primary: 'columnP', columns: [{ name: 'columnP' }, { name: 'column1' }, { name: 'column2' }, { name: 'column3' }] },
          { name: 'table2', primary: 'columnP', columns: [{ name: 'columnP' }, { name: 'column1' }, { name: 'column2' }, { name: 'column3' }] }
        ]
        state.columnSettings = [
          { table1: 'columnP', table2: 'columnP' },
          { table1: 'column1', table2: 'column1' },
          { table1: 'column2', table2: 'column2' },
        ]
        state.getTableDetailLoading = false
      })
    })

    return {
      ...toRefs(state),

      basicRef,
      tableSettingsRef,
      columnSettingsRef,
      isDisableRef,
      isDisableTableRef,

      handleConfirmTable,
      handleConfirm,
      handleCancel
    }
  }
})

function getIsDisableRef(
  state: any,
  basicRef: any,
  tableSettingsRef: any,
  columnSettingsRef: any,
  refName: Array<'basicRef' | 'tableSettingsRef' | 'columnSettingsRef'>
) {
  const checkMap = {
    basicRef: new FinishCheckItem(basicRef, basicRef?.value?.notFinishMsgRef),
    tableSettingsRef: new FinishCheckItem(tableSettingsRef, tableSettingsRef?.value?.notFinishMsgRef),
    columnSettingsRef: new FinishCheckItem(columnSettingsRef, columnSettingsRef?.value?.notFinishMsgRef),
  }
  const data = getIsFinish(refName.map((name: 'basicRef' | 'tableSettingsRef' | 'columnSettingsRef') => checkMap[name]))
  if (refName[0] === 'tableSettingsRef') {
    state.notFinishTableMsg = data.msg
  } else {
    state.notFinishMsg = data.msg
  }
  return !data.result
}

</script>

<style lang="scss">
.search-data-edit-drawer-content {
  position: absolute;
  top: 70px;
  right: 40px;
  bottom: 0;
  left: 40px;



  .search-data-filter-form-btn-container {
    position: absolute;
    left: 0;
    bottom: 15px;

    .#{$ant-prefix}-btn:not(:last-child) {
      margin-right: 10px;
    }
    span > .#{$ant-prefix}-btn {
      margin-right: 10px;
    }
  }
}
</style>
