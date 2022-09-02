<template>
  <x-form :label-col="{ style: { width: '108px', height: '50px' } }">
    <x-form-item label="列名">
      <x-input v-model:value="name" :rules="columnNameRules" placeholder="请填写列名">
        <template #prefix>
          <icon image name="worksheet/column_two_color"/>
        </template>
        <template #suffix>
          <x-tooltip placement="bottomLeft" title="复制">
            <icon color="primary" name="worksheet/copy" @click="() => handleCopyValue(name)"/>
          </x-tooltip>
        </template>
      </x-input>
    </x-form-item>
    <x-form-item label="中文名（选填）">
      <x-textarea v-model:value="comment" autoSize placeholder="请填写中文名">
        <template #prefixIcon>
          <icon image name="worksheet/column_comment_two_color"/>
        </template>
      </x-textarea>
    </x-form-item>
    <x-form-item label="列类型">
      <x-select v-model:value="type" :options="TYPE_OPTION_LIST" is-in-form show-search>
        <template #prefixIcon>
          <icon
            image
            name="worksheet/column_two_color"
          />
        </template>
      </x-select>
    </x-form-item>
    <x-form-item label="列族（选填）">
      <x-input v-model:value="columnFamily">
        <template #prefix>
          <icon image name="worksheet/column_two_color"/>
        </template>
      </x-input>
    </x-form-item>
    <x-form-item
      v-if="['numeric','varchar','char','bit','time','timestamp'].includes(type)"
      label="长度"
    >
      <x-input-number
        v-model:value="length"
        :max="type === 'numeric' ? storageFormat === 'orc' ? 38: 1000 : 10485760"
        :min='1'
      />
    </x-form-item>
    <x-form-item v-if="['numeric'].includes(type)" label="精度">
      <x-input-number v-model:value="scale" :max="length - 1" :min='0'/>
    </x-form-item>
    <x-form-item label="主键">
      <x-switch v-model:checked="isPrimary"/>
    </x-form-item>
  </x-form>
  <!-- SQL 详情挂载 -->
  <div v-if="isUpdateTable" class="v-oushudb-add-table-form-sql-detail">
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
  <div class="v-oushudb-edit-column-form-btn-container">
    <x-button
      :disabled="isSubmitDisabled"
      :loading="execLoading"
      type="primary"
      @click="handleConfirm"
    >
      <icon name="worksheet/submit"/>
      确认
    </x-button>
    <x-button @click="handleCancel">
      <icon name="worksheet/cancel"/>
      取消
    </x-button>
  </div>
</template>
<script lang="ts">
import { computed, defineComponent, onMounted, onUnmounted, PropType, reactive, ref, toRefs, watch } from 'vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { RuleObject } from 'ant-design-vue/es/form/interface'
import Icon from '@/components/Icon.vue'
import { message } from 'ant-design-vue-3'
import * as monaco from 'monaco-editor'
import useClipboard from 'vue-clipboard3'

import { TIME_DEFAULT_TYPE_LIST, TYPE_OPTION_LIST } from './constant'
import { COLOR_PRIMARY_BLUE } from 'lava-fe-lib/lib-common/constant'
import { debounce } from 'lodash'
import { getSqlForCreateColumn, getSqlForUpdateColumn } from '@/api/mock'
import { Table } from '@/components/Worksheet/type'
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

/**
 * 表单验证先不做
 */
export default defineComponent({
  name: 'addOrUpdateColumnBase',
  props: {
    schemaName: {
      type: String,
      required: true,
    },
    table: {
      type: Object as PropType<Table | undefined>,
      required: true,
    },
    initialColumn: {
      type: Object,
      required: true,
    },
    initAlreadyExistNameList: {
      type: Array,
      required: true,
    },
    storageFormat: {
      type: String as PropType<string | undefined>,
      required: true,
    },
    /**
     * 是否是新增 table 时新增字段
     */
    isAdd: {
      type: Boolean,
      default: false,
    },
    /**
     * 表已存在，修改表的字段（添加or编辑）
     */
    isUpdateTable: Boolean,
    execLoading: Boolean,
  },
  components: { ...smartUI, Icon },
  emits: ['close', 'confirm'],
  setup(props, context) {
    const { toClipboard } = useClipboard()

    const formState = reactive({
      name: props.initialColumn.name,
      type: props.initialColumn.type,
      columnFamily: props.initialColumn.columnFamily,
      comment: props.initialColumn.comment,
      isPrimary: props.initialColumn.isPrimary,
      length: props.initialColumn.length,
      scale: props.initialColumn.scale,
    })

    watch(() => props.initialColumn, () => {
      formState.name = props.initialColumn.name
      formState.type = props.initialColumn.type
      formState.columnFamily = props.initialColumn.columnFamily
      formState.comment = props.initialColumn.comment
      formState.isPrimary = props.initialColumn.isPrimary
      formState.length = props.initialColumn.length
      formState.scale = props.initialColumn.scale
    })

    const validatePass = (rule: RuleObject, value: string) => {
      if (value === '') {
        return Promise.reject('请填写列名')
      } else {
        if (props.initAlreadyExistNameList?.includes(formState.name)) {
          return Promise.reject('该列名已存在')
        }

        return Promise.resolve('')
      }
    }


    const columnNameRules = [
      // 非空
      { required: true, validator: validatePass },
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
     * 提交表单
     */
    const handleConfirm = () => {
      context.emit('confirm', {
        ...formState,
      })
    }


    const handleCancel = () => {
      context.emit('close')
    }

    /**
     * 复制 SQL 详情
     */
    const handleCopySQLDetail = () => {
      handleCopyValue(editor.getValue())
    }

    /**
     * 刷新 SQL 详情
     */
    const handleRefreshSQLDetail = debounce(
      // eslint-disable-next-line require-await
      async(cancel = false) => {
        if (cancel) {
          editor.setValue('')
          return
        }

        // todo: 参数检查

        if (props.isAdd) {
          getCreateColumnSQLDetail()
        } else {
          getUpdateColumnSQLDetail()
        }
        // 数据转化
        // eslint-disable-next-line require-await
        async function getCreateColumnSQLDetail() {
          const data: any = {
            schema: props.schemaName,
            table: props.table?.name,
            name: formState.name,
            type: formState.type,
            columnFamily: formState.columnFamily,
            comment: formState.comment,
            isPrimary: formState.isPrimary,
            length: formState.length,
            scale: formState.scale,
          }

          const result = await getSqlForCreateColumn(data)

          if (result.meta.success) {
            editor.setValue(format(typeof result.data === 'string' ? result.data : result.data.join('\n'), {
              language: 'db2',
            }))
          } else {
            editor.setValue('')
          }
        }

        async function getUpdateColumnSQLDetail() {
          const initTable = props.table as Table

          const data: any = {
            schema: props.schemaName,
            table: initTable.name,
            name: formState.name,
            type: formState.type,
            columnFamily: formState.columnFamily,
            comment: formState.comment,
            isPrimary: formState.isPrimary,
            length: formState.length,
            scale: formState.scale,
          }

          const result = await getSqlForUpdateColumn(data)

          if (result.meta.success) {
            editor.setValue(format(typeof result.data === 'string' ? result.data : result.data.join('\n'), {
              language: 'db2',
            }))
          } else {
            editor.setValue('')
          }
        }
      }, 800)

    const temp = computed(() => formState)
    watch(() => temp.value, () => {
      if (!props.isUpdateTable) return
      handleRefreshSQLDetail()
    }, {
      deep: true,
    })

    onMounted(() => {
      initEditor()
    })

    onUnmounted(() => {
      // eslint-disable-next-line
      editor?.dispose()
    })

    return {
      COLOR_PRIMARY_BLUE,

      disabledRef: computed(() => !props.isAdd),
      isSubmitDisabled: false,

      ...toRefs(formState),

      columnNameRules,

      TYPE_OPTION_LIST,
      TIME_DEFAULT_TYPE_LIST,

      handleCopyValue,
      handleCopySQLDetail,
      handleRefreshSQLDetail,

      handleCancel,
      handleConfirm,
    }
  },
})
</script>
<style lang="scss">
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
  // 操作按钮：确认、取消
  .v-oushudb-edit-column-form-btn-container {
    position: absolute;
    right: 0;
    bottom: 0;
    z-index: 1;
    width: 100%;
    padding: 20px 40px;

    .#{$ant-prefix}-btn:not(:last-child) {
      margin-right: 10px;
    }
  }
</style>