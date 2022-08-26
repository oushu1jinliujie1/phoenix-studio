<template>
  <div class="connection-table-settings">
    <!-- 已选表标签列表 -->
    <div class="connection-table-settings-tags">
      <div class="connection-table-settings-tags-icon">
        <icon image name="worksheet/search_table_two_color"/>
      </div>
      <x-tag v-for="table of selectTableList" :key="table.name" closable class="selected-table-tag" @close="removeTableTag(table)">
        connectionTableSettings{{ table.name }}
      </x-tag>
      <div v-show="selectTableList.length === 0" class="selected-table-tag-placeholder">
        请选择需要关联查询的表（表之间需第一主键列匹配）
      </div>
    </div>
    <!-- 选表主体 -->
    <div class="connection-table-settings-main">
      <!-- 数据库选择 -->
      <div class="connection-table-settings-database-container">
        <div class="connection-table-settings-container-title">
          <span>数据库</span>
          <icon color="#336CFF" name="worksheet/refresh" style="cursor: pointer;" @click="refreshDatabaseList()"/>
        </div>
        <x-spin :spinning="database.spinning">
          <!-- 搜索 -->
          <div class="connection-table-settings-container-search">
            <x-input-search
              v-model:value="database.searchValue"
              allow-clear
              class="raw"
              data-test-id="connection-table-settings-database-search-input"
              placeholder="请输入数据库名，按回车搜索"
              @search="handleDatabaseOnSearch"
              @keyup.enter="handleDatabaseOnSearch"
            />
          </div>
          <!-- database列表 -->
          <div class="connection-table-settings-list">
            <div
              v-for="(_database, index) in (database.list as any[])" :key="index"
              :class="[_database === currentDatabase ? 'current-select' : '']"
              class="connection-table-settings-list-item"
              @click="selectCurrentDatabase(_database)"
            >
              <div class="item-content" :title="_database.name">
                
                <icon v-show="_database !== currentDatabase" name="worksheet/database"/>
                <icon v-show="_database === currentDatabase" image name="worksheet/database_selected"/>
                {{ _database.name }}
              </div>
              <div class="item-content">
                <icon name="next"/>
              </div>
            </div>
          </div>
        </x-spin>
      </div>
      <!-- 表选择 -->
      <div class="connection-table-settings-table-container">
        <div class="connection-table-settings-container-title">
          <span>表</span>
          <icon color="#336CFF" name="worksheet/refresh" style="cursor: pointer;" @click="refreshTableList()"/>
        </div>
        <x-spin :spinning="table.spinning">
          <!-- 搜索 -->
          <div class="connection-table-settings-container-search">
            <x-input-search
              v-model:value="table.searchValue"
              allow-clear
              class="raw"
              data-test-id="connection-table-settings-table-search-input"
              placeholder="请输入表名，按回车搜索"
              @search="handleTableOnSearch"
              @keyup.enter="handleTableOnSearch"
            />
          </div>
          <!-- table列表 -->
          <div class="connection-table-settings-list">
            <x-tooltip
              v-for="(_table, index) in (table.list as any[])"
              :key="index"
              :overlayStyle="{ 'pointer-events': 'none' }"
              :visible="isDisabled(_table)?undefined:false">
              <template #title>
                <div style="color: #D74472;white-space: nowrap;">该表的第1个主键列与其他所选表不匹配</div>
              </template>
              <div
                :class="[_table.selected ? 'current-select' : '', isDisabled(_table) ? 'table-disabled' : '']"
                class="connection-table-settings-list-item"
              >
                <div class="item-content-half" :title="_table.name">
                  <x-checkbox
                    :disabled="isDisabled(_table)"
                    v-model:checked="_table.selected"
                    @change="onChangeTableSelected(_table)"
                  />
                  <icon v-show="!_table.selected" name="worksheet/table_line"/>
                  <icon v-show="_table.selected" name="worksheet/table_selected"/>
                  {{ _table.name }}
                </div>
                <div class="item-content-half" :title="_table.primary">
                  <icon name="worksheet/key"/>
                  {{ _table.primary }}
                </div>
              </div>
            </x-tooltip>
          </div>
        </x-spin>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
/* eslint-disable vue/no-side-effects-in-computed-properties */
import { defineComponent, ref, computed, reactive, toRefs, onBeforeMount, PropType } from 'vue'
import Icon from '@/components/Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { message } from 'ant-design-vue-3'
import { RuleObject } from 'ant-design-vue/es/form/interface'
import { debounce, throttle } from 'lodash'

export default defineComponent({
  name: 'connectionTableSettings',
  props: {
    selectTableList: {
      type: Array as PropType<any[]>,
      required: true
    }
  },
  components: { Icon, ...smartUI },
  emits: ['update:selectTableList'],
  setup(props, context) {
    const state = reactive({
      database: {
        spinning: false,
        searchValue: '',
        list: [] as any[],
      },
      table: {
        spinning: false,
        searchValue: '',
        list: [] as any[],
      },
      currentDatabase: null as any
    })
    const notFinishMsgRef = ref('')
    const isFinishRef = computed(() => {
      if (props.selectTableList.length === 0) {
        notFinishMsgRef.value = '请选择需要关联的表'
        return false
      } else {
        notFinishMsgRef.value = ''
        return true
      }
    })

    const getDatabaseList = async() => {
      state.database.spinning = true
      await new Promise((resolve) => {
        setTimeout(() => {
          resolve(0)
        }, 500)
      })
      state.database.list = [{ name: 'database1' }, { name: 'database2' }]
      const current = state.database.list.find((item: any) => item.name === state.currentDatabase?.name)
      state.currentDatabase = current || state.database.list[0] || null
      state.database.spinning = false
    }

    const getTableList = async() => {
      state.table.spinning = true
      await new Promise((resolve) => {
        setTimeout(() => {
          resolve(0)
        }, 500)
      })
      const data: any[] = [
        { name: 'table1', primary: 'columnP', columns: [{ name: 'columnP' }, { name: 'column1' }, { name: 'column2' }, { name: 'column3' }] },
        { name: 'table2', primary: 'columnP', columns: [{ name: 'columnP' }, { name: 'column1' }, { name: 'column2' }, { name: 'column3' }] },
        { name: 'table3', primary: 'columnP1', columns: [{ name: 'columnP1' }, { name: 'column1' }, { name: 'column2' }, { name: 'column3' }] }
      ]
      for (const each of data) {
        if (props.selectTableList.findIndex((item: any) => item.name === each.name) === -1) {
          each.selected = false
          continue
        }
        each.selected = true
      }
      state.table.list = data
      state.table.spinning = false
    }

    const removeTableTag = (table: any) => {
      table.selected = false
      const selectTableList = props.selectTableList.filter((item: any) => item.name !== table.name)
      context.emit('update:selectTableList', selectTableList)
    }

    const onChangeTableSelected = (table: any) => {
      if (table.selected) {
        const selectTableList = [...props.selectTableList, table]
        context.emit('update:selectTableList', selectTableList)
      } else {
        removeTableTag(table)
      }
    }

    const refreshDatabaseList = throttle(async() => {
      await getDatabaseList()
    }, 500)

    const refreshTableList = throttle(async() => {
      await getTableList()
    }, 500)

    const handleDatabaseOnSearch = debounce(async() => {
      await getDatabaseList()
    }, 500)

    const handleTableOnSearch = debounce(async() => {
      await getTableList()
    }, 500)

    const selectCurrentDatabase = debounce(async(database: any) => {
      if (database === state.currentDatabase) return
      state.currentDatabase = database
      await getTableList()
    }, 500)

    const isDisabled = (table: any) => {
      if (!props.selectTableList.length) return false
      const primary = props.selectTableList[0].primary
      if (table.primary === primary) return false
      return true
    }

    onBeforeMount(() => {
      getDatabaseList()
      getTableList()
    })

    return {
      ...toRefs(state),

      notFinishMsgRef,
      isFinishRef,

      getDatabaseList,
      getTableList,
      refreshDatabaseList,
      refreshTableList,
      handleDatabaseOnSearch,
      handleTableOnSearch,
      selectCurrentDatabase,

      removeTableTag,
      onChangeTableSelected,
      isDisabled,
    }
  }
})

</script>

<style lang="scss">
.connection-table-settings {
  display: flex;
  flex-direction: column;
  height: 100%;
  border-radius: 4px;
  border: 1px solid #D5D8D8;

  .connection-table-settings-tags {
    position: relative;
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    height: 86px;
    flex-shrink: 0;
    padding: 15px 58px;
    border-bottom: 1px solid #D5D8D8;

    .connection-table-settings-tags-icon {
      position: absolute;
      top: 18px;
      left: 30px;
      height: 18px;
      width: 18px;
    }

    .selected-table-tag,
    .selected-table-tag:active {
      height: 24px;
      padding: 2px 8px;
      background: rgba(213, 216, 219, 0.5);
      border-radius: 4px;
      color: #282B2E;
      font-size: 14px;
      line-height: 20px;
    }

    .selected-table-tag-placeholder {
      height: 24px;
      padding: 3px 0 1px;
      line-height: 20px;
      color: #85888C;
      user-select: none;
    }
  }

  .connection-table-settings-main {
    flex-grow: 1;
    height: 0;
    display: flex;

    .connection-table-settings-database-container {
      padding: 10px 0;
      width: 40%;
      border-right: 1px solid #D5D8D8;
    }
    .connection-table-settings-table-container {
      padding: 10px 0;
      width: 60%;
    }

    .connection-table-settings-container-title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: 26px;
      padding-bottom: 8px;
      margin: 0 30px;
      border-bottom: 1px solid #D5D8D8;
      line-height: 18px;
      font-size: 12px;

      > span {
        color: #85888C;
      }
    }
    .connection-table-settings-container-search {
      margin: 0 30px;
      padding: 5px 0;
    }

    .connection-table-settings-list {

      .connection-table-settings-list-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        white-space: nowrap;
        @include font-normal();
        border: 1px solid transparent;
        border-radius: 4px;
        margin: 0 10px;
        padding: 0 20px;
        height: 32px;
        cursor: pointer;

        > .item-content-half {
          display: flex;
          align-items: center;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          width: 50%;
          
          .smartui-checkbox {
            margin-right: 5px;
          }

          .icon {
            margin-right: 5px;
          }
        }

        > .item-content:nth-of-type(1) {
          display: flex;
          align-items: center;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;

          .icon {
            margin-right: 5px;
          }
        }

        > .item-content:nth-of-type(2) {
          opacity: 0;
        }

        &:hover,
        &:focus {
          background: rgba(51, 108, 255, 0.2);
        }
        &:hover,
        &:focus,
        &.current-select {
          border-radius: 4px;
          color: #336CFF;

          > .item-content:nth-of-type(2) {
            opacity: 1;
          }
        }
        &.table-disabled {
          background: none;
          color: #282B2E;

          &:hover {
            border: 1px solid #D74472;
          }
        }
      }
    }
  }
}

</style>
