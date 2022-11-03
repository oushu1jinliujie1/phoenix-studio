<template>
  <x-form layout="vertical" :label-col="{ style: { height: '30px' } }">
    <x-form-item label="索引名">
      <x-input v-model:value="name" :rules="indexNameRules" placeholder="请填写索引名">
        <template #prefix>
          <icon image name="worksheet/secondary_index_two_color"/>
        </template>
        <template #suffix>
          <x-tooltip placement="bottomLeft" title="复制">
            <icon color="#336CFF" name="worksheet/copy" @click="() => handleCopyValue(name)"/>
          </x-tooltip>
        </template>
      </x-input>
    </x-form-item>
    <x-form-item label="索引列">
      <x-select
        v-model:value="columns"
        :options="columnsOptions"
        mode="multiple"
        is-in-form
        show-search
        :placeholder="'请选择需要索引的列'"
      >
        <template #prefixIcon>
          <icon
            image
            name="worksheet/column_two_color"
          />
        </template>
      </x-select>
    </x-form-item>
    <x-form-item label="额外列">
      <x-select
        v-model:value="extra"
        :options="extraOptions"
        mode="multiple"
        is-in-form
        show-search
        :placeholder="'请选择需要额外索引的列'"
      >
        <template #prefixIcon>
          <icon
            image
            name="worksheet/column_two_color"
          />
        </template>
      </x-select>
    </x-form-item>
  </x-form>
  <div class="v-oushudb-edit-column-form-btn-container">
    <x-button
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
import { defineComponent, PropType, ref, reactive, toRefs, computed } from 'vue'
import Icon from '@/components/Icon.vue'
import useClipboard from 'vue-clipboard3'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { message } from 'ant-design-vue-3'
import { RuleObject } from 'ant-design-vue/es/form/interface'
import { checkIsInstanceName } from '@/lib/regexp'
import { duplicateTable } from '@/api'

export default defineComponent({
  name: 'CreateIndex',
  props: {
    schema: {
      type: String,
      default: ''
    },
    initialColumns: {
      type: Array,
      required: true
    },
    execLoading: Boolean
  },
  components: { Icon, ...smartUI },
  emits: ['confirm', 'close'],
  setup(props, context) {
    const { toClipboard } = useClipboard()

    const formState = reactive({
      name: '',
      columns: [] as string[],
      extra: [] as string[]
    })

    const columnsOptions = computed(() => {
      return props.initialColumns.filter((option: any) => {
        return !formState.extra.includes(option.value)
      })
    })

    const extraOptions = computed(() => {
      return props.initialColumns.filter((option: any) => {
        return !formState.columns.includes(option.value)
      })
    })

    const validatePass = (rule: RuleObject, value: string) => {
      if (value === '') {
        return Promise.reject('请填写索引名')
      } else {
        if (!checkIsInstanceName(value)) {
          return Promise.reject('请输入不以数字作为开始的由字母/数字/下划线组成的50位以内的字符串')
        }

        return new Promise((resolve, reject) => {
          duplicateTable({
            schemaName: props.schema,
            tableName: value
          }).then((resp) => {
            if (resp.meta.success) {
              resolve('')
            } else {
              reject('该索引名已存在，请检查后重新填写')
            }
          })
        })
      }
    }

    const indexNameRules = [
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
    const handleConfirm = async() => {
      if (!formState.name) {
        message.error('请填写索引名')
        return
      }
      if (!checkIsInstanceName(formState.name)) {
        message.error('请输入不以数字作为开始的由字母/数字/下划线组成的50位以内的字符串')
        return
      }
      const resp = await duplicateTable({ schemaName: props.schema, tableName: formState.name })
      if (!resp.meta.success) {
        message.error('该索引名已存在，请检查后重新填写')
        return
      }
      if (!formState.columns.length) {
        message.error('请至少选择一个索引列')
        return
      }
      context.emit('confirm', {
        ...formState,
      })
    }

    const handleCancel = () => {
      context.emit('close')
    }

    return {
      ...toRefs(formState),
      indexNameRules,

      columnsOptions,
      extraOptions,

      handleCopyValue,
      handleConfirm,
      handleCancel
    }
  }
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
