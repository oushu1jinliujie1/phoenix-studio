<template>
  <DrawerStepsHeader :step="step" :titles="['基本信息', '选择表', '设置关联']" />
  <DrawerStepsContent>
    <div v-show="step===0">
      <BasicInfoForm
        ref="basicRef"
        :init-already-exist-name-list="[]"
        v-model:basic-form="basicForm"
      />
    </div>
    <div v-show="step===1" style="height: 100%;">
      <ConnectionTableSettings
        ref="tableSettingsRef"
        v-model:select-table-list="selectTableList"
      />
    </div>
    <div v-show="step===2">
      <ConnectionColumnSettings
        ref="columnSettingsRef"
        :tables="selectTableList"
        v-model:column-settings="columnSettings"
      />
    </div>
  </DrawerStepsContent>
  <DrawerStepsFooter
    v-model:step="step"
    :disable-next-step="isDisableNextStepRef"
    :max-step="END_STEP"
    :next-step-tooltip="notFinishMsg">
    <template #option>
      <x-tooltip
        v-if="step===END_STEP"
        :title="notFinishMsg"
        :visible="isDisableNextStepRef?undefined:false">
        <x-button
          :disabled="isDisableNextStepRef"
          type="primary"
          @click="handleConfirm"
        >
          <icon name="worksheet/submit"/>
          确认
        </x-button>
      </x-tooltip>
    </template>
  </DrawerStepsFooter>
</template>

<script lang="ts">
import { defineComponent, computed, ref, reactive, toRefs, watch } from 'vue'
import Icon from '@/components/Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { message } from 'ant-design-vue-3'
import { RuleObject } from 'ant-design-vue/es/form/interface'
import { FinishCheckItem, getIsFinish } from '@/lib/common'
import DrawerStepsHeader from '@/components/DrawerSteps/Header.vue'
import DrawerStepsContent from '@/components/DrawerSteps/Content.vue'
import DrawerStepsFooter from '@/components/DrawerSteps/Footer.vue'
import BasicInfoForm from '@/components/Worksheet/WorksheetSideBar/SearchTable/ConnectionSteps/basicInfoForm.vue'
import ConnectionTableSettings from '@/components/Worksheet/WorksheetSideBar/SearchTable/ConnectionSteps/connectionTableSettings.vue'
import ConnectionColumnSettings from '@/components/Worksheet/WorksheetSideBar/SearchTable/ConnectionSteps/connectionColumnSettings.vue'
import { createSearchTable } from '@/api'

const END_STEP = 2

export default defineComponent({
  name: 'addSearchTable',
  props: {

  },
  components: {
    Icon,
    ...smartUI,
    DrawerStepsHeader,
    DrawerStepsContent,
    DrawerStepsFooter,
    BasicInfoForm,
    ConnectionTableSettings,
    ConnectionColumnSettings
  },
  emits: ['close'],
  setup(props, context) {
    const state = reactive({
      step: 0,
      notFinishMsg: '',
      basicForm: {
        name: '',
        comment: '',
        description: ''
      },
      selectTableList: [] as any[],
      columnSettings: [] as any[]
    })

    const basicRef: any = ref(null)
    const tableSettingsRef: any = ref(null)
    const columnSettingsRef: any = ref(null)

    const isDisableNextStepRef = computed(() => getIsDisableNextStepRef(state, basicRef, tableSettingsRef, columnSettingsRef))

    const tablesComputed = computed(() => state.selectTableList)

    watch(tablesComputed, (tables) => {
      state.columnSettings = []
    })

    /**
     * 提交表单
     */
    const handleConfirm = async() => {

      const resp = await createSearchTable({
        queryName: state.basicForm.name,
        chineseName: state.basicForm.comment,
        description: state.basicForm.description,
        tableNames: state.selectTableList.map(table => {
          return {
            schemaName: table.schema,
            tableName: table.name
          }
        }),
        columns: state.columnSettings.map(column => {
          return {
            columnName: column.name,
            dataType: column.type
          }
        })
      })

      if (resp.meta.success) {
        message.success('创建查询表成功')
        context.emit('close', true)
      } else {
        message.error(`创建查询表失败：${resp.meta.message}`)
      }

    }

    return {
      ...toRefs(state),

      END_STEP,
      basicRef,
      tableSettingsRef,
      columnSettingsRef,
      isDisableNextStepRef,

      handleConfirm
    }
  }
})

function getIsDisableNextStepRef(
  state: any,
  basicRef: any,
  tableSettingsRef: any,
  columnSettingsRef: any,
) {
  const checkSteps = [
    [new FinishCheckItem(basicRef, basicRef?.value?.notFinishMsgRef)],
    [new FinishCheckItem(tableSettingsRef, tableSettingsRef?.value?.notFinishMsgRef)],
    [new FinishCheckItem(columnSettingsRef, columnSettingsRef?.value?.notFinishMsgRef)],
  ]
  const data = getIsFinish(checkSteps[state.step])
  state.notFinishMsg = data.msg
  return !data.result
}

</script>

<style lang="scss">

</style>
