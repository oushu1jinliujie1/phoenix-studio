<template>
  <div class="v-oushudb-add-db-form-container">
    <div class="v-oushudb-add-db-form-container-item">
      <div class="v-oushudb-add-db-form-container-item-label">
        模式名
      </div>
      <div class="v-oushudb-add-db-form-container-item-control" style="position: relative; z-index: 1;">
        <x-input
          v-model:value="schemaName"
          :rules="rules"
          error-tip-position="bottom"
          data-test-id="oushudb-worksheet-database-schema-name-add-edit-input"
          :placeholder="'请填写模式名'"
        >
          <template #prefix>
            <icon class="hover-translateY-2" image name="worksheet/database_two_color"/>
          </template>
          <template v-if="!disabled" #suffix>
            <a-tooltip placement="bottomLeft" title="复制">
              <icon
                class="hover-translateY-2" color="primary"
                data-test-id="oushudb-worksheet-database-schema-name-copy"
                name="worksheet/copy"
                style="margin-right: 9px; cursor: pointer;"
                @click="() => handleCopyValue(schemaName)"
              />
            </a-tooltip>
          </template>
        </x-input>
      </div>
    </div>
    <div class="v-oushudb-add-db-form-container-item">
      <div class="v-oushudb-add-db-form-container-item-label">模式备注（选填）</div>
      <div class="v-oushudb-add-db-form-container-item-control">
        <a-textarea
          v-model:value="comment"
          auto-size
          data-test-id="oushudb-worksheet-database-schema-comment-add-edit-textarea"
          placeholder="请填写备注"
        />
        <!-- custom prefix icon -->
        <icon
          class="v-oushudb-add-db-form-container-item-control-prefix-icon"
          image
          name="worksheet/database_comment_two_color"
        />
      </div>
    </div>
  </div>
  <div class="v-oushudb-add-db-form-sql-detail">
    <div class="v-oushudb-add-db-form-sql-detail-header">
      <span>SQL详情</span>
      <!-- 复制、刷新按钮 -->
      <div class="v-oushudb-add-db-form-sql-detail-header-icon-group">
        <a-tooltip placement="bottomLeft" title="复制">
          <icon
            class="hover-translateY-2"
            color="primary"
            data-test-id="oushudb-worksheet-database-schema-sql-copy-btn"
            name="worksheet/copy"
            @click="handleCopySQLDetail"
          />
        </a-tooltip>
      </div>
    </div>
    <div style="overflow: auto;height: 500px;">
      <div id="v-oushudb-add-db-form-sql-detail-container" class="v-oushudb-add-db-form-sql-detail-container"></div>
    </div>
  </div>
  <!-- 提交、取消按钮组 -->
  <div class="v-oushudb-add-db-form-btn-container">
    <x-button
      :disabled="isSubmitDisabled"
      data-test-id="oushudb-worksheet-database-schema-submit-add-edit-btn" type="primary"
      @click="handleSubmit">
      <icon name="worksheet/submit"/>
      提交
    </x-button>
    <x-button data-test-id="oushudb-worksheet-database-schema-cancel-add-edit-btn" @click="handleCancel">
      <icon name="worksheet/cancel"/>
      取消
    </x-button>
  </div>
</template>
<script lang="ts">
/* eslint-disable indent */
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import Icon from '@/components/Icon.vue'
import { computed, defineComponent, onBeforeUnmount, onMounted, reactive, toRefs, watch } from 'vue'
import { message } from 'ant-design-vue-3'
import useClipboard from 'vue-clipboard3'

import * as monaco from 'monaco-editor'
import { format } from 'sql-formatter'
import { debounce } from 'lodash'
import { RuleObject } from 'ant-design-vue/es/form/interface'
import { useI18n } from 'vue-i18n'
import { translateErrorMessage } from 'lava-fe-lib/lib-common/i18n'

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
  const domEditor = document.getElementById('v-oushudb-add-db-form-sql-detail-container') as HTMLElement
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

export default defineComponent({
  name: 'addOrUpdateSchema',
  components: { Icon, ...smartUI },
  props: {
    initAlreadyExistNameList: {
      type: Array,
      default: (): [] => [],
    },
    initComment: {
      type: String,
      default: '',
    },
    initSchemaName: {
      type: String,
      default: '',
    },
    // 执行 SQL 时需用到，与是否是 database drawer 无关
    schema: {
      type: String,
      required: true,
    },
    // is added = false means is edited
    isAdd: {
      type: Boolean,
      required: true,
    },
  },
  emits: ['close'],
  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  setup(props: any, context: any) {
    const { toClipboard } = useClipboard()
    const { t } = useI18n()
    const getError = translateErrorMessage(t)

    const state = reactive({
      // 初始名字得再优化一下
      schemaName: props.initSchemaName ?? '',
      ownerName: props.initOwnerName,
      comment: props.initComment,
      tableSpace: props.initTableSpace,
      disabled: !props.isAdd,
    })

    const validatePass = (rule: RuleObject, value: string) => {
      if (value === '') {
        return Promise.reject('请填写模式名')
      } else {
        if (!props.isAdd) return Promise.resolve()
        if (props.initAlreadyExistNameList?.includes(state.schemaName)) {
          return Promise.reject('该模式名已存在，请检查后重新填写')
        }

        return Promise.resolve('')
      }
    }


    const rules = [
      // 非空
      { required: true, validator: validatePass },
      // {
      //   validator: () => {
      //     if (!props.isAdd) return true
      //     return props.initAlreadyExistNameList?.includes(state.schemaName)
      //   }, message: `该${props.isDatabaseDrawer ? '数据库' : '模式'}名已存在，请检查后重新填写`,
      // },
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

    // 防抖时间，单位 ms
    const DEBOUNCE_WAIT = 300
    /**
     * 刷新 SQL 详情
     */
    const handleRefreshSQLDetail = debounce((cancel = false) => {
        // if (cancel) {
        //   editor.setValue('')
        //   return
        // }

        // 获取新建SQL


        // const data = {
        //   name: state.schemaName,
        //   owner: state.ownerName,
        //   comment: state.comment,
        //   table_space: state.tableSpace,
        //   new_name: '',
        // }
        // const result = await (() => {
        //   if (props.isAdd) {
        //     return (props.isDatabaseDrawer ? getSQLForCreateDatabase : getSQLForCreateSchema)(props.instanceId, props.database, data)
        //   } else {
        //     if (!props.isDatabaseDrawer) {
        //       data.new_name = data.name
        //       data.name = props.initSchemaName
        //     }

        //     return (props.isDatabaseDrawer ? getSQLForEditDatabase : getSQLForEditSchema)(props.instanceId, props.database, data)
        //   }
        // })()

        // if (result.meta.success) {
        //   editor.setValue(format(typeof result.data === 'string' ? result.data : result.data.join('\n')))
        // } else {
        //   editor.setValue('')
        //   // message.warning(result.meta.message)
        // }
      }
      , DEBOUNCE_WAIT)
    /**
     * 提交表单
     */
    const handleSubmit = () => {
      // 获取新建SQL，执行SQL


      // const data = {
      //   name: state.schemaName,
      //   owner: state.ownerName,
      //   comment: state.comment,
      //   table_space: state.tableSpace,
      //   new_name: '',
      // }
      // const getSQLResult = await (() => {
      //   if (props.isAdd) {
      //     return (props.isDatabaseDrawer ? getSQLForCreateDatabase : getSQLForCreateSchema)(props.instanceId, props.database, data)
      //   } else {
      //     if (!props.isDatabaseDrawer) {
      //       data.new_name = data.name
      //       data.name = props.initSchemaName
      //     }

      //     return (props.isDatabaseDrawer ? getSQLForEditDatabase : getSQLForEditSchema)(props.instanceId, props.database, data)
      //   }
      // })()

      // if (!getSQLResult.meta.success) {
      //   message.error(`${props.isAdd ? '新建' : '编辑'}${props.isDatabaseDrawer ? '数据库' : '模式'}失败: ${getSQLResult.meta.message}`)
      //   return
      // }


      // const executeResult = await executeSql({
      //   instanceId: props.instanceId,
      //   databaseName: props.database,
      //   schema: props.schema,
      //   statement: typeof getSQLResult.data === 'string' ? getSQLResult.data : getSQLResult.data.join('\n'),
      // })

      // if (executeResult.meta.success && executeResult.data?.error === '') {
      //   message.success(`${props.isAdd ? '新建' : '编辑'}${props.isDatabaseDrawer ? '数据库' : '模式'}成功`)
      //   context.emit('close', true)
      // } else {
      //   message.error(`${props.isAdd ? '新建' : '编辑'}${ props.isDatabaseDrawer ? '数据库' : '模式'}失败: ${executeResult.data?.error || getError(executeResult) || '无失败提示'}`)
      // }
    }

    const handleCancel = () => {
      context.emit('close')
    }


    onMounted(() => {
      initEditor()
    })

    onBeforeUnmount(() => {
      editor.dispose()
    })

    const isSubmitDisabled = computed(() => {
      return (state.schemaName === '' || state.ownerName === '')
        || (props.isAdd && props.initAlreadyExistNameList?.includes(state.schemaName))
    })

    watch([() => state.schemaName, () => state.ownerName, () => state.tableSpace, () => state.comment], () => {
      handleRefreshSQLDetail(isSubmitDisabled.value)
    })

    onMounted(() => {
      if (!editor) {
        initEditor()
      }
    })

    return {
      ...toRefs(state),
      rules,

      handleCopyValue,
      handleSubmit,
      handleCancel,
      handleCopySQLDetail,
      handleRefreshSQLDetail,
      isSubmitDisabled,
    }
  },
})
</script>
<!--suppress SassScssResolvedByNameOnly -->
<style lang="scss">
// 表单内容
.v-oushudb-add-db-form-container {
  padding: 74px 40px 20px 40px;
  overflow: auto;

  .v-oushudb-add-db-form-container-item {
    display: flex;

    .v-oushudb-add-db-form-container-item-label {
      display: flex;
      flex: 0 0 108px;
      align-items: center;
      height: 51px;
      margin-right: 10px;
      @include font-small();
      color: $color-primary-black;
    }

    .v-oushudb-add-db-form-container-item-label-disabled {
      color: $color-text-comment;
    }

    .v-oushudb-add-db-form-container-item-control {
      position: relative;
      z-index: 0;
      flex: 1;

      .v-oushudb-add-db-form-container-item-control-prefix-icon {
        position: absolute;
        top: 16px;
        left: 9px;
      }
    }
  }

  .#{$ant-prefix}-input-affix-wrapper {
    height: 51px !important;
    padding: 0;

    .#{$ant-prefix}-input-prefix {
      margin-right: 5px;
      margin-left: 9px;
    }

    .#{$ant-prefix}-input {
      height: 100% !important;
    }
  }

  .#{$ant-prefix}-input-affix-wrapper-disabled {
    color: $color-text-comment;
    background-color: unset;
    border: none;
    border-bottom: 1px solid $color-line-bold;
    border-radius: 0;

    .#{$ant-prefix}-input {
      color: $color-text-comment;
      border: none;
    }
  }

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
}

.v-oushudb-add-db-form-sql-detail {
  padding: 10px 40px 0;
  border-top: 1px solid $color-text-comment;

  .v-oushudb-add-db-form-sql-detail-header {
    display: flex;
    justify-content: space-between;
    @include font-small();
    padding-bottom: 20px;
    color: $color-primary-black;

    .v-oushudb-add-db-form-sql-detail-header-icon-group {
      .icon {
        cursor: pointer;
      }

      .icon:not(:last-child) {
        margin-right: 10px;
      }
    }
  }

  .v-oushudb-add-db-form-sql-detail-container {
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
.v-oushudb-add-db-form-btn-container {
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

// 抽屉样式
.v-oushudb-add-db-drawer.#{$ant-prefix}-drawer {
  //position: relative;
  //z-index: 88;

  .#{$ant-prefix}-drawer-content-wrapper {
    .#{$ant-prefix}-drawer-content {
      .#{$ant-prefix}-drawer-wrapper-body {
        .#{$ant-prefix}-drawer-header {
          position: absolute;
          right: 0;
          left: 0;
          z-index: 1;
          padding-top: 30px;
          padding-bottom: 20px;
          padding-left: 40px;
          margin: 0;
          background-color: white;
          border-bottom: 0;

          .#{$ant-prefix}-drawer-title {
            font-size: 18px;
            font-style: normal;
            font-weight: normal;
            line-height: 24px;
            color: #282B2E;
          }
        }

        .#{$ant-prefix}-drawer-body {
          height: calc(100% - 70px);
          padding: 0;
          overflow: auto;
        }
      }
    }
  }
}
</style>