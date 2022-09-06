<template>
  <x-form layout="vertical" :label-col="{ style: { height: '30px' } }">
    <x-form-item label="索引名">
      <x-input v-model:value="name" :rules="indexNameRules" placeholder="请填写列名">
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
      <x-select :disabled="extra.length" v-model:value="columns" :options="initialColumns" mode="multiple" is-in-form show-search :placeholder="extra.length ? '索引列与额外列不能同时选择' : '请选择需要索引的列'">
        <template #prefixIcon>
          <icon
            image
            name="worksheet/column_two_color"
          />
        </template>
      </x-select>
    </x-form-item>
    <x-form-item label="额外列">
      <x-select :disabled="columns.length" v-model:value="extra" :options="initialColumns" mode="multiple" is-in-form show-search :placeholder="columns.length ? '索引列与额外列不能同时选择' : '请选择需要额外索引的列'">
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
import { defineComponent, PropType, ref, reactive, toRefs } from 'vue'
import Icon from '@/components/Icon.vue'
import useClipboard from 'vue-clipboard3'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { message } from 'ant-design-vue-3'
import { RuleObject } from 'ant-design-vue/es/form/interface'

export default defineComponent({
  name: 'createIndex',
  props: {
    initAlreadyExistNameList: {
      type: Array,
      required: true
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
      columns: [],
      extra: []
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
    const handleConfirm = () => {
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
