<template>
  <x-drawer
    v-model:visible="visibleLocal"
    title="二级索引"
    fixed
    width="800"
  >
    <div class="v-oushudb-worksheet-table-detail-table-struct">
      <x-table
          :dataSource="data"
          :columns="columns"
          :pagination="false"
          auto-calc-empty-height
        >
          <template #name="{ text }">
            <span>{{ text }}</span>
          </template>
          <template #columns="{ record }">
            <span>{{ record.columns }}</span>
          </template>
          <template #extra="{ record }">
            <span>{{ record.extra }}</span>
          </template>
          <template #status="{ record }">
            <span>{{ record.status }}</span>
          </template>
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
  name: 'tableSecondaryIndex',
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
  emits: [],
  setup(props, context) {
    // 表格数据
    const data: any = ref([])
    // 表格加载状态
    const loading = ref(false)

    const columns = [
      {
        title: '索引名',
        dataIndex: 'name',
        key: 'name',
      },
      {
        title: '索引列',
        dataIndex: 'name',
        key: 'columns',
        slots: {
          customRender: 'columns'
        }
      },
      {
        title: '额外列',
        dataIndex: 'name',
        key: 'extra',
        slots: {
          customRender: 'extra'
        }
      },
      {
        title: '状态',
        dataIndex: 'name',
        key: 'status',
        slots: {
          customRender: 'status'
        }
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

    data.value.push({
      name: 'IndexName',
      columns: 'column1, column2, column3, column4, column5, column6',
      extra: 'extra1, extra2, extra3, extra4, extra5, extra6',
      status: 0
    })

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
