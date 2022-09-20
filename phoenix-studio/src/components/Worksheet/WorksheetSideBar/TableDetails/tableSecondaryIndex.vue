<template>
  <x-drawer
    v-model:visible="visibleLocal"
    fixed
    width="800"
  >
    <template #title>
      <div style="display: flex;align-items: center;">
        <span style="padding-right: 20px;">二级索引</span>
        <x-button icon-name="worksheet/secondary_index" color="#336CFF" type="default" @click="createIndex()">新建索引</x-button>
      </div>
    </template>
    <div class="v-oushudb-worksheet-table-detail-table-struct">
      <x-table
        :dataSource="tableData"
        :columns="columns"
        :pagination="false"
        auto-calc-empty-height
        emptyImage="empty"
      >
        <template #name="{ text }">
          <span>{{ text }}</span>
        </template>
        <template #columns="{ record }">
          <div class="v-secondary-index-columns">
            <x-tag v-for="column of record.columns" :key="column" color-type="gray">{{ column }}</x-tag>
          </div>
        </template>
        <template #extra="{ record }">
          <div class="v-secondary-index-columns">
            <x-tag v-for="column of record.extra" :key="column" color-type="gray">{{ column }}</x-tag>
          </div>
        </template>
        <template #status="{ record }">
          <x-tooltip placement="topLeft" :title="statusMap[record.status].title">
            <icon :name="statusMap[record.status].icon" />
          </x-tooltip>
        </template>
        <template #action="{ record }">
          <x-tooltip placement="topLeft" title="删除">
            <icon style="cursor: pointer;" color="primary" name="minus_circle" @click="deleteIndex(record)"/>
          </x-tooltip>
        </template>
        <template #emptyDescription>
          暂无二级索引
        </template>
      </x-table>
      <div v-if="moreVisible" class="v-oushudb-worksheet-table-detail-table-load-more" @click="loadMoreWithDebounce()">
        点击加载更多
      </div>
      <x-drawer
        :visible="createIndexVisible"
        class="v-oushudb-edit-column-drawer"
        destroyOnClose
        fixed
        title="新建索引"
        width="800"
        @close="() => { createIndexVisible = false }">
        <CreateIndex
          :schema="schema"
          :initial-columns="table.columns?.map(column => { return { value: column.name } })"
          @close="() => { createIndexVisible=false }"
          @confirm="handleConfirmCreateIndex"
        />
      </x-drawer>
    </div>
  </x-drawer>
</template>

<script lang="ts">
import { defineComponent, PropType, ref, reactive, toRefs, onMounted, watch } from 'vue'
import Icon from '@/components/Icon.vue'
import { throttle } from 'lodash'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { message } from 'ant-design-vue-3'
import { useModel } from '@/smart-ui-vue/utils'
import { Table } from '@/components/Worksheet/type'
import { getSecondaryIndexList, createSecondaryIndex, deleteSecondaryIndex, duplicateTable, executeSql } from '@/api'
import CreateIndex from '@/components/Worksheet/WorksheetSideBar/TableDetails/createIndex.vue'

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
  components: { Icon, ...smartUI, CreateIndex },
  emits: [],
  setup(props, context) {
    const statusMap: any = {
      'b': {
        icon: 'secondary-index/building',
        title: '创建中'
      },
      'a': {
        icon: 'secondary-index/active',
        title: '可用'
      },
      'i': {
        icon: 'secondary-index/inactive',
        title: '不可用，维护中'
      },
      'e': {
        icon: 'secondary-index/active',
        title: '可用'
      },
      'd': {
        icon: 'secondary-index/inactive',
        title: '不可用，维护中'
      },
      'x': {
        icon: 'secondary-index/disabled',
        title: '不可用'
      },
      'r': {
        icon: 'secondary-index/rebuild',
        title: '重建中'
      }
    }
    // 表格数据
    const tableData: any = ref([])
    // 表格加载状态
    const loading = ref(false)

    const state = reactive({
      createIndexVisible: false,
      moreVisible: false,
      offset: 0,
      limit: 10
    })

    const columns = [
      {
        title: '索引名',
        dataIndex: 'name',
        width: '120px',
        key: 'name',
      },
      {
        title: '索引列',
        dataIndex: 'name',
        key: 'columns',
        width: '240px',
        slots: {
          customRender: 'columns'
        }
      },
      {
        title: '额外列',
        dataIndex: 'name',
        key: 'extra',
        width: '240px',
        slots: {
          customRender: 'extra'
        }
      },
      {
        title: '状态',
        dataIndex: 'name',
        key: 'status',
        width: '40px',
        align: 'center',
        slots: {
          customRender: 'status'
        }
      },
      {
        title: '操作',
        key: 'action',
        width: '40px',
        align: 'center',
        slots: {
          customRender: 'action'
        }
      },
    ]

    const deleteIndex = async(record: any) => {
      const resp = await deleteSecondaryIndex({
        schemaName: props.schema,
        tableName: props.table.name,
        indexName: record.name
      })
      if (resp.meta.success) {
        message.success('删除二级索引成功')
        initSecondaryIndexList()
      } else {
        message.error(`删除二级索引失败: ${(resp.meta?.message) || '无失败提示'}`, 5)
      }
    }

    const loadMore = async() => {
      state.offset += state.limit
      const resp = await getSecondaryIndexList({
        schemaName: props.schema,
        tableName: props.table.name,
        offset: state.offset,
        limit: state.limit
      })
      if (resp.meta.success) {
        tableData.value = [
          ...tableData.value,
          ...resp.data.data.map((item: any) => {
            return {
              name: item.indexName,
              columns: item.idxAttrs,
              extra: item.includesAttrs,
              status: item.indexState
            }
          })
        ]
        state.moreVisible = state.offset + state.limit < resp.data.count
      } else {
        message.error(`获取二级索引失败: ${(resp.meta?.message) || '无失败提示'}`, 5)
      }
    }
    const loadMoreWithDebounce = throttle(loadMore, 500)


    const createIndex = () => {
      state.createIndexVisible = true
    }

    const handleConfirmCreateIndex = async(form: any) => {
      const resp = await createSecondaryIndex({
        schemaName: props.schema,
        tableName: props.table.name,
        indexName: form.name,
        attrs: form.columns,
        includesAttrs: form.extra
      })
      if (resp.meta.success) {
        const result = await executeSql(resp.data)
        if (result.meta.success) {
          message.success('新建二级索引成功')
          initSecondaryIndexList()
          state.createIndexVisible = false
        } else {
          message.error(`新建二级索引失败: ${(resp.meta?.message) || '无失败提示'}`, 5)
        }
      } else {
        message.error(`新建二级索引失败: ${(resp.meta?.message) || '无失败提示'}`, 5)
      }
    }

    const initSecondaryIndexList = async() => {
      state.limit += state.offset
      state.offset = 0
      const resp = await getSecondaryIndexList({
        schemaName: props.schema,
        tableName: props.table.name,
        offset: state.offset,
        limit: state.limit
      })
      if (resp.meta.success) {
        tableData.value = resp.data.data.map((item: any) => {
          return {
            name: item.indexName,
            columns: item.idxAttrs,
            extra: item.includesAttrs,
            status: item.indexState
          }
        })
        state.moreVisible = state.offset + state.limit < resp.data.count
      } else {
        message.error(`获取二级索引失败: ${(resp.meta?.message) || '无失败提示'}`, 5)
      }
    }

    const visibleLocal = useModel('visible', props, context)

    watch(visibleLocal, (visible) => {
      if (visible) {
        initSecondaryIndexList()
      }
    })

    return {
      ...toRefs(state),
      statusMap,
      visibleLocal,
      tableData,
      columns,
      loading,

      deleteIndex,
      loadMoreWithDebounce,
      handleConfirmCreateIndex,
      createIndex
    }
  }
})
</script>

<style lang="scss">
  .v-secondary-index-columns {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .x-tag:not(.raw).antv-tag.x-tag-gray {
      margin: 0;
      color: #85888c;
      background-color: #e5e5e5;
      border-color: #e5e5e5;
    }
  }

  .v-oushudb-worksheet-table-detail-table-load-more {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 32px;
    border-bottom: 1px solid #D5D8D8;
    color: #336CFF;
    cursor: pointer;

    &:hover {
      background: rgba(0, 0, 0, .02);
    }
  }
</style>
