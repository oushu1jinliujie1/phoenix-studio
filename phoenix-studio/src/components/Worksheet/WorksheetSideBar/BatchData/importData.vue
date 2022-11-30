<template>
  <x-spin :spinning="spinLoading" tip="文件加载中">
    <div class="upload-container">
      <a-upload-dragger
        accept=".xlsx,.xls"
        :beforeUpload="beforeUpload"
        :showUploadList="false"
        :file-list="fileList"
      >
        <p class="ant-upload-text">
          <span v-if="fileList.length === 0" style="color: #85888C">
            点击选择或拖入文件
          </span>
          <span v-else>
            {{fileList[0].name}} &nbsp;
            <icon image name="file_remove" @click="removeFile"/>
          </span>
        </p>
      </a-upload-dragger>
    </div>
    <div v-show="tableList.length" class="upload-preview-title">导入表预览</div>
    <div v-show="tableList.length" class="upload-preview-container">
      <div class="upload-preview-tables">
        <div class="upload-preview-tables-header">
          <a-checkbox :checked="allTableChecked" @click="checkAllTable()"></a-checkbox>
          <x-button type="link" @click="checkAllTable()">全选/取消</x-button>
        </div>
        <div
          v-for="(_table, index) in (tableList as any[])" :key="index"
          :class="
            importFromBasic ?
            _table.tableName === currentTable?.tableName ? 'current-select' : '' :
            _table.queryName === currentTable?.queryName ? 'current-select' : ''
          "
          class="upload-preview-table"
          @click="selectCurrentTable(_table)"
        >
          <div class="table-content" :title="importFromBasic ? _table.tableName : _table.queryName">
            <a-checkbox v-model:checked="_table.selected"></a-checkbox>
            <icon v-show="importFromBasic ? _table.tableName !== currentTable?.tableName : _table.queryName !== currentTable?.queryName" color="" name="worksheet/table_line"/>
            <icon v-show="importFromBasic ? _table.tableName === currentTable?.tableName : _table.queryName === currentTable?.queryName" color="" image name="worksheet/table_selected"/>
            {{ importFromBasic ? _table.tableName : _table.queryName }}
          </div>
          <div class="table-content">
            <icon name="next"/>
          </div>
        </div>
      </div>
      <div v-if="importFromBasic" class="upload-preview-table-info">
        <div class="upload-preview-table-info-title">基本信息</div>
        <div class="upload-preview-table-info-item"><div class="info-item-col">
            <div class="info-item-label">基础表名</div>
            <div class="info-item-value">{{ currentTable?.tableName || '' }}</div>
          </div>
          <div class="info-item-col">
            <div class="info-item-label">split_on</div>
            <div class="info-item-value">{{ currentTable?.splitOn || '' }}</div>
          </div>
          <div class="info-item-col">
            <div class="info-item-label">SALT_BUCKETS</div>
            <div class="info-item-value">{{ currentTable?.saltBuckets || 0 }}</div>
          </div>
        </div>
        <div class="upload-preview-table-info-item">
          <div class="info-item-col">
            <div class="info-item-label">所属模式</div>
            <div class="info-item-value">{{ currentTable?.schemaName || '' }}</div>
          </div>
          <div class="info-item-col">
            <div class="info-item-label">表备注</div>
            <div class="info-item-value">{{ currentTable?.comment || '' }}</div>
          </div>
        </div>

        <div class="upload-preview-table-info-title">列信息</div>
        <x-table
          :columns="columns"
          :data-source="currentTable?.columns"
          :divider="true"
          :pagination="false"
          :scroll="{ x: 920, y: null }"
          class="v-oushudb-add-table-form-column-table"
          edit-table
          row-key="name"
        >
          <!-- 列名 -->
          <template #name="{ record }">
            <div>
              <x-tooltip
                :overlayStyle="{ 'pointer-events': 'none' }"
              >
                <template #title>
                  <div>{{ record.columnName }}</div>
                </template>
                <div>{{ record.columnName || '--' }}</div>
              </x-tooltip>
            </div>
          </template>
          <!-- 中文名（备注） -->
          <template #comment="{ record }">
            <div>{{ record.comment || '--' }}</div>
          </template>
          <!-- 类型 -->
          <template #type="{ record }">
            <div>{{ record.dataType || '--' }}</div>
          </template>
          <!-- 列族 -->
          <template #columnFamily="{ record }">
            <x-tooltip
              :overlayStyle="{ 'pointer-events': 'none' }"
            >
              <template #title>
                <div style="text-align: justify;">{{ record.familyName }}</div>
              </template>
              <div>{{ record.familyName || '--' }}</div>
            </x-tooltip>
          </template>
          <!-- 长度 -->
          <template #scale="{ record }">
            <div class="v-oushudb-add-table-basic-info-table-cell">
              <div>{{ record.scale || (record.scale === 0 ? 0 : '--') }}</div>
            </div>
          </template>
          <!-- 精度，仅 decimal 有 -->
          <template #precision="{ record }">
            <div class="v-oushudb-add-table-basic-info-table-cell">
              <div>{{ record.precision || (record.precision === 0 ? 0 : '--') }}</div>
            </div>
          </template>
          <!-- 是否为主键，只有编辑状态 -->
          <template #isPrimary="{ record }">
            <a-checkbox disabled v-model:checked="record.pk"/>
          </template>
        </x-table>
      </div>
      <div v-else class="upload-preview-table-info">
        <div class="upload-preview-table-info-title">基本信息</div>
        <div class="upload-preview-table-info-item">
          <div class="info-item-label">查询表名</div>
          <div class="info-item-value">{{ currentTable?.queryName || '' }}</div>
        </div>
        <div class="upload-preview-table-info-item">
          <div class="info-item-label">查询表中文名</div>
          <div class="info-item-value">{{ currentTable?.chineseName || '' }}</div>
        </div>
        <div class="upload-preview-table-info-item">
          <div class="info-item-label">查询表描述</div>
          <div class="info-item-value">{{ currentTable?.description || '' }}</div>
        </div>
        <div class="upload-preview-table-info-title">关联信息</div>
        <x-table
          :data-source="connectionTableData"
          :pagination="false"
          :scroll="{ x: 920, y: null }"
          :rowKey="connectionTableRowKey"
          auto-calc-empty-height
          emptyImage="empty"
        >
          <a-table-column v-for="table of connectionTables" :key="`schema.${table.schemaName}.table.${table.tableName}`" :title="`${table.schemaName}: ${table.tableName}`" width="120px">
            <template #default="{ record }">
              <span>
                <icon name="worksheet/column" style="margin-right: 5px;"/>
                {{ record[`schema.${table.schemaName}.table.${table.tableName}`] }}
              </span>
            </template>
          </a-table-column>
          <template #emptyDescription>
            暂无关联
          </template>
        </x-table>
      </div>
    </div>
    <div v-show="!spinLoading && file && !tableList.length" class="oushudb-worksheet-execute-result-empty">
      <icon image name="empty"/>
      <p v-if="importFromBasic">当前excel文件中没有读取到基础表哦, 请重新选择需要导入的基础表文件</p>
      <p v-else>当前excel文件中没有读取到查询表哦, 请重新选择需要导入的查询表文件</p>
    </div>
    <!-- 提交、取消按钮组 -->
    <div class="upload-table-btn-container">
      <x-button :disabled="!tableList.length || spinLoading" type="primary" @click="handleSubmit">
        <icon name="worksheet/submit"/>
        提交
      </x-button>
      <x-button style="margin-left: 10px;" @click="handleCancel">
        <icon color="inherit" name="worksheet/cancel"/>
        取消
      </x-button>
    </div>
  </x-spin>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, onUnmounted, PropType, reactive, ref, toRefs, watch } from 'vue'
import XSpin from '@/smart-ui-vue/XSpin.vue'
import XButton from '@/smart-ui-vue/XButton.vue'
import XTable from '@/smart-ui-vue/XTable.vue'
import Icon from '@/smart-ui-vue/helper/Icon.vue'
import { parseBasicTableFromExcel, parseSearchTableFromExcel } from '@/lib/common'
import { message } from 'ant-design-vue-3'
import { COLUMNS } from '../constant'
import { uploadBasicTable, uploadSearchTable } from '@/api'


export default defineComponent({
  name: 'ImportData',
  components: {
    XSpin,
    XButton,
    XTable,
    Icon,
  },
  props: {
    importFromBasic: {
      type: Boolean,
      default: false
    },
    schemaName: {
      type: String,
      default: '',
    },
  },
  setup(props: any, context: any) {
    const state = reactive({
      file: null as any,
      fileList: [] as any[],
      spinLoading: false,
      tableList: [] as any[],
      currentTable: null as any,
      connectionTableList: [] as any[],
    })

    const selectedTables = computed(() => {
      return state.tableList.filter((table: any) => table.selected)
    })

    const allTableChecked = computed(() => {
      return selectedTables.value.length === state.tableList.length
    })

    const checkAllTable = () => {
      if (allTableChecked.value) {
        state.tableList.forEach((item: any) => {
          item.selected = false
        })
        return
      }
      state.tableList.forEach((item: any) => {
        item.selected = true
      })
    }

    const beforeUpload = (file: Blob) => {
      if (file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || file.type === 'application/vnd.ms-excel') {
        const reader = new FileReader()
        state.spinLoading = true
        reader.onload = (() => {
          if (reader.result) {
            if (props.importFromBasic) { 
              state.tableList = parseBasicTableFromExcel(props.schemaName, reader.result)
            } else {
              const res = parseSearchTableFromExcel(reader.result)
              state.tableList = res.searchTableList
              state.connectionTableList = res.connectionTableList
            }
            state.tableList.forEach((item: any) => {
              item.selected = true
            })
            state.currentTable = state.tableList[0] || null
            state.spinLoading = false
          } else {
            message.error({
              content: '解析出错,请检查文件内容是否正确',
              duration: 3,
            })
            state.spinLoading = false
            state.fileList = []
            state.file = null
          }
        })
        reader.readAsArrayBuffer(file)
        state.fileList = [file]
        state.file = file
      } else {
        message.error({
          content: '请导入文件格式为xlsx或xls的数据！',
          duration: 3,
        })
      }
      return false
    }

    const removeFile = (e: any) => {
      e.stopPropagation()
      state.fileList = []
      state.file = null
      state.currentTable = null
      state.tableList = []
    }

    const selectCurrentTable = (table: any) => {
      state.currentTable = table
    }

    const handleSubmit = async() => {
      if (!state.file) return

      if (!selectedTables.value.length) {
        message.success('请勾选需要导入的表')
        return
      }

      const importFunc = props.importFromBasic ? uploadBasicTable : uploadSearchTable

      const importRes = await importFunc(selectedTables.value)

      if (importRes.meta.success) {
        // 创建表成功
        message.success('导入成功')
        context.emit('close', true)
      } else {
        message.error(`导入失败: ${importRes.meta?.message || '无失败提示'}`, 5)
      }
    }

    const handleCancel = () => {
      //
      context.emit('close')
    }

    const connectionTableData = computed(() => {
      if (props.importFromBasic) return []
      if (!state.currentTable) return []
      let res: any[] = []
      res = state.connectionTableList.find((item: any) => item.queryName === state.currentTable.queryName)?.connectionColumns
      return res
    })

    const connectionTables = computed(() => {
      if (props.importFromBasic) return []
      if (!state.currentTable) return []
      return state.currentTable.tableNames
    })

    const connectionTableRowKey = computed(() => {
      if (!connectionTables.value.length) return ''
      return `schema.${connectionTables.value[0].schemaName}.table.${connectionTables.value[0].tableName}`
    })

    return {
      ...toRefs(state),
      allTableChecked,
      checkAllTable,
      beforeUpload,
      removeFile,
      selectCurrentTable,
      columns: COLUMNS.slice(0, -1),
      handleSubmit,
      handleCancel,
      connectionTableData,
      connectionTableRowKey,
      connectionTables,
    }
  }
})
</script>

<style lang="scss">
.antv-drawer-body {
  height: calc(100% - 74px);
  .antv-spin-nested-loading {
    height: 100%;
    .antv-spin-container {
      height: 100%;
      position: relative;
      .upload-container {
        height: 80px;
      }
      .upload-preview-title {
        height: 40px;
        line-height: 20px;
        font-size: 14px;
        font-weight: 600;
        color: #282B2E;
        padding: 10px 0;
      }
      .upload-preview-container {
        display: flex;
        height: calc(100% - 192px);
        border: 1px solid #D5D8D8;
        border-radius: 4px;
        .upload-preview-tables {
          position: relative;
          flex-shrink: 0;
          width: 178px;
          height: 100%;
          padding: 30px 0 10px;
          overflow: auto;
          border-right: 1px solid #D5D8D8;
          .upload-preview-tables-header {
            position: absolute;
            top: 0;
            left: 0;
            display: flex;
            align-items: center;
            height: 30px;
            width: 100%;
            padding-left: 10px;
            border-bottom: 1px solid #D5D8D8;
            .x-btn:not(.raw).antv-btn {
              padding-left: 5px;
            }
          }
          .upload-preview-table {
            display: flex;
            align-items: center;
            justify-content: space-between;
            white-space: nowrap;
            @include font-normal();
            border: 1px solid transparent;
            border-radius: 4px;
            margin: 0 10px;
            height: 32px;
            cursor: pointer;
            > .table-content:nth-of-type(1) {
              flex-grow: 1;
              width: 0;
              overflow: hidden;
              text-overflow: ellipsis;
              .icon {
                margin-left: 5px;
                margin-right: 2px;
              }
            }
            > .table-content:nth-of-type(2) {
              opacity: 0;
            }
            &:hover,
            &:focus {
              background: rgba(51, 108, 255, 0.2);
            }
            &:hover,
            &:focus,
            &.current-select {
              border-radius: 4px;
              color: #336CFF;

              > .table-content:nth-of-type(2) {
                opacity: 1;
              }
            }
            &.table-disabled {
              background: none;
              color: #282B2E;

              &:hover {
                border: 1px solid #D74472;
              }
            }
          }
        }
        .upload-preview-table-info {
          flex-grow: 1;
          width: 0;
          height: 100%;
          padding: 10px;
          overflow: auto;
          .upload-preview-table-info-title {
            display: flex;
            align-items: center;
            font-size: 13px;
            line-height: 20px;
            font-weight: 600;
            border: 1px solid transparent;
            border-radius: 4px;
            height: 32px;
            margin-bottom: 4px;
          }
          .upload-preview-table-info-item {
            display: flex;
            align-items: center;
            font-size: 12px;
            line-height: 20px;
            font-weight: 400;
            height: 20px;
            margin-bottom: 10px;
            padding-left: 10px;
            .info-item-col {
              display: flex;
              align-items: center;
              width: 33%;
              padding-right: 20px;
            }
            .info-item-label {
              color: #85888C;
              width: 100px;
              flex-shrink: 0;
            }
            .info-item-value {
              flex-grow: 1;
              width: 0;
            }
          }
        }
      }
      .oushudb-worksheet-execute-result-empty {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: calc(100% - 162px);
        @include font-normal();
        color: $color-primary-black;
        text-align: center;

        // 空状态的 icon
        > .icon {
          width: 180px;
          height: 160px;
        }
      }
      .upload-table-btn-container {
        position: absolute;
        right: 0;
        bottom: 0;
        z-index: 1;
        width: 100%;
        padding: 20px 0;
      }
    }
  }
}
</style>