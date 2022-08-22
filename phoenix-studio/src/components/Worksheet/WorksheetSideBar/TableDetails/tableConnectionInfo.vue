<template>
  <x-drawer
    v-model:visible="visibleLocal"
    title="关联查询表"
    fixed
    width="800"
  >
    <div class="v-oushudb-worksheet-table-detail-table-struct">
      <x-table
          :dataSource="data"
          :columns="columns"
          :pagination="false"
          auto-calc-empty-height
          divider
        >
          <template #action="{ record }">
            <x-tooltip placement="topLeft" title="移除">
              <icon style="cursor: pointer;" color="primary" name="minus_circle" @click="deleteConnection(record)"/>
            </x-tooltip>
          </template>
      </x-table>
    </div>
  </x-drawer>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, ref, watch } from 'vue'
import Icon from '@/components/Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { isProduction, useModel } from '@/smart-ui-vue/utils'
import { Table } from '@/components/Worksheet/type'

export default defineComponent({
  name: 'tableConnectInfo',
  props: {
    // 表数据
    table: {
      type: Object as PropType<Table>,
      default: () => ({})
    },
    // drawer visible
    visible: {
      type: Boolean,
      default: false,
    },
    schema: {
      type: String,
      default: ''
    }
  },
  components: { Icon, ...smartUI },
  emits: ['success', 'confirm', 'update:value'],
  setup(props, context) {
    // 表格数据
    const data: any = ref([])
    // 表格加载状态
    const loading = ref(false)

    const columns = [
      {
        title: '查询表名',
        dataIndex: 'name',
        key: 'name',
      },
      {
        title: '关联列数',
        dataIndex: 'count',
        key: 'count',
      },
      {
        title: '操作',
        key: 'action',
        slots: {
          customRender: 'action'
        }
      },
    ]

    const deleteConnection = (record: any) => {
      console.log('del')
    }

    return {
      visibleLocal: useModel('visible', props, context),
      data,
      columns,
      loading,

      deleteConnection
    }
  }
})
</script>

<style lang="scss">

</style>
