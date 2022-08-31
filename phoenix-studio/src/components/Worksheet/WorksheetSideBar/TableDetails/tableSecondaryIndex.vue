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
        :dataSource="data"
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
            <x-tag v-for="column of record.columns.split(',')" :key="column" color-type="gray">{{ column }}</x-tag>
          </div>
        </template>
        <template #extra="{ record }">
          <div class="v-secondary-index-columns">
            <x-tag v-for="column of record.extra.split(',')" :key="column" color-type="gray">{{ column }}</x-tag>
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
          :init-already-exist-name-list="[]"
          :initial-columns="[{ value: 'column1' }, { value: 'column2' }, { value: 'column3' }]"
          @close="() => { createIndexVisible=false }"
          @confirm="handleConfirmCreateIndex"
        />
      </x-drawer>
    </div>
  </x-drawer>
</template>

<script lang="ts">
import { defineComponent, PropType, ref, reactive, toRefs } from 'vue'
import Icon from '@/components/Icon.vue'
import { throttle } from 'lodash'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { useModel } from '@/smart-ui-vue/utils'
import { Table } from '@/components/Worksheet/type'
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
      0: {
        icon: 'worksheet/creating',
        title: '创建中'
      },
      1: {
        icon: 'worksheet/success',
        title: '已创建'
      }
    }
    // 表格数据
    const data: any = ref([])
    // 表格加载状态
    const loading = ref(false)

    const state = reactive({
      createIndexVisible: false,
      moreVisible: false
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

    const deleteIndex = (record: any) => {
      console.log('del')
    }

    const loadMore = () => {
      data.value.push({
        name: 'IndexName',
        columns: 'column1, column2, column3, column4, column5, column6',
        extra: 'extra1, extra2, extra3, extra4, extra5, extra6',
        status: 0
      })
    }
    const loadMoreWithDebounce = throttle(loadMore, 500)


    const createIndex = () => {
      state.createIndexVisible = true
    }

    const handleConfirmCreateIndex = () => {
      console.log('handleCreate')
    }

    return {
      ...toRefs(state),
      statusMap,
      visibleLocal: useModel('visible', props, context),
      data,
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

    .antv-tag {
      margin: 0;
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
