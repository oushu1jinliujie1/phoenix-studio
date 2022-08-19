<template>
  <x-form :label-col="{ style: { width: '108px', height: '50px' } }">
    <x-form-item label="列名">
      <x-input v-model:value="name" :rules="columnNameRules" placeholder="请填写列名">
        <template #prefix>
          <icon image name="worksheet/column_two_color"/>
        </template>
        <template #suffix>
          <x-tooltip placement="bottomLeft" title="复制">
            <!--            <x-button-->
            <!--              :style=" { color: COLOR_PRIMARY_BLUE}"-->
            <!--              icon-name="worksheet/copy"-->
            <!--              type="text"-->
            <!--              @click="() => handleCopyValue(name)"-->
            <!--            />-->
            <icon color="primary" name="worksheet/copy" @click="() => handleCopyValue(name)"/>
          </x-tooltip>
        </template>
      </x-input>
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
<!--    <x-form-item :label-disabled="disabledRef" label="默认值（选填）">-->
    <x-form-item label="默认值（选填）">
      <x-select
        v-if="['date'].includes(type)"
        v-model:value="defaultValueType"
        :options="TIME_DEFAULT_TYPE_LIST"
        is-in-form
        show-search
      >
        <template #prefixIcon>
          <icon
            image
            name="worksheet/column_two_color"
          />
        </template>
      </x-select>
      <x-input v-else v-model:value="defaultValue">
        <template #prefix>
          <icon image name="worksheet/column_two_color"/>
        </template>
      </x-input>
    </x-form-item>
    <x-form-item label="列备注（选填）">
      <x-textarea v-model:value="comment" autoSize placeholder="请填写备注">
        <template #prefixIcon>
          <icon image name="worksheet/column_comment_two_color"/>
        </template>
      </x-textarea>
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
    <x-form-item label="非空">
      <x-switch v-model:checked="isNotNull"/>
    </x-form-item>
  </x-form>
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
import { computed, defineComponent, PropType, reactive, ref, toRefs, watch } from 'vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { RuleObject } from 'ant-design-vue/es/form/interface'
import Icon from '@/components/Icon.vue'
import { message } from 'ant-design-vue-3'
import useClipboard from 'vue-clipboard3'

import { TIME_DEFAULT_TYPE_LIST, TYPE_OPTION_LIST } from './constant'
import { COLOR_PRIMARY_BLUE } from 'lava-fe-lib/lib-common/constant'
import { Dayjs } from 'dayjs'

/**
 * 表单验证先不做
 */
export default defineComponent({
  name: 'addOrUpdateColumnBase',
  props: {
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
      defaultValue: props.initialColumn.defaultValue,
      comment: props.initialColumn.comment,
      isNotNull: props.initialColumn.isNotNull,
      isDistributionKey: props.initialColumn.isDistributionKey,
      length: props.initialColumn.length,
      scale: props.initialColumn.scale,
    })

    watch(() => props.initialColumn, () => {
      formState.name = props.initialColumn.name
      formState.type = props.initialColumn.type
      formState.defaultValue = props.initialColumn.defaultValue
      formState.comment = props.initialColumn.comment
      formState.isNotNull = props.initialColumn.isNotNull
      formState.isDistributionKey = props.initialColumn.isDistributionKey
      formState.length = props.initialColumn.length
      formState.scale = props.initialColumn.scale
    })

    // type 为 date 时，默认值的类型
    const defaultValueType = ref('当前日期')

    const defaultTimeValueType = ref<Dayjs>()


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
      if (formState.type === 'date') {
        formState.defaultValue = defaultTimeValueType.value?.format('YYYY/MM/DD')
      }
      context.emit('confirm', {
        ...formState,
      })
    }


    const handleCancel = () => {
      context.emit('close')
    }

    return {
      COLOR_PRIMARY_BLUE,

      disabledRef: computed(() => !props.isAdd),
      isSubmitDisabled: false,

      ...toRefs(formState),
      defaultValueType,
      defaultTimeValueType,

      columnNameRules,

      TYPE_OPTION_LIST,
      TIME_DEFAULT_TYPE_LIST,

      handleCopyValue,

      handleCancel,
      handleConfirm,
    }
  },
})
</script>
<style lang="scss">

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