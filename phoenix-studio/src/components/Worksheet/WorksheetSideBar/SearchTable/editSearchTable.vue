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
            v-model:basic-form="basicForm"
            :initial-name="searchTable.name"
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
            :tables="initialSelectTableList"
            :table-settings-button="true"
            v-model:column-settings="columnSettings"
            @show-connection-table-settings="() => { connectionTableDrawerVisible = true }"
          />
        </x-tab-pane>
      </x-tabs>
    </x-spin>
    <div class="search-data-filter-form-btn-container">
      <x-tooltip :visible="isDisableRef?undefined:false">
        <template #title>
          <div style="color: #D74472;">{{ notFinishMsg }}</div>
        </template>
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
import { defineComponent, ref, reactive, toRefs, onMounted, computed, PropType } from 'vue'
import Icon from '@/components/Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { message } from 'ant-design-vue-3'
import { RuleObject } from 'ant-design-vue/es/form/interface'
import BasicInfoForm from '@/components/Worksheet/WorksheetSideBar/SearchTable/ConnectionSteps/basicInfoForm.vue'
import ConnectionTableSettings from '@/components/Worksheet/WorksheetSideBar/SearchTable/ConnectionSteps/connectionTableSettings.vue'
import ConnectionColumnSettings from '@/components/Worksheet/WorksheetSideBar/SearchTable/ConnectionSteps/connectionColumnSettings.vue'
import { FinishCheckItem, getIsFinish } from '@/lib/common'
import { editSearchTable, getTableList } from '@/api'

export default defineComponent({
  name: 'editSearchTable',
  props: {
    searchTable: {
      type: Object as PropType<any>,
      default: () => {
        return {}
      }
    },
    execLoading: Boolean
  },
  components: { Icon, ...smartUI, BasicInfoForm, ConnectionTableSettings, ConnectionColumnSettings },
  emits: ['confirm', 'close'],
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
      initialSelectTableList: [] as any[],
      columnSettings: [] as any[],
      connectionTableDrawerVisible: false
    })

    const basicRef: any = ref(null)
    const tableSettingsRef: any = ref(null)
    const columnSettingsRef: any = ref(null)

    const isDisableRef = computed(() => getIsDisableRef(state, basicRef, tableSettingsRef, columnSettingsRef, ['basicRef', 'columnSettingsRef']))
    const isDisableTableRef = computed(() => getIsDisableRef(state, basicRef, tableSettingsRef, columnSettingsRef, ['tableSettingsRef']))

    const handleConfirmTable = () => {

      state.initialSelectTableList = state.selectTableList

      state.connectionTableDrawerVisible = false
    }

    /**
     * 提交表单
     */
    const handleConfirm = async() => {
      const resp = await editSearchTable({
        queryName: state.basicForm.name,
        chineseName: state.basicForm.comment,
        description: state.basicForm.description,
        tableNames: state.initialSelectTableList.map(table => {
          return {
            schemaName: table.schema,
            tableName: table.name
          }
        }),
        connections: state.columnSettings,
      })

      if (resp.meta.success) {
        message.success('编辑查询表成功')
        context.emit('close', true)
      } else {
        message.error(`编辑查询表失败：${resp.meta.message}`)
      }
    }
    const handleCancel = () => {
      context.emit('close')
    }

    onMounted(async() => {
      state.getTableDetailLoading = true
      state.basicForm = {
        name: props.searchTable.name,
        comment: props.searchTable.comment || '',
        description: props.searchTable.description || ''
      }
      for (const str of props.searchTable.tables.split(',')) {
        let result: any = {}
        const resp = await getTableList({
          schemaName: str.split('.')[0],
          tableName: str.split('.')[1],
          offset: 0,
          limit: 1
        })
        if (resp.meta.success) {
          result = (resp.data.data ?? []).map((table: any) => ({
            name: table.TABLE_NAME,
            schema: str.split('.')[0],
            primary_columns: table.PK_COLUMNS.sort((a: any, b: any) => a.ORDINAL_POSITION - b.ORDINAL_POSITION).map((column: any) => {
              return {
                name: column.COLUMN_NAME,
                type: column.DATA_TYPE_NAME,
                schema: column.TABLE_SCHEM,
                table: column.TABLE_NAME,
                column_family: column.COLUMN_FAMILY,
                order: column.ORDINAL_POSITION,
                primary: true
              }
            }),
          }))[0]
        } else {
          message.error(`获取表信息失败：${resp.meta.message}`)
        }
        if (result) state.selectTableList = [...state.selectTableList, result]
      }
      state.initialSelectTableList = state.selectTableList
      state.columnSettings = props.searchTable.connections
      state.getTableDetailLoading = false
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
