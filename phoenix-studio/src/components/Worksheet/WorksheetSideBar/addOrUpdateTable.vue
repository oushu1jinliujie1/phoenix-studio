<template>
  <div class="v-oushudb-add-or-update-table">
    <!-- 设计目前要求 tab 变换不需要有渐变效果 -->
    <x-spin :spinning="getTableDetailLoading" tip="数据加载中">
        <div class="v-oushudb-add-table-form-container">
          <!-- 表名 -->
          <div class="v-oushudb-add-table-form-container-item">
            <div class="v-oushudb-add-table-form-container-item-label">
              表名
            </div>
            <div class="v-oushudb-add-table-form-container-item-control" style="position: relative">
              <x-input v-if="isAdd" v-model:value="table.name" :rules="tableNameRules" placeholder="请输入表名">
                <template #prefix>
                  <icon class="hover-translateY-2" image name="worksheet/table_info_name_two_color"/>
                </template>
                <x-tooltip placement="bottomLeft" title="复制">
                  <icon
                    class="hover-translateY-2" color="primary"
                    name="worksheet/copy"
                    style="margin-right: 15px; cursor: pointer;"
                    @click="() => handleCopyValue(table.name)"
                  />
                </x-tooltip>
              </x-input>
              <div style="padding: 15px 0;" v-else>{{ table.name || '--' }}</div>
            </div>
          </div>
          <!-- 表备注 -->
          <div class="v-oushudb-add-table-form-container-item">
            <div class="v-oushudb-add-table-form-container-item-label">表备注（选填）</div>
            <div class="v-oushudb-add-table-form-container-item-control">
              <a-textarea v-if="isAdd" v-model:value="table.comment" auto-size placeholder="请输入备注"/>
              <!-- custom prefix icon -->
              <icon
                v-if="isAdd"
                class="v-oushudb-add-table-form-container-item-control-prefix-icon"
                image
                name="worksheet/table_info_comment_two_color"
              />
              <div style="padding: 15px 0;" v-else>{{ table.comment || '--' }}</div>
            </div>
          </div>
          <!-- split on -->
          <div v-if="isAdd" class="v-oushudb-add-table-form-container-item">
            <div class="v-oushudb-add-table-form-container-item-label">
              split on
            </div>
            <div class="v-oushudb-add-table-form-container-item-control" style="position: relative">
              <x-input v-if="isAdd" v-model:value="table.split_on" :rules="tableSplitRules" placeholder="请输入分隔，以逗号区分">
                <template #prefix>
                  <icon class="hover-translateY-2" image name="worksheet/table_info_comment_two_color"/>
                </template>
                <x-tooltip placement="bottomLeft" title="复制">
                  <icon
                    class="hover-translateY-2" color="primary"
                    name="worksheet/copy"
                    style="margin-right: 15px; cursor: pointer;"
                    @click="() => handleCopyValue(table.split_on)"
                  />
                </x-tooltip>
              </x-input>
              <div style="padding: 15px 0;" v-else>{{ table.split_on || '--' }}</div>
            </div>
          </div>
          <!-- SALT_BUCKETS -->
          <div class="v-oushudb-add-table-form-container-item">
            <div class="v-oushudb-add-table-form-container-item-label" :style="isAdd ? 'padding-top: 15px;' : ''">
              SALT_BUCKETS
            </div>
            <div class="v-oushudb-add-table-form-container-item-control" style="position: relative">
              <div v-if="isAdd" class="v-oushudb-add-table-form-container-item-number">
                <x-input-number
                  placeholder="0-256"
                  v-model:value="table.salt_buckets"
                  :max="256"
                  :min="0"
                />
              </div>
              <div style="padding: 15px 0;" v-else>{{ table.salt_buckets || '--' }}</div>
            </div>
          </div>
          <!-- 列信息 -->
          <div class="v-oushudb-add-table-form-container-item" style="margin-top: 20px;margin-bottom: 20px;">
            <div class="v-oushudb-add-table-form-container-item-label">列信息</div>
            <div v-if="isAdd" class="v-oushudb-add-table-form-container-item-control">
              <x-button type="primary" @click="handleAddColumnWithDebounce">
                <icon color="white" name="worksheet/column_add"/>
                新建列
              </x-button>
            </div>
          </div>
          <!-- 列信息表格 -->
          <x-table
            :columns="columns"
            :data-source="table.columnList"
            :divider="true"
            :pagination="false"
            :scroll="{ x: 920, y: 600 }"
            class="v-oushudb-add-table-form-column-table"
            edit-table
            row-key="id"
          >
            <!-- 列名 -->
            <template #name="{ record }">
              <!-- 根据 hover 控制 display 来切换编辑状态 -->
              <icon
                v-if="isAdd"
                class="v-oushudb-add-table-form-column-table-handle"
                color="primary"
                name="worksheet/sort"
                style="cursor: grab; margin-right: 8px;"/>
              <div>
                <x-tooltip
                  :visible="validateColumnName(record.name)?undefined:false"
                  :overlayStyle="{ 'pointer-events': 'none' }"
                >
                  <template #title>
                    <div style="color: #D74472;">{{ validateColumnName(record.name) }}</div>
                  </template>
                  <x-input
                    v-if="isAdd"
                    v-model:value="record.name"
                    :rules="columnNameRules"
                    no-underline
                    origin-form
                    placeholder="请输入列名"
                  />
                  <div v-if="!isAdd">{{ record.name || '--' }}</div>
                </x-tooltip>
              </div>
            </template>
            <!-- 中文名（备注） -->
            <template #comment="{ record }">
              <!-- 根据 hover 控制 display 来切换编辑状态 -->
              <div>
                <x-input
                  v-if="isAdd"
                  v-model:value="record.comment"
                  no-underline
                  origin-form
                  placeholder="输入中文名"
                />
                <div v-if="!isAdd">{{ record.comment || '--' }}</div>
              </div>
            </template>
            <!-- 类型 -->
            <template #type="{ record }">
              <!-- 仅有编辑状态 -->
              <x-select
                v-if="isAdd"
                v-model:value="record.type"
                :options="TYPE_OPTION_LIST"
                class="raw"
                @change="(value: any) => handleColumnTypeChange(value, record)"
              >
              </x-select>
              <div v-if="!isAdd">{{ record.type || '--' }}</div>
            </template>
            <!-- 列族 -->
            <template #columnFamily="{ record }">
              <!-- 根据 hover 控制 display 来切换编辑状态 -->
              <x-tooltip
                v-if="!record.isPrimary"
                :visible="validateColumnFamily(record.columnFamily)?undefined:false"
                :overlayStyle="{ 'pointer-events': 'none' }"
              >
                <template #title>
                  <div style="color: #D74472;text-align: justify;">{{ validateColumnFamily(record.columnFamily) }}</div>
                </template>
                <x-input
                  v-if="isAdd"
                  v-model:value="record.columnFamily"
                  :rules="columnFamilyRules"
                  no-underline
                  origin-form
                  placeholder="--"
                />
                <div v-if="!isAdd">{{ record.columnFamily || '--' }}</div>
              </x-tooltip>
              <div v-else>
                --
              </div>
            </template>
            <!-- 长度 -->
            <template #scale="{ record }">
              <!-- 根据 hover 控制 display 来切换编辑状态 -->
              <div v-if="TYPE_WITH_SCALE.includes(record.type)"
                   class="v-oushudb-add-table-basic-info-table-cell">
                <x-input-number
                  v-if="isAdd"
                  v-model:value="record.scale"
                  :max="record.type === 'DECIMAL' ? 38 : Infinity"
                  :min="TYPE_REQUIRED_SCALE.includes(record.type) ? 1 : 0"
                  placeholder="--"
                  class="raw"/>
                <div v-if="!isAdd">{{ record.scale || '--' }}</div>
              </div>
              <div v-else>
                --
              </div>
            </template>
            <!-- 精度，仅 decimal 有 -->
            <template #precision="{ record }">
              <!-- 根据 hover 控制 display 来切换编辑状态 -->
              <div v-if="TYPE_WITH_PRECISION.includes(record.type)" class="v-oushudb-add-table-basic-info-table-cell">
                <x-input-number v-if="isAdd" v-model:value="record.precision" :max="Math.max(record.scale - 1, 0)" :min='0' placeholder="--" class="raw"/>
                <div v-if="!isAdd">{{ record.precision || '--' }}</div>
              </div>
              <div v-else>
                --
              </div>
            </template>
            <!-- 是否为主键，只有编辑状态 -->
            <template #isPrimary="{ record }">
              <x-tooltip
                title="主键列之间必须相邻，且会在表中置顶"
              >
                <a-checkbox :disabled="!isAdd" v-model:checked="record.isPrimary" @change="(event: any) => columnSelectChange(event, record)"/>
              </x-tooltip>
            </template>
            <!-- 操作区域 -->
            <template v-if="isAdd" #action="{ record }">
              <x-tooltip placement="topLeft" title="编辑">
                <x-button
                  icon-name="worksheet/edit"
                  style="margin-right: 4px"
                  type="text"
                  @click="() => handleEditColumn(record)"
                />
              </x-tooltip>
              <a-popconfirm
                :visible="record.columnDeletePopConfirmVisible"
                cancel-text="取消"
                ok-text="确认"
                title="该列为主键列，确认删除？"
                @cancel="() => record.columnDeletePopConfirmVisible = false"
                @confirm="() => handleDeleteColumn(record)"
                @visibleChange="(visible: any) => handleDeleteVisibleChange(visible, record)"
              >
                <x-tooltip placement="topLeft" title="删除">
                  <x-button icon-name="worksheet/transverse_line" type="text"/>
                </x-tooltip>
              </a-popconfirm>
            </template>
          </x-table>
        </div>
    </x-spin>
    <!-- SQL 详情挂载 -->
    <div v-if="isAdd" class="v-oushudb-add-table-form-sql-detail">
      <div class="v-oushudb-add-table-form-sql-detail-header">
        <span>SQL详情</span>
        <!-- 复制、刷新按钮 -->
        <div class="v-oushudb-add-table-form-sql-detail-header-icon-group">
          <x-tooltip placement="bottomLeft" title="复制">
            <icon class="hover-translateY-2" color="primary" name="worksheet/copy" @click="handleCopySQLDetail"/>
          </x-tooltip>
        </div>
      </div>
      <div style="overflow: auto;height: 500px;">
        <div id="v-oushudb-add-table-form-sql-detail-container"
             class="v-oushudb-add-table-form-sql-detail-container"></div>
      </div>
    </div>
    <!-- 提交、取消按钮组 -->
    <div v-if="isAdd" class="v-oushudb-add-table-form-btn-container">
      <x-button :disabled="getTableDetailLoading" type="primary" @click="handleSubmit">
        <icon name="worksheet/submit"/>
        提交
      </x-button>
      <x-button @click="handleCancel">
        <icon color="inherit" name="worksheet/cancel"/>
        取消
      </x-button>
    </div>
  </div>

  <x-drawer
    :visible="columnDrawerVisible"
    class="v-oushudb-edit-column-drawer"
    destroyOnClose
    fixed
    title="编辑列"
    width="800"
    @close="() => { columnDrawerVisible = false }"
  >
    <add-or-update-column-base
      :initial-column="columnWillEdit"
      :schema-name="schema"
      :table="table"
      :is-add="isAdd"
      @close="() => { columnDrawerVisible=false }"
      @confirm="handleConfirmEditColumn"
    />
  </x-drawer>
</template>
<script lang="ts">
/* eslint-disable */
import { computed, defineComponent, onMounted, onUnmounted, reactive, ref, toRefs, watch } from 'vue'
// @ts-ignore
import smartUUI from '@/smart-ui-vue/index.js'
import Icon from '@/components/Icon.vue'
import { RuleObject } from 'ant-design-vue/es/form/interface'
import { message } from 'ant-design-vue-3'
import { debounce, throttle } from 'lodash'
import * as monaco from 'monaco-editor'
import useClipboard from 'vue-clipboard3'
// @ts-ignore
import Sortable from 'sortablejs'
import {
  COLUMNS,
  STORAGE_FORMAT_LIST,
  TYPE_OPTION_LIST,
  TYPE_REQUIRED_SCALE,
  TYPE_WITH_SCALE,
  TYPE_WITH_PRECISION,
} from './constant'
import addOrUpdateColumnBase from '@/components/Worksheet/WorksheetSideBar/addOrUpdateColumnBase.vue'
import { useStore } from 'vuex'
import { Table } from '@/components/Worksheet/type'
import { useI18n } from 'vue-i18n'
import { translateErrorMessage } from 'lava-fe-lib/lib-common/i18n'
import { createTable, duplicateTable, getColumnList, getSqlForCreateTable, getTableDetails } from '@/api'
import { checkIsInstanceName } from '@/lib/regexp'
import { format } from 'sql-formatter'

import('monaco-themes/themes/Tomorrow.json')
  .then(data => {
    // @ts-ignore
    monaco.editor.defineTheme('Tomorrow', data)
  })

let editor: any = undefined

/**
 * editor 初始化配置
 */
const initEditor = () => {
  const domEditor = document.getElementById('v-oushudb-add-table-form-sql-detail-container') as HTMLElement
  if (!domEditor) return
  // 初始化配置
  editor = monaco.editor.create(domEditor, {
    // eslint-disable-next-line no-magic-numbers
    fontSize: 14,
    theme: 'Tomorrow',
    readOnly: true,
    automaticLayout: false,
    language: 'sql',
    value: '',
    minimap: {
      enabled: false,
    },
    lineNumbers: 'on',
  })
}

let columnIdCount: number = 1

/**
 * 列的默认值
 */
export function getDefaultColumn(pk?: 'isPrimary'): any {
  return {
    id: columnIdCount,
    columnDeletePopConfirmVisible: false,
    name: `column_${columnIdCount++}`,
    type: 'BIGINT',
    comment: '',
    columnFamily: '',
    isPrimary: pk ? true : false,
    scale: 10,
    precision: 2,
  }
}


export default defineComponent({
  name: 'AddOrUpdateTable',
  components: { addOrUpdateColumnBase, Icon, ...smartUUI },
  props: {
    // 编辑时table的初始值
    initTable: {
      type: Object,
      required: true,
    },
    // is added = false means is edited，后续扩展用到
    isAdd: {
      type: Boolean,
      required: true,
    },
    // 执行 SQL 时需用到，与是否是 table drawer 无关
    schema: {
      type: String,
      required: true,
    },
  },
  emits: ['close'],
  setup(props, context) {
    const { toClipboard } = useClipboard()
    const store = useStore()
    const { t } = useI18n()
    const getError = translateErrorMessage(t)
    let sortable: any = null;

    const state = reactive({
      // 编辑表，请求数据时 loading 状态
      getTableDetailLoading: false,

      table: {
        // basic info
        name: '',
        comment: '',
        split_on: '',
        salt_buckets: 0,
        columnList: [getDefaultColumn('isPrimary')] as any[],
      },

      // column drawer
      columnWillEdit: {} as any,
      columnDrawerVisible: false,
      // 新增
    })

    onMounted(() => {
      // 阻止默认行为
      document.body.ondrop = function(event) {
        event.preventDefault()
        event.stopPropagation()
      }

      columnTableSortDrop()
      if (!props.isAdd) {
        // 往后推一下
        setTimeout(() => {
          initTableTransfer()
        }, 0)
      }

      initEditor()
    })

    onUnmounted(() => {
      editor?.dispose()
    })

    /**
     * 表名验证
     * @param rule
     * @param value
     */
    const tableNameValidator = (rule: RuleObject, value: string) => {
      if (value === '') {
        return Promise.reject('请输入表名')
      }

      if (!checkIsInstanceName(value)) {
        return Promise.reject('请输入不以数字作为开始的由字母/数字/下划线组成的50位以内的字符串')
      }

      return new Promise((resolve, reject) => {
        duplicateTable({
          schemaName: props.schema,
          tableName: state.table.name
        }).then((resp) => {
          if (resp.meta.success) {
            resolve('')
          } else {
            reject('该表名已存在，请检查后重新填写')
          }
        })
      })
    }

    /**
     * 表名规则
     */
    const tableNameRules = [
      { required: true, validator: tableNameValidator },
    ]

    const tableSplitRules = [
      { required: true },
    ]

    /**
     * 列名验证
     * @param rule
     * @param value
     */
    const columnNameValidator = (rule: RuleObject, value: string) => {
      if (value === '') {
        return Promise.reject('')
      }
      
      if (state.table.columnList.filter(column => column.name === value).length > 1) {
        return Promise.reject('')
      }

      return Promise.resolve('')
    }

    const validateColumnName = (value: string) => {
      if (value === '') {
        return '请输入列名'
      }
      
      if (state.table.columnList.filter(column => column.name === value).length > 1) {
        return '该列名已存在，请检查后重新填写'
      }

      return ''
    }

    /**
     * 列名规则
     */
    const columnNameRules = [
      { required: true, validator: columnNameValidator },
    ]

    /**
     * 列族验证
     * @param rule
     * @param value
     */
    const columnFamilyValidator = (rule: RuleObject, value: string) => {
      if (value === '') return Promise.resolve('')
      if (!checkIsInstanceName(value)) {
        return Promise.reject('')
      }

      return Promise.resolve('')
    }

    const validateColumnFamily = (value: string) => {
      if (value === '') return ''
      
      if (!checkIsInstanceName(value)) {
        return '请输入不以数字作为开始的由字母/数字/下划线组成的50位以内的列族名'
      }

      return ''
    }

    /**
     * 列名规则
     */
    const columnFamilyRules = [
      { required: false, validator: columnFamilyValidator },
    ]

    /**
     * 处理复制【链接字符串】
     * @value 待复制到粘贴板的信息
     */
    const handleCopyValue = async(value: any) => {
      try {
        await toClipboard(value)
        message.info('已复制到粘贴板')
      } catch (e) {
        message.error('复制失败，请手动复制')
      }
    }

    /**
     * 复制 SQL 详情
     */
    const handleCopySQLDetail = () => {
      handleCopyValue(editor.getValue())
    }

    const originSQL = ref('')
    /**
     * 刷新 SQL 详情
     */// eslint-disable-next-line require-await
    const handleRefreshSQLDetail = debounce(
      async(cancel = false) => {
        if (cancel) {
          editor.setValue('')
          return
        }

        getCreateTableSQLDetail()

        // 数据转化
        async function getCreateTableSQLDetail() {
          const data: any = {
            schemaName: props.schema,
            tableName: state.table.name,
            comment: state.table.comment,
            splitOn: state.table.split_on,
            saltBuckets: state.table.salt_buckets,
            columns: state.table.columnList.map(column => {
              return {
                familyName: column.columnFamily,
                columnName: column.name,
                comment: column.comment,
                dataType: column.type,
                scale: TYPE_WITH_SCALE.indexOf(column.type) !== -1 ? column.scale : 0,
                precision: TYPE_WITH_PRECISION.indexOf(column.type) !== -1 ? column.precision : 0,
                pk: column.isPrimary,
              }
            }),
          }


          const result = await getSqlForCreateTable(data)

          if (result.meta.success) {
            originSQL.value = typeof result.data === 'string' ? result.data : result.data.join('\n')
            editor.setValue(format(typeof result.data === 'string' ? result.data : result.data.join('\n'), {
              language: 'db2',
            }))
          } else {
            editor.setValue('')
          }
        }
      }, 800)

    /**
     * 参数检查
     */
    const tableBasicInfoCheck = async() => {
      if (state.table.name === '') {
        message.warning('请输入表名！')
        return false
      }
      if (!checkIsInstanceName(state.table.name)) {
        message.warning('请输入不以数字作为开始的由字母/数字/下划线组成的50位以内的表名')
        return false
      }
      const resp = await duplicateTable({
        schemaName: props.schema,
        tableName: state.table.name
      })
      if (!resp.meta.success) {
        message.warning('该表名已存在，请重新填写！')
        return false
      }
      // @ts-ignore
      if (state.table.columnList.length === 0) {
        message.warning('请至少输入一个列信息！')
        return false
      }
      if (state.table.columnList.some(column => {
        return column.name === ''
          || (!TYPE_OPTION_LIST.map(item => item.value).includes(column.type))
      })) {
        message.warning('请检查所有列名、选择列类型是否已输入！')
        return false
      }
      if (state.table.columnList.every(column => !column.isPrimary)) {
        message.warning('请至少勾选一个主键列！')
        return false
      }
      return true
    }

    const handleSubmit = async() => {
      const checked = await tableBasicInfoCheck()
      if (!checked) {
        return
      }

      if (state.table.salt_buckets === null) state.table.salt_buckets = 0

      const data: any = {
        schemaName: props.schema,
        tableName: state.table.name,
        comment: state.table.comment,
        splitOn: state.table.split_on,
        saltBuckets: state.table.salt_buckets,
        columns: state.table.columnList.map(column => {
          return {
            familyName: column.columnFamily,
            columnName: column.name,
            comment: column.comment,
            dataType: column.type,
            scale: TYPE_WITH_SCALE.indexOf(column.type) !== -1 ? column.scale : 0,
            precision: TYPE_WITH_PRECISION.indexOf(column.type) !== -1 ? column.precision : 0,
            pk: column.isPrimary,
          }
        }),
      }

      const executeResult = await createTable(data)

      // const executeResult = await executeSql(originSQL.value)

      if (executeResult.meta.success) {
        // 创建表成功
        message.success(`${props.isAdd ? '创建' : '修改'}表成功`)
        context.emit('close', true)
      } else {
        message.error(`${props.isAdd ? '创建' : '修改'}表失败: ${executeResult.meta?.message || getError(executeResult) || '无失败提示'}`, 5)
      }
    }

    const temp = computed(() => state)
    watch(() => temp.value, () => {
      handleRefreshSQLDetail()
    }, {
      deep: true,
    })

    const handleCancel = () => {
      //
      context.emit('close')
    }

    /**
     * 选择主键列
     */
    const columnSelectChange = (event: any, column: any) => {
      const table = state.table.columnList
      const index = table.findIndex((item) => item.id === column.id)
      if (event.target.checked) {
        table[index].columnFamily = ''
        const i = table.findIndex((value) => !value.isPrimary)
        if (index + 1 === i || i === -1) return
        state.table.columnList = [...table.slice(0,i), table[index], ...table.slice(i, index), ...table.slice(index + 1)]
      } else {
        const lastPrimary = table.map((item: any, idx: number) => {
          return {
            ...item,
            idx
          }
        }).filter((value: any) => value.isPrimary).slice(-1)[0]
        if (!lastPrimary) return
        const i = lastPrimary.idx
        if (index === i + 1) return
        state.table.columnList = [...table.slice(0,index), ...table.slice(index + 1, i + 1), table[index], ...table.slice(i + 1)]
      }
    }

    /**
     * 新增列
     */
    const handleAddColumn = () => {
      if (state.table.columnList.length === 0) {
        state.table.columnList.push(getDefaultColumn('isPrimary'))
        return
      }
      state.table.columnList.push(getDefaultColumn())
    }

    /**
     * 新增列
     */
    const handleAddColumnWithDebounce = throttle(handleAddColumn, 500)
    /**
     * column 编辑按钮的点击
     */
    const handleEditColumn = (column: any) => {
      state.columnWillEdit = column
      state.columnDrawerVisible = true
    }

    /**
     * column 删除
     */
    const handleDeleteColumn = (column: any) => {
      const table = state.table.columnList
      const index = table.findIndex((item) => item.id === column.id)
      state.table.columnList = [...table.slice(0, index), ...table.slice(index + 1)]
    }

    const handleDeleteVisibleChange = (visible: boolean, column: any) => {
      if (!visible) {
        column.columnDeletePopConfirmVisible = false
        return
      }

      if (column.isPrimary) {
        column.columnDeletePopConfirmVisible = true
      } else {
        handleDeleteColumn(column)
      }
    }

    /**
     * 列编辑 drawer 确认
     * @param column
     */
    const handleConfirmEditColumn = (column: any) => {
      state.columnDrawerVisible = false
      const columnWillEdit = state.columnWillEdit
      columnWillEdit.name = column.name
      columnWillEdit.type = column.type
      columnWillEdit.comment = column.comment
      columnWillEdit.columnFamily = column.columnFamily
      columnWillEdit.scale = column.scale
      columnWillEdit.precision = column.precision
      columnWillEdit.isPrimary = column.isPrimary
    }

    /**
     * table 行拖拽排序
     */
    const columnTableSortDrop = () => {
      const tbody = document.querySelector('.v-oushudb-add-table-form-column-table tbody')
      sortable = Sortable.create(tbody, {
        handle: '.v-oushudb-add-table-form-column-table-handle',
        animation: 280,
        delay: 0,
        chosenClass: 'v-oushudb-add-table-form-column-drag-chosen',
        dataIdAttr: 'data-row-key',
        // @ts-ignore
        onEnd({ newIndex, oldIndex }) {
          const oldPrimary = state.table.columnList[oldIndex].isPrimary
          const newPrimary = state.table.columnList[newIndex].isPrimary
          if (oldPrimary !== newPrimary) {
            state.table.columnList[oldIndex].isPrimary = state.table.columnList[newIndex].isPrimary
          }
          if (newPrimary) {
            state.table.columnList[oldIndex].columnFamily = ''
          }
          // @ts-ignore
          const currRow = state.table.columnList.splice(oldIndex, 1)[0]
          // @ts-ignore
          state.table.columnList.splice(newIndex, 0, currRow)
        },
      })
    }

    /**
     * 编辑数据初始化处理，较为麻烦，单独抽出来
     */
    const initTableTransfer = async() => {

      const initTable = props.initTable as Table

      state.getTableDetailLoading = true

      // 获取表的详细数据、列信息

      // 详细信息
      const getDetailResult = await getTableDetails({
        schemaName: props.schema,
        tableName: initTable.name
      })

      if (getDetailResult.meta.success) {
        const details = {
          name: getDetailResult.data.TABLE_NAME,
          comment: getDetailResult.data.COMMENT,
          salt_buckets: getDetailResult.data.SALT_BUCKETS,
          split_on: getDetailResult.data.SPLIT_ON
        }
        Object.assign(initTable, details)
      } else {
        message.error(`获取表详细信息失败：${getError(getDetailResult)}`)
        return
      }

      // 列信息
      const getColumnListResult = await getColumnList({
        schemaName: props.schema,
        tableName: initTable.name,
        offset: 0,
        limit: -1
      })

      if (getColumnListResult.meta.success) {
        initTable.columns = getColumnListResult.data?.data ? getColumnListResult.data.data.sort((a: any, b: any) => a.ORDINAL_POSITION - b.ORDINAL_POSITION).map((column: any) => {
          return {
            name: column.COLUMN_NAME,
            type: column.DATA_TYPE_NAME,
            comment: column.COMMENT,
            schema: column.TABLE_SCHEM,
            table: column.TABLE_NAME,
            column_family: column.COLUMN_FAMILY,
            order: column.ORDINAL_POSITION,
            primary: Boolean(column.KEY_SEQ)
          }
        }) : []
      } else {
        message.error(`获取列信息失败：${getError(getColumnListResult)}`)
        return
      }

      state.table.name = initTable.name
      state.table.split_on = initTable.split_on
      state.table.salt_buckets = initTable.salt_buckets
      state.table.comment = initTable.comment
      // schema 未传，用 props 里面的

      state.table.columnList = (initTable.columns ?? []).map(((column, index) => {
        return {
          // 仅前端辅助字段，非实际使用字段
          id: index + 1,
          // 仅前端辅助字段，非实际使用字段
          columnDeletePopConfirmVisible: false,
          // 列名，也可做唯一值
          name: column.name,
          type: column.type || 'VARCHAR',
          comment: column.comment,
          columnFamily: column.column_family,
          // 是否允许为空
          isPrimary: column.primary,
          scale: column.scale || 10,
          precision: column.precision || 2,
        }
      }))

      // 维护自增 id 状态
      columnIdCount = state.table.columnList.length + 1

      state.getTableDetailLoading = false
    }


    return {
      ...toRefs(state),

      STORAGE_FORMAT_LIST,
      TYPE_OPTION_LIST,
      TYPE_WITH_SCALE,
      TYPE_REQUIRED_SCALE,
      TYPE_WITH_PRECISION,

      columns: props.isAdd ? COLUMNS : COLUMNS.slice(0, -1),

      tableNameRules,
      tableSplitRules,
      columnNameRules,
      columnFamilyRules,
      validateColumnName,
      validateColumnFamily,

      handleCopySQLDetail,
      handleRefreshSQLDetail,
      handleSubmit,
      handleCancel,
      handleCopyValue,

      handleAddColumnWithDebounce,
      handleEditColumn,
      handleDeleteColumn,
      handleDeleteVisibleChange,
      handleConfirmEditColumn,
      columnSelectChange,

      handleColumnTypeChange: (value: string, record: any) => {
        if (!TYPE_REQUIRED_SCALE.includes(value)) {
          record.scale = 0
          record.precision = 0
        }
        if (['timetz', 'timestamptz'].includes(value)) {
          record.scale = undefined
          record.precision = undefined
        }
      },
    }
  },
})
</script>
<style lang="scss">

.v-oushudb-add-or-update-table {
  // 基本信息
  .v-oushudb-add-table-form-container {
    // input
    .#{$ant-prefix}-input-affix-wrapper {
      height: 51px !important;
      padding: 0;

      .#{$ant-prefix}-input-prefix {
        margin-right: 5px;
        margin-left: 10px;
      }

      .#{$ant-prefix}-input {
        height: 100% !important;
      }
    }

    // Text Area
    textarea.#{$ant-prefix}-input {
      @include font-normal();
      padding: 15px 0 15px 32px;
      color: $color-primary-black;
      border: none;
      border-bottom: 1px solid $color-line-bold;
      border-radius: 0;

      box-shadow: none !important;

      &:hover {
        border-color: $color-primary-black !important;
      }

      &:focus,
      &:active {
        border-color: $color-primary-blue !important;
      }
    }

    // basic info
    .v-oushudb-add-table-form-container-item {
      display: flex;

      .v-oushudb-add-table-form-container-item-label {
        display: flex;
        flex: 0 0 84px;
        align-items: center;
        max-height: 50px;
        margin-right: 10px;
        @include font-small();
        color: $color-primary-black;
      }

      .v-oushudb-add-table-form-container-item-label-disabled {
        color: $color-text-comment;
      }

      .v-oushudb-add-table-form-container-item-control {
        position: relative;
        z-index: 1;
        flex: 1;

        .v-oushudb-add-table-form-container-item-control-prefix-icon {
          position: absolute;
          top: 16px;
          left: 9px;
        }
        .v-oushudb-add-table-form-container-item-number {
          height: 50px;
          padding-top: 15px;
        }
      }
    }

    // 列表格
    .v-oushudb-add-table-form-column-table {
      margin-bottom: 10px;
    }

    // 单元格
    .v-oushudb-add-table-basic-info-table-cell {
      width: 100%;

      .antv-input-number {
        width: auto;
      }

      .smartui-input:not(.raw).#{$ant-prefix}-input:not(:hover):not(:focus) {
        border-bottom-color: transparent;
      }

      .v-oushudb-add-table-basic-info-table-cell-show {
        display: inline-flex;
        width: 100%;
      }

      .v-oushudb-add-table-basic-info-table-cell-edit {
        display: none;
        width: 100%;
      }

      .v-oushudb-add-table-basic-info-table-cell-select {
        width: 100%;
        min-width: 140px;

        .#{$ant-prefix}-select-selector {
          padding-left: 4px;
        }

        .#{$ant-prefix}-select-selection-search {
          left: 4px;
        }
      }

      .#{$ant-prefix}-input {
        padding-right: 11px;
        padding-left: 11px;
      }

      &:hover {
        .v-oushudb-add-table-basic-info-table-cell-show {
          display: none;
        }

        .v-oushudb-add-table-basic-info-table-cell-edit {
          display: inline-flex;
        }
      }
    }

    // 分布键标题
    .v-oushudb-add-table-distribution-sort-title {
      margin-bottom: 10px;

      @include font-small();
      color: $color-primary-black;
    }

    // 分布键容器
    .v-oushudb-add-table-distribution-sort-list {
      display: flex;
      flex-wrap: wrap;
      padding: 10px 10px 0 0;
      margin-bottom: 10px;
      border: 1px solid $color-line-bold;

      border-radius: 4px;

      .v-oushudb-distribution-key-tag {
        margin-bottom: 10px;
        margin-left: 10px;
      }
    }
  }

  // SQL 预览
  .v-oushudb-add-table-form-sql-detail {
    flex: 1;
    padding: 10px 0 0;
    border-top: 1px solid $color-text-comment;

    .v-oushudb-add-table-form-sql-detail-header {
      display: flex;
      justify-content: space-between;
      @include font-small();
      padding-bottom: 20px;
      color: $color-primary-black;

      .v-oushudb-add-table-form-sql-detail-header-icon-group {
        .icon {
          cursor: pointer;
        }

        .icon:not(:last-child) {
          margin-right: 10px;
        }
      }
    }

    .v-oushudb-add-table-form-sql-detail-container {
      height: 100%;
      //height: calc(100% - 50px);
      //max-height: calc(100% - 50px);

      // 隐藏：右侧的灰线以及 cursor 所在行的指示
      .decorationsOverviewRuler {
        display: none;
      }
    }
  }

  // 操作按钮：提交、取消
  .v-oushudb-add-table-form-btn-container {
    position: absolute;
    right: 0;
    bottom: 0;
    z-index: 1;
    width: 100%;
    padding: 20px 40px;
    background-color: white;

    .#{$ant-prefix}-btn:not(:last-child) {
      margin-right: 10px;
    }
  }
}

.v-oushudb-add-table-drawer.#{$ant-prefix}-drawer {
  .#{$ant-prefix}-drawer-header {
    padding: 30px 40px 30px;

    .#{$ant-prefix}-drawer-title {
      font-size: 18px;
      font-style: normal;
      font-weight: normal;
      line-height: 24px;
      color: #282B2E;
    }
  }

  .#{$ant-prefix}-drawer-body {
    max-height: calc(100vh - 74px - 70px);
    padding: 0 40px 70px 40px !important;
  }
}

.v-oushudb-add-or-update-table-tab-title {
  display: inline-flex;
  align-items: center;
  height: 26px;
}

</style>